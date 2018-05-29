package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.model.sys.TYiQiangMerchantReport;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserMerchantReport;
import com.cn.flypay.model.sys.TweiLianBaoMerchantReport;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;

public interface UserMerchantConfigService {

	public List<UserMerchantConfig> dataGrid(UserMerchantConfig param, PageFilter ph);

	public Long count(UserMerchantConfig param, PageFilter ph);

	public void edit(UserMerchantConfig param);

	public UserMerchantConfig get(Long id);

	/**
	 * 为商家创建商户在行方的配置信息
	 * 
	 * @param user
	 */
	public void createUserMerchants(Tuser user);

	/**
	 * 为商户报备申孚D0直通车商户
	 *
	 * @param user
	 */
	public TuserMerchantReport createShenFuUserMerchants(Tuser user);
	
	/**
	 * 为商户报备申孚D0直通车商户
	 *
	 * @param user
	 */
	public TweiLianBaoMerchantReport createWeiLianBaoUserMerchants(Tuser user);


	/**
	 * 为商户报备亿强D0直通车商户
	 *
	 * @param user
	 */
	public TYiQiangMerchantReport createYiQiangUserMerchants(Tuser user);
}
