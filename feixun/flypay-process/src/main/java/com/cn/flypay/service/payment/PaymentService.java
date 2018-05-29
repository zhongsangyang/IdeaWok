package com.cn.flypay.service.payment;

import java.util.Map;

/**
 * Created by zhoujifeng1 on 16/8/1.
 */
public interface PaymentService {

	/**
	 * 用户转账
	 * 
	 * @param fromUserId
	 * @param toUserId
	 * @param amt
	 * @param desc
	 * @return
	 */
	public String updateAccountWhenTransferAccount(Long fromUserId, Long toUserId, Double amt, String desc);

	/**
	 * 用户提现
	 * 
	 * @param userId
	 * @param liqType
	 *            提现类型
	 * @param parseDouble
	 * @param trfTitle
	 * @return
	 * @throws Exception
	 */
	public String updateAccountWhenLiqAccount(Long userId, String liqType, Double amt, String desc) throws Exception;

	public Map<String, String> updateAccountWhenLiqAccountMap(Long userId, String liqType, Double amt, String desc)
			throws Exception;

	public String updateBrokerageAccountWhenLiqBrokerage(Long userId, Double amt, String trfTitle, String type);

	public Map<String, String> updateBrokerageAccountWhenLiqBrokerageMap(Long userId, Double amt, String trfTitle,
			String type);

	String updateBrokerageAccountWhenLiqBrokerageTwo(Long userId, Double amt, String trfTitle, String type);

	String updateAccountWhenPayAgent(Long userId, Double amt);

}
