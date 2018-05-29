package com.cn.flypay.service.sys;

import java.util.List;

import com.cn.flypay.model.sys.Tbank;
import com.cn.flypay.pageModel.sys.Bank;

public interface BankService {

	List<Bank> findBankList();

	Tbank getBank(Long bankId);

	Bank getBankByBankCode(String bankCode);
}
