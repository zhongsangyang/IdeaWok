package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.gazhi.GaZhiUtil;
/**
 * 嘎吱通道相关操作
 * @author liangchao
 *
 */
@Service(value="gaZhiPaymentService")
public class GaZhiPaymentServiceImpl extends AbstractChannelPaymentService{
	private Log log = LogFactory.getLog(getClass());
	
	//嘎吱配置的机构代码
	private static String organization_code = "29003002"; //嘎吱提供的正式环境测试参数
	
	//嘎吱配置的商户号
	//private static String merchant_code = "528531006700089"; //嘎吱提供的正式环境测试参数
	
	//嘎吱配置的异步返回信息接受地址
	private static String callBack = "http://flipped1212.iok.la:21360/flypayfx/gazhiChannel/asynchronousNotification";
	
	//当前服务器IP地址
	private static String ip = "flipped1212.iok.la";	//嘎吱后端暂时未对客户端ip做处理，所以此处随意  by 2017-08-22
	
	@Autowired
	private CommonService commonService;
	
	/**
	 * 创建嘎吱的订单，并生成二维码
	 */
	@Override
	public Map<String, String> createUnifiedOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, Integer angentType, String desc) throws Exception {
		log.info("进程--创建嘎吱的订单，并生成二维码-- start");
		Map<String, String> reqData = new HashMap<String,String>();
		
		/* 配置的嘎吱支付参数 */
		JSONObject channelConfig = cpr.getConfig();
		
		//商户号
		String merchant_code = channelConfig.getString("merchant_code");
		//String merchant_code = "528531006700089";
		
		
		try {
			//拼接参数
			log.info("拼接请求参数----start");
			//交易码
			reqData.put("tranType", "PAY");
			//支付类型
			String tradeType ="";
			Integer orderType = cpr.getChannel().getType();
			if(orderType - UserOrder.trans_type.ALQR.getCode()==0 ){
				//当订单类型为 “支付宝_二维码”时，嘎吱的请求参数-支付类型为 “支付宝正扫”
				tradeType = "API_ZFBQRCODE";
			}
			if(orderType - UserOrder.trans_type.WXQR.getCode()==0 ){
				//当订单类型为 “微信_二维码”时，嘎吱的请求参数-支付类型为 “微信正扫”
				tradeType = "API_ZFBQRCODE";
			}
			reqData.put("tradeType", tradeType);
			
			//商户流水（20位定长商户流水号，商户须保证流水唯一）  用户订单号yyMMddHHmmss(12)+8位随机数 20位
			String merTrace = commonService.getUniqueTradeSn();
			reqData.put("merTrace", merTrace);
			
			//机构代码
			reqData.put("orgCode", organization_code);
			//商户号
			reqData.put("merNo", merchant_code);
			
			//交易金额	以分为单位
			BigDecimal moneyB = new BigDecimal(money.toString());
			BigDecimal num100 = new BigDecimal(100);
			String res = moneyB.multiply(num100).toString();
			reqData.put("amount", res.substring(0,res.indexOf(".")));	//去掉转化为分后， 小数点后面的数
			//订单信息   不超过32位 
			reqData.put("orderInfo", desc);
			//当前交易日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String transDate = sdf.format(new Date());
			reqData.put("transDate", transDate);
			//交易时间
			reqData.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
			//异步通知地址
			reqData.put("merUrl", callBack);
			//客户端IP
			reqData.put("clientIP", ip);
			//拼接参数  end
			log.info("拼接请求参数----end");
		} catch (Exception e) {
			log.error("拼接请求参数----异常",e);
			return null;
		}
		
		
		try {
			//向嘎吱服务商发送请求
			Map<String,String> result = GaZhiUtil.execute(reqData);
			log.info("接口返回的参数为:"+result);
			if(result !=null && result.containsKey("respCode")){
				if(StringUtil.isNotBlank(result.get("respCode")) && result.get("respCode").equals("00")){
					//请求嘎吱处理成功
					log.info("请求嘎吱服务器成功");
					//创建支付订单
					userOrderService.createTransOrder(user.getId(),reqData.get("merTrace"),null,null, cpr.getChannel().getType(),
							money,UserOrder.cd_type.D.name(),null,null,desc,transPayType, cpr.getChannel(),inputAccType,angentType);
					//声明结果状态
					result.put("return_code", "SUCCESS");
					//声明二维码
					result.put("code_url", result.get("payInfo"));
					log.info("返回的二维码链接为 "+result.get("payInfo"));
					return result;
				}else{
					log.info("嘎吱返回结果为失败: err_code is "+ result.get("respCode") + ", respMsg is "+result.get("respMsg"));
				}
			}else{
				log.error("请求嘎吱服务器失败，无数据返回");
			}
		} catch (Exception e) {
			log.error("进程--创建嘎吱的订单，并生成二维码----出现异常:",e);
			throw e;
		}
		log.info("进程--创建嘎吱的订单，并生成二维码-- end");
		return null;
	}
	
	/**
	 * 根据订单号，向嘎吱通道发送请求查询订单状态信息
	 */
	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {
		log.info("进程--向嘎吱通道发送请求查询订单状态信息 ---start");
		Map<String,String> result = new HashMap<String,String>();
		
		//查询订单号对应的订单
		UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNum);
		
		
		if (userOrder != null) {
			/* 配置的嘎吱支付参数 */
			JSONObject channelConfig = channelService.getChannelConfig(userOrder.getChannelId());
			//商户号
			String merchant_code = channelConfig.getString("merchant_code");
			
			Map<String, String> reqData = new HashMap<String,String>();
			try {
				log.info("拼接接口查询参数---start");
				//交易码
				reqData.put("tranType", "QUERY");
				//机构代码
				reqData.put("orgCode", organization_code);
				//商户号
				reqData.put("merNo", merchant_code);
				//商户流水（20位定长商户流水号，商户须保证流水唯一）  用户订单号yyMMddHHmmss(12)+8位随机数 20位
				String merTrace = commonService.getUniqueTradeSn();
				reqData.put("merTrace", merTrace);
				//当前查询日期
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String transDate = sdf.format(new Date());
				reqData.put("transDate", transDate);
				//查询时间
				reqData.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
				//原交易流水号
				reqData.put("oldMerTrace", orderNum);
				//原交易的订单类型，（支付宝正扫、微信正扫等）
				if(userOrder.getType() -UserOrder.trans_type.ALQR.getCode() ==0){
					reqData.put("tradeType", "API_ZFBQRCODE");
				}
				if(userOrder.getType() -UserOrder.trans_type.WXQR.getCode() ==0){
					reqData.put("tradeType", "API_WXQRCODE");
				}
				
				//for test
				reqData.put("tradeType", "API_ZFBQRCODE");
				
				log.info("拼接接口查询参数---end");
			} catch (Exception e) {
				log.error("拼接接口查询参数---出现异常",e);
				return null;
			}
			
			//向嘎吱服务商发送请求
			try {
				result = GaZhiUtil.execute(reqData);
				log.info("向嘎吱通道发送请求查询订单状态信息--返回结果为   --: "+result);
				PayOrder payOrder = new PayOrder();
				if(result !=null && result.containsKey("respCode")){
					if(StringUtil.isNotBlank(result.get("respCode")) && result.get("respCode").equals("00")){
						//请求嘎吱处理成功
						log.info("---请求嘎吱服务器成功-- ，返回结果为：" + result);
						//判断处理结果
						Boolean rechangeFlag = false;
						if(result.get("tradeStatus").equals("A")){
							//支付成功
							payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
							rechangeFlag = true;
						}else if(result.get("tradeStatus").equals("E")){
							//订单失败
							payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
							rechangeFlag = true;
						}else if(result.get("tradeStatus").equals("R")){
							//原订单成功，未支付（待支付）
							log.info("订单信息返回成功，订单状态为“待支付” 状态, 订单号为 " + orderNum + ",请稍后重试");
						}
						
						if(rechangeFlag){
							//开始保存订单最新状态
							try {
								log.info("查询之后，开始保存订单信息");
								userOrderService.finishInputOrderStatus(orderNum, payOrder);
								//声明结果状态
								result.put("return_code", "SUCCESS");
								log.info("保存订单信息成功");
								return result;
							} catch (Exception e) {
								log.error("----嘎吱保存订单查询结果异常",e);
							}
						}
					}else{
						//请求嘎吱返回处理失败
						log.info("嘎吱返回处理结果，为失败，返回显示原因为 :"+result.get("respMsg") );
						//嘎吱处理失败 未查询到结果信息
						payOrder.setErrorInfo(result.get("respMsg"));
						payOrder.setErrorCode(result.get("respCode"));
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
						//开始保存订单最新状态
						try {
							log.info("开始保存订单的失败信息--start");
							userOrderService.finishInputOrderStatus(orderNum, payOrder);
							log.info("开始保存订单的失败信息--end");
							return result;
						} catch (Exception e) {
							log.error("----嘎吱接口显示处理失败，保存当前订单查询结果异常",e);
						}
						
					}
				}else{
					//无法连接嘎吱
					log.error("连接嘎吱服务器异常");
				}
			} catch (Exception e) {
				log.error("进程--向嘎吱通道发送请求查询订单状态信息 ---进程出现异常",e);
			}
		}else{
			//订单不存在，或者已经被成功处理
			log.info("订单 " + orderNum + "不存在、或者已经被处理");
		}
		log.info("进程--向嘎吱通道发送请求查询订单状态信息 --- end");
		return result;
	}
	
	/**
	 * 查询嘎吱商户余额
	 * 
	 */
	@Override
	public Map<String, String> queryGaZhiBalance(String merNo){
		log.info("进程--查询嘎吱商户余额 --- start");
		Map<String,String> result = new HashMap<String,String>();
		//拼接请求参数
		Map<String, String> reqData = new HashMap<String,String>();
		
		
		try {
			log.info("拼接嘎吱请求参数---start");
			//请求交易码
			reqData.put("tranType", "BALQRY");
			//机构代码
			reqData.put("orgCode", organization_code);
			//商户号
			reqData.put("merNo", merNo);
			//商户流水（20位定长商户流水号，商户须保证流水唯一）用户订单号yyMMddHHmmss(12)+8位随机数 20位
			String merTrace = commonService.getUniqueTradeSn();
			reqData.put("merTrace", merTrace);
			//当前查询日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String transDate = sdf.format(new Date());
			reqData.put("transDate", transDate);
			//查询时间
			reqData.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
			log.info("拼接嘎吱请求参数---end");
		} catch (Exception e) {
			log.error("拼接嘎吱请求参数--异常",e);
			return null;
		}
		
		try {
			result = GaZhiUtil.execute(reqData);
			log.info("查询嘎吱商户余额--接口返回信息为: "+result);
			if(result !=null && result.containsKey("respCode")){
				log.error("连接嘎吱服务器--成功 ");
				//有返回结果
				if(StringUtil.isNotBlank(result.get("respCode")) && result.get("respCode").equals("00")){
					log.error("查询嘎吱商户余额--成功 ");
					//处理请求成功
					//声明结果状态
					result.put("return_code", "SUCCESS");
					return result;
				}else{
					log.error("查询嘎吱商户余额--接口返回信息为失败, 接口显示原因为"+ result.get("respMsg") );
					result.put("return_code", "FAIL");
				}
			}else{
				//无法连接嘎吱
				log.error("连接嘎吱服务器--失败 ");
			}
			
		} catch (Exception e) {
			log.error("查询嘎吱商户余额--进程出现异常:",e);
		}
		
		log.info("进程--查询嘎吱商户余额 --- end");
		return result;
	}
	
	
	/**
	 * 查询商户提款手续费查询
	 * @param merNo	嘎吱商户号
	 * @param amount 提现金额
	 * @return
	 */
	@Override
	public Map<String, String> queryGaZhiWithdrawFee(String merNo,String amount){
		log.info("进程--查询商户提款手续费查询 --- start");
		log.info("gazhi query Withdraw Fee process start");
		Map<String,String> result = new HashMap<String,String>();
		if(StringUtil.isBlank(merNo) || StringUtil.isBlank(amount)){
			log.error("查询商户提款手续费查询 请求参数为空");
			return null;
		}
		
		//拼接请求参数
		Map<String, String> reqData= new HashMap<String,String>();
		try {
			log.info("开始拼接访问嘎吱接口的请求参数----start");
			
			//请求交易码
			reqData.put("tranType", "FEEQRY");
			//机构代码
			reqData.put("orgCode", organization_code);
			//商户号
			reqData.put("merNo", merNo);
			//提款金额
			reqData.put("amount", amount);
			//商户流水（20位定长商户流水号，商户须保证流水唯一）用户订单号yyMMddHHmmss(12)+8位随机数 20位
			String merTrace = commonService.getUniqueTradeSn();
			reqData.put("merTrace", merTrace);
			//当前查询日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String transDate = sdf.format(new Date());
			reqData.put("transDate", transDate);
			//查询时间
			reqData.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
			log.info("拼接访问嘎吱接口的请求参数----end");
		} catch (Exception e) {
			log.error("进程--查询商户提款手续费查询--拼接访问嘎吱请求接口参数异常:",e);
			return null;
		}
		//拼接参数  end
		try {
			result = GaZhiUtil.execute(reqData);
			log.info("嘎吱接口返回的参数为---: "+result);
			if(result != null && result.containsKey("respCode") ){
				//有返回结果
				if(StringUtil.isNotBlank(result.get("respCode")) && result.get("respCode").equals("00")){
					//处理请求成功
					log.info("嘎吱返回数据显示处理成功");
					result.put("result_code", "SUCCESS");
					return result;
				}else{
					//返回结果为处理失败
					log.info("嘎吱返回数据显示处理失败");
					result.put("result_code", "FAIL");
					return result;
				}
				
			}else{
				//无法连接嘎吱
				log.error("连接嘎吱服务器失败");
			}
			
		} catch (Exception e) {
			log.error("请求嘎吱接口-----进程出现异常：",e);
		}
		log.info("进程--查询商户提款手续费查询 --- end");
		return result;
	}
	
	/**
	 * 嘎吱商户提款
	 * @param merNo 商户号
	 * @param tranAmt 提款金额 根据查询交易的返回值获取
	 * @param factAmt 入账金额 根据查询交易的返回值获取
	 * @param feeAmt 手续费
	 * @return
	 */
	@Override
	public Map<String,String> drawGaZhi(String merNo,String tranAmt,String factAmt,String feeAmt){
		log.info("进程-- 嘎吱商户提款--- start");
		Map<String,String> result = new HashMap<String,String>();
		if(StringUtil.isBlank(merNo) || StringUtil.isBlank(tranAmt) || StringUtil.isBlank(factAmt) || StringUtil.isBlank(feeAmt)){
			log.error("进程-- 嘎吱商户提款--- 接口请求参数为空");
			return null;
		}
		//拼接请求参数
		Map<String, String> reqData = new HashMap<String,String>();
		try {
			log.info("开始拼接访问嘎吱接口的请求参数----start");
			//请求交易码
			reqData.put("tranType", "DRAW");
			//机构代码
			reqData.put("orgCode", organization_code);
			//商户号
			reqData.put("merNo", merNo);
			//提款金额
			reqData.put("tranAmt", tranAmt);
			//入账金额
			reqData.put("factAmt", factAmt);
			//手续费
			reqData.put("feeAmt", feeAmt);
			//商户流水（20位定长商户流水号，商户须保证流水唯一）用户订单号yyMMddHHmmss(12)+8位随机数 20位
			String merTrace = commonService.getUniqueTradeSn();
			reqData.put("merTrace", merTrace);
			//当前查询日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String transDate = sdf.format(new Date());
			reqData.put("transDate", transDate);
			//查询时间
			reqData.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
			//拼接参数  end
			log.info("开始拼接访问嘎吱接口的请求参数----end");
		} catch (Exception e) {
			log.error("进程--嘎吱商户提款--拼接访问嘎吱请求接口参数异常:",e);
			return null;
		}
		
		
		try {
			result = GaZhiUtil.execute(reqData);
			log.info("请求嘎吱接口返回信息为: "+result);
			if(result != null && result.containsKey("respCode") ){
				
				//有返回结果
				if(StringUtil.isNotBlank(result.get("respCode")) && result.get("respCode").equals("00")){
					log.info("嘎吱接口返回信息显示--处理成功");
					result.put("result_code", "SUCCESS");
					return result;
				}else{
					//返回结果为处理失败
					log.info("嘎吱接口返回信息显示--处理失败");
					result.put("result_code", "FAIL");
					return result;
				}
			}else{
				//无法连接嘎吱
				log.error("连接嘎吱服务器---失败");
			}
		} catch (Exception e) {
			log.error("gazhi channel draw error",e);
		}
		log.info("进程-- 嘎吱商户提款--- end");
		return result;
	}
	
	/**
	 * 嘎吱商户提款结果查询
	 * @param merNo 商户号
	 * @param oldMerTrace 提款流水号
	 * @param oldTransDate 提款日期 yyyyMMdd 
	 * @param oldTransTime 提款日期 yyyyMMddHHmmssSSS 
	 * @return
	 */
	@Override
	public Map<String,String> queryGaZhiDrawResult(String merNo,String oldMerTrace,String oldTransDate,String oldTransTime){
		log.info("进程-- 嘎吱商户提款结果查询--- start");
		Map<String,String> result = new HashMap<String,String>();
		//拼接请求参数 start 
		Map<String, String> reqData = new HashMap<String,String>();
		
		try {
			log.info("拼接请求参数----开始");
			//请求交易码
			reqData.put("tranType", "TKQRY");
			//机构代码
			reqData.put("orgCode", organization_code);
			//商户号
			reqData.put("merNo", merNo);
			//商户流水（20位定长商户流水号，商户须保证流水唯一）用户订单号yyMMddHHmmss(12)+8位随机数 20位
			String merTrace = commonService.getUniqueTradeSn();
			reqData.put("merTrace", merTrace);
			//当前查询日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String transDate = sdf.format(new Date());
			reqData.put("transDate", transDate);
			//查询时间
			reqData.put("transTime", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
			//提款流水号
			reqData.put("oldMerTrace", oldMerTrace);
			//提款日期
			reqData.put("oldTransDate", oldTransDate);
			//提款时间
			reqData.put("oldTransTime", oldTransTime);
			
			log.info("拼接请求参数----end");
		} catch (Exception e) {
			log.error("拼接请求参数---异常:",e);
			return null;
		}
		
		
		try {
			result = GaZhiUtil.execute(reqData);
			log.info("调用嘎吱接口返回信息为: "+result);
			if(result != null && result.containsKey("respCode")){
				//有返回结果
				if(StringUtil.isNotBlank(result.get("respCode")) && result.get("respCode").equals("00")){
					log.info("嘎吱服务器返回信息显示---处理成功");
					result.put("result_code", "SUCCESS");
					return result;
				}else{
					//返回结果为处理失败
					log.info("嘎吱服务器返回信息显示---处理失败");
					result.put("result_code", "FAIL");
					return result;
				}
			}else{
				//无法连接嘎吱
				log.error("返回信息显示，请求嘎吱服务器失败");
				
			}
		} catch (Exception e) {
			log.error("gazhi channel queryDrawResult error",e);
		}
		log.info("进程---嘎吱商户提款结果查询--- end");
		return result;
	}
	
}
