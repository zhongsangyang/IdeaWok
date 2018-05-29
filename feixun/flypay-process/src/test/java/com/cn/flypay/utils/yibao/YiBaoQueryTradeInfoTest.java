package com.cn.flypay.utils.yibao;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.AccountTradeQueryPartsBuilder;


/**
 * 易宝分润查询接口
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoQueryTradeInfoTest {
	@Test
	public void testQueryTradeInfo(){
		try {
			//传递所需参数
			String behavior = "queryTradeInfo";	
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			
			String splitterType = "";	//分润类型  TRADE_SPLITTER：交易分润
			//下方参数二选一
			String customerNumber = "";	//分润方编号/代理商编号
			String orderNo = "YB20180301171813";	//收款订单号
			
			String beginDate = "";	//开始时间
			String endDate = "";	//结束时间
			String pageNo = "1";		//分页参数
			
			StringBuffer signature = new StringBuffer();
			signature
			        .append(mainCustomerNumber == null ? ""
			                : mainCustomerNumber)
			        .append(customerNumber == null ? "" : customerNumber)
			        .append(splitterType == null ? "" : splitterType)
			        .append(beginDate == null ? "" : beginDate)
			        .append(endDate == null ? "" : endDate)
			        .append(orderNo == null ? "" : orderNo)
			        .append(pageNo == null ? "" : pageNo);
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			Part[] parts = new AccountTradeQueryPartsBuilder()
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setCustomerNumber(customerNumber)
			        .setSplitterType(splitterType).setBeginDate(beginDate)
			        .setEndDate(endDate).setOrderNo(orderNo).setPageNo(pageNo)
			        .setHmac(hmac).generateParams();
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
