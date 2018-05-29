package com.cn.flypay.service.sys;

import java.util.Map;

import com.cn.flypay.model.sys.TcardBankConfig;

public interface CardBankConfigService {

	Map<Long, TcardBankConfig> findCarBinToEntity();

	TcardBankConfig getByCardBin(Long cardBin);

	TcardBankConfig isRealCardNo(String trim);
}
