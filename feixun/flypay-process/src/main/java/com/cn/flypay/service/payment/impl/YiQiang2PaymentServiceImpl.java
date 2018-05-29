package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TYiQiang2MerchantReport;
import com.cn.flypay.model.sys.TYiQiangMerchantReport;
import com.cn.flypay.model.sys.TcreditCardReport;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.model.util.JSON;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.sys.ServiceMerchantService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserMerchantConfigService;
import com.cn.flypay.service.sys.UserMerchantReportService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.yiqiang.YiQiangApplication;
import com.cn.flypay.utils.yiqiang.YiQiangPayUtil;
import com.cn.flypay.utils.yiqiang2.YiQiang2Config;
import com.cn.flypay.utils.yiqiang2.YiQiang2PayUtil;

/**
 * 亿强第二通道;参考文档9.0.4
 * 
 * @author Administrator
 *
 */
@Service(value = "yiQiang2PaymentService")
public class YiQiang2PaymentServiceImpl extends AbstractChannelPaymentService {

	// private static final String RESP_CODE_999 = null;
	private Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	private UserMerchantConfigService userMerchantConfigService;
	@Autowired
	UserMerchantReportService userMerchantReportService;
	@Autowired
	UserOrderService userOrderService;
	@Autowired
	UserCardService userCardService;
	// @Autowired
	// private CreditCardReportService creditCardReportService;
	// @Autowired
	// private BaseDao<TuserMerchantConfig> userMerchantConfigDao;
	@Autowired
	private BaseDao<TuserCard> userCardDao;
	@Autowired
	private BaseDao<Tuser> userDao;
	@Autowired
	private BaseDao<TcreditCardReport> creditCardReportDao;
	@Autowired
	private BaseDao<TYiQiangMerchantReport> yiQiangMerchReportDao;
	@Autowired
	private BaseDao<TYiQiang2MerchantReport> yiQiang2MerchReportDao;
	@Autowired
	private ServiceMerchantService serviceMerchantService;
	@Autowired
	UserSettlementConfigService userSettlementConfigService;
	
	
	//TODO
	@Override
	public Map<String, Object> createSubMerchant(ServiceMerchant sm, User user) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userHql = " select u from Tuser u where u.id=" + user.getId();
		Tuser tuser = userDao.get(userHql);
		if (tuser == null) {
			LOG.error("YiQiang2 register Merchant Failed, Cant Found User userId={},loginName={}", user.getId(), user.getLoginName());
			return null;
		}
		TuserCard settleCard = userCardDao.get("select t from TuserCard t left join t.user u where t.isSettlmentCard=1 and u.id=" + tuser.getId());
		if (settleCard == null) {
			LOG.error("YiQiang2 register Merchant Failed, Cant Found Settle Card userId={},loginName={}", user.getId(), user.getLoginName());
			return null;
		}

		JSONObject conf = JSONObject.parseObject(sm.getConfig());
		Map<String, String> params = new HashMap<String, String>();
		// 新增参数
		params.put("trnCode_01", YiQiang2Config.REGISTER_MERENT);
		params.put("pdtCode_02", "B1");
		params.put("trnDate_03", DateUtil.getyyyyMMddToString());
		params.put("trnTime_04", DateUtil.getHHMMSSToString());
		

		String preMerTrce = YiQiang2Config.ORG_CODE_TEST + DateUtil.getyyyyMMddToString();
		Long sufLen = (long) (24 - preMerTrce.length());
		String randomStr = String.valueOf(System.currentTimeMillis());
		String sufMerTrce =  randomStr.substring((int) (randomStr.length()-sufLen));
		params.put("merTrce_05", preMerTrce + sufMerTrce);//
		
