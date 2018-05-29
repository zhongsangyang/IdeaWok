package com.cn.flypay.model.account;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.cn.flypay.model.sys.Tuser;

/**
 * AccountPoint entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "account_point")
public class TaccountPoint implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -8771034594825464069L;
	private Long id;
	private Long version;
	private Tuser user;
	private Long point;
	private Long lockPoint;
	private Integer status;

	private Integer subPersonNum;
	
	private Integer fumiType;

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

	@Column(name = "point")
	public Long getPoint() {
		return this.point;
	}

	public void setPoint(Long point) {
		this.point = point;
	}

	@Column(name = "lock_point")
	public Long getLockPoint() {
		return this.lockPoint;
	}

	public void setLockPoint(Long lockPoint) {
		this.lockPoint = lockPoint;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name = "sub_person_num")
	public Integer getSubPersonNum() {
		return subPersonNum;
	}

	public void setSubPersonNum(Integer subPersonNum) {
		this.subPersonNum = subPersonNum;
	}
	@Column(name = "fumi_type")
	public Integer getFumiType() {
		return fumiType;
	}

	public void setFumiType(Integer fumiType) {
		this.fumiType = fumiType;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public Tuser getUser() {
		return this.user;
	}

	public void setUser(Tuser user) {
		this.user = user;
	}

}