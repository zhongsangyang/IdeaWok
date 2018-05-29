package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
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

@Service(value = "yiQiangPaymentService")
public class YiQiangPaymentServiceImpl extends AbstractChannelPaymentService {
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
	private ServiceMerchantService serviceMerchantService;
	@Autowired
	UserSettlementConfigService userSettlementConfigService;

	@Override
	public Map<String, Object> createSubMerchant(ServiceMerchant sm, User user) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 交易码 tranType String M HPMERENT
		// 商户流水 merTrace String M 须保证流水唯一
		// 前端商户号 frontMid String M 须保证唯一
		// 商户名称 merName String M
		// 姓名 realName String M 如果需同名校验，姓名和结算户名必须一致
		// 商户所在省份 merState String M 参照7.1省市数据格式
		// 商户所在城市 merCity String M 参照7.1省市数据格式
		// 商户所在详细地址 merAddress String M 除省份和城市之外的地址内容
		// 证件类型 certType String M 01：身份证；02：军官证；03：护照；04：户口簿；05：回乡证；06：其他
		// 证件号 certId String M
		// 手机号 mobile String M
		// 结算账号 accountId String M 如果需同名校验，姓名和结算户名必须一致
		// 结算户名 accountName String M
		// 总行名称 bankName String M
		// 总行联行号 bankCode String M
		// 开户行全称 openBName String O
		// 开户行联行号 openBCode String O
		// 开户行省份 openBState String O
		// 开户行城市 openBCity String O
		// 身份证正面照片 posCemage String O 文件流
		// 身份证反面照片 backCemage String O 文件流
		// 手持身份证照片 handCemage String O 文件流
		// 营业场所照片一 firBuzmage String O 文件流
		// 营业场所照片二 secBuzmage String O 文件流
		// 营业执照照片 buzLicmage String O 文件流
		// 开户许可证照片 openPemage String O 文件流
		// 操作标识 operFlag String M
		// A：新增；M：修改全部；M01:修改商户基本信息；M02:修改结算卡信息；M03:修改T0费率；M04:修改T1费率；M05:修改图片信息（暂时不支持）
		// 单笔D0提现交易手续费 t0drawFee String C 如0.2元/笔则填0.2
		// D0提现交易手续费扣率 t0drawRate String C 如0.6%笔则填0.006；小数点后最多不超过4位
		// 单笔消费交易手续费 t1consFee String M 如0.2元/笔则填0.2
		// 消费交易手续费扣率 t1consRate String M 如0.6%笔则填0.006；小数点后最多不超过4位
		// 报备商户
		String userHql = " select u from Tuser u where u.id=" + user.getId();
		Tuser tuser = userDao.get(userHql);
		JSONObject conf = JSONObject.parseObject(sm.getConfig());
		Map<String, String> params = new HashMap<String, String>();
		// 新增参数
		params.put("tranType", YiQiangApplication.REGISTER_MERENT);
		params.put("tranDate", DateUtil.getyyyyMMddToString());
		params.put("tranTime", DateUtil.getHHMMSSToString());

		// params.put("merTrace", DateUtil.getyyyyMMddHHmmssStringFromDate(new
		// Date()));// 商户名
		params.put("merTrace", "MRG" + YiQiangApplication.ORG_CODE + DateUtil.getyyyyMMddHHmmssStringFromDate(new Date()));// 商户名
		params.put("frontMid", user.getId().toString());
		String merName = StringUtil.isEmpty(user.getMerchantName()) == false ? user.getMerchantName() : "个体" + user.getRealName();
		params.put("merName", merName);
		params.put("realName", user.getRealName());
		params.put("merState", conf.get("merState").toString());
		params.put("merCity", conf.get("merCity").toString());
		params.put("merAddress", conf.get("merAddress").toString());
		params.put("certType", "01");
		params.put("certId", user.getIdNo());
		params.put("mobile", user.getLoginName());
		Map<String, Object> argu = new HashMap<String, Object>();
		argu.put("userID", user.getId());
		String userCardHql = "select ut from TuserCard ut left join ut.user u left join ut.bank b where ut.status=0 and ut.isSettlmentCard=1 and u.id = :userID ";
		List<TuserCard> userCards = userCardDao.find(userCardHql, argu);
		if (userCards == null || userCards.isEmpty()) {
			LOG.info("YiQiang register Merchant Failed, Cant Found Settle Card userId={},loginName={}", user.getId(), user.getLoginName());
			return null;
		}
		TuserCard userCard = (TuserCard) userCards.get(0);
		params.put("accountId", userCard.getCardNo());
		params.put("accountName", user.getRealName());
		String bankName = StringUtil.isEmpty(userCard.getBranchName()) ? userCard.getBank().getBankName() : userCard.getBranchName();
		params.put("bankName", bankName);
		params.put("bankCode", conf.get("bankCode").toString());
		params.put("operFlag", "A");
		params.put("t0drawFee", "0");
		params.put("t0drawRate", "0.0000");
		params.put("t1consFee", "2");
		UserSettlementConfig config = userSettlementConfigService.getByUserId(user.getId());
		String d0feerat = config.getInputFeeD0Yinlian().setScale(4, RoundingMode.DOWN).toString();// 亿强规定小数点最多4位
		params.put("t1consRate", d0feerat);
		LOG.info("Register YiQiang D0 params={}", JSON.getDefault().toJSONString(params));

