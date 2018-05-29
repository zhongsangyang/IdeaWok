package com.cn.flypay.service.payment.impl;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.model.sys.TchannelT0Tixian;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.ChannelT0Tixian;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.sys.ChannelT0TixianService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.xinke.XinkePayUtil;

/**
 * Created by sunyue on 16/12/16.
 */
@Service(value = "xinkePaymentService")
public class XinkePaymentServiceImpl extends AbstractChannelPaymentService {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private ChannelT0TixianService channelT0TixianService;

	@Override
	public UserMerchantConfig createSubMerchant(ServiceMerchant sm, Map<String, String> params) {

		try {
			JSONObject config = JSONObject.parseObject(sm.getConfig());
			String[] keys = { "insNum", "payType", "merName", "regShortName", "merAddress", "merStat", "funcStat", "merType", "legalPerson", "legalPersonCertType", "legalPersonCertNm",
					"legalPersonCertExpire", "contactPerson", "contactMobile", "debitCardName", "debitCardLines", "debitCardNum", "WXT0", "ZFBT0", "WXT1", "ZFBT1", "factorageT0", "factorageT1",
					"bankName", "bankBranchName", "provName", "cityName", "isPrivate" };
			String[] inputParams = { sm.getAppId(), sm.getType() == 200 ? "02" : "01", params.get("merchantName"), params.get("merchantName"),
					StringUtil.isNotBlank(params.get("address")) ? params.get("address") : config.getString("address"), "1", "YYYYYYYYYY", "1", "芦强", "0", "152822199012293814", "20250914", "冯梁",
					"13052222696", "芦强", "03080000", "6214852111454099", "0.26", "0.26", "0.24", "0.24", "0", "0", "招商银行", "上海晨晖支行", "上海", "上海", "N" };
			String responseStr = XinkePayUtil.build(keys, inputParams, "enter");
			log.info(responseStr);
			Map<String, String> result = XmlMapper.xml2Map(responseStr);
			if (result != null && result.containsKey("RSPCOD") && "000000".equals(result.get("RSPCOD"))) {
				if (StringUtil.isNotBlank(result.get("merNum"))) {
					UserMerchantConfig umc = new UserMerchantConfig();
					umc.setSubMerchantId(result.get("merNum"));
					umc.setServiceMerchantId(sm.getId());
					JSONObject json = new JSONObject();
					json.put("merchant_id", result.get("merchantCode"));
					umc.setConfig(json.toJSONString());
					umc.setType(sm.getType());
					return umc;
				}
			} else {
				log.info("xinke business error : err_code " + result.get("RSPCOD") + ", err_code_des " + result.get("RSPMSG"));
			}
		} catch (Exception e) {
			log.error("xinke create userMerchant error", e);
		}
		return null;
	}

