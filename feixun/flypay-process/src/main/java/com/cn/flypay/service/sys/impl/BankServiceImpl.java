package com.cn.flypay.service.sys.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tbank;
import com.cn.flypay.pageModel.sys.Bank;
import com.cn.flypay.service.sys.BankService;

@Service
public class BankServiceImpl implements BankService {

	@Autowired
	private BaseDao<Tbank> bankDao;

	@Override
	public List<Bank> findBankList() {
		List<Bank> bs = new ArrayList<Bank>();
		List<Tbank> ts = bankDao.find("from Tbank t ");
		if (ts != null) {
			for (Tbank tbank : ts) {
				Bank bk = new Bank();
				BeanUtils.copyProperties(tbank, bk);
				bs.add(bk);
			}
		}
		return bs;
	}

	@Override
	public Tbank getBank(Long bankId) {
		return bankDao.get(Tbank.class, bankId);
	}

	@Override
	public Bank getBankByBankCode(String bankCode) {
		Tbank tkb = bankDao.get("from Tbank t where t.status=0 and  t.code='" + bankCode + "'");
		if (tkb != null) {
			Bank bk = new Bank();
			BeanUtils.copyProperties(tkb, bk);
			return bk;
		}
		return null;
	}
}
