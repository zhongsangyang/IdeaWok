package com.cn.flypay.utils.pingan;

import org.junit.Test;

public class PinganPaymentUtilTest {

	@Test
	public void testIsValidSign() {
		String content = "trade_no=2016121314530401745946182679&out_trade_no=20161213145304&open_id=JUnzFblxTtbjIC1%252FM%252FABhX33lOPscwXXafcKJESNFvE4DBvp3D%252BX824bsYylb7vhkRa7LbUFK8AKR9rqdspQew%253D%253D&desc=%E6%94%AF%E4%BB%98%E6%88%90%E5%8A%9F&status=0&pay_time=2016-12-13+14%3A56%3A50&total_fee=0.0100&promotion_amount=0&bank_commission_fee=0.00000&bank_commission_rate=0.00050&liquidator_commission_fee=0.00000&liquidator_commission_rate=0.00350&pay_platform_fee=0.00000&pay_platform_rate=0.00200&sign=It4chD27CBgeGkaX5a4ZyfVCLB%2FMfbpdFI7CAKcRaYwtXSNxHpkOLME3k8w4njCD9AyptmUHdSOhGBNcCAScdDptCgQt%2FqjjSqDmEfPNLhzVIKJKqgoWpLSNjJttmhWpwb%2BdUsbU68LWbv237i7zOmYelWAuC6hKXd4OnkX1iN8%3D&sign_type=RSA";
		Boolean bl = PinganPaymentTestUtil.isValidSign(content);
		System.out.println(bl);
	}
}
