package com.cn.flypay.utils.shenfu;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class AESCoder2 {

	public static final byte[] key = Base64.decodeBase64("Geyd4zScWAbJT9VrWr5cUQ==");

	/**
	 * 
	 * 密钥算法
	 * 
	 */

	public static final String KEY_ALGORITHM = "AES";

	/**
	 * 
	 * 加密/解密算法 / 工作模式 / 填充方式 Java 6支持PKCS5Padding填充方式 Bouncy
	 * 
	 * Castle支持PKCS7Padding填充方式
	 * 
	 */

	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	/**
	 * 
	 * 转换密钥
	 * 
	 * 
	 * 
	 * @param key
	 * 
	 *            二进制密钥
	 * 
	 * @return Key 密钥
	 * 
	 * @throws Exception
	 * 
	 */

	private static Key toKey(byte[] key) throws Exception {

		// 实例化AES密钥材料

		SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);

		return secretKey;

	}

	/**
	 * 
	 * 解密
	 * 
	 * 
	 * 
	 * @param data
	 * 
	 *            待解密数据
	 * 
	 * @param key
	 * 
	 *            密钥
	 * 
	 * @return byte[] 解密数据
	 * 
	 * @throws Exception
	 * 
	 */

	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {

		// 还原密钥

		Key k = toKey(key);

		/*
		 * 
		 * 实例化 使用PKCS7Padding填充方式，按如下方式实现 Cipher.getInstance(CIPHER_ALGORITHM,
		 * 
		 * "BC");
		 * 
		 */

		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

		// 初始化，设置为解密模式

		cipher.init(Cipher.DECRYPT_MODE, k);

		// 执行操作

		return cipher.doFinal(data);

	}

	/**
	 * 
	 * 加密
	 * 
	 * 
	 * 
	 * @param data
	 * 
	 *            待加密数据
	 * 
	 * @param key
	 * 
	 *            密钥
	 * 
	 * @return byte[] 加密数据
	 * 
	 * @throws Exception
	 * 
	 */

	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {

		// 还原密钥

		Key k = toKey(key);

		/*
		 * 
		 * 实例化 使用PKCS7Padding填充方式，按如下方式实现 Cipher.getInstance(CIPHER_ALGORITHM,
		 * 
		 * "BC");
		 * 
		 */

		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

		// 初始化，设置为加密模式

		cipher.init(Cipher.ENCRYPT_MODE, k);

		// 执行操作

		return cipher.doFinal(data);

	}

	/**
	 * 
	 * 生成密钥 <br>
	 * 
	 * 
	 * 
	 * @return byte[] 二进制密钥
	 * 
	 * @throws Exception
	 * 
	 */

	public static byte[] initKey() throws Exception {

		// 实例化

		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);

		/*
		 * 
		 * AES 要求密钥长度为 128位、192位或 256位,这里用128位
		 * 
		 */

		kg.init(128);

		// 生成秘密密钥

		SecretKey secretKey = kg.generateKey();

		// 获得密钥的二进制编码形式

		return secretKey.getEncoded();

	}

	/**
	 * 
	 * 将byte数组转换成16进制String
	 * 
	 * @param buf
	 * 
	 * @return
	 * 
	 */

	public static String parseByte2HexStr(byte buf[]) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < buf.length; i++) {

			String hex = Integer.toHexString(buf[i] & 0xFF);

			if (hex.length() == 1) {

				hex = '0' + hex;

			}

			sb.append(hex.toUpperCase());

		}

		return sb.toString();

	}

	/**
	 * 
	 * 将16进制String转换为byte数组
	 * 
	 * @param hexStr
	 * 
	 * @return
	 * 
	 */

	public static byte[] parseHexStr2Byte(String hexStr) {

		if (hexStr.length() < 1)

			return null;

		byte[] result = new byte[hexStr.length() / 2];

		for (int i = 0; i < hexStr.length() / 2; i++) {

			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);

			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);

			result[i] = (byte) (high * 16 + low);

		}

		return result;

	}

	private static String showByteArray(byte[] data) {
		if (null == data) {
			return null;
		}
		StringBuilder sb = new StringBuilder("{");
		for (byte b : data) {
			sb.append(b).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("}");
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		// 初始化密钥
		byte[] key = Base64.decodeBase64("Geyd4zScWAbJT9VrWr5cUQ==");
		// byte[] key = Base64.decodeBase64("Geyd4zScWAbJT9VrWr5cUQ82");
		// byte[] key = Base64.decodeBase64("0123456789abcdefghijklmn");
		System.out.println("key：" + showByteArray(key));
		String inputStr = "TCWeb.Bank银联在线支付";
		byte[] originInputData = inputStr.getBytes();
		byte[] encryptInputData = encrypt(originInputData, key);

		String userName = Base64.encodeBase64String(encryptInputData);
		System.out.println("加密后:" + userName);
		// 解密
		byte[] userNameData = AESCoder3.decrypt(Base64.decodeBase64(userName), key);
		userName = new String(userNameData);
		System.out.println("userName:" + userName);

	}
}
