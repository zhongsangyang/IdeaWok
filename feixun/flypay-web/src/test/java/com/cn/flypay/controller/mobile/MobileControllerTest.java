package com.cn.flypay.controller.mobile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.ImportUtil;
import com.cn.flypay.utils.MD5Util;
import com.cn.flypay.utils.pingan.MsgInfoDESUtil;

public class MobileControllerTest {
	private static Log log = LogFactory.getLog(MobileControllerTest.class);

	private static String token = "1httw6m8u6ocw";
//	static String remoteUrl = "http://127.0.0.1:8080/flypayfx/mobile";

 static String remoteUrl = "http://xymtian.6655.la/flypayfx/mobile";

	public void testQueryAgentId() {
		try {
			String t = httpJsonRequest(remoteUrl + "/queryAgentId", "POST", "");
			System.out.println(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testLogin() {
		try {
			JSONObject json = new JSONObject();
			json.put("agentId", "F20160001");
			json.put("loginName", "18068089860");
			json.put("loginPwd", MD5Util.md5("123456"));
			json.put("versionId", "1");

			json.put("appType", "ios");

			String t = httpJsonRequest(remoteUrl + "/login", "POST", json.toString());
			System.out.println(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void register() {
		System.out.println();
		JSONObject json = new JSONObject();
		json.put("agentId", "F20160003");
		json.put("loginName", "18068089863");
		json.put("smsCode", "455931");
		json.put("msgCode", "36f17c1de8e74a83a943ac44b874de73");
		json.put("loginPwd", "123456");
		json.put("chnlId", "003M62");
		json.put("appType", "ios");
		try {
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/register", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testSendRegisterSms() {
		try {
			String agentId = "F20160001";
			String phone = "13817117644";
			String smsType = "10";
			String chk = MD5Util.md5(agentId.trim() + phone.trim() + smsType + "flypayzc");

			JSONObject json = new JSONObject();
			json.put("agentId", agentId);
			json.put("smsType", smsType);
			json.put("phone", phone);
			json.put("chkValue", chk);
			json.put("appType", "ios");
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/sendRegisterSms", "POST", json.toString());
			/* 验证签名 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testSendSms() {
		try {
			String agentId = "FFF001";

			JSONObject json = new JSONObject();
			json.put("agentId", agentId);
			json.put("smsType", "20");
			json.put("loginName", "13817117644");
			json.put("merId", "1");
			json.put("appType", "ios");

			String chk = MD5Util.md5(agentId.trim() + "1" + "13817117644" + "20flypaydx");
			System.out.println(chk);
			System.out.println(agentId.trim() + "1" + "13817117644" + "20flypaydx");
			json.put("chkValue", chk);
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/sendSms", "POST", json.toString());
			/* 验证签名 FFF0011超级管理员20flypaydx */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testMobileExistVerify() {
		try {
			String agentId = "FFF001";
			String phone = "13817117644";
			JSONObject json = new JSONObject();
			json.put("agentId", agentId);
			json.put("phone", phone);
			json.put("appType", "ios");
			String chk = MD5Util.md5(agentId.trim() + phone + "flypaysjyz");
			json.put("chkValue", chk);
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/mobileExistVerify", "POST", json.toString());
			/* 验证签名 FFF0011超级管理员20flypaydx */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testSmsCodeVerify() {
		try {

			String agentId = "FFF001";
			String smscode = "907985";
			String msgCode = "71f9acaaa2f842f590173ee5710a3a6e";
			JSONObject json = new JSONObject();
			json.put("agentId", agentId);
			json.put("smsCode", "907985");
			json.put("msgCode", "71f9acaaa2f842f590173ee5710a3a6e");
			json.put("appType", "ios");
			String chk = MD5Util.md5(agentId.trim() + smscode + msgCode + "flypayyzmyz");
			System.out.println(chk);
			json.put("chkValue", chk);
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/smsCodeVerify", "POST", json.toString());
			/* 验证签名 FFF0011超级管理员20flypaydx */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testUploadAttach() {
		try {
			String merId = "1";
			String attachType = "2";
			String attachPath = "71f9acaaa2f842f590173ee5710a3a6e";
			String attachName = "71f9acaaa2f842f590173ee5710a3a6e";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("attachType", attachType);
			json.put("attachPath", attachPath);
			json.put("attachName", attachName);
			json.put("appType", "ios");
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/uploadAttach", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testApplyAuthentication() {

		try {
			String merId = "4";
			String realName = "孙月";
			String realIdNo = "510265790128303";
			String idNo = ImportUtil.getEncIdNo(realIdNo);
			String realCardNo = "6226090000000048";
			String cardNo = ImportUtil.getEncCardNo(realCardNo);
			String openBankId = "12";// 结算银行
			String openProvId = "9999";
			String openAreaId = "700";// 城市
			String openBranchId = "1000";// 联行号
			String openBranchName = "黄埔支行";// 支行名称
			String reservedPhone = "18100000000";
			String cardType = "J";
			String chk = MD5Util.md5(merId.trim() + idNo.trim() + reservedPhone.trim() + cardNo.trim() + openBankId.trim() + "flypaysmrz");
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("idNo", idNo);
			json.put("realName", realName);
			json.put("cardNo", cardNo);
			json.put("openBankId", openBankId);
			json.put("openProvId", openProvId);
			json.put("openAreaId", openAreaId);
			json.put("openBranchId", openBranchId);
			json.put("openBranchName", openBranchName);
			json.put("reservedPhone", reservedPhone);

			json.put("cardType", cardType);
			json.put("chkValue", chk);
			json.put("appType", "ios");
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/applyAuthentication", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testUpdateSettlementBankCard() {

		try {
			String merId = "1";
			String realIdNo = "510265790128303";
			String idNo = ImportUtil.getEncIdNo(realIdNo);
			String realCardNo = "6226090000000048";
			String cardNo = ImportUtil.getEncCardNo(realCardNo);
			String openBankId = "79";// 结算银行
			String openProvId = "9999";
			String openAreaId = "700";// 城市
			String openBranchId = "1000";// 联行号
			String openBranchName = "黄埔支行";// 支行名称
			String reservedPhone = "18100000000";
			String cardType = "J";
			String chk = MD5Util.md5(merId.trim() + reservedPhone.trim() + cardNo.trim() + openBankId.trim() + "flypayjskgx");
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("idNo", idNo);
			json.put("cardNo", cardNo);
			json.put("openBankId", openBankId);
			json.put("openProvId", openProvId);
			json.put("openAreaId", openAreaId);
			json.put("openBranchId", openBranchId);
			json.put("openBranchName", openBranchName);
			json.put("reservedPhone", reservedPhone);

			json.put("cardType", cardType);
			json.put("chkValue", chk);
			json.put("appType", "ios");
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/updateSettlementBankCard", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testAddBankCard() {

		try {
			String merId = "1";
			String realName = "孙月";
			String realIdNo = "370322198703213112";
			String idNo = ImportUtil.getEncIdNo(realIdNo);
			String realCardNo = "60138208099010297";
			String cardNo = ImportUtil.getEncCardNo(realCardNo);
			String openBankId = "12";// 结算银行
			String reservedPhone = "13817117644";
			String cardType = "J";
			String chk = MD5Util.md5(merId.trim() + cardNo.trim() + openBankId.trim() + reservedPhone.trim() + cardType.trim() + "flypaytjyhk");
			System.out.println(merId.trim() + idNo.trim() + reservedPhone.trim() + cardNo.trim() + openBankId.trim() + "flypayjskgx");
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("idNo", idNo);
			json.put("realName", realName);
			json.put("cardNo", cardNo);
			json.put("openBankId", openBankId);
			json.put("reservedPhone", reservedPhone);

			json.put("cardType", cardType);
			json.put("chkValue", chk);
			json.put("appType", "ios");
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/addBankCard", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testQueryMerTransCard() {

		try {

			String merId = "4";
			String isMyself = "Y";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("appType", "ios");
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/queryMerTransCard", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testUpdateLoginPwd() {

		try {
			String agentId = "FFF001";
			String smscode = "907985";
			String msgCode = "71f9acaaa2f842f590173ee5710a3a6e";

			String phone = "admin";
			String oldPwd = MD5Util.md5("admin");
			String newPwd = MD5Util.md5("admin1");
			JSONObject json = new JSONObject();
			json.put("agentId", agentId);
			json.put("smsCode", smscode);
			json.put("msgCode", msgCode);
			json.put("phone", phone);
			json.put("oldPwd", oldPwd);
			json.put("newPwd", newPwd);
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/updateLoginPwd", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testcreateTransPwd() {

		try {
			String agentId = "4";

			String phone = "admin";
			String newPwd = MD5Util.md5("admin1");
			JSONObject json = new JSONObject();
			json.put("merId", agentId);
			json.put("phone", phone);
			json.put("newPwd", newPwd);
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/createTransPwd", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testqueryMerFeeInfo() {

		try {
			String merId = "4";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/queryMerFeeInfo", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testqueryMerBal() {

		try {
			String merId = "4";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("acctType", "RATE");
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/queryMerBal", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testsearchOrderStatus() {
		try {
			String merId = "4";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("agentId", "F20160001");
			json.put("fullOrderNum", "ALQR201701061508214070000000004");
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/searchOrderStatus", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testcreateWxQrPay() {
		try {
			String merId = "4";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("transAmt", "1");
			json.put("transPayType", "10");
			json.put("transType", "ALQR");
			json.put("appType", "ios");
			json.put("accType", "0");
			String t = httpJsonRequest(remoteUrl + "/createWxQrPay", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testcreateYLZXPay() {
		try {
			String merId = "4";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("transAmt", "0.01");
			json.put("frontUrl", "http://flypay.ngrok.sapronlee.com/flypayfx/payment/ylNotify");
			// json.put("cardId","cardId");
			json.put("cardType", "J");
			json.put("cardNo", "4392260033229160");
			json.put("openBankId", "80");
			json.put("reservedPhone", "13817117644");
			json.put("appType", "ios");
			json.put("accType", "0");
			json.put("transPayType", "10");
			// json.put("cvv","cvv");

			String t = httpJsonRequest(remoteUrl + "/createOnlineBankPay", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testqueryOrderList() {
		try {
			String merId = "4";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("transStat", "S");
			json.put("orderType", "700|710");
			json.put("pageNum", "0");
			json.put("startDate", DateUtil.getDateTime("yyyyMMddHHmm", (DateUtil.getBeforeDate(new Date(), 30))));
			json.put("endDate", DateUtil.getDateTime("yyyyMMddHHmm", new Date()));
			json.put("pageSize", "5");
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/queryOrderList", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testsearchRebateMerInfo() {
		try {
			String merId = "4";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("appType", "ios");

			String t = httpJsonRequest(remoteUrl + "/searchRebateMerInfo", "POST", json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testqueryRebateList() {
		try {
			String merId = "4";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("type", "T");
			json.put("pageNum", "1");
			json.put("pageSize", "1");
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/queryRebateList", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testqueryAgentList() {
		try {
			String merId = "1";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("type", "A");
			json.put("pageNum", "1");
			json.put("pageSize", "1");
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/queryAgentList", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testqueryRebateOutList() {
		try {
			String merId = "1";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("pageNum", "1");
			json.put("pageSize", "1");
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/queryRebateOutList", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testappVersion() {
		try {
			JSONObject json = new JSONObject();
			json.put("agentId", "FFF001");
			json.put("appType", "ios");
			json.put("versionId", "1");

			String t = httpJsonRequest(remoteUrl + "/appVersion", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testuploadFaceImg() {
		try {
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("attachPath", "d:/test");
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/uploadFaceImg", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testdoTrfToMer() {
		try {// TODO
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("transAmt", "10");
			json.put("transPwd", "21232f297a57a5a743894a0e4a801fc3");
			json.put("trfTitle", "ios");
			json.put("phone", "13817117644");

			String t = httpJsonRequest(remoteUrl + "/doTrfToMer", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testpayAgentByBill() {
		try {// TODO
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("transAmt", "1000");
			json.put("transPwd", "e10adc3949ba59abbe56e057f20f883e");
			json.put("transType", "ios");

			String t = httpJsonRequest(remoteUrl + "/payAgentByBill", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testvalidateBankCode() {
		try {// TODO
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("bankCode", ImportUtil.getEncCardNo("4392260033229160"));
			json.put("phone", "13817117644");

			String t = httpJsonRequest(remoteUrl + "/validateBankCode", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testdoLiq() {
		try {
			JSONObject json = new JSONObject();
			json.put("merId", "4");
			json.put("transType", "T0");
			json.put("transAmt", "10");
			json.put("transPwd", "e10adc3949ba59abbe56e057f20f883e");

			String t = httpJsonRequest(remoteUrl + "/doLiq", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testdoRebateOut() {
		try {
			JSONObject json = new JSONObject();
			json.put("merId", "4");
			json.put("transType", "T0");
			json.put("amt", "10");
			json.put("transPwd", "e10adc3949ba59abbe56e057f20f883e");

			String t = httpJsonRequest(remoteUrl + "/doRebateOut", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testqueryTgCode() {
		try {
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("agentId", "FFF001");
			;
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/queryTgCode", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testaddFeedback() {
		try {
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("msgCon", "test");
			;
			json.put("appType", "ios");
			String t = httpJsonRequest(remoteUrl + "/addFeedback", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testqueryBankList() {
		try {
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("bankType", "1");

			String t = httpJsonRequest(remoteUrl + "/queryBankList", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用APP服务端的client服务
	 * 
	 * @param requestUrl
	 *            请求的URL
	 * @param requestMethod
	 *            请求的类型POST/GET
	 * @param outputStr
	 *            请求的数据（JSON）
	 * @return
	 * @throws Exception
	 */
	private static String httpJsonRequest(String requestUrl, String requestMethod, String outputStr) throws Exception {
		String jsonObject = null;
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			conn.setRequestProperty("Content-Length", String.valueOf(outputStr.length()));
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(30000);
			conn.setRequestProperty("token", token);
			conn.setRequestMethod(requestMethod);
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				// if (str.startsWith("{")) {
				buffer.append(str);
				// }
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			String bs = buffer.toString();
			if (bs.startsWith("{\"flypayContent\"")) {
				log.info(URLDecoder.decode(MsgInfoDESUtil.DecryptDoNet(bs.substring("{\"flypayContent\":\"".length(), bs.lastIndexOf("\"}"))), "utf-8"));
			} else {
				log.info("request:" + buffer.toString());
			}
			jsonObject = buffer.toString();
			// XmlMapper.map2Xml(m);
			// System.out.println(m);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		return jsonObject;
	}

	public static String upload(File f) {
		try {
			// 服务器IP(这里是从属性文件中读取出来的)
			URL url = new URL(remoteUrl + "/uploadImage");
			String ul = remoteUrl + "/uploadImage";
			String filePath = "d:/08.png";

			RestTemplate rest = new RestTemplate();
			FileSystemResource resource = new FileSystemResource(new File(filePath));
			MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
			param.add("jarFile", resource);
			param.add("merId", "1");

			String string = rest.postForObject(ul, param, String.class);
			System.out.println(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void testApplyManualAuthentication() {

		try {
			String merId = "1";
			String realName = "孙月";
			String realIdNo = "370322198703213112";
			String idNo = ImportUtil.getEncIdNo(realIdNo);
			String realCardNo = "601382080010197";
			String cardNo = ImportUtil.getEncCardNo(realCardNo);
			String openBankId = "1234";// 结算银行
			String openProvId = "9999";
			String openAreaId = "700";// 城市
			String openBranchId = "1000";// 联行号
			String openBranchName = "黄埔支行";// 支行名称
			String reservedPhone = "13817117644";
			String cardType = "J";
			String chk = MD5Util.md5(merId.trim() + idNo.trim() + "flypayrgrz");
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("idNo", idNo);
			json.put("realName", realName);
			json.put("cardNo", cardNo);
			json.put("openBankId", openBankId);
			json.put("openProvId", openProvId);
			json.put("openAreaId", openAreaId);
			json.put("openBranchId", openBranchId);
			json.put("openBranchName", openBranchName);
			json.put("reservedPhone", reservedPhone);

			json.put("frontIDPath", "20160814c2ad838008c7426294b0151072a6339a");
			json.put("backIDPath", "20160814c2ad838008c7426294b0151072a6339a");
			json.put("handIDPath", "20160814c2ad838008c7426294b0151072a6339a");

			json.put("cardType", cardType);
			json.put("chkValue", chk);
			json.put("appType", "ios");
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/applyManualAuthentication", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testqueryAuthFailureReason() {

		try {
			JSONObject json = new JSONObject();
			json.put("merId", "4");
			json.put("appType", "ios");

			String t = httpJsonRequest(remoteUrl + "/queryAuthFailureReason", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void testupdateSltBankCard() {

		try {
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("cardId", "1");
			json.put("appType", "ios");

			String t = httpJsonRequest(remoteUrl + "/updateSltBankCard", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void testqueryInfoList() {

		try {
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("cardId", "1");
			json.put("msgType", "N");
			json.put("pageNum", "1");
			json.put("pageSize", "10");
			json.put("appType", "ios");

			String t = httpJsonRequest(remoteUrl + "/queryInfoList", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void testgetCollectionCode() {

		try {
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("appType", "ios");

			String t = httpJsonRequest(remoteUrl + "/getCollectionCode", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void testaddBusiness() {

		try {
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("contactor", "1");
			json.put("contactPhone", "1");
			json.put("busType", "1");
			json.put("companyNet", "1");
			json.put("busDesc", "1");
			json.put("appType", "ios");

			String t = httpJsonRequest(remoteUrl + "/addBusiness", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void testdeleteBankCard() {

		try {
			JSONObject json = new JSONObject();
			json.put("merId", "1");
			json.put("cardId", "1");
			String t = httpJsonRequest(remoteUrl + "/deleteBankCard", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void testqueryChannelLimitList() {

		try {
			JSONObject json = new JSONObject();
			json.put("merId", "4");
			json.put("agentId", "F20160001");
			String t = httpJsonRequest(remoteUrl + "/queryChannelLimitList", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void testqueryUserPoint() {

		try {
			JSONObject json = new JSONObject();
			json.put("merId", "4");
			json.put("agentId", "F20160001");
			json.put("appType", "ios");

			String t = httpJsonRequest(remoteUrl + "/queryUserPoint", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void testconsumeUserPointByReduceChlRate() {

		try {
			JSONObject json = new JSONObject();
			json.put("merId", "5");
			json.put("agentId", "F20160001");
			json.put("appType", "ios");
			json.put("chlType", "300");
			json.put("consumePoint", "20");
			json.put("accountType", "0");
			json.put("type", "3");
			json.put("transPwd", MD5Util.md5("123456"));
			json.put(
					"chkValue",
					MD5Util.md5(json.getString("merId") + json.getString("agentId") + json.getString("chlType") + json.getString("consumePoint") + json.getString("type") + json.getString("transPwd")
							+ "flypayjfxf"));

			String t = httpJsonRequest(remoteUrl + "/consumeUserPointByReduceChlRate", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void testapplyManualMerchantAuthentication() {
		try {
			String merId = "4";
			String agentId = "F20160001";
			String merchantName = "小小乐乐";
			String address = "上海浦东";
			JSONObject json = new JSONObject();
			json.put("merId", merId);
			json.put("agentId", agentId);
			json.put("merchantName", merchantName);
			json.put("address", address);

			json.put("frontIDPath", "20160814c2ad838008c7426294b0151072a6339a");
			json.put("backIDPath", "20160814c2ad838008c7426294b0151072a6339a");
			json.put("handInCashierDeskPath", "20160814c2ad838008c7426294b0151072a6339a");

			json.put("licensePath", "20160814c2ad838008c7426294b0151072a6339a");
			json.put("interiorView1Path", "20160814c2ad838008c7426294b0151072a6339a");
			json.put("interiorView2Path", "20160814c2ad838008c7426294b0151072a6339a");
			json.put("interiorView3Path", "20160814c2ad838008c7426294b0151072a6339a");
			json.put("shopPath", "20160814c2ad838008c7426294b0151072a6339a");
			json.put("appType", "ios");
			System.out.println(json);
			String t = httpJsonRequest(remoteUrl + "/applyManualMerchantAuthentication", "POST", json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
