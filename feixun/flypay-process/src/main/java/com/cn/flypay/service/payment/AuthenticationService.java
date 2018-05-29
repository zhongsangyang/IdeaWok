package com.cn.flypay.service.payment;

import java.util.List;
import java.util.Set;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.AuthenticationLog;

/**
 * 实名认证
 * 
 * @author sunyue
 * 
 */
public interface AuthenticationService {

	/**
	 * 用户四要素认证
	 * 
	 * @param authLog
	 * @return
	 */
	AuthenticationLog sendInfoToAuthentication(AuthenticationLog authLog) throws Exception;
	
	
	/**
	 * 用户四要素认证
	 * 
	 * @param authLog
	 * @return
	 */
	AuthenticationLog sendInfoToAuthen(AuthenticationLog authLog) throws Exception;

	AuthenticationLog saveAuthentication(AuthenticationLog authLog);

	String findAuthErroInfo(Long merId, Set<Integer> authTypes);

	List<AuthenticationLog> dataGrid(AuthenticationLog authLog, PageFilter ph);

	Long count(AuthenticationLog authLog, PageFilter ph);

	/**
	 * 验证用户是否运行实名认证
	 * 
	 * @param userId
	 * @param idNo
	 * @return
	 */
	Boolean isAllowAuthentication(Long userId, String idNo);
	
	Boolean isIdon(String agentId, String idNo);
}
