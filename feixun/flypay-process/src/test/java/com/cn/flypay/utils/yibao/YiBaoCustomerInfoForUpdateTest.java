package com.cn.flypay.utils.yibao;


import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.CustomerInforUpdatePartsBuilder;


/**
 * 易宝测试子商户信息修改
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoCustomerInfoForUpdateTest {
	@Test
	public void testCustomerInfoForUpdate(){
		
		try {
			//传递所需参数
			String behavior = "customerInforUpdate";	//子商户信息修改
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String customerNumber = "10018555100";	//子商户编号
			
			
			//1,白名单信息  2，银行卡信息  3，结算信息  4，分润信息  5，开通扫码支付  6，子商户基本信息
			String modifyType = "2";		//修改类型
			
			//modifyType = 1  已弃用
			String whiteList = "";	//白名单
			//modifyType = 1 已弃用
			String freezeDays = "";	//冻结天数
			
			
			//modifyType = 2   仅支持借记卡
			String bankCardNumber = "6217002930106637564";	//银行卡号
			//modifyType = 2   对公对私取值不同，参考文档
			String bankName ="建设银行";	//开户行
			
			//modifyType = 3  结算周期  参考文档
			String riskReserveDay = "";		//结算周期
			//modifyType = 3 时
			String manualSettle = "";	//是否自助结算	N隔天自动打款  Y自助结算
			
			//modifyType = 4 时必须
			String splitter = "";	//分润方
			
			//modifyType = 4 时必填
			String splitterProfitFee = "";	//分润比率
			
			//modyfyType = 6 时，下3必选其一
			String bindMobile = "";		//手机号
			String mailStr = "";	//电子邮箱
			String areaCode = "";	//地区码
			
			StringBuffer signature = new StringBuffer();
			
			if ("1".equals(modifyType)) {
			    signature
			            .append(mainCustomerNumber == null ? ""
			                    : mainCustomerNumber)
			            .append(customerNumber == null ? "" : customerNumber)
			            .append(whiteList == null ? "" : whiteList)
			            .append(freezeDays == null ? "" : freezeDays);
			} else if ("2".equals(modifyType)) {
			    signature
			            .append(mainCustomerNumber == null ? ""
			                    : mainCustomerNumber)
			            .append(customerNumber == null ? "" : customerNumber)
			            .append(bankCardNumber == null ? "" : bankCardNumber)
			            .append(bankName == null ? "" : bankName);
			} else if ("3".equals(modifyType)) {
			    signature
			            .append(mainCustomerNumber == null ? ""
			                    : mainCustomerNumber)
			            .append(customerNumber == null ? "" : customerNumber)
			            .append(riskReserveDay == null ? "" : riskReserveDay)
			            .append(manualSettle == null ? "" : manualSettle);
			} else if ("4".equals(modifyType)) {
			    signature
			            .append(mainCustomerNumber == null ? ""
			                    : mainCustomerNumber)
			            .append(customerNumber == null ? "" : customerNumber)
			            .append(splitter == null ? "" : splitter)
			            .append(splitterProfitFee == null ? ""
			                    : splitterProfitFee);
			} else if ("6".equals(modifyType)) {
			    signature
			            .append(mainCustomerNumber == null ? ""
			                    : mainCustomerNumber)
			            .append(customerNumber == null ? "" : customerNumber)
			            .append(bindMobile == null ? "" : bindMobile)
			            .append(mailStr == null ? "" : mailStr);
			}
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			Part[] parts = new CustomerInforUpdatePartsBuilder()
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setCustomerNumber(customerNumber)
			        .setModifyType(modifyType)
			        
			        .setWhiteList(whiteList)
			        .setFreezeDays(freezeDays)
			        
			        .setBankCardNumber(bankCardNumber)
			        .setBankName(bankName)
			        
			        .setRiskReserveDay(riskReserveDay)
			        .setManualSettle(manualSettle)
			        
			        .setSplitter(splitter)
			        .setSplitterProfitFee(splitterProfitFee)
			        .setHmac(hmac).generateParams();
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
