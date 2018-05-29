package com.cn.flypay.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SysConvert {

	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z' };

	/**
	 * 将十进制的数字转换为指定进制的字符串。
	 * 
	 * @param i
	 *            十进制的数字。
	 * @param system
	 *            指定的进制，常见的2/8/16。
	 * @return 转换后的字符串。
	 */
	public static String numericToString(Long i, int system) {
		long num = i;
		char[] buf = new char[32];
		int charPos = 32;
		while ((num / system) > 0) {
			buf[--charPos] = digits[(int) (num % system)];
			num /= system;
		}
		buf[--charPos] = digits[(int) (num % system)];
		return new String(buf, charPos, (32 - charPos));
	}

	/**
	 * 将其它进制的数字（字符串形式）转换为十进制的数字。
	 * 
	 * @param s
	 *            其它进制的数字（字符串形式）
	 * @param system
	 *            指定的进制，常见的2/8/16。
	 * @return 转换后的数字。
	 */
	public static Long stringToNumeric(String s, int system) {
		char[] buf = new char[s.length()];
		s.getChars(0, s.length(), buf, 0);
		long num = 0;
		for (int i = 0; i < buf.length; i++) {
			for (int j = 0; j < digits.length; j++) {
				if (digits[j] == buf[i]) {
					num += j * Math.pow(system, buf.length - i - 1);
					break;
				}
			}
		}
		return num;
	}

	public static Double convertDoubleDigit(Double number) {
		DecimalFormat df = new DecimalFormat("####.00");
		df.format(number);
		df.setRoundingMode(RoundingMode.FLOOR);
		return Double.parseDouble(df.format(number));
	}

	// 3ZZZZGC
	// 100000000
	public static void main(String[] args) {
		/*
		 * SysConvert de = new SysConvert();
		 * System.out.println(SysConvert.numericToString(100000000l, 35));
		 * System.out.println(de.stringToNumeric("1ZNCNY", 35));
		 */
		System.out.println(convertDoubleDigit(200.339d));

	}
}
