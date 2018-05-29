package com.cn.flypay.pageModel.payment.pingan;

/**
 * 商户查询接口
 * 
 * @author sunyue
 * 
 */
public class FshowsLiquidationFinanceDownloadbillResponse extends CommonPinganResponse {
	private String download_url;

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

}