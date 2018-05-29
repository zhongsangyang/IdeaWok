package com.cn.flypay.service.account;

import java.util.List;

import com.cn.flypay.model.account.TaccountPoint;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.account.AccountPoint;
import com.cn.flypay.pageModel.base.PageFilter;

public interface AccountPointService {

	public List<AccountPoint> dataGrid(AccountPoint point, PageFilter ph);

	public Long count(AccountPoint point, PageFilter ph);

	public AccountPoint getAccountPointByUserId(Long userId);

	public void updatePointByUserId(Long userId, String cdType, Long addPoint, String desc, String pointType) throws Exception;

	public void updatePoint(Long userId, String pointType, String desc) throws Exception;

	/**
	 * 
	 * @param agentId
	 * @param userId
	 * @param channelType
	 * @param accountType
	 *            入账类型 1 表示T1入账 0表示D0入账
	 * @param type
	 *            1 高费率降至中费率 2 中费率降至低费率 3 高费率降至低费率
	 * @param realPoint
	 * @param desc
	 */
	public String updateUserInputRateByConsumePoint(String agentId, Long userId, Integer channelType, Integer accountType, Integer type, Integer realPoint);

	public String consumePointChlRateZTH(String agentId, Long userId, Integer channelType, Integer accountType, Integer type, Integer realPoint);
	
	public TaccountPoint freeze(Long id);

	public void initAccountPoint(Tuser user);

	/**
	 * 更新积分 数据以及下限认证人数
	 * 
	 * @param accountPoint
	 */
	public void update(AccountPoint accountPoint);

}
