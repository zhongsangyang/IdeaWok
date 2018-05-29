package com.cn.flypay.controller.payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.model.payment.response.PaymentResult;
import com.cn.flypay.model.sys.TchannelT0Tixian;
import com.cn.flypay.model.sys.TuserMerchantReport;
import com.cn.flypay.model.sys.TweiLianBaoMerchantReport;
import com.cn.flypay.model.trans.TpinganFileDeal;
import com.cn.flypay.model.util.JSON;
import com.cn.flypay.pageModel.sys.SysAsynchronousReturnInfo;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.GaZhiYinLainService;
import com.cn.flypay.service.payment.PingAnExpenseService;
import com.cn.flypay.service.payment.TroughTrainServeice;
import com.cn.flypay.service.payment.WeiLianBaoYinLainService;
import com.cn.flypay.service.payment.YiBaoService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.ChannelT0TixianService;
import com.cn.flypay.service.sys.SysAsynchronousReturnInfoService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserMerchantReportService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.MD5Util;
import com.cn.flypay.utils.SignUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.gazhi.GaZhiXml2MapUtil;
import com.cn.flypay.utils.gazhiyinlian.GaZhiYinLianUtil;
import com.cn.flypay.utils.minsheng.MinShengMerchantInputMinShengUtil;
import com.cn.flypay.utils.pingan.PingAnConfig;
import com.cn.flypay.utils.pingan.PinganPaymentUtil;
import com.cn.flypay.utils.quantong.AESUtil;
import com.cn.flypay.utils.quantong.QuanTongPaymentUtil;
import com.cn.flypay.utils.shenfu.ApplicationBase;
import com.cn.flypay.utils.shenfu.RSAUtil;
import com.cn.flypay.utils.transfar.ParamUtil;
import com.cn.flypay.utils.weilianbao.WeiLianBaoSignUtil;
import com.cn.flypay.utils.yibao.Digest;
import com.cn.flypay.utils.yibao.YiBaoBaseUtil;
import com.cn.flypay.utils.yilian.YiLianYlzxUtil;
import com.cn.flypay.utils.yiqiang.YiQiangCallbackDto;
import com.cn.flypay.utils.yiqiang.YiQiangPayUtil;
import com.cn.flypay.utils.yiqiang2.YiQiang2PayUtil;
import com.cn.flypay.utils.zanshanfu.ZanshanfuPayUtil;
import com.cn.flypay.utils.zheyang.ZheYangUtil;
import com.rd.util.MerchantUtil;

@Controller
@RequestMapping("/payment")
public class PaymentController {
	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserOrderService userOrderService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private PingAnExpenseService pingAnPayService;
	@Autowired
	private ChannelT0TixianService channelT0TixianService;
	@Autowired
	ChannelService channelService;
	@Autowired
	private SysAsynchronousReturnInfoService sysAsynchronousReturnInfoService;
	@Autowired
	private UserMerchantReportService userMerchantReportService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ChannelPaymentService yiBaoPaymentService;
	@Autowired
	private UserService userService;
	@Autowired
	private YiBaoService yiBaoService;
	@Autowired
	private GaZhiYinLainService gaZhiYinLainService;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private TroughTrainServeice troughTrainServeice;
	@Autowired
	private WeiLianBaoYinLainService weiLianBaoYinLainService;

