package com.cn.flypay.controller.mobile;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.controller.base.BaseController;
import com.cn.flypay.model.sys.TYiQiang2MerchantReport;
import com.cn.flypay.model.sys.TcardBankConfig;
import com.cn.flypay.model.sys.TinfoList;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.util.CollectionUtil;
import com.cn.flypay.pageModel.account.Account;
import com.cn.flypay.pageModel.account.AccountPoint;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.AppVersion;
import com.cn.flypay.pageModel.sys.AuthenticationLog;
import com.cn.flypay.pageModel.sys.Bank;
import com.cn.flypay.pageModel.sys.Business;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.pageModel.sys.OrgPointConfig;
import com.cn.flypay.pageModel.sys.PayTypeLimitConfig;
import com.cn.flypay.pageModel.sys.SysMsgHistory;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserCard;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.pageModel.trans.Brokerage;
import com.cn.flypay.pageModel.trans.BrokerageDetail;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.account.AccountPointService;
import com.cn.flypay.service.account.AccountService;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.common.MobileService;
import com.cn.flypay.service.common.UserTokenService;
import com.cn.flypay.service.payment.AuthenticationService;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.GaZhiYinLainService;
import com.cn.flypay.service.payment.PaymentService;
import com.cn.flypay.service.payment.PingAnExpenseService;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.payment.TroughTrainServeice;
import com.cn.flypay.service.payment.WeiLianBaoYinLainService;
import com.cn.flypay.service.payment.YiQiangPointService;
import com.cn.flypay.service.sys.AppVersionService;
import com.cn.flypay.service.sys.BankService;
import com.cn.flypay.service.sys.BusinessService;
import com.cn.flypay.service.sys.CardBankConfigService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.FeedBackService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.InfoListService;
import com.cn.flypay.service.sys.JiguangPushService;
import com.cn.flypay.service.sys.MsgHistoryService;
import com.cn.flypay.service.sys.OrgPointConfigService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.PayTypeLimitConfigService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserImageService;
import com.cn.flypay.service.sys.UserMerchantConfigService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.service.trans.BrokerageDetailService;
import com.cn.flypay.service.trans.BrokerageService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.ImportUtil;
import com.cn.flypay.utils.MD5Util;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.channel.WeixinUtil;
import com.cn.flypay.utils.gazhiyinlian.GaZhiYinLianUtil;
import com.cn.flypay.utils.gazhiyinlian.entities.OrderPayForGaZhiYinLian;
import com.cn.flypay.utils.weilianbao.entity.OrderPayForWeiLianBaoYinLian;
import com.cn.flypay.utils.yilian.YiLianYlzxUtil;
import com.cn.flypay.utils.yiqiang2.YiQiang2Config;
import com.cn.flypay.utils.yiqiang2.YiQiang2PayUtil;
import com.rd.constant.ValueConstant;
import com.rd.model.MerchantQuickPayConfirmReq;

