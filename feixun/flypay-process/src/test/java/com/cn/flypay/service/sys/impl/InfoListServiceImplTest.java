package com.cn.flypay.service.sys.impl;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.service.sys.InfoListService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class InfoListServiceImplTest {

	@Autowired
	private InfoListService infoListService;

	public void testFindSystemNews() {
		List ids = infoListService.findSystemNews("F20160001");
		System.out.println(ids.size());
	}

	@Test
	public void testdataGrid() {
		PageFilter pf = new PageFilter();
		pf.setPage(10);
		pf.setRows(0);
		pf.setSort("id");
		InfoList infoList = new InfoList();
		infoList.setUserId(4l);
		infoList.setStatus(InfoList.info_status.release_success.getCode());
		infoList.setInfoType(1);
		infoList.setAgentId("F2016000111");

		List<InfoList> ll = infoListService.dataGrid(infoList, pf);
		System.out.println(ll);
	}
}
