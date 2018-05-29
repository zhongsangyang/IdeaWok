package com.cn.flypay.model.sys;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
/**
 * 通道每日流入金额统计
 * @author liangchao
 *
 */
@Entity
@Table(name = "sys_channel_todayamt_statistics")
public class TchannelTodayAmtStatistics implements java.io.Serializable{
	
	
	private static final long serialVersionUID = 4020095336937662114L;
	private Long id;
	private Long channel_id;
	/**
	 * 通道的总名称例如民生、微信、赞善赋
	 */
	private String name;
	private Integer type;
	/**
	 * 通道当日流入金额
	 */
	private BigDecimal today_Amt;
	/**
	 * 0正常 1失效
	 */
	private Integer status;
	private Date createTime;
	
	
	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "channel_id")
	public Long getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(Long channel_id) {
		this.channel_id = channel_id;
	}
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name = "today_Amt")
	public BigDecimal getToday_Amt() {
		return today_Amt;
	}
	public void setToday_Amt(BigDecimal today_Amt) {
		this.today_Amt = today_Amt;
	}
	
	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	
	
}
