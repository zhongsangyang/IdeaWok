package com.cn.flypay.service.trans;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.OrgBrokerage;

public interface OrgBrokerageService {

	public List<OrgBrokerage> dataGrid(OrgBrokerage appTransInfo, PageFilter ph);

	public Long count(OrgBrokerage appTransInfo, PageFilter ph);

}
