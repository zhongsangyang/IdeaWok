package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.TuserMerchantConfig;
import com.cn.flypay.model.util.JSON;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantQueryRequest;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.UserChannelService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.pingan.PinganPaymentUtil;

@Service
public class RouteServiceImpl implements RouteService {
	private Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	private BaseDao<Tchannel> channelDao;
	@Autowired
	private BaseDao<TuserMerchantConfig> userMerchantConfigDao;

	@Autowired
	private ChannelService channelService;
	@Autowired
	private ChannelPaymentService weixinPaymentService;
	@Autowired
	private ChannelPaymentService alipayPaymentService;
	@Autowired
	private ChannelPaymentService minshengPaymentService;
	@Autowired
	private ChannelPaymentService zanshanfuPayment_V2_Service;
	@Autowired
	private ChannelPaymentService pinganPaymentService;
	@Autowired
	private ChannelPaymentService xinkePaymentService;
	@Autowired
	private ChannelPaymentService shenFuPaymentService;
	@Autowired
	private ChannelPaymentService yfyianZXPaymentService;
	@Autowired
	private ChannelPaymentService yfyianZXXEPaymentService;
	@Autowired
	private ChannelPaymentService yiBaoPaymentService;
	@Autowired
	private ChannelPaymentService gaZhiYinLianPaymentService;
	@Autowired
	private ChannelPaymentService weiLianBaoPaymentService;
	@Autowired
	private ChannelPaymentService xinkeYinLianPaymentService;
	@Autowired
	private ChannelPaymentService quanTongPaymentService;
	@Autowired
	private ChannelPaymentService gaZhiPaymentService;
	@Autowired
	private ChannelPaymentService zheYangPaymentService;
	@Autowired
	private ChannelPaymentService transfarPaymentService;
	@Autowired
	private ChannelPaymentService yiQiangPaymentService;
	@Autowired
	private ChannelPaymentService yiQiang2PaymentService;
	@Autowired
	private UserChannelService userChannelService;

	@Autowired
	private UserService userService;

