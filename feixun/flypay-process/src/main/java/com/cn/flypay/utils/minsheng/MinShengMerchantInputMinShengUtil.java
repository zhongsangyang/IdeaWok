package com.cn.flypay.utils.minsheng;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.flypay.pageModel.payment.minsheng.CommonSMZF;
import com.cn.flypay.pageModel.payment.minsheng.SMZF001;
import com.cn.flypay.pageModel.payment.minsheng.SMZF006;
import com.cn.flypay.pageModel.payment.minsheng.SMZF007;
import com.cn.flypay.pageModel.payment.minsheng.SMZF021;
import com.cn.flypay.pageModel.payment.minsheng.SMZF022;
import com.cn.flypay.pageModel.payment.minsheng.SMZF024;
import com.cn.flypay.pageModel.payment.minsheng.SMZF030;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.channel.HttpClient4Util;

public class MinShengMerchantInputMinShengUtil {

	final static PublicKey yhPubKey = CryptoUtil.getRSAPublicKeyByFileSuffix("c:/private/AES/ms_rsa_public_key_2048.pem", "pem", "RSA");// 生产
	
	//final static PublicKey yhPubKey = CryptoUtil.getRSAPublicKeyByFileSuffix("c:/private/AES/ms_test_rsa_public_key_2048.pem", "pem", "RSA");//测试

	static PrivateKey hzfPriKey = CryptoUtil.getRSAPrivateKeyByFileSuffix("c:/private/AES/rsa_private_key_2048.pem", "pkcs8", null, "RSA");// 生产
	
	//static PrivateKey hzfPriKey = CryptoUtil.getRSAPrivateKeyByFileSuffix("c:/private/AES/rsa_private_key.pem", "pkcs8", null, "RSA");// 测试
	
	
	final static String Min_sheng_charset = "utf-8";

	// final static String Min_sheng_cooperator = "SMZF_SHFF";
	final static String Min_sheng_cooperator = "SMZF_SHFB";// 生产——福别

	public final static String Min_sheng_D0_cooperator = "SMZF_SHFF_HD_T0";// 测试SMZF_SHFF_T0

	/**
	 * 默认回调地址
	 * */
	final static String Min_sheng_callBack = ApplicatonStaticUtil.product_url + "/payment/minshengNotify";// 生产
	// public final static String Min_sheng_callBack =
	// "http://ffy.ngrok.sapronlee.com/flypayfx/payment/minshengNotify";// 测试
	
	final static String Min_sheng_serverUrl = "https://ipay.cmbc.com.cn:9020/nbp-smzf-hzf";// 生产
	
	//final static String Min_sheng_serverUrl ="http://110.80.39.174:9013/nbp-smzf-hzf";// 测试


	public final static String Min_sheng_tixian_callBack = ApplicatonStaticUtil.product_url + "/payment/minshengTixianNotify";

	/**
	 * 日志对象
	 */
	private final static Logger logger = LoggerFactory.getLogger(MinShengMerchantInputMinShengUtil.class);

