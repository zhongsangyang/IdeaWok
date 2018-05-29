package com.cn.flypay.model.trans;

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
import javax.persistence.Version;

@Entity
@Table(name = "pingan_file_deal")
public class TpinganFileDeal implements java.io.Serializable {

	private static final long serialVersionUID = -1477739500504755799L;
	private Long id;
	private Integer version;
	private Integer totalNum;
	private Double totalAmount;
	private String fileName;
	private String filePath;
	private Integer type;
	private String tradeSn;
	private String randomPwd;
	private String signData;
	private String hashData;
	private String status;
	private String description;
	private Date createTime;

	private TpinganFileDeal fileDeal;
	
	public TpinganFileDeal() {
	}

	public TpinganFileDeal(String fileName, String filePath, Integer type, String tradeSn, Integer totalNum,
			Double totalAmount, String description) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.type = type;
		this.tradeSn = tradeSn;
		this.totalNum = totalNum;
		this.totalAmount = totalAmount;
		this.description = description;
		this.createTime = new Date();
	}

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
	@Column(name = "version")
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "total_num")
	public Integer getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	@Column(name = "total_amount")
	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Column(name = "file_name", length = 64)
	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column(name = "file_path", length = 128)
	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "Trade_sn", length = 64)
	public String getTradeSn() {
		return this.tradeSn;
	}

	public void setTradeSn(String tradeSn) {
		this.tradeSn = tradeSn;
	}

	@Column(name = "Random_Pwd", length = 256)
	public String getRandomPwd() {
		return this.randomPwd;
	}

	public void setRandomPwd(String randomPwd) {
		this.randomPwd = randomPwd;
	}

	@Column(name = "Sign_Data", length = 65535)
	public String getSignData() {
		return this.signData;
	}

	public void setSignData(String signData) {
		this.signData = signData;
	}

	@Column(name = "Hash_Data", length = 65535)
	public String getHashData() {
		return this.hashData;
	}

	public void setHashData(String hashData) {
		this.hashData = hashData;
	}

	@Column(name = "status", length = 64)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "description", length = 128)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "upload_file_id")
	public TpinganFileDeal getFileDeal() {
		return fileDeal;
	}

	public void setFileDeal(TpinganFileDeal fileDeal) {
		this.fileDeal = fileDeal;
	}

}