package com.cn.flypay.service.payment.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.TroughTrainServeice;
import com.cn.flypay.service.payment.YiBaoService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.yibao.AESUtil;
import com.cn.flypay.utils.yibao.Digest;
import com.cn.flypay.utils.yibao.YiBaoBaseUtil;
import com.cn.flypay.utils.yibao.builder.FeeSetPartsBuilder;
import com.cn.flypay.utils.yibao.builder.RegisterPartsBuilder;
import com.cn.flypay.utils.yibao.builder.TradeReviceQueryPartsBuilder;
import com.cn.flypay.utils.yibao.builder.WithDrawApiPartsBulider;

import net.sf.json.JSONArray;

import com.cn.flypay.utils.yibao.builder.ReceviePartsBuiler;

/**
 * 易宝交易接口处理
 * 
 * @author liangchao
 *
 */
@Service(value = "yiBaoPaymentService")
public class YiBaoPaymentServiceImpl extends AbstractChannelPaymentService {
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private YiBaoService yiBaoService;
	@Autowired
	private BaseDao<TuserCard> userCardDao;
	@Autowired
	private UserService userService;
	@Autowired
	private TroughTrainServeice troughTrainServeice;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	@Autowired
	private BaseDao<Tchannel> channelDao;

	/**
	 * 创建子商户 ServiceMerchant 存放平台报备在易宝的代理商配置信息
	 */
	@Override
	public Map<String, String> createSubMerchantByUserId(Long userId) {
		Map<String, String> resMap = new HashMap<String, String>();

		// 调用易宝创建子商户接口
		JSONObject craeteMerRes = yiBaoService.createSubMerchantByUserId(userId);
		if (!craeteMerRes.containsKey("customerNumebr")) {
			resMap.put("return_code", GlobalConstant.RESP_CODE_999);
			resMap.put("return_msg", craeteMerRes.getString("return_msg"));
			return resMap;
		} else {
			String customerNumebr = craeteMerRes.getString("customerNumebr");
			JSONObject validaInfo = craeteMerRes.getJSONObject("validaInfo");
			// 创建成功，并且保存成功，开始与本地申请的参数信息进行校验
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber; // 代理商编号
			String bindMobile = validaInfo.getString("bindMobile");
			String customerType = "CUSTOMER";
			JSONObject validatRes = yiBaoService.customerInfoForQuery(userId, mainCustomerNumber, bindMobile, customerNumebr, customerType, validaInfo);
			if (!validatRes.getString("code").equals("000")) {
				resMap.put("return_code", GlobalConstant.RESP_CODE_999);
				resMap.put("return_msg", validatRes.getString("return_msg"));
				return resMap;
			} else {
				// 与本地信息校验成功，开始调用审核接口
				JSONObject auditRes = yiBaoService.auditMerchant(userId, mainCustomerNumber, customerNumebr, "SUCCESS", "pass");
				if (auditRes == null) {
					resMap.put("return_code", GlobalConstant.RESP_CODE_999);
					resMap.put("return_msg", "请求通道提供方子商户审核时，请求异常");
					return resMap;
				}
				if (!auditRes.getString("code").equals("000")) {
					resMap.put("return_code", GlobalConstant.RESP_CODE_999);
					resMap.put("return_msg", auditRes.getString("return_msg"));
					return resMap;
				} else {
					// 开始设置费率
					// 查询用户费率&限额配置 虽然易宝有自己的YLAXJYB类型，但是还是读取和银联积分YLZXJ相同的配置
					BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(userId, UserOrder.trans_type.YLZXJ.getCode(), 0); // 0
																																					// 代表D0交易
					// 费率 易联要求不带百分号 百分之3 就写3
					BigDecimal b = rate[0];

					JSONObject fee1 = yiBaoService.setYiBaoFee(userId, customerNumebr, "1", String.valueOf(b));// 下单费率
					if (fee1.getString("return_code").equals("000")) {
						log.info("----银联积分易宝-----user_id=" + userId + "，子商户ID为" + customerNumebr + "的用户，下单费率配置成功");
					}
					JSONObject fee3 = yiBaoService.setYiBaoFee(userId, customerNumebr, "3", "3");// 日结结算收取的固定基本费用
					if (fee3.getString("return_code").equals("000")) {
						log.info("----银联积分易宝-----user_id=" + userId + "，子商户ID为" + customerNumebr + "的用户，日结结算收取的固定基本费用配置成功");
					}
					JSONObject fee4 = yiBaoService.setYiBaoFee(userId, customerNumebr, "4", "0");// 日结结算工作日收取的额外费率
					if (fee4.getString("return_code").equals("000")) {
						log.info("----银联积分易宝-----user_id=" + userId + "，子商户ID为" + customerNumebr + "的用户，日结结算工作日收取的额外费率配置成功");
					}
					JSONObject fee5 = yiBaoService.setYiBaoFee(userId, customerNumebr, "5", "0");// 日结结算节假日收取的额外费率
					if (fee5.getString("return_code").equals("000")) {
						log.info("----银联积分易宝-----user_id=" + userId + "，子商户ID为" + customerNumebr + "的用户，日结结算节假日收取的额外费率配置成功");
					}

					resMap.put("return_code", GlobalConstant.RESP_CODE_SUCCESS);
					resMap.put("return_msg", "开通子商户及信息配置成功");
					return resMap;
				}
			}
		}
	}

