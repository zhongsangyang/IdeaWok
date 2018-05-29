package com.cn.flypay.pageModel.payment;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.service.payment.ChannelPaymentService;

/**
 * 通道支付关系
 * 
 * @author sunyue
 * 
 */
public class ChannelPayRef implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8105431913570179061L;
	/**
	 * 负责处理订单的支付service
	 */
	private ChannelPaymentService channelPaymentService;
	/**
	 * 通道
	 */
	private Channel channel;
	/**
	 * 通道账户配置信息
	 */
	private JSONObject config;

	/**
	 * 商家配置
	 */
	private UserMerchantConfig userMerchantConfig;

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public ChannelPaymentService getChannelPaymentService() {
		return channelPaymentService;
	}

	public void setChannelPaymentService(ChannelPaymentService channelPaymentService) {
		this.channelPaymentService = channelPaymentService;
	}

	public JSONObject getConfig() {
		return config;
	}

	public void setConfig(JSONObject config) {
		this.config = config;
	}

	public UserMerchantConfig getUserMerchantConfig() {
		return userMerchantConfig;
	}

	public void setUserMerchantConfig(UserMerchantConfig userMerchantConfig) {
		this.userMerchantConfig = userMerchantConfig;
	}

}
