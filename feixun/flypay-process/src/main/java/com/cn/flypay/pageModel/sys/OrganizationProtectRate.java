package com.cn.flypay.pageModel.sys;

import java.math.BigDecimal;

/**
 * 运营商保本费率
 * @author liangchao
 *
 */
public class OrganizationProtectRate implements java.io.Serializable {

	private static final long serialVersionUID = -4923796521045407475L;
	private Long id;
	private Long organizationId;
	private String type;
	private BigDecimal	baiduProtectRate;
	private BigDecimal	qqProtectRate;
	private BigDecimal	weixinProtectRate;
	private BigDecimal	zhifubaoProtectRate;
	private BigDecimal	jingdongProtectRate;
	private BigDecimal	bigYinlianProtectRate;
	private BigDecimal	yinlianzaixianProtectRate;
	private BigDecimal	yinlianjifenProtectRate;
	private BigDecimal yizhifuProtectRate;
	
	public OrganizationProtectRate(){}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getBaiduProtectRate() {
		return baiduProtectRate;
	}
	public void setBaiduProtectRate(BigDecimal baiduProtectRate) {
		this.baiduProtectRate = baiduProtectRate;
	}
	public BigDecimal getQqProtectRate() {
		return qqProtectRate;
	}
	public void setQqProtectRate(BigDecimal qqProtectRate) {
		this.qqProtectRate = qqProtectRate;
	}
	public BigDecimal getWeixinProtectRate() {
		return weixinProtectRate;
	}
	public void setWeixinProtectRate(BigDecimal weixinProtectRate) {
		this.weixinProtectRate = weixinProtectRate;
	}
	public BigDecimal getZhifubaoProtectRate() {
		return zhifubaoProtectRate;
	}
	public void setZhifubaoProtectRate(BigDecimal zhifubaoProtectRate) {
		this.zhifubaoProtectRate = zhifubaoProtectRate;
	}
	public BigDecimal getJingdongProtectRate() {
		return jingdongProtectRate;
	}
	public void setJingdongProtectRate(BigDecimal jingdongProtectRate) {
		this.jingdongProtectRate = jingdongProtectRate;
	}
	public BigDecimal getBigYinlianProtectRate() {
		return bigYinlianProtectRate;
	}
	public void setBigYinlianProtectRate(BigDecimal bigYinlianProtectRate) {
		this.bigYinlianProtectRate = bigYinlianProtectRate;
	}
	public BigDecimal getYinlianzaixianProtectRate() {
		return yinlianzaixianProtectRate;
	}
	public void setYinlianzaixianProtectRate(BigDecimal yinlianzaixianProtectRate) {
		this.yinlianzaixianProtectRate = yinlianzaixianProtectRate;
	}
	public BigDecimal getYinlianjifenProtectRate() {
		return yinlianjifenProtectRate;
	}
	public void setYinlianjifenProtectRate(BigDecimal yinlianjifenProtectRate) {
		this.yinlianjifenProtectRate = yinlianjifenProtectRate;
	}
	public BigDecimal getYizhifuProtectRate() {
		return yizhifuProtectRate;
	}
	public void setYizhifuProtectRate(BigDecimal yizhifuProtectRate) {
		this.yizhifuProtectRate = yizhifuProtectRate;
	}

	
	
	
	
	
}
