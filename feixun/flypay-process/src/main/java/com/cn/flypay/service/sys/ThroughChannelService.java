package com.cn.flypay.service.sys;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Channel;

public interface ThroughChannelService {

	public List<Channel> dataGrid(Channel channel, PageFilter pf);

	public Long count(Channel channel, PageFilter pf);

	public boolean addchannel(String merchantName, String shortName, String type, String address, String ProvinceCode,
			String CityCode, String DistrictCode) throws Exception;

	/**
	 * 开通平安大商户
	 * @param merchantName
	 * @param shortName
	 * @param type
	 * @param perType
	 * @param address
	 * @param ProvinceCode
	 * @param CityCode
	 * @param DistrictCode
	 * @return
	 * @throws Exception
	 */
	public boolean addPingAnchannel(String merchantName, String shortName, String type, String perType, String address,
			String ProvinceCode, String CityCode, String DistrictCode) throws Exception;
	
	/**
	 * 直清模式，开通大商户，用户平台收取升级费用
	 * @param merchantName
	 * @param shortName
	 * @param type  1，微信，绑定张瑜婷的卡  2，非微信，绑定卢强的卡 （暂时默认卢强）
	 * @return
	 */
	public JSONObject addPingAnPayZhiQing(String merchantName, String shortName, String type);

}
