package com.cn.flypay.model.sys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "sys_yiqiang2_merchant_report")
public class TYiQiang2MerchantReport {

	private Long id;
	private Long userId;
	private Long version;

	private String trnCode_01;
	private String respCode_19;
	private String rspMsg_20;
	private String frtMrNO_06;
	private String comMrno_07;
	private String merTrce_05;
	private String trnSeqn_18;

	private String merName_23;
	private String relName_24;
	// private String merStat_25;
	// private String merCity_26;
	// private String merAddr_27;
	private String crtType_28;
	private String crtIdno_29;
	private String mobPhoe_30;
	private String accIdno_31;
	// private String accName_32;
	// private String accType_33;
	// private String bnkName_34;
	// private String bnkCode_35;
	// private String drawFee_41;
	// private String drawRate_42;

	private String conFee_43;
	private String conRate_44;
	// private String conFmax_45;
	// private String conFmin_45;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Version
	@Column(name = "version", nullable = false, precision = 10, scale = 0)
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getComMrno_07() {
		return comMrno_07;
	}

	public void setComMrno_07(String comMrno_07) {
		this.comMrno_07 = comMrno_07;
	}

	public String getTrnCode_01() {
		return trnCode_01;
	}

	public void setTrnCode_01(String trnCode_01) {
		this.trnCode_01 = trnCode_01;
	}

	public String getRespCode_19() {
		return respCode_19;
	}

	public void setRespCode_19(String respCode_19) {
		this.respCode_19 = respCode_19;
	}

	public String getRspMsg_20() {
		return rspMsg_20;
	}

	public void setRspMsg_20(String rspMsg_20) {
		this.rspMsg_20 = rspMsg_20;
	}

	public String getFrtMrNO_06() {
		return frtMrNO_06;
	}

	public void setFrtMrNO_06(String frtMrNO_06) {
		this.frtMrNO_06 = frtMrNO_06;
	}

	public String getMerTrce_05() {
		return merTrce_05;
	}

	public void setMerTrce_05(String merTrce_05) {
		this.merTrce_05 = merTrce_05;
	}

	public String getTrnSeqn_18() {
		return trnSeqn_18;
	}

	public void setTrnSeqn_18(String trnSeqn_18) {
		this.trnSeqn_18 = trnSeqn_18;
	}

	public String getMerName_23() {
		return merName_23;
	}

	public void setMerName_23(String merName_23) {
		this.merName_23 = merName_23;
	}

	public String getRelName_24() {
		return relName_24;
	}

	public void setRelName_24(String relName_24) {
		this.relName_24 = relName_24;
	}

	public String getCrtType_28() {
		return crtType_28;
	}

	public void setCrtType_28(String crtType_28) {
		this.crtType_28 = crtType_28;
	}

	public String getCrtIdno_29() {
		return crtIdno_29;
	}

	public void setCrtIdno_29(String crtIdno_29) {
		this.crtIdno_29 = crtIdno_29;
	}

	public String getMobPhoe_30() {
		return mobPhoe_30;
	}

	public void setMobPhoe_30(String mobPhoe_30) {
		this.mobPhoe_30 = mobPhoe_30;
	}

	public String getAccIdno_31() {
		return accIdno_31;
	}

	public void setAccIdno_31(String accIdno_31) {
		this.accIdno_31 = accIdno_31;
	}

	public String getConFee_43() {
		return conFee_43;
	}

	public void setConFee_43(String conFee_43) {
		this.conFee_43 = conFee_43;
	}

	public String getConRate_44() {
		return conRate_44;
	}

	public void setConRate_44(String conRate_44) {
		this.conRate_44 = conRate_44;
	}

}
