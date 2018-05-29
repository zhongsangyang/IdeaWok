package com.cn.flypay.service.sys.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.TorgChannel;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.payment.minsheng.SMZF001;
import com.cn.flypay.pageModel.payment.pingan.FshowsAliPayMerchantCreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantBankBindRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantCreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantQueryRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxSubmerchantCreateSupplementRequest;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.service.payment.PingAnService;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.ThroughChannelService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.TroughUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.minsheng.MinShengMerchantInputMinShengUtil;
import com.cn.flypay.utils.pingan.PinganPaymentUtil;

import net.sf.json.JSONArray;

@Service
public class ThroughChannelServiceImpl implements ThroughChannelService {
	private Logger LOG = LoggerFactory.getLogger(ThroughChannelServiceImpl.class);

	@Autowired
	private RouteService routeService;

	@Autowired
	private BaseDao<Tchannel> channelDao;

	@Autowired
	private BaseDao<TorgChannel> orgChannelDao;

	@Autowired
	private BaseDao<Torganization> organizationDao;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private PingAnService pingAnService;
	public final static String Min_sheng_D0_cooperator = "SMZF_SHFF_HD_T0";

	@Override
	public List<Channel> dataGrid(Channel channel, PageFilter ph) {
		List<Channel> ul = new ArrayList<Channel>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from Tchannel t ";
		List<Tchannel> l = channelDao.find(hql + whereHql(channel, params) + orderHql(ph), params, ph.getPage(),
				ph.getRows());
		for (Tchannel t : l) {
			Channel u = new Channel();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}

	@Override
	public Long count(Channel channel, PageFilter pf) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "from Tchannel t ";
		return channelDao.count("select count(t.id) " + hql + whereHql(channel, params), params);
	}