	public static void main(String[] args) {
		try {
			// createSubShopToMinSheng();
//			createSubShopToMinSheng_2();
			// createSMZF005ToMinSheng();
//			 T0Tixian();
			// T0TixianSearch();
			// T0Tixianstatement();
			// createSubShopToMinSheng();
			minsheng007();
//			 minsheng030();
//			minsheng006();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void T0Tixian() throws Exception {
		SMZF021 wx021 = new SMZF021();
		wx021.setMerchantCode("2017043017976979");
		wx021.setRequestId(getUniqueOrderByType("MSTX",70242l));
		wx021.setCallBack(MinShengMerchantInputMinShengUtil.Min_sheng_tixian_callBack);
		wx021.setCooperator("SMZF_SHFF_HD_T0");
		String responseStr = MinShengMerchantInputMinShengUtil.doPost(wx021);
		Map<String, String> result = XmlMapper.xml2Map(responseStr);
		System.out.println(result);
		//江苏省无锡市江阴马镇奇松路11号  江苏省无锡市滨湖区太湖街道贡湖大道
		
//		SMZF022 wx022 = new SMZF022();
//		wx022.setOriReqMsgId("MSTX201705150707496590000042636");
//		wx022.setCooperator("SMZF_SHFF_HD_T0");
//		String responseStr = MinShengMerchantInputMinShengUtil.doPost(wx022);
//		Map<String, String> result = XmlMapper.xml2Map(responseStr);
//		System.out.println(result);
	}//MSTX201705131358588730000070242
	
	public static String getUniqueOrderByType(String transType, Long userId) {
		String out_trade_no = transType + DateUtil.convertCurrentDateTimeToString()
				+ StringUtils.leftPad(String.valueOf(RandomUtils.nextInt(1000)), 3, "0")
				+ StringUtils.leftPad(userId.toString(), 10, "0");
		return out_trade_no;
	}

	public static void T0TixianSearch() throws Exception {
		SMZF022 wx022 = new SMZF022();
		wx022.setOriReqMsgId("20161223134837");
		System.out.println(doPost(wx022));
	}

	public static void T0Tixianstatement() throws Exception {
		SMZF024 f024 = new SMZF024();
		f024.setSettleDate("20161220");
		f024.setFileType("1");
		System.out.println(doPost(f024));
	}

	public static void minsheng007() throws Exception {
		SMZF007 f007 = new SMZF007();
		
		f007.setMerchantId("FB_ZFB_201706201555260");
		// f007.setMerchantCode(st.get(i - 11));
		f007.setCooperator("SMZF_SHFF_HD_T0");
		System.out.println(doPost(f007));
	}
	
	
	
	public static void minsheng006() throws Exception {
		SMZF006 f006 = new SMZF006();
		f006.setOriReqMsgId("WXQR201705241730590450000043569");
		f006.setCooperator(Min_sheng_D0_cooperator);
		System.out.println(doPost(f006));
	}


	protected static void createSubShopToMinSheng() throws IOException {
		try {
			for (int i = 11; i < 16; i++) {

				String merchantId = "FB_WX_201700" + i;// 2017012230980932
				// FB_WX_0007---2016122304829669
				// FB_WX_0008---2016122304829700
				SMZF001 wx001 = new SMZF001();
				wx001.setCooperator(Min_sheng_D0_cooperator);
				wx001.setPayWay("WXZF");
				wx001.setCategory("42");
				wx001.setMerchantId(merchantId);
				wx001.setMerchantName(merchantId);
				wx001.setShortName(merchantId);
				wx001.setMerchantAddress("上海市嘉定区江桥镇华江路129弄7号楼4层4333室");
				wx001.setServicePhone("13296134638");

				// req.setBank_card_no("6214852111454099");
				// req.setCard_holder("芦强");// 6214851213282739

				// req.setBank_card_no("6013820800101978854");
				// req.setCard_holder("孙月");
				wx001.setAccName("芦强");
				wx001.setAccNo("6214852111454099");
				wx001.setT0drawFee("0.2");
				wx001.setT0tradeRate("0.003");
				wx001.setT1drawFee("0.2");
				wx001.setT1tradeRate("0.0027");
				wx001.setChannelMerchantCode(merchantId);

				merchantId = "FB_ZFB_201700" + i;
				SMZF001 zfb001 = new SMZF001();
				zfb001.setPayWay("ZFBZF");
				zfb001.setCooperator(Min_sheng_D0_cooperator);
				zfb001.setCategory("2016062900190068");
				zfb001.setMerchantId(merchantId);
				zfb001.setMerchantName("上海涩零企业营销策划有限公司");
				zfb001.setShortName("钱袋收款");
				zfb001.setMerchantAddress("上海市嘉定区江桥镇华江路129弄7号楼4层4333室");
				zfb001.setServicePhone("13296134638");

				zfb001.setAccName("芦强");
				zfb001.setAccNo("6214852111454099");
				zfb001.setT0drawFee("0.2");
				zfb001.setT0tradeRate("0.003");
				zfb001.setT1drawFee("0.2");
				zfb001.setT1tradeRate("0.0027");
				zfb001.setChannelMerchantCode(merchantId);

				System.out.println("微信===" + doPost(wx001));
				System.out.println("支付宝===" + doPost(zfb001));
			}

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	protected static void createSubShopToMinSheng_2() throws IOException {
		try {
			String merchantId = "FB_WX_201702240051";// 2017012230980932
			// FB_WX_0007---2016122304829669
			// FB_WX_0008---2016122304829700
			SMZF001 wx001 = new SMZF001();
			wx001.setCooperator(Min_sheng_D0_cooperator);
			wx001.setPayWay("WXZF");
			wx001.setCategory("42");
			wx001.setMerchantId(merchantId);
			wx001.setMerchantName("上海蓝璇美容美发有限公司");
			wx001.setShortName("蓝璇");
			wx001.setMerchantAddress("上海市嘉定区江桥镇华江路129弄7号楼4层4333室");
			wx001.setServicePhone("13296134638");

			// req.setBank_card_no("6214852111454099");
			// req.setCard_holder("芦强");// 6214851213282739

			// req.setBank_card_no("6013820800101978854");
			// req.setCard_holder("孙月");
			wx001.setAccName("芦强");
			wx001.setAccNo("6226630401364905");
			wx001.setT0drawFee("0.2");
			wx001.setT0tradeRate("0.003");
			wx001.setT1drawFee("0.2");
			wx001.setT1tradeRate("0.0027");
			wx001.setChannelMerchantCode(merchantId);

			merchantId = "FB_ZFB_20170224005";
			SMZF001 zfb001 = new SMZF001();
			zfb001.setPayWay("ZFBZF");
			zfb001.setCooperator(Min_sheng_D0_cooperator);
			zfb001.setCategory("2016062900190068");
			zfb001.setMerchantId(merchantId);
			zfb001.setMerchantName("上海希善健康管理咨询有限公司");
			zfb001.setShortName("希善");
			zfb001.setMerchantAddress("上海市嘉定区江桥镇华江路129弄7号楼4层4333室");
			zfb001.setServicePhone("13296134638");

			zfb001.setAccName("芦强");
			zfb001.setAccNo("6226630401364905");
			zfb001.setT0drawFee("0.2");
			zfb001.setT0tradeRate("0.003");
			zfb001.setT1drawFee("0.2");
			zfb001.setT1tradeRate("0.0027");
			zfb001.setChannelMerchantCode(merchantId);

			System.out.println("微信===" + doPost(wx001));
//			System.out.println("支付宝===" + doPost(zfb001));
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public static String doPost(CommonSMZF smzf) throws Exception {
		try {
			String zfbMsg = CommonSMZF.getSMZFRequestMessage(smzf);
			logger.info("向SMZF服务器发送报文:"+zfbMsg);
			String reqMsgId = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
			if (StringUtil.isNotBlank(smzf.getRequestId())) {
				reqMsgId = smzf.getRequestId();
			}

			String callBack = Min_sheng_callBack;
			if (StringUtil.isNotBlank(smzf.getCallBack())) {
				callBack = smzf.getCallBack();
			}

			byte[] plainBytes = zfbMsg.getBytes(Min_sheng_charset);
			String keyStr = DateUtil.getDateTime("yyMMddHHmmss", new Date()) + StringUtils.leftPad(String.valueOf(RandomUtils.nextInt(10000)), 4, "0");
			byte[] keyBytes = keyStr.getBytes(Min_sheng_charset);
			String encryptData = new String(Base64.encodeBase64((CryptoUtil.AESEncrypt(plainBytes, keyBytes, "AES", "AES/ECB/PKCS5Padding", null))), Min_sheng_charset);
			String signData = new String(Base64.encodeBase64(CryptoUtil.digitalSign(plainBytes, hzfPriKey, "SHA1WithRSA")), Min_sheng_charset);
			String encrtptKey = new String(Base64.encodeBase64(CryptoUtil.RSAEncrypt(keyBytes, yhPubKey, 2048, 11, "RSA/ECB/PKCS1Padding")), Min_sheng_charset);
			logger.info("向SMZF服务器发出请求reqMsgId:[{}]", new Object[] { reqMsgId });
			List<NameValuePair> nvps = new LinkedList<NameValuePair>();
			nvps.add(new BasicNameValuePair("encryptData", encryptData));
			nvps.add(new BasicNameValuePair("encryptKey", encrtptKey));
			if (StringUtil.isNotBlank(smzf.getCooperator())) {
				nvps.add(new BasicNameValuePair("cooperator", smzf.getCooperator()));
			} else {
				nvps.add(new BasicNameValuePair("cooperator", Min_sheng_cooperator));
			}
			nvps.add(new BasicNameValuePair("signData", signData));
			nvps.add(new BasicNameValuePair("tranCode", smzf.getOperateName()));
			nvps.add(new BasicNameValuePair("callBack", callBack));
			nvps.add(new BasicNameValuePair("reqMsgId", reqMsgId));
			logger.info("向SMZF服务器发送报文加密:"+nvps);
			byte[] b = HttpClient4Util.getInstance().doPost(Min_sheng_serverUrl, null, nvps);
			String respStr = new String(b, Min_sheng_charset);
			JSONObject jsonObject = JSONObject.fromObject(respStr);
			return getResponseContent(jsonObject);
		} catch (Exception e) {
			logger.error("向SMZF服务器发出请求异常", e);
			throw e;
		}
	}

	public static String getResponseContent(JSONObject jsonObject) throws UnsupportedEncodingException {
		String resEncryptData = jsonObject.getString("encryptData");
		String resEncryptKey = jsonObject.getString("encryptKey");

		byte[] decodeBase64KeyBytes = Base64.decodeBase64(resEncryptKey.getBytes(Min_sheng_charset));
		// 解密encryptKey得到merchantAESKey
		byte[] merchantAESKeyBytes = CryptoUtil.RSADecrypt(decodeBase64KeyBytes, hzfPriKey, 2048, 11, "RSA/ECB/PKCS1Padding");
		// 使用base64解码商户请求报文
		byte[] decodeBase64DataBytes = Base64.decodeBase64(resEncryptData.getBytes(Min_sheng_charset));
		// 用解密得到的merchantAESKey解密encryptData
		byte[] merchantXmlDataBytes = CryptoUtil.AESDecrypt(decodeBase64DataBytes, merchantAESKeyBytes, "AES", "AES/ECB/PKCS5Padding", null);
		return new String(merchantXmlDataBytes, Min_sheng_charset);
	}
}
