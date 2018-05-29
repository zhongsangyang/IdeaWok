package com.cn.flypay.service.trans;

import java.util.List;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.trans.SysCreateTestData;
/**
 * 为王美凤造数据
 * @author liangchao
 *
 */
public interface SysCreateTestDataService {

	
	/**
	 * 创建记录
	 */
	public void create(SysCreateTestData req) throws Exception;
	
	
	
	/**
	 * 读取数据
	 * @param req
	 * @param ph
	 * @return
	 */
	public List<SysCreateTestData> dataGrid(SysCreateTestData req,PageFilter ph) throws Exception;
	
	
}
