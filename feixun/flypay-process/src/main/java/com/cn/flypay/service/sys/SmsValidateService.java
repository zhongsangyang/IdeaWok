package com.cn.flypay.service.sys;

public interface SmsValidateService {

	/**
	 * 发送短信验证码服务
	 * 
	 * @param phone
	 *            接收者的手机号
	 * @param templateId
	 *            发送模板
	 * @param code
	 *            验证码
	 * @return
	 */
	public String sendMsgValidate(String phone, String templateId, String code, String agentId);

}
