package com.cn.flypay.utils.pingan;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import com.cn.flypay.utils.Base64;
import com.cn.flypay.utils.KeyConfig;

public class MsgInfoDESUtil {

	public static void main(String[] args) throws Exception {
		String ma = "003M6T";
		String en = EncryptAsDoNet(ma, KeyConfig.pay_code_key);
		String en2 = "d6O043pT0sbIG2n+vMnBOg==";

		System.out.println(URLEncoder.encode(en2));
		System.out.println(URLDecoder.decode(URLEncoder.encode(en2)));
		System.out.println(URLDecoder.decode(URLDecoder.decode(URLEncoder.encode(en2))));
		System.out.println(en);
		String dn = DecryptDoNet(en2, KeyConfig.pay_code_key);

		dn = DecryptDoNet(dn, KeyConfig.pay_code_key);
		System.out.println(dn);
	}

	public static String encodeString(String string) {
		String key = KeyConfig.msg_info_key;
		try {
			String encryptAsDoNet = MsgInfoDESUtil.EncryptAsDoNet(string, key);
			return encryptAsDoNet;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * jie密
	 * 
	 * @param message
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String DecryptDoNet(String message) throws Exception {
		String key = KeyConfig.msg_info_key;
		byte[] bytesrc = Base64.decode(message);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte);
	}
	
	
	public static String DecryptDoNetTwo(String message) throws Exception {
		String key = KeyConfig.msg_info_keyTwo;
		byte[] bytesrc = Base64.decode(message);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte);
	}

	public static String DecryptDoNet(String message, String key) throws Exception {
		byte[] bytesrc = Base64.decode(message);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte);
	}

	/**
	 * jia密
	 * 
	 * @param message
	 * @param key
	 * @return
	 */
	public static String EncryptAsDoNet(String message) {
		return EncryptAsDoNet(message, KeyConfig.msg_info_key);
	}
	
	public static String EncryptAsDoNetTwo(String message) {
		return EncryptAsDoNet(message, KeyConfig.msg_info_keyTwo);
	}


	/**
	 * jia密
	 * 
	 * @param message
	 * @param key
	 * @return
	 */
	public static String EncryptAsDoNet(String message, String key) {
		String encode = null;
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] encryptbyte = cipher.doFinal(message.getBytes());
			encode = new String(Base64.encode(encryptbyte));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encode;
	}

}
