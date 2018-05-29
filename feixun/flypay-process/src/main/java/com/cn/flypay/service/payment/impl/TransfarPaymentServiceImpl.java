package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
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
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserMerchantConfigService;
import com.cn.flypay.service.sys.UserMerchantReportService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.transfar.DateUtils;
import com.cn.flypay.utils.transfar.HttpClient;
import com.cn.flypay.utils.transfar.HttpDownload;
import com.cn.flypay.utils.transfar.ParamUtil;
import com.cn.flypay.utils.transfar.TransfarPayBase;

@Service(value = "transfarPaymentService")
public class TransfarPaymentServiceImpl extends AbstractChannelPaymentService {

	private Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserMerchantConfigService userMerchantConfigService;

	@Autowired
	UserMerchantReportService userMerchantReportService;

	@Autowired
	UserOrderService userOrderService;

	@Autowired
	UserCardService userCardService;

	@Autowired
	private BaseDao<TuserCard> userCardDao;

	@Autowired
	private BaseDao<Tuser> userDao;

	@Override
	public Map<String, String> sendDaiFuReq(Map<String, String> params) {
		Map<String, String> returnMap = new HashMap<String, String>();
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
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("service_id", TransfarPayBase.DAIFU_SERVICEID);
		reqMap.put("appid", "1454001");// **
		// reqMap.put("partyid", "565612683");商户编号6688150131005437
		reqMap.put("dog_sk", "P44ut42635kUwv4K6Tg3");// **
		reqMap.put("tf_timestamp", DateUtils.dateToString(new Date(), "yyyyMMddHHmmss"));// yyyyMMddHHmmss-20141126161900
		reqMap.put("sign_type", "MD5");
		reqMap.put("businessnumber", orderNum);
		reqMap.put("subject", "宝贝钱袋");
		reqMap.put("transactionamount", amt.toString());// 交易金额
		System.out.println("------------transactionamount-------------" + amt.toString());
		reqMap.put("bankcardnumber", tUserCard.getCardNo());// 银行卡号码
		reqMap.put("bankcardname", tuser.getRealName());
		String bankName = tUserCard.getBranchName() == null ? "平安银行" : tUserCard.getBranchName();
		reqMap.put("bankname", bankName);// 银行名称
		reqMap.put("bankcardtype", "个人");
		reqMap.put("bankaccounttype", "储蓄卡");
		reqMap.put("fromaccountnumber", "8800009942689");// 会员账户号**
		// reqMap.put("backurl",
		// "http://liguangchun211.51vip.biz:30483/flypayfx/payment/TransfarDaiFuNotify");
		reqMap.put("backurl", "http://101.200.34.95:36370/flypayfx/payment/TransfarDaiFuNotify");
		JSONObject jsonResp = new JSONObject();
		try {
			reqMap.put("tf_sign", ParamUtil.map2MD5(reqMap));
			System.out.println("-------------------------" + reqMap.get("tf_sign") + "");
			reqMap.remove("dog_sk");
			// String response =
			// HttpClient.sendHttpPost(TransfarPayBase.DAIFU_TEST_HTTP_API,
			// reqMap);
			// System.out.println("-----------response--------------" +
			// response);
			// jsonResp = JSONObject.parseObject(response);
			// String response2 =
			// HttpClient.sendHttpPost(TransfarPayBase.DAIFU_TEST_HTTPS_API,
			// reqMap);
			// System.out.println("-----------response2--------------" +
			// response2);

			// String response3 =
			// HttpClient.sendHttpPost(TransfarPayBase.DAIFU_HTTP_API, reqMap);
			// System.out.println("-----------response3--------------" +
			// response3);
			String response = HttpClient.sendHttpPost(TransfarPayBase.DAIFU_HTTPS_API, reqMap);
			// String response =
			// HttpClient.sendHttpPost(TransfarPayBase.DAIFU_TEST_HTTPS_API,
			// reqMap);
			System.out.println("-----------response4--------------" + response);
			jsonResp = JSONObject.parseObject(response);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			returnMap.put("flag", GlobalConstant.RESP_CODE_999);
			return returnMap;
		}
		String result = jsonResp.getString("result");
		String msg = jsonResp.getString("msg");
		if ("success".equals(result)) {
			returnMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
			returnMap.put("errMsg", msg);
		} else {
			returnMap.put("flag", GlobalConstant.RESP_CODE_999);
			returnMap.put("errMsg", msg);
		}
		return returnMap;
	}
}
