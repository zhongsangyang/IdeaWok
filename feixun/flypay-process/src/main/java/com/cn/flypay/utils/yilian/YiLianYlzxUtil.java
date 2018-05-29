package com.cn.flypay.utils.yilian;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rd.model.MerchantDFQuickPayReq;
import com.rd.model.MerchantOrderPayReq;
import com.rd.model.MerchantPayQryReq;
import com.rd.model.MerchantQuickPayConfirmReq;
import com.rd.util.HttpClientUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rd.constant.ValueConstant;
import com.rd.model.MerchantBaseMsg;
import com.rd.util.MerchantUtil;
/**
 * 易联通道基础类
 * @author liangchao
 *
 */
public class YiLianYlzxUtil {
	private static Log logger = LogFactory.getLog(YiLianYlzxUtil.class);

	/*生产配置 start */
	
	public static String merachnetId = "0000000000000133";
	//加签密钥(商户RSA私钥)
	public static String merchantPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANA96QzO0Nc++Gnqus7L3fNqANa1Oq5/8rj2LL9WdXhkgaU8Ef2Sva18Xy8f7DJbXcAIcdy/aFY/QykPp2fn4hVc2TIpELTNba0aAtrJo4e1CLUW5FDT7bPhE0zAZT+mnsYRSowarPu/ItRoCZlUfIp+xEfvu+XniMpB4GxkhN7nAgMBAAECgYBGa0kt7Zotz7uNhbUaTVKHB+e0s6BediIMbiL/qSscRVCaIdArob3GtvIxK2hiNwdi8vgeIBQWZlo9vm9r8bw/xHRxXxmqrezt4eMeAMAqPtNd0ZnhmwAjBRKVGh7aSvt/ywMi8UoQis/G+L96tCtetzQLrIol8w/Qim2V01TlgQJBAPphzOVCP4VPFG21gTfIPByx69dQHCWh2rSjWeK5D4H1lg/wo4slpUTu6H+iWTQtT9GYIDQ71+Ru+js0xM1zmiECQQDU6g5J8JVOgBdzY42BhWlREmsGVYmec2xBANK42PqviHcb9GVm+NHbtxnCq+rzcYDVG6xD7FS9Ap9G2R4edqgHAkEAhRb/Zs20HhRO85ILQa955HbeufCnRYmnS2WZHd7o01fLkGWFSS2Vj/K+Ozn82LNNPkfytlEpDQ/Ai0AdbGgKgQJAEdUcp0K4Lp/51tdCtdNns7uYLSqi4RuHuJYKECNUM7l5SGUazRoP8ZgPV0ew4PzjrnWn4vR7UG1Uy8lJhYGopwJANBwedcyQKZst7z72+Th+dqOd/UyZcZ9KHURyWnC3yqB88T8JCJ7QRuEgDEeskqcIi2v/dKnDJEbXvFIhhFr8zA==";
	//加密密钥
	public static String aesKey = "5ghmrFVLOVNq3XdACJ9RHQ==";
	//易联请求地址
//	public static String submitUrl = "http://183.60.125.17:18533/rdPay/payProcess";	//老链接
	public static String submitUrl = "http://120.78.222.201:8532/rdPay/payProcess";
	//验签密钥(支付平台公钥)
	public static String channelPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfRRqiTyiDRvgPwAnHm+odB6kEY1O51Zh5rlr3iSYEgDKfO00yD6ZCAh6MlKfYT0DD+WKN91lt6t9g/u0Cw2WJwGeUiOEWUDso/MiOGmdGYrfsarEzGCTSRmu1tIdwFKNi9HThcMTs7aU99lBtoGIYu2mxsXoWnLbdExZ9TaOBgwIDAQAB";
	
	/*生产配置 end */
	
	
	
