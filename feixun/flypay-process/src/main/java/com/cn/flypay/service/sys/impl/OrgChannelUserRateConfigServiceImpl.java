package com.cn.flypay.service.sys.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TorgChannelUserRateConfig;
import com.cn.flypay.model.sys.TorgChannelUserRateConfigId;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.TuserSettlementConfig;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.OrgChannelUserRateConfig;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.service.sys.OrgChannelUserRateConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.UserSettlementConfigService;

@Service
public class OrgChannelUserRateConfigServiceImpl implements OrgChannelUserRateConfigService {

	@Autowired
	private BaseDao<TorgChannelUserRateConfig> bcDao;
	@Autowired
	private BaseDao<Torganization> toDao;
	@Autowired
	private BaseDao<TuserSettlementConfig> userSettlementConfigDao;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private UserSettlementConfigService configService;
	

	@CacheEvict(value = "orgChannelUserRateConfigCache")
	@Override
	public void edit(OrgChannelUserRateConfig bc) {
		Torganization tt = toDao.get(Torganization.class, bc.getOrganizationId());
		TorgChannelUserRateConfigId id = new TorgChannelUserRateConfigId(bc.getChannelType(), bc.getAgentType(), tt);
		TorgChannelUserRateConfig tbc = getTorgChannelUserRateConfig(id);
		tbc.setT1Rate(bc.getT1Rate());
		tbc.setD0Rate(bc.getD0Rate());
		tbc.setT1BigRate(bc.getT1BigRate());
		tbc.setD0BigRate(bc.getD0BigRate());
		bcDao.update(tbc);
	}

	private TorgChannelUserRateConfig getTorgChannelUserRateConfig(TorgChannelUserRateConfigId id) {
		TorgChannelUserRateConfig bc = bcDao.get(TorgChannelUserRateConfig.class, id);
		return bc;
	}

	@Override
	public OrgChannelUserRateConfig get(Integer channelType, Integer agentType, Long organizationId) {
		TorgChannelUserRateConfigId id = new TorgChannelUserRateConfigId(channelType, agentType, toDao.get(Torganization.class, organizationId));
		TorgChannelUserRateConfig tbc = getTorgChannelUserRateConfig(id);
		OrgChannelUserRateConfig bc = new OrgChannelUserRateConfig();

		bc.setOrgName(id.getOrganization().getName());
		bc.setOrganizationId(id.getOrganization().getId());
		bc.setChannelType(id.getChannelType());
		bc.setAgentType(id.getAgentType());

		bc.setT1Rate(tbc.getT1Rate());
		bc.setD0Rate(tbc.getD0Rate());
		bc.setT1BigRate(tbc.getT1BigRate());
		bc.setD0BigRate(tbc.getD0BigRate());
		return bc;
	}

	@Cacheable(value = "orgChannelUserRateConfigCache", key = "#channelType+'getOrgChannelUserRateConfigInCache'+#agentType+'_'+#organizationId")
	@Override
	public OrgChannelUserRateConfig getOrgChannelUserRateConfigInCache(Integer channelType, Integer agentType, Long organizationId) {
		return get(channelType, agentType, organizationId);
	}

