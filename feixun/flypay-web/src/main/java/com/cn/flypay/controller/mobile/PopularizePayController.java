package com.cn.flypay.controller.mobile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.payment.TroughTrainServeice;
import com.cn.flypay.service.sys.DictionaryService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.SmsValidateService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.trans.BrokerageService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.alipay.Alipay;
import com.cn.flypay.utils.channel.WeixinUtil;
import com.cn.flypay.utils.qrcode.QRCodeUtil;

@Controller
@RequestMapping({ "/popularizePay" })
public class PopularizePayController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserService userService;
	@Autowired
	private BrokerageService brokerageService;
	@Autowired
	private SmsValidateService smsValidateService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private SysParamService sysParamService;
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private UserOrderService userOrderService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private TroughTrainServeice troughTrainServeice;
	@Autowired
	private SysParamService paramService;
	@Autowired
	private HolidayService holidayService;

	@RequestMapping(value = { "/paypage" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView paypage(HttpServletRequest request, @RequestHeader("user-agent") String ua) {
		this.logger.info("paypage userCode={},ua={}", request.getParameter("userCode"), ua);
		ModelAndView mv = new ModelAndView("payment/payHead");
		if ((ua.toLowerCase().contains("micromessenger")) || (ua.toLowerCase().contains("alipay"))) {
			String pcode = request.getParameter("userCode");
			if (StringUtil.isNotBlank(pcode)) {
				try {
					User user = this.userService.getByCode(pcode.substring(0, 6));
					if (user != null) {
						request.setAttribute("inputUserCode", pcode);
						request.setAttribute("userName", StringUtil.isNotBlank(user.getMerchantName()) ? user.getMerchantName() : user.getOrganizationAppName());
					} else {
						request.setAttribute("errorInfo", "该用户不存在，请您确定您的收款码正确");
					}
				} catch (Exception e) {
					this.logger.error("支付商家失败pcode=" + pcode, e);
				}
			}
		} else {
			request.setAttribute("errorInfo", "请使用微信或支付宝扫描收款二维码");
		}
		return mv;
	}

	@ResponseBody
	@RequestMapping(value = { "/paymentOrder" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String paymentOrder(HttpServletRequest request, @RequestParam String amt, @RequestParam String inputUserCode, @RequestHeader("user-agent") String ua) {
		JSONObject json = new JSONObject();
		json.put("status", "FAILURE");
		logger.info("paypage amt={},inputUserCode={},ua={}", amt, inputUserCode, ua);

		if (StringUtil.isNotBlank(inputUserCode)) {
			try {
				String lengthCode = inputUserCode.substring(0, 6);
				User user = this.userService.getByCode(lengthCode);
				if (ua.toLowerCase().contains("micromessenger")) {
					ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.WXQR.getCode(), BigDecimal.valueOf(Double.parseDouble(amt)), user.getUserType(), user.getId(), 1);

					Map<String, String> returnMap = cpr.getChannelPaymentService().createUnifiedOrder(user, cpr, 0, Double.valueOf(Double.parseDouble(amt)), Integer.valueOf(UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode()), 0,
							String.format("您正在向%s支付%s元", new Object[] { user.getLoginName(), amt }));
					if (returnMap != null) {
						if (((String) returnMap.get("return_code")).equals("SUCCESS")) {
							if (((String) returnMap.get("result_code")).equals("SUCCESS")) {
								String path = request.getContextPath() + "/popularizePay/qrcode?qrurl=";
								String url = "https://" + request.getServerName() + path + URLEncoder.encode((String) returnMap.get("code_url"));
								json.put("qrurl", url);
								json.put("status", "SUCCESS");
							} else {
								this.logger.info("Wechatpay business error : err_code " + (String) returnMap.get("err_code") + ", err_code_des " + (String) returnMap.get("err_code_des"));
							}
						} else {
							this.logger.info("Wechatpay communication error : " + (String) returnMap.get("return_msg"));
						}
					} else {
						this.logger.info("Wechatpay error : can not request api");
					}
				} else if (ua.toLowerCase().contains("alipay")) {
					ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.ALQR.getCode(), BigDecimal.valueOf(Double.parseDouble(amt)), user.getUserType(), user.getId(), 1);
					Map<String, String> payMap = cpr.getChannelPaymentService().createUnifiedOrder(user, cpr, 0, Double.valueOf(Double.parseDouble(amt)), Integer.valueOf(UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode()), 0,
							String.format("您正在向%s支付%s元", new Object[] { user.getLoginName(), amt }));
					if ((payMap != null) && (((String) payMap.get("return_code")).equals("SUCCESS")) && (((String) payMap.get("result_code")).equals("SUCCESS"))) {
						String path = request.getContextPath() + "/popularizePay/qrcode?qrurl=";
						String url = "https://" + request.getServerName() + path + URLEncoder.encode((String) payMap.get("code_url"));
						json.put("qrurl", url);
						json.put("status", "SUCCESS");
					}
				} else {
					request.setAttribute("errorInfo", "请使用微信或支付宝扫描收款二维码");
				}
			} catch (Exception e) {
				this.logger.error("支付商家失败code=" + inputUserCode, e);
			}
		}
		return json.toString();
	}

	@RequestMapping(value = { "/qrcode" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void popularizeQrcode(HttpServletRequest request, HttpServletResponse resp) {
		try {
			String qrurl = request.getParameter("qrurl");
			logger.info("popularizeQrcode qrurl={}", qrurl);
			BufferedImage image = QRCodeUtil.createImage(qrurl, null, true);
			ServletOutputStream sos = resp.getOutputStream();
			ImageIO.write(image, "jpeg", sos);
			sos.close();
		} catch (Exception e) {
			this.logger.error(e.getMessage());
		}
	}

	/**
	 * 收款码线上支付
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/payOnlinePage", method = RequestMethod.GET)
	public void payOnlinePage(HttpServletRequest request, HttpServletResponse response, @RequestHeader("user-agent") String ua) {
		if (ua.toLowerCase().contains("micromessenger") || ua.toLowerCase().contains("alipay") || ua.toLowerCase().contains("walletclient") || ua.toLowerCase().contains("bestpay")) {
			String pcode = request.getParameter("userCode");
			logger.info("payOnlinePage pcode={},ua={}", pcode, ua);
			if (StringUtil.isNotBlank(pcode)) {
				try {
					String userCode = pcode.substring(0, 6);
					User user = userService.getByCode(userCode);
					
					//为防止聚合码支付出现意外状况，加上开关  1开启 2只开启卢总的账号 0关闭
					Map<String, String> sysParams =  paramService.searchSysParameter();
					if(sysParams.get("centralization_pay_switch_on")== null || !sysParams.get("centralization_pay_switch_on").equals("1")){
						if(	StringUtil.isBlank(sysParams.get("centralization_pay_switch_on")) 
								|| sysParams.get("centralization_pay_switch_on").equals("0")
								|| !( sysParams.get("centralization_pay_switch_on").equals("2") &&  user.getId()==2 )
								){
							request.setAttribute("errorInfo", "二维码交易升级中");
							
						}
					}else if (user != null) {
						request.setAttribute("inputUserCode", pcode);
						request.setAttribute("userName", StringUtil.isNotBlank(user.getMerchantName()) ? user.getMerchantName() : user.getOrganizationAppName());
						if (ua.toLowerCase().contains("micromessenger")) {
							logger.info("------聚合收款码------微信----调用----start");
							String notifyUrl = URLEncoder.encode((String) ApplicatonStaticUtil.getAppStaticData("wxaccount.ggh.auth_url") + "?inputUserCode=" + userCode, "utf-8");
							String notifyUrlBeforeEnCode = (String) ApplicatonStaticUtil.getAppStaticData("wxaccount.ggh.auth_url") + "?inputUserCode=" + userCode;
//							String notifyUrl = URLEncoder.encode("http://1g83849h98.iask.in:34530/flypayfx/popularizePay/weinxinAuth?inputUserCode="+userCode , "utf-8");
//							String notifyUrlBeforeEnCode = "http://1g83849h98.iask.in:34530/flypayfx/popularizePay/weinxinAuth?inputUserCode="+userCode;
							logger.info("------聚合收款码------微信----编码前的notifyUrl=" + notifyUrlBeforeEnCode);
							String url = String.format(WeixinUtil.GET_OAUTH_CODE, sysParamService.searchSysParameter().get("wxaccount.ggh.appid"), notifyUrl, "code", "snsapi_base", userCode);
							logger.info("------聚合收款码------微信----开始访问微信url=" + url);
							try {
								response.sendRedirect(url);
							} catch (IOException e) {
								logger.error("微信公共号支付异常", e);
							}

							// 民生服务窗

						} else if (ua.toLowerCase().contains("alipay")) {
						   //支付宝--聚合支付
							String notifyUrl = URLEncoder.encode((String) ApplicatonStaticUtil.getAppStaticData("alipay.auth_url"), "utf-8");
							String url = String.format(Alipay.AUTH_CODE_URL, Alipay.appId, "auth_base", userCode, notifyUrl);
							try {
								response.sendRedirect(url);
							} catch (IOException e) {
								logger.error("支付宝服务窗支付异常", e);
							}
						}

						// } else if (ua.toLowerCase().contains("alipay")) {
						// try {
						//
						// String prefix = "https://";
						// if (!"bbpurse.com".equals(request.getServerName())) {
						// prefix = "http://";
						// }
						// String path = request.getContextPath() +
						// "/popularizePay/alipayNoneAuth?inputUserCode=" +
						// userCode + "&userId=" + user.getId();
						//
						// String url = prefix + request.getServerName() + path;
						// logger.info(url);
						// response.sendRedirect(url);
						// } catch (IOException e) {
						// logger.error("支付宝服务窗支付异常", e);
						// }
						// }

						else if (ua.toLowerCase().contains("walletclient")) {
							//京东--聚合支付
							try {

								String prefix = "https://";
								if (!"bbpurse.com".equals(request.getServerName())) {
									prefix = "http://";
								}
								String path = request.getContextPath() + "/popularizePay/jidongNoneAuth?inputUserCode=" + userCode + "&userId=" + user.getId();

								String url = prefix + request.getServerName() + path;
								logger.info(url);
								response.sendRedirect(url);
							} catch (IOException e) {
								logger.error("京东线上支付异常", e);
							}
						} else if (ua.toLowerCase().contains("bestpay")) {
							//翼支付--聚合支付
							try {

								String prefix = "https://";
								if (!"bbpurse.com".equals(request.getServerName())) {
									prefix = "http://";
								}
								String path = request.getContextPath() + "/popularizePay/yizhifuNoneAuth?inputUserCode=" + userCode + "&userId=" + user.getId();
								String url = prefix + request.getServerName() + path;
								logger.info(url);
								response.sendRedirect(url);
							} catch (IOException e) {
								logger.error("翼支付线上支付异常", e);
							}
						}

					} else {
						request.setAttribute("errorInfo", "该商户不存在，请您确定您的收款码是否正确");
					}

				} catch (Exception e) {
					logger.error("支付商家失败pcode=" + pcode, e);
				}
			}
		} else {
			request.setAttribute("errorInfo", "请使用微信、支付宝、京东钱包扫描收款二维码");
		}
	}

	/**
	 * 聚合码支付--微信扫描后的回调
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/weinxinAuth", method = RequestMethod.GET)
	public ModelAndView weinxinAuth(HttpServletRequest request, String inputUserCode, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("payment/payOnLineHead");	//跳转至页面
		String code = request.getParameter("code");
		logger.info("weinxinAuth code={},inputUserCode={}", code, inputUserCode);
		if (!StringUtil.isBlank(code)) {// 如果request中包括code，则是微信回调
			try {
				String tokenString = String.format(WeixinUtil.GET_OAUTH_TOKEN, sysParamService.searchSysParameter().get("wxaccount.ggh.appid"), sysParamService.searchSysParameter().get("wxaccount.ggh.key"), code);
				String resp = WeixinUtil.httpsStringRequest(tokenString, "GET", null, null);
				JSONObject json = JSONObject.parseObject(resp);
				request.setAttribute("userId", json.getString("openid"));	//表示微信用户身份的ID
				request.setAttribute("payType", UserOrder.trans_type.WXOL.getCode());
				request.setAttribute("inputUserCode", inputUserCode);
				User user = userService.getByCode(inputUserCode);
				logger.info("微信聚合码扫描后的回调：user_id为"+user.getId()+",微信返回的openId为"+json.getString("openid"));
				request.setAttribute("userName", StringUtil.isNotBlank(user.getMerchantName()) ? user.getMerchantName() : user.getOrganizationAppName());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return mv;
	}

	/**
	 * 聚合码支付--支付宝扫描后的回调
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/alipayAuth", method = RequestMethod.GET)
	public ModelAndView alipayAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView mv = new ModelAndView("payment/payOnLineHead");
		Map<String, String[]> requestParams = request.getParameterMap();
		logger.info(JSONObject.toJSONString(requestParams));
		AlipayClient client = new DefaultAlipayClient(Alipay.URL, Alipay.appId, Alipay.privateKey, "json", "utf-8", Alipay.alipayPublicKey);
		AlipaySystemOauthTokenRequest tokenRequest = new AlipaySystemOauthTokenRequest();
		tokenRequest.setCode(requestParams.get("auth_code")[0]);
		tokenRequest.setGrantType("authorization_code");
		try {
			AlipaySystemOauthTokenResponse alipayresponse = client.execute(tokenRequest);
			logger.info(alipayresponse.toString());
			request.setAttribute("userId", alipayresponse.getUserId());
			String inputUserCode = requestParams.get("state")[0];
			request.setAttribute("inputUserCode", inputUserCode);
			request.setAttribute("payType", UserOrder.trans_type.ALOL.getCode());
			User user = userService.getByCode(inputUserCode);
			request.setAttribute("userName", StringUtil.isNotBlank(user.getMerchantName()) ? user.getMerchantName() : user.getOrganizationAppName());
		} catch (AlipayApiException e) {
			logger.error(e.getMessage());
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/alipayNoneAuth", method = RequestMethod.GET)
	public ModelAndView alipayNoneAuth(HttpServletRequest request, @RequestParam String userId, @RequestParam String inputUserCode) throws IOException {
		ModelAndView mv = new ModelAndView("payment/payOnLineHead");
		logger.info("alipayNoneAuth userId={},inputUserCode={}", userId, inputUserCode);
		request.setAttribute("userId", userId);
		request.setAttribute("inputUserCode", inputUserCode);
		request.setAttribute("payType", UserOrder.trans_type.ALOL.getCode());
		User user = userService.getByCode(inputUserCode);
		request.setAttribute("userName", StringUtil.isNotBlank(user.getMerchantName()) ? user.getMerchantName() : user.getOrganizationAppName());
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/jidongNoneAuth", method = RequestMethod.GET)
	public ModelAndView jidongNoneAuth(HttpServletRequest request, @RequestParam String userId, @RequestParam String inputUserCode) throws IOException {
		ModelAndView mv = new ModelAndView("payment/payOnLineHead");
		logger.info("jidongNoneAuth userId={},inputUserCode={}", userId, inputUserCode);
		request.setAttribute("userId", userId);
		request.setAttribute("inputUserCode", inputUserCode);
		request.setAttribute("payType", UserOrder.trans_type.JDOL.getCode());
		User user = userService.getByCode(inputUserCode);
		request.setAttribute("userName", StringUtil.isNotBlank(user.getMerchantName()) ? user.getMerchantName() : user.getOrganizationAppName());
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/yizhifuNoneAuth", method = RequestMethod.GET)
	public ModelAndView yizhifuNoneAuth(HttpServletRequest request, @RequestParam String userId, @RequestParam String inputUserCode) throws IOException {
		ModelAndView mv = new ModelAndView("payment/payOnLineHead");
		logger.info("yizhifuNoneAuth userId={},inputUserCode={}", userId, inputUserCode);
		request.setAttribute("userId", userId);
		request.setAttribute("inputUserCode", inputUserCode);
		request.setAttribute("payType", UserOrder.trans_type.YIOL.getCode());
		User user = userService.getByCode(inputUserCode);
		request.setAttribute("userName", StringUtil.isNotBlank(user.getMerchantName()) ? user.getMerchantName() : user.getOrganizationAppName());
		return mv;
	}

	@ResponseBody
	@RequestMapping(value = "/paymentOnlineOrder", method = RequestMethod.POST)
	public String paymentOnlineOrder(HttpServletRequest request, @RequestParam String amt, @RequestParam String inputUserCode, @RequestParam String userId, @RequestParam String payType, @RequestHeader("user-agent") String ua) {
		JSONObject json = new JSONObject();
		json.put("status", "FAILURE");
		logger.info("paymentOnlineOrder amt={}, payType={},userId={},inputUserCode={},ua={}", new Object[] { amt, payType, userId, inputUserCode, ua });
		if (StringUtil.isNotBlank(inputUserCode) && StringUtil.isNotBlank(amt) && StringUtil.isNotBlank(userId) && StringUtil.isNotBlank(payType)) {
			logger.info("开始进行线上支付" + inputUserCode + " 支付类型:" + payType);
			try {
				/* 判断用户输入的金额是否为正整数 且后三位不相同 */
				if (StringUtil.isLegalAMT(amt)) {
					
					Integer amtInt = Integer.valueOf(amt);
					if(amtInt < 1){
						logger.info("线上支付交易金额小于1元");
						json.put("amtError", "小主，交易金额不能低于1元哦");
						return json.toString();
					}
					
					
					logger.info("开始进行线上支付" + inputUserCode);
					String lengthCode = inputUserCode.substring(0, 6);
					User user = userService.getByCode(lengthCode);
					Map<String, String> params = new HashMap<String, String>();
					params.put("userId", userId);

					String flag = userService.isAllowUserPay(user.getId(), UserOrder.trans_type.WXQR.name());
					logger.info("开始进行线上支付" + inputUserCode);
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {

						String stmFlag = holidayService.isLimitTimeInterval(new Date(), true, null);
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {
							logger.info("开始进行线上支付" + stmFlag);
							Integer onLinePayType = Integer.parseInt(payType);
							
							//切换至平安直清模式，使用直通车通道
							ChannelPayRef cpr = troughTrainServeice.getChannelPayRef(false, String.valueOf(onLinePayType), user.getId(), "","PINGANPAYZHITONGCHE_ZHIQING");
							
							
							//平安切换至直清模式，需要重新报备商户---开始
							if(cpr==null){
								//1,检查老的PINGANPAYZHITONGCHE通道是否存在
								ChannelPayRef cprold = troughTrainServeice.getChannelPayRef(false, String.valueOf(onLinePayType),  user.getId(), "","PINGANPAYZHITONGCHE");
								if(cprold!=null){
									//2,检查该老通道本地保存的信息是否与平安一致,不一致则将通道设置为失效
									Map<String, String> checkRes = troughTrainServeice.checkPingAnChannelCorrect(cprold.getChannel().getId());
									if(checkRes.get("code").equals("000")){
										//3,重新开通直清模式的PINGNAPAYZHITONGCHE_ZHIQING通道
										JSONObject openPingAnRes = troughTrainServeice.createPingAnZhiQingMer(checkRes.get("name"), checkRes.get("aliasName"), user.getId());
										if(openPingAnRes.getString("code").equals("000")){
											cpr = troughTrainServeice.getChannelPayRef(false, String.valueOf(onLinePayType), user.getId(), "","PINGANPAYZHITONGCHE_ZHIQING");
										}
									}
								}
							}
							//平安切换至直清模式，需要重新报备商户---结束
							
							
							
							if (ua.toLowerCase().contains("micromessenger") && onLinePayType == UserOrder.trans_type.WXOL.getCode()) {
								//微信线上 320
//								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.WXOL.getCode(), BigDecimal.valueOf(Double.parseDouble(amt)), user.getUserType(),user.getId(), 1);
								
								if (cpr != null) {

									Map<String, String> result = cpr.getChannelPaymentService().createOnLineOrder(user, cpr, 1, Double.parseDouble(amt), UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode(),
											String.format(WeixinUtil.DESC_GROP_PAY, StringUtil.isNotBlank(user.getMerchantName()) ? "商户-" + user.getMerchantName() : user.getLoginName(), amt), params);
									if(result!=null && result.containsKey("return_code") && result.get("return_code").equals("make_wx_jsapi_path_error")){
										//处理微信子商户授权目录配置错误
										request.setAttribute("errorInfo",result.get("return_message"));
									}else if (result != null && (StringUtil.isNotBlank(result.get("wxjsapiStr")) || StringUtil.isNotBlank(result.get("PINGANPAY.url")))) {
										json.put("status", "SUCCESS");
										json.put("channelName", cpr.getChannel().getName());
										json.put("wxjsapiStr", result.get("wxjsapiStr"));
										if (cpr.getChannel().getName().equals("PINGANPAYZHITONGCHE_ZHIQING")) {
											json.put("url", result.get("PINGANPAY.url"));
										}
									} else {
										request.setAttribute("errorInfo", "微信通道异常，请您重新扫码");
									}
								} else {
									request.setAttribute("errorInfo", "无该支付类型通道，请客服联系");
								}
							} else if (ua.toLowerCase().contains("alipay") && onLinePayType == UserOrder.trans_type.ALOL.getCode()) {
								//支付宝线上  220
//								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.ALOL.getCode(), BigDecimal.valueOf(Double.parseDouble(amt)), user.getUserType(),user.getId(), 1);
								
								
								if (cpr != null) {
									// 收款码暂时走 支付宝D0大额费率,所以，入账类型填写10 代表大额D0
									Map<String, String> result = cpr.getChannelPaymentService().createOnLineOrder(user, cpr, 11, Double.parseDouble(amt), UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode(),
											String.format(WeixinUtil.DESC_GROP_PAY, StringUtil.isNotBlank(user.getRealName()) ? "商户-" + user.getRealName() : user.getLoginName(), amt), params);
									
									if(result!=null && result.containsKey("return_code") && result.get("return_code").equals("open_ali_error")){
										//处理支付宝入驻失败的处理
										request.setAttribute("errorInfo",result.get("return_message"));
									}else if (result != null && (StringUtil.isNotBlank(result.get("channelNo")) || StringUtil.isNotBlank(result.get("PINGANPAY.url")))) {
										json.put("status", "SUCCESS");
										json.put("url", result.get("channelNo"));
										json.put("channelName", cpr.getChannel().getName());
										json.put("channelNo", result.get("channelNo"));
										if (cpr.getChannel().getName().equals("PINGANPAYZHITONGCHE_ZHIQING")) {
											json.put("url", result.get("PINGANPAY.url"));
										}
									} else {
										request.setAttribute("errorInfo", "支付宝通道异常，请您重新扫码");
									}
								} else {
									request.setAttribute("errorInfo", "无该支付类型通道，请客服联系");
								}
							} else if (ua.toLowerCase().contains("walletclient") && onLinePayType == UserOrder.trans_type.JDOL.getCode()) {
								//京东线上  920
//								ChannelPayRef cpr = routeService.checkChannelPayRoute(onLinePayType, BigDecimal.valueOf(Double.parseDouble(amt)), user.getUserType(), user.getId(), 1);
								
								if (cpr != null) {
									Map<String, String> result = cpr.getChannelPaymentService().createOnLineOrder(user, cpr, 1, Double.parseDouble(amt), UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode(),
											String.format(WeixinUtil.DESC_GROP_PAY, StringUtil.isNotBlank(user.getRealName()) ? "商户-" + user.getRealName() : user.getLoginName(), amt), params);
									if (result != null && (StringUtil.isNotBlank(result.get("channelNo")) || StringUtil.isNotBlank(result.get("PINGANPAY.url")))) {
										json.put("status", "SUCCESS");
										json.put("channelName", cpr.getChannel().getName());
										if (cpr.getChannel().getName().equals("PINGANPAYZHITONGCHE_ZHIQING")) {
											json.put("url", result.get("PINGANPAY.url"));
										}
									} else {
										request.setAttribute("errorInfo", "京东通道异常，请您重新扫码");
									}
								} else {
									request.setAttribute("errorInfo", "无该支付类型通道，请客服联系");
								}
							} else if (ua.toLowerCase().contains("bestpay") && onLinePayType == UserOrder.trans_type.YIOL.getCode()) {
								//翼支付线上 1120
//								ChannelPayRef cpr = routeService.checkChannelPayRoute(onLinePayType, BigDecimal.valueOf(Double.parseDouble(amt)), user.getUserType(), user.getId(), 1);
								
								if (cpr != null) {
									Map<String, String> result = cpr.getChannelPaymentService().createOnLineOrder(user, cpr, 1, Double.parseDouble(amt), UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode(),
											String.format(WeixinUtil.DESC_GROP_PAY, StringUtil.isNotBlank(user.getRealName()) ? "商户-" + user.getRealName() : user.getLoginName(), amt), params);
									if (result != null && (StringUtil.isNotBlank(result.get("channelNo")) || StringUtil.isNotBlank(result.get("PINGANPAY.url")))) {
										json.put("status", "SUCCESS");
										json.put("channelName", cpr.getChannel().getName());
										if (cpr.getChannel().getName().equals("PINGANPAYZHITONGCHE_ZHIQING")) {
											json.put("url", result.get("PINGANPAY.url"));
										}
									} else {
										request.setAttribute("errorInfo", "翼支付通道异常，请您重新扫码");
									}
								} else {
									request.setAttribute("errorInfo", "请使用微信、支付宝、京东钱包扫描收款二维码");
								}
							} else {
								request.setAttribute("errorInfo", "无该支付类型通道，请客服联系");
							}
						} else {
							request.setAttribute("errorInfo", stmFlag);
						}
					}
				} else {
					request.setAttribute("errorInfo", GlobalConstant.map.get(GlobalConstant.RESP_CODE_069));
				}
			} catch (Exception e) {
				logger.error("支付商家失败code=" + inputUserCode, e);
			}
		} else {
			logger.info("线上支付时，参数缺失");
		}
		return json.toString();
	}
}
