package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.Business;

public interface BusinessService {

	public List<Business> dataGrid(Business param, PageFilter ph);

	public Long count(Business param, PageFilter ph);

	public void add(Business param);

	public void delete(Business id);

	public void edit(Business param);
}
