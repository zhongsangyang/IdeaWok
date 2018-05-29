///**
// * Copyright © 2014-2017 TransfarPay.All Rights Reserved.
// */
//package com.cn.flypay.utils.transfar;
//
//import java.security.cert.X509Certificate;
//
//import org.apache.commons.codec.binary.Base64;
//
//import com.itrus.cryptorole.CryptoException;
//import com.itrus.cryptorole.SignatureVerifyException;
//import com.itrus.cryptorole.bc.RecipientBcImpl;
//import com.itrus.cryptorole.bc.SenderBcImpl;
//import com.itrus.cvm.CVM;
//
///**
// * 描述说明
// * 
// * @version V1.0
// * @author huzz
// * @Date 2017年7月15日 下午4:04:34
// * @since JDK 1.7
// */
//public class RSAUtils {
//
//	public static final String CVM_PATH = "src\\resource\\config\\cvm.xml";
//
//	public static final String CER_PATH = "src\\resource\\cafiles\\testca.cer";
//
//	public static byte[] signMessage(String pfxFileName, String keyPassword, byte[] originalMessage) {
//		try {
//			// 签名流程***********************************************
//			// 签名类com.itrus.cryptorole.bc.SenderBcImpl
//			SenderBcImpl send = new SenderBcImpl();
//			// 使用流程先进行密钥初始化
//			send.initCertWithKey(pfxFileName, keyPassword);// pfxFileName PFX证书路径 keyPassword证书密码 。无返回值方法
//			// 初始后调用签名方法
//			byte[] signMsg = send.signMessage(originalMessage); // originalMessage要签名的原文。
//			// 返回签名结果byte[]类型，返回的byte[]类型数据需用再进行编码。
//			return Base64.encodeBase64(signMsg);
//		} catch (Exception e) {
//			System.out.println(e);
//			return null;
//		}
//	}
//
//	public static int verifySignature(byte[] originalMessage, byte[] signedData) {
//		// CVM初始化***********************************************
//		// 首先初始化CVM模块，建议在容器启动时执行CVM初始化
//		// com.itrus.cvm.CVM
//		CVM.config(CVM_PATH); // fileName为cvm.xml文件的路径（注意cvm.xml文件同目录下要用cafiles文件夹，cvm.xml的配置不再累述）
//		// 验证流程*********************************************
//		// 验签类com.itrus.cryptorole.bc.RecipientBcImpl
//		RecipientBcImpl recipient = new RecipientBcImpl();
//		try {
//			X509Certificate userCert = recipient.verifySignature(originalMessage, Base64.decodeBase64(signedData)); // originalMessage原文信息，signedData是通过BASE64解码后的签名值信息org.apache.commons.codec.binary.Base64.decodeBase64(signedData)。如果验证通过返回一个X509Certificate
//			// 证书对象
//			// 验证返回的证书对象
//			return CVM.verifyCertificate(userCert); // userCert为验证的证书对象，返回值int类型 。返回0表示证书有效验证通过。其他值验证失败。
//			// public final int VALID = 0;// 正常
//			// public final int EXPIRED = 1;// 过期但没有被吊销过
//			// public final int REVOKED = 2;// 已吊销但还没有过期
//			// public final int UNKNOWN_ISSUER = 3;// 不是系统所支持的CA所颁发
//			// public final int ILLEGAL_ISSUER = 4;// 非法CA所颁发证书
//			// public final int CRL_UNAVAILABLE = 5;// 没有可供查询的CRL
//			// public final int REVOKED_AND_EXPIRED = 6;// 过期而且被吊销
//		} catch (CryptoException e) {
//			System.out.println("验签异常");
//			return 3;
//		} catch (SignatureVerifyException e) {
//			System.out.println("签名验签异常");
//			return 4;
//		}
//
//	}
//
//	public static void main(String[] args) {
//		String pfxFileName = "E:\\cert\\enterprisetest.pfx";
//		String keyPassword = "123456";
//		String originalMessage = "C2B8D9F72B7C44D9E6Cb5231A49D92D2";
//		byte[] signMessage = signMessage(pfxFileName, keyPassword, originalMessage.getBytes());
//		System.out.println("********加签值***************：" + signMessage.toString());
//		System.out.println("********验签返回值***************：" + verifySignature(originalMessage.getBytes(), signMessage));
//	}
//}
