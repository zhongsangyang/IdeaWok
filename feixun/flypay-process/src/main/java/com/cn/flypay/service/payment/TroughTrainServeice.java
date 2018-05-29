package com.cn.flypay.service.payment;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.pageModel.base.PageFilter;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.User;


/**
 * 直通车service
 * @author LW
 *
 */
public interface TroughTrainServeice {
	
	/**
	 * 核实用户是否开直通车子商户
	 * @param userId
	 * @return
	 */
	public Map<String, String> editChannle(Long userId)throws Exception;
	
	/**
	 * 检查平安通道本地信息与平安报备信息的一致性
	 * @param channelId
	 * @return
	 */
	public Map<String, String> checkPingAnChannelCorrect(Long channelId);
	
	/**
	 * 检查PINGANPAYZHITONGCHE通道是否存在
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> editChannlePAPay(Long userId)throws Exception;
	
	/**
	 * 检查PINGANPAYZHITONGCHE_ZHIQING通道是否存在
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> editChannlePAPayZHIQING(Long userId)throws Exception;
	
	/**
	 * 创建平安直清模式的通道,以及通道支付类型的补充配置
	 * @param merchantName	商户全名
	 * @param shortName  商户简称
	 * @param userId  sys_user的id
	 * @return
	 */
	public JSONObject createPingAnZhiQingMer(String merchantName,String shortName,Long userId);
	
	
	
	/**
	 * 开通翼支付直通车
	 * @param sub_merchant_id 入驻平安返回的子商户id
	 * @param merchantName 入驻平安时的商户名
	 * @param userId
	 * @param shortType
	 * @return
	 */
	public Boolean addPingAnCreateYZFMer(String sub_merchant_id,Long userId);
	
	/**
	 * 保存平安直通车信息
	 * @param detailName
	 * @param type
	 * @param config
	 * @param userId
	 * @param merchantId
	 */
	public void addPINGANPAYZHITONGCHEChannel(String detailName,Integer type,String config,Long userId,String merchantId);
	
	
	/**
	 * 开用户直通车子商户
	 * @param merchantName
	 * @param shortName
	 * @param userId
	 * @return
	 */
	public Boolean addcreatCommercial(String merchantName,String shortName,Long userId,String shortType);
	
	/**
	 * 报备平安--微信子商户配置
	 * @param channelId
	 * @param objStr 参数名称
	 * 		jsapi_path:授权目录  sub_appid:关联微信公众号id    subscribe_appid:推荐关注微信公众号id
	 * @param objVal 参数名称对应的参数值
	 * @return
	 * @throws Exception
	 */
	public  Map<String,String> addPingAnWeiXinSupplement(Long channelId,String objStr,String objVal);
	
	/**
	 * 报备平安-微信补充接口
	 * @param channelId	通道在表中的id
	 * @param userId	
	 * @return
	 */
	public  Map<String,String> addPingAnWeiXinMer(Long channelId);
	
	/**
	 * 报备平安-支付宝补充接口
	 * @param channelId	通道在表中的id
	 * @param userId	
	 * @return
	 */
	public  Map<String,String> addPingAnZhiFuBaoMer(Long channelId);
	
	
	
	
	/**
	 * 创建平安子商户(普通)
	 * @param merchantName
	 * @param shortName
	 * @param userId
	 * @param shortType
	 * @return
	 */
	public Boolean addcreatCommercialPAPay(String merchantName,String shortName,Long userId,String shortType);
	
	
	
	/**
	 * 创建平安子商户（普通）--为支付宝单独开通
	 * @param merchantName
	 * @param shortName
	 * @param userId
	 * @param shortType
	 * @return
	 */
	public Boolean addcreatCommercialPAPayForZhiFuBaoAlone(String merchantName,String shortName,Long userId,String shortType);
	
	
	/**
	 * 开通易宝直通车商户
	 */
	public Boolean addCreateYiBaoZTCChannel(String detailName,Integer type,String config,Long userId,String merchantId);
	
	/**
	 * 获取用户直通车商户通道
	 * @param bool
	 * @param type
	 * @param userId
	 * @param accType  到账类型  0：D0到账   1：T1到账
	 * @param channelName
	 * @return
	 */
	public ChannelPayRef getChannelPayRef(Boolean bool,String type,Long userId,String accType,String channelName);
	
	/**
	 * 直通车费率查询
	 * @return
	 */
	public List<Map<String, String>> getqueryfeethroughtrain();
	
	
	/**
	 * 现在直通车虚账户余额
	 * @param userId
	 * @param transAmt
	 * @return
	 */
	public boolean updateAccount(String userId,String transAmt);
	
	/**
	 * 获取用户
	 * @return
	 */
	public List<User> dataGrid();
	
	
	/**
	 * 开通商户
	 * @param user
	 * @return
	 */
	public Boolean addMocde(User user);
	
	
	public void PINGANYIZF(String Name);

}
