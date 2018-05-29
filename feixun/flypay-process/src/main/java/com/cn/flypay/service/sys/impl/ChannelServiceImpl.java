package com.cn.flypay.service.sys.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.TchannelT0Tixian;
import com.cn.flypay.model.sys.TorgChannel;
import com.cn.flypay.model.sys.Torganization;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.payment.minsheng.SMZF007;
import com.cn.flypay.pageModel.sys.Channel;
import com.cn.flypay.pageModel.sys.ChannelT0Tixian;
import com.cn.flypay.pageModel.sys.PayTypeLimitConfig;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.RouteService;
import com.cn.flypay.service.sys.ChannelService;
import com.cn.flypay.service.sys.HolidayService;
import com.cn.flypay.service.sys.OrgChannelService;
import com.cn.flypay.service.sys.OrganizationService;
import com.cn.flypay.service.sys.PayTypeLimitConfigService;
import com.cn.flypay.service.sys.SysParamService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.minsheng.MinShengMerchantInputMinShengUtil;
import com.cn.flypay.utils.xinke.XinkePayUtil;

@Service
public class ChannelServiceImpl implements ChannelService {
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private BaseDao<Tchannel> channelDao;
	@Autowired
	private BaseDao<TchannelT0Tixian> channelT0TixianDao;
	@Autowired
	private UserSettlementConfigService configService;
	@Autowired
	private OrgChannelService orgChannelService;
	@Autowired
	private SysParamService sysParamService;
	@Autowired
	private RouteService routeService;
	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private PayTypeLimitConfigService payTypeLimitConfigService;
	
	@Autowired
	private HolidayService holidayService;

	@Cacheable(value = "channelCache", key = "#channelId+'getTchannelInCache'")
	@Override
	public Tchannel getTchannelInCache(Long channelId) {
		return channelDao.get(Tchannel.class, channelId);
	}

	@Override
	public void add(Channel c) {
		Tchannel t = new Tchannel();
		BeanUtils.copyProperties(c, t);
		// t.setStatus(0);
		t.setTodayAmt(BigDecimal.ZERO);
		if (StringUtil.isNotBlank(t.getConfig())) {
			t.setConfig(t.getConfig().replaceAll("&quot;", "\""));
		}
		channelDao.save(t);

		/* 添加通道运营商配置 */
		List<TorgChannel> tcs = new ArrayList<TorgChannel>();
		List<Torganization> torgs = organizationService.getTorganiztions();
		for (Torganization org : torgs) {
			TorgChannel tc = new TorgChannel(t.getRealRate(), 0, new Date(), new Date(), "system");
			tc.setChannel(t);
			tc.setOrganization(org);
			tcs.add(tc);
		}
		orgChannelService.addTorgChannelList(tcs);
	}

	@Override
	public void delete(Long id) {
		Tchannel t = channelDao.get(Tchannel.class, id);
		t.setStatus(1);
		channelDao.update(t);
	}

	@CacheEvict(value = "channelCache", allEntries = true)
	@Override
	public void edit(Channel c) {
		Tchannel t = channelDao.get(Tchannel.class, c.getId());
		// t.setName(c.getName());
		t.setRealRate(c.getRealRate());
		t.setShareRate(c.getShareRate());
		t.setShowRate(c.getShowRate());
		t.setStatus(c.getStatus());
		t.setMaxTradeAmt(c.getMaxTradeAmt());
		t.setMinTradeAmt(c.getMinTradeAmt());
		t.setTodayAmt(c.getTodayAmt());
		t.setMaxAmtPerDay(c.getMaxAmtPerDay());
		t.setMinChannelAmt(c.getMinChannelAmt());
		t.setMaxChannelAmt(c.getMaxChannelAmt());
		t.setUserType(c.getUserType());
		t.setLimitType(c.getLimitType());
		t.setSeq(c.getSeq());
		t.setCommissionRate(c.getCommissionRate());
		t.setMaxNumPerPersonPerDay(c.getMaxNumPerPersonPerDay());
		if (StringUtil.isNotBlank(c.getConfig())) {
			t.setConfig(c.getConfig().replaceAll("&quot;", "\""));
		}
		channelDao.update(t);
	}

	@Override
	public Channel get(Long id) {
		Tchannel t = channelDao.get(Tchannel.class, id);
		Channel c = new Channel();
		BeanUtils.copyProperties(t, c);
		return c;
	}
	
	
	@Override
	public void updateConfig(Channel c){
		Tchannel t = channelDao.get(Tchannel.class, c.getId());
		t.setConfig(c.getConfig());
		channelDao.update(t);
	};
	
