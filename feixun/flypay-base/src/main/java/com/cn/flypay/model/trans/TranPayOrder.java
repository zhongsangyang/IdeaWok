package com.cn.flypay.model.trans;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.cn.flypay.model.sys.Tchannel;

@Entity
@Table(name = "tran_pay_order")
public class TranPayOrder implements java.io.Serializable {

	private static final long serialVersionUID = -4016818966319707551L;
	private Long id;
	private Tchannel payChannel;
	/**
	 * 支付系统支付流水号
	 */
	private String payNo;
	/**
	 * 业务流水号
	 */
	private String busNo;
	/**
	 * 支付金额
	 */
	private BigDecimal payAmt;
	/**
	 * 实际金额
	 */
	private BigDecimal realAmt;
	private Date payDate;
	private String payFinishDate;
	private Date finishDate;
	private Integer status;
	private String errorCode;
	private String errorInfo;
	private TuserOrder userOrder;
	private BigDecimal avlAccAmt;

	@Id
	@GenericGenerator(name = "poGenerator", strategy = "foreign", parameters = { @Parameter(name = "property", value = "userOrder") })
	@GeneratedValue(generator = "poGenerator")
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "tranPayOrder")
	public TuserOrder getUserOrder() {
		return userOrder;
	}

	public void setUserOrder(TuserOrder userOrder) {
		this.userOrder = userOrder;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_channel_id")
	public Tchannel getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(Tchannel payChannel) {
		this.payChannel = payChannel;
	}

	@Column(name = "pay_no", length = 128)
	public String getPayNo() {
		return this.payNo;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	@Column(name = "bus_no", length = 128)
	public String getBusNo() {
		return busNo;
	}

	public void setBusNo(String busNo) {
		this.busNo = busNo;
	}

	@Column(name = "pay_amt", precision = 12)
	public BigDecimal getPayAmt() {
		return this.payAmt;
	}

	public void setPayAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
	}

	@Column(name = "real_amt", precision = 12)
	public BigDecimal getRealAmt() {
		return this.realAmt;
	}

	public void setRealAmt(BigDecimal realAmt) {
		this.realAmt = realAmt;
	}

	@Column(name = "pay_date", length = 19)
	public Date getPayDate() {
		return this.payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	@Column(name = "finish_date", length = 19)
	public Date getFinishDate() {
		return this.finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	@Column(name = "pay_finish_date")
	public String getPayFinishDate() {
		return payFinishDate;
	}
	
	
	
	@Column(name = "avl_Acc_Amt", precision = 12)
	public BigDecimal getAvlAccAmt() {
		return avlAccAmt;
	}

	public void setAvlAccAmt(BigDecimal avlAccAmt) {
		this.avlAccAmt = avlAccAmt;
	}

	public void setPayFinishDate(String payFinishDate) {
		this.payFinishDate = payFinishDate;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "error_code", length = 64)
	public String getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Column(name = "error_info", length = 512)
	public String getErrorInfo() {
		return this.errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

}