	/**
	 * 创建订单
	 */
	@Override
	public Map<String, String> createYLZXOrder_v2(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			// 付款卡信息
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			// 查询结算卡
			TuserCard cardJ = userCardDao.get("select t from TuserCard t left join t.bank left join t.user u  where u.id=" + user.getId() + " and t.isSettlmentCard = 1 ");
			if (cardJ == null) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG", "未设置结算卡");
				return resultMap;
			}

			JSONObject config = cpr.getConfig();

			// 查询用户费率&限额配置 --开始
			log.info("易宝---调用收款接口--下单前检验费率是否一致----user_id = " + user.getId() + "---开始");
			BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(user.getId(), UserOrder.trans_type.YLZXJ.getCode(), inputAccType);
			JSONObject yibaoRateJson = yiBaoService.queryYiBaoFee(user.getId(), config.getString("yibao.merchant_id"), "1");
			BigDecimal yibaoRate = yibaoRateJson.getBigDecimal("rate");
			if (rate[0].compareTo(yibaoRate) != 0) {
				JSONObject fee1 = yiBaoService.setYiBaoFee(user.getId(), config.getString("yibao.merchant_id"), "1", String.valueOf(rate[0]));// 下单费率
				if (fee1.getString("return_code").equals("000")) {
					log.info("易宝---调用收款接口--下单前检验费率配置-user_id=" + user.getId() + "，子商户ID为" + config.getString("yibao.merchant_id") + "的用户，下单费率配置成功");
				}
			}
			log.info("易宝---调用收款接口--下单前检验费率是否一致----user_id = " + user.getId() + "---检测通过");
			// 查询用户费率&限额配置 --结束

			// 查询用户绑定的结算卡是否与易宝的一致 --开始
			log.info("易宝---调用收款接口--下单前检验绑定的结算卡是否一致----user_id = " + user.getId() + "---开始");
			JSONObject yibaoCardJson = yiBaoService.queryMerchantInfo(user.getLoginName(), config.getString("yibao.merchant_id"));
			JSONObject yibaoCardJson2 = JSONObject.parseObject(yibaoCardJson.getString("return_msg"));
			String yibaoCardNo = yibaoCardJson2.getString("bankAccountNumber");
			String yibaoCardNoVal1 = yibaoCardNo.substring(0, 4);
			String yibaoCardNoVal2 = yibaoCardNo.substring(yibaoCardNo.length() - 4, yibaoCardNo.length());

			String cardJNo = cardJ.getCardNo();
			String cardJNoVal1 = cardJNo.substring(0, 4);
			String cardJNoVal2 = cardJNo.substring(cardJNo.length() - 4, cardJNo.length());

