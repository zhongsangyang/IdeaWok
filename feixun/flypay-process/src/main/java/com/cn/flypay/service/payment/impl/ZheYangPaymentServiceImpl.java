package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.PayTypeLimitConfig;
import com.cn.flypay.pageModel.sys.ServiceMerchant;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.sys.UserMerchantConfig;
import com.cn.flypay.pageModel.sys.UserSettlementConfig;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.sys.PayTypeLimitConfigService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.utils.DESutil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.zheyang.ZheYangUtil;

/**
 * 哲扬通道
 * 
 * @author liangchao
 *
 */
@Service(value = "zheYangPaymentService")
public class ZheYangPaymentServiceImpl extends AbstractChannelPaymentService {

	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private CommonService commonService;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private UserService userService;
	@Autowired
	private BaseDao<TuserCard> userCardDao;
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	@Autowired
	private PayTypeLimitConfigService payTypeLimitConfigService;

	/**
	 * 创建子商户(进件) 截止到2017-9-25 新版本进件和创建订单合并，该接口弃用
	 */
	@Override
	public UserMerchantConfig createSubMerchant(ServiceMerchant sm, Map<String, String> params) {
		log.info("-------哲扬通道--创建子商户(进件)-start");
		try {
			// 拼接参数
			// JSONObject config = JSONObject.parseObject(sm.getConfig());
			// 查询卡的信息
			// UserCard card = userCardService.get(
			// Long.parseLong(params.get("card_id")));
			// //查询用户的信息
			// User user = userService.get(
			// Long.parseLong(params.get("user_id")));
			JSONObject req = new JSONObject();
			// 通用参数 这里是代理商的信息
			req.put("encryptId", params.get("encryptId"));
			req.put("key", params.get("key"));
			req.put("aid", params.get("aid"));
			// 调用的方法
			req.put("method", "addPersonalMerchant");

			// 商户姓名
			req.put("name", params.get("name"));
			// 联系电话
			req.put("cellPhone", params.get("cellPhone"));
			// 身份证号
			req.put("idCard", params.get("idCard"));
			// 银行卡号
			req.put("accountNumber", params.get("accountNumber"));
			// 预留手机号 2017-9-19 版本，预留手机号可填写随意数字
			req.put("bankAccountTel", params.get("bankAccountTel"));
			// 身份证照片imageId 废除
			// req.put("legalPersonID1ImageId",
			// params.get("legalPersonID1ImageId"));
			// 交易手续费 可选
			if (StringUtil.isNotBlank(params.get("fastpayFee"))) {
				req.put("fastpayFee", params.get("fastpayFee"));
			}
			JSONObject result = ZheYangUtil.execute(req);

			if (result != null) {
				if (result.get("code").equals("000000")) {
					UserMerchantConfig umc = new UserMerchantConfig();
					umc.setSubMerchantId(result.getString("mid"));
					umc.setServiceMerchantId(sm.getId());
					umc.setConfig(result.getJSONObject("data").toJSONString());
					umc.setType(UserMerchantConfig.merchant_config_type.COMPOSITE.getCode()); // 类型为综合
					// 保存子商户信息待定!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					return umc;
				} else {
					log.error("-------哲扬通道--创建子商户(进件)--创建失败，返回错误信息：" + result.getString("message"));
					return null;
				}
			} else {
				log.error("-------哲扬通道--创建子商户(进件)--进程报错: 验证签名失败，请查看前后报文分析异常，请查看前后报文分析异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("-------哲扬通道--创建子商户(进件)-end");
		return null;
	}

	/**
	 * 哲扬通道--商户新增信用卡 截止到2017-9-25 新版本进件和创建订单合并，该接口弃用
	 */
	@Override
	public Map<String, String> addCreditCard(Map<String, String> params) {
		log.info("-------哲扬通道--商户新增信用卡-start");
		try {

			// //查询卡的信息
			// UserCard card = userCardService.get(
			// Long.parseLong(params.get("card_id")));
			// //查询用户的信息
			// User user = userService.get(
			// Long.parseLong(params.get("user_id")));
			JSONObject req = new JSONObject();
			// 通用参数 商户自身的信息
			req.put("encryptId", params.get("encryptId"));
			req.put("key", params.get("key"));
			req.put("mid", params.get("mid"));

			// 调用的方法
			req.put("method", "addCreditCard");
			// 银行卡号
			req.put("accountNumber", params.get("accountNumber"));
			// 预留手机号
			req.put("tel", params.get("tel"));

			// 手持信用卡正面 使用图片上传返回的 imageId
			// req.put("creditCardImageId", params.get("creditCardImageId"));
			// 信用卡有效期
			req.put("expired", params.get("expired"));

			JSONObject data = ZheYangUtil.execute(req);
			Map<String, String> result = new HashMap<String, String>();
			if (data != null) {
				if (data.get("code").equals("000000")) {
					result.put("code", "SUCCESS");
					result.put("message", data.getString("message"));
					return result;
				} else {
					System.out.println("失败，返回错误信息：" + data.getString("message"));
					result.put("code", "FALSE");
					result.put("message", data.getString("message"));
					return result;
				}
			} else {
				log.error("-------哲扬通道--商户新增信用卡--进程报错: 验证签名失败，请查看前后报文分析异常");
			}
		} catch (Exception e) {
			log.error("-------哲扬通道--商户新增信用卡--进程出现错误:", e);
		}

		log.info("-------哲扬通道--商户新增信用卡-start");
		return null;
	}

	/**
	 * 哲扬通道--开通支付接口 (哲扬声称有的通道需要签约才有这一步，我们不需要做）
	 */
	/*
	 * @Override public Map<String,String> fastpayOpenToken(Map<String, String>
	 * params){ log.info("-------哲扬通道--开通支付接口-start"); try { JSONObject req =
	 * new JSONObject(); //通用参数 商户自身的信息 req.put("encryptId",
	 * params.get("encryptId")); req.put("key", params.get("key")); //商户号
	 * req.put("mid", params.get("mid")); //调用的方法 req.put("method",
	 * "fastpayOpenToken"); //银行卡号 req.put("accountNumber",
	 * params.get("accountNumber")); //开卡成功前端回调地址 //req.put("frontUrl",
	 * params.get("frontUrl")); JSONObject data =ZheYangUtil.execute(req);
	 * 
	 * Map<String,String> result = new HashMap<String,String>(); if(data!=null){
	 * if(data.get("code").equals("000000")){ result.put("code", "SUCCESS");
	 * result.put("message", data.getString("message")); result.put("openHtml",
	 * data.getString("openHtml")); //开通用的 html 标签 result.put("isSign",
	 * data.getString("isSign")); //是否已开通
	 * 
	 * return result; }else{
	 * System.out.println("失败，返回错误信息："+data.getString("message"));
	 * result.put("code", "FALSE"); result.put("message",
	 * data.getString("message")); return result; } }else{
	 * log.error("-------哲扬通道--开通支付接口--进程报错: 验证签名错误"); } } catch (Exception e) {
	 * log.error("-------哲扬通道--开通支付接口--进程出现错误:",e); }
	 * 
	 * log.info("-------哲扬通道--开通支付接口-end"); return null; };
	 */

	/**
	 * 哲扬通道--线上支付/快捷支付 params中包含的参数 截止到2017-9-25 新版本进件和创建订单合并，该接口弃用
	 * 
	 */
	@Override
	public Map<String, String> createOnLineOrder(User user, ChannelPayRef cpr, Integer inputAccType, Double money, Integer transPayType, String desc, Map<String, String> params) throws Exception {
		try {
			log.info("-------哲扬通道--线上支付/快捷支付-start");

			// 查询卡的信息
			// UserCard card = userCardService.get(
			// Long.parseLong(params.get("card_id")));

			// 拼接请求参数
			JSONObject req = new JSONObject();

			// 从通道配置中获取相关参数
			JSONObject channelConfig = cpr.getConfig();
			// 调用方法
			req.put("method", "fastpayPrecreate");
			// 通用参数 商户自身的信息
			req.put("encryptId", params.get("encryptId"));
			req.put("key", params.get("key"));
			req.put("mid", params.get("mid"));

			// 商户流水（20位定长商户流水号，商户须保证流水唯一） 用户订单号yyMMddHHmmss(12)+8位随机数 20位
			String bizOrderNumber = commonService.getUniqueTradeSn();
			req.put("bizOrderNumber", bizOrderNumber);
			// 订单金额
			req.put("srcAmt", money);
			// 异步通知地址 不必须
			String notifyUrl = channelConfig.getString("notifyUrl");
			req.put("notifyUrl", notifyUrl);
			// 银行卡号
			req.put("accountNumber", params.get("accountNumber"));
			// 预留手机号 2019-9-19版本，可以随便填写
			req.put("tel", params.get("tel"));
			// 卡的有效期
			req.put("expired", params.get("expired"));
			// cvv2 信用卡背面的签名栏上，紧跟在卡号末四位号码的3位数字
			req.put("cvv2", params.get("cvv2"));
			// H5 通道类型必填，上送页面的支付成功回调地址 2019-9-19 版本新加
			if (StringUtil.isNotBlank(params.get("frontUrl"))) {
				req.put("frontUrl", params.get("frontUrl"));
			}
			// 交易手续费 0.5%传 0.5 Double类型
			if (StringUtil.isNotBlank(params.get("fastpayFee"))) {
				req.put("fastpayFee", params.get("fastpayFee"));
			}

			// data示例{"code":"000000","data":{xxx},"message":"成功"}
			JSONObject data = ZheYangUtil.execute(req);
			Map<String, String> result = new HashMap<String, String>();
			if (data != null) {
				if (data.get("code").equals("000000")) {
					log.info("-------哲扬通道--线上支付/快捷支付--返回信息成功");

					// 判断下单结果
					JSONObject judge = data.getJSONObject("data");
					if (judge.getString("canUse").equals("f")) {
						// 判断卡是否已审核通过
						result.put("return_message", "卡未通过审核");
					}
					if (judge.getString("isSign").equals("f")) {
						// 判断卡是否已经开通支付
						if (StringUtil.isNotBlank(result.get("return_message"))) {
							String message = result.get("return_message");
							result.put("return_message", message + ",卡未开通支付");
						} else {
							result.put("return_message", "卡未开通支付");
						}
					}
					// 第三方订单号，传递给app，等待确认支付时使用
					if (StringUtil.isNotBlank(judge.getString("tn"))) {
						result.put("tn", judge.getString("tn"));
					}
					// 商户订单号
					if (StringUtil.isNotBlank(judge.getString("bizOrderNumber"))) {
						result.put("bizOrderNumber", judge.getString("bizOrderNumber"));
					}
					// 跳转支付的html标签
					if (StringUtil.isNotBlank(judge.getString("openHtml"))) {
						result.put("openHtml", judge.getString("openHtml"));
					}
					// 在卡已开通和审核的情况下，创建支付订单 只创建了trans_order
					if (judge.getString("canUse").equals("t") && judge.getString("isSign").equals("t")) {
						userOrderService.createTransOrder(user.getId(), req.getString("bizOrderNumber"), null, null, cpr.getChannel().getType(), money, UserOrder.cd_type.D.name(), null, null, desc, transPayType, cpr.getChannel(),
								inputAccType, 0);

						log.info("-------哲扬通道--线上支付/快捷支付--保存订单成功");
					}
					result.put("return_code", "SUCCESS");
					result.put("message", data.getString("message"));
				} else {
					log.info("-------哲扬通道--线上支付/快捷支付--返回信息显示失败，原因为:" + data.getString("message"));
					result.put("return_code", "FALSE");
					result.put("return_message", result.get("message"));
				}
				return result;
			} else {
				log.error("-------哲扬通道--线上支付/快捷支付--验签失败");
			}
		} catch (Exception e) {
			log.error("-------哲扬通道--线上支付/快捷支付--进程出现错误:", e);
		}

		return null;
	}

	/**
	 * 哲扬通道--线上支付/快捷支付 截止到2017-9-25 新版本进件和创建订单合并
	 * 哲扬通道第二种方案，给代理商一个公用商户A,之后交易商户不再进件，全部挂载在A下进行下单，下单时直接传递商户信息。
	 * 
	 */
	@Override
	public Map<String, String> createYLZXOrder_v2(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		try {
			Map<String, String> resultMap = new HashMap<String, String>();
			log.info("-------哲扬通道--线上支付/快捷支付-start");

			// 查询卡的信息
			// UserCard card = userCardService.get(
			// Long.parseLong(params.get("card_id")));

			TuserCard cardJ = userCardDao.get("select t from TuserCard t left join t.bank left join t.user u  where u.id=" + user.getId() + " and t.isSettlmentCard = 1 ");
			if (cardJ == null) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG", "未设置结算卡");
				return resultMap;
			}

			// 拼接请求参数
			JSONObject req = new JSONObject();
			// 从通道配置中获取相关参数
			JSONObject channelConfig = cpr.getConfig();
			// 通用参数
			req.put("encryptId", channelConfig.get("ylaccount.encryptId"));
			req.put("key", channelConfig.get("ylaccount.key"));

			// 给代理商分配的，用于免进件直接进行交易的中间商户mid
			req.put("mid", channelConfig.get("ylaccount.mid"));

			// 调用方法
			req.put("method", "fastpayPrecreate2");
			req.put("srcAmt", money);

			// 商户流水（20位定长商户流水号，商户须保证流水唯一） 用户订单号yyMMddHHmmss(12)+8位随机数 20位
			String bizOrderNumber = commonService.getUniqueTradeSn();
			req.put("bizOrderNumber", bizOrderNumber);

			// 异步通知地址 不必须
			String notifyUrl = channelConfig.getString("ylaccount.notifyUrl");
			req.put("notifyUrl", notifyUrl);
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			if (card.getCardType().equals("J")) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG", "暂不支持该卡片");
				return resultMap;
			}
			// 银行卡号
			req.put("accountNumber", card.getCardNo());
			// 预留手机号 2019-9-19版本，可以随便填写
			req.put("tel", card.getPhone());
			// 卡的有效期
			// req.put("expired", card.getValidityDate());
			// cvv2 信用卡背面的签名栏上，紧跟在卡号末四位号码的3位数字
			// req.put("cvv2", card.getCvv());

			// 应哲扬通道提供方的要求，CVV和有效期可以固定
			req.put("expired", "0418");
			req.put("cvv2", "770");

			// H5 通道类型必填，上送页面的支付成功回调地址 2019-9-19 版本新加
			req.put("frontUrl", frontUrl);

			BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(user.getId(), UserOrder.trans_type.YLZX.getCode(), inputAccType);
			// 交易手续费 0.5% 传0.5
			req.put("fastpayFee", rate[0].multiply(new BigDecimal(100)));
			List<PayTypeLimitConfig> paytypes = payTypeLimitConfigService.findPayTypeLimitConfigsFTC(520, user.getAgentId());
			PayTypeLimitConfig c = paytypes.get(0);

			if (c.getMinAmt().compareTo(new BigDecimal(10)) == 0) {
				req.put("agencyType", "msh5");// msh5 交易最低金额10
			} else {
				req.put("agencyType", "h5");// h5交易金额最低500
			}
			// ysh5停用 at 2017-10-31 by liangchao
			/*
			 * else if(c.getMinAmt().compareTo(new BigDecimal(100))==0){
			 * req.put("agencyType", "ysh5");//ysh5 交易金额最低100元 }
			 */

			// 交易商户的姓名
			req.put("holderName", user.getRealName());
			// 交易商户的身份证
			req.put("idcard", user.getIdNo());
			// 交易商户的结算卡
			req.put("settAccountNumber", cardJ.getCardNo());
			// 结算卡预留手机号（部分通道可以不必填）
			req.put("settAccountTel", cardJ.getPhone());
			// 结算卡银行名 （部分通道可以不必填）
			req.put("bankName", cardJ.getBank().getBankName());
			// 额外手续费 （部分通道支持）
			if (inputAccType == 0 || inputAccType == 10) {
				req.put("extraFee", "2");
			} else {
				req.put("extraFee", "1");
			}

			// data示例{"code":"000000","data":{xxx},"message":"成功"}
			JSONObject data = ZheYangUtil.execute(req);
			if (data != null) {
				if (data.get("code").equals("000000")) {
					log.info("-------哲扬通道--线上支付/快捷支付--返回信息成功");

					// 判断下单结果
					JSONObject judge = data.getJSONObject("data");
					if (judge.getString("canUse").equals("f")) {
						// 判断卡是否已审核通过
						resultMap.put("flag", GlobalConstant.RESP_CODE_051);
						resultMap.put("flagMSG", "卡未通过审核");
						return resultMap;
					}
					if (judge.getString("isSign").equals("f")) {
						// 判断卡是否已经开通支付
						resultMap.put("flag", GlobalConstant.RESP_CODE_051);
						resultMap.put("flagMSG", "卡未开通支付");
						return resultMap;
					}
					// 第三方订单号，传递给app，等待确认支付时使用
					if (StringUtil.isNotBlank(judge.getString("tn"))) {
						resultMap.put("tn", judge.getString("tn"));
					}
					// 商户订单号
					if (StringUtil.isNotBlank(judge.getString("bizOrderNumber"))) {
						resultMap.put("bizOrderNumber", judge.getString("bizOrderNumber"));
					}
					// 在卡已开通和审核的情况下，创建支付订单 只创建了trans_order
					if (judge.getString("canUse").equals("t") && judge.getString("isSign").equals("t")) {
						userOrderService.createTransOrder(user.getId(), bizOrderNumber, null, null, UserOrder.trans_type.YLZX.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType, cpr.getChannel(), inputAccType,
								angentType);
						log.info("-------哲扬通道--线上支付/快捷支付--保存订单成功");
					}

					if (StringUtil.isNotBlank(judge.getString("openUrl"))) {
						resultMap.put("html", judge.getString("openUrl"));
					} else {
						String response = judge.getString("openHtml");
						response = DESutil.encrypt(response, DESutil.password);
						response = URLEncoder.encode(response);
						resultMap.put("html", "http://101.200.34.95:26370/flypayfx/mobile/ZheYangResponse?result=" + response);
					}

					resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
					// resultMap.put("message", data.getString("message"));
				} else {
					log.info("-------哲扬通道--线上支付/快捷支付--返回信息显示失败，原因为:" + data.getString("message"));
					resultMap.put("return_code", "FALSE");
					resultMap.put("return_message", resultMap.get("message"));
				}
				return resultMap;
			} else {
				log.error("-------哲扬通道--线上支付/快捷支付--验签失败");
			}
		} catch (Exception e) {
			log.error("-------哲扬通道--线上支付/快捷支付--进程出现错误:", e);
		}

		return null;
	}

	@Override
	public Map<String, String> createUnipayOnlineThroughOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		try {
			Map<String, String> resultMap = new HashMap<String, String>();
			log.info("-------哲扬通道--线上支付/快捷支付-start");

			// 查询卡的信息
			// UserCard card = userCardService.get(
			// Long.parseLong(params.get("card_id")));

			TuserCard cardJ = userCardDao.get("select t from TuserCard t left join t.bank left join t.user u  where u.id=" + user.getId() + " and t.isSettlmentCard = 1 ");
			if (cardJ == null) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG", "未设置结算卡");
				return resultMap;
			}

			// 拼接请求参数
			JSONObject req = new JSONObject();
			// 从通道配置中获取相关参数
			JSONObject channelConfig = cpr.getConfig();
			// 通用参数
			req.put("encryptId", channelConfig.get("ylaccount.encryptId"));
			req.put("key", channelConfig.get("ylaccount.key"));

			// 给代理商分配的，用于免进件直接进行交易的中间商户mid
			req.put("mid", channelConfig.get("ylaccount.mid"));

			// 调用方法
			req.put("method", "fastpayPrecreate2");
			req.put("srcAmt", money);

			// 商户流水（20位定长商户流水号，商户须保证流水唯一） 用户订单号yyMMddHHmmss(12)+8位随机数 20位
			String bizOrderNumber = commonService.getUniqueTradeSn();
			req.put("bizOrderNumber", bizOrderNumber);

			// 异步通知地址 不必须
			String notifyUrl = channelConfig.getString("ylaccount.notifyUrl");
			req.put("notifyUrl", notifyUrl);
			TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
			if (card.getCardType().equals("J")) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_051);
				resultMap.put("flagMSG", "暂不支持该卡片");
				return resultMap;
			}
			// 银行卡号
			req.put("accountNumber", card.getCardNo());
			// 预留手机号 2019-9-19版本，可以随便填写
			req.put("tel", card.getPhone());
			// 卡的有效期
			// req.put("expired", card.getValidityDate());
			// cvv2 信用卡背面的签名栏上，紧跟在卡号末四位号码的3位数字
			// req.put("cvv2", card.getCvv());

			// 应哲扬通道提供方的要求，CVV和有效期可以固定
			req.put("expired", "0418");
			req.put("cvv2", "770");

			// H5 通道类型必填，上送页面的支付成功回调地址 2019-9-19 版本新加
			req.put("frontUrl", frontUrl);

			// BigDecimal[] rate =
			// userSettlementConfigService.getUserInputRateAndShareRate(user.getId(),
			// UserOrder.trans_type.YLZX.getCode(), inputAccType);

			UserSettlementConfig config = userSettlementConfigService.getByUserId(user.getId());
