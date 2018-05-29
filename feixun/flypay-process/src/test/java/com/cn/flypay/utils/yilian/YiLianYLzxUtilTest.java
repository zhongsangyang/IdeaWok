package com.cn.flypay.utils.yilian;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.rd.constant.ValueConstant;
import com.rd.model.MerchantOpenQuickPayReq;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class YiLianYLzxUtilTest {
	@Test
	public void testKaiTongKuaiJieZhiFu() {
		MerchantOpenQuickPayReq req = new MerchantOpenQuickPayReq();
		req.setTransactionId("YF"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		req.setPayType(ValueConstant.PAY_TYPE_1301);
		req.setPageUrl("http://1g83849h98.iask.in:34530/flypayfx/payment/yf_ylzx_Notify");
		req.setCardId("4218717016411786");
		req.setName("芦强");
		req.setIdNum("152822199012293814");
		req.setPhone("1381611119");
		try {
			JSONObject result = YiLianYlzxUtil.send(req, ValueConstant.TRANS_CODE_T01031, "0000000000000147");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
