package com.cn.flypay.utils.shenfu;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 申孚请求发送客户端
 * 
 * @author TD
 *
 */
public class ShenFuHttpSender {

	private static final String RE_SEND_PATH = System.getenv("HOME") + "/resend/";
	private boolean resend = false;
	private static final Logger log = LoggerFactory.getLogger(ShenFuHttpSender.class);

	public ShenFuHttpSender() {
	}

	public ShenFuHttpSender(final String reSendPath, Integer reSendTime) {
		resend = true;
	}

	public enum SEND_METHOD {
		POST, GET
	}

	private boolean useProxy = false;

	public String httpPost(String url, Map<String, Object> data) throws Exception {

		HttpClient httpClient = new HttpClient();
		// 设置超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
		httpClient.getParams().setContentCharset("utf-8");

		// 设置发送URL
		String sendURL = url;
		// 根据发送方式选择发送方法
		HttpMethod method = null;
		String result = null;

		PostMethod postMethod = new PostMethod(sendURL);
		Part[] parts = new Part[data.size()];
		int index = 0;
		for (Entry<String, Object> part : data.entrySet()) {
			if (part.getValue() instanceof File) {
				parts[index] = new FilePart(part.getKey(), (File) part.getValue());
			} else if (part.getValue() instanceof String) {
				StringPart stringPart = new StringPart(part.getKey(), (String) part.getValue(), "utf-8");
				stringPart.setTransferEncoding(null);
				stringPart.setContentType(null);
				// stringPart.setCharSet(null);
				parts[index] = stringPart;
			} else {
				throw new Exception("未知的报文信息：" + part.getValue());
			}
			index++;
		}

		MultipartRequestEntity mre = new MultipartRequestEntity(parts, postMethod.getParams());
		postMethod.setRequestEntity(mre);
		method = postMethod;

		String charset = null;
		// 发送数据
		try {
			log.info("SEND INFO - URL[" + sendURL + "] CHAR_SET[" + charset + "] SEND_MOTHOD[ POST ]");
			log.info("SEND DATA - " + data);

			int statusCode = httpClient.executeMethod(method);
			this.statusCode = statusCode;

			byte[] responseBody = method.getResponseBody();
			Header responseHead = method.getResponseHeader("Content-Type");

			if (responseHead != null) {
				int index2;
				if ((index2 = responseHead.getValue().indexOf("charset")) >= 0) {
					charset = responseHead.getValue().substring(index2 + 8);
				}
			}
			result = new String(responseBody, charset);
			log.info("SEND STATUS[" + statusCode + "]  RESULT [" + result + "]");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			method.releaseConnection();
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
		return result;

	}

	/**
	 * 
	 * @param ipAddress
	 * @param port
	 * @param charset
	 * @param timeout
	 * @param data
	 * @param head
	 * @param methodParams
	 * @param sendMethod
	 * @return
	 * @throws Exception
	 */
	public String mercRegister(Map<String, Object> data) throws Exception {
		HttpClient httpClient = new HttpClient();
		// 设置超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
		httpClient.getParams().setContentCharset("utf-8");

		// 设置发送URL
		String sendURL = "http://bp.shenfupay.com/SF/mercRegister";
		// 根据发送方式选择发送方法
		HttpMethod method = null;
		String result = null;

		PostMethod postMethod = new PostMethod(sendURL);
		Part[] parts = new Part[data.size()];
		int index = 0;
		for (Entry<String, Object> part : data.entrySet()) {
			if (part.getValue() instanceof File) {
				parts[index] = new FilePart(part.getKey(), (File) part.getValue());
			} else if (part.getValue() instanceof String) {
				StringPart stringPart = new StringPart(part.getKey(), (String) part.getValue(), "utf-8");
				stringPart.setTransferEncoding(null);
				stringPart.setContentType(null);
				// stringPart.setCharSet(null);
				parts[index] = stringPart;
			} else {
				throw new Exception("未知的报文信息：" + part.getValue());
			}
			index++;
		}

		MultipartRequestEntity mre = new MultipartRequestEntity(parts, postMethod.getParams());
		postMethod.setRequestEntity(mre);
		method = postMethod;

		String charset = null;
		// 发送数据
		try {
			log.info("SEND INFO - URL[" + sendURL + "] CHAR_SET[" + charset + "] SEND_MOTHOD[ POST ]");
			log.info("SEND DATA - " + data);

			int statusCode = httpClient.executeMethod(method);
			this.statusCode = statusCode;

			byte[] responseBody = method.getResponseBody();
			Header responseHead = method.getResponseHeader("Content-Type");

			if (responseHead != null) {
				int index2;
				if ((index2 = responseHead.getValue().indexOf("charset")) >= 0) {
					charset = responseHead.getValue().substring(index2 + 8);
				}
			}
			result = new String(responseBody, charset);
			log.info("SEND STATUS[" + statusCode + "]  RESULT [" + result + "]");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			method.releaseConnection();
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
		return result;
	}

	public String shenfuD0(Map<String, String> data) throws Exception {
		HttpClient httpClient = new HttpClient();
		// 设置超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
		httpClient.getParams().setContentCharset("utf-8");

		// 设置发送URL
		String sendURL = ApplicationBase.CONSUMED0_URL;
		// 根据发送方式选择发送方法
		HttpMethod method = null;
		String result = null;

		PostMethod postMethod = new PostMethod(sendURL);
		Part[] parts = new Part[data.size()];
		int index = 0;
		for (Entry<String, String> part : data.entrySet()) {
			StringPart stringPart = new StringPart(part.getKey(), (String) part.getValue(), "utf-8");
			stringPart.setTransferEncoding(null);
			stringPart.setContentType(null);
			// stringPart.setCharSet(null);
			parts[index] = stringPart;
			index++;
		}

		MultipartRequestEntity mre = new MultipartRequestEntity(parts, postMethod.getParams());
		postMethod.setRequestEntity(mre);
		method = postMethod;

		String charset = null;
		// 发送数据
		try {
			log.info("SEND INFO - URL[" + sendURL + "] CHAR_SET[" + charset + "] SEND_MOTHOD[ POST ]");
			log.info("SEND DATA - " + data);

			int statusCode = httpClient.executeMethod(method);
			this.statusCode = statusCode;

			byte[] responseBody = method.getResponseBody();
			Header responseHead = method.getResponseHeader("Content-Type");

			if (responseHead != null) {
				int index2;
				if ((index2 = responseHead.getValue().indexOf("charset")) >= 0) {
					charset = responseHead.getValue().substring(index2 + 8);
				}
			}
			result = new String(responseBody, charset);
			log.info("SEND STATUS[" + statusCode + "]  RESULT [" + result + "]");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			method.releaseConnection();
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
		return result;
	}

	/**
	 * 
	 * @param ipAddress
	 * @param port
	 * @param charset
	 * @param timeout
	 * @param data
	 * @param head
	 * @param methodParams
	 * @param sendMethod
	 * @return
	 * @throws Exception
	 */
	public String mercRegister(String ipAddress, Integer port, String charset, Integer timeout, Map<String, Object> data, SEND_METHOD sendMethod) throws Exception {
		HttpClient httpClient = new HttpClient();
		// 设置超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);
		httpClient.getParams().setContentCharset(charset);

		// 设置发送URL
		HttpMethod method = null;
		String sendURL = null;
		// sendURL = ipAddress.toLowerCase().indexOf("http") >= 0 ? ipAddress
		// : "http://" + ipAddress + ":" + port;
		sendURL = "http://bp.shenfupay.com/SF/mercRegister";
		// 根据发送方式选择发送方法
		String result = null;

		switch (sendMethod) {
		case POST:

			PostMethod postMethod = new PostMethod(sendURL);
			Part[] parts = new Part[data.size()];
			int index = 0;
			for (Entry<String, Object> part : data.entrySet()) {
				if (part.getValue() instanceof File) {
					parts[index] = new FilePart(part.getKey(), (File) part.getValue());
				} else if (part.getValue() instanceof String) {
					StringPart stringPart = new StringPart(part.getKey(), (String) part.getValue(), charset);
					stringPart.setTransferEncoding(null);
					stringPart.setContentType(null);
					// stringPart.setCharSet(null);
					parts[index] = stringPart;
				} else {
					throw new Exception("未知的报文信息：" + part.getValue());
				}
				index++;
			}

			MultipartRequestEntity mre = new MultipartRequestEntity(parts, postMethod.getParams());

			postMethod.setRequestEntity(mre);

			// postMethod.setRequestEntity(new StringRequestEntity(data));
			method = postMethod;
			break;
		case GET:
			throw new Exception("传输文件只允许使用POST 提交");
		}

		// 发送数据
		try {

			log.info("SEND INFO - URL[" + sendURL + "] CHAR_SET[" + charset + "] SEND_MOTHOD[" + sendMethod + "]");
			log.info("SEND DATA - " + data);

			int statusCode = httpClient.executeMethod(method);
			this.statusCode = statusCode;
			// if (statusCode != HttpStatus.SC_OK) {
			// writeResendInfo(charset, timeout, data, sendMethod, sendURL);
			// }

			byte[] responseBody = method.getResponseBody();

			Header responseHead = method.getResponseHeader("Content-Type");

			if (responseHead != null) {
				int index;
				if ((index = responseHead.getValue().indexOf("charset")) >= 0) {
					charset = responseHead.getValue().substring(index + 8);
				}
			}

			result = new String(responseBody, charset);
			log.info("SEND STATUS[" + statusCode + "]  RESULT [" + result + "]");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			method.releaseConnection();
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
		return result;
	}

	public String mercUpdate(String mercUpdateUrl, String charset, Integer timeout, Map<String, Object> data, SEND_METHOD sendMethod) throws Exception {
		HttpClient httpClient = new HttpClient();
		// 设置超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);
		httpClient.getParams().setContentCharset(charset);

		// 设置发送URL
		HttpMethod method = null;
		String sendURL = mercUpdateUrl == null ? "http://bp.shenfupay.com/SF/mercRegister" : mercUpdateUrl;

		// 根据发送方式选择发送方法
		String result = null;

		switch (sendMethod) {
		case POST:

			PostMethod postMethod = new PostMethod(sendURL);
			Part[] parts = new Part[data.size()];
			int index = 0;
			for (Entry<String, Object> part : data.entrySet()) {
				if (part.getValue() instanceof File) {
					parts[index] = new FilePart(part.getKey(), (File) part.getValue());
				} else if (part.getValue() instanceof String) {
					StringPart stringPart = new StringPart(part.getKey(), (String) part.getValue(), charset);
					stringPart.setTransferEncoding(null);
					stringPart.setContentType(null);
					// stringPart.setCharSet(null);
					parts[index] = stringPart;
				} else {
					throw new Exception("未知的报文信息：" + part.getValue());
				}
				index++;
			}
			MultipartRequestEntity mre = new MultipartRequestEntity(parts, postMethod.getParams());
			postMethod.setRequestEntity(mre);
			method = postMethod;
			break;
		case GET:
			throw new Exception("传输文件只允许使用POST 提交");
		}

		// 发送数据
		try {
			log.info("SEND UPDATE INFO - URL[" + sendURL + "] CHAR_SET[" + charset + "] SEND_MOTHOD[" + sendMethod + "]");
			log.info("SEND UPDATE DATA - " + data);
			int statusCode = httpClient.executeMethod(method);
			this.statusCode = statusCode;

			byte[] responseBody = method.getResponseBody();
			Header responseHead = method.getResponseHeader("Content-Type");

			if (responseHead != null) {
				int index;
				if ((index = responseHead.getValue().indexOf("charset")) >= 0) {
					charset = responseHead.getValue().substring(index + 8);
				}
			}

			result = new String(responseBody, charset);
			log.info("SEND UPDATE STATUS[" + statusCode + "]  RESULT [" + result + "]");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			method.releaseConnection();
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
		return result;
	}

	private int statusCode;

	public int getStatusCode() {
		return statusCode;
	}

	public static Map<String, Object> getMercRegisterParam() {
		Map<String, Object> params = new HashMap<String, Object>();

		File idcard_front = new File("D:/flypay/shenfuimage/idcard_front.png");
		File idcard_back = new File("D:/flypay/shenfuimage/idcard_back.png");
		File idcard_hold = new File("D:/flypay/shenfuimage/idcard_hold.png");
		File bankcard_front = new File("D:/flypay/shenfuimage/bankcard_front.png");
		File bankcard_back = new File("D:/flypay/shenfuimage/bankcard_back.png");
		// 新增参数
		params.put("agentid", "AGE000000902423");
		params.put("mercnam", "中华老字号");//
		params.put("comtype", "3");
		params.put("address", "上海浦东新区纳贤路800号");
		params.put("merctel", "13052595515");//
		params.put("mercemail", "guangchun.li@qq.com");
		params.put("cityid", "021");//
		params.put("merchantnature", "0");
		params.put("legalperson", "李广春");//
		params.put("corporateidentity", "411503198702110416");//
		params.put("actname", "李广春");//
		params.put("openbank", "中国建设银行股份有限公司上海川沙支行");
		params.put("actno", "6217001210092813227");//
		params.put("rcvbanksettleno", "105290041007");

		params.put("feerat", "0.3");//
		params.put("crefeerat", "0.3");//
		params.put("d0feerat", "0");//
		params.put("d0channelfee", "0");
		params.put("settelfee", "0");

		params.put("img001", idcard_front);
		params.put("img002", idcard_back);
		params.put("img003", idcard_hold);
		params.put("img004", bankcard_front);
		params.put("img005", bankcard_back);

		return params;
	}

	public static void doMercRegister() {
		ShenFuHttpSender httpSend = new ShenFuHttpSender();
		Map<String, Object> params = getMercRegisterParam();
		try {
			httpSend.mercRegister("http://bp.shenfupay.com/SF/mercRegister", 80, "utf-8", 100000, params, ShenFuHttpSender.SEND_METHOD.POST);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Object> getMercUpdaterParam() {
		Map<String, Object> params = new HashMap<String, Object>();
		// File idcard_front = new
		// File("D:/flypay/shenfuimage/idcard_front.png");
		// File idcard_back = new File("D:/flypay/shenfuimage/idcard_back.png");
		// File idcard_hold = new File("D:/flypay/shenfuimage/idcard_hold.png");
		// File bankcard_front = new
		// File("D:/flypay/shenfuimage/bankcard_front.png");
		// File bankcard_back = new
		// File("D:/flypay/shenfuimage/bankcard_back.png");

		// 新增参数
		params.put("agentid", "AGE000000902423");
		params.put("mercid", "486000000108493");
		params.put("merctel", "13052595515");
		params.put("actname", "李广春");
		// params.put("openbank", "中国建设银行股份有限公司上海川沙支行");
		params.put("openbank", "农业银行");
		params.put("actno", "6217001210092813227");
		// params.put("rcvbanksettleno", "105290041007");// real
		params.put("rcvbanksettleno", "103582272708");//

		// params.put("mercnam", "中华老字号");
		// params.put("comtype", "3");
		// params.put("address", "上海浦东新区纳贤路800号");
		// params.put("mercemail", "guangchun.li@qq.com");
		// params.put("cityid", "021");//
		// params.put("merchantnature", "0");
		// params.put("legalperson", "李广春");
		// params.put("corporateidentity", "411503198702110416");
		params.put("feerat", "0.21");
		params.put("crefeerat", "0.21");
		params.put("d0feerat", "0.24");
		params.put("d0channelfee", "0");
		params.put("settelfee", "0");
		// params.put("img001", idcard_front);
		// params.put("img002", idcard_back);
		// params.put("img003", idcard_hold);
		// params.put("img004", bankcard_front);
		// params.put("img005", bankcard_back);

		return params;
	}

	public static void doMercUpdate() {
		ShenFuHttpSender httpSend = new ShenFuHttpSender();
		Map<String, Object> params = getMercUpdaterParam();
		try {
			httpSend.mercUpdate("http://bp.shenfupay.com/SF/help/mercUpdate.action", "utf-8", 100000, params, ShenFuHttpSender.SEND_METHOD.POST);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		// doMercRegister();
		// doMercUpdate();
		BigDecimal fee = new BigDecimal("0.0025");
		BigDecimal fee2 = fee.multiply(new BigDecimal(100));
		BigDecimal fee3 = fee2.setScale(2, BigDecimal.ROUND_DOWN);
		System.out.println(fee);
		System.out.println(fee2.toString());

		System.out.println(fee3.toString());
	}

}
