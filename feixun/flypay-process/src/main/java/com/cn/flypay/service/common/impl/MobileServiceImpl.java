package com.cn.flypay.service.common.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cn.flypay.service.common.MobileService;

@Service
public class MobileServiceImpl implements MobileService {
	/**
	 * 服务商编号
	 */
	@Value("${agent_id}")
	private String agent_id;
	/**
	 * android 版本号
	 */
	@Value("${current_app_android_version_Id}")
	private String current_app_android_version_Id;
	/**
	 * ios 版本号
	 */
	@Value("${current_app_ios_version_Id}")
	private String current_app_ios_version_Id;
	/**
	 * android 版本号
	 */
	@Value("${current_app_android_need_update}")
	private String current_app_android_need_update;
	/**
	 * ios 版本号
	 */
	@Value("${current_app_ios_need_update}")
	private String current_app_ios_need_update;
	/**
	 * android 版本号
	 */
	@Value("${current_app_android_url}")
	private String current_app_android_url;
	/**
	 * ios 版本号
	 */
	@Value("${current_app_ios_url}")
	private String current_app_ios_url;

	@Override
	public String getAgentId() {
		return agent_id;
	}

	@Override
	public Map<String, String> getAppVersion() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("current_app_android_version_Id", current_app_android_version_Id);
		map.put("current_app_ios_version_Id", current_app_ios_version_Id);
		map.put("current_app_android_url", current_app_android_url);
		map.put("current_app_ios_url", current_app_ios_url);
		map.put("current_app_android_need_update", current_app_android_need_update);
		map.put("current_app_ios_need_update", current_app_ios_need_update);
		return map;
	}
}
