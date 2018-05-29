package com.cn.flypay.utils.xinke;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class XinKeMD5Util {
	public static String getMD5Str2(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else {
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
		}
		return md5StrBuff.toString();
	}

	public static String getMD5Str(String sMsg) {
		MessageDigest messageDigest = null;
		try {
			byte[] msg = sMsg.getBytes("utf-8");
			System.out.println();
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(msg);
		} catch (UnsupportedEncodingException e) {
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
		}
		byte[] b = messageDigest.digest();
		return new String(Base64.encodeBase64(b));
	}
}