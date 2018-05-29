package com.cn.flypay.utils.yibao;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.ReceviePartsBuiler;


/**
 * 易宝测试收款
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoReceiveApiTest {
	@Test
	public void testReceiveApi(){
		try {
			//传递所需参数
			String behavior = "receiveApi";	//
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String customerNumber = "10018524674";	//子商户编号
			
			String requestId = "PAY" +new SimpleDateFormat("yyMMdd_HHmmssSSS").format(new Date());	//收款订单号
			String source ="B";	//D:卡号收款  B：店主代付  S:短信收款	T:二维码收款
			String amount = "4980.0";	//订单金额
			
			String mcc = "5311";	//5311:百货商店 4511:航空公司  4733：大型景区售票
			String callBackUrl = "https://bbpurse.com/flypayfx/payment/yibao_ylzx_Notify";	//收款成功回调地址
			//支付方式为D/B时必填
			String webCallBackUrl = "http://www.baidu.com";	//支付成功页面重定向地址
			String mobileNumber = "13774665436";	//付款人手机号
			
			//可选 支付方式为S时用到
			String smgCallBackUrl = "";	//短信发送成功回调地址
			//可选  透过支付卡号到wap收银台,可以跳过卡号输入页面
			String payerBankAccountNo = "6221560601399317";	//支付卡号
			
			//可选  代理商开通需要开通“指定体现卡号”功能
			String withdrawCardNo = "6217001930009512915";	//指定提现卡号
			//可选  代理商需要先开通“逐笔结算功能”。
			String autoWithdraw = "true";	//逐笔结算  是否自动发起出款请求
			//可选  代理商需要先开通“定制手续费”功能
			String customeFee = "";	//定制手续费
			//可选   
			String withdrawCallBackUrl = "";	//逐笔结算回调地址
		
			
			StringBuilder hmacStr = new StringBuilder();
	        hmacStr.append(source == null ? "" : source)
	                .append(mainCustomerNumber == null ? "" : mainCustomerNumber)
	                .append(customerNumber == null ? "" : customerNumber)
	                .append(amount == null ? "" : amount)
	                .append(mcc == null ? "" : mcc)
	                .append(requestId == null ? "" : requestId)
	                .append(mobileNumber == null ? "" : mobileNumber)
	                .append(callBackUrl == null ? "" : callBackUrl)
	                .append(webCallBackUrl == null ? "" : webCallBackUrl)
	                .append(smgCallBackUrl == null ? "" : smgCallBackUrl)
	                .append(payerBankAccountNo == null ? "" : payerBankAccountNo);

	        System.out.println("===============");
	        System.out.println("hmacStr.toString()=" + hmacStr.toString());
	        System.out.println("===============");
	       
	       String hmac = Digest.hmacSign(hmacStr.toString(), YiBaoBaseUtil.hmacKey);
	        //String hmac = Digest.hmacSign("f2fa3a5f761439ce29c1f53f9eed3575", hmacKey);
	        System.out.println("===============");
	        System.out.println("hmac=" + hmac);
	        System.out.println("===============");
			
			StringBuilder signature = new StringBuilder();
			signature.append(source == null ? "" : source)
				.append(mainCustomerNumber == null ? "" : mainCustomerNumber)
				.append(customerNumber == null ? "" : customerNumber)
				.append(amount == null ? "" : amount)
				.append(mcc == null ? "" : mcc)
				.append(requestId == null ? "" : requestId)
				.append(mobileNumber == null ? "" : mobileNumber)
				.append(callBackUrl == null ? "" : callBackUrl)
				.append(webCallBackUrl == null ? "" : webCallBackUrl)
				.append(smgCallBackUrl == null ? "" : smgCallBackUrl)
				.append(payerBankAccountNo == null ? "" : payerBankAccountNo)
//				.append(withdrawCardNo == null ? "" : withdrawCardNo)
//				.append(autoWithdraw == null ? "" : autoWithdraw)
				;
				
			//生成签名
//			String hmac2 = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			
			   System.out.println("===============");
		        System.out.println("signature.toString()=" + signature.toString());
		        System.out.println("===============");
		       
		       String hmac2 = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
		        //String hmac = Digest.hmacSign("f2fa3a5f761439ce29c1f53f9eed3575", hmacKey);
		        System.out.println("===============");
		        System.out.println("hmac2=" + hmac2);
		        System.out.println("===============");
		        

		        System.out.println("======source=" + hmacStr.toString().equals(signature.toString())   + "=========");
		        System.out.println("======hmac=" + hmac.equals(hmac2)   + "=========");
		        

			Part[] parts = new ReceviePartsBuiler()
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setCustomerNumber(customerNumber)
			        .setRequestId(requestId)
			        .setSource(source)
			        .setAmount(amount)
			        .setMcc(mcc)
			        .setMobileNumber(mobileNumber)
			        .setCallBackUrl(callBackUrl)
			        .setWebCallBackUrl(webCallBackUrl)
			        .setPayerBankAccountNo(payerBankAccountNo)
			        .setWithdrawCardNo(withdrawCardNo)
			        .setAutoWithdraw(autoWithdraw)
			        .setSmgCallBackUrl(smgCallBackUrl)
			        .setHamc(hmac)
                    .setDescription("")
//                	.setCustomFee(customeFee)
//			        .setCustomTradeFee(customeFee)	//有问题
//			        .setWithdrawCallBackUrl(withdrawCallBackUrl)
			        .generateParams();
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
