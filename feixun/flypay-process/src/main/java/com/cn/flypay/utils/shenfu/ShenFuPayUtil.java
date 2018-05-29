package com.cn.flypay.utils.shenfu;

import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.AESCodeUtil;
import org.apache.commons.codec.binary.Base64;

public class ShenFuPayUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ShenFuPayUtil.class);

	public static String sendPayShenFu(Map<String, String> map) throws Exception {
		String sign = RSAUtil.sign(ApplicationBase.coverMap2String(map).getBytes("UTF-8"), ApplicationBase.PRIVATEKEY);
		map.put("signature", sign);
		LOG.info("ShenFuPay URL={}", ApplicationBase.CONSUMEURL);
		String result = HttpClientUtil.doPost(ApplicationBase.CONSUMEURL, map, "UTF-8");
		String encryptHtml = AESCodeUtil.encrypt(result);
		// LOG.info("sendPayShenFu encryptHtml={}", encryptHtml);
		return encryptHtml;
	}

	public static String sendPayShenFuV2(Map<String, String> map, String mercPrivateKey) throws Exception {
		String sign = RSAUtil.sign(ApplicationBase.coverMap2String(map).getBytes("UTF-8"), mercPrivateKey);
		map.put("signature", sign);
		LOG.info("ShenFuPayV2 URL={}", ApplicationBase.CONSUMEURL);
		String result = HttpClientUtil.doPost(ApplicationBase.CONSUMEURL, map, "UTF-8");
		String encryptHtml = AESCodeUtil.encrypt(result);
		LOG.info("sendPayShenFu encryptHtml={}", encryptHtml);
		return encryptHtml;
	}

	public static String authCard(Map<String, String> map, String mercPrivateKey) throws Exception {
		String valueChain = ApplicationBase.coverTreeMap2String(map);
		String sign = RSAUtil.sign(valueChain.getBytes("UTF-8"), mercPrivateKey);
		LOG.info("valueChain={},privateKey={},sign={}", valueChain, mercPrivateKey, sign);
		map.put("signature", sign);
		String result = HttpClientUtil.doPost(ApplicationBase.SHENFU_AUTHCARD_URL, map, "UTF-8");
		LOG.info("authCard result={}", result);
		return result;
	}

	public static String addCreditCard(Map<String, String> map, String mercPrivateKey) throws Exception {
		String sign = RSAUtil.sign(ApplicationBase.coverTreeMap2String(map).getBytes("UTF-8"), mercPrivateKey);
		map.put("signature", sign);
		String result = HttpClientUtil.doPost(ApplicationBase.SHENFU_ADDCREDIT_URL, map, "UTF-8");
		LOG.info("addCreditCard result={}", result);
		result = new String(Base64.encodeBase64(result.getBytes()), "UTF-8");
		result = URLEncoder.encode(result, "UTF-8");
		LOG.info("addCreditCard encode result2={}", result);
		return result;
	}

	/**
	 * 向申孚校验并报备卡
	 */
	public static String verifyAndReportCreditCard(Map<String, String> map, String mercPrivateKey) throws Exception {
		String sign = RSAUtil.sign(ApplicationBase.coverTreeMap2String(map).getBytes("UTF-8"), mercPrivateKey);
		LOG.info("SHNEFU params out mercId={},accNo={},certNo={},name={},sign={},privateKey={}", new Object[] { map.get("mercId"), map.get("accNo"), map.get("certNo"), map.get("name"), sign, mercPrivateKey });
		map.put("signature", sign);
		String result = HttpClientUtil.doPost(ApplicationBase.SHENFU_VERIFYCARD_URL, map, "UTF-8");
		LOG.info("PayShenFu VerifyAndReportCreditCard result={}", result);
		return result;
	}

	/**
	 * 向申孚更新结算信息
	 */
	public static String updateBankAccount(Map<String, String> map, String mercPrivateKey) throws Exception {
		String sign = RSAUtil.sign(ApplicationBase.coverTreeMap2String(map).getBytes("UTF-8"), mercPrivateKey);
		LOG.info("SHNEFU params updateBankAccount mercId={},merctel={},actname={},openbank={}, actno={},   rcvbanksettleno={},  feerat={},  crefeerat={}, settelfee={},d0feerat={},d0channelfee={},                   sign={},privateKey={}",
				new Object[] { map.get("mercId"), map.get("merctel"), map.get("actname"), map.get("openbank"), map.get("actno"), map.get("rcvbanksettleno"), map.get("feerat"), map.get("crefeerat"), map.get("settelfee"), map.get("d0feerat"),
						map.get("d0channelfee"), sign, mercPrivateKey });
		map.put("signature", sign);
		String result = HttpClientUtil.doPost(ApplicationBase.SHENFU_UPDATESETTLE_URL, map, "UTF-8");
		LOG.info("PayShenFu VerifyAndReportCreditCard result={}", result);
		return result;
	}

	public static String sendPayShenFuD0(Map<String, String> map, String mercPrivateKey) throws Exception {
		String sign = RSAUtil.sign(ApplicationBase.coverTreeMap2String(map).getBytes(), mercPrivateKey);
		map.put("signature", sign);
		String result = HttpClientUtil.doPost(ApplicationBase.CONSUMED0_URL, map, "UTF-8");
		String encryptHtml = AESCodeUtil.encrypt(result);
		// LOG.info("sendPayShenFuD0 encryptHtml={}", encryptHtml);
		return encryptHtml;
	}

	public static String registerShenFuMer(Map<String, Object> data) throws Exception {
		ShenFuHttpSender httpSender = new ShenFuHttpSender();
		return httpSender.mercRegister(data);
	}

	/**
	 * 商户支付功能配置
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String mercRateconf(Map<String, Object> data) throws Exception {
		ShenFuHttpSender httpSender = new ShenFuHttpSender();
		return httpSender.httpPost(ApplicationBase.SHENFU_MercRateconf_ACTION, data);
	}

	public static String payShenFuD0(Map<String, String> data, String mercPrivateKey) throws Exception {
		String sign = RSAUtil.sign(ApplicationBase.coverTreeMap2String(data).getBytes("UTF-8"), mercPrivateKey);
		data.put("signature", sign);
		ShenFuHttpSender httpSender = new ShenFuHttpSender();
		return httpSender.shenfuD0(data);
	}

	public static String shenFuDaiFu(Map<String, String> map, String mercPrivateKey) throws Exception {
		String sign = RSAUtil.sign(ApplicationBase.coverTreeMap2String(map).getBytes("UTF-8"), mercPrivateKey);
		map.put("signature", sign);
		String result = HttpClientUtil.doPost(ApplicationBase.SHENFU_DAIFU_URL, map, "GBK");
		// String encryptHtml = AESCodeUtil.encrypt(result);
		LOG.info("shenFuDaiFu result={}", result);
		return result;
	}

	public static JSONObject searchOrderToShenfu(Map<String, String> data, String mercPrivateKey) throws Exception {
		String sign = RSAUtil.sign(ApplicationBase.coverTreeMap2String(data).getBytes("UTF-8"), mercPrivateKey);
		data.put("signature", sign);
		String result = HttpClientUtil.doPost(ApplicationBase.SHENFU_SEARCHORDER_URL, data, "UTF-8");
		System.out.println("searchOrder=" + result);
		return JSONObject.parseObject(result);
	}

	public static void main(String[] args) throws Exception {
		// TODO searchOrder
		Map<String, String> map = new TreeMap<String, String>();
		map.put("mercId", "486000000395517");
		map.put("orderNo", "SFZTCYLZX201711230956076710000103691");
		String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJT/Rrw3qi9Z5PxvJ6DeHIW/Sv2ZX97z8Eq3TPk0yH/RZ65qBVjRumHbAGQceyo8h9XAEHH6g264dyGViJP9wAwwH/nyFngrcjO9gRYR09YJLRgutIbF7zHRh3eWEdKF6io6ZRRerOflvKQYD1GiuYtFtt47a30alFDW1lvC2bo9AgMBAAECgYEAhEs9NTV/qFd7GCBh4VYVoTO+k/mwxDd4Lf8fG06gj80Q8C423Swq/2QCruETAueH/GRytaEnPhIPxQQOpshx0Xqojbz7d5iyO27r0fR/yJ8eEaL8p/qbIKn1frLCe1VGFyc7NEJUH8trQDSMZrZnVzWfvC/tXhWKcqArO7StbIECQQD7UuLyGPS49bG+0TcqrRQPpkGordUvS1LwFG5nlFB7R8OIHzBOD7klnZwwQqKupdAPi6lYL1FAjKu02KQTOrKtAkEAl8T8BCWUmqUIyW5uMrviDRDTXnKxu4LIHQ0tsgJMagY4o0mCaNQl3+FRaUVKktd6pFw+MH15xP+cbZc2Zbun0QJAf3Ju57pED694bX2G7jtK2wzCaxmL7CnUay7RNfF+RqYDv/mg81UB3NNqKwb/nHU1Z0y5maCVIZi0UkcyHRY9GQJAFgs/i56lwfx8lT1b9jSB9eHewZYpKJIrXMTgQT3COZgju9vefDlsMuN4PrEYztFLd/oJ8mMXP7zczLIs7YPfMQJBAJ9AMc9zKzj3sEPSn9I8t83TxMNiNozxC6xe9p6OfosDMWYKwi2aNz8XmZQzQ67vNnxnAAj0Yp+fpPWS4LSXVXY=";
		JSONObject json = ShenFuPayUtil.searchOrderToShenfu(map, privateKey);
		System.out.println(json.get("retCode"));
		System.out.println(json.get("logNo"));
		System.out.println(json.get("txnType"));
		System.out.println(json.get("txnStatus"));
		System.out.println(json.get("txnAmt"));
		System.out.println(json.get("cardNo"));
		System.out.println(json.get("orderNo"));
		System.out.println(json.get("txnStatusDesc"));
		// mercId=486000000249461,accNo=4218717016411786,certNo=152822199012293814,name=芦强,privateKey=MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJfbvMeAEO4Kx76uqS7TPlh9An2GobDjYunc4HadCLkoJ/r/K8pq6Npv9TVseDhArl155aYOXJssCDHhqifuDk5WK8uI2IA1nFVdT9A/3HoB1X5HbX4TG3hPJKIqkX1UGBcYWJfzwUcybyDM251ywxvfiY/xKyKz/iFtxXZJTRBJAgMBAAECgYAWb9X1l/toYFlg2AeRLo7wng/HIiQzsXUE3qiffdYzHTbDA/6hWqhq1c3iBdPBY/UfRIpkvYx7NMdFL3Vnjum3+IzdIIpaIRNGog2Fn6aUGknNstkAPmKUJysBDRuALSkTxUDiP9P4kkSQABPSdJiF8InycYhK/zApRY3rkOUVfQJBAOBl2z1FssVvFXOmU9GELG+E4B1ypNF+iuMWq2ocRLixr2fLOWFi099NsuvSnAAEJzsxfEhq/2NPGaThyk93i68CQQCtPqMV1q2EIHYaJTmQhic2oU6lvpgF+S6gBxWMmSSaNB8igm/sOecJtV6iP9nA7IVPPir9yEsXfnOUkDQcSsmHAkABKuaCIhu1BUfySxpCi9KDXgigfpk28dadKeAIdE8zOtFz083foNkDX28f5P+kzGC74R0Jb20WwjGa9wXLdg7HAkA2SUJhPXvxwxo+5xs6pk7zV2iFl0ub05FAo/ekrvhBOcPn1Get5hb3e9XDka5yPasoQZrHG1QscHtXOjiaBPVRAkBgZRM+VtqmqDj5eU6Z+oig33l+sHEhbExdIMn84LulZ4TUyQQUCrfXtS3DW8LW6V6h+JYmN0bEFWdyEPcUvQTO

		// Map<String, String> map = new TreeMap<String, String>();
		// map.put("mercId", "486000000261274");
		// map.put("accNo", "4218717016411786");//
		// map.put("certNo", "152822199012293814");//
		// map.put("name", "芦强");//
		// String privateKey =
		// "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALkhyisPYwNcasmlBTuoMbsHc4tRfdBy2gQlNfpje/Vi4PGcK+EjXV0OhbCwpjAMMRDn7vLwVJOK31A5fQ4+qdGdFRjvlgZ82GSna0wbPP1qE/FJjsijwFKozliGCfaSVm1y8MMDe3dVnNlyBmizOHEkFCmSX8QFg/Va4SFbG9P7AgMBAAECgYEAnMx8oXnXv5n/rCfDReLPs/u8pgvwRqNv+cDWderaq1wC7z/5fiboxY8uNhd3ugwpZos6O7LHv2sIdrcqLu1t//TdZtyF1nJLUy5oItYT8yG+RpYCKFJbeewLxF0dfuWcP0XCWp0U+WCM0gzkFjy5l6cZp3bLGn61FTWX74kWQLkCQQDnNew/Z3rBzsFv409znC6SxURNgYj6NwCJ5mzBgPoToX9M3/OhdwtY5DbOhiI3ancgzyy08leKpmevHahT6983AkEAzPsjeUW53TkbZNMR1j0DCprlsVtCeSSvWuodSaJk2KUy/OL1zb864pV9Jhg+6ofw8TwqvR9FhLRxMEQ8Rz6rXQJATcNLFAAfv3Nkh9cx77ZFy8NhN5grk8xP+BFx3pscgOG+SQLBlDrPrDQFYuymMXEzY+uKI8mmO9G/6ZvV83PPfQJAcOqvgfC9EHaS9pmnK8N0V3U4rti854sj5gstkxCYSRfH3LMSAQMk5wi+ZivZDM1SFzGzwXbmjtHvwEz2f7CwKQJAUjkWYqkRAB4aZBEioBL9Cgbfg/vgRjtorkxTCWwIjDCU192RJnIyp0g3VagDPLA8219iNF9Cg7TPCDkUPx/XQw==";
		// try {
		// String response = ShenFuPayUtil.verifyAndReportCreditCard(map,
		// privateKey);
		// System.out.println(response);
		// } catch (Exception e) {
		// //Auto-generated catch block
		// e.printStackTrace();
		// }

		// TODO 卡校验
		// Map<String, String> map = new TreeMap<String, String>();
		// map.put("mercId", "486000000261679");
		// map.put("accNo", "6226890097597800");//
		// map.put("certNo", "411503198702110416");//
		// map.put("name", "李广春");//
		// String privateKey =
		// "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAPwNEMLg2iOVkRmfB2OiwWRb/pWeUW6ubGLMJlgx3O4nJToW725cr+ESvi2XO3IfoNA4lz0TDoXvbQvhtEK/7OIQy6RuHEiPrz/KjOItnMewh14ONoIpZYyhO3u21WxhMzyTcrWQkugx9YMUZdflV1bNtwdLxKje1lwU7Me5S5mRAgMBAAECgYEAtIBS0YyfCz6wI84MR6Elp85tug4fuQi2W+ZhS2WiFvb3pOsJ2loMJj367cFQ9ACMdLEiNb2w99+nkwu2wNqbMzoKSz66j/z1PffquKhTylOyMkEnOe1aK91y2NJaikxYmTyfyO66XvtiPlAKoU/+AgzbcVA2PpD7PmpPy10xLrECQQD+xqBHcutxX3zC0yvrch/BMEwFcXJ7nghnJavUFNNv7rp0ivfEFF/LkgoGOhXatKJIr4aSWzO9KC0HX0iTd4z1AkEA/UMWe9dm6BNMVQTCKPxeyrAbG9jnktVLKcF4x8hr1AVMIUntJEzuLw+9v7OboIEEdRJsBqge+wqJ5WDppKT4rQJBAM8Dj4peq0Y0o0gelAo8cebkmnC1zKL7NrKxHlw2C9Ngc+psXIPbVjcv06tZmRZbPZ232a2kI3coZOYfmvYP5t0CQH7vNAKn6ETi/zwRYXsHWj8+WqfvY8l9K+nmtTF0q06dxRVeJwQtpqdhvVmqnJKqd2Bx9kCz3Ks4EfssQo4pvRUCQFbtyEDIgQZY3iWsiBySq8/6BHF+onZh7224Po3kn0jSsX4UZmyerONxr8kXN07+RkKV5v+6g+54hqvmcW0SP8M=";
		// try {
		// String response = ShenFuPayUtil.verifyAndReportCreditCard(map,
		// privateKey);
		// System.out.println(response);
		// } catch (Exception e) {
		// // Auto-generated catch block
		// e.printStackTrace();
		// }

		// String res =
		// HttpClientUtil.doPost(ApplicationBase.SHENFU_VERIFYCARD_URL, map,
		// "utf-8");
		// System.out.println(res);

		// -------------------------
		// Map<String, Object> params = new HashMap<String, Object>();
		// File kangxi = new File("D:/flypay/shenfuimage/kangxi.jpg");
		// File timg = new File("D:/flypay/shenfuimage/timg.jpg");
		// File timg2 = new File("D:/flypay/shenfuimage/timg2.jpg");
		// File timg3 = new File("D:/flypay/shenfuimage/timg3.jpg");
		// File bomei = new File("D:/flypay/shenfuimage/bomei.jpg");
		//
		// // 新增参数
		// params.put("agentid", "AGE000000902423");
		// params.put("mercnam", "中华老字号");
		// params.put("comtype", "3");
		// params.put("address", "上海浦东新区纳贤路800号");
		// params.put("merctel", "13052595515");
		// params.put("mercemail", "guangchun.li@qq.com");
		// params.put("cityid", "021");//
		// params.put("merchantnature", "0");
		// params.put("legalperson", "李广春");
		// params.put("corporateidentity", "411503198702110416");
		// params.put("actname", "李广春");
		// params.put("openbank", "中国建设银行股份有限公司上海川沙支行");//
		// params.put("actno", "6217001210092813227");
		// params.put("rcvbanksettleno", "105290041007");//
		// params.put("feerat", "0.3");
		// params.put("crefeerat", "0.3");
		// params.put("d0feerat", "0");
		// params.put("d0channelfee", "0");
		// params.put("settelfee", "0");
		//
		// params.put("img001", kangxi);
		// params.put("img002", timg);
		// params.put("img003", timg2);
		// params.put("img004", timg3);
		// params.put("img005", bomei);
		//
		// ShenFuHttpSender httpSender = new ShenFuHttpSender();
		// String res = null;
		// try {
		// res =
		// httpSender.mercRegister("http://bp.shenfupay.com/SF/mercRegister",
		// 80, "utf-8", 100000, params,
		// ShenFuHttpSender.SEND_METHOD.POST);
		// } catch (Exception e) {
		// // Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// System.out.println("res:" + res);
	}
}
