package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sys_channel_t0_tixian")
public class TchannelT0Tixian implements java.io.Serializable {
	private static final long serialVersionUID = 1129504462667819772L;
	private Long id;
	private Integer version;
	private String orderNum;
	private String busNum;
	private BigDecimal amt;
	private BigDecimal drawFee;
	private BigDecimal tradeFee;
	private Tchannel channel;
	/* 100 成功 200 失败 300 进行中 */
	private Integer status;
	/* 100 成功 200 失败 300 未对账 */
	private Integer sltStatus;
	private String errorCode;
	private String errorDesc;
	private String cardNo;
	private String cardHolder;
	private Date createDate;
	private Date finishDate;
	private String creator;

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "order_num", length = 64)
	public String getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	@Column(name = "bus_num", length = 64)
	public String getBusNum() {
		return this.busNum;
	}

	public void setBusNum(String busNum) {
		this.busNum = busNum;
	}

	@Column(name = "amt", precision = 15)
	public BigDecimal getAmt() {
		return this.amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	@Column(name = "draw_Fee", precision = 15)
	public BigDecimal getDrawFee() {
		return this.drawFee;
	}

	public void setDrawFee(BigDecimal drawFee) {
		this.drawFee = drawFee;
	}

	@Column(name = "trade_Fee", precision = 15)
	public BigDecimal getTradeFee() {
		return this.tradeFee;
	}

	public void setTradeFee(BigDecimal tradeFee) {
		this.tradeFee = tradeFee;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chl_id")
	public Tchannel getChannel() {
		return channel;
	}

	public void setChannel(Tchannel channel) {
		this.channel = channel;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "slt_status")
	public Integer getSltStatus() {
		return this.sltStatus;
	}

	public void setSltStatus(Integer sltStatus) {
		this.sltStatus = sltStatus;
	}

	@Column(name = "error_code", length = 64)
	public String getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Column(name = "error_desc", length = 256)
	public String getErrorDesc() {
		return this.errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	@Column(name = "card_no", length = 64)
	public String getCardNo() {
		return this.cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	@Column(name = "card_holder", length = 64)
	public String getCardHolder() {
		return this.cardHolder;
	}

	public void setCardHolder(String cardHolder) {
		this.cardHolder = cardHolder;
	}

	@Column(name = "create_date", length = 19)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name = "finish_date", length = 19)
	public Date getFinishDate() {
		return this.finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	@Column(name = "creator", length = 64)
	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

}