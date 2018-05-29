package com.cn.flypay.service.trans.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.trans.TorderBonusProcess;
import com.cn.flypay.service.trans.RouteShareBonusService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class RouteShareBonusServiceImplTest {

	@Autowired
	private RouteShareBonusService routeShareBonusService;
	@Autowired
	private BaseDao<TorderBonusProcess> bonusProcessDao;

	@Test
	public void dealBonusWhenOrder() {
		String orderNum = "ALQR201701231521466840000012837";
		TorderBonusProcess bonusProcess = bonusProcessDao
				.get("select t from TorderBonusProcess t left join t.order d  left join d.user u left join u.organization g where t.status=0 and t.id=" + 23960);
		routeShareBonusService.dealBonusWhenOrder(bonusProcess);
	}
}
