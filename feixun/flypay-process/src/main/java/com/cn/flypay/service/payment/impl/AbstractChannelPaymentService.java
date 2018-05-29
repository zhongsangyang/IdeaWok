package com.cn.flypay.service.payment.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.model.payment.response.PaymentResponse;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.statement.StatementService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.ServiceMerchantService;
import com.cn.flypay.service.trans.UserOrderService;

@Service(value = "channelPaymentService")
public abstract class AbstractChannelPaymentService implements ChannelPaymentService {
	@Autowired
	UserOrderService userOrderService;
	@Autowired
	CommonService commonService;
	@Autowired
	ChannelService channelService;
	@Autowired
	StatementService tradeStatementService;
	@Autowired
	ServiceMerchantService serviceMerchantService;

	/**
	 * 发送短信
	 * 
	 * @param sm
	 * @param user
	 * @return
	 */
	public JSONObject sendSmsCode(Map<String, String> params) {
		return null;
	}

	@Override
	public Map<String, Object> createSubMerchant(ServiceMerchant sm, User user) {
		return null;
	}
	
	@Override
	public Map<String, Object> registerSubMerchant(ServiceMerchant sm, User user) {
		return null;
	}

	@Override
	public UserMerchantConfig createSubMerchant(ServiceMerchant sm, Map<String, String> params) {
		return null;
	}

	@Override
	public Map<String, String> createSubMerchantByUserId(Long userId) {
		return null;
	};

	@Override
	public Map<String, String> auditSubMerchantByChannelId(Long channelId) {
		return null;
	};

	@Override
	public Map<String, String> createUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, Integer angentType, String desc) throws Exception {
		return null;
	}

	@Override
	public Map<String, String> createSmUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, String authCode, Double money, Integer transPayType, Integer angentType, String desc) throws Exception {
		return null;
	}

	@Override
	public Map<String, String> createOnLineOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, String desc, Map<String, String> params) throws Exception {
		return null;
	}

	@Override
	public PaymentResponse createYLZXOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		return null;
	}

	@Override
	public Map<String, String> createYLZXOrder_v2(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		return null;
	}

	@Override
	public Map<String, String> createUnipayOnlineThroughOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		return null;
	}

	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {
		return null;
	}

	@Override
	public Boolean dealDownloadStatement(String dateStr) {
		return null;
	}

	@Override
	public void dealChannelT0Tixian(Long channelId, Map<String, String> params) {

	}

	@Override
	public void sendT0TixianSearch(String orderNum) {
	}

	/**
	 * 查询嘎吱商户账户余额
	 * 
	 * @param merNo
	 *            嘎吱商户号
	 * @return
	 */
	@Override
	public Map<String, String> queryGaZhiBalance(String merNo) {
		return null;
	}

	/**
	 * 查询商户提款手续费查询
	 * 
	 * @param merNo
	 *            嘎吱商户号
	 * @param amount
	 *            提现金额
	 * @return
	 */
	@Override
	public Map<String, String> queryGaZhiWithdrawFee(String merNo, String amount) {
		return null;
	}

	/**
	 * 嘎吱商户提款
	 * 
	 * @param merNo
	 * @param tranAmt
	 * @param factAmt
	 * @param feeAmt
	 * @return
	 */
	@Override
	public Map<String, String> drawGaZhi(String merNo, String tranAmt, String factAmt, String feeAmt) {
		return null;
	};

	/**
	 * 嘎吱商户提款结果查询
	 * 
	 * @param merNo
	 * @param oldMerTrace
	 * @param oldTransDate
	 * @param oldTransTime
	 * @return
	 */
	@Override
	public Map<String, String> queryGaZhiDrawResult(String merNo, String oldMerTrace, String oldTransDate, String oldTransTime) {
		return null;
	};

	/**
	 * 商户新增信用卡
	 */
	@Override
	public Map<String, String> addCreditCard(Map<String, String> params) {
		return null;
	};

	/**
	 * 哲扬通道--开通支付接口 ,(哲扬声称有的通道需要签约才有这一步，我们暂时不需要做）
	 */
	/*
	 * @Override public Map<String,String> fastpayOpenToken(Map<String, String>
	 * params){ return null; };
	 */

	/**
	 * 线上支付,哲扬通道第二种方案，商户不再进件，下单时直接传递商户信息。
	 */
	@Override
	public Map<String, String> createOnLineOrder2(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, String desc, Map<String, String> params) throws Exception {
		return null;
	}

	/**
	 * 哲扬通道--确认支付
	 * 
	 * @param params
	 * @return
	 */
	@Override
	public Map<String, String> fastpayCheckMessage(Map<String, Object> params) {
		return null;
	};

	/**
	 * 哲扬通道--修改结算卡接口
	 * 
	 * @param params
	 */
	@Override
	public Map<String, String> updateBankAccountInfo(Map<String, String> params) {
		return null;
	};

	/**
	 * 哲扬通道--修改商户费率接口
	 * 
	 * @param params
	 * @return
	 */
	public Map<String, String> updateFastPayFee(Map<String, Object> params) {
		return null;
	};

	public Map<String, String> sendDaiFuReq(Map<String, String> params) {
		return null;
	}

}
