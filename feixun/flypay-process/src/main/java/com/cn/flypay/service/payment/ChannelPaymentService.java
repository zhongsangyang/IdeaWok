package com.cn.flypay.service.payment;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.model.payment.response.PaymentResponse;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;

public interface ChannelPaymentService {

	/**
	 * 发送短信
	 * 
	 * @param sm
	 * @param user
	 * @return
	 */
	public JSONObject sendSmsCode(Map<String, String> params);

	public Map<String, Object> createSubMerchant(ServiceMerchant sm, User user);
	
	public Map<String, Object> registerSubMerchant(ServiceMerchant sm, User user);

	/**
	 * 创建子商户
	 * 
	 * @param params
	 * @return
	 */
	public UserMerchantConfig createSubMerchant(ServiceMerchant sm, Map<String, String> params);

	/**
	 * 通过用户Id，创建子商户 新增易宝通道时创建
	 */
	public Map<String, String> createSubMerchantByUserId(Long userId);

	/**
	 * 通过通道ID查询报备的商户号，进行校验和审核 新增易宝通道时创建
	 * 
	 * @param channelId
	 * @return
	 */
	public Map<String, String> auditSubMerchantByChannelId(Long channelId);

	/**
	 * 二维码支付
	 * 
	 * @param u
	 *            操作用户
	 * @param cpr
	 *            路由通道信息
	 * @param inputAccType
	 *            入账类型 （0， 1,5, 8,10, 11, 100 ，对应着 D0，T1，T5，T8，大额D0、大额T1、 T10 ）
	 * @param money
	 *            支付金额
	 * @param transPayType
	 *            支付目的 10 普通支付类型 20 代理费支付类型
	 * @param desc
	 *            描述
	 * @param angentType
	 *            0 通用，21 钻石升级，22 金牌升级
	 * @return
	 * @throws Exception
	 */
	Map<String, String> createUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, Integer angentType, String desc) throws Exception;

	/**
	 * 扫码支付
	 * 
	 * @param u
	 * @param cpr
	 * @param inputAccType
	 *            订单输入类型 0 及时入账 5 T5到账
	 * @param authCode
	 * @param money
	 * @param transPayType
	 *            订单支付目的 10 交易流水 20表示购买代理
	 * @param desc
	 *            支付描述
	 * @return
	 * @throws Exception
	 */
	Map<String, String> createSmUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, String authCode, Double money, Integer transPayType, Integer angentType, String desc) throws Exception;

	/**
	 * 线上支付
	 * 
	 * @param u
	 * @param cpr
	 * @param inputAccType
	 *            入账类型 （0， 1,5, 8,10, 11, 100 ，对应着 D0，T1，T5，T8，大额D0、大额T1、 T10 ）
	 * @param money
	 * @param transPayType
	 * @param desc
	 * @param params
	 * @return
	 * @throws Exception
	 */
	Map<String, String> createOnLineOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, String desc, Map<String, String> params) throws Exception;

	/**
	 * 发送支付请求，成功后创建订单
	 * 
	 * @param user
	 * @param cpr
	 * @param inputAccType
	 * @param cardId
	 * @param frontUrl
	 * @param money
	 * @param transPayType
	 * @return
	 */

	public PaymentResponse createYLZXOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc);

	/**
	 * 发送支付请求，成功后创建订单
	 * 
	 * @param user
	 * @param cpr
	 * @param inputAccType
	 * @param cardId
	 * @param frontUrl
	 * @param money
	 * @param transPayType
	 * @return
	 */

	public Map<String, String> createYLZXOrder_v2(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc);

	/**
	 * 发送银联直通车支付请求，成功后创建订单[从申孚、哲扬通道开始]
	 * 
	 * @param user
	 * @param cpr
	 * @param inputAccType
	 * @param cardId
	 * @param frontUrl
	 * @param money
	 * @param transPayType
	 * @return
	 */
	public Map<String, String> createUnipayOnlineThroughOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc);

	/**
	 * 重新查询
	 * 
	 * @param orderNum
	 * @return
	 */
	Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum);

	/**
	 * 对账单
	 * 
	 * @param dateStr
	 * @return
	 */
	Boolean dealDownloadStatement(String dateStr);

	public void dealChannelT0Tixian(Long channelId, Map<String, String> params);

	public void sendT0TixianSearch(String orderNum);

	/**
	 * 查询嘎吱商户账户余额
	 * 
	 * @param merNo
	 * @return
	 */
	public Map<String, String> queryGaZhiBalance(String merNo);

	/**
	 * 查询商户提款手续费查询
	 * 
	 * @param merNo
	 *            嘎吱商户号
	 * @param amount
	 *            提现金额
	 * @return
	 */
	public Map<String, String> queryGaZhiWithdrawFee(String merNo, String amount);

	/**
	 * 嘎吱商户提款
	 * 
	 * @param merNo
	 * @param tranAmt
	 * @param factAmt
	 * @param feeAmt
	 * @return
	 */
	public Map<String, String> drawGaZhi(String merNo, String tranAmt, String factAmt, String feeAmt);

	/**
	 * 嘎吱商户提款结果查询
	 * 
	 * @param merNo
	 * @param oldMerTrace
	 * @param oldTransDate
	 * @param oldTransTime
	 * @return
	 */
	public Map<String, String> queryGaZhiDrawResult(String merNo, String oldMerTrace, String oldTransDate, String oldTransTime);

	/**
	 * 哲扬通道--商户新增信用卡
	 */
	public Map<String, String> addCreditCard(Map<String, String> params);

	/**
	 * 哲扬通道--开通支付接口 ,(哲扬声称有的通道需要签约才有这一步，我们暂时不需要做）
	 */
	/*
	 * public Map<String,String> fastpayOpenToken(Map<String, String> params);
	 */

	/**
	 * 线上支付,哲扬通道第二种方案，商户不再进件，下单时直接传递商户信息。
	 * 
	 * @param u
	 * @param cpr
	 * @param inputAccType
	 * @param money
	 * @param transPayType
	 * @param desc
	 * @param params
	 * @return
	 * @throws Exception
	 */
	Map<String, String> createOnLineOrder2(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, String desc, Map<String, String> params) throws Exception;

	/**
	 * 哲扬通道--确认支付
	 * 
	 * @param params
	 * @return
	 */
	public Map<String, String> fastpayCheckMessage(Map<String, Object> params);

	/**
	 * 哲扬通道--修改结算卡接口
	 * 
	 * @param params
	 */
	public Map<String, String> updateBankAccountInfo(Map<String, String> params);

	/**
	 * 哲扬通道--修改商户费率接口
	 * 
	 * @param params
	 * @return
	 */
	public Map<String, String> updateFastPayFee(Map<String, Object> params);

	/**
	 * 代付
	 * 
	 * @param params
	 * @return
	 */
	public Map<String, String> sendDaiFuReq(Map<String, String> params);

}
