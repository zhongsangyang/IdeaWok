package com.cn.flypay.service.payment.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.model.trans.TpinganFileDeal;
import com.cn.flypay.model.trans.TranPayOrder;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.model.util.JSON;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.PinganFileDeal;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.payment.PingAnExpenseService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.pingan.Packets;
import com.cn.flypay.utils.pingan.PingAnConfig;
import com.cn.flypay.utils.pingan.YQUtil;

/**
 * Created by sunyue on 16/8/1.
 */

@Service(value = "pingAnExpenseService")
public class PingAnExpenseServiceImpl implements PingAnExpenseService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserCardService cardService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private BaseDao<TuserOrder> orderDao;
	@Autowired
	private BaseDao<TpinganFileDeal> fileDealDao;
	@Autowired
	private HolidayService holidayService;
	@Value("${pingan_client_ip}")
	private String pingan_client_ip;
	@Value("${pingan_client_port}")
	private String pingan_client_port;
	@Value("${pingan_max_scan_order_num}")
	private String pingan_max_scan_order_num;

	@Value("${pingan_T1_batch_root_path}")
	private String pingan_T1_batch_root_path;

	private static Set<String> exceptionCodes = new HashSet<>();
	static {
		exceptionCodes.add("YQ9999");
		exceptionCodes.add("GW3002");
		exceptionCodes.add("EBLN00");
		exceptionCodes.add("AFE004");
		exceptionCodes.add("E00006");
		exceptionCodes.add("E00007");
		exceptionCodes.add("E00008");
		exceptionCodes.add("YQ9989");
		exceptionCodes.add("YQ9976");
	}

	@Override
	public Map<String, String> sendOrderToPingAN(String orderNum) throws Exception {

		logger.info("--------发送给平安代付订单 begin----orderNum=" + orderNum);
		Map<String, String> returnMap = new HashMap<String, String>();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNum", orderNum);
		TuserOrder order = orderDao.get(
				"select t from TuserOrder t  left join t.tranPayOrder u left join t.card c where  t.orderNum=:orderNum",
				params);

		TranPayOrder payOrder = order.getTranPayOrder();
		Tuser user = order.getUser();
		String errorInfo = user.getRealName() + "提现订单已成功发送至平安通道，订单号：" + orderNum;
		int isSendSuccess = 0;
		if (order != null && UserOrder.order_status.PROCESSING.getCode() == order.getStatus()) {
			try {
				TuserCard settlementCard = order.getCard();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("OrderNumber", orderNum);
				map.put("AcctNo", ApplicatonStaticUtil.getAppStaticData("pinganAccount.signNo"));
				map.put("BusiType", "00000");
				map.put("CorpId", ApplicatonStaticUtil.getAppStaticData("pinganAccount.protocol"));
				map.put("CcyCode", "RMB");
				map.put("TranAmount", String.valueOf(order.getAmt().doubleValue()));
				map.put("InAcctNo", settlementCard.getCardNo());
				map.put("InAcctName", user.getRealName());
				// map.put("InAcctBankName", "平安银行");
				// map.put("InAcctBankNode", "");
				// map.put("Mobile", settlementCard.getPhone());
				map.put("Remark", "银联代付");

				String xmlBody = XmlMapper.mapObject2Xml(map, "Result").replaceAll("\n", "").replaceAll("> <", "><");
				String content = YQUtil.asemblyPackets(
						(String) ApplicatonStaticUtil.getAppStaticData("pinganAccount.accNo"), PingAnConfig.pay_code,
						xmlBody);
				logger.info("平安提现开始:" + content);
				try {
					/* 系统默认发送过代付指令，无论是否有异常，都认为指令已发送 */
					Packets packets = YQUtil.send2server(pingan_client_ip, Integer.parseInt(pingan_client_port),
							content);
					logger.info("平安提现返回开始");
					if (packets != null && packets.getHead() != null) {
						String head = new String(packets.getHead(), "GBK");
						logger.info("平安提现返回head:" + head);
						String result = head.substring(93, 143).trim();
						logger.info("平安提现返回result:" + result);
						if (result.contains("成功")) {
							String body = new String(packets.getBody(), "GBK");
							logger.info(body);
							Map<String, String> returnBodyMap = XmlMapper.xml2Map(body);
							payOrder.setBusNo(returnBodyMap.get("BussFlowNo"));
							payOrder.setPayDate(new Date());
							isSendSuccess = 100;
							returnMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
						} else {
							// 错误代码
							String errorCode = head.substring(87, 93).trim();
							if (exceptionCodes.contains(errorCode)) {
								isSendSuccess = 500;// 需要等待再次查询
								returnMap.put("flag", GlobalConstant.RESP_CODE_079);
								errorInfo = "提现订单通信异常，异常原因：" + errorCode + " ,等待再次查询, 原因：" + result;
							} else {
								isSendSuccess = 200;// 订单异常
								// 订单失败状态
								order.setStatus(UserOrder.order_status.FAILURE.getCode());
								payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
								payOrder.setErrorCode(errorCode);
								payOrder.setErrorInfo(result);
								returnMap.put("flag", GlobalConstant.RESP_CODE_047);
								errorInfo = "提现订单失败，失败原因：" + result + " 订单号：" + orderNum;
							}
						}
					} else {
						logger.error("提现订单发送指令回执异常，订单号=" + orderNum);
						returnMap.put("flag", GlobalConstant.RESP_CODE_079);
					}
				} catch (Exception e) {
					logger.error("提交提现订单通信异常", e);
					returnMap.put("flag", GlobalConstant.RESP_CODE_079);
					errorInfo = "提交提现订单通信异常，订单号" + orderNum;
				}
			} catch (Exception e) {
				logger.error("提交提现订单的请求抛出异常", e);
				returnMap.put("flag", GlobalConstant.RESP_CODE_995);
				errorInfo = "提交提现订单格式异常，订单号" + orderNum;
			}

		} else {
			logger.info("提现订单状态异常，拒绝发送给平安代付通道");
			errorInfo = "提现订单状态异常，拒绝发送给平安代付通道,订单号：" + orderNum;
			returnMap.put("flag", GlobalConstant.RESP_CODE_046);
		}
		if (isSendSuccess == 200) {
			/* 若发送的订单失败，回退给用户锁定的money */
			String failflag = null;
			/* 账户退款 */
			if (UserOrder.trans_type.YJTX.getCode() == order.getType()) {
				failflag = accountService.updateBrokerageAccountAfterLiqFailure(user.getId(), order);
			} else {
				failflag = accountService.updateAccountAfterLiqFailure(user.getId(), order);
			}
			if (GlobalConstant.RESP_CODE_SUCCESS.equals(failflag)) {
				failflag = GlobalConstant.RESP_CODE_047;
			}
			returnMap.put("flag", failflag);
		}
		payOrder.setErrorInfo(errorInfo);
		orderDao.update(order);
		logger.info("--------发送给平安代付订单 end----");
		return returnMap;

	}

	@Override
	public Map<String, String> sendSearchOrderToPingAN(Long orderId) throws Exception {
		logger.info("--------单笔查询代付订单 begin---- orderId=" + orderId);
		Map<String, String> returnMap = new HashMap<String, String>();
		String hql = " select t from TuserOrder t  left join t.user left join t.tranPayOrder u where  t.id=:orderId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderId", orderId);
		TuserOrder order = orderDao.get(hql, params);
		if (order != null) {
			try {
				TranPayOrder payOrder = order.getTranPayOrder();
				Map<String, String> returnBodyMap = sendSearchOrderToPingan(order.getOrderNum());
				boolean flag = false;
				if (returnBodyMap.containsKey("transfer_status")
						&& "SUCCESS".equals(returnBodyMap.get("transfer_status"))) {
					payOrder.setBusNo(returnBodyMap.get("BussFlowNo"));
					returnMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);

					Tuser user = order.getUser();
					if ("20".equals(returnBodyMap.get("Status"))) {
						/* 处理成功 */
						order.setStatus(UserOrder.order_status.SUCCESS.getCode());
						payOrder.setPayNo(returnBodyMap.get("TranFlowNo"));
						payOrder.setFinishDate(new Date());
						payOrder.setPayFinishDate(returnBodyMap.get("SettleDate"));
						payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(returnBodyMap.get("TranAmount"))));
						Tchannel chl = channelService.getTchannelByTransType(UserOrder.trans_type.XJTX.getCode());
						payOrder.setPayChannel(chl);
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
						if (UserOrder.trans_type.YJTX.getCode() == order.getType()) {
							accountService.updateBrokerageAccountAfterLiqSuccess(user.getId(), order);
						} else {
							accountService.updateAccountAfterLiqSuccess(user.getId(), order);
						}
						flag = true;

					} else if ("30".equals(returnBodyMap.get("Status"))) {
						/* 处理失败 */
						/* 若发送的订单失败，回退给用户锁定的money */
						/* 关闭订单 */
						// 订单失败状态
						order.setStatus(UserOrder.order_status.FAILURE.getCode());
						// 错误代码
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());

						payOrder.setErrorCode(returnBodyMap.get("RetCode"));
						payOrder.setErrorInfo(returnBodyMap.get("RetMsg"));
						returnMap.put("flag", GlobalConstant.RESP_CODE_047);

						/* 账户退款 */
						if (UserOrder.trans_type.YJTX.getCode() == order.getType()) {
							accountService.updateBrokerageAccountAfterLiqFailure(user.getId(), order);
						} else {
							accountService.updateAccountAfterLiqFailure(user.getId(), order);
						}
					} else {
						/* 处理中 等待下一次扫描 */
					}
				} else {
					// 查询失败， 等待再出处理

				}
				order.setScanNum(order.getScanNum() + 1);
				/* 若订单扫描次数大于5次 ，系统将订单交给人工处理 */
				if (!flag && order.getScanNum() >= Integer.parseInt(pingan_max_scan_order_num)) {
					order.setStatus(UserOrder.order_status.MANUAL_PROCESSING.getCode());
				}
				orderDao.update(order);
			} catch (Exception e) {
				returnMap.put("flag", GlobalConstant.RESP_CODE_024);
				throw e;
			}
		} else {
			returnMap.put("flag", GlobalConstant.RESP_CODE_046);
		}
		logger.info("--------单笔查询代付订单 end----");
		return returnMap;
	}

	@Override
	public Map<String, String> sendSearchOrderToPingANLong(Long orderId) throws Exception {
		logger.info("--------单笔查询代付订单 begin---- orderId=" + orderId);
		Map<String, String> returnMap = new HashMap<String, String>();
		String hql = " select t from TuserOrder t  left join t.user left join t.tranPayOrder u where  t.id=:orderId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderId", orderId);
		TuserOrder order = orderDao.get(hql, params);
		if (order != null) {
			try {
				TranPayOrder payOrder = order.getTranPayOrder();
				Map<String, String> returnBodyMap = sendSearchOrderToPingan(order.getOrderNum());
				logger.info("returnBodyMap={}", JSONObject.toJSONString(returnBodyMap));
				boolean flag = false;
				if (returnBodyMap.containsKey("transfer_status")
						&& "SUCCESS".equals(returnBodyMap.get("transfer_status"))) {
					payOrder.setBusNo(returnBodyMap.get("BussFlowNo"));
					returnMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);

					Tuser user = order.getUser();
					if ("20".equals(returnBodyMap.get("Status"))) {
						/* 处理成功 */
						order.setStatus(UserOrder.order_status.SUCCESS.getCode());
						payOrder.setPayNo(returnBodyMap.get("TranFlowNo"));
						payOrder.setFinishDate(new Date());
						payOrder.setPayFinishDate(returnBodyMap.get("SettleDate"));
						payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(returnBodyMap.get("TranAmount"))));
						Tchannel chl = channelService.getTchannelByTransType(UserOrder.trans_type.XJTX.getCode());
						payOrder.setPayChannel(chl);
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
						if (UserOrder.trans_type.YJTX.getCode() == order.getType()) {
							accountService.updateBrokerageAccountAfterLiqSuccess(user.getId(), order);
						} else {
							accountService.updateAccountAfterLiqSuccess(user.getId(), order);
						}
						flag = true;

					} else if ("30".equals(returnBodyMap.get("Status"))) {
						/* 处理失败 */
						/* 若发送的订单失败，回退给用户锁定的money */
						/* 关闭订单 */
						// 订单失败状态
						order.setStatus(UserOrder.order_status.FAILURE.getCode());
						// 错误代码
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
						payOrder.setErrorCode(returnBodyMap.get("RetCode"));
						payOrder.setErrorInfo(returnBodyMap.get("RetMsg"));
						returnMap.put("flag", GlobalConstant.RESP_CODE_047);
						/* 账户退款 */
						if (UserOrder.trans_type.YJTX.getCode() == order.getType()) {
							accountService.updateBrokerageAccountAfterLiqFailure(user.getId(), order);
						} else {
							accountService.updateAccountAfterLiqFailure(user.getId(), order);
						}
					} else {
						/* 处理中 等待下一次扫描 */
					}
				} else {
					// 查询失败， 等待再出处理
					/* 处理失败 */
					/* 若发送的订单失败，回退给用户锁定的money */
					/* 关闭订单 */
					// 订单失败状态
					order.setStatus(UserOrder.order_status.FAILURE.getCode());
					// 错误代码
					payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());

					payOrder.setErrorCode(returnBodyMap.get("order_error_code"));
					payOrder.setErrorInfo(returnBodyMap.get("order_error_info"));
					returnMap.put("flag", GlobalConstant.RESP_CODE_047);

					/* 账户退款 */
					Tuser user = order.getUser();
					if (UserOrder.trans_type.YJTX.getCode() == order.getType()) {
						accountService.updateBrokerageAccountAfterLiqFailure(user.getId(), order);
					} else {
						accountService.updateAccountAfterLiqFailure(user.getId(), order);
					}
					flag = true;
				}
				order.setScanNum(order.getScanNum() + 1);
				/* 若订单扫描次数大于10次 ，系统将订单交给人工处理 */
				if (!flag && order.getScanNum() >= 10) {
					order.setStatus(UserOrder.order_status.MANUAL_PROCESSING.getCode());
				}
				orderDao.update(order);
			} catch (Exception e) {
				returnMap.put("flag", GlobalConstant.RESP_CODE_024);
				throw e;
			}
		} else {
			returnMap.put("flag", GlobalConstant.RESP_CODE_046);
		}
		logger.info("--------单笔查询代付订单 end----");
		return returnMap;
	}

	public static void main(String[] args) throws Exception {
		Map<String, Object> searchBodyMap = new HashMap<String, Object>();
		searchBodyMap.put("AcctNo", ApplicatonStaticUtil.getAppStaticData("pinganAccount.signNo"));
		searchBodyMap.put("OrderNumber", "17112713270912241373");
		String xmlBody = XmlMapper.mapObject2Xml(searchBodyMap, "Result").replaceAll("\n", "").replaceAll("> <", "><");
		String content = YQUtil.asemblyPackets((String) ApplicatonStaticUtil.getAppStaticData("pinganAccount.accNo"),
				PingAnConfig.search_code, xmlBody);
		Packets packets = YQUtil.send2server("101.200.34.95", 7070, content);
		String head = new String(packets.getHead(), "GBK");
		String body = new String(packets.getBody(), "GBK");
		String result = head.substring(93, 143).trim();
		Map<String, String> returnBodyMap = XmlMapper.xml2Map(body);
		System.out.println("result:" + head);
		System.out.println("body:" + body);
		System.out.println("body2:" + JSON.getDefault().toJSONString(returnBodyMap));
	}

	private Map<String, String> sendSearchOrderToPingan(String orderNum) throws Exception {
		try {
			Map<String, String> returnMap = new HashMap<String, String>();

			Map<String, Object> searchBodyMap = new HashMap<String, Object>();
			searchBodyMap.put("AcctNo", ApplicatonStaticUtil.getAppStaticData("pinganAccount.signNo"));
			searchBodyMap.put("OrderNumber", orderNum);

			String xmlBody = XmlMapper.mapObject2Xml(searchBodyMap, "Result").replaceAll("\n", "").replaceAll("> <",
					"><");
			String content = YQUtil.asemblyPackets(
					(String) ApplicatonStaticUtil.getAppStaticData("pinganAccount.accNo"), PingAnConfig.search_code,
					xmlBody);
			logger.info(content);
			Packets packets = YQUtil.send2server(pingan_client_ip, Integer.parseInt(pingan_client_port), content);
			if (packets != null && packets.getHead() != null) {
				String head = new String(packets.getHead(), "GBK");
				String result = head.substring(93, 143).trim();
				logger.info(head);
				if (result.contains("成功")) {
					returnMap.put("transfer_status", "SUCCESS");
					String body = new String(packets.getBody(), "GBK");
					logger.info(body);
					Map<String, String> returnBodyMap = XmlMapper.xml2Map(body);
					returnMap.putAll(returnBodyMap);
					if ("20".equals(returnBodyMap.get("Status"))) {
						returnMap.put("order_status", "SUCCESS");
					} else if ("30".equals(returnBodyMap.get("Status"))) {
						returnMap.put("order_status", "FAILURE");
					} else {
						/* 处理中 等待下一次扫描 */
					}
				} else {
					returnMap.put("transfer_status", "FAILURE");
					String errorCode = head.substring(87, 93).trim();
					returnMap.put("order_error_code", errorCode);
					returnMap.put("order_error_info", result);
				}
			} else {
				returnMap.put("transfer_status", "FAILURE");
				returnMap.put("order_error_info", "平安代付查询出现异常");
			}
			return returnMap;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public TpinganFileDeal sendBatchT1OrderToPingAN(String dateyyyyMMdd) throws Exception {
		String fileName = dateyyyyMMdd;
		Map<String, Object> params = new HashMap<String, Object>();
		Date t1Date = DateUtil.convertStringToDate("yyyyMMdd", dateyyyyMMdd);
		Date[] dates = holidayService.getT1StartAndEndStatementWorkDate(DateUtil.getDatebyInterval(t1Date, 1));
		params.put("dateTimeStart", dates[0]);
		params.put("dateTimeEnd", dates[1]);
		Set<Integer> codes = new HashSet<Integer>();
		codes.add(UserOrder.trans_type.XJTX.getCode());
		codes.add(UserOrder.trans_type.YJTX.getCode());
		params.put("type", codes);
		params.put("status", UserOrder.order_status.PROCESSING.getCode());
		params.put("payType", 1);
		List<TuserOrder> orderList = orderDao.find(
				"select t from TuserOrder t inner join t.user u left join t.tranPayOrder p left join t.card c where  "
						+ "  t.status=:status and t.type in(:type)  and t.payType=:payType and u.id in( select acc.user.id from Taccount acc where  acc.user.id=u.id  and acc.status=0 )and  t.createTime BETWEEN :dateTimeStart and :dateTimeEnd",
				params);
		if (orderList != null && orderList.size() > 0) {
			try {

				/* 生成待发送的文件 */
				TpinganFileDeal fileDeal = writeT1ToFile(fileName, orderList);
				if (fileDeal != null) {
					String uuid = commonService.getUniqueTradeSn();
					fileDeal.setTradeSn(uuid);
					/* 发送代付文件给平安 */
					sendFile01ToPingAn(fileDeal);
					return fileDeal;
				}
			} catch (Exception e) {
				logger.error("发送给平安客户端异常", e);
			}
		} else {
			logger.info("在" + dateyyyyMMdd + "内，无T1提现订单");
		}
		return null;
	}

	/**
	 * 将T1列表写入待处理文件
	 * 
	 * @param fileName
	 * @param orderList
	 * @throws Exception
	 */
	private TpinganFileDeal writeT1ToFile(String fileName, List<TuserOrder> orderList) throws Exception {
		String folderPath = pingan_T1_batch_root_path + File.separator + fileName;
		File todoFolder = new File(folderPath);
		if (todoFolder.exists()) {
			todoFolder.mkdirs();
		}
		if (orderList != null && orderList.size() > 0) {
			String filePath = folderPath + File.separator + fileName + ".txt";
			File todoFile = new File(filePath);
			List<String> ls = new ArrayList<String>();
			BigDecimal sum = BigDecimal.ZERO;
			for (TuserOrder order : orderList) {
				StringBuffer sb = new StringBuffer();
				sb.append(order.getOrderNum());// 订单号
				sb.append(PingAnConfig.word_separator);
				if (order.getCard() != null) {
					sb.append(order.getCard().getCardNo());// 卡号
				}
				sb.append(PingAnConfig.word_separator);
				if (order.getUser() != null) {
					sb.append(order.getUser().getRealName());// 真是姓名
				}
				sb.append(PingAnConfig.word_separator);// 省
				sb.append(PingAnConfig.word_separator);// 市
				sb.append(PingAnConfig.word_separator);// 收款账号开户行名称
				sb.append(PingAnConfig.word_separator);// 金额
				sb.append(order.getAmt().doubleValue());
				sum = sum.add(order.getAmt());
				sb.append(PingAnConfig.word_separator);// 摘要
				if (StringUtil.isNotEmpty(order.getDescription())) {
					sb.append(order.getDescription());
				} else {
					sb.append("代付");
				}
				sb.append(PingAnConfig.word_separator);// 收款账号开户行联行号
				sb.append(PingAnConfig.word_separator);// 手机号
				ls.add(sb.toString());
			}
			String fileHead = ApplicatonStaticUtil.getAppStaticData("pinganAccount.signNo")
					+ PingAnConfig.word_separator + orderList.size() + PingAnConfig.word_separator
					+ sum.setScale(2, BigDecimal.ROUND_FLOOR);
			ls.add(0, fileHead);
			try {
				FileUtils.writeLines(todoFile, PingAnConfig.encoding, ls);
			} catch (IOException e) {
				logger.error("生成批量T1交易文件异常", e);
				throw e;
			}
			return new TpinganFileDeal(fileName, folderPath, 1, null, orderList.size(), sum.doubleValue(), null);
		}
		return null;
	}

	/**
	 * 发送文件请求File1
	 * 
	 * @param tradeSn
	 * @param file_name
	 * @param filePath
	 * @throws Exception
	 */
	public String sendFile01ToPingAn(TpinganFileDeal fileDeal) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("TradeSn", fileDeal.getTradeSn());
		map.put("FileName", fileDeal.getFileName() + ".txt");
		map.put("FilePath", fileDeal.getFilePath());

		String xmlBody = XmlMapper.mapObject2Xml(map, "Result").replaceAll("\n", "").replaceAll("> <", "><");
		String content = YQUtil.asemblyPackets((String) ApplicatonStaticUtil.getAppStaticData("pinganAccount.accNo"),
				PingAnConfig.file_01_code, xmlBody);
		logger.info(content);
		Packets packets = YQUtil.send2server(pingan_client_ip, Integer.parseInt(pingan_client_port), content);
		String head = new String(packets.getHead(), "GBK");
		String result = head.substring(93, 143).trim();
		logger.info(head);
		if (result.contains("成功")) {
			fileDeal.setDescription("向平安通道发送T1文件，等待发送代付指令");
			fileDeal.setStatus(PinganFileDeal.file_status.file01_success.name());
		} else {
			fileDeal.setDescription("向平安通道发送T1文件失败");
			fileDeal.setStatus(PinganFileDeal.file_status.file01_fail.name());
			logger.error("向平安通道发送T1文件失败");
		}
		fileDealDao.saveOrUpdate(fileDeal);
		return "";
	}

	/**
	 * 查询文件请求File02
	 * 
	 * @throws Exception
	 */
	public TpinganFileDeal sendFile02ToPingAn(TpinganFileDeal fileDeal) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("TradeSn", fileDeal.getTradeSn());

		String xmlBody = XmlMapper.mapObject2Xml(map, "Result").replaceAll("\n", "").replaceAll("> <", "><");
		String content = YQUtil.asemblyPackets((String) ApplicatonStaticUtil.getAppStaticData("pinganAccount.accNo"),
				PingAnConfig.file_02_code, xmlBody);
		logger.info(content);
		Packets packets = YQUtil.send2server(pingan_client_ip, Integer.parseInt(pingan_client_port), content);
		String head = new String(packets.getHead(), "GBK");
		String result = head.substring(93, 143).trim();
		logger.info(head);
		if (result.contains("成功")) {
			String body = new String(packets.getBody(), "GBK");
			logger.info(body);
			String Code = body.substring(body.indexOf("<Code>") + 6, body.indexOf("</Code>"));
			String desc = body.substring(body.indexOf("<Desc>") + 6, body.indexOf("</Desc>"));
			if ("F0".equals(Code) || "E7".equals(Code)) {
				String RandomPwd = body.substring(body.indexOf("<RandomPwd>") + 11, body.indexOf("</RandomPwd>"));
				fileDeal.setRandomPwd(RandomPwd);
				String HashData = body.substring(body.indexOf("<HashData>") + 10, body.indexOf("</HashData>"));
				fileDeal.setHashData(HashData);
				String SignData = body.substring(body.indexOf("<SignData>") + 10, body.indexOf("</SignData>"));
				fileDeal.setSignData(SignData);
				fileDeal.setDescription(desc);
				fileDeal.setStatus(PinganFileDeal.file_status.file02_success.name());
			} else {
				fileDeal.setStatus(PinganFileDeal.file_status.file02_fail.name());
				fileDeal.setDescription(Code + "---" + desc);
			}
			fileDealDao.update(fileDeal);

			logger.info("Code" + Code);
			logger.info(body);
		} else {
			throw new Exception("向平安通道发送文件失败");
		}
		return fileDeal;
	}

	public String generateFile04ReturnSuccessBody(TpinganFileDeal fileDeal) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("TradeSn", fileDeal.getTradeSn());
		map.put("FileName", fileDeal.getFileName() + ".txt");
		map.put("FilePath", fileDeal.getFilePath());

		String xmlBody = "<?xml version=\"1.0\" encoding=\"GBK\"?><Result></Result>";
		String content = YQUtil.asemblyPackets((String) ApplicatonStaticUtil.getAppStaticData("pinganAccount.accNo"),
				PingAnConfig.file_04_code, xmlBody);
		logger.info(content);
		return content;
	}

	@Override
	public TpinganFileDeal dealFile04FeedBack(String body) {
		TpinganFileDeal fileDeal = null;
		if (StringUtil.isNotBlank(body)) {
			String TradeSn = body.substring(body.indexOf("<TradeSn>") + 9, body.indexOf("</TradeSn>"));
			fileDeal = fileDealDao.get("select t from TpinganFileDeal t where t.tradeSn='" + TradeSn.trim() + "'");
			if (fileDeal != null) {
				String RandomPwd = body.substring(body.indexOf("<RandomPwd>") + 11, body.indexOf("</RandomPwd>"));
				fileDeal.setRandomPwd(RandomPwd);
				String HashData = body.substring(body.indexOf("<HashData>") + 10, body.indexOf("</HashData>"));
				fileDeal.setHashData(HashData);
				String SignData = body.substring(body.indexOf("<SignData>") + 10, body.indexOf("</SignData>"));
				fileDeal.setSignData(SignData);
				fileDeal.setDescription("File04已解析，T1文件已经上传给平安服务器");
				fileDeal.setStatus(PinganFileDeal.file_status.file04_success.name());
				fileDealDao.update(fileDeal);
				// try {
				// sendKHKF01ToPingan(fileDeal);
				// } catch (Exception e) {
				// logger.error("文件上传后，发送代付请求报错", e);
				// }
			}
		}
		return fileDeal;
	}

	// public static void main(String[] args) {
	// PingAnExpenseServiceImpl pps = new PingAnExpenseServiceImpl();
	// String body =
	// "A0010101010090107980000khkf0000000000170FILE04123450120160831152411YQTEST20160831152409F0
	// 文件下载成功 000001 00000000000<?xml version=\"1.0\"
	// encoding=\"GBK\"?><Result><Action>2</Action><TradeSn>16083115240934364497</TradeSn><FileName>5160831635447020160831152146408563.txt</FileName></Result>";
	// body = body.substring(body.indexOf("<Result>"));
	// pps.dealFile04FeedBack(body);
	// }

	public void sendKHKF01ToPingan(TpinganFileDeal fileDeal) throws Exception {
		logger.info("----发送批量代付指令 begin");
		String downSn = DateUtil.getStringFromDate(new Date(), "yyyyMMdd");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("BatchNo", downSn);
		map.put("AcctNo", ApplicatonStaticUtil.getAppStaticData("pinganAccount.signNo"));
		map.put("BusiType", "00000");
		map.put("CorpId", ApplicatonStaticUtil.getAppStaticData("pinganAccount.protocol"));
		map.put("TotalNum", fileDeal.getTotalNum().toString());
		map.put("TotalAmount", fileDeal.getTotalAmount().toString());
		// map.put("Remark",Remark);
		map.put("FileName", fileDeal.getFileName() + ".txt");
		map.put("RandomPwd", fileDeal.getRandomPwd());
		if (StringUtil.isNotBlank(fileDeal.getHashData())) {
			map.put("HashData", fileDeal.getHashData());
		}
		if (StringUtil.isNotBlank(fileDeal.getSignData())) {
			map.put("SignData", fileDeal.getSignData());
		}

		String xmlBody = XmlMapper.mapObject2Xml(map, "Result").replaceAll("\n", "").replaceAll("> <", "><");
		String content = YQUtil.asemblyPackets((String) ApplicatonStaticUtil.getAppStaticData("pinganAccount.accNo"),
				PingAnConfig.batch_pay_code, xmlBody);
		logger.info(content);
		Packets packets = YQUtil.send2server(pingan_client_ip, Integer.parseInt(pingan_client_port), content);
		String head = new String(packets.getHead(), "GBK");
		String result = head.substring(93, 143).trim();
		logger.info(head);
		if (result.contains("成功")) {
			String body = new String(packets.getBody(), "GBK");
			logger.info(body);
			fileDeal.setDescription("批量付款指令已下达成功,稍后请查询T1结果 ");
			fileDeal.setStatus("SUCCESS");
			fileDealDao.update(fileDeal);
			Map<String, String> fileMap = XmlMapper.xml2Map(body);

			TpinganFileDeal fd = new TpinganFileDeal(null, null, 2, downSn,
					Integer.parseInt((String) fileMap.get("TotalNum")),
					Double.parseDouble((String) fileMap.get("TotalAmount")), "批量代付指令已下达，请查询T1结果");
			fd.setStatus(PinganFileDeal.file_status.KHKF01_success.name());
			fd.setFileDeal(fileDeal);
			fileDealDao.save(fd);
		} else {
			throw new Exception("向平安通道发送文件失败");
		}
		logger.info("----发送批量代付指令 end");
	}

	/**
	 * 多次定时查询
	 */
	public void sendKHKF02ToPingan(TpinganFileDeal fileDeal) throws Exception {
		logger.info("----发送批量代付查询指令 begin");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("BatchNo", fileDeal.getTradeSn());
		map.put("AcctNo", ApplicatonStaticUtil.getAppStaticData("pinganAccount.signNo"));

		String xmlBody = XmlMapper.mapObject2Xml(map, "Result").replaceAll("\n", "").replaceAll("> <", "><");
		String content = YQUtil.asemblyPackets((String) ApplicatonStaticUtil.getAppStaticData("pinganAccount.accNo"),
				PingAnConfig.batch_search_code, xmlBody);
		logger.info(content);
		Packets packets = YQUtil.send2server(pingan_client_ip, Integer.parseInt(pingan_client_port), content);
		String head = new String(packets.getHead(), "GBK");
		String result = head.substring(93, 143).trim();
		logger.info(head);
		if (result.contains("成功")) {
			String body = new String(packets.getBody(), "GBK");
			logger.info(body);
			Map<String, String> fileMap = XmlMapper.xml2Map(body);
			fileDeal.setFileName(fileMap.get("FileName"));
			fileDeal.setHashData(fileMap.get("HashData"));
			fileDeal.setRandomPwd(fileMap.get("RandomPwd"));
			fileDeal.setSignData(fileMap.get("SignData"));
			fileDeal.setDescription("批量付款文件已成功，等待平安批量处理 ");
			if (Integer.parseInt((String) fileMap.get("BatchStt")) == 20) {
				fileDeal.setStatus(PinganFileDeal.file_status.KHKF02_success.name());
				fileDeal.setDescription("批量付款文件提交已成功并且平安成功处理，等待下载 ");
				// 直接下载下载文件
				sendFile03ToPingAn(fileDeal);
			} else if (Integer.parseInt((String) fileMap.get("BatchStt")) == 30) {
				fileDeal.setStatus(PinganFileDeal.file_status.KHKF02_fail.name());
				fileDeal.setDescription("批量付款文件提交已成功但平安处理失败，请重新处理 ");
			}
			fileDealDao.update(fileDeal);
		} else {
			throw new Exception("向平安通道发送文件失败");
		}
		logger.info("----发送批量代付查询指令  end");
	}

	/**
	 * 查询文件请求File03,下载文件
	 * 
	 * @param tradeSn
	 * @param file_name
	 * @param filePath
	 * @throws Exception
	 */
	public void sendFile03ToPingAn(TpinganFileDeal fileDeal) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("TradeSn", commonService.getUniqueTradeSn());
		map.put("FileName", fileDeal.getFileName());
		map.put("RandomPwd", fileDeal.getRandomPwd());

		String xmlBody = XmlMapper.mapObject2Xml(map, "Result").replaceAll("\n", "").replaceAll("> <", "><");
		String content = YQUtil.asemblyPackets((String) ApplicatonStaticUtil.getAppStaticData("pinganAccount.accNo"),
				PingAnConfig.file_03_code, xmlBody);
		logger.info(content);
		Packets packets = YQUtil.send2server(pingan_client_ip, Integer.parseInt(pingan_client_port), content);
		String head = new String(packets.getHead(), "GBK");
		String result = head.substring(93, 143).trim();
		logger.info(head);
		if (result.contains("成功")) {
			fileDeal.setStatus(PinganFileDeal.file_status.file03_success.name());
			fileDeal.setDescription("T1处理文件已经成功下载");
		} else {
			fileDeal.setStatus(PinganFileDeal.file_status.file03_fail.name());
			fileDeal.setDescription("T1处理文件成功失败，请重试");
			throw new Exception("向平安通道发送文件失败");
		}
	}

	@Override
	public String dealBatchT1Result(TpinganFileDeal fileDeal) {
		String flag = "SUCCESS";
		String filePath = pingan_T1_batch_root_path + File.separator + fileDeal.getFileName();
		File file = new File(filePath);
		if (file.exists()) {
			try {
				List<String> cts = FileUtils.readLines(file, "GBK");
				for (int i = 1; i < cts.size(); i++) {
					String row = cts.get(i);
					String[] cls = row.split("\\|::\\|");
					/*
					 * 第三方流水号| 收款借记卡/账号 |收款人户名| 金额 |实收手续费| 返回码| 返回消息
					 */

					String hql = "select t from TuserOrder t  left join t.user left join t.tranPayOrder u where t.status=300 and  t.orderNum=:orderNum";
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("orderNum", cls[0]);
					TuserOrder order = orderDao.get(hql, params);
					if (order != null) {
						TranPayOrder payOrder = order.getTranPayOrder();
						Tuser user = order.getUser();
						if ("0000".equals(cls[5])) {
							/* 处理成功 */
							order.setStatus(UserOrder.order_status.SUCCESS.getCode());
							payOrder.setFinishDate(new Date());
							payOrder.setRealAmt(
									BigDecimal.valueOf(Double.parseDouble(cls[3]) - Double.parseDouble(cls[4])));
							Tchannel chl = channelService.getTchannelByTransType(UserOrder.trans_type.XJTX.getCode());
							payOrder.setPayChannel(chl);
							payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());

							accountService.updateAccountAfterLiqSuccess(user.getId(), order);
						} else if ("9001".equals(cls[5])) {// 不明确，等待下一批T1

						} else {

							/* 处理失败 */
							/* 关闭订单 */
							// 订单失败状态
							order.setStatus(UserOrder.order_status.FAILURE.getCode());
							// 错误代码
							payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
							payOrder.setErrorInfo(cls[6]);
							/* 账户退款 */
							accountService.updateAccountAfterLiqFailure(user.getId(), order);
						}
						/*
						 * order.setScanNum(order.getScanNum() + 1); 若订单扫描次数大于5次
						 * ，系统将订单交给人工处理 if (order.getScanNum() >=
						 * Integer.parseInt(pingan_max_scan_order_num) &&
						 * UserOrder.order_status.SUCCESS.getCode() !=
						 * order.getStatus()) {
						 * order.setStatus(UserOrder.order_status
						 * .MANUAL_PROCESSING.getCode()); }
						 * orderDao.update(order);
						 */
					}
				}
			} catch (IOException e) {
				logger.error("处理T1回馈结果异常", e);
				flag = "处理T1回馈结果异常";
			}
		} else {
			flag = "没有下载到T1文件，请重现查询T1代付状态！";
		}
		return flag;
	}

	@Override
	public void dealDownLoadStatement(String date) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Date", date);
		map.put("AcctNo", ApplicatonStaticUtil.getAppStaticData("pinganAccount.signNo"));
		map.put("FileType", "KHKF01");// KHKF01对账文件 KHKF02差错文件
		try {
			String xmlBody = XmlMapper.mapObject2Xml(map, "Result").replaceAll("\n", "").replaceAll("> <", "><");

			String content = YQUtil.asemblyPackets(
					(String) ApplicatonStaticUtil.getAppStaticData("pinganAccount.accNo"), PingAnConfig.statment_code,
					xmlBody);
			logger.info(content);
			Packets packets = YQUtil.send2server(pingan_client_ip, Integer.parseInt(pingan_client_port), content);
			String head = new String(packets.getHead(), "GBK");
			String result = head.substring(93, 143).trim();
			logger.info(head);
			TpinganFileDeal fileDeal = new TpinganFileDeal(null, null, 2, null, null, null, null);
			if (result.contains("成功")) {
				String body = new String(packets.getBody(), "GBK");
				logger.info(body);
				Map<String, String> returnBodyMap = XmlMapper.xml2Map(body);
				if ("20".equals(returnBodyMap.get("Stt"))) {
					String fileXml = body.substring(body.indexOf("<list>"), body.indexOf("</list>") + 7);
					Map<String, String> returnListMap = XmlMapper.xml2Map(fileXml);
					fileDeal.setFileName(returnListMap.get("FileName"));
					fileDeal.setRandomPwd(returnListMap.get("RandomPwd"));
					try {
						sendFile03ToPingAn(fileDeal);
						fileDeal.setStatus(PinganFileDeal.file_status.KHKF05_success.name());
					} catch (Exception e) {
						logger.error("发送File03失败", e);
						fileDeal.setStatus(PinganFileDeal.file_status.KHKF05_fail.name());
						throw e;
					}
				} else {
					fileDeal.setDescription("无对账文件");
					fileDeal.setStatus(PinganFileDeal.file_status.KHKF05_fail.name());
				}
			} else {
				logger.error("向平安通道发送T1文件失败");
				fileDeal.setStatus(PinganFileDeal.file_status.KHKF05_fail.name());
			}
			fileDealDao.save(fileDeal);
		} catch (Exception e) {
			logger.error("下载对账单失败", e);
			throw e;
		}
	}

}
