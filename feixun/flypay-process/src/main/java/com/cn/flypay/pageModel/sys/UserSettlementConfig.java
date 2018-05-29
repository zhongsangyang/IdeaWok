package com.cn.flypay.pageModel.sys;

import java.math.BigDecimal;

public class UserSettlementConfig implements java.io.Serializable {

	private static final long serialVersionUID = -3317085779482827661L;
	private long id;
	private BigDecimal t0Fee;
	private BigDecimal t1Fee;
	private BigDecimal minT0Amt;
	private BigDecimal maxT0Amt;
	private BigDecimal minT1Amt;
	private BigDecimal maxT1Amt;
	private BigDecimal rabaleFee;
	private BigDecimal maxRabaleAmt;
	private BigDecimal minRabaleAmt;

	private BigDecimal inputFee;
	private BigDecimal inputFeeAlipay;
	private BigDecimal inputFeeWeixin;
	private BigDecimal inputFeeYinlian;
	private BigDecimal inputFeeJingDong;
	private BigDecimal inputFeeBaidu;
	private BigDecimal inputFeeYizhifu;
	private BigDecimal inputFeeYinLianzhifu;
	private BigDecimal inputFeeD0Alipay;
	private BigDecimal inputFeeD0Weixin;
	private BigDecimal inputFeeD0Yinlian;
	private BigDecimal inputFeeD0JingDong;
	private BigDecimal inputFeeD0Baidu;
	private BigDecimal inputFeeD0Yizhifu;
	private BigDecimal inputFeeD0YinLianzhifu;

	private BigDecimal inputFeeBigAlipay;
	private BigDecimal inputFeeBigWeixin;
	private BigDecimal inputFeeBigYinlian;
	private BigDecimal inputFeeBigJingDong;
	private BigDecimal inputFeeBigBaidu;
	private BigDecimal inputFeeBigYizhifu;
	private BigDecimal inputFeeBigYinLianzhifu;
	private BigDecimal inputFeeBigQQzhifu;
	private BigDecimal inputFeeD0BigAlipay;
	private BigDecimal inputFeeD0BigWeixin;
	private BigDecimal inputFeeD0BigYinlian;
	private BigDecimal inputFeeD0BigJingDong;
	private BigDecimal inputFeeD0BigBaidu;
	private BigDecimal inputFeeD0BigYizhifu;
	private BigDecimal inputFeeD0BigYinLianzhifu;
	private BigDecimal inputFeeD0BigQQzhifu;

	private BigDecimal inputFeeZtAlipay;
	private BigDecimal inputFeeD0ZtAlipay;
	private BigDecimal inputFeeZtWeixin;
	private BigDecimal inputFeeD0ZtWeixin;
	private BigDecimal inputFeeZtQQzhifu;
	private BigDecimal inputFeeD0ZtQQzhifu;
	private BigDecimal inputFeeZtYinlian;
	private BigDecimal inputFeeD0ZtYinlian;

	private BigDecimal inputFeeZtYinlianJf;
	private BigDecimal inputFeeD0ZtYinlianJf;
	
	/*
	 * 哲扬费率
	 */
	private BigDecimal inputFeeZtYinlianJfZY;
	private BigDecimal inputFeeD0ZtYinlianJfZY;

	private String realName;
	private String loginName;
	private String agentId;
	private User stmUser;
	private BigDecimal maxTodayOutAmt;
	private BigDecimal shareFee;
	private Long organizationId;

	private String organizationAppName;
	private String organizationName;

	private User operateUser;

