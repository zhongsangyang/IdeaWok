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
import com.cn.flypay.utils.zanshanfu.ZanshanfuPayUtil;

@Service(value = "zanshanfuPayment_V2_Service")
public class ZanshanfuPayment_V2_ServiceImpl extends AbstractChannelPaymentService {
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
	public Map<String, String> createYLZXOrder_v2(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer agentType, String dec) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String orderNum = commonService.getUniqueOrderByType(UserOrder.trans_type.YLZX.name(), user.getId());

			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			JSONObject config = cpr.getConfig();
			JSONObject json = new JSONObject();

			json.put("action", "goAndPay");
			json.put("txnamt", money * 100);
			json.put("merid", config.getString("ylaccount.appId"));
			json.put("orderid", orderNum);
			json.put("backurl", config.getString("ylaccount.bankUrl"));
			json.put("fronturl", frontUrl);
			json.put("accname", user.getRealName());
			json.put("accno", user.getIdNo());
			json.put("cardno", card.getCardNo());

			String response = ZanshanfuPayUtil.sendPayOrderToZanshanfu(json,config.getString("ylaccount.key"));

			if (StringUtil.isNotBlank(response)) {
				String desc = user.getRealName() + "_" + user.getLoginName() + "正在用" + card.getCardNo() + "支付" + money + "元";
				userOrderService.createTransOrder(user.getId(), orderNum, null, null, UserOrder.trans_type.YLZX.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType,
						cpr.getChannel(), inputAccType, agentType);
				resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
				resultMap.put("html", response);
			} else {
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			}
		} catch (Exception e) {
			log.error("发送银联在线支付请求出错", e);
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
		}
		return resultMap;

	}

	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {

		UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNum);
		if (userOrder != null) {
			JSONObject channelJson = channelService.getChannelConfig(userOrder.getChannelId());
			JSONObject requestJson = new JSONObject();
			requestJson.put("orderid", orderNum);
			requestJson.put("merid", channelJson.getString("ylaccount.appId"));
			try {
				JSONObject responseJson = ZanshanfuPayUtil.sendSearchOrderToZanshanfu(requestJson);
				if (responseJson != null && "00".equals(responseJson.getString("respcode"))) {
					PayOrder payOrder = new PayOrder();
					payOrder.setPayAmt(BigDecimal.valueOf(responseJson.getDoubleValue("txnamt") / 100));
					payOrder.setRealAmt(BigDecimal.valueOf(responseJson.getDoubleValue("txnamt") / 100));

					payOrder.setPayFinishDate(responseJson.getString("paytime"));
					payOrder.setPayNo(responseJson.getString("queryid"));
					Boolean flag = false;
					if ("0000".equals(responseJson.getString("resultcode")) || "1002".equals(responseJson.getString("resultcode"))) {
						flag=true;
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
					} else if ("1003".equals(responseJson.getString("resultcode"))) {
						flag=true;
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
					}
					try {
						if (flag) {
							userOrderService.finishInputOrderStatus(orderNum, payOrder);
						}
					} catch (Exception e) {
						log.error("----银联订单回调异常", e);
					}
				} else {
					log.info("银联订单：" + orderNum + " 未支付");
				}
			} catch (Exception e1) {
				log.error("----银联订单回调异常", e1);
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