//			BigDecimal d0ZTJifenFeeRate = config.getInputFeeD0ZtYinlianJf();
			BigDecimal d0ZTJifenFeeRate = config.getInputFeeD0ZtYinlianJfZY();
			// 交易手续费 0.5% 传0.5
			req.put("fastpayFee", d0ZTJifenFeeRate.multiply(new BigDecimal(100)));
			Integer chanType = cpr.getChannel().getType();
			PayTypeLimitConfig c = null;
			boolean isJiFen = false;
			if (chanType == 552) {
				isJiFen = true;
				List<PayTypeLimitConfig> paytypes = payTypeLimitConfigService.findPayTypeLimitConfigsFTC(552, user.getAgentId());
				c = paytypes.get(0);
			} else {
				List<PayTypeLimitConfig> paytypes = payTypeLimitConfigService.findPayTypeLimitConfigsFTC(520, user.getAgentId());
				c = paytypes.get(0);
			}
			
			if (c.getMinAmt().compareTo(new BigDecimal(10)) == 0) {
				req.put("agencyType", "msh5");// msh5 交易最低金额10
			} else {
				req.put("agencyType", isJiFen ? "shanglv2" : "h5");// h5交易金额最低500
			}
			// ysh5停用 at 2017-10-31 by liangchao
			// 积分有 jifen换为shanglv2(商旅) 20180327
			/*
			 * else if(c.getMinAmt().compareTo(new BigDecimal(100))==0){
			 * req.put("agencyType", "ysh5");//ysh5 交易金额最低100元 }
			 */

			// 交易商户的姓名
			req.put("holderName", user.getRealName());
			// 交易商户的身份证
			req.put("idcard", user.getIdNo());
			// 交易商户的结算卡
			req.put("settAccountNumber", cardJ.getCardNo());
			// 结算卡预留手机号（部分通道可以不必填）
			req.put("settAccountTel", cardJ.getPhone());
			// 结算卡银行名 （部分通道可以不必填）
			req.put("bankName", cardJ.getBank().getBankName());
			// 额外手续费 （部分通道支持）
			if (inputAccType == 0 || inputAccType == 10) {
				req.put("extraFee", "3");
			} else {
				req.put("extraFee", "1");
			}

			// data示例{"code":"000000","data":{xxx},"message":"成功"}
			JSONObject data = ZheYangUtil.execute(req);
			if (data != null) {
				if (data.get("code").equals("000000")) {
					log.info("-------哲扬通道--线上支付/快捷支付--返回信息成功");

					// 判断下单结果
					JSONObject judge = data.getJSONObject("data");
					if (judge.getString("canUse").equals("f")) {
						// 判断卡是否已审核通过
						resultMap.put("flag", GlobalConstant.RESP_CODE_051);
						resultMap.put("flagMSG", "卡未通过审核");
						return resultMap;
					}
					if (judge.getString("isSign").equals("f")) {
						// 判断卡是否已经开通支付
						resultMap.put("flag", GlobalConstant.RESP_CODE_051);
						resultMap.put("flagMSG", "卡未开通支付");
						return resultMap;
					}
					// 第三方订单号，传递给app，等待确认支付时使用
					if (StringUtil.isNotBlank(judge.getString("tn"))) {
						resultMap.put("tn", judge.getString("tn"));
					}
					// 商户订单号
					if (StringUtil.isNotBlank(judge.getString("bizOrderNumber"))) {
						resultMap.put("bizOrderNumber", judge.getString("bizOrderNumber"));
					}
					// 在卡已开通和审核的情况下，创建支付订单 只创建了trans_order
					if (judge.getString("canUse").equals("t") && judge.getString("isSign").equals("t")) {
						userOrderService.createTransOrder(user.getId(), bizOrderNumber, null, null, UserOrder.trans_type.YLZXJZY.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType, cpr.getChannel(), inputAccType,
								angentType);
						log.info("-------哲扬通道--线上支付/快捷支付--保存订单成功");
					}

					if (StringUtil.isNotBlank(judge.getString("openUrl"))) {
						resultMap.put("html", judge.getString("openUrl"));
					} else {
						String response = judge.getString("openHtml");
						response = DESutil.encrypt(response, DESutil.password);
						response = URLEncoder.encode(response);
						resultMap.put("html", "http://101.200.34.95:26370/flypayfx/mobile/ZheYangResponse?result=" + response);
					}

					resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
					// resultMap.put("message", data.getString("message"));
				} else {
					log.info("-------哲扬通道--线上支付/快捷支付--返回信息显示失败，原因为:" + data.getString("message"));
					resultMap.put("flag", "FALSE");
					resultMap.put("flagMSG", data.getString("message"));
				}
				return resultMap;
			} else {
				log.error("-------哲扬通道--线上支付/快捷支付--验签失败");
			}
		} catch (Exception e) {
			log.error("-------哲扬通道--线上支付/快捷支付--进程出现错误:", e);
		}

		return null;
	}

	/**
	 * 哲扬通道--确认支付
	 * 
	 * @param params
	 *            如果下单经过银联页面支付，那么可以省略这一步 params中包含的参数 card_id
	 *            sus_user_card表中商户的卡id user_id sys_user表中对应的商户id encryptId
	 *            哲扬商户的encryptId key 哲扬商户的key mid 哲扬商户号的mid bizOrderNumber 商户订单号
	 *            tn 第三方订单号 smsCode 短信验证码
	 * 
	 * @return
	 */
	@Override
	public Map<String, String> fastpayCheckMessage(Map<String, Object> params) {
		try {
			log.info("-------哲扬通道--确认支付-start");
			Map<String, String> result = new HashMap<String, String>();
			JSONObject req = new JSONObject();
			// 通用参数 商户自身的信息
			req.put("encryptId", params.get("encryptId"));
			req.put("key", params.get("key"));
			req.put("mid", params.get("mid"));
			// 调用的方法
			req.put("method", "fastpayCheckMessage");

			// 商户订单号
			req.put("bizOrderNumber", params.get("bizOrderNumber"));
			// 第三方订单号
			req.put("tn", params.get("tn"));
			// 短信验证码
			req.put("smsCode", params.get("smsCode"));
			// 银行卡号
			req.put("accountNumber", params.get("accountNumber"));
			// 预留手机号
			req.put("tel", params.get("tel"));
			// 有效期
			req.put("expired", params.get("expired"));
			// cvv2
			req.put("cvv2", params.get("cvv2"));
			// 金额
			req.put("srcAmt", params.get("srcAmt"));
			// 根据订单号查询订单状态是否正确
			String bizOrderNumber = String.valueOf(params.get("bizOrderNumber"));
			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(bizOrderNumber);
			if (userOrder == null) {
				log.info("-------哲扬通道--确认支付----订单不存在或者已经被处理了-----------------");
				result.put("code", "FALSE");
				result.put("message", "确认支付之前查询状态为处理中的订单，订单不存在或已经被处理掉");
				return result;
			}

			JSONObject data = ZheYangUtil.execute(req);
			if (data != null) {
				if (data.get("code").equals("000000")) {

					// 商户订单号
					bizOrderNumber = data.getJSONObject("data").getString("bizOrderNumber");
					// 订单状态
					String txnStatus = data.getJSONObject("data").getString("txnStatus");
					log.info("-------哲扬通道--确认支付-请求返回参数:  bizOrderNumber= " + bizOrderNumber + " txnStatus= " + txnStatus);

					// 保存订单状态
					PayOrder payOrder = new PayOrder();
					// 用于判断处理结果有无存储必要
					Boolean rechangeFlag = false;
					if (txnStatus.equals("p")) {
						// 支付中
						payOrder.setStatus(PayOrder.pay_status.PROCESSING.getCode()); // 等待支付

					} else if (txnStatus.equals("s")) {
						// 交易成功
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
						rechangeFlag = true;
					} else if (txnStatus.equals("c")) {
						// 交易关闭
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
						rechangeFlag = true;
					}
					if (rechangeFlag) {
						log.info("查询之后，开始保存订单信息");
						userOrderService.finishInputOrderStatus(req.getString("bizOrderNumber"), payOrder);
						log.info("保存订单信息成功");
					}
					result.put("code", "SUCCESS");
					result.put("message", "保存订单成功，通道返回信息显示" + data.getString("message"));
				} else {
					System.out.println("失败，返回错误信息：" + data.getString("message"));
					result.put("code", "FALSE");
					result.put("message", data.getString("message"));
					return result;
				}
			} else {
				log.error("-------哲扬通道--确认支付--进程报错: 验证签名失败，请查看前后报文分析异常");
			}
		} catch (Exception e) {
			log.info("-------哲扬通道--确认支付--进程出错：" + e);
		}
		return null;
	};

	/**
	 * 查询订单状态并保存
	 */
	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {
		log.info("-------哲扬通道--查询订单状态--start");
		try {
			// 根据订单查询通道配置的参数
			// UserOrder userOrder =
			// userOrderService.findTodoUserOrderByOrderNum(orderNum);
			// JSONObject channelConfig =
			// channelService.getChannelConfig(userOrder.getChannelId());
			// String mid = channelConfig.getString("mid");
			String mid = "000040036000025";
			JSONObject req = new JSONObject();
			// 商户号
			// req.put("mid", mid);
			log.info("下面的数据是写死在逻辑中的，注意去除掉~~~~");
			req.put("mid", "000600002000002");
			req.put("encryptId", "000600002000002");
			req.put("key", "56c4ca18ee04b3f111677d3d6d0be98c");

			req.put("method", "fastpayQuery");
			req.put("bizOrderNumber", orderNum);
			JSONObject data = ZheYangUtil.execute(req);
			Map<String, String> result = new HashMap<String, String>();
			if (data != null) {
				if (data.get("code").equals("000000")) {
					result.put("code", "SUCCESS");
					// 商户订单号
					String bizOrderNumber = data.getJSONObject("data").getString("bizOrderNumber");
					// 商户订单号
					String txnStatus = data.getJSONObject("data").getString("txnStatus");

					// 保存订单状态
					PayOrder payOrder = new PayOrder();
					// 用于判断处理结果有无存储必要
					Boolean rechangeFlag = false;
					if (txnStatus.equals("p")) {
						// 支付中
						log.info("查询结果为支付中，此状态默认为等待支付，所以当前状态不做更改");
						payOrder.setStatus(PayOrder.pay_status.PROCESSING.getCode()); // 等待支付

					} else if (txnStatus.equals("s")) {
						// 交易成功
						payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
						rechangeFlag = true;
					} else if (txnStatus.equals("c")) {
						// 交易关闭
						payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
						rechangeFlag = true;
					}
					if (rechangeFlag) {
						log.info("查询之后，开始保存订单信息");
						userOrderService.finishInputOrderStatus(bizOrderNumber, payOrder);
						log.info("保存订单信息成功");
					}
					return result;
				} else {
					log.info("失败，返回错误信息：" + data.getString("message"));
					result.put("code", "FALSE");
					result.put("message", data.getString("message"));
					return result;
				}
			} else {
				log.error("-------哲扬通道--查询订单状态--进程报错: 验证签名失败，请查看前后报文分析异常");
			}
		} catch (Exception e) {
			log.error("-------哲扬通道--查询订单状态--进程报错: ", e);
		}
		log.info("-------哲扬通道--查询订单状态--end");
		return null;
	};

	/**
	 * 修改结算卡接口
	 * 
	 * @param params
	 */
	@Override
	public Map<String, String> updateBankAccountInfo(Map<String, String> params) {
		log.info("-------哲扬通道--修改结算卡--start");

		try {
			JSONObject req = new JSONObject();
			req.put("method", "updateBankAccountInfo");
			req.put("encryptId", params.get("encryptId"));
			req.put("key", params.get("key"));
			req.put("mid", params.get("mid"));
			req.put("accountNumber", params.get("accountNumber"));
			req.put("tel", params.get("tel"));

			JSONObject data = ZheYangUtil.execute(req);

			Map<String, String> result = new HashMap<String, String>();

			if (data != null) {
				if (data.getString("code").equals("000000")) {
					result.put("code", "SUCCESS");
				} else {
					result.put("code", "FALSE");
					result.put("message", data.getString("message"));
				}
				return result;
			} else {
				log.error("-------哲扬通道--修改结算卡--进程报错: 验证签名失败，请查看前后报文分析异常");
			}
		} catch (Exception e) {
			log.error("-------哲扬通道--修改结算卡--进程报错:", e);
		}

		log.info("-------哲扬通道--修改结算卡--end");
		return null;
	};

	/**
	 * 哲扬通道--修改商户费率接口(第二天生效)，哲扬无参数结果返回
	 * 
	 * @param params
	 * @return
	 */
	public Map<String, String> updateFastPayFee(Map<String, Object> params) {
		log.info("-------哲扬通道--修改商户费率接口--start");
		try {
			JSONObject req = new JSONObject();
			// 公共参数
			req.put("encryptId", params.get("encryptId"));
			req.put("key", params.get("key"));

			req.put("method", "updateFastPayFee");
			req.put("mid", params.get("mid"));
			req.put("fastpayFee", params.get("fastpayFee")); // 费率，double

			JSONObject data = ZheYangUtil.execute(req);
			Map<String, String> result = new HashMap<String, String>();
			if (data != null) {
				if (data.getString("code").equals("000000")) {
					result.put("code", "SUCCESS");
				} else {
					result.put("code", "FALSE");
					result.put("message", data.getString("message"));
				}
				return result;
			} else {
				log.error("-------哲扬通道--修改商户费率接口--进程报错: 验证签名失败，请查看前后报文分析异常");
			}
		} catch (Exception e) {
			log.error("-------哲扬通道--修改商户费率接口--进程报错:", e);
		}

		log.info("-------哲扬通道--修改商户费率接口--end");
		return null;
	};

}
