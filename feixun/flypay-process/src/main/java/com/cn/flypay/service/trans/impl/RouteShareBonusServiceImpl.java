package com.cn.flypay.service.trans.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.model.trans.TuserOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.trans.FuMiService;
import com.cn.flypay.service.trans.RouteShareBonusService;
import com.cn.flypay.service.trans.ShareBonusService;

@Service(value = "routeShareBonusService")
public class RouteShareBonusServiceImpl implements RouteShareBonusService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	private ShareBonusService equalRateShareBonusService;
	@Autowired
	private ShareBonusService xiaShangYunLianlRateShareBonusService;
	@Autowired
	private ShareBonusService rateDifferentiaShareBonusService;
	@Autowired
	private ShareBonusService boliShareBonusService;
	@Autowired
	ShareBonusService jinQGRateShareBonusService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private FuMiService fuMiServiceImpl;
	@Autowired
	private ShareBonusService xiaShangYunLianService;
	@Autowired
	private BaseDao<TorderBonusProcess> bonusProcessDao;
	@Autowired
	private ShareBonusService flyPayRateShareBonusService;
	@Autowired
	private ShareBonusService baoBeRateShareBonusService;
	@Autowired
	private ShareBonusService boBeiQianDaiService;
	@Autowired
	private ShareBonusService duoLianBaoRateShareBonusService;

	@Override
	public Boolean dealBonusWhenOrder(TorderBonusProcess bonusProcess) {
		if (bonusProcess != null) {
			TuserOrder userOrder = bonusProcess.getOrder();
			if (userOrder.getUser() != null) {
				Tuser user = userOrder.getUser();
				LOG.info("total bonus id={}, amt={}", bonusProcess.getId(), bonusProcess.getTotalAmt());
				/* 总机构拿出的分润比例 */
				Torganization org = organizationService.getTorganizationInCacheByCode(user.getAgentId());
				if (org.getShareBonusType() != 0) {
					if ("F20160002".equals(org.getCode())) { // 泊力商务
						/* 前期特殊的分润规则 */
						boliShareBonusService.updateABUserWhenShare(user, bonusProcess.getTotalAmt(), bonusProcess, userOrder.getTransPayType());
					} else if ("F00060009".equals(org.getCode())) { // 蚨米
						BigDecimal totalBonus = bonusProcess.getTotalAmt().multiply(BigDecimal.ONE.subtract(org.getPrincipalRate()));
						if (totalBonus.doubleValue() > 0.01) {
							fuMiServiceImpl.updateABUserWhenShare(user, totalBonus, bonusProcess, userOrder.getTransPayType());
						}
					} else {
						BigDecimal totalBonus = bonusProcess.getTotalAmt().multiply(BigDecimal.ONE.subtract(org.getPrincipalRate()));
						if (userOrder.getTransPayType() - UserOrder.trans_pay_type.AGENT_PAY_TYPE.getCode() == 0) {
							// 购买代理订单
							totalBonus = bonusProcess.getTotalAmt().multiply(BigDecimal.ONE.subtract(org.getPrincipalAgentRate()));
							if (totalBonus.doubleValue() > 0.01) {
								LOG.info("organization total bonus amt " + totalBonus.doubleValue());
								if ("F20160017".equals(org.getCode())) {
									//
									xiaShangYunLianlRateShareBonusService.updateABUserWhenShare(user, totalBonus, bonusProcess, userOrder.getTransPayType());
								} else if ("F20160001".equals(org.getCode()) || 
										"F20160010".equals(org.getCode()) || 
										"F20160003".equals(org.getCode()) || 
										"F20160004".equals(org.getCode()) || 
										"F20160011".equals(org.getCode())) {
									baoBeRateShareBonusService.updateABUserWhenShare(user, totalBonus, bonusProcess, userOrder.getTransPayType());
								} else if ("F20180002".equals(org.getCode())) {// 多联宝
									duoLianBaoRateShareBonusService.updateABUserWhenShare(user, totalBonus, bonusProcess, userOrder.getTransPayType());
								} else {
									equalRateShareBonusService.updateABUserWhenShare(user, totalBonus, bonusProcess, userOrder.getTransPayType());
								}
							} else {
								LOG.info("organization total bonus amt " + totalBonus + ", less than 0.01");
							}
						} else {
							if (totalBonus.doubleValue() > 0.01) {
								LOG.info("organization total bonus amt " + totalBonus.doubleValue());
								// 0不分润，1 固定比例分润；2 代理固定比例，流量交易差
								if (org.getShareBonusType() == 1) {
									if (user.getAgentId().equals("F20160015")) {
										jinQGRateShareBonusService.updateABUserWhenShare(user, totalBonus, bonusProcess, userOrder.getTransPayType());
									} else {
										equalRateShareBonusService.updateABUserWhenShare(user, totalBonus, bonusProcess, userOrder.getTransPayType());
									}
								} else if (org.getShareBonusType() == 2) {
									if ("F20160017".equals(org.getCode())) {
										//
										xiaShangYunLianService.updateABUserWhenShare(user, totalBonus, bonusProcess, userOrder.getTransPayType());
									} else if ("F20160001".equals(org.getCode()) || "F20160010".equals(org.getCode()) || "F20160003".equals(org.getCode()) || "F20160004".equals(org.getCode()) || "F20160011".equals(org.getCode())) {
										// 宝贝钱袋及OEM2.0的交易分润
										boBeiQianDaiService.updateABUserWhenShare(user, totalBonus, bonusProcess, userOrder.getTransPayType());
									} else if ("F20180002".equals(org.getCode())) {// 多联宝
										duoLianBaoRateShareBonusService.updateABUserWhenShare(user, totalBonus, bonusProcess, userOrder.getTransPayType());
									} else {
										rateDifferentiaShareBonusService.updateABUserWhenShare(user, totalBonus, bonusProcess, userOrder.getTransPayType());
									}
								} else {
									LOG.info("organization can't allow share bonus!");
								}
							} else {
								LOG.info("organization total bonus amt " + totalBonus + ", less than 0.01");
							}
						}
					}
					bonusProcess.setStatus(100);
					bonusProcessDao.update(bonusProcess);
				}
			}
		} else {
			LOG.info("none waiting share bonus  ");
		}
		return null;
	}
}
