package com.cn.flypay.service.payment.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TweiLianBaoCardReport;
import com.cn.flypay.model.sys.TweiLianBaoMerchantReport;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.service.payment.WeiLianBaoYinLainService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.weilianbao.Des3Encryption;
import com.cn.flypay.utils.weilianbao.WeiLianBaoPayUtil;
import com.cn.flypay.utils.weilianbao.WeiLianBaoSignUtil;
import com.cn.flypay.utils.weilianbao.entity.OrderPayForWeiLianBaoYinLian;

@Service
public class WeiLianBaoYinLainServiceImpl implements WeiLianBaoYinLainService {

	private Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private BaseDao<TweiLianBaoCardReport> weiLianBaoCardReportDao;
	@Autowired
	private BaseDao<TweiLianBaoMerchantReport> merchantDao;
	@Autowired
	private BaseDao<TuserOrder> userOrderDao;

	public TweiLianBaoMerchantReport findMerchantReport(String merchantNo) {
		List<TweiLianBaoMerchantReport> merchantReports = merchantDao.find(" from  TweiLianBaoMerchantReport t where t.merchantNo='" + merchantNo + "'");
		if (CollectionUtil.isEmpty(merchantReports)) {
			LOG.info("findMerchantReport merchantNo={} is NULL ", merchantNo);
			return null;
		}
		LOG.info("findMerchantReport merchantNo={} is SUCCESS ", merchantNo);
		return merchantReports.get(0);
	}

	public JSONObject openCard(TweiLianBaoMerchantReport merchReport, Map<String, String> resultMap, ChannelPayRef cpr) {
		try {
			JSONObject config = cpr.getConfig();
			Map<String, String> openCardMap = new LinkedHashMap<String, String>();
			openCardMap.put("trxType", "OPEN_CARD");
			openCardMap.put("merchantNo", merchReport.getMerchantNo());
			openCardMap.put("orderNum", resultMap.get("orderNo"));
			openCardMap.put("trxTime", DateUtil.convertCurrentDateTimeToString());
			openCardMap.put("callbackUrl", config.getString("callbackUrl"));
			openCardMap.put("serverCallbackUrl", config.getString("serverOpenCardCallbackUrl"));
			String signValue = WeiLianBaoSignUtil.signMd5(openCardMap, merchReport.getSignKey());
			LOG.info("发送开卡请求 ={}", JSONObject.toJSONString(openCardMap));
			openCardMap.put("sign", signValue);
			JSONObject retJson = WeiLianBaoPayUtil.openCard(openCardMap);
			LOG.info("发送开卡响应 ={}", retJson.toJSONString());
			// System.out.print(retJson.toString());
			return retJson;
		} catch (Exception e) {
			LOG.info("开卡失败", e);
			return null;
		}
	}

	public JSONObject sendSmsCodeOpenCard(Map<String, String> params) {
		try {
			String retCode = params.get("retCode");
			String retMsg = params.get("retMsg");
			String merchantNo = params.get("merchantNo");
			String token = params.get("token");
			String cardNo = params.get("cardNo");
			String phone = params.get("phone");
			String orderNum = params.get("orderNum");

			boolean isSuccess = false;
			if ("0000".equals(retCode)) {
				isSuccess = true;
			}
			saveWeiLianBaoCardNotify(isSuccess, merchantNo, token, cardNo, phone, orderNum);
			return null;
			
//			List<TweiLianBaoMerchantReport> merchantReports = merchantDao.find(" from  TweiLianBaoMerchantReport t where t.merchantNo='" + merchantNo + "'");
//			if (CollectionUtil.isEmpty(merchantReports)) {
//				return null;
//			}
//			TweiLianBaoMerchantReport merchantReport = merchantReports.get(0);
//			Map<String, Object> params2 = new HashMap<String, Object>();
//			params2.put("orderNum", orderNum);
//			String hql = " From TuserOrder where orderNum=:orderNum ";
//			List<TuserOrder> orders = userOrderDao.find(hql, params2);
//			if (CollectionUtil.isEmpty(orders)) {
//				return null;
//			}
//			TuserOrder order = orders.get(0);
//			// TODO
//			Map<String, String> openCardSmsMap = new LinkedHashMap<String, String>();
//			openCardSmsMap.put("trxType", "SMS_CODE");
//			openCardSmsMap.put("merchantNo", merchantNo);
//			openCardSmsMap.put("orderNum", orderNum);
//			openCardSmsMap.put("trxTime", DateUtil.convertCurrentDateTimeToString());
//			openCardSmsMap.put("smsType", "02");
//			openCardSmsMap.put("phone", Des3Encryption.encode(merchantReport.getDesKey(), phone));
//			openCardSmsMap.put("amount", order.getAmt().toString());
//			openCardSmsMap.put("encrypt", "T0");
//			openCardSmsMap.put("token", Des3Encryption.encode(merchantReport.getDesKey(), token));
//			String signValue = WeiLianBaoSignUtil.signMd5(openCardSmsMap, merchantReport.getSignKey());
//			openCardSmsMap.put("sign", signValue);
//			JSONObject retJson = WeiLianBaoPayUtil.sendSmsCode(openCardSmsMap);
////			System.out.print(retJson.toString());
//			LOG.info("-------微联宝通道--retJson=[{}]-创建子商户(进件)-end",retJson.toJSONString());
//			return retJson;
		} catch (Exception e) {
			LOG.info("发送验证码失败", e);
			return null;
		}
	}

