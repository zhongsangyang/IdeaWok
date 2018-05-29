package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 新闻咨询图片信息
 * @author liangchao
 *
 */
@Entity
@Table(name = "sys_information_photo")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TsysInformationPhoto implements java.io.Serializable{


	private static final long serialVersionUID = -5219959770051070648L;
	
	private Long id;
	private Integer status;
	private String text1;
	private String photo1_url;
	
	private String text2;
	private String photo2_url;
	
	private String text3;
	private String photo3_url;
	
	private String text4;
	private String photo4_url;
	
	private String text5;
	private String photo5_url;
	
	private String text6;
	private String photo6_url;
	
	private String text7;
	private String photo7_url;

	private String text8;
	private String photo8_url;
	
	private String text9;
	private String photo9_url;
	
	private String agentId;
	
	private Date createTime;
	
	private Long readNumber;
	
	private String type;
	
	private String showLocation;
	
	
	public TsysInformationPhoto(){}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "text_1")
	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}
	
	@Column(name = "photo1_url")
	public String getPhoto1_url() {
		return photo1_url;
	}

	public void setPhoto1_url(String photo1_url) {
		this.photo1_url = photo1_url;
	}

	@Column(name = "text_2")
	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}

	@Column(name = "photo2_url")
	public String getPhoto2_url() {
		return photo2_url;
	}

	public void setPhoto2_url(String photo2_url) {
		this.photo2_url = photo2_url;
	}
	
	@Column(name = "text_3")
	public String getText3() {
		return text3;
	}

	public void setText3(String text3) {
		this.text3 = text3;
	}

	@Column(name = "photo3_url")
	public String getPhoto3_url() {
		return photo3_url;
	}

	public void setPhoto3_url(String photo3_url) {
		this.photo3_url = photo3_url;
	}

	@Column(name = "text_4")
	public String getText4() {
		return text4;
	}

	public void setText4(String text4) {
		this.text4 = text4;
	}

	@Column(name = "photo4_url")
	public String getPhoto4_url() {
		return photo4_url;
	}

	public void setPhoto4_url(String photo4_url) {
		this.photo4_url = photo4_url;
	}

	@Column(name = "text_5")
	public String getText5() {
		return text5;
	}

	public void setText5(String text5) {
		this.text5 = text5;
	}

	@Column(name = "photo5_url")
	public String getPhoto5_url() {
		return photo5_url;
	}

	public void setPhoto5_url(String photo5_url) {
		this.photo5_url = photo5_url;
	}

	@Column(name = "text_6")
	public String getText6() {
		return text6;
	}

	public void setText6(String text6) {
		this.text6 = text6;
	}

	@Column(name = "photo6_url")
	public String getPhoto6_url() {
		return photo6_url;
	}

	public void setPhoto6_url(String photo6_url) {
		this.photo6_url = photo6_url;
	}

	@Column(name = "text_7")
	public String getText7() {
		return text7;
	}

	public void setText7(String text7) {
		this.text7 = text7;
	}
	
	@Column(name = "photo7_url")
	public String getPhoto7_url() {
		return photo7_url;
	}

	
	public void setPhoto7_url(String photo7_url) {
		this.photo7_url = photo7_url;
	}
	
	@Column(name = "text_8")
	public String getText8() {
		return text8;
	}

	public void setText8(String text8) {
		this.text8 = text8;
	}

	@Column(name = "photo8_url")
	public String getPhoto8_url() {
		return photo8_url;
	}

	public void setPhoto8_url(String photo8_url) {
		this.photo8_url = photo8_url;
	}

	@Column(name = "text_9")
	public String getText9() {
		return text9;
	}

	public void setText9(String text9) {
		this.text9 = text9;
	}

	@Column(name = "photo9_url")
	public String getPhoto9_url() {
		return photo9_url;
	}

	public void setPhoto9_url(String photo9_url) {
		this.photo9_url = photo9_url;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "read_number")
	public Long getReadNumber() {
		return readNumber;
	}

	public void setReadNumber(Long readNumber) {
		this.readNumber = readNumber;
	}
	
	@Column(name = "agent_id")
	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	
	
	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "show_location")
	public String getShowLocation() {
		return showLocation;
	}

	public void setShowLocation(String showLocation) {
		this.showLocation = showLocation;
	}
	
	
	
	
}
