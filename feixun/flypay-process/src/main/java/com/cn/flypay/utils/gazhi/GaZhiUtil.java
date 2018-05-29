package com.cn.flypay.utils.gazhi;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * 与嘎吱通道对接
 * @author liangchao
 *
 */
@Component
public class GaZhiUtil {
	//公钥
	private static String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCc1PvuAfY88htqX1RUmg4xUZt4gvZxyG7/BG5bT5fU7QiS2eDWHQfb19+HZcFICWWMxZps4lDPXSE/ctIRKiYQqlBpZavUXuKFv6JfDbO5jL8SDl4arz5LLRrpw8+S/4ImTcFlQr7fCdJ13RT8j/krW/g95Atdk50oMKi/Q0131wIDAQAB";
	//请求嘎吱链接URL
	//private static String request_url = "http://163.53.90.117:9803/QRConsume/inform/";  //提供的测试环境
	private static String request_url ="http://163.53.90.117:19803/QRConsume/inform/";  //提供的正式环境
	
	
	
	
	/**
	 * 向嘎吱发送信息
	 * @param param
	 */
	public static Map<String, String> execute(Map<String,String> param) throws Exception{
			//将信息xml格式化，并加密处理
			String postBody = createPostBody(param);
			//String result = GaZhiHttpReqUtils.httpPostRequest(gaizhi_reqest_url, postBody);
			String result = GaZhiHttpReqUtils.httpPostRequest(request_url, postBody);
			System.out.println(result);
			Map<String, String> resMap = GaZhiXml2MapUtil.xml2map(new String(Base64.decodeBase64(result)));
			System.out.println(resMap);
			return resMap;
			
	}
	
	
	/**
	 * 将请求参数map转化为XML,并用公钥进行进行加密
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private static String createPostBody(Map<String, String> map) throws Exception {
		if (map != null) {
			StringBuffer xml = new StringBuffer();
			xml.append("<qrbody>");
			for (Entry<String, String> entry : map.entrySet()) {
				if (StringUtils.isNotBlank(entry.getKey()) && StringUtils.isNotBlank(entry.getValue())) {
					xml.append("<").append(entry.getKey()).append(">").append(entry.getValue()).append("</")
							.append(entry.getKey()).append(">");
				}
			}
			xml.append("</qrbody>");
			//对包含<qrbody>标签在内的域段进行SHA1哈希运算
			byte[] hexBody = GaZhiRSAEncrypt.SHA1(xml.toString());
			//用支付公钥进行加密
			//String key = gazhi_public_key;
			String enc = GaZhiRSAEncrypt.encryptByPublicKey(hexBody, public_key);
			StringBuffer qrxml = new StringBuffer();
			//保存到<securedata>域段内上传送
			qrxml.append("<qrxml>").append(xml).append("<securedata>").append(enc).append("</securedata>")
					.append("</qrxml>");
			System.out.println("qrxml:{" + qrxml + "}");
			//进行base64位编码
			return Base64.encodeBase64String(qrxml.toString().getBytes());
		}
		return "";
	}
}