	@Cacheable(value = "orgChannelUserRateConfigCache", key = "#channelType+'getUserShareRateInCache'+#sourceUserType+#targetUserType+'_'+'#accountType'+'_'+#organizationId")
	@Override
	public BigDecimal getUserShareRateInCache(Integer channelType, Integer sourceUserType, Integer targetUserType, Integer accountType, Long organizationId) {
		BigDecimal shareRate = BigDecimal.ZERO;
		if (sourceUserType > targetUserType) {

			/* 钻石的费率 */
			OrgChannelUserRateConfig ouc21 = getOrgChannelUserRateConfigInCache(channelType, targetUserType, organizationId);
			/* 用户的费率 */
			OrgChannelUserRateConfig ouc_agentType = getOrgChannelUserRateConfigInCache(channelType, sourceUserType, organizationId);
			switch (accountType) {
			case 0:
				shareRate = ouc_agentType.getD0Rate().subtract(ouc21.getD0Rate());
				break;
			case 1:
				shareRate = ouc_agentType.getT1Rate().subtract(ouc21.getT1Rate());
				break;
			case 10:
				shareRate = ouc_agentType.getD0BigRate().subtract(ouc21.getD0BigRate());
				break;
			case 11:
				shareRate = ouc_agentType.getT1BigRate().subtract(ouc21.getT1BigRate());
				break;
			default:
				break;
			}
		} else {
			/* 同为钻石用户，没有利率差 */
		}
		return shareRate;
	}
	
	
	@Override
	public BigDecimal getUserShareRateInCacheC(Integer channelType, Integer sourceUserType, Integer targetUserType, Integer accountType, Long organizationId) {
		BigDecimal shareRate = BigDecimal.ZERO;
			OrgChannelUserRateConfig ouc_agentType = getOrgChannelUserRateConfigInCache(channelType, sourceUserType, organizationId);
			switch (accountType) {
			case 0:
				shareRate = ouc_agentType.getD0Rate();
				break;
			case 1:
				shareRate = ouc_agentType.getT1Rate();
				break;
			case 10:
				shareRate = ouc_agentType.getD0BigRate();
				break;
			case 11:
				shareRate = ouc_agentType.getT1BigRate();
				break;
			default:
				break;
			}
		return shareRate;
	}
	

	@Override
	public List<OrgChannelUserRateConfig> dataGrid(OrgChannelUserRateConfig bc, PageFilter ph) {
		List<OrgChannelUserRateConfig> ul = new ArrayList<OrgChannelUserRateConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TorgChannelUserRateConfig t left join t.id.organization";
		List<TorgChannelUserRateConfig> bcl = bcDao.find(hql + whereHql(bc, params), params, ph.getPage(), ph.getRows());
		for (TorgChannelUserRateConfig tbc : bcl) {
			OrgChannelUserRateConfig nbc = new OrgChannelUserRateConfig();
			nbc.setOrgName(tbc.getId().getOrganization().getName());
			nbc.setOrganizationId(tbc.getId().getOrganization().getId());
			nbc.setChannelType(tbc.getId().getChannelType());
			nbc.setAgentType(tbc.getId().getAgentType());
			nbc.setD0Rate(tbc.getD0Rate());
			nbc.setT1Rate(tbc.getT1Rate());
			nbc.setT1BigRate(tbc.getT1BigRate());
			nbc.setD0BigRate(tbc.getD0BigRate());
			ul.add(nbc);
		}
		return ul;
	}

	private String whereHql(OrgChannelUserRateConfig bc, Map<String, Object> params) {
		String hql = "";
		if (bc != null) {
			hql += " where 1=1 ";
			if (bc.getOrganizationId() != null && bc.getOrganizationId() != 0) {
				hql += " and t.id.organization.id = :organizationId";
				params.put("organizationId", bc.getOrganizationId());
			}
		}
		return hql;
	}

	@Override
	public Long count(OrgChannelUserRateConfig bc, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TorgChannelUserRateConfig  t ";
		return bcDao.count("select count(*) " + hql + whereHql(bc, params), params);
	}

	@Override
	public void initOrgChannelUserRateConfigByCopyParent(Torganization t) {
		List<TorgChannelUserRateConfig> orgList = bcDao.find("select t from TorgChannelUserRateConfig t left join t.id.organization org where org.id=" + t.getOrganization().getId());
		for (TorgChannelUserRateConfig tof : orgList) {
			TorgChannelUserRateConfig tof1 = new TorgChannelUserRateConfig();
			BeanUtils.copyProperties(tof, tof1);
			tof1.getId().setOrganization(t);
			bcDao.save(tof1);
		}
	}
	
	
	
