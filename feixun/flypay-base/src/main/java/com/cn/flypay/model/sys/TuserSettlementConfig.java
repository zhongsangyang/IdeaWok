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

/**
 * SysUserSettlementConfig entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_user_settlement_config")
public class TuserSettlementConfig implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -8208392412730791564L;
	private Long id;
	private BigDecimal t0Fee;
	private BigDecimal t1Fee;
	private BigDecimal minT0Amt;
	private BigDecimal maxT0Amt;
	private BigDecimal minT1Amt;
	private BigDecimal maxT1Amt;
	private BigDecimal rabaleFee;
	private BigDecimal maxRabaleAmt;
	private BigDecimal minRabaleAmt;
	private BigDecimal maxTodayOutAmt;
	private BigDecimal inputFee;

	private BigDecimal inputFeeAlipay;
	private BigDecimal inputFeeWeixin;
	private BigDecimal inputFeeYinlian;

	private BigDecimal inputFeeJingDong;
	private BigDecimal inputFeeBaidu;
	private BigDecimal inputFeeYizhifu;
	private BigDecimal inputFeeYinLianzhifu;

	private BigDecimal inputFeeD0Alipay;
	private BigDecimal inputFeeD0Weixin;
	private BigDecimal inputFeeD0Yinlian;
	private BigDecimal inputFeeD0JingDong;
	private BigDecimal inputFeeD0Baidu;
	private BigDecimal inputFeeD0Yizhifu;
	private BigDecimal inputFeeD0YinLianzhifu;

	private BigDecimal inputFeeBigAlipay;
	private BigDecimal inputFeeBigWeixin;
	private BigDecimal inputFeeBigYinlian;
	private BigDecimal inputFeeBigJingDong;
	private BigDecimal inputFeeBigBaidu;
	private BigDecimal inputFeeBigYizhifu;
	private BigDecimal inputFeeBigYinLianzhifu;
	private BigDecimal inputFeeBigQQzhifu;
	private BigDecimal inputFeeD0BigAlipay;
	private BigDecimal inputFeeD0BigWeixin;
	private BigDecimal inputFeeD0BigYinlian;
	private BigDecimal inputFeeD0BigJingDong;
	private BigDecimal inputFeeD0BigBaidu;
	private BigDecimal inputFeeD0BigYizhifu;
	private BigDecimal inputFeeD0BigYinLianzhifu;
	private BigDecimal inputFeeD0BigQQzhifu;

	private BigDecimal inputFeeZtAlipay;
	private BigDecimal inputFeeD0ZtAlipay;
	private BigDecimal inputFeeZtWeixin;
	private BigDecimal inputFeeD0ZtWeixin;
	private BigDecimal inputFeeZtQQzhifu;
	private BigDecimal inputFeeD0ZtQQzhifu;
	private BigDecimal inputFeeZtYinlian;
	private BigDecimal inputFeeD0ZtYinlian;

	private BigDecimal inputFeeZtYinlianJf;
	private BigDecimal inputFeeD0ZtYinlianJf;

	/*
	 * 哲扬费率
	 */
	private BigDecimal inputFeeZtYinlianJfZY = new BigDecimal(0.005300);
	private BigDecimal inputFeeD0ZtYinlianJfZY = new BigDecimal(0.005800);

	/**
	 * 交易流量分润比例
	 */
	private BigDecimal shareFee;

	private Tuser user;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public Tuser getUser() {
		return this.user;
	}

	public void setUser(Tuser user) {
		this.user = user;
	}

	@Column(name = "t0_fee", precision = 12)
	public BigDecimal getT0Fee() {
		return t0Fee;
	}

	public void setT0Fee(BigDecimal t0Fee) {
		this.t0Fee = t0Fee;
	}

	@Column(name = "t1_fee", precision = 12)
	public BigDecimal getT1Fee() {
		return this.t1Fee;
	}

	public void setT1Fee(BigDecimal t1Fee) {
		this.t1Fee = t1Fee;
	}

	@Column(name = "min_t0_amt", precision = 12)
	public BigDecimal getMinT0Amt() {
		return this.minT0Amt;
	}

	public void setMinT0Amt(BigDecimal minT0Amt) {
		this.minT0Amt = minT0Amt;
	}

	@Column(name = "max_t0_amt", precision = 12)
	public BigDecimal getMaxT0Amt() {
		return this.maxT0Amt;
	}

	public void setMaxT0Amt(BigDecimal maxT0Amt) {
		this.maxT0Amt = maxT0Amt;
	}

	@Column(name = "max_Today_Out_Amt", precision = 12)
	public BigDecimal getMaxTodayOutAmt() {
		return maxTodayOutAmt;
	}

	public void setMaxTodayOutAmt(BigDecimal maxTodayOutAmt) {
		this.maxTodayOutAmt = maxTodayOutAmt;
	}

	@Column(name = "min_t1_amt", precision = 12)
	public BigDecimal getMinT1Amt() {
		return this.minT1Amt;
	}

	public void setMinT1Amt(BigDecimal minT1Amt) {
		this.minT1Amt = minT1Amt;
	}

	@Column(name = "max_t1_amt", precision = 12)
	public BigDecimal getMaxT1Amt() {
		return this.maxT1Amt;
	}

	public void setMaxT1Amt(BigDecimal maxT1Amt) {
		this.maxT1Amt = maxT1Amt;
	}

	@Column(name = "rabale_fee", precision = 12)
	public BigDecimal getRabaleFee() {
		return this.rabaleFee;
	}

	public void setRabaleFee(BigDecimal rabaleFee) {
		this.rabaleFee = rabaleFee;
	}

	@Column(name = "max_rabale_amt", precision = 12)
	public BigDecimal getMaxRabaleAmt() {
		return this.maxRabaleAmt;
	}

	public void setMaxRabaleAmt(BigDecimal maxRabaleAmt) {
		this.maxRabaleAmt = maxRabaleAmt;
	}

	@Column(name = "min_rabale_amt", precision = 12)
	public BigDecimal getMinRabaleAmt() {
		return this.minRabaleAmt;
	}

	public void setMinRabaleAmt(BigDecimal minRabaleAmt) {
		this.minRabaleAmt = minRabaleAmt;
	}

	@Column(name = "input_fee", precision = 12)
	public BigDecimal getInputFee() {
		return inputFee;
	}

	public void setInputFee(BigDecimal inputFee) {
		this.inputFee = inputFee;
	}

	@Column(name = "share_fee", precision = 12)
	public BigDecimal getShareFee() {
		return shareFee;
	}

	public void setShareFee(BigDecimal shareFee) {
		this.shareFee = shareFee;
	}

	@Column(name = "input_fee_alipay", precision = 12)
	public BigDecimal getInputFeeAlipay() {
		return inputFeeAlipay;
	}

	public void setInputFeeAlipay(BigDecimal inputFeeAlipay) {
		this.inputFeeAlipay = inputFeeAlipay;
	}

	@Column(name = "input_fee_weixin", precision = 12)
	public BigDecimal getInputFeeWeixin() {
		return inputFeeWeixin;
	}

	public void setInputFeeWeixin(BigDecimal inputFeeWeixin) {
		this.inputFeeWeixin = inputFeeWeixin;
	}

	@Column(name = "input_fee_yinlian", precision = 12)
	public BigDecimal getInputFeeYinlian() {
		return inputFeeYinlian;
	}

	public void setInputFeeYinlian(BigDecimal inputFeeYinlian) {
		this.inputFeeYinlian = inputFeeYinlian;
	}

	@Column(name = "input_fee_jingdong", precision = 12)
	public BigDecimal getInputFeeJingDong() {
		return inputFeeJingDong;
	}

	public void setInputFeeJingDong(BigDecimal inputFeeJingDong) {
		this.inputFeeJingDong = inputFeeJingDong;
	}

	@Column(name = "input_fee_baidu", precision = 12)
	public BigDecimal getInputFeeBaidu() {
		return inputFeeBaidu;
	}

	public void setInputFeeBaidu(BigDecimal inputFeeBaidu) {
		this.inputFeeBaidu = inputFeeBaidu;
	}

	@Column(name = "input_fee_Yizhifu", precision = 12)
	public BigDecimal getInputFeeYizhifu() {
		return inputFeeYizhifu;
	}

	public void setInputFeeYizhifu(BigDecimal inputFeeYizhifu) {
		this.inputFeeYizhifu = inputFeeYizhifu;
	}

	@Column(name = "inputFeeYinLianzhifu", precision = 12)
	public BigDecimal getInputFeeYinLianzhifu() {
		return inputFeeYinLianzhifu;
	}

	public void setInputFeeYinLianzhifu(BigDecimal inputFeeYinLianzhifu) {
		this.inputFeeYinLianzhifu = inputFeeYinLianzhifu;
	}

	@Column(name = "input_fee_d0_alipay", precision = 12)
	public BigDecimal getInputFeeD0Alipay() {
		return inputFeeD0Alipay;
	}

	public void setInputFeeD0Alipay(BigDecimal inputFeeD0Alipay) {
		this.inputFeeD0Alipay = inputFeeD0Alipay;
	}

	@Column(name = "input_fee_d0_weixin", precision = 12)
	public BigDecimal getInputFeeD0Weixin() {
		return inputFeeD0Weixin;
	}

	public void setInputFeeD0Weixin(BigDecimal inputFeeD0Weixin) {
		this.inputFeeD0Weixin = inputFeeD0Weixin;
	}

	@Column(name = "input_fee_d0_yinlian", precision = 12)
	public BigDecimal getInputFeeD0Yinlian() {
		return inputFeeD0Yinlian;
	}

	public void setInputFeeD0Yinlian(BigDecimal inputFeeD0Yinlian) {
		this.inputFeeD0Yinlian = inputFeeD0Yinlian;
	}

	@Column(name = "input_fee_d0_jingdong", precision = 12)
	public BigDecimal getInputFeeD0JingDong() {
		return inputFeeD0JingDong;
	}

	public void setInputFeeD0JingDong(BigDecimal inputFeeD0JingDong) {
		this.inputFeeD0JingDong = inputFeeD0JingDong;
	}

	@Column(name = "input_fee_d0_baidu", precision = 12)
	public BigDecimal getInputFeeD0Baidu() {
		return inputFeeD0Baidu;
	}

	public void setInputFeeD0Baidu(BigDecimal inputFeeD0Baidu) {
		this.inputFeeD0Baidu = inputFeeD0Baidu;
	}

	@Column(name = "input_fee_d0_Yizhifu", precision = 12)
	public BigDecimal getInputFeeD0Yizhifu() {
		return inputFeeD0Yizhifu;
	}

	public void setInputFeeD0Yizhifu(BigDecimal inputFeeD0Yizhifu) {
		this.inputFeeD0Yizhifu = inputFeeD0Yizhifu;
	}

	@Column(name = "input_fee_d0_YinLianzhifu", precision = 12)
	public BigDecimal getInputFeeD0YinLianzhifu() {
		return inputFeeD0YinLianzhifu;
	}

	public void setInputFeeD0YinLianzhifu(BigDecimal inputFeeD0YinLianzhifu) {
		this.inputFeeD0YinLianzhifu = inputFeeD0YinLianzhifu;
	}

	@Column(name = "input_fee_big_Alipay", precision = 12)
	public BigDecimal getInputFeeBigAlipay() {
		return inputFeeBigAlipay;
	}

	public void setInputFeeBigAlipay(BigDecimal inputFeeBigAlipay) {
		this.inputFeeBigAlipay = inputFeeBigAlipay;
	}

	@Column(name = "input_fee_big_weixin", precision = 12)
	public BigDecimal getInputFeeBigWeixin() {
		return inputFeeBigWeixin;
	}

	public void setInputFeeBigWeixin(BigDecimal inputFeeBigWeixin) {
		this.inputFeeBigWeixin = inputFeeBigWeixin;
	}

	@Column(name = "input_fee_big_yinlian", precision = 12)
	public BigDecimal getInputFeeBigYinlian() {
		return inputFeeBigYinlian;
	}

	public void setInputFeeBigYinlian(BigDecimal inputFeeBigYinlian) {
		this.inputFeeBigYinlian = inputFeeBigYinlian;
	}

	@Column(name = "input_fee_big_jingdong", precision = 12)
	public BigDecimal getInputFeeBigJingDong() {
		return inputFeeBigJingDong;
	}

	public void setInputFeeBigJingDong(BigDecimal inputFeeBigJingDong) {
		this.inputFeeBigJingDong = inputFeeBigJingDong;
	}

	@Column(name = "input_fee_big_baidu", precision = 12)
	public BigDecimal getInputFeeBigBaidu() {
		return inputFeeBigBaidu;
	}

	public void setInputFeeBigBaidu(BigDecimal inputFeeBigBaidu) {
		this.inputFeeBigBaidu = inputFeeBigBaidu;
	}

	@Column(name = "input_fee_big_Yizhifu", precision = 12)
	public BigDecimal getInputFeeBigYizhifu() {
		return inputFeeBigYizhifu;
	}

	public void setInputFeeBigYizhifu(BigDecimal inputFeeBigYizhifu) {
		this.inputFeeBigYizhifu = inputFeeBigYizhifu;
	}

	@Column(name = "input_fee_big_YinLianzhifu", precision = 12)
	public BigDecimal getInputFeeBigYinLianzhifu() {
		return inputFeeBigYinLianzhifu;
	}

	public void setInputFeeBigYinLianzhifu(BigDecimal inputFeeBigYinLianzhifu) {
		this.inputFeeBigYinLianzhifu = inputFeeBigYinLianzhifu;
	}

	@Column(name = "input_fee_d0_big_alipay", precision = 12)
	public BigDecimal getInputFeeD0BigAlipay() {
		return inputFeeD0BigAlipay;
	}

	public void setInputFeeD0BigAlipay(BigDecimal inputFeeD0BigAlipay) {
		this.inputFeeD0BigAlipay = inputFeeD0BigAlipay;
	}

	@Column(name = "input_fee_d0_big_weixin", precision = 12)
	public BigDecimal getInputFeeD0BigWeixin() {
		return inputFeeD0BigWeixin;
	}

	public void setInputFeeD0BigWeixin(BigDecimal inputFeeD0BigWeixin) {
		this.inputFeeD0BigWeixin = inputFeeD0BigWeixin;
	}

	@Column(name = "input_fee_d0_big_yinlian", precision = 12)
	public BigDecimal getInputFeeD0BigYinlian() {
		return inputFeeD0BigYinlian;
	}

	public void setInputFeeD0BigYinlian(BigDecimal inputFeeD0BigYinlian) {
		this.inputFeeD0BigYinlian = inputFeeD0BigYinlian;
	}

	@Column(name = "input_fee_d0_big_jingdong", precision = 12)
	public BigDecimal getInputFeeD0BigJingDong() {
		return inputFeeD0BigJingDong;
	}

	public void setInputFeeD0BigJingDong(BigDecimal inputFeeD0BigJingDong) {
		this.inputFeeD0BigJingDong = inputFeeD0BigJingDong;
	}

	@Column(name = "input_fee_d0_big_baidu", precision = 12)
	public BigDecimal getInputFeeD0BigBaidu() {
		return inputFeeD0BigBaidu;
	}

	public void setInputFeeD0BigBaidu(BigDecimal inputFeeD0BigBaidu) {
		this.inputFeeD0BigBaidu = inputFeeD0BigBaidu;
	}

	@Column(name = "input_fee_d0_big_Yizhifu", precision = 12)
	public BigDecimal getInputFeeD0BigYizhifu() {
		return inputFeeD0BigYizhifu;
	}

	public void setInputFeeD0BigYizhifu(BigDecimal inputFeeD0BigYizhifu) {
		this.inputFeeD0BigYizhifu = inputFeeD0BigYizhifu;
	}

	@Column(name = "input_fee_d0_big_YinLianzhifu", precision = 12)
	public BigDecimal getInputFeeD0BigYinLianzhifu() {
		return inputFeeD0BigYinLianzhifu;
	}

	public void setInputFeeD0BigYinLianzhifu(BigDecimal inputFeeD0BigYinLianzhifu) {
		this.inputFeeD0BigYinLianzhifu = inputFeeD0BigYinLianzhifu;
	}

	@Column(name = "input_fee_big_QQzhifu", precision = 12)
	public BigDecimal getInputFeeBigQQzhifu() {
		return inputFeeBigQQzhifu;
	}

	public void setInputFeeBigQQzhifu(BigDecimal inputFeeBigQQzhifu) {
		this.inputFeeBigQQzhifu = inputFeeBigQQzhifu;
	}

	@Column(name = "input_fee_d0_big_QQzhifu", precision = 12)
	public BigDecimal getInputFeeD0BigQQzhifu() {
		return inputFeeD0BigQQzhifu;
	}

	public void setInputFeeD0BigQQzhifu(BigDecimal inputFeeD0BigQQzhifu) {
		this.inputFeeD0BigQQzhifu = inputFeeD0BigQQzhifu;
	}

	@Column(name = "input_fee_zt_alipay", precision = 12)
	public BigDecimal getInputFeeZtAlipay() {
		return inputFeeZtAlipay;
	}

	public void setInputFeeZtAlipay(BigDecimal inputFeeZtAlipay) {
		this.inputFeeZtAlipay = inputFeeZtAlipay;
	}

	@Column(name = "input_fee_d0_zt_alipay", precision = 12)
	public BigDecimal getInputFeeD0ZtAlipay() {
		return inputFeeD0ZtAlipay;
	}

	public void setInputFeeD0ZtAlipay(BigDecimal inputFeeD0ZtAlipay) {
		this.inputFeeD0ZtAlipay = inputFeeD0ZtAlipay;
	}

	@Column(name = "input_fee_zt_weixin", precision = 12)
	public BigDecimal getInputFeeZtWeixin() {
		return inputFeeZtWeixin;
	}

	public void setInputFeeZtWeixin(BigDecimal inputFeeZtWeixin) {
		this.inputFeeZtWeixin = inputFeeZtWeixin;
	}

	@Column(name = "input_fee_d0_zt_weixin", precision = 12)
	public BigDecimal getInputFeeD0ZtWeixin() {
		return inputFeeD0ZtWeixin;
	}

	public void setInputFeeD0ZtWeixin(BigDecimal inputFeeD0ZtWeixin) {
		this.inputFeeD0ZtWeixin = inputFeeD0ZtWeixin;
	}

	@Column(name = "input_fee_zt_QQzhifu", precision = 12)
	public BigDecimal getInputFeeZtQQzhifu() {
		return inputFeeZtQQzhifu;
	}

	public void setInputFeeZtQQzhifu(BigDecimal inputFeeZtQQzhifu) {
		this.inputFeeZtQQzhifu = inputFeeZtQQzhifu;
	}

	@Column(name = "input_fee_d0_zt_QQzhifu", precision = 12)
	public BigDecimal getInputFeeD0ZtQQzhifu() {
		return inputFeeD0ZtQQzhifu;
	}

	public void setInputFeeD0ZtQQzhifu(BigDecimal inputFeeD0ZtQQzhifu) {
		this.inputFeeD0ZtQQzhifu = inputFeeD0ZtQQzhifu;
	}

	@Column(name = "input_fee_zt_yinlian", precision = 12)
	public BigDecimal getInputFeeZtYinlian() {
		return inputFeeZtYinlian;
	}

	public void setInputFeeZtYinlian(BigDecimal inputFeeZtYinlian) {
		this.inputFeeZtYinlian = inputFeeZtYinlian;
	}

	@Column(name = "input_fee_d0_zt_yinlian", precision = 12)
	public BigDecimal getInputFeeD0ZtYinlian() {
		return inputFeeD0ZtYinlian;
	}

	public void setInputFeeD0ZtYinlian(BigDecimal inputFeeD0ZtYinlian) {
		this.inputFeeD0ZtYinlian = inputFeeD0ZtYinlian;
	}

	@Column(name = "input_fee_zt_yinlianjf", precision = 12)
	public BigDecimal getInputFeeZtYinlianJf() {
		return inputFeeZtYinlianJf;
	}

	public void setInputFeeZtYinlianJf(BigDecimal inputFeeZtYinlianJf) {
		this.inputFeeZtYinlianJf = inputFeeZtYinlianJf;
	}

	@Column(name = "input_fee_d0_zt_yinlianjf", precision = 12)
	public BigDecimal getInputFeeD0ZtYinlianJf() {
		return inputFeeD0ZtYinlianJf;
	}

	public void setInputFeeD0ZtYinlianJf(BigDecimal inputFeeD0ZtYinlianJf) {
		this.inputFeeD0ZtYinlianJf = inputFeeD0ZtYinlianJf;
	}

	@Column(name = "input_fee_zt_yinlianjfZY", precision = 12)
	public BigDecimal getInputFeeZtYinlianJfZY() {
		return inputFeeZtYinlianJfZY;
	}

	public void setInputFeeZtYinlianJfZY(BigDecimal inputFeeZtYinlianJfZY) {
		this.inputFeeZtYinlianJfZY = inputFeeZtYinlianJfZY;
	}

	@Column(name = "input_fee_d0_zt_yinlianjfZY", precision = 12)
	public BigDecimal getInputFeeD0ZtYinlianJfZY() {
		return inputFeeD0ZtYinlianJfZY;
	}

	public void setInputFeeD0ZtYinlianJfZY(BigDecimal inputFeeD0ZtYinlianJfZY) {
		this.inputFeeD0ZtYinlianJfZY = inputFeeD0ZtYinlianJfZY;
	}

}