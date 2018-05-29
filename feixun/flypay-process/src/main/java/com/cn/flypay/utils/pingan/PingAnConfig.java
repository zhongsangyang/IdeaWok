package com.cn.flypay.utils.pingan;

public class PingAnConfig {

	/**
	 * 平安文件流_字符集
	 */
	public static String encoding = "GBK";

	/**
	 * FILE01文件上传接口
	 */
	public static String file_01_code = "FILE01";
	/**
	 * FILE02文件上传和下载进度查询
	 */
	public static String file_02_code = "FILE02";
	/**
	 * FILE03文件下载
	 */
	public static String file_03_code = "FILE03";
	/**
	 * FILE04文件上传和下载进度查询
	 */
	public static String file_04_code = "FILE04";

	/**
	 * 批量付款文件提交 [KHKF01]
	 */
	public static String batch_pay_code = "KHKF01";
	/**
	 * 批量付款结果查询[KHKF02]
	 */
	public static String batch_search_code = "KHKF02";
	/**
	 * 单笔付款申请[KHKF03]
	 */
	public static String pay_code = "KHKF03";
	/**
	 * 单笔付款结果查询[KHKF04]
	 */
	public static String search_code = "KHKF04";
	/**
	 * 对账/差错文件查询[KHKF05]
	 */
	public static String statment_code = "KHKF05";
	/**
	 * 文件分割符
	 */
	public static String word_separator = "|::|";
	/**
	 * 提现附言描述
	 */
	public static String tixian_att_desc = "%s提现";
}
