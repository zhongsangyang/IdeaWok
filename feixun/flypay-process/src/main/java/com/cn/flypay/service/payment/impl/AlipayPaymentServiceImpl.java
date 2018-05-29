package com.cn.flypay.service.payment.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.ZipUtil;
import com.cn.flypay.utils.alipay.Alipay;

@Service(value = "alipayPaymentService")
public class AlipayPaymentServiceImpl extends AbstractChannelPaymentService {
	private Log log = LogFactory.getLog(getClass());

	@Value("${alipay_statement_root_path}")
	private String alipay_statement_root_path;

	@Override
	public Map<String, String> createUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, Integer angentType, String desc) throws Exception {

		Map<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("return_code", "FAILURE");

		String out_trade_no = commonService.getUniqueOrderByType(UserOrder.trans_type.ALQR.name(), user.getId());

		JSONObject channelConfig = cpr.getConfig();

		AlipayClient client = new DefaultAlipayClient(channelConfig.getString("alipay.url"), channelConfig.getString("alipay.appId"), channelConfig.getString("alipay.privateKey"), "json", "utf-8",
				channelConfig.getString("alipay.alipayPublicKey"));
		// 实例化具体API对应的request类,类名称和接口名称对应，当前调用接口名称：alipay.offline.material.image.upload
		AlipayTradePrecreateRequest alipayRequest = new AlipayTradePrecreateRequest();
		alipayRequest.setNotifyUrl(channelConfig.getString("alipay.qr_pay_notify"));
		JSONObject alipayJson = new JSONObject();
		alipayJson.put("subject", desc);
		alipayJson.put("out_trade_no", out_trade_no);
		alipayJson.put("total_amount", money);
		alipayJson.put("timeout_express", "4h");
		JSONObject extendParamsJson = new JSONObject();
		extendParamsJson.put("sys_service_provider_id", ApplicatonStaticUtil.getAppStaticData("alipay.isv.pid"));
		alipayJson.put("extend_params", extendParamsJson);
		alipayRequest.setBizContent(alipayJson.toString());
		AlipayTradePrecreateResponse alipayResponse = client.execute(alipayRequest);
		alipayResponse = client.execute(alipayRequest);

		if (alipayResponse.getCode().equals(Alipay.RETURN_CODE_SUCCESS)) {
			userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, UserOrder.trans_type.ALQR.getCode(), money, UserOrder.cd_type.D.name(), null, null, desc, transPayType,
					cpr.getChannel(), inputAccType, angentType);
			returnMap.put("return_code", "SUCCESS");
			returnMap.put("result_code", "SUCCESS");
			returnMap.put("orderNum", out_trade_no);
			returnMap.put("code_url", alipayResponse.getQrCode());// 支付二维码
			log.info(alipayResponse.getQrCode());
		} else {
			log.info("ALIPAY communication error ");
		}
		return returnMap;

	}

	@Override
	public Map<String, String> createSmUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, String authCode, Double money, Integer transPayType, Integer angentType, String desc)
			throws Exception {

		Map<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("return_code", "FAILURE");

		JSONObject channelConfig = cpr.getConfig();
		/* 用户订单号= 用户ID + 14位时间戳+ userType（2位） */
		String out_trade_no = commonService.getUniqueOrderByType(UserOrder.trans_type.ALSM.name(), user.getId());
		AlipayClient client = new DefaultAlipayClient(channelConfig.getString("alipay.url"), channelConfig.getString("alipay.appId"), channelConfig.getString("alipay.privateKey"), "json", "utf-8",
				channelConfig.getString("alipay.alipayPublicKey"));
		// 实例化具体API对应的request类,类名称和接口名称对应，当前调用接口名称：alipay.offline.material.image.upload
		AlipayTradePayRequest alipayRequest = new AlipayTradePayRequest();
		alipayRequest.setNotifyUrl(Alipay.qr_pay_notify);
		JSONObject alipayJson = new JSONObject();
		alipayJson.put("subject", desc);
		alipayJson.put("out_trade_no", out_trade_no);
		alipayJson.put("scene", "bar_code");
		alipayJson.put("auth_code", authCode);
		alipayJson.put("total_amount", money);
		JSONObject extendParamsJson = new JSONObject();
		extendParamsJson.put("sys_service_provider_id", ApplicatonStaticUtil.getAppStaticData("alipay.isv.pid"));
		alipayJson.put("extend_params", extendParamsJson);
		alipayRequest.setBizContent(alipayJson.toString());
		AlipayTradePayResponse alipayResponse = client.execute(alipayRequest);
		try {
			/* 创建订单 */
			userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, UserOrder.trans_type.ALSM.getCode(), money, UserOrder.cd_type.D.name(), null, null, desc, transPayType,
					cpr.getChannel(), inputAccType, angentType);
			/* 支付宝请求完成订单 */
			/* 完成订单后，更新原有订单 */
			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(out_trade_no);
			if (userOrder != null) {
				PayOrder payOrder = new PayOrder();
				Boolean changeFlag = false;
				log.info(alipayResponse);
				if (alipayResponse.getCode().equals(Alipay.RETURN_CODE_SUCCESS)) {
					payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(alipayResponse.getTotalAmount())));
					payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(alipayResponse.getReceiptAmount())));
					String time_end = DateUtil.getDateTime("yyyyMMddHHmmss", alipayResponse.getGmtPayment());

					payOrder.setPayNo(alipayResponse.getTradeNo());
					payOrder.setPayFinishDate(time_end);
					payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
					/* 支付成功 */
					returnMap.put("return_code", "SUCCESS");
					returnMap.put("result_code", "SUCCESS");
					changeFlag = true;
				} else if (alipayResponse.getCode().equals(Alipay.RETURN_CODE_FAILURE)) {
					payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
					payOrder.setErrorInfo(alipayResponse.getMsg() + "--" + alipayResponse.getSubMsg());
					log.info("支付失败！out_trade_no: " + out_trade_no + " " + alipayResponse.getMsg() + "---" + alipayResponse.getSubMsg());
					changeFlag = true;
				} else {
					returnMap.put("return_code", GlobalConstant.WAITING);
				}
				if (changeFlag) {
					try {
						userOrderService.finishInputOrderStatus(out_trade_no, payOrder);
					} catch (Exception e) {
						log.error("----支付宝订单回调异常", e);
					}
				}
			} else {
				log.info("支付宝订单：" + out_trade_no + " 不存在或已经被成功处理了");
			}
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		return returnMap;

	}

	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {

		UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNum);
		if (userOrder != null) {
			JSONObject channelConfig = channelService.getChannelConfig(userOrder.getChannelId());
			AlipayClient alipayClient = new DefaultAlipayClient(channelConfig.getString("alipay.url"), channelConfig.getString("alipay.appId"), channelConfig.getString("alipay.privateKey"), "json",
					"utf-8", channelConfig.getString("alipay.alipayPublicKey"));
			AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
			try {
				JSONObject alipayJson = new JSONObject();
				alipayJson.put("out_trade_no", orderNum);
				alipayRequest.setBizContent(alipayJson.toString());
				AlipayTradeQueryResponse response = alipayClient.execute(alipayRequest);
				if (response.isSuccess()) {

					if ("TRADE_SUCCESS".equals(response.getTradeStatus())) {
						PayOrder payOrder = new PayOrder();
						payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(response.getTotalAmount())));
						payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(response.getReceiptAmount())));
						String time_end = DateUtil.getDateTime("yyyy-Mm-dd HH:mm:ss", response.getSendPayDate());

						String transaction_id = response.getTradeNo();
						payOrder.setPayNo(transaction_id);
						payOrder.setPayFinishDate(time_end);

						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
						try {
							userOrderService.finishInputOrderStatus(orderNum, payOrder);
						} catch (Exception e) {
							log.error("----支付宝订单回调异常", e);
						}

					} else {
						log.error("支付通信失败！");
					}
				} else {
				}
			} catch (AlipayApiException e) {
				log.error(e);
			}
		} else {
			log.info("支付宝订单：" + orderNum + " 不存在或已经被成功处理了");
		}
		return null;
	}

	@Override
	public Boolean dealDownloadStatement(String dateStr) {

		List<JSONObject> channelCfgs = channelService.getAvailableChannelConfigByChannelName("ALIPAY");
		Boolean flag = false;
		for (JSONObject channelConfig : channelCfgs) {
			try {
				String appId = channelConfig.getString("alipay.appId");
				AlipayClient alipayClient = new DefaultAlipayClient(channelConfig.getString("alipay.url"), channelConfig.getString("alipay.appId"), channelConfig.getString("alipay.privateKey"),
						"json", "utf-8", channelConfig.getString("alipay.alipayPublicKey"));
				AlipayDataDataserviceBillDownloadurlQueryRequest alipayRequest = new AlipayDataDataserviceBillDownloadurlQueryRequest();
				try {
					JSONObject alipayJson = new JSONObject();
					alipayJson.put("bill_type", "trade");
					alipayJson.put("bill_date", DateUtil.getStringFromDate(DateUtil.convertStringToDate("yyyyMMdd", dateStr), "yyyy-MM-dd"));
					alipayRequest.setBizContent(alipayJson.toString());
					AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(alipayRequest);
					if (response.isSuccess()) {
						String downloadUrl = response.getBillDownloadUrl();
						log.info(downloadUrl);
						if (StringUtil.isNotBlank(downloadUrl)) {

							// 指定希望保存的文件路径
							File file = new File(alipay_statement_root_path);
							if (!file.exists()) {
								file.mkdirs();
							}
							String filePath = alipay_statement_root_path + File.separator + dateStr + "_" + appId + ".zip";
							URL url = new URL(response.getBillDownloadUrl());
							HttpURLConnection httpUrlConnection = null;
							InputStream fis = null;
							FileOutputStream fos = null;
							try {
								httpUrlConnection = (HttpURLConnection) url.openConnection();
								httpUrlConnection.setConnectTimeout(5 * 10000);
								httpUrlConnection.setDoInput(true);
								httpUrlConnection.setDoOutput(true);
								httpUrlConnection.setUseCaches(false);
								httpUrlConnection.setRequestMethod("GET");
								httpUrlConnection.setRequestProperty("Charsert", "UTF-8");
								httpUrlConnection.connect();
								fis = httpUrlConnection.getInputStream();
								byte[] temp = new byte[1024];
								int b;
								fos = new FileOutputStream(new File(filePath));
								while ((b = fis.read(temp)) != -1) {
									fos.write(temp, 0, b);
									fos.flush();
								}
								/* 解压缩 */
								ZipUtil.unzip(filePath, alipay_statement_root_path, true);
								/* 处理对账单 */
								tradeStatementService.dealAlipayStatement(alipay_statement_root_path + File.separator + dateStr + "_" + appId, dateStr);
							} catch (MalformedURLException e) {
								log.error(e);
								e.printStackTrace();
							} catch (IOException e) {
								log.error(e);
							} catch (Exception e) {
								log.error(e);
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
						} else {
							log.info(dateStr + "_" + appId + "无对账单");
						}
					} else {
						log.info(dateStr + "_" + appId + "无对账单");
					}
				} catch (AlipayApiException e) {
					log.error(e);
				} catch (IOException e) {
					log.error(e);
				} catch (ParseException e1) {
					log.error(e1);
				}
				flag = true;
			} catch (Exception e) {
				log.error(e);
			}
		}
		return flag;
	}
}