		String frtMrNo =  StringUtil.rightPad(String.valueOf(tuser.getId()), 15, "Y");
		System.out.println(frtMrNo);
		params.put("frtMrno_06", frtMrNo);
		String merName = StringUtil.isEmpty(user.getMerchantName()) == false ? user.getMerchantName() : "个体" + user.getRealName();
		params.put("merName_23", merName);
//		params.put("relName_24", user.getRealName());
		params.put("relName_24", "张三");
		params.put("merStat_25", "310000");
		params.put("merCity_26", "310000");
		params.put("merAddr_27", user.getAddress());
		params.put("crtType_28", "01");
//		params.put("crtIdno_29", user.getIdNo());
		params.put("crtIdno_29", "510265790128303");
//		params.put("mobPhoe_30", settleCard.getPhone());
		params.put("mobPhoe_30", "18100000000 ");
//		params.put("accIdno_31", settleCard.getCardNo());
		params.put("accIdno_31", "6226090000000048");
//		params.put("accName_32", user.getRealName());
		params.put("accName_32", "张三");
		params.put("accType_33", "B");
//		params.put("bnkName_34", settleCard.getBranchName());
		params.put("bnkName_34", "招商银行");
		params.put("bnkCode_35", "308584000013");
		// params.put("opnName_36", settleCard.getBank().getBankName());
		// params.put("opnCode_37", "");
		// params.put("opnStat_38", "");
		// params.put("opnCity_39", "");
		params.put("oprFlag_40", "A");
		params.put("drawFee_41", "0");
		params.put("drawRate_42", "0.0000");
		params.put("conFee_43", "200");
		UserSettlementConfig config = userSettlementConfigService.getByUserId(user.getId());
		String d0feerat = config.getInputFeeD0ZtYinlianJf().setScale(4, RoundingMode.DOWN).toString();// 亿强规定小数点最多4位
		params.put("conRate_44", "0.005");
		
//		交易手续费扣率必须大于或等于0.005
		params.put("conFmax_45", "99999900");
		params.put("conFmin_46", "0");

		LOG.info("Register YiQiang2 Merchant D0 params={}", JSON.getDefault().toJSONString(params));

		TYiQiang2MerchantReport yiQiang2MerchantReport = new TYiQiang2MerchantReport();
		fillYiQiang2MerchantReport(yiQiang2MerchantReport, params);
		yiQiang2MerchantReport.setUserId(user.getId());

		String applyResult = YiQiang2PayUtil.registerYiQiang2Mer(params);
		Map<String, String> applyResultMap = JSONObject.parseObject(applyResult, Map.class);

		yiQiang2MerchantReport.setTrnSeqn_18(applyResultMap.get("trnSeqn_18"));
		yiQiang2MerchantReport.setRespCode_19(applyResultMap.get("respCode_19"));
		yiQiang2MerchantReport.setRspMsg_20(applyResultMap.get("rspMsg_20"));
		String comMrno_07 = applyResultMap.get("comMrno_07");
//		String respCode_19 = applyResultMap.get("respCode_19");
		yiQiang2MerchantReport.setComMrno_07(comMrno_07);

		if (StringUtil.isNotEmpty(comMrno_07)) {
			yiQiang2MerchReportDao.save(yiQiang2MerchantReport);
			resultMap.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
		} else {
			resultMap.put("respCode", GlobalConstant.RESP_CODE_012);
		}

