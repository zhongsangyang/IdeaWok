package com.cn.flypay.service.payment.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.TchannelT0Tixian;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.payment.minsheng.SMZF001;
import com.cn.flypay.pageModel.payment.minsheng.SMZF002;
import com.cn.flypay.pageModel.payment.minsheng.SMZF003;
import com.cn.flypay.pageModel.payment.minsheng.SMZF006;
import com.cn.flypay.pageModel.payment.minsheng.SMZF010;
import com.cn.flypay.pageModel.payment.minsheng.SMZF020;
import com.cn.flypay.pageModel.payment.minsheng.SMZF021;
import com.cn.flypay.pageModel.payment.minsheng.SMZF022;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.ChannelT0Tixian;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.sys.ChannelT0TixianService;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.minsheng.MinShengMerchantInputMinShengUtil;

/**
 * Created by sunyue on 16/8/1.
 */
@Service(value = "minshengPaymentService")
public class MinshengPaymentServiceImpl extends AbstractChannelPaymentService {

	private Log log = LogFactory.getLog(getClass());
	@Value("${minsheng_statement_root_path}")
	private String minsheng_statement_root_path;
	@Autowired
	private ChannelT0TixianService channelT0TixianService;

	@Override
	public UserMerchantConfig createSubMerchant(ServiceMerchant sm, Map<String, String> params) {

		try {
			JSONObject config = JSONObject.parseObject(sm.getConfig());
			SMZF001 merchant001 = new SMZF001();
			merchant001.setPayWay(sm.getType() == 200 ? "ZFBZF" : "WXZF");
			merchant001.setCategory(StringUtil.isNotBlank(params.get("category")) ? params.get("category") : config.getString("category"));
			merchant001.setMerchantId("MS_" + commonService.getUniqueTradeSn());
			merchant001.setMerchantName(params.get("merchantName"));
			merchant001.setShortName(params.get("shortName"));
			merchant001.setMerchantAddress(StringUtil.isNotBlank(params.get("address")) ? params.get("address") : config.getString("address"));
			merchant001.setServicePhone(StringUtil.isNotBlank(params.get("servicePhone")) ? params.get("servicePhone") : config.getString("servicePhone"));
			merchant001.setCooperator(sm.getAppId());
			String responseStr = MinShengMerchantInputMinShengUtil.doPost(merchant001);
			Map<String, String> result = XmlMapper.xml2Map(responseStr);
			if (result != null && result.containsKey("merchantCode")) {
				if (StringUtil.isNotBlank(result.get("merchantCode"))) {
					UserMerchantConfig umc = new UserMerchantConfig();
					umc.setSubMerchantId(result.get("merchantCode"));
					umc.setServiceMerchantId(sm.getId());
					JSONObject json = new JSONObject();
					json.put("merchant_id", result.get("merchantCode"));
					umc.setConfig(json.toJSONString());
					umc.setType(sm.getType());
					return umc;
				} else {
					log.info("mingsheng business error : err_code " + result.get("err_code") + ", err_code_des " + result.get("err_code_des"));
				}
			} else {
				log.info("mingsheng error : can not request api");
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	@Override
	public Map<String, String> createUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, Integer angentType, String desc) throws Exception {

		Integer orderType = cpr.getChannel().getType();
		String type = UserOrder.trans_type.WXQR.name();
		if (orderType - UserOrder.trans_type.ALQR.getCode() == 0) {
			type = UserOrder.trans_type.ALQR.name();
		}
		if (orderType - UserOrder.trans_type.QQQR.getCode() == 0) {
			type = UserOrder.trans_type.QQQR.name();
		}
		if (orderType - UserOrder.trans_type.BDQR.getCode() == 0) {
			type = UserOrder.trans_type.BDQR.name();
		}
		JSONObject channelConfig = cpr.getConfig();
		String merchantCode = channelConfig.getString("merchant_code");
		if (cpr.getUserMerchantConfig() != null) {
			merchantCode = cpr.getUserMerchantConfig().getSubMerchantId();
		}
		String callBackUrl = channelConfig.getString("callBack");

		log.info("创建民生二维码订单 begin");

		/* 用户订单号= 用户ID + 14位时间戳+ userType（2位） */
		String out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
		SMZF002 s002 = new SMZF002();
		s002.setRequestId(out_trade_no);
		s002.setMerchantCode(merchantCode);
		s002.setCallBack(callBackUrl);
		s002.setTotalAmount(String.valueOf(money));
		s002.setSubject(user.getOrganizationAppName());
		String order_desc = String.format(desc, money);
		s002.setDesc(desc);
		s002.setCooperator(channelConfig.getString("cooperator"));
		try {
			String responseStr = MinShengMerchantInputMinShengUtil.doPost(s002);
			Map<String, String> result = XmlMapper.xml2Map(responseStr);
			if (result != null && result.containsKey("qrCode")) {
				if (StringUtil.isNotBlank(result.get("qrCode"))) {
					log.info("Wechatpay request success");
					result.put("orderNum", out_trade_no);
					try {
						userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, orderType, money, UserOrder.cd_type.D.name(), null, null, order_desc, transPayType, cpr.getChannel(),
								inputAccType, angentType);
						result.put("return_code", "SUCCESS");
						result.put("result_code", "SUCCESS");
						result.put("code_url", result.get("qrCode"));
						log.info(result.get("qrCode"));
					} catch (Exception e) {
						log.error(e);
						throw e;
					}
					return result;
				} else {
					log.info("mingsheng business error : err_code " + result.get("err_code") + ", err_code_des " + result.get("err_code_des"));
				}
			} else {
				log.info("mingsheng error : can not request api");
			}
		} catch (Exception e) {
			log.error("mingsheng error ", e);
			throw e;
		}
		log.info("创建民生二维码订单 end");
		return null;

	}

	@Override
	public Map<String, String> createSmUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, String authCode, Double money, Integer transPayType, Integer angentType, String desc)
			throws Exception {
		log.info("创建民生二扫码订单 start");
		Integer orderType = cpr.getChannel().getType();
		String type = UserOrder.trans_type.WXSM.name();
		if (orderType - UserOrder.trans_type.ALSM.getCode() == 0) {
			type = UserOrder.trans_type.ALSM.name();
		}
		JSONObject channelConfig = cpr.getConfig();
		String callBackUrl = channelConfig.getString("callBack");
		String merchantCode = channelConfig.getString("merchant_code");

		/* 用户订单号= 用户ID + 14位时间戳+ userType（2位） */
		String out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
		SMZF003 s003 = new SMZF003();
		s003.setCallBack(callBackUrl);
		s003.setRequestId(out_trade_no);
		s003.setMerchantCode(merchantCode);
		s003.setScene("1");
		s003.setSubject(user.getOrganizationAppName());
		s003.setAuthCode(authCode);
		s003.setTotalAmount(money.toString());

		String order_desc = String.format(desc, money);
		s003.setDesc(order_desc);
		s003.setCooperator(channelConfig.getString("cooperator"));
		try {
			userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, orderType, money, UserOrder.cd_type.D.name(), null, null, desc, transPayType, cpr.getChannel(), inputAccType,
					angentType);
			String responseStr = MinShengMerchantInputMinShengUtil.doPost(s003);
			log.info("民生扫码回馈" + responseStr);
			Map<String, String> result = XmlMapper.xml2Map(responseStr);
			if (result != null && result.containsKey("respType")) {
				UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(out_trade_no);
				/* 完成订单后，更新原有订单 */
				if (userOrder != null) {
					PayOrder payOrder = new PayOrder();
					if (result.containsKey("totalAmount") && StringUtil.isNotBlank(result.get("totalAmount"))) {
						payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(result.get("totalAmount"))));
					}

					if (result.containsKey("buyerPayAmount") && StringUtil.isNotBlank(result.get("buyerPayAmount"))) {
						payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(result.get("buyerPayAmount"))));
					}

					if (result.containsKey("payTime") && StringUtil.isNotBlank(result.get("payTime"))) {
						payOrder.setPayFinishDate(result.get("payTime"));
					}

					if (result.containsKey("channelNo") && StringUtil.isNotBlank(result.get("channelNo"))) {
						payOrder.setPayNo(result.get("channelNo"));
					}
					String return_msg = result.get("respMsg");
					String errCode = result.get("respCode");
					payOrder.setErrorInfo(return_msg);
					payOrder.setErrorCode(errCode);
					Boolean changeFlag = false;
					if (result.get("respType").equals("S") && (result.get("respCode").equals("000000") || result.get("respCode").equals("000090"))) {
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
						result.put("return_code", "SUCCESS");
						result.put("result_code", "SUCCESS");
						changeFlag = true;
					} else if (result.get("respType").equals("E")) {
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
						log.error("支付失败！out_trade_no:" + out_trade_no + ", err_code:" + errCode + ", return_msg:" + return_msg);
						changeFlag = true;
					} else {
						result.put("return_code", GlobalConstant.WAITING);
					}
					if (changeFlag) {
						try {
							userOrderService.finishInputOrderStatus(out_trade_no, payOrder);
							return result;
						} catch (Exception e) {
							log.error("----订单回调异常", e);
						}
					}
				} else {
					log.info("订单：" + out_trade_no + " 不存在或已经被成功处理了");
				}
			} else {
				log.info("minsheng communication error : " + result.get("return_msg"));
			}

		} catch (Exception e) {
			log.error("minsheng error ", e);
			throw e;
		}
		log.info("创建民生二扫码订单 end");
		return null;

	}

	@Override
	public Map<String, String> createOnLineOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, String desc, Map<String, String> params) throws Exception {

		Integer orderType = cpr.getChannel().getType();
		String type = UserOrder.trans_type.WXOL.name();
		if (orderType - UserOrder.trans_type.ALOL.getCode() == 0) {
			type = UserOrder.trans_type.ALOL.name();
		}
		/* 用户订单号= 用户ID + 14位时间戳+ userType（2位） */
		String out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
		String callBackUrl = cpr.getConfig().getString("callBack");

		JSONObject channelConfig = cpr.getConfig();

		SMZF010 s010 = new SMZF010();
		s010.setCallBack(callBackUrl);
		s010.setRequestId(out_trade_no);
		s010.setMerchantCode(channelConfig.getString("merchant_code"));
		s010.setTotalAmount(money.toString());
		s010.setSubject(desc);
		s010.setCooperator(channelConfig.getString("cooperator"));
		if (orderType - UserOrder.trans_type.WXOL.getCode() == 0) {
			s010.setSubAppid((String) ApplicatonStaticUtil.getAppStaticData("wxaccount.ggh.appid"));
		}
		s010.setUserId(params.get("userId"));

		try {
			String responseStr = MinShengMerchantInputMinShengUtil.doPost(s010);
			Map<String, String> result = XmlMapper.xml2Map(responseStr);
			log.info(result);
			if (result != null && (orderType - UserOrder.trans_type.WXOL.getCode() == 0 && StringUtil.isNotBlank(result.get("wxjsapiStr")))
					|| (orderType - UserOrder.trans_type.ALOL.getCode() == 0 && StringUtil.isNotBlank(result.get("channelNo")))) {
				try {
					userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, orderType, money, UserOrder.cd_type.D.name(), null, null, desc, transPayType, cpr.getChannel(),
							inputAccType, 0);
					return result;
				} catch (Exception e) {
					log.error(e);
					throw e;
				}
			} else {
				log.info("minsheng business error : err_code " + result.get("err_code") + ", err_code_des " + result.get("err_code_des"));
			}
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		return null;
	}

	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {

		UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNum);
		if (userOrder != null) {
			SMZF006 f006 = new SMZF006();
			f006.setOriReqMsgId(orderNum);
			try {
				Tchannel tcl = channelService.getTchannelInCache(userOrder.getChannelId());
				f006.setCooperator(tcl.getAccount());
				String responseStr = MinShengMerchantInputMinShengUtil.doPost(f006);
				log.info(responseStr);
				Map<String, String> result = XmlMapper.xml2Map(responseStr);
				PayOrder payOrder = new PayOrder();
				if (result.containsKey("totalAmount") && StringUtil.isNotBlank(result.get("totalAmount"))) {
					payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(result.get("totalAmount"))));
				}

				if (result.containsKey("buyerPayAmount") && StringUtil.isNotBlank(result.get("buyerPayAmount"))) {
					payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(result.get("buyerPayAmount"))));
				}

				if (result.containsKey("payTime") && StringUtil.isNotBlank(result.get("payTime"))) {
					payOrder.setPayFinishDate(result.get("payTime"));
					payOrder.setFinishDate(DateUtil.convertStringToDate(DateUtil.FORMAT_YYYYMMDDHHmmss, result.get("payTime")));
				}

				if (result.containsKey("channelNo") && StringUtil.isNotBlank(result.get("channelNo"))) {
					payOrder.setPayNo(result.get("channelNo"));
				}
				String return_msg = result.get("respMsg");
				String errCode = result.get("respCode");
				payOrder.setErrorInfo(return_msg);
				payOrder.setErrorCode(errCode);
				Boolean rechangeFlag = false;
				if (result.get("oriRespType").equals("S") && result.get("oriRespCode").equals("000000")) {
					payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
					result.put("return_code", "SUCCESS");
					result.put("result_code", "SUCCESS");
					rechangeFlag = true;
				} else if (result.get("oriRespType").equals("E")) {
					payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
					log.error("支付失败！out_trade_no:" + orderNum + ", err_code:" + result.get("oriRespCode") + ", return_msg:" + result.get("oriRespMsg"));
					rechangeFlag = true;
				} else if (result.get("oriRespType").equals("R")) {
					log.info("未支付！out_trade_no:" + orderNum + "，请稍后重试");
				}
				if (rechangeFlag) {
					try {
						userOrderService.finishInputOrderStatus(orderNum, payOrder);
						return result;
					} catch (Exception e) {
						log.error("----订单回调异常", e);
					}
				}
			} catch (Exception e) {
				log.error("----订单回调异常", e);
			}
		} else {
			log.info("订单：" + orderNum + " 不存在或已经被成功处理了");
		}
		return null;
	}

	@Override
	public Boolean dealDownloadStatement(String dateStr) {
		List<JSONObject> channelCfgs = channelService.getAvailableChannelConfigByChannelName("MINSHENG");
		for (JSONObject channelConfig : channelCfgs) {
			SMZF020 f020 = new SMZF020();
			f020.setSettleDate(dateStr);
			f020.setFileType("1");
			String cooperator = channelConfig.getString("cooperator");
			f020.setCooperator("SMZF_SHFF_HD_T0");
			try {
				String responseStr = MinShengMerchantInputMinShengUtil.doPost(f020);
				Map<String, String> response = XmlMapper.xml2Map(responseStr);

				String statementContent = response.get("content");
				if (StringUtil.isNotBlank(statementContent)) {
					// 指定希望保存的文件路径
					File file = new File(minsheng_statement_root_path);
					if (!file.exists()) {
						file.mkdirs();
					}
					String filePath = minsheng_statement_root_path + File.separator + cooperator + "_" + dateStr + ".txt";
					FileUtils.writeStringToFile(new File(filePath), statementContent, "utf-8");
					/* 处理对账单 */
					tradeStatementService.dealMinshengStatement(filePath, dateStr);
				} else {
					log.info(dateStr + "无对账单");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void dealChannelT0Tixian(Long channelId, Map<String, String> params) {
		Channel chl = channelService.get(channelId);
		String out_trade_no = commonService.getUniqueOrderByType("MSTX", Long.parseLong(params.get("operator_id")));
		SMZF021 wx021 = new SMZF021();
		JSONObject json = JSONObject.parseObject(chl.getConfig());
		wx021.setMerchantCode(json.getString("merchant_code"));
		wx021.setRequestId(out_trade_no);
		wx021.setCallBack(MinShengMerchantInputMinShengUtil.Min_sheng_tixian_callBack);
		wx021.setCooperator(chl.getAccount());
		try {
			log.info("--------D0民生提现开始--------"+out_trade_no+"--------");
			String responseStr = MinShengMerchantInputMinShengUtil.doPost(wx021);
			Map<String, String> result = XmlMapper.xml2Map(responseStr);
			log.info(result);
			ChannelT0Tixian channelT0Tixian = new ChannelT0Tixian(out_trade_no, null, null, null, channelId, params.get("operator_name"));
			channelT0Tixian.setErrorCode(result.get("respCode"));
			channelT0Tixian.setErrorDesc(result.get("respType")+result.get("respMsg"));
			channelT0TixianService.add(channelT0Tixian);
			log.info("--------D0民生提现结算----------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendT0TixianSearch(String orderNum) {
		try {
			TchannelT0Tixian t = channelT0TixianService.getTodoTchannelT0TixianByOrderNum(orderNum);
			if (t != null) {
				SMZF022 wx022 = new SMZF022();
				wx022.setOriReqMsgId(orderNum);
				wx022.setCooperator(t.getChannel().getAccount());
				String responseStr = MinShengMerchantInputMinShengUtil.doPost(wx022);
				Map<String, String> result = XmlMapper.xml2Map(responseStr);
				/* 完成订单后，更新原有订单 */
				if (t != null) {
					if (result.containsKey("drawAmount") && StringUtil.isNotBlank(result.get("drawAmount"))) {
						t.setAmt(BigDecimal.valueOf(Double.parseDouble(result.get("drawAmount"))));
					}
					if (result.containsKey("drawFee") && StringUtil.isNotBlank(result.get("drawFee"))) {
						t.setDrawFee(BigDecimal.valueOf(Double.parseDouble(result.get("drawFee"))));
					}
					if (result.containsKey("tradeFee") && StringUtil.isNotBlank(result.get("tradeFee"))) {
						t.setTradeFee(BigDecimal.valueOf(Double.parseDouble(result.get("tradeFee"))));
					}
					String return_msg = result.get("oriRespMsg");
					String errCode = result.get("oriRespCode");
					t.setErrorDesc(return_msg);
					t.setErrorCode(errCode);
					Boolean rechangeFlag = false;
					if (result.get("oriRespType").equals("S") && result.get("oriRespCode").equals("00")) {
						t.setStatus(UserOrder.order_status.SUCCESS.getCode());
						t.setFinishDate(new Date());
						rechangeFlag = true;
					} else if (result.get("oriRespType").equals("E")) {
						t.setStatus(UserOrder.order_status.FAILURE.getCode());
						rechangeFlag = true;
					}
					if (rechangeFlag) {
						try {
							channelT0TixianService.updateTchannel(t);
						} catch (Exception e) {
							log.error("----订单回调异常", e);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
