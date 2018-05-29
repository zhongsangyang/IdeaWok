package com.cn.flypay.service.sys.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TYiQiangMerchantReport;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.model.sys.TuserMerchantConfig;
import com.cn.flypay.model.sys.TuserMerchantReport;
import com.cn.flypay.model.sys.TweiLianBaoMerchantReport;
import com.cn.flypay.model.util.JSON;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.ServiceMerchantService;
import com.cn.flypay.service.sys.UserMerchantConfigService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.weilianbao.WeiLianBaoPayUtil;

@Service
public class UserMerchantConfigServiceImpl implements UserMerchantConfigService {

	private static Logger LOG = LoggerFactory.getLogger(UserMerchantConfigServiceImpl.class);

	@Autowired
	private ServiceMerchantService serviceMerchantService;
	@Autowired
	private ChannelPaymentService pinganPaymentService;
	@Autowired
	private ChannelPaymentService minshengPaymentService;
	@Autowired
	private ChannelPaymentService xinkePaymentService;
	@Autowired
	private ChannelPaymentService shenFuPaymentService;
	@Autowired
	private ChannelPaymentService weiLianBaoPaymentService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private BaseDao<TuserMerchantConfig> userMerchantConfigDao;
	@Autowired
	private BaseDao<TuserCard> userCarDao;
	@Autowired
	private UserService userService;
	@Autowired
	UserSettlementConfigService userSettlementConfigService;

