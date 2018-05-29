package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TcardBankConfig;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.sys.CardBankConfigService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.ImportUtil;
import com.cn.flypay.utils.yilian.YiLianYlzxUtil;
import com.rd.constant.ValueConstant;
import com.rd.model.MerchantPagePayDReq;
import com.rd.model.MerchantPayQryReq;

/**
 * 银联限额大额直通车
 * 易联通道--快捷支付接口
 * 通道name = 'YILIANZHIFUZTC'
 * @author liangchao
 *
 */
@Service(value = "yfyianZXXEPaymentService")
public class FYianZXXEPaymentServiceImpl extends AbstractChannelPaymentService{
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private BaseDao<TuserCard> userCardDao;
	
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	
	@Autowired
	private CardBankConfigService bankConfigService;
	
	@Autowired
	private UserCardService userCardService;
	
	/**
	 * 易联通道更新，开通与下单结合成一个请求接口
	 * @param user
	 * @param cpr
	 * @param inputAccType
	 * @param cardId
	 * @param frontUrl
	 * @param money
	 * @param transPayType
	 * @param angentType
	 * @param desc
	 * @return
	 */
	@Override
	public Map<String, String> createYLZXOrder_v2(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			//付款卡信息
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			//查询结算卡
			TuserCard cardJ = userCardDao.get("select t from TuserCard t left join t.bank left join t.user u  where u.id=" + user.getId() +" and t.isSettlmentCard = 1 ");
			if(cardJ==null){
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG", "未设置结算卡");
				return resultMap;
			}
			JSONObject config = cpr.getConfig();
			
			//查询用户费率&限额配置
			BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(user.getId(), UserOrder.trans_type.YLZXJ.getCode(), inputAccType);
			
			//按照卢总要求，添加2元手续费
			//添加2元提现费--start
			BigDecimal b1 = BigDecimal.valueOf(money);
			BigDecimal b2 = BigDecimal.valueOf(2); 
			BigDecimal b3 = b2.divide(b1,4,BigDecimal.ROUND_DOWN);	//提现费手续费=2/交易金额（最小保留万分数，后面有余数不做四舍五入） 
			BigDecimal b4 = rate[0].add(b3);
			//添加2元提现费--end
			
			
			//费率 易联要求不带百分号  百分之3  就写3
			BigDecimal b = b4.multiply(new BigDecimal(100));
			MerchantPagePayDReq req = new MerchantPagePayDReq();
			//商品订单号
			req.setTransactionId("YF"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			//商品订单金额
			req.setOrderAmount(String.valueOf(money));
			//币种  CNY：人民币
			req.setCur("CNY");
			
			//订单描述
			req.setOrderDesc("跑步机");
			//服务器接受支付结果的前台地址
			req.setPageUrl(frontUrl);
			
			//服务器接受支付结果的后台地址
			req.setBgUrl(config.getString("ylaccount.bankUrl"));	//  payment/yfXe_ylzx_Notify
			//下单ip
			req.setBuyerIp("192.168.1.14");
			//支付方式  1006：银联快捷支付（同名代付） 1012：银联快捷支付（页面支付）
			req.setPayType("1012");
			//付款银行编码  易联不做校验
			String code = getBankCode(card.getBank().getBankName());
//		if(code.equals("CMB")||card.getCardType().equals("J")){
//			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
//			resultMap.put("flagMSG","暂不支持此卡");
//			return resultMap;
//		}
			req.setPayerBankCode(code);
			//付款方银行卡号
			req.setPayerAcc(card.getCardNo());
			//付款方名称
			req.setPayerName(user.getRealName());
			//付款方手机号
			req.setPayerPhoneNo(card.getPhone());
			
			//付款方身份证号
			req.setPayerIdNum(user.getIdNo());
			//付款方卡类型，借记：DC；贷记：CC
			req.setCardType("CC");
			//校验用户的cvv和验证码信息
//			if(StringUtil.isBlank(card.getCvv())  ||  StringUtil.isBlank(card.getValidityDate())){
//				resultMap.put("flag", GlobalConstant.RESP_CODE_083);
//				resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_083) );
//				return resultMap;
//			}
//			String Date = card.getValidityDate();
//			String Date1 = Date.substring(0,2);
//			String Date2 = Date.substring(2,4);
//		req.setExpiryDate(Date2+Date1);
//		req.setCvv2(card.getCvv());
//		
			//贷记卡有效期
			req.setExpiryDate("0121");
			//CVV2码
			req.setCvv2("832");
			
			//对公：B；对私：C
			req.setPrivateFlag("C");
			//收款方银行编码
			req.setPayeeBankCode(getBankCode(cardJ.getBank().getBankName()));
			
			TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(cardJ.getCardNo()));
			
			//收款方银行联行号
			req.setPayeeUnionBank(cbc.getBankCode());
			
			//收款方银行卡号
			req.setPayeeAcc(cardJ.getCardNo());
			//收款方手机号
			req.setPayeePhoneNo(cardJ.getPhone());
			//费率，不带百分号   例如：百分之1  就填写  1
			req.setFeeRate(String.valueOf(b));
			//封顶手续费
			req.setMaxFee("200");
			
			//调用易联通道-快捷支付接口-快捷预下单
			JSONObject result = YiLianYlzxUtil.send(req, ValueConstant.TRANS_CODE_T01005, YiLianYlzxUtil.merachnetId);
			if(result == null){
				log.info("---银联积分---易联通道----快捷通道 ---调用快捷支付---请求易联失败");
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_051));
			}
			if(result.containsKey("retCode") && result.getString("retCode").equals("RC0002")){
				
				
				desc = user.getRealName() + "_" + user.getLoginName() + "正在用卡" + card.getCardNo() + "向"+cardJ.getCardNo()+"支付" + money+ "元";
				userOrderService.createTransOrder(user.getId(), req.getTransactionId(), null, null, UserOrder.trans_type.YLZXJ.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType,
						cpr.getChannel(), inputAccType, angentType);
				log.info("---银联积分---易联通道----快捷通道 ---调用快捷支付---保存订单："+req.getTransactionId() + "  ----成功");
				
				
				//跳转到易联给的支付页面去
				resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
				String openHtml = result.getString("payUrl");
				
				String a = convertWordEncode(openHtml);
				log.info("utf-8 编码：" + a) ;  
				resultMap.put("html", a);
//				resultMap.put("html", "http://1g83849h98.iask.in:34530/flypayfx/mobile/YiLianResponse?result="+openHtml);	//测试用
				//为2的时候，表示要去调用易联开通快捷支付,但是，易联新接口实现开通和下单结合，这里就也使用2
				resultMap.put("type", "2");
				return resultMap;
			}else{
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				
				//手续费不能低于5.00
				if(result.getString("retRemark").equals("手续费不能低于5.00")){
					resultMap.put("flagMSG", "输入金额过低");
				}else{
					resultMap.put("flagMSG", result.getString("retRemark"));
				}
				
			}
		} catch (Exception e) {
			log.error("---银联积分---易联通道----快捷通道 ---异常:", e);
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_051));
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 易联通道更新，开通与下单结合成一个请求接口
	 * @param user
	 * @param cpr
	 * @param inputAccType
	 * @param cardId
	 * @param frontUrl
	 * @param money
	 * @param transPayType
	 * @param angentType
	 * @param desc
	 * @return
	 */
	@Override
	public Map<String, String> createUnipayOnlineThroughOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			//付款卡信息
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			//查询结算卡
			TuserCard cardJ = userCardDao.get("select t from TuserCard t left join t.bank left join t.user u  where u.id=" + user.getId() +" and t.isSettlmentCard = 1 ");
			if(cardJ==null){
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG", "未设置结算卡");
				return resultMap;
			}
			JSONObject config = cpr.getConfig();
			
			//查询用户费率&限额配置
			BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(user.getId(), UserOrder.trans_type.YLZXJ.getCode(), inputAccType);
			
			//按照卢总要求，添加2元手续费
			//添加2元提现费--start
			BigDecimal b1 = BigDecimal.valueOf(money);
			BigDecimal b2 = BigDecimal.valueOf(2); 
			BigDecimal b3 = b2.divide(b1,4,BigDecimal.ROUND_DOWN);	//提现费手续费=2/交易金额（最小保留万分数，后面有余数不做四舍五入） 
			BigDecimal b4 = rate[0].add(b3);
			//添加2元提现费--end
			
			
			//费率 易联要求不带百分号  百分之3  就写3
			BigDecimal b = b4.multiply(new BigDecimal(100));
			MerchantPagePayDReq req = new MerchantPagePayDReq();
			//商品订单号
			req.setTransactionId("YF"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			//商品订单金额
			req.setOrderAmount(String.valueOf(money));
			//币种  CNY：人民币
			req.setCur("CNY");
			
			//订单描述
			req.setOrderDesc("跑步机");
			//服务器接受支付结果的前台地址
			req.setPageUrl(frontUrl);
			
			//服务器接受支付结果的后台地址
			req.setBgUrl(config.getString("ylaccount.bankUrl"));	//  payment/yfXe_ylzx_Notify
			//下单ip
			req.setBuyerIp("192.168.1.14");
			//支付方式  1006：银联快捷支付（同名代付） 1012：银联快捷支付（页面支付）
			req.setPayType("1012");
			//付款银行编码  易联不做校验
			String code = getBankCode(card.getBank().getBankName());
//		if(code.equals("CMB")||card.getCardType().equals("J")){
//			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
//			resultMap.put("flagMSG","暂不支持此卡");
//			return resultMap;
//		}
			req.setPayerBankCode(code);
			//付款方银行卡号
			req.setPayerAcc(card.getCardNo());
			//付款方名称
			req.setPayerName(user.getRealName());
			//付款方手机号
			req.setPayerPhoneNo(card.getPhone());
			
			//付款方身份证号
			req.setPayerIdNum(user.getIdNo());
			//付款方卡类型，借记：DC；贷记：CC
			req.setCardType("CC");
			//校验用户的cvv和验证码信息
//			if(StringUtil.isBlank(card.getCvv())  ||  StringUtil.isBlank(card.getValidityDate())){
//				resultMap.put("flag", GlobalConstant.RESP_CODE_083);
//				resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_083) );
//				return resultMap;
//			}
//			String Date = card.getValidityDate();
//			String Date1 = Date.substring(0,2);
//			String Date2 = Date.substring(2,4);
//		req.setExpiryDate(Date2+Date1);
//		req.setCvv2(card.getCvv());
//		
			//贷记卡有效期
			req.setExpiryDate("0121");
			//CVV2码
			req.setCvv2("832");
			
			//对公：B；对私：C
			req.setPrivateFlag("C");
			//收款方银行编码
			req.setPayeeBankCode(getBankCode(cardJ.getBank().getBankName()));
			
			TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(cardJ.getCardNo()));
			
			//收款方银行联行号
			req.setPayeeUnionBank(cbc.getBankCode());
			
			//收款方银行卡号
			req.setPayeeAcc(cardJ.getCardNo());
			//收款方手机号
			req.setPayeePhoneNo(cardJ.getPhone());
			//费率，不带百分号   例如：百分之1  就填写  1
			req.setFeeRate(String.valueOf(b));
			//封顶手续费
			req.setMaxFee("200");
			
			//调用易联通道-快捷支付接口-快捷预下单
			JSONObject result = YiLianYlzxUtil.send(req, ValueConstant.TRANS_CODE_T01005, YiLianYlzxUtil.merachnetId);
			if(result == null){
				log.info("---银联积分---易联通道----快捷通道 ---调用快捷支付---请求易联失败");
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_051));
			}
			if(result.containsKey("retCode") && result.getString("retCode").equals("RC0002")){
				
				
				desc = user.getRealName() + "_" + user.getLoginName() + "正在用卡" + card.getCardNo() + "向"+cardJ.getCardNo()+"支付" + money+ "元";
				userOrderService.createTransOrder(user.getId(), req.getTransactionId(), null, null, UserOrder.trans_type.YLZXJ.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType,
						cpr.getChannel(), inputAccType, angentType);
				log.info("---银联积分---易联通道----快捷通道 ---调用快捷支付---保存订单："+req.getTransactionId() + "  ----成功");
				
				
				//跳转到易联给的支付页面去
				resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
				String openHtml = result.getString("payUrl");
				
				String a = convertWordEncode(openHtml);
				log.info("utf-8 编码：" + a) ;  
				resultMap.put("html", a);
//				resultMap.put("html", "http://1g83849h98.iask.in:34530/flypayfx/mobile/YiLianResponse?result="+openHtml);	//测试用
				//为2的时候，表示要去调用易联开通快捷支付,但是，易联新接口实现开通和下单结合，这里就也使用2
				resultMap.put("type", "2");
				return resultMap;
			}else{
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				
				//手续费不能低于5.00
				if(result.getString("retRemark").equals("手续费不能低于5.00")){
					resultMap.put("flagMSG", "输入金额过低");
				}else{
					resultMap.put("flagMSG", result.getString("retRemark"));
				}
				
			}
		} catch (Exception e) {
			log.error("---银联积分---易联通道----快捷通道 ---异常:", e);
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_051));
			e.printStackTrace();
		}
		return resultMap;
	}
	
	//对返回的链接进行转码
	public static String convertWordEncode(String content) {
		try {
			String string = "";
			String str0 = "";
			for (int i = 0; i < content.length(); i++) {
				if (content.substring(i, i + 1).matches("[\u4e00-\u9fa5]+")) {
					str0 = URLEncoder.encode(content.substring(i, i + 1), "utf-8");
				} else {
					str0 = content.substring(i, i + 1);
				}
				string += str0;
			}
			return string;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	
	/**
	 * 该接口已作废
	 */
//	@Override
//	public Map<String, String> createYLZXOrder_v2(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
//		Map<String, String> resultMap = new HashMap<String, String>();
//		try {
//			//付款卡信息
//			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
//			//查询结算卡
//			TuserCard cardJ = userCardDao.get("select t from TuserCard t left join t.bank left join t.user u  where u.id=" + user.getId() +" and t.isSettlmentCard = 1 ");
//			if(cardJ==null){
//				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
//				resultMap.put("flagMSG", "未设置结算卡");
//				return resultMap;
//			}
//			JSONObject config = cpr.getConfig();
//			
//			
//			//添加开通快捷支付   0未开通  1开通
////			if(StringUtil.isBlank(card.getIsOpenYilianQuickPay())){
////				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
////				resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_999));
////				return resultMap;
////			}else 
//				
//				
//			if(StringUtil.isBlank(card.getIsOpenYilianQuickPay()) || card.getIsOpenYilianQuickPay().equals("0")){
//				log.info("---银联积分---易联通道----快捷通道 ---当前用户未开通易联快捷支付");
//				MerchantOpenQuickPayReq openReq = new MerchantOpenQuickPayReq();
//				openReq.setTransactionId("YF"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//				openReq.setPayType(ValueConstant.PAY_TYPE_1301);
//				//绑卡成功，点击页面-返回商户 时触发该链接，供APP捕捉做相应处理
//				openReq.setPageUrl(frontUrl);	
//				openReq.setCardId(card.getCardNo()); //必须是信用卡
//				openReq.setName(user.getRealName());
//				openReq.setIdNum(user.getIdNo());
//				openReq.setPhone(card.getPhone());	//必须和银行卡的手机号码对应
//				
//				//调用易联通道-开通快捷支付接口
//				log.info("---银联积分---易联通道----快捷通道 ---调用开通快捷支付---开通信用卡号为"+card.getId());
//				JSONObject openRes = YiLianYlzxUtil.send(openReq, ValueConstant.TRANS_CODE_T01031, config.getString("ylaccount.merId"));
//				
//				if(openRes != null){
//					if(openRes.containsKey("isSign") && StringUtil.isNotBlank(openRes.getString("isSign")) && openRes.get("isSign").equals("1") ){
//						//返回结果显示已经开通,更新状态为已开通
//						userCardService.updateIsOpenYilianQuickPay(card.getId(),"1");
//					}else if(openRes.containsKey("retCode") && openRes.getString("retCode").equals("RC0000")){
//						//跳转至开通输入资料界面
//						resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
//						String openHtml = openRes.getString("openHtml");
//						openHtml = DESutil.encrypt(openHtml, DESutil.password);
//						openHtml = URLEncoder.encode(openHtml);
//						resultMap.put("html", "http://101.200.34.95:26370/flypayfx/mobile/YiLianResponse?result="+openHtml);
////						resultMap.put("html", "http://1g83849h98.iask.in:34530/flypayfx/mobile/YiLianResponse?result="+openHtml);	//测试用
//						//为2的时候，表示要去调用易联开通快捷支付
//						resultMap.put("type", "2");
//						return resultMap;
//					}else{
//						log.info("---银联积分---易联通道----快捷通道 ---调用开通快捷支付---开通信用卡号为"+card.getId());
//						//显示订单创建失败
//						resultMap.put("flag", GlobalConstant.RESP_CODE_999);
//						resultMap.put("flagMSG",openRes.getString("retRemark"));
//						return resultMap;
//					}
//				}
//			}
//			
//			//查询用户费率&限额配置
//			BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(user.getId(), UserOrder.trans_type.YLZXJ.getCode(), inputAccType);
//			//费率 易联要求不带百分号  百分之3  就写3
//			BigDecimal b = rate[0].multiply(new BigDecimal(100));
//			
//			MerchantDFQuickPayReq req = new MerchantDFQuickPayReq();
//			req.setTransactionId("YF"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//			req.setOrderAmount(String.valueOf(money));
//			//币种  CNY：人民币
//			req.setCur("CNY");
//			//商品名称
//			req.setProductName("跑步机");
//			//商品数量
//			req.setProductNum("1");
//			req.setProductDesc("跑步机");
//			req.setOrderDesc("跑步机");
//			//服务器接受支付结果的后台地址
//			req.setBgUrl(config.getString("ylaccount.bankUrl"));	//  payment/yfXe_ylzx_Notify
//			//下单ip
//			req.setBuyerIp("192.168.1.14");
//			//支付方式  1006：银联快捷支付（同名代付）
//			req.setPayType("1006");
//			//付款银行编码  易联不做校验
//			String code = getBankCode(card.getBank().getBankName());
////			if(code.equals("CMB")||card.getCardType().equals("J")){
////				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
////				resultMap.put("flagMSG","暂不支持此卡");
////				return resultMap;
////			}
//			req.setPayerBankCode(code);
//			//付款方银行卡号
//			req.setPayerAcc(card.getCardNo());
//			//付款方名称
//			req.setPayerName(user.getRealName());
//			req.setPayerPhoneNo(card.getPhone());
//			//付款方卡类型，借记：DC；贷记：CC
//			req.setCardType("CC");
//			
//			//校验用户的cvv和验证码信息
//			if(StringUtil.isBlank(card.getCvv())  ||  StringUtil.isBlank(card.getValidityDate())){
//				resultMap.put("flag", GlobalConstant.RESP_CODE_083);
//				resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_083) );
//				return resultMap;
//			}
//			String Date = card.getValidityDate();
//			String Date1 = Date.substring(0,2);
//			String Date2 = Date.substring(2,4);
////			req.setExpiryDate(Date2+Date1);
////			req.setCvv2(card.getCvv());
////			
//			req.setExpiryDate("0121");
//			req.setCvv2("832");
//			
//			
//			//付款方身份证号
//			req.setPayerIdNum(user.getIdNo());
//			//对公：B；对私：C
//			req.setPrivateFlag("C");
//			//收款方银行编码
//			req.setPayeeBankCode(getBankCode(cardJ.getBank().getBankName()));
//			//收款方银行卡号
//			req.setPayeeAcc(cardJ.getCardNo());
//			//收款方手机号
//			req.setPayeePhoneNo(cardJ.getPhone());
//			//费率，不带百分号   例如：百分之1  就填写  1
//			req.setFeeRate(String.valueOf(b));
//			
//			//调用易联通道-快捷支付接口-快捷预下单
//			JSONObject result = YiLianYlzxUtil.send(req, ValueConstant.TRANS_CODE_T01015, config.getString("ylaccount.merId"));
//			if (result!=null) {
//				if(result.containsKey("retCode") && result.getString("retCode").equals("RC0000")){
//					if(result.containsKey("transactionId")){
//						desc = user.getRealName() + "_" + user.getLoginName() + "正在用卡" + card.getCardNo() + "向"+cardJ.getCardNo()+"支付" + money+ "元";
//						userOrderService.createTransOrder(user.getId(), req.getTransactionId(), null, null, UserOrder.trans_type.YLZXJ.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType,
//								cpr.getChannel(), inputAccType, angentType);
//						log.info("---银联积分---易联通道----快捷通道 ---调用快捷支付---保存订单："+req.getTransactionId() + "  ----成功");
//						//为1时表示要去调用验证码页面
//						resultMap.put("type", "1");
//						resultMap.put("transactionId", result.getString("transactionId"));
//						resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
//					}
//				}else{
//					resultMap.put("flag", GlobalConstant.RESP_CODE_051);
//					resultMap.put("flagMSG", result.getString("retRemark"));
//				}
//			}else {
//				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
//				resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_051));
//			}
//		} catch (Exception e) {
//			log.error("---银联积分---易联通道----快捷通道 ---异常:", e);
//			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
//			resultMap.put("flagMSG",GlobalConstant.map.get(GlobalConstant.RESP_CODE_051));
//			e.printStackTrace();
//		}
//		return resultMap;
//	}
	
	
	
	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {
		UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNum);
		if (userOrder != null) {
			JSONObject channelJson = channelService.getChannelConfig(userOrder.getChannelId());
			try {
				MerchantPayQryReq req = new MerchantPayQryReq();
				//此次查询的
				req.setTransactionId("YFYLZXCX"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				//原商户订单号
				req.setRefTxnId(orderNum);
				//调用易联通道查询接口
				JSONObject result = YiLianYlzxUtil.send(req, ValueConstant.TRANS_CODE_Q01001, channelJson.getString("ylaccount.merId"));
				if(result!=null && result.getString("qryRetCode").equals("RC0000")){
					PayOrder payOrder = new PayOrder();
					payOrder.setPayAmt(BigDecimal.valueOf(result.getDoubleValue("orderAmount")));
					payOrder.setRealAmt(BigDecimal.valueOf(result.getDoubleValue("orderAmount")));
					payOrder.setPayFinishDate(new Date().toString());
					payOrder.setPayNo(result.getString("transactionId"));
					Boolean flag = false;
					if ("RC0000".equals(result.getString("retCode"))) {
						flag=true;
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
					} else if ("RC0003".equals(result.getString("retCode"))) {
						flag=true;
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
					}
					if (flag) {
						userOrderService.finishInputOrderStatus(orderNum, payOrder);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	
	private BigDecimal getInputOrderFee(Integer inputAccType, Integer transPayType, BigDecimal amtbd, BigDecimal feeRate) {
		BigDecimal fee = BigDecimal.ZERO;
		if (inputAccType == 0 || inputAccType == 1 || inputAccType == 10 || inputAccType == 11) {
			if (transPayType - UserOrder.trans_pay_type.PUBLIC_PAY_TYPE.getCode() == 0) {
				// T0 输入的手续费，最少0.01元
				fee = BigDecimal.valueOf(0.01d);
				BigDecimal realAmt = amtbd.multiply(feeRate);
				if (realAmt.compareTo(fee) >= 0) {
					fee = realAmt.setScale(2, RoundingMode.UP);
				}
			}
		} else if (inputAccType == 5) {
			// T5 或者 购买代理类型
			fee = BigDecimal.ZERO;
		}
		return fee;
	}
	
	
	
	
	/**
	 * 获取付款银行编码
	 * @param bank
	 * @return
	 */
	public String getBankCode(String bank){
		Map<String, String> map = new HashMap<String, String>();
		map.put("中国工商银行", "ICBC");
		map.put("中国银行", "BOC");
		map.put("工商银行", "ICBC");
		map.put("交通银行", "BOCM");
		map.put("邮箱储蓄银行", "PSBC");
		map.put("招商银行", "CMB");
		map.put("中信银行", "CITIC");
		map.put("民生银行", "CMBC");
		map.put("光大银行", "CEB");
		map.put("华夏银行", "HXB");
		map.put("广东发展银行", "GDB");
		map.put("广发银行股份有限公司", "GDB");
		map.put("兴业银行", "CIB");
		map.put("国家开发银行", "CDB");
		map.put("农业银行", "ABC");
		map.put("中国建设银行", "CCB");
		map.put("中国进出口银行", "EIB");
		map.put("中国农业发展银行", "ADBC");
		map.put("平安银行", "PAB");
		map.put("浦东发展银行", "SPDB");
		map.put("渤海银行股份有限公司", "CBHB");
		map.put("浙商银行", "CZB");
		map.put("徽商银行", "HSB");
		String bankCode = map.get(bank);
		if(bankCode==null){
			return "BOC";
		}
		return bankCode;
	}
	
	
	

}
