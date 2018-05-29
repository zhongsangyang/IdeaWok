package com.cn.flypay.utils.shenfu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.codec.binary.Base64;

public class BaseEncode {
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		String src = "中华人名";
		String src1 = new String(Base64.encodeBase64(src.getBytes()), "UTF-8");
		String result = URLEncoder.encode(src1, "UTF-8");
		
		System.out.println("src=" +src);
		

		System.out.println("src1=" +src1);
		

		System.out.println("result=" +result);
		
		String result2 = new String(Base64.decodeBase64(result), "UTF-8");  
		
		System.out.println("result2=" +result2);
		
		System.out.print("==" +src.equals(result2));
		
	}
}
