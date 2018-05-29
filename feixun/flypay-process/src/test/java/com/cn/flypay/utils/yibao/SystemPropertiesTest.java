package com.cn.flypay.utils.yibao;

import com.cn.flypay.utils.transfar.DateUtils;
import com.google.gson.Gson;

public class SystemPropertiesTest {
	public static void main(String[] args) {
		char c='a';
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<26;i++){
			sb.append(c);
			c++;
//			System.out.print(c++);
			
		}
		String strlist=sb.toString();
		System.out.println(strlist);
		
		for(int i=0;i<strlist.length();i++){
			char c1=strlist.charAt(i);
			if(Character.isLetter(c1)){
				sb.append(Character.toUpperCase(c1));
			}else if(Character.isUpperCase(c1)){
				sb.append(Character.toUpperCase(c1));
			}
		}
		System.out.println(sb.toString());
		
		
		float[] height = new float[20];
		java.util.Arrays.fill( height, 175.5f );
		for (float f : height) {
			System.out.println(f);
		}
		String sdate=DateUtils.getPreDate("2018-05-09 9:56","yyyy-mm-dd HH:mm");
		String sday=DateUtils.getCurrentDate("yyyy-mm-dd HH:mm");
		System.out.println(sdate+">>>>>>>>"+sday);
//		Gson 的序列化与序列化操作
		Gson gson=new Gson();
		TestGsonClass testGsonClas=new TestGsonClass("zsy","18721010781@163.com",21,true);
		String strJson=gson.toJson(testGsonClas);
		System.out.println(strJson.replaceAll("}", ","));
		String str1="\"other\"";
		TestGsonClass testGsonClass1=gson.fromJson(strJson, TestGsonClass.class);
		System.out.println(testGsonClass1);
	}
}
