package com.cn.flypay.service.payment.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TcreditCardReport;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.model.sys.TuserMerchantReport;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.model.util.JSON;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserMerchantConfigService;
import com.cn.flypay.service.sys.UserMerchantReportService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.shenfu.ApplicationBase;
import com.cn.flypay.utils.shenfu.ShenFuPayUtil;

@Service(value = "shenFuPaymentService")
public class ShenFuPaymentServiceImpl extends AbstractChannelPaymentService {
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
	private BaseDao<TuserMerchantReport> userMerchReportDao;
	@Autowired
	private BaseDao<TcreditCardReport> creditCardReportDao;
	@Autowired
	UserSettlementConfigService userSettlementConfigService;

	@Override
	public Map<String, String> addCreditCard(Map<String, String> params) {
		// TODO 添加申孚信用卡
		LOG.info("-------申孚通道--商户新增信用卡-start");
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String userId = params.get("userId");// 用户ID
			String merchReportId = params.get("merchReportId");// 商户报备表ID
			String mercId = params.get("mercId");// 商户报备表商户号
			String cardId = params.get("cardId");// 卡号ID
			String privateKey = params.get("privateKey");// 私钥
			String addNew = params.get("addNew");// 是否新加记录
			String orderNo = params.get("orderNo");// TcreditCardReport的orderNo
			String cardReportId = params.get("cardReportId");// TcreditCardReport的ID

			// 校验信用卡信息
			Long cardIdL = Long.valueOf(cardId);
			String orderHql = "select c from TuserCard c where c.id=" + cardIdL;
			TuserCard userCard = userCardDao.get(orderHql);
			if (userCard == null) {
				LOG.info("找不到记录,无法报备申孚CreditCard,userId={},mercId={},cardId={}", new Object[] { userId, mercId, cardId });
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "找不到交易卡记录");
				return resultMap;
			} else if ("X".equals(userCard.getCardType()) == false || StringUtil.isEmpty(userCard.getCardNo())) {
				LOG.info("无法报备申孚CreditCard卡信息有误,cardId={},cardType={},cardNo={}", new Object[] { cardId, userCard.getCardType(), userCard.getCardNo() });
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "非信用卡或卡号不准确");
				return resultMap;
			}

			// 添加记录addNew==1
			TcreditCardReport cardReport = null;
			if ("1".equals(addNew)) {
				cardReport = new TcreditCardReport();
				cardReport.setCardId(Long.valueOf(cardId));
				cardReport.setBranchName(userCard.getBranchName());
				cardReport.setCardNo(userCard.getCardNo());
				cardReport.setChannelName("SHENFUZTC");
				cardReport.setCreateTime(new Date());
				cardReport.setMerchReportId(Long.valueOf(merchReportId));
				cardReport.setMercId(mercId);
				cardReport.setUserId(Long.valueOf(userId));
				cardReport.setStatus("00");
				cardReport.setOrderNo(orderNo);
				creditCardReportDao.save(cardReport);
			} else {
				String cardReportHql = "select r from TcreditCardReport r where r.id=" + Long.valueOf(cardReportId);
				cardReport = creditCardReportDao.get(cardReportHql);
			}

			String userHql = " select u from Tuser u where u.id=" + Long.valueOf(userId);
			Map<String, String> reqMap = new TreeMap<String, String>();
			Tuser tuser = userDao.get(userHql);
			reqMap.put("accNo", userCard.getCardNo());
			reqMap.put("certNo", tuser.getIdNo());
			reqMap.put("mercId", mercId);
			reqMap.put("name", tuser.getRealName());
			LOG.info("SHNEFU Validate Credit card mercId={},accNo={},certNo={},name={},privateKey={}", new Object[] { reqMap.get("mercId"), reqMap.get("accNo"), reqMap.get("certNo"), reqMap.get("name"), privateKey });
			String response = ShenFuPayUtil.verifyAndReportCreditCard(reqMap, privateKey);
			LOG.info("SHNEFU addCreditCard response info={}", response);
			Map<String, String> retMap = JSON.getDefault().parseToObject(response, Map.class);
			LOG.info("SHNEFU addCreditCard retMap={}", JSONObject.toJSONString(retMap));

			if (retMap == null || retMap.size() < 1) {
				LOG.info("SHNEFU addCreditCard Failed info={}", response);
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "银行卡校验返回失败");
				return resultMap;
			}

			String retCode = retMap.get("retCode");
			String retMsg = retMap.get("retMsg");
			cardReport.setVerifyCode(retCode);
			cardReport.setVerifyMsg(retMsg);
			if ("00".equals(retCode)) {
				String subStr = cardReport.getStatus().substring(1);
				cardReport.setStatus("1" + subStr);
				creditCardReportDao.saveOrUpdate(cardReport);
				resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
				resultMap.put("flagMSG", "银行卡校验成功");
			} else {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "银行卡校验失败");
			}
		} catch (Exception e) {
			LOG.error("-------申孚通道--商户新增信用卡--进程出现错误:", e);
		}
		LOG.info("-------申孚通道--商户新增信用卡-------END");
		return resultMap;
	}

	@Override
	@Transactional
	public UserMerchantConfig createSubMerchant(ServiceMerchant sm, Map<String, String> params) {
		LOG.info("-------申孚通道--创建子商户(进件)-start");
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			JSONObject config = JSONObject.parseObject(sm.getConfig());
			File idcard_front = new File(config.get("idcard_front").toString());
			File idcard_back = new File(config.get("idcard_back").toString());
			File idcard_hold = new File(config.get("idcard_hold").toString());
			File bankcard_front = new File(config.get("bankcard_front").toString());
			File bankcard_back = new File(config.get("bankcard_back").toString());

			// 新增参数
			paramMap.put("agentid", config.get("agentid"));// 平台代理编号
			paramMap.put("mercnam", params.get("mercnam"));// 商户名号
			paramMap.put("mersnm", params.get("mercnam"));// 商户简称
			paramMap.put("comtype", config.get("comtype"));// 公司类型
			paramMap.put("address", config.get("address"));
			paramMap.put("merctel", params.get("merctel"));// 手机号
			paramMap.put("mercemail", params.get("merctel") + config.get("mercemail"));// 邮箱
			paramMap.put("cityid", config.get("cityid"));
			paramMap.put("indregnam", "");// 工商注册名称
			paramMap.put("regcapamt", "0");// 注册资金
			paramMap.put("merchantnature", config.get("merchantnature"));// 商户性质
			paramMap.put("legalperson", params.get("legalperson"));// 法人
			paramMap.put("corporateidentity", params.get("corporateidentity"));// 法人身份证
			paramMap.put("actname", params.get("actname"));// 开户名
			paramMap.put("openbank", config.get("openbank"));// 开户银行
			paramMap.put("actno", params.get("actno"));// 开户账号
			paramMap.put("rcvbanksettleno", config.get("rcvbanksettleno"));// 开户联行号
			paramMap.put("feerat", params.get("feerat"));//
			paramMap.put("crefeerat", params.get("crefeerat"));//
			paramMap.put("d0feerat", params.get("d0feerat"));//
			paramMap.put("d0channelfee", "2");
			paramMap.put("settelfee", "0");
			paramMap.put("img001", idcard_front);
			paramMap.put("img002", idcard_back);
			paramMap.put("img003", idcard_hold);
			paramMap.put("img004", bankcard_front);
			paramMap.put("img005", bankcard_back);
			String regisResult = ShenFuPayUtil.registerShenFuMer(paramMap);
			LOG.info("SHNEFU register merch respone info={}", regisResult);
			Map<String, String> regisMap = JSON.getDefault().parseToObject(regisResult, Map.class);
			if (regisMap == null || regisMap.size() < 1) {
				LOG.info("申孚通道--创建子商户(进件)--创建失败 LoginName={} register Shen Fu 1 failed, INFO=[{}]", params.get("merctel"), JSON.getDefault().toJSONString(paramMap));
				return null;
			}
			String userId = params.get("userId");
			String retCode = regisMap.get("retCode");// 响应码 00:成功
			String retMsg = regisMap.get("retMsg");// 响应信息
			String mercId = regisMap.get("mercId");// 平台注册商户号
			String publicKey = regisMap.get("publicKey");//
			String privateKey = regisMap.get("privateKey");
			String userHql = " from Tuser u where u.id = :userId ";
			Map<String, Object> argu = new HashMap<String, Object>();
			argu.put("userId", Long.valueOf(userId));
			List<Tuser> userEntitys = userDao.find(userHql, argu);

			if (userEntitys == null || userEntitys.size() != 1) {
				LOG.info("申孚通道--创建子商户(进件)--创建失败 LoginName {} register Shen Fu 2 failed, INFO=[{}]", params.get("merctel"), JSON.getDefault().toJSONString(userEntitys));
				return null;
			}

			Tuser userEntity = userEntitys.get(0);
			TuserMerchantReport merchantReport = null;
			if ("00".equals(retCode)) {// 成功: 写入TuserMerchantReport
				merchantReport = new TuserMerchantReport();
				merchantReport.setUserId(Long.valueOf(userId));
				merchantReport.setLoginName((String) paramMap.get("merctel"));
				merchantReport.setMerchantName((String) paramMap.get("mercnam"));
				merchantReport.setMercId(mercId);
				merchantReport.setRetCode(retCode);
				merchantReport.setRetMsg(retMsg);
				merchantReport.setPrivateKey(privateKey);
				merchantReport.setPublicKey(publicKey);
				merchantReport.setSettleCardNo((String) paramMap.get("actno"));
				merchantReport.setChannelName("SHENFUZTC");
				merchantReport.setCreateTime(new Date());
				userMerchReportDao.save(merchantReport);
				LOG.info("申孚通道--创建子商户(进件)--创建成功 userId={}, retMsg=[{}]", userId, retMsg);
			} else if ("96".equals(retCode)) {// 商户已经报备,不返回privateKey
				String searchHql = " select t from TuserMerchantReport t where t.userId = " + Long.valueOf(userId);
				merchantReport = userMerchReportDao.get(searchHql);
				if (merchantReport == null) {
					LOG.error("申孚通道--创建子商户失败3,userId={},retCode=96,retMsg={}", new Object[] { userId, retMsg });
				}
			} else {// 商户报备失败,不返回privateKey
				LOG.error("申孚通道--创建子商户失败4,userId={},retCode={},retMsg={}", new Object[] { userId, retCode, retMsg });
			}

			UserMerchantConfig umc = new UserMerchantConfig();
			umc.setSubMerchantId(mercId);
			umc.setServiceMerchantId(sm.getId());
			umc.setConfig(regisResult);
			umc.setMerchantChannelName("SHENFUZTC");
			umc.setType(520); // 520 银联直通
			umc.setReport(merchantReport);
			LOG.info("-------申孚通道--创建子商户(进件)-end");
			return umc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * TODO 申孚T1方法
	 */
	@Override
	public Map<String, String> createYLZXOrder_v2(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer agentType, String dec) {
		// 1判断表TUserMerchantConfig 是否存在记录
		// 2引入UserMerchantConfigService，获取Tuser 3 调用 createShenFuUserMerchants
		Map<String, String> resultMap = new HashMap<String, String>();
		String response = null;
		try {
			TuserMerchantReport merchReport = null;
			String loginName = user.getLoginName();
			String merchReportHql = " select r from TuserMerchantReport r where r.loginName=" + loginName;
			merchReport = userMerchReportDao.get(merchReportHql);
			if (merchReport == null) {
				// 报备商户
				String userHql = " select u from Tuser u where u.id=" + user.getId();
				Tuser tuser = userDao.get(userHql);
				merchReport = userMerchantConfigService.createShenFuUserMerchants(tuser);
			}

			JSONObject jsonret = null;
			if (merchReport.getPrivateKey2() == null) {
				// 1.5 商户支付功能配置
				ServiceMerchant smm = null;
				List<ServiceMerchant> sml = serviceMerchantService.findAllServiceMerchant("SHENFU");
				for (ServiceMerchant sm : sml) {
					if ("SHENFU".equals(sm.getName())) {
						smm = sm;
					}
				}
				jsonret = mercRateconf(merchReport, user, smm);
			} else {
				jsonret = new JSONObject();
				jsonret.put("retCode", "00");
				jsonret.put("retMsg", "已经配置银联网关");
				jsonret.put("privateKey", merchReport.getPrivateKey2());
			}

			String retCode = jsonret.getString("retCode");
			String retMsg = jsonret.getString("retMsg");
			// String publicKey = jsonret.getString("publicKey");
			String privateKey = jsonret.getString("privateKey");
			if (!"00".equals(retCode)) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMsg", retMsg);
				return resultMap;
			}

			String orderNum = "SF" + commonService.getUniqueOrderByType(UserOrder.trans_type.YLZX.name(), user.getId());
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			JSONObject config = cpr.getConfig();
			Map<String, String> map = new TreeMap<String, String>();

			// 2.1 交易卡实名认证
			JSONObject authJson = authCard(card, merchReport, privateKey);
			if (!"00".equals(authJson.getString("retCode"))) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMsg", "SF卡验失败");
				return resultMap;
			}
			map.put("accNo", card.getCardNo());
			map.put("frontUrl", frontUrl);
			map.put("mercId", merchReport.getMercId());
			map.put("notifyUrl", config.getString("ylaccount.bankUrl"));
			map.put("orderNo", orderNum);
			map.put("txnAmt", String.valueOf(money));
			response = ShenFuPayUtil.sendPayShenFuV2(map, merchReport.getPrivateKey2());
			if (StringUtil.isNotBlank(response)) {
				String desc = user.getRealName() + "_" + user.getLoginName() + "正在用" + card.getCardNo() + "支付" + money + "元";
				userOrderService.createTransOrder(user.getId(), orderNum, null, null, UserOrder.trans_type.YLZX.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType, channelService.get(cpr.getChannel().getId()),
						inputAccType, agentType);
				resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
				resultMap.put("html", "http://101.200.34.95:26370/flypayfx/mobile/ShenFuResponse?result=" + response);// 生产
			} else {
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			}
		} catch (Exception e) {
			LOG.error("发送银联在线支付请求出错", e);
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
		}
		return resultMap;
	}

	private JSONObject authCard(TuserCard card, TuserMerchantReport merchReport, String privateKey) {
		// 添加记录addNew==1
		TcreditCardReport cardReport = null;
		JSONObject retJson = new JSONObject();
		boolean needAuth = false;
		String cardReportHql = "select r from TcreditCardReport r where r.cardId=" + card.getId() + " and r.mercId=" + merchReport.getMercId();
		cardReport = creditCardReportDao.get(cardReportHql);
		if (cardReport != null) {
			if ("1".equals(cardReport.getIsAuth())) {
				retJson.put("retCode", "00");
				return retJson;
			} else {
				needAuth = true;
			}
		} else {
			cardReport = new TcreditCardReport();
			cardReport.setCardId(card.getId());
			cardReport.setBranchName(card.getBranchName());
			cardReport.setCardNo(card.getCardNo());
			cardReport.setChannelName("SHENFUZTC");
			cardReport.setCreateTime(new Date());
			cardReport.setMerchReportId(Long.valueOf(merchReport.getId()));
			cardReport.setMercId(merchReport.getMercId());
			cardReport.setUserId(merchReport.getUserId());
			cardReport.setStatus("00");
			cardReport.setOrderNo("00");
			cardReport.setIsAuth("0");
			creditCardReportDao.save(cardReport);
			needAuth = true;
		}

		if (needAuth) {
			Map<String, String> map = new TreeMap<String, String>();
			map.put("accNo", card.getCardNo());
			map.put("mercId", merchReport.getMercId());
			try {
				String response = ShenFuPayUtil.authCard(map, privateKey);
				LOG.info("SHNEFU authCard response info={}", response);
				retJson = JSONObject.parseObject(response);
				if ("00".equals(retJson.getString("retCode"))) {
					cardReport.setIsAuth("1");
					creditCardReportDao.saveOrUpdate(cardReport);
				}
			} catch (Exception e) {
				retJson.put("retCode", "-1");
			}
		}
		return retJson;
	}

	JSONObject mercRateconf(TuserMerchantReport merchReport, User user, ServiceMerchant smm) throws Exception {
		// agentid 代理商编号 String 是 本平台提供
		// mercid 商户号 String 是 商户入网成功返回的商户号
		// subtype 配置交易子类型 String 是 本平台提供
		// t1flag T1 标志 0：关闭 1：开通（修改） String 是 T1交易标志： 0为关闭T1功能，1为开通T1功能。
		// t1feerate T1 费率信息 String 是 手续费费率信息，存放百分比值，比如0.50%，上传0.50。两位小数
		// d0flag D0 标志 0：关闭 1：开通（修改） String 是 暂不支持D0业务，请填 0 ,不可为空。
		// d0feerat d0费率信息 String 否
		// d0channelfee d0通道手续费 String 否
		Map<String, Object> paramMap = new HashMap<String, Object>();
		JSONObject config = JSONObject.parseObject(smm.getConfig());
		// 新增参数
		paramMap.put("agentid", config.get("agentid"));// 平台代理编号
		paramMap.put("mercid", merchReport.getMercId());// 商户名号
		paramMap.put("subtype", "AF");
		paramMap.put("t1flag", "1");
		UserSettlementConfig config2 = userSettlementConfigService.getByUserId(user.getId());
//		String feerat = config2.getInputFeeYinlian().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString();	
		String feerat = config2.getInputFeeZtYinlianJf().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString();
		paramMap.put("t1feerate", feerat);// 公司要求申孚要用积分的费率
		paramMap.put("d0flag", "0");
		// paramMap.put("d0feerat", "");
		// paramMap.put("d0channelfee", "");
		String jsonresult = ShenFuPayUtil.mercRateconf(paramMap);
		JSONObject json = JSONObject.parseObject(jsonresult);
		LOG.info("mercRateconf Result={}.", json);
		String retCode = json.getString("retCode");
		if ("00".equals(retCode)) {
			String publicKey2 = json.getString("publicKey");
			String privateKey2 = json.getString("privateKey");
			merchReport.setPublicKey2(publicKey2);
			merchReport.setPrivateKey2(privateKey2);
			userMerchReportDao.saveOrUpdate(merchReport);
		}

		return json;
	}

	/**
	 * 申孚银联直通车
	 */
	@Override
	public Map<String, String> createUnipayOnlineThroughOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer agentType, String des) {

		// // 1 判断表TuserMerchantReport 是否存在记录
		// TuserMerchantReport merchReport = null;
		// String loginName = user.getLoginName();
		// String merchReportHql = " select r from TuserMerchantReport r where
		// r.loginName=" + loginName;
		// merchReport = userMerchReportDao.get(merchReportHql);
		// if (merchReport == null) {
		// // 报备商户
		// String userHql = " select u from Tuser u where u.id=" + user.getId();
		// Tuser tuser = userDao.get(userHql);
		// merchReport =
		// userMerchantConfigService.createShenFuUserMerchants(tuser);
		// }
		//
		// Map<String, String> resultMap = new HashMap<String, String>();
		// if (merchReport == null ||
		// StringUtil.isEmpty(merchReport.getMercId())) {
		// LOG.info("Cannt Found merchReport For userId={},loginName={}",
		// user.getId(), user.getLoginName());
		// resultMap.put("flag", GlobalConstant.RESP_CODE_085);
		// resultMap.put("flagMSG",
		// GlobalConstant.map.get(GlobalConstant.RESP_CODE_085));
		// return resultMap;
		// }
		// // LOG.info("Found merchReport ={}", merchReport.getMercId());
		// // 2卡的报备状态 00无效10校验成功未开通快捷01未校验成功开通快捷11即校验成功又开通快捷
		// String _orderNo = "SFCCR" + user.getId() + "_" + merchReport.getId()
		// + "_" + cardId;// 卡报备订单号
		// String cardReportHql = " select c from TcreditCardReport c where
		// c.orderNo='" + _orderNo + "'";
		// // LOG.info("Search TcreditCardReport orderNo={}", _orderNo);
		// TcreditCardReport cardReport =
		// creditCardReportDao.get(cardReportHql);
		//
		// Map<String, String> params = new TreeMap<String, String>();
		// if (cardReport == null ||
		// "0".equals(cardReport.getStatus().substring(0, 1))) {// 0:卡未校验成功
		// // 实名注册信用卡
		// if (cardReport == null) {
		// params.put("addNew", "1");// 新加一条记录
		// } else {
		// params.put("addNew", "0");// 不新加一条记录
		// params.put("cardReportId", String.valueOf(cardReport.getId()));//
		// 不新加一条记录
		// }
		// params.put("userId", String.valueOf(user.getId()));
		// params.put("cardId", String.valueOf(cardId));
		// params.put("merchReportId", String.valueOf(merchReport.getId()));
		// params.put("mercId", String.valueOf(merchReport.getMercId()));
		// params.put("privateKey", merchReport.getPrivateKey());
		// params.put("orderNo", _orderNo);
		// resultMap = addCreditCard(params);
		// // LOG.info("Reprot card ={}", cardId);
		// } else {
		// // 卡已报备,发起交易
		// resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
		// resultMap.put("flagMSG", "银行卡已经校验成功");
		// }
		//
		// if (GlobalConstant.RESP_CODE_SUCCESS.equals(resultMap.get("flag")) ==
		// false) {
		// return resultMap;
		// }
		// // 3校验结算卡是否变更 
		// // Map<String, String> updateResult = updateBankAccountInfo(params);
		//
		// resultMap = createThroughOrder(merchReport, user, cpr, inputAccType,
		// cardId, frontUrl, money, transPayType, agentType, des);
		//
		// return resultMap;

		// 1判断表TUserMerchantConfig 是否存在记录
		// 2引入UserMerchantConfigService，获取Tuser 3 调用 createShenFuUserMerchants
		Map<String, String> resultMap = new HashMap<String, String>();
		String response = null;
		try {
			TuserMerchantReport merchReport = null;
			String loginName = user.getLoginName();
			String merchReportHql = " select r from TuserMerchantReport r where r.loginName=" + loginName;
			merchReport = userMerchReportDao.get(merchReportHql);
			if (merchReport == null) {
				// 报备商户
				String userHql = " select u from Tuser u where u.id=" + user.getId();
				Tuser tuser = userDao.get(userHql);
				merchReport = userMerchantConfigService.createShenFuUserMerchants(tuser);
			}

			JSONObject jsonret = null;
			if (merchReport.getPrivateKey2() == null) {
				// 1.5 商户支付功能配置
				ServiceMerchant smm = null;
				List<ServiceMerchant> sml = serviceMerchantService.findAllServiceMerchant("SHENFU");
				for (ServiceMerchant sm : sml) {
					if ("SHENFU".equals(sm.getName())) {
						smm = sm;
					}
				}
				jsonret = mercRateconf(merchReport, user, smm);
			} else {
				jsonret = new JSONObject();
				jsonret.put("retCode", "00");
				jsonret.put("retMsg", "已经配置银联网关");
				jsonret.put("privateKey", merchReport.getPrivateKey2());
			}

			String retCode = jsonret.getString("retCode");
			String retMsg = jsonret.getString("retMsg");
			// String publicKey = jsonret.getString("publicKey");
			String privateKey = jsonret.getString("privateKey");
			if (!"00".equals(retCode)) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", retMsg);
				return resultMap;
			}

			String orderNum = "SF" + commonService.getUniqueOrderByType(UserOrder.trans_type.YLZX.name(), user.getId());
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			JSONObject config = cpr.getConfig();
			Map<String, String> map = new TreeMap<String, String>();

			// 2.1 交易卡实名认证
			JSONObject authJson = authCard(card, merchReport, privateKey);
			if (!"00".equals(authJson.getString("retCode"))) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "SF卡验失败");
				return resultMap;
			}
			map.put("accNo", card.getCardNo());
			map.put("frontUrl", frontUrl);
			map.put("mercId", merchReport.getMercId());
			map.put("notifyUrl", config.getString("ylaccount.notifyUrl"));
			map.put("orderNo", orderNum);
			map.put("txnAmt", String.valueOf(money));
			response = ShenFuPayUtil.sendPayShenFuV2(map, merchReport.getPrivateKey2());
			if (StringUtil.isNotBlank(response)) {
				String desc = user.getRealName() + "_" + user.getLoginName() + "正在用" + card.getCardNo() + "支付" + money + "元";
				userOrderService.createTransOrder(user.getId(), orderNum, null, null, UserOrder.trans_type.YLZX.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType, channelService.get(cpr.getChannel().getId()),
						inputAccType, agentType);
				resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
				resultMap.put("html", "http://101.200.34.95:26370/flypayfx/mobile/ShenFuResponse?result=" + response);// 生产
			} else {
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			}
		} catch (Exception e) {
			LOG.error("发送银联在线支付请求出错", e);
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
		}
		return resultMap;
	}

	/**
	 * 发起申孚直通车请求
	 */
	private Map<String, String> createThroughOrder(TuserMerchantReport merchReport, User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer agentType, String des) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String orderNum = "SFZTC" + commonService.getUniqueOrderByType(UserOrder.trans_type.YLZXE.name(), user.getId());
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			Map<String, String> map = new TreeMap<String, String>();
			// 判断是不是信用卡
			if (card == null || "X".equalsIgnoreCase(card.getCardType()) == false) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_054);
				return resultMap;
			}
			JSONObject config = cpr.getConfig();
			map.put("mercId", merchReport.getMercId());
			map.put("accNo", card.getCardNo());
			map.put("txnAmt", String.valueOf(money));
			map.put("orderNo", orderNum);
			map.put("frontUrl", frontUrl);
			map.put("notifyUrl", config.getString("ylaccount.notifyUrl"));
			if (inputAccType == 0) {
				map.put("txnAmtType", "D0");
			} else {
				map.put("txnAmtType", "T1");
			}
			LOG.info("Send Shenfu D0 payment arguments={}", JSON.getDefault().toJSONString(map));
			String response = ShenFuPayUtil.sendPayShenFuD0(map, merchReport.getPrivateKey());
			if (StringUtil.isNotBlank(response)) {
				String desc = user.getRealName() + "_" + user.getLoginName() + "正在用" + card.getCardNo() + "支付" + money + "元";
				userOrderService.createTransOrder(user.getId(), orderNum, null, null, UserOrder.trans_type.YLZXE.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType, channelService.get(cpr.getChannel().getId()),
						inputAccType, agentType);
				resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);

				resultMap.put("html", "http://101.200.34.95:26370/flypayfx/mobile/ShenFuResponse?result=" + response);// 生产
				// resultMap.put("html",
				// "http://18b2z84712.51mypc.cn:23746/flypayfx/mobile/ShenFuResponse?result="
				// + response);// 测试

			} else {
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			}
		} catch (Exception e) {
			LOG.error("发送银联在线支付请求出错", e);
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
		}
		return resultMap;
	}

	/**
	 * 修改结算卡接口
	 * 
	 * @param params
	 */
	// @Override
	// public Map<String, String> updateBankAccountInfo(Map<String, String>
	// params) {
	// LOG.info("-------申孚通道--修改结算卡--start");
	// try {
	//
	// params.put("userId", String.valueOf(user.getId()));
	// params.put("cardId", String.valueOf(cardId));
	// params.put("merchReportId", String.valueOf(merchReport.getId()));
	// params.put("mercId", String.valueOf(merchReport.getMercId()));
	// params.put("privateKey", merchReport.getPrivateKey());
	// params.put("orderNo", _orderNo);
	//
	// Long cardIdL = Long.valueOf(params.get("userId"));
	// String orderHql = " select c from TuserCard c where c.id=" + cardIdL;
	// TuserCard userCard = userCardDao.get(orderHql);
	//
	// String cardReportHql = " select r from TcreditCardReport r where
	// r.cardId=" + Long.valueOf(params.get("cardId"));
	// TcreditCardReport cardReport = creditCardReportDao.get(cardReportHql);
	//
	// String userHql = " select u from Tuser u where u.id=" +
	// Long.valueOf(params.get("userId"));
	// Tuser tuser = userDao.get(userHql);
	//
	// String privateKey = params.get("privateKey");
	//
	// Map<String, String> map = new HashMap<String, String>();
	//
	// String response = ShenFuPayUtil.updateBankAccount(map, privateKey);
	// // updateBankAccount
	// Map<String, String> result = new HashMap<String, String>();
	//
	// if (data != null) {
	// if (data.getString("code").equals("000000")) {
	// result.put("code", "SUCCESS");
	// } else {
	// result.put("code", "FALSE");
	// result.put("message", data.getString("message"));
	// }
	// return result;
	// } else {
	// LOG.error("-------申孚通道--修改结算卡--进程报错: 验证签名失败，请查看前后报文分析异常");
	// }
	// } catch (Exception e) {
	// LOG.error("-------申孚通道--修改结算卡--进程报错:", e);
	// }
	//
	// LOG.info("-------申孚通道--修改结算卡--end");
	// return null;
	// };

	// 查询订单
	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {
		TuserOrder tOrder = userOrderService.getTorderByOrderNo(orderNum);
		if (tOrder != null && tOrder.getUser() != null && tOrder.getUser().getId() != null) {
			try {
				Long userId = tOrder.getUser().getId();
				TuserMerchantReport merchantReport = userMerchantReportService.get(userId);

				Map<String, String> orderMap = new TreeMap<String, String>();
				if (merchantReport == null || merchantReport.getMercId() == null) {
					return orderMap;
				}
				orderMap.put("mercId", merchantReport.getMercId());
				orderMap.put("orderNo", orderNum);
				String mercPrivateKey = merchantReport.getPrivateKey();
				JSONObject jsonResult = ShenFuPayUtil.searchOrderToShenfu(orderMap, mercPrivateKey);
				LOG.info("Search Shenfu Order={},result={}", orderNum, jsonResult.toJSONString());
				String retCode = (String) jsonResult.get("retCode");
				String retMsg = (String) jsonResult.get("retMsg");
				String txnStatus = (String) jsonResult.get("txnStatus");
				Map<String, String> orderInfo = new HashMap<String, String>();
				orderInfo.put("retCode", retCode);
				orderInfo.put("retMsg", retMsg);
				boolean successFlag = false;
				if ("00".equals(retCode) && "00".equals(txnStatus)) {
					successFlag = true;
				}
				String payNo = "00".equals(retCode) ? (String) jsonResult.get("logNo") : "SEARCH_" + tOrder.getOrderNum();
				User operator = new User();
				operator.setLoginName("SYSTEM");
				String result = userOrderService.affirmOrderStatus(tOrder.getId(), successFlag, operator);
				orderInfo.put("resultMsg", result);
				return orderInfo;
			} catch (Exception e1) {
				LOG.error("----银联订单回调异常", e1);
			}

		} else {
			LOG.info("申孚银联订单：" + orderNum + "信息不完整。");
		}
		return null;

	}

	@Override
	public Map<String, String> sendDaiFuReq(Map<String, String> params) {
		Map<String, String> returnMap = new HashMap<String, String>();
		Map<String, String> reqMap = new TreeMap<String, String>();
		String userId = params.get("userId");
		String orderNum = params.get("orderNum");
		String userHql = " select u from Tuser u where u.id='" + userId + "'";
		Tuser tuser = userDao.get(userHql);
		TuserOrder tuserOrder = userOrderService.getTorderByOrderNo(orderNum);
		BigDecimal amt = tuserOrder.getAmt();

		TuserCard tUserCard = userCardService.getTUserCarByUserId(Long.valueOf(userId));
		if (tUserCard == null || StringUtil.isEmpty(tUserCard.getCardNo())) {
			returnMap.put("flag", GlobalConstant.RESP_CODE_024);
			returnMap.put("errMsg", GlobalConstant.map.get(GlobalConstant.RESP_CODE_024));
			return returnMap;
		}
		reqMap.put("mercId", ApplicationBase.SHENFU_DAIFU_MERCHID);
		reqMap.put("orderId", orderNum);
		reqMap.put("accNo", tUserCard.getCardNo());
		reqMap.put("bankCity", "上海市");
		String bankName = StringUtil.isEmpty(tUserCard.getBranchName()) ? "工商银行" : tUserCard.getBranchName();
		reqMap.put("bankName", bankName);
		reqMap.put("accName", tuser.getRealName());
		BigDecimal fixAmt = amt.multiply(new BigDecimal(100L)).setScale(0);
		reqMap.put("transAmt", String.valueOf(fixAmt));
		reqMap.put("certifId", tuser.getIdNo());
		reqMap.put("notifyUrl", "http://101.200.34.95:36370/flypayfx/payment/ShenFuDaiFuNotify?orderId=" + orderNum);
		LOG.info("sendDaiFuReq mapjson={}", JSON.getDefault().toJSONString(reqMap));
		JSONObject jsonResult = new JSONObject();
		try {
			String daifuResult = ShenFuPayUtil.shenFuDaiFu(reqMap, ApplicationBase.SHENFU_DAIFU_PRIVATEKEY);
			LOG.info("SHENFU daifu order={}, Result={}", orderNum, daifuResult);
			jsonResult = JSONObject.parseObject(daifuResult);
		} catch (Exception e) {
			returnMap.put("flag", GlobalConstant.RESP_CODE_999);
			return returnMap;
		}
		String errCode = jsonResult.getString("errCode");
		String errMsg = jsonResult.getString("errMsg");
		if ("000000".equals(errCode)) {
			returnMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
			returnMap.put("errMsg", errMsg);
		} else {
			returnMap.put("flag", GlobalConstant.RESP_CODE_999);
			returnMap.put("errMsg", errMsg);
		}
		return returnMap;
	}

	public static void main(String[] args) {

		BigDecimal amt = new BigDecimal("1.23");

		System.out.println(amt);
		BigDecimal amt2 = amt.multiply(new BigDecimal(100L));
		System.out.println(amt2);
		// Map<String, String> map = new HashMap<String, String>();
		// map.put("AA", "aa");
		// map.put("BB", "bb");
		// String json = JSON.getDefault().toJSONString(map);
		// System.out.print(json);
		// Map<String, String> map2 = JSON.getDefault().parseToObject(json,
		// Map.class);
		// Map<String, String> map3 =
		// JSON.getDefault().parseToExplictObject(json, Map.class);
	}

}
