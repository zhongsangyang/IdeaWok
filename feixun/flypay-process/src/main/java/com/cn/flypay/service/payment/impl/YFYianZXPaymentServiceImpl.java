package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.pageModel.account.Account;
import com.cn.flypay.pageModel.account.AccountPoint;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserCard;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.ImportUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.yilian.YiLianYlzxUtil;
import com.rd.constant.ValueConstant;
import com.rd.model.MerchantOrderPayReq;
import com.rd.model.MerchantPayQryReq;

/**
 * 
 * 易联通道--手机控件支付接口
 * 通道name = 'YILIANZHIFU'
 * @author liangchao
 *
 */
@Service(value = "yfyianZXPaymentService")
public class YFYianZXPaymentServiceImpl extends AbstractChannelPaymentService{
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private BaseDao<TuserCard> userCardDao;
	
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	
	
	
	@Override
	public Map<String, String> createYLZXOrder_v2(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String orderNum = "YF"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			TuserCard cardJ = userCardDao.get("select t from TuserCard t left join t.bank left join t.user u  where u.id=" + user.getId() +" and t.isSettlmentCard = 1 ");
			if(cardJ==null){
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG", "未设置结算卡");
				return resultMap;
			}
			BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(user.getId(), UserOrder.trans_type.YLZX.getCode(), inputAccType);
			BigDecimal fee = getInputOrderFee(inputAccType, transPayType, new BigDecimal(money), rate[0]);
			
			
			JSONObject config = cpr.getConfig();
			MerchantOrderPayReq req = new MerchantOrderPayReq();
			//商户订单号
			req.setTransactionId(orderNum);
			//商户订单金额
			req.setOrderAmount(String.valueOf(money));
			//币种
			req.setCur("CNY");
			
			//服务器接受支付结果的后台地址
			req.setBgUrl(config.getString("ylaccount.bankUrl"));	//  payment/yf_ylzx_Notify
			//下单ip
			req.setBuyerIp("192.168.1.14");
			//1007：手机控件支付（同名鉴权）（测试填写1005，生产填写1007）
			req.setPayType("1007");
			//固定手续费
			req.setFixFee(String.valueOf(fee));
			//收款银行卡号
			req.setPayerAcc(card.getCardNo());
			//收款银行名称
			req.setPayeeBankName(cardJ.getBank().getBankName());
			//收款人身份证号
			req.setPayeeIdNum(user.getIdNo());
			//收款银行卡户名
			req.setPayeeName(user.getRealName());
			//调用易联支付--手机控件支付接口
			JSONObject result = YiLianYlzxUtil.send(req, ValueConstant.TRANS_CODE_T01014, config.getString("ylaccount.merId"));
			if (result!=null) {
				if(result.containsKey("retCode") && result.getString("retCode").equals("RC0002")){
					if(result.containsKey("tn")){
						resultMap.put("tn", result.getString("tn"));
						userOrderService.createTransOrder(user.getId(), orderNum, null, null, UserOrder.trans_type.YLZX.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType,
								cpr.getChannel(), inputAccType, angentType);
						resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
					}
				}else{
					resultMap.put("flag", GlobalConstant.RESP_CODE_051);
					resultMap.put("flagMSG", result.getString("retRemark"));
				}
			}else {
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_051));
			}
		} catch (Exception e) {
			log.error("---易联在线异常---", e);
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_051));
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
	
	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {
		UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNum);
		if (userOrder != null) {
			JSONObject channelJson = channelService.getChannelConfig(userOrder.getChannelId());
			try {
				MerchantPayQryReq req = new MerchantPayQryReq();
				req.setTransactionId("YFYLZXCX"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				req.setRefTxnId("ZF20170703172531");
				JSONObject result = YiLianYlzxUtil.send(req, ValueConstant.TRANS_CODE_Q01001, channelJson.getString("ylaccount.merId"));
				if(result!=null && result.getString("qryRetCode").equals("RC0000")){
					PayOrder payOrder = new PayOrder();
					payOrder.setPayAmt(BigDecimal.valueOf(result.getDoubleValue("orderAmount")));
					payOrder.setRealAmt(BigDecimal.valueOf(result.getDoubleValue("orderAmount")));
					payOrder.setPayFinishDate(new Date().toString());
					payOrder.setPayNo(result.getString("transactionId"));
					Boolean flag = false;
					if ("RC0000".equals(result.getString("retCode"))) {
						flag=true;
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
					} else if ("RC0003".equals(result.getString("retCode"))) {
						flag=true;
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
					}
					if (flag) {
						userOrderService.finishInputOrderStatus(orderNum, payOrder);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	
	private BigDecimal getInputOrderFee(Integer inputAccType, Integer transPayType, BigDecimal amtbd, BigDecimal feeRate) {
		BigDecimal fee = BigDecimal.ZERO;
		if (inputAccType == 0 || inputAccType == 1 || inputAccType == 10 || inputAccType == 11) {
			if (transPayType - UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode() == 0) {
				// T0 输入的手续费，最少0.01元
				fee = BigDecimal.valueOf(0.01d);
				BigDecimal realAmt = amtbd.multiply(feeRate);
				if (realAmt.compareTo(fee) >= 0) {
					fee = realAmt.setScale(2, RoundingMode.UP);
				}
			}
		} else if (inputAccType == 5) {
			// T5 或者 购买代理类型
			fee = BigDecimal.ZERO;
		}
		return fee;
	}
	
	
	

}
