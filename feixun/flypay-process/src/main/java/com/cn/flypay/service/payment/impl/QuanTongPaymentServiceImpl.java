package com.cn.flypay.service.payment.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.quantong.QuanTongPaymentUtil;




@Service(value = "quanTongPaymentService")
public class QuanTongPaymentServiceImpl extends AbstractChannelPaymentService{
	
	private Log log = LogFactory.getLog(getClass());
	
	
	
	@Override
	public Map<String, String> createUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, Integer angentType, String desc) throws Exception {
		   log.info("------------全晶通创建二维码start-----------------");
		   Map<String, String> result = new HashMap<String, String>();
		   Integer orderType = cpr.getChannel().getType();
		   Map<String, String> params = new HashMap<String, String>();
		   String type = UserOrder.trans_type.WXQR.name();
			if (orderType - UserOrder.trans_type.ALQR.getCode() == 0) {
				type = UserOrder.trans_type.ALQR.name();
			}
		    String out_trade_no = commonService.getUniqueOrderByType(type, user.getId());
			params.put("orderId", out_trade_no);
			params.put("Fee", String.valueOf(money*100));
			params.put("Time", DateUtil.convertCurrentDateTimeToString());
			if(inputAccType==0||inputAccType==10){
				params.put("clearType", "0");
			}else{
				params.put("clearType", "1");
			}
			JSONObject config = cpr.getConfig();
			JSONObject json = QuanTongPaymentUtil.PostQrCodeForApp(params, config.getString("mId"));
		    if(json!=null){
		    	if(json.get("code")!=null && json.get("code").equals("0")){
		    		try {
		    			result.put("orderNum", out_trade_no);
			    		result.put("return_code", "SUCCESS");
						result.put("result_code", "SUCCESS");
						result.put("code_url", String.valueOf(json.get("url")));
						log.info("全晶通URL:"+result.get("qrCode"));
						String order_desc = String.format(desc, money);
						userOrderService.createTransOrder(user.getId(), out_trade_no, null, null, orderType, money, UserOrder.cd_type.D.name(), null, null, order_desc, transPayType, cpr.getChannel(),
								inputAccType, angentType);
						return result;
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	}else{
		    		log.info("全晶通创建订单异常:"+json.get("code")+json.get("msg"));
		    	}
		    }
		   log.info("------------全晶通创建二维码end-----------------");
		return null;
	}
	
	

}
