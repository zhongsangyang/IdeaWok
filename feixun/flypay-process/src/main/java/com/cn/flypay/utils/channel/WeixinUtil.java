package com.cn.flypay.utils.channel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.http.conn.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.flypay.pageModel.statement.WeixinStatement;
import com.cn.flypay.pageModel.statement.WeixinStatementDetail;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.CommonX509TrustManager;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.SignUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;

public class WeixinUtil {
	private static Logger logger = LoggerFactory.getLogger(WeixinUtil.class);

	public static final String UNIFIEDORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	public static final String UNIFIEDORDER_SM_URL = "https://api.mch.weixin.qq.com/pay/micropay";
	/**
	 * 微信订单查询
	 */
	public static final String ORDER_STATUS_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
	public static final String DOWN_LOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";

	public static final String GET_OAUTH_CODE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s#wechat_redirect";
	// 网页授权OAuth2.0获取token
	public static final String GET_OAUTH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
	
	public static final String USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";

	public static final String DESC_SELF_PAY = "您正在向%s支付%s元";
	public static final String DESC_SELF_PAY_AGENT = "您支付%s元购买%s";
	public static final String DESC_GROP_PAY = "您正在向%s支付%s元";
	private static String caPath = "d:/cert";

	/**
	 * 根据url 发送支付请求
	 * 
	 * @param url
	 * @param requestParam
	 * @param caPsw
	 *            密钥
	 * @return
	 */
	public static Map<String, String> doRequest(String url, Map<String, String> requestParam, String caPsw) {
		requestParam.put("nonce_str", StringUtil.getRandomStringByLength(32));
		String sign = SignUtil.getSign(requestParam,
				(String) ApplicatonStaticUtil.getAppStaticData("wxaccount.appPaySecret"));
		requestParam.put("sign", sign);
		String xmlParam = XmlMapper.map2Xml(requestParam);
		logger.info(xmlParam);
		Map<String, String> result = httpsXmlRequest(url, "POST", xmlParam, caPsw);
		logger.info(XmlMapper.map2Xml(result));
		if (result != null) {
			if (result.containsKey("return_code") && result.get("return_code").equals("SUCCESS")) {
				if (result.get("result_code").equals("SUCCESS")) {
					logger.info("Wechatpay request success");
					return result;
				} else {
					logger.info("Wechatpay business error : err_code " + result.get("err_code") + ", err_code_des "
							+ result.get("err_code_des"));
				}
			} else {
				logger.info("Wechatpay communication error : " + result.get("return_msg"));
			}
		} else {
			logger.info("Wechatpay error : can not request api");
		}
		return null;
	}

