package com.cn.flypay.service.payment.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.cn.flypay.model.sys.TweiLianBaoCardReport;
import com.cn.flypay.model.sys.TweiLianBaoMerchantReport;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.model.util.JSON;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.WeiLianBaoYinLainService;
import com.cn.flypay.service.sys.UserMerchantConfigService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.weilianbao.Des3Encryption;
import com.cn.flypay.utils.weilianbao.Md5Util;
import com.cn.flypay.utils.weilianbao.WeiLianBaoPayUtil;

/**
 * 微联宝银联直通车
 */
@Service(value = "weiLianBaoPaymentService")
public class WeiLianBaoPaymentServiceImpl extends AbstractChannelPaymentService {
	//
	private static Logger LOG = LoggerFactory.getLogger(WeiLianBaoPaymentServiceImpl.class);
	@Autowired
	private BaseDao<Tuser> userDao;
	@Autowired
	private BaseDao<TuserOrder> userOrderDao;
	@Autowired
	private BaseDao<TuserCard> userCardDao;
	@Autowired
	private BaseDao<TweiLianBaoCardReport> weiLianBaoCardReportDao;
	@Autowired
	private BaseDao<TcreditCardReport> creditCardReportDao;
	@Autowired
	private BaseDao<TweiLianBaoMerchantReport> userMerchReportDao;
	@Autowired
	private UserMerchantConfigService userMerchantConfigService;
	@Autowired
	private WeiLianBaoYinLainService weiLianBaoYinLainService;

	private Long saveWeiLianBaoCard(String merchantNo, String cardNo, String reservedPhoneNo, String srcOrderNum) {
		TweiLianBaoCardReport report = null;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("merchantNo", merchantNo);
		params.put("cardNo", cardNo);
		String cardReportHql = " From TweiLianBaoCardReport where merchantNo=:merchantNo and cardNo=:cardNo ";
		List<TweiLianBaoCardReport> cards = weiLianBaoCardReportDao.find(cardReportHql, params);
		if (CollectionUtil.isEmpty(cards)) {
			report = new TweiLianBaoCardReport();
			report.setCardNo(cardNo);
			report.setMerchantNo(merchantNo);
			report.setPhone(reservedPhoneNo);
		} else {
			report = cards.get(0);
		}
		report.setSrcOrderNum(srcOrderNum);
		report.setStatus("0");
		report.setCreateDate(new Date());
		weiLianBaoCardReportDao.saveOrUpdate(report);
		return report.getId();
	}

