package com.cn.flypay.model.sys;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.File;
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

import com.cn.flypay.utils.StringUtil;
/**
 * SysImage entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "sys_image")
public class TsysImage implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -1637993110253273727L;
	private Long id;
	private Long version;
	private Tuser user;
	private Integer type;
	private String imageName;
	private String path;
	private String url;
	private Date createTime;
	private String description;
	/**
	 * -1未认证，1已认证，2认证失败
	 */
	private Integer status;

	public TsysImage() {
	}

	public TsysImage(Tuser user, Integer type, String imageName, String url) {
		this.user = user;
		this.type = type;
		this.imageName = imageName;
		if (imageName.matches("[0-9]{8}.*")) {
			this.path = imageName.substring(0, 8) + File.separator + imageName.substring(8);
		}
		if (StringUtil.isNotBlank(url)) {
			this.url = url + imageName;
		}
		this.createTime = new Date();
		this.status = -1;// 初始化
	}

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
	@Column(name = "version")
	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public Tuser getUser() {
		return this.user;
	}

	public void setUser(Tuser user) {
		this.user = user;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "image_name", length = 512)
	public String getImageName() {
		return this.imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	@Column(name = "path", length = 512)
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Column(name = "description", length = 512)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "url", length = 512)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "create_time", length = 10)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}