	/*测试配置  start*/
/*	public static String merachnetId = "0000000000000147";
	//加签密钥(商户RSA私钥)
	public static String merchantPrivateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOTYIqkjyCrIHdIeOAvTwaggG6mAhXU6byrW5SIqAXE3znaiBeOeDVNWJzs/pQtXuTn6fB1LoU3Q93hPcLkh7kdoH3+BJDzoPWZ5tPyzgua2nad9xMNNphfRYDVTiEoAxOnFc3aNI22gse+wPS0Ll29/LGp+z3e/p+e1cRP/ibFJAgMBAAECgYEA3pVbISisiPAcEUNTQC23LtAMF9Hp/RvZBNIADDrPLFAbgUgWck5Ip8YkYnyFC4NHphz8m4H0Yrvd+CdMfMWD/BkPRf3eafhnJlHGKyGqsAXLmGh/mvJbleE3NH9LS1N/0+pPam58mAjvkujxoPQ0v5BxHyS7r14lBMkvxiXN9AECQQD8B2zTpvsXDWJFwjKYmKRkWCs3JOaOJmWX6MTY3qPSE6mFW/93blDAs1kEioB01ZsbKiE3fIubZVcFEzI90nCXAkEA6HMxd+GYWA7+UdeOklhz/XhBdtlsOeHZDG8glOFhsHJguURcnov2TG4G5L1t+qdnpZzTeNKVrSyT2ECE4gVJHwJAVwiZZF39x/AvR7fQkTHlU2G/SsPLert3ygXwNJRuLlXr7MngZvYJnQJSc2cBBVfewHrEDc1MyNUuP+ppJ0BM8QJBALdi6gwiNwaCDbKT1S8wCZJXZY5WSkQAIjTlF1dd2KxUEGsZu9h5o3747wdXS4UMvYCzEUOpH9zX5mwdurh2YxECQQDuPsVpoJlevwbIuRymGzvYvVZvDP2N+O4rN0lrJnlhTXkYdsRLSw92QcBX0jRqjwl/LwEMPt8EaK25xJ6rEc07";
	//加密密钥
	public static String aesKey = "xbDy7BIi4rgG+Bp0m4JpQA==";
	//易联请求地址
//	public static String submitUrl = "http://183.60.125.17:18532/rdPay/payProcess";		//老测试连接
	public static String submitUrl = "http://120.78.196.14:8532/rdPay/payProcess";		//新测试链接
	//验签密钥(支付平台公钥)
	public static String channelPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfRRqiTyiDRvgPwAnHm+odB6kEY1O51Zh5rlr3iSYEgDKfO00yD6ZCAh6MlKfYT0DD+WKN91lt6t9g/u0Cw2WJwGeUiOEWUDso/MiOGmdGYrfsarEzGCTSRmu1tIdwFKNi9HThcMTs7aU99lBtoGIYu2mxsXoWnLbdExZ9TaOBgwIDAQAB";
	*/
	
	/*测试配置  end */
	
	
	
	// public static String merchantPrivateKey =
	// "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOTYIqkjyCrIHdIeOAvTwaggG6mAhXU6byrW5SIqAXE3znaiBeOeDVNWJzs/pQtXuTn6fB1LoU3Q93hPcLkh7kdoH3+BJDzoPWZ5tPyzgua2nad9xMNNphfRYDVTiEoAxOnFc3aNI22gse+wPS0Ll29/LGp+z3e/p+e1cRP/ibFJAgMBAAECgYEA3pVbISisiPAcEUNTQC23LtAMF9Hp/RvZBNIADDrPLFAbgUgWck5Ip8YkYnyFC4NHphz8m4H0Yrvd+CdMfMWD/BkPRf3eafhnJlHGKyGqsAXLmGh/mvJbleE3NH9LS1N/0+pPam58mAjvkujxoPQ0v5BxHyS7r14lBMkvxiXN9AECQQD8B2zTpvsXDWJFwjKYmKRkWCs3JOaOJmWX6MTY3qPSE6mFW/93blDAs1kEioB01ZsbKiE3fIubZVcFEzI90nCXAkEA6HMxd+GYWA7+UdeOklhz/XhBdtlsOeHZDG8glOFhsHJguURcnov2TG4G5L1t+qdnpZzTeNKVrSyT2ECE4gVJHwJAVwiZZF39x/AvR7fQkTHlU2G/SsPLert3ygXwNJRuLlXr7MngZvYJnQJSc2cBBVfewHrEDc1MyNUuP+ppJ0BM8QJBALdi6gwiNwaCDbKT1S8wCZJXZY5WSkQAIjTlF1dd2KxUEGsZu9h5o3747wdXS4UMvYCzEUOpH9zX5mwdurh2YxECQQDuPsVpoJlevwbIuRymGzvYvVZvDP2N+O4rN0lrJnlhTXkYdsRLSw92QcBX0jRqjwl/LwEMPt8EaK25xJ6rEc07";
	// public static String aesKey = "xbDy7BIi4rgG+Bp0m4JpQA==";
	// public static String submitUrl =
	// "http://183.60.125.17:18532/rdPay/payProcess";
	// public static String channelPublicKey =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfRRqiTyiDRvgPwAnHm+odB6kEY1O51Zh5rlr3iSYEgDKfO00yD6ZCAh6MlKfYT0DD+WKN91lt6t9g/u0Cw2WJwGeUiOEWUDso/MiOGmdGYrfsarEzGCTSRmu1tIdwFKNi9HThcMTs7aU99lBtoGIYu2mxsXoWnLbdExZ9TaOBgwIDAQAB";

