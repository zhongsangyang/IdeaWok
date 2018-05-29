package com.cn.flypay.service.common.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.TuserToken;
import com.cn.flypay.pageModel.account.UserToken;
import com.cn.flypay.service.common.UserTokenService;
import com.cn.flypay.utils.DateUtil;

@Service
public class UserTokenServiceImpl implements UserTokenService {

	@Autowired
	private BaseDao<TuserToken> tokenDao;

	@Value("${token_expires_in_second}")
	public Long token_expires_in_second;

	@Override
	public UserToken getUserTokenByToken(String token) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", token);
		TuserToken t = tokenDao.get("from TuserToken where token=:token", params);
		if (t != null) {
			UserToken u = new UserToken();
			BeanUtils.copyProperties(t, u);
			return u;
		}
		return null;
	}

	@Override
	public UserToken getUserTokenByUserId(Long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		TuserToken t = tokenDao.get("from TuserToken where userId=:userId", params);
		if (t != null) {
			UserToken u = new UserToken();
			BeanUtils.copyProperties(t, u);
			return u;
		}
		return null;
	}

	@Override
	public String updateUserTokenWhenLogin(Long userId, String token) {
		TuserToken t = tokenDao.get("select t from TuserToken t where t.userId=" + userId);
		if (t != null) {
			t.setToken(token);
			t.setUpdateTime(new Date());
			tokenDao.update(t);
			return token;
		} else {
			return saveUserToken(token, userId, token_expires_in_second);
		}
	}

	private String saveUserToken(String token, Long userId, Long expiresIn) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		TuserToken t = new TuserToken();
		t.setUserId(userId);
		t.setToken(token);
		t.setExpiresIn(expiresIn);
		t.setCreateTime(new Date());
		t.setUpdateTime(new Date());
		tokenDao.save(t);
		return token;
	}

	@Override
	public boolean isLegalUserToken(long userId, String token) {
		TuserToken t = tokenDao.get("select t from TuserToken t where t.userId=" + userId + " and t.token='"
				+ token.trim() + "'");
		if (t != null) {
			if (DateUtil.getBetweenMins(t.getUpdateTime(), new Date()) * 60 > t.getExpiresIn()) {
				return false;
			}
			return true;
		}
		return false;
	}
}
