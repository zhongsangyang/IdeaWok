package com.cn.flypay.controller.mobile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.pageModel.account.Account;
import com.cn.flypay.pageModel.account.AccountPoint;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.BannerImage;
import com.cn.flypay.pageModel.sys.InfoList;
import com.cn.flypay.pageModel.sys.Organization;
import com.cn.flypay.pageModel.sys.PayTypeLimitConfig;
import com.cn.flypay.pageModel.sys.SysParameter;
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
import com.cn.flypay.service.sys.BannerImageService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.InfoListService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.PayTypeLimitConfigService;
import com.cn.flypay.service.sys.SysInformationPhotoService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.service.trans.BrokerageDetailService;
import com.cn.flypay.service.trans.BrokerageService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.ImportUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.channel.WeixinUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 微信授权登录
 * 
 * @author LiWei
 *
 */
@Controller
@RequestMapping("/mobile")
public class WeiXinLoginController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CommonService commonService;

	@Autowired
	private AccountPointService accountPointService;

	@Autowired
	private SysParamService sysParamService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserTokenService userTokenService;

	@Autowired
	private MobileService mobileService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private InfoListService infoListService;

	@Autowired
	private UserOrderService orderService;

	@Autowired
	private BrokerageService brokerageService;

	@Autowired
	private BrokerageDetailService brokerageDetailService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserCardService cardService;

	@Autowired
	private PayTypeLimitConfigService payTypeLimitConfigService;

	@Autowired
	private SysParamService paramService;

	@Autowired
	private UserSettlementConfigService configService;

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private SysInformationPhotoService sysInformationPhotoService;

	@Autowired
	BannerImageService bannerImageService;

	/**
	 * 
	 * 授权登录接口
	 * 
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/WeiXinCode")
	public JSONObject WeiXinLogin(HttpServletRequest request) {
		logger.info("----微信Code登录 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("WeiXinLogin json={}", json.toJSONString());
			String code = json.getString("code");
			String agentId = json.getString("agentId");

			String tokenString = String.format(WeixinUtil.GET_OAUTH_TOKEN, "wx57994b616f84a185", "9e0c1f4ed946ac0f154dc7edf5dfe3fa", code);
			String resp = WeixinUtil.httpsStringRequest(tokenString, "GET", null, null);
			JSONObject respJson = JSONObject.parseObject(resp);
			String access_token = respJson.getString("access_token");
			String openid = respJson.getString("openid");

			String infoString = String.format(WeixinUtil.USER_INFO, access_token, openid);
			String respInfo = WeixinUtil.httpsStringRequest(infoString, "GET", null, null);
			JSONObject infoJson = JSONObject.parseObject(respInfo);
			String openidTwo = infoJson.getString("openid");
			String nickname = infoJson.getString("nickname");
			String unionid = infoJson.getString("unionid");

			User user = userService.getOpen(openidTwo, unionid, agentId);
			if (user == null) {
				returnJson.put("type", "1");
			} else {
				returnJson.put("type", "2");
			}
			returnJson.put("unionid", unionid);
			returnJson.put("nickname", nickname);
			returnJson.put("openid", openidTwo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("----微信Code登录 end-----");
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
	@RequestMapping(value = "/loginCode", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject loginCode(HttpServletRequest request) {
		logger.info("----用户登录 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("loginCode json={}", json.toJSONString());
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
							if (u.getState() == 1) {
								returnJson.put("respCode", GlobalConstant.RESP_CODE_082);
								returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_082));
							} else {
								String openid = json.getString("openid");
								String nickname = json.getString("nickname");
								String unionid = json.getString("unionid");
								if (StringUtil.isNotEmpty(openid) && StringUtil.isNotEmpty(nickname) && StringUtil.isNotEmpty(unionid)) {
									userService.updateUserWeiXinCode(openid, nickname, unionid, u.getId());
								}
								String sessionId = request.getSession().getId();
								returnJson.put("merId", u.getId());
								returnJson.put("realName", u.getRealName());
								returnJson.put("lastLoginDate", u.getLastDateTime());
								returnJson.put("isAuthentication", u.getAuthenticationStatus().toString());
								returnJson.put("merchantType", u.getMerchantType());
								returnJson.put("currentDate", DateUtil.convertCurrentDateTimeToString());
								// List<String> news =
								// infoListService.findSystemNews(agentId);
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
							}
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
	 * 2.3.2 登陆接口
	 * 
	 * @param param
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/loginWeiXin", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject loginWeiXin(HttpServletRequest request) {
		logger.info("----用户登录 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("loginWeiXin json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号

			String openid = json.getString("openid");
			String nickname = json.getString("nickname");
			String unionid = json.getString("unionid");
			if (StringUtil.isNotEmpty(openid) && StringUtil.isNotEmpty(nickname) && StringUtil.isNotEmpty(unionid)) {
				User u = userService.getOpen(openid, unionid, agentId);
				if (u == null) {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_057);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_057));
					return commonService.getEncryptBody(returnJson);
				}
				String apploginName = u.getLoginName();
				Torganization torg = organizationService.getTorganizationInCacheByCode(agentId);
				if (torg != null) {
					if (u.getState() == 1) {
						returnJson.put("respCode", GlobalConstant.RESP_CODE_082);
						returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_082));
					} else {
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
					}
				} else {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_067);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_067));
				}
			} else {
				/* 登录失败 */
				returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
			}
		} catch (Exception e) {
			/* 系统出错 */
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
		}
		logger.info("----用户登录 end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 
	 * 我的推荐人
	 * 
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/myReferrer")
	public JSONObject myReferrer(HttpServletRequest request) {
		logger.info("----推荐人获取 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("myReferrer json={}", json.toJSONString());
			String merId = json.getString("merId");
			Tuser user = userService.getTuser(Long.valueOf(merId));
			Tuser puser = userService.getTuser(user.getParentUser().getId());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("CreateDate", puser.getCreateDatetime());
			map.put("mobile", puser.getLoginName());
			if (puser != null) {
				map.put("nickName", puser.getRealName());
			}
			map.put("WeiXinName", puser.getNickname());
			if (puser.getPrivacyType() != null) {// 隐私开关
				map.put("privacyType", puser.getPrivacyType());
			}
			Torganization t = organizationService.getTorganizationInCodeTwo(puser.getLoginName());
			if (t != null) {
				map.put("type", t.getOrgType());
			} else {
				map.put("type", puser.getUserType());
			}
			returnJson.put("Referrer", map);

			Organization org = organizationService.get(puser.getOrganization().getId());
			Map<String, Object> dmap = new HashMap<String, Object>();
			Tuser user2 = userService.findUserByLoginNameT(org.getUserPhone(), user.getAgentId());
			if (user2 != null) {
				dmap.put("CreateDate", user2.getCreateDatetime());
				dmap.put("mobile", user2.getLoginName());
				dmap.put("nickName", user2.getRealName());
				dmap.put("WeiXinName", user2.getNickname());
				dmap.put("privacyType", user2.getPrivacyType());// 隐私开关
			}
			dmap.put("type", org.getOrgType());
			returnJson.put("Daferrer", dmap);
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----推荐人获取 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 
	 * 累计收款记录
	 * 
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/myOrderList")
	public JSONObject myOrderList(HttpServletRequest request) {
		logger.info("----累计收款记录 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("myOrderList json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String pageNum = json.getString("pageNum");
				String pageSize = json.getString("pageSize");
				String startDateStr = json.getString("startDate");
				String endDateStr = json.getString("endDate");
				if (StringUtil.isNotEmpty(pageNum) && StringUtil.isNotEmpty(pageSize)) {
					PageFilter pf = new PageFilter();
					pf.setPage(Integer.parseInt(pageNum));
					pf.setRows(Integer.parseInt(pageSize));
					pf.setSort("createTime");
					pf.setOrder(" desc");

					UserOrder order = new UserOrder();
					order.setUserId(Long.parseLong(merId));
					order.setStatus(100);
					Date startDate = new Date();
					if (StringUtil.isNotBlank(startDateStr)) {
						startDate = DateUtil.convertStringToDate("yyyyMMddHHmm", startDateStr);
					} else {
						startDate = null;
					}
					Date endDate = new Date();
					if (StringUtil.isNotBlank(endDateStr)) {
						endDate = DateUtil.convertStringToDate("yyyyMMddHHmm", endDateStr);
					} else {
						endDate = null;
					}
					order.setCreateDatetimeStart(startDate);
					order.setCreateDatetimeEnd(endDate);
					List<UserOrder> orders = orderService.dataGridXS(order, pf);
					DecimalFormat df = new DecimalFormat("####.00");
					BigDecimal b = orderService.getOrderOrgAmtTwo(order, pf);
					if (b == null) {
						returnJson.put("tradeAmt", "0.00");
					} else {
						returnJson.put("tradeAmt", df.format(orderService.getOrderOrgAmtTwo(order, pf).doubleValue()));
					}
					JSONArray ja = new JSONArray();
					for (UserOrder d : orders) {
						JSONObject job = new JSONObject();
						converOrderDetailToJson(job, d);
						ja.add(job);
					}
					returnJson.put("ordersInfo", ja);
				} else {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----累计收款记录 end-----");
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
	}

	/**
	 * 
	 * 我的商户
	 * 
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	// TODO
	@ResponseBody
	@RequestMapping(value = "/myCommercial")
	public JSONObject myCommercial(HttpServletRequest request) {
		logger.info("----我的商户 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("myCommercial json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");// 商户ID
			String agentId = json.getString("agentId");
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				Long Count24 = brokerageService.getUserTotalNums(Long.valueOf(merId), 24, agentId);
				Long Count23 = brokerageService.getUserTotalNums(Long.valueOf(merId), 22, agentId);
				Long Count22 = brokerageService.getUserTotalNums(Long.valueOf(merId), 21, agentId);
				Long Sum24 = brokerageService.getUserTotalNumsTwo(Long.valueOf(merId), 24, agentId);
				Long Sum23 = brokerageService.getUserTotalNumsTwo(Long.valueOf(merId), 22, agentId);
				Long Sum22 = brokerageService.getUserTotalNumsTwo(Long.valueOf(merId), 21, agentId);
				Long Jum21 = brokerageService.getUserTotalTwo(Long.valueOf(merId), agentId);
				Long Sum21 = brokerageService.getUserTotal(Long.valueOf(merId), agentId);
				Long TotalNum = Count24 + Count23 + Count22 + Jum21 + Sum21;
				returnJson.put("totalPersonNum", TotalNum);
				returnJson.put("Sum24", Sum24);
				returnJson.put("Sum23", Sum23);
				returnJson.put("Sum22", Sum22);
				returnJson.put("Sum21", Sum21);
				returnJson.put("Jum24", Count24 - Sum24);
				returnJson.put("Jum23", Count23 - Sum23);
				returnJson.put("Jum22", Count22 - Sum22);
				returnJson.put("Jum21", Jum21);
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----我的商户 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 
	 * 钱包
	 * 
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/myBrokerage")
	public JSONObject myBrokerage(HttpServletRequest request) {
		logger.info("----我的钱包 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("myBrokerage json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				Brokerage b = brokerageService.getBrokerageByUserId(Long.parseLong(merId));
				returnJson.put("cashAgent", b.getTotalAgentBrokerage());
				returnJson.put("cashTrans", b.getTotalTransBrokerage());
				returnJson.put("cashLead", b.getTotalLeadBrokerage());

				DecimalFormat df = new DecimalFormat("#######.00");

				Double totalAgent = Double.parseDouble(brokerageDetailService.getSum(Long.parseLong(merId), 2));
				Double totalTrans = Double.parseDouble(brokerageDetailService.getSum(Long.parseLong(merId), 1));
				Double totalLead = Double.parseDouble(brokerageDetailService.getSum(Long.parseLong(merId), 3));
				if (totalAgent != 0.0) {
					df.format(totalAgent);
				}
				if (totalTrans != 0.0) {
					df.format(totalTrans);
				}
				if (totalLead != 0.0) {
					df.format(totalLead);
				}
				returnJson.put("totalAgent", totalAgent);
				returnJson.put("totalTrans", totalTrans);
				returnJson.put("totalLead", totalLead);
				returnJson.put("tradeAmt", orderService.getOrderOrg(Long.parseLong(merId)));
				Account acc = accountService.getAccountByUserId(Long.parseLong(merId));
				returnJson.put("avlAmt", acc.getAvlAmt().setScale(2, BigDecimal.ROUND_FLOOR));
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----我的钱包 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 
	 * 钱包
	 * 
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/myBrokerageDeatil")
	public JSONObject myBrokerageDeatil(HttpServletRequest request) {
		logger.info("----收款记录 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("myBrokerageDeatil json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String pageNum = json.getString("pageNum");
				String pageSize = json.getString("pageSize");
				String startDateStr = json.getString("startDate");
				String endDateStr = json.getString("endDate");
				String brokerageType = json.getString("brokerageType");
				if (StringUtil.isNotEmpty(pageNum) && StringUtil.isNotEmpty(pageSize) && StringUtil.isNotEmpty(brokerageType)) {
					PageFilter pf = new PageFilter();
					pf.setPage(Integer.parseInt(pageNum));
					pf.setRows(Integer.parseInt(pageSize));
					pf.setOrder("desc");
					pf.setSort("transDatetime");
					BrokerageDetail bk = new BrokerageDetail();
					bk.setBrokerageUserId(Long.parseLong(merId));
					bk.setBrokerageType(Integer.parseInt(brokerageType));
					Date startDate = new Date();
					if (StringUtil.isNotBlank(startDateStr)) {
						startDate = DateUtil.convertStringToDate("yyyyMMddHHmm", startDateStr);
					} else {
						startDate = null;
					}
					Date endDate = new Date();
					if (StringUtil.isNotBlank(endDateStr)) {
						endDate = DateUtil.convertStringToDate("yyyyMMddHHmm", endDateStr);
					} else {
						endDate = null;
					}
					bk.setCreateDatetimeStart(startDate);
					bk.setCreateDatetimeEnd(endDate);
					List<BrokerageDetail> bds = brokerageDetailService.dataGrid(bk, pf);
					JSONArray ja = new JSONArray();
					BigDecimal b = BigDecimal.ZERO;
					for (BrokerageDetail d : bds) {
						JSONObject job = new JSONObject();
						job.put("orderAmt", d.getBrokerage().setScale(2, BigDecimal.ROUND_DOWN));
						b = b.add(d.getBrokerage().setScale(2, BigDecimal.ROUND_DOWN));
						job.put("createDateTime", DateUtil.getyyyyMMddHHmmssStringFromDate(d.getTransDatetime()));
						ja.add(job);
					}
					returnJson.put("ordersInfo", ja);
					returnJson.put("tradeAmt", b);
				} else {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----收款记录 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 
	 * 钱包
	 * 
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/myBrokerageDeatilTwo")
	public JSONObject myBrokerageDeatilTwo(HttpServletRequest request) {
		logger.info("----收款记录 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("myBrokerageDeatilTwo json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);

			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				String pageNum = json.getString("pageNum");
				String pageSize = json.getString("pageSize");
				String appType = json.getString("appType");
				String startDateStr = json.getString("startDate");
				String endDateStr = json.getString("endDate");
				if (StringUtil.isNotEmpty(pageNum) && StringUtil.isNotEmpty(pageSize)) {
					PageFilter pf = new PageFilter();
					pf.setPage(Integer.parseInt(pageNum));
					pf.setRows(Integer.parseInt(pageSize));
					pf.setOrder("desc");
					pf.setSort("transDatetime");
					BrokerageDetail bk = new BrokerageDetail();
					bk.setBrokerageUserId(Long.parseLong(merId));
					Date startDate = new Date();
					if (StringUtil.isNotBlank(startDateStr)) {
						startDate = DateUtil.convertStringToDate("yyyyMMddHHmm", startDateStr);
					} else {
						startDate = null;
					}
					Date endDate = new Date();
					if (StringUtil.isNotBlank(endDateStr)) {
						endDate = DateUtil.convertStringToDate("yyyyMMddHHmm", endDateStr);
					} else {
						endDate = null;
					}
					bk.setCreateDatetimeStart(startDate);
					bk.setCreateDatetimeEnd(endDate);
					List<BrokerageDetail> bds = brokerageDetailService.dataGrid(bk, pf);
					// update：2017.11.17 查询月佣金总收益
					String sumBrokerage = brokerageDetailService.dataGridSum(bk);
					BigDecimal b = BigDecimal.ZERO;
					BigDecimal sum = new BigDecimal(sumBrokerage);
					b = b.add(sum.setScale(2, BigDecimal.ROUND_DOWN));
					JSONArray ja = new JSONArray();
					for (BrokerageDetail d : bds) {
						JSONObject job = new JSONObject();
						job.put("orderAmt", d.getBrokerage().setScale(2, BigDecimal.ROUND_DOWN));
						// b = b.add(d.getBrokerage().setScale(2,
						// BigDecimal.ROUND_DOWN));
						job.put("createDateTime", DateUtil.getyyyyMMddHHmmssStringFromDate(d.getTransDatetime()));
						job.put("phone", d.getPhone());
						/*
						 * 根据受益用户级别在收益明细显示对应图标，注释掉，改用下面分拥提供用户级别 if
						 * (d.getBrokerageUserId() != null) { Tuser user =
						 * userService.getTuser(Long.parseLong(d.
						 * getBrokerageUserId().toString())); if (user != null)
						 * { Torganization t =
						 * organizationService.getTorganizationInCodeTwo(user.
						 * getLoginName()); if (t != null) { job.put("merType",
						 * t.getOrgType()); } else { job.put("merType",
						 * user.getUserType()); } } }
						 */
						// update：2017.11.17 查找交易提供分拥的下级用户的用户级别
						if (d.getPhone() != null) {
							// 查找对应的分拥提供用户
							Tuser user = userService.getUserLoginName(d.getPhone());
							if (user != null) {
								if (user.getLoginName() != null) {
									// 该用户是否是代理商、运营中心
									Torganization t = organizationService.getTorganizationInCodeTwo(user.getLoginName());
									if (t != null) {
										// 设置代理商、运营中心
										job.put("merType", t.getOrgType());
									} else {
										// 普通用户级别
										job.put("merType", user.getUserType());
									}
								}
							}
						}

						ja.add(job);
					}
					returnJson.put("ordersInfo", ja);
					returnJson.put("tradeAmt", b);
					// update:2017.11.21 IOS每次请求后金额累计，所以第二页开始返回0
					if ("ios".equalsIgnoreCase(appType) && pf.getPage() > 1) {
						returnJson.put("tradeAmt", BigDecimal.ZERO);
					}
				} else {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_005);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_005));
				}
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----收款记录 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 
	 * 我的商户记录
	 * 
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/myCommercialList")
	public JSONObject myCommercialList(HttpServletRequest request) {
		logger.info("----我的商户记录start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("myCommercialList json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			String pageNum = json.getString("pageNum");
			String pageSize = json.getString("pageSize");
			String userType = json.getString("userType");
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(pageNum) && StringUtil.isNotBlank(pageSize) && StringUtil.isNotBlank(userType) && StringUtil.isNotBlank(token)
					&& userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				User u = new User();
				u.setId(Long.parseLong(merId));
				PageFilter pf = new PageFilter();
				pf.setPage(Integer.parseInt(pageNum));
				pf.setRows(Integer.parseInt(pageSize));
				pf.setOrder("desc");
				pf.setSort("id");
				JSONArray ja = new JSONArray();
				List<Map<String, String>> bds = new ArrayList<Map<String, String>>();
				if (userType.equals("10")) {
					bds = brokerageService.getAgentListByUserIdTherer(u, pf, Integer.parseInt(userType));
				} else {
					bds = brokerageService.getAgentListByUserIdTwo(u, pf, Integer.parseInt(userType));
				}
				for (Map<String, String> d : bds) {
					JSONObject job = new JSONObject();
					job.putAll(d);
					ja.add(job);
				}
				returnJson.put("ordersInfo", ja);
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----我的商户记录 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	@ResponseBody
	@RequestMapping(value = "/queryMerInfoTwo", produces = MediaType.APPLICATION_JSON_VALUE)
	public JSONObject queryMerInfoTwo(HttpServletRequest request) {
		logger.info("----查询用户基础信息接口 begin-----");
		JSONObject returnJson = new JSONObject();
		try {
			String token = request.getHeader("token");

			JSONObject json = commonService.getRequstBody(request);
			logger.info("queryMerInfoTwo json={}", json.toJSONString());
			String agentId = StringUtil.isEmpty(json.getString("agentId")) ? mobileService.getAgentId() : json.getString("agentId").trim();// 服务商编号
			String merId = json.getString("merId");// 商户ID
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				User user = userService.get(Long.parseLong(merId));

				returnJson.put("merId", merId);
				returnJson.put("merCode", user.getCode());
				returnJson.put("phone", user.getLoginName());
				returnJson.put("realName", user.getRealName());
				if (StringUtil.isNotBlank(user.getMerchantName()) && user.getAuthenticationStatus() - User.authentication_status.SUCCESS.getCode() == 0) {
					returnJson.put("merchantName", user.getMerchantName());
				} else {
					returnJson.put("merchantName", user.getRealName());
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
				returnJson.put("settlementStatus", user.getSettlementStatus());
				returnJson.put("iconPath", user.getIconPath());
				returnJson.put("isSetTransPwd", StringUtil.isNotBlank(user.getStmPsw()) ? 1 : 0);
				returnJson.put("isChnl", user.getIsChnl());
				Account acc = accountService.getAccountByUserId(Long.parseLong(merId));
				returnJson.put("avlAmt", acc.getAvlAmt().setScale(2, BigDecimal.ROUND_FLOOR));

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
				/* 储蓄卡张数 */
				returnJson.put("bankCardNum", cards.size());
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
	 * 
	 * 我的商户
	 * 
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/querEearnings")
	public JSONObject querEearnings(HttpServletRequest request) {
		logger.info("----我的商户 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("querEearnings json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				Long ZauthTrue = brokerageService.getUserEearningsNums(Long.valueOf(merId), 1, true);
				Long ZauthFalse = brokerageService.getUserEearningsNums(Long.valueOf(merId), -1, true);
				Long JauthTrue = brokerageService.getUserEearningsNums(Long.valueOf(merId), 1, false);
				Long JauthFalse = brokerageService.getUserEearningsNums(Long.valueOf(merId), -1, false);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ZauthTrue", ZauthTrue);
				map.put("ZauthFalse", ZauthFalse);
				map.put("JauthTrue", JauthTrue);
				map.put("JauthFalse", JauthFalse);
				map.put("Dzian", brokerageService.getUserTotalNums(Long.valueOf(merId), false));
				map.put("DJian", brokerageService.getUserTotalNums(Long.valueOf(merId), true));
				returnJson.put("map", map);
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----我的商户 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 获取代理收益--直接推荐用户信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/querEearningsTwo")
	public JSONObject querEearningsTwo(HttpServletRequest request) {
		logger.info("----我的商户 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("querEearningsTwo json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				Long ZauthTrue = brokerageService.getUserEearningsNums(Long.valueOf(merId), 1, true);
				Long ZauthFalse = brokerageService.getUserEearningsNums(Long.valueOf(merId), -1, true);
				returnJson.put("ZauthTrue", ZauthTrue);
				returnJson.put("ZauthFalse", ZauthFalse);
				returnJson.put("Dzian", brokerageService.getUserTotalNumsFour(Long.valueOf(merId), "5"));
				returnJson.put("Dcenter", brokerageService.getUserTotalNumsFour(Long.valueOf(merId), "4"));
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----我的商户 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 获取代理收益--所有推荐用户信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/querEearningsThere")
	public JSONObject querEearningsThere(HttpServletRequest request) {
		logger.info("----我的商户 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("querEearningsThere json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");// 商户ID
			String userType = json.getString("userType");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && StringUtil.isNotBlank(userType) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				Long JauthTrue = null;
				Long JauthFalse = null;
				if (userType.equals("1")) {
					JauthTrue = brokerageService.getUserEearningsZums(Long.valueOf(merId), 1);
					JauthFalse = brokerageService.getUserEearningsZums(Long.valueOf(merId), -1);
				} else if (userType.equals("2")) {
					JauthTrue = brokerageService.getUserEearningsDZums(Long.valueOf(merId), 1);
					JauthFalse = brokerageService.getUserEearningsDZums(Long.valueOf(merId), -1);
				}
				returnJson.put("ZauthTrue", JauthTrue);
				returnJson.put("ZauthFalse", JauthFalse);
				returnJson.put("Dzian", brokerageService.getUserTotalNumsThere(Long.valueOf(merId), "5"));
				returnJson.put("Dcenter", brokerageService.getUserTotalNumsThere(Long.valueOf(merId), "4"));
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----我的商户 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	@ResponseBody
	@RequestMapping(value = "/querEearningsFour")
	public JSONObject querEearningsFour(HttpServletRequest request) {
		logger.info("----我的商户 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("querEearningsFour json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");// 商户ID
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				Long ZauthTrue = brokerageService.getUserEearningsFour(Long.valueOf(merId), 1);
				Long ZauthFalse = brokerageService.getUserEearningsFour(Long.valueOf(merId), -1);
				returnJson.put("ZauthTrue", ZauthTrue);
				returnJson.put("ZauthFalse", ZauthFalse);
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----我的商户 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	@ResponseBody
	@RequestMapping(value = "/querEearningsTwoList")
	public JSONObject querEearningsTwoList(HttpServletRequest request) {
		logger.info("----我的商户 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("querEearningsFour json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");
			String type = json.getString("type");
			String userType = json.getString("userType");
			String derType = json.getString("derType");
			String pageNum = json.getString("pageNum");
			String pageSize = json.getString("pageSize");
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(type) && StringUtil.isNotBlank(userType) && StringUtil.isNotBlank(pageNum) && StringUtil.isNotBlank(pageSize) && StringUtil.isNotBlank(token)
					&& userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				User u = new User();
				u.setId(Long.parseLong(merId));
				PageFilter pf = new PageFilter();
				pf.setPage(Integer.parseInt(pageNum));
				pf.setRows(Integer.parseInt(pageSize));
				pf.setOrder("desc");
				pf.setSort("id");
				JSONArray ja = new JSONArray();
				List<Map<String, String>> bds = new ArrayList<Map<String, String>>();
				if (userType.equals("1")) {// 直推栏
					if (type.equals("1")) {
						bds = brokerageService.getAgentListByUserIdFave(u, pf, 1, true);
					} else if (type.equals("2")) {
						bds = brokerageService.getAgentListByUserIdFave(u, pf, -1, true);
					} else if (type.equals("3")) {
						bds = brokerageService.getAgentListByUserIdFive(u, pf, true, "3");
					} else if (type.equals("4")) {
						bds = brokerageService.getAgentListByUserIdFive(u, pf, true, "4");
					}
				} else if (userType.equals("2")) {// 间推栏
					if (type.equals("1")) {
						if (derType.equals("1")) {
							bds = brokerageService.getAgentListByUserIdFave(u, pf, 1, false);
						} else {
							bds = brokerageService.getAgentListByDer(u, pf, 1);
						}
					} else if (type.equals("2")) {
						if (derType.equals("1")) {
							bds = brokerageService.getAgentListByUserIdFave(u, pf, -1, false);
						} else {
							bds = brokerageService.getAgentListByDer(u, pf, -1);
						}
					} else if (type.equals("3")) {
						bds = brokerageService.getAgentListByUserIdFive(u, pf, false, "3");
					} else if (type.equals("4")) {
						bds = brokerageService.getAgentListByUserIdFive(u, pf, false, "4");
					}
				} else if (userType.equals("3")) {
					if (type.equals("1")) {
						bds = brokerageService.getAgentListByUserIdFour(u, pf, 1);
					} else if (type.equals("2")) {
						bds = brokerageService.getAgentListByUserIdFour(u, pf, -1);
					}
				}
				for (Map<String, String> d : bds) {
					JSONObject job = new JSONObject();
					job.putAll(d);
					ja.add(job);
				}
				returnJson.put("ordersInfo", ja);
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
			returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----我的商户 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	@ResponseBody
	@RequestMapping(value = "/querEearningsList")
	public JSONObject querEearningsList(HttpServletRequest request) {
		logger.info("----我的商户 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("querEearningsList json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");
			String pageNum = json.getString("pageNum");
			String pageSize = json.getString("pageSize");
			String type = json.getString("type");
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(pageNum) && StringUtil.isNotBlank(pageSize) && StringUtil.isNotBlank(type) && StringUtil.isNotBlank(token)
					&& userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				User u = userService.get(Long.parseLong(merId));
				PageFilter pf = new PageFilter();
				pf.setPage(Integer.parseInt(pageNum));
				pf.setRows(Integer.parseInt(pageSize));
				pf.setOrder("desc");
				pf.setSort("id");
				JSONArray ja = new JSONArray();
				List<Map<String, String>> bds = new ArrayList<Map<String, String>>();
				if (type.equals("1")) {
					bds = brokerageService.getAgentListByUserIdFex(u, pf, 1);
				} else if (type.equals("2")) {
					bds = brokerageService.getAgentListByUserIdFex(u, pf, -1);
				} else if (type.equals("3")) {
					bds = brokerageService.getAgentListByUserIdFourTwo(u, pf);
				}
				for (Map<String, String> d : bds) {
					JSONObject job = new JSONObject();
					job.putAll(d);
					ja.add(job);
				}
				returnJson.put("ordersInfo", ja);
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----我的商户 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 返回交易页面交易模式列表
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/channelList")
	public JSONObject channelList(HttpServletRequest request) {
		logger.info("----通道列表 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("channelList json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");
			String agentId = json.getString("agentId");
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(agentId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				// List<Integer> transTypes = new ArrayList<Integer>()
				Set<Integer> transTypes = new LinkedHashSet<>();
				transTypes.add(UserOrder.trans_type.YLZX.getCode());
				transTypes.add(UserOrder.trans_type.YLZXE.getCode());
				transTypes.add(UserOrder.trans_type.YLZXJ.getCode());
				transTypes.add(UserOrder.trans_type.YLZXJYB.getCode());
				transTypes.add(UserOrder.trans_type.YLZXJZY.getCode());
				transTypes.add(UserOrder.trans_type.ALQR.getCode());
				transTypes.add(UserOrder.trans_type.WXQR.getCode());
				transTypes.add(UserOrder.trans_type.QQQR.getCode());
				transTypes.add(UserOrder.trans_type.BDQR.getCode());
				transTypes.add(UserOrder.trans_type.JDQR.getCode());
				transTypes.add(UserOrder.trans_type.YZFQR.getCode());
				List<PayTypeLimitConfig> paytypes = payTypeLimitConfigService.findPayTypeLimitConfigsXTC(transTypes, agentId);
				returnJson.put("channelInfo", paytypes);
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		// logger.info("----通道列表 end----- = {}
		// -----",returnJson.toJSONString());
		return commonService.getEncryptBody(returnJson);
	}

	@ResponseBody
	@RequestMapping(value = "/channelDetails")
	public JSONObject channelDetails(HttpServletRequest request) {
		logger.info("----通道列表详情 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("channelDetails json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");
			String agentId = json.getString("agentId");
			String channelType = json.getString("channelType"); // 支付类型 ：例如微信二维码
																// 300
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(agentId) && StringUtil.isNotBlank(channelType) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				Integer PayType = Integer.parseInt(channelType);
				UserSettlementConfig scg = configService.getByUserId(Long.parseLong(merId)); // 从用户费率对应表中获取费率
				List<PayTypeLimitConfig> paytypes = payTypeLimitConfigService.findPayTypeLimitConfigsFTC(PayType, agentId); // 获取通道限额配置信息（不包括小额）

				JSONArray ja = new JSONArray();
				for (PayTypeLimitConfig t : paytypes) {
					Map<String, String> m = new HashMap<String, String>();
					if (t.getPayType() == UserOrder.trans_type.YLZX.getCode() && t.getAmtType() == 100) {
						m.put("channelRate", scg.getInputFeeYinlian().toString());
						m.put("channelD0Rate", scg.getInputFeeD0Yinlian().toString());
					} else if (t.getPayType() == UserOrder.trans_type.YLZX.getCode() && t.getAmtType() == 200) {
						m.put("channelRate", scg.getInputFeeBigYinlian().toString());
						m.put("channelD0Rate", scg.getInputFeeD0Yinlian().toString());
					} else if (t.getPayType() == UserOrder.trans_type.YLZX.getCode() && t.getAmtType() == 300) {
						m.put("channelRate", scg.getInputFeeZtYinlian().toString());
						m.put("channelD0Rate", scg.getInputFeeD0ZtYinlian().toString());
					} else if (t.getPayType() == UserOrder.trans_type.YLZXJ.getCode() && t.getAmtType() == 300) { // 银联积分
						m.put("channelRate", scg.getInputFeeZtYinlianJf().toString());
						m.put("channelD0Rate", scg.getInputFeeD0ZtYinlianJf().toString());
					} else if (t.getPayType() == UserOrder.trans_type.YLZXJYB.getCode() && t.getAmtType() == 300) { // 银联积分--易宝
						// 使用的费率和银联积分相同
						m.put("channelRate", scg.getInputFeeZtYinlianJf().toString());
						m.put("channelD0Rate", scg.getInputFeeD0ZtYinlianJf().toString());
					} else if (t.getPayType() == UserOrder.trans_type.YLZXJZY.getCode() && t.getAmtType() == 300) { // 银联积分--哲扬
						m.put("channelRate", scg.getInputFeeZtYinlianJfZY().toString());
						m.put("channelD0Rate", scg.getInputFeeD0ZtYinlianJfZY().toString());
					} else if (t.getPayType() == UserOrder.trans_type.YLZXE.getCode() && t.getAmtType() == 300) {
						m.put("channelRate", scg.getInputFeeZtYinlian().toString());
						m.put("channelD0Rate", scg.getInputFeeD0ZtYinlian().toString());
					} else if (t.getPayType() == UserOrder.trans_type.ALQR.getCode() && t.getAmtType() == 100) {
						m.put("channelRate", scg.getInputFeeAlipay().toString());
						m.put("channelD0Rate", scg.getInputFeeD0Alipay().toString());
					} else if (t.getPayType() == UserOrder.trans_type.ALQR.getCode() && t.getAmtType() == 200) {
						m.put("channelRate", scg.getInputFeeBigAlipay().toString());
						m.put("channelD0Rate", scg.getInputFeeD0BigAlipay().toString());
					} else if (t.getPayType() == UserOrder.trans_type.ALQR.getCode() && t.getAmtType() == 300) {
						m.put("channelRate", scg.getInputFeeZtAlipay().toString());
						m.put("channelD0Rate", scg.getInputFeeD0ZtAlipay().toString());
					} else if (t.getPayType() == UserOrder.trans_type.WXQR.getCode() && t.getAmtType() == 100) {
						m.put("channelRate", scg.getInputFeeWeixin().toString());
						m.put("channelD0Rate", scg.getInputFeeD0Weixin().toString());
					} else if (t.getPayType() == UserOrder.trans_type.WXQR.getCode() && t.getAmtType() == 200) {
						m.put("channelRate", scg.getInputFeeBigWeixin().toString());
						m.put("channelD0Rate", scg.getInputFeeD0BigWeixin().toString());
					} else if (t.getPayType() == UserOrder.trans_type.WXQR.getCode() && t.getAmtType() == 300) {
						m.put("channelRate", scg.getInputFeeZtWeixin().toString());
						m.put("channelD0Rate", scg.getInputFeeD0ZtWeixin().toString());
					}

					else if (t.getPayType() == UserOrder.trans_type.QQQR.getCode() && t.getAmtType() == 200) {
						m.put("channelRate", scg.getInputFeeBigQQzhifu().toString());
						m.put("channelD0Rate", scg.getInputFeeD0BigQQzhifu().toString());
					} else if (t.getPayType() == UserOrder.trans_type.QQQR.getCode() && t.getAmtType() == 300) {
						m.put("channelRate", scg.getInputFeeZtYinlian().toString());
						m.put("channelD0Rate", scg.getInputFeeD0ZtYinlian().toString());
					}

					else if (t.getPayType() == UserOrder.trans_type.JDQR.getCode() && t.getAmtType() == 100) {
						m.put("channelRate", scg.getInputFeeJingDong().toString());
						m.put("channelD0Rate", scg.getInputFeeD0JingDong().toString());
					} else if (t.getPayType() == UserOrder.trans_type.JDQR.getCode() && t.getAmtType() == 200) {
						m.put("channelRate", scg.getInputFeeBigJingDong().toString());
						m.put("channelD0Rate", scg.getInputFeeD0BigJingDong().toString());
					} else if (t.getPayType() == UserOrder.trans_type.BDQR.getCode() && t.getAmtType() == 100) {
						m.put("channelRate", scg.getInputFeeBaidu().toString());
						m.put("channelD0Rate", scg.getInputFeeD0Baidu().toString());
					} else if (t.getPayType() == UserOrder.trans_type.BDQR.getCode() && t.getAmtType() == 200) {
						m.put("channelRate", scg.getInputFeeBigBaidu().toString());
						m.put("channelD0Rate", scg.getInputFeeD0BigBaidu().toString());
					} else if (t.getPayType() == UserOrder.trans_type.YZFQR.getCode() && t.getAmtType() == 100) {
						m.put("channelRate", scg.getInputFeeYizhifu().toString());
						m.put("channelD0Rate", scg.getInputFeeD0Yizhifu().toString());
					} else if (t.getPayType() == UserOrder.trans_type.YZFQR.getCode() && t.getAmtType() == 200) {
						m.put("channelRate", scg.getInputFeeBigYizhifu().toString());
						m.put("channelD0Rate", scg.getInputFeeD0Yizhifu().toString());
					} else if (t.getPayType() == UserOrder.trans_type.YZFQR.getCode() && t.getAmtType() == 300) {
						m.put("channelRate", scg.getInputFeeYizhifu().toString());
						m.put("channelD0Rate", scg.getInputFeeD0Yizhifu().toString());
					}

					String withdrawTpey = null;
					if (t.getAmtType() == 300) { // 100 小额 200大额 300 直通车
						withdrawTpey = "1"; // 直通车 自动提现
					} else {
						withdrawTpey = "2"; // 非直通车 手动提现
					}

					Map<String, String> D0 = new HashMap<String, String>();
					D0.put("payType", t.getPayType().toString()); // 通道限额配置中的交易支付类型-微信二维码=300-和请求参数中一致
					D0.put("AmtType", t.getAmtType().toString()); // 大小额类型-100=小额-200=大额-300=直通车
					D0.put("withdrawTpey", withdrawTpey); // 直通车 /非直通车
					D0.put("maxAmt", t.getMaxAmt().toString()); // 单笔最大金额
					D0.put("minAmt", t.getMinAmt().toString()); // 单笔最小金额
					D0.put("srvFee", t.getSrvFee().toString()); // 单笔服务费
					D0.put("channelName", t.getSltType()); // 默认为D0
					D0.put("channelCode", t.getCode());
					D0.put("unSupportCardName", t.getUnSupportCardName());
					D0.put("payTypeName", t.getPayTypeName());
					D0.put("startTime", t.getStartTime());
					D0.put("endTime", t.getEndTime());

					D0.put("hotType", t.getHotType());
					D0.put("channelRate", m.get("channelD0Rate")); // 用户费率对应表中，用户对应的D0费率
					if (t.getPayType() != 500 || t.getAmtType() == 300) { // 当为非银联在线
																			// 或者
																			// 为直通车时
						if (t.getAmtType() == 300) { // 当为直通车类型时

							if (t.getPayType() == 200 || t.getPayType() == 300 || t.getPayType() == 1100) { // 支付宝或者微信或者翼支付
								if (holidayService.getisThroughD0Work()) { // 系统参数--D0直通车微信支付宝
																			// 开关
																			// 1关闭(false)
																			// 0开启(true)
									ja.add(D0);
								}
							} else {
								if (holidayService.getisThroughYLD0Work()) { // 系统参数--D0直通车银联0
																				// 开启(true)
																				// 1关闭(false)
									ja.add(D0);
								}
							}

						} else { // 非直通车类型
							ja.add(D0);
						}
					} else {
						// if (!agentId.equals("F20160015") &&
						// holidayService.getisZTCD0Work()) { 为金钱龟管家
						if (agentId.equals("F20160015") || holidayService.getisZTCD0Work()) { // update：2017.11.24
																								// 金钱龟管家开启D0银联手动
																								// D0银联收款支付
																								// 0关闭
																								// (false)
																								// 1开启(true)
							ja.add(D0);
						}
					}

					if (agentId != null) {
						Map<String, String> D1 = new HashMap<String, String>();
						D1.put("payType", t.getPayType().toString());
						D1.put("AmtType", t.getAmtType().toString());
						D1.put("withdrawTpey", withdrawTpey);
						D1.put("maxAmt", t.getMaxAmt().toString());
						D1.put("minAmt", t.getMinAmt().toString());
						D1.put("srvFee", t.getSrvFee().toString()); // 单笔服务费
						D1.put("channelName", t.getSltType()); // 默认为D1
						D1.put("channelCode", t.getCode());
						D1.put("unSupportCardName", t.getUnSupportCardName());
						D1.put("payTypeName", t.getPayTypeName());
						D1.put("startTime", t.getStartTime());
						D1.put("endTime", t.getEndTime());
						D1.put("hotType", t.getHotType());
						D1.put("channelRate", m.get("channelRate")); // 用户费率表中

						if (t.getAmtType() != 300) { // 非直通车
							ja.add(D1);
						} else {
							if (t.getPayType() != 500 && t.getPayType() != 550 && t.getPayType() != 551 && t.getPayType() != 552 && t.getPayType() != 520) { // 非银联在线、非银联小额、非银联积分、非银联积分易宝
								if (t.getAmtType() == 300) {
									if (holidayService.getisThroughT1Work()) { // D1直通车收款支付1关闭(false)0开启(true)
										ja.add(D1);
									}
								} else { // 非直通车
									ja.add(D1);
								}
							}
						}
					}
				}
				returnJson.put("channelInfo", ja);
				returnJson.put("t5Switch", paramService.searchSysParameter().get("t5_swift_on")); // T5开关
																									// 0关闭1开启
				returnJson.put("t8Switch", paramService.searchSysParameter().get("t8_swift_on")); // T8开关
																									// 0关闭1开启
				// 为每个通道设置单独的T10开关
				if (PayType == 580) {
					returnJson.put("t10Switch", paramService.searchSysParameter().get("yinlian_t10_swift_on"));// 银联T10开关
				} else if (PayType == UserOrder.trans_type.ALQR.getCode()) {
					returnJson.put("t10Switch", paramService.searchSysParameter().get("alipay_t10_swift_on"));// 支付宝T10开关
				} else if (PayType == UserOrder.trans_type.WXQR.getCode()) {
					returnJson.put("t10Switch", paramService.searchSysParameter().get("weixin_t10_swift_on"));// 微信T10开关
				} else if (PayType == UserOrder.trans_type.JDQR.getCode()) {
					returnJson.put("t10Switch", paramService.searchSysParameter().get("jingdong_t10_swift_on"));// 京东T10开关
				} else if (PayType == UserOrder.trans_type.YZFQR.getCode()) {
					returnJson.put("t10Switch", paramService.searchSysParameter().get("yizhifu_t10_swift_on"));// 翼支付T10开关
				} else if (PayType == UserOrder.trans_type.QQQR.getCode()) {
					returnJson.put("t10Switch", paramService.searchSysParameter().get("qqqianbao_t10_swift_on"));// QQ钱包T10开关
				} else {
					returnJson.put("t10Switch", paramService.searchSysParameter().get("t10_swift_on")); // T10开关
				}
				returnJson.put("t58MinType", paramService.searchSysParameter().get("t58MinType")); // T5
																									// T8
																									// 小额
																									// 0关闭1开启
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----通道列表详情  end = {} -----", returnJson.toJSONString());
		return commonService.getEncryptBody(returnJson);
	}

	@ResponseBody
	@RequestMapping(value = "/getTextList")
	public JSONObject getTextList(HttpServletRequest request) {
		logger.info("----公告列表 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("getTextList json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");
			String agentId = json.getString("agentId");
			logger.info("userId=" + merId + "   token=" + token);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(agentId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				returnJson.put("channelInfo", sysInformationPhotoService.dataGridApp(agentId));
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----公告列表  end-----");
		return commonService.getEncryptBody(returnJson);
	}

	@ResponseBody
	@RequestMapping(value = "/DisplaySwitch")
	public JSONObject DisplaySwitch(HttpServletRequest request) {
		logger.info("----IOS发版开关 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("DisplaySwitch json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");
			String agentId = json.getString("agentId");
			String versionId = json.getString("versionId");
			logger.info("userId=" + merId + "   token=" + token);
			String flg = "0000";
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(agentId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				if (agentId.equals("F20160015")) {// 金钱龟
					boolean bTargetVersion = false;
					SysParameter param = paramService.getByName("F20160015_ios_hiden_version");
					if (versionId != null && param != null && versionId.equals(param.getParaValue())) {
						logger.info("F20160015 versionId={},paraValue={}", versionId, param.getParaValue());
						bTargetVersion = true;
					}
					if (paramService.getThrough("F20160015_ios_show_switch_on") == false && bTargetVersion) {// 关闭
						flg = "9999";
					}
				} else if (agentId.equals("F20160001")) {// 宝贝钱袋
					boolean bTargetVersion = false;
					SysParameter param = paramService.getByName("F20160001_ios_hiden_version");
					if (versionId != null && param != null && versionId.equals(param.getParaValue())) {
						logger.info("F20160001 versionId={},paraValue={}", versionId, param.getParaValue());
						bTargetVersion = true;
					}
					if (paramService.getThrough("F20160001_ios_show_switch_on") == false && bTargetVersion) {// 关闭
						flg = "9999";
					}
				} else if (agentId.equals("F20160003")) {// 云付钱袋
					boolean bTargetVersion = false;
					SysParameter param = paramService.getByName("F20160003_ios_hiden_version");
					if (versionId != null && param != null && versionId.equals(param.getParaValue())) {
						logger.info("F20160003 versionId={},paraValue={}", versionId, param.getParaValue());
						bTargetVersion = true;
					}
					if (paramService.getThrough("F20160003_ios_show_switch_on") == false && bTargetVersion) {// 关闭
						flg = "9999";
					}
				} else if (agentId.equals("F20160004")) {// 宝库钱袋
					boolean bTargetVersion = false;
					SysParameter param = paramService.getByName("F20160004_ios_hiden_version");
					if (versionId != null && param != null && versionId.equals(param.getParaValue())) {
						logger.info("F20160004 versionId={},paraValue={}", versionId, param.getParaValue());
						bTargetVersion = true;
					}
					if (paramService.getThrough("F20160004_ios_show_switch_on") == false && bTargetVersion) {// 关闭
						flg = "9999";
					}
				} else if (agentId.equals("F20160010")) {// 天一猫钱袋
					boolean bTargetVersion = false;
					SysParameter param = paramService.getByName("F20160010_ios_hiden_version");
					if (versionId != null && param != null && versionId.equals(param.getParaValue())) {
						logger.info("F20160010 versionId={},paraValue={}", versionId, param.getParaValue());
						bTargetVersion = true;
					}
					if (paramService.getThrough("F20160010_ios_show_switch_on") == false && bTargetVersion) {// 关闭
						flg = "9999";
					}
				} else if (agentId.equals("F20160011")) {// 云汇宝
					boolean bTargetVersion = false;
					SysParameter param = paramService.getByName("F20160011_ios_hiden_version");
					if (versionId != null && param != null && versionId.equals(param.getParaValue())) {
						logger.info("F20160011 versionId={},paraValue={}", versionId, param.getParaValue());
						bTargetVersion = true;
					}
					if (paramService.getThrough("F20160011_ios_show_switch_on") == false && bTargetVersion) {// 关闭
						flg = "9999";
					}
				} else if (agentId.equals("F20180002")) {// 多联宝
					boolean bTargetVersion = false;
					SysParameter param = paramService.getByName("F20180002_ios_hiden_version");
					if (versionId != null && param != null && versionId.equals(param.getParaValue())) {
						logger.info("F20180002 versionId={},paraValue={}", versionId, param.getParaValue());
						bTargetVersion = true;
					}
					if (paramService.getThrough("F20180002_ios_show_switch_on") == false && bTargetVersion) {// 关闭
						flg = "9999";
					}
				}else if (agentId.equals("F20160017")) {// 夏商云联
					boolean bTargetVersion = false;
					SysParameter param = paramService.getByName("F20160017_ios_hiden_version");
					if (versionId != null && param != null && versionId.equals(param.getParaValue())) {
						logger.info("F20160017 versionId={},paraValue={}", versionId, param.getParaValue());
						bTargetVersion = true;
					}
					if (paramService.getThrough("F20160017_ios_show_switch_on") == false && bTargetVersion) {// 关闭
						flg = "9999";
					}
				}
				returnJson.put("flg", flg);
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----IOS发版开关 end-----");
		return commonService.getEncryptBody(returnJson);
	}

	@ResponseBody
	@RequestMapping(value = "/getBannerInfoData")
	public JSONObject getBannerInfoData(HttpServletRequest request) {
		logger.info("----Banner start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("getBannerInfoData json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");
			String agentId = json.getString("agentId");
			PageFilter ph = new PageFilter();
			ph.setOrder("desc");
			ph.setSort("id");

			logger.info("userId=" + merId + "   agentId=" + agentId);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(agentId) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				BannerImage param = new BannerImage();
				param.setCode(agentId);
				param.setStatus("1");
				List<BannerImage> bannerImages = bannerImageService.dataGrid(param, ph);
				JSONArray array = new JSONArray();
				for (BannerImage banner : bannerImages) {
					JSONObject j = new JSONObject();
					j.put("name", banner.getName());
					j.put("imgUrl", banner.getImgUrl());
					j.put("actionUrl", banner.getActionUrl());
					array.add(j);
				}
				returnJson.put("banners", array);
				returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
			} else {
				returnJson.put("respCode", GlobalConstant.RESP_CODE_008);
				returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_008));
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----Banner  end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 修改隐私开关状态
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/setPrivacyType")
	public JSONObject setPrivacyType(HttpServletRequest request) {
		logger.info("----更改隐私权限开关 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("setPrivacyType json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");
			String privacyType = json.getString("privacyType");
			logger.info("userId = " + merId + ",privacyType = " + privacyType);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(privacyType) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				boolean isSuccess = userService.updatePrivacyType(Long.parseLong(merId), Integer.parseInt(privacyType));
				if (isSuccess) {
					returnJson.put("privacyType", privacyType);
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				} else {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				}
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----更改隐私权限开关  end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	/**
	 * 修改一键代还状态
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/setLoanType")
	public JSONObject setLoanType(HttpServletRequest request) {
		logger.info("----更改一键代还开关 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("setLoanType json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");
			String loanType = json.getString("loanType");
			logger.info("userId = " + merId + ",loanType = " + loanType);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(loanType) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				boolean isSuccess = userService.updateLoanType(Long.parseLong(merId), Integer.parseInt(loanType));
				if (isSuccess) {
					returnJson.put("loanType", loanType);
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				} else {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				}
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----更改一键代还开关  end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);
	}

	@ResponseBody
	@RequestMapping(value = "/setSpeech")
	public JSONObject setSpeech(HttpServletRequest request) {
		logger.info("----更改语音播报方式 start-----");
		JSONObject returnJson = new JSONObject();
		try {
			JSONObject json = commonService.getRequstBody(request);
			logger.info("setSpeech json={}", json.toJSONString());
			String token = request.getHeader("token");
			String merId = json.getString("merId");
			String speechType = json.getString("speechType");
			logger.info("userId = " + merId + ",speechType = " + speechType);
			if (StringUtil.isNotBlank(merId) && StringUtil.isNotBlank(speechType) && StringUtil.isNotBlank(token) && userTokenService.isLegalUserToken(Long.parseLong(merId), token)) {
				boolean isSuccess = userService.updateSpeechType(Long.parseLong(merId), speechType);
				if (isSuccess) {
					User user = userService.get(Long.parseLong(merId));
					returnJson.put("speechType", user.getSpeechType());
					returnJson.put("respCode", GlobalConstant.RESP_CODE_SUCCESS);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				} else {
					returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
					returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_SUCCESS));
				}
			}
		} catch (Exception e) {
			returnJson.put("respCode", GlobalConstant.RESP_CODE_999);
			returnJson.put("respDesc", GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
			e.printStackTrace();
		}
		logger.info("----更改语音播报方式  end-----" + returnJson);
		return commonService.getEncryptBody(returnJson);

	}
}
