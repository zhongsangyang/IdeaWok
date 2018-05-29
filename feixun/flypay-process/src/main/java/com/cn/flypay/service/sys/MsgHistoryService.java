package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.SysMsgHistory;

public interface MsgHistoryService {

	/**
	 * 根据短信编号验证 验证码
	 * 
	 * @param smsCode
	 *            验证码
	 * @param msgCode
	 *            短信编码
	 * @return
	 */
	Boolean validateSmsCode(String smsCode, String msgCode);

	/**
	 * 发送消息给用户手机
	 * 
	 * @param phone
	 * @param type
	 * @return
	 * @throws Exception
	 */
	SysMsgHistory sendSmsToUserPhone(String phone, String agentId, Integer type) throws Exception;

	List<SysMsgHistory> dataGrid(SysMsgHistory msgHistory, PageFilter ph);

	Long count(SysMsgHistory msgHistory, PageFilter ph);

}
