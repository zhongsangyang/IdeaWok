package com.cn.flypay.utils.minsheng;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.flypay.pageModel.payment.minsheng.CommonSMZF;
import com.cn.flypay.pageModel.payment.minsheng.SMZF001;
import com.cn.flypay.pageModel.payment.minsheng.SMZF007;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.channel.HttpClient4Util;

public class MinShengCreateMerchant {

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
	private final static Logger logger = LoggerFactory.getLogger(MinShengCreateMerchant.class);

	private final static Long maxChannelId = 6385l;//sys_channel数据库中最大ID
	private final static Long orgChannelId = 11840l;//sys_org_channel数据库最大ID

	public static void main(String[] args) {
		try {
			String merchantId = DateUtil.convertCurrentDateTimeToString();
			// String[] merchantNames = { "上海蓝璇美容美发有限公司", "芸鼎美发（上海）有限公司",
			// "上海煜永美容美发有限公司", "上海迪佳美容有限公司", "上海泽木理发店" };
			// String[] shortNames = { "蓝璇", "芸鼎", "煜永", "迪佳", "泽木" };
//			List<String> lines = FileUtils.readLines(new File("D:/子商户名称.txt"));
//			Integer i = 0;
//			for (String line : lines) {
//				String[] cs = line.split("\t");
//				createSubShopToMinSheng_2(merchantId + (i), cs[0], cs[1], i);
//				i++;
//			}
			
			List<Map<String, String>> lines = new ArrayList<Map<String, String>>();
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("name", "多美徐汇龙鱼美容美发有限公司");
//			map.put("add", "多美浦东");
//			
//			Map<String, String> map1 = new HashMap<String, String>();
//			map1.put("name", "多美徐汇龙鱼美容美发有限公司");
//			map1.put("add", "多美嘉定");
//			
//			Map<String, String> map2 = new HashMap<String, String>();
//			map2.put("name", "多美徐汇龙鱼美容美发有限公司");
//			map2.put("add", "多美普陀");
//			
//			Map<String, String> map3 = new HashMap<String, String>();
//			map3.put("name", "多美徐汇龙鱼美容美发有限公司");
//			map3.put("add", "多美金山");
//			
//			Map<String, String> map4 = new HashMap<String, String>();
//			map4.put("name", "多美徐汇龙鱼美容美发有限公司");
//			map4.put("add", "多美奉贤");
//			
//			Map<String, String> map5 = new HashMap<String, String>();
//			map5.put("name", "多美徐汇龙鱼美容美发有限公司");
//			map5.put("add", "多美青浦");
//			
//			Map<String, String> map6 = new HashMap<String, String>();
//			map6.put("name", "多美徐汇龙鱼美容美发有限公司");
//			map6.put("add", "多美静安");
//			
//			Map<String, String> map7 = new HashMap<String, String>();
//			map7.put("name", "多美徐汇龙鱼美容美发有限公司");
//			map7.put("add", "多美虹口");
//			
//			Map<String, String> map8 = new HashMap<String, String>();
//			map8.put("name", "多美北京龙鱼美容美发有限公司");
//			map8.put("add", "多美淮安");
//			
//			Map<String, String> map9 = new HashMap<String, String>();
//			map9.put("name", "多美北京龙鱼美容美发有限公司");
//			map9.put("add", "多美杭州");
//			
//			Map<String, String> map10 = new HashMap<String, String>();
//			map10.put("name", "多美北京龙鱼美容美发有限公司");
//			map10.put("add", "多美温州");
//			
//			Map<String, String> map11 = new HashMap<String, String>();
//			map11.put("name", "多美北京龙鱼美容美发有限公司");
//			map11.put("add", "多美金华");
//			
//			Map<String, String> map12 = new HashMap<String, String>();
//			map12.put("name", "多美北京龙鱼美容美发有限公司");
//			map12.put("add", "多美湖州");
//			
//			Map<String, String> map13 = new HashMap<String, String>();
//			map13.put("name", "多美北京龙鱼美容美发有限公司");
//			map13.put("add", "多美义乌");
//			
//			Map<String, String> map14 = new HashMap<String, String>();
//			map14.put("name", "多美北京龙鱼美容美发有限公司");
//			map14.put("add", "多美衢州");
//			
//			Map<String, String> map15 = new HashMap<String, String>();
//			map15.put("name", "多美北京龙鱼美容美发有限公司");
//			map15.put("add", "多美绍兴");
//			
//			Map<String, String> map16 = new HashMap<String, String>();
//			map16.put("name", "多美北京龙鱼美容美发有限公司");
//			map16.put("add", "多美丽水");
//			
//			Map<String, String> map17 = new HashMap<String, String>();
//			map17.put("name", "长伟浦东长伟健康管理有限公司");
//			map17.put("add", "长伟静安");
//			
//			Map<String, String> map18 = new HashMap<String, String>();
//			map18.put("name", "长伟浦东长伟健康管理有限公司");
//			map18.put("add", "长伟金山");
//			
//			
//			Map<String, String> map19 = new HashMap<String, String>();
//			map19.put("name", "长伟浦东长伟健康管理有限公司");
//			map19.put("add", "长伟虹口");
			
			
			Map<String, String> map20 = new HashMap<String, String>();
			map20.put("name", "长伟浦东长伟健康管理有限公司");
			map20.put("add", "长伟青浦");
			
			
//			lines.add(map);
//			lines.add(map1);
//			lines.add(map2);
//			lines.add(map3);
//			lines.add(map4);
			
//			lines.add(map5);
//			lines.add(map6);
//			lines.add(map7);
//			lines.add(map8);
//			lines.add(map9);
			
//			lines.add(map10);
//			lines.add(map11);
//			lines.add(map12);
//			lines.add(map13);
//			lines.add(map14);
//			lines.add(map15);
			
//			lines.add(map16);
//			lines.add(map17);
//			lines.add(map18);
//			lines.add(map19);
			lines.add(map20);
			
			
			Integer i = 0;
			for (int j = 0; j < lines.size(); j++) {
				Map<String, String> mapl = lines.get(j);
				createSubShopToMinSheng_2(merchantId + (i), mapl.get("name"),mapl.get("add"), i);
				i++;
			}
			/*
			 * for (int i = 0; i < merchantNames.length; i++) {
			 * createSubShopToMinSheng_2(merchantId + i, merchantNames[i],
			 * shortNames[i], i); }
			 */
			// minsheng007();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void createSubShopToMinSheng_2(String merchantId, String merchantName, String shortName, int num) throws IOException {
		try {
//			merchantId = "FB_WX_" + merchantId;
//			SMZF001 wx001 = new SMZF001();
//			wx001.setCooperator(Min_sheng_D0_cooperator);
//			wx001.setPayWay("WXZF");
//			wx001.setCategory("42");
//			wx001.setMerchantId(merchantId);
//			wx001.setMerchantName(merchantName);
//			wx001.setShortName(shortName);
//			wx001.setMerchantAddress("上海市嘉定区江桥镇华江路129弄7号楼4层4333室");
//			wx001.setServicePhone("13296134638");
//			wx001.setIdCard("152822199012293814");
//			wx001.setAccName("芦强");
//			wx001.setAccNo("6226630401364905");
//			wx001.setT0drawFee("0.2");
//			wx001.setT0tradeRate("0.003");
//			wx001.setT1drawFee("0.2");
//			wx001.setT1tradeRate("0.0027");
//			wx001.setChannelMerchantCode(merchantId);
//
//			merchantId = "FB_ZFB_" + merchantId;
//			SMZF001 zfb001 = new SMZF001();
//			zfb001.setPayWay("ZFBZF");
//			zfb001.setCooperator(Min_sheng_D0_cooperator);
//			zfb001.setCategory("2015063000020189");
//			zfb001.setMerchantId(merchantId);
//			zfb001.setMerchantName("个体户芦强");
//			zfb001.setShortName("芦强麻辣烫小店");
//			zfb001.setMerchantAddress("北京市西城区华江路129号楼4层4333室");
//			zfb001.setServicePhone("13296134638");
//			zfb001.setIdCard("152822199012293814");
//			zfb001.setAccName("芦强");
//			zfb001.setAccNo("6226630401364905");
//			zfb001.setT0drawFee("0.2");
//			zfb001.setT0tradeRate("0.003");
//			zfb001.setT1drawFee("0.2");
//			zfb001.setT1tradeRate("0.0027");
//			zfb001.setChannelMerchantCode(merchantId);
//			zfb001.setProvinceCode("110000");
//			zfb001.setCityCode("110100");
//			zfb001.setDistrictCode("110102");
//			zfb001.setContactType("01");
//			zfb001.setContactName("芦强");
//			System.out.println("hao:"+merchantId);
			
		
			
			
			merchantId = "FB_QQ_" + merchantId;
			SMZF001 qqzf001 = new SMZF001();
			qqzf001.setPayWay("QQZF");
			qqzf001.setCooperator("SMZF_SHFF_HD_T0");
			qqzf001.setCategory("2016062900190068");
			qqzf001.setMerchantId(merchantId);
			qqzf001.setMerchantName("小花烘焙店");
			qqzf001.setShortName("小花烘焙店");
			qqzf001.setMerchantAddress("上海市嘉定区江桥镇华江路129弄7号楼4层4333室");
			qqzf001.setServicePhone("13296134638");
			qqzf001.setIdCard("152822199012293814");
			qqzf001.setAccName("芦强");
			qqzf001.setAccNo("6226630401364905");
			qqzf001.setT0drawFee("0.2");
			qqzf001.setT0tradeRate("0.003");
			qqzf001.setT1drawFee("0.2");
			qqzf001.setT1tradeRate("0.0027");
			qqzf001.setProvinceCode("110000");
			qqzf001.setCityCode("110100");
			qqzf001.setDistrictCode("110102");
			qqzf001.setContactType("01");
			qqzf001.setContactName("芦强");
			qqzf001.setChannelMerchantCode(merchantId);
			System.out.println("merchantId:"+merchantId);
			
			
			
//			merchantId = "FB_BD_" + merchantId;
//			SMZF001 bdzf001 = new SMZF001();
//			bdzf001.setPayWay("BDQB");
//			bdzf001.setCooperator("SMZF_SHFF_HD_T0");
//			bdzf001.setCategory("2016062900190068");
//			bdzf001.setMerchantId(merchantId);
//			bdzf001.setMerchantName(merchantName);
//			bdzf001.setShortName(shortName);
//			bdzf001.setMerchantAddress("上海市嘉定区江桥镇华江路129弄7号楼4层4333室");
//			bdzf001.setServicePhone("13296134638");
//			bdzf001.setIdCard("152822199012293814");
//			bdzf001.setAccName("芦强");
//			bdzf001.setAccNo("6226630401364905");
//			bdzf001.setT0drawFee("0.2");
//			bdzf001.setT0tradeRate("0.003");
//			bdzf001.setT1drawFee("0.2");
//			bdzf001.setT1tradeRate("0.0027");
//			bdzf001.setChannelMerchantCode(merchantId);
			
			
			Map<String, String> zfbXmlMap = XmlMapper.xml2Map(doPost(qqzf001));
			System.out.println(shortName + "====QQ支付===" + zfbXmlMap.get("merchantCode"));
			generateSqlQQ(maxChannelId + num, orgChannelId + num * 10, zfbXmlMap.get("merchantCode"), shortName, 1300);
			
//			Map<String, String> zfbXmlMap = XmlMapper.xml2Map(doPost(zfb001));
//			System.out.println(shortName + "====支付宝===" + zfbXmlMap.get("merchantCode"));
//			generateSql(maxChannelId + num, orgChannelId + num * 10, zfbXmlMap.get("merchantCode"), shortName, 200);
			
			
//			 Map<String, String> wxXmlMap = XmlMapper.xml2Map(doPost(wx001));
//			 System.out.println(shortName + "====微信===" +
//			 wxXmlMap.get("merchantCode"));
//			 generateSql(maxChannelId + num, orgChannelId + num * 10,
//			 wxXmlMap.get("merchantCode"), shortName, 300);
			
//			
//			Map<String, String> zfbXmlMap = XmlMapper.xml2Map(doPost(bdzf001));
//			System.out.println(shortName + "====百度支付===" + zfbXmlMap.get("merchantCode"));
//			generateSqlBD(maxChannelId + num, orgChannelId + num * 10, zfbXmlMap.get("merchantCode"), shortName, 1000);

		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	
	
	

	private static String generateSql(Long channelId, Long orgChannelId, String merchantCode, String shoreName, Integer type) {

		String channleSql = "INSERT INTO `sys_channel` (`ID`, `VERSION`, `type`, `name`, `real_rate`, `show_rate`, `share_rate`, `max_trade_amt`, `min_trade_amt`, `status`, `max_channel_amt`, `min_channel_amt`, `today_Amt`, `max_Amt_Per_Day`, `account`, `config`, `detail_name`, `seq`, `user_type`, `commission_rate`) VALUES (%s, 0, "
				+ type
				+ ", 'MINSHENG', 0.0027, 0.0049, 0.0010, 100000.00, 0.00, 1, 20000.00, 0.00, 0.00, 200000.00, 'SMZF_SHFF_HD_T0','{\"cooperator\":\"SMZF_SHFF_HD_T0\",\"callBack\":\"https://bbpurse.com/flypayfx/payment/minshengNotify\",\"serverUrl\":\"https://ipay.cmbc.com.cn:9020/nbp-smzf-hzf\",\"merchant_code\":\"%s\"}','民生D0_"
				+ shoreName + (type == 200 ? "支付宝" : "微信") + "二维码','100','700',0);";
		String orgChannel = "INSERT INTO `sys_org_channel` VALUES (	%s	, 0, %s,	%s	, 0.0028, 0, '2016-10-27 14:43:38', '2016-10-27 14:43:38', '2016-10-27 14:43:38', 'system', '2016-10-27 14:43:38', 'system');";

		System.out.println(String.format(channleSql, ++channelId, merchantCode));
		orgChannelId++;
		int[] orgIds = { 1, 2, 3, 39, 40 ,44 ,45, 47, 48, 49 };//运营商 OEM 的ID sys_organization
		for (int i = 0; i < orgIds.length; i++) {
			System.out.println(String.format(orgChannel, orgChannelId + i, orgIds[i], channelId));
		}
		return null;
	}
	
	
	private static String generateSqlQQ(Long channelId, Long orgChannelId, String merchantCode, String shoreName, Integer type) {

		String channleSql = "INSERT INTO `sys_channel` (`ID`, `VERSION`, `type`, `name`, `real_rate`, `show_rate`, `share_rate`, `max_trade_amt`, `min_trade_amt`, `status`, `max_channel_amt`, `min_channel_amt`, `today_Amt`, `max_Amt_Per_Day`, `account`, `config`, `detail_name`, `seq`, `user_type`, `commission_rate`) VALUES (%s, 0, "
				+ type
				+ ", 'MINSHENG', 0.0027, 0.0049, 0.0010, 100000.00, 0.00, 1, 20000.00, 0.00, 0.00, 200000.00, 'SMZF_SHFF_HD_T0','{\"cooperator\":\"SMZF_SHFF_HD_T0\",\"callBack\":\"https://bbpurse.com/flypayfx/payment/minshengNotify\",\"serverUrl\":\"https://ipay.cmbc.com.cn:9020/nbp-smzf-hzf\",\"merchant_code\":\"%s\"}','民生福别"
				+ shoreName + ("QQ支付") + "二维码','100','700',0);";
		String orgChannel = "INSERT INTO `sys_org_channel` VALUES (	%s	, 0, %s,	%s	, 0.0028, 0, '2016-10-27 14:43:38', '2016-10-27 14:43:38', '2016-10-27 14:43:38', 'system', '2016-10-27 14:43:38', 'system');";

		System.out.println(String.format(channleSql, ++channelId, merchantCode));
		orgChannelId++;
		int[] orgIds = { 1, 2, 3, 39, 40 ,44 ,45, 47, 48, 49 };//运营商 OEM 的ID sys_organization
		for (int i = 0; i < orgIds.length; i++) {
			System.out.println(String.format(orgChannel, orgChannelId + i, orgIds[i], channelId));
		}
		return null;
	}
	
	
	private static String generateSqlBD(Long channelId, Long orgChannelId, String merchantCode, String shoreName, Integer type) {

		String channleSql = "INSERT INTO `sys_channel` (`ID`, `VERSION`, `type`, `name`, `real_rate`, `show_rate`, `share_rate`, `max_trade_amt`, `min_trade_amt`, `status`, `max_channel_amt`, `min_channel_amt`, `today_Amt`, `max_Amt_Per_Day`, `account`, `config`, `detail_name`, `seq`, `user_type`, `commission_rate`) VALUES (%s, 0, "
				+ type
				+ ", 'MINSHENG', 0.0027, 0.0049, 0.0010, 100000.00, 0.00, 1, 20000.00, 0.00, 0.00, 200000.00, 'SMZF_SHFF_HD_T0','{\"cooperator\":\"SMZF_SHFF_HD_T0\",\"callBack\":\"https://bbpurse.com/flypayfx/payment/minshengNotify\",\"serverUrl\":\"https://ipay.cmbc.com.cn:9020/nbp-smzf-hzf\",\"merchant_code\":\"%s\"}','民生福别"
				+ shoreName + ("百度支付") + "二维码','100','700',0);";
		String orgChannel = "INSERT INTO `sys_org_channel` VALUES (	%s	, 0, %s,	%s	, 0.0028, 0, '2016-10-27 14:43:38', '2016-10-27 14:43:38', '2016-10-27 14:43:38', 'system', '2016-10-27 14:43:38', 'system');";

		System.out.println(String.format(channleSql, ++channelId, merchantCode));
		orgChannelId++;
		int[] orgIds = { 1, 2, 3, 39, 40 ,44 ,45, 47, 48, 49 };//运营商 OEM 的ID sys_organization
		for (int i = 0; i < orgIds.length; i++) {
			System.out.println(String.format(orgChannel, orgChannelId + i, orgIds[i], channelId));
		}
		return null;
	}
	

	public static String doPost(CommonSMZF smzf) throws Exception {
		try {
			String zfbMsg = CommonSMZF.getSMZFRequestMessage(smzf);
			String reqMsgId = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
			if (StringUtil.isNotBlank(smzf.getRequestId())) {
				reqMsgId = smzf.getRequestId();
			}
			reqMsgId ="MSSHFF" + reqMsgId;
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
			byte[] b = HttpClient4Util.getInstance().doPost(Min_sheng_serverUrl, null, nvps);
			String respStr = new String(b, Min_sheng_charset);
			JSONObject jsonObject = JSONObject.fromObject(respStr);
			String response = getResponseContent(jsonObject);
			logger.info(response);
			return response;
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

	public static void minsheng007() throws Exception {
		SMZF007 f007 = new SMZF007();

		f007.setMerchantId("FB_WX_20170224002");
		// f007.setMerchantCode(st.get(i - 11));
		f007.setCooperator(Min_sheng_D0_cooperator);
		System.out.println(doPost(f007));
	}
}
