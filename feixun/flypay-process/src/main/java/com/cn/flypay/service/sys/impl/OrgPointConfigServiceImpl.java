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
import com.cn.flypay.model.sys.TorgPointConfig;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.TuserSettlementConfig;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.OrgPointConfig;
import com.cn.flypay.service.sys.OrgPointConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.utils.StringUtil;

@Service
public class OrgPointConfigServiceImpl implements OrgPointConfigService {

	@Autowired
	private BaseDao<TorgPointConfig> orgPointConfigDao;

	@Autowired
	private OrganizationService organizationService;

	@Override
	public List<OrgPointConfig> dataGrid(OrgPointConfig param, PageFilter ph) {
		List<OrgPointConfig> ul = new ArrayList<OrgPointConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from TorgPointConfig t left join t.organization org ";
		List<TorgPointConfig> l = orgPointConfigDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TorgPointConfig t : l) {
			OrgPointConfig u = new OrgPointConfig();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrgName(t.getOrganization().getName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(OrgPointConfig param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TorgPointConfig t left join t.organization org ";
		return orgPointConfigDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	private String whereHql(OrgPointConfig param, Map<String, Object> params) {
		String hql = "";
		if (param != null) {
			hql += " where 1=1 ";
		}
		if (param.getOrganizationId() != null) {
			hql += " and  org.id =:orgIds";
			params.put("orgIds", param.getOrganizationId());
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

	@CacheEvict(value = { "orgPointConfigCache" }, allEntries = true)
	@Override
	public void edit(OrgPointConfig poc) {
		TorgPointConfig t = orgPointConfigDao.get(TorgPointConfig.class, poc.getId());
		t.setLowRate(poc.getLowRate());
		t.setMidRate(poc.getMidRate());
		t.setTopRate(poc.getTopRate());
		t.setToMidNum(poc.getToMidNum());
		t.setToLowNum(poc.getToLowNum());
		t.setStatus(poc.getStatus());
		orgPointConfigDao.update(t);
	}

	@Override
	public OrgPointConfig get(Long id) {
		TorgPointConfig t = orgPointConfigDao.get("select t from TorgPointConfig t left join t.organization org where t.id=" + id);
		if (t != null) {
			OrgPointConfig u = new OrgPointConfig();
			BeanUtils.copyProperties(t, u);
			if (t.getOrganization() != null) {
				u.setOrgName(t.getOrganization().getName());
			}
			return u;
		}
		return null;
	}

	@Cacheable(value = "orgPointConfigCache", key = "#agentId+'getByAgentId'+#payType+'_'+#type")
	@Override
	public OrgPointConfig getByAgentIdAndPayType(String agentId, Integer payType, Integer type) {
		String hql = "select t from TorgPointConfig t left join t.organization g where t.payType=:payType and g.code=:agentId and t.type=:type";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("agentId", StringUtil.getAgentId(agentId));
		params.put("payType", payType);
		params.put("type", type);
		TorgPointConfig t = orgPointConfigDao.get(hql, params);
		if (t != null) {
			OrgPointConfig oc = new OrgPointConfig();
			BeanUtils.copyProperties(t, oc);
			return oc;
		}
		return null;
	}

	@Override
	public List<OrgPointConfig> findOrgPointConfigsByAgentId(String agentId,Boolean flag) {
		String hql = "";
		if(flag){
			hql = "select t from TorgPointConfig t left join t.organization g where  g.code=:agentId and t.status=0 and t.type in (20,21)";
		}else{
			hql = "select t from TorgPointConfig t left join t.organization g where  g.code=:agentId and t.status=0";
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("agentId", StringUtil.getAgentId(agentId));
		List<TorgPointConfig> ts = orgPointConfigDao.find(hql, params);
		List<OrgPointConfig> opcl = new ArrayList<OrgPointConfig>();
		for (TorgPointConfig t : ts) {
			OrgPointConfig oc = new OrgPointConfig();
			BeanUtils.copyProperties(t, oc);
			opcl.add(oc);
		}
		return opcl;
	}

	@Override
	public void initOrgPointConfigs(Torganization t) {
		String hql = "select t from TorgPointConfig t left join t.organization g where  g.id=:gid";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gid", t.getOrganization().getId());
		List<TorgPointConfig> ts = orgPointConfigDao.find(hql, params);
		for (TorgPointConfig o : ts) {
			TorgPointConfig ntc = new TorgPointConfig();
			BeanUtils.copyProperties(o, ntc);
			ntc.setId(null);
			ntc.setVersion(0l);
			ntc.setOrganization(t);
			orgPointConfigDao.save(ntc);
		}
	}

	/**
	 * 从积分降费率配置中，配置用户费率
	 */
	@Override
	public void initUserSettlementWhenPoint(String agentId, TuserSettlementConfig sc) {
		/* T1 小额 */
		sc.setInputFeeAlipay(getByAgentIdAndPayType(agentId, 200, 1).getTopRate());
		sc.setInputFeeWeixin(getByAgentIdAndPayType(agentId, 300, 1).getTopRate());
		sc.setInputFeeYinlian(getByAgentIdAndPayType(agentId, 500, 1).getTopRate());
		sc.setInputFeeJingDong(getByAgentIdAndPayType(agentId, 900, 1).getTopRate());
		sc.setInputFeeBaidu(getByAgentIdAndPayType(agentId, 1000, 1).getTopRate());
		sc.setInputFeeYizhifu(getByAgentIdAndPayType(agentId, 1100, 1).getTopRate());
		sc.setInputFeeYinLianzhifu(getByAgentIdAndPayType(agentId, 1200, 1).getTopRate());
		/* D0 小额 */
		sc.setInputFeeD0Alipay(getByAgentIdAndPayType(agentId, 200, 0).getTopRate());
		sc.setInputFeeD0Weixin(getByAgentIdAndPayType(agentId, 300, 0).getTopRate());
		sc.setInputFeeD0Yinlian(getByAgentIdAndPayType(agentId, 500, 0).getTopRate());
		sc.setInputFeeD0JingDong(getByAgentIdAndPayType(agentId, 900, 0).getTopRate());
		sc.setInputFeeD0Baidu(getByAgentIdAndPayType(agentId, 1000, 0).getTopRate());
		sc.setInputFeeD0Yizhifu(getByAgentIdAndPayType(agentId, 1100, 0).getTopRate());
		sc.setInputFeeD0YinLianzhifu(getByAgentIdAndPayType(agentId, 1200, 0).getTopRate());
		/* T1大额 */
		sc.setInputFeeBigAlipay(getByAgentIdAndPayType(agentId, 200, 11).getTopRate());
		sc.setInputFeeBigWeixin(getByAgentIdAndPayType(agentId, 300, 11).getTopRate());
		sc.setInputFeeBigYinlian(getByAgentIdAndPayType(agentId, 500, 11).getTopRate());
		sc.setInputFeeBigJingDong(getByAgentIdAndPayType(agentId, 900, 11).getTopRate());
		sc.setInputFeeBigBaidu(getByAgentIdAndPayType(agentId, 1000, 11).getTopRate());
		sc.setInputFeeBigYizhifu(getByAgentIdAndPayType(agentId, 1100, 11).getTopRate());
		sc.setInputFeeBigYinLianzhifu(getByAgentIdAndPayType(agentId, 1200, 11).getTopRate());
		sc.setInputFeeBigQQzhifu(getByAgentIdAndPayType(agentId, 1300, 11).getTopRate());
		
		/* D0大额 */
		sc.setInputFeeD0BigAlipay(getByAgentIdAndPayType(agentId, 200, 10).getTopRate());
		sc.setInputFeeD0BigWeixin(getByAgentIdAndPayType(agentId, 300, 10).getTopRate());
		sc.setInputFeeD0BigYinlian(getByAgentIdAndPayType(agentId, 500, 10).getTopRate());
		sc.setInputFeeD0BigJingDong(getByAgentIdAndPayType(agentId, 900, 10).getTopRate());
		sc.setInputFeeD0BigBaidu(getByAgentIdAndPayType(agentId, 1000, 10).getTopRate());
		sc.setInputFeeD0BigYizhifu(getByAgentIdAndPayType(agentId, 1100, 10).getTopRate());
		sc.setInputFeeD0BigYinLianzhifu(getByAgentIdAndPayType(agentId, 1200, 10).getTopRate());
		sc.setInputFeeD0BigQQzhifu(getByAgentIdAndPayType(agentId, 1300, 10).getTopRate());
		
		/*直通车*/
		if(agentId.equals("F20160001") || agentId.equals("F20160015")){		//F20160001   宝贝钱袋 （最高文件夹图标级别 ）   F20160015   金钱龟管家（图标为小房子级别）
			/*直通车T1  21*/
			sc.setInputFeeZtAlipay(getByAgentIdAndPayType(agentId, 200, 21).getTopRate());
			sc.setInputFeeZtWeixin(getByAgentIdAndPayType(agentId, 300, 21).getTopRate());
			sc.setInputFeeZtYinlian(getByAgentIdAndPayType(agentId, 500, 21).getTopRate());
			sc.setInputFeeZtQQzhifu(getByAgentIdAndPayType(agentId, 1300, 21).getTopRate());
			sc.setInputFeeZtYinlianJf(getByAgentIdAndPayType(agentId, 550, 21).getTopRate());	//银联积分T1直通车
			sc.setInputFeeZtYinlianJfZY(getByAgentIdAndPayType(agentId, 552, 21).getTopRate());
			/*直通车D0   20*/
			sc.setInputFeeD0ZtAlipay(getByAgentIdAndPayType(agentId, 200, 20).getTopRate());
			sc.setInputFeeD0ZtWeixin(getByAgentIdAndPayType(agentId, 300, 20).getTopRate());
			sc.setInputFeeD0ZtYinlian(getByAgentIdAndPayType(agentId, 500, 20).getTopRate());
			sc.setInputFeeD0ZtQQzhifu(getByAgentIdAndPayType(agentId, 1300, 20).getTopRate());
			sc.setInputFeeD0ZtYinlianJf(getByAgentIdAndPayType(agentId, 550, 20).getTopRate());	//银联积分D0直通车
			sc.setInputFeeD0ZtYinlianJfZY(getByAgentIdAndPayType(agentId, 552, 20).getTopRate());	//银联积分D0直通车
		}else{
			sc.setInputFeeZtAlipay(new BigDecimal(0.0049));
			sc.setInputFeeZtWeixin(new BigDecimal(0.0049));
			sc.setInputFeeZtYinlian(new BigDecimal(0.0052));
			sc.setInputFeeZtQQzhifu(new BigDecimal(0.0049));
			sc.setInputFeeZtYinlianJf(new BigDecimal(0.0052));	//银联积分T1直通车
			
			sc.setInputFeeD0ZtAlipay(new BigDecimal(0.0049));
			sc.setInputFeeD0ZtWeixin(new BigDecimal(0.0049));
			sc.setInputFeeD0ZtYinlian(new BigDecimal(0.0052));
			sc.setInputFeeD0ZtQQzhifu(new BigDecimal(0.0049));
			sc.setInputFeeD0ZtYinlianJf(new BigDecimal(0.0052));	//银联积分D0直通车
			sc.setInputFeeZtYinlianJfZY(new BigDecimal(0.0053));
			sc.setInputFeeD0ZtYinlianJfZY(new BigDecimal(0.0058));	//银联积分D0直通车
		}
	}

	@Override
	public List<OrgPointConfig> findOrgPointConfigsByAgentIdPayType(String agentId,String payType, String InfoType) {
		String hql = "";
		if(InfoType.equals("1")){
			hql = "select t from TorgPointConfig t left join t.organization g where  g.code=:agentId and t.payType=:payType and t.status=0 and t.type in (20,21)";
		}else{
			hql = "select t from TorgPointConfig t left join t.organization g where  g.code=:agentId and t.payType=:payType and t.status=0 and t.type not in (20,21)";
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("agentId", StringUtil.getAgentId(agentId));
		params.put("payType", Integer.parseInt(payType));
		List<TorgPointConfig> ts = orgPointConfigDao.find(hql, params);
		List<OrgPointConfig> opcl = new ArrayList<OrgPointConfig>();
		for (TorgPointConfig t : ts) {
			OrgPointConfig oc = new OrgPointConfig();
			BeanUtils.copyProperties(t, oc);
			opcl.add(oc);
		}
		return opcl;
	}

	
	@Override
	public List<OrgPointConfig> findOrgPointConfigsByAgentIdAgentId(String agentId) {
		String hql = "select t from TorgPointConfig t left join t.organization g where  g.code=:agentId and t.status=0";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("agentId", StringUtil.getAgentId(agentId));
		List<TorgPointConfig> ts = orgPointConfigDao.find(hql, params);
		List<OrgPointConfig> opcl = new ArrayList<OrgPointConfig>();
		for (TorgPointConfig t : ts) {
			OrgPointConfig oc = new OrgPointConfig();
			BeanUtils.copyProperties(t, oc);
			opcl.add(oc);
		}
		return opcl;
	}
	
	
	
}
