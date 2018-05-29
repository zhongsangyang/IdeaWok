package com.cn.flypay.utils.zheyang;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * 哲扬通道加密
 * @author liangchao
 *
 */
public class MD5Util {
	
	public final static String md5(String s, String entype) { 
	    String result = "";
	    char hexDigits[] = 
	                  { '0', '1', '2', '3', 
	              '4', '5', '6', '7',
	              '8', '9', 'a', 'b', 
	              'c', 'd', 'e', 'f' };    
	    try {
	        byte[] strTemp = s.getBytes(entype);      
	        MessageDigest mdTemp = MessageDigest.getInstance("MD5");    
	        mdTemp.update(strTemp);    
	        byte[] md = mdTemp.digest();    
	        int j = md.length;    
	        char str[] = new char[j * 2];    
	        int k = 0;    
	        for (int i = 0; i < j; i++) {    
	            byte b = md[i];       
	            str[k++] = hexDigits[b >> 4 & 0xf];    
	            str[k++] = hexDigits[b & 0xf];    
	        }    
	        result = new String(str);
	    } catch (Exception e) 
	        {e.printStackTrace();}    
	    return result;
	    }

	/**
	 * MD5方法
	 *
	 * @param text  明文
	 * @param salt盐
	 * @return 密文
	 * @throws Exception
	 */
	public static String md5LowerCase(String text, String salt) throws NoSuchAlgorithmException{
		byte[] bytes = (text + salt).getBytes();
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(bytes);
		bytes = messageDigest.digest();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			if ((bytes[i] & 0xff) < 0x10) {
				sb.append("0");
			}
			sb.append(Long.toString(bytes[i] & 0xff, 16));
		}
		return sb.toString().toLowerCase();
	}  
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(md5LowerCase("7", "participant"));
		System.out.println(md5LowerCase("7", "encryptId"));
	}
	
}