@Controller
@Component
@RequestMapping("/mobile")
public class MobileController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MobileService mobileService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserTokenService userTokenService;
	@Autowired
	private MsgHistoryService msgHistoryService;
	@Autowired
	private UserCardService cardService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private BrokerageService brokerageService;
	@Autowired
	private UserOrderService orderService;
	@Autowired
	private BrokerageDetailService brokerageDetailService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private UserImageService imageService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private AppVersionService appVersionService;
	@Autowired
	private BankService bankService;
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private CardBankConfigService bankConfigService;
	@Autowired
	private InfoListService infoListService;
	@Autowired
	private HolidayService holidayService;
	@Autowired
	private SysParamService paramService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private FeedBackService feedBackService;
	@Autowired
	private AccountPointService accountPointService;
	@Autowired
	private OrgPointConfigService orgPointConfigService;
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	@Autowired
	private JiguangPushService jiguangPushService;
	@Autowired
	private PingAnExpenseService pingAnExpenseService;
	@Autowired
	private TroughTrainServeice troughTrainServeice;
	@Autowired
	private SysParamService sysParamService;
	@Autowired
	private ChannelPaymentService yiBaoPaymentService; // 易宝
	@Autowired
	private PayTypeLimitConfigService payTypeLimitConfigService;
	@Autowired
	private UserMerchantConfigService userMerchantConfigService;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private GaZhiYinLainService gaZhiYinLainService;
	@Autowired
	private ChannelPaymentService gaZhiYinLianPaymentService;
	@Autowired
	WeiLianBaoYinLainService weiLianBaoYinLainService;
	@Autowired
	YiQiangPointService yiQiangPointService;

	/**
	 * 2.3.1 服务商编号查询接口
	 * 
	 * @param param
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryAgentId", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryAgentId(HttpServletRequest request) {
		logger.info("----获取服务商号 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryAgentId json={}", json.toJSONString());
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			returnJson.put("agentId", mobileService.getAgentId());
		} catch (Exception e) {
			logger.error("queryAgentId", e);
		}
		logger.info("----获取服务商号 end-----");

		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.2 登陆接口
	 * 
	 * @param param
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject login(HttpServletRequest request) {
		logger.info("----用户登录 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("login json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号

			String apploginName = json.getString("loginName");
			logger.info("用户" + apploginName + " 登录系统");
			String loginPwd = json.getString("loginPwd");
			if (StringUtil.isNotEmpty(apploginName) && StringUtil.isNotEmpty(loginPwd)) {
				User isExistUser = userService.findUserByLoginName(apploginName.trim(), agentId);
				if (isExistUser == null) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_057);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_057));
				} else {
					Torganization torg = organizationService.getTorganizationInCacheByCode(agentId);
					if (torg != null) {
						User u = userService.loginApp(apploginName.trim(), agentId, loginPwd.trim());
						if (u != null) {
							String sessionId = request.getSession().getId();
							returnJson.put("merId", u.getId());
							returnJson.put("realName", u.getRealName());
							returnJson.put("lastLoginDate", u.getLastDateTime());
							returnJson.put("isAuthentication", u.getAuthenticationStatus().toString());
							returnJson.put("merchantType", u.getMerchantType());
							returnJson.put("currentDate", DateUtil.convertCurrentDateTimeToString());
							// List<String> news =
							// infoListService.findSystemNews(agentId);
							// if (news != null && news.size() > 0) {
							// JSONArray arry = new JSONArray();
							// for (String n : news) {
							// JSONObject job = new JSONObject();
							// job.put("news", n);
							// arry.add(job);
							// }
							// returnJson.put("newsInfo", arry);
							// }
							List<InfoList> infos = infoListService.findSystemNotice(agentId);
							if (infos != null && infos.size() > 0) {
								JSONArray arry = new JSONArray();
								for (InfoList info : infos) {
									JSONObject job = new JSONObject();
									job.put("title", info.getTitle());
									job.put("news", info.getContent());
									arry.add(job);
								}
								returnJson.put("newsInfo", arry);
							}
							/*
							 * 更新用户登录的token
							 */
							returnJson.put("token", userTokenService.updateUserTokenWhenLogin(u.getId(), sessionId));
							logger.info("用户" + apploginName + " 登录系统时 获得token");
							returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
						} else {
							logger.info("用户" + apploginName + " 登录系统时 密码错误");
							User user = userService.findUserByLoginName(apploginName.trim(), agentId);
							if (user != null) {
								returnJson.put("respCode", user.getLoginErrorNum());
								returnJson.put("respDesc", "您今天还有" + (6 - user.getLoginErrorNum()) + "次机会尝试登陆机会");
							} else {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_002);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_002));
							}
						}
					} else {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_067);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_067));
					}
				}
			} else {
				logger.info("用户" + apploginName + " 登录系统时 缺失参数");
				/* 登录失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----用户登录 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.3 注册接口
	 * 
	 * @param param
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject register(HttpServletRequest request) {
		logger.info("----用户注册 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("register json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String appLoginName = json.getString("loginName");
			String smsCode = json.getString("smsCode");
			String msgCode = json.getString("msgCode");
			String loginPwd = json.getString("loginPwd");

			if (StringUtil.isNotEmpty(appLoginName) && StringUtil.isNotEmpty(smsCode) && StringUtil.isNotEmpty(msgCode) && StringUtil.isNotEmpty(loginPwd)) {
				Boolean flag = msgHistoryService.validateSmsCode(smsCode, msgCode);
				if (flag) {
					String chnlId = json.getString("chnlId");// 推荐码
					User isExistUser = userService.findUserByLoginName(appLoginName.trim(), agentId);
					if (isExistUser == null) {
						if (agentId.equals("F00060009") || agentId.equals("F20160010") || agentId.equals("F20160013")) {
							if (StringUtil.isNotBlank(chnlId)) {
								Tuser popurlarUser = userService.findTuserByUserCodeOrPhone(chnlId, StringUtil.getAgentId(agentId));
								if (popurlarUser == null) {
									returnJson.put("respCode", GlobalConstant.RESP_CODE_045);
									returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_045));
									return commonService.getEncryptBody(returnJson);
								}
							}
						} else {
							if (StringUtil.isNotBlank(chnlId)) {
								Tuser popurlarUser = userService.findTuserByUserCodeOrPhone(chnlId, StringUtil.getAgentId(agentId));
								if (popurlarUser == null) {
									returnJson.put("respCode", GlobalConstant.RESP_CODE_045);
									returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_045));
									return commonService.getEncryptBody(returnJson);
								}
							} else {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_045);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_045));
								return commonService.getEncryptBody(returnJson);
							}
						}

						User u = userService.addUser(appLoginName.trim(), loginPwd.trim(), chnlId, StringUtil.getAgentId(agentId));
						if (u != null) {
							String sessionId = request.getSession().getId();
							returnJson.put("merId", u.getId());
							returnJson.put("realName", u.getRealName());
							returnJson.put("lastLoginDate", u.getLastDateTime());
							returnJson.put("isAuthentication", u.getAuthenticationStatus().toString());
							returnJson.put("merchantType", u.getMerchantType());
							returnJson.put("currentDate", DateUtil.convertCurrentDateTimeToString());
							/*
							 * 更新用户登录的token
							 */
							returnJson.put("token", userTokenService.updateUserTokenWhenLogin(u.getId(), sessionId));
							returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
							logger.info("用户" + appLoginName.trim() + "注册成功");
						} else {
							/* 验证码失败 */
							returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
						}
					} else {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_026);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_026));
					}
				} else {
					/* 验证码失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_001);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_001));
				}

			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			logger.error("user regist error ", e);
			e.printStackTrace();
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----用户注册 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.4 注册短信发送接口 不需要token支持
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sendRegisterSms", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject sendRegisterSms(HttpServletRequest request) {
		logger.info("----用户注册短信 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("sendRegisterSms json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String phone = json.getString("phone");
			String smsType = json.getString("smsType");

			String chkValue = json.getString("chkValue");
			if (StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(smsType) && StringUtil.isNotEmpty(phone) && StringUtil.isNotEmpty(chkValue)) {
				String chk = MD5Util.md5(agentId.trim() + phone.trim() + smsType.trim() + "flypayzc");
				/* 验证签名 */
				if (chk.equals(chkValue)) {
					try {
						Integer type = Integer.parseInt(smsType.trim());
						if (SysMsgHistory.SMS_MSG_TYPE.trans_psw.getType() == type) {
							User user = userService.findUserByLoginName(phone, agentId);
							if (user == null) {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_006);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_006));
							}
						} else {
							SysMsgHistory smsHistory = msgHistoryService.sendSmsToUserPhone(phone, agentId, type);
							if (smsHistory.getId() == null) {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_007);
								returnJson.put("respDesc", smsHistory.getContent());
							} else {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
								returnJson.put("msgCode", smsHistory.getMsgCode());
								returnJson.put("smsCode", smsHistory.getValidateCode());
							}
						}
					} catch (Exception e) {
						/* 签名失败 */
						returnJson.put("respCode", GlobalConstant.RESP_CODE_007);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_007));
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
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----用户注册短信 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.5 短信发送接口 需要token支持
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sendSms", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject sendSms(HttpServletRequest request) {
		logger.info("----用户发送短信接口 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("sendSms json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号

				String loginName = json.getString("loginName");
				String smsType = json.getString("smsType");
				String chkValue = json.getString("chkValue");

				if (StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(loginName) && StringUtil.isNotEmpty(smsType) && StringUtil.isNotEmpty(chkValue)) {
					String chk = MD5Util.md5(agentId.trim() + merId + loginName.trim() + smsType.trim() + "flypaydx");
					/* 验证签名 */
					if (chk.equals(chkValue)) {
						try {
							SysMsgHistory smsHistory = msgHistoryService.sendSmsToUserPhone(loginName, agentId, Integer.parseInt(smsType.trim()));
							if (smsHistory.getId() == null) {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_007);
								returnJson.put("respDesc", smsHistory.getContent());
							} else {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
								returnJson.put("msgCode", smsHistory.getMsgCode());
								returnJson.put("smsCode", smsHistory.getValidateCode());
							}
						} catch (Exception e) {
							/* 签名失败 */
							returnJson.put("respCode", GlobalConstant.RESP_CODE_007);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_007));
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
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			logger.error("send sms error", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----用户发送短信接口 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.6 手机号验证接口 不需要token
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/mobileExistVerify", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject mobileExistVerify(HttpServletRequest request) {
		logger.info("----手机号验证接口 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("mobileExistVerify json={}", json.toJSONString());
			String agentId = json.getString("agentId");// 服务商编号

			String phone = json.getString("loginName");
			String chkValue = json.getString("chkValue");

			if (StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(phone) && StringUtil.isNotEmpty(chkValue)) {
				String chk = MD5Util.md5(agentId.trim() + phone.trim() + "flypaysjyz");
				/* 验证签名 */
				if (chk.equals(chkValue)) {
					try {
						User user = userService.findUserByPhone(phone, agentId);
						if (user == null) {
							returnJson.put("isExist", "N");
						} else {
							returnJson.put("isExist", "Y");
						}
						returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					} catch (Exception e) {
						logger.error("mobile Exist Verify error", e);
						/* 签名失败 */
						returnJson.put("respCode", GlobalConstant.RESP_CODE_996);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_996));
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
			logger.error("mobile Exist Verify error", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----手机号验证接口 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.7 验证码验证接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/smsCodeVerify", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject smsCodeVerify(HttpServletRequest request) {
		logger.info("----验证码验证接口-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("smsCodeVerify json={}", json.toJSONString());
			String agentId = json.getString("agentId");// 服务商编号

			String smsCode = json.getString("smsCode");// 短信验证码
			String msgCode = json.getString("msgCode");// 系统短信代码
			String chkValue = json.getString("chkValue");

			if (StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(smsCode) && StringUtil.isNotEmpty(msgCode) && StringUtil.isNotEmpty(chkValue)) {
				String chk = MD5Util.md5(agentId.trim() + smsCode.trim() + msgCode.trim() + "flypayyzmyz");
				/* 验证签名 */
				if (chk.equals(chkValue)) {
					Boolean flag = msgHistoryService.validateSmsCode(smsCode, msgCode);

					if (flag) {
						returnJson.put("isTrue", "Y");
					} else {
						returnJson.put("isTrue", "N");
					}
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));

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
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.8 实名认证接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/applyAuthentication", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject applyAuthentication(HttpServletRequest request) {
		logger.info("----实名认证接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			logger.info("applyAuthentication json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID

			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			logger.info("实名认证接口 userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String realName = json.getString("realName");
				String idNo = json.getString("idNo");
				String cardNo = json.getString("cardNo");
				String openBankId = json.getString("openBankId");// 结算银行
				String openProvId = json.getString("openProvId");// 省
				String openAreaId = json.getString("openAreaId");// 城市
				String openBranchId = json.getString("openBranchId");// 联行号
				String openBranchName = json.getString("openBranchName");// 支行名称

				String merchantCity = json.getString("merchantCity");// 店铺所在城市
				String merchantName = json.getString("merchantName");// 店铺名称

				String cardType = json.getString("cardType");
				String chkValue = json.getString("chkValue");
				String reservedPhone = json.getString("reservedPhone");

				String cvv = json.getString("cvv");// cvv
				String expiryDate = json.getString("expiryDate");// 有效期

				if (StringUtil.isNotEmpty(merId) && StringUtil.isNotEmpty(realName) && StringUtil.isNotEmpty(cardNo) && StringUtil.isNotEmpty(openBankId) && StringUtil.isNotEmpty(reservedPhone) && StringUtil.isNotEmpty(chkValue)) {
					if (!authenticationService.isIdon(agentId, idNo)) {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
						returnJson.put("respDesc", "一张身份证只能实名两个账号");
						return commonService.getEncryptBody(returnJson);
					}

					String realIdNo = ImportUtil.getDecIdNo(idNo);
					String realCardNo = ImportUtil.getDecCardNo(cardNo);

					String chk = MD5Util.md5(merId.trim() + idNo.trim() + reservedPhone.trim() + cardNo.trim() + openBankId.trim() + "flypaysmrz");
					/* 验证签名 */
					if (chk.equals(chkValue)) {
						json.put("idNo", realIdNo);
						json.put("cardNo", realCardNo);
						json.put("agentId", agentId);
						if (json.containsKey("cvv") && StringUtil.isNotBlank(cvv)) {
							String realCvv = ImportUtil.getDecCvv(cvv);
							json.put("cvv", realCvv);
						}
						if (authenticationService.isAllowAuthentication(Long.parseLong(merId), realIdNo.trim())) {
							Map<String, String> cardInfos = json.toJavaObject(json, Map.class);
							Map<String, String> flagMap = cardService.sendCardInfoToBankValidate(cardInfos, UserCard.card_type.J.name().equals(cardType) ? true : false, true, agentId);
							if (GlobalConstant.RESP_CODE_SUCCESS.equals(flagMap.get("flag"))) {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
							} else {
								/* 交易失败 */
								if (flagMap.containsKey("errorCode")) {
									returnJson.put("respCode", flagMap.get("errorCode"));
									returnJson.put("respDesc", flagMap.get("errorInfo"));
								} else {
									returnJson.put("respCode", flagMap.get("flag"));
									returnJson.put("respDesc", GlobalConstant.map.get(flagMap.get("flag")));
								}
							}
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_081);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_081));
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
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}

		logger.info("----实名认证接口 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.9 证件上传接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/uploadAttach", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject uploadAttach(HttpServletRequest request) {
		logger.info("----证件上传接口-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("uploadAttach json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID

			String attachPath = json.getString("attachPath");
			String attachName = json.getString("attachName");
			String attachType = json.getString("attachType");

			if (StringUtil.isNotEmpty(merId) && StringUtil.isNotEmpty(attachPath) && StringUtil.isNotEmpty(attachType) && StringUtil.isNotEmpty(attachName)) {
				userService.addUserImage(Long.parseLong(merId.trim()), attachPath.trim(), Integer.parseInt(attachType), attachName.trim());

				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));

			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.10 更新结算银行卡接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateSettlementBankCard", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject updateSettlementBankCard(HttpServletRequest request) {
		logger.info("----更新结算银行卡接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("updateSettlementBankCard json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			logger.info("更新结算银行卡接口 userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				// String realName = json.getString("realName");
				// String idNo = json.getString("idNo");
				String cardNo = json.getString("cardNo");
				String openBankId = json.getString("openBankId");// 结算银行
				String openProvId = json.getString("openProvId");
				String openAreaId = json.getString("openAreaId");// 城市
				String openBranchId = json.getString("openBranchId");// 联行号
				String openBranchName = json.getString("openBranchName");// 支行名称
				String cardType = json.getString("cardType");
				String chkValue = json.getString("chkValue");
				String reservedPhone = json.getString("reservedPhone");

				if (StringUtil.isNotEmpty(merId) && StringUtil.isNotEmpty(cardNo) && StringUtil.isNotEmpty(openBankId) && StringUtil.isNotEmpty(reservedPhone) && StringUtil.isNotEmpty(chkValue)) {
					// String realIdNo = ImportUtil.getDecIdNo(idNo);
					String realCardNo = ImportUtil.getDecCardNo(cardNo);

					String chk = MD5Util.md5(merId.trim() + reservedPhone.trim() + cardNo.trim() + openBankId.trim() + "flypayjskgx");
					/* 验证签名 */
					if (chk.equals(chkValue)) {
						// json.put("idNo", realIdNo);
						json.put("cardNo", realCardNo);
						json.put("agentId", agentId);
						Map<String, String> cardInfos = json.toJavaObject(json, Map.class);
						Map<String, String> flags = cardService.sendCardInfoToBankValidate(cardInfos, true, false, agentId);
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(flags.get("flag"))) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
						} else {
							/* 交易失败 */
							returnJson.put("respCode", flags.get("flag"));
							returnJson.put("respDesc", GlobalConstant.map.get(flags.get("flag")));
							if (flags.containsKey("errorCode")) {
								returnJson.put("respCode", flags.get("errorCode"));
								returnJson.put("respDesc", flags.get("errorInfo"));
							}
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
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----更新结算银行卡接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.11 添加银行卡接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addBankCard", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject addBankCard(HttpServletRequest request) {
		logger.info("----添加银行卡接口 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("addBankCard json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String cardNo = json.getString("cardNo");// 卡号
				String cvv = json.getString("cvv");// cvv
				String openBankId = json.getString("openBankId");// 虽所在银行

				String openProvId = json.getString("openProvId");
				String openAreaId = json.getString("openAreaId");// 城市
				String openBranchId = json.getString("openBranchId");// 联行号
				String openBranchName = json.getString("openBranchName");// 支行名称

				String reservedPhone = json.getString("reservedPhone");// 预留手机号
				String expiryDate = json.getString("expiryDate");// 有效期
				String cardType = json.getString("cardType");
				String chkValue = json.getString("chkValue");
				String appType = json.getString("appType");
				if (StringUtil.isNotEmpty(merId) && StringUtil.isNotEmpty(cardNo) && StringUtil.isNotEmpty(openBankId) && StringUtil.isNotEmpty(reservedPhone) && StringUtil.isNotEmpty(cardType) && StringUtil.isNotEmpty(chkValue)) {

					String chk = MD5Util.md5(merId.trim() + cardNo.trim() + openBankId.trim() + reservedPhone.trim() + cardType.trim() + "flypaytjyhk");
					/* 验证签名 */
					if (chk.equals(chkValue)) {

						if (StringUtil.isNotBlank(cvv)) {
							String realCvv = ImportUtil.getDecCvv(cvv);
							json.put("cvv", realCvv);
						}
						if (StringUtil.isNotBlank(cardNo)) {
							String realCardNo = ImportUtil.getDecCardNo(cardNo);
							json.put("cardNo", realCardNo);
						}
						json.put("agentId", agentId);
						Map<String, String> cardInfos = json.toJavaObject(json, Map.class);
						Map<String, String> flags = cardService.sendCardInfoToBankValidate(cardInfos, false, false, StringUtil.getAgentId(agentId));
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(flags.get("flag"))) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
						} else {
							/* 交易失败 */
							returnJson.put("respCode", flags.get("flag"));
							returnJson.put("respDesc", GlobalConstant.map.get(flags.get("flag")));
							if (flags.containsKey("errorCode")) {
								returnJson.put("respCode", flags.get("errorCode"));
								returnJson.put("respDesc", GlobalConstant.map.get(flags.get("errorCode")));
							}
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
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			logger.error("添加银行卡出错", e);
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----添加银行卡接口  end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.11 商户交易卡查询接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryMerTransCard", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryMerTransCard(HttpServletRequest request) {
		logger.info("----商户交易卡查询接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryMerTransCard json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String cardType = json.getString("cardType");// 卡类型

				if (StringUtil.isNotEmpty(merId)) {
					List<UserCard> cards = cardService.findCarsByUserId(Long.parseLong(merId.trim()), cardType);
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					returnJson.put("currentDate", DateUtil.convertCurrentDateTimeToString());
					returnJson.put("totalNum", cards.size());
					JSONArray ja = new JSONArray();
					for (UserCard userCard : cards) {
						JSONObject job = new JSONObject();
						job.put("cardPhone", userCard.getPhone());
						job.put("cardId", userCard.getId());
						job.put("cardNo", ImportUtil.getEncCardNo(userCard.getCardNo()));
						job.put("openBankId", userCard.getBankId());
						job.put("openBankName", userCard.getBankName());
						job.put("cardType", userCard.getCardType());
						job.put("bankIco", userCard.getBankIco());
						job.put("isSettlmentCard", userCard.getIsSettlmentCard().toString());
						ja.add(job);
					}
					returnJson.put("ordersInfo", ja);
					// support_card_YL_on 1开启易联判断卡 0关闭易联判断卡
					String support_card_YL_on = paramService.searchSysParameter().get("support_card_YL_on");
					returnJson.put("supportCardYL", support_card_YL_on);
					// support_card_YB_on 1开启易宝判断卡 0关闭易宝判断卡
					String support_card_YB_on = paramService.searchSysParameter().get("support_card_YB_on");
					returnJson.put("supportCardYB", support_card_YB_on);
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			logger.error("商户交易卡查询error", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----商户交易卡查询接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.12 登录密码更改接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateLoginPwd", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject updateLoginPwd(HttpServletRequest request) {
		logger.info("----登录密码更改接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {

			JSONObject json = commonService.getRequstBody(request);
			logger.info("updateLoginPwd json={}", json.toJSONString());
			String agentId = json.getString("agentId");// 商户ID
			String phone = json.getString("phone");
			// String smsCode = json.getString("smsCode");
			// String msgCode = json.getString("msgCode");
			String oldPwd = json.getString("oldPwd");
			String newPwd = json.getString("newPwd");

			if (StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(phone) && StringUtil.isNotEmpty(oldPwd) && StringUtil.isNotEmpty(newPwd)) {

				/* 验证验证码的有效性 */
				// Boolean flag = msgHistoryService.validateSmsCode(smsCode,
				// msgCode);
				Boolean flag = true;
				if (flag) {
					Boolean isUpdate = userService.updateUserPsw(phone, oldPwd, newPwd, true, agentId);
					if (isUpdate) {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					} else {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_002);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_002));
					}
				} else {
					/* 验证码失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_001);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_001));
				}
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----登录密码更改接口 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.12 忘记密码更改接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/forgetLoginPwd", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject forgetLoginPwd(HttpServletRequest request) {
		logger.info("----登录密码更改接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {

			JSONObject json = commonService.getRequstBody(request);
			logger.info("forgetLoginPwd json={}", json.toJSONString());
			String agentId = json.getString("agentId");// 商户ID
			String phone = json.getString("phone");
			String smsCode = json.getString("smsCode");
			String msgCode = json.getString("msgCode");
			String newPwd = json.getString("newPwd");

			if (StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(phone) && StringUtil.isNotEmpty(smsCode) && StringUtil.isNotEmpty(msgCode) && StringUtil.isNotEmpty(newPwd)) {

				/* 验证验证码的有效性 */
				Boolean flag = msgHistoryService.validateSmsCode(smsCode, msgCode);

				if (flag) {
					Boolean isUpdate = userService.updateUserPsw(phone, null, newPwd, true, agentId);
					if (isUpdate) {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					} else {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_002);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_002));
					}
				} else {
					/* 验证码失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_001);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_001));
				}
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----登录密码更改接口 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.12 结算密码更改接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateTransPwd", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject updateTransPwd(HttpServletRequest request) {
		logger.info("----结算密码更改接口 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("updateTransPwd json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				String agentId = json.getString("agentId");// 商户ID
				String phone = json.getString("phone");
				String smsCode = json.getString("smsCode");
				String msgCode = json.getString("msgCode");
				String oldPwd = json.getString("oldPwd");
				String newPwd = json.getString("newPwd");

				if (StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(phone) && StringUtil.isNotEmpty(smsCode) && StringUtil.isNotEmpty(msgCode) && StringUtil.isNotEmpty(newPwd)) {

					/* 验证验证码的有效性 */
					Boolean flag = msgHistoryService.validateSmsCode(smsCode, msgCode);

					if (flag) {
						Boolean isUpdate = userService.updateUserPsw(phone, oldPwd, newPwd, false, agentId);
						if (isUpdate) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_003);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_003));
						}
					} else {
						/* 验证码失败 */
						returnJson.put("respCode", GlobalConstant.RESP_CODE_001);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_001));
					}
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.12 初始化结算密码接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createTransPwd", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject createTransPwd(HttpServletRequest request) {
		logger.info("---- 初始化结算密码接口 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			logger.info("createTransPwd json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				String phone = json.getString("phone");
				String newPwd = json.getString("newPwd");

				if (StringUtil.isNotEmpty(phone) && StringUtil.isNotEmpty(newPwd)) {

					Boolean isUpdate = userService.updateUserPsw(phone, null, newPwd, false, agentId);
					if (isUpdate) {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					} else {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_002);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_002));
					}
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 初始化结算密码接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.12 获取个人中心
	 * 
	 * @category 获取个人中心
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryMerInfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryMerInfo(HttpServletRequest request) {
		logger.info("----查询用户基础信息接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryMerInfo json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				User user = userService.get(Long.parseLong(merId));

				returnJson.put("merId", merId);
				returnJson.put("merCode", user.getCode());
				returnJson.put("phone", user.getLoginName());
				returnJson.put("realName", user.getRealName());
				returnJson.put("speechType", user.getSpeechType());
				if (StringUtil.isNotBlank(user.getMerchantName()) && user.getAuthenticationStatus() - User.authentication_status.SUCCESS.getCode() == 0) {
					returnJson.put("merchantName", user.getMerchantName());
				} else {
					returnJson.put("merchantName", user.getRealName());
				}

				// 隐私权限开关
				if (user.getPrivacyType() != null) {
					returnJson.put("privacyType", user.getPrivacyType());
				} else {
					returnJson.put("privacyType", 1);
				}

				// 一键代还开关
				if (user.getLoanType() != null) {
					returnJson.put("loanType", user.getLoanType());
				} else {
					returnJson.put("loanType", 1);
				}
				// Torganization t =
				// organizationService.getTorganizationInCodeTwo(user.getLoginName());
				// 查询用户是否是代理商加上所属平台限制
				Torganization t = organizationService.getTorganizationByAgentIdAndPhone(agentId, user.getLoginName());
				if (t != null) {
					returnJson.put("merType", t.getOrgType());
				} else {
					returnJson.put("merType", user.getUserType());
				}
				returnJson.put("openDate", DateUtil.getDateTime("yyyyMMddHHmmss", user.getCreateDatetime()));
				returnJson.put("authenticationStatus", user.getAuthenticationStatus());
				returnJson.put("merchantType", user.getMerchantType());
				String merchAuthMsg = StringUtil.isEmpty(user.getMerchantAuthMsg()) ? "" : user.getMerchantAuthMsg();
				returnJson.put("merchantAuthMsg", merchAuthMsg);
				returnJson.put("settlementStatus", user.getSettlementStatus());

				// 再次检测卡表中是否真的有结算卡 --开始
				if (user.getSettlementStatus() == 1) {
					// sys_user表中显示已经绑卡
					if (!userCardService.checkExistSettlementCard(user.getId())) {
						// 若实际不存在结算卡，将用户结算卡状态改为0
						if (!userService.editSettlementStauts(user.getId(), 0)) {
							// 更改用户结算卡状态失败
							returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
							returnJson.put("respDesc", "读取结算卡状态异常，请联系客服");
							return returnJson;
						} else {
							user.setSettlementStatus(0);
						}
					}
				} else {
					// sys_user表中显示未绑卡
					if (userCardService.checkExistSettlementCard(user.getId())) {
						if (!userService.editSettlementStauts(user.getId(), 1)) {
							// 更改用户结算卡状态失败
							returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
							returnJson.put("respDesc", "读取结算卡状态异常，请联系客服");
							return returnJson;
						} else {
							user.setSettlementStatus(1);
						}
					}
				}
				// 再次检测卡表中是否真的有结算卡 --结束

				returnJson.put("iconPath", user.getIconPath());
				returnJson.put("isSetTransPwd", StringUtil.isNotBlank(user.getStmPsw()) ? 1 : 0);
				returnJson.put("isChnl", user.getIsChnl());
				Account acc = accountService.getAccountByUserId(Long.parseLong(merId));
				returnJson.put("avlAmt", acc.getAvlAmt().setScale(2, BigDecimal.ROUND_FLOOR)); // 用户可用余额

				AccountPoint accPoint = accountPointService.getAccountPointByUserId(Long.parseLong(merId));
				returnJson.put("avlPoint", accPoint.getPoint());

				returnJson.put("perMonthInAmt", acc.getPerMonthInAmt().setScale(2, BigDecimal.ROUND_FLOOR));
				returnJson.put("perMonthOutAmt", acc.getPerMonthOutAmt().setScale(2, BigDecimal.ROUND_FLOOR));
				List<UserCard> cards = cardService.findCarsByUserId(Long.parseLong(merId), UserCard.card_type.J.name());
				for (UserCard userCard : cards) {
					if (userCard != null && userCard.getIsSettlmentCard() == 1) {
						returnJson.put("cardNo", ImportUtil.getEncCardNo(userCard.getCardNo()));
						returnJson.put("bankName", userCard.getBranchName());
						returnJson.put("bankIco", userCard.getBankIco());
					}
				}
				returnJson.put("unReadedNum", infoListService.countUnreadedMsgInfoByUserId(Long.parseLong(merId)));
				String credit_on = paramService.searchSysParameter().get("credit_on");
				returnJson.put("creditType", credit_on);
				String merchant_perfecting_on = paramService.searchSysParameter().get("merchant_perfecting_on");
				returnJson.put("merchantPerfectingType", merchant_perfecting_on);
				/* 储蓄卡张数 */
				returnJson.put("bankCardNum", cards.size());
				/* 提现功能开关 */
				String withdrawal_Type = paramService.searchSysParameter().get("withdrawal_Type");
				returnJson.put("withdrawalType", withdrawal_Type);
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----查询用户基础信息接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.13 商户交易渠道费率查询接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryChannelFeeInfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryChannelFeeInfo(HttpServletRequest request) {
		logger.info("---- 商户交易渠道费率查询接口 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryChannelFeeInfo json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号

			String channelType = json.getString("channelType");
			if (StringUtil.isNotEmpty(merId)) {
				if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && StringUtil.isNotBlank(channelType) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
					Integer accountType = 1;
					if (json.getInteger("channelType") != null) {
						accountType = json.getInteger("channelType");
					}
					returnJson.put("rate", userSettlementConfigService.getUserInputRate(Integer.parseInt(channelType), Long.parseLong(merId), accountType));
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				} else {
					/* token 过期 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				}
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 商户交易渠道费率查询接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.15 商户提现费率查询接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryMerFeeInfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryMerFeeInfo(HttpServletRequest request) {
		logger.info("---- 商户提现费率查询接口 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryMerFeeInfo json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			String agentId = json.getString("agentId");// 商户ID

			if (StringUtil.isNotEmpty(merId)) {
				if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
					UserSettlementConfig usc = userService.getStmConfigByUserId(Long.parseLong(merId));
					returnJson.put("currentDate", DateUtil.convertCurrentDateTimeToString());
					returnJson.put("isStm", usc.getStmUser().getSettlementStatus());
					returnJson.put("isSetTransPwd", StringUtil.isNotBlank(usc.getStmUser().getStmPsw()) ? 1 : 0);
					returnJson.put("feeT0", usc.getT0Fee().toString());
					returnJson.put("feeT1", usc.getT1Fee().toString());
					returnJson.put("maxFeeAmtT0", usc.getMaxT0Amt().toString());
					returnJson.put("minFeeAmtT0", usc.getMinT0Amt().toString());
					returnJson.put("maxFeeAmtT1", usc.getMaxT1Amt().toString());
					returnJson.put("minFeeAmtT1", usc.getMinT1Amt().toString());
					returnJson.put("rebateFee", usc.getRabaleFee().toString());
					returnJson.put("maxRebateFee", usc.getMaxRabaleAmt().toString());
					if (agentId.equals("F20160015") || agentId.equals("F20160017") || agentId.equals("F20160001")) {
						returnJson.put("minRebateFee", "10");
						returnJson.put("minBrokerageFee", "10");
					} else {
						returnJson.put("minRebateFee", usc.getMinRabaleAmt().toString());
					}
					/* D0 T1提现 */
					String dexchange_Type = paramService.searchSysParameter().get("dexchange_Type");
					returnJson.put("dexchangeType", dexchange_Type);
					returnJson.put("inputFee", usc.getInputFee().toString());
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				} else {
					/* token 过期 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				}
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 商户提现费率查询接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.16 余额查询接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryMerBal", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryMerBal(HttpServletRequest request) {
		logger.info("----更新结算银行卡接口-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryMerBal json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String acctType = json.getString("acctType");// 账户类型
				if (StringUtil.isNotEmpty(acctType)) {
					BigDecimal avlBal = BigDecimal.ZERO;
					if ("PAY0".equals(acctType.trim())) {
						Account acc = accountService.getAccountByUserId(Long.parseLong(merId));
						avlBal = acc.getAvlAmt().setScale(2, BigDecimal.ROUND_FLOOR);
					} else if ("RATE".equals(acctType.trim())) {
						Brokerage b = brokerageService.getBrokerageByUserId(Long.parseLong(merId));
						avlBal = b.getBrokerage().setScale(2, BigDecimal.ROUND_FLOOR);
					}
					returnJson.put("avlBal", avlBal.toString());
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));

				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}

			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.17 消费订单状态查询接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/searchOrderStatus", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject searchOrderStatus(HttpServletRequest request) {
		logger.info("----消费订单状态查询接口 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("searchOrderStatus json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String orderNum = json.getString("fullOrderNum");// 账户类型
				if (StringUtil.isNotEmpty(orderNum)) {
					orderService.dealReSentSearchOrder(orderNum);
					UserOrder userorder = new UserOrder();
					userorder.setOrderNum(orderNum);
					if (order != null) {
						PageFilter pf = new PageFilter();
						pf.setPage(0);
						pf.setRows(5);
						List<UserOrder> orders = orderService.dataGrid(userorder, pf);

						returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
						for (UserOrder d : orders) {
							converOrderDetailToJson(returnJson, d);
							break;
						}
						returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					} else {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_046);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_046));
					}

				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}

			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			logger.error("消费订单状态查询接口 error", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----消费订单状态查询接口end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 将订单明细 转换为json
	 * 
	 * @param returnJson
	 * @param userOrder
	 */
	private void converOrderDetailToJson(JSONObject returnJson, UserOrder userOrder) {
		returnJson.put("orderId", userOrder.getId());
		returnJson.put("createDateTime", DateUtil.getyyyyMMddHHmmssStringFromDate(userOrder.getCreateTime()));
		if (userOrder.getPayOrder() != null && userOrder.getPayOrder().getFinishDate() != null) {
			returnJson.put("succDateTime", DateUtil.getyyyyMMddHHmmssStringFromDate(userOrder.getPayOrder().getFinishDate()));
		}
		String orderNum = userOrder.getOrderNum();
		returnJson.put("fullOrderNum", orderNum);
		if (StringUtil.isNotBlank(orderNum) && orderNum.length() > 19) {
			orderNum = orderNum.substring(orderNum.length() - 19);
		}
		returnJson.put("orderNum", orderNum);
		returnJson.put("orderAmt", userOrder.getOrgAmt().setScale(2, BigDecimal.ROUND_FLOOR));
		returnJson.put("orderFee", userOrder.getFee().setScale(2, BigDecimal.ROUND_FLOOR));
		returnJson.put("orderType", userOrder.getType());

		String orderStatus = "I";
		if (userOrder.getStatus().intValue() < 200) {
			orderStatus = "S";
		} else if (userOrder.getStatus().intValue() >= 200 && userOrder.getStatus().intValue() < 300) {
			orderStatus = "F";
		}
		returnJson.put("isReSearchStatus", 0);
		if (userOrder.getStatus() - UserOrder.order_status.PROCESSING.getCode() == 0 && UserOrder.getCollectOrderTypes().contains(userOrder.getType())) {
			returnJson.put("isReSearchStatus", 1);
		}
		returnJson.put("orderStatus", orderStatus);
		returnJson.put("cdType", UserOrder.cd_type.C.name().equals(userOrder.getCdType()) ? -1 : 1);
		returnJson.put("payType", userOrder.getPayType());
		returnJson.put("remark", userOrder.getDescription());
		returnJson.put("accType", userOrder.getInputAccType());
		if (userOrder.getUserCard() != null) {
			returnJson.put("cardNo", ImportUtil.getDecCardNo(userOrder.getUserCard().getCardNo()));
			returnJson.put("bankName", userOrder.getUserCard().getBankName());
		}
		returnJson.put("transferAccPhone", userOrder.getTransPhone());
		returnJson.put("transPayType", userOrder.getTransPayType());// 支付订单类型
		returnJson.put("channelType", userOrder.getChannelType());// 订单是否手动提现（直通车）
	}

	/**
	 * 2.3.18 微信\支付宝\京东消费订单接口 大商户模式
	 * 
	 * @category 微信 支付宝 京东
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createWxQrPay", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject createWxQrPay(HttpServletRequest request) {
		logger.info("----微信消费订单接口-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("createWxQrPay json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				String transAmt = json.getString("transAmt");// 账户类型
				// WXQR 微信二维码 WXSM 微信扫码支付
				String transType = json.getString("transType");// 支付类型
				String authCode = json.getString("authCode");// 支付类型
				// 10 普通收款 20 代理费收款
				String transPayType = json.getString("transPayType");// 支付用途
				// 0： 即时到账 5：T+5到账
				String accType = json.getString("accType");// 支付用途

				if (!"20".equals(transPayType)) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
					returnJson.put("respDesc", "交易失败,请联系客服");
					return commonService.getEncryptBody(returnJson);
				}

				// 判断首笔成功交易
				String switch_ylzx_first_succ_txn = paramService.searchSysParameter().get("switch_ylzx_first_succ_txn");
				if ("1".equals(switch_ylzx_first_succ_txn) && "10".equals(transPayType)) {
					String firstUnipay = orderService.adjustFirstUnipayInfo(Long.parseLong(merId));
					if (!"1".equals(firstUnipay)) {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_087);
						returnJson.put("respDesc", "首笔交易请使用银联在线");
						return commonService.getEncryptBody(returnJson);
					}
				}
				if (StringUtil.isNullOrEmpty(accType)) {
					accType = "0";
					logger.info("没有传入accType参数");
				}
				if (holidayService.isD0Work() && accType.equals("0")) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "D0收款升级");
					return commonService.getEncryptBody(returnJson);
				}
				if (holidayService.isD0Work() && accType.equals("10")) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "D0收款升级");
					return commonService.getEncryptBody(returnJson);
				}
				// 专门针对翼支付，翼支付原参数为YIQR，改为YZFQR等待飞飞APP再次改版，修复所有APP错误后再去掉 by
				// liangchao at 2017-10-18
				// start

				if (StringUtil.isBlank(transType)) {
					transType = "YZFQR";
				}
				// end
				if (StringUtil.isNotEmpty(transAmt) && StringUtil.isNotEmpty(transType) && StringUtil.isNotEmpty(transPayType)
						&& (UserOrder.trans_type.WXQR.name().equals(transType) || UserOrder.trans_type.ALQR.name().equals(transType) || UserOrder.trans_type.JDQR.name().equals(transType) || UserOrder.trans_type.QQQR.name().equals(transType)
								|| UserOrder.trans_type.BDQR.name().equals(transType) || UserOrder.trans_type.YZFQR.name().equals(transType) || UserOrder.trans_type.YLQR.name().equals(transType)
								|| (UserOrder.trans_type.WXSM.name().equals(transType) && StringUtil.isNotEmpty(authCode)) || (UserOrder.trans_type.ALSM.name().equals(transType) && StringUtil.isNotEmpty(authCode))

						)) {

					// if (StringUtil.isLegalAMT(transAmt)) {

					/* 判断扫码的授权码是否是数字 */
					if ((UserOrder.trans_type.WXSM.name().equals(transType) && StringUtil.isNotEmpty(authCode)) || (UserOrder.trans_type.ALSM.name().equals(transType) && StringUtil.isNotEmpty(authCode))) {
						if (!authCode.matches("[0-9]*")) {
							returnJson.put("respCode", "E90001");
							returnJson.put("respDesc", "您扫码的支付授权码无效，请重试");
							return commonService.getEncryptBody(returnJson);
						}
					}
					User user = userService.get(Long.parseLong(merId));

					String flag = userService.isAllowUserPay(user.getId(), transType);

					if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
						String stmFlag = holidayService.isLimitTimeInterval(new Date(), true, null);
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {

							Double money = Double.valueOf(transAmt);
							Map<String, String> returnOrder = null;
							Integer transPayTypeInt = Integer.parseInt(transPayType);
							String getMoneyer = StringUtil.isNotBlank(user.getRealName()) ? "商户-" + user.getRealName() : user.getLoginName();
							// getMoneyer = UserOrder.getTypeQR(transType) +
							// getMoneyer;
							if (user.getMerchantType() == User.merchant_type.REAL_MERCHANT.getCode() || user.getMerchantType() == User.merchant_type.NONE_MERCHANT.getCode()) {
								if (StringUtil.isNotBlank(user.getMerchantName())) {
									getMoneyer = StringUtil.isNotBlank(user.getMerchantShortName()) ? user.getMerchantShortName() : user.getMerchantName();
								}
							}
							String desc = "线下扫码";
							Integer angentType = 0;
							if (transPayTypeInt == 20) {
								angentType = json.getInteger("agentType");
								if (angentType != null) {
									String aAgent = "钻石用户";
									String bAgent = "金牌用户";
									if (agentId.equals("F20160002")) { // 泊力商务
										aAgent = "高级会员";
										bAgent = "中级会员";
									} else if (agentId.equals("F20160017")) { // 夏商云联
										aAgent = "店长会员";
										bAgent = "店员会员";
									} else {
										aAgent = "钻石用户";
										bAgent = "金牌用户";
									}
									desc = String.format(WeixinUtil.DESC_SELF_PAY_AGENT, money, angentType == 21 ? aAgent : bAgent);
								}
								flag = orderService.isAllowPayAgent(user, angentType);
								if (!GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
									/* 购买代理过于频繁 */
									returnJson.put("respCode", flag);
									returnJson.put("respDesc", GlobalConstant.map.get(flag));
									return commonService.getEncryptBody(returnJson);
								}
							}
							/* 有无支付通道 */
							Boolean isChannel = true;
							if (UserOrder.trans_type.WXQR.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.WXQR.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createUnifiedOrder(user, cpr, Integer.parseInt(accType), money, transPayTypeInt, angentType, desc);
								}
							} else if (UserOrder.trans_type.WXSM.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.WXSM.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createSmUnifiedOrder(user, cpr, Integer.parseInt(accType), authCode, money, transPayTypeInt, angentType, desc);
								}

							} else if (UserOrder.trans_type.ALQR.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.ALQR.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createUnifiedOrder(user, cpr, Integer.parseInt(accType), money, transPayTypeInt, angentType, desc);
								}
							} else if (UserOrder.trans_type.ALSM.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.ALSM.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createSmUnifiedOrder(user, cpr, Integer.parseInt(accType), authCode, money, transPayTypeInt, angentType, desc);
								}
							} else if (UserOrder.trans_type.JDQR.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.JDQR.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createUnifiedOrder(user, cpr, Integer.parseInt(accType), money, transPayTypeInt, angentType, desc);
								}
							} else if (UserOrder.trans_type.JDSM.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.JDSM.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createSmUnifiedOrder(user, cpr, Integer.parseInt(accType), authCode, money, transPayTypeInt, angentType, desc);
								}
							} else if (UserOrder.trans_type.BDQR.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.BDQR.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createUnifiedOrder(user, cpr, Integer.parseInt(accType), money, transPayTypeInt, angentType, desc);
								}
							} else if (UserOrder.trans_type.BDSM.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.BDSM.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createSmUnifiedOrder(user, cpr, Integer.parseInt(accType), authCode, money, transPayTypeInt, angentType, desc);
								}
							} else if (UserOrder.trans_type.YZFQR.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.YZFQR.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createUnifiedOrder(user, cpr, Integer.parseInt(accType), money, transPayTypeInt, angentType, desc);
								}
							} else if (UserOrder.trans_type.YISM.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.YISM.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createSmUnifiedOrder(user, cpr, Integer.parseInt(accType), authCode, money, transPayTypeInt, angentType, desc);
								}
							} else if (UserOrder.trans_type.YLQR.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.YLQR.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createUnifiedOrder(user, cpr, Integer.parseInt(accType), money, transPayTypeInt, angentType, desc);
								}
							} else if (UserOrder.trans_type.YLSM.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.YLSM.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createSmUnifiedOrder(user, cpr, Integer.parseInt(accType), authCode, money, transPayTypeInt, angentType, desc);
								}
							} else if (UserOrder.trans_type.QQQR.name().equals(transType)) {
								ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.QQQR.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
								if (cpr == null) {
									isChannel = false;
								} else {
									returnOrder = cpr.getChannelPaymentService().createUnifiedOrder(user, cpr, Integer.parseInt(accType), money, transPayTypeInt, angentType, desc);
								}
							}
							if (returnOrder != null && returnOrder.get("return_code").equals(GlobalConstant.SUCCESS)) {

								if (GlobalConstant.SUCCESS.equals(returnOrder.get("result_code"))) {

									returnJson.put("orderNum", returnOrder.get("orderNum"));
									if (returnOrder.containsKey("code_url")) {
										returnJson.put("transQrUrl", returnOrder.get("code_url"));// 支付二维码
									}
									returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
									returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
								} else if (GlobalConstant.WAITING.equals(returnOrder.get("result_code"))) {
									returnJson.put("respCode", GlobalConstant.RESP_CODE_068);
									returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_068));
								}
							} else if (!isChannel) {
								/* 无支付通道 */
								returnJson.put("respCode", GlobalConstant.RESP_CODE_500);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_500));
							} else {
								/* 交易失败 */
								returnJson.put("respCode", GlobalConstant.RESP_CODE_047);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_047));
							}
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_066);
							returnJson.put("respDesc", stmFlag);
						}
					} else {
						/* 交易失败 */
						returnJson.put("respCode", flag);
						returnJson.put("respDesc", GlobalConstant.map.get(flag));
					}

					// } else {
					// /* 参数失败 */
					// returnJson.put("respCode", GlobalConstant.RESP_CODE_069);
					// returnJson.put("respDesc",
					// GlobalConstant.map.get(GlobalConstant.RESP_CODE_069));
					// }
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));

				}

			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			logger.error("创建订单异常", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_994);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_994));
		}
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.19 银联在线消费订单接口
	 * 
	 * @category 银联在线
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createOnlineBankPay", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject createOnlineBankPay(HttpServletRequest request) {

		logger.info("----银联在线消费订单接口 begin-----");//
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("createOnlineBankPay json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID

			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String transAmt = json.getString("transAmt");// 账户类型
				String frontUrl = json.getString("frontUrl");// 前端返回地址
				String cardId = json.getString("cardId");// 交易卡ID
				String cardNo = json.getString("cardNo");// 交易卡号
				String transPayType = json.getString("transPayType");// 交易卡号

				/*
				 * 0： 即时到账 5：T+5到账
				 */
				String accType = json.getString("accType");// 支付用途
				if (StringUtil.isNullOrEmpty(accType)) {
					accType = "0";
					logger.info("没有传入accType参数");
				}
				if (holidayService.getisYILIAND0Work() && accType.equals("0") && accType.equals("10")) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "D0收款升级");
					return commonService.getEncryptBody(returnJson);
				}
				if (StringUtil.isNotEmpty(frontUrl) && StringUtil.isNotEmpty(transAmt) && StringUtil.isNotEmpty(transPayType) && (StringUtil.isNotEmpty(cardId) || StringUtil.isNotEmpty(cardNo))) {
					User user = userService.get(Long.parseLong(merId));

					String flag = userService.isAllowUserPay(user.getId(), UserOrder.trans_type.YLZX.name());
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {

						String stmFlag = holidayService.isLimitTimeInterval(new Date(), true, null);
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {

							Integer transPayTypeInt = Integer.parseInt(transPayType);
							Integer agentType = 0;
							if (transPayTypeInt == 20) {
								agentType = json.getInteger("agentType");// 支付类型
								flag = orderService.isAllowPayAgent(user, agentType);
								if (!GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
									/* 购买代理过于频繁 */
									returnJson.put("respCode", flag);
									returnJson.put("respDesc", GlobalConstant.map.get(flag));
									return commonService.getEncryptBody(returnJson);
								}
							}

							if (StringUtil.isNotBlank(json.getString("cvv"))) {
								String realCvv = ImportUtil.getDecCvv(json.getString("cvv"));
								json.put("cvv", realCvv);
							}
							if (StringUtil.isNotBlank(json.getString("cardNo"))) {
								String realCardNo = ImportUtil.getDecCardNo(json.getString("cardNo"));
								json.put("cardNo", realCardNo);
							}
							json.put("agentId", agentId);
							Map<String, String> cardInfos = json.toJavaObject(json, Map.class);
							Map<String, String> flags = cardService.sendCardInfoToBankValidateTwo(cardInfos, false, false, agentId);
							if (GlobalConstant.RESP_CODE_SUCCESS.equals(flags.get("flag"))) {
								if (StringUtil.isNotBlank(json.getString("cardNo"))) {
									cardInfos.put("cardId", cardService.getfindCode(merId, cardInfos.get("cardNo")));
								}
								Double money = Double.valueOf(transAmt);
								String ylzx_max_amt_per_time = paramService.searchSysParameter().get("ylzx_max_amt_per_time");
								if (money - Double.parseDouble(ylzx_max_amt_per_time) <= 0) {
									ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.YLZX.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
									if (cpr == null) {
										/* 无支付通道 */
										returnJson.put("respCode", GlobalConstant.RESP_CODE_500);
										returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_500));
									} else {
										Map<String, String> payResponse = cpr.getChannelPaymentService().createYLZXOrder_v2(user, cpr, Integer.parseInt(accType), Long.parseLong(cardInfos.get("cardId")), frontUrl, money,
												Integer.parseInt(transPayType), agentType, null);
										if (payResponse.containsKey("flag") && GlobalConstant.RESP_CODE_SUCCESS.equals(payResponse.get("flag"))) {
											returnJson.put("content", payResponse.get("html"));
											returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
											returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
										} else {
											/* 交易失败 */
											returnJson.put("respCode", payResponse.get("flag"));
											String msg = payResponse.get("flagMsg") == null ? GlobalConstant.map.get(payResponse.get("flag")) : payResponse.get("flagMsg");
											returnJson.put("respDesc", msg);
										}
									}
								} else {
									returnJson.put("respCode", GlobalConstant.RESP_CODE_047);
									returnJson.put("respDesc", "银联在线单笔交易不得超过" + ylzx_max_amt_per_time + "元");
								}
							} else {
								/* 交易失败 */
								returnJson.put("respCode", flags.get("flag"));
								returnJson.put("respDesc", GlobalConstant.map.get(flags.get("flag")));
								if (flags.containsKey("errorCode")) {
									returnJson.put("respCode", flags.get("errorCode"));
									returnJson.put("respDesc", GlobalConstant.map.get(flags.get("errorInfo")));
								}
							}
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_066);
							returnJson.put("respDesc", stmFlag);
						}
					} else {
						/* 交易失败 */
						returnJson.put("respCode", flag);
						returnJson.put("respDesc", GlobalConstant.map.get(flag));
					}

				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}

			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----银联在线消费订单接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.19 银联在线直通车消费订单接口
	 * 
	 * @category 银联在线
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createOnlineThroughBankPay", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject createOnlineThroughBankPay(HttpServletRequest request) {

		logger.info("----银联在线直通车消费订单接口 begin-----");//
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("createOnlineThroughBankPay json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID

			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String transAmt = json.getString("transAmt");// 账户类型
				String frontUrl = json.getString("frontUrl");// 前端返回地址
				// String cardId = json.getString("cardId");// 交易卡ID
				// String cardNo = json.getString("cardNo");// 交易卡号
				String transPayType = json.getString("transPayType");// 交易卡号

				/*
				 * 0： 即时到账 5：T+5到账
				 */
				String accType = json.getString("accType");// 支付用途
				if (StringUtil.isNullOrEmpty(accType)) {
					accType = "0";
					logger.info("没有传入accType参数");
				}
				if (StringUtil.isNotEmpty(frontUrl) && StringUtil.isNotEmpty(transAmt) && StringUtil.isNotEmpty(transPayType)) {
					User user = userService.get(Long.parseLong(merId));

					String flag = userService.isAllowUserPay(user.getId(), UserOrder.trans_type.YLZX.name());
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {

						String stmFlag = holidayService.isLimitTimeInterval(new Date(), true, null);
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {

							Integer transPayTypeInt = Integer.parseInt(transPayType);
							Integer agentType = 0;
							if (transPayTypeInt == 20) {
								agentType = json.getInteger("agentType");// 支付类型
								flag = orderService.isAllowPayAgent(user, agentType);
								if (!GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
									/* 购买代理过于频繁 */
									returnJson.put("respCode", flag);
									returnJson.put("respDesc", GlobalConstant.map.get(flag));
									return commonService.getEncryptBody(returnJson);
								}
							}

							if (StringUtil.isNotBlank(json.getString("cvv"))) {
								String realCvv = ImportUtil.getDecCvv(json.getString("cvv"));
								json.put("cvv", realCvv);
							}
							if (StringUtil.isNotBlank(json.getString("cardNo"))) {
								String realCardNo = ImportUtil.getDecCardNo(json.getString("cardNo"));
								json.put("cardNo", realCardNo);
							}
							json.put("agentId", agentId);
							Map<String, String> cardInfos = json.toJavaObject(json, Map.class);
							Map<String, String> flags = cardService.sendCardInfoToBankValidateTwo(cardInfos, false, false, agentId);
							if (GlobalConstant.RESP_CODE_SUCCESS.equals(flags.get("flag"))) {
								if (StringUtil.isNotBlank(json.getString("cardNo"))) {
									cardInfos.put("cardId", cardService.getfindCode(merId, cardInfos.get("cardNo")));
								}
								Double money = Double.valueOf(transAmt);
								String ylzx_max_amt_per_time = paramService.searchSysParameter().get("ylzx_max_amt_per_time");
								if (money - Double.parseDouble(ylzx_max_amt_per_time) <= 0) {
									ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.YLZXT.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
									if (cpr == null) {
										/* 无支付通道 */
										returnJson.put("respCode", GlobalConstant.RESP_CODE_500);
										returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_500));
									} else {
										Map<String, String> payResponse = cpr.getChannelPaymentService().createYLZXOrder_v2(user, cpr, Integer.parseInt(accType), Long.parseLong(cardInfos.get("cardId")), frontUrl, money,
												Integer.parseInt(transPayType), agentType, null);
										if (payResponse.containsKey("flag") && GlobalConstant.RESP_CODE_SUCCESS.equals(payResponse.get("flag"))) {
											returnJson.put("orderNum", payResponse.get("tn"));
											returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
											returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
										} else {
											/* 交易失败 */
											returnJson.put("respCode", payResponse.get("flag"));
											returnJson.put("respDesc", payResponse.get("flagMSG"));
										}
									}
								} else {
									returnJson.put("respCode", GlobalConstant.RESP_CODE_047);
									returnJson.put("respDesc", "银联在线单笔交易不得超过" + ylzx_max_amt_per_time + "元");
								}
							} else {
								/* 交易失败 */
								returnJson.put("respCode", flags.get("flag"));
								returnJson.put("respDesc", GlobalConstant.map.get(flags.get("flag")));
								if (flags.containsKey("errorCode")) {
									returnJson.put("respCode", flags.get("errorCode"));
									returnJson.put("respDesc", GlobalConstant.map.get(flags.get("errorInfo")));
								}
							}
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_066);
							returnJson.put("respDesc", stmFlag);
						}
					} else {
						/* 交易失败 */
						returnJson.put("respCode", flag);
						returnJson.put("respDesc", GlobalConstant.map.get(flag));
					}

				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}

			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----银联在线直通车消费订单接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.19 银联在线积分消费订单接口--易宝专用 和 createOnlineThroughBankPayJF 方法保持同步
	 * 
	 * @category 银联在线
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createOnlineThroughBankPayJFForYiBao", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject createOnlineThroughBankPayJFFromYiBao(HttpServletRequest request) {

		logger.info("----银联在线积分消费订单接口--易宝接口 begin-----");//
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			String merId = json.getString("merId");// 商户ID

			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String transAmt = json.getString("transAmt");// 账户类型
				String frontUrl = json.getString("frontUrl");// 前端返回地址
				String cardId = json.getString("cardId");// 交易卡ID
				String cardNo = json.getString("cardNo");// 交易卡号
				String transPayType = json.getString("transPayType");

				/*
				 * 0： 即时到账 5：T+5到账
				 */
				String accType = json.getString("accType");// 支付用途
				if (StringUtil.isNullOrEmpty(accType)) {
					accType = "0";
					logger.info("没有传入accType参数");
				}
				if (StringUtil.isNotEmpty(frontUrl) && StringUtil.isNotEmpty(transAmt) && StringUtil.isNotEmpty(transPayType) && (StringUtil.isNotEmpty(cardId) || StringUtil.isNotEmpty(cardNo))) {
					User user = userService.get(Long.parseLong(merId));

					String flag = userService.isAllowUserPay(user.getId(), UserOrder.trans_type.YLZX.name());
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {

						String stmFlag = holidayService.isLimitTimeInterval(new Date(), true, null);
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {

							Integer transPayTypeInt = Integer.parseInt(transPayType);
							Integer agentType = 0;
							if (transPayTypeInt == 20) {
								agentType = json.getInteger("agentType");// 支付类型
								flag = orderService.isAllowPayAgent(user, agentType);
								if (!GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
									/* 购买代理过于频繁 */
									returnJson.put("respCode", flag);
									returnJson.put("respDesc", GlobalConstant.map.get(flag));
									return commonService.getEncryptBody(returnJson);
								}
							}

							if (StringUtil.isNotBlank(json.getString("cvv"))) {
								String realCvv = ImportUtil.getDecCvv(json.getString("cvv"));
								json.put("cvv", realCvv);
							}
							if (StringUtil.isNotBlank(json.getString("cardNo"))) {
								String realCardNo = ImportUtil.getDecCardNo(json.getString("cardNo"));
								json.put("cardNo", realCardNo);
							}
							json.put("agentId", agentId);
							Map<String, String> cardInfos = json.toJavaObject(json, Map.class);
							Map<String, String> flags = cardService.sendCardInfoToBankValidateTwo(cardInfos, false, false, agentId);
							if (GlobalConstant.RESP_CODE_SUCCESS.equals(flags.get("flag"))) {
								if (StringUtil.isNotBlank(json.getString("cardNo"))) {
									cardInfos.put("cardId", cardService.getfindCode(merId, cardInfos.get("cardNo")));
								}
								Double money = Double.valueOf(transAmt);

								// 读取通道限额配置的金额 新增类型为551，代表为易宝
								PayTypeLimitConfig c = payTypeLimitConfigService.getPayType(300, agentId, 551);
								if (c == null) {
									returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
									returnJson.put("respDesc", "通道配置有误");
									return commonService.getEncryptBody(returnJson);
								}

								Double minAmt = c.getMinAmt().doubleValue();
								BigDecimal m = BigDecimal.valueOf(money);// 将Double转换成BigDecimal
								if (m.compareTo(c.getMinAmt()) == -1) {// 比较值是否相等
									// 收款金额小于通道最低限额
									returnJson.put("respCode", GlobalConstant.RESP_CODE_047);
									returnJson.put("respDesc", "银联在线单笔交易不得小于" + c.getMinAmt() + "元");
									return commonService.getEncryptBody(returnJson);
								}
								if (m.compareTo(c.getMaxAmt()) == 1) {
									// 收款金额大于通道最高限额
									returnJson.put("respCode", GlobalConstant.RESP_CODE_047);
									returnJson.put("respDesc", "银联在线单笔交易不得大于" + c.getMaxAmt() + "元");
									return commonService.getEncryptBody(returnJson);
								}

								// 查询可用通道
								ChannelPayRef cpr = troughTrainServeice.getChannelPayRef(true, "551", Long.valueOf(merId), accType, "YIBAOZHITONGCHE");
								if (cpr == null) {
									/* 无支付通道 */
									// 调用开通子商户接口
									Map<String, String> openMer = yiBaoPaymentService.createSubMerchantByUserId(Long.valueOf(merId));
									if (!openMer.get("return_code").equals("000")) {
										returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
										returnJson.put("respDesc", openMer.get("return_msg"));
										return commonService.getEncryptBody(returnJson);
									} else {
										// 再次查询可用通道
										cpr = troughTrainServeice.getChannelPayRef(true, "551", Long.valueOf(merId), accType, "YIBAOZHITONGCHE");
									}
								}

								// 进入通道
								Map<String, String> payResponse = cpr.getChannelPaymentService().createYLZXOrder_v2(user, cpr, Integer.parseInt(accType), Long.parseLong(cardInfos.get("cardId")), frontUrl, money,
										Integer.parseInt(transPayType), agentType, null);
								if (payResponse.containsKey("flag") && GlobalConstant.RESP_CODE_SUCCESS.equals(payResponse.get("flag"))) {
									if (payResponse.get("type").equals("3")) {
										returnJson.put("type", "3");
										returnJson.put("content", payResponse.get("url"));
									}
									returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
									returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
								} else {
									/* 交易失败 */
									returnJson.put("respCode", payResponse.get("flag"));
									returnJson.put("respDesc", payResponse.get("flagMSG"));
								}
							} else {
								/* 交易失败 */
								returnJson.put("respCode", flags.get("flag"));
								returnJson.put("respDesc", GlobalConstant.map.get(flags.get("flag")));
								if (flags.containsKey("errorCode")) {
									returnJson.put("respCode", flags.get("errorCode"));
									returnJson.put("respDesc", GlobalConstant.map.get(flags.get("errorInfo")));
								}
							}
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_066);
							returnJson.put("respDesc", stmFlag);
						}
					} else {
						/* 交易失败 */
						returnJson.put("respCode", flag);
						returnJson.put("respDesc", GlobalConstant.map.get(flag));
					}

				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}

			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----银联在线积分消费订单接口 --易宝end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.19 银联在线积分消费订单接口
	 * 
	 * @category 银联在线
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createOnlineThroughBankPayJF", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject createOnlineThroughBankPayJF(HttpServletRequest request) {

		logger.info("----银联在线积分消费订单接口 begin-----");//
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			String merId = json.getString("merId");// 商户ID

			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			logger.info("userId=" + merId + "  token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String transAmt = json.getString("transAmt");// 账户类型
				String frontUrl = json.getString("frontUrl");// 前端返回地址
				String cardId = json.getString("cardId");// 交易卡ID
				String cardNo = json.getString("cardNo");// 交易卡号
				String transPayType = json.getString("transPayType");
				String channelCode = json.getString("channelCode");
				// String orderNo = json.getString("orderNo");

				/*
				 * 0： 即时到账 5：T+5到账
				 */
				String accType = json.getString("accType");// 支付用途
				if (StringUtil.isNullOrEmpty(accType)) {
					accType = "0";
					logger.info("没有传入accType参数");
				}
				if (StringUtil.isNotEmpty(frontUrl) && StringUtil.isNotEmpty(transAmt) && StringUtil.isNotEmpty(transPayType) && (StringUtil.isNotEmpty(cardId) || StringUtil.isNotEmpty(cardNo))) {
					User user = userService.get(Long.parseLong(merId));

					String flag = userService.isAllowUserPay(user.getId(), UserOrder.trans_type.YLZX.name());
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {

						String stmFlag = holidayService.isLimitTimeInterval(new Date(), true, null);
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {

							Integer transPayTypeInt = Integer.parseInt(transPayType);
							Integer agentType = 0;
							if (transPayTypeInt == 20) {
								agentType = json.getInteger("agentType");// 支付类型
								flag = orderService.isAllowPayAgent(user, agentType);
								if (!GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
									/* 购买代理过于频繁 */
									returnJson.put("respCode", flag);
									returnJson.put("respDesc", GlobalConstant.map.get(flag));
									return commonService.getEncryptBody(returnJson);
								}
							}
							if (StringUtil.isNotBlank(json.getString("cvv"))) {
								String realCvv = ImportUtil.getDecCvv(json.getString("cvv"));
								json.put("cvv", realCvv);
							}
							if (StringUtil.isNotBlank(json.getString("cardNo"))) {
								String realCardNo = ImportUtil.getDecCardNo(json.getString("cardNo"));
								json.put("cardNo", realCardNo);
							}
							json.put("agentId", agentId);
							Map<String, String> cardInfos = json.toJavaObject(json, Map.class);
							Map<String, String> flags = cardService.sendCardInfoToBankValidateTwo(cardInfos, false, false, agentId);
							if (GlobalConstant.RESP_CODE_SUCCESS.equals(flags.get("flag"))) {
								if (StringUtil.isNotBlank(json.getString("cardNo"))) {
									cardInfos.put("cardId", cardService.getfindCode(merId, cardInfos.get("cardNo")));
								}
								Double money = Double.valueOf(transAmt);
								// 查询 银联在线单笔最大金额为50000元 的开关状态
								String yilian_jifen_max_amt_per_time = paramService.searchSysParameter().get("yilian_jifen_max_amt_per_time"); // 必须加上这个配置，否则前端无法判断金额
								if (money - Double.parseDouble(yilian_jifen_max_amt_per_time) <= 0) {
									// 查询可用通道
									// ChannelPayRef cpr =
									// troughTrainServeice.getChannelPayRef(true,
									// "550",Long.parseLong(merId), accType,
									// "XINKKEYINLIAN");
									// 易联银联积分D0小额 800 - 20000
									// 用这种方法，无法累计交易金额和累计通道使用次数 ,accType
									// 用来判断大小额的，（参考银联小额通道路由），在这里无用

									// 检查嘎吱银联通道的开关
									// String gazhi_yinlian_on =
									// paramService.searchSysParameter().get("gazhi_yinlian_on");
									if ("SHENFUZTC".equals(channelCode)) {
										// TOD 申孚通道
										ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.YLZXE.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2,
												"SHENFUZTC");
										if (cpr == null) {
											/* 无支付通道 */
											returnJson.put("respCode", GlobalConstant.RESP_CODE_500);
											returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_500));
										} else {
											returnJson.put("type", "2");
											Map<String, String> payResponse = cpr.getChannelPaymentService().createUnipayOnlineThroughOrder(user, cpr, Integer.parseInt(accType), Long.parseLong(cardInfos.get("cardId")), frontUrl, money,
													Integer.parseInt(transPayType), agentType, null);
											if (payResponse == null) {
												returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
												returnJson.put("respDesc", "交易通道错误");
											} else {
												if (payResponse.containsKey("flag") && GlobalConstant.RESP_CODE_SUCCESS.equals(payResponse.get("flag"))) {
													returnJson.put("content", payResponse.get("html"));
													returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
													returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
												} else {
													/* 交易失败 */
													returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
													returnJson.put("respDesc", payResponse.get("flagMSG"));
												}
											}
										}
									} else if ("GAZHIJFZTC".equals(channelCode)) {
										// 走嘎吱银联通道
										ChannelPayRef cpr = troughTrainServeice.getChannelPayRef(true, "550", Long.valueOf(merId), accType, "GAZHIYINLIANJIFENZHITONGCHE");
										if (cpr == null) {
											// 去开通通道
											Map<String, String> res = gaZhiYinLianPaymentService.createSubMerchantByUserId(Long.valueOf(merId));
											if (!res.get("return_code").equals("000")) {
												returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
												returnJson.put("respDesc", "GZ商户报备失败");
												return commonService.getEncryptBody(returnJson);
											}
											// 重新查询
											cpr = troughTrainServeice.getChannelPayRef(true, "550", null, accType, "GAZHIYINLIANJIFENZHITONGCHE");
										}
										// 进入通道
										Map<String, String> payResponse = cpr.getChannelPaymentService().createUnipayOnlineThroughOrder(user, cpr, Integer.parseInt(accType), Long.parseLong(cardInfos.get("cardId")), frontUrl, money,
												Integer.parseInt(transPayType), agentType, null);
										if (payResponse.containsKey("flag") && GlobalConstant.RESP_CODE_SUCCESS.equals(payResponse.get("flag"))) {

											if (payResponse.get("type").equals("1")) {
												// 卡已开通，即将调用验证码页面
												returnJson.put("transactionId", payResponse.get("transactionId"));

												returnJson.put("payAmount", payResponse.get("payAmount"));
												returnJson.put("payCardId", payResponse.get("payCardId"));
												returnJson.put("orderId", payResponse.get("orderId"));

												returnJson.put("type", "1");
											} else if (payResponse.get("type").equals("2")) {
												// 卡未开通，调用html跳转至开通页面
												returnJson.put("content", payResponse.get("html"));
												// returnJson.put("content",
												// frontUrl);
												returnJson.put("type", "2");
											}
											returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
											returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));

										} else {
											/* 交易失败 */
											returnJson.put("respCode", payResponse.get("flag"));
											// returnJson.put("respDesc",
											// GlobalConstant.map.get(payResponse.get("flag")));
											returnJson.put("respDesc", payResponse.get("flagMSG"));
										}
									} else if ("YILIANJFZTC".equals(channelCode)) {
										// 走易联通道
										ChannelPayRef cpr = troughTrainServeice.getChannelPayRef(true, "550", null, accType, "YILIANYINLIANJIFENZTC");
										if (cpr == null) {
											/* 无支付通道 */
											returnJson.put("respCode", GlobalConstant.RESP_CODE_500);
											returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_500));
										} else {
											// 进入通道
											Map<String, String> payResponse = cpr.getChannelPaymentService().createUnipayOnlineThroughOrder(user, cpr, Integer.parseInt(accType), Long.parseLong(cardInfos.get("cardId")), frontUrl, money,
													Integer.parseInt(transPayType), agentType, null);
											if (payResponse.containsKey("flag") && GlobalConstant.RESP_CODE_SUCCESS.equals(payResponse.get("flag"))) {

												if (payResponse.get("type").equals("1")) {
													// 卡已开通，即将调用验证码页面
													returnJson.put("transactionId", payResponse.get("transactionId"));
													returnJson.put("type", "1");
												} else if (payResponse.get("type").equals("2")) {
													// 卡未开通，调用html跳转至易联开通页面
													returnJson.put("content", payResponse.get("html"));
													// returnJson.put("content",
													// frontUrl);
													returnJson.put("type", "2");
												}
												returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
												returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));

											} else {
												/* 交易失败 */
												returnJson.put("respCode", payResponse.get("flag"));
												// returnJson.put("respDesc",
												// GlobalConstant.map.get(payResponse.get("flag")));
												returnJson.put("respDesc", payResponse.get("flagMSG"));
											}
										}
									} else if ("ZHEYANGJFZTC".equals(channelCode)) {
										// 哲扬积分联通道
										ChannelPayRef cpr = troughTrainServeice.getChannelPayRef(true, "552", null, accType, "ZHEYANGJFZTC");

										if (cpr == null) {//
											/* 无支付通道 */
											returnJson.put("respCode", GlobalConstant.RESP_CODE_500);
											returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_500));
										} else {
											// 进入通道
											returnJson.put("type", "2");
											Map<String, String> payResponse = cpr.getChannelPaymentService().createUnipayOnlineThroughOrder(user, cpr, Integer.parseInt(accType), Long.parseLong(cardInfos.get("cardId")), frontUrl, money,
													Integer.parseInt(transPayType), agentType, null);
											if (payResponse == null) {
												returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
												returnJson.put("respDesc", "交易通道错误");
											} else {
												if (payResponse.containsKey("flag") && GlobalConstant.RESP_CODE_SUCCESS.equals(payResponse.get("flag"))) {
													returnJson.put("content", payResponse.get("html"));
													returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
													returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
												} else {
													/* 交易失败 */
													returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
													returnJson.put("respDesc", payResponse.get("flagMSG"));
												}
											}
										}
									} else if ("WEILIANBAOJFZTC".equals(channelCode)) {
										// TODO we聯保积分联通道
										ChannelPayRef cpr = troughTrainServeice.getChannelPayRef(true, "550", null, accType, "WEILIANBAOJFZTC");
										if (cpr == null) {
											/* 无支付通道 */
											returnJson.put("respCode", GlobalConstant.RESP_CODE_500);
											returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_500));
										} else {
											// 进入通道
											// returnJson.put("type", "1");
											String orderNo = json.getString("orderNo");// 开卡二次进入传该值
											Map<String, String> payResponse = cpr.getChannelPaymentService().createUnipayOnlineThroughOrder(user, cpr, Integer.parseInt(accType), Long.parseLong(cardInfos.get("cardId")), frontUrl, money,
													Integer.parseInt(transPayType), agentType, orderNo);
											if (payResponse == null) {
												returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
												returnJson.put("respDesc", "交易通道错误");
											} else {
												if (payResponse.containsKey("flag") && GlobalConstant.RESP_CODE_SUCCESS.equals(payResponse.get("flag"))) {
													returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
													if (payResponse.get("type").equals("1")) {
														// 卡已开通，即将调用验证码页面
														returnJson.put("transactionId", payResponse.get("transactionId"));
														returnJson.put("payAmount", payResponse.get("payAmount"));
														returnJson.put("payCardId", payResponse.get("payCardId"));
														returnJson.put("orderId", payResponse.get("orderId"));
														returnJson.put("type", "1");
													} else {
														// 卡未开通，调用html跳转至开通页面
														returnJson.put("content", payResponse.get("html"));
														returnJson.put("type", payResponse.get("type"));
														returnJson.put("orderNo", payResponse.get("orderNo"));
														returnJson.put("merchantNo", payResponse.get("merchantNo"));
														returnJson.put("sign", payResponse.get("sign"));
														returnJson.put("retMsg", payResponse.get("retMsg"));
														returnJson.put("trxType", payResponse.get("trxType"));
													}
												} else {
													/* 交易失败 */
													returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
													returnJson.put("respDesc", payResponse.get("flagMSG"));
												}
											}
										}
									} else if ("YIQIANG2JFZTC".equals(channelCode)) {
										// TODO 亿强2积分联通道
										ChannelPayRef cpr = troughTrainServeice.getChannelPayRef(true, "550", null, accType, "YIQIANG2JFZTC");
										if (cpr == null) {
											/* 无支付通道 */
											returnJson.put("respCode", GlobalConstant.RESP_CODE_500);
											returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_500));
										} else {
											// 进入通道
											// returnJson.put("type", "1");
											Map<String, String> payResponse = cpr.getChannelPaymentService().createUnipayOnlineThroughOrder(user, cpr, Integer.parseInt(accType), Long.parseLong(cardInfos.get("cardId")), frontUrl, money,
													Integer.parseInt(transPayType), agentType, "");
											if (payResponse == null) {
												returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
												returnJson.put("respDesc", "交易通道错误");
											} else {
												if (payResponse.containsKey("flag") && GlobalConstant.RESP_CODE_SUCCESS.equals(payResponse.get("flag"))) {
													returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
													returnJson.put("transactionId", payResponse.get("transactionId"));
													returnJson.put("payAmount", payResponse.get("payAmount"));
													returnJson.put("payCardId", payResponse.get("payCardId"));
													returnJson.put("orderId", payResponse.get("orderId"));
													returnJson.put("type", "1");
												} else {
													/* 交易失败 */
													returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
													returnJson.put("respDesc", payResponse.get("flagMSG"));
												}
											}
										}
									} else {
										returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
										returnJson.put("respDesc", "通道关闭,请稍后再试");
									}
								} else {
									returnJson.put("respCode", GlobalConstant.RESP_CODE_047);
									returnJson.put("respDesc", "银联在线单笔交易不得超过" + yilian_jifen_max_amt_per_time + "元");
								}
							} else {
								/* 交易失败 */
								returnJson.put("respCode", flags.get("flag"));
								returnJson.put("respDesc", GlobalConstant.map.get(flags.get("flag")));
								if (flags.containsKey("errorCode")) {
									returnJson.put("respCode", flags.get("errorCode"));
									returnJson.put("respDesc", GlobalConstant.map.get(flags.get("errorInfo")));
								}
							}
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_066);
							returnJson.put("respDesc", stmFlag);
						}
					} else {
						/* 交易失败 */
						returnJson.put("respCode", flag);
						returnJson.put("respDesc", GlobalConstant.map.get(flag));
					}

				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----银联在线积分消费订单接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.19 银联在线小额直通车消费订单接口
	 * 
	 * @category 银联在线
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createOnlineThroughBankPayZT", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject createOnlineThroughBankPayZT(HttpServletRequest request) {

		logger.info("----银联在线直通车消费订单接口 begin-----");//
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("createOnlineThroughBankPayZT json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID

			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String transAmt = json.getString("transAmt");// 账户类型
				String frontUrl = json.getString("frontUrl");// 前端返回地址
				String cardId = json.getString("cardId");// 交易卡ID
				String transPayType = json.getString("transPayType");// 交易卡号
				String channelCode = json.getString("channelCode");// 通道码

				/*
				 * 0： 即时到账 5：T+5到账
				 */
				String accType = json.getString("accType");// 支付用途
				if (StringUtil.isNullOrEmpty(accType)) {
					accType = "0";
					logger.info("没有传入accType参数");
				}
				if (StringUtil.isNotEmpty(frontUrl) && StringUtil.isNotEmpty(transAmt) && StringUtil.isNotEmpty(transPayType) && (StringUtil.isNotEmpty(cardId))) {
					UserCard cardInfo = cardService.get(Long.valueOf(cardId));

					String needCheck = paramService.searchSysParameter().get("ylzx_cvv_exptime_validate");
					logger.info("CVV校验开关ZT={}", needCheck);
					if ("1".equals(needCheck)) {
						// 判断信用卡的CVV安全码
						if (cardInfo.getCardType().equals("X") && StringUtil.isBlank(cardInfo.getCvv())) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_083);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_083));
							return commonService.getEncryptBody(returnJson);
						}
						// 判断信用卡的有效期
						if (cardInfo.getCardType().equals("X") && StringUtil.isBlank(cardInfo.getValidityDate())) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_084);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_084));
							return commonService.getEncryptBody(returnJson);
						}
					}
					User user = userService.get(Long.parseLong(merId));

					// 判断用户是否可以支付
					String flag = userService.isAllowUserPay(user.getId(), UserOrder.trans_type.YLZX.name());
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
						// 用户是否在操作时间段
						String stmFlag = holidayService.isLimitTimeInterval(new Date(), true, null);
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {

							Integer transPayTypeInt = Integer.parseInt(transPayType);
							Integer agentType = 0;
							// 判断是普通订单还是升级订单
							if (transPayTypeInt == 20) {
								agentType = json.getInteger("agentType");// 支付类型
								flag = orderService.isAllowPayAgent(user, agentType);
								if (!GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
									/* 购买代理过于频繁 */
									returnJson.put("respCode", flag);
									returnJson.put("respDesc", GlobalConstant.map.get(flag));
									return commonService.getEncryptBody(returnJson);
								}
							}
							// 解密CVV和卡号
							if (StringUtil.isNotBlank(json.getString("cvv"))) {
								String realCvv = ImportUtil.getDecCvv(json.getString("cvv"));
								json.put("cvv", realCvv);
							}
							if (StringUtil.isNotBlank(json.getString("cardNo"))) {
								String realCardNo = ImportUtil.getDecCardNo(json.getString("cardNo"));
								json.put("cardNo", realCardNo);
							}
							json.put("agentId", agentId);
							Map<String, String> cardInfos = json.toJavaObject(json, Map.class);
							// 校验银行卡信息
							Map<String, String> flags = cardService.sendCardInfoToBankValidateTwo(cardInfos, false, false, agentId);
							if (GlobalConstant.RESP_CODE_SUCCESS.equals(flags.get("flag"))) {
								if (StringUtil.isNotBlank(json.getString("cardNo"))) {
									cardInfos.put("cardId", cardService.getfindCode(merId, cardInfos.get("cardNo")));
								}
								Double money = Double.valueOf(transAmt);
								// 判断银联在线单笔最大金额
								String ylzx_max_amt_per_time = paramService.searchSysParameter().get("ylzx_max_amt_per_time");
								if (money - Double.parseDouble(ylzx_max_amt_per_time) <= 0) {
									// 查询可用通道
									ChannelPayRef cpr = null;
									if ("0".equals(accType)) {
										if (StringUtil.isNotEmpty(channelCode)) {
											cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.YLZXE.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2, channelCode);
										} else {
											cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.YLZXE.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
										}
									} else {
										cpr = routeService.getYLZXET1ChannelPayRoute(UserOrder.trans_type.YLZXE.getCode(), BigDecimal.valueOf(money), user.getUserType(), user.getId(), Integer.parseInt(accType) < 10 ? 1 : 2);
									}

									if (cpr == null) {
										/* 无支付通道 */
										returnJson.put("respCode", GlobalConstant.RESP_CODE_500);
										returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_500));
									} else {
										if (sysParamService.getSwitch("payment_YLZX_seift_on")) {
											// 银联申孚直通车需要先报备商户
											returnJson.put("type", "1");
											// Map<String, String> payResponse =
											// cpr.getChannelPaymentService()
											// .createYLZXOrder_v2(user, cpr,
											// Integer.parseInt(accType),
											// Long.parseLong(cardInfos.get("cardId")),
											// frontUrl, money,
											// Integer.parseInt(transPayType),
											// agentType, null);
											// 新直通车
											logger.info("start to shenfu 1");
											Map<String, String> payResponse = cpr.getChannelPaymentService().createUnipayOnlineThroughOrder(user, cpr, Integer.parseInt(accType), Long.parseLong(cardInfos.get("cardId")), frontUrl, money,
													Integer.parseInt(transPayType), agentType, null);

											if (payResponse.containsKey("flag") && GlobalConstant.RESP_CODE_SUCCESS.equals(payResponse.get("flag"))) {
												returnJson.put("transactionId", payResponse.get("transactionId"));
												returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
												returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
											} else {
												/* 交易失败 */
												returnJson.put("respCode", payResponse.get("flag"));
												returnJson.put("respDesc", payResponse.get("flagMSG"));
											}
										} else {
											returnJson.put("type", "2");
											Map<String, String> payResponse = cpr.getChannelPaymentService().createUnipayOnlineThroughOrder(user, cpr, Integer.parseInt(accType), Long.parseLong(cardInfos.get("cardId")), frontUrl, money,
													Integer.parseInt(transPayType), agentType, null);
											if (payResponse == null) {
												returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
												returnJson.put("respDesc", "交易通道错误");
											} else {
												if (payResponse.containsKey("flag") && GlobalConstant.RESP_CODE_SUCCESS.equals(payResponse.get("flag"))) {
													returnJson.put("content", payResponse.get("html"));
													returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
													returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
												} else {
													/* 交易失败 */
													returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
													returnJson.put("respDesc", payResponse.get("flagMSG"));
												}
											}

										}
									}
								} else {

									returnJson.put("respCode", GlobalConstant.RESP_CODE_047);
									returnJson.put("respDesc", "银联在线单笔交易不得超过" + ylzx_max_amt_per_time + "元");
								}
							} else {
								/* 交易失败 */
								returnJson.put("respCode", flags.get("flag"));
								returnJson.put("respDesc", GlobalConstant.map.get(flags.get("flag")));
								if (flags.containsKey("errorCode")) {
									returnJson.put("respCode", flags.get("errorCode"));
									returnJson.put("respDesc", GlobalConstant.map.get(flags.get("errorInfo")));
								}
							}
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_066);
							returnJson.put("respDesc", stmFlag);
						}
					} else {
						/* 交易失败 */
						returnJson.put("respCode", flag);
						returnJson.put("respDesc", GlobalConstant.map.get(flag));
					}

				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}

			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			logger.info("error={}", e);
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----银联在线直通车消费订单接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 银联在线小额直通车消费订单接口 ---易联快捷支付--短信验证码确认请求 type=1
	 * 
	 * @category 银联在线
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createOnlineVerify", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject createOnlineVerify(HttpServletRequest request) {

		logger.info("----银联在线直通车消费订单接口----传递手机验证码-- begin-----");//
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			logger.info("createOnlineVerify json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String authCode = json.getString("authCode"); // 验证码
				String transactionId = json.getString("transactionId");
				if (StringUtil.isNotBlank(authCode) && StringUtil.isNotBlank(transactionId)) {

					// 要添加判断是易联还是嘎吱银联积分
					if (transactionId.substring(0, 2).equals("GZ")) {
						// 嘎吱（银联） 通道 开始调用嘎吱消费支付接口
						String payAmount = json.getString("payAmount"); // 支付金额
																		// 以分为单位
						String payCardId = json.getString("payCardId"); // 支付卡的ID
						String orderId = json.getString("orderId"); // 调用发送验证码接口时，生成的订单号，在调用发送支付短信接口时，提前创建了本地订单
						if (StringUtil.isBlank(payAmount) || StringUtil.isBlank(payCardId) || StringUtil.isBlank(orderId)) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
							return commonService.getEncryptBody(returnJson);
						}

						// 查询用户对应的嘎吱（银联）通道信息
						ChannelPayRef cpr = troughTrainServeice.getChannelPayRef(false, "550", Long.parseLong(merId), "", "GAZHIYINLIANJIFENZHITONGCHE");
						if (cpr == null) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
							returnJson.put("respDesc", "无可用支付通道");
							return commonService.getEncryptBody(returnJson);
						}

						JSONObject channelConfig = cpr.getConfig();
						UserCard card = userCardService.get(Long.valueOf(payCardId));
						User user = userService.get(Long.valueOf(merId));

						// 调用消费支付接口
						OrderPayForGaZhiYinLian req = new OrderPayForGaZhiYinLian();
						req.setTranType("CONPAY");
						req.setMerNo(channelConfig.getString("gazhiyinlian.merNo"));
						req.setMerTrace("GZYL" + UUID.randomUUID().toString().substring(0, 8));// 商户流水

						req.setOrderId(orderId);
						req.setPayNo(transactionId.substring(2, transactionId.length())); // 支付流水号是支付短信返回的
						req.setPayAmount(payAmount);
						req.setRateCode(GaZhiYinLianUtil.rateCode); // 商户费率编号
						req.setCardNo(card.getCardNo()); // 支付卡号
						req.setAccountName(user.getRealName());
						req.setCardType("2"); // 银行卡类型 1-借记卡2-信用卡
						req.setBankCode("111");
						req.setBankAbbr("111");
						// 判断选择的支付卡的卡类型
						if (card.getCardType().equals("J")) {
							req.setCardType("1"); // 1借记卡 2信用卡
						} else {
							req.setCardType("2"); // 1借记卡 2信用卡
						}
						// 处理支付银行卡信息
						Bank bank = null;
						String bankName = "";
						TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(card.getCardNo()));
						if (cbc == null) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_053);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_053));
							return returnJson;
						} else {
							bank = bankService.getBankByBankCode(cbc.getBankCode());
							if (bank == null) {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_054);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_054));
								return returnJson;
							} else {
								bankName = bank.getBankName();
							}
						}
						String bankIco = bank.getBankIco();
						int location = bankIco.indexOf("_");
						String bankAddr = bankIco.substring(location + 1, bankIco.length()).toUpperCase(); // 银行代码大写

						// 判断卡是否在嘎吱支持的卡列表当中
						if (StringUtil.isBlank(GaZhiYinLianUtil.map.get(bankAddr))) {
							if (StringUtil.isBlank(GaZhiYinLianUtil.map2.get(bankName))) {
								// 因为通过银行编码进行匹配可能匹配不上，所以再次根据卡的名称是否有对应
								returnJson.put("respCode", GlobalConstant.RESP_CODE_054);
								returnJson.put("respDesc", "通道暂不支持该支付卡");
								return returnJson;
							} else {
								req.setBankCode(GaZhiYinLianUtil.map.get(GaZhiYinLianUtil.map2.get(bankName)));// 银行代码
																												// 数字
								req.setBankAbbr(GaZhiYinLianUtil.map2.get(bankName)); // 银行代号
							}

						} else {
							req.setBankCode(GaZhiYinLianUtil.map.get(bankAddr));// 银行代码
							req.setBankAbbr(bankAddr); // 银行代号
						}

						req.setPhoneno(card.getPhone());
						req.setCertType("01");
						req.setCertNo(user.getIdNo()); // 银行预留证件号
						req.setSmsCode(authCode);
						req.setProductName("线上支付");
						req.setProductDesc("线上支付");
						req.setNotifyUrl(GaZhiYinLianUtil.notifyUrl); // 后台通知url
						JSONObject res = gaZhiYinLainService.orderPay(req);
						if (!res.getString("code").equals("000")) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
							returnJson.put("respDesc", "请求通道下单失败");
							return commonService.getEncryptBody(returnJson);
						}
						JSONObject resJson = JSONObject.parseObject(res.getString("message"));
						logger.info("resJson={}", resJson);
						// 检查返回的orderId是否和请求的一致
						returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
						return commonService.getEncryptBody(returnJson);

					} else if (transactionId.substring(0, 3).equals("WLB")) {
						// 微联宝
						String payAmount = json.getString("payAmount"); // 支付金额
						// 以分为单位
						String payCardId = json.getString("payCardId"); // 支付卡的ID
						String orderId = json.getString("orderId"); // 调用发送验证码接口时，生成的订单号，在调用发送支付短信接口时，提前创建了本地订单
						// String authCode = json.getString("authCode"); // 验证码
						// String transactionId =
						// json.getString("transactionId");
						if (StringUtil.isBlank(payAmount) || StringUtil.isBlank(payCardId) || StringUtil.isBlank(orderId)) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
							return commonService.getEncryptBody(returnJson);
						}

						ChannelPayRef cpr = troughTrainServeice.getChannelPayRef(true, "550", null, null, "WEILIANBAOJFZTC");
						if (cpr == null) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
							returnJson.put("respDesc", "无可用支付通道");
							return commonService.getEncryptBody(returnJson);
						}

						JSONObject channelConfig = cpr.getConfig();
						UserCard card = userCardService.get(Long.valueOf(payCardId));
						User user = userService.get(Long.valueOf(merId));
						// String authCode = json.getString("authCode"); // 验证码
						// String transactionId =
						// json.getString("transactionId");
						OrderPayForWeiLianBaoYinLian reqPar = new OrderPayForWeiLianBaoYinLian();
						reqPar.setTrxType("CONSUME");
						// reqPar.setMerchantNo("");
						// reqPar.setToken("");
						reqPar.setGoodsName("云商物品");
						// reqPar.setServerDfUrl("");
						// reqPar.setServerCallbackUrl("");
						reqPar.setOrderNum(transactionId);
						reqPar.setTrxTime(DateUtil.convertCurrentDateTimeToString());
						reqPar.setSmsCode(authCode);
						// reqPar.setSign("");
						JSONObject retJson = weiLianBaoYinLainService.consume(reqPar, card.getCardNo());
						if (retJson == null || !"0000".equals(retJson.getString("retCode"))) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
							returnJson.put("respDesc", retJson.getString("retMsg"));
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
							returnJson.put("respDesc", retJson.getString("retMsg"));
						}
						return commonService.getEncryptBody(returnJson);
					} else if (transactionId.substring(0, 8).equals(YiQiang2Config.ORG_CODE_TEST)) {
						//TODO 亿强积分
						String payAmount = json.getString("payAmount"); // 支付金额
						// 以分为单位
						String payCardId = json.getString("payCardId"); // 支付卡的ID
						String orderId = json.getString("orderId"); // 调用发送验证码接口时，生成的订单号，在调用发送支付短信接口时，提前创建了本地订单
						// String authCode = json.getString("authCode"); // 验证码
						// String transactionId =
						// json.getString("transactionId");
						if (StringUtil.isBlank(payAmount) || StringUtil.isBlank(payCardId) || StringUtil.isBlank(orderId)) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
							return commonService.getEncryptBody(returnJson);
						}

						ChannelPayRef cpr = troughTrainServeice.getChannelPayRef(true, "550", null, null, "YIQIANG2JFZTC");
						if (cpr == null) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
							returnJson.put("respDesc", "无可用支付通道");
							return commonService.getEncryptBody(returnJson);
						}

						JSONObject channelConfig = cpr.getConfig();
						UserCard card = userCardService.get(Long.valueOf(payCardId));
						User user = userService.get(Long.valueOf(merId));
						// String authCode = json.getString("authCode"); // 验证码
						// String transactionId =
						// json.getString("transactionId");
						OrderPayForWeiLianBaoYinLian reqPar = new OrderPayForWeiLianBaoYinLian();