	private String whereHql(Channel channel, Map<String, Object> params) {
		String hql = "";
		if (channel != null) {
			hql += " where 1=1";
			if (channel.getType() != null) {
				hql += " and t.type =:type";
				params.put("type", channel.getType());
			}
			if (channel.getStatus() != null) {
				hql += " and t.status =:status";
				params.put("status", channel.getStatus());
			}
			if (channel.getUserType() != null) {
				hql += " and t.userType in(:userType)";
				params.put("userType", routeService.getChannelUserTypes(channel.getUserType()));
			}
			if (channel.getChannelType() != null) {
				if (channel.getChannelType() == 1) {
					hql += " and t.name not in ('MINGSHENGZHITONGCHE','XINKKEZHITONGCHE')";
				} else {
					hql += " and t.name in ('MINGSHENGZHITONGCHE','XINKKEZHITONGCHE')";
				}
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
	public boolean addchannel(String merchantName, String shortName, String type, String address, String ProvinceCode,
			String CityCode, String DistrictCode) throws Exception {
		if (type.equals("1")) {
			TroughMingSheng("FB_ZFB_", "ZFBZF", TroughUtil.TroughZFB(), merchantName, shortName, address, 200,
					"民生支付宝" + shortName, ProvinceCode, CityCode, DistrictCode);
		} else if (type.equals("2")) {
			TroughMingSheng("FB_WX_", "WXZF", TroughUtil.TroughWX(), merchantName, shortName, address, 300,
					"民生微信" + shortName, ProvinceCode, CityCode, DistrictCode);
		} else if (type.equals("3")) {
			TroughMingSheng("FB_QQ_", "QQZF", "2016062900190068", merchantName, shortName, address, 1300,
					"民生QQ支付" + shortName, ProvinceCode, CityCode, DistrictCode);
		}
		return false;
	}

	public Boolean TroughMingSheng(String merType, String payway, String category, String merchantName,
			String shortName, String address, Integer type, String detailName, String ProvinceCode, String CityCode,
			String DistrictCode) throws Exception {
		String merchantId = DateUtil.convertCurrentDateTimeToString();
		SMZF001 smzf001 = new SMZF001();
		merchantId = merType + merchantId;
		smzf001.setCooperator(Min_sheng_D0_cooperator);
		smzf001.setPayWay(payway);
		smzf001.setCategory(category);
		smzf001.setMerchantId(merchantId);
		smzf001.setMerchantName("个体户" + merchantName);
		smzf001.setShortName(shortName);
		smzf001.setMerchantAddress(address);
		smzf001.setServicePhone("13816111195");
		smzf001.setIdCard("152822199012293814");
		smzf001.setAccName("芦强");
		smzf001.setAccNo("6226630401364905");
		smzf001.setT0drawFee("0.2");
		smzf001.setT0tradeRate("0.003");
		smzf001.setT1drawFee("0.2");
		smzf001.setT1tradeRate("0.0027");
		smzf001.setChannelMerchantCode(merchantId);
		smzf001.setProvinceCode(ProvinceCode);
		smzf001.setCityCode(CityCode);
		smzf001.setDistrictCode(DistrictCode);
		smzf001.setContactType("01");
		smzf001.setContactName(merchantName);
		String responseStr = MinShengMerchantInputMinShengUtil.doPost(smzf001);
		Map<String, String> result = XmlMapper.xml2Map(responseStr);
		if (result == null) {
			return false;
		}
		String config = "{\"cooperator\":\"%s\",\"callBack\":\"https://bbpurse.com/flypayfx/payment/minshengNotify\",\"serverUrl\":\"https://ipay.cmbc.com.cn:9020/nbp-smzf-hzf\",\"merchant_code\":\"%s\"}";
		if (result.get("respCode").equals("200012")) {
			String merchantCode = "test";
			addMingShenChannel(detailName, type, config, merchantCode, merchantId, 4);
		} else if (result.get("respCode").equals("000000")) {
			String merchantCode = result.get("merchantCode");
			addMingShenChannel(detailName, type, config, merchantCode, merchantId, 1);
		} else {
			return false;
		}
		return true;
	}

	public void addMingShenChannel(String detailName, Integer type, String config, String merchantCode,
			String merchantId, Integer Stratus) {
		Tchannel t = new Tchannel();
		t.setVersion(Long.parseLong("0"));
		t.setType(type);
		t.setName("MINSHENG");
		t.setRealRate(new BigDecimal(0.0025));
		t.setShowRate(new BigDecimal(0.0049));
		t.setShareRate(new BigDecimal(0.0010));
		t.setMaxTradeAmt(new BigDecimal(100000.00));
		t.setMinTradeAmt(new BigDecimal(0.00));
		t.setStatus(Stratus);
		t.setMaxChannelAmt(new BigDecimal(20000.00));
		t.setMinChannelAmt(new BigDecimal(0.00));
		t.setTodayAmt(new BigDecimal(0.00));
		t.setMaxAmtPerDay(new BigDecimal(200000.00));
		t.setAccount(Min_sheng_D0_cooperator);
		t.setConfig(String.format(config, Min_sheng_D0_cooperator, merchantCode));
		t.setDetailName(detailName);
		t.setSeq(100);
		t.setUserType(700);
		t.setCommissionRate(new BigDecimal(0.00));
		t.setMerchantId(merchantId);
		t.setLimitType(0);
		t.setMaxNumPerPersonPerDay(100000l);
		t.setUserId(0l);
		t.setPayType(5l);
		channelDao.save(t);
		int[] orgIds = { 1, 2, 3, 39, 40, 44, 45, 47, 48, 49 };
		for (int i = 0; i < orgIds.length; i++) {
			TorgChannel org = new TorgChannel();
			org.setVersion(0);
			org.setOrganization(organizationDao.get(Torganization.class, Long.parseLong(String.valueOf(orgIds[i]))));
			org.setChannel(t);
			org.setRealRate(new BigDecimal(0.0028));
			org.setStatus(0);
			org.setStartDate(new Date());
			org.setEndDate(new Date());
			org.setCreateDate(new Date());
			org.setCreator("system");
			org.setUpdateDate(new Date());
			org.setUpdator("system");
			orgChannelDao.save(org);
		}
	}

	
	/**
	 * 直清模式，开通大商户，用户平台收取升级费用
	 * @param merchantName
	 * @param shortName
	 * @param type  1，微信，绑定张瑜婷的卡  2，非微信，绑定卢强的卡
	 * @return
	 */
	@Override
	public JSONObject addPingAnPayZhiQing(String merchantName, String shortName, String type){
		JSONObject  res = new JSONObject();
		LOG.info("开通用户收款的 大商户,商户全称为"+merchantName + "，商户简称为"+shortName+"");
		
		//开通商户
		FshowsLiquidationSubmerchantCreateRequest req = new FshowsLiquidationSubmerchantCreateRequest();
		String patt = "PATT_" + DateUtil.convertCurrentDateTimeToString();
		req.setExternal_id(patt);
		req.setName(merchantName);
//		req.setName("上海福别信息技术服务有限公司 ");
		req.setAlias_name(merchantName);
		req.setService_phone("13052222696");
		req.setCategory_id("2015080600000001");
		req.setId_card_name("张瑜婷");
		req.setId_card_num("310115198811254020");
		req.setStore_address("上海市浦东新区顺和路");
		req.setId_card_hand_img_url("https://bbpurse.com/flypayfx/mobile/getImage/20160909a7f617d569954c6cbd48ed38bb54d7f7");
		req.setStore_front_img_url("https://bbpurse.com/flypayfx/mobile/getImage/20160910d9bff73b2cb74031b191fbba954331e7");
		req.setProvince("上海");
		req.setCity("上海市");
		req.setDistrict("浦东新区");
		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantCreateRequest.class);
		if(result == null){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户时请求平安失败");
			return res;
		}
		if(!result.getBooleanValue("success")){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户时,返回错误信息:"+result.getString("error_message"));
			return res;
		}
		
		// 校验平安通道的可用性 --start
		// 因平安报备商户可能存在报备的id和实际姓名不对照，校验平安通道是否可用 at 2017-11-16 by liangchao
		String merId = result.getJSONObject("return_value").getString("sub_merchant_id");
		FshowsLiquidationSubmerchantQueryRequest reqQuery = new FshowsLiquidationSubmerchantQueryRequest();
		reqQuery.setSub_merchant_id(merId);
		JSONObject validatInfo = PinganPaymentUtil.sentRequstToPingAnPayment(reqQuery, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantQueryRequest.class).getJSONObject("return_value");
		
		if (!validatInfo.getString("service_phone").equals("13052222696")
				|| !(validatInfo.getString("name").equals(merchantName) // 商户全称
						|| validatInfo.getString("alias_name").equals(merchantName) // 商户简称
				)) {
			LOG.error("开通平安支付通道报备后再查询信息，前后不对称。merchantName = " + merchantName + ",type=" + type);
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户后，与本地通道信息校验不一致");
			return res;
		}
		// 校验平安通道的可用性 --end
		
		
		String sub_merchant_id = result.getJSONObject("return_value").getString("sub_merchant_id");
		//开始绑卡
		//商户绑卡
		FshowsLiquidationSubmerchantBankBindRequest bindReq = new FshowsLiquidationSubmerchantBankBindRequest();
		bindReq.setSub_merchant_id(sub_merchant_id);
//		if ("1".equals(type)) {	//微信
//			bindReq.setBank_card_no("6222600110054222778");
//			bindReq.setCard_holder("张瑜婷");
//		} else {
			bindReq.setBank_card_no("6214852111454099");
			bindReq.setCard_holder("芦强");
//		}
		JSONObject bankResult = PinganPaymentUtil.sentRequstToPingAnPayment(bindReq, PinganPaymentUtil.appId,FshowsLiquidationSubmerchantBankBindRequest.class);
		if(bankResult == null){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--绑卡请求平安失败");
			return res;
		}
		if(!bankResult.getBooleanValue("success")){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--绑卡请求返回失败信息:"+bankResult.getString("error_message"));
			return res;
		}
		
		//保存通道信息
		 Map<String,String> configMap = new LinkedHashMap<String,String>();
		 configMap.put("pingan.appId", PinganPaymentUtil.appId);
		 configMap.put("pingan.merchant_id",sub_merchant_id);
		 configMap.put("pingan.notifyUrl","https://bbpurse.com/flypayfx/payment/pinganPayNotify");
		 configMap.put("pingan.createIp", "127.0.0.1");
		 String configArray = JSONArray.fromObject(configMap).toString();
		 String config = configArray.substring(1, configArray.length()-1);
		//保存京东
		 addPinaAnChannelForZhiQing(merchantName, 900, config,  patt, 0);
		 addPinaAnChannelForZhiQing(merchantName, 920, config,  patt, 0);
		
		//开始报备支付宝
		
		 FshowsAliPayMerchantCreateRequest alireq = new FshowsAliPayMerchantCreateRequest();
		 alireq.setStore_id(merId);
		 JSONObject aliresult = PinganPaymentUtil.sentRequstToPingAnPayment(alireq, PinganPaymentUtil.appId, FshowsAliPayMerchantCreateRequest.class);
		if(aliresult == null){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--报备支付宝请求失败");
			return res;
		}
		if(!aliresult.containsKey("return_value")){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--报备支付宝请求返回失败信息:"+aliresult.getString("error_message"));
			return res;
		}
		//保存开通支付宝的信息
		
		JSONObject config200 = JSONObject.parseObject(config);
		config200.put("pingan.add.ali", "success");
		addPinaAnChannelForZhiQing(merchantName, 200, config200.toJSONString(),  patt, 0);
		addPinaAnChannelForZhiQing(merchantName, 220, config200.toJSONString(),  patt, 0);
		
		
		
		//开始报备翼支付
		JSONObject yzfMer = pingAnService.createYZFMer(sub_merchant_id, merchantName, PinganPaymentUtil.appId); 
		if(!yzfMer.getString("respCode").equals("000")){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--报备翼支付请求失败:"+yzfMer.getString("respDesc"));
			return res;
		}
		JSONObject config1100 =  JSONObject.parseObject(config);
		config1100.put("pingan.yzf.craete", "success");
		
		addPinaAnChannelForZhiQing(merchantName, 1100, config1100.toJSONString(),  patt, 0);
		addPinaAnChannelForZhiQing(merchantName, 1200, config1100.toJSONString(),  patt, 0);
		
		
		//开始报备微信入驻
		
		FshowsLiquidationWxSubmerchantCreateSupplementRequest wxReq = new FshowsLiquidationWxSubmerchantCreateSupplementRequest();
		wxReq.setStore_id(sub_merchant_id);	//商户的id
		wxReq.setMerchant_name(PinganPaymentUtil.merchant_name); //商户名称,作为主体名称，与微信公众号进行校验
		wxReq.setMerchant_shortname(merchantName);  //商户名称，显示给消费者看的
		wxReq.setService_phone("13052222696");	//客服电话，方便微信在必要时能联系上商家，会在支付详情展示给消费者
		wxReq.setBusiness("308");  //经营行业类目
		wxReq.setPay_type("3");	//3为当面付
		JSONObject wxResult = PinganPaymentUtil.sentRequstToPingAnPayment(wxReq, PinganPaymentUtil.appId, FshowsLiquidationWxSubmerchantCreateSupplementRequest.class);
		if(wxResult==null){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--报备微信入驻请求失败");
			return res;
		}
		if(!wxResult.containsKey("return_value")){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--报备微信入驻请求返回失败信息:"+wxResult.getString("error_message"));
			return res;
		}
		
		JSONObject config300 =  JSONObject.parseObject(config);
		
		config300.put("sub_wx_id", wxResult.getJSONObject("return_value").getString("sub_mch_id"));
		config300.put("sub_wx_merchant_name",  wxReq.getMerchant_name());
		config300.put("sub_wx_merchant_shortname",  wxReq.getMerchant_shortname());
		
		//开始补充微信---授权目录
		
		FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest jsapi_path_req = new FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest();
		jsapi_path_req.setStore_id(sub_merchant_id);
		jsapi_path_req.setJsapi_path(PinganPaymentUtil.jsapi_path);
		jsapi_path_req.setPay_type("3");
		JSONObject jsapi_path_Back = PinganPaymentUtil.sentRequstToPingAnPayment(jsapi_path_req, PinganPaymentUtil.appId, FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest.class);
		if(jsapi_path_Back == null){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--补充微信---授权目录请求失败");
			return res;
		}
		if(!jsapi_path_Back.containsKey("return_value")){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--补充微信---授权目录返回失败信息:"+jsapi_path_Back.getString("error_message"));
			return res;
		}
		config300.put("sub_wx_supplement_jsapi_path",  PinganPaymentUtil.jsapi_path);
		
		
		//开始补充微信--关联微信appid
		FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest sub_appid_req = new FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest();
		sub_appid_req.setStore_id(sub_merchant_id);
		sub_appid_req.setSub_appid(PinganPaymentUtil.wx_app_id);
		sub_appid_req.setPay_type("3");
		JSONObject sub_appid_Back = PinganPaymentUtil.sentRequstToPingAnPayment(sub_appid_req, PinganPaymentUtil.appId, FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest.class);
		
		if(sub_appid_Back==null){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--补充微信---关联微信appid请求失败");
			return res;
		}
		if(!sub_appid_Back.containsKey("return_value")){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--补充微信---关联微信appid返回失败信息:"+jsapi_path_Back.getString("error_message"));
			return res;
		}
		config300.put("sub_wx_supplement_sub_id",  PinganPaymentUtil.wx_app_id);
		
		
		//开始补充微信--推荐关注appID
		FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest subscribe_appid_req = new FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest();
		subscribe_appid_req.setStore_id(sub_merchant_id);
		subscribe_appid_req.setSubscribe_appid(PinganPaymentUtil.wx_app_id);
		subscribe_appid_req.setPay_type("3");
		JSONObject subscribe_appid_back = PinganPaymentUtil.sentRequstToPingAnPayment(subscribe_appid_req, PinganPaymentUtil.appId, FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest.class);
		if(subscribe_appid_back == null){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--补充微信---推荐关注appID请求失败");
			return res;
		}
		if(!subscribe_appid_back.containsKey("return_value")){
			res.put("code",  GlobalConstant.RESP_CODE_999);
			res.put("message", "开通子商户--补充微信---推荐关注appID返回失败信息:"+subscribe_appid_back.getString("error_message"));
			return res;
		}
		
		config300.put("sub_wx_supplement_subscribe_appid",  PinganPaymentUtil.wx_app_id);
		
		
		addPinaAnChannelForZhiQing(merchantName, 300, config300.toJSONString(),  patt, 0);
		addPinaAnChannelForZhiQing(merchantName, 320, config300.toJSONString(),  patt, 0);
		
		res.put("code",  GlobalConstant.RESP_CODE_SUCCESS);
		res.put("message", "开通成功");
		
		return res;
	};
	
	//查询指定的大商户通道
	private Channel queryChannel (String detailName, Integer type,	String merchantId, Integer Stratus ){
		Channel queryChannel = new Channel();
		queryChannel.setDetailName(detailName);
		queryChannel.setType(type);
		queryChannel.setMerchantId(merchantId);
		queryChannel.setStatus(Stratus);
		List<Tchannel> channels = channelService.searchTchannels(queryChannel);
		if(channels.size()>0){
//			BeanUtils.copyProperties(t, u);
			Channel c = new Channel();
			BeanUtils.copyProperties(channels.get(0), c);
			return c;
		}else {
			return null;
		}
	}
	
	
	@Override
	public boolean addPingAnchannel(String merchantName, String shortName, String type, String perType, String address,
			String ProvinceCode, String CityCode, String DistrictCode) throws Exception {
		LOG.info("addPingAnchannel args-merchantName={},shortName={},type={},perType={}",
				new Object[] { merchantName, shortName, type, perType });
		//创建子商户
		FshowsLiquidationSubmerchantCreateRequest req = new FshowsLiquidationSubmerchantCreateRequest();
		String patt = "PATT_" + DateUtil.convertCurrentDateTimeToString();
		req.setExternal_id(patt);
		req.setName(merchantName); // 商户全称
		req.setAlias_name(merchantName); // 商户简称
		req.setService_phone("13052222696");
		req.setCategory_id("2015080600000001");
		req.setId_card_name("张瑜婷");
		req.setId_card_num("310115198811254020");
		req.setStore_address("上海市浦东新区顺和路");
		req.setId_card_hand_img_url("https://bbpurse.com/flypayfx/mobile/getImage/20160909a7f617d569954c6cbd48ed38bb54d7f7");
		req.setStore_front_img_url("https://bbpurse.com/flypayfx/mobile/getImage/20160910d9bff73b2cb74031b191fbba954331e7");
		req.setProvince("上海");
		req.setCity("上海市");
		req.setDistrict("浦东新区");

		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId,
				FshowsLiquidationSubmerchantCreateRequest.class);
		
		
		// 校验平安通道的可用性 --start
		// 因平安报备商户可能存在报备的id和实际姓名不对照，校验平安通道是否可用 at 2017-11-16 by liangchao
		String appId = PinganPaymentUtil.appId;
		String merId = result.getJSONObject("return_value").getString("sub_merchant_id");
		FshowsLiquidationSubmerchantQueryRequest reqQuery = new FshowsLiquidationSubmerchantQueryRequest();
		reqQuery.setSub_merchant_id(merId);
		JSONObject validatInfo = PinganPaymentUtil
				.sentRequstToPingAnPayment(reqQuery, appId, FshowsLiquidationSubmerchantQueryRequest.class)
				.getJSONObject("return_value");
		if (!validatInfo.getString("service_phone").equals("13052222696")
				|| !(validatInfo.getString("name").equals(merchantName) // 商户全称
						|| validatInfo.getString("alias_name").equals(merchantName) // 商户简称
				)) {
			LOG.error("开通平安支付通道报备后再查询信息，前后不对称。merchantName = " + merchantName + ",type=" + type);
			return false;
		}
		// 校验平安通道的可用性 --end
		

		LOG.info("addPingAnchannel result={}", new Object[] { result });
		if (result != null) {
			if (result.getBooleanValue("success")) {
				String sub_merchant_id = result.getJSONObject("return_value").getString("sub_merchant_id");
				//商户绑卡
				FshowsLiquidationSubmerchantBankBindRequest bindReq = new FshowsLiquidationSubmerchantBankBindRequest();
				bindReq.setSub_merchant_id(sub_merchant_id);
				if ("2".equals(perType)) {	//微信
					bindReq.setBank_card_no("6222600110054222778");
					bindReq.setCard_holder("张瑜婷");
				} else {
					bindReq.setBank_card_no("6214852111454099");
					bindReq.setCard_holder("芦强");
				}
				JSONObject bindResult = PinganPaymentUtil.sentRequstToPingAnPayment(bindReq, PinganPaymentUtil.appId,
						FshowsLiquidationSubmerchantBankBindRequest.class);
				String config = "{\"pingan.appId\":\"20161102104403706\",\"pingan.merchant_id\": \"%s\",\"pingan.notifyUrl\": \"https://bbpurse.com/flypayfx/payment/pinganPayNotify\",\"pingan.createIp\": \"127.0.0.1\"}";
				LOG.info("addPingAnchannel sub_merchant_id={},Bank_card_no={},Card_holder={}",
						new Object[] { sub_merchant_id, bindReq.getBank_card_no(), bindReq.getCard_holder() });
				LOG.info("addPingAnchannel merchant={}", bindResult.toJSONString());
				if (type.equals("1")) {// 支付宝
					LOG.info("addPingAnchannel ALIPAY merchantName={}", merchantName);
					addPinaAnChannel(merchantName, 200, config, sub_merchant_id, patt, 100);
				} else if (type.equals("2")) {// 微信
					addPinaAnChannel(merchantName, 300, config, sub_merchant_id, patt, 100);
				} else if (type.equals("4")) {// 京东
					addPinaAnChannel(merchantName, 900, config, sub_merchant_id, patt, 100);
				} else if (type.equals("1100")) {// 翼支付
					addPinaAnChannel(merchantName, 1100, config, sub_merchant_id, patt, 100);
				}
			}
		}
		return false;
	}

	//保存直清模式下开通的大商户
	private void addPinaAnChannelForZhiQing(String detailName, Integer type, String config,	String merchantId, Integer Stratus) {
		Tchannel t = new Tchannel();
		t.setVersion(Long.parseLong("0"));
		t.setType(type);
		t.setName("PINGANPAY");
		t.setRealRate(new BigDecimal(0.0025));
		t.setShowRate(new BigDecimal(0.0049));
		t.setShareRate(new BigDecimal(0.0010));
		t.setMaxTradeAmt(new BigDecimal(100000.00));
		t.setMinTradeAmt(new BigDecimal(0.00));
		t.setStatus(Stratus);
		t.setMaxChannelAmt(new BigDecimal(20000.00));
		t.setMinChannelAmt(new BigDecimal(0.00));
		t.setTodayAmt(new BigDecimal(0.00));
		t.setMaxAmtPerDay(new BigDecimal(200000.00));
		t.setAccount(PinganPaymentUtil.appId);
		t.setConfig(config);
		t.setDetailName(detailName);
		t.setSeq(100);
		t.setUserType(700);
		t.setCommissionRate(new BigDecimal(0.00));
		t.setMerchantId(merchantId);
		t.setLimitType(0);
		t.setMaxNumPerPersonPerDay(100000l);
		t.setUserId(0l);
		t.setPayType(5l);
		t.setCreateDate(new Date());
		channelDao.save(t);
	}
	
	
	private void addPinaAnChannel(String detailName, Integer type, String config, String merchantCode,
			String merchantId, Integer Stratus) {
		Tchannel t = new Tchannel();
		t.setVersion(Long.parseLong("0"));
		t.setType(type);
		t.setName("PINGANPAY");
		t.setRealRate(new BigDecimal(0.0025));
		t.setShowRate(new BigDecimal(0.0049));
		t.setShareRate(new BigDecimal(0.0010));
		t.setMaxTradeAmt(new BigDecimal(100000.00));
		t.setMinTradeAmt(new BigDecimal(0.00));
		t.setStatus(Stratus);
		t.setMaxChannelAmt(new BigDecimal(20000.00));
		t.setMinChannelAmt(new BigDecimal(0.00));
		t.setTodayAmt(new BigDecimal(0.00));
		t.setMaxAmtPerDay(new BigDecimal(200000.00));
		t.setAccount(PinganPaymentUtil.appId);
		t.setConfig(String.format(config, merchantCode));
		t.setDetailName(detailName);
		t.setSeq(100);
		t.setUserType(700);
		t.setCommissionRate(new BigDecimal(0.00));
		t.setMerchantId(merchantId);
		t.setLimitType(0);
		t.setMaxNumPerPersonPerDay(100000l);
		t.setUserId(0l);
		t.setPayType(5l);
		t.setCreateDate(new Date());
		channelDao.save(t);
	}

}