			if (!yibaoCardNoVal1.equals(cardJNoVal1) || !yibaoCardNoVal2.equals(cardJNoVal2)) {
				// 易宝绑定的卡和结算卡不一致
				log.info("易宝---调用收款接口--下单前检验绑定的结算卡是否一致----user_id = " + user.getId() + "---检验不一致,易宝的结算卡为" + yibaoCardNo + ",本地的结算卡为" + cardJNo + "---开始更换绑卡信息");
				JSONObject changeCardResJson = yiBaoService.changeMerchantCard(config.getString("yibao.merchant_id"), cardJ.getCardNo(), cardJ.getBank().getBankName());
				if (!changeCardResJson.getString("return_code").equals("000")) {
					resultMap.put("flag", GlobalConstant.RESP_CODE_999);
					resultMap.put("flagMSG", "更新通道结算卡信息失败，请联系客服");
					log.info("易宝---调用收款接口--下单前检验绑定的结算卡是否一致----user_id = " + user.getId() + "---更换绑卡信息失败");
					return resultMap;
				} else {
					log.info("易宝---调用收款接口--下单前检验绑定的结算卡是否一致----user_id = " + user.getId() + "---更换成功");
				}
			} else {
				log.info("易宝---调用收款接口--下单前检验绑定的结算卡是否一致----user_id = " + user.getId() + "---检测一致，通过");
			}
			// 查询用户绑定的结算卡是否与易宝的一致 --结束

			String behavior = "receiveApi";
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;
			String customerNumber = config.getString("yibao.merchant_id"); // 子商户编号

			String requestId = "YB" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); // 银联在线积分
			String source = "B"; // D:卡号收款 B：店主代付 S:短信收款 T:二维码收款
			String amount = money.toString(); // 订单金额
			
//			BigDecimal amount = new BigDecimal(money).setScale(2,BigDecimal.ROUND_DOWN);
			String mcc = "5311"; // 5311:百货商店 4511:航空公司 4733：大型景区售票
			String callBackUrl = YiBaoBaseUtil.callBackUrl; // 收款成功回调地址

			// String callBackUrl = config.getString("yibao.bankUrl");
			// //收款成功回调地址

			// 触发该链接，供APP捕捉做相应处理