//						交易码  trnCode_01  M  固定值：QKODRPAY 
//						产品类型  pdtCode_02  M   
//						交易日期  trnDate_03  M   
//						交易时间  trnTime_04  M   
//						商户流水  merTrce_05  M   
//						合作商户号  comMrno_07  M   
//						订单时间  odrTime_08  M  支付短信订单时间 
//						商户订单号  odrIdno_09  M  支付短信订单号 
//						订单币种  ccyCode_12  M  默认为 156 
//						订单金额  odrAmt_10  M  单位(分) 
//						真实姓名  relName_24  M   
//						证件类型  crtType_28  M  默认：01，参照 7.5 证件类型 
//						证件号码  crtIdno_29  M   
//						总行名称  bnkName_34     
//						总行联行号  bnkCode_35  M   
//						卡号  accCard_13  M  消费账户 
//						户名  accName_32  M  消费账户名称 
//						账号类型  accType_33  M     
//						手机号码  mobPhoe_30  M  银行预留手机号码 
//						卡密码  pasWord_14  C   
//						CVN2  accCvn2_16  C  账号类型为贷记卡，必填 
//						卡有效期  valiDate_15  C  账号类型为贷记卡，必填 
//						短信验证码  smsCode_17  M  短信验证码 
//						短信流水号  smsSeqn_47  M  支付短信流水号 
//						通知地址 url  backUrl_48  M   
						Map<String, String> params = new HashMap<String, String>();
						// 新增参数
						params.put("trnCode_01", YiQiang2Config.ORDER_PAY);
						params.put("pdtCode_02", "B1");
						params.put("trnDate_03", DateUtil.getyyyyMMddToString());
						params.put("trnTime_04", DateUtil.getHHMMSSToString());
						String preMerTrce = YiQiang2Config.ORG_CODE_TEST + DateUtil.getyyyyMMddToString();
						Long sufLen = (long) (24 - preMerTrce.length());
						String randomStr = String.valueOf(System.currentTimeMillis());
						String sufMerTrce =  randomStr.substring((int) (randomStr.length()-sufLen));
						params.put("merTrce_05", preMerTrce + sufMerTrce);//
						TYiQiang2MerchantReport merchantReport=yiQiangPointService.findByUserId(Long.valueOf(merId));
						params.put("comMrno_07", merchantReport.getComMrno_07());
						
						String[] info=  orderId.split("_");
						