	//读取升级降费率的信息配置用户费率信息
	@Override
	public void updateOrgChannelUserRate(Integer type, Long userId,String agendId) {
		TuserSettlementConfig scg = configService.getTuserSettlementConfigByUserId(userId);
		Torganization org = organizationService.getTorganizationInCacheByCode(agendId);
		OrgChannelUserRateConfig rateZ = getOrgChannelUserRateConfigInCache(200, type, org.getId());
		scg.setInputFeeAlipay(rateZ.getT1Rate());
		scg.setInputFeeD0Alipay(rateZ.getD0Rate());
		scg.setInputFeeBigAlipay(rateZ.getT1BigRate());
		scg.setInputFeeD0BigAlipay(rateZ.getD0BigRate());
		scg.setInputFeeZtAlipay(rateZ.getT1Rate());
		scg.setInputFeeD0ZtAlipay(rateZ.getD0Rate());
		
		
		OrgChannelUserRateConfig rateW = getOrgChannelUserRateConfigInCache(300, type, org.getId());
		scg.setInputFeeWeixin(rateW.getT1Rate());
		scg.setInputFeeD0Weixin(rateW.getD0Rate());
		scg.setInputFeeBigWeixin(rateW.getT1BigRate());
		scg.setInputFeeD0BigWeixin(rateW.getD0BigRate());
		scg.setInputFeeZtWeixin(rateW.getT1Rate());
		scg.setInputFeeD0ZtWeixin(rateW.getD0Rate());
		
		
		OrgChannelUserRateConfig rateY = getOrgChannelUserRateConfigInCache(500, type, org.getId());	//500 银联在线
		OrgChannelUserRateConfig rateYZ = getOrgChannelUserRateConfigInCache(530, type, org.getId());	//530 银联大额
		OrgChannelUserRateConfig rateYJ = getOrgChannelUserRateConfigInCache(550, type, org.getId());	//550 银联积分
		rateYZ = rateYZ==null?rateY:rateYZ;
		rateYJ = rateYJ==null?rateY:rateYJ;
		scg.setInputFeeYinlian(rateY.getT1Rate());
		scg.setInputFeeD0Yinlian(rateY.getD0Rate());
		scg.setInputFeeBigYinlian(rateY.getT1BigRate());
		scg.setInputFeeD0BigYinlian(rateY.getD0BigRate());
		scg.setInputFeeZtYinlian(rateYZ.getT1Rate());
		scg.setInputFeeD0ZtYinlian(rateYZ.getD0Rate());
		scg.setInputFeeZtYinlianJf(rateYJ.getT1Rate());
		scg.setInputFeeD0ZtYinlianJf(rateYJ.getD0Rate());
		
		
		OrgChannelUserRateConfig rateJ = getOrgChannelUserRateConfigInCache(900, type, org.getId());
		scg.setInputFeeJingDong(rateJ.getT1Rate());
		scg.setInputFeeD0JingDong(rateJ.getD0Rate());
		scg.setInputFeeBigJingDong(rateJ.getT1BigRate());
		scg.setInputFeeD0BigJingDong(rateJ.getD0BigRate());
		
		
		OrgChannelUserRateConfig rateB = getOrgChannelUserRateConfigInCache(1000, type, org.getId());
		scg.setInputFeeBaidu(rateB.getT1Rate());;
		scg.setInputFeeD0Baidu(rateB.getD0Rate());
		scg.setInputFeeBigBaidu(rateB.getT1BigRate());
		scg.setInputFeeD0BigBaidu(rateB.getD0BigRate());
		
		//增加翼支付
		OrgChannelUserRateConfig rateYz = getOrgChannelUserRateConfigInCache(1100, type, org.getId());
		scg.setInputFeeYizhifu(rateYz.getT1Rate());;
		scg.setInputFeeD0Yizhifu(rateYz.getD0Rate());
		scg.setInputFeeBigYizhifu(rateYz.getT1BigRate());
		scg.setInputFeeD0BigYizhifu(rateYz.getD0BigRate());
		
		OrgChannelUserRateConfig rateQ = getOrgChannelUserRateConfigInCache(1300, type, org.getId());
		scg.setInputFeeBigQQzhifu(rateQ.getT1BigRate());
		scg.setInputFeeD0BigQQzhifu(rateQ.getD0BigRate());
		scg.setInputFeeZtQQzhifu(rateQ.getT1Rate());
		scg.setInputFeeD0ZtQQzhifu(rateQ.getD0Rate());
		userSettlementConfigDao.update(scg);
	}
	
	
	
	
	
	

}
