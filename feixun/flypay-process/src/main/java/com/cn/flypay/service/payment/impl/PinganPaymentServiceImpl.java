package com.cn.flypay.service.payment.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationAlipayTradeQueryRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationBestpayH5payRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationFinanceDownloadbillRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationJdpayH5payRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationJdpayUniorderRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantAlipayTradePayRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantAlipayTradePrecreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantBankBindRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantCreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantRateQuery;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantRateSetRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxTradePayRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxTradePrecreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxpayMppayRequest;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.TroughTrainServeice;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.pingan.PinganPaymentUtil;

/**
 * Created by sunyue on 16/8/1.
 */
@Service(value = "pinganPaymentService")
public class PinganPaymentServiceImpl extends AbstractChannelPaymentService {

	private Logger log = LoggerFactory.getLogger(getClass());
	@Value("${pingan_statement_root_path}")
	private String pingan_statement_root_path;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	@Autowired
	private TroughTrainServeice troughTrainServeice;

	/**
	 * 创建子商户
	 */
	@Override
	public UserMerchantConfig createSubMerchant(ServiceMerchant sm, Map<String, String> params) {
		int k = 0;
		FshowsLiquidationSubmerchantCreateRequest req = new FshowsLiquidationSubmerchantCreateRequest();
		req.setExternal_id("PA_" + DateUtil.convertCurrentDateTimeToString() + (k++));
		req.setName(params.get("merchantName"));
		req.setAlias_name(params.get("shortName"));
		JSONObject config = JSONObject.parseObject(sm.getConfig());
		req.setService_phone(StringUtil.isNotBlank(params.get("servicePhone")) ? params.get("servicePhone") : config.getString("servicePhone"));
		req.setCategory_id(StringUtil.isNotBlank(params.get("category")) ? params.get("category") : config.getString("category"));
		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, sm.getAppId(), FshowsLiquidationSubmerchantCreateRequest.class);
		if (result != null) {
			if (result.getBooleanValue("success")) {
				String sub_merchant_id = result.getJSONObject("return_value").getString("sub_merchant_id");
				if (StringUtil.isNotBlank(sub_merchant_id)) {
					FshowsLiquidationSubmerchantBankBindRequest bindReq = new FshowsLiquidationSubmerchantBankBindRequest();
					bindReq.setSub_merchant_id(sub_merchant_id);
					bindReq.setBank_card_no(config.getString("card_no"));
					bindReq.setCard_holder(config.getString("card_user_name"));
					JSONObject bindResult = PinganPaymentUtil.sentRequstToPingAnPayment(bindReq, sm.getAppId(), FshowsLiquidationSubmerchantBankBindRequest.class);
					if (bindResult != null && bindResult.getBooleanValue("success")) {
						UserMerchantConfig umc = new UserMerchantConfig();
						umc.setSubMerchantId(sub_merchant_id);
						umc.setServiceMerchantId(sm.getId());
						JSONObject json = new JSONObject();
						json.put("merchant_id", sub_merchant_id);
						umc.setConfig(json.toJSONString());
						umc.setType(UserMerchantConfig.merchant_config_type.COMPOSITE.getCode());
						return umc;
					}
				}
			} else {
				log.error("error_code=" + result.getString("error_code") + "   error_message=" + result.getString("error_message"));
			}
		} else {
			log.error("连接平安失败");
		}
		return null;
	}

	/**
	 * 创建收款界面二维码支付
	 */
	@Override
	public Map<String, String> createUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, Integer angentType, String desc) throws Exception {
		/* 配置的平安支付参数 */
		JSONObject channelConfig = cpr.getConfig();
		String appId = channelConfig.getString("pingan.appId");
		String merchantId = channelConfig.getString("pingan.merchant_id");
		if (cpr.getUserMerchantConfig() != null) {
			merchantId = cpr.getUserMerchantConfig().getSubMerchantId();
		}
		String notifyUrl = channelConfig.getString("pingan.notifyUrl");
		String createIp = channelConfig.getString("pingan.createIp");
		/* 根据不同的订单类型创建订单 */
		Integer orderType = cpr.getChannel().getType();
		String type = UserOrder.trans_type.WXQR.name();
		String qrurl = null;
		String out_trade_no = "";

		// 平安下单前检查平安子商户的费率--start
		BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(user.getId(), orderType, inputAccType); // 本地保存的费率

		FshowsLiquidationSubmerchantRateQuery reqFee = new FshowsLiquidationSubmerchantRateQuery();
		reqFee.setSub_merchant_id(merchantId);
		BigDecimal default_up_rate = null; // 升级订单默认支付费率

		switch (orderType) {
		case 200:
			reqFee.setType("1"); // 支付宝
			default_up_rate = BigDecimal.valueOf(0.0025);
			break;
		case 300:
			reqFee.setType("2"); // 微信
			default_up_rate = BigDecimal.valueOf(0.0025);
			break;
		case 1100:
			reqFee.setType("4"); // 翼支付
			default_up_rate = BigDecimal.valueOf(0.0017);
			break;
		case 900:
			reqFee.setType("5"); // 京东
			default_up_rate = BigDecimal.valueOf(0.0025);
			break;

		}
		JSONObject feeRes = PinganPaymentUtil.sentRequstToPingAnPayment(reqFee, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantRateQuery.class);
		if (!feeRes.containsKey("return_value")) {
			log.error("平安下单--下单前请求平安查询费率接口，连接平安失败，返回错误信息为" + feeRes.toJSONString());
			return null;
		} else {
			// 查询平安报备的费率成功
			String merchant_rate = feeRes.getJSONObject("return_value").getString("merchant_rate");
			BigDecimal merRate = new BigDecimal(merchant_rate);

			// 判断平安的费率是否与添加了2元提现费的费率是否一致
			// 添加2元提现费--start
			BigDecimal b1 = BigDecimal.valueOf(money);
			BigDecimal b2 = BigDecimal.valueOf(2);
			BigDecimal b3 = b2.divide(b1, 4, BigDecimal.ROUND_DOWN); // 提现费手续费=2/交易金额（最小保留万分数，后面有余数不做四舍五入）
			BigDecimal b4 = rate[0].add(b3);
			// 添加2元提现费--end

			BigDecimal new_pingan_rate = null;
			// 判断订单类型，升级订单不加提现手续费
			if (transPayType == 10) {
				// 普通订单
				new_pingan_rate = b4;
			} else {
				// 升级订单，走大商户，设置默认支付通道费率,不收取2元提现手续费
				new_pingan_rate = default_up_rate;
			}

			if (merRate.compareTo(new_pingan_rate) != 0) {
				// 如果用户下单的费率和平安对应的交易类型费率不一致，那么需要设置平安的费率
				FshowsLiquidationSubmerchantRateSetRequest rateSetReq = new FshowsLiquidationSubmerchantRateSetRequest();
				rateSetReq.setSub_merchant_id(merchantId);
				// rateSetReq.setMerchant_rate(rate[0].toString());
				// //默认只设置了微信的费率
				rateSetReq.setMerchant_rate(new_pingan_rate.stripTrailingZeros().toPlainString()); // 去除多余的0，并避免使用科学计数法
				rateSetReq.setType(reqFee.getType());
				JSONObject rateSetResult = PinganPaymentUtil.sentRequstToPingAnPayment(rateSetReq, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantRateSetRequest.class);
				if (!rateSetResult.containsKey("return_value")) {
					log.error("平安下单--下单前请求平安设置费率接口，连接平安失败，返回错误信息为" + feeRes.toJSONString());
					return null;
				} else {
					log.info("平安下单--下单前请求平安设置费率接口，请求成功");
				}
			}
		}

		// 平安下单前检查平安子商户的费率--end

		if (orderType - UserOrder.trans_type.ALQR.getCode() == 0) {

			// 支付宝下单，检查是否入驻支付宝---开始
			// 上面已经查出来的配置信息有缓存，这里重新查询，保证数据的及时更新
			JSONObject configJson = new JSONObject();
			Long cpr200_id = null;
			if (transPayType == 20) {
				// 如果是升级订单,则使用提前准备好的大商户通道收款
				configJson = JSONObject.parseObject(cpr.getChannel().getConfig());
				cpr200_id = cpr.getChannel().getId();
			} else {
				// 普通交易订单，统一使用直通车
				ChannelPayRef cpr200 = troughTrainServeice.getChannelPayRef(false, "200", user.getId(), "", "PINGANPAYZHITONGCHE_ZHIQING");
				cpr200_id = cpr200.getChannel().getId();
				configJson = JSONObject.parseObject(cpr200.getChannel().getConfig());
			}

			String pingan_ali = configJson.getString("pingan.add.ali");
			if (StringUtil.isBlank(pingan_ali) || !pingan_ali.equals("success")) {
				// 配置平安支付宝通道

				Map<String, String> openAli = troughTrainServeice.addPingAnZhiFuBaoMer(cpr200_id);
				if (!openAli.get("code").equals("000")) {
					Map<String, String> aliresult = new HashMap<String, String>();
					aliresult.put("return_code", "open_ali_error");
					if (openAli.containsKey("messgae") && openAli.get("messgae").equals("今日支付宝入驻已达上限")) {
						aliresult.put("return_message", "今日支付宝入驻已达上限，请联系客服");
					} else {
						aliresult.put("return_message", "调用支付宝通道失败，请联系客服");
					}
					return aliresult;
				}
			}
			// 支付宝下单，检查是否入驻支付宝---结束

			type = UserOrder.trans_type.ALQR.name();
			out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
			FshowsLiquidationSubmerchantAlipayTradePrecreateRequest req = new FshowsLiquidationSubmerchantAlipayTradePrecreateRequest();
			req.setOut_trade_no(out_trade_no);
			req.setNotify_url(notifyUrl);
			req.setTotal_amount(money.toString());
			req.setSubject(desc);
			req.setSub_merchant(JSONObject.parseObject("{\"merchant_id\":\"" + merchantId + "\"}"));

			JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantAlipayTradePrecreateRequest.class);
			if (result != null) {
				if (result.getBooleanValue("success")) {
					qrurl = result.getJSONObject("return_value").getString("qrCode");
				} else {
					log.error("error_code=" + result.getString("error_code") + "   error_message=" + result.getString("error_message"));
				}
			} else {
				log.error("连接平安失败");
			}
		} else if (orderType - UserOrder.trans_type.JDQR.getCode() == 0) {
			type = UserOrder.trans_type.JDQR.name();
			out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
			FshowsLiquidationJdpayUniorderRequest req = new FshowsLiquidationJdpayUniorderRequest();
			req.setOut_trade_no(out_trade_no);
			req.setNotify_url(notifyUrl);
			req.setSub_merchant_id(merchantId);
			req.setBody(desc);
			req.setTotal_fee(money.toString());
			req.setOrder_type("1");
			JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationJdpayUniorderRequest.class);
			if (result != null) {
				JSONObject return_value = result.getJSONObject("return_value");
				if (return_value != null) {
					qrurl = return_value.getString("qr_code");
				}
				log.info(qrurl);
			} else {
				log.error("连接平安失败");
			}
		} else if (orderType - UserOrder.trans_type.YZFQR.getCode() == 0) {
			type = UserOrder.trans_type.YZFQR.name();
			out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
			FshowsLiquidationBestpayH5payRequest req = new FshowsLiquidationBestpayH5payRequest();
			req.setOut_trade_no(out_trade_no);
			req.setNotify_url(notifyUrl);
			req.setSub_merchant_id(merchantId);
			req.setBody(desc);
			req.setTotal_fee(money.toString());
			JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationBestpayH5payRequest.class);
			if (result != null) {
				if (result.getBooleanValue("success")) {
					String trade_no = result.getJSONObject("return_value").getString("trade_no");
					String callback_url = String.format(ApplicatonStaticUtil.success_pay_url, out_trade_no);
					qrurl = String.format(PinganPaymentUtil.best_pay_product_url, trade_no, URLEncoder.encode(desc, "UTF-8"), callback_url);
					log.info(qrurl);
				} else {
					log.error("error_code=" + result.getString("error_code") + "   error_message=" + result.getString("error_message"));
				}
			} else {
				log.error("连接平安失败");
			}
		} else {

			// 微信类型检查授权目录是否配置正确
			JSONObject configJson = new JSONObject();
			Long cpr300_id = null;
			if (transPayType == 20) {
				// 如果是升级订单,则使用提前准备好的大商户通道收款
				configJson = JSONObject.parseObject(cpr.getChannel().getConfig());
				cpr300_id = cpr.getChannel().getId();
			} else {
				// 普通交易订单，统一使用直通车
				ChannelPayRef cprWx300 = troughTrainServeice.getChannelPayRef(false, "300", user.getId(), "", "PINGANPAYZHITONGCHE_ZHIQING");
				cpr300_id = cprWx300.getChannel().getId();
				configJson = JSONObject.parseObject(cprWx300.getChannel().getConfig());
			}

			String sub_wx_supplement_jsapi_path = configJson.getString("sub_wx_supplement_jsapi_path");
			if (StringUtil.isBlank(sub_wx_supplement_jsapi_path) || !sub_wx_supplement_jsapi_path.equals(PinganPaymentUtil.jsapi_path)) {
				// 重新配置
				Map<String, String> openWxSup1 = troughTrainServeice.addPingAnWeiXinSupplement(cpr300_id, "jsapi_path", PinganPaymentUtil.jsapi_path);
				if (!openWxSup1.get("code").equals("000")) {
					Map<String, String> wxresult = new HashMap<String, String>();
					wxresult.put("return_code", "make_wx_jsapi_path_error");
					wxresult.put("return_message", "小主，微信通道现在使用人数过多，路上有点堵，请重新选择通道使用。");
					return wxresult;
				}
			}

			FshowsLiquidationWxTradePrecreateRequest req = new FshowsLiquidationWxTradePrecreateRequest();
			req.setBody(desc);
			out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
			req.setOut_trade_no(out_trade_no);
			req.setTotal_fee(money.toString());
			req.setSpbill_create_ip(createIp);
			req.setNotify_url(notifyUrl);
			req.setStore_id(merchantId);
			// String sub_mch_id = channelConfig.getString("sub_wx_id");
			// if (StringUtil.isNotEmpty(sub_mch_id)) {
			// req.setSub_appid(sub_mch_id); //更多微信公众号
			// req.setSub_mch_id(sub_mch_id); //微信子商户号
			// }
			JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationWxTradePrecreateRequest.class);
			if (result != null) {
				if (result.getBooleanValue("success")) {
					qrurl = result.getJSONObject("return_value").getString("qr_code");
				} else {
					log.error("error_code=" + result.getString("error_code") + "   error_message=" + result.getString("error_message"));
				}
			} else {
				log.error("连接平安失败");
			}
		}
		if (StringUtil.isNotBlank(qrurl)) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("orderNum", out_trade_no);
			try {
				userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, orderType, money, UserOrder.cd_type.D.name(), null, null, desc, transPayType, cpr.getChannel(), inputAccType, angentType);
				result.put("return_code", "SUCCESS");
				result.put("result_code", "SUCCESS");
				result.put("code_url", qrurl);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw e;
			}
			return result;
		}
		return null;
	}

	/**
	 * 该扫码接口项目中未被使用过，所以没有同步至直清模式
	 */
	@Override
	public Map<String, String> createSmUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, String authCode, Double money, Integer transPayType, Integer agentType, String desc) throws Exception {

		/* 配置的平安支付参数 */
		JSONObject channelConfig = cpr.getConfig();
		String appId = channelConfig.getString("pingan.appId");
		String merchantId = channelConfig.getString("pingan.merchant_id");
		if (cpr.getUserMerchantConfig() != null) {
			merchantId = cpr.getUserMerchantConfig().getSubMerchantId();
		}
		String createIp = channelConfig.getString("pingan.createIp");
		/* 根据不同的订单类型创建订单 */
		Integer orderType = cpr.getChannel().getType();
		String type = UserOrder.trans_type.WXSM.name();
		String out_trade_no = "";
		JSONObject result = null;
		if (orderType - UserOrder.trans_type.ALSM.getCode() == 0) {
			type = UserOrder.trans_type.ALSM.name();
			out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
			userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, orderType, money, UserOrder.cd_type.D.name(), null, null, desc, transPayType, cpr.getChannel(), inputAccType, agentType);
			FshowsLiquidationSubmerchantAlipayTradePayRequest req = new FshowsLiquidationSubmerchantAlipayTradePayRequest();
			req.setOut_trade_no(out_trade_no);
			req.setNotify_url("https://bbpurse.com/flypayfx/payment/pinganFeedback");
			req.setScene("bar_code");
			req.setAuth_code(authCode);
			req.setTotal_amount(money.toString());
			req.setSubject(desc);
			req.setSub_merchant(JSONObject.parseObject("{\"merchant_id\":\"" + merchantId + "\"}"));
			result = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantAlipayTradePayRequest.class);
		} else {
			FshowsLiquidationWxTradePayRequest req = new FshowsLiquidationWxTradePayRequest();
			out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
			userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, orderType, money, UserOrder.cd_type.D.name(), null, null, desc, transPayType, cpr.getChannel(), inputAccType, agentType);
			req.setBody(desc);
			req.setOut_trade_no(out_trade_no);
			req.setTotal_fee(money.toString());
			req.setSpbill_create_ip(createIp);
			req.setAuth_code(authCode);
			req.setStore_id(merchantId);
			result = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationWxTradePayRequest.class);
		}
		try {
			if (result != null) {
				UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(out_trade_no);
				if (userOrder != null) {
					Boolean changeFlag = false;
					PayOrder payOrder = new PayOrder();
					Map<String, String> resultMap = new HashMap<String, String>();
					if (result != null && result.containsKey("success") && result.getBooleanValue("success")) {
						JSONObject resultJson = result.getJSONObject("return_value");
						/* 完成订单后，更新原有订单 */
						if (orderType - UserOrder.trans_type.ALSM.getCode() == 0) {
							if (resultJson.containsKey("totalAmount") && StringUtil.isNotBlank(resultJson.getString("totalAmount"))) {
								payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(resultJson.getString("totalAmount"))));
							}

							if (resultJson.containsKey("receiptAmount") && StringUtil.isNotBlank(resultJson.getString("receiptAmount"))) {
								payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(resultJson.getString("receiptAmount"))));
							}

							if (resultJson.containsKey("gmtPayment") && StringUtil.isNotBlank(resultJson.getString("gmtPayment"))) {
								Date finishDate = new Date(Long.parseLong(resultJson.getString("gmtPayment")));
								payOrder.setPayFinishDate(DateUtil.getyyyyMMddHHmmssStringFromDate(finishDate));
								payOrder.setFinishDate(finishDate);
							}

							if (resultJson.containsKey("tradeNo") && StringUtil.isNotBlank(resultJson.getString("tradeNo"))) {
								payOrder.setPayNo(resultJson.getString("tradeNo"));
							}

						} else {

							if (resultJson.containsKey("total_fee") && StringUtil.isNotBlank(resultJson.getString("total_fee"))) {
								payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(resultJson.getString("total_fee"))));
							}

							if (resultJson.containsKey("net_money") && StringUtil.isNotBlank(resultJson.getString("net_money"))) {
								payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(resultJson.getString("net_money"))));
							}

							if (resultJson.containsKey("time_end") && StringUtil.isNotBlank(resultJson.getString("time_end"))) {
								payOrder.setPayFinishDate(resultJson.getString("time_end"));
								payOrder.setFinishDate(DateUtil.convertStringToDate(DateUtil.FORMAT_YYYYMMDDHHmmss, resultJson.getString("time_end")));
							}

							if (resultJson.containsKey("transaction_id") && StringUtil.isNotBlank(resultJson.getString("transaction_id"))) {
								payOrder.setPayNo(resultJson.getString("transaction_id"));
							}
						}
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
						changeFlag = true;
						resultMap.put("return_code", GlobalConstant.SUCCESS);
						resultMap.put("result_code", GlobalConstant.SUCCESS);
					} else if (result.containsKey("error_code") && result.get("error_code").equals("100")) {
						resultMap.put("return_code", GlobalConstant.SUCCESS);
						resultMap.put("result_code", GlobalConstant.WAITING);
					} else {
						resultMap.put("return_code", GlobalConstant.FAILUER);
						String return_msg = result.getString("error_message");
						String errCode = result.getString("error_code");
						payOrder.setErrorInfo(return_msg);
						payOrder.setErrorCode(errCode);
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
						changeFlag = true;
					}
					if (changeFlag) {
						try {
							userOrderService.finishInputOrderStatus(out_trade_no, payOrder);
							return resultMap;
						} catch (Exception e) {
							log.error("----订单回调异常", e);
						}
					}
				} else {
					log.info("订单：" + out_trade_no + " 不存在或已经被成功处理了");
				}
			} else {
				log.info("平安支付通道");
			}
		} catch (Exception e) {
			log.error("pingan error ", e);
			throw e;
		}
		log.info("创建平安扫码订单 end");
		return null;
	}

	/**
	 * 创建聚合码在线支付订单
	 */
	@Override
	public Map<String, String> createOnLineOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, String desc, Map<String, String> params) throws Exception {

		Integer orderType = cpr.getChannel().getType();
		String type = UserOrder.trans_type.WXOL.name();
		if (orderType - UserOrder.trans_type.ALOL.getCode() == 0) {
			type = UserOrder.trans_type.ALOL.name();
		} else if (orderType - UserOrder.trans_type.JDOL.getCode() == 0) {
			type = UserOrder.trans_type.JDOL.name();
		} else if (orderType - UserOrder.trans_type.YIOL.getCode() == 0) {
			type = UserOrder.trans_type.YIOL.name();
		}
		/* 用户订单号= 用户ID + 14位时间戳+ userType（2位） */
		String out_trade_no = commonService.getUniqueOrderByType(type, user.getId());

		JSONObject channelConfig = cpr.getConfig();

		String appId = channelConfig.getString("pingan.appId");
		String merchantId = channelConfig.getString("pingan.merchant_id");
		if (cpr.getUserMerchantConfig() != null) {
			merchantId = cpr.getUserMerchantConfig().getSubMerchantId();
		}
		String notifyUrl = channelConfig.getString("pingan.notifyUrl");
		String createIp = channelConfig.getString("pingan.createIp");

		JSONObject result = null;
		String url = null;

		// 聚合码下单前，与平安同步费率--开始
		FshowsLiquidationSubmerchantRateQuery reqFee = new FshowsLiquidationSubmerchantRateQuery();
		reqFee.setSub_merchant_id(merchantId);
		Integer rateOrderType = null;
		switch (orderType) {
		case 220:
			reqFee.setType("1"); // 支付宝
			rateOrderType = 200;
			break;
		case 320:
			reqFee.setType("2"); // 微信
			rateOrderType = 300;
			break;
		case 1120:
			reqFee.setType("4"); // 翼支付
			rateOrderType = 1100;
			break;
		case 920:
			reqFee.setType("5"); // 京东
			rateOrderType = 900;
			break;

		}
		BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(user.getId(), rateOrderType, inputAccType);
		JSONObject feeRes = PinganPaymentUtil.sentRequstToPingAnPayment(reqFee, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantRateQuery.class);
		if (!feeRes.containsKey("return_value")) {
			log.error("平安下单--下单前请求平安查询费率接口，连接平安失败，返回错误信息为" + feeRes.toJSONString());
			return null;
		} else {
			String merchant_rate = feeRes.getJSONObject("return_value").getString("merchant_rate");
			BigDecimal merRate = new BigDecimal(merchant_rate);

			// 添加2元提现费--start
			BigDecimal b1 = BigDecimal.valueOf(money);
			BigDecimal b2 = BigDecimal.valueOf(2);
			BigDecimal b3 = b2.divide(b1, 4, BigDecimal.ROUND_DOWN); // 提现费手续费=2/交易金额（最小保留万分数，后面有余数不做四舍五入）
			BigDecimal b4 = rate[0].add(b3);
			// 添加2元提现费--end

			// BigDecimal new_pingan_rate = null;
			// //判断订单类型，升级订单不加提现手续费
			// if(transPayType == 10){
			// //普通订单
			//// new_pingan_rate = b4.stripTrailingZeros().toPlainString();
			// //去除多余的0，并避免使用科学计数法
			// new_pingan_rate = b4;
			// }else{
			// //升级订单
			// new_pingan_rate = rate[0];
			// }

			// 由于聚合码下单类型只有普通订单，所以不用对订单是否是升级类型做出判断
			if (merRate.compareTo(b4) != 0) {
				// 如果用户下单的费率和平安对应的交易类型费率不一致，那么需要设置平安的费率
				FshowsLiquidationSubmerchantRateSetRequest rateSetReq = new FshowsLiquidationSubmerchantRateSetRequest();
				rateSetReq.setSub_merchant_id(merchantId);
				// rateSetReq.setMerchant_rate(rate[0].toString());
				// //默认只设置了微信的费率
				rateSetReq.setMerchant_rate(b4.stripTrailingZeros().toPlainString()); // 去除多余的0，并避免使用科学计数法
				rateSetReq.setType(reqFee.getType());
				JSONObject rateSetResult = PinganPaymentUtil.sentRequstToPingAnPayment(rateSetReq, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantRateSetRequest.class);
				if (!rateSetResult.containsKey("return_value")) {
					log.error("平安下单--下单前请求平安设置费率接口，连接平安失败，返回错误信息为" + feeRes.toJSONString());
					return null;
				} else {
					log.info("平安下单--下单前请求平安设置费率接口，请求成功");
				}
			}
		}
		// 聚合码下单前，与平安同步费率--结束

		if (orderType - UserOrder.trans_type.ALOL.getCode() == 0) {
			// 支付宝下单，检查是否入驻支付宝---开始
			ChannelPayRef cpr200 = troughTrainServeice.getChannelPayRef(false, "220", user.getId(), "", "PINGANPAYZHITONGCHE_ZHIQING");
			JSONObject configJson = JSONObject.parseObject(cpr200.getChannel().getConfig());
			String pingan_ali = configJson.getString("pingan.add.ali");
			if (StringUtil.isBlank(pingan_ali) || !pingan_ali.equals("success")) {
				// 配置平安支付宝通道

				Map<String, String> openAli = troughTrainServeice.addPingAnZhiFuBaoMer(cpr200.getChannel().getId());
				if (!openAli.get("code").equals("000")) {
					Map<String, String> aliresult = new HashMap<String, String>();
					aliresult.put("return_code", "open_ali_error");
					if (openAli.containsKey("messgae") && openAli.get("messgae").equals("今日支付宝入驻已达上限")) {
						aliresult.put("return_message", "今日支付宝入驻已达上限，请联系客服");
					} else {
						aliresult.put("return_message", "调用支付宝通道出了点小问题，请联系客服");
					}
					return aliresult;
				}
			}
			// 支付宝下单，检查是否入驻支付宝---结束

			FshowsLiquidationSubmerchantAlipayTradePrecreateRequest req = new FshowsLiquidationSubmerchantAlipayTradePrecreateRequest();
			req.setBody(desc);
			out_trade_no = commonService.getUniqueOrderByType(type, user.getId());

			req.setOut_trade_no(out_trade_no);
			req.setNotify_url(notifyUrl);
			req.setTotal_amount(money.toString());
			req.setSubject(desc);

			req.setSub_merchant(JSONObject.parseObject("{\"merchant_id\":\"" + merchantId + "\"}"));

			result = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantAlipayTradePrecreateRequest.class);
			log.info(result.toJSONString());
			if (result != null && result.containsKey("success") && result.getBooleanValue("success") && StringUtil.isNotBlank(result.getJSONObject("return_value").getString("qrCode"))) {
				url = result.getJSONObject("return_value").getString("qrCode");
			}
		} else if (orderType - UserOrder.trans_type.JDOL.getCode() == 0) {

			out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
			FshowsLiquidationJdpayH5payRequest req = new FshowsLiquidationJdpayH5payRequest();
			req.setOut_trade_no(out_trade_no);
			req.setNotify_url(notifyUrl);
			req.setSub_merchant_id(merchantId);
			req.setBody(desc);
			req.setTotal_fee(money.toString());
			result = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationJdpayH5payRequest.class);

			log.info(result.toJSONString());
			if (result != null && result.containsKey("success") && result.getBooleanValue("success") && StringUtil.isNotBlank(result.getJSONObject("return_value").getString("prepay_id"))) {
				url = result.getJSONObject("return_value").getString("qrCode");
				String prepay_id = result.getJSONObject("return_value").getString("prepay_id");
				String trade_no = result.getJSONObject("return_value").getString("trade_no");
				url = String.format(PinganPaymentUtil.jd_pay_product_url, prepay_id, trade_no, URLEncoder.encode(desc, "UTF-8"), String.format(ApplicatonStaticUtil.success_pay_url, out_trade_no));

			}
		} else if (orderType - UserOrder.trans_type.WXOL.getCode() == 0) {

			// 微信类型检查授权目录是否配置正确
			ChannelPayRef cprWx300 = troughTrainServeice.getChannelPayRef(false, "320", user.getId(), "", "PINGANPAYZHITONGCHE_ZHIQING");
			JSONObject configJson = JSONObject.parseObject(cprWx300.getChannel().getConfig());
			String sub_wx_supplement_jsapi_path = configJson.getString("sub_wx_supplement_jsapi_path");
			if (StringUtil.isBlank(sub_wx_supplement_jsapi_path) || !sub_wx_supplement_jsapi_path.equals(PinganPaymentUtil.jsapi_path)) {
				// 重新配置
				Map<String, String> openWxSup1 = troughTrainServeice.addPingAnWeiXinSupplement(cprWx300.getChannel().getId(), "jsapi_path", PinganPaymentUtil.jsapi_path);
				if (!openWxSup1.get("code").equals("000")) {
					Map<String, String> wxresult = new HashMap<String, String>();
					wxresult.put("return_code", "make_wx_jsapi_path_error");
					wxresult.put("return_message", "小主，微信通道现在使用人数过多，路上有点堵，请重新选择通道使用。");
					return wxresult;
				}
			}

			FshowsLiquidationWxpayMppayRequest req = new FshowsLiquidationWxpayMppayRequest();
			req.setSub_merchant_id(merchantId);
			req.setBody(user.getOrganizationAppName());
			req.setOut_trade_no(out_trade_no);
			req.setTotal_fee(money.toString());
			req.setSpbill_create_ip(createIp);
			req.setSub_openid(params.get("userId")); // 其实是扫描了该聚合收款码的微信用户，在平台配置的微信公众号所搭建的环境上，用户唯一标识自己身份的openId
			req.setNotify_url(notifyUrl);
			result = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationWxpayMppayRequest.class);
			log.info(result.toJSONString());
			if (result != null && result.containsKey("success") && result.getBooleanValue("success") && StringUtil.isNotBlank(result.getJSONObject("return_value").getString("prepay_id"))) {
				// 跳转至微信公众号支付页面
				// url =
				// String.format(PinganPaymentUtil.online_wx_pay_product_url,
				// result.getJSONObject("return_value").getString("prepay_id"),ApplicatonStaticUtil.success_wxpay_url);
				url = String.format(PinganPaymentUtil.online_wx_pay_product_url, result.getJSONObject("return_value").getString("prepay_id"), String.format(ApplicatonStaticUtil.success_pay_url, out_trade_no));
			}
		} else if (orderType - UserOrder.trans_type.YIOL.getCode() == 0) {
			out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
			FshowsLiquidationBestpayH5payRequest req = new FshowsLiquidationBestpayH5payRequest();
			req.setOut_trade_no(out_trade_no);
			req.setNotify_url(notifyUrl);
			req.setSub_merchant_id(merchantId);
			req.setBody(desc);
			req.setTotal_fee(money.toString());
			result = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationBestpayH5payRequest.class);

			log.info(result.toJSONString());
			if (result != null && result.containsKey("success") && result.getBooleanValue("success") && StringUtil.isNotBlank(result.getJSONObject("return_value").getString("trade_no"))) {
				url = result.getJSONObject("return_value").getString("qrCode");
				String trade_no = result.getJSONObject("return_value").getString("trade_no");
				url = String.format(PinganPaymentUtil.best_pay_product_url, trade_no, URLEncoder.encode(desc, "UTF-8"), String.format(ApplicatonStaticUtil.success_pay_url, out_trade_no));

			}
		}
		if (url != null) {
			Map<String, String> preMap = new HashMap<String, String>();
			preMap.put("PINGANPAY.url", url);
			userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, orderType, money, UserOrder.cd_type.D.name(), null, null, desc, transPayType, cpr.getChannel(), inputAccType, 0);
			return preMap;
		}
		return null;
	}

	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {

		UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNum);
		if (userOrder != null) {
			FshowsLiquidationAlipayTradeQueryRequest req = new FshowsLiquidationAlipayTradeQueryRequest();
			req.setOut_trade_no(orderNum);
			try {
				JSONObject channelConfig = channelService.getChannelConfig(userOrder.getChannelId());
				JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, channelConfig.getString("pingan.appId"), FshowsLiquidationAlipayTradeQueryRequest.class);
				if (result != null && result.containsKey("success") && result.getBooleanValue("success")) {
					JSONObject resultJson = result.getJSONObject("return_value");
					PayOrder payOrder = new PayOrder();
					Boolean successFlag = false;
					if (orderNum.startsWith("JD") && (resultJson.containsKey("status") && StringUtil.isNotBlank(resultJson.getString("status")) && resultJson.getInteger("status") == 2)) {
						if (resultJson.containsKey("total_fee") && StringUtil.isNotBlank(resultJson.getString("total_fee"))) {
							payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(resultJson.getString("total_fee"))));
						}
						if (resultJson.containsKey("net_receipt_amount") && StringUtil.isNotBlank(resultJson.getString("net_receipt_amount"))) {
							payOrder.setRealAmt(BigDecimal.valueOf(resultJson.getDouble("net_receipt_amount")));
						}
						if (resultJson.containsKey("pay_time") && StringUtil.isNotBlank(resultJson.getString("pay_time"))) {
							payOrder.setPayFinishDate(resultJson.getString("pay_time"));
							payOrder.setFinishDate(resultJson.getDate("pay_time"));
						}
						successFlag = true;
					} else if (orderNum.startsWith("WX") && (resultJson.containsKey("trade_state") && StringUtil.isNotBlank(resultJson.getString("trade_state")) && GlobalConstant.SUCCESS.equals(resultJson.getString("trade_state")))) {
						if (resultJson.containsKey("total_fee") && StringUtil.isNotBlank(resultJson.getString("total_fee"))) {
							payOrder.setPayAmt(BigDecimal.valueOf(resultJson.getDouble("total_fee")));
						}
						if (resultJson.containsKey("net_receipt_amount") && StringUtil.isNotBlank(resultJson.getString("net_receipt_amount"))) {
							payOrder.setRealAmt(BigDecimal.valueOf(resultJson.getDouble("net_receipt_amount")));
						}
						if (resultJson.containsKey("time_end") && StringUtil.isNotBlank(resultJson.getString("time_end"))) {
							payOrder.setPayFinishDate(resultJson.getString("time_end"));
							payOrder.setFinishDate(resultJson.getDate("time_end"));
						}
						successFlag = true;
					} else if (orderNum.startsWith("AL") && (resultJson.containsKey("trade_status") && StringUtil.isNotBlank(resultJson.getString("trade_status")) && "TRADE_SUCCESS".equals(resultJson.getString("trade_status")))) {
						if (resultJson.containsKey("total_amount") && StringUtil.isNotBlank(resultJson.getString("total_amount"))) {
							payOrder.setPayAmt(BigDecimal.valueOf(resultJson.getDouble("total_amount")));
						}
						if (resultJson.containsKey("receipt_amount") && StringUtil.isNotBlank(resultJson.getString("receipt_amount"))) {
							payOrder.setRealAmt(BigDecimal.valueOf(resultJson.getDouble("receipt_amount")));
						}
						if (resultJson.containsKey("send_pay_date") && StringUtil.isNotBlank(resultJson.getString("send_pay_date"))) {
							payOrder.setPayFinishDate(resultJson.getString("send_pay_date"));
							payOrder.setFinishDate(resultJson.getDate("send_pay_date"));
						}
						successFlag = true;
					} else if (orderNum.startsWith("YZF") && (resultJson.containsKey("trans_status") && StringUtil.isNotBlank(resultJson.getString("trans_status")) && "B".equals(resultJson.getString("trade_status")) // B为支付成功
					)) {
						if (resultJson.containsKey("total_fee") && StringUtil.isNotBlank(resultJson.getString("total_fee"))) {
							payOrder.setPayAmt(BigDecimal.valueOf(resultJson.getDouble("total_fee")));
						}
						if (resultJson.containsKey("net_receipt_amount") && StringUtil.isNotBlank(resultJson.getString("net_receipt_amount"))) {
							payOrder.setRealAmt(BigDecimal.valueOf(resultJson.getDouble("net_receipt_amount")));
						}
						if (resultJson.containsKey("order_date") && StringUtil.isNotBlank(resultJson.getString("order_date"))) {
							payOrder.setPayFinishDate(resultJson.getString("order_date"));
							payOrder.setFinishDate(resultJson.getDate("order_date"));
						}
						successFlag = true;
					}

					if (resultJson.containsKey("trade_no") && StringUtil.isNotBlank(resultJson.getString("trade_no"))) {
						payOrder.setPayNo(resultJson.getString("trade_no"));
					}
					if (successFlag) {
						try {
							payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
							userOrderService.finishInputOrderStatus(orderNum, payOrder);
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
	public Boolean dealDownloadStatement(String dateStr) {

		List<JSONObject> channelCfgs = channelService.getAvailableChannelConfigByChannelName("PINGANPAY");
		Boolean flag = false;
		for (JSONObject channelConfig : channelCfgs) {
			try {
				String appId = channelConfig.getString("pingan.appId");
				FshowsLiquidationFinanceDownloadbillRequest request = new FshowsLiquidationFinanceDownloadbillRequest();
				request.setBill_date(dateStr);
				request.setPay_platform(String.valueOf(FshowsLiquidationFinanceDownloadbillRequest.PAY_PLATFORM.ALIPAY.getCode()));
				// 1、支付宝
				JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(request, appId, request.getClass());
				log.info("aliResult is {}", result);
				// 指定希望保存的文件路径
				File file = new File(pingan_statement_root_path);
				if (!file.exists()) {
					file.mkdirs();
				}
				if (result.getBooleanValue("success")) {
					String downloadUrl = result.getJSONObject("return_value").getString("download_url");
					if (StringUtil.isNotBlank(downloadUrl)) {
						String filePath = pingan_statement_root_path + File.separator + dateStr + "_" + FshowsLiquidationFinanceDownloadbillRequest.PAY_PLATFORM.ALIPAY.getCode() + appId + ".csv";
						try {
							downloadStatementFile(downloadUrl, filePath);
							tradeStatementService.dealPinganStatement(filePath, dateStr);
						} catch (Exception e) {
							log.error(e.getMessage());
						}
					} else {
						log.info(dateStr + "_" + appId + "无对账单");
					}
				} else {
					log.info(dateStr + "_" + appId + "无对账单");
				}

				// 2、微信
				request.setPay_platform(String.valueOf(FshowsLiquidationFinanceDownloadbillRequest.PAY_PLATFORM.WEIXIN.getCode()));
				JSONObject wxResult = PinganPaymentUtil.sentRequstToPingAnPayment(request, appId, request.getClass());
				log.info("wxResult is {}", wxResult);
				if (wxResult.getBooleanValue("success")) {
					String downloadUrl = wxResult.getJSONObject("return_value").getString("download_url");
					if (StringUtil.isNotBlank(downloadUrl)) {
						String filePath = pingan_statement_root_path + File.separator + dateStr + "_" + FshowsLiquidationFinanceDownloadbillRequest.PAY_PLATFORM.WEIXIN.getCode() + appId + ".csv";
						try {
							downloadStatementFile(downloadUrl, filePath);
							tradeStatementService.dealPinganStatement(filePath, dateStr);
						} catch (Exception e) {
							log.error(e.getMessage());
						}
					} else {
						log.info(dateStr + "_" + appId + "无对账单");
					}
				} else {
					log.info(dateStr + "_" + appId + "无对账单");
				}
				// 京东
				request.setPay_platform(String.valueOf(FshowsLiquidationFinanceDownloadbillRequest.PAY_PLATFORM.JINGDONG.getCode()));
				JSONObject jdResult = PinganPaymentUtil.sentRequstToPingAnPayment(request, appId, request.getClass());
				log.info("jdResult is {}", jdResult);
				if (jdResult.getBooleanValue("success")) {
					String downloadUrl = jdResult.getJSONObject("return_value").getString("download_url");
					if (StringUtil.isNotBlank(downloadUrl)) {
						String filePath = pingan_statement_root_path + File.separator + dateStr + "_" + FshowsLiquidationFinanceDownloadbillRequest.PAY_PLATFORM.JINGDONG.getCode() + appId + ".csv";
						try {
							downloadStatementFile(downloadUrl, filePath);
							tradeStatementService.dealPinganStatement(filePath, dateStr);
						} catch (Exception e) {
							log.error(e.getMessage());
						}
					} else {
						log.info(dateStr + "_" + appId + "无对账单");
					}
				} else {
					log.info(dateStr + "_" + appId + "无对账单");
				}
				// 翼支付
				request.setPay_platform(String.valueOf(FshowsLiquidationFinanceDownloadbillRequest.PAY_PLATFORM.YIZHIFU.getCode()));
				JSONObject yzfResult = PinganPaymentUtil.sentRequstToPingAnPayment(request, appId, request.getClass());
				log.info("yzfResult is {}", yzfResult);
				if (yzfResult.getBooleanValue("success")) {
					String downloadUrl = yzfResult.getJSONObject("return_value").getString("download_url");
					if (StringUtil.isNotBlank(downloadUrl)) {
						String filePath = pingan_statement_root_path + File.separator + dateStr + "_" + FshowsLiquidationFinanceDownloadbillRequest.PAY_PLATFORM.YIZHIFU.getCode() + appId + ".csv";
						try {
							downloadStatementFile(downloadUrl, filePath);
							tradeStatementService.dealPinganStatement(filePath, dateStr);
						} catch (Exception e) {
							log.error(e.getMessage());
						}
					} else {
						log.info(dateStr + "_" + appId + "无对账单");
					}
				} else {
					log.info(dateStr + "_" + appId + "无对账单");
				}
				flag = true;
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return flag;
	}

	private void downloadStatementFile(String downloadUrl, String fileName) throws Exception {
		String[] sps = downloadUrl.split("\\?");
		String URL = downloadUrl.substring(0, downloadUrl.indexOf("finance/") + 8);
		String main = sps[0].substring(URL.length());
		String other = sps[1];
		downloadUrl = URL + URLEncoder.encode(main, "utf-8") + "?" + other;
		log.info(downloadUrl);
		HttpURLConnection httpUrlConnection = null;
		InputStream fis = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(downloadUrl);
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setConnectTimeout(5 * 10000);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestMethod("GET");
			httpUrlConnection.setRequestProperty("Charsert", "GBK");
			httpUrlConnection.connect();
			fis = httpUrlConnection.getInputStream();
			byte[] temp = new byte[1024];
			int b;
			fos = new FileOutputStream(new File(fileName));
			while ((b = fis.read(temp)) != -1) {
				fos.write(temp, 0, b);
				fos.flush();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
				if (httpUrlConnection != null)
					httpUrlConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