	@RequestMapping(value = "/alipayNotify")
	public void alipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("---支付宝notify start---");
		Map<String, String[]> requestParams = request.getParameterMap();
		LOG.info(XmlMapper.map2Xml(requestParams, false));
		String trade_status = requestParams.get("trade_status")[0];
		if ("TRADE_SUCCESS".equals(trade_status)) {

			String out_trade_no = requestParams.get("out_trade_no")[0];
			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(out_trade_no);
			if (userOrder != null) {
				PayOrder payOrder = new PayOrder();

				payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(requestParams.get("total_amount")[0])));
				payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(requestParams.get("receipt_amount")[0])));
				String time_end = requestParams.get("gmt_payment")[0];

				String transaction_id = requestParams.get("trade_no")[0];
				payOrder.setPayNo(transaction_id);
				payOrder.setPayFinishDate(time_end);

				payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
				try {
					userOrderService.finishInputOrderStatus(out_trade_no, payOrder);
				} catch (Exception e) {
					LOG.error("----支付宝订单回调异常", e);
				}

			} else {
				LOG.info("支付宝订单：" + out_trade_no + " 不存在或已经被成功处理了");
			}
		} else {
			LOG.error("支付通信失败！");
		}
		LOG.info("---支付宝 notify end----");

	}

	@RequestMapping("/pinganFeedback")
	@ResponseBody
	public String pinganFeedback(HttpServletRequest request, @RequestHeader("user-agent") String ua) {
		// 返回客户端地址
		try {
			LOG.info(ua);
			String requestString = commonService.getBodyFromRequst(request, PingAnConfig.encoding);
			LOG.info(requestString);// 返回请求数据
			if (StringUtil.isNotBlank(requestString)) {
				String head = requestString.substring(0, requestString.indexOf("<?xml"));
				try {
					String result = requestString.substring(93, 143).trim();
					if (result.contains("成功")) {
						String body = requestString.substring(requestString.indexOf("<Result>"));

						TpinganFileDeal fileDeal = pingAnPayService.dealFile04FeedBack(body);
						if (fileDeal != null) {
							return pingAnPayService.generateFile04ReturnSuccessBody(fileDeal);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				String ss = head + "<?xml version=\"1.0\" encoding=\"GBK\"?><Result></Result>";
				LOG.info(ss);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	@RequestMapping("/wxqrnotify")
	@ResponseBody
	public String wxqrnotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("---微信订单回调接口 start---------");
		try {
			String xml = commonService.getBodyFromRequst(request);
			LOG.info(xml);
			Map<String, String> map = XmlMapper.xml2Map(xml);
			String sign = map.remove("sign");
			if (SignUtil.getSign(map, (String) ApplicatonStaticUtil.getAppStaticData("wxaccount.appPaySecret")).equals(sign)) {
				String returnCode = map.get("return_code");
				if ("SUCCESS".equals(returnCode)) {
					String out_trade_no = map.get("out_trade_no");
					UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(out_trade_no);
					if (userOrder != null) {
						PayOrder payOrder = new PayOrder();

						String resultCode = map.get("result_code");
						String outTradeNo = map.get("out_trade_no");

						int total_fee = Integer.parseInt(map.get("total_fee"));
						payOrder.setPayAmt(BigDecimal.valueOf(total_fee / 100));
						int settlementTotalFee = 0;
						if (map.containsKey("settlement_total_fee")) {
							settlementTotalFee = Integer.parseInt(map.get("settlement_total_fee"));
							payOrder.setRealAmt(BigDecimal.valueOf(settlementTotalFee / 100));
						}
						String time_end = map.get("time_end");

						String transaction_id = map.get("transaction_id");
						payOrder.setPayNo(transaction_id);

						payOrder.setPayFinishDate(time_end);

						if ("SUCCESS".equals(resultCode)) {
							payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());

							/*
							 * try { Map<String, String> params = new
							 * HashMap<String, String>(); params.put("title",
							 * "微信二维码收款成功"); params.put("orderNum",
							 * userOrder.getOrderNum()); params.put("content",
							 * "微信成功收款" + userOrder.getAmt());
							 * params.put("createTime",
							 * DateUtil.getDateTime("yyyyMMddHHmmss", new
							 * Date()));
							 * jiguangPushService.sendToPersionWhenPayFinish
							 * (userOrder.getUserId(), params); } catch
							 * (Exception e) { logger.error("----发送收款通知异常", e);
							 * }
							 */
						} else {
							String return_msg = map.get("return_msg");
							payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
							payOrder.setErrorInfo(return_msg);
							String errCode = map.get("err_code");
							LOG.error("支付失败！out_trade_no:" + outTradeNo + ",result_code:" + resultCode + ", err_code:" + errCode);
						}
						try {
							userOrderService.finishInputOrderStatus(out_trade_no, payOrder);
						} catch (Exception e) {
							LOG.error("----微信订单回调异常", e);
						}

					} else {
						LOG.info("微信订单：" + out_trade_no + " 不存在或已经被成功处理了");
					}

				} else {
					LOG.error("支付通信失败！");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----微信订单回调异常", e);
		}
		LOG.info("---微信订单回调接口 end---------");
		String returnStr = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";

		PrintWriter out = null;
		try {
			response.reset();
			response.setContentType("text/xml");
			out = response.getWriter();
			out.println(returnStr.toString());
			out.flush();

		} catch (Exception e) {
			LOG.error("----微信订单回调反馈异常", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					LOG.error("----微信订单回调反馈异常", e);
				}
			}
		}
		return returnStr;
	}

	@RequestMapping("/xinkeNotify")
	@ResponseBody
	public String xinkeNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("---欣客订单回调接口 start---------");
		try {
			String jsonStr = commonService.getBodyFromRequst(request);
			LOG.info(jsonStr);
			if (StringUtil.isNotBlank(jsonStr)) {
				JSONObject json = JSONObject.parseObject(jsonStr);
				if (json.containsKey("order_id") && StringUtil.isNotBlank(json.getString("order_id"))) {
					String returnCode = json.getString("rep_code");
					String out_trade_no = json.getString("order_id");
					if ("000000".equals(returnCode)) {
						UserOrder userOrder = userOrderService.findTodoUserOrderByPayNum(out_trade_no);
						if (userOrder != null) {
							PayOrder payOrder = new PayOrder();
							payOrder.setPayFinishDate(DateUtil.convertCurrentDateTimeToString());
							payOrder.setFinishDate(new Date());
							String pay_status = json.getString("pay_status");
							boolean isChange = false;
							if ("S".equals(pay_status)) {
								payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
								isChange = true;
							} else if ("F".equals(pay_status)) {
								String desc = json.getString("desc");
								payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
								payOrder.setErrorInfo(desc);
								LOG.error("支付失败！out_trade_no:" + out_trade_no + ", err_code:" + desc);
								isChange = true;
							}
							try {
								if (isChange) {
									userOrderService.finishInputOrderStatus(userOrder.getOrderNum(), payOrder);
								}
							} catch (Exception e) {
								LOG.error("----欣客订单回调异常", e);
							}

						} else {
							LOG.info("欣客订单：" + out_trade_no + " 不存在或已经被成功处理了");
						}

					} else {
						LOG.error("支付通信失败！");
					}
					JSONObject returnJson = new JSONObject();
					returnJson.put("status", "OK");
					returnJson.put("order_id", out_trade_no);
					LOG.info("---欣客订单回调接口 end---------");
					return returnJson.toJSONString();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----欣客订单回调异常", e);
		}
		LOG.info("---欣客订单回调接口 end---------");
		return null;
	}

	/**
	 * 嘎吱银联侧绑卡异步回调通知
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/gaZhiYinLianOpenCardNotifyUrl")
	@ResponseBody
	public String gaZhiOpenCardNotifyUrl(@RequestBody YiQiangCallbackDto req, HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 由于嘎吱侧绑卡的回调返回的信息只是开通时的请求订单号，并无实际的卡信息。
		// 并且，开通绑卡时的订单号也不方便保存，用户再次下单进行绑卡时，又会返回卡的开通状态，所以该异步回调只作为报文信息保留，对卡状态不做处理。
		LOG.info("--gaZhiYinLianOpenCardNotifyUrl URL---");
		String orgCode = req.getOrgCode();
		String sign = req.getSign();
		String body = req.getBody();
		LOG.info("--gaZhiYinLianOpenCardNotifyUrl orgCode={},body={},sign={}", orgCode, body, sign);
		JSONObject bodyJson = GaZhiYinLianUtil.decrypt(body);
		LOG.info("--gaZhiYinLianOpenCardNotifyUrl bodyJson={}---", bodyJson.toJSONString());
		return "SUCCESS";
	}

	@RequestMapping("/pinganPayNotify")
	@ResponseBody
	public String pinganPayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("---平安支付订单回调接口 start---------");
		String returnStr = null;
		try {
			String content = commonService.getBodyFromRequst(request);
			LOG.info("平安反馈消息:" + content);
			if (StringUtil.isNotBlank(content) && PinganPaymentUtil.isValidSign(content)) {
				Map<String, String> map = PinganPaymentUtil.convertParamtFromPinganPay(content);
				String out_trade_no = map.get("out_trade_no");
				String status = map.get("status");// 京东-status
				String pay_time = map.get("pay_time");// 京东--订单支付完成时间
				String time_end = map.get("time_end");// 微信--订单支付完成时间
				String returnCode = map.get("trade_status");// 支付宝--订单状态
				String retnCode = map.get("retn_code");// 翼支付--订单支付状态
				String tran_date = map.get("tran_date"); // 翼支付支付时间
				PayOrder payOrder = new PayOrder();
				LOG.info("--------平安支付回调订单号" + out_trade_no + "---------");
				// 异步请求记录模块
				SysAsynchronousReturnInfo backInfo = new SysAsynchronousReturnInfo();
				backInfo.setOrderNum(out_trade_no);

				if (StringUtil.isNotBlank(map.get("sign"))) {
					map.remove("sign");
				}
				backInfo.setReturnInfo(map.toString());
				sysAsynchronousReturnInfoService.save(backInfo);

				// 下面判断方法的依据，请参考平安提供的--清算平台接口文档.pdf
				if (StringUtil.isNotBlank(out_trade_no) && out_trade_no.startsWith("JD") && StringUtil.isNotBlank(pay_time) && "0".equals(status)) {
					if (map.containsKey("total_fee") && StringUtil.isNotBlank(map.get("total_fee"))) {
						payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(map.get("total_fee"))));
					}
					if (map.containsKey("receipt_amount") && StringUtil.isNotBlank(map.get("receipt_amount"))) {
						Double realFee = Double.parseDouble(map.get("total_fee")) - Double.parseDouble(map.get("pay_platform_fee")) + Double.parseDouble(map.get("liquidator_commission_fee"))
								+ Double.parseDouble(map.get("bank_commission_fee"));
						payOrder.setRealAmt(BigDecimal.valueOf(realFee));
					}
					if (map.containsKey("pay_time") && StringUtil.isNotBlank(map.get("pay_time"))) {
						payOrder.setPayFinishDate(map.get("pay_time"));
						payOrder.setFinishDate(DateUtil.getDateTimeFromString(map.get("pay_time")));
					}
					if (map.containsKey("trade_no") && StringUtil.isNotBlank(map.get("trade_no"))) {
						payOrder.setPayNo(map.get("trade_no"));
					}
					returnStr = "success";
				} else if (StringUtil.isNotBlank(out_trade_no) && out_trade_no.startsWith("YZF") && StringUtil.isNotBlank(tran_date) && "0000".equals(retnCode)) {
					if (map.containsKey("total_fee") && StringUtil.isNotBlank(map.get("total_fee"))) {
						payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(map.get("total_fee"))));
					}
					if (map.containsKey("net_receipt_amount") && StringUtil.isNotBlank(map.get("net_receipt_amount"))) { // 实收金额
						/*
						 * Double realFee =
						 * Double.parseDouble(map.get("total_fee")) -
						 * Double.parseDouble(map.get("pay_platform_fee")) +
						 * Double.parseDouble(map.get(
						 * "liquidator_commission_fee")) +
						 * Double.parseDouble(map.get("bank_commission_fee"));
						 * payOrder.setRealAmt(BigDecimal.valueOf(realFee));
						 */
						payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(map.get("net_receipt_amount")))); // 净收入
					}
					if (map.containsKey("tran_date") && StringUtil.isNotBlank(map.get("tran_date"))) {
						payOrder.setPayFinishDate(map.get("tran_date"));
						payOrder.setFinishDate(DateUtil.getDateTimeFromString(map.get("tran_date")));
					}
					if (map.containsKey("trade_no") && StringUtil.isNotBlank(map.get("trade_no"))) {
						payOrder.setPayNo(map.get("trade_no")); // 平台订单号
																// 平安提供的，非我们生成的带有YZF的订单号
					}
					returnStr = "success";
				} else if (StringUtil.isNotBlank(out_trade_no) && out_trade_no.startsWith("WX") && StringUtil.isNotBlank(time_end)) {
					if (map.containsKey("total_fee") && StringUtil.isNotBlank(map.get("total_fee"))) {
						payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(map.get("total_fee"))));
					}
					if (map.containsKey("net_money") && StringUtil.isNotBlank(map.get("net_money"))) { // 净收入
						payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(map.get("net_money"))));
					}
					if (map.containsKey("time_end") && StringUtil.isNotBlank(map.get("time_end"))) {
						payOrder.setPayFinishDate(map.get("time_end"));
						payOrder.setFinishDate(DateUtil.convertStringToDate(DateUtil.FORMAT_YYYYMMDDHHmmss, map.get("time_end")));
					}
					if (map.containsKey("transaction_id") && StringUtil.isNotBlank(map.get("transaction_id"))) {
						payOrder.setPayNo(map.get("transaction_id"));
					}
					returnStr = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";

				} else if (StringUtil.isNotBlank(out_trade_no) && out_trade_no.startsWith("AL") && "TRADE_SUCCESS".equals(returnCode)) {
					if (map.containsKey("total_amount") && StringUtil.isNotBlank(map.get("total_amount"))) {
						payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(map.get("total_amount"))));
					}
					if (map.containsKey("receipt_amount") && StringUtil.isNotBlank(map.get("receipt_amount"))) { // 实收金额
						payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(map.get("receipt_amount"))));
					}
					if (map.containsKey("gmt_payment") && StringUtil.isNotBlank(map.get("gmt_payment"))) {
						payOrder.setPayFinishDate(map.get("gmt_payment"));
						payOrder.setFinishDate(DateUtil.getDateTimeFromString(map.get("gmt_payment")));
					}
					if (map.containsKey("trade_no") && StringUtil.isNotBlank(map.get("trade_no"))) {
						payOrder.setPayNo(map.get("trade_no"));
					}
					returnStr = "success";
				}
				if (StringUtil.isNotBlank(returnStr)) {
					UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(out_trade_no);
					if (userOrder != null) {
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
						try {
							userOrderService.finishInputOrderStatus(out_trade_no, payOrder);
						} catch (Exception e) {
							LOG.error("----平安支付订单回调异常", e);
						}
					} else {
						LOG.info("平安支付订单：" + out_trade_no + " 不存在或已经被成功处理了");
					}
				} else {
					LOG.info("平安支付订单：状态未标记为成功");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----平安支付订单回调异常", e);
		}
		LOG.info("---平安支付订单回调接口 end---------");
		return returnStr;
	}

	@RequestMapping("/minshengNotify")
	@ResponseBody
	public String minshengNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("---民生订单回调接口 start---------");
		Map<String, String[]> params = request.getParameterMap();
		net.sf.json.JSONObject reqObj = new net.sf.json.JSONObject();
		for (String key : params.keySet()) {
			String[] values = params.get(key);
			if (values.length > 0) {
				reqObj.put(key, values[0]);
			}
		}
		LOG.info("扫码支付|民生-->>商户|接收支付异步通知数据：[{}]", new Object[] { reqObj.toString() });
		String xmlContent = MinShengMerchantInputMinShengUtil.getResponseContent(reqObj);
		LOG.info(xmlContent);

		String reqMsgId = reqObj.getString("reqMsgId");
		if (StringUtil.isNotBlank(reqMsgId)) {

			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(reqMsgId);
			Map<String, String> result = XmlMapper.xml2Map(xmlContent);
			/* 完成订单后，更新原有订单 */
			if (userOrder != null) {
				PayOrder payOrder = new PayOrder();
				if (result.containsKey("totalAmount") && StringUtil.isNotBlank(result.get("totalAmount"))) {
					payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(result.get("totalAmount"))));
				}

				if (result.containsKey("buyerPayAmount") && StringUtil.isNotBlank(result.get("buyerPayAmount"))) {
					payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(result.get("buyerPayAmount"))));
				}

				if (result.containsKey("payTime") && StringUtil.isNotBlank(result.get("payTime"))) {
					payOrder.setPayFinishDate(result.get("payTime"));
				}

				if (result.containsKey("channelNo") && StringUtil.isNotBlank(result.get("channelNo"))) {
					payOrder.setPayNo(result.get("channelNo"));
				}
				String return_msg = result.get("respMsg");
				String errCode = result.get("respCode");
				payOrder.setErrorInfo(return_msg);
				payOrder.setErrorCode(errCode);
				Boolean rechangeFlag = false;
				if (result.get("respType").equals("S") && (result.get("respCode").equals("000000") || result.get("respCode").equals("000090"))) {
					payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
					result.put("return_code", "SUCCESS");
					result.put("result_code", "SUCCESS");
					rechangeFlag = true;
				} else if (result.get("respType").equals("E")) {
					payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
					LOG.error("支付失败！out_trade_no:" + reqMsgId + ", err_code:" + errCode + ", return_msg:" + return_msg);
					rechangeFlag = true;
				}

				if (rechangeFlag) {
					try {
						userOrderService.finishInputOrderStatus(reqMsgId, payOrder);
					} catch (Exception e) {
						LOG.error("----订单回调异常", e);
					}
				}
			} else {
				LOG.info("订单：" + reqMsgId + " 不存在或已经被成功处理了");
			}

		} else {
			LOG.error("支付通信失败！");
		}
		// 直接默认返回
		LOG.info("---民生订单回调接口 end---------");
		return "000000";
	}

	@RequestMapping("/minshengTixianNotify")
	@ResponseBody
	public String minshengTixianNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("---民生提现订单回调接口 start---------");
		Map<String, String[]> params = request.getParameterMap();
		net.sf.json.JSONObject reqObj = new net.sf.json.JSONObject();
		for (String key : params.keySet()) {
			String[] values = params.get(key);
			if (values.length > 0) {
				reqObj.put(key, values[0]);
			}
		}
		LOG.info("扫码支付|民生-->>商户|接收支付异步通知数据：[{}]", new Object[] { reqObj.toString() });
		String xmlContent = MinShengMerchantInputMinShengUtil.getResponseContent(reqObj);
		LOG.info(xmlContent);

		String reqMsgId = reqObj.getString("reqMsgId");
		if (StringUtil.isNotBlank(reqMsgId)) {
			TchannelT0Tixian t = channelT0TixianService.getTodoTchannelT0TixianByOrderNum(reqMsgId);
			Map<String, String> result = XmlMapper.xml2Map(xmlContent);
			/* 完成订单后，更新原有订单 */
			if (t != null) {
				if (result.containsKey("drawAmount") && StringUtil.isNotBlank(result.get("drawAmount"))) {
					t.setAmt(BigDecimal.valueOf(Double.parseDouble(result.get("drawAmount"))));
				}
				if (result.containsKey("drawFee") && StringUtil.isNotBlank(result.get("drawFee"))) {
					t.setDrawFee(BigDecimal.valueOf(Double.parseDouble(result.get("drawFee"))));
				}
				if (result.containsKey("tradeFee") && StringUtil.isNotBlank(result.get("tradeFee"))) {
					t.setTradeFee(BigDecimal.valueOf(Double.parseDouble(result.get("tradeFee"))));
				}

				String return_msg = result.get("respMsg");
				String errCode = result.get("respCode");
				t.setErrorDesc(return_msg);
				t.setErrorCode(errCode);
				Boolean rechangeFlag = false;
				if (result.get("respType").equals("S") && result.get("respCode").equals("00")) {
					t.setStatus(UserOrder.order_status.SUCCESS.getCode());
					t.setFinishDate(new Date());
					rechangeFlag = true;
				} else if (result.get("respType").equals("E")) {
					t.setStatus(UserOrder.order_status.FAILURE.getCode());
					rechangeFlag = true;
				}
				if (rechangeFlag) {
					try {
						// userOrderService.finishInputOrderStatus(reqMsgId,
						// payOrder);
						channelT0TixianService.updateTchannel(t);
					} catch (Exception e) {
						LOG.error("----订单回调异常", e);
					}
				}

			} else {
				LOG.error("支付通信失败！");
			}
		}
		// 直接默认返回
		LOG.info("---民生提现订单回调接口 end---------");
		return "000000";
	}

	@ResponseBody
	@RequestMapping(value = "/yl_v2_Notify", method = RequestMethod.POST)
	public String yl_v2_Notify(@RequestParam String resp, @RequestParam String sign) throws IOException {

		LOG.info("---银联在线订单回调接口 start---------");
		try {
			LOG.info("response=" + resp);
			LOG.info("sign=" + sign);

			if (ZanshanfuPayUtil.isValidateSign(resp, sign)) {

				JSONObject json = ZanshanfuPayUtil.zansanfuDecodeResponse(resp);
				String orderId = json.getString("orderid");

				UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderId);
				if (userOrder != null) {

					if (userOrder.getOrgAmt().doubleValue() - (json.getDoubleValue("txnamt") / 100) == 0) {
						PayOrder payOrder = new PayOrder();
						payOrder.setPayAmt(BigDecimal.valueOf(json.getDoubleValue("txnamt") / 100));
						payOrder.setRealAmt(BigDecimal.valueOf(json.getDoubleValue("txnamt") / 100));
						payOrder.setPayNo(json.getString("queryid"));

						boolean isChange = false;
						if ("0000".equals(json.getString("resultcode")) || "1002".equals(json.getString("resultcode"))) {
							if (json.containsKey("paytime") && StringUtil.isNotBlank(json.getString("paytime"))) {
								payOrder.setPayFinishDate(json.getString("paytime"));
							}
							payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
							isChange = true;
						} else if ("1003".equals(json.getString("resultcode"))) {
							payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
							LOG.error("支付失败！out_trade_no:" + json.getString("queryid"));
							isChange = true;
						} else {
							LOG.error("支付状态未知！out_trade_no:" + json.getString("queryid"));
						}
						try {
							if (isChange) {
								userOrderService.finishInputOrderStatus(orderId, payOrder);
							}
						} catch (Exception e) {
							LOG.error("----银联订单回调异常", e);
						}
					} else {
						LOG.error("银联订单：" + orderId + " 支付金额与回执不一致，请继续等待");
					}
				} else {
					LOG.info("银联订单：" + orderId + " 不存在或已经被成功处理了");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----银联在线回调异常", e);
		} finally {
			LOG.info("---银联在线订单回调接口 end---------");
		}
		return "0000";
	}

	@ResponseBody
	@RequestMapping(value = "/yl_v3_Notify", method = { RequestMethod.POST, RequestMethod.GET })
	public String yl_v3_Notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("---申付银联在线订单回调接口 start---------");
		try {
			Map<String, String[]> params = request.getParameterMap();
			Map<String, String> map = new HashMap<String, String>();
			for (String key : params.keySet()) {
				String[] values = params.get(key);
				if (values.length > 0) {
					map.put(key, values[0]);
				}
			}
			String signature = map.get("signature");
			// LOG.info("----------map:" + map);
			// LOG.info("-------------signature-------------" + signature);
			map.remove("signature");
			String sign = RSAUtil.sign(ApplicationBase.coverMap2String(map).getBytes(), ApplicationBase.PRIVATEKEY);
			// LOG.info("-------------sign-------------" + sign);
			String orderNo = map.get("orderNo");
			if (!StringUtil.isNotBlank(orderNo)) {
				LOG.info("---------------申付回调订单为空-----------------");
				return "0000";
			}
			LOG.info("---------------申付回调订单参数=" + JSON.getDefault().toJSONString(map) + "-----------------");
			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNo);
			if (userOrder == null) {
				LOG.info("---------------订单不存在或者已经被处理了-----------------" + orderNo);
				return "200";
			}
			if (userOrder.getOrgAmt().doubleValue() - Double.parseDouble(map.get("txnAmt")) != 0) {
				LOG.error("申付银联订单：" + orderNo + " 支付金额与回执不一致，请继续等待");
				return "0000";
			}

			PayOrder payOrder = new PayOrder();
			if (map.containsKey("txnAmt") && StringUtil.isNotBlank(map.get("txnAmt"))) {
				payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(map.get("txnAmt"))));
			}
			if (map.containsKey("settleAmt") && StringUtil.isNotBlank(map.get("settleAmt"))) {
				payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(map.get("settleAmt"))));
			}
			if (map.containsKey("traceTime") && StringUtil.isNotBlank(map.get("traceTime"))) {
				payOrder.setPayFinishDate(map.get("traceTime"));
			}
			if (map.containsKey("txnTime") && StringUtil.isNotBlank(map.get("txnTime"))) {
				payOrder.setPayDate(DateUtil.getDateFromString(map.get("traceTime")));
			}
			if (map.containsKey("logNo") && StringUtil.isNotBlank(map.get("logNo"))) {
				payOrder.setPayNo(map.get("logNo"));
			}
			if (map.containsKey("respCode") && StringUtil.isNotBlank(map.get("respCode"))) {
				payOrder.setErrorCode(map.get("respCode"));
			}
			if (map.containsKey("respMsg") && StringUtil.isNotBlank(map.get("respMsg"))) {
				payOrder.setErrorInfo(map.get("respMsg"));
			}

			if (map.get("respCode").equals("00")) {
				payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
			} else {
				payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
			}
			try {
				LOG.info("---准备支付---------");
				userOrderService.finishInputOrderStatus(orderNo, payOrder);
				LOG.info("---准备支付end---------");
			} catch (Exception e) {
				LOG.error("----订单回调异常", e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----申付银联在线回调异常", e);
		} finally {
			LOG.info("---申付银联在线订单回调接口 end---------");
		}
		return "200";
	}

	/**
	 * 易联通道 手机控件支付接口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/yf_ylzx_Notify", method = { RequestMethod.POST, RequestMethod.GET })
	public String yf_ylzx_Notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("---易联银联在线订单回调接口 start---------");
		try {
			Map<String, String[]> params = request.getParameterMap();
			Map<String, String> map = new HashMap<String, String>();
			for (String key : params.keySet()) {
				String[] values = params.get(key);
				if (values.length > 0) {
					map.put(key, values[0]);
				}
			}

			LOG.info("---异步回调响应请求request.Parame---" + map);
			String rspMsg = MerchantUtil.decryptDataByAES(map.get("businessContext"), YiLianYlzxUtil.aesKey, "UTF-8");
			LOG.info("---异步回调响应rspMsg---" + rspMsg);
			boolean isTrue = MerchantUtil.verify(rspMsg, YiLianYlzxUtil.channelPublicKey, MerchantUtil.SIGNTYPE_RSA, "UTF-8");
			if (!isTrue) {
				LOG.info("---易联验签失败---");
				return "ERROR";
			}

			JSONObject json = JSONObject.parseObject(rspMsg);
			String reqMsgId = json.getString("transactionId");
			if (!StringUtil.isNotBlank(reqMsgId)) {
				LOG.info("---------------易联回调订单为空-----------------");
				return "ERROR";
			}

			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(reqMsgId);
			if (userOrder == null) {
				LOG.info("---------------订单不存在或者已经被处理了-----------------" + reqMsgId);
				return "ERROR";
			}
			if (userOrder.getOrgAmt().doubleValue() - Double.parseDouble(json.getString("payAmount")) != 0) {
				LOG.error("易联银联订单：" + reqMsgId + " 支付金额与回执不一致，请继续等待");
				return "ERROR";
			}

			PayOrder payOrder = new PayOrder();
			if (json.containsKey("payAmount") && StringUtil.isNotBlank(json.getString("payAmount"))) {
				payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(json.getString("payAmount"))));
			}
			if (json.containsKey("payAmount") && StringUtil.isNotBlank(json.getString("payAmount"))) {
				payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(json.getString("payAmount"))));
			}
			if (json.containsKey("dealTime") && StringUtil.isNotBlank(json.getString("dealTime"))) {
				payOrder.setPayFinishDate(json.getString("dealTime"));
			}
			if (json.containsKey("dealId") && StringUtil.isNotBlank(json.getString("dealId"))) {
				payOrder.setPayNo(json.getString("dealId"));
			}
			if (json.containsKey("retCode") && StringUtil.isNotBlank(json.getString("retCode"))) {
				payOrder.setErrorCode(json.getString("retCode"));
			}
			if (json.containsKey("retRemark") && StringUtil.isNotBlank(json.getString("retRemark"))) {
				payOrder.setErrorInfo(json.getString("retRemark"));
			}

			if (json.getString("retCode").equals("RC0000")) {
				payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
			} else if (json.getString("retCode").equals("RC0002")) {
				payOrder.setStatus(PayOrder.pay_status.PROCESSING.getCode());
			} else {
				payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
			}
			try {
				LOG.info("---准备支付---------");
				userOrderService.finishInputOrderStatus(reqMsgId, payOrder);
				LOG.info("---准备支付end---------");
			} catch (Exception e) {
				LOG.error("----订单回调异常", e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----易联银联在线回调异常", e);
			return "ERROR";
		} finally {
			LOG.info("---易联银联在线订单回调接口 end---------");
		}
		return "SUCCESS";
	}

	/**
	 * 嘎吱银联积分下单成功异步返回处理
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/gazhi_ylzx_notify", method = { RequestMethod.POST, RequestMethod.GET })
	public String gazhi_ylzx_notify(@RequestBody YiQiangCallbackDto reqDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("--gazhi_ylzx_notify URL---");
		String orgCode = reqDto.getOrgCode();
		String sign = reqDto.getSign();
		String body = reqDto.getBody();
		LOG.info("--gazhi_ylzx_notify orgCode={},body={},sign={}", orgCode, body, sign);
		// 合作商户编号 merNo 合作商户的唯一标识
		// 商户流水号 merTrace
		// 商户订单号 orderNo 商户系统保证唯一
		// 订单状态 transStatus 0 待支付、1 处理中、2 已支付、3 支付超时、4 重新付款、5 付款失败
		JSONObject bodyJson = GaZhiYinLianUtil.decrypt(body);
		String merNo = bodyJson.getString("merNo");// 合作商户的唯一标识
		String merTrace = bodyJson.getString("merTrace");
		String orderNo = bodyJson.getString("orderNo");
		// 0 待支付、1 处理中、2 已支付、3 支付超时、4 重新付款、5 付款失败
		String transStatus = bodyJson.getString("transStatus");
		LOG.info("--gazhi_ylzx_notify transStatus={},orderNo={},merNo={},merTrace={}", transStatus, orderNo, merNo, merTrace);
		PayOrder payOrder = new PayOrder();
		if ("2".equals(transStatus)) {
			payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
		} else {
			payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
		}
		String dateStr = DateUtil.convertCurrentDateTimeToString();
		payOrder.setPayFinishDate(dateStr);
		payOrder.setPayNo(orderNo);
		payOrder.setPayFinishDate(dateStr);
		payOrder.setFinishDate(new Date());
		payOrder.setErrorCode(transStatus);
		payOrder.setErrorInfo(transStatus);
		LOG.info("---GAZHI支付回调完结---------");
		userOrderService.finishInputOrderStatus(orderNo, payOrder);
		return "SUCCESS";
	}

	@ResponseBody
	@RequestMapping(value = "/yibao_ylzx_Notify", method = { RequestMethod.POST, RequestMethod.GET })
	public String yibao_ylzx_Notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("----银联积分易宝--交易成功回调---start");

		try {
			Map<String, String[]> params = request.getParameterMap();
			Map<String, String> map = new HashMap<String, String>();
			for (String key : params.keySet()) {
				String[] values = params.get(key);
				if (values.length > 0) {
					map.put(key, values[0]);
				}
			}

			// Map<String, String> map = new LinkedHashMap<String, String>();
			// for (String key : params.keySet()) {
			// String[] values = params.get(key);
			// if (values.length > 0) {
			// map.put(key, values[0]);
			// }
			// }

			LOG.info("---银联积分易宝---异步回调响应参数---" + map);

			// 校验签名
			String hmac = map.get("hmac");
			StringBuilder signature = new StringBuilder();
			signature.append(map.get("code") == null ? "" : map.get("code")).append(map.get("message") == null ? "" : map.get("message")).append(map.get("requestId") == null ? "" : map.get("requestId"))
					.append(map.get("customerNumber") == null ? "" : map.get("customerNumber")).append(map.get("externalld") == null ? "" : map.get("externalld")).append(map.get("createTime") == null ? "" : map.get("createTime"))
					.append(map.get("payTime") == null ? "" : map.get("payTime")).append(map.get("amount") == null ? "" : map.get("amount")).append(map.get("fee") == null ? "" : map.get("fee"))
					.append(map.get("status") == null ? "" : map.get("status")).append(map.get("busiType") == null ? "" : map.get("busiType")).append(map.get("bankCode") == null ? "" : map.get("bankCode"))
					.append(map.get("payerName") == null ? "" : map.get("payerName")).append(map.get("payerPhone") == null ? "" : map.get("payerPhone")).append(map.get("lastNo") == null ? "" : map.get("lastNo"))
					.append(map.get("src") == null ? "" : map.get("src"));

			// 生成签名
			String hmac2 = Digest.hmacSign(signature.toString(), YiBaoBaseUtil.hmacKey);
			if (!hmac.equals(hmac2)) {
				LOG.info("---银联积分易宝-----交易成功异步返回-------验签失败---");
				return "ERROR";
			}

			// 校验本地订单状况
			String reqMsgId = map.get("requestId");
			if (StringUtil.isBlank(reqMsgId)) {
				LOG.info("---银联积分易宝-----回调订单requestId号码为空-----------------");
				return "ERROR";
			}
			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(reqMsgId);
			if (userOrder == null) {
				LOG.info("----银联积分易宝---订单不存在或者已经被处理了-----------------" + reqMsgId);
				return "ERROR";
			}

			if (userOrder.getOrgAmt().doubleValue() - Double.parseDouble(map.get("amount")) != 0) {
				LOG.error("-----银联积分易宝---订单：" + reqMsgId + " 支付金额与回执不一致，请继续等待");
				return "ERROR";
			}

			PayOrder payOrder = new PayOrder();
			if (map.containsKey("amount") && StringUtil.isNotBlank(map.get("amount"))) {
				// 下单金额
				payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(map.get("amount"))));
			}
			if (map.containsKey("amount") && StringUtil.isNotBlank(map.get("amount"))) {
				// 实际支付金额
				payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(map.get("amount"))));
			}
			if (map.containsKey("payTime") && StringUtil.isNotBlank(map.get("payTime"))) {
				// 支付时间
				payOrder.setPayFinishDate(map.get("payTime"));
			}
			if (map.containsKey("externalld") && StringUtil.isNotBlank(map.get("externalld"))) {
				// 支付平台交易序号
				payOrder.setPayNo(map.get("externalld"));
			}
			if (map.containsKey("code") && StringUtil.isNotBlank(map.get("code"))) {
				// 应答码
				payOrder.setErrorCode(map.get("code"));
			}
			if (map.containsKey("message") && StringUtil.isNotBlank(map.get("message"))) {
				// 应答码描述
				payOrder.setErrorInfo(map.get("message"));
			}

			// 根据应答码判断订单状态
			if (map.get("code").equals("0000")) {
				if (map.get("status").equals("SUCCESS")) {
					payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
				} else if (map.get("status").equals("FAIL")) {
					payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
				} else {
					// 含有本地订单状态之外的参数，例如冻结、解冻等，默认不做保存,因为系统会判定非成功状态的订单，强制设置为失败
					return "ERROR";

				}
			} else {
				LOG.info("----银联积分易宝--异步回调--返回码为" + map.get("code") + map.get("message"));
			}

			try {
				LOG.info("---准备支付---------");
				userOrderService.finishInputOrderStatus(reqMsgId, payOrder);
				LOG.info("---准备支付end---------");
			} catch (Exception e) {
				LOG.error("----订单回调异常", e);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----银联积分易宝在线回调异常", e);
			return "ERROR";
		} finally {
			LOG.info("---银联积分易宝订单回调接口 end---------");
		}
		return "SUCCESS";
	}

	/**
	 * 易联限额通道(银联积分-直通车-D0) 快捷支付接口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/yfXe_ylzx_Notify", method = { RequestMethod.POST, RequestMethod.GET })
	public String yfXe_ylzx_Notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("---易联银联积分直通车DO---订单回调接口 start---------");
		try {
			Map<String, String[]> params = request.getParameterMap();
			Map<String, String> map = new HashMap<String, String>();
			for (String key : params.keySet()) {
				String[] values = params.get(key);
				if (values.length > 0) {
					map.put(key, values[0]);
				}
			}

			LOG.info("---易联银联积分直通车DO---异步回调响应请求request.Parame---" + map);
			String rspMsg = MerchantUtil.decryptDataByAES(map.get("businessContext"), YiLianYlzxUtil.aesKey, "UTF-8");
			LOG.info("---易联银联积分直通车DO---异步回调响应rspMsg---" + rspMsg);
			boolean isTrue = MerchantUtil.verify(rspMsg, YiLianYlzxUtil.channelPublicKey, MerchantUtil.SIGNTYPE_RSA, "UTF-8");
			if (!isTrue) {
				LOG.info("---易联银联积分直通车DO---易联验签失败---");
				return "ERROR";
			}

			JSONObject json = JSONObject.parseObject(rspMsg);
			String reqMsgId = json.getString("transactionId");
			if (!StringUtil.isNotBlank(reqMsgId)) {
				LOG.info("---易联银联积分直通车DO-----易联回调订单为空-----------------");
				return "ERROR";
			}

			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(reqMsgId);
			if (userOrder == null) {
				LOG.info("----易联银联积分直通车DO---订单不存在或者已经被处理了-----------------" + reqMsgId);
				return "ERROR";
			}
			if (userOrder.getOrgAmt().doubleValue() - Double.parseDouble(json.getString("orderAmount")) != 0) {
				LOG.error("-----易联银联积分直通车DO---易联银联订单：" + reqMsgId + " 支付金额与回执不一致，请继续等待");
				return "ERROR";
			}

			PayOrder payOrder = new PayOrder();
			if (json.containsKey("payAmount") && StringUtil.isNotBlank(json.getString("payAmount"))) {
				payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(json.getString("payAmount"))));
			}
			if (json.containsKey("orderAmount") && StringUtil.isNotBlank(json.getString("orderAmount"))) {
				payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(json.getString("orderAmount"))));
			}
			if (json.containsKey("dealTime") && StringUtil.isNotBlank(json.getString("dealTime"))) {
				payOrder.setPayFinishDate(json.getString("dealTime"));
			}
			if (json.containsKey("dealId") && StringUtil.isNotBlank(json.getString("dealId"))) {
				payOrder.setPayNo(json.getString("dealId"));
			}
			if (json.containsKey("retCode") && StringUtil.isNotBlank(json.getString("retCode"))) {
				payOrder.setErrorCode(json.getString("retCode"));
			}
			if (json.containsKey("retRemark") && StringUtil.isNotBlank(json.getString("retRemark"))) {
				payOrder.setErrorInfo(json.getString("retRemark"));
			}

			if (json.getString("retCode").equals("RC0000")) {
				payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
			} else if (json.getString("retCode").equals("RC0002")) {
				payOrder.setStatus(PayOrder.pay_status.PROCESSING.getCode());
			} else {
				payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
			}
			try {
				LOG.info("---准备支付---------");
				userOrderService.finishInputOrderStatus(reqMsgId, payOrder);
				LOG.info("---准备支付end---------");
			} catch (Exception e) {
				LOG.error("----订单回调异常", e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----易联银联在线回调异常", e);
			return "ERROR";
		} finally {
			LOG.info("---易联银联在线订单回调接口 end---------");
		}
		return "SUCCESS";
	}

	@ResponseBody
	@RequestMapping(value = "/xk_ylzx_Notify", method = { RequestMethod.POST, RequestMethod.GET })
	public String xk_ylzx_Notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("---欣客银联在线订单回调接口 start---------");
		try {
			LOG.info("---异步回调响应请求request.Parame---" + request.getParameter("orderNo"));
			String reqMsgId = String.valueOf(request.getParameter("orderNo"));
			if (!StringUtil.isNotBlank(reqMsgId)) {
				LOG.info("---------------欣客银联回调订单为空-----------------");
				return "error";
			}
			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(reqMsgId);
			if (userOrder == null) {
				LOG.info("---------------订单不存在或者已经被处理了-----------------" + reqMsgId);
				return "error";
			}
			if (userOrder.getOrgAmt().doubleValue() - Double.parseDouble(String.valueOf(request.getParameter("transAmount"))) / 100 != 0) {
				LOG.error("欣客银联订单：" + reqMsgId + " 支付金额与回执不一致，请继续等待");
				return "error";
			}

			PayOrder payOrder = new PayOrder();
			if (StringUtil.isNotBlank(String.valueOf(request.getParameter("transAmount")))) {
				payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(String.valueOf(request.getParameter("transAmount"))) / 100));
			}
			if (StringUtil.isNotBlank(String.valueOf(request.getParameter("actualAmount")))) {
				payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(String.valueOf(request.getParameter("actualAmount"))) / 100));
			}
			if (StringUtil.isNotBlank(String.valueOf(request.getParameter("transDate")))) {
				payOrder.setPayFinishDate(String.valueOf(request.getParameter("transDate")));
			}
			if (StringUtil.isNotBlank(String.valueOf(request.getParameter("transSeq")))) {
				payOrder.setPayNo(String.valueOf(request.getParameter("transSeq")));
			}
			if (StringUtil.isNotBlank(String.valueOf(request.getParameter("statusCode")))) {
				payOrder.setErrorCode(String.valueOf(request.getParameter("statusCode")));
			}
			if (StringUtil.isNotBlank(String.valueOf(request.getParameter("statusMsg")))) {
				payOrder.setErrorInfo(String.valueOf(request.getParameter("statusMsg")));
			}

			if (request.getParameter("statusCode").equals("00")) {
				payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
			} else {
				payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
			}

			try {
				LOG.info("---准备支付---------");
				userOrderService.finishInputOrderStatus(reqMsgId, payOrder);
				LOG.info("---准备支付end---------");
			} catch (Exception e) {
				LOG.error("----订单回调异常", e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----欣客银联在线回调异常", e);
			return "error";
		} finally {
			LOG.info("---欣客银联在线订单回调接口 end---------");
		}
		return "success";
	}

	@ResponseBody
	@RequestMapping(value = "/yl_v5_Notify", method = RequestMethod.POST)
	public String yl_v5_Notify(@RequestParam String resp, @RequestParam String sign) throws IOException {

		LOG.info("---银联在线封顶订单回调接口 start---------");
		try {
			LOG.info("response=" + resp);
			LOG.info("sign=" + sign);

			if (ZanshanfuPayUtil.isValidateSignV5(resp, sign)) {

				JSONObject json = ZanshanfuPayUtil.zansanfuDecodeResponse(resp);
				String orderId = json.getString("orderid");

				UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderId);
				if (userOrder != null) {

					if (userOrder.getOrgAmt().doubleValue() - (json.getDoubleValue("txnamt") / 100) == 0) {
						PayOrder payOrder = new PayOrder();
						payOrder.setPayAmt(BigDecimal.valueOf(json.getDoubleValue("txnamt") / 100));
						payOrder.setRealAmt(BigDecimal.valueOf(json.getDoubleValue("txnamt") / 100));
						payOrder.setPayNo(json.getString("queryid"));

						boolean isChange = false;
						if ("0000".equals(json.getString("resultcode")) || "1002".equals(json.getString("resultcode"))) {
							if (json.containsKey("paytime") && StringUtil.isNotBlank(json.getString("paytime"))) {
								payOrder.setPayFinishDate(json.getString("paytime"));
							}
							payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
							isChange = true;
						} else if ("1003".equals(json.getString("resultcode"))) {
							payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
							LOG.error("支付失败！out_trade_no:" + json.getString("queryid"));
							isChange = true;
						} else {
							LOG.error("支付状态未知！out_trade_no:" + json.getString("queryid"));
						}
						try {
							if (isChange) {
								userOrderService.finishInputOrderStatus(orderId, payOrder);
							}
						} catch (Exception e) {
							LOG.error("----银联订单回调异常", e);
						}
					} else {
						LOG.error("银联订单：" + orderId + " 支付金额与回执不一致，请继续等待");
					}
				} else {
					LOG.info("银联订单：" + orderId + " 不存在或已经被成功处理了");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----银联在线封顶回调异常", e);
		} finally {
			LOG.info("---银联在线封顶订单回调接口 end---------");
		}
		return "0000";
	}

	@ResponseBody
	@RequestMapping(value = "/ylNotify", method = RequestMethod.POST)
	public String ylNotify(@RequestParam String order_id, @RequestParam Double m_actual, @RequestParam Integer s_pay, @RequestParam String app_order_id) throws IOException {

		LOG.info("---银联在线订单回调接口 start---------");
		try {
			// order_id=706678a7f04810a871a62e8d950e8621&m_actual=0&s_pay=0&app_id=71&app_order_id=YLZX201608101345200000000001
			LOG.info("order_id=" + order_id);
			LOG.info("m_actual=" + m_actual);
			LOG.info("s_pay=" + s_pay);
			LOG.info("app_order_id=" + app_order_id);
			PaymentResult result = new PaymentResult();
			if (StringUtil.isNotBlank(order_id)) {
				result.setPaymenrVoucherNo(order_id);
			}
			if (StringUtil.isNotBlank(app_order_id)) {
				result.setAppOrderId(app_order_id);
			}
			if (m_actual != null) {
				result.setmActual(BigDecimal.valueOf(m_actual));
			}
			if (s_pay != null) {
				result.setSuccess(s_pay == 1);
			}

			if (!result.isSuccess()) {
				LOG.info("银联订单：" + result.getAppOrderId() + " 未完成支付，请继续等待");
				return "FAILURE";
			}
			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(result.getAppOrderId());
			if (userOrder != null) {
				if (userOrder.getOrgAmt().doubleValue() - result.getmActual().doubleValue() == 0) {
					PayOrder payOrder = new PayOrder();
					payOrder.setPayAmt(result.getmActual());
					payOrder.setRealAmt(result.getmActual());
					payOrder.setPayNo(result.getPaymenrVoucherNo());
					payOrder.setPayFinishDate(DateUtil.convertCurrentDateTimeToString());

					if (result.isSuccess()) {
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
					} else {
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
						LOG.error("支付失败！out_trade_no:" + result.getPaymenrVoucherNo());
					}
					try {
						userOrderService.finishInputOrderStatus(result.getAppOrderId(), payOrder);
					} catch (Exception e) {
						LOG.error("----银联订单回调异常", e);
					}
				} else {
					LOG.error("银联订单：" + result.getAppOrderId() + " 支付金额与回执不一致，请继续等待");
					return "FAILURE";
				}
			} else {
				LOG.info("银联订单：" + result.getAppOrderId() + " 不存在或已经被成功处理了");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----银联在线回调异常", e);
		} finally {
			LOG.info("---银联在线订单回调接口 end---------");
		}

		return "SUCCESS";
	}

	@RequestMapping("/successedpay")
	public String successedpay(HttpServletRequest request, String orderNum) {
		try {
			LOG.info("反馈消息:" + orderNum);
			if (StringUtil.isNotEmpty(orderNum)) {
				UserOrder t = userOrderService.findOrderByOrderNum(orderNum);
				request.setAttribute("orderNum", orderNum);
				request.setAttribute("amt", t.getOrgAmt());
				String channelName = "";
				if (orderNum.startsWith("AL")) {
					channelName = "支付宝";
				} else if (orderNum.startsWith("WX")) {
					channelName = "微信";
				} else if (orderNum.startsWith("JD")) {
					channelName = "京东";
				} else if (orderNum.startsWith("YZF")) {
					channelName = "翼支付";
				}
				request.setAttribute("channelName", channelName);
				if (t.getPayOrderFinishDate() != null) {
					request.setAttribute("finishtDate", DateUtil.getDateTime("yyyy/MM/dd HH:mm:ss", t.getPayOrderFinishDate()));
				} else {
					request.setAttribute("finishtDate", DateUtil.getDateTime("yyyy/MM/dd HH:mm:ss", new Date()));
				}
			}
		} catch (Exception e) {
			LOG.error("通道成功回馈消息异常", e);
		}
		return "/admin/successedpay";
	}

	@ResponseBody
	@RequestMapping(value = "/quanTongNotify", method = { RequestMethod.POST, RequestMethod.GET })
	public String quanTongNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("---全晶通订单回调接口 start---------");
		try {
			LOG.info("---异步回调响应请求request.Parame---" + request.getParameter("code"));
			String code = request.getParameter("code");
			String msg = request.getParameter("msg");
			String type = request.getParameter("type");
			if (type.equals("1")) {
				String data = request.getParameter("data");
				String Sign = request.getParameter("Sign");
				String resp = AESUtil.encrypt(data, AESUtil.AES_KEY);
				if (Sign.equals(MD5Util.md5(resp))) {
					Map<String, String> map = QuanTongPaymentUtil.MakeMap(resp);
					String reqMsgId = String.valueOf(map.get("orderId"));
					if (!StringUtil.isNotBlank(reqMsgId)) {
						LOG.info("---------------全晶通回调订单为空-----------------");
						return "error";
					}
					UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(reqMsgId);
					if (userOrder == null) {
						LOG.info("---------------订单不存在或者已经被处理了-----------------" + reqMsgId);
						return "error";
					}
					if (userOrder.getOrgAmt().doubleValue() - Double.parseDouble(String.valueOf(map.get("Fee"))) / 100 != 0) {
						LOG.error("全晶通订单:" + reqMsgId + " 支付金额与回执不一致，请继续等待");
						return "error";
					}

					PayOrder payOrder = new PayOrder();
					Double amt = Double.parseDouble(String.valueOf(map.get("Fee"))) / 100;
					if (StringUtil.isNotBlank(String.valueOf(amt))) {
						payOrder.setPayAmt(BigDecimal.valueOf(amt));
					}
					if (code.equals("0")) {
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
					} else {
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
					}
					if (StringUtil.isNotBlank(code)) {
						payOrder.setErrorCode(code);
					}
					if (StringUtil.isNotBlank(msg)) {
						payOrder.setErrorInfo(msg);
					}
					String qtOrderId = map.get("qtOrderId");
					if (StringUtil.isNotBlank(qtOrderId)) {
						payOrder.setPayNo(qtOrderId);
					}
					if (StringUtil.isNotBlank(String.valueOf(map.get("time")))) {
						payOrder.setPayFinishDate(String.valueOf(map.get("time")));
					}
					try {
						LOG.info("---准备支付---------");
						userOrderService.finishInputOrderStatus(reqMsgId, payOrder);
						LOG.info("---准备支付end---------");
					} catch (Exception e) {
						LOG.error("----订单回调异常", e);
					}
				} else {
					LOG.info("----------全晶通订单回调签名失败-------------");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----全晶通回调异常", e);
			return "error";
		} finally {
			LOG.info("---全晶通订单回调接口 end---------");
		}
		return "000000";
	}

	@ResponseBody
	@RequestMapping(value = "/asynchronousNotification", method = { RequestMethod.POST, RequestMethod.GET })
	public String asynchronousNotification(HttpServletRequest req) throws Exception {
		LOG.info("---嘎吱订单回调接口 start---------");
		try {
			LOG.info(req.toString());
			BufferedReader reader = req.getReader();
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			System.out.println(sb.toString());
			String s = URLDecoder.decode(sb.toString(), "UTF-8");
			System.out.println(s);
			byte[] b = Base64.decodeBase64(s);
			System.out.println(new String(b)); // b.toString() 是对象的内存地址
			Map<String, String> resMap = GaZhiXml2MapUtil.xml2map(new String(Base64.decodeBase64(s)));

			if (resMap != null) {
				String reqMsgId = String.valueOf(resMap.get("merTrace"));
				if (!StringUtil.isNotBlank(reqMsgId)) {
					LOG.info("---------------欣客银联回调订单为空-----------------");
					return "error";
				}
				UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(reqMsgId);
				if (userOrder == null) {
					LOG.info("---------------订单不存在或者已经被处理了-----------------" + reqMsgId);
					return "error";
				}
				if (userOrder.getOrgAmt().doubleValue() - Double.parseDouble(String.valueOf(resMap.get("amount"))) / 100 != 0) {
					LOG.error("嘎吱订单：" + reqMsgId + " 支付金额与回执不一致，请继续等待");
					return "error";
				}

				PayOrder payOrder = new PayOrder();
				Double amt = Double.parseDouble(String.valueOf(resMap.get("amount"))) / 100;
				if (StringUtil.isNotBlank(String.valueOf(amt))) {
					payOrder.setPayAmt(BigDecimal.valueOf(amt));
				}
				if (resMap.get("tradeStatus").equals("S") && StringUtil.isNotBlank(resMap.get("merTrace"))) {
					payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
				} else {
					payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
				}

				try {
					LOG.info("---准备支付---------");
					userOrderService.finishInputOrderStatus(reqMsgId, payOrder);
					LOG.info("---准备支付end---------");
				} catch (Exception e) {
					LOG.error("----订单回调异常", e);
				}
			} else {
				LOG.info("-------嘎吱回调信息为空---------");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("----嘎吱通回调异常", e);
			return "error";
		} finally {
			LOG.info("---嘎吱订单回调接口 end---------");
		}
		return "SUCCEED";
	}

	/**
	 * 哲扬通道下单异步返回接口
	 * 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/zheYangPayNotify", method = { RequestMethod.POST, RequestMethod.GET })
	public void zheYangPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("---哲扬订单回调接口 start---------");
		try {
			JSONObject reqParams = new JSONObject();
			String bizOrderNumber = request.getParameter("bizOrderNumber");
			String completedTime = request.getParameter("completedTime");
			String srcAmt = request.getParameter("srcAmt");
			String mid = request.getParameter("mid");
			String sign = request.getParameter("sign");
			reqParams.put("completedTime", completedTime);
			reqParams.put("bizOrderNumber", bizOrderNumber);
			reqParams.put("mid", mid);
			reqParams.put("srcAmt", srcAmt);
			reqParams.put("sign", sign);
			reqParams.put("key", "01c57d2c27806a336692f7b14951a8b3");
			if (ZheYangUtil.judgeSign(reqParams)) {
				String reqMsgId = bizOrderNumber;
				if (!StringUtil.isNotBlank(reqMsgId)) {
					LOG.info("---------------哲扬银联回调订单为空-----------------");
					response.getWriter().print("error");
				}
				UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(reqMsgId);
				if (userOrder == null) {
					LOG.info("---------------订单不存在或者已经被处理了-----------------" + reqMsgId);
					response.getWriter().print("error");
				}
				if (userOrder.getOrgAmt().doubleValue() - Double.parseDouble(srcAmt) != 0) {
					LOG.error("哲扬订单：" + reqMsgId + " 支付金额与回执不一致，请继续等待");
					response.getWriter().print("error");
				}
				PayOrder payOrder = new PayOrder();
				Double amt = Double.parseDouble(srcAmt);
				if (StringUtil.isNotBlank(String.valueOf(amt))) {
					payOrder.setPayAmt(BigDecimal.valueOf(amt));
				}
				if (StringUtil.isNotBlank(completedTime)) {
					payOrder.setPayFinishDate(completedTime);
				}
				payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
				try {
					LOG.info("---准备支付---------");
					userOrderService.finishInputOrderStatus(reqMsgId, payOrder);
					LOG.info("---准备支付end---------");
				} catch (Exception e) {
					LOG.error("----订单回调异常", e);
					response.getWriter().print("false");
				}
				response.getWriter().print("success");
			} else {
				LOG.info("---哲扬订单回调接口 -----验证参数失败---------");
				response.getWriter().print("false");
			}
		} catch (Exception e) {
			LOG.info("---哲扬订单回调接口 end---------");
		}
		response.getWriter().print("false");
	}

	/**
	 * 申孚通道下单异步返回接口
	 * 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/ShenFuPayNotify", method = { RequestMethod.POST, RequestMethod.GET })
	public String ShenFuPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("---申孚直通车订单回调接口 start---------");
		try {
			// JSONObject reqParams = new JSONObject();
			String respCode = request.getParameter("respCode");// 交易结果码
																// 00表示成功，其他值表示失败。
			String respMsg = request.getParameter("respMsg");// 交易结果消息
			String logNo = request.getParameter("logNo");// 交易流水号 平台交易的流水号。
			String orderNo = request.getParameter("orderNo");// 订单号 同消费接口中的订单号,
																// 商户订单号。
			String settleAmt = request.getParameter("settleAmt");// 清算金额
																	// 以元为单位，如1.34。
			String settleDate = request.getParameter("settleDate");// 清算日期
																	// MMDD格式，如0620。
			String txnTime = request.getParameter("txnTime");// 交易时间
																// 格式：YYYYMMDDHHMISS，如20170419163141。
			String txnAmt = request.getParameter("txnAmt");// 交易金额 以元为单位，如1.34。
			String traceNo = request.getParameter("traceNo");// 追踪号 银联交易追踪号。
			String traceTime = request.getParameter("traceTime");// 追踪时间
																	// 银联交易追踪时间，格式：MMDDHHMISS，如0419163141。
			String signature = request.getParameter("signature");
			LOG.info("SHENFU D0 notify:respCode={},respMsg={},logNo={},orderNo={},settleAmt={},settleDate={},txnTime={},txnAmt={},traceNo={},traceTime={}",
					new Object[] { respCode, respMsg, logNo, orderNo, settleAmt, settleDate, txnTime, txnAmt, traceNo, traceTime });
			if (!StringUtil.isNotBlank(orderNo)) {
				LOG.info("---------------申付直通车回调订单为空-----------------orderNo={}", orderNo);
				return "0000";
			}
			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNo);
			if (userOrder == null) {
				LOG.info("---------------订单不存在或者已经被处理了-----------------" + orderNo);
				return "200";
			}
			if (userOrder.getOrgAmt().doubleValue() - Double.parseDouble(txnAmt) != 0) {
				LOG.error("申孚直通车订单：" + orderNo + " 支付金额与回执不一致，请继续等待");
				return "0000";
			}

			TuserMerchantReport report = userMerchantReportService.get(userOrder.getUserId());
			Map<String, String> map = new TreeMap<String, String>();
			map.put("respCode", respCode);
			map.put("respMsg", respMsg);
			map.put("logNo", logNo);
			map.put("orderNo", orderNo);
			map.put("settleAmt", settleAmt);
			map.put("settleDate", settleDate);
			map.put("txnTime", txnTime);
			map.put("txnAmt", txnAmt);
			map.put("traceNo", traceNo);
			map.put("traceTime", traceTime);

			boolean bKeySign = RSAUtil.verify(ApplicationBase.coverMap2String(map).getBytes("UTF-8"), report.getPublicKey(), signature);
			LOG.info("ShenFuPayNotify PublicKey orderNo={} Sign={}", orderNo, bKeySign);

			// sign值匹配 {}
			if ("00".equals(respCode) || bKeySign) {
				// 设置后续信息
				PayOrder payOrder = new PayOrder();
				if (StringUtil.isNotBlank(txnAmt)) {
					payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(txnAmt)));
				}
				if (StringUtil.isNotBlank(settleAmt)) {
					payOrder.setRealAmt(BigDecimal.valueOf(Double.parseDouble(settleAmt)));
				}
				if (StringUtil.isNotBlank(settleDate)) {
					payOrder.setPayFinishDate(settleDate);
				}
				if (StringUtil.isNotBlank(logNo)) {
					payOrder.setPayNo(logNo);
				}
				if (StringUtil.isNotBlank(respCode)) {
					payOrder.setErrorCode(respCode);
				}
				if (StringUtil.isNotBlank(respMsg)) {
					payOrder.setErrorInfo(respMsg);
				}
				if ("00".equals(respCode)) {
					payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
				} else {
					payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
				}
				try {
					LOG.info("---准备支付---------");
					userOrderService.finishInputOrderStatus(orderNo, payOrder);
					LOG.info("---准备支付end---------");
				} catch (Exception e) {
					LOG.error("----订单回调异常", e);
				}
				return "success";
			} else {
				LOG.info("---申孚直通车订单回调接口 -----验证参数失败---------");
				return "false";
			}

		} catch (Exception e) {
			LOG.info("---申孚直通车订单回调接口 end---------");
		}
		return "false";
	}

	@ResponseBody
	@RequestMapping(value = "/ShenFuDaiFuNotify", method = { RequestMethod.POST, RequestMethod.GET })
	public String shenFuDaiFuNotify(HttpServletRequest request, HttpServletResponse response, String orderId) throws Exception {
		LOG.info("--shenFuDaiFuNotify ORDERID={}", orderId);
		try {
			// merSeqId 该平台交易流水号 String 是 平台交易流水号
			// errCode 响应码 String 是 响应码 （详见应答码描述）
			// errMsg 响应码说明 String 是 响应码说明
			// respDate 相应日期 Sring 是 相应日期
			// respTime 相应时间 String 是 相应时间
			// accNo 卡号 String 是 交易卡号
			// transAmt 交易金额 Stirng 是 交易金额
			// traceNo 系统跟踪号 String 是 系统跟踪号
			// ansReserved1 响应保留域1 String 否 响应保留域1
			// ansReserved2 响应保留域2 String 否 响应保留域2
			// signature 响应签名字符串 String 是 响应签名字符串
			String errCode = request.getParameter("errCode");// 000000 成功
			String errMsg = request.getParameter("errMsg");//
			String merSeqId = request.getParameter("merSeqId");//
			String respDate = request.getParameter("respDate");//
			String respTime = request.getParameter("respTime");//
			String accNo = request.getParameter("accNo");//
			String transAmt = request.getParameter("transAmt");//
			BigDecimal Amt = StringUtil.isEmpty(transAmt) ? BigDecimal.ZERO : new BigDecimal(transAmt);
			BigDecimal realAmt = Amt.multiply(new BigDecimal("0.01")).setScale(2);
			String traceNo = request.getParameter("traceNo");//
			String ansReserved1 = request.getParameter("ansReserved1");//
			String ansReserved2 = request.getParameter("ansReserved2");//
			String signature = request.getParameter("signature");//
			LOG.info("--shenFuDaiFuNotify orderId={},errCode={},errMsg={},merSeqId={},respDate={},respTime={},accNo={},transAmt={},traceNo={},ansReserved1={},ansReserved2={},signature={}",
					new Object[] { orderId, errCode, errMsg, merSeqId, respDate, respTime, accNo, realAmt, traceNo, ansReserved1, ansReserved2, signature });
			// 校验
			if (!StringUtil.isNotBlank(orderId)) {
				LOG.info("---------------申付代付回调订单为空-----------------orderNo={}", orderId);
				return "0000";
			}
			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderId);
			if (userOrder == null) {
				LOG.info("---------------申付代付订单不存在或者已经被处理了-----------------" + orderId);
				return "200";
			}
			if (userOrder.getAmt().doubleValue() - realAmt.doubleValue() != 0) {
				LOG.error("申付代付订单：" + orderId + " 支付金额与回执不一致，请继续等待");
				// return "0000";
			}
			Map<String, String> map = new TreeMap<String, String>();
			map.put("errCode", errCode);
			map.put("errMsg", errMsg);
			map.put("respDate", respDate);
			map.put("respTime", respTime);
			if (StringUtil.isNotEmpty(merSeqId)) {
				map.put("merSeqId", merSeqId);
			}
			if (StringUtil.isNotEmpty(accNo)) {
				map.put("accNo", accNo);
			}
			if (StringUtil.isNotEmpty(transAmt)) {// 原值校验
				map.put("transAmt", transAmt);
			}
			if (StringUtil.isNotEmpty(traceNo)) {
				map.put("traceNo", traceNo);
			}
			if (StringUtil.isNotEmpty(ansReserved1)) {
				map.put("ansReserved1", ansReserved1);
			}
			if (StringUtil.isNotEmpty(ansReserved2)) {
				map.put("ansReserved2", ansReserved2);
			}

			boolean bKeySign = RSAUtil.verify(ApplicationBase.coverMap2String(map).getBytes("UTF-8"), ApplicationBase.SHENFU_DAIFU_PUBLICKEY, signature);
			LOG.info("ShenFuDaiFuNotify PublicKey orderId={},Sign={}", orderId, bKeySign);
			if (bKeySign) {
				// 设置后续信息
				PayOrder payOrder = new PayOrder();
				// if (StringUtil.isNotBlank(txnAmt)) {
				// payOrder.setPayAmt(BigDecimal.valueOf(Double.parseDouble(txnAmt)));
				// }
				if (StringUtil.isNotBlank(transAmt)) {
					payOrder.setRealAmt(realAmt);
				}
				if (StringUtil.isNotBlank(respDate)) {
					payOrder.setPayFinishDate(respDate);
				}
				if (StringUtil.isNotBlank(merSeqId)) {
					payOrder.setPayNo(merSeqId);
				}
				if (StringUtil.isNotBlank(errCode)) {
					payOrder.setErrorCode(errCode);
				}
				if (StringUtil.isNotBlank(errMsg)) {
					payOrder.setErrorInfo(errMsg);
				}
				payOrder.setPayFinishDate(DateUtil.convertCurrentDateTimeToString());
				payOrder.setFinishDate(new Date());
				payOrder.setErrorCode(errCode);
				payOrder.setErrorInfo(errMsg);
				boolean success = false;
				if ("000000".equals(errCode)) {
					success = true;
				}
				userOrderService.affirmDaiFuNotify(success, orderId, payOrder);
				return "success";
			} else {
				LOG.info("---申孚代付订单回调接口 -----验证参数失败---------");
				return "false";
			}
		} catch (Exception e) {
			LOG.info("---申孚代付订单回调接口 end---------");
		}
		
		return "false";
	}
	
	// 
		@ResponseBody
		@RequestMapping(value = "/yiQiangPointPayNotify", method = { RequestMethod.POST})
		public String yiQiangPointPayNotify(@RequestBody YiQiangCallbackDto req, HttpServletRequest request, HttpServletResponse response) throws Exception {
			LOG.info("--yiQiangPointPayNotify URL---");
			String orgCode = req.getOrgCode();
			String sign = req.getSign();
			String body = req.getBody();
			LOG.info("--yiQiangPointPayNotify orgCode={},body={},sign={}", orgCode, body, sign);
			String bodyStr = YiQiang2PayUtil.decrypt(body);
			Map<String, String> bodyMap = JSONObject.parseObject(bodyStr, Map.class);
			LOG.info("--yiQiangPointPayNotify bodyStr={}", bodyStr);
			return "SUCCESS";
//			合作商户号  comMrno_07  R   
//			商户流水号  merTrce_05  R   
//			订单时间  odrTime_08  R   
//			商户订单号  odrIdno_09  R   
//			支付状态  trnStus_21  M   
//			系统流水号  trnSeqn_18  M   
			
//			String comMrno_07 = bodyMap.get("comMrno_07");// 合作商户的唯一标识
//			String merTrce_05 = bodyMap.get("merTrce_05");
//			String odrTime_08 = bodyMap.get("odrTime_08");
//			// 00 交易成功;01 交易失败;02 交易处理中;03 未支付;99 交易初始;对于02、03、99，需通过发起查询交易确认结果状态
//			String odrIdno_09 = bodyMap.get("odrIdno_09");
//			String trnStus_21 = bodyMap.get("trnStus_21");
//			String trnSeqn_18 = bodyMap.get("trnSeqn_18");
//			
//			PayOrder payOrder = new PayOrder();
//			if ("00".equals(trnStus_21)) {
//				payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
//			} else {
//				payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
//			}
//			String dateStr = DateUtil.convertCurrentDateTimeToString();
//			payOrder.setPayFinishDate(dateStr);
//			payOrder.setPayNo(merTrce_05);
//			payOrder.setPayFinishDate(dateStr);
//			payOrder.setFinishDate(new Date());
//			payOrder.setErrorCode(trnStus_21);
//			payOrder.setErrorInfo(trnSeqn_18);
//			userOrderService.finishInputOrderStatus(odrIdno_09, payOrder);
//			LOG.info("---YiQiang支付回调完结2---------");
//			return "SUCCESS";
		}

	// YIQIANG
	@ResponseBody
	@RequestMapping(value = "/yiQiangPayNotify", method = { RequestMethod.POST, RequestMethod.GET })
	public String yiQiangPayNotify(@RequestBody YiQiangCallbackDto req, HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("--yiQiangPayNotify URL---");
		String orgCode = req.getOrgCode();
		String sign = req.getSign();
		String body = req.getBody();
		LOG.info("--yiQiangPayNotify orgCode={},body={},sign={}", orgCode, body, sign);

		String bodyStr = YiQiangPayUtil.decrypt(body);
		Map<String, String> bodyMap = JSONObject.parseObject(bodyStr, Map.class);

		String merNo = bodyMap.get("merNo");// 合作商户的唯一标识
		String merTrace = bodyMap.get("merTrace");
		String orderNo = bodyMap.get("orderNo");
		// 00 交易成功;01 交易失败;02 交易处理中;03 未支付;99 交易初始;对于02、03、99，需通过发起查询交易确认结果状态
		String transStatus = bodyMap.get("transStatus");
		String transSeq = bodyMap.get("transSeq");
		LOG.info("--yiQiangPayNotify transStatus={},orderNo={},merNo={},merTrace={},transSeq={}", transStatus, orderNo, merNo, merTrace, transSeq);
		PayOrder payOrder = new PayOrder();
		if ("00".equals(transStatus)) {
			payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
		} else {
			payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
		}
		String dateStr = DateUtil.convertCurrentDateTimeToString();
		payOrder.setPayFinishDate(dateStr);
		payOrder.setPayNo(transSeq);
		payOrder.setPayFinishDate(dateStr);
		payOrder.setFinishDate(new Date());
		payOrder.setErrorCode(transStatus);
		payOrder.setErrorInfo(transStatus);
		LOG.info("---YiQiang支付回调完结---------");
		userOrderService.finishInputOrderStatus(orderNo, payOrder);
		return "SUCCESS";
	}

	// 银联侧绑卡开通后台通知url
	@RequestMapping(value = "/GaZhiOpenCardNotifyUrl", method = { RequestMethod.POST, RequestMethod.GET })
	public String GaZhiOpenCardNotifyUrl(@RequestBody YiQiangCallbackDto req, HttpServletResponse response) throws Exception {
		LOG.info("--GaZhiOpenCardNotifyUrl URL---");
		String orgCode = req.getOrgCode();
		String sign = req.getSign();
		String body = req.getBody();
		LOG.info("--GaZhiOpenCardNotifyUrl orgCode={},body={},sign={}", orgCode, body, sign);
		JSONObject bodyStr = GaZhiYinLianUtil.decrypt(body);

		if (bodyStr != null) {
			LOG.info("--GaZhiOpenCardNotifyUrl bodyStr={}", bodyStr);
		}

		return "SUCCESS";
	}

	@ResponseBody
	@RequestMapping(value = "/TransfarDaiFuNotify", method = { RequestMethod.POST, RequestMethod.GET })
	public String transfarDaiFuNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOG.info("--TransfarDaiFuNotify URL---");
		try {

			// Map<String, String[]> resMap = reque st.getParameterMap();
			Map<String, Object> signMap = new HashMap<String, Object>();
			// for (String key : resMap.keySet()) {
			// for (String innerV : resMap.get(key)) {
			// LOG.info("--TransfarDaiFuNotify key={},value={}", key, innerV);
			// signMap.put(key, innerV);
			// }
			// }

			String fromaccountnumber = request.getParameter("fromaccountnumber");//
			String status = request.getParameter("status");//
			String frompartyid = request.getParameter("frompartyid");//
			String remark = request.getParameter("remark");//
			String subject = request.getParameter("subject");//
			String inputdate = request.getParameter("inputdate");//
			String transactiontype = request.getParameter("transactiontype");//
			String transactionamount = request.getParameter("transactionamount");//
			String businessnumber = request.getParameter("businessnumber");//
			String tf_sign = request.getParameter("tf_sign");//
			String transactiondate = request.getParameter("transactiondate");//
			String billamount = request.getParameter("billamount");//
			String businessrecordnumber = request.getParameter("businessrecordnumber");//

			LOG.info("--TransfarDaiFuNotify fromaccountnumber={},status={},frompartyid={},remark={},subject={},inputdate={},transactiontype={},transactionamount={},businessnumber={},transactiondate={},billamount={},businessrecordnumber={}",
					fromaccountnumber, status, frompartyid, remark, subject, inputdate, transactiontype, transactionamount, businessnumber, transactiondate, billamount, businessrecordnumber);

			signMap.put("fromaccountnumber", fromaccountnumber);
			signMap.put("status", status);
			signMap.put("frompartyid", frompartyid);
			signMap.put("remark", remark);
			signMap.put("subject", subject);
			signMap.put("inputdate", inputdate);
			signMap.put("transactiontype", transactiontype);
			signMap.put("transactionamount", transactionamount);
			signMap.put("businessnumber", businessnumber);
			// signMap.put("tf_sign", request.getParameter("tf_sign"));
			signMap.put("transactiondate", transactiondate);
			signMap.put("billamount", billamount);
			signMap.put("businessrecordnumber", businessrecordnumber);
			signMap.put("dog_sk", "P44ut42635kUwv4K6Tg3");
			// signMap.put("dog_sk", "08Oe4YI71I5S3e72qYT2");
			String checkSign = ParamUtil.map2MD5(signMap);
			Boolean bSignVal = tf_sign.equals(checkSign);
			LOG.info("--TransfarDaiFuNotify checkSignResult={},originSign={},checkSign={}", bSignVal, tf_sign, checkSign);

			if (bSignVal) {
				// 设置后续信息
				PayOrder payOrder = new PayOrder();
				if (StringUtil.isNotBlank(transactionamount)) {
					payOrder.setPayAmt(new BigDecimal(billamount));
				}
				if (StringUtil.isNotBlank(transactionamount)) {
					payOrder.setRealAmt(new BigDecimal(transactionamount));
				}
				if (StringUtil.isNotBlank(transactiondate)) {
					payOrder.setPayFinishDate(transactiondate);
				}
				if (StringUtil.isNotBlank(businessrecordnumber)) {
					payOrder.setPayNo(businessrecordnumber);
				}
				if (StringUtil.isNotBlank(status)) {
					payOrder.setErrorCode(status);
				}
				if (StringUtil.isNotBlank(remark)) {
					payOrder.setErrorInfo(remark);
				}
				payOrder.setPayFinishDate(DateUtil.convertCurrentDateTimeToString());
				payOrder.setFinishDate(new Date());
				// payOrder.setErrorCode(errCode);
				// payOrder.setErrorInfo(errMsg);
				boolean success = false;
				if ("成功".equals(status)) {
					success = true;
				}
				userOrderService.affirmDaiFuNotify(success, businessnumber, payOrder);

				// TuserOrder tuserOrder =
				// userOrderService.getTorderByOrderNo(businessnumber);
				// Tuser user = tuserOrder.getUser();
				// try {
				// LOG.info("----准备结束代付订单{}----", businessnumber);
				// if ("成功".equals(status)) {
				// /* 处理成功 */
				// LOG.info("--TransfarDaiFuNotify 处理成功");
				// tuserOrder.setStatus(UserOrder.order_status.SUCCESS.getCode());
				// payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
				// if (UserOrder.trans_type.YJTX.getCode() ==
				// tuserOrder.getType()) {
				// accountService.updateBrokerageAccountAfterLiqSuccess(user.getId(),
				// tuserOrder);
				// } else {
				// accountService.updateAccountAfterLiqSuccess(user.getId(),
				// tuserOrder);
				// }
				// } else {
				// // 订单失败状态
				// LOG.info("--TransfarDaiFuNotify 订单失败");
				// tuserOrder.setStatus(UserOrder.order_status.FAILURE.getCode());
				// payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
				// /* 账户退款 */
				// if (UserOrder.trans_type.YJTX.getCode() ==
				// tuserOrder.getType()) {
				// accountService.updateBrokerageAccountAfterLiqFailure(user.getId(),
				// tuserOrder);
				// } else {
				// accountService.updateAccountAfterLiqFailure(user.getId(),
				// tuserOrder);
				// }
				// }
				// userOrderService.updatePayOrderAfterNotify(payOrder,
				// tuserOrder);
				// LOG.info("----END申孚代付订单{}----", businessnumber);
				// } catch (Exception e) {
				// LOG.error("----订单回调异常", e);
				// }

				return "success";
			} else {
				LOG.info("---申孚代付订单回调接口 -----验证参数失败----{}-----", businessnumber);
				return "false";
			}
		} catch (Exception e) {
			LOG.info("---申孚代付订单回调接口 end---------");
		}
		return "false";
	}

	@RequestMapping(value = "/weilianbaoDfNotify", method = { RequestMethod.POST }, produces = {"application/json"})
	@ResponseBody
	public String weilianbaoDfNotify(@RequestBody JSONObject request) throws Exception {
		LOG.info("weilianbaoDfNotify result={}", request.toJSONString());
		String trxType = request.getString("trxType");//
		String retCode = request.getString("retCode");//
		String retMsg = request.getString("retMsg");//
		String r1_merchantNo = request.getString("r1_merchantNo");//
		String r2_orderNumber = request.getString("r2_orderNumber");//
		String r3_amount = request.getString("r3_amount");//
		String r4_bankId = request.getString("r4_bankId");//
		String r5_business = request.getString("r5_business");//
		String r6_timestamp = request.getString("r6_timestamp");//
		String r7_completeDate = request.getString("r7_completeDate");//
		String r8_orderStatus = request.getString("r8_orderStatus");//
		String r9_serialNumber = request.getString("r9_serialNumber");//
		String r10_t0PayResult = request.getString("r10_t0PayResult");//
		String sign = request.getString("sign");//

		Map<String, String> consumeMap = new LinkedHashMap<String, String>();
		consumeMap.put("trxType", trxType);
		consumeMap.put("retCode", retCode);
		consumeMap.put("retMsg", retMsg);
		consumeMap.put("r1_merchantNo", r1_merchantNo);
		consumeMap.put("r2_orderNumber", r2_orderNumber);
		consumeMap.put("r3_amount", r3_amount);
		consumeMap.put("r4_bankId", r4_bankId);
		consumeMap.put("r5_business", r5_business);
		consumeMap.put("r6_timestamp", r6_timestamp);
		consumeMap.put("r7_completeDate", r7_completeDate);
		consumeMap.put("r8_orderStatus", r8_orderStatus);
		consumeMap.put("r9_serialNumber", r9_serialNumber);
		consumeMap.put("r10_t0PayResult", r10_t0PayResult);

		TweiLianBaoMerchantReport merchantReport = weiLianBaoYinLainService.findMerchantReport(r1_merchantNo);
		String signValue = WeiLianBaoSignUtil.signMd5(consumeMap, merchantReport.getSignKey());

		LOG.info("weilianbaoDfNotify sign-[SrcSign={}/TarSign={}]", sign, signValue);

		// Map<String, String> params = new HashMap<String, String>();
		// if (!"0000".equals(retCode) || !sign.equals(signValue)) {
		// LOG.info("weilianbaoConsumeNotify retCode={},retMsg={},merchantNo={},
		// ERROR", retCode, retMsg, r1_merchantNo);
		// return "Failed";
		// }

		PayOrder payOrder = new PayOrder();
		payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
		payOrder.setPayFinishDate(r6_timestamp);
		payOrder.setPayNo(r9_serialNumber);
		payOrder.setPayFinishDate(r6_timestamp);
		payOrder.setFinishDate(new Date());
		payOrder.setErrorCode(retCode);
		payOrder.setErrorInfo(retMsg);
		LOG.info("---WeiLianBao支付回调完结---------");
		userOrderService.finishInputOrderStatus(r2_orderNumber, payOrder);

		return "success";
	}

	@ResponseBody
	@RequestMapping(value = "/weilianbaoConsumeNotify", method = RequestMethod.POST)
	public String weilianbaoConsumeNotify(@RequestBody JSONObject requestJson) throws Exception {
		// TODO
		LOG.info("weilianbaoConsumeNotify result={}", requestJson.toJSONString());
		String trxType = requestJson.getString("trxType");//
		String retCode = requestJson.getString("retCode");//
		String retMsg = requestJson.getString("retMsg");//
		String r1_merchantNo = requestJson.getString("r1_merchantNo");//
		String r2_orderNumber = requestJson.getString("r2_orderNumber");//
		String r3_amount = requestJson.getString("r3_amount");//
		String r4_bankId = requestJson.getString("r4_bankId");//
		String r5_business = requestJson.getString("r5_business");//
		String r6_timestamp = requestJson.getString("r6_timestamp");//
		String r7_completeDate = requestJson.getString("r7_completeDate");//
		String r8_orderStatus = requestJson.getString("r8_orderStatus");//
		String r9_serialNumber = requestJson.getString("r9_serialNumber");//
		String r10_t0PayResult = requestJson.getString("r10_t0PayResult");//
		String sign = requestJson.getString("sign");//

		Map<String, String> consumeMap = new LinkedHashMap<String, String>();
		consumeMap.put("trxType", trxType);
		consumeMap.put("retCode", retCode);
		consumeMap.put("retMsg", retMsg);
		consumeMap.put("r1_merchantNo", r1_merchantNo);
		consumeMap.put("r2_orderNumber", r2_orderNumber);
		consumeMap.put("r3_amount", r3_amount);
		consumeMap.put("r4_bankId", r4_bankId);
		consumeMap.put("r5_business", r5_business);
		consumeMap.put("r6_timestamp", r6_timestamp);
		consumeMap.put("r7_completeDate", r7_completeDate);
		consumeMap.put("r8_orderStatus", r8_orderStatus);
		consumeMap.put("r9_serialNumber", r9_serialNumber);
		consumeMap.put("r10_t0PayResult", r10_t0PayResult);

		TweiLianBaoMerchantReport merchantReport = weiLianBaoYinLainService.findMerchantReport(r1_merchantNo);
		String signValue = WeiLianBaoSignUtil.signMd5(consumeMap, merchantReport.getSignKey());
		LOG.info("weilianbaoConsumeNotify sign-[SrcSign={}/TarSign={}]", sign, signValue);
		Map<String, String> params = new HashMap<String, String>();
		if (!"0000".equals(retCode) || !sign.equals(signValue)) {
			LOG.info("weilianbaoConsumeNotify  retCode={},retMsg={},merchantNo={}, ERROR", retCode, retMsg, r1_merchantNo);
			// return "failed";
		}
		PayOrder payOrder = new PayOrder();
		payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
		payOrder.setPayFinishDate(r6_timestamp);
		payOrder.setPayNo(r9_serialNumber);
		payOrder.setPayFinishDate(r6_timestamp);
		payOrder.setFinishDate(new Date());
		payOrder.setErrorCode(retCode);
		payOrder.setErrorInfo(retMsg);
		LOG.info("---WeiLianBao支付回调完结---------");
		userOrderService.finishInputOrderStatus(r2_orderNumber, payOrder);
		return "success";
	}

	@ResponseBody
	@RequestMapping(value = "/weilianbaoConsumeNotify2", method = RequestMethod.POST)
	public String weilianbaoConsumeNotify2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO
		Map<String, String[]> params = request.getParameterMap();
		Map<String, String> map = new HashMap<String, String>();
		for (String key : params.keySet()) {
			String[] values = params.get(key);
			if (values.length > 0) {
				map.put(key, values[0]);
			}
		}
		
		LOG.info("---微联宝银联积分直通车DO---异步回调响应请求request.Params---" + JSONObject.toJSONString(map));
		String trxType = request.getParameter("trxType");// OnlineQuery
		String retCode = request.getParameter("retCode");// 0000
		// String retMsg = request.getParameter("retMsg");//
		String r1_merchantNo = request.getParameter("r1_merchantNo");// B105244615
		String r2_orderNumber = request.getParameter("r2_orderNumber");// WLBZTCYLZXJ201804201040176650000000001
		String r3_amount = request.getParameter("r3_amount");// 2.00
		String r4_bankId = request.getParameter("r4_bankId");// KUAI
		String r5_business = request.getParameter("r5_business");// KUAI
		String r6_createDate = request.getParameter("r6_createDate");// 2018-04-20
																		// 10:40:16
		String r7_completeDate = request.getParameter("r7_completeDate");// 2018-04-20
																			// 10:40:47
		String r8_orderStatus = request.getParameter("r8_orderStatus");// SUCCESS
		String r9_withdrawStatus = request.getParameter("r9_withdrawStatus");// INIT
		// String r10_t0PayResult = request.getParameter("r10_t0PayResult");//
		String sign = request.getParameter("sign");// 61804c0c3354c20a441bd1f760dcda50
		//
		Map<String, String> consumeMap = new LinkedHashMap<String, String>();
		consumeMap.put("trxType", trxType);
		consumeMap.put("retCode", retCode);
		// consumeMap.put("retMsg", retMsg);
		consumeMap.put("r1_merchantNo", r1_merchantNo);
		consumeMap.put("r2_orderNumber", r2_orderNumber);
		consumeMap.put("r3_amount", r3_amount);
		consumeMap.put("r4_bankId", r4_bankId);
		consumeMap.put("r5_business", r5_business);
		consumeMap.put("r6_createDate", r6_createDate);
		consumeMap.put("r7_completeDate", r7_completeDate);
		consumeMap.put("r8_orderStatus", r8_orderStatus);
		consumeMap.put("r9_withdrawStatus", r9_withdrawStatus);
		// consumeMap.put("r10_t0PayResult", r10_t0PayResult);

		LOG.info("---微联宝银联积分直通车DO---异步回调响应请求Sign Source={}" + JSONObject.toJSONString(consumeMap));
		TweiLianBaoMerchantReport merchantReport = weiLianBaoYinLainService.findMerchantReport(r1_merchantNo);
		String signValue = WeiLianBaoSignUtil.signMd5(consumeMap, merchantReport.getSignKey());
		LOG.info("weilianbaoConsumeNotify2 sign-[SrcSign={}/TarSign={}]", sign, signValue);
		if (!"0000".equals(retCode) || !sign.equals(signValue)) {
			LOG.info("weilianbaoConsumeNotify  retCode={},orderNumber={},merchantNo={}, ERROR", retCode, r2_orderNumber, r1_merchantNo);
			// return "failed";
		}
		PayOrder payOrder = new PayOrder();
		payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
		payOrder.setPayFinishDate(r7_completeDate);
		payOrder.setPayNo(r2_orderNumber);
		payOrder.setFinishDate(DateUtil.getDateTimeFromString(r7_completeDate));
		payOrder.setErrorCode(retCode);
		payOrder.setErrorInfo(r8_orderStatus);
		LOG.info("---WeiLianBao支付回调完结---------");
		userOrderService.finishInputOrderStatus(r2_orderNumber, payOrder);
		return "success";
	}

	/**
	 * 哲扬通道，app请求进行确认支付 下单经过银联页面支付，那么可以省略这一步
	 */
	@ResponseBody
	@RequestMapping(value = "/zheYangFastpayCheckMessage", method = { RequestMethod.POST, RequestMethod.GET })
	public String zheyangAppFastpayCheckMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 等待通道模式确定是直清还是二清后,然后再写逻辑。 测试代码模板详见 ZheYangPaymentServiceImplTest2.java

		return "";
	}

}
