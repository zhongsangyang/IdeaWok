package com.cn.flypay.service.sys;

import java.math.BigDecimal;
import java.util.List;

import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.account.PlatformOrgConfig;
import com.cn.flypay.pageModel.base.PageFilter;

public interface PlatformOrgConfigService {

	public List<PlatformOrgConfig> dataGrid(PlatformOrgConfig poc, PageFilter ph);

	public Long count(PlatformOrgConfig poc, PageFilter ph);

	public void add(PlatformOrgConfig poc);

	public void delete(Long id);

	public void edit(PlatformOrgConfig poc);

	public PlatformOrgConfig get(Long id);

	public PlatformOrgConfig getPlatformOrgConfig(String agentId);

	BigDecimal getPlatformOrgFee(String agentId, Integer type, BigDecimal amt);

	public void initPlatformOrgConfig(Torganization t);

}
