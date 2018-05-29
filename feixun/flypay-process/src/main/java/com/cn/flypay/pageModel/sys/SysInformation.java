package com.cn.flypay.pageModel.sys;

import java.util.Date;

public class SysInformation implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1232003494611557800L;
	
	
	private Long id;
	private Long organizationId;
	private String title;
	private String context;
	private Integer downSum;
	private Integer readingSum;
	private Integer status;
	private Date creatime;
    private String stateDateStart;	
    private String stateDateEnd;	
    private String imgePath;
    private String sketch;
    private String turl;
    private String qurl;
    private String type;
    
	
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public Integer getDownSum() {
		return downSum;
	}
	public void setDownSum(Integer downSum) {
		this.downSum = downSum;
	}
	public Integer getReadingSum() {
		return readingSum;
	}
	public void setReadingSum(Integer readingSum) {
		this.readingSum = readingSum;
	}
	public Date getCreatime() {
		return creatime;
	}
	public void setCreatime(Date creatime) {
		this.creatime = creatime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getStateDateStart() {
		return stateDateStart;
	}
	public void setStateDateStart(String stateDateStart) {
		this.stateDateStart = stateDateStart;
	}
	public String getStateDateEnd() {
		return stateDateEnd;
	}
	public void setStateDateEnd(String stateDateEnd) {
		this.stateDateEnd = stateDateEnd;
	}
	public String getImgePath() {
		return imgePath;
	}
	public void setImgePath(String imgePath) {
		this.imgePath = imgePath;
	}
	public String getSketch() {
		return sketch;
	}
	public void setSketch(String sketch) {
		this.sketch = sketch;
	}
	public String getTurl() {
		return turl;
	}
	public void setTurl(String turl) {
		this.turl = turl;
	}
	public String getQurl() {
		return qurl;
	}
	public void setQurl(String qurl) {
		this.qurl = qurl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
