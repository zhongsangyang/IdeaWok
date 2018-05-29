package com.cn.flypay.pageModel.trans;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserCard;

public class UserOrder implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4688664592423255558L;

	public static String getOrderStatusChineseName(int status) {

		String name = "支付成功";
		switch (status) {
		case 200:
			name = "支付失败";
			break;
		case 300:
			name = "订单等待支付";
			break;
		case 500:
			name = "等待人工处理";
			break;
		}
		return name;
	}

	// Fields
	public enum order_status {
		/**
		 * 支付成功
		 */
		SUCCESS(100),
		/**
		 * 支付失败
		 */
		FAILURE(200),
		/**
		 * 初始化订单
		 */
		PROCESSING(300),
		/**
		 * 待人工处理
		 */
		MANUAL_PROCESSING(500);
		private order_status(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	/**
	 * 
	 */
	private Long id;
	private Long version;
	private Long userId;

	private BigDecimal orgAmt;
	private BigDecimal fee;
	private BigDecimal srvFee;
	private BigDecimal extractFee; //手续费中包含的提现手续费
	private BigDecimal amt;
	private BigDecimal avlAmt;
	private Integer status;
	private Date createTime;
	private Integer timeOut;
	private String orderNum;
	private String description;
	private String returnOrderNum;
	private Long cardId;
	private Date finallyDate;
	/**
	 * @serial 0 D0入账
	 * @serial 1 T1入账
	 * @serial 5 T5入账
	 */
	private Integer inputAccType;
	private UserCard userCard;

	private String cdType;
	/**
	 * 直通车判断  
	 * NOTZHITONGCHE 非直通车
	 * MINGSHENGZHITONGCHE 民生直通车
	 * XINKKEZHITONGCHE 欣客直通车
	 * YILIANZHIFUZTC 易联直通车
	 * ZHEYANGZTC 哲扬直通车
	 * PINGANPAYZHITONGCHE 平安直通车
	 * 
	 */
	private String ztcType; 
	/**
	 * 通道名称
	 */
	private String channelName;

	private Long channelId;
	/**
	 * 通道类型,是否自动提现
	 */
	private String channelType;
	
	/**
	 * 0立即下单，1 隔日下单 T0 T1
	 */
	private Integer payType;

	/**
	 * 扫描次数
	 */
	private Integer scanNum;

	private Integer agentType;

	/**
	 * 借指资产的增加和负债的减少,贷则相反
	 * 
	 * @author sunyue
	 * 
	 */
	public enum cd_type {
		/**
		 * 贷记 减少 提现
		 */
		C,
		/**
		 * 借记 增加 收款
		 */
		D,
		/**
		 * 收款
		 */
		S,
		/**
		 * 提现
		 */
		T,
		/**
		 * 提现冻结
		 */
		R,
		/**
		 * 钱包购买代理
		 */
		N,
		/**
		 * 佣提现冻结余额
		 */
	    Y,
	    /**
		 * 提现失败返还
		 */
	    F,
	    /**
		 * 余额转账转出
		 */
	    H,
	    /**
		 * 余额转账转入
		 */
	    J,
	    /**
		 * 调账转入
		 */
	    A,
	    /**
		 * 调账转出
		 */
	    B
		
	};

	private String payDatetimeStart;
	private String payDatetimeEnd;

	private String finishDatetimeStart;
	private String finishDatetimeEnd;

	private Date createDatetimeStart;
	private Date createDatetimeEnd;

	private PayOrder payOrder;
	private String payOrderPayNo;
	private String payOrderBusNo;
	private Date payOrderFinishDate;

	private String frontStatus;

	/**
	 * 支付类型 100：佣金_代理 110 佣金_流量 200：支付宝_二维码 210：支付宝_扫码 300、微信_二维码 310、微信_扫码
	 * 400、NFC 500、银联在线 600、卡头 700、提现_现金 710提现_佣金 800、转账
	 */
	private Integer type;

	/**
	 * 页面多类型查询
	 */
	private Set<Integer> mulType = new HashSet<Integer>();

	private String transPhone;
	private Integer transPayType;

	public enum trans_pay_type {
		/**
		 * 10 普通支付订单
		 */
		PUBLIC_PAY_TYPE(10),
		/**
		 * 20 代理费用支付订单
		 */
		AGENT_PAY_TYPE(20);
		private trans_pay_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	/**
	 * 用于购买升级代理的订单类型
	 * 
	 * @author sunyue
	 * 
	 */
	public enum agent_type {
		/**
		 * 普通支付订单，默认为0
		 */
		common(0),
		/**
		 * 钻石
		 */
		diamond(21),
		/**
		 * 金牌
		 */
		gold(22);
		private agent_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};

	private String userPhone;
	private String userName;

	private String errorInfo;

	private Long organizationId;
	private String organizationName;
	private User operateUser;
	private BigDecimal personRate;
	private BigDecimal shareRate;

	public enum trans_type {
		/**
		 * 佣金_代理
		 */
		YJDL(100),
		/**
		 * 佣金_流量
		 */
		YJLL(110),
		/**
		 * 支付宝_二维码
		 */
		ALQR(200),
		/**
		 * 支付宝_扫码
		 */
		ALSM(210),
		/**
		 * 支付宝_在线
		 */
		ALOL(220),
		/**
		 * 微信_二维码
		 */
		WXQR(300),
		/**
		 * 微信_扫码
		 */
		WXSM(310),
		/**
		 * 微信_在线
		 */
		WXOL(320),
		/**
		 * NFC
		 */
		NFC(400),
		/**
		 * 银联在线
		 */
		YLZX(500),
		/**
		 * 银联在线小额直通车
		 */
		YLZXE(520),
		/**
		 * 银联在线直通车
		 */
		YLZXT(530),
		/**
		 * 银联在线积分
		 */
		YLZXJ(550),
		/**
		 * 银联在线积分--易宝
		 */
		YLZXJYB(551),
		/**
		 * 银联在线积分--易宝
		 */
		YLZXJZY(552),
		/**
		 * card
		 */
		CTZF(600),
		/**
		 * 提现_现金流水
		 */
		XJTX(700),
		/**
		 * 提现_佣金
		 */
		YJTX(710),
		/**
		 * 钱包转账
		 */
		QBZZ(800),
		/**
		 * 京东-二维码
		 */
		JDQR(900),
		/**
		 * 京东-扫码
		 */
		JDSM(910),
		/**
		 * 京东-线上
		 */
		JDOL(920),
		/**
		 * 百度-二维码
		 */
		BDQR(1000),
		/**
		 * 百度-扫码
		 */
		BDSM(1010),
		/**
		 * 百度-线上
		 */
		BDOL(1020),
		/**
		 * 翼支付-二维码
		 */
		YZFQR(1100),
		/**
		 * 翼支付-扫码
		 */
		YISM(1110),
		/**
		 * 翼支付-线上
		 */
		YIOL(1120),
		/**
		 * 银联-二维码
		 */
		YLQR(1200),
		/**
		 * 银联-扫码
		 */
		YLSM(1210),
		/**
		 * 银联-线上
		 */
		YLOL(1220),
		/**
		 * QQ钱包-二维码
		 */
		QQQR(1300);

		private trans_type(int code) {
			this.code = code;
		}

		private int code;

		public int getCode() {
			return this.code;
		}
	};
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCdType() {
		return cdType;
	}

	public String getTransPhone() {
		return transPhone;
	}

	public void setTransPhone(String transPhone) {
		this.transPhone = transPhone;
	}

	public void setCdType(String cdType) {
		this.cdType = cdType;
	}

	public Long getChannelId() {
		return channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Set<Integer> getMulType() {
		return mulType;
	}

	public void setMulType(Set<Integer> mulType) {
		this.mulType = mulType;
	}

	public void setMulType(int mulType) {
		Set<Integer> ts = new HashSet<Integer>();
		switch (mulType) {
		case 100:
			ts.add(100);
			ts.add(110);
			break;
		case 200:
			ts.add(200);
			ts.add(210);
			ts.add(220);
			break;
		case 300:
			ts.add(300);
			ts.add(310);
			ts.add(320);
			break;
		case 400:
			ts.add(400);
			break;
		case 500:
			ts.add(500);
			break;
		case 600:
			ts.add(600);
			break;
		case 700:
			ts.add(700);
			break;
		case 710:
			ts.add(710);
		case 800:
			ts.add(800);
			break;
		case 900:
			ts.add(900);
			ts.add(910);
			ts.add(920);
			break;
		case 1000:
			ts.add(1000);
			ts.add(1010);
			ts.add(1020);
			break;
		case 1100:
			ts.add(1100);
			ts.add(1110);
			ts.add(1120);
			break;
		case 1200:
			ts.add(1200);
			ts.add(1210);
			ts.add(1220);
			break;
		case 1300:
			ts.add(1300);
			break;
		default:
			break;
		}
		this.mulType.addAll(ts);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public BigDecimal getOrgAmt() {
		return orgAmt;
	}

	public void setOrgAmt(BigDecimal orgAmt) {
		this.orgAmt = orgAmt;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	
	public BigDecimal getExtractFee() {
		return extractFee;
	}

	public void setExtractFee(BigDecimal extractFee) {
		this.extractFee = extractFee;
	}

	public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public BigDecimal getSrvFee() {
		return srvFee;
	}

	public void setSrvFee(BigDecimal srvFee) {
		this.srvFee = srvFee;
	}

	public BigDecimal getAvlAmt() {
		return avlAmt;
	}

	public void setAvlAmt(BigDecimal avlAmt) {
		this.avlAmt = avlAmt;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReturnOrderNum() {
		return returnOrderNum;
	}

	public void setReturnOrderNum(String returnOrderNum) {
		this.returnOrderNum = returnOrderNum;
	}

	public Long getCardId() {
		return cardId;
	}

	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}

	public Date getFinallyDate() {
		return finallyDate;
	}

	public void setFinallyDate(Date finallyDate) {
		this.finallyDate = finallyDate;
	}

	public String getPayDatetimeStart() {
		return payDatetimeStart;
	}

	public void setPayDatetimeStart(String payDatetimeStart) {
		this.payDatetimeStart = payDatetimeStart;
	}

	public String getPayDatetimeEnd() {
		return payDatetimeEnd;
	}

	public void setPayDatetimeEnd(String payDatetimeEnd) {
		this.payDatetimeEnd = payDatetimeEnd;
	}

	public PayOrder getPayOrder() {
		return payOrder;
	}

	public void setPayOrder(PayOrder payOrder) {
		this.payOrder = payOrder;
	}

	public String getFrontStatus() {
		return frontStatus;
	}

	public void setFrontStatus(String frontStatus) {
		this.frontStatus = frontStatus;
	}

	public Integer getScanNum() {
		return scanNum;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public String getFinishDatetimeStart() {
		return finishDatetimeStart;
	}

	public void setFinishDatetimeStart(String finishDatetimeStart) {
		this.finishDatetimeStart = finishDatetimeStart;
	}

	public String getFinishDatetimeEnd() {
		return finishDatetimeEnd;
	}

	public void setFinishDatetimeEnd(String finishDatetimeEnd) {
		this.finishDatetimeEnd = finishDatetimeEnd;
	}

	public void setScanNum(Integer scanNum) {
		this.scanNum = scanNum;
	}

	public UserCard getUserCard() {
		return userCard;
	}

	public void setUserCard(UserCard userCard) {
		this.userCard = userCard;
	}

	public Integer getTransPayType() {
		return transPayType;
	}

	public void setTransPayType(Integer transPayType) {
		this.transPayType = transPayType;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserName() {
		return userName;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getPayOrderPayNo() {
		return payOrderPayNo;
	}

	public void setPayOrderPayNo(String payOrderPayNo) {
		this.payOrderPayNo = payOrderPayNo;
	}

	public Date getCreateDatetimeStart() {
		return createDatetimeStart;
	}

	public void setCreateDatetimeStart(Date createDatetimeStart) {
		this.createDatetimeStart = createDatetimeStart;
	}

	public Date getCreateDatetimeEnd() {
		return createDatetimeEnd;
	}

	public void setCreateDatetimeEnd(Date createDatetimeEnd) {
		this.createDatetimeEnd = createDatetimeEnd;
	}

	public Date getPayOrderFinishDate() {
		return payOrderFinishDate;
	}

	public void setPayOrderFinishDate(Date payOrderFinishDate) {
		this.payOrderFinishDate = payOrderFinishDate;
	}


	public Integer getInputAccType() {
		return inputAccType;
	}

	public void setInputAccType(Integer inputAccType) {
		this.inputAccType = inputAccType;
	}

	public User getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(User operateUser) {
		this.operateUser = operateUser;
	}

	public BigDecimal getPersonRate() {
		return personRate;
	}

	public BigDecimal getShareRate() {
		return shareRate;
	}

	public void setShareRate(BigDecimal shareRate) {
		this.shareRate = shareRate;
	}

	public String getPayOrderBusNo() {
		return payOrderBusNo;
	}

	public void setPayOrderBusNo(String payOrderBusNo) {
		this.payOrderBusNo = payOrderBusNo;
	}

	public void setPersonRate(BigDecimal personRate) {
		this.personRate = personRate;
	}

	public Integer getAgentType() {
		return agentType;
	}

	public void setAgentType(Integer agentType) {
		this.agentType = agentType;
	}

	public static Set<Integer> getCollectOrderTypes() {
		Set<Integer> types = new HashSet<Integer>();
		types.add(200);
		types.add(210);
		types.add(220);

		types.add(300);
		types.add(310);
		types.add(320);
		types.add(500);
		types.add(520);
		types.add(530);
		types.add(550);
		types.add(900);
		types.add(910);
		types.add(920);

		types.add(1000);
		types.add(1010);
		types.add(1020);

		types.add(1100);
		types.add(1110);
		types.add(1120);

		types.add(1200);
		types.add(1210);
		types.add(1220);
		types.add(1300);

		return types;
	}
	
	public static Set<Integer> getCollectOrderTypesForFinanceProfit() {
		Set<Integer> types = new HashSet<Integer>();
		types.add(200);
		types.add(210);
		
		types.add(300);
		types.add(310);
		
		types.add(500);
		types.add(900);
		types.add(910);

		types.add(1000);
		types.add(1010);

		types.add(1100);
		types.add(1110);

		types.add(1200);
		types.add(1210);
		
		types.add(1300);
		return types;
	}

	/**
	 * 根据用户订单类型确定订单支付方式
	 * 
	 * @param orderType
	 * @return
	 */
	public static Integer getUserPayChannelType(Integer orderType) {
		return orderType / 100 * 100;
	}
	
	
	
	
	
	public static String getType(String type){
		Map<String, String> map = new HashMap<String, String>();
		map.put("100", "佣金代理");
		map.put("110", "佣金流量");
		map.put("200", "支付宝二维码");
		map.put("210", "支付宝扫码");
		map.put("220", "支付宝在线");
		map.put("300", "微信二维码");
		map.put("310", "微信扫码");
		map.put("320", "微信在线");
		map.put("400", "NFC");
		map.put("500", "银联在线");
		map.put("600", "card");
		map.put("700", "余额提现");
		map.put("710", "佣金提现");
		map.put("800", "钱包转账");
		map.put("900", "京东二维码");
		map.put("910", "京东扫码");
		map.put("920", "京东线上");
		map.put("1000", "百度二维码");
		map.put("1010", "百度扫码");
		map.put("1020", "百度线上");
		map.put("1100", "翼支付二维码");
		map.put("1110", "翼支付扫码");
		map.put("1120", "翼支付线上");
		map.put("1200", "银联二维码");
		map.put("1210", "银联扫码");
		map.put("1220", "银联线上");
		map.put("1300", "QQ钱包二维码");
		return map.get(type);
	}
	
	
	public static String getTypeQR(String type){
		Map<String, String> map = new HashMap<String, String>();
		map.put("YJDL", "佣金代理");
		map.put("YJLL", "佣金流量");
		map.put("ALQR", "支付宝二维码");
		map.put("ALSM", "支付宝扫码");
		map.put("ALOL", "支付宝在线");
		map.put("WXQR", "微信二维码");
		map.put("WXSM", "微信扫码");
		map.put("WXOL", "微信在线");
		map.put("NFC", "NFC");
		map.put("YLZX", "银联在线");
		map.put("CTZF", "card");
		map.put("XJTX", "余额提现");
		map.put("YJTX", "佣金提现");
		map.put("QBZZ", "钱包转账");
		map.put("JDQR", "京东二维码");
		map.put("JDSM", "京东扫码");
		map.put("JDOL", "京东线上");
		map.put("BDQR", "百度二维码");
		map.put("BDSM", "百度扫码");
		map.put("BDOL", "百度线上");
		map.put("YZFQR", "翼支付二维码");
		map.put("YISM", "翼支付扫码");
		map.put("YIOL", "翼支付线上");
		map.put("YLQR", "银联二维码");
		map.put("YLSM", "银联扫码");
		map.put("YLOL", "银联线上");
		map.put("QQQR", "QQ钱包二维码");
		return map.get(type);
	}
	
	public static String getInputType(String InputType){
		Map<String, String> map = new HashMap<String, String>();
		map.put("0", "小额D0");
		map.put("10", "大额D0");
		map.put("5", "T5");
		map.put("8", "T8");
		map.put("1", "小额T1");
		map.put("11", "大额T1");
		return map.get(InputType);
	}

	public String getZtcType() {
		return ztcType;
	}

	public void setZtcType(String ztcType) {
		this.ztcType = ztcType;
	}
	

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

}