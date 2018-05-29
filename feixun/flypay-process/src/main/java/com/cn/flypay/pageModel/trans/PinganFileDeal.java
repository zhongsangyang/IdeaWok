package com.cn.flypay.pageModel.trans;

import java.util.Date;

public class PinganFileDeal implements java.io.Serializable {

	private static final long serialVersionUID = -14777395005755799L;
	private Long id;
	private Integer version;
	private String fileName;
	private String filePath;
	private Integer type;
	private String tradeSn;
	private String randomPwd;
	private String signData;
	private String hashData;
	private String status;
	private String description;
	private Integer totalNum;
	private Double totalAmount;
	private Date createTime;

	// Fields
	public enum file_status {
		/**
		 * 发送批量代付文件成功
		 */
		file01_success, file01_fail,
		/**
		 * 查询批次代付_处理失败
		 */
		KHKF02_fail,
		/**
		 * 查询批次代付_处理成功
		 */
		KHKF02_success,

		file03_fail,
		
		file02_success,
		file02_fail,
		/**
		 * T1处理文件已经成功下载
		 */
		file03_success,
		/**
		 * T1代付指令——批量付款文件提交KHKF01已成功
		 */
		KHKF01_success,
		/**
		 * ，通知文件上传、下载的文件的结果
		 */
		file04_success,
		/**
		 * 发送对账文件请求_对账/差错文件查询
		 */
		KHKF05_success, KHKF05_fail
	};

	public Integer getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTradeSn() {
		return tradeSn;
	}

	public void setTradeSn(String tradeSn) {
		this.tradeSn = tradeSn;
	}

	public String getRandomPwd() {
		return randomPwd;
	}

	public void setRandomPwd(String randomPwd) {
		this.randomPwd = randomPwd;
	}

	public String getSignData() {
		return signData;
	}

	public void setSignData(String signData) {
		this.signData = signData;
	}

	public String getHashData() {
		return hashData;
	}

	public void setHashData(String hashData) {
		this.hashData = hashData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}