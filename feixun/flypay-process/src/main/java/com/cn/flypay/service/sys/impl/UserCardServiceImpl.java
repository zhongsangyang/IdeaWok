package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.pageModel.account.AccountPoint;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.base.SessionInfo;
import com.cn.flypay.pageModel.sys.AuthenticationLog;
import com.cn.flypay.pageModel.sys.Role;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserCard;
import com.cn.flypay.service.account.AccountPointService;
import com.cn.flypay.service.payment.AuthenticationService;
import com.cn.flypay.service.sys.BankService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.StringUtil;

@Service
public class UserCardServiceImpl implements UserCardService {

	@Autowired
	private BaseDao<TuserCard> cardDao;
	@Autowired
	private BaseDao<Tuser> userDao;
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private BankService bankService;
	@Autowired
	private SysParamService sysParamService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private AccountPointService accountPointService;
	@Autowired
	private UserService userService;

	@Override
	public List<UserCard> findCarsByUserId(Long userId, String cardType) {
		List<UserCard> cards = new ArrayList<UserCard>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		String hql = "select t from TuserCard t left join t.user u left join t.bank b where t.status=0 and u.id=:uid";
		if (StringUtil.isNotBlank(cardType)) {
			params.put("cardType", cardType);
			hql += " and t.cardType=:cardType";
		}
		List<TuserCard> ts = cardDao.find(hql, params);
		for (TuserCard t : ts) {
			cards.add(new UserCard(t));
		}
		return cards;
	}

	@Override
	public UserCard getSettlementCarsByUserId(Long userId) {

		TuserCard t = getTUserCarByUserId(userId);
		if (t != null) {
			return new UserCard(t);
		}
		return null;
	}

	@Override
	public UserCard getUserCardByIsOpenGaZhiYinLianQuickPay(String isOpenGaZhiYinLianQuickPay) {
		String hql = "select t from TuserCard t where t.isOpenGaZhiYinLianQuickPay = " + isOpenGaZhiYinLianQuickPay;
		List<TuserCard> ts = cardDao.find(hql);
		for (TuserCard t : ts) {
			return new UserCard(t);
		}
		return null;
	};

	@Override
	public TuserCard getTUserCarByUserId(Long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", userId);
		List<TuserCard> ts = cardDao.find(
				"select t from TuserCard t left join t.user u where t.status=0 and u.id=:uid and t.cardType='J' and t.isSettlmentCard=1",
				params);
		for (TuserCard t : ts) {
			return t;
		}
		return null;
	}

