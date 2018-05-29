package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

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


@Entity
@Table(name = "sys_information")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TsysInformation implements java.io.Serializable{

	private static final long serialVersionUID = -5504258394211647957L;
	
	private Long id;
	private Torganization organization;
	private String title;
	private String context;
	private Integer downSum; 
	private Integer readingSum; 
	private Integer stauts; 
	private Date creatime;
	private String imgePath;
	private String sketch;
	
	
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
	
	
	@Column(name = "title")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(name = "context")
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	
	
	@Column(name = "downSum")
	public Integer getDownSum() {
		return downSum;
	}
	public void setDownSum(Integer downSum) {
		this.downSum = downSum;
	}
	
	@Column(name = "readingSum")
	public Integer getReadingSum() {
		return readingSum;
	}
	public void setReadingSum(Integer readingSum) {
		this.readingSum = readingSum;
	}
	
	@Column(name = "stauts")
	public Integer getStauts() {
		return stauts;
	}
	public void setStauts(Integer stauts) {
		this.stauts = stauts;
	}
	
	
	@Column(name = "creatime")
	public Date getCreatime() {
		return creatime;
	}
	public void setCreatime(Date creatime) {
		this.creatime = creatime;
	}
	
	@Column(name = "imgePath")
	public String getImgePath() {
		return imgePath;
	}
	public void setImgePath(String imgePath) {
		this.imgePath = imgePath;
	}
	
	@Column(name = "sketch")
	public String getSketch() {
		return sketch;
	}
	public void setSketch(String sketch) {
		this.sketch = sketch;
	}
	
	
	
	
	
	
}
