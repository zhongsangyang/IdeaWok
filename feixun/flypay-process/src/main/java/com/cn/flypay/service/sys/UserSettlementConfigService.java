package com.cn.flypay.service.sys;

import java.math.BigDecimal;
import java.util.List;

import com.cn.flypay.model.sys.TuserSettlementConfig;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;

public interface UserSettlementConfigService {

	public List<UserSettlementConfig> dataGrid(UserSettlementConfig param, PageFilter ph);

	public Long count(UserSettlementConfig param, PageFilter ph);

	public void edit(UserSettlementConfig param);

	public UserSettlementConfig get(Long id);

	public TuserSettlementConfig getTuserSettlementConfigByUserId(Long userId);

	public UserSettlementConfig getByUserId(Long userId);

	/**
	 * 获取用户的订单费率
	 * 
	 * @param orderType
	 * @param userId
	 * @param inputAccType
	 * @return
	 */
	public BigDecimal getUserInputRate(Integer orderType, Long userId, Integer inputAccType);

	/**
	 * 是否满足降低通道费率
	 * 
	 * @param userId
	 *            用户ID
	 * @param agentId
	 *            代理商ID
	 * @param payType
	 *            支付类型
	 * @param accountType
	 *            入账类型
	 * @param exchangeType
	 *            兑换类型 1 高费率降至中费率 2 中费率降至低费率 3 高费率降至低费率
	 * @param point
	 *            消耗积分数
	 * @return
	 */
	public String isAllowReduceInputRateWhenPoint(Long userId, String agentId, Integer payType, Integer accountType, Integer exchangeType, Integer point);

	/**
	 * 批量更新用户的T0最大提现金额
	 * 
	 * @param maxT0Amt
	 */
	public void updateAllUserMaxT0Amt(BigDecimal maxT0Amt);

	/**
	 * /** 根据用户选择的支付方式以及入账类型确定用户分润的比例
	 * 
	 * @see 用户所属运营商若不支持升级降费率，那么分润比例为用户配置信息中的比例
	 * @see 用户所属运营商支持升级降费率，那么分润比例应为用户当前比例-钻石用户比例
	 * 
	 * @param userId
	 *            用户ID
	 * @param channelType
	 *            支付类型
	 * @param accountType
	 *            入账类型
	 * @return
	 */
	public BigDecimal getUserShareRate(Long userId, Integer channelType, Integer accountType);

	/**
	 * 根据用户的输入的类型确定 用户的手续费以及分佣比例
	 * 
	 * @param userId
	 * @param orderType
	 * @param accountType
	 *           入账类型： 0D0小额 1 T1大额 5 T5 10D0小额 11 T1大额
	 * @return
	 */
	BigDecimal[] getUserInputRateAndShareRate(Long userId, Integer orderType, Integer accountType);

	/**
	 * 读取升级降费率的配置信息，存放至用户费率表中
	 * @param tuscs
	 * @param userType
	 * @param orgId
	 */
	public void setSettlementConfigWhenUserUpdate(TuserSettlementConfig tuscs, Integer userType, Long orgId);

}
