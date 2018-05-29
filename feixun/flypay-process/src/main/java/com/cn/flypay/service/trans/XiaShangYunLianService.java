package com.cn.flypay.service.trans;

import java.math.BigDecimal;

import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.trans.TorderBonusProcess;

public interface XiaShangYunLianService {
	
	void updateABUserWhenShare(Tuser user, BigDecimal totalBonus, TorderBonusProcess bonusProcess, Integer transPayType);

}