	// 创建子商户
	@Override
	@Transactional
	public UserMerchantConfig createSubMerchant(ServiceMerchant sm, Map<String, String> params) {
		LOG.info("-------微联宝通道--创建子商户(进件)-start");
		try {
			Map<String, String> paramMap = new TreeMap<String, String>();
			paramMap.put("channelNo", params.get("channelNo"));
			paramMap.put("channelName", params.get("channelName"));
			paramMap.put("merchantName", params.get("merchantName"));
			paramMap.put("merchantBillName", params.get("merchantBillName"));
			paramMap.put("installProvince", params.get("installProvince"));
			paramMap.put("installCity", params.get("installCity"));
			paramMap.put("installCounty", params.get("installCounty"));
			paramMap.put("operateAddress", params.get("operateAddress"));
			paramMap.put("merchantType", params.get("merchantType"));
			paramMap.put("isOneOrBig", params.get("isOneOrBig"));
			paramMap.put("legalPersonName", params.get("legalPersonName"));
			paramMap.put("legalPersonID", params.get("legalPersonID"));
			paramMap.put("merchantPersonType", params.get("merchantPersonType"));
			paramMap.put("merchantPersonName", params.get("merchantPersonName"));
			paramMap.put("merchantPersonPhone", params.get("merchantPersonPhone"));
			paramMap.put("wxType", params.get("wxType"));
			paramMap.put("wxT1Fee", params.get("wxT1Fee"));
			paramMap.put("wxT0Fee", params.get("wxT0Fee"));
			paramMap.put("alipayType", params.get("alipayType"));
			paramMap.put("alipayT1Fee", params.get("alipayT1Fee"));
			paramMap.put("alipayT0Fee", params.get("alipayT0Fee"));
			paramMap.put("province_code", params.get("province_code"));
			paramMap.put("city_code", params.get("city_code"));
			paramMap.put("district_code", params.get("district_code"));
			paramMap.put("bankType", params.get("bankType"));
			paramMap.put("accountName", params.get("accountName"));
			// System.out.print(params.get("accountNo") + "\r\n");
			paramMap.put("accountNo", Des3Encryption.encode(WeiLianBaoPayUtil.DES_KEY, params.get("accountNo")));
			paramMap.put("bankName", params.get("bankName"));
			paramMap.put("bankProv", params.get("bankProv"));
			paramMap.put("bankCity", params.get("bankCity"));
			paramMap.put("bankBranch", params.get("bankBranch"));
			String bankCode = params.get("bankCode");
			LOG.info("Register WEILIANBAO Merch bankCode={}", bankCode);
			paramMap.put("bankCode", "402799000081");
			String signValue = Md5Util.MD5(JSON.getDefault().toJSONString(paramMap) + WeiLianBaoPayUtil.SIGN_KEY);
			paramMap.put("sign", signValue);
			String paramStr = JSON.getDefault().toJSONString(paramMap);
			LOG.info("Register WEILIANBAO D0 params={}", JSON.getDefault().toJSONString(paramMap));
			JSONObject retJson = new JSONObject();
			String retStr = WeiLianBaoPayUtil.registerSubMerchant2(paramStr);

			LOG.info("WEILIANBAO register merch respone info={}", retStr);
			Map<String, String> regisMap = JSONObject.parseObject(retStr, Map.class);
			if (regisMap == null || regisMap.size() < 1 || !"0000".equals(regisMap.get("respCode"))) {
				LOG.info("微联宝通道报备商户失败 LoginName={} register 1 failed, INFO=[{}]", params.get("loginName"), retStr);
				return null;
			}

			String respCode = regisMap.get("respCode");
			String respMsg = regisMap.get("respMsg");
			String merchantNo = regisMap.get("merchantNo");
			String signKey = regisMap.get("signKey");
			String desKey = regisMap.get("desKey");
			String queryKey = regisMap.get("queryKey");
			String remarks = regisMap.get("remarks");
			String sign = regisMap.get("sign");
			String userId = params.get("userId");
			String accountNo = params.get("accountNo");

			String userHql = " from Tuser u where u.id = :userId ";
			Map<String, Object> argu = new HashMap<String, Object>();
			argu.put("userId", Long.valueOf(userId));
			List<Tuser> userEntitys = userDao.find(userHql, argu);
			if (userEntitys == null || userEntitys.size() != 1) {
				LOG.info("微联宝通道报备商户失败--创建失败 LoginName {} register 2 failed, INFO=[{}]", params.get("loginName"), JSON.getDefault().toJSONString(userEntitys));
				return null;
			}

			Tuser userEntity = userEntitys.get(0);
			TweiLianBaoMerchantReport merchantReport = null;
			if ("0000".equals(respCode)) {// 成功: 写入TuserMerchantReport
				merchantReport = new TweiLianBaoMerchantReport();
				merchantReport.setUserId(Long.valueOf(userId));
				merchantReport.setSettleCardNo(accountNo);
				merchantReport.setRespCode(respCode);
				merchantReport.setRespMsg(respMsg);
				merchantReport.setMerchantNo(merchantNo);
				merchantReport.setSignKey(signKey);
				merchantReport.setDesKey(desKey);
				merchantReport.setQueryKey(queryKey);
				merchantReport.setRemarks(remarks);
				merchantReport.setSign(sign);
				merchantReport.setCreateTime(new Date());
				userMerchReportDao.save(merchantReport);
				LOG.info("微联宝通道--创建子商户(进件)--创建成功 loginName={},respCode=[{}],respMsg=[{}]", params.get("loginName"), respCode, respMsg);
			} else {
				LOG.error("微联宝通道--创建子商户失败,loginName={},respCode={},respMsg={}", params.get("loginName"), respCode, respMsg);
			}

			Map<String, String> paramMap2 = new TreeMap<String, String>();
			paramMap2.put("channelName", params.get("channelName"));
			paramMap2.put("channelNo", params.get("channelNo"));
			paramMap2.put("merchantNo", merchantReport.getMerchantNo());
			paramMap2.put("productType", "QUICKPAY");
			paramMap2.put("t0Fee", params.get("t0Fee"));
			paramMap2.put("t1Fee", params.get("t1Fee"));
			String signValue2 = Md5Util.MD5(JSON.getDefault().toJSONString(paramMap2) + WeiLianBaoPayUtil.SIGN_KEY);

			paramMap2.put("sign", signValue2);
			String paramStr2 = JSON.getDefault().toJSONString(paramMap2);
			LOG.info("ADDFEE WEILIANBAO params={}", JSON.getDefault().toJSONString(paramStr2));
			String retStr2 = WeiLianBaoPayUtil.addFee(paramStr2);
			LOG.info("WEILIANBAO addFee merch respone info={}", retStr2);
			Map<String, String> regisMap2 = JSONObject.parseObject(retStr2, Map.class);
			if (regisMap2 == null || regisMap2.size() < 1 || !"0000".equals(regisMap2.get("respCode"))) {
				LOG.info("微联宝通道添加费率失败 LoginName={} register 1 failed, INFO=[{}]", params.get("loginName"), retStr);
				return null;
			}

			UserMerchantConfig umc = new UserMerchantConfig();
			umc.setSubMerchantId(merchantNo);
			umc.setServiceMerchantId(sm.getId());
			umc.setConfig(retJson.toJSONString());
			umc.setMerchantChannelName("WEILIANBAOZTC");
			umc.setType(550); // 550 银联直通
			umc.setWeiLianBaoMerchantReport(merchantReport);
			LOG.info("-------微联宝通道--创建子商户(进件)-end");
			return umc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 发起交易
	@Override
	public Map<String, String> createUnipayOnlineThroughOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer agentType, String orderNo) {

		// 1 判断表TweiLianBaoMerchantReport 是否存在记录
		TweiLianBaoMerchantReport merchReport = null;
		String loginName = user.getLoginName();
		String merchReportHql = " select r from TweiLianBaoMerchantReport r where r.userId=" + user.getId();
		merchReport = userMerchReportDao.get(merchReportHql);
		if (merchReport == null) {
			// 报备商户
			String userHql = " select u from Tuser u where u.id=" + user.getId();
			Tuser tuser = userDao.get(userHql);
			merchReport = userMerchantConfigService.createWeiLianBaoUserMerchants(tuser);
		}

		Map<String, String> resultMap = new HashMap<String, String>();
		if (merchReport == null || StringUtil.isEmpty(merchReport.getMerchantNo())) {
			LOG.info("Cannt Found merchReport For userId={},loginName={}", user.getId(), user.getLoginName());
			resultMap.put("flag", GlobalConstant.RESP_CODE_085);
			resultMap.put("flagMSG", "商户信息有误,请联系银行");
			return resultMap;
		}

		TuserCard userCard = userCardDao.get(TuserCard.class, cardId);
		String cardNo = userCard.getCardNo();
		String reservedPhoneNo = userCard.getPhone();
		String merchantNo = merchReport.getMerchantNo();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("merchantNo", merchantNo);
		params.put("cardNo", cardNo);
		String cardReportHql = " From TweiLianBaoCardReport where merchantNo=:merchantNo and cardNo=:cardNo ";

		String cardReportStatus = "0";// 0未开卡 1 卡已开通
		List<TweiLianBaoCardReport> cards = weiLianBaoCardReportDao.find(cardReportHql, params);
		TweiLianBaoCardReport cardReport = null;
		if (cards == null || cards.size() == 0) {
			cardReportStatus = "0";
		} else {
			cardReport = cards.get(0);
			if (!StringUtil.isEmpty(cardReport.getToken())) {
				cardReportStatus = "1";
			}
		}

		// 1.创建订单
		if (StringUtil.isEmpty(orderNo)) {//
			resultMap = createOnlyOrder(merchReport, user, cpr, inputAccType, cardId, frontUrl, money, transPayType, agentType);
			if (!GlobalConstant.RESP_CODE_SUCCESS.equals(resultMap.get("flag"))) {
				return resultMap;
			}
		} else {
			resultMap.put("orderNo", orderNo);
		}

		String orderNum = resultMap.get("orderNo");
		if (StringUtil.isEmpty(orderNo) && "0".equals(cardReportStatus)) {
			// 空卡或错开则执行开卡
			saveWeiLianBaoCard(merchantNo, cardNo, reservedPhoneNo, orderNum);
			JSONObject retOpenCardJson = weiLianBaoYinLainService.openCard(merchReport, resultMap, cpr);
			if (retOpenCardJson == null) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "银行网络延迟,请稍后再试");
				return resultMap;
			} else {
				LOG.info("Open_Card Result={}", retOpenCardJson.toJSONString());
				String _orderNo = retOpenCardJson.getString("r2_orderNumber");
				String qrCode = retOpenCardJson.getString("r3_qrCode");
				String merchantNo2 = retOpenCardJson.getString("r1_merchantNo");
				String sign = retOpenCardJson.getString("sign");
				String trxType = retOpenCardJson.getString("trxType");
				String retCode = retOpenCardJson.getString("retCode");
				String retMsg = retOpenCardJson.getString("retMsg");
				if ("0000".equals(retCode)) {
					resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
					resultMap.put("type", "3");
					resultMap.put("orderNo", _orderNo);
					resultMap.put("merchantNo", merchantNo2);
					resultMap.put("sign", sign);
					resultMap.put("retMsg", retMsg);
					resultMap.put("trxType", trxType);
					resultMap.put("html", qrCode);
				} else {
					resultMap.put("flag", GlobalConstant.RESP_CODE_999);
					resultMap.put("flagMSG", "银行卡信息不全");
				}
			}
		} else {
			Map<String, String> params2 = new LinkedHashMap<String, String>();
			// trxType 接口类型 是 SMS_CODE
			// merchantNo 商户编号 是 B100001
			// orderNum 订单号 是 1-50位 数字加字母
			// trxTime 订单提交时间 是 yyyyMMddHHmmss
			// smsType 短信类型 是 固定值 02
			// phone 手机号 是 3des加密后，使用base64，utf8编码做加密。
			// amount 金额 是
			// encrypt T0/T1 是
			// token 卡开通的token 是 3des加密后，使用base64，utf8编码做加密。
			// sign 签名 是
			params2.put("trxType", "SMS_CODE");
			params2.put("merchantNo", merchantNo);
			params2.put("orderNum", orderNum);
			params2.put("trxTime", DateUtil.convertCurrentDateTimeToString());
			params2.put("smsType", "02");
			params2.put("phone", Des3Encryption.encode(merchReport.getDesKey(), cardReport.getPhone()));
			params2.put("amount", money.toString());
			params2.put("encrypt", "T0");
			params2.put("token", Des3Encryption.encode(merchReport.getDesKey(), cardReport.getToken()));// 卡必须已经开通,当前流程卡还没有收到响应,没有Token值

			JSONObject retTxnSmsJson = weiLianBaoYinLainService.sendSmsCodeTxn(params2, merchReport.getSignKey());
			String retCode = retTxnSmsJson.getString("retCode");
			String retMsg = retTxnSmsJson.getString("retMsg");

			if ("000004".equals(retCode)) {
				// 签名不过,但会有后台响应
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "银行卡校验完成,请重新下单");
				return resultMap;
			} else if (!"0000".equals(retCode)) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", retMsg + ",请联系客服");
				return resultMap;
			}
			resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
			resultMap.put("type", "1");
			resultMap.put("transactionId", orderNum);
			resultMap.put("payAmount", money.toString());
			resultMap.put("payCardId", cardId.toString());
			resultMap.put("orderId", "99999999");
		}

		return resultMap;
	}

	/**
	 * 发起申孚直通车请求
	 */
	private Map<String, String> createOnlyOrder(TweiLianBaoMerchantReport merchReport, User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType,
			Integer agentType/* , String des */) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String orderNo = "WLBZTC" + commonService.getUniqueOrderByType(UserOrder.trans_type.YLZXJ.name(), user.getId());
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

	public static void main(String[] args) {
		//
		// String a = "卡号:6217992********1691类型：Debit";
		//
		//
		// int index = a.indexOf("类型");
		//
		// a.split(regex)
		//

	}
}
