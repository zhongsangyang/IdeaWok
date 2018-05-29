package com.cn.flypay.service.sys.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jiguang.commom.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.model.sys.TinfoList;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.sys.JiguangPushService;
import com.cn.flypay.service.sys.OrgSysConfigService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.jpush.api.JPushClient;
import com.cn.flypay.utils.jpush.api.push.PushResult;
import com.cn.flypay.utils.jpush.api.push.model.Message;
import com.cn.flypay.utils.jpush.api.push.model.Options;
import com.cn.flypay.utils.jpush.api.push.model.Platform;
import com.cn.flypay.utils.jpush.api.push.model.PushPayload;
import com.cn.flypay.utils.jpush.api.push.model.audience.Audience;
import com.cn.flypay.utils.jpush.api.push.model.notification.AndroidNotification;
import com.cn.flypay.utils.jpush.api.push.model.notification.IosNotification;
import com.cn.flypay.utils.jpush.api.push.model.notification.Notification;

@Service
public class JiguangPushServiceImpl implements JiguangPushService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private OrgSysConfigService orgSysConfigService;
	@Autowired
	private UserService userService;

	private String[] getKeyAndSecret(String agentId) {
		String[] ks = new String[2];
		JSONObject config = orgSysConfigService.getJiGuangConfigJSONObject(agentId);
		if (config != null) {
			ks[0] = config.getString("appKey");
			ks[1] = config.getString("masterSecret");
		} else {
			ks[0] = (String) ApplicatonStaticUtil.getAppStaticData("jiguang.appKey");
			ks[1] = (String) ApplicatonStaticUtil.getAppStaticData("jiguang.masterSecret");
		}
		return ks;
	}

	@Override
	public String sendMsgInfo(InfoList infoList, String agentId) {
		ClientConfig clientConfig = ClientConfig.getInstance();
		String[] confgs = getKeyAndSecret(agentId);
		JPushClient jpushClient = new JPushClient(confgs[1], confgs[0], null, clientConfig);
		Map<String, String> map = new HashMap<String, String>();
		map.put("title", infoList.getTitle());
		map.put("content", infoList.getContent());
		map.put("createTime", DateUtil.getDateTime("yyyyMMddHHmmss", infoList.getCreateTime()));

		PushPayload payload = PushPayload
				.newBuilder()
				.setPlatform(Platform.android_ios())
				.setAudience(Audience.all())
				.setNotification(
						Notification.newBuilder().setAlert(infoList.getContent()).addPlatformNotification(AndroidNotification.newBuilder().setTitle(infoList.getTitle()).addExtras(map).build())
								.addPlatformNotification(IosNotification.newBuilder().incrBadge(1).addExtras(map).build()).build()).setOptions(Options.newBuilder().setApnsProduction(true).build())
				.build();

		try {
			PushResult result = jpushClient.sendPush(payload);
			infoList.setMsgId(result.msg_id);
			infoList.setSendNo(new Long(result.sendno));
			logger.info("Got result - " + result);
		} catch (APIConnectionException e) {
			infoList.setStatus(InfoList.info_status.release_failure.getCode());
			logger.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			logger.error("Error response from JPush server. Should review and fix it. ", e);
			logger.info("HTTP Status: " + e.getStatus());
			logger.info("Error Code: " + e.getErrorCode());
			logger.info("Error Message: " + e.getErrorMessage());
			logger.info("Msg ID: " + e.getMsgId());
			infoList.setStatus(InfoList.info_status.release_failure.getCode());
		}
		return null;
	}

	@Override
	public String sendMsgInfoToPerson(TinfoList infoList) {

		ClientConfig clientConfig = ClientConfig.getInstance();
		Long userId = infoList.getUser().getId();
		User user = userService.get(userId);
		String[] confgs = getKeyAndSecret(user.getAgentId());
		JPushClient jpushClient = new JPushClient(confgs[1], confgs[0], null, clientConfig);

		Map<String, String> map = new HashMap<String, String>();
		map.put("title", infoList.getTitle());
		map.put("content", infoList.getContent());
		map.put("createTime", DateUtil.getDateTime("yyyyMMddHHmmss", infoList.getCreateTime()));

		PushPayload payload = PushPayload
				.newBuilder()
				.setPlatform(Platform.android_ios())
				.setAudience(Audience.tag(userId.toString()))
				.setNotification(
						Notification.newBuilder().setAlert(infoList.getContent()).addPlatformNotification(AndroidNotification.newBuilder().setTitle(infoList.getTitle()).addExtras(map).build())
								.addPlatformNotification(IosNotification.newBuilder().incrBadge(1).addExtras(map).build()).build()).setOptions(Options.newBuilder().setApnsProduction(true).build())
				.build();

		try {
			PushResult result = jpushClient.sendPush(payload);
			infoList.setStatus(InfoList.info_status.release_success.getCode());
			infoList.setMsgId(result.msg_id);
			infoList.setSendNo(new Long(result.sendno));
			logger.info("Got result - " + result);
		} catch (APIConnectionException e) {
			logger.error("Connection error. Should retry later. ", e);
			infoList.setStatus(InfoList.info_status.release_failure.getCode());
		} catch (APIRequestException e) {
			logger.error("Error response from JPush server. Should review and fix it. ", e);
			logger.info("HTTP Status: " + e.getStatus());
			logger.info("Error Code: " + e.getErrorCode());
			logger.info("Error Message: " + e.getErrorMessage());
			logger.info("Msg ID: " + e.getMsgId());
			infoList.setStatus(InfoList.info_status.release_failure.getCode());
		}

		return null;
	}

	@Override
	public String sendMsgInfoToPerson(InfoList infoList) {

		ClientConfig clientConfig = ClientConfig.getInstance();
		Long userId = infoList.getUserId();
		Tuser user = userService.getTuser(userId);
		String[] confgs = getKeyAndSecret(user.getAgentId());
		JPushClient jpushClient = new JPushClient(confgs[1], confgs[0], null, clientConfig);

		Map<String, String> map = new HashMap<String, String>();
		map.put("title", infoList.getTitle());
		map.put("content", infoList.getContent());
		map.put("createTime", DateUtil.getDateTime("yyyyMMddHHmmss", infoList.getCreateTime()));

		PushPayload payload = PushPayload
				.newBuilder()
				.setPlatform(Platform.android_ios())
				.setAudience(Audience.tag(infoList.getUserId().toString()))
				.setNotification(
						Notification.newBuilder().setAlert(infoList.getContent()).addPlatformNotification(AndroidNotification.newBuilder().setTitle(infoList.getTitle()).addExtras(map).build())
								.addPlatformNotification(IosNotification.newBuilder().incrBadge(1).addExtras(map).build()).build()).setOptions(Options.newBuilder().setApnsProduction(true).build())
				.build();

		try {
			PushResult result = jpushClient.sendPush(payload);
			infoList.setStatus(InfoList.info_status.release_success.getCode());
			infoList.setMsgId(result.msg_id);
			infoList.setSendNo(new Long(result.sendno));
			logger.info("Got result - " + result);
		} catch (APIConnectionException e) {
			logger.error("Connection error. Should retry later. ", e);
			infoList.setStatus(InfoList.info_status.release_failure.getCode());
		} catch (APIRequestException e) {
			logger.error("Error response from JPush server. Should review and fix it. ", e);
			logger.info("HTTP Status: " + e.getStatus());
			logger.info("Error Code: " + e.getErrorCode());
			logger.info("Error Message: " + e.getErrorMessage());
			logger.info("Msg ID: " + e.getMsgId());
			infoList.setStatus(InfoList.info_status.release_failure.getCode());
		}
		return null;
	}

	@Override
	public String sendMsgSoundInfoToPerson(Long userId) {
		ClientConfig clientConfig = ClientConfig.getInstance();
		User user = userService.get(userId);
		String[] confgs = getKeyAndSecret(user.getAgentId());
		JPushClient jpushClient = new JPushClient(confgs[1], confgs[0], null, clientConfig);
		Message message = Message.newBuilder().setMsgContent("收款成功").addExtra("sound", "dingdong.caf").build();
		PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.android_ios()).setAudience(Audience.tag(userId.toString())).setMessage(message)
				.setOptions(Options.newBuilder().setApnsProduction(true).build()).build();
		try {
			PushResult result = jpushClient.sendPush(payload);
			logger.info("Got result - " + result);
		} catch (APIConnectionException e) {
			logger.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			logger.error("Error response from JPush server. Should review and fix it. ", e);
			logger.info("HTTP Status: " + e.getStatus());
			logger.info("Error Code: " + e.getErrorCode());
			logger.info("Error Message: " + e.getErrorMessage());
			logger.info("Msg ID: " + e.getMsgId());
		}
		return null;
	}
}
