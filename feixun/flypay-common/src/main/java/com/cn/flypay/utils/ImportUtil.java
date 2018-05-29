package com.cn.flypay.utils;

public class ImportUtil {

	private static int cardnum = 688;

	private static int idnum = 996;

	private static int cvvnum = 731;

	/**
	 * 加密卡号
	 * 
	 * @param cardNo
	 * @return
	 */
	public static String getEncCardNo(String cardNo) {
//		int length = cardNo.length();
//		String enc = cardNo.substring(length - 3);
//		int done = Integer.parseInt(enc) ^ cardnum;
		// return cardNo.substring(0, length - 3) + done;
		return cardNo;
	}

	/**
	 * 解密卡号
	 * 
	 * @param cardNo
	 * @return
	 */
	public static String getDecCardNo(String cardNo) {
		return getEncCardNo(cardNo);
	}

	/**
	 * 
	 * @param idNo
	 * @return
	 */
	public static String getEncIdNo(String idNo) {
//		int length = idNo.length();
//		String enc = idNo.substring(length - 4, length - 1);
//		int done = Integer.parseInt(enc) ^ idnum;
		// return idNo.substring(0, length - 4) + done+idNo.substring(length -
		// 1);
		return idNo;
	}

	public static String getDecIdNo(String idNo) {
		return getEncIdNo(idNo);
	}

	/**
	 * 
	 * @param idNo
	 * @return
	 */
	public static String getEncCvv(String cvv) {
		// int done =Integer.parseInt(cvv) ^ cvvnum;
		// return String.valueOf(done);
		return cvv;
	}

	public static String getDecCvv(String cvv) {
		return getEncCvv(cvv);
	}

	public static void main(String[] args) {
		String cardNo = "4392260033229528";
		String c = getEncCardNo(cardNo);
		System.out.println(c);
		// System.out.println(getEncCvv("403"));
		// System.out.println(getEncCvv(c));
	}

}
