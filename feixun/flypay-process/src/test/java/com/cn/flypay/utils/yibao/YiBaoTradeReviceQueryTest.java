package com.cn.flypay.utils.yibao;


import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.TradeReviceQueryPartsBuilder;

/**
 * 易宝交易查询接口
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoTradeReviceQueryTest {
	
	@Test
	public void testTradeReviceQuery(){
		try {
			//传递所需参数
			String behavior = "tradeReviceQuery";
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String customerNumber = "";	//子商户编号
			
			//可选
			String requestId = "";	//收款订单号
			//可选
			String createTimeBegin = "";	//请求时间-开始时间
			//可选
			String createTimeEnd = "";		//请求时间-结束时间
			//可选
			String payTimebegin = "";		//支付开始时间
			//可选
			String payTimeEnd = "";			//支付结束时间
			//可选
			String lastUpdateTimeBegin = "";	//收款状态更新时间--开始时间
			//可选
			String lastUpdateTimeEnd = "";		//收款状态更新时间--结束时间
			//可选
			String status = "";	//订单状态	INIT:未支付	 SUCCESS:成功  FAIL:失败  FROZEN：冻结  THAWED:解冻  REVERSE: 冲正
			
			
			
			String busiType = "";	//订单类型  COMMON:普通交易 
			String pageNo = "";	//必须是正整数--页数   每页显示20条数据
			
			
			StringBuilder signature = new StringBuilder();
			signature.append(customerNumber == null ? "" : customerNumber)
			        .append(mainCustomerNumber == null ? "" : mainCustomerNumber)
			        .append(requestId == null ? "" : requestId)
			        .append(createTimeBegin == null ? "" : createTimeBegin)
			        .append(createTimeEnd == null ? "" : createTimeEnd)
			        .append(payTimebegin == null ? "" : payTimebegin)
			        .append(payTimeEnd == null ? "" : payTimeEnd)
			        .append(lastUpdateTimeBegin == null ? "" : lastUpdateTimeBegin)
			        .append(lastUpdateTimeEnd == null ? "" : lastUpdateTimeEnd)
			        .append(status == null ? "" : status)
			        .append(busiType == null ? "" : busiType)
			        .append(pageNo == null ? "" : pageNo);
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			Part[] parts = new TradeReviceQueryPartsBuilder()
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setRequestId(requestId)
			        .setCreateTimeBegin(createTimeBegin)
			        .setCreateTimeEnd(createTimeEnd)
			        .setPayTimebegin(payTimebegin)
			        .setPayTimeEnd(payTimeEnd)
			        .setLastUpdateTimeBegin(lastUpdateTimeBegin)
			        .setLastUpdateTimeEnd(lastUpdateTimeEnd)
			        .setStatus(status)
			        .setBusiType(busiType)
			        .setPageNo(pageNo)
			        .setHmac(hmac).generateParams();
			        
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		        
	}
	
}