	@Override
	public Map<String, String> sendCardInfoToBankValidate(Map<String, String> cardInfos, Boolean isSettmentCard,
			Boolean isAuthUser, String agentId) throws Exception {
		Map<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
		boolean isChangeUser = false;
		Long userId = Long.parseLong(cardInfos.get("merId"));
		Tuser user = userDao.get(Tuser.class, userId);
		Long maxAutoNum = Long.parseLong(sysParamService.searchSysParameter().get("authErrorNum"));
		Long maxAutoCardNum = Long.parseLong(sysParamService.searchSysParameter().get("authCardErrorNum"));
		if (isAuthUser && user.getAuthErrorNum() >= maxAutoNum) {
			returnMap.put("flag", GlobalConstant.RESP_CODE_056);
			return returnMap;
		}
		if (!isAuthUser && user.getAuthCardErrorNum() >= maxAutoCardNum) {
			returnMap.put("flag", GlobalConstant.RESP_CODE_058);
			return returnMap;
		}
		int type = 0;
		if ("ios".equals(cardInfos.get("appType"))) {
			type = 2;
		} else if ("android".equals(cardInfos.get("appType"))) {
			type = 1;
		}

		String idNo = user.getIdNo();
		if (cardInfos.containsKey("idNo") && StringUtil.isNotBlank(cardInfos.get("idNo"))) {
			idNo = cardInfos.get("idNo");
		}
		String realName = user.getRealName();
		if (cardInfos.containsKey("realName") && StringUtil.isNotBlank(cardInfos.get("realName"))) {
			realName = cardInfos.get("realName");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TuserCard t left join t.user u where 1=1 and u.agentId=:agentId";
		params.put("agentId", agentId);
		if (cardInfos.containsKey("cardId") && StringUtil.isNotBlank(cardInfos.get("cardId"))) {
			hql = hql + " and t.id=:cardId";
			params.put("cardId", Long.parseLong(cardInfos.get("cardId")));
		} else {
			hql = hql + " and u.id=:uid and t.cardNo=:cardNo";
			params.put("uid", userId);
			params.put("cardNo", cardInfos.get("cardNo"));
		}
		TuserCard ts = cardDao.get(hql, params);
		/* 若卡片存在，更新卡片的结算卡信息 */
		if (ts != null) {
			if (isSettmentCard) {

				/* 取消原始的结算卡 */
				List<TuserCard> oldts = cardDao.find(
						"select t from TuserCard t left join t.user u where t.isSettlmentCard=1 and u.id=" + userId);
				for (TuserCard tc : oldts) {
					tc.setIsSettlmentCard(0);
					cardDao.update(tc);
				}

				/* 设置新结算卡 */
				user.setSettlementStatus(1);
				if (ts.getCardType().equals("J")) {
					ts.setIsSettlmentCard(1);
					ts.setStatus(0);
					cardDao.update(ts);
				}
			} else if (ts.getStatus() != 0) {
				ts.setStatus(0);
				cardDao.update(ts);
			}
		} else {
			/* 若这张卡不存在，需要先进行实名认证 */
			AuthenticationLog authLog = new AuthenticationLog(userId, 0, type, idNo, realName,
					cardInfos.get("reservedPhone"), cardInfos.get("cardNo"), null, agentId);
			authLog = authenticationService.sendInfoToAuthentication(authLog);
			if (authLog.getStatus() == 1) {

				ts = new TuserCard();
				if (cardInfos.containsKey("openBankId") && StringUtil.isNotBlank(cardInfos.get("openBankId"))) {

					ts.setBank(bankService.getBank(Long.parseLong(cardInfos.get("openBankId"))));
				}
				if (cardInfos.containsKey("cardType")) {
					ts.setCardType(cardInfos.get("cardType"));
				}
				if (cardInfos.containsKey("openBranchId")) {
					ts.setBranchId(cardInfos.get("openBranchId"));
				}
				if (cardInfos.containsKey("openBranchName")) {
					ts.setBranchName(cardInfos.get("openBranchName"));
				}
				if (cardInfos.containsKey("cardNo")) {
					ts.setCardNo(cardInfos.get("cardNo"));
				}
				if (cardInfos.containsKey("openAreaId")) {
					ts.setCity(cardInfos.get("openAreaId"));
				}
				if (cardInfos.containsKey("openProvId")) {
					ts.setProvince(cardInfos.get("openProvId"));
				}

				if (cardInfos.containsKey("cvv")) {
					ts.setCvv(cardInfos.get("cvv"));
				}

				if (cardInfos.containsKey("expiryDate")) {
					ts.setValidityDate(cardInfos.get("expiryDate"));
				}
				if (cardInfos.containsKey("reservedPhone")) {
					ts.setPhone(cardInfos.get("reservedPhone"));
				}

				ts.setIsSettlmentCard(isSettmentCard && ts.getCardType().equals("J") ? 1 : 0);
				ts.setStatus(0);
				ts.setUser(user);

				if (isAuthUser) {
					isChangeUser = true;
					user.setIdNo(idNo);
					user.setRealName(realName);
					if (StringUtil.isNotBlank(cardInfos.get("merchantCity"))) {
						user.setMerchantCity(cardInfos.get("merchantCity").trim());
					}
					if (StringUtil.isNotBlank(cardInfos.get("merchantName"))) {
						user.setMerchantName(cardInfos.get("merchantName").trim());
					}
					user.setAuthenticationStatus(User.authentication_status.SUCCESS.getCode());
					/*
					 * 实名认证后，给推荐人加积分
					 */
					accountPointService.updatePoint(user.getParentUser().getId(), AccountPoint.pointTypes_popularity,
							"您推荐的" + user.getLoginName() + "_" + user.getRealName() + "实名认证通过了");
					AccountPoint point = accountPointService.getAccountPointByUserId(user.getParentUser().getId());
					userService.updateUserType(user.getParentUser().getId(), null, point.getSubPersonNum());
				}

				if (isSettmentCard) {
					isChangeUser = true;
					/* 用户存在结算卡才可以设置为结算卡 */
					user.setSettlementStatus(1);
					/* 更新原有结算卡 */
					cardDao.executeHql("update TuserCard t set t.isSettlmentCard=0 where t.user.id= " + user.getId()
							+ " and  t.isSettlmentCard=1");
				}
				cardDao.save(ts);
			} else {
				isChangeUser = true;
				String errorInfo = authLog.getErrorInfo();
				if (isAuthUser) {
					user.setAuthErrorNum(user.getAuthErrorNum() + 1);
					errorInfo += "\r\n 您今天还有" + (maxAutoNum - user.getAuthErrorNum()) + "次实名认证的机会";
				} else {
					user.setAuthCardErrorNum(user.getAuthCardErrorNum() + 1);
					errorInfo += "\r\n 您今天还有" + (maxAutoCardNum - user.getAuthCardErrorNum()) + "次认证银行卡的机会";
				}
				returnMap.put("flag", GlobalConstant.RESP_CODE_100);
				returnMap.put("errorCode", authLog.getErrorCode());
				returnMap.put("errorInfo", errorInfo);
			}
			/* 若用户信息已经更正，需要重新设置 */
			if (isChangeUser) {
				userDao.update(user);
			}
		}
		// cardInfos.put("cardId", ts.getId().toString());
		return returnMap;
	}

	@Override
	public Map<String, String> sendCardInfoToBankValidateTwo(Map<String, String> cardInfos, Boolean isSettmentCard,
			Boolean isAuthUser, String agentId) throws Exception {
		Map<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
		boolean isChangeUser = false;
		Long userId = Long.parseLong(cardInfos.get("merId"));
		Tuser user = userDao.get(Tuser.class, userId);
		int type = 0;
		if ("ios".equals(cardInfos.get("appType"))) {
			type = 2;
		} else if ("android".equals(cardInfos.get("appType"))) {
			type = 1;
		}

		String idNo = user.getIdNo();
		if (cardInfos.containsKey("idNo") && StringUtil.isNotBlank(cardInfos.get("idNo"))) {
			idNo = cardInfos.get("idNo");
		}
		String realName = user.getRealName();
		if (cardInfos.containsKey("realName") && StringUtil.isNotBlank(cardInfos.get("realName"))) {
			realName = cardInfos.get("realName");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TuserCard t left join t.user u where 1=1 and u.agentId=:agentId";
		params.put("agentId", agentId);
		if (cardInfos.containsKey("cardId") && StringUtil.isNotBlank(cardInfos.get("cardId"))) {
			hql = hql + " and t.id=:cardId";
			params.put("cardId", Long.parseLong(cardInfos.get("cardId")));
		} else {
			hql = hql + " and u.id=:uid and t.cardNo=:cardNo";
			params.put("uid", userId);
			params.put("cardNo", cardInfos.get("cardNo"));
		}
		TuserCard ts = cardDao.get(hql, params);
		/* 若卡片存在，更新卡片的结算卡信息 */
		if (ts != null) {
			if (isSettmentCard) {

				/* 取消原始的结算卡 */
				List<TuserCard> oldts = cardDao.find(
						"select t from TuserCard t left join t.user u where t.isSettlmentCard=1 and u.id=" + userId);
				for (TuserCard tc : oldts) {
					tc.setIsSettlmentCard(0);
					cardDao.update(tc);
				}

				/* 设置新结算卡 */
				user.setSettlementStatus(1);
				if (ts.getCardType().equals("J")) {
					ts.setIsSettlmentCard(1);
					ts.setStatus(0);
					cardDao.update(ts);
				}
			} else if (ts.getStatus() != 0) {
				ts.setStatus(0);
				cardDao.update(ts);
			}
		} else {
			/* 若这张卡不存在，需要先进行实名认证 */
			AuthenticationLog authLog = new AuthenticationLog(userId, 0, type, idNo, realName,
					cardInfos.get("reservedPhone"), cardInfos.get("cardNo"), null, agentId);
			authLog = authenticationService.sendInfoToAuthentication(authLog);
			if (authLog.getStatus() == 1) {

				ts = new TuserCard();
				if (cardInfos.containsKey("openBankId") && StringUtil.isNotBlank(cardInfos.get("openBankId"))) {

					ts.setBank(bankService.getBank(Long.parseLong(cardInfos.get("openBankId"))));
				}
				if (cardInfos.containsKey("cardType")) {
					ts.setCardType(cardInfos.get("cardType"));
				}
				if (cardInfos.containsKey("openBranchId")) {
					ts.setBranchId(cardInfos.get("openBranchId"));
				}
				if (cardInfos.containsKey("openBranchName")) {
					ts.setBranchName(cardInfos.get("openBranchName"));
				}
				if (cardInfos.containsKey("cardNo")) {
					ts.setCardNo(cardInfos.get("cardNo"));
				}
				if (cardInfos.containsKey("openAreaId")) {
					ts.setCity(cardInfos.get("openAreaId"));
				}
				if (cardInfos.containsKey("openProvId")) {
					ts.setProvince(cardInfos.get("openProvId"));
				}

				if (cardInfos.containsKey("cvv")) {
					ts.setCvv(cardInfos.get("cvv"));
				}

				if (cardInfos.containsKey("expiryDate")) {
					ts.setValidityDate(cardInfos.get("expiryDate"));
				}
				if (cardInfos.containsKey("reservedPhone")) {
					ts.setPhone(cardInfos.get("reservedPhone"));
				}

				ts.setIsSettlmentCard(isSettmentCard && ts.getCardType().equals("J") ? 1 : 0);
				ts.setStatus(0);
				ts.setUser(user);

				if (isAuthUser) {
					isChangeUser = true;
					user.setIdNo(idNo);
					user.setRealName(realName);
					if (StringUtil.isNotBlank(cardInfos.get("merchantCity"))) {
						user.setMerchantCity(cardInfos.get("merchantCity").trim());
					}
					if (StringUtil.isNotBlank(cardInfos.get("merchantName"))) {
						user.setMerchantName(cardInfos.get("merchantName").trim());
					}
					user.setAuthenticationStatus(User.authentication_status.SUCCESS.getCode());
					/*
					 * 实名认证后，给推荐人加积分
					 */
					accountPointService.updatePoint(user.getParentUser().getId(), AccountPoint.pointTypes_popularity,
							"您推荐的" + user.getLoginName() + "_" + user.getRealName() + "实名认证通过了");
					System.out.println("user:" + user.getParentUser().getId());
					AccountPoint point = accountPointService.getAccountPointByUserId(user.getParentUser().getId());
					userService.updateUserType(user.getParentUser().getId(), null, point.getSubPersonNum());
				}

				if (isSettmentCard) {
					isChangeUser = true;
					/* 用户存在结算卡才可以设置为结算卡 */
					user.setSettlementStatus(1);
					/* 更新原有结算卡 */
					cardDao.executeHql("update TuserCard t set t.isSettlmentCard=0 where t.user.id= " + user.getId()
							+ " and  t.isSettlmentCard=1");
				}
				cardDao.save(ts);
			} else {
				isChangeUser = true;
				String errorInfo = authLog.getErrorInfo();
				returnMap.put("flag", GlobalConstant.RESP_CODE_100);
				returnMap.put("errorCode", authLog.getErrorCode());
				returnMap.put("errorInfo", errorInfo);
			}
			/* 若用户信息已经更正，需要重新设置 */
			if (isChangeUser) {
				userDao.update(user);
			}
		}
		// cardInfos.put("cardId", ts.getId().toString());
		return returnMap;
	}

	@Override
	public String updateSettlementCard(Long userId, Long cardId) {
		/* 取消原始的结算卡 */
		TuserCard oldts = cardDao
				.get("select t from TuserCard t left join t.user u where t.isSettlmentCard=1 and u.id=" + userId);
		if (oldts != null) {
			oldts.setIsSettlmentCard(0);
			cardDao.update(oldts);
		} else {
			userDao.executeHql("update Tuser set settlementStatus=1 where id = " + userId);
		}
		TuserCard newCard = cardDao.get("select t from TuserCard t where t.id=" + cardId);
		newCard.setIsSettlmentCard(1);
		cardDao.update(newCard);
		return null;
	}

	@Override
	public void updateIsOpenYilianQuickPay(Long cardId, String isOpenYilianQuickPay) {
		TuserCard card = cardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
		card.setIsOpenYilianQuickPay(isOpenYilianQuickPay);
		cardDao.update(card);
	};

	@Override
	public void updateIsOpenGaZhiYinLianQuickPay(Long cardId, String isOpenGaZhiYinLianQuickPay) {
		TuserCard card = cardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
		card.setIsOpenGaZhiYinLianQuickPay(isOpenGaZhiYinLianQuickPay);
		cardDao.update(card);
	};

	@Override
	public String deleteCard(Long userId, Long cardId) {
		TuserCard card = cardDao.get("select t from TuserCard t left join t.user u where t.id=" + cardId);
		if (card != null) {
			Integer isSettlement = card.getIsSettlmentCard();
			card.setIsSettlmentCard(0);
			card.setStatus(1);
			cardDao.update(card);

			// 更改用户表中的结算卡设置状态
			if (isSettlement == 1 && card.getUser() != null) {
				card.getUser().setSettlementStatus(0);
				userDao.update(card.getUser());
			}
			return "1";
		}
		return "0";
	}

	@Override
	public boolean checkExistSettlementCard(Long userId) {
		String hql = "select t from TuserCard t left join t.user u where  t.isSettlmentCard = 1  and u.id = " + userId
				+ "  ";
		List<TuserCard> l = cardDao.find(hql);
		if (l.size() > 0) {
			return true;
		}
		return false;
	};

	@Override
	public List<UserCard> dataGrid(SessionInfo sessionInfo,UserCard card, PageFilter ph) {
		System.out.println("<UserCard> dataGrid");
		List<UserCard> ul = new ArrayList<UserCard>();
		boolean isCanSee = false;
		Set<Long> resIds = sessionInfo.getResourceIds();	
		for (Long resIds2 : resIds) {
			System.out.print(resIds2+"," );
		}		
		isCanSee = resIds.contains(11431L);
		System.out.println("是否授权："+isCanSee);
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TuserCard t left join t.user u left join u.organization tog ";
		List<TuserCard> l = cardDao.find(hql + whereHql(card, params) + orderHql(ph), params, ph.getPage(),
				ph.getRows());
		for (TuserCard t : l) {
			UserCard u = new UserCard(t);
			//card.getOperateUser() == null || !userService.isSuperAdmin(card.getOperateUser().getId())
			if (!isCanSee) {
				u.setIdNo(StringUtil.getCiphertextInfo(u.getIdNo(), 3, 4));
				u.setCardNo(StringUtil.getCiphertextInfo(u.getCardNo(), 3, 4));
				u.setPhone(StringUtil.getCiphertextInfo(u.getPhone(), 3, 4));
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(UserCard card, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TuserCard t left join t.user u left join u.organization tog ";
		return cardDao.count("select count(t.id) " + hql + whereHql(card, params), params);
	}

	private String whereHql(UserCard card, Map<String, Object> params) {
		String hql = "";
		if (card != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(card.getLoginName())) {
				hql += " and u.loginName like :loginName";
				params.put("loginName", "%%" + card.getLoginName() + "%%");
			}
			if (card.getOrganizationId() != null) {
				hql += " and  tog.id in(:orgIds)";
				params.put("orgIds", organizationService.getOwerOrgIds(card.getOrganizationId()));
			}
			if (card.getOperateUser() != null) {

				hql += " and  tog.id in(:operaterOrgIds)";
				params.put("operaterOrgIds",
						organizationService.getOwerOrgIds(card.getOperateUser().getOrganizationId()));
			}
		}
		return hql;
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	@Override
	public UserCard get(Long id) {
		TuserCard t = cardDao.get(TuserCard.class, id);
		if (t != null) {
			return new UserCard(t);
		}
		return null;
	}

	@Override
	public String getfindCode(String userId, String crdeNo) {
		TuserCard card = cardDao.get("select t from TuserCard t left join t.user u where u.id=" + Long.parseLong(userId)
				+ " and t.cardNo = " + crdeNo);
		return String.valueOf(card.getId());
	}
}
