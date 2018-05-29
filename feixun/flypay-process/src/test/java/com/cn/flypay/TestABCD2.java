package com.cn.flypay;

import java.util.Vector;

public class TestABCD2 {

	Vector all = new Vector();
	String strA = null;
	String strB = null;
	String results = null;
	String passThen = null;

	public void getABCD(String wawa, String passBefore) {

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < wawa.length(); i++) {
			strA = wawa.charAt(i) + "";
			sb.append(strA);
			strB = wawa.replaceFirst(strA, "");
			for (int j = 0; j < strB.length(); j++) {
				strA = strB.charAt(j) + "";
				sb.append(strA);
				strB = strB.replaceFirst(strA, "");
				for (int k = 0; k < strB.length(); k++) {
					strA = strB.charAt(k) + "";
					sb.append(strA);
					strB = strB.replaceFirst(strA, "");
					for (int n = 0; n < strB.length(); n++) {
						strA = strB.charAt(n) + "";
						sb.append(strA);
						strB = strB.replaceFirst(strA, "");
						System.out.println(sb.toString());
						sb.delete(0, 4);
					}
				}
			}
		}
	}

	public void see() {// 查看结果
		/*
		 * for (int i = 0; i < all.size(); i++) { String result = (String)
		 * all.elementAt(i); System.out.println("  " + result + "   ----------"
		 * + i); }
		 */
	}

	public static void main(String[] args) {
		TestABCD2 t = new TestABCD2();
		t.getABCD("ABCD", "");
		t.see();// 查看结果
	}
}