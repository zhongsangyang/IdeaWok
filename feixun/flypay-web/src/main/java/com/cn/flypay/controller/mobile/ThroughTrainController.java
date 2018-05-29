package com.cn.flypay.controller.mobile;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.model.sys.TweiLianBaoMerchantReport;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.SysInformation;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.account.AccountPointService;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.common.UserTokenService;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.payment.TroughTrainServeice;
import com.cn.flypay.service.payment.WeiLianBaoYinLainService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.CreditCardReportService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.InfoListService;
import com.cn.flypay.service.sys.JiguangPushService;
import com.cn.flypay.service.sys.NewsInforMationService;
import com.cn.flypay.service.sys.OrgChannelUserRateConfigService;
import com.cn.flypay.service.sys.OrgPointConfigService;
import com.cn.flypay.service.sys.PayTypeLimitConfigService;
import com.cn.flypay.service.sys.SysInformationPhotoService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.AESCodeUtil;
import com.cn.flypay.utils.DESutil;
import com.cn.flypay.utils.MD5Util;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.weilianbao.WeiLianBaoPayUtil;
import com.cn.flypay.utils.weilianbao.WeiLianBaoSignUtil;

import java.io.PrintWriter;

/**
 * 民生直通车
 * 
 * @author LW
 *
 */
@Controller
@RequestMapping("/mobile")
public class ThroughTrainController {

	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Value("${TroughType}")
	private String TroughType;
	@Autowired
	private CommonService commonService;
	@Autowired
	private TroughTrainServeice troughTrainServeice;
	@Autowired
	private UserService userService;
	@Autowired
	private HolidayService holidayService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private UserTokenService userTokenService;
	@Autowired
	private OrgPointConfigService orgPointConfigService;
	@Autowired
	private PayTypeLimitConfigService payTypeLimitConfigService;
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	@Autowired
	private AccountPointService accountPointService;
	@Autowired
	private NewsInforMationService newsInforMationService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private SysInformationPhotoService sysInformationPhotoService;
	@Autowired
	private OrgChannelUserRateConfigService orgChannelUserRateConfigService;
	@Autowired
	private JiguangPushService jiguangPushService;
	@Autowired
	private InfoListService infoService;
	@Autowired
	private CreditCardReportService creditCardReportService;
	@Autowired
	ChannelPaymentService weiLianBaoPaymentService;
	@Autowired
	private WeiLianBaoYinLainService weiLianBaoYinLainService;

