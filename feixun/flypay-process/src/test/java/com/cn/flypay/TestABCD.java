package com.cn.flypay;

import java.util.Vector;

public class TestABCD {

	Vector all = new Vector();
	String strA = null;
	String strB = null;
	String results = null;
	String passThen = null;

	public void getABCD(String wawa, String passBefore) {

		for (int i = 0; i < wawa.length(); i++) {

			strA = wawa.charAt(i) + "";// 第一i个字母
			strB = wawa.replaceFirst(strA, "");// 去掉后剩下的字母
			passThen = passBefore + strA;// 将要向下一个节点传的字符串

			if (wawa.length() > 2) {
				getABCD(strB, passThen);
			} else if (wawa.length() == 2) {
				results = passThen + strB;// 得到结果
				all.add(results);// 放入集合
			}
		}
	}

	public void see() {// 查看结果
		for (int i = 0; i < all.size(); i++) {
			String result = (String) all.elementAt(i);
			System.out.println("  " + result + "   ----------" + i);
		}
	}

	public static void main(String[] args) {
		TestABCD t = new TestABCD();
		t.getABCD("ABCD", "");
		t.see();// 查看结果
	}
}