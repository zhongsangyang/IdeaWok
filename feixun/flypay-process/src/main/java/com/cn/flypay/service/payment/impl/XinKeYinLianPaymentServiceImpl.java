package com.cn.flypay.service.payment.impl;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.DESutil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.xinke.XinkePayUtil;


@Service(value = "xinkeYinLianPaymentService")
public class XinKeYinLianPaymentServiceImpl extends AbstractChannelPaymentService{
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private BaseDao<TuserCard> userCardDao;
	
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	
	
	
	@Override
	public Map<String, String> createYLZXOrder_v2(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String orderNum = "FF"+DateUtil.convertCurrentDateTimeToString();
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			JSONObject config = cpr.getConfig();
			
			
//			String[] keys = { "instCode", "merId", "orderNo", "orderTime", "currencyCode", "orderAmount", "name",
//			           "idNumber","acctNo","tellNo","productType","paymentType","frontUrl","backUrl"};
//	         String[] params = {"00000021", config.getString("ylaccount.merId"), orderNum,DateUtil.convertCurrentDateTimeToString(),
//			"156",String.valueOf(money*100),"asdfsd","341126197709218366","6216261000000000018","13552535506","100000","2008",
//			frontUrl,config.getString("ylaccount.bankUrl")};
			
			String[] keys = { "instCode", "merId", "orderNo", "orderTime", "currencyCode", "orderAmount", "name",
			           "idNumber","acctNo","tellNo","productType","paymentType","frontUrl","backUrl"};
	        String[] params = { "00000021", config.getString("xinke.merchant_id"), "FF"+DateUtil.convertCurrentDateTimeToString(),DateUtil.convertCurrentDateTimeToString(),
			"156",doubleTrans2(money*100),user.getRealName(),user.getIdNo(),card.getCardNo(),card.getPhone(),"100000","2008",
			"https://bbpurse.com/flypayfx/payment/xk_ylzx_Notify","https://bbpurse.com/flypayfx/payment/xk_ylzx_Notify"};
			
			
	        String response = XinkePayUtil.build(keys, params, "online_gate_waypay");
			
	        
	        if (StringUtil.isNotBlank(response)) {
	        	userOrderService.createTransOrder(user.getId(), orderNum, null, null, UserOrder.trans_type.YLZXJ.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType,
						cpr.getChannel(), inputAccType, angentType);
				resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
				
				response = DESutil.encrypt(response, DESutil.password);
				response = URLEncoder.encode(response);
				resultMap.put("html", "http://101.200.34.95:26370/flypayfx/mobile/XinkeResponse?result="+response);
			} else {
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			}
			
			
		} catch (Exception e) {
			log.error("---易联在线异常---", e);
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
	
	
	public static String doubleTrans2(double num){
	    if(Math.round(num)-num==0){
		        return String.valueOf((long)num);
	    }
	    return String.valueOf(num);
}
	

}
