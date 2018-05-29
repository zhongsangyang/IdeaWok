package com.cn.flypay.utils.yiqiang;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSAEncrypt {

	public static final String KEY_ALGORITHMS = "RSA";
	/** */
	/**
	 * 签名算法
	 */
	//public static final String SIGNATURE_ALGORITHMS = "SHA1WithRSA";//SHA1WithRSA 或者 MD5withRSA

	/** */
	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;

	/** */
	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;

	/** 
	 * 字节数据转字符串专用集合 
	 */
	private static final char[] HEX_CHAR =
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/** 
	 * 从文件中输入流中加载公钥 
	 * @param in 公钥输入流 
	 * @throws Exception 加载公钥时产生的异常 
	 */
	public static RSAPublicKey loadPublicKey(InputStream in) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			return loadPublicKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("公钥数据流读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥输入流为空");
		}
	}

	/** 
	 * 从字符串中加载公钥 
	 * @param publicKeyStr 公钥数据字符串 
	 * @throws Exception 加载公钥时产生的异常 
	 */
	public static RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHMS);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (IOException e) {
			throw new Exception("公钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	/** 
	 * 从文件中加载私钥 
	 * @param keyFileName 私钥文件名 
	 * @return 是否成功 
	 * @throws Exception  
	 */
	public static RSAPrivateKey loadPrivateKey(InputStream in) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			return loadPrivateKey(sb.toString());
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		}
	}

	public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHMS);
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (IOException e) {
			throw new Exception("私钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	/** 
	* RSA签名 
	* @param content 待签名数据 
	* @param privateKey 商户私钥 
	* @param encode 字符集编码 
	* @return 签名值 
	*/
	public static String sign(String content, RSAPrivateKey privateKey, String encode,String signature_algorithm)
			throws Exception {

		Signature signature = Signature.getInstance(signature_algorithm);

		signature.initSign(privateKey);
		signature.update(content.getBytes(encode));

		byte[] signed = signature.sign();

		return (new BASE64Encoder()).encodeBuffer(signed);

	}

	public static String sign(String content, RSAPrivateKey privateKey,String signature_algorithm) throws Exception {

		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(signature_algorithm);
		signature.initSign(privateKey);
		signature.update(content.getBytes("utf-8"));
		byte[] signed = signature.sign();
		return (new BASE64Encoder()).encodeBuffer(signed);

	}

	/** 
	* RSA验签名检查 
	* @param content 待签名数据 
	* @param sign 签名值 
	* @param publicKey 分配给开发商公钥 
	* @param encode 字符集编码 
	* @return 布尔值 
	*/
	public static boolean doCheck(String content, String sign, RSAPublicKey publicKey,
			String encode,String signature_algorithm) throws Exception {

		Signature signature = Signature.getInstance(signature_algorithm);

		signature.initVerify(publicKey);
		signature.update(content.getBytes(encode));

		return signature.verify(new BASE64Decoder().decodeBuffer(sign));

	}

	public static boolean doCheck(String content, String sign, RSAPublicKey publicKey,String signature_algorithm)
			throws Exception {

		java.security.Signature signature =
				java.security.Signature.getInstance(signature_algorithm);

		signature.initVerify(publicKey);
		signature.update(content.getBytes());

		return signature.verify(new BASE64Decoder().decodeBuffer(sign));

	}

	/** 
	 * 加密过程 
	 * @param publicKey 公钥 
	 * @param plainTextData 明文数据 
	 * @return 
	 * @throws Exception 加密过程中的异常信息 
	 */
	public static String encrypt(RSAPublicKey publicKey, byte[] data) throws Exception {
		if (publicKey == null) {
			throw new Exception("加密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHMS);
			cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			//cipher = Cipher.getInstance(KEY_ALGORITHMS, new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_ENCRYPT_BLOCK;
			}
			byte[] encryptedData = out.toByteArray();
			out.close();
			return new BASE64Encoder().encodeBuffer(encryptedData);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}

	/** 
	 * 解密过程 
	 * @param privateKey 私钥 
	 * @param cipherData 密文数据 
	 * @return 明文 
	 * @throws Exception 解密过程中的异常信息 
	 */
	public static byte[] decrypt(RSAPrivateKey privateKey, byte[] encryptedData) throws Exception {
		if (privateKey == null) {
			throw new Exception("解密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHMS);
			cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			//cipher = Cipher.getInstance(KEY_ALGORITHMS, new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
					cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_DECRYPT_BLOCK;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();
			return decryptedData;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}

	/** 
	 * 字节数据转十六进制字符串 
	 * @param data 输入数据 
	 * @return 十六进制内容 
	 */
	public static String byteArrayToString(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			//取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移  
			stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
			//取出字节的低四位 作为索引得到相应的十六进制标识符  
			stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
			if (i < data.length - 1) {
				stringBuilder.append(' ');
			}
		}
		return stringBuilder.toString();
	}

	public static String byteToHex(byte[] source) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < source.length; i++) {
			String shaHex = Integer.toHexString(source[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexString.append(0);
			}
			hexString.append(shaHex);
		}
		return hexString.toString();
	}

	public static byte[] hexToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}