	@Override
	public List<UserMerchantConfig> dataGrid(UserMerchantConfig param, PageFilter ph) {
		List<UserMerchantConfig> ul = new ArrayList<UserMerchantConfig>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " select t from TuserMerchantConfig t left join t.user u left join u.organization g left join t.serviceMerchant sm ";
		List<TuserMerchantConfig> l = userMerchantConfigDao.find(hql + whereHql(param, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		for (TuserMerchantConfig t : l) {
			UserMerchantConfig u = new UserMerchantConfig();
			BeanUtils.copyProperties(t, u);
			if (t.getUser() != null) {
				u.setRealName(t.getUser().getRealName());
				u.setLoginName(t.getUser().getLoginName());
				u.setMerchantName(t.getUser().getMerchantName());
				u.setMerchantShortName(t.getUser().getMerchantShortName());
				u.setAddress(t.getUser().getAddress());
				Torganization org = t.getUser().getOrganization();
				if (org != null) {
					u.setOrganizationName(org.getName());
				}
			}
			if (t.getServiceMerchant() != null) {
				u.setServiceMerchantDetailName(t.getServiceMerchant().getDetailName());
				u.setServiceMerchantName(t.getServiceMerchant().getName());
			}
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(UserMerchantConfig param, PageFilter ph) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = " from TuserMerchantConfig t left join t.user u left join u.organization g  left join t.serviceMerchant sm ";
		return userMerchantConfigDao.count("select count(t.id) " + hql + whereHql(param, params), params);
	}

	private String whereHql(UserMerchantConfig param, Map<String, Object> params) {
		String hql = "";
		if (param != null) {
			hql += " where 1=1 ";
			if (StringUtil.isNotBlank(param.getLoginName())) {
				hql += " and u.loginName = :loginName";
				params.put("loginName", param.getLoginName());
			}
			if (param.getOperateUser() != null) {
				hql += " and  g.id in(:operaterOrgIds)";
				params.put("operaterOrgIds", organizationService.getOwerOrgIds(param.getOperateUser().getOrganizationId()));
			}
		}
		return hql;
	}

	private String orderHql(PageFilter ph) {
		String orderString = "";
		if ((ph.getSort() != null) && (ph.getOrder() != null)) {
			orderString = " order by t." + ph.getSort() + " " + ph.getOrder();
		}
		return orderString;
	}

	@Override
	public void edit(UserMerchantConfig param) {
		// TODO Auto-generated method stub

	}

	@Override
	public UserMerchantConfig get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createUserMerchants(Tuser user) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("merchantName", user.getMerchantName());
		params.put("shortName", user.getMerchantShortName());
		params.put("address", user.getAddress());
		List<ServiceMerchant> sml = serviceMerchantService.findAllServiceMerchant();
		for (ServiceMerchant sm : sml) {
			UserMerchantConfig umc = null;
			if ("PINGANPAY".equals(sm.getName())) {
				umc = pinganPaymentService.createSubMerchant(sm, params);
			} else if ("MINSHENG".equals(sm.getName())) {
				umc = minshengPaymentService.createSubMerchant(sm, params);
			} else if ("XINKE".equals(sm.getName())) {
				umc = xinkePaymentService.createSubMerchant(sm, params);
			}
			if (umc != null) {
				TuserMerchantConfig tmc = new TuserMerchantConfig();
				BeanUtils.copyProperties(umc, tmc);
				tmc.setServiceMerchant(serviceMerchantService.getTserviceMerchant(umc.getServiceMerchantId()));
				tmc.setUser(user);
				userMerchantConfigDao.save(tmc);
			}
		}
	}

	@Override
	public TuserMerchantReport createShenFuUserMerchants(Tuser user) {
		Map<String, Object> argu = new HashMap<String, Object>();
		argu.put("userID", user.getId());
		String userHql = "select ut from TuserCard ut left join ut.user u left join ut.bank b where ut.status=0 and ut.isSettlmentCard=1 and u.id = :userID ";
		List<TuserCard> userCards = userCarDao.find(userHql, argu);
		if (userCards == null || userCards.isEmpty()) {
			LOG.info("SHENFU register Merchant Failed, Cant Found Settle Card userId={},loginName={}", user.getId(), user.getLoginName());
			// TODO 可删除
			// user.setIsShenfuD0Open("9");
			// user.setShenfuD0Msg("找不到结算卡");
			// userService.updateTuser(user);
			return null;
		}
		LOG.info("SHENFU register Merchant userId={},AppLogin={},realName={},userType={},idNo={},SettlCardSize={}",
				new Object[] { user.getId(), user.getLoginName(), user.getRealName(), user.getUserType(), user.getIdNo(), userCards.size() });

		TuserCard userCard = (TuserCard) userCards.get(0);
		Map<String, String> params = new HashMap<String, String>();
		// TODO 新增参数
		params.put("userId", user.getId().toString());
		params.put("mercnam", StringUtil.isNotEmpty(user.getMerchantName()) ? user.getMerchantName() + "M" : user.getName() + "M");// 商户名
		params.put("merctel", user.getLoginName());// 商户电话
		params.put("mercemail", user.getLoginName() + "@qq.com");// 商户邮箱
		params.put("legalperson", user.getRealName());// 法人姓名
		params.put("corporateidentity", user.getIdNo());// 法人身份证
		params.put("actname", user.getRealName());// 开卡人名称
		params.put("actno", userCard.getCardNo());// 结算卡号
		UserSettlementConfig config = userSettlementConfigService.getByUserId(user.getId());
		String feerat = config.getInputFeeYinlian().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString();
		String d0feerat = config.getInputFeeD0Yinlian().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString();
		params.put("feerat", feerat);
		params.put("crefeerat", feerat);
		params.put("d0feerat", d0feerat);
		LOG.info("Register ShenFu D0 params={}", JSON.getDefault().toJSONString(params));
		List<ServiceMerchant> sml = serviceMerchantService.findAllServiceMerchant("SHENFU");
		for (ServiceMerchant sm : sml) {
			UserMerchantConfig umc = null;
			if ("SHENFU".equals(sm.getName())) {
				umc = shenFuPaymentService.createSubMerchant(sm, params);
			}
			if (umc != null) {
				return umc.getReport();
			}
			return null;
		}
		return null;
	}

	@Override
	public TweiLianBaoMerchantReport createWeiLianBaoUserMerchants(Tuser user) {
		Map<String, Object> argu = new HashMap<String, Object>();
		argu.put("userID", user.getId());
		String userHql = "select ut from TuserCard ut left join ut.user u left join ut.bank b where ut.status=0 and ut.isSettlmentCard=1 and u.id = :userID ";
		List<TuserCard> userCards = userCarDao.find(userHql, argu);
		if (userCards == null || userCards.isEmpty()) {
			LOG.info("WEILIANBAO register Merchant Failed, Cant Found Settle Card userId={},loginName={}", user.getId(), user.getLoginName());
			// 可删除
			// user.setIsShenfuD0Open("9");
			// user.setShenfuD0Msg("找不到结算卡");
			// userService.updateTuser(user);
			return null;
		}
		LOG.info("WEILIANBAO register Merchant userId={},AppLogin={},realName={},userType={},idNo={},SettlCardSize={}",
				new Object[] { user.getId(), user.getLoginName(), user.getRealName(), user.getUserType(), user.getIdNo(), userCards.size() });

		TuserCard userCard = (TuserCard) userCards.get(0);
		Map<String, String> params = new HashMap<String, String>();
		// TODO 新增参数
		params.put("channelNo", WeiLianBaoPayUtil.CHANNEL_NO);
		params.put("channelName", WeiLianBaoPayUtil.CHANNEL_NAME);
		params.put("userId", user.getId().toString());
		params.put("loginName", user.getLoginName());
		params.put("merchantName", StringUtil.isNotEmpty(user.getMerchantName()) ? user.getMerchantName() : user.getName());// 商户名

		params.put("merchantBillName", params.get("merchantName"));// 签购单显示名称
		params.put("installProvince", user.getProvince());// 安装归属省
		params.put("installCity", user.getCity());// 安装归属市
		params.put("installCounty", user.getCountry());// 安装归属县
		params.put("operateAddress", user.getAddress());// 经营地址
		params.put("merchantType", "PERSON");// ENTERPRISE -企业商户 INSTITUTION
												// -事业单位商户 INDIVIDUALBISS -个体工商户
												// PERSON -个人商户
		// params.put("businessLicense", "");// 营业执照号码
		// params.put("business_license_type", "");// 营业执照类别
		params.put("isOneOrBig", "Y");// 是否一户一码
		// params.put("appid", "");//
		params.put("legalPersonName", user.getRealName());// 法人代表姓名
		params.put("legalPersonID", user.getIdNo());// 法人代表身份证号
		params.put("merchantPersonType", "LEGAL_PERSON");// 联系人类型
															// LEGAL_PERSON：法人；CONTROLLER：实际控制人；AGENT：代理人；OTHER：其他
		params.put("merchantPersonName", user.getRealName());// 商户联系人姓名
		params.put("merchantPersonPhone", userCard.getPhone());// 商户联系人电话
		// params.put("merchantPersonEmail", "");// 商户联系人邮箱 可不传，若传，全局唯一
		params.put("wxType", "204");// 微信分类类目
		params.put("wxT1Fee", "0.0038");// 微信商户手T1续费
		params.put("wxT0Fee", "0.0038");// 微信商户手T0续费
		params.put("alipayType", "2015091000052157");// 支付宝经营类目
		params.put("alipayT1Fee", "0.0038");// 支付宝商户手T1续费
		params.put("alipayT0Fee", "0.0038");// 支付宝商户手T0续费
		
		UserSettlementConfig  userSettlementConfig = userSettlementConfigService.getByUserId(user.getId());
		BigDecimal t1JFFee =  userSettlementConfig.getInputFeeZtYinlianJf();
		BigDecimal t0JFFee =   userSettlementConfig.getInputFeeD0ZtYinlianJf();
		params.put("t0Fee", t0JFFee.toString());// 银联积分T0续费
		params.put("t1Fee", t1JFFee.toString());// 银联积分T1续费
		
		params.put("province_code", "310000");// 支付宝省代码
		params.put("city_code", "310100");// 支付宝市代码
		params.put("district_code", "310115");// 支付宝区代码
		params.put("bankType", "TOPRIVATE");// 结算账户性质 对公-TOPUBLIC 对私-TOPRIVATE
		params.put("accountName", user.getRealName());// 开户名称
		params.put("accountNo", userCard.getCardNo());// 开户账号
		params.put("bankName", userCard.getBranchName());// 开户银行名（大行全称）
		params.put("bankProv", "上海");// 开户行省
		params.put("bankCity", "上海市");// 开户行市
		params.put("bankBranch", userCard.getBranchName());// 开户银行名称（精确到支行）
		params.put("bankCode", userCard.getBank().getCode());// 联行号
		List<ServiceMerchant> sml = serviceMerchantService.findAllServiceMerchant("WEILIANBAO");
		for (ServiceMerchant sm : sml) {
			UserMerchantConfig umc = null;
			if ("WEILIANBAO".equals(sm.getName())) {
				umc = weiLianBaoPaymentService.createSubMerchant(sm, params);
			}
			if (umc != null) {
				return umc.getWeiLianBaoMerchantReport();
			}
			return null;
		}
		return null;
	}

	@Override
	public TYiQiangMerchantReport createYiQiangUserMerchants(Tuser user) {
		return null;
	}

	public static void name(String[] args) {

	}

}
