package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
/**
 * 运营商保本费率
 * @author liangchao
 *
 */
@Entity
@Table(name = "sys_organization_protect_rate")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TOrganizationProtectRate implements java.io.Serializable{

	private static final long serialVersionUID = 7444388445136994470L;

	
	private Long id;
	private Torganization organization;
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
	public TOrganizationProtectRate(){}

	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	public Torganization getOrganization() {
		return organization;
	}


	public void setOrganization(Torganization organization) {
		this.organization = organization;
	}

	@Column(name = "type")
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	@Column(name = "baidu_protect_rate", nullable = false, precision = 5, scale = 3)
	public BigDecimal getBaiduProtectRate() {
		return baiduProtectRate;
	}

	public void setBaiduProtectRate(BigDecimal baiduProtectRate) {
		this.baiduProtectRate = baiduProtectRate;
	}

	@Column(name = "qq_protect_rate", nullable = false, precision = 5, scale = 3)
	public BigDecimal getQqProtectRate() {
		return qqProtectRate;
	}

	public void setQqProtectRate(BigDecimal qqProtectRate) {
		this.qqProtectRate = qqProtectRate;
	}

	@Column(name = "weixin_protect_rate", nullable = false, precision = 5, scale = 3)
	public BigDecimal getWeixinProtectRate() {
		return weixinProtectRate;
	}

	public void setWeixinProtectRate(BigDecimal weixinProtectRate) {
		this.weixinProtectRate = weixinProtectRate;
	}

	@Column(name = "zhifubao_protect_rate", nullable = false, precision = 5, scale = 3)
	public BigDecimal getZhifubaoProtectRate() {
		return zhifubaoProtectRate;
	}

	public void setZhifubaoProtectRate(BigDecimal zhifubaoProtectRate) {
		this.zhifubaoProtectRate = zhifubaoProtectRate;
	}

	@Column(name = "yizhifu_protect_rate", nullable = false, precision = 5, scale = 3)
	public BigDecimal getYizhifuProtectRate() {
		return yizhifuProtectRate;
	}

	public void setYizhifuProtectRate(BigDecimal yizhifuProtectRate) {
		this.yizhifuProtectRate = yizhifuProtectRate;
	}
	
	@Column(name = "jingdong_protect_rate", nullable = false, precision = 5, scale = 3)
	public BigDecimal getJingdongProtectRate() {
		return jingdongProtectRate;
	}

	public void setJingdongProtectRate(BigDecimal jingdongProtectRate) {
		this.jingdongProtectRate = jingdongProtectRate;
	}

	@Column(name = "big_yinlian_protect_rate", nullable = false, precision = 5, scale = 3)
	public BigDecimal getBigYinlianProtectRate() {
		return bigYinlianProtectRate;
	}

	public void setBigYinlianProtectRate(BigDecimal bigYinlianProtectRate) {
		this.bigYinlianProtectRate = bigYinlianProtectRate;
	}

	@Column(name = "yinlianzaixian_protect_rate", nullable = false, precision = 5, scale = 3)
	public BigDecimal getYinlianzaixianProtectRate() {
		return yinlianzaixianProtectRate;
	}

	public void setYinlianzaixianProtectRate(BigDecimal yinlianzaixianProtectRate) {
		this.yinlianzaixianProtectRate = yinlianzaixianProtectRate;
	}

	@Column(name = "yinlianjifen_protect_rate", nullable = false, precision = 5, scale = 3)
	public BigDecimal getYinlianjifenProtectRate() {
		return yinlianjifenProtectRate;
	}

	public void setYinlianjifenProtectRate(BigDecimal yinlianjifenProtectRate) {
		this.yinlianjifenProtectRate = yinlianjifenProtectRate;
	}
	
	
	

}