		resultMap.put("applyResult", applyResult);
		resultMap.put("merchantReport", yiQiang2MerchantReport);
		return resultMap;
	}
	
	@Override
	public JSONObject sendSmsCode(Map<String, String> params) {
		return null;
	}

	private void fillYiQiang2MerchantReport(TYiQiang2MerchantReport merc, Map<String, String> params) {

		

		// private String merStat_25;
		// private String merCity_26;
		// private String merAddr_27;
		// private String accName_32;
		// private String accType_33;
		// private String bnkName_34;
		// private String bnkCode_35;
		// private String drawFee_41;
		// private String drawRate_42;
		merc.setTrnCode_01(params.get("trnCode_01"));
		merc.setMerTrce_05(params.get("merTrce_05"));
		merc.setFrtMrNO_06(params.get("frtMrno_06"));
		merc.setMerName_23(params.get("merName_23"));
		merc.setRelName_24(params.get("relName_24"));
		merc.setCrtType_28(params.get("crtType_28"));
		merc.setCrtIdno_29(params.get("crtIdno_29"));
		merc.setMobPhoe_30(params.get("mobPhoe_30"));
		merc.setAccIdno_31(params.get("accIdno_31"));
		merc.setConFee_43(params.get("conFee_43"));
		merc.setConRate_44(params.get("conRate_44"));
	}

	/**
	 * TODO 亿强2银联直通车
	 */
	@Override
	public Map<String, String> createUnipayOnlineThroughOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer agentType, String des) {
		// 1 判断表TYiQiangMerchantReport 是否存在记录
		Map<String, String> returnMap = new HashMap<String, String>();

		TYiQiang2MerchantReport merchReport = null;
		String loginName = user.getLoginName();
		Long userId = user.getId();
		String merchReportHql = " select r from TYiQiang2MerchantReport r where r.userId=" + userId;
		merchReport = yiQiang2MerchReportDao.get(merchReportHql);
		if (merchReport == null) {// createSubMerchant
			ServiceMerchant sm = serviceMerchantService.findServiceMerchant("YIQIANGJF");
			Map<String, Object> resultMap = createSubMerchant(sm, user);
			String respCode = resultMap.get("respCode").toString();
			String applyResult = resultMap.get("applyResult").toString();
			if (GlobalConstant.RESP_CODE_SUCCESS.equals(respCode)) {
				merchReport = (TYiQiang2MerchantReport) resultMap.get("merchantReport");
			} else {
				LOG.info("YiQiang2 create Merchant Fail For userId={},loginName={},applyResult={}", user.getId(), user.getLoginName(), applyResult);
				returnMap.put("flag", respCode);
				returnMap.put("flagMSG", "您好,目前交易过于繁忙,请稍后或明日再操作!");
				return returnMap;
			}
		}

		if (merchReport == null || StringUtil.isEmpty(merchReport.getComMrno_07())) {
			LOG.info("Cannt Found YiQiang2 merchReport For userId={},loginName={}", user.getId(), user.getLoginName());
			returnMap.put("flag", GlobalConstant.RESP_CODE_085);
			returnMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_085));
			return returnMap;
		}
		LOG.info("Found YiQiang2 merchReport userId={}, comMrNo={}", user.getId(), merchReport.getComMrno_07());
		
		Map<String, String> paySmsMap = sendPaySms(merchReport, user, cpr, inputAccType, cardId, frontUrl, money, transPayType, agentType, des);
		