	/**
	 * 直通车检查子商户
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/throughtrainCheck", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject throughtrainCheck(HttpServletRequest request) {
		LOG.info("----直通车模式检查是否有子商户start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			LOG.info("throughtrainCheck json={}", json.toJSONString());
			String merId = json.getString("merId");
			if (StringUtil.isEmpty(token) || userTokenService.isLegalUserToken(Long.parseLong(merId), token) == false) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				return commonService.getEncryptBody(returnJson);
			}
			if (StringUtils.isEmpty(merId)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				return commonService.getEncryptBody(returnJson);
			}

			// 检查直清通道PINGANPAYZHITONGCHE_ZHIQING是否存在
			Map<String, String> mapobj_zhiqing = troughTrainServeice.editChannlePAPayZHIQING(Long.parseLong(merId));
			if (mapobj_zhiqing.get("respCode").equals("999")) {
				// 检查老平安通道PINGANPAYZHOTONGCHE是否存在
				Map<String, String> mapobj = troughTrainServeice.editChannlePAPay(Long.parseLong(merId));
				if (mapobj.get("respCode").equals("999")) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "未开通商户");
					return commonService.getEncryptBody(returnJson);
				}
			}

			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			return commonService.getEncryptBody(returnJson);
		}
		LOG.info("----直通车模式检查是否有子商户 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 直通车费率查询
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryfeethroughtrain", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryfeethroughtrain(HttpServletRequest request) {
		LOG.info("----直通车模式查询费率start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			LOG.info("queryfeethroughtrain json={}", json.toJSONString());
			String merId = json.getString("merId");
			String agentId = json.getString("agentId");
			String appType = json.getString("appType");
			if (StringUtil.isEmpty(token) || userTokenService.isLegalUserToken(Long.parseLong(merId), token) == false) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				return commonService.getEncryptBody(returnJson);
			}
			JSONArray ja = new JSONArray();
			List<Map<String, String>> chls = channelService.findShowChannelLimitZTC(Long.parseLong(merId), agentId);
			ja.addAll(chls);
			// JSONArray ja = new JSONArray();
			// List<Map<String, String>> chls =
			// troughTrainServeice.getqueryfeethroughtrain();
			// ja.addAll(chls);

			returnJson.put("throughInfo", ja);
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			return commonService.getEncryptBody(returnJson);
		}
		LOG.info("----直通车模式查询费率 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 直通车费率查询
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryfeethroughtrainTwo", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryfeethroughtrainTwo(HttpServletRequest request) {
		LOG.info("----直通车模式查询费率start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			LOG.info("queryfeethroughtrainTwo json={}", json.toJSONString());
			String merId = json.getString("merId");
			String agentId = json.getString("agentId");
			if (StringUtil.isEmpty(token) || userTokenService.isLegalUserToken(Long.parseLong(merId), token) == false) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				return commonService.getEncryptBody(returnJson);
			}
			List<Map<String, String>> chls = channelService.findShowChannelLimitZTC(Long.parseLong(merId), agentId);
			JSONArray ja = new JSONArray();
			ja.addAll(chls);
			returnJson.put("throughInfo", ja);
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			return commonService.getEncryptBody(returnJson);
		}
		LOG.info("----直通车模式查询费率 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 直通车开通子商户
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/creatCommercial", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject creatCommercial(HttpServletRequest request) {
		LOG.info("----直通车开通子商户 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			LOG.info("creatCommercial json={}", json.toJSONString());
			String merId = json.getString("merId");
			if (StringUtil.isEmpty(token) || userTokenService.isLegalUserToken(Long.parseLong(merId), token) == false) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008); // 用户登录状态失效或在其他地方登录，请重新登录
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				return commonService.getEncryptBody(returnJson);
			}

			String merchantName = json.getString("merchantName");
			String shortName = json.getString("shortName");
			String shortType = json.getString("shortType");
			if (StringUtils.isEmpty(merchantName) || StringUtils.isEmpty(shortName) || StringUtils.isEmpty(shortName) || StringUtils.isEmpty(merId) || StringUtils.isEmpty(shortType)) {
				// returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				// //参数非法
				// returnJson.put("respDesc",
				// GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));

				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", "开通失败，请检查填入信息是否有误");
				return commonService.getEncryptBody(returnJson);
			}
			String regex = "^[\u4e00-\u9fa5]*$";
			if (!merchantName.matches(regex)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", "商户名称必须为中文");
				return commonService.getEncryptBody(returnJson);
			}

			/*
			 * //平安大商户模式转直清模式前的老逻辑 --开始 //开始入驻商户 if
			 * (!troughTrainServeice.addcreatCommercialPAPay(merchantName,
			 * shortName, Long.parseLong(merId),shortType) ||
			 * !troughTrainServeice.addcreatCommercialPAPayForZhiFuBaoAlone(
			 * merchantName, shortName, Long.parseLong(merId),shortType)) {
			 * 
			 * returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
			 * returnJson.put("respDesc", "开通失败，请检查填入信息是否有误"); return
			 * commonService.getEncryptBody(returnJson); }
			 * 
			 * //平安普通类型子商户开通完成后，开始补充报备翼支付 ChannelPayRef cprWx =
			 * troughTrainServeice.getChannelPayRef(false, "300",
			 * Long.parseLong(merId), "0","PINGANPAYZHITONGCHE"); JSONObject
			 * configWx =JSONObject.parseObject(cprWx.getChannel().getConfig());
			 * if(!troughTrainServeice.addPingAnCreateYZFMer(configWx.getString(
			 * "pingan.merchant_id"), Long.valueOf(merId))){
			 * returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
			 * returnJson.put("respDesc", "子商户开通错误"); return
			 * commonService.getEncryptBody(returnJson); } //平安大商户模式转直清模式前的老逻辑
			 * --结束
			 */

