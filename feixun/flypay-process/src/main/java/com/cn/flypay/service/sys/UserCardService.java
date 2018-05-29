package com.cn.flypay.service.sys;

import java.util.List;
import java.util.Map;

import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.UserCard;

public interface UserCardService {

	public List<UserCard> dataGrid(SessionInfo sessionInfo, UserCard card, PageFilter ph);

	public Long count(UserCard card, PageFilter ph);

	public UserCard get(Long id);
	
	/**
	 * 检查用户的卡表中是否存在结算卡
	 * 
	 * @param userId
	 * @return true 存在结算卡   flase 不存在结算卡
	 */
	public boolean checkExistSettlementCard(Long userId);

	/**
	 * 根据用户ID以及卡类型，查询出用户的卡
	 * 
	 * @param userId
	 * @param cardType
	 * @return 若cardType为null， 表示所有的卡信息
	 */
	public List<UserCard> findCarsByUserId(Long userId, String cardType);

	public UserCard getSettlementCarsByUserId(Long userId);

	public TuserCard getTUserCarByUserId(Long userId);

	/**
	 * 验证卡信息 实名认证用户
	 * 
	 * @param cardInfos
	 * @param isSettmentCard
	 *            是否结算卡更新
	 * @param isAuthUser
	 *            是否实名认证用户
	 * @return
	 */
	public Map<String, String> sendCardInfoToBankValidate(Map<String, String> cardInfos, Boolean isSettmentCard,
			Boolean isAuthUser, String agentId) throws Exception;
	
	
	public Map<String, String> sendCardInfoToBankValidateTwo(Map<String, String> cardInfos, Boolean isSettmentCard,
			Boolean isAuthUser, String agentId) throws Exception;

	/**
	 * 更新用户的结算卡
	 * 
	 * @param merId
	 * @param cardId
	 * @return
	 */
	public String updateSettlementCard(Long merId, Long cardId);
	
	/**
	 * 更新信用卡开通易联快捷支付状态
	 * @param merId
	 * @param isOpenYilianQuickPay
	 */
	public void updateIsOpenYilianQuickPay(Long cardId,String isOpenYilianQuickPay);
	
	/**
	 * 更新卡开通状态--嘎吱银联侧绑卡开通状态
	 */
	public void updateIsOpenGaZhiYinLianQuickPay(Long cardId,String isOpenGaZhiYinLianQuickPay);
	
	
	/**
	 * 根据嘎吱（银联）侧绑卡开通时的订单号来获取卡信息
	 * @param isOpenGaZhiYinLianQuickPay
	 * @return
	 */
	public UserCard getUserCardByIsOpenGaZhiYinLianQuickPay(String isOpenGaZhiYinLianQuickPay);
	
	/**
	 * 删除用户的银行卡
	 * 
	 * @param merId
	 * @param cardId
	 * @return
	 */
	public String deleteCard(Long merId, Long cardId);
	
	
	public String getfindCode(String userId,String crdeNo);


}
