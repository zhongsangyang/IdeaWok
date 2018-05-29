package com.cn.flypay.pageModel.sys;

import java.util.Date;

public class SysImage implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -6213697023284399565L;
	private long id;
	private long version;
	private long userId;
	private Integer type;
	private Integer status;

	// Fields
	public enum image_type {
		/**
		 * 0为身份证正面
		 */
		front_ID(0),
		/**
		 * 1为身份证反面，
		 */
		back_ID(1),
		/**
		 * 2手持身份证，
		 */
		hand_ID(2),
		/**
		 * 营业执照，
		 */
		LICENSE_ID(3),
		/**
		 * 申请人手持身份证在收银台内照片
		 */
		HAND_IN_CASHIER_DESK(4),
		/**
		 * 营业执照1，
		 */
		INTERIOR_VIEW_1(5),
		/**
		 * 营业执照1，
		 */
		INTERIOR_VIEW_2(6),
		/**
		 * 营业执照1，
		 */
		INTERIOR_VIEW_3(7),
		/**
		 * 经营许可证，
		 */
		SHOP_ID(8);
		private image_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	private String imageName;
	private String path;
	private String url;
	private Date createTime;

	// Constructors

	/** default constructor */
	public SysImage() {
	}

	/** full constructor */
	public SysImage(long userId, Integer type, String imageName, String path, String url, Date createTime) {
		this.userId = userId;
		this.type = type;
		this.imageName = imageName;
		this.path = path;
		this.url = url;
		this.createTime = createTime;
	}

	public long getId() {
		return id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}