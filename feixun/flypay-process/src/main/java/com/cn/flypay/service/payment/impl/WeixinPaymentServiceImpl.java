package com.cn.flypay.service.payment.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.payment.OrderQueryRequest;
import com.cn.flypay.pageModel.payment.UnifiedOrder;
import com.cn.flypay.pageModel.payment.WxDownloadBillRequest;
import com.cn.flypay.pageModel.payment.WxsmUnifiedOrder;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.BeanUtils;
import com.cn.flypay.utils.CommonX509TrustManager;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.SignUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.channel.WeixinUtil;

@Service(value = "weixinPaymentService")
public class WeixinPaymentServiceImpl extends AbstractChannelPaymentService {
	private Log log = LogFactory.getLog(getClass());
	@Value("${wx_statement_root_path}")
	private String wx_statement_root_path;

	@Override
	public Map<String, String> createUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, Integer angentType, String desc) throws Exception {
		/* 用户订单号= 用户ID + 14位时间戳+ userType（2位） */
		String out_trade_no = commonService.getUniqueOrderByType(UserOrder.trans_type.WXQR.name(), user.getId());
		JSONObject channelConfig = cpr.getConfig();
		UnifiedOrder unifiedOrder = new UnifiedOrder(channelConfig.getString("wxaccount.appid"), channelConfig.getString("wxaccount.mchId"), channelConfig.getString("wxaccount.subMchId"),
				channelConfig.getString("wxaccount.ip"), channelConfig.getString("wxaccount.notifyUrl"));

		unifiedOrder.setTrade_type(UnifiedOrder.PAY_TRADE_TYPE.NATIVE.name());

		unifiedOrder.setOut_trade_no(out_trade_no);
		Double totalMoney = money * 100;
		unifiedOrder.setTotal_fee(totalMoney.intValue());// 按照 分 计算 *100
		String order_desc = String.format(desc, money);
		unifiedOrder.setBody(order_desc);
		unifiedOrder.setTime_start(DateUtil.convertCurrentDateTimeToString());
		unifiedOrder.setTime_expire(DateUtil.getyyyyMMddHHmmssStringFromDate(DateUtil.getHoursbyInterval(new Date(), 4)));
		Map<String, String> requestParam = BeanUtils.bean2Map(unifiedOrder);
		Map<String, String> result = WeixinUtil.doRequest(WeixinUtil.UNIFIEDORDER_URL, requestParam, null);
		if (result != null) {
			if (result.get("return_code").equals("SUCCESS")) {
				if (result.get("result_code").equals("SUCCESS")) {
					log.info("Wechatpay request success");
					result.put("orderNum", out_trade_no);
					try {
						userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, UserOrder.trans_type.WXQR.getCode(), money, UserOrder.cd_type.D.name(), null, null, order_desc,
								transPayType, cpr.getChannel(), inputAccType, angentType);
					} catch (Exception e) {
						log.error(e);
						throw e;
					}
					return result;
				} else {
					log.info("Wechatpay business error : err_code " + result.get("err_code") + ", err_code_des " + result.get("err_code_des"));
				}
			} else {
				log.info("Wechatpay communication error : " + result.get("return_msg"));
			}
		} else {
			log.info("Wechatpay error : can not request api");
		}
		return null;
	}

	@Override
	public Map<String, String> createSmUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, String authCode, Double money, Integer transPayType, Integer angentType, String desc)
			throws Exception {
		/* 用户订单号= 用户ID + 14位时间戳+ userType（2位） */
		String out_trade_no = commonService.getUniqueOrderByType(UserOrder.trans_type.WXSM.name(), user.getId());

		JSONObject channelConfig = cpr.getConfig();

		WxsmUnifiedOrder smOrder = new WxsmUnifiedOrder(channelConfig.getString("wxaccount.appid"), channelConfig.getString("wxaccount.mchId"), channelConfig.getString("wxaccount.subMchId"),
				channelConfig.getString("wxaccount.ip"));

		smOrder.setOut_trade_no(out_trade_no);
		smOrder.setBody(desc);
		smOrder.setAuth_code(authCode);
		Double totalMoney = money * 100;
		smOrder.setTotal_fee(totalMoney.intValue());// 按照 分 计算 *100
		Map<String, String> requestParam = BeanUtils.bean2Map(smOrder);
		try {
			/* 创建订单 */
			userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, UserOrder.trans_type.WXQR.getCode(), money, UserOrder.cd_type.D.name(), null, null, desc, transPayType,
					cpr.getChannel(), inputAccType, angentType);
			/* 请求微信完成订单 */
			Map<String, String> map = WeixinUtil.doRequest(WeixinUtil.UNIFIEDORDER_SM_URL, requestParam, null);
			if (map != null) {
				if (map.get("return_code").equals("SUCCESS")) {
					/* 完成订单后，更新原有订单 */
					UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(out_trade_no);
					if (userOrder != null) {
						PayOrder payOrder = new PayOrder();

						String resultCode = map.get("result_code");
						String outTradeNo = map.get("out_trade_no");

						int total_fee = Integer.parseInt(map.get("total_fee"));
						payOrder.setPayAmt(BigDecimal.valueOf(total_fee / 100));
						int settlementTotalFee = 0;
						if (map.containsKey("settlement_total_fee")) {
							settlementTotalFee = Integer.parseInt(map.get("settlement_total_fee"));
							payOrder.setRealAmt(BigDecimal.valueOf(settlementTotalFee / 100));
						}
						String time_end = map.get("time_end");

						String transaction_id = map.get("transaction_id");
						payOrder.setPayNo(transaction_id);

						payOrder.setPayFinishDate(time_end);

						if ("SUCCESS".equals(resultCode)) {
							payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
						} else {
							String return_msg = map.get("return_msg");
							payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
							payOrder.setErrorInfo(return_msg);
							String errCode = map.get("err_code");
							log.error("支付失败！out_trade_no:" + outTradeNo + ",result_code:" + resultCode + ", err_code:" + errCode);
						}
						try {
							userOrderService.finishInputOrderStatus(out_trade_no, payOrder);
							return map;
						} catch (Exception e) {
							log.error("----微信订单回调异常", e);
						}

					} else {
						log.info("微信订单：" + out_trade_no + " 不存在或已经被成功处理了");
					}
				} else {
					log.info("Wechatpay communication error : " + map.get("return_msg"));
				}
			} else {
				log.info("Wechatpay error : can not request api");
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
			JSONObject channelConfig = channelService.getChannelConfig(userOrder.getChannelId());
			OrderQueryRequest request = new OrderQueryRequest(channelConfig.getString("wxaccount.appid"), channelConfig.getString("wxaccount.mchId"), channelConfig.getString("wxaccount.subMchId"));
			request.setOut_trade_no(orderNum);
			Map<String, String> requestParam = BeanUtils.bean2Map(request);
			Map<String, String> map = WeixinUtil.doRequest(WeixinUtil.ORDER_STATUS_URL, requestParam, null);
			if (map != null) {
				String returnCode = map.get("return_code");
				if ("SUCCESS".equals(returnCode)) {

					PayOrder payOrder = new PayOrder();

					String resultCode = map.get("result_code");
					String outTradeNo = map.get("out_trade_no");

					String time_end = map.get("time_end");

					String transaction_id = map.get("transaction_id");
					payOrder.setPayNo(transaction_id);

					payOrder.setPayFinishDate(time_end);

					String trade_state = map.get("trade_state");
					Boolean isUpdateOrder = false;
					if ("SUCCESS".equals(resultCode) && trade_state.equals("SUCCESS")) {
						int total_fee = Integer.parseInt(map.get("total_fee"));
						payOrder.setPayAmt(BigDecimal.valueOf(total_fee / 100));
						int settlementTotalFee = 0;
						if (map.containsKey("settlement_total_fee")) {
							settlementTotalFee = Integer.parseInt(map.get("settlement_total_fee"));
							payOrder.setRealAmt(BigDecimal.valueOf(settlementTotalFee / 100));
						}
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
						isUpdateOrder = true;

					} else if ("SUCCESS".equals(resultCode) && (trade_state.equals("PAYERROR") || trade_state.equals("NOTPAY") || trade_state.equals("CLOSED"))) {
						String return_msg = map.get("return_msg");
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
						payOrder.setErrorInfo(return_msg);
						String errCode = map.get("err_code");
						log.error("支付失败！out_trade_no:" + outTradeNo + ",result_code:" + resultCode + ", err_code:" + errCode);
						isUpdateOrder = true;
					}
					if (isUpdateOrder) {
						try {
							userOrderService.finishInputOrderStatus(orderNum, payOrder);
						} catch (Exception e) {
							log.error("----微信订单回调异常", e);
						}
					}
				} else {
					log.error("支付通信失败！");
				}
				return map;
			}
		} else {
			log.info("微信订单：" + orderNum + " 不存在或已经被成功处理了");
		}
		return null;

	}

	@Override
	public Boolean dealDownloadStatement(String dateStr) {

		List<JSONObject> channelCfgs = channelService.getAvailableChannelConfigByChannelName("WEIXIN");
		Boolean flag = false;
		for (JSONObject channelConfig : channelCfgs) {

			String subMchId = channelConfig.getString("wxaccount.subMchId");
			WxDownloadBillRequest request = new WxDownloadBillRequest(channelConfig.getString("wxaccount.appid"), channelConfig.getString("wxaccount.mchId"), subMchId);
			request.setBill_date(dateStr);
			request.setBill_type("SUCCESS");
			Map<String, String> requestParam = BeanUtils.bean2Map(request);
			requestParam.put("nonce_str", StringUtil.getRandomStringByLength(32));
			String sign = SignUtil.getSign(requestParam, (String) ApplicatonStaticUtil.getAppStaticData("wxaccount.appPaySecret"));
			requestParam.put("sign", sign);
			try {
				SSLContext sslContext = SSLContext.getInstance("TLSv1");
				TrustManager[] tm = { new CommonX509TrustManager() };
				sslContext.init(null, tm, new SecureRandom());

				SSLSocketFactory ssf = sslContext.getSocketFactory();
				HttpsURLConnection httpUrlConn = (HttpsURLConnection) new URL(WeixinUtil.DOWN_LOAD_BILL_URL).openConnection();
				httpUrlConn.setSSLSocketFactory(ssf);
				httpUrlConn.setDoOutput(true);
				httpUrlConn.setDoInput(true);
				httpUrlConn.setUseCaches(false);
				httpUrlConn.setRequestMethod("POST");
				OutputStream outputStream = httpUrlConn.getOutputStream();
				outputStream.write(XmlMapper.map2Xml(requestParam).getBytes("UTF-8"));
				outputStream.close();
				InputStream inputStream = httpUrlConn.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String statementFileName = wx_statement_root_path + File.separator + dateStr + "_" + subMchId;
				File f = new File(statementFileName);
				List<String> lines = new ArrayList<String>();
				String str;
				while ((str = bufferedReader.readLine()) != null) {
					lines.add(str);
				}
				FileUtils.writeLines(f, "GBK", lines);
				bufferedReader.close();
				inputStreamReader.close();

				inputStream.close();
				httpUrlConn.disconnect();
				/* 处理对账单 */
				tradeStatementService.dealWeixinStatement(statementFileName, dateStr);
				flag = true;
			} catch (Exception e) {
				log.error("http get throw Exception", e);
			}
		}
		return flag;
	}
}