	public static void main(String[] args) throws Exception {
		// MerchantOrderPayReq req = new MerchantOrderPayReq();
		// req.setTransactionId("YF"+new
		// SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		// req.setOrderAmount("98.78");
		// req.setBgUrl("http://www.baiddd.com");
		// req.setBuyerIp("192.168.1.14");
		// req.setPayType(ValueConstant.PAY_TYPE_1005);
		// req.setFeeRate("0.04");
		// req.setFixFee("100");
		// req.setPayeeBankName("工商银行");
		// req.setPayeeAcc("6216261000000000018");
		// req.setPayerAcc("6216261000000000018");
		// req.setPayeeIdNum("341126197709218366");
		// req.setPayeeName("全渠道");
		// YiLianYlzxUtil.send(req, ValueConstant.TRANS_CODE_T01014,
		// "0000000000000133");

		// MerchantPayQryReq req = new MerchantPayQryReq();
		// req.setTransactionId("YFYLZXCX"+new
		// SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		// req.setRefTxnId("YF20170704110144");
		// YiLianYlzxUtil.send(req, ValueConstant.TRANS_CODE_Q01001,
		// "0000000000000133");

		MerchantDFQuickPayReq req = new MerchantDFQuickPayReq();
		req.setTransactionId("FF" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		req.setOrderAmount("1");
		req.setCur("CNY");
		req.setProductName("小米MIX");
		req.setProductNum("1");
		req.setProductDesc("小米MIX");
		req.setOrderDesc("小米MIX");
		req.setBgUrl("http://1k7387k300.iask.in:11688/flypayfx/payment/yfXe_ylzx_Notify");
		req.setBuyerIp("127.0.0.1");
		req.setPayType("1006");
		req.setPayerBankCode("CMBC");// 银行编号
		req.setPayerAcc("4218717016411786");// 付款银行卡号
		req.setPayerName("芦强");// 付款姓名
		req.setPayerPhoneNo("13816111195");
		req.setCardType("CC");// 借记：DC；贷记：CC
		req.setExpiryDate("2105");
		req.setCvv2("805");
		req.setPayerIdNum("152822199012293814");// 身份证
		req.setPrivateFlag("B");// 对公：B；对私：C
		req.setPayeeBankCode("CMBC");// 收款方银行编码
		req.setPayeeAcc("6216910206201383");// 收款方银行卡号
		req.setPayeePhoneNo("13816111195");
		req.setFeeRate("90");
		YiLianYlzxUtil.send(req, ValueConstant.TRANS_CODE_T01015, "0000000000000147");// 0000000000000147

		MerchantQuickPayConfirmReq confirmReq = new MerchantQuickPayConfirmReq();
		confirmReq.setRefTxnId(req.getTransactionId());
		confirmReq.setVerificationCode("568261");
		confirmReq.setTransactionId("FF" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		YiLianYlzxUtil.send(confirmReq, ValueConstant.TRANS_CODE_T01016, "0000000000000147");

	}

	/**
	 * 发送请求
	 * @param req  	
	 * @param transCode	交易类型code，详见易联提供的文档
	 * @param merId	商户号，平台分配的唯一商户编号
	 * @return
	 * @throws Exception
	 */
	public static JSONObject send(Object req, String transCode, String merId) throws Exception {
		// 1.签名
		String jsonStr = JSON.toJSONString(req);
		String signData = MerchantUtil.sign(jsonStr, merchantPrivateKey, MerchantUtil.SIGNTYPE_RSA, "UTF-8");
		
		logger.info("-----易联通道----加密前的报文" + jsonStr);
		
		// 2.加密
		String businessContext = MerchantUtil.encryptDataByAES(signData, aesKey, "UTF-8");
		// logger.info("密文："+businessContext);
		
		// 3.组装报文
		MerchantBaseMsg baseReq = new MerchantBaseMsg();
		baseReq.setVersion("1.0");
		baseReq.setMerId(merId);	//商户号   易联为平台分配的唯一商户编号
		baseReq.setTransCode(transCode);	//交易类型
		baseReq.setOrderTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));	
		baseReq.setSignType("RSA");
		baseReq.setCharse("UTF-8");
		baseReq.setBusinessContext(businessContext);
		
		
		// 4.发送报文
		@SuppressWarnings("unchecked") 
		Map<String, String> requestParams = JSON.parseObject(JSON.toJSONString(baseReq), Map.class);
		logger.info("-----易联通道----发送信息:" + requestParams.toString());
		String rspStr = HttpClientUtil.doPost(requestParams, submitUrl);
		MerchantBaseMsg baseMsg = JSON.parseObject(rspStr, MerchantBaseMsg.class);
		// 5.解密
		businessContext = baseMsg.getBusinessContext();
		// logger.info("应答密文："+businessContext);
		String rspMsg = MerchantUtil.decryptDataByAES(businessContext, aesKey, "UTF-8");
		// 6.验签
		boolean isTrue = MerchantUtil.verify(rspMsg, channelPublicKey, MerchantUtil.SIGNTYPE_RSA, "UTF-8");

		if (!isTrue) {
			rspMsg = "{'retCode':'RC0013','retRemark':'验签失败'}";
			logger.info("-----易联通道----验签失败---");
		}
		logger.info("-----易联通道----响应体---" + rspMsg);
		return JSONObject.parseObject(rspMsg);
	}

}
