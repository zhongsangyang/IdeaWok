package com.cn.flypay.service.payment.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.TserviceMerchant;
import com.cn.flypay.model.sys.TsysChannelDistinctMerid;
import com.cn.flypay.model.util.StringUtil;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationAlipayTradeQueryRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationBestpayH5payRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationBestpayMerchantAccountRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationFinanceDownloadbillRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationJdMerchantCreate;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantBankBindRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantCreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantQueryRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantRateQuery;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantRateSetRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxTradePrecreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxpayMppayRequest;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.payment.TroughTrainServeice;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.ServiceMerchantService;
import com.cn.flypay.service.sys.ThroughChannelService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.TroughUtil;
import com.cn.flypay.utils.pingan.PinganPaymentUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring*.xml" })
public class PinganPaymentServiceImplTest {

	@Autowired
	private ChannelPaymentService pinganPaymentService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private ServiceMerchantService serviceMerchantService;
	
	
	@Autowired
	private TroughTrainServeice troughTrainServeice;

	public void testcreateSubMerchant() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("merchantName", "冯氏快餐店");
		params.put("shortName", "冯氏快餐店");
		TserviceMerchant sm = serviceMerchantService.getTserviceMerchant(1l);
		ServiceMerchant sms = new ServiceMerchant();
		BeanUtils.copyProperties(sm, sms);
		pinganPaymentService.createSubMerchant(sms, params);
	}


	public void testCreateUnifiedOrder() {
		User u = new User();
		u.setId(4l);
		u.setOrganizationAppName("飞付宝");
		try {
			ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.JDQR.getCode(), BigDecimal.valueOf(0.11), 21, 3l, 0);
			pinganPaymentService.createUnifiedOrder(u, cpr, 5, 0.01d, 10, 0, "商户小孙可口可乐兑奖券");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testCreateSMOrder() {
		User u = new User();
		u.setId(1l);
		String authCode = "289838872506404311";
		try {
			ChannelPayRef cpr = routeService.checkChannelPayRoute(UserOrder.trans_type.ALSM.getCode(), BigDecimal.valueOf(1), 21, 4l, 0);
			pinganPaymentService.createSmUnifiedOrder(u, cpr, 5, authCode, 0.01d, 10, 0, "我来测试了");
			// Map<String, String> map = new HashMap<>();
			// pinganPaymentService.createWxOnLineOrder(u, cpr, 0,6000d, 10,
			// "我来测试了", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testsentOrderNumToChannelForSearchStatus() {
		String orderNum = "JDQR201612141023544750000000004";
		pinganPaymentService.sendOrderNumToChannelForSearchStatus(orderNum);
	}
	@Test
	public void testdealDownloadStatement() {
//		User u = new User();
//		u.setId(1l);
		try {
			pinganPaymentService.dealDownloadStatement("20170601");
			testcreateSubMerchant();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试查询订单
	 */
	@Test
	public void testQuery(){
		pinganPaymentService.sendOrderNumToChannelForSearchStatus("YZFQR201710311915008750000000002");
	}
	
	@Test
	public void testtest(){
		Map<String,String> a= new HashMap<String,String>();
		a.put("1", "123");
		a.put("2", "123");
		Map<String,String> b =a;
		a.remove("2");
		System.out.println("a="+a.toString());
		System.out.println("b="+b.toString());
		
		
		
	}
	
	
	
	@Test
	public void testYZFOrder(){
		
		FshowsLiquidationBestpayH5payRequest req = new FshowsLiquidationBestpayH5payRequest();
		String out_trade_no = "YZFQR201711011446538210000086445";
		req.setOut_trade_no(out_trade_no);
		req.setNotify_url("http://1g83849h98.iask.in:34530/flypayfx/payment/pinganPayNotify");
		req.setSub_merchant_id("20170821160108027562");
		req.setBody("线下扫码");
		Double money = 0.01;
		req.setTotal_fee(money.toString());
		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, "20161102104403706", FshowsLiquidationBestpayH5payRequest.class);
		System.out.println(result);
		if (result.getBooleanValue("success")) {
			try {
				String trade_no = result.getJSONObject("return_value").getString("trade_no");
				String callback_url = String.format(ApplicatonStaticUtil.success_pay_url, out_trade_no);
				String qrurl = String.format(PinganPaymentUtil.best_pay_product_url, trade_no, URLEncoder.encode("线下扫码", "UTF-8"), callback_url);
				
				System.out.println(qrurl);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	/**
	 * 查询平安订单信息
	 */
	@Test
	public void testQueryOrder(){
		FshowsLiquidationAlipayTradeQueryRequest req = new FshowsLiquidationAlipayTradeQueryRequest();
		//订单号
		req.setOut_trade_no("JDQR201711031013201360000090506");
		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, "20161102104403706", FshowsLiquidationAlipayTradeQueryRequest.class);
		
		
	}
	
	
	/**
	 * 下载平安对账单信息
	 */
	@Test
	public void testDownloadFile(){
		FshowsLiquidationFinanceDownloadbillRequest request = new FshowsLiquidationFinanceDownloadbillRequest();
		request.setBill_date("20171019");
		request.setPay_platform(String.valueOf(4));
		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(request, "20161102104403706", request.getClass());
		System.out.println(result);
	}
	/**
	 * 查询平安查询报备商户
	 */
	@Test
	public void testQueryMer(){
		FshowsLiquidationSubmerchantQueryRequest req = new FshowsLiquidationSubmerchantQueryRequest();

		req.setSub_merchant_id("20171025172209020987");

		JSONObject json2 = PinganPaymentUtil.sentRequstToPingAnPayment(req, "20161102104403706", FshowsLiquidationSubmerchantQueryRequest.class);
		System.out.println(json2.toJSONString());
	}
	
	
	/**
	 * 平安--微信子商户入驻补充接口
	 */
	
//	private void testCreateSupplement(){
//		FshowsLiquidationWxSubmerchantCreateSupplementRequest wxReq = new FshowsLiquidationWxSubmerchantCreateSupplementRequest();
//		wxReq.setStore_id("20171025172215023145");	//商户的id
//		wxReq.setMerchant_name("潘达通讯设备有限公司"); //商户名称
//		wxReq.setMerchant_shortname("潘达通讯设备有限公司");  //商户名称，显示给消费者看的
//		wxReq.setService_phone("13052222696");	//客服电话，方便微信在必要时能联系上商家，会在支付详情展示给消费者
//		wxReq.setBusiness("302");  //经营行业类目
//		wxReq.setPay_type("3");	//3为当面付
//		JSONObject wxResult = PinganPaymentUtil.sentRequstToPingAnPayment(wxReq, "20161102104403706", FshowsLiquidationWxSubmerchantCreateSupplementRequest.class);
//		System.out.println(wxResult.toJSONString());
//		
//	}

	/**
	 * 报备平安-支付宝补充接口
	 */
	@Test
	public void testaddPingAnZhiFuBaoMer(){
		try {
			Long channelId=58880L;
			Map<String,String>  res= troughTrainServeice.addPingAnZhiFuBaoMer(channelId);
			
			System.out.println("111");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 报备平安-微信补充接口
	 */
	@Test
	public void testaddcreatCommercial(){
		try {
			Long channelId=58880L;
			Map<String,String>  res= troughTrainServeice.addPingAnWeiXinMer(channelId);
			
			System.out.println("111");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 报备平安-微信补充接口
	 */
	@Test
	public void testaddPingAnWeiXinSupplement(){
		try {
			Long channelId=58880L;
			String objStr="sub_appid";
			String objVal="wxba01e33510fcc661";
			Map<String,String>  res= troughTrainServeice.addPingAnWeiXinSupplement(channelId,objStr,objVal);
			
			System.out.println("111");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 测试绑卡
	 */
	@Test
	public void testbangka(){
		try {
			FshowsLiquidationSubmerchantBankBindRequest bindReq = new FshowsLiquidationSubmerchantBankBindRequest();
			bindReq.setSub_merchant_id("20171205150715025874");	//我的
			bindReq.setBank_card_no("6259585205667376");	//卡的卡号
			bindReq.setCard_holder("李海平");	//卡的持有人
			JSONObject bindResult = PinganPaymentUtil.sentRequstToPingAnPayment(bindReq, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantBankBindRequest.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试报备商户
	 */
	@Test
	public void testAddMer(){
		//请求参数
		String merchantName = "轩辕玩具店";
		
		
		FshowsLiquidationSubmerchantCreateRequest req = new FshowsLiquidationSubmerchantCreateRequest();
		String patt = "PATT_" + DateUtil.convertCurrentDateTimeToString();
		req.setExternal_id(patt);
		req.setName(merchantName);
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
		
	}
	
	/**
	 * 设置商户的报备费率
	 */
	@Test
	public void testSetMerFee(){
		FshowsLiquidationSubmerchantRateSetRequest rateset = new FshowsLiquidationSubmerchantRateSetRequest();
		rateset.setSub_merchant_id("20171209155351026061");
	    rateset.setMerchant_rate("0.0035");	//默认只设置了微信的费率
	    rateset.setType("2");
	    JSONObject rateResult = PinganPaymentUtil.sentRequstToPingAnPayment(rateset, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantRateSetRequest.class);
	    System.out.println(rateResult.toJSONString());
	}
	
	
	/**
	 * 查询商户报备费率
	 */
	@Test
	public void testMerFee(){
		FshowsLiquidationSubmerchantRateQuery req = new FshowsLiquidationSubmerchantRateQuery();
		req.setSub_merchant_id("20171209155351026061");
		req.setType("2");
		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantRateQuery.class);
		System.out.println(result.toJSONString());
	}
	
	/**
	 * 测试报备翼支付
	 */
	@Test
	public void testCreateYZFMer(){
		String  sub_merchant_id = "";
		String name = "";
		FshowsLiquidationBestpayMerchantAccountRequest account = new FshowsLiquidationBestpayMerchantAccountRequest();
	    account.setStore_id(sub_merchant_id);
	    account.setMerchant_store_name(name);
	    account.setStore_area_id(TroughUtil.YIZFProvince());
	    account.setStore_city_id(TroughUtil.YIZFCity());
	    account.setMcc_code(TroughUtil.TroughYIZF());
		JSONObject accountResult = PinganPaymentUtil.sentRequstToPingAnPayment(account, "20161102104403706", FshowsLiquidationBestpayMerchantAccountRequest.class);
	}
	
	@Autowired
	private UserService userService;
	@Autowired
	private CommonService commonService;
	
	
	/**
	 * 测试微信公众号 下单
	 */
	@Test
	public void testwxgongzhonghao(){
		FshowsLiquidationWxpayMppayRequest req = new FshowsLiquidationWxpayMppayRequest();
		
		//先输入一个商户的code
//		User user = userService.getByCode("");
		/* 用户订单号= 用户ID + 14位时间戳+ userType（2位） */
//		String createIp = channelConfig.getString("pingan.createIp");
//		String appId = channelConfig.getString("pingan.appId");
//		String notifyUrl = channelConfig.getString("pingan.notifyUrl");
		
		String user_id = "2";
		String sub_merchant_id ="20171220162322028788";
		
		
		String out_trade_no = commonService.getUniqueOrderByType("320", Long.valueOf(user_id));
		req.setSub_merchant_id(sub_merchant_id);	//平安子商户id
		req.setBody("宝贝钱袋");	//sys_organization -- app_name
		req.setOut_trade_no(out_trade_no);
		req.setTotal_fee("0.1");
		req.setSpbill_create_ip("127.0.0.1");	//APP和网页支付提交用户端ip
//		req.setSub_openid(params.get("userId"));	//用户在子商户appid下的唯一标识
		req.setSub_openid("oH_4txMSsoGpLRPOa8hPDcX7Ap-E");
		req.setNotify_url("http://1g83849h98.iask.in:34530/flypayfx/payment/pinganPayNotify");	//回调地址

		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsLiquidationWxpayMppayRequest.class);
		System.out.println(result.toJSONString());
		
		
	}
	
	
	
	
	/**
	 * 统计平安的子商户id数量
	 */
	@Autowired
	private BaseDao<Tchannel> channelDao;
	@Autowired
	private BaseDao<TsysChannelDistinctMerid> tsysChannelDistinctMeridDao;
	
	
	@Test
	public void testCountPingAnMerId(){
		
		List<Tchannel> ts = channelDao.find("select t from Tchannel t where  t.name in ('PINGANPAYZHITONGCHE','PINGANPAY')  ");
		int a = ts.size();
		int b = 0;
		for(Tchannel t:ts){
			b++;
			System.out.println("当前位数为"+b+"，总数为"+a);
			TsysChannelDistinctMerid tc = new TsysChannelDistinctMerid();
			if(StringUtil.isNotBlank(t.getConfig())){
				
				
				JSONObject json = JSONObject.parseObject(t.getConfig());
				if(StringUtil.isNotBlank(json.getString("pingan.merchant_id"))){
					tc.setSubMerchantId(json.getString("pingan.merchant_id"));
				}
				tc.setChannelId(t.getId().toString());
				tc.setChannelName(t.getName());
				tsysChannelDistinctMeridDao.save(tc);
			}
		}
	}
	
	/**
	 * 测试报备平安一条龙服务
	 */
	@Test
	public void testCreateZhiQingMer(){
		//贝克星雨阁米兰西点  微信
		JSONObject res = troughTrainServeice.createPingAnZhiQingMer("贝克星雨阁米兰西点", "贝克星雨阁米兰西点", Long.valueOf("132212"));
		System.out.println(res.toJSONString());
	}
	
	
	@Test
	public void testCreateJingDongMer(){
		
		FshowsLiquidationJdMerchantCreate req = new FshowsLiquidationJdMerchantCreate();
		
		req.setStore_id("20171220114556021524");
		req.setBusiness("005");
		
		JSONObject queryMerInfo = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsLiquidationJdMerchantCreate.class).getJSONObject("return_value");
		System.out.println(queryMerInfo.toJSONString());
	}
	
	
	@Test
	public void testBangDingGongzhonghoa(){
		FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest req = new FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest();
		req.setStore_id("20171222114410024555");
		req.setJsapi_path("https://openapi-liquidation.51fubei.com/payPage/");
		req.setPay_type("3");
		JSONObject reqBack = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest.class);
		System.out.println(reqBack.toJSONString());
		
		
	}
	
	
	@Test 
	public void testPingAnPay(){
		String merchantId ="20171115103133021891";
		String notifyUrl = "https://bbpurse.com/flypayfx/payment/pinganPayNotify";
		String createIp = "127.0.0.1";
		String money = "0.01";
		String out_trade_no = commonService.getUniqueOrderByType("200", 2l);
		FshowsLiquidationWxTradePrecreateRequest req = new FshowsLiquidationWxTradePrecreateRequest();
		req.setBody("向卢强先生致敬");
		req.setOut_trade_no(out_trade_no);
		req.setTotal_fee(money.toString());
		req.setSpbill_create_ip(createIp);
		req.setNotify_url(notifyUrl);
		req.setStore_id(merchantId);
		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsLiquidationWxTradePrecreateRequest.class);
	}
	
	
	@Autowired
	private ThroughChannelService throughChannelService;
	
	/**
	 * 测试直通车开通大商户
	 */
	@Test
	public void testCreateShoukuanMer(){
		
		JSONObject res = throughChannelService.addPingAnPayZhiQing("上海优分期金融", "上海优分期金融", "2");
		System.out.println(res.toJSONString());
	}
	
	
	
	@Autowired
	private UserCardService userCardService;
	
	
	@Test
	public void checkExistSettlementCardtest(){
		if(userCardService.checkExistSettlementCard(2l)){
			System.out.println("true");
		}else{
			System.out.println("false");
		}
		
	}
	
	
	@Autowired
	private ChannelService channelService;
	
	/**
	 * 测试补充报备微信
	 */
	@Test 
	public void baobeiWeixin(){
		JSONObject  res = new JSONObject();
		Channel channel = channelService.get(80755l);
		JSONObject config300 =JSONObject.parseObject(channel.getConfig());
		config300.put("sub_wx_id", "85058987");
		config300.put("sub_wx_merchant_name", PinganPaymentUtil.merchant_name);
		config300.put("sub_wx_merchant_shortname",  channel.getDetailName());
		String sub_merchant_id = "20171225200544023102";
		
		//开始补充微信---授权目录
		
				FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest jsapi_path_req = new FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest();
				jsapi_path_req.setStore_id(sub_merchant_id);
				jsapi_path_req.setJsapi_path(PinganPaymentUtil.jsapi_path);
				jsapi_path_req.setPay_type("3");
				JSONObject jsapi_path_Back = PinganPaymentUtil.sentRequstToPingAnPayment(jsapi_path_req, PinganPaymentUtil.appId, FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest.class);
				if(jsapi_path_Back == null){
					res.put("code",  GlobalConstant.RESP_CODE_999);
					res.put("message", "开通子商户--补充微信---授权目录请求失败");
					System.out.println(res.toJSONString());
				}
				if(!jsapi_path_Back.containsKey("return_value")){
					res.put("code",  GlobalConstant.RESP_CODE_999);
					res.put("message", "开通子商户--补充微信---授权目录返回失败信息:"+jsapi_path_Back.getString("error_message"));
					System.out.println(res.toJSONString());
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
					System.out.println(res.toJSONString());
				}
				if(!sub_appid_Back.containsKey("return_value")){
					res.put("code",  GlobalConstant.RESP_CODE_999);
					res.put("message", "开通子商户--补充微信---关联微信appid返回失败信息:"+jsapi_path_Back.getString("error_message"));
					System.out.println(res.toJSONString());
				}
				config300.put("sub_wx_supplement_wx_app_id",  PinganPaymentUtil.wx_app_id);
				
				
				//开始补充微信--推荐关注appID
				FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest subscribe_appid_req = new FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest();
				subscribe_appid_req.setStore_id(sub_merchant_id);
				subscribe_appid_req.setSubscribe_appid(PinganPaymentUtil.wx_app_id);
				subscribe_appid_req.setPay_type("3");
				JSONObject subscribe_appid_back = PinganPaymentUtil.sentRequstToPingAnPayment(subscribe_appid_req, PinganPaymentUtil.appId, FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest.class);
				if(subscribe_appid_back == null){
					res.put("code",  GlobalConstant.RESP_CODE_999);
					res.put("message", "开通子商户--补充微信---推荐关注appID请求失败");
					System.out.println(res.toJSONString());
				}
				if(!subscribe_appid_back.containsKey("return_value")){
					res.put("code",  GlobalConstant.RESP_CODE_999);
					res.put("message", "开通子商户--补充微信---推荐关注appID返回失败信息:"+subscribe_appid_back.getString("error_message"));
					System.out.println(res.toJSONString());
				}
				
				config300.put("sub_wx_supplement_subscribe_appid",  PinganPaymentUtil.wx_app_id);
				addPinaAnChannelForZhiQing(channel.getDetailName(), 300, config300.toJSONString(),  channel.getMerchantId(), 0);
				addPinaAnChannelForZhiQing(channel.getDetailName(), 320, config300.toJSONString(),  channel.getMerchantId(), 0);
		
		
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
	
		
		
		
	
}
