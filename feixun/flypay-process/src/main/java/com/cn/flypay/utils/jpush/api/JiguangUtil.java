package com.cn.flypay.utils.jpush.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiguangUtil {
	private static Logger logger = LoggerFactory.getLogger(JiguangUtil.class);
	public static final String ALTER_PARENT_POPULAR = "启禀小主，您推荐的%s成功注册成为%s用户，赶紧提示他完成实名认证吧！认证后就可以开启您的收益之旅喽！";
	public static final String ALTER_USER_BROKERAGER = "启禀小主，您推荐的%s为您赚得%s元佣金，现金已经存入您的账户！详见%s收益账户。";
	public static final String ALTER_USER_BROKERAGERTWO = "启禀小主，您推荐的%s为您赚得%s元佣金，现金已经存入您的账户！详见钱包页面。";

	public static final String ALTER_UNAGENT_USER_BROKERAGER_TITLE = "未获得佣金提醒";
	//public static final String ALTER_UNAGENT_USER_BROKERAGER = "启禀小主，由您发展的%s级用户%s升级为%s用户了，但因您目前未进行升级，所以失去了获取返佣的机会哦！";
	//宝贝钱袋2.0升级启用新的推送消息
	public static final String ALTER_UNAGENT_USER_BROKERAGER = "启禀小主，通过您邀请的%s升级为代理商了，但是由于目前您尚未升级，所以很遗憾您失去了200元、166元、66元等高额返佣的机会！";

}