	public enum settlement_type {
		T0, T1, RABALE
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getT0Fee() {
		return t0Fee;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setT0Fee(BigDecimal t0Fee) {
		this.t0Fee = t0Fee;
	}

	public BigDecimal getInputFeeAlipay() {
		return inputFeeAlipay;
	}

	public void setInputFeeAlipay(BigDecimal inputFeeAlipay) {
		this.inputFeeAlipay = inputFeeAlipay;
	}

	public Integer setInputFeeByPayType(BigDecimal inputFeen, Integer payType, Integer accountType) {
		Integer compareReturn = 0;
		switch (payType) {
		case 200:
			if (accountType == 0) {
				compareReturn = this.inputFeeD0Alipay.compareTo(inputFeen);
				this.inputFeeD0Alipay = inputFeen;
			} else if (accountType == 1) {
				compareReturn = this.inputFeeAlipay.compareTo(inputFeen);
				this.inputFeeAlipay = inputFeen;
			} else if (accountType == 10) {
				compareReturn = this.inputFeeD0BigAlipay.compareTo(inputFeen);
				this.inputFeeD0BigAlipay = inputFeen;
			} else if (accountType == 11) {
				compareReturn = this.inputFeeBigAlipay.compareTo(inputFeen);
				this.inputFeeBigAlipay = inputFeen;
			} else if (accountType == 20) {
				compareReturn = this.inputFeeD0ZtAlipay.compareTo(inputFeen);
				this.inputFeeD0ZtAlipay = inputFeen;
			} else if (accountType == 21) {
				compareReturn = this.inputFeeZtAlipay.compareTo(inputFeen);
				this.inputFeeZtAlipay = inputFeen;
			}
			break;
		case 300:
			if (accountType == 0) {
				compareReturn = this.inputFeeD0Weixin.compareTo(inputFeen);
				this.inputFeeD0Weixin = inputFeen;
			} else if (accountType == 1) {
				compareReturn = this.inputFeeWeixin.compareTo(inputFeen);
				this.inputFeeWeixin = inputFeen;
			} else if (accountType == 10) {
				compareReturn = this.inputFeeD0BigWeixin.compareTo(inputFeen);
				this.inputFeeD0BigWeixin = inputFeen;
			} else if (accountType == 11) {
				compareReturn = this.inputFeeBigWeixin.compareTo(inputFeen);
				this.inputFeeBigWeixin = inputFeen;
			} else if (accountType == 20) {
				compareReturn = this.inputFeeD0ZtWeixin.compareTo(inputFeen);
				this.inputFeeD0ZtWeixin = inputFeen;
			} else if (accountType == 21) {
				compareReturn = this.inputFeeZtWeixin.compareTo(inputFeen);
				this.inputFeeZtWeixin = inputFeen;
			}
			break;
		case 500:
			if (accountType == 0) {
				compareReturn = this.inputFeeD0Yinlian.compareTo(inputFeen);
				this.inputFeeD0Yinlian = inputFeen;
			} else if (accountType == 1) {
				compareReturn = this.inputFeeYinlian.compareTo(inputFeen);
				this.inputFeeYinlian = inputFeen;
			} else if (accountType == 10) {
				compareReturn = this.inputFeeD0BigYinlian.compareTo(inputFeen);
				this.inputFeeD0BigYinlian = inputFeen;
			} else if (accountType == 11) {
				compareReturn = this.inputFeeBigYinlian.compareTo(inputFeen);
				this.inputFeeBigYinlian = inputFeen;
			} else if (accountType == 20) {
				compareReturn = this.inputFeeD0ZtYinlian.compareTo(inputFeen);
				this.inputFeeD0ZtYinlian = inputFeen;
			} else if (accountType == 21) {
				compareReturn = this.inputFeeZtYinlian.compareTo(inputFeen);
				this.inputFeeZtYinlian = inputFeen;
			}
			break;
		case 550:
			if (accountType == 20) {
				compareReturn = this.inputFeeD0ZtYinlianJf.compareTo(inputFeen);
				this.inputFeeD0ZtYinlianJf = inputFeen;
			} else if (accountType == 21) {
				compareReturn = this.inputFeeZtYinlianJf.compareTo(inputFeen);
				this.inputFeeZtYinlianJf = inputFeen;
			}
			break;
		case 900:
			if (accountType == 0) {
				compareReturn = this.inputFeeD0JingDong.compareTo(inputFeen);
				this.inputFeeD0JingDong = inputFeen;
			} else if (accountType == 1) {
				compareReturn = this.inputFeeJingDong.compareTo(inputFeen);
				this.inputFeeJingDong = inputFeen;
			} else if (accountType == 10) {
				compareReturn = this.inputFeeD0BigJingDong.compareTo(inputFeen);
				this.inputFeeD0BigJingDong = inputFeen;
			} else if (accountType == 11) {
				compareReturn = this.inputFeeBigJingDong.compareTo(inputFeen);
				this.inputFeeBigJingDong = inputFeen;
			}
			break;
		case 1000:
			if (accountType == 0) {
				compareReturn = this.inputFeeD0Baidu.compareTo(inputFeen);
				this.inputFeeD0Baidu = inputFeen;
			} else if (accountType == 1) {
				compareReturn = this.inputFeeBaidu.compareTo(inputFeen);
				this.inputFeeBaidu = inputFeen;
			} else if (accountType == 10) {
				compareReturn = this.inputFeeD0BigBaidu.compareTo(inputFeen);
				this.inputFeeD0BigBaidu = inputFeen;
			} else if (accountType == 11) {
				compareReturn = this.inputFeeBigBaidu.compareTo(inputFeen);
				this.inputFeeBigBaidu = inputFeen;
			}
			break;
		case 1100:
			if (accountType == 0) {
				compareReturn = this.inputFeeD0Yizhifu.compareTo(inputFeen);
				this.inputFeeD0Yizhifu = inputFeen;
			} else if (accountType == 1) {
				compareReturn = this.inputFeeYizhifu.compareTo(inputFeen);
				this.inputFeeYizhifu = inputFeen;
			} else if (accountType == 10) {
				compareReturn = this.inputFeeD0BigYizhifu.compareTo(inputFeen);
				this.inputFeeD0BigYizhifu = inputFeen;
			} else if (accountType == 11) {
				compareReturn = this.inputFeeBigYizhifu.compareTo(inputFeen);
				this.inputFeeBigYizhifu = inputFeen;
			}
			break;
		case 1200:
			if (accountType == 0) {
				compareReturn = this.inputFeeD0YinLianzhifu.compareTo(inputFeen);
				this.inputFeeD0YinLianzhifu = inputFeen;
			} else if (accountType == 1) {
				compareReturn = this.inputFeeYinLianzhifu.compareTo(inputFeen);
				this.inputFeeYinLianzhifu = inputFeen;
			} else if (accountType == 10) {
				compareReturn = this.inputFeeD0BigYinLianzhifu.compareTo(inputFeen);
				this.inputFeeD0BigYinLianzhifu = inputFeen;
			} else if (accountType == 11) {
				compareReturn = this.inputFeeBigYinLianzhifu.compareTo(inputFeen);
				this.inputFeeBigYinLianzhifu = inputFeen;
			}
			break;
		case 1300:
			if (accountType == 10) {
				compareReturn = this.inputFeeBigQQzhifu.compareTo(inputFeen);
				this.inputFeeD0BigQQzhifu = inputFeen;
			} else if (accountType == 11) {
				compareReturn = this.inputFeeD0BigQQzhifu.compareTo(inputFeen);
				this.inputFeeBigQQzhifu = inputFeen;
			} else if (accountType == 20) {
				compareReturn = this.inputFeeD0ZtQQzhifu.compareTo(inputFeen);
				this.inputFeeD0ZtQQzhifu = inputFeen;
			} else if (accountType == 21) {
				compareReturn = this.inputFeeZtQQzhifu.compareTo(inputFeen);
				this.inputFeeZtQQzhifu = inputFeen;
			}
			break;

		}

		return compareReturn;
	}

	public BigDecimal getInputFeeByPayType(Integer payType, Integer accountType) {
		BigDecimal inputRate = this.inputFee;
		switch (payType) {
		case 200:
			if (accountType == 0) {
				inputRate = this.inputFeeD0Alipay;
			} else if (accountType == 1) {
//				inputRate = this.inputFeeAlipay;
				inputRate = this.inputFeeD0ZtAlipay;
			} else if (accountType == 101) {
				inputRate = this.inputFeeD0ZtAlipay;
			} else if (accountType == 10) {
				inputRate = this.inputFeeD0BigAlipay;
			} else if (accountType == 11) {
				inputRate = this.inputFeeBigAlipay;
			} else if (accountType == 20) {
				inputRate = this.inputFeeD0ZtAlipay;
			} else if (accountType == 21) {
				inputRate = this.inputFeeZtAlipay;
			}
			break;
		case 300:
			if (accountType == 0) {
				inputRate = this.inputFeeD0Weixin;
			} else if (accountType == 1) {
				inputRate = this.inputFeeD0ZtWeixin;
//				inputRate = this.inputFeeWeixin;
			} else if (accountType == 101) {
				inputRate = this.inputFeeD0ZtWeixin;
			} else if (accountType == 10) {
				inputRate = this.inputFeeD0BigWeixin;
			} else if (accountType == 11) {
				inputRate = this.inputFeeBigWeixin;
			} else if (accountType == 20) {
				inputRate = this.inputFeeD0ZtWeixin;
			} else if (accountType == 21) {
				inputRate = this.inputFeeZtWeixin;
			}
			break;
		case 500:
			if (accountType == 0) {
				inputRate = this.inputFeeD0Yinlian;
			} else if (accountType == 1) {
				inputRate = this.inputFeeYinlian;
			} else if (accountType == 10) {
				inputRate = this.inputFeeD0BigYinlian;
			} else if (accountType == 11) {
				inputRate = this.inputFeeBigYinlian;
			} else if (accountType == 20) {
				inputRate = this.inputFeeD0ZtYinlian;
			} else if (accountType == 21) {
				inputRate = this.inputFeeZtYinlian;
			}
			break;
		case 520:
			if (accountType == 0) {
				inputRate = this.inputFeeD0Yinlian;
			} else if (accountType == 1) {
				inputRate = this.inputFeeYinlian;
			} else if (accountType == 10) {
				inputRate = this.inputFeeD0BigYinlian;
			} else if (accountType == 11) {
				inputRate = this.inputFeeBigYinlian;
			} else if (accountType == 20) {
				inputRate = this.inputFeeD0ZtYinlian;
			} else if (accountType == 21) {
				inputRate = this.inputFeeZtYinlian;
			}
			break;
		case 550:
			if (accountType == 0) { // 易联银联积分直通车D0新增 at 2017-11-10 by liangchao
				inputRate = this.inputFeeD0ZtYinlianJf;
			} else if (accountType == 20) {
				inputRate = this.inputFeeD0ZtYinlianJf;
			} else if (accountType == 21) {
				inputRate = this.inputFeeZtYinlianJf;
			}
			break;
		case 552:
			if (accountType == 0) { // 易联银联积分直通车D0新增 at 2017-11-10 by liangchao
				inputRate = this.inputFeeD0ZtYinlianJfZY;
			} else if (accountType == 20) {
				inputRate = this.inputFeeD0ZtYinlianJfZY;
			} else if (accountType == 21) {
				inputRate = this.inputFeeZtYinlianJfZY;
			}
			break;
		case 900:
			if (accountType == 0) {
				inputRate = this.inputFeeD0JingDong;
			} else if (accountType == 1) {
				inputRate = this.inputFeeJingDong;
			} else if (accountType == 10) {
				inputRate = this.inputFeeD0BigJingDong;
			} else if (accountType == 11) {
				inputRate = this.inputFeeBigJingDong;
			}
			break;
		case 1000:
			if (accountType == 0) {
				inputRate = this.inputFeeD0Baidu;
			} else if (accountType == 1) {
				inputRate = this.inputFeeBaidu;
			} else if (accountType == 10) {
				inputRate = this.inputFeeD0BigBaidu;
			} else if (accountType == 11) {
				inputRate = this.inputFeeBigBaidu;
			}
			break;
		case 1100:
			if (accountType == 0) {
				inputRate = this.inputFeeD0Yizhifu;
			} else if (accountType == 1) {
				inputRate = this.inputFeeBigYizhifu;
			} else if (accountType == 101) {
				inputRate = this.inputFeeBigYizhifu;
			} else if (accountType == 10) {
				inputRate = this.inputFeeD0BigYizhifu;
			} else if (accountType == 11) {
				inputRate = this.inputFeeBigYizhifu;
			}
			break;
		case 1200:
			if (accountType == 0) {
				inputRate = this.inputFeeD0YinLianzhifu;
			} else if (accountType == 1) {
				inputRate = this.inputFeeYinLianzhifu;
			} else if (accountType == 10) {
				inputRate = this.inputFeeD0BigYinLianzhifu;
			} else if (accountType == 11) {
				inputRate = this.inputFeeBigYinLianzhifu;
			}
			break;
		case 1300:
			if (accountType == 10) {
				inputRate = this.inputFeeD0BigQQzhifu;
			} else if (accountType == 11) {
				inputRate = this.inputFeeBigQQzhifu;
			} else if (accountType == 20) {
				inputRate = this.inputFeeD0ZtQQzhifu;
			} else if (accountType == 21) {
				inputRate = this.inputFeeZtQQzhifu;
			}
			break;
		}
		return inputRate;
	}

	public BigDecimal getInputFeeJingDong() {
		return inputFeeJingDong;
	}

	public void setInputFeeJingDong(BigDecimal inputFeeJingDong) {
		this.inputFeeJingDong = inputFeeJingDong;
	}

	public BigDecimal getInputFeeBaidu() {
		return inputFeeBaidu;
	}

	public void setInputFeeBaidu(BigDecimal inputFeeBaidu) {
		this.inputFeeBaidu = inputFeeBaidu;
	}

	public BigDecimal getInputFeeYizhifu() {
		return inputFeeYizhifu;
	}

	public void setInputFeeYizhifu(BigDecimal inputFeeYizhifu) {
		this.inputFeeYizhifu = inputFeeYizhifu;
	}

	public BigDecimal getInputFeeYinLianzhifu() {
		return inputFeeYinLianzhifu;
	}

	public void setInputFeeYinLianzhifu(BigDecimal inputFeeYinLianzhifu) {
		this.inputFeeYinLianzhifu = inputFeeYinLianzhifu;
	}

	public BigDecimal getInputFeeWeixin() {
		return inputFeeWeixin;
	}

	public void setInputFeeWeixin(BigDecimal inputFeeWeixin) {
		this.inputFeeWeixin = inputFeeWeixin;
	}

	public BigDecimal getInputFeeYinlian() {
		return inputFeeYinlian;
	}

	public void setInputFeeYinlian(BigDecimal inputFeeYinlian) {
		this.inputFeeYinlian = inputFeeYinlian;
	}

	public BigDecimal getMaxTodayOutAmt() {
		return maxTodayOutAmt;
	}

	public BigDecimal getShareFee() {
		return shareFee;
	}

	public void setShareFee(BigDecimal shareFee) {
		this.shareFee = shareFee;
	}

	public void setMaxTodayOutAmt(BigDecimal maxTodayOutAmt) {
		this.maxTodayOutAmt = maxTodayOutAmt;
	}

	public BigDecimal getT1Fee() {
		return t1Fee;
	}

	public BigDecimal getInputFee() {
		return inputFee;
	}

	public void setInputFee(BigDecimal inputFee) {
		this.inputFee = inputFee;
	}

	public void setT1Fee(BigDecimal t1Fee) {
		this.t1Fee = t1Fee;
	}

	public BigDecimal getMinT0Amt() {
		return minT0Amt;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public void setMinT0Amt(BigDecimal minT0Amt) {
		this.minT0Amt = minT0Amt;
	}

	public BigDecimal getMaxT0Amt() {
		return maxT0Amt;
	}

	public void setMaxT0Amt(BigDecimal maxT0Amt) {
		this.maxT0Amt = maxT0Amt;
	}

	public BigDecimal getMinT1Amt() {
		return minT1Amt;
	}

	public void setMinT1Amt(BigDecimal minT1Amt) {
		this.minT1Amt = minT1Amt;
	}

	public BigDecimal getMaxT1Amt() {
		return maxT1Amt;
	}

	public void setMaxT1Amt(BigDecimal maxT1Amt) {
		this.maxT1Amt = maxT1Amt;
	}

	public BigDecimal getRabaleFee() {
		return rabaleFee;
	}

	public void setRabaleFee(BigDecimal rabaleFee) {
		this.rabaleFee = rabaleFee;
	}

	public BigDecimal getMaxRabaleAmt() {
		return maxRabaleAmt;
	}

	public void setMaxRabaleAmt(BigDecimal maxRabaleAmt) {
		this.maxRabaleAmt = maxRabaleAmt;
	}

	public BigDecimal getMinRabaleAmt() {
		return minRabaleAmt;
	}

	public void setMinRabaleAmt(BigDecimal minRabaleAmt) {
		this.minRabaleAmt = minRabaleAmt;
	}

	public void setStmUser(User stmUser) {
		this.stmUser = stmUser;
	}

	public User getStmUser() {
		return stmUser;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationAppName() {
		return organizationAppName;
	}

	public void setOrganizationAppName(String organizationAppName) {
		this.organizationAppName = organizationAppName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public BigDecimal getInputFeeD0Alipay() {
		return inputFeeD0Alipay;
	}

	public void setInputFeeD0Alipay(BigDecimal inputFeeD0Alipay) {
		this.inputFeeD0Alipay = inputFeeD0Alipay;
	}

	public BigDecimal getInputFeeD0Weixin() {
		return inputFeeD0Weixin;
	}

	public void setInputFeeD0Weixin(BigDecimal inputFeeD0Weixin) {
		this.inputFeeD0Weixin = inputFeeD0Weixin;
	}

	public BigDecimal getInputFeeD0Yinlian() {
		return inputFeeD0Yinlian;
	}

	public void setInputFeeD0Yinlian(BigDecimal inputFeeD0Yinlian) {
		this.inputFeeD0Yinlian = inputFeeD0Yinlian;
	}

	public BigDecimal getInputFeeD0JingDong() {
		return inputFeeD0JingDong;
	}

	public void setInputFeeD0JingDong(BigDecimal inputFeeD0JingDong) {
		this.inputFeeD0JingDong = inputFeeD0JingDong;
	}

	public BigDecimal getInputFeeD0Baidu() {
		return inputFeeD0Baidu;
	}

	public void setInputFeeD0Baidu(BigDecimal inputFeeD0Baidu) {
		this.inputFeeD0Baidu = inputFeeD0Baidu;
	}

	public BigDecimal getInputFeeD0Yizhifu() {
		return inputFeeD0Yizhifu;
	}

	public void setInputFeeD0Yizhifu(BigDecimal inputFeeD0Yizhifu) {
		this.inputFeeD0Yizhifu = inputFeeD0Yizhifu;
	}

	public BigDecimal getInputFeeD0YinLianzhifu() {
		return inputFeeD0YinLianzhifu;
	}

	public void setInputFeeD0YinLianzhifu(BigDecimal inputFeeD0YinLianzhifu) {
		this.inputFeeD0YinLianzhifu = inputFeeD0YinLianzhifu;
	}

	public BigDecimal getInputFeeBigAlipay() {
		return inputFeeBigAlipay;
	}

	public void setInputFeeBigAlipay(BigDecimal inputFeeBigAlipay) {
		this.inputFeeBigAlipay = inputFeeBigAlipay;
	}

	public BigDecimal getInputFeeBigWeixin() {
		return inputFeeBigWeixin;
	}

	public void setInputFeeBigWeixin(BigDecimal inputFeeBigWeixin) {
		this.inputFeeBigWeixin = inputFeeBigWeixin;
	}

	public BigDecimal getInputFeeBigYinlian() {
		return inputFeeBigYinlian;
	}

	public void setInputFeeBigYinlian(BigDecimal inputFeeBigYinlian) {
		this.inputFeeBigYinlian = inputFeeBigYinlian;
	}

	public BigDecimal getInputFeeBigJingDong() {
		return inputFeeBigJingDong;
	}

	public void setInputFeeBigJingDong(BigDecimal inputFeeBigJingDong) {
		this.inputFeeBigJingDong = inputFeeBigJingDong;
	}

	public BigDecimal getInputFeeBigBaidu() {
		return inputFeeBigBaidu;
	}

	public void setInputFeeBigBaidu(BigDecimal inputFeeBigBaidu) {
		this.inputFeeBigBaidu = inputFeeBigBaidu;
	}

	public BigDecimal getInputFeeBigYizhifu() {
		return inputFeeBigYizhifu;
	}

	public void setInputFeeBigYizhifu(BigDecimal inputFeeBigYizhifu) {
		this.inputFeeBigYizhifu = inputFeeBigYizhifu;
	}

	public BigDecimal getInputFeeBigYinLianzhifu() {
		return inputFeeBigYinLianzhifu;
	}

	public void setInputFeeBigYinLianzhifu(BigDecimal inputFeeBigYinLianzhifu) {
		this.inputFeeBigYinLianzhifu = inputFeeBigYinLianzhifu;
	}

	public BigDecimal getInputFeeD0BigAlipay() {
		return inputFeeD0BigAlipay;
	}

	public void setInputFeeD0BigAlipay(BigDecimal inputFeeD0BigAlipay) {
		this.inputFeeD0BigAlipay = inputFeeD0BigAlipay;
	}

	public BigDecimal getInputFeeD0BigWeixin() {
		return inputFeeD0BigWeixin;
	}

	public void setInputFeeD0BigWeixin(BigDecimal inputFeeD0BigWeixin) {
		this.inputFeeD0BigWeixin = inputFeeD0BigWeixin;
	}

	public BigDecimal getInputFeeD0BigYinlian() {
		return inputFeeD0BigYinlian;
	}

	public void setInputFeeD0BigYinlian(BigDecimal inputFeeD0BigYinlian) {
		this.inputFeeD0BigYinlian = inputFeeD0BigYinlian;
	}

	public BigDecimal getInputFeeD0BigJingDong() {
		return inputFeeD0BigJingDong;
	}

	public void setInputFeeD0BigJingDong(BigDecimal inputFeeD0BigJingDong) {
		this.inputFeeD0BigJingDong = inputFeeD0BigJingDong;
	}

	public BigDecimal getInputFeeD0BigBaidu() {
		return inputFeeD0BigBaidu;
	}

	public void setInputFeeD0BigBaidu(BigDecimal inputFeeD0BigBaidu) {
		this.inputFeeD0BigBaidu = inputFeeD0BigBaidu;
	}

	public BigDecimal getInputFeeD0BigYizhifu() {
		return inputFeeD0BigYizhifu;
	}

	public void setInputFeeD0BigYizhifu(BigDecimal inputFeeD0BigYizhifu) {
		this.inputFeeD0BigYizhifu = inputFeeD0BigYizhifu;
	}

	public BigDecimal getInputFeeD0BigYinLianzhifu() {
		return inputFeeD0BigYinLianzhifu;
	}

	public void setInputFeeD0BigYinLianzhifu(BigDecimal inputFeeD0BigYinLianzhifu) {
		this.inputFeeD0BigYinLianzhifu = inputFeeD0BigYinLianzhifu;
	}

	public BigDecimal getInputFeeBigQQzhifu() {
		return inputFeeBigQQzhifu;
	}

	public void setInputFeeBigQQzhifu(BigDecimal inputFeeBigQQzhifu) {
		this.inputFeeBigQQzhifu = inputFeeBigQQzhifu;
	}

	public BigDecimal getInputFeeD0BigQQzhifu() {
		return inputFeeD0BigQQzhifu;
	}

	public void setInputFeeD0BigQQzhifu(BigDecimal inputFeeD0BigQQzhifu) {
		this.inputFeeD0BigQQzhifu = inputFeeD0BigQQzhifu;
	}

	public BigDecimal getInputFeeZtAlipay() {
		return inputFeeZtAlipay;
	}

	public void setInputFeeZtAlipay(BigDecimal inputFeeZtAlipay) {
		this.inputFeeZtAlipay = inputFeeZtAlipay;
	}

	public BigDecimal getInputFeeD0ZtAlipay() {
		return inputFeeD0ZtAlipay;
	}

	public void setInputFeeD0ZtAlipay(BigDecimal inputFeeD0ZtAlipay) {
		this.inputFeeD0ZtAlipay = inputFeeD0ZtAlipay;
	}

	public BigDecimal getInputFeeZtWeixin() {
		return inputFeeZtWeixin;
	}

	public void setInputFeeZtWeixin(BigDecimal inputFeeZtWeixin) {
		this.inputFeeZtWeixin = inputFeeZtWeixin;
	}

	public BigDecimal getInputFeeD0ZtWeixin() {
		return inputFeeD0ZtWeixin;
	}

	public void setInputFeeD0ZtWeixin(BigDecimal inputFeeD0ZtWeixin) {
		this.inputFeeD0ZtWeixin = inputFeeD0ZtWeixin;
	}

	public BigDecimal getInputFeeZtQQzhifu() {
		return inputFeeZtQQzhifu;
	}

	public void setInputFeeZtQQzhifu(BigDecimal inputFeeZtQQzhifu) {
		this.inputFeeZtQQzhifu = inputFeeZtQQzhifu;
	}

	public BigDecimal getInputFeeD0ZtQQzhifu() {
		return inputFeeD0ZtQQzhifu;
	}

	public void setInputFeeD0ZtQQzhifu(BigDecimal inputFeeD0ZtQQzhifu) {
		this.inputFeeD0ZtQQzhifu = inputFeeD0ZtQQzhifu;
	}

	public BigDecimal getInputFeeZtYinlian() {
		return inputFeeZtYinlian;
	}

	public void setInputFeeZtYinlian(BigDecimal inputFeeZtYinlian) {
		this.inputFeeZtYinlian = inputFeeZtYinlian;
	}

	public BigDecimal getInputFeeD0ZtYinlian() {
		return inputFeeD0ZtYinlian;
	}

	public void setInputFeeD0ZtYinlian(BigDecimal inputFeeD0ZtYinlian) {
		this.inputFeeD0ZtYinlian = inputFeeD0ZtYinlian;
	}

	public BigDecimal getInputFeeZtYinlianJf() {
		return inputFeeZtYinlianJf;
	}

	public void setInputFeeZtYinlianJf(BigDecimal inputFeeZtYinlianJf) {
		this.inputFeeZtYinlianJf = inputFeeZtYinlianJf;
	}

	public BigDecimal getInputFeeD0ZtYinlianJf() {
		return inputFeeD0ZtYinlianJf;
	}

	public void setInputFeeD0ZtYinlianJf(BigDecimal inputFeeD0ZtYinlianJf) {
		this.inputFeeD0ZtYinlianJf = inputFeeD0ZtYinlianJf;
	}

	public BigDecimal getInputFeeZtYinlianJfZY() {
		return inputFeeZtYinlianJfZY;
	}

	public void setInputFeeZtYinlianJfZY(BigDecimal inputFeeZtYinlianJfZY) {
		this.inputFeeZtYinlianJfZY = inputFeeZtYinlianJfZY;
	}

	public BigDecimal getInputFeeD0ZtYinlianJfZY() {
		return inputFeeD0ZtYinlianJfZY;
	}

	public void setInputFeeD0ZtYinlianJfZY(BigDecimal inputFeeD0ZtYinlianJfZY) {
		this.inputFeeD0ZtYinlianJfZY = inputFeeD0ZtYinlianJfZY;
	}

}