	/**
	 * 发送https请求
	 * 
	 * @param requestUrl
	 * @param requestMethod
	 * @param outputStr
	 * @param caPsw
	 * @return
	 */
	public static Map<String, String> httpsXmlRequest(String requestUrl, String requestMethod, String outputStr,
			String caPsw) {
		StringBuilder buffer = new StringBuilder();
		try {
			System.setProperty("https.protocols", "TLSv1");
			SSLContext sslContext = SSLContext.getInstance("TLSv1");

			if (StringUtil.isNullOrEmpty(caPsw)) {
				TrustManager[] tm = { new CommonX509TrustManager() };
				sslContext.init(null, tm, new SecureRandom());
			} else {
				KeyStore keyStore = KeyStore.getInstance("PKCS12");
				FileInputStream instream = new FileInputStream(caPath);
				keyStore.load(instream, caPsw.toCharArray());
				sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, caPsw.toCharArray()).build();
				instream.close();
			}

			SSLSocketFactory ssf = sslContext.getSocketFactory();
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) new URL(requestUrl).openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);

			httpUrlConn.setRequestMethod(requestMethod);
			if ("GET".equalsIgnoreCase(requestMethod)) {
				httpUrlConn.connect();
			}
			if (outputStr != null) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();

			inputStream.close();
			httpUrlConn.disconnect();
			logger.info(buffer.toString());
			return XmlMapper.xml2Map(buffer.toString());
		} catch (ConnectException e) {
			logger.error("ConnectException :{}", e);
		} catch (Exception e) {
			logger.error("https request error:{}", e);
		}
		return null;
	}

	/**
	 * 发送https请求
	 * 
	 * @param requestUrl
	 * @param requestMethod
	 * @param outputStr
	 * @param caPsw
	 * @return
	 */
	public static String httpsStringRequest(String requestUrl, String requestMethod, String outputStr, String caPsw) {
		StringBuilder buffer = new StringBuilder();
		try {
			System.setProperty("https.protocols", "TLSv1");
			SSLContext sslContext = SSLContext.getInstance("TLSv1");

			if (StringUtil.isNullOrEmpty(caPsw)) {
				TrustManager[] tm = { new CommonX509TrustManager() };
				sslContext.init(null, tm, new SecureRandom());
			} else {
				KeyStore keyStore = KeyStore.getInstance("PKCS12");
				FileInputStream instream = new FileInputStream(caPath);
				keyStore.load(instream, caPsw.toCharArray());
				sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, caPsw.toCharArray()).build();
				instream.close();
			}

			SSLSocketFactory ssf = sslContext.getSocketFactory();
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) new URL(requestUrl).openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);

			httpUrlConn.setRequestMethod(requestMethod);
			if ("GET".equalsIgnoreCase(requestMethod)) {
				httpUrlConn.connect();
			}
			if (outputStr != null) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();

			inputStream.close();
			httpUrlConn.disconnect();
			return buffer.toString();
		} catch (ConnectException e) {
			logger.error("ConnectException :{}", e);
		} catch (Exception e) {
			logger.error("https request error:{}", e);
		}
		return null;
	}

	/**
	 * 获取微信对账单信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public static WeixinStatement getWeixinStatement(String filePath) throws Exception {
		File statementFile = new File(filePath);
		File forder = new File(statementFile.getParent() + File.separator + "wx_stm_"
				+ DateUtil.convertDateStrYYYYMMDD(new Date()));
		if (!forder.exists()) {
			forder.mkdirs();
		}
		if (statementFile.isFile()) {
			try {
				FileUtils.copyFileToDirectory(statementFile, forder);
				WeixinStatement wxs = new WeixinStatement();
				List<String> rows = FileUtils.readLines(statementFile, "GBK");
				String start = rows.get(0);
				if (start.startsWith("<xml>")) {
					return wxs;
				}
				String endRow = rows.get(rows.size() - 1);
				String[] endcls = endRow.split(",");
				wxs.setTotalNum(Long.parseLong(endcls[0].substring(1)));
				wxs.setTotalAmt(Double.parseDouble(endcls[1].substring(1)));
				wxs.setTotalRefundAmt(Double.parseDouble(endcls[2].substring(1)));
				wxs.setTotalBonusRefundAmt(Double.parseDouble(endcls[3].substring(1)));
				wxs.setTotalFeeAmt(Double.parseDouble(endcls[4].substring(1)));
				for (int i = 1; i < rows.size() - 2; i++) {
					String row = rows.get(i);
					String[] cls = row.split(",");
					WeixinStatementDetail wsd = new WeixinStatementDetail();

					wsd.setTranDateStr(cls[0].substring(1));
					wsd.setWeixinNum(cls[5].substring(1));
					wsd.setOrderNum(cls[6].substring(1));
					wsd.setUserInfo(cls[7].substring(1));
					wsd.setTranType(cls[8].substring(1));
					wsd.setTranStatus(cls[9].substring(1));
					wsd.setCft(cls[10].substring(1));
					wsd.setCcy(cls[11].substring(1));
					wsd.setTotalAmt(Double.parseDouble(cls[12].substring(1)));
					wsd.setFee(Double.parseDouble(cls[16].substring(1)));
					wsd.setFeeRate(cls[17].substring(1));
					if (i == 1) {
						wxs.setFeeRate(BigDecimal.valueOf(Double.parseDouble(cls[17].substring(1, cls[17].length() - 1))));
					}
					wxs.getDetails().add(wsd);
				}
				statementFile.deleteOnExit();
				return wxs;
			} catch (IOException e) {
				logger.error("解析微信对账文件出错", e);
				throw e;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println("`0.60%".substring(1, "`0.60%".length() - 1));
	}
}