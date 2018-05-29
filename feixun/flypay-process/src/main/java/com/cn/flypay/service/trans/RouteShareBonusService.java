package com.cn.flypay.service.trans;

import com.cn.flypay.model.trans.TorderBonusProcess;

/**
 * 分润路由
 * 
 * @author sunyue
 * 
 */
public interface RouteShareBonusService {

	Boolean dealBonusWhenOrder(TorderBonusProcess bonusProcess);
}
