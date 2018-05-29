package com.cn.flypay.service.common;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

public interface CommonService {

	public JSONObject getRequstBody(HttpServletRequest request) throws Exception;

	public String getBodyFromRequst(HttpServletRequest request) throws Exception;

	public String getBodyFromRequst(HttpServletRequest request, String encoding) throws Exception;

	/**
	 * 用户订单号 类型(4)+yyyyMMddHHmmss(14)+3位随机数+userId(10) 28位
	 * 
	 * @param transType
	 * @param userId
	 * @return
	 */
	public String getUniqueOrderByType(String transType, Long userId);

	/**
	 * 用户订单号yyMMddHHmmss(12)+userId(6) +2位随机数 20位
	 * 
	 * @param transType
	 * @param userId
	 * @return
	 */
	public String getUniqueOrderByUserId(Long userId);

	/**
	 * 用户订单号yyMMddHHmmss(12)+8位随机数 20位
	 * 
	 * @param transType
	 * @param userId
	 * @return
	 */
	public String getUniqueTradeSn();

	JSONObject getEncryptBody(JSONObject json);


}