//						params.put("odrTime_08", DateUtil.convertCurrentDateTimeToString());
						params.put("odrTime_08", info[0]);
						params.put("odrIdno_09", transactionId);
						params.put("ccyCode_12", "156");
						params.put("odrAmt_10", "10001");//分
						params.put("relName_24", "张三");
//						params.put("relName_24", merchantReport.getRelName_24());
						params.put("crtType_28", "01");
//						params.put("crtIdno_29", user.getIdNo());
						params.put("crtIdno_29", "510265790128303");
//						params.put("bnkName_34", merchantReport.getBnk());
//						params.put("bnkCode_35", merchantReport.getBnk());
						params.put("bnkName_34", "华夏银行");
						params.put("bnkCode_35", "304100040000");
						params.put("accCard_13", "6226388000000095");
						params.put("accName_32", "张三");
//						params.put("accName_32", merchantReport.getRelName_24());
						params.put("accType_33", "A");
						params.put("mobPhoe_30", "18100000000");
//						params.put("pasWord_14", "01");
						params.put("accCvn2_16", "248");
						params.put("valiDate_15", "1912");
						params.put("smsCode_17", authCode);
						params.put("smsSeqn_47", info[1]); 
						params.put("backUrl_48", "http://liguangchun211.51vip.biz:30483/flypayfx/payment/yiQiangPointPayNotify");
						
