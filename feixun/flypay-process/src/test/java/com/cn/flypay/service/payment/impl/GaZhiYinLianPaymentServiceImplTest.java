package com.cn.flypay.service.payment.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.gazhiyinlian.GaZhiYinLianUtil;
import com.cn.flypay.utils.gazhiyinlian.entities.BindCardForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.OrderPayForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.QueryCardInfoForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.QueryOrderInfoForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.RateAndCardJChangeForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.RegisterForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.SendSMSForGaZhiYinLian;

/**
 * 嘎吱（银联）通道接口测试类
 * 
 * @author liangchao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class GaZhiYinLianPaymentServiceImplTest {

	/**
	 * 测试文档--商户注册
	 */
	@Test
	public void createMerchant() {
		RegisterForGaZhiYinLian req = new RegisterForGaZhiYinLian();
		req.setTranType("MERREG");
		req.setMerTrace("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		req.setMerName("卢强");
		req.setMerAbbr("卢强简称");
		req.setRateCode(GaZhiYinLianUtil.rateCode);
		req.setIdCardNo("152822199012293814");
		req.setBankAccNo("6216910203870123");
		req.setPhoneno("13816111195");
		req.setBankAccName("卢强");
		req.setBankAccType("2");
		req.setBankName("民生银行");
		req.setBankSubName("民生银行");
		req.setBankCode("305");// 银行代码
		req.setBankAbbr("305"); // 银行代号
		req.setBankChannelNo("20160906ps_cmbc"); // 银行联行号
		req.setBankProvince("河南"); // 省
		req.setBankCity("焦作市"); // 市
		req.setDebitRate("0.002"); // 借记卡费率
		req.setDebitCapAmount("200000000"); // 借记卡封顶
											// (商户借记卡封顶金额填写错误，封顶金额不能低于服务商封顶金额)
		req.setCreditRate("0.002"); // 信用卡费率
		req.setCreditCapAmount("200000000"); // 信用卡封顶
		req.setWithdRate("0.002"); // 提现费率
		req.setWithdSgFee("500"); // 单笔提现手续费 (费用不能低于服务商单笔提现费用)

		String encryptStr = JSON.toJSONString(req);

		JSONObject res = GaZhiYinLianUtil.send(encryptStr);

		System.out.println(res.toJSONString());

	}

	/**
	 * 银联侧绑卡开通
	 */
	@Test
	public void bindCardTest() {

		BindCardForGaZhiYinLian req = new BindCardForGaZhiYinLian();
		req.setTranType("POPNCD");
		req.setMerTrace("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		req.setMerNo("10000818");
		try {
			// 未避免在开通的时候，和微信等开通逻辑生成相同的patt，这里暂停下线程
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		req.setOrderId("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

		req.setRateCode(GaZhiYinLianUtil.rateCode);

		req.setCardNo("4218717016411786"); // 银行卡卡号
		req.setAccountName("卢强");
		req.setCardType("2");
		req.setBankCode("111"); // 银行代码 随便填
		req.setBankAbbr("111"); // 银行代号 随便填
		req.setPhoneno("13816111195");
		req.setCvn2("111"); // 卡背面Cvn2数字 随便填
		req.setExpired("1111"); // 卡有效期 随便填
		req.setCertType("01");
		req.setCertNo("152822199012293814");
		req.setPageReturnUrl("www.baidu.com");
		req.setOfflineNotifyUrl("http://1g83849h98.iask.in:34530/flypayfx/payment/gaZhiYinLianOpenCardNotifyUrl");
		String encryptStr = JSON.toJSONString(req);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		System.out.println(res.toJSONString());
	}

	/**
	 * 卡开通状态查询
	 */
	@Test
	public void queryCardInfo() {
		QueryCardInfoForGaZhiYinLian req = new QueryCardInfoForGaZhiYinLian();
		req.setTranType("OPNCQRY");
		req.setMerNo("10000818");
		req.setMerTrace("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		req.setOrderId("GZYL20180109203410");
		String encryptStr = JSON.toJSONString(req);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		System.out.println(res.toJSONString());
	}

	/**
	 * 发送支付短信
	 */
	@Test
	public void sendSMSForTest() {
		SendSMSForGaZhiYinLian req = new SendSMSForGaZhiYinLian();
		req.setTranType("PAYMSG");
		req.setMerNo("10000818");
		req.setMerTrace("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		try {
			// 未避免在开通的时候，和微信等开通逻辑生成相同的patt，这里暂停下线程
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		req.setOrderId("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		req.setOrderAmount("10000"); // 订单金额以分为单位
		req.setRateCode(GaZhiYinLianUtil.rateCode);

		req.setCardNo("4218717016411786"); // 银行卡号
		req.setAccountName("卢强"); // 银行卡姓名
		req.setCardType("2"); // 1-借记卡2-信用卡
		req.setBankCode("111");
		req.setBankAbbr("111");
		req.setPhoneno("13816111195");
		req.setCertType("01");
		req.setCertNo("152822199012293814"); // 银行预留证件号
		String encryptStr = JSON.toJSONString(req);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		System.out.println(res.toJSONString());
	}

	/**
	 * 消费支付
	 */
	@Test
	public void orderPayTest() {

		OrderPayForGaZhiYinLian req = new OrderPayForGaZhiYinLian();
		req.setTranType("CONPAY");
		req.setMerNo("10000818");
		req.setMerTrace("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		req.setOrderId("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		try {
			// 未避免在开通的时候，和微信等开通逻辑生成相同的patt，这里暂停下线程
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		req.setPayNo("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		req.setPayAmount("10000");
		req.setRateCode(GaZhiYinLianUtil.rateCode);
		req.setCardNo("4218717016411786");
		req.setAccountName("卢强");
		req.setCardType("2");
		req.setBankCode("111");
		req.setBankAbbr("111");
		req.setPhoneno("13816111195");
		req.setCertType("01");
		req.setCertNo("152822199012293814");
		req.setSmsCode("110");
		req.setProductName("线上支付");
		req.setProductDesc("线上支付");
		req.setNotifyUrl("");
		String encryptStr = JSON.toJSONString(req);
		System.out.println("请求参数为" + encryptStr);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		System.out.println(res.toJSONString());
	}

	/**
	 * 支付状态查询
	 */
	@Test
	public void queryOrderInfo() {
		QueryOrderInfoForGaZhiYinLian req = new QueryOrderInfoForGaZhiYinLian();
		req.setTranType("PAYQRY");
		req.setMerNo("10000818");
		req.setMerTrace("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		req.setOrderId("");

		String encryptStr = JSON.toJSONString(req);
		System.out.println("请求参数为" + encryptStr);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		System.out.println(res.toJSONString());
	}

	/**
	 * 商户费率、结算银行卡变更测试
	 */
	@Test
	public void changeRateAndCardJ() {
		RateAndCardJChangeForGaZhiYinLian req = new RateAndCardJChangeForGaZhiYinLian();
		req.setTranType("MERCHG");
		req.setMerNo("10714811");
		req.setMerTrace("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		req.setChangeType("4");
		// req.setRateCode(GaZhiYinLianUtil.rateCode);
		// req.setDebitRate("0.012");
		// req.setDebitCapAmount("20000000000");
		// req.setCreditRate("0.012");
		// req.setCreditCapAmount("20000000000");
		req.setWithdRate("0");
		req.setWithdSgFee("200");
		String encryptStr = JSON.toJSONString(req);
		System.out.println("请求参数为" + encryptStr);
		JSONObject res = GaZhiYinLianUtil.send(encryptStr);
		System.out.println("结果=" + res.toJSONString());

	}

}
