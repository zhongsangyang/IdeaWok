package com.cn.flypay.service.sys;

import java.util.List;
import java.util.Map;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.SysParameter;

public interface SysParamService {

	public List<SysParameter> dataGrid(SysParameter param, PageFilter ph);

	public Long count(SysParameter param, PageFilter ph);

	public Map<String, String> searchSysParameter();

	public void add(SysParameter param);

	public void delete(Long id);

	public void edit(SysParameter param);

	public SysParameter getByName(String name);

	public SysParameter get(Long id);

	/**
	 * 获取不同渠道获取佣金的比例
	 * 
	 * @return
	 */
	public Map<Long, Double> getChargeRateConfig();

	/**
	 * 钱包流量的处理数量，默认每次100
	 * 
	 * @return
	 */
	public Integer getMaxTaskNumber();
	
	
	/**
	 * 银联在线
	 * @return
	 */
	public boolean getD0YINLIAN();
	
	/**
	 * 直通车
	 * @return
	 */
	public boolean getThrough(String code);
	
	/**
	 * 提现开关
	 * @return
	 */
	public boolean getTiXian();
	
	
	/**
	 * 总开关接口
	 * @return
	 */
	public boolean getSwitch(String code);
	
	
	/**
	 * 总开关值
	 * @return
	 */
	public String getSwitchSUM(String code);

}