		TYiQiangMerchantReport yiQiangMerchantReport = new TYiQiangMerchantReport();
		fillYiQiangMerchantReport(yiQiangMerchantReport, params);
		
		String applyResult = YiQiangPayUtil.registerYiQiangMer(params);
		Map<String, String> applyResultMap = JSONObject.parseObject(applyResult, Map.class);

		yiQiangMerchantReport.setRespCode(applyResultMap.get("respCode"));
		yiQiangMerchantReport.setRespMsg(applyResultMap.get("respMsg"));
		yiQiangMerchantReport.setUserType(user.getUserType().toString());
		String merNo = applyResultMap.get("merNo");
		yiQiangMerchantReport.setMerNo(merNo);

		if (StringUtil.isNotEmpty(merNo)) {
			yiQiangMerchReportDao.save(yiQiangMerchantReport);
			resultMap.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
		} else {
			resultMap.put("respCode", GlobalConstant.RESP_CODE_012);
		}
		resultMap.put("applyResult", applyResult);
		resultMap.put("merchantReport", yiQiangMerchantReport);
		
		
		
		
		return resultMap;
	}

	private void fillYiQiangMerchantReport(TYiQiangMerchantReport merc, Map<String, String> params) {
		merc.setAccountId(params.get("accountId"));
		merc.setAccountName(params.get("accountName"));
		merc.setBankCode(params.get("bankCode"));
		merc.setBankName(params.get("bankName"));
		merc.setCertId(params.get("certId"));
		merc.setCertType(params.get("certType"));
		merc.setFrontMid(params.get("frontMid"));
		// merc.setId(id);
		merc.setMerAddress(params.get("merAddress"));
		merc.setMerCity(params.get("merCity"));
		merc.setMerName(params.get("merName"));
		// merc.setMerNo("");
		merc.setMerState(params.get("merState"));
		merc.setMerTrace(params.get("merTrace"));
		merc.setMobile(params.get("mobile"));
		merc.setOperFlag(params.get("operFlag"));
		merc.setRealName(params.get("realName"));
		// merc.setRespCode("");
		// merc.setRespMsg("");
		merc.setT1consFee(params.get("t1consFee"));
		merc.setT1consRate(params.get("t1consRate"));
		// merc.setVersion(version);
	}

	/**
	 * 申孚银联直通车
	 */
	@Override
	public Map<String, String> createUnipayOnlineThroughOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer agentType, String des) {
		// 1 判断表TYiQiangMerchantReport 是否存在记录
		Map<String, String> returnMap = new HashMap<String, String>();

		TYiQiangMerchantReport merchReport = null;
		String loginName = user.getLoginName();
		String merchReportHql = " select r from TYiQiangMerchantReport r where r.mobile=" + loginName;
		merchReport = yiQiangMerchReportDao.get(merchReportHql);
		if (merchReport == null) {// createSubMerchant
			ServiceMerchant sm = serviceMerchantService.findServiceMerchant("YIQIANG");
			Map<String, Object> resultMap = createSubMerchant(sm, user);
			String respCode = resultMap.get("respCode").toString();
			String applyResult = resultMap.get("applyResult").toString();
			if (GlobalConstant.RESP_CODE_SUCCESS.equals(respCode)) {
				merchReport = (TYiQiangMerchantReport) resultMap.get("merchantReport");
			} else {
				LOG.info("YiQiang apply Merchant Fail For userId={},loginName={},applyResult={}", user.getId(), user.getLoginName(), applyResult);
				returnMap.put("flag", respCode);
				returnMap.put("flagMSG", "您好,目前交易过于繁忙,请稍后或明日再操作!");
				return returnMap;
			}
		}

		if (merchReport == null || StringUtil.isEmpty(merchReport.getMerNo())) {
			LOG.info("Cannt Found YiQiang merchReport For userId={},loginName={}", user.getId(), user.getLoginName());
			returnMap.put("flag", GlobalConstant.RESP_CODE_085);
			returnMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_085));
			return returnMap;
		}
		// LOG.info("Found merchReport ={}", merchReport.getMercId());
		// 2卡的报备状态 00无效10校验成功未开通快捷01未校验成功开通快捷11即校验成功又开通快捷
		// String _orderNo = "SFCCR" + user.getId() + "_" + merchReport.getId()
		// + "_" + cardId;// 卡报备订单号
		// String cardReportHql = " select c from TcreditCardReport c where
		// c.orderNo='" + _orderNo + "'";
		// // LOG.info("Search TcreditCardReport orderNo={}", _orderNo);
		// TcreditCardReport cardReport =
		// creditCardReportDao.get(cardReportHql);
		returnMap = createThroughOrder(merchReport, user, cpr, inputAccType, cardId, frontUrl, money, transPayType, agentType, des);

		return returnMap;
	}

	/**
	 * 发起申孚直通车请求
	 */
	private Map<String, String> createThroughOrder(TYiQiangMerchantReport merchReport, User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer agentType, String des) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String orderNum = "YiQZTC" + commonService.getUniqueOrderByType(UserOrder.trans_type.YLZXE.name(), user.getId());
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			Map<String, String> map = new TreeMap<String, String>();
			// 判断是不是信用卡
			if (card == null || "X".equalsIgnoreCase(card.getCardType()) == false) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_054);
				return resultMap;
			}
			JSONObject config = cpr.getConfig();
			map.put("tranType", YiQiangApplication.SENDORDER_CONPAY);
			map.put("tranDate", DateUtil.getyyyyMMddToString());
			map.put("tranTime", DateUtil.getHHMMSSToString());
			map.put("merTrace", "MRG" + YiQiangApplication.ORG_CODE + DateUtil.getyyyyMMddHHmmssStringFromDate(new Date()));// 商户名
			map.put("merNo", merchReport.getMerNo());
			map.put("orderNo", orderNum);
			map.put("currencyCode", "156");
			BigDecimal orderAmount = new BigDecimal(money);
			orderAmount = orderAmount.multiply(new BigDecimal(100));
			orderAmount = orderAmount.setScale(0);
			map.put("orderAmount", orderAmount.toString());// 单位(分)
			map.put("name", user.getRealName());
			map.put("idNumber", user.getIdNo());
			map.put("certType", "01");
			String accType = null;
			if (StringUtil.isNotEmpty(card.getCardType()) && "J".equalsIgnoreCase(card.getCardType())) {
				accType = "DEBIT";
			} else {
				accType = "CREDIT";
			}
			map.put("accType", accType);
			map.put("accNo", card.getCardNo());
			map.put("telNo", user.getLoginName());
			map.put("productType", "100000");
			map.put("paymentType", "2008");
			map.put("frontUrl", frontUrl);
			JSONObject _config = cpr.getConfig();
			map.put("backUrl", _config.getString("ylaccount.notifyUrl"));

			// map.put("accNo", card.getCardNo());
			// map.put("txnAmt", String.valueOf(money));
			// map.put("orderNo", orderNum);
			// map.put("frontUrl", frontUrl);
			// map.put("notifyUrl", config.getString("ylaccount.notifyUrl"));
			LOG.info("Send YiQiang D0 payment arguments={}", JSON.getDefault().toJSONString(map));

			String response = YiQiangPayUtil.sendPayYiQiangD0(map);
			Map<String, String> jsonMap = JSONObject.parseObject(response, Map.class);
			if (jsonMap != null && StringUtil.isNotEmpty(jsonMap.get("html"))) {
				String desc = user.getRealName() + "_" + user.getLoginName() + "正在用" + card.getCardNo() + "支付" + money + "元";
				userOrderService.createTransOrder(user.getId(), orderNum, null, null, UserOrder.trans_type.YLZXE.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType, channelService.get(cpr.getChannel().getId()),
						inputAccType, agentType);
				resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
				// resultMap.put("html",
				// "http://18b2z84712.51mypc.cn:23746/flypayfx/mobile/YiQiangResponse?result="
				// + jsonMap.get("html"));// 测试
				resultMap.put("html", "https://bbpurse.com/flypayfx/mobile/YiQiangResponse?result=" + jsonMap.get("html"));// 测试
			} else {
				LOG.info("Send YiQiang D0 payment Fail loginName={},response={}", user.getLoginName(), response);
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			}
		} catch (Exception e) {
			LOG.error("发送银联在线支付请求出错", e);
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
		}
		return resultMap;
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

		Map<String, String> params = new HashMap<String, String>();
		params.put("tranType", YiQiangApplication.ORDERQUERY_CONQRY);
		params.put("merTrace", "MRG" + YiQiangApplication.ORG_CODE + DateUtil.getyyyyMMddHHmmssStringFromDate(new Date()));
		params.put("merNo", "528888800811021");
		params.put("orderNo", "YiQZTCYLZXE201801181629281500000000002");
		System.out.println("Search YiQiang Order params=" + JSON.getDefault().toJSONString(params));
		String applyResult = YiQiangPayUtil.searchOrder(params);
		// TODO
		System.out.println("Search YiQiang Result=" + applyResult);
	}
}