//		trnCode_01  R   
//		返回码  respCode_19  M   
//		返回消息  rspMsg_20  M   
//		合作商户号  comMrno_07  R   
//		商户流水号  merTrce_05  R   
//		订单时间  odrTime_08  R   
//		商户订单号  odrIdno_09  R   
//		系统流水号  trnSeqn_18  M  短信流水号 
//		以下信息在返回码为 00 时返回 
//		状态码  trnStus_21  C   
//		状态信息  trnMsg_22  C  
//		{"trnCode_01":"QKPAYSMS","respCode_19":"00","rspMsg_20":"短信验证码发送成功","comMrno_07":"528888800004496","merTrce_05":"201810022018042797084212","odrTime_08":"20180427104444","odrIdno_09":"201810022018042797084212","trnSeqn_18":"28087808","trnStus_21":"03","trnMsg_22":"00-短信验证码发送成功"}
		String respCode_19 = paySmsMap.get("respCode_19");
		String odrIdno_09 = paySmsMap.get("odrIdno_09");
		String odrTime_08 = paySmsMap.get("odrTime_08");
		String trnSeqn_18 = paySmsMap.get("trnSeqn_18");
		if("00".equals(respCode_19)) {
			returnMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
//			returnMap.put("flagMSG", "您好,目前交易过于繁忙,请稍后或明日再操作!");
			returnMap.put("type", "1");
			returnMap.put("transactionId", odrIdno_09);
			returnMap.put("payAmount", "222.00");
			returnMap.put("payCardId", "12");
			returnMap.put("orderId", odrTime_08+"_"+trnSeqn_18);
			returnMap.put("type", "1");
		} else {
			returnMap.put("flag", GlobalConstant.RESP_CODE_999);
			returnMap.put("flagMSG", "发短信不成功");
		}
		
				
		return returnMap;
	}

	private Map<String, String> sendPaySms(TYiQiang2MerchantReport merchantReport,User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer agentType, String des) {

//		交易码     M  固定值：QKPAYSMS 
//		产品类型  pdtCode_02  M   
//		交易日期  trnDate_03  M   
//		交易时间  trnTime_04  M   
//		商户流水号  merTrce_05  M   
//		合作商户号  comMrno_07  M   
//		订单时间  odrTime_08  M   
//		商户订单号  odrIdno_09  M   
//		订单币种  ccyCode_12  M  默认：156 
//		订单金额  odrAmt_10  M   
//		真实姓名  relName_24  M  如果需同名校验，真实姓名和结算户名必须一致 
//		证件类型  crtType_28  M  默认：01   
//		证件号码  crtIdno_29  M     
//		总行名称  bnkName_34  M   
//		总行联行号  bnkCode_35  M   
//		卡号  accCard_13  M  消费账户 
//		户名  accName_32  M  消费账户名称 
//		账号类型  accType_33  M     
//		手机号码  mobPhoe_30  M  银行预留手机号码 
//		卡密码  pasWord_14  C   
//		CVN2  accCvn2_16  C  账号类型为贷记卡，必填 
//		卡有效期  valiDate_15  C  账号类型为贷记卡，必填 

		Map<String, String> params = new HashMap<String, String>();
		// TODO 新增参数
		params.put("trnCode_01", YiQiang2Config.PAY_SMS);
		params.put("pdtCode_02", "B1");
		params.put("trnDate_03", DateUtil.getyyyyMMddToString());
		params.put("trnTime_04", DateUtil.getHHMMSSToString());
		
		String preMerTrce = YiQiang2Config.ORG_CODE_TEST  + DateUtil.getyyyyMMddToString();
		Long sufLen = (long) (24 - preMerTrce.length());
		String randomStr = String.valueOf(System.currentTimeMillis());
		String sufMerTrce =  randomStr.substring((int) (randomStr.length()-sufLen));
		String merTrce = preMerTrce + sufMerTrce;
		
		createThroughOrder(merchantReport, user, cpr, inputAccType, cardId, frontUrl, money, transPayType, agentType, merTrce);
		
		params.put("merTrce_05", merTrce);// 商户名
		
		params.put("comMrno_07", merchantReport.getComMrno_07());
		
		params.put("odrTime_08", DateUtil.convertCurrentDateTimeToString());
		//
		params.put("odrIdno_09", merTrce);
		
		params.put("ccyCode_12", "156");
		
		params.put("odrAmt_10","10001");//分
		
		params.put("relName_24", merchantReport.getRelName_24());
		params.put("merStat_25", "310000");
		params.put("merCity_26", "310000");
		params.put("merAddr_27", "上海市纳贤路700号");
		params.put("crtType_28", "01");
//		params.put("crtIdno_29", "310115198811254020");
		params.put("crtIdno_29", "510265790128303");
		
//		params.put("bnkName_34", "民生银行");
		params.put("bnkName_34", "华夏银行");
		params.put("bnkCode_35", "304100040000");
		
//		params.put("accCard_13", "4816990029553516");
		params.put("accCard_13", "6226388000000095");
		params.put("accName_32", merchantReport.getRelName_24());
		params.put("accType_33", "A");
		params.put("mobPhoe_30", "18100000000");
//		params.put("pasWord_14", "111222");//c
		params.put("accCvn2_16", "248");//c
		params.put("valiDate_15", "1912");//c
			
		LOG.info("Send SMS YiQiang2 D0 params={}", JSON.getDefault().toJSONString(params));

		String applyResult = YiQiang2PayUtil.sendYiQiang2Sms(params);
//		trnCode_01  R   
//		返回码  respCode_19  M   
//		返回消息  rspMsg_20  M   
//		合作商户号  comMrno_07  R   
//		商户流水号  merTrce_05  R   
//		订单时间  odrTime_08  R   
//		商户订单号  odrIdno_09  R   
//		系统流水号  trnSeqn_18  M  短信流水号 
//		以下信息在返回码为 00 时返回 
//		状态码  trnStus_21  C   
//		状态信息  trnMsg_22  C   
		Map<String, String> applyResultMap = JSONObject.parseObject(applyResult, Map.class);
		
		return applyResultMap;
	}
	
	/**
	 * 发起申孚直通车请求
	 */
	private Map<String, String> createThroughOrder(TYiQiang2MerchantReport merchReport, User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer agentType, String orderNo) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
