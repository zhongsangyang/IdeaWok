package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.account.Taccount;
import com.cn.flypay.model.account.TaccountLog;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.Tuser;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.payment.minsheng.SMZF001;
import com.cn.flypay.pageModel.payment.minsheng.SMZF007;
import com.cn.flypay.pageModel.payment.pingan.FshowsAliPayMerchantCreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationBestpayMerchantAccountRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantBankBindRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantCreateRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantQueryRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantRateQuery;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantRateSetRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationSubmerchantRateSetResponse;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest;
import com.cn.flypay.pageModel.payment.pingan.FshowsLiquidationWxSubmerchantCreateSupplementRequest;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.service.payment.ChannelPaymentService;
import com.cn.flypay.service.payment.PingAnService;
import com.cn.flypay.service.payment.TroughTrainServeice;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.TroughUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.minsheng.MinShengMerchantInputMinShengUtil;
import com.cn.flypay.utils.pingan.PinganPaymentUtil;
import com.cn.flypay.utils.xinke.XinkePayUtil;
import com.cn.flypay.utils.yibao.YiBaoBaseUtil;
import net.sf.json.JSONArray;



/**
 * 直通车impl
 * @author LW
 *
 */
@Service
public class TroughTrainServeiceImpl implements TroughTrainServeice {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Value("${TroughTrainT0drawFee}")
	private String TroughTrainT0drawFee;

	@Value("${TroughTrainT1drawFee}")
	private String TroughTrainT1drawFee;
	
	@Value("${TrougWXD0tradeRate}")
	private String TrougWXD0tradeRate;
	
	@Value("${TrougWXT1tradeRate}")
	private String TrougWXT1tradeRate;
	
	@Value("${TrougZFBD0tradeRate}")
	private String TrougZFBD0tradeRate;
	
	@Value("${TrougZFBT1tradeRate}")
	private String TrougZFBT1tradeRate;
	
	@Value("${TrougQQZFD0tradeRate}")
	private String TrougQQZFD0tradeRate;
	
	@Value("${TrougQQZFT1tradeRate}")
	private String TrougQQZFT1tradeRate;
	
	@Value("${TrougJDD0tradeRate}")
	private String TrougJDD0tradeRate;
	
	@Value("${TrougJDT1tradeRate}")
	private String TrougJDT1tradeRate;
	
	@Value("${TrougYLD0tradeRate}")
	private String TrougYLD0tradeRate;
	
	@Value("${TrougYLT1tradeRate}")
	private String TrougYLT1tradeRate;
	
	@Value("${TrougWXMaxAmt}")
	private String TrougWXMaxAmt;
	
	@Value("${TrougWXMinAmt}")
	private String TrougWXMinAmt;
	
	@Value("${TrougZFBMaxAmt}")
	private String TrougZFBMaxAmt;
	
	@Value("${TrougZFBMinAmt}")
	private String TrougZFBMinAmt;
	
	@Value("${TrougQQZFMaxAmt}")
	private String TrougQQZFMaxAmt;
	
	@Value("${TrougQQZFMinAmt}")
	private String TrougQQZFMinAmt;
	
	@Value("${TrougJDMaxAmt}")
	private String TrougJDMaxAmt;
	
	@Value("${TrougJDMinAmt}")
	private String TrougJDMinAmt;
	
	@Value("${TrougYLMaxAmt}")
	private String TrougYLMaxAmt;
	
	@Value("${TrougYLMinAmt}")
	private String TrougYLMinAmt;
	
	
	@Autowired
	private BaseDao<Tuser> userDao;
	@Autowired
	private BaseDao<Tchannel> channelDao;
	
	@Autowired
	private BaseDao<TuserCard> cardDao;
	
	@Autowired
	private BaseDao<Taccount> accountDao;
	
	@Autowired
	private BaseDao<TaccountLog> accountLogDao;
	
	@Autowired
	private ChannelPaymentService minshengPaymentService;
	
	@Autowired
	private ChannelPaymentService xinkePaymentService;
	
	@Autowired
	private ChannelPaymentService zheYangPaymentService;
	
	@Autowired
	private ChannelPaymentService pinganPaymentService;
	
	@Autowired
	private ChannelPaymentService yfyianZXXEPaymentService;
	
	@Autowired
	private ChannelPaymentService yiBaoPaymentService;
	
	@Autowired
	private ChannelPaymentService gaZhiYinLianPaymentService;
	
	@Autowired
	private ChannelPaymentService weiLianBaoPaymentService;
	
	@Autowired
	private ChannelPaymentService yiQiang2PaymentService;
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private ChannelPaymentService xinkeYinLianPaymentService;
	@Autowired
	private PingAnService pingAnService;
	
	public final static String Min_sheng_D0_cooperator = "SMZF_SHFF_HD_T0";
	
	
   
	@Override
	public Map<String, String> editChannle(Long userId) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> result = new HashMap<String, String>();
		result.put("respType", "000");
		result.put("respCode", "000");
		params.put("uid", userId);
		List<Tchannel> tx = channelDao.find("select t from Tchannel t  where t.status in(3,4) and t.name='XINKKEZHITONGCHE' and t.userId=:uid", params);
		if(tx.size()==0){
			result.put("respCode", "999");
			return result;
		}
		List<Tchannel> ts = channelDao.find("select t from Tchannel t  where t.status in(3,4) and t.name='MINGSHENGZHITONGCHE' and t.userId=:uid", params);
		if(ts.size()==0){
			result.put("respCode", "999");
			return result;
		}
		for (Tchannel t : ts) {
			if(String.valueOf(t.getStatus()).equals("3")){
				if(t.getType()==1300){
					result.put("respType", "666");
				}
			}
			if(String.valueOf(t.getStatus()).equals("4")){
				SMZF007 f007 = new SMZF007();
				f007.setMerchantId(t.getMerchantId());
				f007.setCooperator(Min_sheng_D0_cooperator);
				String responseStr = MinShengMerchantInputMinShengUtil.doPost(f007);;
				Map<String, String> resp = XmlMapper.xml2Map(responseStr);
				if(resp.get("oriRespType").equals("R")){
					result.put("respCode", "222");
			    }else if(resp.get("oriRespType").equals("E")){
			    	t.setStatus(5);
			    	channelDao.update(t);
			    	result.put("respCode", "222");
			    	result.put("respType", "333");
			    }else if(resp.get("oriRespType").equals("S")){
			    	if(resp.get("oriRespCode").equals("000000")){
			    		t.setStatus(3);
						String config = "{\"cooperator\":\"SMZF_SHFF_HD_T0\",\"callBack\":\"https://bbpurse.com/flypayfx/payment/minshengNotify\",\"serverUrl\":\"https://ipay.cmbc.com.cn:9020/nbp-smzf-hzf\",\"merchant_code\":\"%s\"}";
						t.setConfig(String.format(config,resp.get("merchantCode")));
						channelDao.update(t);
				    }else{
				    	result.put("respCode", "222");
				    }
			    }
			}
		}
		