	private Tchannel getTchannel(Integer transType, BigDecimal tradeAmt, Integer userType, Long userId, Integer limitType) {
		Map<String, Object> params = new HashMap<String, Object>();
		/*
		 * 通道开启，支付类型一致，今日输入金额小于最大金额， 交易金额在通道的最大与最小阈值之间，按照ID的顺序排列。
		 */
		String hql = "select t from Tchannel t  where t.status=0 and t.type=:type and t.userType in(:userType) and t.todayAmt<=t.maxAmtPerDay and t.maxChannelAmt>=:tradeAmt and t.minChannelAmt<=:tradeAmt   and t.limitType in(:limitType)     order by t.seq , t.userNum";
		params.put("type", transType);
		params.put("tradeAmt", tradeAmt);
		params.put("userType", getChannelUserTypes(userType));
		Set<Integer> limitTypes = new HashSet<Integer>();
		limitTypes.add(0);
		limitTypes.add(limitType);
		params.put("limitType", limitTypes);

		LOG.info("Test Collection code ScanCode params={}", JSON.getDefault().toJSONString(params));
		List<Tchannel> cls = channelDao.find(hql, params);
		for (Tchannel cl : cls) {
			if (userChannelService.updateUserChannel(userId, cl.getId())) {
				LOG.info("类型" + transType + " 交易金额" + tradeAmt + " 选择的通道为" + cl.getDetailName());

				// 校验平安通道的可用性(不包括翼支付) --start
				// 因平安报备商户可能存在报备的id和实际姓名不对照，校验平安通道是否可用 at 2017-11-16 by
				// liangchao
				if (cl.getName().equals("PINGANPAY") && cl.getType() != 1100) {
					JSONObject config = JSONObject.parseObject(cl.getConfig());
					String appId = config.getString("pingan.appId");
					String merId = config.getString("pingan.merchant_id");
					FshowsLiquidationSubmerchantQueryRequest req = new FshowsLiquidationSubmerchantQueryRequest();
					req.setSub_merchant_id(merId);
					JSONObject validatInfo = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantQueryRequest.class).getJSONObject("return_value");
					if (!validatInfo.getString("service_phone").equals("13052222696") || !(validatInfo.getString("name").equals(cl.getDetailName()) // 商户全称
							|| validatInfo.getString("alias_name").equals(cl.getDetailName()) // 商户简称
					)) {
						LOG.error("平安支付通道id=" + cl.getId() + "通道的报备出错，detail_name和平安存储信息不对称，该通道不可用。");
						channelService.updateStatus(cl.getId(), 1); // 设置通道为失效
						continue;
					}
				}
				// 校验平安通道的可用性 --end

				// 增加使用次数记录
				if (cl.getUserNum() == null) {
					cl.setUserNum(1l);
				} else {
					cl.setUserNum(cl.getUserNum() + 1);
				}
				channelDao.update(cl);
				return cl;
			}
		}
		LOG.error("类型" + transType + " 交易金额" + tradeAmt + " 没有合适的通道");
		return null;
	}

	private Tchannel getTchannel(Integer transType, BigDecimal tradeAmt, Integer userType, Long userId, Integer limitType, String channelCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		/*
		 * 通道开启，支付类型一致，今日输入金额小于最大金额， 交易金额在通道的最大与最小阈值之间，按照ID的顺序排列。
		 */
		String hql = " select t from Tchannel t where t.name=:name and t.type=:type and t.userType in(:userType) and t.todayAmt<=t.maxAmtPerDay and t.maxChannelAmt>=:tradeAmt and t.minChannelAmt<=:tradeAmt   and t.limitType in(:limitType)     order by t.seq , t.userNum";
		params.put("type", transType);
		params.put("name", channelCode);
		params.put("tradeAmt", tradeAmt);
		params.put("userType", getChannelUserTypes(userType));
		Set<Integer> limitTypes = new HashSet<Integer>();
		limitTypes.add(0);
		limitTypes.add(limitType);
		params.put("limitType", limitTypes);

		LOG.info("Test Collection code ScanCode params={}", JSON.getDefault().toJSONString(params));
		List<Tchannel> cls = channelDao.find(hql, params);
		for (Tchannel cl : cls) {
			if (userChannelService.updateUserChannel(userId, cl.getId())) {
				LOG.info("类型" + transType + " 交易金额" + tradeAmt + " 选择的通道为" + cl.getDetailName());

				// 校验平安通道的可用性(不包括翼支付) --start
				// 因平安报备商户可能存在报备的id和实际姓名不对照，校验平安通道是否可用 at 2017-11-16 by
				// liangchao
				if (cl.getName().equals("PINGANPAY") && cl.getType() != 1100) {
					JSONObject config = JSONObject.parseObject(cl.getConfig());
					String appId = config.getString("pingan.appId");
					String merId = config.getString("pingan.merchant_id");
					FshowsLiquidationSubmerchantQueryRequest req = new FshowsLiquidationSubmerchantQueryRequest();
					req.setSub_merchant_id(merId);
					JSONObject validatInfo = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantQueryRequest.class).getJSONObject("return_value");
					if (!validatInfo.getString("service_phone").equals("13052222696") || !(validatInfo.getString("name").equals(cl.getDetailName()) // 商户全称
							|| validatInfo.getString("alias_name").equals(cl.getDetailName()) // 商户简称
					)) {
						LOG.error("平安支付通道id=" + cl.getId() + "通道的报备出错，detail_name和平安存储信息不对称，该通道不可用。");
						channelService.updateStatus(cl.getId(), 1); // 设置通道为失效
						continue;
					}
				}
				// 校验平安通道的可用性 --end

				// 增加使用次数记录
				if (cl.getUserNum() == null) {
					cl.setUserNum(1l);
				} else {
					cl.setUserNum(cl.getUserNum() + 1);
				}
				channelDao.update(cl);
				return cl;
			}
		}
		LOG.error("类型" + transType + " 交易金额" + tradeAmt + " 没有合适的通道");
		return null;
	}

	private ChannelPayRef getMerchantChannelPayRef(Integer transType, BigDecimal tradeAmt, Integer userType, Long userId, Integer limitType) {
		Map<String, Object> params = new HashMap<String, Object>();
		String userMerchantConfigHql = "select t from TuserMerchantConfig t left join t.serviceMerchant m left join t.user u where m.status=1 and t.status=0 and u.id=:userId and t.type in(:type)";
		params.put("userId", userId);
		params.put("type", getMerchantConfigTypes(transType));
		List<TuserMerchantConfig> tmcs = userMerchantConfigDao.find(userMerchantConfigHql, params);
		for (TuserMerchantConfig tmc : tmcs) {
			params.clear();
			/*
			 * 通道开启，支付类型一致，今日输入金额小于最大金额， 交易金额在通道的最大与最小阈值之间，按照ID的顺序排列。
			 */
			String hql = "select t from Tchannel t  where t.status=0 and t.type=:type and t.account=:account and t.userType in(:userType) and t.todayAmt<=t.maxAmtPerDay and t.maxChannelAmt>=:tradeAmt and t.minChannelAmt<=:tradeAmt and t.limitType in(:limitType)  order by t.seq";
			params.put("type", transType);
			params.put("account", tmc.getServiceMerchant().getAppId());
			params.put("tradeAmt", tradeAmt);
			params.put("userType", getChannelUserTypes(userType));
			Set<Integer> limitTypes = new HashSet<Integer>();
			limitTypes.add(0);
			limitTypes.add(limitType);
			params.put("limitType", limitTypes);
			List<Tchannel> cls = channelDao.find(hql, params);
			for (Tchannel cl : cls) {
				LOG.info("类型" + transType + " 交易金额" + tradeAmt + " 选择的商户通道为" + cl.getDetailName());
				ChannelPayRef cpr = new ChannelPayRef();

				Channel cnl = new Channel();
				BeanUtils.copyProperties(cl, cnl);
				cpr.setChannel(cnl);

				UserMerchantConfig umc = new UserMerchantConfig();
				BeanUtils.copyProperties(tmc, umc);
				cpr.setUserMerchantConfig(umc);
				return cpr;
			}

		}

		LOG.error("类型" + transType + " 交易金额" + tradeAmt + " 没有合适的通道");
		return null;
	}

	/**
	 * 按照支付类型，得到满足子商户支付条件的账户类型
	 * 
	 * @param transType
	 * @return
	 */
	@Cacheable(value = "routeCache", key = "#transType+'getMerchantConfigTypes'")
	private Object getMerchantConfigTypes(Integer transType) {
		Set<Integer> sg = new HashSet<>();
		sg.add(UserMerchantConfig.merchant_config_type.COMPOSITE.getCode());
		sg.add(transType);
		return sg;

	}

	@Override
	public ChannelPayRef checkChannelPayRoute(Integer transType, BigDecimal tradeAmt, Integer userType, Long userId, Integer limitType) {
		User user = userService.getSimpleUser(userId);
		ChannelPayRef cpr = null;
		/* 若用户为商家，首选商家通道 */
		// if (user.getMerchantType() ==
		// User.merchant_type.REAL_MERCHANT.getCode() || user.getMerchantType()
		// == User.merchant_type.NONE_MERCHANT.getCode()) {
		// cpr = getMerchantChannelPayRef(transType, tradeAmt, userType, userId,
		// limitType);
		// if (cpr != null) {
		// cpr.setChannelPaymentService(getPaymentServiceChannelName().get(cpr.getChannel().getName()));
		// cpr.setConfig(channelService.getChannelConfig(cpr.getChannel().getId()));
		// return cpr;
		// }
		// }
		/* 对于无商家通道或者 普通用户，按照默认的路由规则查询通道 */
		if (cpr == null) {
			Tchannel tcl = getTchannel(transType, tradeAmt, userType, userId, limitType);
			if (tcl != null) {
				Channel cnl = new Channel();
				BeanUtils.copyProperties(tcl, cnl);
				cpr = new ChannelPayRef();
				cpr.setChannel(cnl);
				cpr.setChannelPaymentService(getPaymentServiceChannelName().get(tcl.getName()));
				cpr.setConfig(channelService.getChannelConfig(tcl.getId()));
				return cpr;
			}
		}
		return null;
	}

	@Override
	public ChannelPayRef checkChannelPayRoute(Integer transType, BigDecimal tradeAmt, Integer userType, Long userId, Integer limitType, String channelCode) {
		User user = userService.getSimpleUser(userId);
		ChannelPayRef cpr = null;
		if (cpr == null) {
			Tchannel tcl = getTchannel(transType, tradeAmt, userType, userId, limitType, channelCode);
			if (tcl != null) {
				Channel cnl = new Channel();
				BeanUtils.copyProperties(tcl, cnl);
				cpr = new ChannelPayRef();
				cpr.setChannel(cnl);
				cpr.setChannelPaymentService(getPaymentServiceChannelName().get(tcl.getName()));
				cpr.setConfig(channelService.getChannelConfig(tcl.getId()));
				return cpr;
			}
		}
		return null;
	}

	// @Cacheable(value = "routeCache", key = "getDefaultChannelPayRoute")
	@Override
	public ChannelPayRef getDefaultChannelPayRoute(Integer transType) {
		Tchannel tcl = getDefaultTchannel(transType);
		if (tcl != null) {
			Channel cnl = new Channel();
			BeanUtils.copyProperties(tcl, cnl);
			ChannelPayRef cpr = new ChannelPayRef();
			cpr.setChannel(cnl);
			cpr.setChannelPaymentService(getPaymentServiceChannelName().get(tcl.getName()));
			cpr.setConfig(channelService.getChannelConfig(tcl.getId()));
			return cpr;
		}
		return null;
	}

	@Cacheable(value = "routeCache", key = "#userType+'getChannelUserTypes'")
	@Override
	public Set<Integer> getChannelUserTypes(Integer userType) {
		Set<Integer> sg = new HashSet<>();
		if (userType == 21) {
			sg.add(100);// 钻石会员
			sg.add(300);// 钻石和金牌会员
			sg.add(500);// 钻石和普通会员
			sg.add(700);// 所有用户
		} else if (userType == 22) {
			sg.add(200);// 金牌会员
			sg.add(300);// 钻石和金牌会员
			sg.add(600);// 金牌和普通
			sg.add(700);
		} else if (userType == 24) {
			sg.add(400);
			sg.add(500);
			sg.add(600);
			sg.add(700);
		} else {
			sg.add(700);
		}
		return sg;
	}

	private Tchannel getDefaultTchannel(Integer transType) {
		Map<String, Object> params = new HashMap<String, Object>();
		/*
		 * 通道开启，支付类型一致，今日输入金额小于最大金额， 交易金额在通道的最大与最小阈值之间，按照ID的顺序排列。
		 */
		String hql = "select t from Tchannel t  where t.status=0 and t.type=:type  order by t.seq";
		params.put("type", transType);
		List<Tchannel> cls = channelDao.find(hql, params);
		for (Tchannel cl : cls) {
			LOG.info("类型" + transType + " 默认选择的通道为" + cl.getDetailName());
			return cl;
		}
		LOG.error("类型" + transType + " 没有默认的通道");
		return null;
	}

	@Cacheable(value = "routeCache", key = "getPaymentServiceChannelName")
	private Map<String, ChannelPaymentService> getPaymentServiceChannelName() {
		Map<String, ChannelPaymentService> payMap = new HashMap<String, ChannelPaymentService>();
		payMap.put("WEIXIN", weixinPaymentService);
		payMap.put("ALIPAY", alipayPaymentService);
		payMap.put("ZANSHANFU", zanshanfuPayment_V2_Service);
		payMap.put("MINSHENG", minshengPaymentService);
		payMap.put("PINGANPAY", pinganPaymentService);
		payMap.put("XINKE", xinkePaymentService);
		payMap.put("SHENFU", shenFuPaymentService);
		payMap.put("SHENFUZTC", shenFuPaymentService);
		payMap.put("SHENFUDAIFU", shenFuPaymentService);
		payMap.put("YIQIANGZTC", yiQiangPaymentService);
		payMap.put("YIQIANG2ZTC", yiQiang2PaymentService);
		payMap.put("YIQIANG2JFZTC", yiQiang2PaymentService);
		payMap.put("TRANSFAR", transfarPaymentService);
		payMap.put("YILIANZHIFU", yfyianZXPaymentService); // 易联支付
		payMap.put("YILIANZHIFUZTC", yfyianZXXEPaymentService); // 易联支付直通车
		payMap.put("YILIANYINLIANJIFENZTC", yfyianZXXEPaymentService); // 新增易联银联积分大额D0直通车
																		// at
																		// 2017-11-09
																		// by
																		// liangchao
		payMap.put("YIBAOZHITONGCHE", yiBaoPaymentService); // 新增易宝银联积分大额D0直通车
															// at 2017-11-22 by
															// liangchao
		payMap.put("GAZHIYINLIANJIFENZHITONGCHE", gaZhiYinLianPaymentService); // 新增嘎吱银联积分
																				// at
																				// 2018-1-11
																				// by
																				// liangchao
		payMap.put("WEILIANBAOYINLIANJIFENZHITONGCHE", weiLianBaoPaymentService); 
		payMap.put("XINKEYINLIAN", xinkeYinLianPaymentService);
		payMap.put("QUANTONG", quanTongPaymentService);
		payMap.put("GAZHI", gaZhiPaymentService);
		payMap.put("ZHEYANGZTC", zheYangPaymentService);

		return payMap;
	}

	@Override
	public ChannelPaymentService getChannelPayRouteByChannelName(String channelName) {
		Map<String, ChannelPaymentService> map = getPaymentServiceChannelName();
		return map.get(channelName);
	}

	public ChannelPayRef getYLZXET1ChannelPayRoute(Integer transType, BigDecimal tradeAmt, Integer userType, Long userId, Integer limitType) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from Tchannel t  where t.name='SHENFUZTC' and t.type='520' order by t.seq";
		List<Tchannel> cls = channelDao.find(hql, params);
		if (cls == null || cls.size() != 1) {
			return null;
		}
		ChannelPayRef cpr = null;
		Tchannel tcl = cls.get(0);
		if (tcl != null) {
			Channel cnl = new Channel();
			BeanUtils.copyProperties(tcl, cnl);
			cpr = new ChannelPayRef();
			cpr.setChannel(cnl);
			cpr.setChannelPaymentService(getPaymentServiceChannelName().get(tcl.getName()));
			cpr.setConfig(channelService.getChannelConfig(tcl.getId()));
			return cpr;
		}
		return null;
	}

}
