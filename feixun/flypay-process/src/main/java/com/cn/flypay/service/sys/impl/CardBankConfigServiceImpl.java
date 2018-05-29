package com.cn.flypay.service.sys.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TcardBankConfig;
import com.cn.flypay.service.sys.CardBankConfigService;
import com.cn.flypay.utils.StringUtil;

@Service
public class CardBankConfigServiceImpl implements CardBankConfigService {

	@Autowired
	private BaseDao<TcardBankConfig> cardBankConfigDao;

	@Override
	public Map<Long, TcardBankConfig> findCarBinToEntity() {
		Map<Long, TcardBankConfig> map = new HashMap<Long, TcardBankConfig>();
		List<TcardBankConfig> ls = cardBankConfigDao.find("from TcardBankConfig t");
		for (TcardBankConfig t : ls) {
			map.put(t.getCardBin(), t);
		}
		return map;
	}

	@Override
	public TcardBankConfig getByCardBin(Long cardBin) {

		return null;
	}

	@Override
	public TcardBankConfig isRealCardNo(String cardNo) {

		if (StringUtil.isNotBlank(cardNo) && cardNo.length() >= 14) {
			Map<Long, TcardBankConfig> map = findCarBinToEntity();
			Long no = Long.parseLong(cardNo.substring(0,4));
			if (map.containsKey(no)) {
				return map.get(no);
			}
			no = Long.parseLong(cardNo.substring(0,5));
			if (map.containsKey(no)) {
				return map.get(no);
			}
			no = Long.parseLong(cardNo.substring(0,6));
			if (map.containsKey(no)) {
				return map.get(no);
			}
			no = Long.parseLong(cardNo.substring(0,7));
			if (map.containsKey(no)) {
				return map.get(no);
			}
			no = Long.parseLong(cardNo.substring(0,8));
			if (map.containsKey(no)) {
				return map.get(no);
			}

		}
		return null;
	}

}
