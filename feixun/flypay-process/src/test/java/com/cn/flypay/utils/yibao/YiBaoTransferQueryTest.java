package com.cn.flypay.utils.yibao;


import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.TransferQueryPartsBulider;

/**
 * 易宝结算记录查询
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoTransferQueryTest {
	
	@Test
	public void testtransferQuery(){
		try {
			//传递所需参数
			String behavior = "transferQuery";
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String customerNumber = "";	//子商户编号
			//可选
			String externalNo = "";		//结算请求唯一号
			//可选
			String serialNo = "";	//收款宝流水号

			String requestDataSectionBegin = "2018-03-31 00:00:00";	//结算请求时间
			String requestDataSectionEnd = "2018-03-31 23:59:59";	//结算请求时间
			//可选
			String transferStatus = "SUCCESSED";	//结算状态  RECEIVED:已接受  PROCESSED:处理中  SUCCESSED:打款成功FAILED:打款失败 REFUNED:已退款 CANCELLED:已撤销
			String transferWay = "1";	//结算方式  1：T0自助结算  2：T1自助结算  3：T1自动结算
			String pageNo = "1";	//页码 每页20条
			
			StringBuilder signature = new StringBuilder();
			signature.append(customerNumber == null ? "" : customerNumber)
				.append(externalNo == null ? "" : externalNo)
			 	.append(mainCustomerNumber == null ? "" : mainCustomerNumber)
			 	.append(pageNo == null ? "" : pageNo)
				.append(requestDataSectionBegin == null ? "" : requestDataSectionBegin)
				.append(requestDataSectionEnd == null ? "" : requestDataSectionEnd)
			    .append(serialNo == null ? "" : serialNo)
			    .append(transferStatus == null ? "" : transferStatus)
			    .append(transferWay == null ? "" : transferWay);
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			Part[] parts = new TransferQueryPartsBulider()
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setCustomerNumber(customerNumber)
			        .setExternalNo(externalNo)
			        .setSerialNo(serialNo)
			        .setRequestDataSectionBegin(requestDataSectionBegin)
			        .setrequestDataSectionEnd(requestDataSectionEnd)
			        .setTransferStatus(transferStatus)
			        .setTransferWay(transferWay)
			        .setpageNo(pageNo)
			        .setHmac(hmac)
			        .generateParams();
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