			// 平安大商户模式转直清模式--开始
			JSONObject openPingAnRes = troughTrainServeice.createPingAnZhiQingMer(merchantName, shortName, Long.parseLong(merId));
			LOG.info("----直通车开通子商户 ----入驻结果为：" + openPingAnRes.toJSONString());
			if (!openPingAnRes.getString("code").equals("000")) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", "小主，今日注册人数过多，路上有点堵，请重新提交申请。");
				return commonService.getEncryptBody(returnJson);
			}
			// 平安大商户模式转直清模式--结束

			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", "开通成功，您可以收款啦");
		} catch (Exception e) {
			e.printStackTrace();
			// returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			// returnJson.put("respDesc",
			// GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
			returnJson.put("respDesc", "开通失败，请检查填入信息是否有误");
			return commonService.getEncryptBody(returnJson);
		}
		LOG.info("----直通车开通子商户 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 直通车模式收款
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/throughtrain", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject throughtrain(HttpServletRequest request) {
		LOG.info("----直通车模式 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			LOG.info("throughtrain json={}", json.toJSONString());
			String merId = json.getString("merId");
			if (StringUtil.isEmpty(token) || userTokenService.isLegalUserToken(Long.parseLong(merId), token) == false) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				return commonService.getEncryptBody(returnJson);
			}

			String agentId = json.getString("agentId");
			String transPayType = json.getString("transPayType");// 默认为10 10
																	// 普通支付订单 20
																	// 代理费用支付订单
																	// 普通支付订单 20
																	// 代理费用支付订单
			String transAmt = json.getString("transAmt");
			String accType = json.getString("accType");// 到账类型0：D0到账 1：T1到账
			String transType = json.getString("transType");// 通道类型
			String agentTypeStr = json.getString("agentTypeStr");// 默认为零
			String appType = json.getString("appType");

			// D0收款强制关闭
			// if (accType.equals("0")) {
			// returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
			// returnJson.put("respDesc", "D0收款升级");
			// return commonService.getEncryptBody(returnJson);
			// }

			// 判断参数是否齐全
			if (StringUtils.isEmpty(merId) || StringUtils.isEmpty(agentId) || StringUtils.isEmpty(transPayType) || StringUtils.isEmpty(transAmt) || StringUtils.isEmpty(accType) || StringUtils.isEmpty(appType)
					|| StringUtils.isEmpty(transType) || StringUtils.isEmpty(agentTypeStr)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				return commonService.getEncryptBody(returnJson);
			}

			// 检查用户是否满足交易状态
			User user = userService.get(Long.parseLong(merId));
			String flag = userService.isAllowUserPay(user.getId(), transType);
			if (!GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
				returnJson.put("respCode", flag);
				returnJson.put("respDesc", GlobalConstant.map.get(flag));
				return commonService.getEncryptBody(returnJson);
			}

			// 判断用户操作时间是否满足
			String stmFlag = holidayService.isLimitTimeInterval(new Date(), true, null);
			if (!GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_066);
				returnJson.put("respDesc", stmFlag);
				return commonService.getEncryptBody(returnJson);
			}

			Double money = Double.valueOf(transAmt);
			Map<String, String> returnOrder = null;
			String getMoneyer = StringUtil.isNotBlank(user.getRealName()) ? "商户-" + user.getRealName() : user.getLoginName();
			if (user.getMerchantType() == User.merchant_type.REAL_MERCHANT.getCode() || user.getMerchantType() == User.merchant_type.NONE_MERCHANT.getCode()) {
				if (StringUtil.isNotBlank(user.getMerchantName())) {
					getMoneyer = StringUtil.isNotBlank(user.getMerchantShortName()) ? user.getMerchantShortName() : user.getMerchantName();
				}
			}
			String desc = "线下扫码";
			Integer transPayTypeInt = Integer.parseInt(transPayType);
			Integer angentType = Integer.parseInt(agentTypeStr); // 0 通用，21
																	// 钻石升级，22
																	// 金牌升级

			ChannelPayRef cpr = null;
			if (transType.equals("1300")) { // QQ钱包
				cpr = troughTrainServeice.getChannelPayRef(false, transType, Long.parseLong(merId), accType, TroughType);
			} else {
				// cpr = troughTrainServeice.getChannelPayRef(false, transType,
				// Long.parseLong(merId), accType,
				// "PINGANPAYZHITONGCHE");
				cpr = troughTrainServeice.getChannelPayRef(false, transType, Long.parseLong(merId), accType, "PINGANPAYZHITONGCHE_ZHIQING");
			}
			if (cpr == null) {
				// 平安切换至直清模式，需要重新报备商户
				// 1,检查老的PINGANPAYZHITONGCHE通道是否存在,
				ChannelPayRef cprold = troughTrainServeice.getChannelPayRef(false, transType, Long.parseLong(merId), accType, "PINGANPAYZHITONGCHE");
				if (cprold == null) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "用户未开通子商户");
					return commonService.getEncryptBody(returnJson);
				}
				// 2,检查该老通道本地保存的信息是否与平安一致，不一致，则将不一致通道设为失效

				Map<String, String> checkRes = troughTrainServeice.checkPingAnChannelCorrect(cprold.getChannel().getId());
				if (!checkRes.get("code").equals("000")) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "小主：遇到了点小麻烦吗？咱们联系下客服小妹吧！");
					return commonService.getEncryptBody(returnJson);
				}

				// 3,重新开通直清模式的PINGNAPAYZHITONGCHE_ZHIQING通道
				JSONObject openPingAnRes = troughTrainServeice.createPingAnZhiQingMer(checkRes.get("name"), checkRes.get("aliasName"), Long.parseLong(merId));
				if (!openPingAnRes.getString("code").equals("000")) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "小主，现在使用人数过多，路上有点堵，请重新选择通道使用。");
					return commonService.getEncryptBody(returnJson);
				}

				cpr = troughTrainServeice.getChannelPayRef(false, transType, Long.parseLong(merId), accType, "PINGANPAYZHITONGCHE_ZHIQING");

			}

			// 开始创建订单
			returnOrder = cpr.getChannelPaymentService().createUnifiedOrder(user, cpr, Integer.parseInt(accType), money, transPayTypeInt, angentType, desc);

			if (returnOrder == null && !returnOrder.get("return_code").equals(GlobalConstant.SUCCESS)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_500);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_500));
				return commonService.getEncryptBody(returnJson);
			}

			if (returnOrder.containsKey("return_code")) {
				if (returnOrder.get("return_code").equals("open_ali_error")) {
					// 针对支付宝入驻问题单独做判断
					returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
					returnJson.put("respDesc", returnOrder.get("return_message"));
					return commonService.getEncryptBody(returnJson);
				}
				if (returnOrder.get("return_code").equals("make_wx_jsapi_path_error")) {
					// 针对微信子商户授权目录配置问题单独做判断
					returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
					returnJson.put("respDesc", returnOrder.get("return_message"));
					return commonService.getEncryptBody(returnJson);
				}

			}

			if (!GlobalConstant.SUCCESS.equals(returnOrder.get("result_code"))) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_068);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_068));
				return commonService.getEncryptBody(returnJson);
			}
			if (returnOrder.containsKey("code_url")) {
				returnJson.put("transQrUrl", returnOrder.get("code_url"));
			}
			returnJson.put("orderNum", returnOrder.get("orderNum"));
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_994);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_994));
			return commonService.getEncryptBody(returnJson);
		}
		LOG.info("----直通车模式  end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.51 用户积分兑换降费率接口
	 * 
	 * @category 用户积分兑换降费率接口
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/consumePointChlRateZTH", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject consumePointChlRateZTH(HttpServletRequest request) {
		LOG.info("---- 用户积分查询接口start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			LOG.info("consumePointChlRateZTH json={}", json.toJSONString());
			String agentId = json.getString("agentId");// 服务商编号
			String merId = json.getString("merId");// 商户ID
			LOG.info("userId=" + merId + "   token=" + token);
			String chlType = json.getString("chlType");// 通道类型 200支付宝 300 微信 500
														// 银联在线
			String consumePoint = json.getString("consumePoint");// 消耗的点数
			/*
			 * 1 高费率降至中费率 2 中费率降至低费率 3 高费率降至低费率
			 */
			String type = json.getString("type");
			String transPwd = json.getString("transPwd");// 交易密码
			String chkValue = json.getString("chkValue");
			if (StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(merId) && StringUtil.isNotEmpty(chlType) && StringUtil.isNotEmpty(consumePoint) && StringUtil.isNotEmpty(type) && StringUtil.isNotEmpty(transPwd)
					&& StringUtil.isNotEmpty(chkValue)) {
				String chk = MD5Util.md5(merId.trim() + agentId.trim() + chlType.trim() + consumePoint.trim() + type.trim() + transPwd.trim() + "flypayjfxf");
				/* 验证签名 */
				if (chk.equals(chkValue)) {
					Integer channelType = Integer.parseInt(chlType);
					Integer accountType = 1;// 入账类型 默认T1小额
					if (json.containsKey("accountType") && json.getInteger("accountType") != null) {
						accountType = json.getInteger("accountType");
					}
					String stmFlag = userSettlementConfigService.isAllowReduceInputRateWhenPoint(Long.parseLong(merId), agentId, channelType, accountType, Integer.parseInt(type), Integer.parseInt(consumePoint));

					if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {

						stmFlag = userService.isConsumePoint(Long.parseLong(merId), transPwd, Long.parseLong(consumePoint));
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {
							Integer point = Integer.parseInt(consumePoint);
							stmFlag = accountPointService.consumePointChlRateZTH(agentId, Long.parseLong(merId), channelType, accountType, Integer.parseInt(type), Integer.parseInt(consumePoint));

							if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
							} else {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_996);
								returnJson.put("respDesc", stmFlag);
							}
						} else {
							returnJson.put("respCode", stmFlag);
							returnJson.put("respDesc", GlobalConstant.map.get(stmFlag));
						}
					} else {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_078);
						returnJson.put("respDesc", stmFlag);
					}
				} else {
					/* 签名失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_996);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_996));
				}

			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			LOG.error("通道费率限额列表查询接口接口 error ", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		LOG.info("----用户积分查询接口end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	// @RequestMapping(value = "/ShenFuAddCreditCardNotify", method = {
	// RequestMethod.POST, RequestMethod.GET })
	// public void addShenFuCreditCard(HttpServletRequest request,
	// HttpServletResponse response) throws Exception {
	// // respCode 操作结果码 String 是 00表示成功，其他值表示失败。
	// // respMsg 操作结果消息 String 否
	// // mercId 商户ID String 是 与开通快捷提交时的mercId一致
	// // logNo 操作流水号 String 是 平台操作的流水号。
	// // orderNo 开通订单号 String 是 与开通快捷提交时的orderNo一致
	// // signature 签名 String 是 详见签名算法。
	// // 申孚信用卡卡报备响应
	// String respCode = request.getParameter("respCode");
	// String respMsg = request.getParameter("respMsg");
	// String mercId = request.getParameter("mercId");
	// String logNo = request.getParameter("logNo");
	// String orderNo = request.getParameter("orderNo");
	// String signature = request.getParameter("signature");
	// TcreditCardReport cardReport = creditCardReportService.get(orderNo);
	// LOG.info("Notify TcreditCardReport
	// respCode={},respMsg={},mercId={},logNo={},orderNo={},signature={},",
	// new Object[] { respCode, respMsg, mercId, logNo, orderNo, signature });
	// if (cardReport == null) {
	// LOG.info("Cant Find TcreditCardReport orderNo={}", orderNo);
	// return;
	// }
	// if ("00".equals(respCode)) {
	// // 成功
	// cardReport.setNotifyCode(respCode);
	// cardReport.setStatus("1");
	// cardReport.setLogNo(logNo);
	// } else {
	// // 失败
	// cardReport.setNotifyCode(respCode);
	// cardReport.setLogNo(logNo);
	// }
	// LOG.info("Change TcreditCardReport orderNo={},notifyCode={}", orderNo,
	// respCode);
	// creditCardReportService.saveOrUpdate(cardReport);
	// }

	@RequestMapping(value = "/ShenFuResponse", method = { RequestMethod.POST, RequestMethod.GET })
	public void ShenFuResponse(HttpServletResponse response, String result) throws Exception {
		// LOG.info("ShenFuResponse result={}", result);
		String content = AESCodeUtil.decrypt(result);
		// LOG.info("ShenFuResponse content={}", content);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(content);
	}

	@RequestMapping(value = "/YiQiangResponse", method = { RequestMethod.POST, RequestMethod.GET })
	public void YiQiangResponse(HttpServletResponse response, String result) throws Exception {
		LOG.info("YiQiangResponse result={}", result);
		String content = AESCodeUtil.decrypt(result);
		LOG.info("YiQiangResponse content={}", content);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(content);
	}

	@RequestMapping(value = "/GaZhiResponse", method = { RequestMethod.POST, RequestMethod.GET })
	public void GaZhiResponse(HttpServletResponse response, String result) throws Exception {
		LOG.info("GaZhiResponse result={}", result);
		String content = AESCodeUtil.decrypt(result).trim();
		LOG.info("GaZhiResponse content={}", content);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(content);
	}

	@RequestMapping(value = "/XinkeResponse", method = { RequestMethod.POST, RequestMethod.GET })
	public void XinkeResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		String result = request.getParameter("result");
		result = new String(result.getBytes("iso8859-1"), "UTF-8");
		result = new String(DESutil.decrypt(result, DESutil.password));
		writer.write(result);
	}

	@RequestMapping(value = "/weilianbaoOpenCardPayNotify", method = { RequestMethod.POST, RequestMethod.GET })
	public void weilianbaoOpenCardPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String retCode = request.getParameter("retCode");// 0000 成功
		String retMsg = request.getParameter("retMsg");//
		String merchantNo = request.getParameter("merchantNo");//
		String token = request.getParameter("token");//
		String cardNo = request.getParameter("cardNo");//
		String phone = request.getParameter("phone");//
		String orderNum = request.getParameter("orderNum");//
		Map<String, String> params = new HashMap<String, String>();
		if (!"0000".equals(retCode)) {
			LOG.info("weilianbaoOpenCardPayNotify retCode={},retMsg={},merchantNo={},", retCode, retMsg, merchantNo);
			return;
		}
		params.put("retCode", retCode);
		params.put("retMsg", retMsg);
		params.put("merchantNo", merchantNo);
		params.put("token", token);
		params.put("cardNo", cardNo);
		params.put("phone", phone);
		params.put("orderNum", orderNum);
		params.put("type", "init");

		weiLianBaoYinLainService.sendSmsCodeOpenCard(params);
	}

	@RequestMapping(value = "/weilianbaoOpenCardPayNotify2", method = { RequestMethod.POST })
	public void weilianbaoOpenCardPayNotify2(@RequestBody JSONObject request) throws Exception {
		String retCode = request.getString("retCode");// 0000 成功
		String retMsg = request.getString("retMsg");//
		String merchantNo = request.getString("merchantNo");//
		String token = request.getString("token");//
		String cardNo = request.getString("cardNo");//
		String phone = request.getString("phone");//
		String orderNum = request.getString("orderNum");//
		Map<String, String> params = new HashMap<String, String>();
		LOG.info("weilianbaoOpenCardPayNotify2 result={}", request.toJSONString());
		if (!"0000".equals(retCode)) {
			return;
		}
		params.put("retCode", retCode);
		params.put("retMsg", retMsg);
		params.put("merchantNo", merchantNo);
		params.put("token", token);
		params.put("cardNo", cardNo);
		params.put("phone", phone);
		params.put("orderNum", orderNum);
		params.put("type", "openCard");
		weiLianBaoYinLainService.sendSmsCodeOpenCard(params);
	}


	/**
	 * 哲扬通道接口返回页面元素拼接
	 * 
	 * @param request
	 * @param response
	 * @param result
	 * @throws Exception
	 */
	@RequestMapping(value = "/ZheYangResponse", method = { RequestMethod.POST, RequestMethod.GET })
	public void ZheYangResponse(HttpServletRequest request, HttpServletResponse response, String result) throws Exception {
		PrintWriter writer = response.getWriter();
		result = new String(result.getBytes("iso8859-1"), "UTF-8");
		result = new String(DESutil.decrypt(result, DESutil.password));
		writer.write(result);
	}

	/**
	 * 易联通道接口返回页面元素拼接
	 * 
	 * @param request
	 * @param response
	 * @param result
	 * @throws Exception
	 */
	@RequestMapping(value = "/YiLianResponse", method = { RequestMethod.POST, RequestMethod.GET })
	public void YiLianResponse(HttpServletRequest request, HttpServletResponse response, String result) throws Exception {
		PrintWriter writer = response.getWriter();
		result = new String(result.getBytes("iso8859-1"), "UTF-8");
		result = new String(DESutil.decrypt(result, DESutil.password));
		writer.write(result);
	}

	/**
	 * 新闻资讯查询
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/NewsInforQuery", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject NewsInforQuery(HttpServletRequest request) {
		LOG.info("----新闻资讯查询 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			LOG.info("NewsInforQuery json={}", json.toJSONString());
			String merId = json.getString("merId");
			if (StringUtil.isEmpty(token) || userTokenService.isLegalUserToken(Long.parseLong(merId), token) == false) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				return commonService.getEncryptBody(returnJson);
			}

			String agentId = json.getString("agentId");
			String pageSize = json.getString("pageSize");
			String pageNum = json.getString("pageNum");
			if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(pageNum)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				return commonService.getEncryptBody(returnJson);
			}
			PageFilter pf = new PageFilter();
			pf.setPage(Integer.parseInt(pageNum));
			pf.setRows(Integer.parseInt(pageSize));
			pf.setSort("creatime");
			pf.setOrder("desc");

			returnJson.put("list", newsInforMationService.findList(agentId, pf));
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			return commonService.getEncryptBody(returnJson);
		}
		LOG.info("----新闻资讯查询 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 新闻资讯查询
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/NewsInforSum", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject NewsInforSum(HttpServletRequest request) {
		LOG.info("----新闻资讯阅读下载量 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			LOG.info("NewsInforSum json={}", json.toJSONString());
			String merId = json.getString("merId");
			if (StringUtil.isEmpty(token) || userTokenService.isLegalUserToken(Long.parseLong(merId), token) == false) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				return commonService.getEncryptBody(returnJson);
			}

			String type = json.getString("type");
			String NewId = json.getString("NewId");
			if (StringUtils.isEmpty(type) || StringUtils.isEmpty(NewId)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				return commonService.getEncryptBody(returnJson);
			}
			SysInformation sys = new SysInformation();
			sys.setId(Long.parseLong(NewId));
			sys.setType(type);
			if (!newsInforMationService.editSum(sys)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
				return commonService.getEncryptBody(returnJson);
			}
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));

		} catch (Exception e) {
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			return commonService.getEncryptBody(returnJson);
		}
		LOG.info("----新闻资讯阅读下载量 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 新闻资讯查询 金钱龟最新技术显示框使用该接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/NewsInforQueryTwo", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject NewsInforQueryTwo(HttpServletRequest request) {
		LOG.info("----新闻资讯查询 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			LOG.info("NewsInforQueryTwo json={}", json.toJSONString());
			String merId = json.getString("merId");
			if (StringUtil.isEmpty(token) || userTokenService.isLegalUserToken(Long.parseLong(merId), token) == false) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				return commonService.getEncryptBody(returnJson);
			}

			String agentId = json.getString("agentId");
			String pageSize = json.getString("pageSize");
			String pageNum = json.getString("pageNum");
			if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(pageNum)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				return commonService.getEncryptBody(returnJson);
			}
			PageFilter pf = new PageFilter();
			pf.setPage(Integer.parseInt(pageNum));
			pf.setRows(Integer.parseInt(pageSize));
			pf.setSort("createTime");
			pf.setOrder("desc");

			returnJson.put("list", sysInformationPhotoService.findList(agentId, pf));
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			return commonService.getEncryptBody(returnJson);
		}
		LOG.info("----新闻资讯查询 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 新闻资讯查询 文案库
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/NewsInforQueryThere", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject NewsInforQueryThere(HttpServletRequest request) {
		LOG.info("----新闻资讯查询 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			LOG.info("NewsInforQueryThere json={}", json.toJSONString());
			String merId = json.getString("merId");
			if (StringUtil.isEmpty(token) || userTokenService.isLegalUserToken(Long.parseLong(merId), token) == false) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				return commonService.getEncryptBody(returnJson);
			}

			String agentId = json.getString("agentId");
			String pageSize = json.getString("pageSize");
			String pageNum = json.getString("pageNum");
			if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(pageNum)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				return commonService.getEncryptBody(returnJson);
			}
			PageFilter pf = new PageFilter();
			pf.setPage(Integer.parseInt(pageNum));
			pf.setRows(Integer.parseInt(pageSize));
			pf.setSort("createTime");
			pf.setOrder("desc");

			returnJson.put("list", sysInformationPhotoService.findListTwo(agentId, pf));
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			return commonService.getEncryptBody(returnJson);
		}
		LOG.info("----新闻资讯查询 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 新闻资讯详情
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/NewsInforDetail", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject NewsInforDetail(HttpServletRequest request) {
		LOG.info("----新闻资讯详情 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			LOG.info("NewsInforDetail json={}", json.toJSONString());
			String merId = json.getString("merId");
			if (StringUtil.isEmpty(token) || userTokenService.isLegalUserToken(Long.parseLong(merId), token) == false) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				return commonService.getEncryptBody(returnJson);
			}

			String agentId = json.getString("agentId");
			String newiId = json.getString("newiId");
			if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(newiId)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				return commonService.getEncryptBody(returnJson);
			}
			returnJson.put("list", sysInformationPhotoService.findDeatil(Long.parseLong(newiId)));
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			return commonService.getEncryptBody(returnJson);
		}
		LOG.info("----新闻资讯详情 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 新闻资讯增加阅读量
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addReadNum", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject addReadNum(HttpServletRequest request) {
		LOG.info("----新闻资讯增加阅读量 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			LOG.info("addReadNum json={}", json.toJSONString());
			String merId = json.getString("merId");
			if (StringUtil.isEmpty(token) || userTokenService.isLegalUserToken(Long.parseLong(merId), token) == false) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				return commonService.getEncryptBody(returnJson);
			}

			String agentId = json.getString("agentId");
			String newiId = json.getString("newiId");
			if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(newiId)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				return commonService.getEncryptBody(returnJson);
			}
			sysInformationPhotoService.addReadNum(Long.parseLong(newiId));
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			return commonService.getEncryptBody(returnJson);
		}
		LOG.info("----新闻资讯增加阅读量 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 欣客批量开子商户
	 * 
	 * @param request
	 * @return
	 */
	// @ResponseBody
	// @RequestMapping(value = "/XinKeKT", produces =
	// MediaType.APPLICATION_JSON_VALUE)
	// public JSONObject XinKeKT(HttpServletRequest request) {
	// log.info("----欣客批量开子商户 start-----");
	// JSONObject returnJson = new JSONObject();
	// try {
	// List<User> list = troughTrainServeice.dataGrid();
	// log.info("用户数量OKOKOKOKOKOKOK");
	// log.info("用户数量:"+list.size());
	// int k =0;
	// int h =0;
	// int c =0;
	// for (int i = 0; i < list.size(); i++) {
	// k++;
	// User user = list.get(i);
	// String ls = user.getPhone();
	// if(ls!=null){
	// if(ls.length()==11){
	// Boolean b = troughTrainServeice.addMocde(user);
	// if(b){
	// h++;
	// }else{
	// c++;
	// }
	// }
	// }
	// }
	// log.info("kkkk:"+k+"hhhh:"+h+"cccc:"+c);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
	// returnJson.put("respDesc",
	// GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
	// log.info("----欣客批量开子商户 end-----");
	// return commonService.getEncryptBody(returnJson);
	// }

	// @ResponseBody
	// @RequestMapping(value = "/XinKeKT", produces =
	// MediaType.APPLICATION_JSON_VALUE)
	// public JSONObject XinKeKT(HttpServletRequest request) {
	// log.info("----欣客批量开子商户 start-----");
	// JSONObject returnJson = new JSONObject();
	// try {
	// List<User> list = troughTrainServeice.dataGrid();
	// log.info("用户数量:"+list.size());
	// int k =0;
	// int h =0;
	// int c =0;
	// for (int i = 0; i < list.size(); i++) {
	// k++;
	// User user = list.get(i);
	// if(user.getAgentId().equals("F20160001")){
	// if(user.getUserType()==24){
	// orgChannelUserRateConfigService.updateOrgChannelUserRate(24,
	// user.getId(),user.getAgentId());
	// h++;
	// log.info("------普通执行数------"+h);
	// }else{
	// orgChannelUserRateConfigService.updateOrgChannelUserRate(23,
	// user.getId(),user.getAgentId());
	// c++;
	// log.info("------钻石执行数------"+c);
	// }
	//
	// }
	// }
	// log.info("用户数量总数:"+k+"更改普通费率总数:"+h+"更改钻石费率总数:"+c);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
	// returnJson.put("respDesc",
	// GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
	// log.info("----欣客批量开子商户 end-----");
	// return commonService.getEncryptBody(returnJson);
	// }

	// @ResponseBody
	// @RequestMapping(value = "/XinKeKT", produces =
	// MediaType.APPLICATION_JSON_VALUE)
	// public JSONObject XinKeKT(HttpServletRequest request) {
	// log.info("----欣客批量开子商户 start-----");
	// JSONObject returnJson = new JSONObject();
	// try {
	// List<User> list = troughTrainServeice.dataGrid();
	// log.info("用户数量:"+list.size());
	// int k =0;
	// int h =0;
	// int c =0;
	// for (int i = 0; i < list.size(); i++) {
	// k++;
	// User user = list.get(i);
	// if(user.getAgentId().equals("F20160001")){
	// InfoList infoList = new InfoList();
	// infoList.setUserId(user.getId());
	// infoList.setOrganizationId(2l);
	// infoList.setTitle("黄金免费升级钻石");
	// infoList.setInfoType(1);
	// infoList.setContent("尊敬的黄金会员，您好！为了感谢您一路的支持与陪伴，我们宝贝钱袋2.0新版上线后，免费为您升级到钻石会员等级。希望您多提宝贵建议，与我们宝贝钱袋一起成长！");
	// infoList.setStatus(1);
	// infoList.setPhone(user.getLoginName());
	// jiguangPushService.sendMsgInfoToPerson(infoList);
	// infoService.add(infoList);
	// h++;
	// }
	// }
	// log.info("用户数量总数:"+k+"更改普通费率总数:"+h+"更改钻石费率总数:"+c);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
	// returnJson.put("respDesc",
	// GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
	// log.info("----欣客批量开子商户 end-----");
	// return commonService.getEncryptBody(returnJson);
	// }

	@ResponseBody
	@RequestMapping(value = "/XinKeKT", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject XinKeKT(HttpServletRequest request) {
		LOG.info("----平安翼支付批量开子商户 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			List<User> list = troughTrainServeice.dataGrid();
			LOG.info("用户数量:" + list.size());
			int k = 0;
			for (int i = 0; i < list.size(); i++) {
				k++;
				User user = list.get(i);
				troughTrainServeice.PINGANYIZF("个体户" + user.getRealName());
			}
			LOG.info("用户数量总数:" + k);
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
		returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		LOG.info("----平安翼支付批量开子商户 end-----");
		return commonService.getEncryptBody(returnJson);
	}

}
