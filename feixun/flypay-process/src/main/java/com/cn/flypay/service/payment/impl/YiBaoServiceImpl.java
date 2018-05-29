package com.cn.flypay.service.payment.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TcardBankConfig;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.pageModel.sys.Bank;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.payment.YiBaoService;
import com.cn.flypay.service.sys.BankService;
import com.cn.flypay.service.sys.CardBankConfigService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.ImportUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.yibao.Digest;
import com.cn.flypay.utils.yibao.YiBaoBaseUtil;
import com.cn.flypay.utils.yibao.builder.AuditMerchantPartsBuilder;
import com.cn.flypay.utils.yibao.builder.CustomerInforQueryPartsBuilder;
import com.cn.flypay.utils.yibao.builder.CustomerInforUpdatePartsBuilder;
import com.cn.flypay.utils.yibao.builder.FeeSetPartsBuilder;
import com.cn.flypay.utils.yibao.builder.QueryFeeSetPartsBuilder;
import com.cn.flypay.utils.yibao.builder.RegisterPartsBuilder;
/**
 * 易宝相关服务
 * @author liangchao
 *
 */
@Service
public class YiBaoServiceImpl implements YiBaoService{
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private BaseDao<TuserCard> userCardDao;
	@Autowired
	private UserService userService;
	@Autowired
	private BaseDao<Tchannel> channelDao;
	@Autowired
	private BankService bankService;
	@Autowired
	private CardBankConfigService bankConfigService;
	
	
	/**
	 * 更改易宝子商户绑定的结算卡
	 * @param mainCustomerNumber 代理商编号
	 * @param customerNumebr 子商户编号/分润方编号
	 * @param bankCardNumber 银行卡号
	 * @param bankName 开户行
	 * @return
	 */
	@Override
	public JSONObject changeMerchantCard(String customerNumber,String bankCardNumber,String bankName){
		JSONObject resInfo = new JSONObject();
		
		String behavior = "customerInforUpdate";	//子商户信息修改
		String mainCustomerNumber = YiBaoBaseUtil.customerNumber;
		String modifyType = "2";		//修改类型  1,白名单信息  2，银行卡信息  3，结算信息  4，分润信息  5，开通扫码支付  6，子商户基本信息
		
		StringBuffer signature = new StringBuffer();
		signature
		 		.append(mainCustomerNumber == null ? "" : mainCustomerNumber)
		 		.append(customerNumber == null ? "" : customerNumber)
		 		.append(bankCardNumber == null ? "" : bankCardNumber)
		 		.append(bankName == null ? "" : bankName);
		
		
		//生成签名
		String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
		Part[] parts = new CustomerInforUpdatePartsBuilder()
				.setMainCustomerNumber(mainCustomerNumber)
		        .setCustomerNumber(customerNumber)
		        .setBankCardNumber(bankCardNumber)
		        .setModifyType(modifyType)
		        .setBankName(bankName)
		        .setHmac(hmac).generateParams();;
		
		//调用公用请求模块
		JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);   
		log.info("-----更改易宝通道结算卡---请求服务器返回信息:" + result.toJSONString());
		if(result == null){
			resInfo.put("code", GlobalConstant.RESP_CODE_999);
			resInfo.put("return_msg", "连接通道提供方失败");
			return resInfo;
		}
		if(!result.getString("code").equals("0000")){
			resInfo.put("code", GlobalConstant.RESP_CODE_999);
			resInfo.put("return_msg", result.getString("message"));
			return resInfo;
		}
		//修改成功 
		resInfo.put("return_code",GlobalConstant.RESP_CODE_SUCCESS);
		return resInfo;
	};
	
	/**
	 * 子商户信息查询接口
	 * @param mainCustomerNumber	代理商编号
	 * @param phone	商户的手机号
	 * @param customerNumber	子商户编码
	 * @return
	 */
	@Override
	public JSONObject queryMerchantInfo(String mobilePhone , String customerNumber){
		JSONObject resInfo = new JSONObject();
		
		String behavior = "customerInforQuery";	//子商户信息查询
		
		String mainCustomerNumber = YiBaoBaseUtil.customerNumber;
		String customerType = "CUSTOMER";	//商户类型   不传默认为： CUSTOMER 子商户     SPLITTER 分润方 
		
		
		StringBuffer signature = new StringBuffer();
		signature
				.append(mainCustomerNumber == null ? "": mainCustomerNumber)
			 	.append(mobilePhone == null ? "" : mobilePhone)
			 	.append(customerNumber == null ? "" : customerNumber)
			 	.append(customerType == null ? "" : customerType);
		
		
		//生成签名
		String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
		
		Part[] parts = new CustomerInforQueryPartsBuilder()
		         .setMainCustomerNumber(mainCustomerNumber)
		         .setMobilePhone(mobilePhone)
		         .setCustomerNumber(customerNumber)
		         .setCustomerType(customerType)
		         .setHmac(hmac)
		         .generateParams();
		
		//调用公用请求模块
		JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		if(result == null){
			resInfo.put("code", GlobalConstant.RESP_CODE_999);
			resInfo.put("return_msg", "连接通道提供方失败");
			return resInfo;
		}
		if(!result.getString("code").equals("0000")){
			resInfo.put("code", GlobalConstant.RESP_CODE_999);
			resInfo.put("return_msg", result.getString("message"));
			return resInfo;
		}
		//查询成功 
		resInfo.put("return_code",GlobalConstant.RESP_CODE_SUCCESS);
		String retListStr = result.getString("retList");
		//去掉集合中的大括号
		String retListStr2 = retListStr.substring(1, retListStr.length()-1);
		
		resInfo.put("return_msg",retListStr2);
		return resInfo;
	};
	
	
	
	
	
	/**
	 * 子商户费率查询接口
	 * @param user_id
	 * @param merId 易宝子商户ID
	 * @param productType 产品类型
	 *  		1,无卡支付  2，T1自助结算  3，T0自助结算基本  4，T0自助结算工作日额外  5,T0自助结算非工作日额外
	 * @return
	 */
	@Override
	public JSONObject queryYiBaoFee(Long user_id,String merId,String productType){
		JSONObject resInfo = new JSONObject();
		String behavior = "queryFeeSetApi";	//子商户费率查询接口
		String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
		String customerNumber = merId;
		
		if(StringUtil.isBlank(productType)){
			resInfo.put("return_code", GlobalConstant.RESP_CODE_999);
			resInfo.put("return_msg","产品类型参数缺失");
			return resInfo;
		}
		
		//生成签名
		StringBuffer signature = new StringBuffer();
		signature.append(customerNumber == null ? "" : customerNumber)
		        .append(mainCustomerNumber == null ? "" : mainCustomerNumber)
		        .append(productType == null ? "" : productType);
		//生成签名
		String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
		
		
		Part[] parts = new QueryFeeSetPartsBuilder()
		        .setCustomerNumber(customerNumber)
		        .setMainCustomerNumber(mainCustomerNumber)
		        .setProductType(productType)
		        .setHmac(hmac).generateParams();
		
		//调用公用请求模块
		JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		
		if(result == null){
			resInfo.put("code", GlobalConstant.RESP_CODE_999);
			resInfo.put("return_msg", "连接通道提供方失败");
			return resInfo;
		}
		if(!result.getString("code").equals("0000")){
			resInfo.put("code", GlobalConstant.RESP_CODE_999);
			resInfo.put("return_msg", result.getString("message"));
			return resInfo;
		}
		
		//用户的费率
		resInfo.put("return_code",GlobalConstant.RESP_CODE_SUCCESS);
		resInfo.put("rate",result.getString("rate"));
		return resInfo;
	}
	
	
	
	
	/**
	 * 子商户费率设置接口
	 */
	@Override
	public JSONObject setYiBaoFee(Long user_id,String merId,String productType,String rate){
		JSONObject resInfo = new JSONObject();
		String behavior = "feeSetApi";	//子商户费率设置
		String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
		String customerNumber = merId;	//子商户编号
		
		StringBuffer signature = new StringBuffer();
		signature.append(customerNumber == null ? "" : customerNumber)
			.append(mainCustomerNumber == null ? "": mainCustomerNumber)
			.append(productType == null ? "" : productType)
			.append(rate == null ? "" : rate);
		
		//生成签名
		String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
		Part[] parts = new FeeSetPartsBuilder()
		        .setCustomerNumber(customerNumber)
		        .setGroupCustomerNumber(mainCustomerNumber)
		        .setProductType(productType)
		        .setHmac(hmac).setRate(rate).generateParams();
		
		//调用公用请求模块
		JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
		
		if(StringUtil.isNotBlank(result.getString("code")) 
				&& result.getString("code").equals("0000")){
			resInfo.put("return_code",GlobalConstant.RESP_CODE_SUCCESS);
			resInfo.put("return_msg",result.getString("message"));
			
			//保存校验通过的信息
			log.info("----易宝校验逻辑--易宝商户号为"+customerNumber+"  保存fee"+productType+"的值为"+ rate+"--开始");
			Tchannel c = channelDao.get("select t from Tchannel t  where t.status=10 and t.name='YIBAOZHITONGCHE' and t.userId="+ user_id +"");
			JSONObject merConfig = JSONObject.parseObject(c.getConfig());
			
			merConfig.put("fee"+productType, rate);
			c.setConfig(merConfig.toJSONString());
			channelDao.save(c);
			log.info("----易宝校验逻辑--易宝商户号为"+customerNumber+" 保存fee"+productType+"的值为"+ rate+"--成功");
			
			
			
		}else{
			resInfo.put("return_code", GlobalConstant.RESP_CODE_999);
			resInfo.put("return_msg",result.getString("message"));
		}
		return resInfo;
		
	};
	
	
	
	
	/**
	 * 查询并校验子商户的信息
	 */
	@Override
	public JSONObject customerInfoForQuery(Long user_id,String mainCustomerNumber, String mobilePhone, String customerNumber,
			String customerType,JSONObject validInfo) {
		
		JSONObject resInfo = new JSONObject();
		
		try {
			String behavior = "customerInforQuery";
			customerType = "CUSTOMER";
			StringBuilder signature = new StringBuilder();
			signature
					 .append(mainCustomerNumber == null ? "": mainCustomerNumber)
					 .append(mobilePhone == null ? "": mobilePhone)
					 .append(customerNumber == null ? "": customerNumber)
			         .append(customerType == null ? "" : customerType);
			
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			//生成Part
			Part[] parts = new CustomerInforQueryPartsBuilder()
			         .setMainCustomerNumber(mainCustomerNumber)
			         .setMobilePhone(mobilePhone)
			         .setCustomerNumber(customerNumber)
			         .setCustomerType(customerType)
			         .setHmac(hmac)
			         .generateParams();
			JSONObject reqJson = new JSONObject();
			reqJson.put("mainCustomerNumber", mainCustomerNumber);
			reqJson.put("mobilePhone", mobilePhone);
			reqJson.put("customerNumber", customerNumber);
			reqJson.put("customerType", customerType);
			
			//调用公用请求模块
			log.info("-----易宝----调用查询子商户信息接口--请求参数为"+reqJson.toJSONString());
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
			log.info("-----易宝----调用查询子商户信息接口---返回值为"+result.toJSONString());
			
			if(result == null){
				resInfo.put("code", GlobalConstant.RESP_CODE_999);
				resInfo.put("return_msg", "连接通道提供方失败");
				return resInfo;
			}else{
				//校验子商户信息是否和本地申请时一致
				if(!result.getString("code").equals("0000")){
					resInfo.put("code", GlobalConstant.RESP_CODE_999);
					resInfo.put("return_msg", result.getString("message"));
					return resInfo;
				}else{
//					JSONObject retList = result.getJSONObject("retList");
					String retListStr = result.getString("retList");
					System.out.println(retListStr);
					JSONObject retList = JSONObject.parseObject(retListStr.substring(1, retListStr.length()-1));
					
					boolean judge = true;
					StringBuilder str = new StringBuilder("");
					String localBankAccountNumber = validInfo.getString("bankAccountNumber");
					//身份证前4位
					String localBankAccountNumber1 = localBankAccountNumber.substring(0, 4);
					//身份证后4位
					String localBankAccountNumber2 = localBankAccountNumber.substring(localBankAccountNumber.length()-4, localBankAccountNumber.length());
					String yibaoBankAccountNumber = retList.getString("bankAccountNumber");
					String yibaoBankAccountNumber1 = retList.getString("bankAccountNumber").substring(0, 4);
					String yibaoBankAccountNumber2 = retList.getString("bankAccountNumber").substring(yibaoBankAccountNumber.length()-4, yibaoBankAccountNumber.length());
					
					
					String localIdCard = validInfo.getString("idCard");
					String localIdCard1 = localIdCard.substring(localIdCard.length()-4, localIdCard.length());
					String yibaoIdCard = retList.getString("idCard");
					String yibaoIdCard1 = yibaoIdCard.substring(yibaoIdCard.length()-4, yibaoIdCard.length());
					
					
					if(! (localBankAccountNumber1.equals(yibaoBankAccountNumber1) && localBankAccountNumber2.equals(yibaoBankAccountNumber2)) ){
						judge = false;
						str.append(" |bankAccountNumber 银行卡号 校验不一致 ");
					}
						
					if(!validInfo.getString("bankName").equals(retList.getString("bankName"))){
						judge = false;
						str.append(" |bankName 开户行 校验不一致 ");
					}
					if(!localIdCard1.equals(yibaoIdCard1)){
						judge = false;
						str.append(" |idCard  身份证号  校验不一致 ");
					}
					if(!validInfo.getString("linkMan").equals(retList.getString("linkMan"))){
						judge = false;
						str.append(" |linkMan 推荐人名称 校验不一致 ");
					}
					if(!validInfo.getString("riskReserveDay").equals(retList.getString("riskReserverDay"))){	//两个名称不一样，请注意，因为易宝提供的接口命名不一样
						judge = false;
						str.append(" |riskReserveDay 结算周期 校验不一致 ");
					}
					if(!validInfo.getString("singnedName").equals(retList.getString("signName"))){
						judge = false;
						str.append(" |signName 签约名 校验不一致 ");
					}
					if(!validInfo.getString("manualSettle").equals(retList.getString("manualSettle"))){
						judge = false;
						str.append(" |manualSettle 是否自助结算参数 校验不一致 ");
					}
					if(judge){
						log.info("----易宝校验逻辑--易宝商户号为"+customerNumber+"  的校验结果一致");
						resInfo.put("code", GlobalConstant.RESP_CODE_SUCCESS);
						resInfo.put("return_msg",GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
						
						//保存校验通过的信息
						log.info("----易宝校验逻辑--易宝商户号为"+customerNumber+"  保存校验结果一致的状态--开始");
						Tchannel c = channelDao.get("select t from Tchannel t  where t.status=10 and t.name='YIBAOZHITONGCHE' and t.userId="+ user_id +"");
						JSONObject merConfig = JSONObject.parseObject(c.getConfig());
						merConfig.put("check_info", "pass");
						c.setConfig(merConfig.toJSONString());
						channelDao.save(c);
						log.info("----易宝校验逻辑--易宝商户号为"+customerNumber+" 保存校验结果一致的状态--成功 ");
						
						return resInfo;
					}else{
						log.info("易宝校验逻辑--易宝商户号为"+customerNumber+"  的校验结果为"+str.toString());
						resInfo.put("code", GlobalConstant.RESP_CODE_999);
						resInfo.put("return_msg","参数校验失败");
						return resInfo;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resInfo;
	}

	/**
	 * 子商户审核接口
	 */
	@Override
	public JSONObject auditMerchant(Long user_id,String mainCustomerNumber, String customerNumber, String status, String reason) {
		JSONObject resInfo = new JSONObject();
		
		try {
			String behavior = "auditMerchant";	//子商户审核
			StringBuilder signature = new StringBuilder();
			signature
					.append(customerNumber == null ? "" : customerNumber)
					.append(mainCustomerNumber == null ? "" : mainCustomerNumber)
			        .append(status == null ? "" : status)
			        .append(reason == null ? "" : reason);
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			
			Part[] parts = new AuditMerchantPartsBuilder()
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setCustomerNumber(customerNumber).setStatus(status)
			        .setReason(reason)
			        .setHmac(hmac).generateParams();
			JSONObject reqJson = new JSONObject();
			reqJson.put("mainCustomerNumber", mainCustomerNumber);
			reqJson.put("customerNumber", customerNumber);
			reqJson.put("status", status);
			reqJson.put("reason", reason);
			
			//调用公用请求模块
			log.info("易宝---调用审核接口--商户号为"+customerNumber +" 请求内容为"+reqJson.toJSONString());
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
			log.info("易宝---调用审核接口--商户号为"+customerNumber +" 返回内容为"+result.toJSONString());
			if(result == null){
				resInfo.put("code", GlobalConstant.RESP_CODE_999);
				resInfo.put("return_msg", "连接通道失败");
				return resInfo;
			}else{
				if(!result.getString("code").equals("0000") && !result.getString("message").equals("商户已经审核过,无需再审!")){
					resInfo.put("code", GlobalConstant.RESP_CODE_999);
					resInfo.put("return_msg", result.getString("message"));
					return resInfo;
				}else{
					
					//保存审核通过的信息
					log.info("----易宝校验逻辑--易宝商户号为"+customerNumber+"  保存审核通过的状态--开始");
					Tchannel c = channelDao.get("select t from Tchannel t  where t.status=10 and t.name='YIBAOZHITONGCHE' and t.userId="+ user_id +"");
					JSONObject merConfig = JSONObject.parseObject(c.getConfig());
					merConfig.put("audit_status", "SUCCESS");
					c.setConfig(merConfig.toJSONString());
					channelDao.save(c);
					log.info("----易宝校验逻辑--易宝商户号为"+customerNumber+" 保存审核通过的状态--成功 ");
					
					resInfo.put("code", GlobalConstant.RESP_CODE_SUCCESS);
					resInfo.put("return_msg",GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					return resInfo;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resInfo;
	}
	
	/**
	 * 通过用户Id，创建子商户
	 */
	@Override
	public JSONObject createSubMerchantByUserId(Long userId){
		JSONObject resultMap = new JSONObject();
		try {
			//检验是否重复申请  状态为10才是直通车
			Tchannel c = channelDao.get("select t from Tchannel t  where t.status=10 and t.name='YIBAOZHITONGCHE' and t.userId="+ userId +"");
			if(c!=null){
				resultMap.put("return_code", GlobalConstant.RESP_CODE_999);
				resultMap.put("return_msg", "商户重复报备");
				return resultMap;
			}
			
			//获取请求参数中的值
			User  user= userService.get(userId);
			//查询结算卡
			TuserCard cardJ = userCardDao.get("select t from TuserCard t left join t.bank left join t.user u  where u.id=" + user.getId() +" and t.isSettlmentCard = 1 ");
			if(cardJ==null){
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG", "未设置结算卡");
				return resultMap;
			}
			String behavior = "register";	//子商户注册
			String mainCustomerNumber = YiBaoBaseUtil.customerNumber;	//代理商编号
			String requestId= "YB_" + DateUtil.convertCurrentDateTimeToString()
							+StringUtils.leftPad(String.valueOf(RandomUtils.nextInt(1000)), 3, "0");	//注册请求号，保持唯一  最大20位
			
			String customerType = "PERSON";	//商户类型	ENTERPRIST 企业 INDIVIDUAL  个体工商户   PERSON 个人
			String bindMobile = user.getLoginName();	//商户注册手机号
			String signedName = user.getRealName();	//签约名
			String linkMan = "芦强";	//推荐人姓名
			
			String idCard = user.getIdNo();	//身份证号
			String legalPerson = user.getRealName();	//商户法人姓名
			String minSettleAmount = "1";	//起始金额	必须大于0
			String riskReserveDay = "0";	//结算周期   0：T1 T0自助结算   1:T1自助结算
			String bankAccountType = "PrivateCash";	//银行卡类型  PrivateCash 对私  PublicCash 对公   为空则默认对私
			String bankAccountNumber = cardJ.getCardNo();	//银行卡号
			
			//银行卡开户行单独查询
			
			
			String bankName ="";	//银行卡开户行
			TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(cardJ.getCardNo()));
			if(cbc==null){
				resultMap.put("return_code", GlobalConstant.RESP_CODE_053);
				resultMap.put("return_msg", GlobalConstant.map.get(GlobalConstant.RESP_CODE_053));
				return resultMap;
			}else{
				Bank bank = bankService.getBankByBankCode(cbc.getBankCode());
				if(bank == null){
					resultMap.put("return_code", GlobalConstant.RESP_CODE_054);
					resultMap.put("return_msg", GlobalConstant.map.get(GlobalConstant.RESP_CODE_054));
					return resultMap;
				}else{
					bankName = bank.getBankName();
				}
			}
			
			
			
			String accountName = user.getRealName();	//银行卡开户名
			String areaCode = "2900";	//地区码 
			String manualSettle = "Y"; //是否自助结算   N：隔天自动打款   Y：自助结算
			
			//照片的绝对路径要事先准备好  "idcard_front":"C:/shenfuimage/idcard_front.png",
			File bankCardPhoto = new File("C:\\yibaoimage\\bankcard_front.png");  //银行卡正面照
			File idCardPhoto = new File("C:\\yibaoimage\\idcard_front.png");	//身份证正面照
			File idCardBackPhoto = new File("C:\\yibaoimage\\idcard_back.png");
			File personPhoto = new File("C:\\yibaoimage\\idcard_hold.png");
			//报备子商户接口中可选的参数(用于生成签名)
			String loginPassword = "";
			String tradePassword = "";
			String businessLicence = "";
			String splitter = "";
			String splitterprofitfee = "";
			String whiteList = "";
			String freezeDays = "";
			
			StringBuilder signature = new StringBuilder();
			signature
					 .append(mainCustomerNumber == null ? "": mainCustomerNumber)
			         .append(loginPassword == null ? "" : loginPassword)
			         .append(tradePassword == null ? "" : tradePassword)
			         .append(requestId == null ? "" : requestId)
			         .append(customerType == null ? "" : customerType)
			         .append(businessLicence == null ? "" : businessLicence)
			         .append(bindMobile == null ? "" : bindMobile)
			         .append(signedName == null ? "" : signedName)
			         .append(linkMan == null ? "" : linkMan)
			         .append(idCard == null ? "" : idCard)
			         .append(legalPerson == null ? "" : legalPerson)
			         .append(minSettleAmount == null ? "" : minSettleAmount)
			         .append(riskReserveDay == null ? "" : riskReserveDay)
//			         .append(bankAccountType == null ? "" : bankAccountType)
			         .append(bankAccountNumber == null ? "" : bankAccountNumber)
			         .append(bankName == null ? "" : bankName)
			         .append(accountName == null ? "" : accountName)
			         .append(areaCode == null ? "" : areaCode)
			         .append(splitter == null ? "" : splitter)
			         .append(splitterprofitfee == null ? "" : splitterprofitfee)
			         .append(whiteList == null ? "" : whiteList)
			         .append(freezeDays == null ? "" : freezeDays)
			         .append(manualSettle == null ? "" : manualSettle);
			
			//生成用于校验的注册信息
//			JSONObject reqPars = JSONObject.parseObject(signature.toString());
			
			//生成签名
			String hmac = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			//生成Part
			Part[] parts = new RegisterPartsBuilder()
			        .setMainCustomerNumber(mainCustomerNumber)
			        .setRequestId(requestId)
			        .setCustomerType(customerType)
			        .setBindMobile(bindMobile)
			        .setSignedName(signedName)
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
			log.info("----银联积分易宝--调用申请开通子商户接口---请求参数集合（不包括照片）为:"+signature);
			JSONObject  result = YiBaoBaseUtil.registerRequesterByPart(behavior, parts);
			log.info("----银联积分易宝--调用申请开通子商户接口---返回结果:"+result.toJSONString());
			//假定已经申请成功
//			result.put("code", "0000");
//			result.put("customerNumber", "10017116654");
//			result.put("message", "注册成功");
			
			//判断调用开通子商户接口的返回结果
			if(result==null){
				resultMap.put("return_code", GlobalConstant.RESP_CODE_999);
				resultMap.put("return_msg", "请求通道报备商户时提供方异常");
				return resultMap;
			}else{
				if(StringUtil.isBlank(result.getString("code"))){
					resultMap.put("return_code", GlobalConstant.RESP_CODE_999);
					resultMap.put("return_msg", "请求通道报备商户时,返回信息异常");
					return resultMap;
				}				
				if(!result.getString("code").equals("0000")){
					//输出错误信息
					resultMap.put("return_code", GlobalConstant.RESP_CODE_999);
//					resultMap.put("return_msg", result.getString("message"));
					
					if(result.getString("message").equals("抱歉，只能使用指定的13家银行进行注册！")){
						resultMap.put("return_msg", "通道暂不支持该银行卡");
					}else{
						resultMap.put("return_msg", "请求通道失败");
					}
					return resultMap;
				}else{
					//返回成功
					log.info("----银联积分易宝--调用申请开通子商户接口---返回成功，描述信息为："+result.getString("message"));
					log.info("----银联积分易宝--调用申请开通子商户接口---返回成功，返回的商户编号为："+result.getString("customerNumber")+"对应本地用户ID为"+userId);
					String customerNumebr = result.getString("customerNumber");
					//创建直通车的通道记录
					
					JSONObject configJson = new JSONObject();
					configJson.put("yibao.appId", YiBaoBaseUtil.customerNumber);
					configJson.put("yibao.merchant_id", customerNumebr);
					configJson.put("yibao.notifyUrl", "http://101.200.34.95:26370/flypayfx/payment/yibao_ylzx_Notify");
					//创建易宝直通车记录
					if(!addCreateYiBaoZTCChannel(user.getRealName(), 551, configJson.toJSONString(), user.getId())){
						log.info("----银联积分易宝--调用申请开通子商户接口--保存商户信息失败,user_id="+user.getId());
						resultMap.put("return_code", GlobalConstant.RESP_CODE_999);
						resultMap.put("return_msg", result.getString("保存开通商户信息失败"));
						return resultMap;
					}
					resultMap.put("return_code", GlobalConstant.RESP_CODE_SUCCESS);
					resultMap.put("customerNumebr", customerNumebr);
					JSONObject validaInfo = new JSONObject();
						validaInfo.put("bindMobile", bindMobile);
						validaInfo.put("singnedName",signedName);
						validaInfo.put("linkMan",linkMan);
						validaInfo.put("idCard",idCard);
						validaInfo.put("legalPerson",legalPerson);
						validaInfo.put("minSettleAmount",minSettleAmount);
						validaInfo.put("riskReserveDay",riskReserveDay);
						validaInfo.put("bankAccountType",bankAccountType);
						validaInfo.put("bankAccountNumber",bankAccountNumber);
						validaInfo.put("bankName",bankName);
						validaInfo.put("accountName",accountName);
						validaInfo.put("areaCode",areaCode);
						validaInfo.put("manualSettle",manualSettle);
					resultMap.put("validaInfo", validaInfo);	//传递用于参数校验的信息
					return resultMap;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
	/**
	 * 开通易宝直通车
	 * @param detailName
	 * @param type	支付类型
	 * @param config  通道配置
	 * @param userId  通道对应的使用人
	 * @param merchantCode  子商户id
	 * @param merchantId
	 */
//	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public Boolean addCreateYiBaoZTCChannel(String detailName,Integer type,String config,Long userId){
		try {
			Tchannel t = new Tchannel();
			t.setVersion(Long.parseLong("0"));
			t.setType(type);
			t.setName("YIBAOZHITONGCHE");
			t.setRealRate(new BigDecimal(0.0026));
			t.setRealRate(new BigDecimal(0.0026));
			t.setShowRate(new BigDecimal(0.0049));
			t.setShareRate(new BigDecimal(0.0010));
			t.setMaxTradeAmt(new BigDecimal(100000.00));
			t.setMinTradeAmt(new BigDecimal(0.00));
			t.setStatus(10);
			t.setMaxChannelAmt(new BigDecimal(20000.00));
			t.setMinChannelAmt(new BigDecimal(0.00));
			t.setTodayAmt(new BigDecimal(0.00));
			t.setMaxAmtPerDay(new BigDecimal(200000.00));
			t.setAccount(YiBaoBaseUtil.customerNumber);	//易宝的代理商ID
			t.setConfig(config);
			t.setDetailName(detailName);
			t.setSeq(880);
			t.setUserType(700);
			t.setCommissionRate(new BigDecimal(0.00));
			t.setUserId(userId);
			t.setMerchantName("易宝银联积分直通车");
			t.setShortName("易宝银联积分直通车");
			t.setCreateDate(new Date());
			channelDao.save(t);
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
		
}
		
