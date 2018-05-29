package com.cn.flypay.service.trans;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.OrgBrokeragePay;

public interface OrgBrokeragePayService {

	public List<OrgBrokeragePay> dataGrid(OrgBrokeragePay appTransInfo, PageFilter ph);

	public Long count(OrgBrokeragePay appTransInfo, PageFilter ph);

	public String add(OrgBrokeragePay orgBrokeragePay, Long userId);

}
