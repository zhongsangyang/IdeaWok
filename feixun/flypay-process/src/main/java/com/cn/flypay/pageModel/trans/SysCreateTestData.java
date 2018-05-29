package com.cn.flypay.pageModel.trans;

import java.util.Date;
/**
 * 为王美凤造数据
 * @author liangchao
 *
 */
public class SysCreateTestData implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3849631611269565277L;
	private Long id;
	/**
	 * 对应sys_user 的id
	 */
	private String user_id;
	/**
	 * 对应sys_user 的real_name,若为空，则取name
	 */
	private String user_name;
	/**
	 * 可提现金额
	 */
	private String avlBrokerage;
	/**
	 * 总计佣金
	 */
	private String totalBrokerage;
	/**
	 * 今日佣金
	 */
	private String todayBrokerage;
	/**
	 * 昨日佣金
	 */
	private String yesterdayBrokerage;
	/**
	 * 邀请记录
	 */
	private String totalPersonNum;
	/**
	 * 实名认证人数--直接推荐用户
	 */
	private String zauthTrue_zhijie;
	/**
	 * 未实名认证人数--直接推荐用户
	 */
	private String zauthFalse_zhijie;
	/**
	 * 代理商人数--直接推荐用户
	 */
	private String dzian_zhijie;
	/**
	 * 实名认证人数--所有间接推荐用户
	 */
	private String zauthTrue_suoyou;
	/**
	 * 未实名认证人数--所有间接推荐用户
	 */
	private String zauthFalse_suoyou;
	/**
	 * 代理商--所有间接推荐用户
	 */
	private String dzian_suoyou;
	/**
	 * 记录创建时间
	 */
	private Date create_time;
	/**
	 * 查询开始时间
	 */
	private String searchDateStart;
	/**
	 * 查询结束时间
	 */
	private String searchDateEnd;
	public SysCreateTestData(){}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getAvlBrokerage() {
		return avlBrokerage;
	}
	public void setAvlBrokerage(String avlBrokerage) {
		this.avlBrokerage = avlBrokerage;
	}
	public String getTotalBrokerage() {
		return totalBrokerage;
	}
	public void setTotalBrokerage(String totalBrokerage) {
		this.totalBrokerage = totalBrokerage;
	}
	public String getTodayBrokerage() {
		return todayBrokerage;
	}
	public void setTodayBrokerage(String todayBrokerage) {
		this.todayBrokerage = todayBrokerage;
	}
	public String getYesterdayBrokerage() {
		return yesterdayBrokerage;
	}
	public void setYesterdayBrokerage(String yesterdayBrokerage) {
		this.yesterdayBrokerage = yesterdayBrokerage;
	}
	public String getTotalPersonNum() {
		return totalPersonNum;
	}
	public void setTotalPersonNum(String totalPersonNum) {
		this.totalPersonNum = totalPersonNum;
	}
	public String getZauthTrue_zhijie() {
		return zauthTrue_zhijie;
	}
	public void setZauthTrue_zhijie(String zauthTrue_zhijie) {
		this.zauthTrue_zhijie = zauthTrue_zhijie;
	}
	public String getZauthFalse_zhijie() {
		return zauthFalse_zhijie;
	}
	public void setZauthFalse_zhijie(String zauthFalse_zhijie) {
		this.zauthFalse_zhijie = zauthFalse_zhijie;
	}
	public String getDzian_zhijie() {
		return dzian_zhijie;
	}
	public void setDzian_zhijie(String dzian_zhijie) {
		this.dzian_zhijie = dzian_zhijie;
	}
	public String getZauthTrue_suoyou() {
		return zauthTrue_suoyou;
	}
	public void setZauthTrue_suoyou(String zauthTrue_suoyou) {
		this.zauthTrue_suoyou = zauthTrue_suoyou;
	}
	public String getZauthFalse_suoyou() {
		return zauthFalse_suoyou;
	}
	public void setZauthFalse_suoyou(String zauthFalse_suoyou) {
		this.zauthFalse_suoyou = zauthFalse_suoyou;
	}
	public String getDzian_suoyou() {
		return dzian_suoyou;
	}
	public void setDzian_suoyou(String dzian_suoyou) {
		this.dzian_suoyou = dzian_suoyou;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
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
	
	
}
