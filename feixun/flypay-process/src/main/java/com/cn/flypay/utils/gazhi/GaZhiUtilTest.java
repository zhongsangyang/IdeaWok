package com.cn.flypay.utils.gazhi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GaZhiUtilTest {
	public static void main(String[] args) {
		new GaZhiUtilTest().demoDraw();
	}
	
	/**
	 * 统一下单交易
	 * 2017年5月18日 下午3:15:06
	 * @author jian.yang
	 *
	 * @return
	 */
	public String demoPay() {
		Map<String, String> map = new HashMap<String, String>();
		/**
		 * 必传参数
		 */
		map.put("tradeType", "API_WXQRCODE");	//支付类型
		map.put("orgCode", "29003001");		//机构代码
		map.put("tranType", "PAY");		//交易码
		map.put("merNo", "999002100009898");	//商户号
		map.put("merTrace", "12345678903211333");	//商户流水
		map.put("amount", "000000000010");		//交易金额
		map.put("orderInfo", "iphone8s");		//订单信息
		map.put("transDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));		//交易日期
		map.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));		//交易时间
		map.put("merUrl", "http://flipped1212.iok.la:21360/flypayfx/gazhiChannel/asynchronousNotification");	//通知地址	
		map.put("clientIP", "127.0.0.1");		//客户端IP

		try {
			Map<String, String> a = GaZhiUtil.execute(map);
			System.out.println(a);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 订单查询交易
	 * 2017年5月18日 下午3:15:06
	 * @author jian.yang
	 *
	 * @return
	 */
	public String demoOrderQuery() {
		Map<String, String> map = new HashMap<String, String>();
		/**
		 * 必传参数
		 */
		map.put("tranType", "QUERY");     //交易码  定值
		map.put("orgCode", "29003001");   //机构代码	定值
		map.put("merNo", "999002100009898");//商户号
		map.put("merTrace", "12345678903211334321");//商户订单流水号
		map.put("amount", "000000000010");//交易金额
		map.put("transDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));   //交易日期
		map.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));//交易时间
		map.put("oldMerTrace", "12345678903211333123");//原交易订单流水号
		map.put("tradeType", "API_WXQRCODE");//原交易类型
		map.put("merUrl", "http://163.53.90.118/pilot/notify");

		try {
			Map<String, String> a = GaZhiUtil.execute(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	
	/**
	 * 余额查询交易
	 * 2017年5月18日 下午3:15:06
	 * @author jian.yang
	 *
	 * @return
	 */
	public String demoBalQuery() {
		Map<String, String> map = new HashMap<String, String>();
		/**
		 * 必传参数
		 */
		map.put("tranType", "BALQRY");     //交易码 定值
		map.put("orgCode", "29003001");   //机构代码  定值
		map.put("merNo", "999002100009898");//商户号
		map.put("merTrace", "12345678903211334321");//商户订单流水号
		map.put("transDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));   //交易日期
		map.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));//交易时间

		try {
			Map<String, String> a = GaZhiUtil.execute(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 提现手续费查询交易
	 * 2017年5月18日 下午3:15:06
	 * @author jian.yang
	 *
	 * @return
	 */
	public String demoFeeQuery() {
		Map<String, String> map = new HashMap<String, String>();
		/**
		 * 必传参数
		 */
		map.put("tranType", "FEEQRY");     //交易码  定值
		map.put("orgCode", "29003001");   //机构代码  定值
		map.put("merNo", "999002100009898");//商户号  15位
		map.put("merTrace", "12345678903211334321");//商户订单流水号
		map.put("transDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));   //交易日期
		map.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));//交易时间
		map.put("amount", "000000001000");//提现金额

		try {
			Map<String, String> a = GaZhiUtil.execute(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 提现交易
	 * 2017年5月18日 下午3:15:06
	 * @author jian.yang
	 *
	 * @return
	 */
	public String demoDraw() {
		Map<String, String> map = new HashMap<String, String>();
		/**
		 * 必传参数
		 */
		map.put("tranType", "DRAW");     //交易码  定值
		map.put("orgCode", "29003001");   //机构代码  定值
		map.put("merNo", "999002100009898");//商户号
		map.put("merTrace", "12345678903211334321");//商户订单流水号
		map.put("transDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));   //交易日期
		map.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));//交易时间
		map.put("tranAmt", "000000001000");//提现金额
		map.put("factAmt", "000000001000");//入账金额
		map.put("feeAmt", "000000001000");//手续费金额

		try {
			Map<String, String> a = GaZhiUtil.execute(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 提现结果查询交易
	 * 2017年5月18日 下午3:15:06
	 * @author jian.yang
	 *
	 * @return
	 */
	public String demoDrawQuery() {
		Map<String, String> map = new HashMap<String, String>();
		/**
		 * 必传参数
		 */
		map.put("tranType", "TKQRY");     //交易码	定值
		map.put("orgCode", "29003001");   //机构代码  定值
		map.put("merNo", "999002100009898");//商户号
		map.put("merTrace", "12345678903211334321");//商户订单流水号
		map.put("transDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));   //交易日期
		map.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));//交易时间
		map.put("oldMerTrace", "1234567890321131");//提款流水号
		map.put("oldTransDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));   //提款日期
		map.put("oldTransTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));//提款时间

		try {
			Map<String, String> a = GaZhiUtil.execute(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	
	
	
	
}