	public JSONObject sendSmsCodeTxn(Map<String, String> params, String signKey) {
		try {
			LOG.info("-------微联宝通道--直接交易短信发送SMS请求1={}", JSONObject.toJSONString(params));
			String signValue = WeiLianBaoSignUtil.signMd5(params, signKey);
			params.put("sign", signValue);
			LOG.info("-------微联宝通道--直接交易短信发送SMS请求2={}", JSONObject.toJSONString(params));
			JSONObject retJson = WeiLianBaoPayUtil.sendSmsCode(params);
			LOG.info("-------微联宝通道--直接交易短信发送SMS响应={}", retJson.toJSONString());
			return retJson;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Long saveWeiLianBaoCardNotify(boolean isSuccess, String merchantNo, String token, String cardEncNo, String phone, String srcOrderNum) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("merchantNo", merchantNo);
		params.put("srcOrderNum", srcOrderNum);
		String hql = " From TweiLianBaoCardReport where merchantNo=:merchantNo and srcOrderNum=:srcOrderNum ";
		List<TweiLianBaoCardReport> cardReports = weiLianBaoCardReportDao.find(hql, params);
		TweiLianBaoCardReport report = null;
		if (CollectionUtil.isEmpty(cardReports)) {
			report = new TweiLianBaoCardReport();
			report.setCardEncNo(cardEncNo);
			report.setToken(token);
			report.setMerchantNo(merchantNo);
			report.setPhone(phone);
			report.setSrcOrderNum(srcOrderNum);
			report.setStatus("1");
			report.setCreateDate(new Date());
			weiLianBaoCardReportDao.save(report);
		} else {
			report = cardReports.get(0);
			// 卡不成功
			if (!isSuccess) {
				report.setToken("");
				report.setCardEncNo("");
				report.setStatus("1");
			} else {
				// TODO 卡號校验 前6 后4
				String srcCardNo = report.getCardNo();
				String prefixCardNo = srcCardNo.substring(0, 6);
				String sufCardNo = srcCardNo.substring(srcCardNo.length() - 4, srcCardNo.length() - 1);
				String prefixCardNo2 = cardEncNo.substring(0, 6);
				String sufCardNo2 = cardEncNo.substring(cardEncNo.length() - 4, cardEncNo.length() - 1);
				LOG.info("--微联宝通道--开卡响应前缀srcCardNo|notifyCardNo=prefix[{}|{}],suffix[{}|{}]", prefixCardNo, prefixCardNo2, sufCardNo, sufCardNo2);
				// if(!prefixCardNo2.equals( prefixCardNo ) ||
				// !sufCardNo2.equals( sufCardNo ) ) {
				// report.setCardNo("");
				// report.setMerchantNo("9999999");
				// report.setPhone("11111111111");
				// report.setSrcOrderNum("000000");
				// report.setStatus("0");
				// } else {
				report.setPhone(phone);
				report.setCardEncNo(cardEncNo);
				report.setToken(token);
				report.setStatus("1");
				// }
			}
			weiLianBaoCardReportDao.update(report);
		}
		return report.getId();
	}

	@Override
	public JSONObject consume(OrderPayForWeiLianBaoYinLian reqPar, String cardNo) {
		List<TweiLianBaoCardReport> cardReports = weiLianBaoCardReportDao.find(" FROM TweiLianBaoCardReport where cardNo='" + cardNo + "'");
		if (CollectionUtil.isEmpty(cardReports)) {
			return null;
		}

		TweiLianBaoCardReport cardReport = cardReports.get(0);
		String merchantNo = cardReport.getMerchantNo();
		String token = cardReport.getToken();

		List<TweiLianBaoMerchantReport> merchantReports = merchantDao.find(" FROM TweiLianBaoMerchantReport where merchantNo='" + merchantNo + "'");
		if (CollectionUtil.isEmpty(merchantReports)) {
			return null;
		}

		TweiLianBaoMerchantReport merchantReport = merchantReports.get(0);
		String signKey = merchantReport.getSignKey();
		reqPar.setMerchantNo(merchantNo);
		reqPar.setToken(token);

		Map<String, String> consumeMap = new LinkedHashMap<String, String>();
		consumeMap.put("trxType", "CONSUME");
		consumeMap.put("merchantNo", merchantNo);
		consumeMap.put("token", Des3Encryption.encode(merchantReport.getDesKey(), token));
		consumeMap.put("goodsName", reqPar.getGoodsName());
//		consumeMap.put("serverDfUrl", "http://liguangchun211.51vip.biz:30483/flypayfx/payment/weilianbaoDfNotify");
//		consumeMap.put("serverCallbackUrl", "http://liguangchun211.51vip.biz:30483/flypayfx/payment/weilianbaoConsumeNotify2");
		
		consumeMap.put("serverDfUrl", "http://101.200.34.95:46370/flypayfx/payment/weilianbaoDfNotify");
		consumeMap.put("serverCallbackUrl", "http://101.200.34.95:46370/flypayfx/payment/weilianbaoConsumeNotify2");
		consumeMap.put("orderNum", reqPar.getOrderNum());
		consumeMap.put("trxTime", reqPar.getTrxTime());
		consumeMap.put("smsCode", Des3Encryption.encode(merchantReport.getDesKey(), reqPar.getSmsCode()));
		String signValue = WeiLianBaoSignUtil.signMd5(consumeMap, signKey);
		consumeMap.put("sign", signValue);
		LOG.info("发送微联宝交易请求{}", JSONObject.toJSONString(consumeMap));
		JSONObject retJson = new JSONObject();
		try {
			retJson = WeiLianBaoPayUtil.consume(consumeMap);
			LOG.info("发送微联宝交易响应{}", retJson.toJSONString());
			return retJson;
		} catch (Exception e) {
			LOG.info("发起交易失败", e);
			return null;
		}
	}
}
