package com.cn.flypay.pageModel.payment.minsheng;

/**
 * 商户提现接口
 * 
 * @author LW
 * 
 */

public class SMZF030 extends CommonSMZF {
	public static String operate_name = "SMZF030";
	
	private String merchantCode;
	
	private String zdlx;
	
	private String zdsj;
	
	private String extend1;
	
	private String extend2;
	
	private String extend3;
	

	@Override
	public String getOperateName() {
		return operate_name;
	}


	public String getMerchantCode() {
		return merchantCode;
	}


	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}


	public String getZdlx() {
		return zdlx;
	}


	public void setZdlx(String zdlx) {
		this.zdlx = zdlx;
	}


	public String getZdsj() {
		return zdsj;
	}


	public void setZdsj(String zdsj) {
		this.zdsj = zdsj;
	}


	public String getExtend1() {
		return extend1;
	}


	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}


	public String getExtend2() {
		return extend2;
	}


	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}


	public String getExtend3() {
		return extend3;
	}


	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}
	
	
	
	
}
