package com.cn.flypay.utils.yibao;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.yibao.builder.RegisterPartsBuilder;

/**
 * 易宝测试子商户注册类
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring*.xml"})
public class YiBaoBaseRegisterTest {
	@Test
	public void testRegister(){
		try {
			//传递所需参数
			String behavior = "register";	//子商户注册
			
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String requestId= "";	//注册请求号，保持唯一
			String customerType = "PERSON";	//商户类型	ENTERPRIST 企业 INDIVIDUAL  个体工商户   PERSON 个人
			String bindMobile = "";	//商户注册手机号
			String singnedName = "";	//签约名
			String linkMan = "芦强";	//推荐人姓名
			String idCard = "";	//身份证号
			String legalPerson = "";	//商户法人姓名
			String minSettleAmount = "";	//起始金额
			String riskReserveDay = "";	//结算周期   0：T1 T0自助结算   1:T1自助结算
			String bankAccountType = "PrivateCash";	//银行卡类型  PrivateCash 对私  PublicCash 对公   为空则默认对私
			String bankAccountNumber = "";	//银行卡号
			String bankName = "";	//银行卡开户行
			String accountName = "";	//银行卡开户名
			String areaCode = "";	//地区码
			String manualSettle = ""; //是否自助结算   N：隔天自动打款   Y：自助结算
			
			File bankCardPhoto = new File("");	//银行卡正面照
			File idCardPhoto = new File("");	//身份证正面照
			File idCardBackPhoto = new File("");	//身份证背面照
			File personPhoto = new File("");	//身份证+银行卡+本人合照
			
			//按照参数顺序进行排列，不包括上传的文件
			StringBuilder signature = new StringBuilder();
	        signature.append(mainCustomerNumber == null ? ""
	                         : mainCustomerNumber)
	                 .append(requestId == null ? "" : requestId)
	                 .append(customerType == null ? "" : customerType)
	                 .append(bindMobile == null ? "" : bindMobile)
	                 .append(singnedName == null ? "" : singnedName)
	                 .append(linkMan == null ? "" : linkMan)
	                 .append(idCard == null ? "" : idCard)
	                 .append(legalPerson == null ? "" : legalPerson)
	                 .append(minSettleAmount == null ? "" : minSettleAmount)
	                 .append(riskReserveDay == null ? "" : riskReserveDay)
	                 .append(bankAccountType == null ? "" : bankAccountType)
	                 .append(bankAccountNumber == null ? "" : bankAccountNumber)
	                 .append(bankName == null ? "" : bankName)
	                 .append(accountName == null ? "" : accountName)
	                 .append(areaCode == null ? "" : areaCode)
	                 .append(manualSettle == null ? "" : manualSettle);
			
	        //生成签名
	        String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
	        
	        //生成Part
	        
			Part[] parts = new RegisterPartsBuilder()
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setRequestId(requestId)
			        .setCustomerType(customerType)
			        .setBindMobile(bindMobile)
			        .setSignedName(singnedName)
			        .setLinkMan(linkMan)
			        .setIdCard(idCard)
			        .setLegalPerson(legalPerson)
			        .setMinSettleAmount(minSettleAmount)
			        .setRiskReserveDay(riskReserveDay)
			        .setBankaccounttype(bankAccountType)
			        .setBankAccountNumber(bankAccountNumber)
			        .setBankName(bankName)
			        .setAccountName(accountName)
			        .setAreaCode(areaCode)
			        .setManualSettle(manualSettle)
			        .setHmac(hmac) 	//签名
			        .setBankCardPhoto(bankCardPhoto) //银行卡正面照  是
			        .setBusinessLicensePhoto(idCardPhoto)	//身份证正面照
			        .setIdCardPhoto(idCardPhoto)		//身份证正面照
			        .setIdCardBackPhoto(idCardBackPhoto)  //身份证背面照
			        .setPersonPhoto(personPhoto)	//身份证+银行卡+本人合照    是
			        .generateParams();
			
			//调用公用请求模块
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        
        
        
 		
	}
}
