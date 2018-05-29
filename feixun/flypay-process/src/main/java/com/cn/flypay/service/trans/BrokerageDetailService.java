package com.cn.flypay.service.trans;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.BrokerageDetail;

public interface BrokerageDetailService {

	public List<BrokerageDetail> dataGrid(BrokerageDetail brokerageDetail, PageFilter ph);
	
	
	public List<BrokerageDetail> dataGridTwo(BrokerageDetail brokerageDetail, PageFilter ph);
	
	public String dataGridSum(BrokerageDetail brokerageDetail);

	public Long count(BrokerageDetail brokerageDetail, PageFilter ph);
	
	public String getSum(Long suerId,Integer brokerage);

}
