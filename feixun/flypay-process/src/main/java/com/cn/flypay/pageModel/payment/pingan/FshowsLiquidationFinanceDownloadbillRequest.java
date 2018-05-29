package com.cn.flypay.pageModel.payment.pingan;

/**
 * 对帐单接口
 * 
 * @author sunyue
 * 
 */
public class FshowsLiquidationFinanceDownloadbillRequest {
	private String bill_date;
	private String pay_platform;

	public enum PAY_PLATFORM {
		ALIPAY(1),
		/**
		 * 微信
		 */
		WEIXIN(2),
		/**
		 * 京东
		 */
		JINGDONG(3),
		
		YIZHIFU(4);

		private PAY_PLATFORM(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	}

	public String getBill_date() {
		return bill_date;
	}

	public void setBill_date(String bill_date) {
		this.bill_date = bill_date;
	}

	public String getPay_platform() {
		return pay_platform;
	}

	public void setPay_platform(String pay_platform) {
		this.pay_platform = pay_platform;
	}

}