	@Override
	public void updateStatus(Long id,Integer status){
		Tchannel t = channelDao.get(Tchannel.class, id);
		t.setStatus(status);
		channelDao.update(t);
	};

	@Override
	public Channel getChannelByTransType(Integer transType) {
		Tchannel t = getTchannelByTransType(transType);
		if (t != null) {
			Channel c = new Channel();
			BeanUtils.copyProperties(t, c);
			return c;
		}
		return null;
	}

	@Override
	public void appendTodayAmt(Long channelId, BigDecimal amt) {

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("channelId", channelId);
			params.put("amt", amt);
			String hql = "update Tchannel set todayAmt=todayAmt+:amt where id=:channelId";
			channelDao.executeHql(hql, params);
		} catch (Exception e) {
			log.error(e);
		}
	}

	@Override
	public void removeTodayAmt(Long channelId) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			String hql = "update Tchannel set todayAmt=0 ,userNum=0 ";
			if (channelId != null) {
				hql = hql + " where id=:channelId";
				params.put("channelId", channelId);
			}
			channelDao.executeHql(hql, params);
		} catch (Exception e) {
			log.error(e);
		}
	}

	@Override
	public Tchannel getTchannelByTransType(Integer transType) {
		Map<String, Object> chns = new HashMap<String, Object>();
		chns.put("type", transType);
		Tchannel ts = channelDao.get("select t from Tchannel t where t.status=0 and t.type=:type", chns);
		if (ts != null) {
			return ts;
		}
		return null;
	}

	@Cacheable(value = "channelCache", key = "'getChannelLimit'")
	private List<Map<String, String>> getChannelLimit(String agentId) {
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		Set<Integer> transTypes = new HashSet<>();
		transTypes.add(UserOrder.trans_type.ALQR.getCode());
		transTypes.add(UserOrder.trans_type.WXQR.getCode());
		transTypes.add(UserOrder.trans_type.YLZX.getCode());
		transTypes.add(UserOrder.trans_type.JDQR.getCode());
		transTypes.add(UserOrder.trans_type.QQQR.getCode());
		transTypes.add(UserOrder.trans_type.BDQR.getCode());
		/*
		 * transTypes.add(UserOrder.trans_type.BDQR.getCode());
		 * transTypes.add(UserOrder.trans_type.YZFQR.getCode());
		 * transTypes.add(UserOrder.trans_type.YLQR.getCode());
		 */

		List<PayTypeLimitConfig> paytypes = payTypeLimitConfigService.findPayTypeLimitConfigs(transTypes,agentId);	//查询通道限额配置表中目前启动的配置
		Map<String, String> my = new HashMap<String, String>();
		Map<String, String> ma = new HashMap<String, String>();
		Map<String, String> mw = new HashMap<String, String>();
		Map<String, String> mj = new HashMap<String, String>();
		Map<String, String> mq = new HashMap<String, String>();
		Map<String, String> mb = new HashMap<String, String>();
		for (PayTypeLimitConfig t : paytypes) {
		  if(t.getAmtType()!=300){	//通道限额配置中的非直通车类型
				if (!transTypes.contains(t.getPayType())) { //如果通道限额配置中启动的配置不包括想要的类型
					continue;
				}
				Map<String, String> m = new HashMap<String, String>();
				m.put("maxAmt", t.getMaxAmt().toString());
				m.put("minAmt", t.getMinAmt().toString());
				m.put("accountType", t.getAmtType().toString());	//大小额类型	100 小额	200	大额	300	直通车
				/*
				 * 1付款， 2提现
				 */
				m.put("type", "1");
				m.put("fee", "0");
				m.put(t.getPayType() + "_" + t.getAmtType(), t.getPayType().toString());	//key:支付类型code_大小额类型code  例如支付宝小额  200_100
				String channelName = null;
				if (t.getPayType() == UserOrder.trans_type.ALQR.getCode()) {
					channelName = "支付宝";
					if (t.getAmtType() == 100) {
						m.put("isOn", sysParamService.searchSysParameter().get("alipay_swift_on"));
					} else if (t.getAmtType() == 200) {
						m.put("isOn", sysParamService.searchSysParameter().get("alipay_big_swift_on"));
					}
				} else if (t.getPayType() == UserOrder.trans_type.WXQR.getCode()) {
					channelName = "微信";
					if (t.getAmtType() == 100) {
						m.put("isOn", sysParamService.searchSysParameter().get("weixin_swift_on"));
					} else if (t.getAmtType() == 200) {
						m.put("isOn", sysParamService.searchSysParameter().get("weixin_big_swift_on"));
					}
				} else if (t.getPayType() == UserOrder.trans_type.YLZX.getCode()) {
					if(agentId.equals("F20160001")||agentId.equals("F20160013")||agentId.equals("F20160015")
					 ||agentId.equals("F20160017")||agentId.equals("F20160011")||agentId.equals("F20160003")
					 ||agentId.equals("F20160010")){
						channelName = "银联小额";
					}else{
						channelName = "银联在线";
					}
					if (t.getAmtType() == 100) {
						m.put("isOn", sysParamService.searchSysParameter().get("yinlian_swift_on"));
					} else if (t.getAmtType() == 200) {
						m.put("isOn", sysParamService.searchSysParameter().get("yinlian_big_swift_on"));
					}
				} else if (t.getPayType() == UserOrder.trans_type.JDQR.getCode()) {
					channelName = "京东";
					if (t.getAmtType() == 100) {
						m.put("isOn", sysParamService.searchSysParameter().get("jingdong_swift_on"));
					} else if (t.getAmtType() == 200) {
						m.put("isOn", sysParamService.searchSysParameter().get("jingdong_big_swift_on"));
					}
				} else if (t.getPayType() == UserOrder.trans_type.BDQR.getCode()) {
					channelName = "百度钱包";
					if (t.getAmtType() == 100) {
						m.put("isOn", sysParamService.searchSysParameter().get("baidu_swift_on"));
					} else if (t.getAmtType() == 200) {
						m.put("isOn", sysParamService.searchSysParameter().get("baidu_big_swift_on"));
					}
				} else if (t.getPayType() == UserOrder.trans_type.YZFQR.getCode()) {
					channelName = "翼支付";
					if (t.getAmtType() == 100) {
						m.put("isOn", sysParamService.searchSysParameter().get("yizhifu_swift_on"));
					} else if (t.getAmtType() == 200) {
						m.put("isOn", sysParamService.searchSysParameter().get("yizhifu_big_swift_on"));
					}
				} else if (t.getPayType() == UserOrder.trans_type.YLQR.getCode()) {
					channelName = "银联支付";
					if (t.getAmtType() == 100) {
						m.put("isOn", sysParamService.searchSysParameter().get("yinlianzhifu_swift_on"));
					} else if (t.getAmtType() == 200) {
						m.put("isOn", sysParamService.searchSysParameter().get("yinlianzhifu_big_swift_on"));
					}
				}else if(t.getPayType() == UserOrder.trans_type.QQQR.getCode()){
					channelName = "QQ钱包";
					if (t.getAmtType() == 200) {
						m.put("isOn", sysParamService.searchSysParameter().get("qqqianbao_big_swift_on"));
					}
				}
				m.put("channelName", channelName);
				if(m.get("isOn").equals("1")){
					if (t.getPayType() == UserOrder.trans_type.ALQR.getCode()) {
						ma.putAll(m);
					}else if (t.getPayType() == UserOrder.trans_type.WXQR.getCode()) {
						mw.putAll(m);
					}else if (t.getPayType() == UserOrder.trans_type.YLZX.getCode()) {
						my.putAll(m);
					}else if (t.getPayType() == UserOrder.trans_type.JDQR.getCode()) {
						mj.putAll(m);
					}else if (t.getPayType() == UserOrder.trans_type.BDQR.getCode()) {
						mb.putAll(m);
					}else if (t.getPayType() == UserOrder.trans_type.QQQR.getCode()) {
						mq.putAll(m);
					}
				}
		  }
		}
		maps.add(0, my);
		maps.add(1, ma);
		maps.add(2, mw);
		maps.add(3, mj);
		maps.add(4, mq);
		maps.add(5, mb);
		return maps;
	}
	
	
	@Cacheable(value = "channelCache", key = "'getChannelLimitZTC'")
	private List<Map<String, String>> getChannelLimitZTC(String agentId) {
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		Set<Integer> transTypes = new HashSet<>();
		transTypes.add(UserOrder.trans_type.ALQR.getCode());
		transTypes.add(UserOrder.trans_type.WXQR.getCode());
		transTypes.add(UserOrder.trans_type.YLZX.getCode());
		transTypes.add(UserOrder.trans_type.YLZXJ.getCode());
		transTypes.add(UserOrder.trans_type.YLZXE.getCode());
		transTypes.add(UserOrder.trans_type.QQQR.getCode());

		
		
		List<PayTypeLimitConfig> paytypes = payTypeLimitConfigService.findPayTypeLimitConfigsZTC(transTypes,agentId);
		Map<String, String> my = new HashMap<String, String>();
		Map<String, String> myj = new HashMap<String, String>();
		Map<String, String> ma = new HashMap<String, String>();
		Map<String, String> mw = new HashMap<String, String>();
		Map<String, String> mq = new HashMap<String, String>();
		for (PayTypeLimitConfig t : paytypes) {
				if (!transTypes.contains(t.getPayType())) {
					continue;
				}
				Map<String, String> m = new HashMap<String, String>();
				m.put("maxAmt", t.getMaxAmt().toString());
				m.put("minAmt", t.getMinAmt().toString());
				m.put("accountType", t.getAmtType().toString());
				/*
				 * 1付款， 2提现
				 */
				m.put("type", "1");
				m.put("fee", "0");
				m.put(t.getPayType() + "_" + t.getAmtType(), t.getPayType().toString());
				m.put("payType", t.getPayType().toString());
				String channelName = null;
				if (t.getPayType() == UserOrder.trans_type.ALQR.getCode()) {
					channelName = "支付宝";
				} else if (t.getPayType() == UserOrder.trans_type.WXQR.getCode()) {
					channelName = "微信";
				} else if (t.getPayType() == UserOrder.trans_type.YLZX.getCode()) {
					channelName = "银联大额";
				} else if(t.getPayType() == UserOrder.trans_type.QQQR.getCode()){
					channelName = "QQ钱包";
				}else if(t.getPayType() == UserOrder.trans_type.YLZXJ.getCode()){
					channelName = "银联积分";
				}else if(t.getPayType() == UserOrder.trans_type.YLZXE.getCode()){
					channelName = "银联小额";
				}
				m.put("channelName", channelName);
				if (t.getPayType() == UserOrder.trans_type.ALQR.getCode()) {
					ma.putAll(m);
				}else if (t.getPayType() == UserOrder.trans_type.WXQR.getCode()) {
					mw.putAll(m);
				}else if (t.getPayType() == UserOrder.trans_type.YLZX.getCode()) {
					my.putAll(m);
				}else if (t.getPayType() == UserOrder.trans_type.YLZXJ.getCode()) {
					myj.putAll(m);
				}else if (t.getPayType() == UserOrder.trans_type.QQQR.getCode()) {
					mq.putAll(m);
				}
				maps.add(m);
		}
//		maps.add(0, my);
//		maps.add(1, myj);
//		maps.add(2, ma);
//		maps.add(3, mw);
//		maps.add(4, mq);
		return maps;
	}
	
	
	

	@Override
	public List<Map<String, String>> findShowChannelLimit(Long userId, String agentId) {
		List<Map<String, String>> maps = getChannelLimit(agentId);

		UserSettlementConfig scg = configService.getByUserId(userId);

		for (Map<String, String> m : maps) {
			if (m.containsKey(UserOrder.trans_type.ALQR.getCode() + "_100")) {
				m.put("channelRate", scg.getInputFeeAlipay().toString());
				m.put("channelD0Rate", scg.getInputFeeD0Alipay().toString());
			} else if (m.containsKey(UserOrder.trans_type.ALQR.getCode() + "_200")) {
				m.put("channelRate", scg.getInputFeeBigAlipay().toString());
				m.put("channelD0Rate", scg.getInputFeeD0BigAlipay().toString());
			} else if (m.containsKey(UserOrder.trans_type.WXQR.getCode() + "_100")) {
				m.put("channelRate", scg.getInputFeeWeixin().toString());
				m.put("channelD0Rate", scg.getInputFeeD0Weixin().toString());
			} else if (m.containsKey(UserOrder.trans_type.WXQR.getCode() + "_200")) {
				m.put("channelRate", scg.getInputFeeBigWeixin().toString());
				m.put("channelD0Rate", scg.getInputFeeD0BigWeixin().toString());
			} else if (m.containsKey(UserOrder.trans_type.YLZX.getCode() + "_100")) {
				m.put("channelRate", scg.getInputFeeYinlian().toString());
				m.put("channelD0Rate", scg.getInputFeeD0Yinlian().toString());
				
				//isWork 对应 银联手动D0通道  PS:建议和APP协商
				//金钱龟APP单独判断银联手动D0通道   金钱龟放开，其他通道读取公用配置
				if(agentId.equals("F20160015")){
					m.put("isWork", "1");
				}else{
					if(holidayService.getisYILIAND0Work()){
						m.put("isWork", "0");
					}else{
						m.put("isWork", "1");
					}
				}
				
				
//				if(holidayService.getisYILIAND0Work()){
//					m.put("isWork", "0");
//				}else{
//					m.put("isWork", "1");
//				}
			} else if (m.containsKey(UserOrder.trans_type.YLZX.getCode() + "_200")) {
				m.put("channelRate", scg.getInputFeeBigYinlian().toString());
				m.put("channelD0Rate", scg.getInputFeeD0Yinlian().toString());
				//金钱龟关闭，其他通道放开
//				if(agentId.equals("F20160015")){
//					m.put("isWork", "0");
//				}else{
//					if(holidayService.getisYILIAND0Work()){
//		                m.put("isWork", "1");
//		            }else{
//		                m.put("isWork", "1");
//		            }
//				}
				if(holidayService.getisYILIAND0Work()){
					m.put("isWork", "0");
				}else{
					m.put("isWork", "1");
				}
			} else if (m.containsKey(UserOrder.trans_type.JDQR.getCode() + "_100")) {
				m.put("channelRate", scg.getInputFeeJingDong().toString());
				m.put("channelD0Rate", scg.getInputFeeD0JingDong().toString());
			} else if (m.containsKey(UserOrder.trans_type.JDQR.getCode() + "_200")) {
				m.put("channelRate", scg.getInputFeeBigJingDong().toString());
				m.put("channelD0Rate", scg.getInputFeeD0BigJingDong().toString());
			} else if (m.containsKey(UserOrder.trans_type.BDQR.getCode() + "_100")) {
				m.put("channelRate", scg.getInputFeeBaidu().toString());
				m.put("channelD0Rate", scg.getInputFeeD0Baidu().toString());
			} else if (m.containsKey(UserOrder.trans_type.BDQR.getCode() + "_200")) {
				m.put("channelRate", scg.getInputFeeBigBaidu().toString());
				m.put("channelD0Rate", scg.getInputFeeD0BigBaidu().toString());
			} else if (m.containsKey(UserOrder.trans_type.YZFQR.getCode() + "_100")) {
				m.put("channelRate", scg.getInputFeeYizhifu().toString());
				m.put("channelD0Rate", scg.getInputFeeD0Yizhifu().toString());
			} else if (m.containsKey(UserOrder.trans_type.YZFQR.getCode() + "_200")) {
				m.put("channelRate", scg.getInputFeeBigYizhifu().toString());
				m.put("channelD0Rate", scg.getInputFeeD0Yizhifu().toString());
			} else if (m.containsKey(UserOrder.trans_type.YLQR.getCode() + "_100")) {
				m.put("channelRate", scg.getInputFeeYinLianzhifu().toString());
				m.put("channelD0Rate", scg.getInputFeeD0YinLianzhifu().toString());
			} else if (m.containsKey(UserOrder.trans_type.YLQR.getCode() + "_200")) {
				m.put("channelRate", scg.getInputFeeBigYinLianzhifu().toString());
				m.put("channelD0Rate", scg.getInputFeeD0YinLianzhifu().toString());
			}else if (m.containsKey(UserOrder.trans_type.QQQR.getCode() + "_200")) {
				m.put("channelRate", scg.getInputFeeBigQQzhifu().toString());
				m.put("channelD0Rate", scg.getInputFeeD0BigQQzhifu().toString());
			}
		}

		if (scg != null) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("channelName", "T0");
			m.put("channelRate", "0");
			/*
			 * 1付款， 2提现
			 */
			m.put("type", "2");
			m.put("maxAmt", scg.getMaxT0Amt().toString());
			m.put("minAmt", scg.getMinT0Amt().toString());
			m.put("fee", scg.getT0Fee().toString());
			maps.add(m);

			Map<String, String> m2 = new HashMap<String, String>();
			m2.put("channelName", "T1");
			m2.put("channelRate", "0");
			/*
			 * 1付款， 2提现
			 */
			m2.put("type", "2");
			m2.put("maxAmt", scg.getMaxT1Amt().toString());
			m2.put("minAmt", scg.getMinT1Amt().toString());
			m2.put("fee", scg.getT1Fee().toString());
			maps.add(m2);
		}
		return maps;
	}
	
	
	
	@Override
	public List<Map<String, String>> findShowChannelLimitZTC(Long userId, String agentId) {
		List<Map<String, String>> maps = getChannelLimitZTC(agentId);

		UserSettlementConfig scg = configService.getByUserId(userId);

		for (Map<String, String> m : maps) {
			if (m.containsKey(UserOrder.trans_type.ALQR.getCode() + "_300")) {
				m.put("channelRate", scg.getInputFeeZtAlipay().toString());
				m.put("channelD0Rate", scg.getInputFeeD0ZtAlipay().toString());
			} else if (m.containsKey(UserOrder.trans_type.WXQR.getCode() + "_300")) {
				m.put("channelRate", scg.getInputFeeZtWeixin().toString());
				m.put("channelD0Rate", scg.getInputFeeD0ZtWeixin().toString());
			} else if (m.containsKey(UserOrder.trans_type.YLZX.getCode() + "_300")) {
				m.put("channelRate", scg.getInputFeeZtYinlian().toString());
				m.put("channelD0Rate",scg.getInputFeeD0ZtYinlian().toString());
			} else if (m.containsKey(UserOrder.trans_type.YLZXJ.getCode() + "_300")) {
				m.put("channelRate", scg.getInputFeeZtYinlianJf().toString());
				m.put("channelD0Rate",scg.getInputFeeD0ZtYinlianJf().toString());
			}else if (m.containsKey(UserOrder.trans_type.YLZXE.getCode() + "_300")) {
				m.put("channelRate", scg.getInputFeeZtYinlianJf().toString());
				m.put("channelD0Rate",scg.getInputFeeD0ZtYinlianJf().toString());
			} else if (m.containsKey(UserOrder.trans_type.QQQR.getCode() + "_300")) {
				m.put("channelRate", scg.getInputFeeZtQQzhifu().toString());
				m.put("channelD0Rate",scg.getInputFeeD0ZtQQzhifu().toString());
			} 
		}
		
		
		
//		if (scg != null) {
//			Map<String, String> m = new HashMap<String, String>();
//			m.put("channelName", "T0");
//			m.put("channelRate", "0");
//			/*
//			 * 1付款， 2提现
//			 */
//			m.put("type", "2");
//			m.put("maxAmt", scg.getMaxT0Amt().toString());
//			m.put("minAmt", scg.getMinT0Amt().toString());
//			m.put("fee", scg.getT0Fee().toString());
//			maps.add(m);
//
//			Map<String, String> m2 = new HashMap<String, String>();
//			m2.put("channelName", "T1");
//			m2.put("channelRate", "0");
//			/*
//			 * 1付款， 2提现
//			 */
//			m2.put("type", "2");
//			m2.put("maxAmt", scg.getMaxT1Amt().toString());
//			m2.put("minAmt", scg.getMinT1Amt().toString());
//			m2.put("fee", scg.getT1Fee().toString());
//			maps.add(m2);
//		}
		return maps;
	}
	
	
	

	@Override
	public List<Tchannel> searchTchannels() {
		String hql = " from Tchannel t ";
		return channelDao.find(hql);
	}

	@Cacheable(value = "channelCache", key = "#channelId+'getChannelConfig'")
	@Override
	public JSONObject getChannelConfig(Long channelId) {
		Channel cl = get(channelId);
		if (cl != null) {
			return JSONObject.parseObject(cl.getConfig());
		}
		return null;
	}

	@Cacheable(value = "channelCache", key = "#channelName+'getAvailableChannelConfigByChannelName'")
	@Override
	public List<JSONObject> getAvailableChannelConfigByChannelName(String channelName) {
		List<JSONObject> ots = new ArrayList<JSONObject>();
		List<Object[]> t = channelDao.findBySql("select config, 1 from sys_channel where name='" + channelName + "'  GROUP BY account");
		for (Object[] obj : t) {
			ots.add(JSONObject.parseObject((String) obj[0]));
		}
		return ots;
	}

	@Override
	public List<Tchannel> searchTchannels(Channel channel) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from Tchannel t where 1=1 ";
		if (StringUtil.isNotBlank(channel.getName())) {
			hql += " and t.name =:name";
			params.put("name", channel.getName());
		}
		if(channel.getUserId()!=null){
			hql += " and t.userId =:userId";
			params.put("userId", channel.getUserId());
		}
		if(channel.getStatus()!=null){
			hql += " and t.status =:status";
			params.put("status", channel.getStatus());
		}
		if(StringUtil.isNotBlank(channel.getDetailName())){
			hql += " and t.detailName =:detailName";
			params.put("detailName", channel.getDetailName());
		}
		if(channel.getType()!=null){
			hql += " and t.type =:type";
			params.put("type", channel.getType());
		}
		if(StringUtil.isNotBlank(channel.getMerchantId())){
			hql += " and t.merchantId =:merchantId";
			params.put("merchantId", channel.getMerchantId());
		}
		List<Tchannel> l = channelDao.find(hql , params);
		return l;
	}
	
	
	@Override
	public List<Channel> dataGrid(Channel channel, PageFilter ph) {
		List<Channel> ul = new ArrayList<Channel>();
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select t from Tchannel t ";
		List<Tchannel> l = channelDao.find(hql + whereHql(channel, params) + orderHql(ph), params, ph.getPage(), ph.getRows());
		Map<String, String> map = getCountAmt();
		for (Tchannel t : l) {
			Channel u = new Channel();
			if(t.getName().equals("MINSHENG")){
				if(t.getTodayAmt().doubleValue()>0){
					Map<String, Object> cqlparams = new HashMap<String, Object>();
					String cql = "select sum(t.amt) from sys_channel_t0_tixian t left join sys_channel cl on t.chl_id=cl.ID ";
					ChannelT0Tixian ti = new ChannelT0Tixian();
					ti.setId(t.getId());
					ti.setSltStatus(100);
					ti.setCreateDatetimeStart(DateUtil.getStringFromDate(DateUtil.getStartOfDay(new Date())));
					ti.setCreateDatetimeEnd(DateUtil.getStringFromDate(DateUtil.getEndOfDay(new Date()))); 
					String hcql = cql + whereCql(ti,cqlparams);
					List<Object[]> accs = channelT0TixianDao.findBySql(hcql, cqlparams);
				    if(accs.get(0)!=null){
				    	BigDecimal acc = new BigDecimal(String.valueOf(accs.get(0)));
				    	u.setChannelAMT(String.valueOf(t.getTodayAmt().subtract(acc)));
				    }else{
				    	BigDecimal as = new BigDecimal(String.valueOf(0));
				    	u.setChannelAMT(String.valueOf(t.getTodayAmt().subtract(as)));
				    }
				}else{
					u.setChannelAMT("0.00");
				}
			    if(Double.parseDouble(u.getChannelAMT())<0){
			    	u.setChannelAMT("0.00");
			    }
			}
			BeanUtils.copyProperties(t, u);
			u.setCountTodayAmt(map.get("countTodayAmt"));
			u.setTixianAmt(map.get("TixianAmt"));
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
			hql += " where 1=1 and t.status !=10";
			if (StringUtil.isNotBlank(channel.getName())) {
				hql += " and t.name =:name";
				params.put("name", channel.getName());
			}
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
			if(channel.getChannelType()!=null){
				if(channel.getChannelType()==1){
					hql += " and t.name not in ('MINGSHENGZHITONGCHE','XINKKEZHITONGCHE','PINGANPAYZHITONGCHE','PINGANPAYZHITONGCHE_ZHIQING')";
				}else{
					hql += " and t.name in ('MINGSHENGZHITONGCHE','XINKKEZHITONGCHE','PINGANPAYZHITONGCHE','PINGANPAYZHITONGCHE_ZHIQING')";
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
	
	private String whereCql(ChannelT0Tixian t0, Map<String, Object> params) {
		String hql = "";
		if (t0 != null) {
			hql += " where 1=1 ";
			if (t0.getStatus() != null) {
				hql += " and t.status = :status";
				params.put("status", t0.getStatus());
			}
			if (t0.getId() != null) {
				hql += " and cl.id = :id";
				params.put("id", t0.getId());
			}
			try {
				if (StringUtil.isNotBlank(t0.getCreateDatetimeStart())) {
					hql += " and t.create_date >= :createDatetimeStart";
					params.put("createDatetimeStart", t0.getCreateDatetimeStart());
				}
				if (StringUtil.isNotBlank(t0.getCreateDatetimeEnd())) {
					hql += " and t.create_date <= :createDatetimeEnd";
					params.put("createDatetimeEnd", t0.getCreateDatetimeEnd());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return hql;
	}
   

	@Override
	public void updateThroughChannel() throws Exception {
		List<Tchannel> tcl = channelDao.find("select t from Tchannel t  where t.status=4");
		for (Tchannel t : tcl) {
			if(t.getName().equals("MINGSHENGZHITONGCHE")){
				SMZF007 f007 = new SMZF007();
				f007.setMerchantId(t.getMerchantId());
				f007.setCooperator("SMZF_SHFF_HD_T0");
				String responseStr = MinShengMerchantInputMinShengUtil.doPost(f007);;
				Map<String, String> resp = XmlMapper.xml2Map(responseStr);
				 if(resp.get("oriRespType").equals("S")){
					 if(resp.get("oriRespCode").equals("000000")){
						if(t.getName().equals("MINGSHENGZHITONGCHE")){
							t.setStatus(3);
						}else{
							t.setStatus(1);
						}
						String config = "{\"cooperator\":\"SMZF_SHFF_HD_T0\",\"callBack\":\"https://bbpurse.com/flypayfx/payment/minshengNotify\",\"serverUrl\":\"https://ipay.cmbc.com.cn:9020/nbp-smzf-hzf\",\"merchant_code\":\"%s\"}";
						t.setConfig(String.format(config,resp.get("merchantCode")));
						channelDao.update(t);
					 } 
				 }else if(resp.get("oriRespType").equals("E")){
				       t.setStatus(5);
				       channelDao.update(t);
				}
			}else if(t.getName().equals("XINKEZHITONGCHE")){
				JSONObject config = JSONObject.parseObject(t.getConfig());
				String[] keys = {"merid"};
				String[] inputParams = {config.getString("xinke.merchant_id")};
				String responseStr = XinkePayUtil.build(keys, inputParams, "out_mer_query");
				System.out.println(responseStr);
				Map<String, String> resultmap = XmlMapper.xml2Map(responseStr);
				System.out.println(resultmap);
				if (resultmap != null && resultmap.containsKey("rspCode") && "000000".equals(resultmap.get("rspCode"))) {
					if(resultmap.get("ZFBRSP")!=null && resultmap.get("ZFBRSP").equals("支付宝报备成功") && 
					   resultmap.get("WXRSP")!=null && resultmap.get("WXRSP").equals("微信报备成功")){
						t.setStatus(3);
						channelDao.update(t);
					}
				}
			}
		}
	}

	@Override
	public Map<String, String> getCountAmt() {
		Map<String, String> map = new HashMap<String, String>();
		List<Object[]> accs = channelDao.findBySql("select sum(today_Amt) from sys_channel c where c.name in('MINSHENG','MINGSHENGZHITONGCHE')");
		ChannelT0Tixian ti = new ChannelT0Tixian();
		ti.setStatus(100);
		ti.setCreateDatetimeStart(DateUtil.getStringFromDate(DateUtil.getStartOfDay(new Date())));
		ti.setCreateDatetimeEnd(DateUtil.getStringFromDate(DateUtil.getEndOfDay(new Date()))); 
		Map<String, Object> cqlparams = new HashMap<String, Object>();
		String cql = "select sum(t.amt) from sys_channel_t0_tixian t left join sys_channel cl on t.chl_id=cl.ID ";
		String hcql = cql + whereCql(ti,cqlparams);
		List<Object[]> accsTwo = channelT0TixianDao.findBySql(hcql, cqlparams);
		if(accsTwo.get(0)!=null){
			 map.put("TixianAmt", String.valueOf(accsTwo.get(0)));
		 }else{
			 map.put("TixianAmt", "0.00");
		 }
		if(accs.get(0)!=null){
			map.put("countTodayAmt", String.valueOf(accs.get(0)));
		}else{
			map.put("countTodayAmt", "0.00");
		}
		return map;
	}

	
	
	
	@Override
	public void updateThroughChannelPA() throws Exception {
		if(sysParamService.getSwitch("pingan_channel_seift_on")){
			Integer sum = Integer.parseInt(sysParamService.getSwitchSUM("pingan_channel_sum_on"));
			Integer sumTwo = sum;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("type", 200);
			params.put("status", 100);
			String hql = "select t from Tchannel t where t.name='PINGANPAY' and t.status=:status and t.type=:type";
			List<Tchannel> ul = channelDao.find(hql,params);
			if(ul.size()!=0){
				if(sum>ul.size()){
					sum = ul.size();
				}
				ediTChannel(hql, params);
				for (int i = 0; i < sum; i++) {
					Tchannel c = ul.get(i);
					c.setStatus(0);
					c.setSeq(1);
					channelDao.update(c);
				}
			}else{
				log.info("-----支付宝无备用通道暂不替换--------");
			}
			
			
			
			params.put("type", 300);
			params.put("status", 100);
			List<Tchannel> ut = channelDao.find(hql,params);
			if(ut!=null){
				if(sumTwo>ut.size()){
					sumTwo = ut.size();
				}
				ediTChannel(hql, params);
				for (int i = 0; i < sumTwo; i++) {
					Tchannel e = ut.get(i);
					e.setStatus(0);
					e.setSeq(1);
					channelDao.update(e);
				}
			}else{
				log.info("-----微信无备用通道暂不替换--------");
			}
		}else{
			log.info("-----更新平安子商户信息开关关闭--------");
		}
	}

	
	
	
	

	
	private void ediTChannel(String hql,Map<String, Object> params){
		params.put("status", 0);
		List<Tchannel> uc = channelDao.find(hql,params);
		for (int i = 0; i < uc.size(); i++) {
			Tchannel l = uc.get(i);
			l.setSeq(10);
			l.setStatus(1);
			channelDao.update(l);
		}
	}
	
	
	
	
	
	
	

}