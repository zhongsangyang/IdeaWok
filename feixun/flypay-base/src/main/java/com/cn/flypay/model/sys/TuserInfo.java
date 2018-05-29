package com.cn.flypay.model.sys;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "sys_user_info")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TuserInfo implements java.io.Serializable {
	private static final long serialVersionUID = -6363727511239778180L;

	private Long id;
	private Long version;
	private String firstSuccUnipayTxn;
	private Tuser user;
	private Date createtime;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version", nullable = false)
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "firstSuccUnipayTxn")
	public String getFirstSuccUnipayTxn() {
		return firstSuccUnipayTxn;
	}

	public void setFirstSuccUnipayTxn(String firstSuccUnipayTxn) {
		this.firstSuccUnipayTxn = firstSuccUnipayTxn;
	}

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	public Tuser getUser() {
		return user;
	}

	public void setUser(Tuser user) {
		this.user = user;
	}

	@Column(name = "createTime", nullable = false, length = 19)
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

}
