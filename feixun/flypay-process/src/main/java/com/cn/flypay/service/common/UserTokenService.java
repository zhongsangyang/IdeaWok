package com.cn.flypay.service.common;

import com.cn.flypay.pageModel.account.UserToken;

public interface UserTokenService {

	public UserToken getUserTokenByToken(String token);

	UserToken getUserTokenByUserId(Long userId);

	String updateUserTokenWhenLogin(Long userId, String token);

	/**
	 * 根据用户ID 和token 查询token 并且判断token是否有效
	 * 
	 * @param parseLong
	 * @param token
	 * @return
	 */
	public boolean isLegalUserToken(long parseLong, String token);

}
