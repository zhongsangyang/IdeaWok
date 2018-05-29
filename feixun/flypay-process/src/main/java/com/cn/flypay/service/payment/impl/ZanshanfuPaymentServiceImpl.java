package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.payment.enums.PayStatus;
import com.cn.flypay.model.payment.enums.TradeStatus;
import com.cn.flypay.model.payment.request.PaymentRequest;
import com.cn.flypay.model.payment.response.PaymentResponse;
import com.cn.flypay.model.payment.response.PaymentResult;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.utils.ApiUtil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.JsonUtil;
import com.cn.flypay.utils.StringUtil;

@Service(value = "zanshanfuPaymentService")
public class ZanshanfuPaymentServiceImpl extends AbstractChannelPaymentService {
	private Log log = LogFactory.getLog(getClass());

	private static final String ELEMENT_INPUT = "input";
	private static final String ELEMENT_VALUE = "value";
	private static final String ELEMENT_ID = "orderId";

	@Value("${do.pay.url}")
	private String do_pay_url;
	@Value("${do.query.url}")
	private String do_query_url;
	@Autowired
	private BaseDao<TuserCard> userCardDao;

	@Override
	public PaymentResponse createYLZXOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer agentType, String dec) {

		PaymentResponse response = new PaymentResponse();
		try {
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			JSONObject config = cpr.getConfig();

			PaymentRequest request = new PaymentRequest(config.getString("ylaccount.bankUrl"), config.getString("ylaccount.appId"), config.getString("ylaccount.channel"));
			request.setAccNo(card.getCardNo());
			if (card.getBank() != null) {
				request.setBankCode(card.getBank().getCode());
			}
			request.setCardNo(card.getCardNo());
			request.setFrontUrl(frontUrl);
			request.setmPay(BigDecimal.valueOf(money));
			String orderNum = commonService.getUniqueOrderByType(UserOrder.trans_type.YLZX.name(), user.getId());
			request.setOrderId(orderNum);
			// request.setPayType(card.getBranchId());

			response = doYLZXGet(response, request);

			if (response.getResult().isSuccess()) {
				String desc = user.getRealName() + "_" + user.getLoginName() + "正在用" + card.getCardNo() + "支付" + money + "元";
				userOrderService.createTransOrder(user.getId(), orderNum, null, null, UserOrder.trans_type.YLZX.getCode(), money, UserOrder.cd_type.D.name(), response.getResult()
						.getPaymenrVoucherNo(), card, desc, transPayType, channelService.get(8l), inputAccType, agentType);
				response.setFlag(GlobalConstant.RESP_CODE_SUCCESS);
			} else {
				response.setFlag(GlobalConstant.RESP_CODE_051);
			}
		} catch (Exception e) {
			log.error("发送银联在线支付请求出错", e);
			response.setFlag(GlobalConstant.RESP_CODE_051);
		}
		return response;

	}

	private PaymentResponse doYLZXGet(PaymentResponse response, PaymentRequest request) throws Exception {
		StringBuffer params = new StringBuffer();
		params.append("?back_url=" + request.getBankUrl());

		if (StringUtil.isNotEmpty(request.getFrontUrl())) {
			params.append("&front_url=" + request.getFrontUrl());
		}
		if (request.getmPay() != null) {
			params.append("&m_pay=" + request.getmPay().doubleValue());
		}
		if (StringUtil.isNotEmpty(request.getAppId())) {
			params.append("&app_id=" + request.getAppId());
		}
		if (StringUtil.isNotEmpty(request.getChannel())) {
			params.append("&channel=" + request.getChannel());
		}
		if (StringUtil.isNotEmpty(request.getExtra())) {
			params.append("&extra=" + request.getExtra());
		}
		if (request.getAccNo() != null) {
			params.append("&accNo=" + request.getAccNo());
		}
		if (StringUtil.isNotEmpty(request.getOrderId())) {
			params.append("&order_id=" + request.getOrderId());
		}
		if (StringUtil.isNotEmpty(request.getBankCode())) {
			params.append("&bank_code=" + request.getBankCode());
		}

		if (StringUtil.isNotEmpty(request.getPayType())) {
			params.append("&pay_type=" + request.getPayType());
		}
		/** 四要素 */
		if (StringUtil.isNotEmpty(request.getCardNo())) {
			params.append("&card_no=" + request.getCardNo());
		}
		if (StringUtil.isNotEmpty(request.getOwner())) {
			params.append("&owner=" + request.getOwner());
		}
		if (StringUtil.isNotEmpty(request.getPhone())) {
			params.append("&phone=" + request.getPhone());
		}
		if (StringUtil.isNotEmpty(request.getCertNo())) {
			params.append("&cert_no=" + request.getCertNo());
		}
		ApiUtil apiUtil = new ApiUtil();
		PaymentResult paymentResult = new PaymentResult();
		paymentResult.setAppOrderId(request.getOrderId());
		try {
			String result = apiUtil.doGet(do_pay_url + params.toString());
			// 获取攒善付 支付订单号
			String payOrderId = getPayOrderId(result);
			if (result.contains("<html") && StringUtil.isNotBlank(payOrderId)) {
				paymentResult.setAppOrderId(request.getOrderId());
				paymentResult.setPaymenrVoucherNo(payOrderId);
				paymentResult.setTradeStatus(TradeStatus.PAY_PROCESS);
				paymentResult.setSuccess(true);
				response.setHtmlStr(result);
			} else {
				paymentResult.setMsg(result);
				paymentResult.setTradeStatus(TradeStatus.PAY_FAIL);
			}
		} catch (Exception t) {
			paymentResult.setMsg("订单号:" + request.getOrderId() + "发起支付请求异常");
			log.info("订单号:" + request.getOrderId() + "发起支付请求异常,异常信息" + t.getMessage() + "异常内容:" + t);
			throw t;
		}
		response.setResult(paymentResult);
		return response;
	}

	/**
	 * 根据返回的表单 发起模拟 post请求获取攒善付订单各项参数
	 * 
	 * @return
	 */
	private String getPayOrderId(String source) {
		String reg = "<" + ELEMENT_INPUT + "[^<>]*?\\s" + "id='" + ELEMENT_ID + "'[^<>]*?\\s" + ELEMENT_VALUE + "=['\"]?(.*?)['\"]?\\s.*?>";
		Matcher m = Pattern.compile(reg).matcher(source);
		String orderId = null;
		while (m.find()) {
			orderId = m.group(1);
		}
		return orderId;

	}

	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {

		UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNum);
		if (userOrder != null && StringUtil.isNotBlank(userOrder.getPayOrderPayNo())) {
			PaymentResult result = doPost(userOrder.getPayOrderPayNo());
			if (result.isSuccess() && result.getsPay().getCode() == PayStatus.PAY.getCode()) {

				if (userOrder != null) {
					PayOrder payOrder = new PayOrder();
					payOrder.setPayAmt(result.getmActual());
					payOrder.setRealAmt(result.getmActual());

					payOrder.setPayFinishDate(DateUtil.convertCurrentDateTimeToString());

					payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
					try {
						userOrderService.finishInputOrderStatus(orderNum, payOrder);
					} catch (Exception e) {
						log.error("----银联订单回调异常", e);
					}

				} else {
					log.info("银联订单：" + result.getAppOrderId() + " 不存在或已经被成功处理了");
				}
			} else {
				log.info("银联订单：" + result.getAppOrderId() + " 未支付");
			}
		}
		return null;

	}

	/**
	 * 根据攒善付订单id获取支付结果
	 * 
	 * @param orderId
	 * @return
	 */
	private PaymentResult doPost(String orderId) {
		StringBuffer params = new StringBuffer();
		params.append(do_query_url);
		params.append("?order_id=" + orderId);
		ApiUtil apiUtil = new ApiUtil();
		PaymentResult paymentResult = new PaymentResult();
		try {
			String result = apiUtil.doGet(params.toString());

			HashMap resultMap = JsonUtil.toObject(result, HashMap.class);
			log.info("请求支付返回结果:【" + resultMap + "】");
			if (MapUtils.isNotEmpty(resultMap)) {
				paymentResult.setSuccess(true);
				paymentResult.setMsg(((HashMap) resultMap.get("info")).get("msg").toString());
				if ((Integer) resultMap.get("result") == 0) {
					HashMap infoMap = (HashMap) resultMap.get("info");
					if (infoMap != null && infoMap.containsKey("data")) {
						HashMap dataMap = (HashMap) infoMap.get("data");
						if (dataMap != null) {
							if (dataMap.containsKey("order_id") && dataMap.get("order_id") != null) {
								paymentResult.setPaymenrVoucherNo(dataMap.get("order_id").toString());
							}
							if (dataMap.containsKey("extra") && dataMap.get("extra") != null) {
								paymentResult.setExtra(dataMap.get("extra").toString());
							}
							if (dataMap.containsKey("m_actual") && dataMap.get("m_actual") != null) {
								paymentResult.setmActual(new BigDecimal(dataMap.get("m_actual").toString()));
							}
							if (dataMap.containsKey("s_pay") && dataMap.get("s_pay") != null) {
								paymentResult.setsPay(PayStatus.getByCode(Integer.parseInt(dataMap.get("s_pay").toString())));
							}

						}
					}
				}
			}
		} catch (Throwable t) {
			paymentResult.setMsg("订单号:" + orderId + "发起查询支付异常");
			log.info("订单号:" + orderId + "发起支付请求异常,异常信息" + t.getMessage() + "异常内容:" + t);

		}
		return paymentResult;
	}

	@Override
	public Boolean dealDownloadStatement(String dateStr) {
		return null;
	}
}