//			String webCallBackUrl = frontUrl; // 支付成功页面重定向地址
			String webCallBackUrl = "http://www.baidu.com"; // 支付成功页面重定向地址
			String mobileNumber = card.getPhone(); // 付款人手机号
			// 可选 支付方式为S时用到
			String smgCallBackUrl = ""; // 短信发送成功回调地址
			// 可选 透过支付卡号到wap收银台,可以跳过卡号输入页面
			String payerBankAccountNo = card.getCardNo(); // 支付卡号

			// 0.0.19版本 新增的参数，暂时用不到
			// 可选 代理商开通需要开通“指定提现卡号”功能
			String withdrawCardNo = cardJ.getCardNo(); // 指定提现卡号 不加入签名
			// 可选 代理商需要先开通“逐笔结算功能”。
			String autoWithdraw = "true"; // 逐笔结算 是否自动发起出款请求 不加入签名
			// 可选 代理商需要先开通“定制手续费”功能
			String customFee = "false"; // 定制手续费 (提供方声称不可用)
			// 可选
			String withdrawCallBackUrl = ""; // 逐笔结算回调地址

			StringBuilder signature = new StringBuilder();
			signature.append(source == null ? "" : source).
			append(mainCustomerNumber == null ? "" : mainCustomerNumber).
			append(customerNumber == null ? "" : customerNumber).
					append(amount == null ? "" : amount).
			append(mcc == null ? "" : mcc).
			append(requestId == null ? "" : requestId).
			append(mobileNumber == null ? "" : mobileNumber).
			append(callBackUrl == null ? "" : callBackUrl).
			append(webCallBackUrl == null ? "" : webCallBackUrl).
			append(smgCallBackUrl == null ? "" : smgCallBackUrl).
			append(payerBankAccountNo == null ? "" : payerBankAccountNo);
			
			
			// 生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);

			JSONObject reqJson = new JSONObject();
			reqJson.put("mainCustomerNumber", mainCustomerNumber);
			reqJson.put("customerNumber", customerNumber);
			reqJson.put("requestId", requestId);
			reqJson.put("source", source);
			reqJson.put("amount", amount);
			reqJson.put("mcc", mcc);
			reqJson.put("callBackUrl", callBackUrl);
			reqJson.put("webCallBackUrl", webCallBackUrl);
			reqJson.put("mobileNumber", mobileNumber);
			reqJson.put("smgCallBackUrl", smgCallBackUrl);
			reqJson.put("payerBankAccountNo", payerBankAccountNo);
			reqJson.put("withdrawCardNo", withdrawCardNo);
			reqJson.put("autoWithdraw", autoWithdraw);
			reqJson.put("hmac", hmac);

			Part[] parts = new ReceviePartsBuiler().setMainCustomerNumber(mainCustomerNumber).
					setCustomerNumber(customerNumber).
					setRequestId(requestId).
					setSource(source).
					setAmount(amount).
					setMcc(mcc).
					setCallBackUrl(callBackUrl).
					setWebCallBackUrl(webCallBackUrl).
					setMobileNumber(mobileNumber).
					setSmgCallBackUrl(smgCallBackUrl).
					setPayerBankAccountNo(payerBankAccountNo).
					setWithdrawCardNo(withdrawCardNo).
					setAutoWithdraw(autoWithdraw).
					setHamc(hmac).generateParams();

			log.info("易宝---调用收款接口--商户号为" + customerNumber + " 请求内容为" + reqJson.toJSONString());
			JSONObject result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
			log.info("易宝---调用收款接口--商户号为" + customerNumber + " 返回内容为" + result.toJSONString());

			if (result == null) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "连接通道失败");
				return resultMap;
			} else {
				if (!result.getString("code").equals("0000")) {
					resultMap.put("flag", GlobalConstant.RESP_CODE_999);
					resultMap.put("flagMSG", result.getString("message"));
					return resultMap;
				} else {
					String urlAes = result.getString("url");
					String url = AESUtil.decrypt(urlAes, YiBaoBaseUtil.hmacKey.substring(0, 16));
					log.info("---银联积分---易宝通道---收款接口 ---请求成功，返回页面为:" + url);

					// 创建订单 yibao.merchant_id
					desc = user.getRealName() + "_" + user.getLoginName() + "正在用卡" + card.getCardNo() + "向" + cardJ.getCardNo() + "支付" + money + "元";
					log.info("---银联积分---易宝通道---收款接口 ---请求成功,开始保存订单，订单号为:" + requestId);
					String desc_detail = "易宝通道：" + desc;
					userOrderService.createTransOrder(user.getId(), requestId, null, null, UserOrder.trans_type.YLZXJ.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc_detail, transPayType, cpr.getChannel(), inputAccType,
							angentType);
					log.info("---银联积分---易宝通道---收款接口 ---请求成功,保存订单成功，订单号为:" + requestId);
					// 为3时表示下单成功，跳转至主页
					resultMap.put("type", "3");
					resultMap.put("requestId", requestId);
					resultMap.put("url", url);
					resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
					return resultMap;
				}
			}

		} catch (Exception e) {
			log.error("---银联积分---易宝通道---收款接口 ---异常:", e);
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_051));
			e.printStackTrace();
		}

		return resultMap;
	}

	/**
	 * 结算接口(未使用)
	 * 
	 * @param customerNumber
	 *            子商户编号
	 * @param externalNo
	 *            结算请求唯一号
	 * @param transferWay
	 *            结算类型 1：T0自助结算 2：T1自助结算
	 * @param amount
	 *            结算金额
	 * @param callBackUrl
	 *            结算回调地址 为空时，不会回调
	 * @return
	 */
	private Map<String, String> withDraw(String customerNumber, String externalNo, String transferWay, String amount, String callBackUrl) {
		Map<String, String> resultMap = new HashMap<String, String>();

		try {
			// 传递所需参数
			String behavior = "withDrawApi";

			String mainCustomerNumber = YiBaoBaseUtil.customerNumber; // 代理商编号

			StringBuilder signature = new StringBuilder();
			signature.append(customerNumber == null ? "" : customerNumber).append(mainCustomerNumber == null ? "" : mainCustomerNumber).append(externalNo == null ? "" : externalNo).append(transferWay == null ? "" : transferWay)
					.append(amount == null ? "" : amount).append(callBackUrl == null ? "" : callBackUrl);

			// 生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);

			Part[] parts = new WithDrawApiPartsBulider().setMainCustomerNumber(mainCustomerNumber).setCustomerNumber(customerNumber).setExternalNo(externalNo).setTransferWay(transferWay).setAmount(amount).setCallBackUrl(callBackUrl)
					.setHmac(hmac).generateParams();

			// 调用公用请求模块
			JSONObject result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);

			if (StringUtil.isNotBlank(result.getString("code")) && result.getString("code").equals("0000")) {

			} else {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultMap;
	}

	/**
	 * 重新查询订单
	 */
	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {
		Map<String, String> resultMap = new HashMap<String, String>();

		UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNum);
		if (userOrder == null) {
			log.info("----银联积分易宝--调用重新查询订单---订单：" + orderNum + " 不存在或已经被成功处理了");
			return null;
		}

		JSONObject channelJson = channelService.getChannelConfig(userOrder.getChannelId());

		// 传递所需参数
		String behavior = "tradeReviceQuery";
		// 必选
		String mainCustomerNumber = YiBaoBaseUtil.customerNumber; // 代理商编号
		String customerNumber = channelJson.getString("customerNumber"); // 子商户编号

		// 可选
		String requestId = orderNum; // 收款订单号

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(userOrder.getCreateTime());
		calendar.add(Calendar.DAY_OF_MONTH, +1);
		Date afterOrderDay = calendar.getTime(); // 下单的后一天

		// 可选
		String createTimeBegin = sdf1.format(userOrder.getCreateTime()); // 请求时间-开始时间
		// 可选
		String createTimeEnd = sdf1.format(afterOrderDay); // 请求时间-结束时间
		// 可选
		String payTimebegin = ""; // 支付开始时间
		// 可选
		String payTimeEnd = ""; // 支付结束时间
		// 可选
		String lastUpdateTimeBegin = ""; // 收款状态更新时间--开始时间
		// 可选
		String lastUpdateTimeEnd = ""; // 收款状态更新时间--结束时间
		// 可选
		String status = ""; // 订单状态 INIT:未支付 SUCCESS:成功 FAIL:失败 FROZEN：冻结
							// THAWED:解冻 REVERSE: 冲正

		String busiType = "COMMON"; // 订单类型 COMMON:普通交易
		String pageNo = "1"; // 必须是正整数--页数 每页显示20条数据

		StringBuilder signature = new StringBuilder();
		signature.append(mainCustomerNumber == null ? "" : mainCustomerNumber).append(customerNumber == null ? "" : customerNumber).append(requestId == null ? "" : requestId).append(createTimeBegin == null ? "" : createTimeBegin)
				.append(createTimeEnd == null ? "" : createTimeEnd).append(payTimebegin == null ? "" : payTimebegin).append(payTimeEnd == null ? "" : payTimeEnd).append(lastUpdateTimeBegin == null ? "" : lastUpdateTimeBegin)
				.append(lastUpdateTimeEnd == null ? "" : lastUpdateTimeEnd).append(status == null ? "" : status).append(busiType == null ? "" : busiType).append(pageNo == null ? "" : pageNo);

		// 生成签名
		String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);

		JSONObject reqJson = new JSONObject();
		reqJson.put("mainCustomerNumber", mainCustomerNumber);
		reqJson.put("customerNumber", customerNumber);
		reqJson.put("requestId", requestId);
		reqJson.put("createTimeBegin", createTimeBegin);
		reqJson.put("createTimeEnd", createTimeEnd);
		reqJson.put("payTimebegin", payTimebegin);
		reqJson.put("payTimeEnd", payTimeEnd);
		reqJson.put("lastUpdateTimeBegin", lastUpdateTimeBegin);
		reqJson.put("status", status);
		reqJson.put("busiType", busiType);
		reqJson.put("pageNo", pageNo);

		Part[] parts = new TradeReviceQueryPartsBuilder().setMainCustomerNumber(mainCustomerNumber).setRequestId(requestId).setCreateTimeBegin(createTimeBegin).setCreateTimeEnd(createTimeEnd).setPayTimebegin(payTimebegin)
				.setPayTimeEnd(payTimeEnd).setLastUpdateTimeBegin(lastUpdateTimeBegin).setLastUpdateTimeEnd(lastUpdateTimeEnd).setStatus(status).setBusiType(busiType).setPageNo(pageNo).setHmac(hmac).generateParams();

		log.info("----银联积分易宝--调用重新查询订单---请求参数集合为:" + reqJson.toJSONString());
		JSONObject res = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		log.info("----银联积分易宝--调用重新查询订单---请求订单号为:" + requestId + ",返回结果集合为" + res.toJSONString());

		try {
			if (res != null) {
				if (res.containsKey("code") && res.getString("code").equals("0000")) {
					String tradeReceives = res.getString("tradeReceives");
					String tradeReceives2 = tradeReceives.substring(1, tradeReceives.length() - 1); // 去掉大括号
																									// []
					JSONObject tradeReceivesObj = JSONObject.parseObject(tradeReceives2);
					StringBuilder signature2 = new StringBuilder();
					signature2.append(res.getString("code") == null ? "" : res.getString("code")).append(res.getString("message") == null ? "" : res.getString("message"))
							.append(res.getString("totalRecords") == null ? "" : res.getString("totalRecords")).append(tradeReceives == null ? "" : tradeReceives);

					// 生成签名
					String hmac2 = Digest.hmacSign(signature2.toString(), YiBaoBaseUtil.hmacKey);
					if (!hmac2.equals(res.getString("hmac"))) {
						log.info("----银联积分易宝--调用重新查询订单---返回结果验签失败");
						return null;
					}
					log.info("----银联积分易宝--调用重新查询订单---验签成功，保存订单信息--开始");
					// 开始保存订单信息

					PayOrder payOrder = new PayOrder();
					payOrder.setPayAmt(BigDecimal.valueOf(tradeReceivesObj.getDoubleValue("amount")));
					payOrder.setRealAmt(BigDecimal.valueOf(tradeReceivesObj.getDoubleValue("amount")));
					payOrder.setPayFinishDate(tradeReceivesObj.getString("payTime")); // payTime为付款时间
					payOrder.setFinishDate(tradeReceivesObj.getDate("payTime"));
					payOrder.setPayNo(tradeReceivesObj.getString("requestId"));
					Boolean flag = false;
					if (tradeReceivesObj.getString("status").equals("SUCCESS")) {
						flag = true;
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode()); // 支付成功
					} else if (tradeReceivesObj.getString("status").equals("FAIL")) {
						flag = true;
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode()); // 订单失败
					}

					// 对于易宝的其它订单状态，系统不做处理
					if (flag) {
						userOrderService.finishInputOrderStatus(orderNum, payOrder);
						log.info("----银联积分易宝--调用重新查询订单---验签成功，保存订单信息--成功");
					} else {
						log.info("----银联积分易宝--调用重新查询订单---验签成功，保存订单信息--判断为非成功和失败类型，由于平台处理逻辑不包含该状态类型，默认不做处理，略过");
					}
				} else {
					resultMap.put("flag", GlobalConstant.RESP_CODE_051);
					resultMap.put("flagMSG", res.getString("message"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
			String oriString = "A7909F8E773270CC58CFCCFCDC1F6DCEE5D2E431AA71F4831A14118DE4FD661D0452CEAA07449B13F4549728F263C727742D0E177A72AEBEC9482D7903D784070C05F089DF8F0FC01D0D86A95488BA2B";
			
			String kkkkString = YiBaoBaseUtil.hmacKey.substring(0, 16);
			System.out.println(kkkkString);
			String url = AESUtil.decrypt(oriString, kkkkString);
			
			System.out.println(url);
	}

}