	@Override
	public Map<String, String> createUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, Integer agentType, String desc) throws Exception {

		log.info("创建欣客二维码订单 begin");
		Integer orderType = cpr.getChannel().getType();
		String type = UserOrder.trans_type.WXQR.name();
		String payType = "03";// 微信
		if (orderType - UserOrder.trans_type.ALQR.getCode() == 0) {
			type = UserOrder.trans_type.ALQR.name();
			payType = "04";// 支付宝
		}
		JSONObject channelConfig = cpr.getConfig();
		String merchantCode = channelConfig.getString("xinke.merchant_id");

		/* 用户订单号= 用户ID + 14位时间戳+ userType（2位） */
		String out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
		String[] keys = { "INSTID", "USRID", "OUTORDERID", "TXAMT", "BODY", "TXNTYPE", "NOTIFYURL" };
		String[] params = { cpr.getChannel().getAccount(), merchantCode, out_trade_no, String.valueOf(money), desc, payType, XinkePayUtil.xinke_notify_url };
		try {
			String responseStr = XinkePayUtil.build(keys, params, "online_order_dopay.xml");
			Map<String, String> result = XmlMapper.xml2Map(responseStr);
			if (result != null && result.containsKey("QR_CODE_DATA")) {
				if (StringUtil.isNotBlank(result.get("QR_CODE_DATA"))) {
					result.put("orderNum", out_trade_no);
					try {
						userOrderService.createTransOrder(user.getId(), out_trade_no, result.get("ORDER_ID"), result.get("ORD_NO"), orderType, money, UserOrder.cd_type.D.name(), null, null, desc,
								transPayType, cpr.getChannel(), inputAccType, agentType);
						result.put("return_code", "SUCCESS");
						result.put("result_code", "SUCCESS");
						result.put("code_url", result.get("QR_CODE_DATA"));
						log.info(result.get("QR_CODE_DATA"));
					} catch (Exception e) {
						log.error(e);
						throw e;
					}
					return result;
				} else {
					log.info("xinke business error : err_code " + result.get("err_code") + ", err_code_des " + result.get("err_code_des"));
				}
			} else {
				log.info("xinke error : can not request api");
			}
		} catch (Exception e) {
			log.error("xinke error ", e);
			throw e;
		}
		log.info("创建欣客二维码订单 end");
		return null;

	}

	@Override
	public Map<String, String> createOnLineOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, String desc, Map<String, String> params) throws Exception {

		return null;
	}

	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {

		UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNum);
		if (userOrder != null) {
			try {
				String[] keys = { "ORD_ID", "TXNTYPE" };
				String payType = "03";// 微信
				if (orderNum.startsWith("AL")) {
					payType = "04";
				}
				String[] params = { userOrder.getPayOrderBusNo(), payType };
				String responseStr = XinkePayUtil.build(keys, params, "online_do_query.xml");
				Map<String, String> result = XmlMapper.xml2Map(responseStr);
				if (result.containsKey("RSPCOD") && "000000".equals(result.get("RSPCOD"))) {
					PayOrder payOrder = new PayOrder();
					/*
					 * "F", "付款失败" "S", "付款成功" "I", "待付款" "P", "付款中"
					 */
					String return_status = result.get("PAY_STATUS");
					String return_msg = result.get("PAY_DESC");
					payOrder.setErrorInfo(return_msg);
					Boolean rechangeFlag = false;
					if ("S".equals(return_status)) {
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
						result.put("return_code", "SUCCESS");
						result.put("result_code", "SUCCESS");
						rechangeFlag = true;
					} else if ("F".equals(return_status)) {
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
						log.error("支付失败！out_trade_no:" + orderNum + ", return_msg:" + return_msg);
						rechangeFlag = true;
					} else if ("I".equals(return_status) || "P".equals(return_status)) {
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
	public void dealChannelT0Tixian(Long channelId, Map<String, String> params) {
		Channel chl = channelService.get(channelId);
		String out_trade_no = commonService.getUniqueOrderByType("XKTX", Long.parseLong(params.get("operator_id")));
		String account = chl.getAccount();
		String type = "WEIXIN";
		if (UserOrder.getUserPayChannelType(chl.getType()) == 200) {
			type = "ZHIFUBAO";
		}
		JSONObject configs = JSONObject.parseObject(chl.getConfig());
		String[] keys = { "merId", "merDate", "merOrderId", "merTransId", "remark" };
		String[] tixianParams = { account + configs.getString("xinke.merchant_id"), DateUtil.convertDateStrYYYYMMDD(new Date()), out_trade_no, out_trade_no, type };
		String responseStr = XinkePayUtil.build(keys, tixianParams, "outcome");
		log.info(responseStr);

		ChannelT0Tixian channelT0Tixian = new ChannelT0Tixian(out_trade_no, null, null, null, channelId, params.get("operator_name"));
		channelT0TixianService.add(channelT0Tixian);
	}

	@Override
	public void sendT0TixianSearch(String orderNum) {
		try {
			TchannelT0Tixian t = channelT0TixianService.getTodoTchannelT0TixianByOrderNum(orderNum);
			if (t != null) {
				String[] keys = { "merId", "merDate", "merOrderId" };
				String account = t.getChannel().getAccount();
				JSONObject configs = JSONObject.parseObject(t.getChannel().getConfig());
				String[] params = { account + configs.getString("xinke.merchant_id"), DateUtil.convertDateStrYYYYMMDD(t.getCreateDate()), t.getOrderNum() };
				String responseStr = XinkePayUtil.build(keys, params, "outcomeQuery");
				log.info(responseStr);
				JSONObject result = JSONObject.parseObject(responseStr);
				if ("S".equals(result.getString("queryStatus"))) {
					String return_msg = result.getString("transDesc");
					String errCode = result.getString("transCode");
					String outcomeId = result.getString("outcomeId");
					t.setBusNum(outcomeId);
					t.setErrorDesc(return_msg);
					t.setErrorCode(errCode);
					Boolean rechangeFlag = false;
					if ("S".equals(result.get("transStatus"))) {
						t.setStatus(UserOrder.order_status.SUCCESS.getCode());
						t.setFinishDate(new Date());
						rechangeFlag = true;
					} else if ("F".equals(result.get("transStatus"))) {
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