//						trnCode_01  R   
//						返回码  respCode_19  M   
//						返回消息  rspMsg_20  M   
//						合作商户号  comMrno_07  R   
//						商户流水号  merTrce_05  R   
//						订单时间  odrTime_08  R   
//						商户订单号  odrIdno_09  R   
//						系统流水号  trnSeqn_18  M  短信流水号 
//						以下信息在返回码为 00 时返回 
//						状态码  trnStus_21  C   
//						状态信息  trnMsg_22  C   
						logger.info("Yiqiang2Point pay req json={}", JSONObject.toJSON(params));
						String applyResult =  YiQiang2PayUtil.sendYiQiang2Pay(params);
						logger.info("Yiqiang2Point pay ret json={}", applyResult);
						Map<String, String> applyResultMap = JSONObject.parseObject(applyResult, Map.class);

						if (applyResultMap == null || !"00".equals(applyResultMap.get("respCode_19"))) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
							returnJson.put("respDesc", "银行网络故障,请联系客服");
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
							returnJson.put("respDesc", applyResultMap.get("rspMsg_20"));
						}
						return commonService.getEncryptBody(returnJson);
						
						
					} else {
						// 调用易联支付--快捷确认支付接口--传递短信验证码
						MerchantQuickPayConfirmReq confirmReq = new MerchantQuickPayConfirmReq();
						// 原商户订单号
						confirmReq.setRefTxnId(transactionId);
						// 短信验证码
						confirmReq.setVerificationCode(authCode);
						// 商户订单号
						confirmReq.setTransactionId("YF" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
						JSONObject result = YiLianYlzxUtil.send(confirmReq, ValueConstant.TRANS_CODE_T01016, YiLianYlzxUtil.merachnetId);
						if (result != null) {
							if (result.containsKey("retCode") && result.getString("retCode").equals("RC0000")) {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
							} else {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_051);
								returnJson.put("respDesc", result.getString("retRemark"));
							}
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_051);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_051));
						}
					}
				} else {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----银联在线直通车消费订单接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.20
	 * 
	 * @category 暂不启用
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/verifyOnlineBankPay", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject verifyOnlineBankPay(HttpServletRequest request) {

		logger.info("----查询银联在线订单接口 -----");//
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			logger.info("verifyOnlineBankPay json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String orderNum = json.getString("orderNum");
				String checkCode = json.getString("checkCode");
				if (StringUtil.isNotEmpty(orderNum) && StringUtil.isNotEmpty(checkCode)) {
					// String sendReturnCode =
					// paymentService.sendChecCodeToOnline(orderNum, checkCode);
					// if (StringUtil.isNotBlank(sendReturnCode) &&
					// "SUCCESS".equals(sendReturnCode)) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					// } else {
					// /* 交易失败 */
					// returnJson.put("respCode", sendReturnCode);
					// returnJson.put("respDesc",
					// GlobalConstant.map.get(sendReturnCode));
					// }
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}

			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.20 提现接口
	 * 
	 * @category 提现
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doLiq", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject doLiq(HttpServletRequest request) {
		logger.info("---- 提现接口接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("doLiq json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				String transType = json.getString("transType");
				String transAmt = json.getString("transAmt");
				String transPwd = json.getString("transPwd");

				String account_draw_min = paramService.getByName("account_draw_min").getParaValue();
				String account_draw_max = paramService.getByName("account_draw_max").getParaValue();
				BigDecimal account_draw_min_amt = new BigDecimal(account_draw_min);
				BigDecimal account_draw_max_amt = new BigDecimal(account_draw_max);
				BigDecimal transAmtDec = new BigDecimal(transAmt);

				if (account_draw_min_amt.compareTo(transAmtDec) > 0 || account_draw_max_amt.compareTo(transAmtDec) < 0) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "提现金额超限,请联系客服");
					return commonService.getEncryptBody(returnJson);
				}

				// 厦商总分润账号，级别最高的那个账号，屏蔽提现功能
				if (merId.equals("102204")) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "此账号提现功能暂时关闭");
					return commonService.getEncryptBody(returnJson);
				}

				if (!"12".equals(merId) && paramService.getTiXian()) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "亲爱的小主，银行付款系统维护中");
					return commonService.getEncryptBody(returnJson);
				}

				// 提现为次日到账
				// if (!"T0".equals(transType.trim())) {
				// returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				// returnJson.put("respDesc",
				// "亲爱的小主，您已经好久没有更换新装啦！快去给自己升级换身装备吧！");
				// return commonService.getEncryptBody(returnJson);
				// }

				if (StringUtil.isNotEmpty(transType) && StringUtil.isNotEmpty(transAmt) && StringUtil.isNotEmpty(transPwd)) {
					String stmFlag = userService.isTransAccount(Long.parseLong(merId), transPwd, Double.parseDouble(transAmt), false);
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {

						stmFlag = userService.isOverLimit(Long.parseLong(merId), transType, Double.parseDouble(transAmt));
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {
							stmFlag = holidayService.isLimitTimeInterval(new Date(), false, transType.trim());
							if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {
								String description = "用户申请提现" + transAmt + "元";
								/* 更新用户账户金额，创建用户订单 */
								Map<String, String> resMap = paymentService.updateAccountWhenLiqAccountMap(Long.parseLong(merId), transType.trim(), Double.parseDouble(transAmt), description);
								String flag = resMap.get("flag");
								String orderNum = resMap.get("orderNum");
								String channelName = resMap.get("channelName");
								logger.info("doLiq flag={},userId={},orderNum={},channelName={},transAmt={}", new Object[] { flag, merId, orderNum, channelName, transAmt });
								if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
									// 若为T0提现，订单创建完成后，系统主动发送给平安代付指令
									/*
									 * String tranOrderFlag = ""; String
									 * tranOrderMsg = ""; if
									 * ("T0".equals(transType.trim())) { if
									 * ("SHENFUDAIFU".equals(channelName)) {
									 * Map<String, String> params = new
									 * HashMap<String, String>();
									 * params.put("userId", merId);
									 * params.put("orderNum", orderNum);
									 * Map<String, String> map = routeService.
									 * getChannelPayRouteByChannelName(
									 * "SHENFUDAIFU").sendDaiFuReq(params); //
									 * Map<String, String> map = // routeService
									 * // .getChannelPayRouteByChannelName(
									 * channelName).sendDaiFuReq(params);
									 * tranOrderFlag = map.get("flag");
									 * tranOrderMsg = map.get("errMsg"); } else
									 * if ("TRANSFAR".equals(channelName)) {
									 * Map<String, String> params = new
									 * HashMap<String, String>();
									 * params.put("userId", merId);
									 * params.put("orderNum", orderNum);
									 * Map<String, String> map = routeService.
									 * getChannelPayRouteByChannelName(
									 * "TRANSFAR").sendDaiFuReq(params);
									 * tranOrderFlag = map.get("flag");
									 * tranOrderMsg = map.get("errMsg"); } else
									 * { Map<String, String> map =
									 * pingAnExpenseService.sendOrderToPingAN(
									 * orderNum); tranOrderFlag =
									 * map.get("flag"); tranOrderMsg =
									 * GlobalConstant.map.get(tranOrderFlag); }
									 * } returnJson.put("respCode",
									 * tranOrderFlag);
									 * returnJson.put("respDesc", tranOrderMsg);
									 */
									returnJson.put("respCode", flag);
									returnJson.put("respDesc", GlobalConstant.map.get(flag));
								}
							} else {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_066);
								returnJson.put("respDesc", stmFlag);
							}
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
							returnJson.put("respDesc", GlobalConstant.map.get(stmFlag));
							if (!GlobalConstant.RESP_CODE_028.equals(stmFlag)) {
								returnJson.put("respDesc", stmFlag);
							}
						}
					} else {
						/* 参数失败 */
						returnJson.put("respCode", stmFlag);
						returnJson.put("respDesc", GlobalConstant.map.get(stmFlag));
					}
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 提现接口接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.21 交易订单列表查询接口
	 * 
	 * @category 订单明细
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryOrderList", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryOrderList(HttpServletRequest request) {
		logger.info("---- 交易订单列表查询接口 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryOrderList json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				String transStat = json.getString("transStat");
				String orderType = json.getString("orderType");
				String pageNum = json.getString("pageNum");
				String pageSize = json.getString("pageSize");

				String startDateStr = json.getString("startDate");
				String endDateStr = json.getString("endDate");
				Account acc = accountService.getAccountByUserId(Long.parseLong(merId));
				returnJson.put("throughAmt", acc.getThroughAmt());

				if (StringUtil.isNotEmpty(pageNum) && StringUtil.isNotEmpty(pageSize)) {
					PageFilter pf = new PageFilter();
					pf.setPage(Integer.parseInt(pageNum));
					pf.setRows(Integer.parseInt(pageSize));
					pf.setSort("createTime");
					pf.setOrder(" desc");
					UserOrder order = new UserOrder();
					order.setUserId(Long.parseLong(merId));
					order.setFrontStatus(transStat);
					if (StringUtil.isNotBlank(orderType)) {
						String[] orderTypes = orderType.split("\\|");
						for (String ot : orderTypes) {
							int type = Integer.parseInt(ot);
							order.setMulType(type);
						}
					} else {
						order.setMulType(UserOrder.getCollectOrderTypes());
					}
					Date startDate = new Date();
					if (StringUtil.isNotBlank(startDateStr) && startDateStr.length() == 12) {
						startDate = DateUtil.convertStringToDate("yyyyMMddHHmm", startDateStr);
					} else {
						startDate = null;
					}
					Date endDate = new Date();
					if (StringUtil.isNotBlank(endDateStr) && endDateStr.length() == 12) {
						endDate = DateUtil.convertStringToDate("yyyyMMddHHmm", endDateStr);
					} else {
						endDate = null;
					}
					int maxDays = Integer.parseInt(paramService.searchSysParameter().get("app_search_order_max_days"));
					// if (DateUtil.getBetweenDays(startDate, endDate) <=
					// maxDays) {

					order.setCreateDatetimeStart(startDate);
					order.setCreateDatetimeEnd(endDate);
					List<UserOrder> orders = orderService.dataGrid(order, pf);
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					returnJson.put("currentDate", DateUtil.convertCurrentDateTimeToString());
					returnJson.put("totalNum", orderService.count(order, pf));
					returnJson.put("tradeAmt", orderService.getOrderOrgAmt(order, pf));// 交易总金额
					User user = userService.get(Long.parseLong(merId));
					returnJson.put("merCode", user.getCode());
					JSONArray ja = new JSONArray();
					for (UserOrder d : orders) {
						JSONObject job = new JSONObject();
						converOrderDetailToJson(job, d);
						ja.add(job);
					}
					returnJson.put("ordersInfo", ja);
					// } else {
					// returnJson.put("respCode", GlobalConstant.RESP_CODE_080);
					// returnJson.put("respDesc",
					// String.format(GlobalConstant.map.get(GlobalConstant.RESP_CODE_080),
					// maxDays));
					// }
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			logger.error("交易订单列表查询接口 error ", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}

		logger.info("---- 交易订单列表查询接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.22 商户返佣查询接口
	 * 
	 * @category 佣金账户基础信息
	 * @category 分享有礼基础查询
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/searchRebateMerInfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject searchRebateMerInfo(HttpServletRequest request) {
		logger.info("---- 商户二维码推广接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("searchRebateMerInfo json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token)) {
				if (userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
					Map<String, String> map = brokerageService.getRebateInfoByUserId(Long.parseLong(merId));

					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					returnJson.put("merType", map.get("merType"));
					returnJson.put("avlBrokerage", map.get("avlBrokerage"));
					returnJson.put("totalBrokerage", map.get("totalBrokerage"));
					returnJson.put("totalPersonNum", map.get("totalPersonNum"));
					returnJson.put("diamondFee", map.get("diamondFee"));
					returnJson.put("goldFee", map.get("goldFee"));
					String url = "https://bbpurse.com/flypayfx/popularize/registpage?pcode=";
					// String url =
					// "http://127.0.0.1:8080/flypayfx/popularize/registpage?pcode=";
					// String url =
					// "http://18b2z84712.51mypc.cn:23746/flypayfx/popularize/registpage?pcode=";
					url = url + map.get("tgCodeNo") + StringUtils.leftPad(String.valueOf(RandomUtils.nextInt(99999)), 5, "0");
					// String prefix = "https://";
					// if (!"bbpurse.com".equals(request.getServerName())) {
					// prefix = "http://";
					// }
					// String url = prefix + request.getServerName() + path;
					returnJson.put("tgCodeNo", url);
					returnJson.put("merCode", map.get("merCode"));
					returnJson.put("merName", map.get("merName"));
					returnJson.put("todayBrokerage", map.get("todayBrokerage"));
					returnJson.put("yesterdayBrokerage", map.get("yesterdayBrokerage"));
				} else {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("---- 商户返佣基础查询接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.23 商户返佣明细列表查询接口
	 * 
	 * @category 查询返佣明细
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryRebateList", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryRebateList(HttpServletRequest request) {
		logger.info("---- 商户返佣明细列表查询接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryRebateList json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				String type = json.getString("type");

				String pageNum = json.getString("pageNum");
				String pageSize = json.getString("pageSize");

				if (StringUtil.isNotEmpty(type) && StringUtil.isNotEmpty(pageNum) && StringUtil.isNotEmpty(pageSize)) {
					PageFilter pf = new PageFilter();
					pf.setPage(Integer.parseInt(pageNum));
					pf.setRows(Integer.parseInt(pageSize));
					pf.setOrder("desc");
					pf.setSort("transDatetime");
					BrokerageDetail bk = new BrokerageDetail();
					bk.setBrokerageUserId(Long.parseLong(merId));
					bk.setSearchType(type);
					List<BrokerageDetail> bds = brokerageDetailService.dataGrid(bk, pf);

					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					returnJson.put("currentDate", DateUtil.convertCurrentDateTimeToString());

					/* 计算佣金今日和隶属的明细数目列表 */
					Long total = 0l;// 满足条件的佣金明细数量
					Long today = 0l;
					Long his = 0l;
					if ("T".equals(type)) {
						today = brokerageDetailService.count(bk, pf);
						total = today;
						bk.setSearchType("F");
						his = brokerageDetailService.count(bk, pf);
					} else if ("H".equals(type)) {
						his = brokerageDetailService.count(bk, pf);
						total = his;
						bk.setSearchType("T");
						today = brokerageDetailService.count(bk, pf);
					}

					returnJson.put("todayNum", today);
					returnJson.put("hisNum", his);
					returnJson.put("totalNum", total);
					JSONArray ja = new JSONArray();
					for (BrokerageDetail d : bds) {
						JSONObject job = new JSONObject();
						job.put("phone", d.getPhone());
						job.put("merType", d.getUserType());
						job.put("payType", d.getTransType());
						job.put("rebateLevel", d.getBrokerageUserType());
						job.put("rebateRate", d.getBrokerageUserRate() == null ? 0 : d.getBrokerageUserRate().toString());
						job.put("rebateAmt", d.getBrokerage().setScale(2, BigDecimal.ROUND_DOWN));
						job.put("rebateDate", DateUtil.getyyyyMMddHHmmssStringFromDate(d.getTransDatetime()));
						job.put("rebateDec", d.getDescription());

						ja.add(job);
					}
					returnJson.put("ordersInfo", ja);
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 商户返佣明细列表查询接口 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.24 商户返佣代理列表查询接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryAgentList", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryAgentList(HttpServletRequest request) {
		logger.info("---- 商户返佣推荐人明细列表查询接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryAgentList json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				String type = json.getString("type");

				String pageNum = json.getString("pageNum");
				String pageSize = json.getString("pageSize");

				if (StringUtil.isNotEmpty(type) && StringUtil.isNotEmpty(pageNum) && StringUtil.isNotEmpty(pageSize)) {
					PageFilter pf = new PageFilter();
					pf.setPage(Integer.parseInt(pageNum));
					pf.setRows(Integer.parseInt(pageSize));
					pf.setOrder("desc");
					pf.setSort("id");
					User u = new User();
					u.setIsChnl("A".equals(type.trim()) ? 1 : 0);
					u.setId(Long.parseLong(merId));
					List<Map<String, String>> bds = brokerageService.getAgentListByUserId(u, pf);

					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					returnJson.put("currentDate", DateUtil.convertCurrentDateTimeToString());

					/* 计算代理与普通用户数目列表 */
					Long total = 0l;// 满足条件的人数明细数量
					Long agentNum = brokerageService.getUserTotalNumsByUserId(Long.parseLong(merId), true);
					Long userNum = brokerageService.getUserTotalNumsByUserId(Long.parseLong(merId), false);
					if ("A".equals(type)) {
						total = agentNum;
					} else if ("U".equals(type)) {
						total = userNum;
					}
					returnJson.put("agentNum", agentNum);
					returnJson.put("userNum", userNum);
					returnJson.put("totalNum", total);
					JSONArray ja = new JSONArray();
					for (Map<String, String> d : bds) {
						JSONObject job = new JSONObject();
						job.putAll(d);
						ja.add(job);
					}
					returnJson.put("ordersInfo", ja);
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 商户返佣推荐人明细列表查询接口 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.25 商户返佣提现接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doRebateOut", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject doRebateOut(HttpServletRequest request) {
		logger.info("---- 商户返佣提现接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("doRebateOut json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID

			// 厦商总分润账号，级别最高的那个账号，屏蔽提现功能
			if (merId.equals("102204")) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", "此账号提现功能暂时关闭");
				return commonService.getEncryptBody(returnJson);
			}

			if (!"12".equals(merId) && paramService.getTiXian()) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", "亲爱的小主，银行付款系统维护中");
				return commonService.getEncryptBody(returnJson);
			}

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				String transAmt = json.getString("amt");
				String transPwd = json.getString("transPwd");

				String brokerage_draw_min = paramService.getByName("brokerage_draw_min").getParaValue();
				String brokerage_draw_max = paramService.getByName("brokerage_draw_max").getParaValue();
				BigDecimal brokerage_draw_min_amt = new BigDecimal(brokerage_draw_min);
				BigDecimal brokerage_draw_max_amt = new BigDecimal(brokerage_draw_max);
				BigDecimal transAmtDec = new BigDecimal(transAmt);

				if (brokerage_draw_min_amt.compareTo(transAmtDec) > 0 || brokerage_draw_max_amt.compareTo(transAmtDec) < 0) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "提现金额超限,请联系客服");
					return commonService.getEncryptBody(returnJson);
				}

				if (StringUtil.isNotEmpty(transAmt) && StringUtil.isNotEmpty(transPwd)) {
					String stmFlag = userService.isTransAccount(Long.parseLong(merId), transPwd, Double.parseDouble(transAmt), true);
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {

						stmFlag = userService.isOverLimit(Long.parseLong(merId), UserSettlementConfig.settlement_type.RABALE.name(), Double.parseDouble(transAmt));
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {
							/* 佣金体现默认是T0 交易 */
							stmFlag = holidayService.isLimitTimeInterval(new Date(), false, UserSettlementConfig.settlement_type.T0.name());
							if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {
								String trfTitle = "用户申请提取佣金" + transAmt + "元";
								String type = json.getString("type");

								Map<String, String> resMap = new HashMap<String, String>();
								if (type == null) {
									resMap = paymentService.updateBrokerageAccountWhenLiqBrokerageMap(Long.parseLong(merId), Double.parseDouble(transAmt), trfTitle, null);
								} else {
									resMap = paymentService.updateBrokerageAccountWhenLiqBrokerageMap(Long.parseLong(merId), Double.parseDouble(transAmt), trfTitle, type);
								}
								String flag = resMap.get("flag");
								String orderNum = resMap.get("orderNum");
								String channelName = resMap.get("channelName");
								logger.info("doRebateOut flag={},orderNum={},channelName={},transAmt={}", new Object[] { flag, orderNum, channelName, transAmt });

								// SEND PAY HTTP BEGIN
								if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {

									/* T0立即发起发起代付指令 */
									// String tranOrderFlag = "";
									// String tranOrderMsg = "";
									// if ("SHENFUDAIFU".equals(channelName)) {
									// Map<String, String> params = new
									// HashMap<String, String>();
									// params.put("userId", merId);
									// params.put("orderNum", orderNum);
									// Map<String, String> map =
									// routeService.getChannelPayRouteByChannelName("SHENFUDAIFU").sendDaiFuReq(params);
									// tranOrderFlag = map.get("flag");
									// tranOrderMsg = map.get("errMsg");
									// } else if
									// ("TRANSFAR".equals(channelName)) {
									// Map<String, String> params = new
									// HashMap<String, String>();
									// params.put("userId", merId);
									// params.put("orderNum", orderNum);
									// Map<String, String> map =
									// routeService.getChannelPayRouteByChannelName("TRANSFAR").sendDaiFuReq(params);
									// tranOrderFlag = map.get("flag");
									// tranOrderMsg = map.get("errMsg");
									// } else {
									// Map<String, String> map =
									// pingAnExpenseService.sendOrderToPingAN(orderNum);
									// tranOrderFlag = map.get("flag");
									// tranOrderMsg =
									// GlobalConstant.map.get(tranOrderFlag);
									// }
									// returnJson.put("respCode",
									// tranOrderFlag);
									// returnJson.put("respDesc", tranOrderMsg);

									returnJson.put("respCode", flag);
									returnJson.put("respDesc", GlobalConstant.map.get(flag));

								} else {
									returnJson.put("respCode", flag);
									returnJson.put("respDesc", GlobalConstant.map.get(flag));
								}
								// SEND PAY HTTP END
							} else {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_066);
								returnJson.put("respDesc", stmFlag);
							}
						} else {
							returnJson.put("respCode", stmFlag);
							returnJson.put("respDesc", GlobalConstant.map.get(stmFlag));
							if (!GlobalConstant.RESP_CODE_028.equals(stmFlag)) {
								returnJson.put("respDesc", stmFlag);
							}
						}
					} else {
						/* 参数失败 */
						returnJson.put("respCode", stmFlag);
						returnJson.put("respDesc", GlobalConstant.map.get(stmFlag));
					}
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 商户返佣提现接口 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.26 商户返佣提现列表查询接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryRebateOutList", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryRebateOutList(HttpServletRequest request) {
		logger.info("---- 商户返佣提现列表查询接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryRebateOutList json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String pageNum = json.getString("pageNum");
				String pageSize = json.getString("pageSize");

				if (StringUtil.isNotEmpty(pageNum) && StringUtil.isNotEmpty(pageSize)) {
					PageFilter pf = new PageFilter();
					pf.setPage(Integer.parseInt(pageNum));
					pf.setRows(Integer.parseInt(pageSize));
					// update：2017.11.16 根据提现订单时间降序排序
					pf.setSort("createTime");
					pf.setOrder("desc");
					UserOrder order = new UserOrder();
					order.setUserId(Long.parseLong(merId));
					order.setType(UserOrder.trans_type.YJTX.getCode());
					List<UserOrder> orders = orderService.dataGrid(order, pf);

					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					returnJson.put("currentDate", DateUtil.convertCurrentDateTimeToString());
					returnJson.put("totalNum", orderService.count(order, pf));
					JSONArray ja = new JSONArray();
					for (UserOrder d : orders) {
						JSONObject job = new JSONObject();
						job.put("orderId", d.getId());
						job.put("createDateTime", DateUtil.getyyyyMMddHHmmssStringFromDate(d.getCreateTime()));
						if (d.getPayOrder() != null && d.getPayOrder().getFinishDate() != null) {
							job.put("succDateTime", DateUtil.getyyyyMMddHHmmssStringFromDate(d.getPayOrder().getFinishDate()));
						}
						job.put("orderNum", d.getOrderNum());
						job.put("orderAmt", d.getOrgAmt());
						job.put("avlBal", d.getAvlAmt());
						String orderStatus = "I";
						if (d.getStatus() < 200) {
							orderStatus = "S";
						} else if (d.getStatus() >= 200 && d.getStatus() < 300) {
							orderStatus = "F";
						}
						job.put("orderStatus", orderStatus);
						job.put("remark", d.getDescription());

						ja.add(job);
					}
					returnJson.put("ordersInfo", ja);
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 商户返佣提现列表查询接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.27 获取当前版本接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/appVersion", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject appVersion(HttpServletRequest request) {
		logger.info("----获取当前版本接口-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("appVersion json={}", json.toJSONString());
			String agentId = json.getString("agentId");// 服务商编号

			String appType = json.getString("appType");// ‘ios’- 苹果 ‘android’-
			String versionId = json.getString("versionId");// 客户端版本号

			if (StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(appType) && StringUtil.isNotEmpty(versionId)) {
				AppVersion version = appVersionService.getNewestAppVersion(appType, agentId);
				if (version.getVersionName().equalsIgnoreCase(versionId)) {
					returnJson.put("isUpdate", "N");
				} else {
					AppVersion ver = appVersionService.getNewestApp(appType, agentId, versionId);
					if (ver.getBool()) {
						returnJson.put("isUpdate", "Y");
					} else {
						returnJson.put("isUpdate", "N");
					}
				}
				returnJson.put("isForce", version.getIsForce());
				returnJson.put("updateContent", version.getContent());
				returnJson.put("appUrl", version.getUpdateUrl());
				// returnJson.put("appUrl", version.getDownloadNet());//
				// 20171201
				// 飞飞注册下载切换
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.35 图片上传接口
	 * 
	 * @param merId
	 * @param jarFile
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/uploadFaceImg", consumes = "multipart/form-data", method = RequestMethod.POST)
	public String uploadFaceImg(HttpServletRequest request, String merId, MultipartFile jarFile) throws Exception {
		logger.info("----上传头像图片 接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			// 下面是测试代码
			if (StringUtil.isNotEmpty(merId) && jarFile != null) {
				String folder = DateUtil.convertDateStrYYYYMMDD(new Date());
				String fileName = imageService.writeImageTofolder(Long.parseLong(merId), folder, jarFile);
				if (StringUtil.isNotBlank(fileName)) {
					userService.updateUserHeadIcon(Long.parseLong(merId), folder + fileName);
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					returnJson.put("fileName", folder + fileName);
				}
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----上传头像 接口 end-----" + returnJson);
		return returnJson.toJSONString();

	}

	/**
	 * 2.3.29 商户转账接口
	 * 
	 * @category 转账
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doTrfToMer", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject doTrfToMer(HttpServletRequest request) {
		logger.info("---- 商户转账接口 begin-----");
		JSONObject returnJson = new JSONObject();

		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			logger.info("doTrfToMer json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			// 暂时关闭转账功能
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
				returnJson.put("respDesc", "转账升级,敬请谅解!");
				logger.info("---- 商户转账接口 end-----" + returnJson);
				return commonService.getEncryptBody(returnJson);
			}

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				jiguangPushService.sendMsgSoundInfoToPerson(Long.parseLong(merId));
				String phone = json.getString("phone");

				String transAmt = json.getString("transAmt");
				String transPwd = json.getString("transPwd");
				String trfTitle = json.getString("trfTitle");

				if (StringUtil.isNotEmpty(phone) && StringUtil.isNotEmpty(transAmt) && StringUtil.isNotEmpty(transPwd)) {
					String stmFlag = userService.isTransAccount(Long.parseLong(merId), transPwd, Double.parseDouble(transAmt), false);
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {
						User toUser = userService.findUserByLoginName(phone, agentId);
						if (toUser != null) {
							String tranFlag = paymentService.updateAccountWhenTransferAccount(Long.parseLong(merId), toUser.getId(), Double.parseDouble(transAmt), trfTitle);
							if (GlobalConstant.RESP_CODE_SUCCESS.equals(tranFlag)) {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
							} else {
								returnJson.put("respCode", stmFlag);
								returnJson.put("respDesc", GlobalConstant.map.get(stmFlag));
							}
						} else {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_012);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_012));
						}
					} else {
						returnJson.put("respCode", stmFlag);
						returnJson.put("respDesc", GlobalConstant.map.get(stmFlag));
					}
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 商户转账接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.30 获取推广码接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryTgCode", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryTgCode(HttpServletRequest request) {
		logger.info("----获取推广码接口-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryTgCode json={}", json.toJSONString());
			String agentId = json.getString("agentId");// 服务商编号

			String merId = json.getString("merId");

			if (StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(merId)) {
				User u = userService.get(Long.parseLong(merId));
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				returnJson.put("tgCodeNo", u.getCode());
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 商户转账接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.31 意见反馈接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addFeedback", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject addFeedback(HttpServletRequest request) {
		logger.info("----意见反馈接口-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("addFeedback json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 服务商编号

			String msgCon = json.getString("msgCon");

			if (StringUtil.isNotEmpty(merId) && StringUtil.isNotEmpty(msgCon)) {
				feedBackService.addFeedBack(Long.parseLong(merId), msgCon, agentId);
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_995);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_995));
		}
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.8 人工实名认证接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/applyManualAuthentication", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject applyManualAuthentication(HttpServletRequest request) {
		logger.info("----人工实名认证接口begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			String url = request.getContextPath();
			JSONObject json = commonService.getRequstBody(request);
			logger.info("applyManualAuthentication json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("实名认证接口 userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String realName = json.getString("realName");
				String idNo = json.getString("idNo");
				String merName = json.getString("merName");// 商户名称
				String frontIDPath = json.getString("frontIDPath");// 身份证正面
				String backIDPath = json.getString("backIDPath");// 身份证反面
				String handIDPath = json.getString("handIDPath");// 手持身份证
				String merchantCity = json.getString("merchantCity");// 店铺所在城市
				String merchantName = json.getString("merchantName");// 店铺名称

				// ios添加人工认证开关---开始
				// String appType = json.getString("appType");// 手机型号
				// if (appType.equals("IOS") || appType.equals("ios") ||
				// appType.equals("Ios")) {
				// // 只关闭宝贝钱袋的
				// User judgeUser = userService.get(Long.valueOf(merId));
				// if (judgeUser.getAgentId().equals("F20160001")) {
				// returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
				// returnJson.put("respDesc", "人工认证升级中，请使用自动认证");
				// return commonService.getEncryptBody(returnJson);
				// }
				//
				// }
				// ios添加人工认证开关---结束

				String chkValue = json.getString("chkValue");// 手机型号
				if (StringUtil.isNotEmpty(merId) && StringUtil.isNotEmpty(realName) && StringUtil.isNotEmpty(frontIDPath) && StringUtil.isNotEmpty(backIDPath) && StringUtil.isNotEmpty(handIDPath) && StringUtil.isNotEmpty(chkValue)) {
					String realIdNo = ImportUtil.getDecIdNo(idNo);

					String chk = MD5Util.md5(merId.trim() + idNo.trim() + "flypayrgrz");
					/* 验证签名 */
					if (chk.equals(chkValue)) {
						json.put("idNo", realIdNo);
						json.put("url", url + "/mobile/getImage/");
						Map<String, String> cardInfos = json.toJavaObject(json, Map.class);
						String flag = userService.updateUserAuthWhenManualAuth(Long.parseLong(merId), cardInfos);
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
						} else {
							/* 交易失败 */
							returnJson.put("respCode", flag);
							returnJson.put("respDesc", GlobalConstant.map.get(flag));
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
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----人工实名认证接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.35 图片上传接口
	 * 
	 * @param merId
	 * @param jarFile
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/uploadImage", consumes = "multipart/form-data", method = RequestMethod.POST)
	public String uploadImage(HttpServletRequest request, String merId, MultipartFile jarFile) throws Exception {
		logger.info("----上传图片 接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			// 下面是测试代码
			if (StringUtil.isNotEmpty(merId) && jarFile != null) {
				String folder = DateUtil.convertDateStrYYYYMMDD(new Date());
				String fileName = imageService.writeImageTofolder(Long.parseLong(merId), folder, jarFile);
				if (StringUtil.isNotBlank(fileName)) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					returnJson.put("fileName", folder + fileName);
				}
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----上传图片 接口 end-----");
		String retStr = returnJson.toJSONString();
		String	replaceStr= retStr.replace("\\", "");
		logger.info("----上传图片 接口 end-----" + replaceStr );
//		String b = replaceStr.substring(1, replaceStr.length() - 1);
		return replaceStr;
	}

	/**
	 * 获取图片
	 * 
	 * @param request
	 * @param resp
	 * @param fileName
	 */
	@ResponseBody
	@RequestMapping(value = "/getImage/{fileName}", method = RequestMethod.GET)
	public void getImage(HttpServletRequest request, HttpServletResponse resp, @PathVariable String fileName) {
		logger.info("---获得图片 start---");
		try {
			logger.info("---获得图片 --  fileName=" + fileName);
			if (fileName.matches("[0-9]{8}.*")) {
				File file = imageService.getImage(fileName);
				if (file != null) {
					FileInputStream fs = new FileInputStream(file);
					ServletOutputStream sos = resp.getOutputStream();
					IOUtils.copy(fs, sos);
					sos.close();
					fs.close();
				}
			}
		} catch (Exception e) {
			logger.error("获取图片失败", e);
		}
		logger.info("---获得图片 end---");
	}

	/**
	 * 2.3.39 银行编号查询接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryBankList", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryBankList(HttpServletRequest request) {
		logger.info("---- 银行编号查询接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryBankList json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				String bankType = json.getString("bankType");

				if (StringUtil.isNotEmpty(bankType)) {
					List<Bank> bks = bankService.findBankList();

					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					returnJson.put("totalNum", bks.size());
					JSONArray ja = new JSONArray();
					for (Bank bk : bks) {
						JSONObject job = new JSONObject();
						job.put("bankId", bk.getId().toString());
						job.put("bankName", bk.getBankName());
						ja.add(job);
					}
					returnJson.put("ordersInfo", ja);
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 银行编号查询接口 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.41验证银行卡是否有效，以及所属行
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/validateBankCode", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject validateBankCode(HttpServletRequest request) {
		logger.info("---- 银行编号查询接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("validateBankCode json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				String bankCode = json.getString("bankCode");

				if (StringUtil.isNotEmpty(bankCode) && bankCode.length() >= 14) {
					TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(bankCode.trim()));
					if (cbc != null) {
						Bank bank = bankService.getBankByBankCode(cbc.getBankCode());
						if (bank != null) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
							returnJson.put("isConfirm", "1");
							returnJson.put("bankId", bank.getId());
							returnJson.put("bankName", bank.getBankName());
							returnJson.put("cardType", "借记卡".equals(cbc.getCardType()) ? "J" : "X");
						} else {
							/* 参数失败 */
							returnJson.put("respCode", GlobalConstant.RESP_CODE_054);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_054));
						}
					} else {
						/* 参数失败 */
						returnJson.put("respCode", GlobalConstant.RESP_CODE_053);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_053));
					}
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_053);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_053));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			e.printStackTrace();
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 银行编号查询接口 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.29商户使用钱包购买代理
	 * 
	 * @category 使用钱包购买代理
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/payAgentByBill", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject payAgentByBill(HttpServletRequest request) {
		logger.info("---- 商户使用钱包购买代理 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("payAgentByBill json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				String transAmt = json.getString("transAmt");
				String transType = json.getString("transType");
				String transPwd = json.getString("transPwd");

				if (StringUtil.isNotEmpty(transType) && StringUtil.isNotEmpty(transAmt) && StringUtil.isNotEmpty(transPwd)) {
					String stmFlag = userService.isTransAccount(Long.parseLong(merId), transPwd, Double.parseDouble(transAmt), false);
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {

						if (json.containsKey("agentType") && StringUtil.isNotEmpty(json.getString("agentType"))) {

							String agentType = json.getString("agentType");// 支付类型
							stmFlag = orderService.isAllowPayAgent(userService.get(Long.parseLong(merId)), Integer.parseInt(agentType));
							if (!GlobalConstant.RESP_CODE_SUCCESS.equals(stmFlag)) {
								/* 购买代理过于频繁 */
								returnJson.put("respCode", stmFlag);
								returnJson.put("respDesc", GlobalConstant.map.get(stmFlag));
								return commonService.getEncryptBody(returnJson);
							}
						}

						String tranFlag = paymentService.updateAccountWhenPayAgent(Long.parseLong(merId), Double.parseDouble(transAmt));
						if (GlobalConstant.RESP_CODE_SUCCESS.equals(tranFlag)) {
							returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
							returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
						} else {
							returnJson.put("respCode", tranFlag);
							returnJson.put("respDesc", GlobalConstant.map.get(tranFlag));
						}
					} else {
						returnJson.put("respCode", stmFlag);
						returnJson.put("respDesc", GlobalConstant.map.get(stmFlag));
					}
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 商户使用钱包购买代理 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.43 商务合作接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addBusiness", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject addBusiness(HttpServletRequest request) {
		logger.info("----商务合作接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("addBusiness json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 用户编号

			String contactor = json.getString("contactor");
			String contactPhone = json.getString("contactPhone");
			Integer busType = json.getInteger("busType");
			String companyNet = json.getString("companyNet");
			String busDesc = json.getString("busDesc");

			if (StringUtil.isNotEmpty(merId) && StringUtil.isNotEmpty(contactor) && StringUtil.isNotEmpty(contactPhone)) {
				Business bs = new Business(contactor, contactPhone, busType, companyNet, busDesc, agentId);
				businessService.add(bs);
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_995);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_995));
		}
		logger.info("----商务合作接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.44 收款二维码接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getCollectionCode", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject getCollectionCode(HttpServletRequest request) {
		logger.info("----收款二维码接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("getCollectionCode json={}", json.toJSONString());
			String agentId = json.getString("agentId");
			String merId = json.getString("merId");// 服务商编号
			if (StringUtil.isNotEmpty(merId)) {
				User user = userService.get(Long.parseLong(merId));
				if (!agentId.equals("F20160017") && !agentId.equals("F20160011") && !agentId.equals("F20160013") && !agentId.equals("F20160015")) {
					if (!agentId.equals("F20160001") && user.getMerchantType() != 1 && user.getMerchantType() != 10) {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
						returnJson.put("respDesc", "二维码交易升级中");
						return commonService.getEncryptBody(returnJson);
					}
				}

				// 为防止聚合码支付出现意外状况，加上开关 1开启 2只开启卢总的账号 0关闭
				Map<String, String> sysParams = paramService.searchSysParameter();
				if (sysParams.get("centralization_pay_switch_on") == null || !sysParams.get("centralization_pay_switch_on").equals("1")) {
					if (StringUtil.isBlank(sysParams.get("centralization_pay_switch_on")) || sysParams.get("centralization_pay_switch_on").equals("0") || !(sysParams.get("centralization_pay_switch_on").equals("2") && user.getId() == 2)) {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
						returnJson.put("respDesc", "二维码交易升级中");
						return commonService.getEncryptBody(returnJson);

					}
				}

				if (user.getBlackStatus() == 1) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", "认证信息有误,请联系客服");
					return commonService.getEncryptBody(returnJson);
				}
				String path = request.getContextPath() + "/popularizePay/payOnlinePage?userCode=";
				path = path + user.getCode() + StringUtils.leftPad(String.valueOf(RandomUtils.nextInt(99999)), 5, "0");
				String buf = "https://";
				if (!"bbpurse.com".equals(request.getServerName())) {
					buf = "http://";
				}
				String url = buf + request.getServerName() + path;
				// String url = buf + "1g83849h98.iask.in:34530" + path;
				returnJson.put("payUrl", url);
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----收款二维码接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.45 系统公告消息接口
	 * 
	 * @category 系统公告
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryInfoList", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryInfoList(HttpServletRequest request) {
		logger.info("---- 系统公告消息接口start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryInfoList json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String msgType = json.getString("msgType");
				String pageNum = json.getString("pageNum");
				String pageSize = json.getString("pageSize");
				if (StringUtil.isNotBlank(msgType) & StringUtil.isNotBlank(pageNum) & StringUtil.isNotBlank(pageSize)) {

					PageFilter pf = new PageFilter();
					pf.setPage(Integer.parseInt(pageNum));
					pf.setRows(Integer.parseInt(pageSize));
					pf.setSort("id");
					pf.setOrder(" desc");
					InfoList infoList = new InfoList();
					infoList.setUserId(Long.parseLong(merId));
					// infoList.setStatus(InfoList.info_status.release_success.getCode());
					infoList.setInfoType(msgType.equals("N") ? InfoList.info_Type.person.getCode() : InfoList.info_Type.company.getCode());
					infoList.setAgentId(agentId);
					infoList.setIsShow(1);
					List<InfoList> infolists = infoListService.dataGrid(infoList, pf);

					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					returnJson.put("currentDate", DateUtil.convertCurrentDateTimeToString());
					returnJson.put("totalNum", infoListService.count(infoList, pf));
					infoList.setIsRead(InfoList.is_Read.unread.getCode());
					// 之前的查询逻辑，直接查询未读消息数
					/*
					 * returnJson.put("unReadedNum",
					 * infoListService.count(infoList, pf));
					 */
					// update 2017.11.22 查询出未读消息id，方便下面更新未读为已读
					List<TinfoList> list = infoListService.findUnRead(infoList, pf);
					if (CollectionUtil.isEmpty(list)) {
						returnJson.put("unReadedNum", 0);
					} else {
						returnJson.put("unReadedNum", list.size());
					}
					JSONArray ja = new JSONArray();
					for (InfoList d : infolists) {
						JSONObject job = new JSONObject();
						job.put("id", d.getId());
						job.put("createDateTime", DateUtil.getyyyyMMddHHmmssStringFromDate(d.getCreateTime()));
						job.put("title", d.getTitle());
						job.put("content", d.getContent());
						ja.add(job);
					}
					returnJson.put("ordersInfo", ja);
					// update：如果不存在未读消息，不需要更新
					if (infoList.getInfoType() == InfoList.info_Type.person.getCode() && list.size() >= 1) {
						infoListService.updateAllInfoListToReadedStatus(infoList, list);
					}
				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			logger.error("系统公告消息接口 error ", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("---- 系统公告消息接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.46 替换结算银行卡接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateSltBankCard", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject updateSltBankCard(HttpServletRequest request) {
		logger.info("----替换结算银行卡接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("updateSltBankCard json={}", json.toJSONString());
			Long merId = json.getLong("merId");// 服务商编号
			Long cardId = json.getLong("cardId");// 卡ID

			if (merId != null && cardId != null) {
				String flag = cardService.updateSettlementCard(merId, cardId);

				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----替换结算银行卡接口end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.46 删除银行卡接口
	 * 
	 * @category 删除银行卡
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteBankCard", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject deleteBankCard(HttpServletRequest request) {
		logger.info("----删除银行卡接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("deleteBankCard json={}", json.toJSONString());
			Long merId = json.getLong("merId");// 服务商编号
			Long cardId = json.getLong("cardId");// 卡ID

			if (merId != null && cardId != null) {
				cardService.deleteCard(merId, cardId);
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----删除结算银行卡接口end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.47 实名认证失败原因接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryAuthFailureReason", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryAuthFailureReason(HttpServletRequest request) {
		logger.info("----实名认证失败原因接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryAuthFailureReason json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			Long merId = json.getLong("merId");

			if (merId != null) {
				User user = userService.get(merId);
				if (user != null && user.getAuthenticationStatus() == User.authentication_status.FAILURE.getCode()) {
					Set<Integer> authTyps = new HashSet<Integer>();
					authTyps.add(AuthenticationLog.auth_type.auto.getCode());
					authTyps.add(AuthenticationLog.auth_type.manual.getCode());
					returnJson.put("errorContent", authenticationService.findAuthErroInfo(merId, authTyps));
				}

				if (user != null && user.getAuthenticationStatus() == User.authentication_status.SUCCESS.getCode()) {
					returnJson.put("idNo", ImportUtil.getEncIdNo(user.getIdNo()));
					returnJson.put("merchantName", user.getMerchantName());
					if (user.getMerchantType() == User.merchant_type.FAILURE.getCode()) {
						Set<Integer> authTyps = new HashSet<Integer>();
						authTyps.add(AuthenticationLog.auth_type.manual_merchant.getCode());
						returnJson.put("errorContent", authenticationService.findAuthErroInfo(merId, authTyps));
					}
				}
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				returnJson.put("authenticationStatus", user.getAuthenticationStatus());
				returnJson.put("merchantType", user.getMerchantType());

			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----实名认证失败原因接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.49 通道费率限额列表查询接口
	 * 
	 * @category 收款页面 通道限额接口
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryChannelLimitList", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryChannelLimitList(HttpServletRequest request) {
		logger.info("---- 通道费率限额列表查询接口start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryChannelLimitList json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {

				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				returnJson.put("currentDate", DateUtil.convertCurrentDateTimeToString());

				JSONArray ja = new JSONArray();
				List<Map<String, String>> chls = channelService.findShowChannelLimit(Long.parseLong(merId), agentId);
				ja.addAll(chls);
				returnJson.put("ordersInfo", ja);
				returnJson.put("t5Switch", paramService.searchSysParameter().get("t5_swift_on"));
				returnJson.put("t8Switch", paramService.searchSysParameter().get("t8_swift_on"));
				returnJson.put("t10Switch", paramService.searchSysParameter().get("t10_swift_on"));
				returnJson.put("t58MinType", paramService.searchSysParameter().get("t58MinType"));

			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			logger.error("通道费率限额列表查询接口接口 error ", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----通道费率限额列表查询接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.50 用户积分查询接口
	 * 
	 * @category 用户积分查询接口
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryUserPoint", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryUserPoint(HttpServletRequest request) {
		logger.info("---- 用户积分查询接口start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryUserPoint json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				AccountPoint accPoint = accountPointService.getAccountPointByUserId(Long.parseLong(merId));
				returnJson.put("avlPoint", accPoint.getPoint());
				JSONArray ja = new JSONArray();
				JSONArray jaz = new JSONArray();
				List<OrgPointConfig> opcs = orgPointConfigService.findOrgPointConfigsByAgentId(agentId, false);
				for (OrgPointConfig d : opcs) {
					JSONObject job = new JSONObject();
					job.put("chlType", d.getPayType());
					job.put("topRate", d.getTopRate());
					job.put("midRate", d.getMidRate());
					job.put("lowRate", d.getLowRate());
					job.put("toMidRatePoint", d.getToMidNum());
					job.put("toLowRatePoint", d.getToLowNum());
					job.put("accountType", d.getType());
					int payAmtType = 100;
					if (d.getType() == 0 || d.getType() == 1) {
						payAmtType = 100;
					} else if (d.getType() == 10 || d.getType() == 11) {
						payAmtType = 200;
					} else if (d.getType() == 20 || d.getType() == 21) {
						payAmtType = 300;
					}
					PayTypeLimitConfig p = payTypeLimitConfigService.getPayType(payAmtType, agentId, d.getPayType());
					job.put("minAmt", p.getMinAmt());
					job.put("maxAmt", p.getMaxAmt());
					job.put("currentRate", userSettlementConfigService.getUserInputRate(d.getPayType(), Long.parseLong(merId), d.getType()));
					if (d.getType() == 20 || d.getType() == 21) {
						jaz.add(job);
					} else {
						ja.add(job);
					}
				}
				if (agentId.equals("F20160017")) {
					Tuser user = userService.getTuser(Long.parseLong(merId));
					returnJson.put("userType", user.getUserType());
				}
				returnJson.put("pointsInfo", ja);
				returnJson.put("pointsInfoZTH", jaz);

			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}

		} catch (Exception e) {
			logger.error("通道费率限额列表查询接口接口 error ", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----用户积分查询接口end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.50 新用户积分查询接口
	 * 
	 * @category 新用户积分查询接口
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryUserPointNew", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryUserPointNew(HttpServletRequest request) {
		logger.info("---- 用户积分查询接口start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryUserPointNew json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 商户ID
			String payType = json.getString("payType");
			String InfoType = json.getString("InfoType");
			logger.info("userId=" + merId + "   token=" + token);
			if (userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && StringUtil.isNotBlank(payType) && StringUtil.isNotBlank(InfoType)) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					AccountPoint accPoint = accountPointService.getAccountPointByUserId(Long.parseLong(merId));
					returnJson.put("avlPoint", accPoint.getPoint());
					List<OrgPointConfig> opcs = orgPointConfigService.findOrgPointConfigsByAgentIdPayType(agentId, payType, InfoType);
					JSONArray ja = new JSONArray();
					JSONArray jaz = new JSONArray();
					for (OrgPointConfig d : opcs) {
						JSONObject job = new JSONObject();
						job.put("chlType", d.getPayType());
						job.put("topRate", d.getTopRate());
						job.put("midRate", d.getMidRate());
						job.put("lowRate", d.getLowRate());
						job.put("toMidRatePoint", d.getToMidNum());
						job.put("toLowRatePoint", d.getToLowNum());
						job.put("accountType", d.getType());
						int payAmtType = 100;
						if (d.getType() == 0 || d.getType() == 1) {
							payAmtType = 100;
						} else if (d.getType() == 10 || d.getType() == 11) {
							payAmtType = 200;
						} else if (d.getType() == 20 || d.getType() == 21) {
							payAmtType = 300;
						}
						PayTypeLimitConfig p = payTypeLimitConfigService.getPayType(payAmtType, agentId, d.getPayType());
						job.put("minAmt", p.getMinAmt());
						job.put("maxAmt", p.getMaxAmt());
						job.put("currentRate", userSettlementConfigService.getUserInputRate(d.getPayType(), Long.parseLong(merId), d.getType()));
						if (d.getType() == 20 || d.getType() == 21) {
							jaz.add(job);
						} else {
							ja.add(job);
						}
					}
					if (InfoType.equals("1")) {
						returnJson.put("pointsInfoZTH", jaz);
					} else if (InfoType.equals("2")) {
						returnJson.put("pointsInfo", ja);
					}
				} else {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			logger.error("通道费率限额列表查询接口接口 error ", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----用户积分查询接口end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.50 新用户积分查询接口
	 * 
	 * @category 新用户积分查询接口
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryUserPointChck", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryUserPointChck(HttpServletRequest request) {
		logger.info("---- 检查积分接口start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryUserPointChck json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 商户ID

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				List<OrgPointConfig> opcs = orgPointConfigService.findOrgPointConfigsByAgentIdAgentId(agentId);
				JSONArray ja = new JSONArray();
				Map<String, String> mw = new HashMap<String, String>();
				Map<String, String> mz = new HashMap<String, String>();
				Map<String, String> ml = new HashMap<String, String>();
				Map<String, String> mq = new HashMap<String, String>();
				Map<String, String> mb = new HashMap<String, String>();
				Map<String, String> mj = new HashMap<String, String>();
				Map<String, String> mjf = new HashMap<String, String>();
				mw.put("payType", "300");
				mw.put("payValue", "1");
				mw.put("payValueZ", "1");
				mz.put("payType", "200");
				mz.put("payValue", "1");
				mz.put("payValueZ", "1");
				ml.put("payType", "500");
				ml.put("payValue", "1");
				ml.put("payValueZ", "1");
				mq.put("payType", "1300");
				mq.put("payValue", "1");
				mq.put("payValueZ", "1");
				mb.put("payType", "1000");
				mb.put("payValue", "1");
				mb.put("payValueZ", "1");
				mj.put("payType", "900");
				mj.put("payValue", "1");
				mj.put("payValueZ", "1");
				mjf.put("payType", "550");
				mjf.put("payValue", "1");
				mjf.put("payValueZ", "1");
				for (OrgPointConfig d : opcs) {
					if (d.getType() == 20 || d.getType() == 21) {
						if (d.getPayType() == 300) {
							mw.put("payValueZ", "0");
						} else if (d.getPayType() == 200) {
							mz.put("payValueZ", "0");
						} else if (d.getPayType() == 500) {
							ml.put("payValueZ", "0");
						} else if (d.getPayType() == 1300) {
							mq.put("payValueZ", "0");
						} else if (d.getPayType() == 550) {
							mjf.put("payValueZ", "0");
						}
					} else {
						if (d.getPayType() == 300) {
							mw.put("payValue", "0");
						} else if (d.getPayType() == 200) {
							mz.put("payValue", "0");
						} else if (d.getPayType() == 500) {
							ml.put("payValue", "0");
						} else if (d.getPayType() == 1300) {
							mq.put("payValue", "0");
						} else if (d.getPayType() == 1000) {
							mb.put("payValue", "0");
						} else if (d.getPayType() == 900) {
							mj.put("payValue", "0");
						}
					}
				}
				ja.add(0, mw);
				ja.add(1, mz);
				ja.add(2, ml);
				ja.add(3, mq);
				ja.add(4, mb);
				ja.add(5, mj);
				ja.add(6, mjf);
				returnJson.put("Chcke", ja);
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			logger.error("通道费率限额列表查询接口接口 error ", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----检查积分接口end-----" + returnJson);
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
	@RequestMapping(value = "/consumeUserPointByReduceChlRate", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject consumeUserPointByReduceChlRate(HttpServletRequest request) {
		logger.info("---- 用户积分查询接口start-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			JSONObject json = commonService.getRequstBody(request);
			logger.info("consumeUserPointByReduceChlRate json={}", json.toJSONString());
			String agentId = json.getString("agentId");// 服务商编号
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
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
							// accountPointService.updatePointByUserId(Long.parseLong(merId),
							// "C", point.longValue() * (-1) , "降低通道费率消耗10积分",
							// AccountPoint.pointTypes_consume);
							stmFlag = accountPointService.updateUserInputRateByConsumePoint(agentId, Long.parseLong(merId), channelType, accountType, Integer.parseInt(type), Integer.parseInt(consumePoint));

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
			logger.error("通道费率限额列表查询接口接口 error ", e);
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----用户积分查询接口end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.8 人工商家实名认证接口
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/applyManualMerchantAuthentication", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject applyManualMerchantAuthentication(HttpServletRequest request) {
		logger.info("----人工商家认证接口begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");
			String url = request.getContextPath();
			JSONObject json = commonService.getRequstBody(request);
			logger.info("applyManualMerchantAuthentication json={}", json.toJSONString());
			String merId = json.getString("merId");// 商户ID
			logger.info("实名认证接口 userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String merchantName = json.getString("merchantName");// 商户名称
				String address = json.getString("address");// 商户地址
				String frontIDPath = json.getString("frontIDPath");// 身份证正面
				String backIDPath = json.getString("backIDPath");// 身份证反面

				// String licensePath = json.getString("licensePath");// 营业执照
				// String handInCashierDeskPath =
				// json.getString("handInCashierDeskPath");// 申请人手持身份证在收银台内照片
				String interiorView1Path = json.getString("interiorView1Path");// 内景照1
				// String interiorView2Path =
				// json.getString("interiorView2Path");// 内景照2
				// String interiorView3Path =
				// json.getString("interiorView3Path");// 内景照3
				String shopPath = json.getString("shopPath");// 申请人与门头合照

				String appType = json.getString("appType");// 手机型号
				if (StringUtil.isNotEmpty(merId) && StringUtil.isNotEmpty(merchantName) && StringUtil.isNotEmpty(address) && StringUtil.isNotEmpty(frontIDPath) && StringUtil.isNotEmpty(backIDPath) && StringUtil.isNotEmpty(shopPath)
						&& StringUtil.isNotEmpty(interiorView1Path)) {
					json.put("url", url + "/mobile/getImage/");
					Map<String, String> cardInfos = json.toJavaObject(json, Map.class);
					String flag = userService.updateUserMerchantAuthWhenManualAuth(Long.parseLong(merId), cardInfos);
					if (GlobalConstant.RESP_CODE_SUCCESS.equals(flag)) {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
					} else {
						/* 交易失败 */
						returnJson.put("respCode", flag);
						returnJson.put("respDesc", GlobalConstant.map.get(flag));
					}

				} else {
					/* 参数失败 */
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				/* token 过期 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----人工商家认证接口 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 2.3.53收款成功声音设置接口
	 * 
	 * @category 收款成功声音设置
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/setVoiceOfSuccess", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject setVoiceOfSuccess(HttpServletRequest request) {
		logger.info("----收款成功声音设置接口-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("setVoiceOfSuccess json={}", json.toJSONString());
			String merId = json.getString("merId");
			String agentId = json.getString("agentId");
			String voiceType = json.getString("voiceType");
			String appType = json.getString("appType");

			if (StringUtil.isNotEmpty(merId) && StringUtil.isNotEmpty(agentId) && StringUtil.isNotEmpty(voiceType) && StringUtil.isNotEmpty(appType)) {
				userService.updateUserVoiceType(Long.parseLong(merId), Integer.parseInt(voiceType));
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));

			} else {
				/* 参数失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		return commonService.getEncryptBody(returnJson);
	}
	
	public static void main(String[] args) {
		String aaString="{\"respCode\":\"000\",\"fileName\":\"20180504b503699d95214674950928f376c28fe4\",\"respDesc\":\"成功\"}";
//		aaString.replace("\\", "");
		
		
//		String retStr = returnJson.toJSONString();
		String	replaceStr= aaString.replace("\\", "");
		String b = replaceStr.substring(1, replaceStr.length() - 1);
		System.out.println(b);
		
	}
}