//			String orderNo = "WLBZTC" + commonService.getUniqueOrderByType(UserOrder.trans_type.YLZXJ.name(), user.getId());
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);

			// 判断是不是信用卡
			if (card == null || "X".equalsIgnoreCase(card.getCardType()) == false) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_054);
				return resultMap;
			}

			String desc = user.getRealName() + "_" + user.getLoginName() + "正在用" + card.getCardNo() + "支付" + money + "元";
			userOrderService.createTransOrder(user.getId(), orderNo, null, null, UserOrder.trans_type.YLZXJ.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType, channelService.get(cpr.getChannel().getId()),
					inputAccType, agentType);
			resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
			resultMap.put("orderNo", orderNo);
			// resultMap.put("orderType", "openCard");
			return resultMap;
		} catch (Exception e) {
			LOG.error("发送银联在线支付请求出错", e);
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			return resultMap;
		}
	}

	// 查询订单
	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {
		TuserOrder tOrder = userOrderService.getTorderByOrderNo(orderNum);
		if (tOrder != null && tOrder.getUser() != null && tOrder.getUser().getId() != null) {
			try {
				Long userId = tOrder.getUser().getId();
				String reportHql = "select r from TYiQiangMerchantReport r where r.frontMid='" + userId + "'";
				TYiQiangMerchantReport report = yiQiangMerchReportDao.get(reportHql);
				String merNo = report.getMerNo();
				Map<String, String> params = new HashMap<String, String>();

				params.put("tranType", YiQiangApplication.ORDERQUERY_CONQRY);
				params.put("merTrace", "MRG" + YiQiangApplication.ORG_CODE_TEST + DateUtil.getyyyyMMddHHmmssStringFromDate(new Date()));
				params.put("merNo", merNo);
				params.put("orderNo", orderNum);
				LOG.info("Search YiQiang Order params={}", JSON.getDefault().toJSONString(params));

				String applyResult = YiQiangPayUtil.searchOrder(params);
				// TODO
				LOG.error("----Search YiQiang Result={}", applyResult);
			} catch (Exception e1) {
				LOG.error("----银联订单回调异常", e1);
			}
		} else {
			LOG.info("亿强银联订单：" + orderNum + "信息不完整。");
		}
		return null;
	}

	public static void main(String[] args) {

		String preMerTrce = YiQiangApplication.ORG_CODE + DateUtil.getyyyyMMddToString();
		Long sufLen = (long) (24 - preMerTrce.length());
		String randomStr = String.valueOf(System.currentTimeMillis());
		String sufMerTrce =  randomStr.substring((int) (randomStr.length()-sufLen));
		
		
		System.out.println("Target Str=" + preMerTrce + sufMerTrce );
		System.out.println("Target Len=" + (preMerTrce + sufMerTrce).length() );
		
	}

}
