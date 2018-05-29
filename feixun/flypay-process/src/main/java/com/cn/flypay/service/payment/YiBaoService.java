package com.cn.flypay.service.payment;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
/**
 * 易宝相关服务
 * @author liangchao
 *
 */
public interface YiBaoService {
	
	/**
	 * 更改易宝子商户绑定的结算卡
	 * @param mainCustomerNumber 代理商编号
	 * @param customerNumebr 子商户编号/分润方编号
	 * @param bankCardNumber 银行卡号
	 * @param bankName 开户行
	 * @return
	 */
	public JSONObject changeMerchantCard(String customerNumber,String bankCardNumber,String bankName);
	
	/**
	 * 子商户信息查询接口
	 * @param phone 电话号码
	 * @param customerNumber	子商户编码
	 * @return
	 */
	public JSONObject queryMerchantInfo(String phone , String customerNumber);
	

	/**
	 * 查询并校验子商户的信息
	 * @param user_id 用户的id
	 * @param mainCustomerNumber 代理商编号
	 * @param mobilePhone	注册手机号
	 * @param customerNumebr	子商户编号/分润方编号
	 * @param customerType  商户类型   不传默认为： CUSTOMER 子商户     SPLITTER 分润方 (已弃用)
	 * @param validInfo	用于校验的客户信息
	 * @return
	 */
	public JSONObject customerInfoForQuery(Long user_id,String mainCustomerNumber,String mobilePhone, String customerNumebr,String customerType,JSONObject validInfo); 


	/**
	 * 子商户审核接口
	 * @param mainCustomerNumber 代理商编号
	 * @param customerNumebr 子商户编号
	 * @param status 审核状态
 	 * @param reason 审核原因
	 * @return
	 */
	public JSONObject auditMerchant(Long user_id,String mainCustomerNumber,String customerNumebr,String status,String reason);


	/**
	 * 子商户费率查询接口
	 * @param user_id
	 * @param merId 易宝子商户ID
	 * @param productType 产品类型
	 *  		1,无卡支付  2，T1自助结算  3，T0自助结算基本  4，T0自助结算工作日额外  5,T0自助结算非工作日额外
	 * @return
	 */
	public JSONObject queryYiBaoFee(Long user_id,String merId,String productType);
	
	
	/**
	 * 子商户费率设置接口
	 * @param merId 易宝子商户ID
	 * @param productType 产品类型
	 * 			1,无卡支付  2，T1自助结算  3，T0自助结算基本  4，T0自助结算工作日额外  5,T0自助结算非工作日额外
	 * @param rate 费率  
 	 * 			参考文档
	 * @return
	 */
	public JSONObject setYiBaoFee(Long user_id,String merId,String productType,String rate);
	
	/**
	 * 保存易宝直通车子商户
	 * @param detailName
	 * @param type
	 * @param config
	 * @param userId
	 * @return
	 */
	public Boolean addCreateYiBaoZTCChannel(String detailName,Integer type,String config,Long userId);
	
	
	
	/**
	 * 通过用户Id，创建子商户
	 */
	public JSONObject createSubMerchantByUserId(Long userId);
	
	
	
}