		for (Tchannel t : tx) {
			if(String.valueOf(t.getStatus()).equals("3")){
				if(t.getType()==1300){
					result.put("respType", "666");
				}
			}
			if(String.valueOf(t.getStatus()).equals("4")){
				JSONObject config = JSONObject.parseObject(t.getConfig());
				String[] keys = {"merid"};
				String[] inputParams = {config.getString("xinke.merchant_id")};
				String responseStr = XinkePayUtil.build(keys, inputParams, "out_mer_query");
				Map<String, String> resultmap = XmlMapper.xml2Map(responseStr);
				if (resultmap != null && resultmap.containsKey("rspCode") && "000000".equals(resultmap.get("rspCode"))) {
					if(resultmap.get("ZFBRSP")!=null && resultmap.get("ZFBRSP").equals("支付宝报备成功") && 
					   resultmap.get("WXRSP")!=null && resultmap.get("WXRSP").equals("微信报备成功")){
						t.setStatus(4);
						channelDao.update(t);
					}else{
						result.put("respCode", "222");
					}
				}
			}
		}
		return result;
	}
	
	
	
	@Override
	public Map<String, String> editChannlePAPay(Long userId) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> result = new HashMap<String, String>();
		result.put("respCode", "000");
		params.put("uid", userId);
		List<Tchannel> tx = channelDao.find("select t from Tchannel t  where t.status='10' and t.name='PINGANPAYZHITONGCHE' and t.userId=:uid", params);
		if(tx.size()==0){
			result.put("respCode", "999");
			return result;
		}
		return result;
	}
	
	@Override
	public Map<String, String> editChannlePAPayZHIQING(Long userId) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> result = new HashMap<String, String>();
		result.put("respCode", "000");
		params.put("uid", userId);
		List<Tchannel> tx = channelDao.find("select t from Tchannel t  where t.status='10' and t.name='PINGANPAYZHITONGCHE_ZHIQING' and t.userId=:uid", params);
		if(tx.size()==0){
			result.put("respCode", "999");
			return result;
		}
		return result;
	}
	

	@Override
	public  Boolean addcreatCommercial(String merchantName, String shortName,Long userId,String shortType) {
		TuserCard card = cardDao.get("select t from TuserCard t left join t.user u where t.isSettlmentCard=1 and u.id=" + userId);
		synchronized (this){
			try {
				if(card==null){
					return false;
				}
//				Tchannel tcl = channelDao.get("select t from Tchannel t  where t.status=3 and t.name='MINGSHENGZHITONGCHE' and t.userId="+ userId +"and t.type="+300 +"and t.payType=0");
//				if (tcl == null) {
//					if(!TroughMingSheng(TroughUtil.TroughWX(),Min_sheng_D0_cooperator,"民生微信D0直通车", merchantName, shortName, card, userId, 300, "WXZF", "FB_WX_", card.getUser().getIdNo(), 0l,shortType)){
//						return false;
//					}
//				}
//				
//				Tchannel tcl1 = channelDao.get("select t from Tchannel t  where t.status=3 and t.name='MINGSHENGZHITONGCHE' and t.userId="+ userId +"and t.type="+200 +"and t.payType=0");
//				if (tcl1 == null) {
//					if(!TroughMingSheng(TroughUtil.TroughZFB(),Min_sheng_D0_cooperator,"民生支付宝D0直通车", merchantName, shortName, card, userId, 200, "ZFBZF", "FB_ZFB_", card.getUser().getIdNo(), 0l,shortType)){
//						return false;
//					}
//				}
//				
//				Tchannel tcl2 = channelDao.get("select t from Tchannel t  where t.status=3 and t.name='MINGSHENGZHITONGCHE' and t.userId="+ userId +"and t.type="+1300 +"and t.payType=0");
//				if (tcl2 == null) {
//					if(!TroughMingSheng("2016062900190068",Min_sheng_D0_cooperator,"民生QQ支付D0直通车", merchantName, shortName, card, userId, 1300, "QQZF", "FB_QQ_", card.getUser().getIdNo(), 0l,shortType)){
//						return false;
//					}
//				}
//				
//				
//				Tchannel tcl3 = channelDao.get("select t from Tchannel t  where t.status=3 and t.name='MINGSHENGZHITONGCHE' and t.userId="+ userId +"and t.type="+300 +"and t.payType=1");
//				if (tcl3 == null) {
//					if(!TroughMingSheng(TroughUtil.TroughWX(),Min_sheng_D0_cooperator,"民生微信T1直通车", merchantName, shortName, card, userId, 300, "WXZF", "FB_WX_", card.getUser().getIdNo(), 1l,shortType)){
//						return false;
//					}
//				}
//				
//				Tchannel tcl4 = channelDao.get("select t from Tchannel t  where t.status=3 and t.name='MINGSHENGZHITONGCHE' and t.userId="+ userId +"and t.type="+200 +"and t.payType=1");
//				if (tcl4 == null) {
//					if(!TroughMingSheng(TroughUtil.TroughZFB(),Min_sheng_D0_cooperator,"民生支付宝T1直通车", merchantName, shortName, card, userId, 200, "ZFBZF", "FB_ZFB_", card.getUser().getIdNo(), 1l,shortType)){
//						return false;
//					}
//				}
//				
//				Tchannel tcl5 = channelDao.get("select t from Tchannel t  where t.status=3 and t.name='MINGSHENGZHITONGCHE' and t.userId="+ userId +"and t.type="+1300 +"and t.payType=1");
//				if (tcl5 == null) {
//					if(!TroughMingSheng("2016062900190068",Min_sheng_D0_cooperator,"民生QQ支付T1直通车", merchantName, shortName, card, userId, 1300, "QQZF", "FB_QQ_", card.getUser().getIdNo(), 1l,shortType)){
//						return false;
//					}
//				}
				
				Tchannel tcl6 = channelDao.get("select t from Tchannel t  where t.status=3 and t.name='XINKKEZHITONGCHE' and t.userId="+ userId +"and t.payType=0");
				if (tcl6 == null) {
					if(!TroughXinKe(merchantName, card, shortName,userId,0l)){
						return false;
					}
				}
				
				
				Tchannel tcl7 = channelDao.get("select t from Tchannel t  where t.status=3 and t.name='XINKKEZHITONGCHE' and t.userId="+ userId +"and t.payType=1");
				if (tcl7 == null) {
					if(!TroughXinKe(merchantName, card, shortName,userId,1l)){
						return false;
					}
				}
				
				card.setCardzhitong(1);
				cardDao.update(card);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	
		return true;
	}
	
	/**
	 * 批量修改通道信息为失败
	 */
	private void updateChannelStatusToFail(Long userId,String channelName){
		log.info("---开通平安子商户子商户---user_id为"+userId+",将所有开通的通道类型为"+channelName+"设置为失效");
		Channel c = new Channel();
		c.setName(channelName);
		c.setStatus(10);
		c.setUserId(userId);
		List<Tchannel> ls = channelService.searchTchannels(c);
		for(Tchannel l:ls){
			channelService.updateStatus(l.getId(),1);
		}
		
	}
	
	
	/**
	 * 创建平安直清模式的通道
	 * PINGANPAYZHITONGCHE_ZHIQING
	 * @param merchantName	商户全名
	 * @param shortName  商户简称
	 * @param userId  sys_user的id
	 * @return
	 */
	@Override
	public JSONObject createPingAnZhiQingMer(String merchantName,String shortName,Long userId){
		JSONObject res = new JSONObject();
		
		if (StringUtils.isEmpty(merchantName) || StringUtils.isEmpty(shortName) || userId==null){
			res.put("code", GlobalConstant.RESP_CODE_005);
			res.put("message", "开通失败，请检查填入信息是否有误");
			updateChannelStatusToFail(userId,"PINGANPAYZHITONGCHE_ZHIQING");
			return res;
		}
		
		//校验商户名称
		String regex = "^[\u4e00-\u9fa5]*$";
		if (!merchantName.matches(regex)) {
			res.put("code", GlobalConstant.RESP_CODE_005);
			res.put("message", "商户名称必须为中文");
			updateChannelStatusToFail(userId,"PINGANPAYZHITONGCHE_ZHIQING");
			return res;
		}
		
		
		//商户入驻清算平台
		if(!addcreatCommercialPAPay(merchantName, shortName, userId,"")){
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("message", "商户入驻清算平台失败");
			updateChannelStatusToFail(userId,"PINGANPAYZHITONGCHE_ZHIQING");
			return res;
		}
		
		//商户入驻清算平台--支付宝单独开通
		if(!addcreatCommercialPAPayForZhiFuBaoAlone(merchantName, shortName,  userId,"")){
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("message", "商户入驻清算平台单独入驻支付宝失败");
			updateChannelStatusToFail(userId,"PINGANPAYZHITONGCHE_ZHIQING");
			return res;
		}
		
		//配置平安支付宝通道. 未避免支付宝入驻限额导致所有配置类型无法配置，将该代码迁移至交易逻辑中进行判断
//		ChannelPayRef cpr200 = getChannelPayRef(false, "200", userId, "","PINGANPAYZHITONGCHE_ZHIQING");
//		Map<String,String> openAli = addPingAnZhiFuBaoMer(cpr200.getChannel().getId());
//		if(!openAli.get("code").equals("000")){
//			res.put("code", GlobalConstant.RESP_CODE_999);
//			res.put("message", "商户配置支付宝通道失败");
//			updateChannelStatusToFail(userId,"PINGANPAYZHITONGCHE_ZHIQING");
//			return res;
//		}
		
		
		//微信入驻
		ChannelPayRef cpr300 = getChannelPayRef(false, "300", userId, "","PINGANPAYZHITONGCHE_ZHIQING");
		Map<String,String> openWx = addPingAnWeiXinMer(cpr300.getChannel().getId());
		if(!openWx.get("code").equals("000")){
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("message", "子商户微信入驻失败");
			updateChannelStatusToFail(userId,"PINGANPAYZHITONGCHE_ZHIQING");
			return res;
		}
		
		//微信-授权目录
		Map<String,String> openWxSup1 = addPingAnWeiXinSupplement(cpr300.getChannel().getId(),"jsapi_path",PinganPaymentUtil.jsapi_path);
		if(!openWxSup1.get("code").equals("000")){
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("message", "子商户微信授权目录匹配错误");
			updateChannelStatusToFail(userId,"PINGANPAYZHITONGCHE_ZHIQING");
			return res;
		}
		
		
		//微信--关联微信appid
		Map<String,String> openWxSup2 = addPingAnWeiXinSupplement(cpr300.getChannel().getId(),"sub_appid",PinganPaymentUtil.wx_app_id);
		if(!openWxSup2.get("code").equals("000")){
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("message", "子商户微信关联微信公众号appid错误");
			updateChannelStatusToFail(userId,"PINGANPAYZHITONGCHE_ZHIQING");
			return res;
		}
		
		
		//微信--推荐关注appID
		Map<String,String> openWxSup3 = addPingAnWeiXinSupplement(cpr300.getChannel().getId(),"subscribe_appid",PinganPaymentUtil.wx_app_id);
		if(!openWxSup3.get("code").equals("000")){
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("message", "子商户微信推荐关注appId错误");
			updateChannelStatusToFail(userId,"PINGANPAYZHITONGCHE_ZHIQING");
			return res;
		}
		
		
		
		
		//平安普通类型子商户开通完成后，开始补充报备翼支付
		ChannelPayRef cprWx = getChannelPayRef(false, "300", userId, "0","PINGANPAYZHITONGCHE_ZHIQING");
		JSONObject configWx =JSONObject.parseObject(cprWx.getChannel().getConfig());
		if(!addPingAnCreateYZFMer(configWx.getString("pingan.merchant_id"), userId)){
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("message", "商户报备翼支付失败");
			updateChannelStatusToFail(userId,"PINGANPAYZHITONGCHE_ZHIQING");
			return res;
		}
		
		
		res.put("code", GlobalConstant.RESP_CODE_SUCCESS);
		res.put("message", "子商户报备成功");
		//将老的通道设置为失效
		updateChannelStatusToFail(userId,"PINGANPAYZHITONGCHE");
		return res;
	};
	
	
	
	
	
	/**
	 * 报备平安--微信子商户配置
	 * @param channelId
	 * @return
	 */
	@Override
	public  Map<String,String> addPingAnWeiXinSupplement(Long channelId,String objStr,String objVal){
		Map<String,String> res = new HashMap<String,String>();
		Channel channel = channelService.get(channelId);
		JSONObject changeConfig = JSONObject.parseObject(channel.getConfig());
		if(!changeConfig.containsKey("sub_wx_supplement") || StringUtil.isBlank(changeConfig.getString("sub_wx_supplement"))){
			String merchant_id = changeConfig.getString("pingan.merchant_id");
			log.info("--报备平安--微信子商户配置--商户在平安的id为"+merchant_id);
			FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest req = new FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest();
			req.setStore_id(merchant_id);
			if(objStr.equals("jsapi_path")){
				req.setJsapi_path(objVal);
			}else if (objStr.equals("sub_appid")){
				req.setSub_appid(objVal);
			}else if(objStr.equals("subscribe_appid")){
				req.setSubscribe_appid(objVal);
			}else{
				res.put("code", GlobalConstant.RESP_CODE_999);
				res.put("messgae", "接口参数错误");
				return res;
			}
			
			req.setPay_type("3");
			log.info("--报备平安--微信子商户配置--请求参数为"+JSONObject.toJSONString(req));
			JSONObject reqBack = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsLiquidationWxSubmerchantConfigCreateSupplementRequest.class);
			log.info("--报备平安--微信子商户配置--返回参数为"+reqBack.toJSONString());
			if(reqBack!= null){
				if(reqBack.getBoolean("success")){
					//保存进入通道中
					changeConfig.put("sub_wx_supplement_"+objStr, objVal);
					channel.setConfig(changeConfig.toJSONString());
					channelService.updateConfig(channel);
					
					//将对应的聚合码通道也配置
					//查询对应的支付宝220聚合码专用通道，同时进行保存
					ChannelPayRef cpr320 = getChannelPayRef(false, "320", channel.getUserId(), "1","PINGANPAYZHITONGCHE_ZHIQING");
					if(cpr320==null){
						//重新保存一个专用于聚合码,并将支付宝入驻配置同步
						addPINGANPAYChannel(channel.getDetailName(), 320, channel.getConfig(), channel.getUserId(), channel.getMerchantId());	//聚合码专用
					}else{
						//更新支付宝聚合码入驻配置情况
						Channel channel320 = cpr320.getChannel();
						channel320.setConfig(changeConfig.toJSONString());
						channelService.updateConfig(channel320);
					}
					
					res.put("code", GlobalConstant.RESP_CODE_SUCCESS);
					res.put("messgae", reqBack.getString("return_value"));	//微信子商户配置新增成功
				}else{
					log.error("平安下单---报备平安微信商户接口--在平安的商户号为"+merchant_id+"返回值异常:"+reqBack.getString("error_message"));
					res.put("code", GlobalConstant.RESP_CODE_999);
					res.put("messgae", reqBack.getString("error_message"));
				}
			}else{
				log.error("平安下单---报备平安微信商户接口--请求失败,在平安的商户号为"+merchant_id);
				res.put("code", GlobalConstant.RESP_CODE_999);
				res.put("messgae", "请求平安失败");
			}
				
		}else{
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("messgae", "微信子商户入驻，子商户重复入驻！");
		}
		
		return res;
		
	};
	
	
	@Override
	public Map<String, String> checkPingAnChannelCorrect(Long channelId){
		Map<String,String> res = new HashMap<String,String>();
		Channel channel = channelService.get(channelId);
		JSONObject changeConfig = JSONObject.parseObject(channel.getConfig());
		String merchant_id = changeConfig.getString("pingan.merchant_id");
		FshowsLiquidationSubmerchantQueryRequest req = new FshowsLiquidationSubmerchantQueryRequest();
		req.setSub_merchant_id(merchant_id);
		JSONObject queryMerInfo = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantQueryRequest.class).getJSONObject("return_value");
		String queryName ="";
		String queryAliasName ="";
		String queryCategoryId = "";	//平安规定的行业类目，跟微信的规范不同
		String queryServicePhone = "";
		if(queryMerInfo ==null){
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("messgae", "检测平安商户信息的准确性，请求平安失败");
			return res;
		}else{
			if(StringUtil.isNotBlank(queryMerInfo.getString("name")) 
					&& StringUtil.isNotBlank(queryMerInfo.getString("alias_name"))
					&& StringUtil.isNotBlank(queryMerInfo.getString("category_id"))
					&& StringUtil.isNotBlank(queryMerInfo.getString("service_phone"))){
				queryName = queryMerInfo.getString("name");
				queryAliasName = queryMerInfo.getString("alias_name");
				queryCategoryId = queryMerInfo.getString("category_id");
				queryServicePhone = queryMerInfo.getString("service_phone");
				
				if( (channel.getDetailName().equals(queryName) || channel.getDetailName().equals(queryAliasName))
						&& queryCategoryId.equals("2015080600000001")
						&& queryServicePhone.equals("13052222696")){
					res.put("code", GlobalConstant.RESP_CODE_SUCCESS);
					res.put("name", queryName);
					res.put("aliasName", queryAliasName);
					return res;
				}else{
					res.put("code", GlobalConstant.RESP_CODE_999);
					res.put("messgae", "本地保存信息与平安报备的信息不一致");
					//将该通道状态设置为失效
					channelService.updateStatus(channelId,1);
					return res;
				}
			}else{
				res.put("code", GlobalConstant.RESP_CODE_999);
				res.put("messgae", "检测平安商户信息的准确性，请求返回的参数异常");
				return res;
			}
		}
	};
	
	
	/**
	 * 报备平安-微信补充接口
	 * @return
	 */
	@Override
	public Map<String,String> addPingAnWeiXinMer(Long channelId){
		Map<String,String> res = new HashMap<String,String>();
		Channel channel = channelService.get(channelId);
		JSONObject changeConfig = JSONObject.parseObject(channel.getConfig());
		if(!changeConfig.containsKey("sub_wx_id") || StringUtil.isBlank(changeConfig.getString("sub_wx_id"))){
			String merchant_id = changeConfig.getString("pingan.merchant_id");
			log.info("--报备平安-微信补充接口--查询商户在平安的信息，商户在平安的id为"+merchant_id);
			FshowsLiquidationSubmerchantQueryRequest req = new FshowsLiquidationSubmerchantQueryRequest();
			req.setSub_merchant_id(merchant_id);
			JSONObject queryMerInfo = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantQueryRequest.class).getJSONObject("return_value");
			String queryName ="";
			String queryAliasName ="";
			String queryCategoryId = "";	//平安规定的行业类目，跟微信的规范不同
			String queryServicePhone = "";
			if(queryMerInfo !=null){
				if(StringUtil.isNotBlank(queryMerInfo.getString("name")) 
						&& StringUtil.isNotBlank(queryMerInfo.getString("alias_name"))
						&& StringUtil.isNotBlank(queryMerInfo.getString("category_id"))
						&& StringUtil.isNotBlank(queryMerInfo.getString("service_phone"))){
					queryName = queryMerInfo.getString("name");
					queryAliasName = queryMerInfo.getString("alias_name");
					queryCategoryId = queryMerInfo.getString("category_id");
					queryServicePhone = queryMerInfo.getString("service_phone");
					
				}else{
					log.error("--报备平安-微信补充接口---报备微信商户接口前查询商户信息，返回值异常");
					res.put("code", GlobalConstant.RESP_CODE_999);
					res.put("messgae", "报备微信商户接口前查询商户信息，返回值异常");
				}
			}else{
				log.error("--报备平安-微信补充接口---报备微信商户接口前查询商户信息,连接平安失败");
				res.put("code", GlobalConstant.RESP_CODE_999);
				res.put("messgae", "报备微信商户接口前查询商户信息,连接平安失败");
			}
			
			
			
			//调用报备平安微信商户接口
			log.info("--报备平安-微信补充接口--报备平安微信商户接口，商户在平安的id为"+merchant_id);
			FshowsLiquidationWxSubmerchantCreateSupplementRequest wxReq = new FshowsLiquidationWxSubmerchantCreateSupplementRequest();
			wxReq.setStore_id(merchant_id);	//商户的id
			wxReq.setMerchant_name(PinganPaymentUtil.merchant_name); //商户名称,作为主体名称，与微信公众号进行校验
			wxReq.setMerchant_shortname(queryAliasName);  //商户名称，显示给消费者看的
			wxReq.setService_phone(queryServicePhone);	//客服电话，方便微信在必要时能联系上商家，会在支付详情展示给消费者
			wxReq.setBusiness("308");  //经营行业类目
			wxReq.setPay_type("3");	//3为当面付
			log.info("--报备平安-微信补充接口--报备平安微信商户接口--请求参数为"+JSONObject.toJSONString(req));
			JSONObject wxResult = PinganPaymentUtil.sentRequstToPingAnPayment(wxReq, PinganPaymentUtil.appId, FshowsLiquidationWxSubmerchantCreateSupplementRequest.class);
			log.info("--报备平安-微信补充接口--报备平安微信商户接口--返回参数为"+wxResult.toJSONString());
			if(wxResult != null){
				if (wxResult.getBooleanValue("success")) {
					String sub_mch_id = wxResult.getJSONObject("return_value").getString("sub_mch_id");
					//保存进入通道中
					changeConfig.put("sub_wx_id", sub_mch_id);
					changeConfig.put("sub_wx_merchant_name", wxReq.getMerchant_name());
					changeConfig.put("sub_wx_merchant_shortname", wxReq.getMerchant_shortname());
					channel.setConfig(changeConfig.toJSONString());
					channelService.updateConfig(channel);
					
					//将对应的聚合码通道也配置
					//查询对应聚合码专用通道，同时进行保存
					ChannelPayRef cpr320 = getChannelPayRef(false, "320", channel.getUserId(), "1","PINGANPAYZHITONGCHE_ZHIQING");
					if(cpr320==null){
						//重新保存一个专用于聚合码,并将支付宝入驻配置同步
						addPINGANPAYChannel(channel.getDetailName(), 320, channel.getConfig(), channel.getUserId(), channel.getMerchantId());	//聚合码专用
					}else{
						//更新支付宝聚合码入驻配置情况
						Channel channel320 = cpr320.getChannel();
						channel320.setConfig(changeConfig.toJSONString());
						channelService.updateConfig(channel320);
					}
					res.put("code", GlobalConstant.RESP_CODE_SUCCESS);
					res.put("messgae", "SUCCESS");
				}else{
					log.error("平安下单---报备平安微信商户接口，返回值异常:"+wxResult.getString("error_message"));
					if( wxResult.getString("error_message").equals("微信子商户入驻，子商户重复入驻！")){
						changeConfig.put("sub_wx_id", "微信子商户入驻，子商户重复入驻");
						channel.setConfig(changeConfig.toJSONString());
						channelService.updateConfig(channel);	//保存重复状态，防止重复请求
					}
					
					res.put("code", wxResult.getString("error_code"));
					res.put("messgae", wxResult.getString("error_message"));
				}
			}else {
				log.error("--报备平安-微信补充接口--开通微信子商户入驻补充接口，连接平安失败，平安商户的id为"+merchant_id);
				res.put("code", GlobalConstant.RESP_CODE_999);
				res.put("messgae", "请求平安失败");
			}
		}else{
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("messgae", "微信子商户入驻，子商户重复入驻！");
		}
		
		return res;
		
	}
	
	
	
	
	/**
	 * 报备平安-支付宝补充接口
	 * @return
	 */
	@Override
	public  Map<String,String> addPingAnZhiFuBaoMer(Long channelId){
		Map<String,String> res = new HashMap<String,String>();
		
		Channel channel = channelService.get(channelId);
		
		JSONObject channelConfig = JSONObject.parseObject(channel.getConfig());
		
		FshowsAliPayMerchantCreateRequest req = new FshowsAliPayMerchantCreateRequest();
		req.setStore_id(channelConfig.getString("pingan.merchant_id"));
		log.info("-----平安-支付宝补充接口----请求参数为"+JSONObject.toJSONString(req));
		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsAliPayMerchantCreateRequest.class);
		log.info("-----平安-支付宝补充接口----返回参数为"+result.toJSONString());
		if(result!=null){
			if(result.containsKey("return_value")){
				String status = result.getJSONObject("return_value").getString("status");
				if(status.equals("1")){	//1代表报备成功
					//保存报备信息
					JSONObject config = JSONObject.parseObject(channel.getConfig());
					config.put("pingan.add.ali", "success");
					channel.setConfig(config.toJSONString());
					channelService.updateConfig(channel);
					//查询对应的支付宝220聚合码专用通道，同时进行保存
					ChannelPayRef cpr220 = getChannelPayRef(false, "220", channel.getUserId(), "1","PINGANPAYZHITONGCHE_ZHIQING");
					if(cpr220==null){
						//重新保存一个专用于聚合码,并将支付宝入驻配置同步
						addPINGANPAYChannel(channel.getDetailName(), 220, channel.getConfig(), channel.getUserId(), channel.getMerchantId());	//聚合码专用
					}else{
						//更新支付宝入驻配置情况
						Channel channel220 = cpr220.getChannel();
						channel220.setConfig(config.toJSONString());
						channelService.updateConfig(channel220);
					}
					
					
					res.put("code", GlobalConstant.RESP_CODE_SUCCESS);
					res.put("messgae", "SUCCESS");
				}else{
					res.put("code", "998");
					if(status.equals("0")){
						res.put("messgae", result.getString("商户状态为关闭"));
					}
					if(status.equals("2")){
						res.put("messgae", result.getString("审核不通过"));
					}
					if(status.equals("3")){
						res.put("messgae", result.getString("未审核"));
					}
				}
				
			}else{
				res.put("code", GlobalConstant.RESP_CODE_999);
				res.put("messgae", result.getString("error_message"));
			}
		}else{
			res.put("code", GlobalConstant.RESP_CODE_999);
			res.put("messgae", "请求平安失败");
		}
		return res;
	}
	
	
	/**
	 * 开通翼支付直通车
	 * @param sub_merchant_id 入驻平安返回的子商户id
	 * @param merchantName 入驻平安时的商户名
	 * @param userId
	 * @param shortType
	 * @return
	 */
	@Override
	public Boolean addPingAnCreateYZFMer(String sub_merchant_id,Long userId){
		
		
		//查询平安报备的信息
		JSONObject merInfoRes = pingAnService.queryPingAnMerInfo(sub_merchant_id, PinganPaymentUtil.appId);
		if(!merInfoRes.getString("respCode").equals("000")){
			return false;
		}
		//调用报备翼支付接口
		JSONObject merInfo = merInfoRes.getJSONObject("respDesc");
		JSONObject yzfMer = pingAnService.createYZFMer(sub_merchant_id, merInfo.getString("name"), PinganPaymentUtil.appId); 
		if(!yzfMer.getString("respCode").equals("000")){
			return false;
		}
		//保存直通车信息
		log.info("------保存翼支付直通车信息,user_id = "+userId+",子商户id="+sub_merchant_id,"开始");
		
	    Map<String,String> configMap = new LinkedHashMap<String,String>();
	 	configMap.put("pingan.appId", PinganPaymentUtil.appId);
	    configMap.put("pingan.merchant_id", sub_merchant_id);
	    configMap.put("pingan.notifyUrl", "https://bbpurse.com/flypayfx/payment/pinganPayNotify");
	    configMap.put("pingan.createIp", "127.0.0.1");
	    configMap.put("pingan.yzf.craete", "success");
	    String configArray = JSONArray.fromObject(configMap).toString();
	    String config = configArray.substring(1, configArray.length()-1);
		addPINGANPAYChannel(merInfo.getString("name"), 1100, config, userId, merInfo.getString("external_id"));
		addPINGANPAYChannel(merInfo.getString("name"), 1120, config, userId, merInfo.getString("external_id"));	//聚合码支付专用
		log.info("------保存翼支付直通车信息,user_id = "+userId+",子商户id="+sub_merchant_id,"成功");
		return true;
	};	
	
	
	
	
	
	
	/**
	 * 创建平安子商户（普通）--为支付宝单独开通
	 * @param merchantName
	 * @param shortName
	 * @param userId
	 * @param shortType
	 * @return
	 */
	@Override
	public Boolean addcreatCommercialPAPayForZhiFuBaoAlone(String merchantName,String shortName,Long userId,String shortType){
		TuserCard card = cardDao.get("select t from TuserCard t left join t.user u where t.isSettlmentCard=1 and u.id=" + userId);
		synchronized (this){
			try {
				if(card==null){
					return false;
				}
				Tchannel c = channelDao.get("select t from Tchannel t  where t.status=0 and t.type = 200 and t.name='PINGANPAYZHITONGCHE_ZHIQING' and t.userId="+ userId +"and t.payType=0");
				if (c == null) {
					FshowsLiquidationSubmerchantCreateRequest req = new FshowsLiquidationSubmerchantCreateRequest();
					try {
						//未避免在开通的时候，和微信等开通逻辑生成相同的patt，这里暂停下线程
						Thread.sleep(500);
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
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
					log.info("------用户单独为支付宝开通子商户----用户userId="+userId + ",请求平安入驻商户接口请求参数为："+JSONObject.toJSONString(req));
					JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantCreateRequest.class);
					log.info("------用户单独为支付宝开通子商户----用户userId="+userId + ",请求平安入驻商户接口返回结果为："+result.toJSONString());
					if (result == null) {
						log.info("------用户单独为支付宝开通子商户----用户userId="+userId + ",请求平安入驻商户接口请求失败");
						return false;
					}
					if (!result.getBooleanValue("success")) {
						return false;
					}
					String sub_merchant_id = result.getJSONObject("return_value").getString("sub_merchant_id");
					//开始绑卡
					FshowsLiquidationSubmerchantBankBindRequest bindReq = new FshowsLiquidationSubmerchantBankBindRequest();
					bindReq.setSub_merchant_id(sub_merchant_id);
					bindReq.setBank_card_no(card.getCardNo());
					bindReq.setCard_holder(card.getUser().getRealName());
					log.info("------用户单独为支付宝开通子商户----用户userId="+userId + ",请求平安绑卡接口请求参数为："+JSONObject.toJSONString(bindReq));
					JSONObject bindResult = PinganPaymentUtil.sentRequstToPingAnPayment(bindReq, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantBankBindRequest.class);
					log.info("------用户单独为支付宝开通子商户----用户userId="+userId + ",请求平安绑卡接口返回结果为："+bindResult.toJSONString());
					if (bindResult == null) {
						log.info("------用户单独为支付宝开通子商户----用户userId="+userId + ",请求平安平安绑卡接口请求失败");
						return false;
					}
					if(!bindResult.getBooleanValue("success")){
						return false;
					}
					//设置商户终端费率接口
//					FshowsLiquidationSubmerchantRateSetRequest rateset = new FshowsLiquidationSubmerchantRateSetRequest();
//					rateset.setSub_merchant_id(sub_merchant_id);
//				    rateset.setMerchant_rate("0.0035");		//先设置默认，实际交易时会再次变更
//				    log.info("------用户单独为支付宝开通子商户----用户userId="+userId + ",请求平安商户终端费率设置接口，请求参数为："+JSONObject.toJSONString(rateset));
//				    JSONObject rateResult = PinganPaymentUtil.sentRequstToPingAnPayment(rateset, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantRateSetRequest.class);
//				    log.info("------用户单独为支付宝开通子商户----用户userId="+userId + ",请求平安商户终端费率设置接口，返回结果为："+rateResult.toJSONString());
//				    if(rateResult==null){
//				    	log.info("------用户单独为支付宝开通子商户----用户userId="+userId + ",请求平安商户终端费率设置接口请求失败");
//				    	return false;
//				    }
//				    if(!rateResult.getBooleanValue("success")){
//						return false;
//					}
					
					
//				    String config = "{\"pingan.appId\":\"20161102104403706\",\"pingan.merchant_id\": \"%s\",\"pingan.notifyUrl\": \"https://bbpurse.com/flypayfx/payment/pinganPayNotify\",\"pingan.createIp\": \"127.0.0.1\"}";
				    
				    Map<String,String> configMap = new LinkedHashMap<String,String>();
				    configMap.put("pingan.appId", PinganPaymentUtil.appId);
				    configMap.put("pingan.merchant_id", sub_merchant_id);
				    configMap.put("pingan.notifyUrl", "https://bbpurse.com/flypayfx/payment/pinganPayNotify");
				    configMap.put("pingan.createIp", "127.0.0.1");
				    String configArray = JSONArray.fromObject(configMap).toString();
				    String config = configArray.substring(1, configArray.length()-1);
				    log.info("------用户单独为支付宝开通子商户----用户userId="+userId + "保存支付宝通道信息--开始");
				    addPINGANPAYChannel(merchantName, 200, config, userId, patt);
				    addPINGANPAYChannel(merchantName, 220, config, userId, patt);	//聚合码专用
//				    addPINGANPAYChannel(merchantName, 300, config, userId, patt);
//				    addPINGANPAYChannel(merchantName, 900, config, userId, patt);
				    log.info("------用户单独为支付宝开通子商户----用户userId="+userId + "保存支付宝通道信息--开始");
				}
				card.setCardzhitong(1);
				cardDao.update(card);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	};
	
	
	
	/**
	 * 开通平安子商户
	 */
	@Override
	public  Boolean addcreatCommercialPAPay(String merchantName, String shortName,Long userId,String shortType) {
		TuserCard card = cardDao.get("select t from TuserCard t left join t.user u where t.isSettlmentCard=1 and u.id=" + userId);
		synchronized (this){
			try {
				if(card==null){
					return false;
				}
				Tchannel c = channelDao.get("select t from Tchannel t  where t.status=0 and t.name='PINGANPAYZHITONGCHE_ZHIQING' and t.userId="+ userId +"and t.payType=0");
				if (c == null) {
					FshowsLiquidationSubmerchantCreateRequest req = new FshowsLiquidationSubmerchantCreateRequest();
					String patt = "PATT_" + DateUtil.convertCurrentDateTimeToString();
					req.setExternal_id(patt);
					req.setName(merchantName);
//					req.setName("上海福别信息技术服务有限公司 ");
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
					log.info("------用户开通子商户----用户userId="+userId + ",请求平安入驻商户接口请求参数为："+JSONObject.toJSONString(req));
					JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantCreateRequest.class);
					log.info("------用户开通子商户----用户userId="+userId + ",请求平安入驻商户接口返回结果为："+result.toJSONString());
					if (result == null) {
						log.info("------用户开通子商户----用户userId="+userId + ",请求平安入驻商户接口请求失败");
						return false;
					}
					if (!result.getBooleanValue("success")) {
						return false;
					}
					String sub_merchant_id = result.getJSONObject("return_value").getString("sub_merchant_id");
					//开始绑卡
					FshowsLiquidationSubmerchantBankBindRequest bindReq = new FshowsLiquidationSubmerchantBankBindRequest();
					bindReq.setSub_merchant_id(sub_merchant_id);
					bindReq.setBank_card_no(card.getCardNo());
					bindReq.setCard_holder(card.getUser().getRealName());
					log.info("------用户开通子商户----用户userId="+userId + ",请求平安绑卡接口请求参数为："+JSONObject.toJSONString(bindReq));
					JSONObject bindResult = PinganPaymentUtil.sentRequstToPingAnPayment(bindReq, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantBankBindRequest.class);
					log.info("------用户开通子商户----用户userId="+userId + ",请求平安绑卡接口返回结果为："+bindResult.toJSONString());
					if (bindResult == null) {
						log.info("------用户开通子商户----用户userId="+userId + ",请求平安平安绑卡接口请求失败");
						return false;
					}
					if(!bindResult.getBooleanValue("success")){
						return false;
					}
					//设置商户终端费率接口
//					FshowsLiquidationSubmerchantRateSetRequest rateset = new FshowsLiquidationSubmerchantRateSetRequest();
//					rateset.setSub_merchant_id(sub_merchant_id);
////				    rateset.setMerchant_rate("0.0035");	//先设置默认，实际交易时会再次变更
//				    rateset.setMerchant_rate("0.0010");	//先设置默认，实际交易时会再次变更
//				    log.info("------用户开通子商户----用户userId="+userId + ",请求平安商户终端费率设置接口，请求参数为："+JSONObject.toJSONString(rateset));
//				    JSONObject rateResult = PinganPaymentUtil.sentRequstToPingAnPayment(rateset, PinganPaymentUtil.appId, FshowsLiquidationSubmerchantRateSetRequest.class);
//				    log.info("------用户开通子商户----用户userId="+userId + ",请求平安商户终端费率设置接口，返回结果为："+rateResult.toJSONString());
//				    if(rateResult==null){
//				    	log.info("------用户开通子商户----用户userId="+userId + ",请求平安商户终端费率设置接口请求失败");
//				    	return false;
//				    }
//				    if(!rateResult.getBooleanValue("success")){
//						return false;
//					}
					
//				    String config = "{\"pingan.appId\":\"20161102104403706\",\"pingan.merchant_id\": \"%s\",\"pingan.notifyUrl\": \"https://bbpurse.com/flypayfx/payment/pinganPayNotify\",\"pingan.createIp\": \"127.0.0.1\"}";
				  
				    Map<String,String> configMap = new LinkedHashMap<String,String>();
				    configMap.put("pingan.appId", PinganPaymentUtil.appId);
				    configMap.put("pingan.merchant_id",sub_merchant_id);
				    configMap.put("pingan.notifyUrl","https://bbpurse.com/flypayfx/payment/pinganPayNotify");
				    configMap.put("pingan.createIp", "127.0.0.1");
				    String configArray = JSONArray.fromObject(configMap).toString();
				    String config = configArray.substring(1, configArray.length()-1);
				    
				    log.info("------用户开通子商户----用户userId="+userId + "保存微信与京东通道信息--开始");
//				    addPINGANPAYChannel(merchantName, 200, config, userId, sub_merchant_id, patt);	//开通支付宝的子商户功能单独拉出来 addcreatCommercialPAPayForZhiFuBaoAlone
				    addPINGANPAYChannel(merchantName, 300, config, userId, patt);
				    addPINGANPAYChannel(merchantName, 900, config, userId, patt);
				    //同时添加聚合码支付通道保存
				    addPINGANPAYChannel(merchantName, 320, config, userId, patt);
				    addPINGANPAYChannel(merchantName, 920, config, userId, patt);
				    log.info("------用户开通子商户----用户userId="+userId + "保存微信与京东通道信息--成功");
				}
				card.setCardzhitong(1);
				cardDao.update(card);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}



	@Override
	public ChannelPayRef getChannelPayRef(Boolean bool,String type, Long userId,String accType,String channelName) {
		String hql = "select t from Tchannel t  where t.status=10 ";
		if(userId!=null){
			hql+="and t.userId="+ userId;
		}
		hql+=" and t.type="+type+" and t.name= '"+channelName+"'";
		
		List<Tchannel> listtcl = channelDao.find(hql);
		if (listtcl.size() == 0) {
			return null;
		}
		Tchannel tcl = listtcl.get(0);
		Channel cnl = new Channel();
		BeanUtils.copyProperties(tcl, cnl);
		ChannelPayRef cpr = new ChannelPayRef();
		if(tcl.getName().equals("MINGSHENGZHITONGCHE")){
			cpr.setChannelPaymentService(minshengPaymentService);
		}else if(tcl.getName().equals("XINKKEZHITONGCHE")){
			cpr.setChannelPaymentService(xinkePaymentService);
		}else if(tcl.getName().equals("ZHEYANGJIFENZTC")){
			cpr.setChannelPaymentService(zheYangPaymentService);
		}else if (tcl.getName().equals("ZHEYANGJFZTC")) {
			cpr.setChannelPaymentService(zheYangPaymentService);
		}else if (tcl.getName().equals("WEILIANBAOJFZTC")) {
			cpr.setChannelPaymentService(weiLianBaoPaymentService);
		}else if (tcl.getName().equals("YIQIANG2JFZTC")) {
			cpr.setChannelPaymentService(yiQiang2PaymentService);
		}
		else if(tcl.getName().equals("XINKKEYINLIAN")){
			cpr.setChannelPaymentService(xinkeYinLianPaymentService);
		}else if(tcl.getName().equals("PINGANPAYZHITONGCHE")  ||  tcl.getName().equals("PINGANPAYZHITONGCHE_ZHIQING")){
			//校验平安通道的可用性 --start
			//因平安报备商户可能存在报备的id和实际姓名不对照，校验平安通道是否可用  at 2017-11-16 by liangchao
				JSONObject config = JSONObject.parseObject(tcl.getConfig());
				String appId = config.getString("pingan.appId");
				String merId = config.getString("pingan.merchant_id");
				FshowsLiquidationSubmerchantQueryRequest req = new FshowsLiquidationSubmerchantQueryRequest();
				req.setSub_merchant_id(merId);
				log.info("----查询平安支付可用通道时，校验与平安备注的信息是否一致--平安支付通道id=" + tcl.getId() + "----请求平安查询商户接口，请求参数为"+JSONObject.toJSONString(req));
				JSONObject validatInfo = PinganPaymentUtil.sentRequstToPingAnPayment(req, appId, FshowsLiquidationSubmerchantQueryRequest.class).getJSONObject("return_value");
				if(!validatInfo.getString("service_phone").equals("13052222696") 
					||!(validatInfo.getString("name").equals(tcl.getDetailName())	//商户全称
						||validatInfo.getString("alias_name").equals(tcl.getDetailName())	//商户简称
						)
					){
					log.error("平安支付通道id=" + tcl.getId() + "通道的报备出错，detail_name和平安存储信息不对称，该通道不可用。");
					channelService.updateStatus(tcl.getId(),1);	//设置通道为失效
					return null;
				}
				log.info("----查询平安支付可用通道时，校验与平安备注的信息是否一致--平安支付通道id=" + tcl.getId() + "----校验成功，结果一致");
			//校验平安通道的可用性 --end
			
			cpr.setChannelPaymentService(pinganPaymentService);
		}else if(tcl.getName().equals("YILIANYINLIANJIFENZTC")){
			cpr.setChannelPaymentService(yfyianZXXEPaymentService);
		}else if(tcl.getName().equals("YIBAOZHITONGCHE")){
			cpr.setChannelPaymentService(yiBaoPaymentService);
		}else if(tcl.getName().equals("GAZHIYINLIANJIFENZHITONGCHE")){
			cpr.setChannelPaymentService(gaZhiYinLianPaymentService);
		}else if(tcl.getName().equals("WEILIANBAOYINLIANJIFENZHITONGCHE")){
			cpr.setChannelPaymentService(weiLianBaoPaymentService);
		}
		cpr.setChannel(cnl);
		cpr.setConfig(channelService.getChannelConfig(tcl.getId()));
		return cpr;
	}



	@Override
	public List<Map<String, String>> getqueryfeethroughtrain() {
		Map<String, String> mapwx = new HashMap<String, String>();
		Map<String, String> mapzf = new HashMap<String, String>();
		Map<String, String> mapqq = new HashMap<String, String>();
		Map<String, String> mapjd = new HashMap<String, String>();
		Map<String, String> mapyl = new HashMap<String, String>();
		Map<String, String> mapjf = new HashMap<String, String>();
		
		mapwx.put("typename", "微信");
		mapwx.put("typevalue", "300");
		mapwx.put("TrougD0tradeRate", TrougWXD0tradeRate);
		mapwx.put("TrougT1tradeRate", TrougWXT1tradeRate);
		mapwx.put("TrougMaxAmt", TrougWXMaxAmt);
		mapwx.put("TrougMinAmt", TrougWXMinAmt);
		
		mapzf.put("typename", "支付宝");
		mapzf.put("typevalue", "200");
		mapzf.put("TrougD0tradeRate", TrougZFBD0tradeRate);
		mapzf.put("TrougT1tradeRate", TrougZFBT1tradeRate);
		mapzf.put("TrougMaxAmt", TrougZFBMaxAmt);
		mapzf.put("TrougMinAmt", TrougZFBMinAmt);
		
		
		mapjd.put("typename", "京东");
		mapjd.put("typevalue", "900");
		mapjd.put("TrougD0tradeRate", TrougJDD0tradeRate);
		mapjd.put("TrougT1tradeRate", TrougJDT1tradeRate);
		mapjd.put("TrougMaxAmt", TrougJDMaxAmt);
		mapjd.put("TrougMinAmt", TrougJDMinAmt);
		
		mapqq.put("typename", "QQ钱包");
		mapqq.put("typevalue", "1300");
		mapqq.put("TrougD0tradeRate", TrougQQZFD0tradeRate);
		mapqq.put("TrougT1tradeRate", TrougQQZFT1tradeRate);
		mapqq.put("TrougMaxAmt", TrougQQZFMaxAmt);
		mapqq.put("TrougMinAmt", TrougQQZFMinAmt);
		
		mapyl.put("typename", "银联在线");
		mapyl.put("typevalue", "500");
		mapyl.put("TrougD0tradeRate", TrougYLD0tradeRate);
		mapyl.put("TrougT1tradeRate", TrougYLT1tradeRate);
		mapyl.put("TrougMaxAmt", "20000.00");
		mapyl.put("TrougMinAmt", "16000.00");
		
		
		mapjf.put("typename", "银联积分");
		mapjf.put("typevalue", "550");
		mapjf.put("TrougD0tradeRate", TrougYLD0tradeRate);
		mapjf.put("TrougT1tradeRate", TrougYLT1tradeRate);
		mapjf.put("TrougMaxAmt", TrougYLMaxAmt);
		mapjf.put("TrougMinAmt", TrougYLMinAmt);
		
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		maps.add(mapwx);
		maps.add(mapzf);
		maps.add(mapqq);
		maps.add(mapyl);
//		maps.add(mapjd);
		maps.add(mapjf);
		
		return maps;
	}
	
	
	public  boolean TroughPingAn(String merchantName,String shortName,TuserCard card,Long userId,Integer type){
		int k = 0;
		FshowsLiquidationSubmerchantCreateRequest req = new FshowsLiquidationSubmerchantCreateRequest();
		req.setExternal_id("PATT_" + DateUtil.convertCurrentDateTimeToString() + (k++));
		req.setName(merchantName);
		req.setAlias_name(shortName);
		req.setService_phone("13052222696");
		req.setCategory_id("2016062900190068");
		req.setId_card_name("张瑜婷");
		req.setId_card_num("310115198811254020");
		req.setStore_address("上海市浦东新区顺和路");
		req.setId_card_hand_img_url("https://bbpurse.com/flypayfx/mobile/getImage/20160909a7f617d569954c6cbd48ed38bb54d7f7");
		req.setStore_front_img_url("https://bbpurse.com/flypayfx/mobile/getImage/20160910d9bff73b2cb74031b191fbba954331e7");
		
		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, "20161102104403706", FshowsLiquidationSubmerchantCreateRequest.class);
		if (result == null || !result.getBooleanValue("success")) {
		   return false;
		}
		String sub_merchant_id = result.getJSONObject("return_value").getString("sub_merchant_id");
		if (StringUtil.isEmpty(sub_merchant_id)) {
		  return false;
		}
		FshowsLiquidationSubmerchantBankBindRequest bindReq = new FshowsLiquidationSubmerchantBankBindRequest();
		bindReq.setSub_merchant_id(sub_merchant_id);
		bindReq.setBank_card_no(card.getCardNo());
		bindReq.setCard_holder(card.getUser().getRealName());
		JSONObject bindResult = PinganPaymentUtil.sentRequstToPingAnPayment(bindReq, "20161102104403706", FshowsLiquidationSubmerchantBankBindRequest.class);
		if (bindResult == null || !bindResult.getBooleanValue("success")) {
			return false;
		}
		log.info("平安支付商户ID:========="+sub_merchant_id);
		FshowsLiquidationSubmerchantRateSetResponse rateReq = new FshowsLiquidationSubmerchantRateSetResponse();
		rateReq.setSub_merchant_id(sub_merchant_id);
		rateReq.setMerchant_rate("0.0038");
		JSONObject rateResult = PinganPaymentUtil.sentRequstToPingAnPayment(rateReq, "20161102104403706", FshowsLiquidationSubmerchantRateSetResponse.class);
		if (rateResult == null || !rateResult.getBooleanValue("success")) {
			return false;
		}
		String config = "{\"pingan.appId\":\"20161102104403706\",\"pingan.merchant_id\": \"%s\",\"pingan.notifyUrl\": \"https://bbpurse.com/flypayfx/payment/pinganPayNotify\",\"pingan.createIp\": \"127.0.0.1\"}";
		addChannel("平安直通车子商户勿动", type, config, userId, sub_merchant_id);
		return true;
	}
	
	
	public static void main(String[] args) {
//			FshowsLiquidationSubmerchantCreateRequest req = new FshowsLiquidationSubmerchantCreateRequest();
//			String patt = "PATT_" + DateUtil.convertCurrentDateTimeToString();
//			req.setExternal_id(patt);
//			req.setName("爱华超市");
//			req.setAlias_name("爱华超市");
//			req.setService_phone("13052222696");
//			req.setCategory_id("2015080600000001");
//			req.setId_card_name("张瑜婷");
//			req.setId_card_num("310115198811254020");
//			req.setStore_address("上海市浦东新区顺和路");
//			req.setId_card_hand_img_url("https://bbpurse.com/flypayfx/mobile/getImage/20160909a7f617d569954c6cbd48ed38bb54d7f7");
//			req.setStore_front_img_url("https://bbpurse.com/flypayfx/mobile/getImage/20160910d9bff73b2cb74031b191fbba954331e7");
//			req.setProvince("上海");
//			req.setCity("上海市");
//			req.setDistrict("浦东新区");
//			
//			
//			JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, "20161102104403706", FshowsLiquidationSubmerchantCreateRequest.class);
//			String sub_merchant_id = result.getJSONObject("return_value").getString("sub_merchant_id");
//			FshowsLiquidationSubmerchantBankBindRequest bindReq = new FshowsLiquidationSubmerchantBankBindRequest();
//			bindReq.setSub_merchant_id(sub_merchant_id);
//			bindReq.setBank_card_no("6214852111454099");
//			bindReq.setCard_holder("芦强");
//			JSONObject bindResult = PinganPaymentUtil.sentRequstToPingAnPayment(bindReq, "20161102104403706", FshowsLiquidationSubmerchantBankBindRequest.class);
		 
			//System.out.println("sub_merchant_id:"+sub_merchant_id);
		    //System.out.println("patt:"+patt);
		    
		    
		    FshowsLiquidationBestpayMerchantAccountRequest account = new FshowsLiquidationBestpayMerchantAccountRequest();
		    account.setStore_id("20171019172131028810");
		    account.setMerchant_store_name("爱华超市");
		    account.setStore_area_id(TroughUtil.YIZFProvince());
		    account.setStore_city_id(TroughUtil.YIZFCity());
		    account.setMcc_code(TroughUtil.TroughYIZF());
			JSONObject accountResult = PinganPaymentUtil.sentRequstToPingAnPayment(account, "20161102104403706", FshowsLiquidationBestpayMerchantAccountRequest.class);
		    System.out.println("result:"+accountResult);
	}
	
	
	
	
	
	public Boolean TroughMingSheng(String category,String cooperator,String nameType,String merchantName,String shortName,TuserCard card,Long userId,Integer type,String payway,String merType,String idNo,Long payType,String shortType) throws Exception{
		    String merchantId = DateUtil.convertCurrentDateTimeToString();
		    SMZF001 smzf001 = new SMZF001();
			merchantId = merType + merchantId;
			smzf001.setCooperator(cooperator);
			smzf001.setPayWay(payway);
			smzf001.setCategory(category);
			smzf001.setMerchantId(merchantId);
			smzf001.setMerchantName("个体户"+card.getUser().getRealName());
			smzf001.setShortName(merchantName+shortType);
			smzf001.setMerchantAddress(shortName);
			smzf001.setServicePhone(card.getPhone());
			smzf001.setIdCard(idNo);
			smzf001.setAccName(card.getUser().getRealName());
			smzf001.setAccNo(card.getCardNo());
			smzf001.setT0drawFee(TroughTrainT0drawFee);
			smzf001.setT0tradeRate(TrougWXD0tradeRate);
			smzf001.setT1drawFee(TroughTrainT1drawFee);
			smzf001.setT1tradeRate(TrougWXT1tradeRate);
			smzf001.setChannelMerchantCode(merchantId);
			smzf001.setProvinceCode("110000");
			smzf001.setCityCode("110100");
			smzf001.setDistrictCode("110102");
			smzf001.setContactType("01");
			smzf001.setContactName(card.getUser().getRealName());
			String responseStr = MinShengMerchantInputMinShengUtil.doPost(smzf001);
			log.info(responseStr);
			Map<String, String> result = XmlMapper.xml2Map(responseStr);
			if(result==null){
				return false;
			}
			String config = "{\"cooperator\":\"%s\",\"callBack\":\"https://bbpurse.com/flypayfx/payment/minshengNotify\",\"serverUrl\":\"https://ipay.cmbc.com.cn:9020/nbp-smzf-hzf\",\"merchant_code\":\"%s\"}";
			if(result.get("respCode").equals("200012")){
				String merchantCode =  "test";
				addMingShenChannel(cooperator,4,nameType,type, config, userId, merchantCode,merchantId,payType);
			}else if(result.get("respCode").equals("000000")){
				String merchantCode = result.get("merchantCode");
				addMingShenChannel(cooperator,3,nameType,type, config, userId, merchantCode,merchantId,payType);
			}else{
				return false;
			}
			return true;
	}
	
	
//	public static void main(String[] args) {
//		String[] keys = { "insNum", "payType", "merName", "regShortName", "merAddress", "merStat", 
//				"funcStat","legalPerson", "legalPersonCertType", "legalPersonCertNm","legalPersonCertExpire", 
//				"contactPerson", "contactMobile", "debitCardName", "debitCardLines", "debitCardNum", "WXT0", 
//				"ZFBT0", "WXT1", "ZFBT1", "factorageT0", "factorageT1","bankName", 
//				"bankBranchName", "provName", "cityName", "isPrivate","merProvince","merCity","merArea","merType",
//                "qcPayFeeT1","qcPayFeeRateT1","qcWithDrawFeeT0","qcWithDrawFeeRateT0","outMerId","outTermNo",
//                 "outTermName","outTermType","outTermProv","outTermCity","outTermArea","outTermAddress","outTermSN",
//                 "creditCardFee","creditCardFeeFixed","debitCardFee","debitCardMax"};
//		
//		   String[] inputParams = { "00000021", "99","个体户周强","个体户周强饮品店","上海市浦东新区孙桥饮品店", "1",
//				   "YYYYYYYYYY", "芦强", "0", "152822199012293816", "20250914", 
//				   "芦强","13052222698", "芦强", "20250914", "6214852111454099", "0.26", 
//				   "0.26", "0.24", "0.24", "2", "0", "招商银行", 
//				   "上海支行", "上海", "上海", "N","110000","110100","110105","2",
//				   "1","0","2","0","5855968","13052222698",
//				   "花为","0","北京","北京市","朝阳区","朝阳大街110号","SN000001","0","0","0","0"};
//		   String responseStr = XinkePayUtil.build(keys, inputParams, "enter");
//		   System.out.println("responseStr:"+responseStr);
//	}
	
	
	
	
	public Boolean TroughXinKe(String merchantName,TuserCard card,String shortName,Long userId,Long inpuType){
		String[] keys = { "insNum", "payType", "merName", "regShortName", "merAddress", "merStat", 
				"funcStat","legalPerson", "legalPersonCertType", "legalPersonCertNm","legalPersonCertExpire", 
				"contactPerson", "contactMobile", "debitCardName", "debitCardLines", "debitCardNum", "WXT0", 
				"ZFBT0", "WXT1", "ZFBT1", "factorageT0", "factorageT1","bankName", 
				"bankBranchName", "provName", "cityName", "isPrivate","merProvince","merCity","merArea","merType",
				"qcPayFeeT1","qcPayFeeRateT1","qcWithDrawFeeT0","qcWithDrawFeeRateT0","outMerId","outTermNo",
                "outTermName","outTermType","outTermProv","outTermCity","outTermArea","outTermAddress","outTermSN",
                "creditCardFee","creditCardFeeFixed","debitCardFee","debitCardMax"};
		
		String bankName = "上海银行";
		String bankBranchName = "上海浦东支行";
		if(card.getBranchName()!=null){
			bankName = card.getBranchName();
			bankBranchName =card.getBranchName()+"浦东支行";
		}
	   
	   String[] inputParams = { "00000021", "99","个体户"+card.getUser().getRealName(),merchantName,shortName, "1",
			   "YYYYYYYYYY", card.getUser().getRealName(), "0", card.getUser().getIdNo(), "20250914", 
			   card.getUser().getRealName(),card.getPhone(), card.getUser().getRealName(), "03080000", card.getCardNo(), "0.26", 
			   "0.26", "0.24", "0.24", "2", "0", bankName, 
			   bankBranchName, "上海", "上海", "N","110000","110100","110105","2",
			   "1","0","2","0","5855968",card.getPhone(),
			   "花为","0","北京","北京市","朝阳区","朝阳大街110号","SN000001","0","0","0","0"};
	   
	   
	   String responseStr = XinkePayUtil.build(keys, inputParams, "enter");
	   log.info(responseStr);
		Map<String, String> result = XmlMapper.xml2Map(responseStr);
		if (result != null && result.containsKey("rspCode") && "000000".equals(result.get("rspCode"))) {
			if (StringUtil.isNotBlank(result.get("merNum"))) {
				String merchantCode = result.get("merNum");
				String config = "{\"xinke.merchant_id\":\"%s\"}";
				if(inpuType==0){
					addXinKeChannel("00000021", 3, "欣客支付宝D0直通车", 200, config, userId, merchantCode, inpuType,merchantName,shortName);
					addXinKeChannel("00000021", 3, "欣客微信D0直通车", 300, config, userId, merchantCode, inpuType,merchantName,shortName);
				}else if(inpuType==1){
					addXinKeChannel("00000021", 3, "欣客支付宝T1直通车", 200, config, userId, merchantCode, inpuType,merchantName,shortName);
					addXinKeChannel("00000021", 3, "欣客微信T1直通车", 300, config, userId, merchantCode, inpuType,merchantName,shortName);
				}
			}else{
				return false;
			}
		}else if("002035".equals(result.get("RSPCOD"))){
			log.info("-----商户已经报备----");
		}else{
			return false;
		}
	   
		return true;
	}
	
	
	public void addMingShenChannel(String cooperator,Integer Status,String detailName,Integer type,String config,Long userId,String merchantCode,String merchantId,Long payType){
		Tchannel t = new Tchannel();
		t.setVersion(Long.parseLong("0"));
		t.setType(type);
		t.setName("MINGSHENGZHITONGCHE");
		t.setRealRate(new BigDecimal(0.0027));
		t.setShowRate(new BigDecimal(0.0049));
		t.setShareRate(new BigDecimal(0.0010));
		t.setMaxTradeAmt(new BigDecimal(100000.00));
		t.setMinTradeAmt(new BigDecimal(0.00));
		t.setStatus(Status);
		t.setMaxChannelAmt(new BigDecimal(20000.00));
		t.setMinChannelAmt(new BigDecimal(0.00));
		t.setTodayAmt(new BigDecimal(0.00));
		t.setMaxAmtPerDay(new BigDecimal(200000.00));
		t.setAccount(cooperator);
		t.setConfig(String.format(config,cooperator,merchantCode));
		t.setDetailName(detailName);
		t.setSeq(100);
		t.setUserType(700);
		t.setCommissionRate(new BigDecimal(0.00));
		t.setUserId(userId);
		t.setMerchantId(merchantId);
	    t.setPayType(payType);
		channelDao.save(t);
	}
	
	/**
	 * 保存平安直通车通道
	 * @param detailName
	 * @param type
	 * @param config
	 * @param userId
	 * @param merchantId
	 */
	public void addPINGANPAYChannel(String detailName,Integer type,String config,Long userId,String merchantId){
		Tchannel t = new Tchannel();
		t.setVersion(Long.parseLong("0"));
		t.setType(type);
		t.setName("PINGANPAYZHITONGCHE_ZHIQING");
		t.setRealRate(new BigDecimal(0.0027));
		t.setShowRate(new BigDecimal(0.0049));
		t.setShareRate(new BigDecimal(0.0010));
		t.setMaxTradeAmt(new BigDecimal(100000.00));
		t.setMinTradeAmt(new BigDecimal(0.00));
		t.setStatus(10);
		t.setMaxChannelAmt(new BigDecimal(20000.00));
		t.setMinChannelAmt(new BigDecimal(0.00));
		t.setTodayAmt(new BigDecimal(0.00));
		t.setMaxAmtPerDay(new BigDecimal(200000.00));
		t.setAccount(PinganPaymentUtil.appId);
		t.setConfig(config);
		t.setDetailName(detailName);
		t.setSeq(880);
		t.setUserType(700);
		t.setCommissionRate(new BigDecimal(0.00));
		t.setUserId(userId);
		t.setMerchantId(merchantId);
	    t.setPayType(5l);
	    t.setCreateDate(new Date());
		channelDao.save(t);
	}
	
	/**
	 * 开通易宝直通车
	 * @param detailName
	 * @param type	支付类型
	 * @param config  通道配置
	 * @param userId  通道对应的使用人
	 * @param merchantCode  子商户id
	 * @param merchantId
	 */
	@Override
	public Boolean addCreateYiBaoZTCChannel(String detailName,Integer type,String config,Long userId,String merchantId){
		try {
			Tchannel t = new Tchannel();
			t.setVersion(Long.parseLong("0"));
			t.setType(type);
			t.setName("YIBAOZHITONGCHE");
			t.setRealRate(new BigDecimal(0.0026));
			t.setShowRate(new BigDecimal(0.0049));
			t.setShareRate(new BigDecimal(0.0010));
			t.setMaxTradeAmt(new BigDecimal(100000.00));
			t.setMinTradeAmt(new BigDecimal(0.00));
			t.setStatus(10);
			t.setMaxChannelAmt(new BigDecimal(20000.00));
			t.setMinChannelAmt(new BigDecimal(10.00));
			t.setTodayAmt(new BigDecimal(0.00));
			t.setMaxAmtPerDay(new BigDecimal(200000.00));
			t.setAccount(YiBaoBaseUtil.customerNumber);	//易宝的代理商ID
			t.setConfig(String.format(config,YiBaoBaseUtil.customerNumber,merchantId));
			t.setDetailName(detailName);
			t.setSeq(880);
			t.setUserType(700);
			t.setCommissionRate(new BigDecimal(0.00));
			t.setUserId(userId);
			channelDao.save(t);
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@Override
	public void addPINGANPAYZHITONGCHEChannel(String detailName,Integer type,String config,Long userId,String merchantId){
		Tchannel t = new Tchannel();
		t.setVersion(Long.parseLong("0"));
		t.setType(type);
		t.setName("PINGANPAYZHITONGCHE_ZHIQING");
		t.setRealRate(new BigDecimal(0.0027));
		t.setShowRate(new BigDecimal(0.0049));
		t.setShareRate(new BigDecimal(0.0010));
		t.setMaxTradeAmt(new BigDecimal(100000.00));
		t.setMinTradeAmt(new BigDecimal(0.00));
		t.setStatus(10);
		t.setMaxChannelAmt(new BigDecimal(20000.00));
		t.setMinChannelAmt(new BigDecimal(0.00));
		t.setTodayAmt(new BigDecimal(0.00));
		t.setMaxAmtPerDay(new BigDecimal(200000.00));
		t.setAccount(PinganPaymentUtil.appId);
		t.setConfig(config);
		t.setDetailName(detailName);
		t.setSeq(880);
		t.setUserType(700);
		t.setCommissionRate(new BigDecimal(0.00));
		t.setUserId(userId);
		t.setMerchantId(merchantId);
	    t.setPayType(5l);
	    t.setCreateDate(new Date());
		channelDao.save(t);
	};
	
	
	
	
	public void addXinKeChannel(String cooperator,Integer Status,String detailName,Integer type,String config,Long userId,String merchantCode,Long payType,String merchantName,String shortName){
		Tchannel t = new Tchannel();
		t.setVersion(Long.parseLong("0"));
		t.setType(type);
		t.setName("XINKKEZHITONGCHE");
		t.setRealRate(new BigDecimal(0.0026));
		t.setShowRate(new BigDecimal(0.0049));
		t.setShareRate(new BigDecimal(0.0010));
		t.setMaxTradeAmt(new BigDecimal(100000.00));
		t.setMinTradeAmt(new BigDecimal(0.00));
		t.setStatus(Status);
		t.setMaxChannelAmt(new BigDecimal(20000.00));
		t.setMinChannelAmt(new BigDecimal(0.00));
		t.setTodayAmt(new BigDecimal(0.00));
		t.setMaxAmtPerDay(new BigDecimal(200000.00));
		t.setAccount(cooperator);
		t.setConfig(String.format(config,merchantCode));
		t.setDetailName(detailName);
		t.setSeq(100);
		t.setUserType(700);
		t.setCommissionRate(new BigDecimal(0.00));
		t.setUserId(userId);
	    t.setPayType(payType);
	    t.setMerchantName(merchantName);
	    t.setShortName(shortName);
		channelDao.save(t);
	}
	
	
	public void addXinKeChannelTwo(String cooperator,Integer Status,String detailName,Integer type,String config,Long userId,String merchantCode,Long payType,String merchantName,String shortName){
		Tchannel t = new Tchannel();
		t.setVersion(Long.parseLong("0"));
		t.setType(type);
		t.setName("XINKKEYINLIAN");
		t.setRealRate(new BigDecimal(0.0026));
		t.setShowRate(new BigDecimal(0.0049));
		t.setShareRate(new BigDecimal(0.0010));
		t.setMaxTradeAmt(new BigDecimal(100000.00));
		t.setMinTradeAmt(new BigDecimal(0.00));
		t.setStatus(Status);
		t.setMaxChannelAmt(new BigDecimal(20000.00));
		t.setMinChannelAmt(new BigDecimal(0.00));
		t.setTodayAmt(new BigDecimal(0.00));
		t.setMaxAmtPerDay(new BigDecimal(200000.00));
		t.setAccount(cooperator);
		t.setConfig(String.format(config,merchantCode));
		t.setDetailName(detailName);
		t.setSeq(100);
		t.setUserType(700);
		t.setCommissionRate(new BigDecimal(0.00));
		t.setUserId(userId);
	    t.setPayType(payType);
	    t.setMerchantName(merchantName);
	    t.setShortName(shortName);
		channelDao.save(t);
	}
	
	
	
	public void addChannel(String detailName,Integer type,String config,Long userId,String merchantCode){
		Tchannel t = new Tchannel();
		t.setVersion(Long.parseLong("0"));
		t.setType(type);
		t.setName("MINGSHENGZHITONGCHE");
		t.setRealRate(new BigDecimal(0.0027));
		t.setShowRate(new BigDecimal(0.0049));
		t.setShareRate(new BigDecimal(0.0010));
		t.setMaxTradeAmt(new BigDecimal(100000.00));
		t.setMinTradeAmt(new BigDecimal(0.00));
		t.setStatus(4);
		t.setMaxChannelAmt(new BigDecimal(20000.00));
		t.setMinChannelAmt(new BigDecimal(0.00));
		t.setTodayAmt(new BigDecimal(0.00));
		t.setMaxAmtPerDay(new BigDecimal(200000.00));
		t.setAccount("SMZF_SHFF_HD_T0");
		t.setConfig(String.format(config,merchantCode));
		t.setDetailName(detailName);
		t.setSeq(100);
		t.setUserType(700);
		t.setCommissionRate(new BigDecimal(0.00));
		t.setUserId(userId);
		channelDao.save(t);
	}



	@Override
	public boolean updateAccount(String userId, String transAmt) {
		try {
			Taccount acc = accountDao.get("select t from Taccount t left join t.user u where u.id=" + Long.parseLong(userId));
			acc.setThroughAmt(new BigDecimal(transAmt).add(acc.getThroughAmt()));
			//update:2017.11.28 直通车模式，月收入和月支出加上直通车金额
			acc.setPerMonthInAmt(new BigDecimal(transAmt).add(acc.getPerMonthInAmt()));//月收入金额增加
			acc.setPerMonthOutAmt(new BigDecimal(transAmt).add(acc.getPerMonthOutAmt()));//月支出金额增加
			accountDao.update(acc);
//			TaccountLog accountLog = new TaccountLog(acc, "C", new BigDecimal(transAmt), "直通车入账");
//			accountLog.setAvlAmt(acc.getAvlAmt());
//			accountLogDao.save(accountLog);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}



	@Override
	public List<User> dataGrid() {
		List<Tuser> l = userDao.find(" select t from Tuser t where t.authenticationStatus=1 ");
		List<User> ul = new ArrayList<User>();
		for (Tuser t : l) {
			User u = new User();
			BeanUtils.copyProperties(t, u);
			ul.add(u);
		}
		return ul;
	}



	@Override
	public Boolean addMocde(User user) {
		String[] keys = { "insNum", "payType", "merName", "regShortName", "merAddress", "merStat", 
				"funcStat","legalPerson", "legalPersonCertType", "legalPersonCertNm","legalPersonCertExpire", 
				"contactPerson", "contactMobile", "debitCardName", "debitCardLines", "debitCardNum", "WXT0", 
				"ZFBT0", "WXT1", "ZFBT1", "factorageT0", "factorageT1","bankName", 
				"bankBranchName", "provName", "cityName", "isPrivate","merProvince","merCity","merArea","merType",
				"qcPayFeeT1","qcPayFeeRateT1","qcWithDrawFeeT0","qcWithDrawFeeRateT0","outMerId","outTermNo",
                "outTermName","outTermType","outTermProv","outTermCity","outTermArea","outTermAddress","outTermSN",
                "creditCardFee","creditCardFeeFixed","debitCardFee","debitCardMax"};
		
		String bankName = "上海银行";
		String bankBranchName = "上海浦东支行";
		
		TuserCard card = cardDao.get("select t from TuserCard t left join t.user u where t.isSettlmentCard=1 and u.id=" + user.getId());
		if(card==null){
			return false;
		}	
		
		if(card.getBranchName()!=null){
				bankName = card.getBranchName();
				bankBranchName =card.getBranchName()+"浦东支行";
			}
		   
		   String[] inputParams = { "00000021", "99","个体户"+card.getUser().getRealName(),"大同餐饮","大同餐饮", "1",
				   "YYYYYYYYYY", card.getUser().getRealName(), "0", card.getUser().getIdNo(), "20250914", 
				   card.getUser().getRealName(),card.getPhone(), card.getUser().getRealName(), "03080000", card.getCardNo(), "0.26", 
				   "0.26", "0.24", "0.24", "2", "0", bankName, 
				   bankBranchName, "上海", "上海", "N","110000","110100","110105","2",
				   "1","0.56","2","0","5855968",card.getPhone(),
				   "花为","0","北京","北京市","朝阳区","朝阳大街110号","SN000001","0","0","0","0"};
		   
		   
		   String responseStr = XinkePayUtil.build(keys, inputParams, "enter");
		   log.info(responseStr);
			Map<String, String> result = XmlMapper.xml2Map(responseStr);
			if (result != null && result.containsKey("rspCode") && "000000".equals(result.get("rspCode"))) {
				if (StringUtil.isNotBlank(result.get("merNum"))) {
					String merchantCode = result.get("merNum");
					String config = "{\"xinke.merchant_id\":\"%s\"}";
					addXinKeChannelTwo("00000021", 3, "欣客银联快捷D0直通车", 550, config, user.getId(), merchantCode, 0l,"欣客银联快捷","欣客银联快捷");
				}else{
					return false;
				}
			}else if("002035".equals(result.get("RSPCOD"))){
				log.info("-----商户已经报备----");
			}else{
				return false;
			}
		return true;
	}



	@Override
	public void PINGANYIZF(String Name) {
		FshowsLiquidationSubmerchantCreateRequest req = new FshowsLiquidationSubmerchantCreateRequest();
		String patt = "PATT_" + DateUtil.convertCurrentDateTimeToString();
		req.setExternal_id(patt);
		req.setName(Name);
		req.setAlias_name(Name);
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
		
		
		JSONObject result = PinganPaymentUtil.sentRequstToPingAnPayment(req, "20161102104403706", FshowsLiquidationSubmerchantCreateRequest.class);
		String sub_merchant_id = result.getJSONObject("return_value").getString("sub_merchant_id");
		FshowsLiquidationSubmerchantBankBindRequest bindReq = new FshowsLiquidationSubmerchantBankBindRequest();
		bindReq.setSub_merchant_id(sub_merchant_id);
		bindReq.setBank_card_no("6214852111454099");
		bindReq.setCard_holder("芦强");
		JSONObject bindResult = PinganPaymentUtil.sentRequstToPingAnPayment(bindReq, "20161102104403706", FshowsLiquidationSubmerchantBankBindRequest.class);

		
		
	    FshowsLiquidationBestpayMerchantAccountRequest account = new FshowsLiquidationBestpayMerchantAccountRequest();
	    account.setStore_id(sub_merchant_id);
	    account.setMerchant_store_name(Name);
	    account.setStore_area_id(TroughUtil.YIZFProvince());
	    account.setStore_city_id(TroughUtil.YIZFCity());
	    account.setMcc_code(TroughUtil.TroughYIZF());
		JSONObject accountResult = PinganPaymentUtil.sentRequstToPingAnPayment(account, "20161102104403706", FshowsLiquidationBestpayMerchantAccountRequest.class);
	    System.out.println("result:"+accountResult);
	}
	
	
	
	
	
	

}
