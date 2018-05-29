package com.cn.flypay.pageModel.sys;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

/**
 * 新闻咨询图片信息
 * @author liangchao
 *
 */
public class SysInformationPhoto implements java.io.Serializable{

	private static final long serialVersionUID = -2484051094303006831L;
	private Long id;
	private Integer stauts;
	private String text1;
	private MultipartFile photo1;
	private String photo1_url;
	
	private String text2;
	private MultipartFile photo2;
	private String photo2_url;
	
	private String text3;
	private MultipartFile photo3;
	private String photo3_url;
	
	private String text4;
	private MultipartFile photo4;
	private String photo4_url;
	
	private String text5;
	private MultipartFile photo5;
	private String photo5_url;
	
	private String text6;
	private MultipartFile photo6;
	private String photo6_url;
	
	private String text7;
	private MultipartFile photo7;
	private String photo7_url;
	
	private String text8;
	private MultipartFile photo8;
	private String photo8_url;
	
	private String text9;
	private MultipartFile photo9;
	private String photo9_url;
	
	private String searchDateStart;
	private String searchDateEnd;
	private Date createTime;
	
	private Long readNumber;
	
	private String type;
	
	private String showLocation;
	
	private String agentId;
	
	public SysInformationPhoto(){}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public MultipartFile getPhoto1() {
		return photo1;
	}

	public void setPhoto1(MultipartFile photo1) {
		this.photo1 = photo1;
	}

	public String getPhoto1_url() {
		return photo1_url;
	}

	public void setPhoto1_url(String photo1_url) {
		this.photo1_url = photo1_url;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}

	public MultipartFile getPhoto2() {
		return photo2;
	}

	public void setPhoto2(MultipartFile photo2) {
		this.photo2 = photo2;
	}
	public String getPhoto2_url() {
		return photo2_url;
	}

	public void setPhoto2_url(String photo2_url) {
		this.photo2_url = photo2_url;
	}

	public String getText3() {
		return text3;
	}

	public void setText3(String text3) {
		this.text3 = text3;
	}

	public MultipartFile getPhoto3() {
		return photo3;
	}

	public void setPhoto3(MultipartFile photo3) {
		this.photo3 = photo3;
	}

	public String getPhoto3_url() {
		return photo3_url;
	}

	public void setPhoto3_url(String photo3_url) {
		this.photo3_url = photo3_url;
	}

	public String getText4() {
		return text4;
	}

	public void setText4(String text4) {
		this.text4 = text4;
	}

	public MultipartFile getPhoto4() {
		return photo4;
	}

	public void setPhoto4(MultipartFile photo4) {
		this.photo4 = photo4;
	}

	public String getPhoto4_url() {
		return photo4_url;
	}

	public void setPhoto4_url(String photo4_url) {
		this.photo4_url = photo4_url;
	}

	public String getText5() {
		return text5;
	}

	public void setText5(String text5) {
		this.text5 = text5;
	}

	public MultipartFile getPhoto5() {
		return photo5;
	}

	public void setPhoto5(MultipartFile photo5) {
		this.photo5 = photo5;
	}

	public String getPhoto5_url() {
		return photo5_url;
	}

	public void setPhoto5_url(String photo5_url) {
		this.photo5_url = photo5_url;
	}

	public String getText6() {
		return text6;
	}

	public void setText6(String text6) {
		this.text6 = text6;
	}

	public MultipartFile getPhoto6() {
		return photo6;
	}

	public void setPhoto6(MultipartFile photo6) {
		this.photo6 = photo6;
	}

	public String getPhoto6_url() {
		return photo6_url;
	}

	public void setPhoto6_url(String photo6_url) {
		this.photo6_url = photo6_url;
	}
	
	public String getText7() {
		return text7;
	}

	public void setText7(String text7) {
		this.text7 = text7;
	}

	public MultipartFile getPhoto7() {
		return photo7;
	}

	public void setPhoto7(MultipartFile photo7) {
		this.photo7 = photo7;
	}

	public String getPhoto7_url() {
		return photo7_url;
	}

	public void setPhoto7_url(String photo7_url) {
		this.photo7_url = photo7_url;
	}

	public String getText8() {
		return text8;
	}

	public void setText8(String text8) {
		this.text8 = text8;
	}

	public MultipartFile getPhoto8() {
		return photo8;
	}

	public void setPhoto8(MultipartFile photo8) {
		this.photo8 = photo8;
	}

	public String getPhoto8_url() {
		return photo8_url;
	}

	public void setPhoto8_url(String photo8_url) {
		this.photo8_url = photo8_url;
	}

	public String getText9() {
		return text9;
	}

	public void setText9(String text9) {
		this.text9 = text9;
	}

	public MultipartFile getPhoto9() {
		return photo9;
	}

	public void setPhoto9(MultipartFile photo9) {
		this.photo9 = photo9;
	}

	public String getPhoto9_url() {
		return photo9_url;
	}

	public void setPhoto9_url(String photo9_url) {
		this.photo9_url = photo9_url;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getSearchDateStart() {
		return searchDateStart;
	}

	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}

	public Integer getStauts() {
		return stauts;
	}

	public void setStauts(Integer stauts) {
		this.stauts = stauts;
	}

	public Long getReadNumber() {
		return readNumber;
	}

	public void setReadNumber(Long readNumber) {
		this.readNumber = readNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getShowLocation() {
		return showLocation;
	}

	public void setShowLocation(String showLocation) {
		this.showLocation = showLocation;
	}


    
	
	
}
