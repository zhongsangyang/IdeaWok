package com.cn.flypay.service.payment.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.constant.GlobalConstant;
import com.cn.flypay.dao.BaseDao;
import com.cn.flypay.model.sys.TcardBankConfig;
import com.cn.flypay.model.sys.Tchannel;
import com.cn.flypay.model.sys.TuserCard;
import com.cn.flypay.model.util.StringUtil;
import com.cn.flypay.pageModel.payment.ChannelPayRef;
import com.cn.flypay.pageModel.sys.Bank;
import com.cn.flypay.pageModel.sys.User;
import com.cn.flypay.pageModel.trans.PayOrder;
import com.cn.flypay.pageModel.trans.UserOrder;
import com.cn.flypay.service.payment.GaZhiYinLainService;
import com.cn.flypay.service.payment.TroughTrainServeice;
import com.cn.flypay.service.sys.BankService;
import com.cn.flypay.service.sys.CardBankConfigService;
import com.cn.flypay.service.sys.UserCardService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.service.sys.UserSettlementConfigService;
import com.cn.flypay.service.trans.UserOrderService;
import com.cn.flypay.utils.AESCodeUtil;
import com.cn.flypay.utils.DESutil;
import com.cn.flypay.utils.ImportUtil;
import com.cn.flypay.utils.gazhiyinlian.GaZhiYinLianUtil;
import com.cn.flypay.utils.gazhiyinlian.entities.BindCardForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.QueryCardInfoForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.QueryOrderInfoForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.RateAndCardJChangeForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.RegisterForGaZhiYinLian;
import com.cn.flypay.utils.gazhiyinlian.entities.SendSMSForGaZhiYinLian;

/**
 * 嘎吱（银联）通道接口
 * 
 * @author liangchao
 *
 */
@Service(value = "gaZhiYinLianPaymentService")
public class GaZhiYinLianPaymentServiceImpl extends AbstractChannelPaymentService {
	private Log log = LogFactory.getLog(getClass());
	@Autowired
	private BaseDao<TuserCard> userCardDao;
	@Autowired
	private UserService userService;
	@Autowired
	private GaZhiYinLainService gaZhiYinLainService;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private TroughTrainServeice troughTrainServeice;
	@Autowired
	private UserOrderService userOrderService;
	@Autowired
	private BaseDao<Tchannel> channelDao;
	@Autowired
	private UserSettlementConfigService userSettlementConfigService;
	@Autowired
	private CardBankConfigService bankConfigService;
	@Autowired
	private BankService bankService;

	/**
	 * 商户报备流程
	 */
	@Override
	public Map<String, String> createSubMerchantByUserId(Long userId) {
		Map<String, String> resMap = new HashMap<String, String>();
		User user = userService.get(userId);
		TuserCard cardJ = userCardDao.get("select t from TuserCard t left join t.bank left join t.user u  where u.id=" + user.getId() + " and t.isSettlmentCard = 1 ");
		Bank bank = null;
		String bankName = "";
		TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(cardJ.getCardNo()));
		if (cbc == null) {
			resMap.put("return_code", GlobalConstant.RESP_CODE_053);
			resMap.put("return_msg", GlobalConstant.map.get(GlobalConstant.RESP_CODE_053));
			return resMap;
		} else {
			bank = bankService.getBankByBankCode(cbc.getBankCode());
			if (bank == null) {
				resMap.put("return_code", GlobalConstant.RESP_CODE_054);
				resMap.put("return_msg", GlobalConstant.map.get(GlobalConstant.RESP_CODE_054));
				return resMap;
			} else {
				bankName = bank.getBankName();
			}
		}

		BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(user.getId(), UserOrder.trans_type.YLZXJ.getCode(), 0);

		// 调用嘎吱商户注册接口
		RegisterForGaZhiYinLian req = new RegisterForGaZhiYinLian();
		req.setTranType("MERREG"); // 注册
		req.setMerTrace("GZYL" + UUID.randomUUID().toString().substring(0, 8)); // 商户流水
		req.setMerName(user.getRealName()); // 商户名称
		req.setMerAbbr(user.getRealName()); // 商户简称
		req.setRateCode(GaZhiYinLianUtil.rateCode); // 合作商户费率编号
		req.setIdCardNo(user.getIdNo()); // 身份证号
		req.setBankAccNo(cardJ.getCardNo()); // 银行卡卡号
		req.setPhoneno(cardJ.getPhone()); // 银行卡预留手机
		req.setBankAccName(user.getRealName()); // 银行卡户名
		req.setBankAccType("2"); // 银行卡账户类型
		req.setBankName(bankName); // 银行名称
		req.setBankSubName(bankName); // 银行支行名称

		String bankIco = bank.getBankIco();
		int location = bankIco.indexOf("_");
		String bankAddr = bankIco.substring(location + 1, bankIco.length()).toUpperCase(); // 银行代码大写
		// 判断卡是否在嘎吱支持的卡列表当中
		if (StringUtil.isBlank(GaZhiYinLianUtil.map.get(bankAddr))) {
			resMap.put("return_code", GlobalConstant.RESP_CODE_054);
			resMap.put("return_msg", "通道暂不支持该卡");
			return resMap;
		}
		req.setBankCode(GaZhiYinLianUtil.map.get(bankAddr));// 银行代码
		req.setBankAbbr(bankAddr); // 银行代号

		if (GaZhiYinLianUtil.map.get(bankAddr).equals("weizhi")) {
			// 对于未知联行号的银行，按照卢总的意思，同一设置为 102100099996 中国工商银行总行清算中心
			req.setBankChannelNo("102100099996");// 银行联行号
		} else {
			req.setBankChannelNo(GaZhiYinLianUtil.map.get(bankAddr));// 银行联行号
		}

		req.setBankProvince("上海"); // 省
		req.setBankCity("上海市"); // 市
		req.setDebitRate(String.valueOf(rate[0])); // 借记卡费率
		req.setDebitCapAmount("99999900"); // 借记卡封顶
											// (商户借记卡封顶金额填写错误，封顶金额不能低于服务商封顶金额)
		req.setCreditRate(String.valueOf(rate[0])); // 信用卡费率
		req.setCreditCapAmount("99999900"); // 信用卡封顶
		req.setWithdRate("0.00"); // 提现费率
		req.setWithdSgFee("200"); // 单笔提现手续费值 (费用不能低于服务商单笔提现费用)
		JSONObject createMer = gaZhiYinLainService.createMerchant(req);
		if (!createMer.getString("code").equals("000")) {
			resMap.put("return_code", GlobalConstant.RESP_CODE_999);
			resMap.put("return_msg", "创建通道子商户失败");
			return resMap;
		}
		JSONObject merInfo = JSONObject.parseObject(createMer.getString("message"));
		String merchantUuid = merInfo.getString("merchantUuid");
		String merNo = merInfo.getString("merNo");

		JSONObject configJson = new JSONObject();
		configJson.put("gazhiyinlian.rateCode", GaZhiYinLianUtil.rateCode);
		configJson.put("gazhiyinlian.merNo", merNo);
		configJson.put("gazhiyinlian.merchantUuid", merchantUuid);
		configJson.put("gazhiyinlian.bindCardJNo", cardJ.getCardNo());
		configJson.put("gazhiyinlian.cardRate", String.valueOf(rate[0]));
		// configJson.put("gazhiyinlian.orderNotifyUrl",
		// "http://101.200.34.95:26370/flypayfx/payment/yibao_ylzx_Notify");
		// //订单异步返回链接
		// configJson.put("gazhiyinlian.openCardUrl",
		// "http://101.200.34.95:26370/flypayfx/payment/yibao_ylzx_Notify");
		// //银联侧绑卡开通异步返回链接

		// 保存通道信息
		if (!gaZhiYinLainService.addCreateGaZhiYinLianZhiTongCheChannel(user.getRealName(), 550, configJson.toJSONString(), user.getId())) {
			log.info("----嘎吱（银联）--开通保存商户信息失败,user_id=" + user.getId() + ", merNo=" + merNo);
			resMap.put("return_code", GlobalConstant.RESP_CODE_999);
			resMap.put("return_msg", "保存通道信息失败");
			return resMap;
		}
		resMap.put("return_code", GlobalConstant.RESP_CODE_SUCCESS);
		return resMap;
	}

	/**
	 * 创建订单 TODO
	 */
	@Override
	public Map<String, String> createYLZXOrder_v2(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		Map<String, String> resultMap = new HashMap<String, String>();
		// 付款卡信息
		TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
		// 查询结算卡
		TuserCard cardJ = userCardDao.get("select t from TuserCard t left join t.bank left join t.user u  where u.id=" + user.getId() + " and t.isSettlmentCard = 1 ");
		if (cardJ == null) {
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			resultMap.put("flagMSG", "未设置结算卡");
			return resultMap;
		}
		JSONObject config = cpr.getConfig();
		// cpr中的通道信息有缓存，所以这里重复查询一次
		Tchannel channel = channelDao.get("select t from Tchannel t  where t.status=10 and t.name='GAZHIYINLIANJIFENZHITONGCHE' and t.userId=" + user.getId() + "");
		config = JSONObject.parseObject(channel.getConfig());

		boolean payStatus = false; // 可支付状态

		// 1 检查通道绑定的费率信息是否一致 --开始
		BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(user.getId(), UserOrder.trans_type.YLZXJ.getCode(), inputAccType);
		if (config.getBigDecimal("gazhiyinlian.cardRate").compareTo(rate[0]) != 0) {
			// if(true){
			// 开始更改费率
			RateAndCardJChangeForGaZhiYinLian req = new RateAndCardJChangeForGaZhiYinLian();
			req.setTranType("MERCHG");
			req.setMerNo(config.getString("gazhiyinlian.merNo"));
			req.setMerTrace("GZYL" + UUID.randomUUID().toString().substring(0, 8));
			req.setChangeType("1"); // 1 交易费率变更 2 银行卡信息变更 3 交易费率新增 4 提现费率变更
			req.setRateCode(GaZhiYinLianUtil.rateCode);
			req.setDebitRate(String.valueOf(rate[0])); // 借记卡费率
			req.setDebitCapAmount("99999900"); // 借记卡封顶
			req.setCreditRate(String.valueOf(rate[0]));// 信用卡费率
			req.setCreditCapAmount("99999900"); // 信用卡封顶
			req.setWithdRate("0.00"); // 提现费率
			req.setWithdSgFee("200"); // 单笔提现手续费
			JSONObject res = gaZhiYinLainService.changeRateAndCardJInfo(req);
			if (!res.getString("code").equals("000")) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "请求通道更新费率信息异常");
				return resultMap;
			}
			// 如果更改成功,更新进入通道信息
			Tchannel c = channelDao.get("select t from Tchannel t  where t.status=10 and t.name='GAZHIYINLIANJIFENZHITONGCHE' and t.userId=" + user.getId() + "");
			JSONObject merConfig = JSONObject.parseObject(c.getConfig());
			String oldrate = merConfig.getString("gazhiyinlian.cardRate");
			merConfig.remove("gazhiyinlian.cardRate");
			merConfig.put("gazhiyinlian.cardRate", String.valueOf(rate[0]));
			c.setConfig(merConfig.toJSONString());
			channelDao.save(c);
			log.info("----嘎吱校验逻辑--更新费率信息成功,从" + oldrate + "更改为" + rate[0]);
		}

		// 检查通道绑定的费率信息是否一致 --结束

		// 2 检查通道绑定的结算卡信息是否一致 --开始
		String gazhiCardJNo = config.getString("gazhiyinlian.bindCardJNo");
		if (!gazhiCardJNo.equals(cardJ.getCardNo())) {
			// 如果绑定的结算卡不一致
			RateAndCardJChangeForGaZhiYinLian req = new RateAndCardJChangeForGaZhiYinLian();
			req.setTranType("MERCHG");
			req.setMerNo(config.getString("gazhiyinlian.merNo"));
			req.setMerTrace("GZYL" + UUID.randomUUID().toString().substring(0, 8));
			req.setChangeType("2"); // 1 交易费率变更 2 银行卡信息变更 3 交易费率新增 4 提现费率变更
			req.setBankAccNo(cardJ.getCardNo()); // 银行卡卡号
			req.setPhoneno(cardJ.getPhone()); // 银行卡预留手机

			Bank bank = null;
			String bankName = "";
			TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(cardJ.getCardNo()));
			if (cbc == null) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_053);
				resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_053));
				return resultMap;
			} else {
				bank = bankService.getBankByBankCode(cbc.getBankCode());
				if (bank == null) {
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_054));
					return resultMap;
				} else {
					bankName = bank.getBankName(); // 银行名称
				}
			}

			req.setBankName(bankName); // 银行名称
			req.setBankSubName(bankName); // 银行支行名称

			String bankIco = bank.getBankIco();
			int location = bankIco.indexOf("_");
			String bankAddr = bankIco.substring(location + 1, bankIco.length()).toUpperCase(); // 银行代码大写
			// 判断卡是否在嘎吱支持的卡列表当中
			if (StringUtil.isBlank(GaZhiYinLianUtil.map.get(bankAddr))) {
				if (StringUtil.isBlank(GaZhiYinLianUtil.map2.get(bankName))) {
					// 因为通过银行编码进行匹配可能匹配不上，所以再次根据卡的名称是否有对应
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", "通道暂不支持该支付卡");
					return resultMap;
				} else {
					req.setBankCode(GaZhiYinLianUtil.map.get(GaZhiYinLianUtil.map2.get(bankName)));// 银行代码
																									// 数字
					req.setBankAbbr(GaZhiYinLianUtil.map2.get(bankName)); // 银行代号
				}
			}
			req.setBankCode(GaZhiYinLianUtil.map.get(bankAddr));// 银行代码
			req.setBankAbbr(bankAddr); // 银行代号

			if (GaZhiYinLianUtil.map.get(bankAddr).equals("weizhi")) {
				// 对于未知联行号的银行，按照卢总的意思，同一设置为 102100099996 中国工商银行总行清算中心
				req.setBankChannelNo("102100099996");// 银行联行号
			} else {
				req.setBankChannelNo(GaZhiYinLianUtil.map.get(bankAddr));// 银行联行号
			}
			req.setBankProvince("上海"); // 省
			req.setBankCity("上海市"); // 市
			JSONObject res = gaZhiYinLainService.changeRateAndCardJInfo(req);
			if (!res.getString("code").equals("000")) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "请求通道更新结算卡信息异常");
				return resultMap;
			}
			// 如果更改成功,更新进入通道信息
			Tchannel c = channelDao.get("select t from Tchannel t  where t.status=10 and t.name='GAZHIYINLIANJIFENZHITONGCHE' and t.userId=" + user.getId() + "");
			JSONObject merConfig = JSONObject.parseObject(c.getConfig());
			merConfig.remove("gazhiyinlian.bindCardJNo");
			merConfig.put("gazhiyinlian.bindCardJNo", cardJ.getCardNo());
			c.setConfig(merConfig.toJSONString());
			channelDao.save(c);

			log.info("----嘎吱校验逻辑--更新结算卡信息成功,从" + gazhiCardJNo + "更改为" + cardJ.getCardNo());
		}
		// 检查通道绑定的结算卡信息是否一致 --结束

		// 3 判断是否已经把开通卡的功能绑过 新加卡表的快捷开通字段
		if (StringUtil.isBlank(card.getIsOpenGaZhiYinLianQuickPay()) || card.getIsOpenGaZhiYinLianQuickPay().equals("0")) {
			// 调用银联侧绑卡开通接口
			BindCardForGaZhiYinLian req = new BindCardForGaZhiYinLian();
			req.setTranType("POPNCD");
			req.setMerTrace("GZYL" + UUID.randomUUID().toString().substring(0, 8));
			req.setMerNo(config.getString("gazhiyinlian.merNo"));
			req.setOrderId("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			req.setRateCode(GaZhiYinLianUtil.rateCode);
			req.setCardNo(card.getCardNo()); // 银行卡卡号
			req.setAccountName(user.getRealName()); // 银行卡姓名

			// 判断选择的支付卡的卡类型
			if (card.getCardType().equals("J")) {
				req.setCardType("1"); // 1借记卡 2信用卡
			} else {
				String cv2 = card.getCvv();
				String expired = card.getValidityDate();
				if (com.cn.flypay.utils.StringUtil.isEmpty(cv2) || com.cn.flypay.utils.StringUtil.isEmpty(expired) || expired.length() != 4) {
					resultMap.put("flag", GlobalConstant.RESP_CODE_999);
					resultMap.put("flagMSG", "交易卡信息有误");
					return resultMap;
				}
				String yearStr = expired.substring(2, 4);
				String monthStr = expired.substring(0, 2);
				req.setCardType("2"); // 1借记卡 2信用卡
				req.setCvn2(cv2); // 卡背面Cvn2数字
				req.setExpired(yearStr + monthStr); // 卡有效期
			}

			Bank bank = null;
			String bankName = "";
			TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(card.getCardNo()));
			if (cbc == null) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_053);
				resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_053));
				return resultMap;
			} else {
				bank = bankService.getBankByBankCode(cbc.getBankCode());
				if (bank == null) {
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_054));
					return resultMap;
				} else {
					bankName = bank.getBankName();
				}
			}

			String bankIco = bank.getBankIco();
			int location = bankIco.indexOf("_");
			String bankAddr = bankIco.substring(location + 1, bankIco.length()).toUpperCase(); // 银行代码大写
			// 判断卡是否在嘎吱支持的卡列表当中
			if (StringUtil.isBlank(GaZhiYinLianUtil.map.get(bankAddr))) {
				if (StringUtil.isBlank(GaZhiYinLianUtil.map2.get(bankName))) {
					// 因为通过银行编码进行匹配可能匹配不上，所以再次根据卡的名称是否有对应
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", "通道暂不支持该支付卡");
					return resultMap;
				} else {
					req.setBankCode(GaZhiYinLianUtil.map.get(GaZhiYinLianUtil.map2.get(bankName)));// 银行代码-数字
					req.setBankAbbr(GaZhiYinLianUtil.map2.get(bankName)); // 银行代号
				}

			} else {
				req.setBankCode(GaZhiYinLianUtil.map.get(bankAddr));// 银行代码
				req.setBankAbbr(bankAddr); // 银行代号
			}
			req.setPhoneno(card.getPhone()); // 银行预留手机号
			req.setCertType("01");
			req.setCertNo(user.getIdNo()); // 银行预留证件号
			req.setPageReturnUrl(frontUrl);
			req.setOfflineNotifyUrl(GaZhiYinLianUtil.bindCardNotifyUrl);

			JSONObject res = gaZhiYinLainService.bindCard(req);
			if (!res.getString("code").equals("000")) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "开通银行卡交易权限失败");
				return resultMap;
			}

			JSONObject resJson = JSONObject.parseObject(res.getString("message"));

			String activateStatus = resJson.getString("activateStatus");
			// 判断卡的状态 1 等待签约中 2 开通成功 3 开通失败 4 绑卡状态失效
			if (activateStatus.equals("1")) {
				// 等待签约中
				// 跳转至开通界面
				String openHtml = resJson.getString("html");
				String encryptHtml = "";
				try {
					encryptHtml = AESCodeUtil.encrypt(openHtml);
				} catch (Exception e) {
					e.printStackTrace();
				}
				resultMap.put("html", GaZhiYinLianUtil.bindCardResponseUrl + encryptHtml); // 测试用
				resultMap.put("type", "2");
				resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
				return resultMap;

			} else if (activateStatus.equals("2")) {
				// 开通成功
				// 更新卡的状态信息
				userCardService.updateIsOpenGaZhiYinLianQuickPay(card.getId(), "1");
				payStatus = true;
			} else if (activateStatus.equals("3")) {
				// 开通失败
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "该支付卡通道开通失败");
				return resultMap;

			} else if (activateStatus.equals("4")) {
				// 绑卡状态失效
				userCardService.updateIsOpenGaZhiYinLianQuickPay(card.getId(), "0");
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "支付异常，请重试");
				return resultMap;
			}
		} else if (card.getIsOpenGaZhiYinLianQuickPay().equals("1")) {
			payStatus = true;
		}

		// 4 检查是否可以进行支付
		if (payStatus || card.getIsOpenGaZhiYinLianQuickPay().equals("1")) {
			// 若已经开通过卡,走支付短信接口
			SendSMSForGaZhiYinLian req = new SendSMSForGaZhiYinLian();
			req.setTranType("PAYMSG");
			req.setMerNo(config.getString("gazhiyinlian.merNo"));
			req.setMerTrace("GZYL" + UUID.randomUUID().toString().substring(0, 8));
			req.setOrderId("GZYLZX" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			BigDecimal orderAmount = new BigDecimal(money * 100).setScale(0);
			req.setOrderAmount(orderAmount.toString()); // 订单金额以分为单位
			req.setRateCode(GaZhiYinLianUtil.rateCode);
			req.setCardNo(card.getCardNo()); // 银行卡号
			req.setAccountName(user.getRealName()); // 银行卡姓名

			// 判断选择的支付卡的卡类型
			if (card.getCardType().equals("J")) {
				req.setCardType("1"); // 1借记卡 2信用卡
			} else {
				req.setCardType("2"); // 1借记卡 2信用卡
			}

			Bank bank = null;
			String bankName = "";
			TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(card.getCardNo()));
			if (cbc == null) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_053);
				resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_053));
				return resultMap;
			} else {
				bank = bankService.getBankByBankCode(cbc.getBankCode());
				if (bank == null) {
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_054));
					return resultMap;
				} else {
					bankName = bank.getBankName();
				}
			}
			String bankIco = bank.getBankIco();
			int location = bankIco.indexOf("_");
			String bankAddr = bankIco.substring(location + 1, bankIco.length()).toUpperCase(); // 银行代码大写
			// 判断卡是否在嘎吱支持的卡列表当中
			if (StringUtil.isBlank(GaZhiYinLianUtil.map.get(bankAddr))) {
				if (StringUtil.isBlank(GaZhiYinLianUtil.map2.get(bankName))) {
					// 因为通过银行编码进行匹配可能匹配不上，所以再次根据卡的名称是否有对应
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", "通道暂不支持该支付卡");
					return resultMap;
				} else {
					req.setBankCode(GaZhiYinLianUtil.map.get(GaZhiYinLianUtil.map2.get(bankName)));// 银行代码
																									// 数字
					req.setBankAbbr(GaZhiYinLianUtil.map2.get(bankName)); // 银行代号
				}
			} else {
				req.setBankCode(GaZhiYinLianUtil.map.get(bankAddr));// 银行代码
				req.setBankAbbr(bankAddr); // 银行代号
			}

			req.setPhoneno(card.getPhone());
			req.setCertType("01"); // 证件类型
			req.setCertNo(user.getIdNo()); // 银行预留证件号
			JSONObject res = gaZhiYinLainService.sendSMS(req);
			if (!res.getString("code").equals("000")) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "发送支付短信失败");
				return resultMap;
			}
			JSONObject resJson = JSONObject.parseObject(res.getString("message"));
			// 支付流水号
			String payNo = resJson.getString("payNo");
			String resultCode = resJson.getString("resultCode");
			String resultMsg = resJson.getString("resultMsg");
			if (!"000000".equals(resultCode) || !"SUCCESS".equals(resultMsg)) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "发送支付短信失败");
				return resultMap;
			}

			String orderId = req.getOrderId(); // 订单号
			desc = "嘎吱银联积分通道--" + user.getRealName() + "_" + user.getLoginName() + "正在用卡" + card.getCardNo() + "向" + cardJ.getCardNo() + "支付" + money + "元";
			try {
				// 由于验证码下单接口 createOnlineVerify
				// 中缺少一些参数，且该接口的参数需要前端返回，所以，尽管下单是在createOnlineVerify接口，但综合考虑，创建订单还是放在发送验证码接口中
				userOrderService.createTransOrder(user.getId(), orderId, null, null, UserOrder.trans_type.YLZXJ.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType, cpr.getChannel(), inputAccType, angentType);
				log.info("---银联积分---嘎吱通道----调用发送验证码接口---保存订单：" + orderId + "  ----成功");
			} catch (Exception e) {
				log.error("---银联积分---嘎吱通道----调用发送验证码接口---保存订单：" + orderId + "  ----失败：" + e);
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "保存订单失败");
				return resultMap;
			}
			// 为1时表示要去调用验证码页面
			resultMap.put("type", "1");
			resultMap.put("transactionId", "GZ" + payNo); // 方便createOnlineVerify接口判断，加上字符
			resultMap.put("payAmount", req.getOrderAmount()); // 支付金额
			resultMap.put("payCardId", String.valueOf(card.getId())); // 支付卡的ID
			resultMap.put("orderId", orderId); // 订单
			resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
			return resultMap;

		}
		return resultMap;
	}
	
	@Override
	public Map<String, String> createUnipayOnlineThroughOrder(User user, ChannelPayRef cpr, Integer inputAccType, Long cardId, String frontUrl, Double money, Integer transPayType, Integer angentType, String desc) {
		Map<String, String> resultMap = new HashMap<String, String>();
		// 付款卡信息
		TuserCard card = userCardDao.get("select t from TuserCard t left join t.bank where t.id=" + cardId);
		// 查询结算卡
		TuserCard cardJ = userCardDao.get("select t from TuserCard t left join t.bank left join t.user u  where u.id=" + user.getId() + " and t.isSettlmentCard = 1 ");
		if (cardJ == null) {
			resultMap.put("flag", GlobalConstant.RESP_CODE_051);
			resultMap.put("flagMSG", "未设置结算卡");
			return resultMap;
		}
		JSONObject config = cpr.getConfig();
		// cpr中的通道信息有缓存，所以这里重复查询一次
		Tchannel channel = channelDao.get("select t from Tchannel t  where t.status=10 and t.name='GAZHIYINLIANJIFENZHITONGCHE' and t.userId=" + user.getId() + "");
		config = JSONObject.parseObject(channel.getConfig());

		boolean payStatus = false; // 可支付状态

		// 1 检查通道绑定的费率信息是否一致 --开始
		BigDecimal[] rate = userSettlementConfigService.getUserInputRateAndShareRate(user.getId(), UserOrder.trans_type.YLZXJ.getCode(), inputAccType);
		if (config.getBigDecimal("gazhiyinlian.cardRate").compareTo(rate[0]) != 0) {
			// if(true){
			// 开始更改费率
			RateAndCardJChangeForGaZhiYinLian req = new RateAndCardJChangeForGaZhiYinLian();
			req.setTranType("MERCHG");
			req.setMerNo(config.getString("gazhiyinlian.merNo"));
			req.setMerTrace("GZYL" + UUID.randomUUID().toString().substring(0, 8));
			req.setChangeType("1"); // 1 交易费率变更 2 银行卡信息变更 3 交易费率新增 4 提现费率变更
			req.setRateCode(GaZhiYinLianUtil.rateCode);
			req.setDebitRate(String.valueOf(rate[0])); // 借记卡费率
			req.setDebitCapAmount("99999900"); // 借记卡封顶
			req.setCreditRate(String.valueOf(rate[0]));// 信用卡费率
			req.setCreditCapAmount("99999900"); // 信用卡封顶
			req.setWithdRate("0.00"); // 提现费率
			req.setWithdSgFee("200"); // 单笔提现手续费
			JSONObject res = gaZhiYinLainService.changeRateAndCardJInfo(req);
			if (!res.getString("code").equals("000")) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "请求通道更新费率信息异常");
				return resultMap;
			}
			// 如果更改成功,更新进入通道信息
			Tchannel c = channelDao.get("select t from Tchannel t  where t.status=10 and t.name='GAZHIYINLIANJIFENZHITONGCHE' and t.userId=" + user.getId() + "");
			JSONObject merConfig = JSONObject.parseObject(c.getConfig());
			String oldrate = merConfig.getString("gazhiyinlian.cardRate");
			merConfig.remove("gazhiyinlian.cardRate");
			merConfig.put("gazhiyinlian.cardRate", String.valueOf(rate[0]));
			c.setConfig(merConfig.toJSONString());
			channelDao.save(c);
			log.info("----嘎吱校验逻辑--更新费率信息成功,从" + oldrate + "更改为" + rate[0]);
		}

		// 检查通道绑定的费率信息是否一致 --结束

		// 2 检查通道绑定的结算卡信息是否一致 --开始
		String gazhiCardJNo = config.getString("gazhiyinlian.bindCardJNo");
		if (!gazhiCardJNo.equals(cardJ.getCardNo())) {
			// 如果绑定的结算卡不一致
			RateAndCardJChangeForGaZhiYinLian req = new RateAndCardJChangeForGaZhiYinLian();
			req.setTranType("MERCHG");
			req.setMerNo(config.getString("gazhiyinlian.merNo"));
			req.setMerTrace("GZYL" + UUID.randomUUID().toString().substring(0, 8));
			req.setChangeType("2"); // 1 交易费率变更 2 银行卡信息变更 3 交易费率新增 4 提现费率变更
			req.setBankAccNo(cardJ.getCardNo()); // 银行卡卡号
			req.setPhoneno(cardJ.getPhone()); // 银行卡预留手机

			Bank bank = null;
			String bankName = "";
			TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(cardJ.getCardNo()));
			if (cbc == null) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_053);
				resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_053));
				return resultMap;
			} else {
				bank = bankService.getBankByBankCode(cbc.getBankCode());
				if (bank == null) {
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_054));
					return resultMap;
				} else {
					bankName = bank.getBankName(); // 银行名称
				}
			}

			req.setBankName(bankName); // 银行名称
			req.setBankSubName(bankName); // 银行支行名称

			String bankIco = bank.getBankIco();
			int location = bankIco.indexOf("_");
			String bankAddr = bankIco.substring(location + 1, bankIco.length()).toUpperCase(); // 银行代码大写
			// 判断卡是否在嘎吱支持的卡列表当中
			if (StringUtil.isBlank(GaZhiYinLianUtil.map.get(bankAddr))) {
				if (StringUtil.isBlank(GaZhiYinLianUtil.map2.get(bankName))) {
					// 因为通过银行编码进行匹配可能匹配不上，所以再次根据卡的名称是否有对应
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", "通道暂不支持该支付卡");
					return resultMap;
				} else {
					req.setBankCode(GaZhiYinLianUtil.map.get(GaZhiYinLianUtil.map2.get(bankName)));// 银行代码
																									// 数字
					req.setBankAbbr(GaZhiYinLianUtil.map2.get(bankName)); // 银行代号
				}
			}
			req.setBankCode(GaZhiYinLianUtil.map.get(bankAddr));// 银行代码
			req.setBankAbbr(bankAddr); // 银行代号

			if (GaZhiYinLianUtil.map.get(bankAddr).equals("weizhi")) {
				// 对于未知联行号的银行，按照卢总的意思，同一设置为 102100099996 中国工商银行总行清算中心
				req.setBankChannelNo("102100099996");// 银行联行号
			} else {
				req.setBankChannelNo(GaZhiYinLianUtil.map.get(bankAddr));// 银行联行号
			}
			req.setBankProvince("上海"); // 省
			req.setBankCity("上海市"); // 市
			JSONObject res = gaZhiYinLainService.changeRateAndCardJInfo(req);
			if (!res.getString("code").equals("000")) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "请求通道更新结算卡信息异常");
				return resultMap;
			}
			// 如果更改成功,更新进入通道信息
			Tchannel c = channelDao.get("select t from Tchannel t  where t.status=10 and t.name='GAZHIYINLIANJIFENZHITONGCHE' and t.userId=" + user.getId() + "");
			JSONObject merConfig = JSONObject.parseObject(c.getConfig());
			merConfig.remove("gazhiyinlian.bindCardJNo");
			merConfig.put("gazhiyinlian.bindCardJNo", cardJ.getCardNo());
			c.setConfig(merConfig.toJSONString());
			channelDao.save(c);

			log.info("----嘎吱校验逻辑--更新结算卡信息成功,从" + gazhiCardJNo + "更改为" + cardJ.getCardNo());
		}
		// 检查通道绑定的结算卡信息是否一致 --结束

		// 3 判断是否已经把开通卡的功能绑过 新加卡表的快捷开通字段
		if (StringUtil.isBlank(card.getIsOpenGaZhiYinLianQuickPay()) || card.getIsOpenGaZhiYinLianQuickPay().equals("0")) {
			// 调用银联侧绑卡开通接口
			BindCardForGaZhiYinLian req = new BindCardForGaZhiYinLian();
			req.setTranType("POPNCD");
			req.setMerTrace("GZYL" + UUID.randomUUID().toString().substring(0, 8));
			req.setMerNo(config.getString("gazhiyinlian.merNo"));
			req.setOrderId("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			req.setRateCode(GaZhiYinLianUtil.rateCode);
			req.setCardNo(card.getCardNo()); // 银行卡卡号
			req.setAccountName(user.getRealName()); // 银行卡姓名

			// 判断选择的支付卡的卡类型
			if (card.getCardType().equals("J")) {
				req.setCardType("1"); // 1借记卡 2信用卡
			} else {
				String cv2 = card.getCvv();
				String expired = card.getValidityDate();
				if (com.cn.flypay.utils.StringUtil.isEmpty(cv2) || com.cn.flypay.utils.StringUtil.isEmpty(expired) || expired.length() != 4) {
					resultMap.put("flag", GlobalConstant.RESP_CODE_999);
					resultMap.put("flagMSG", "交易卡信息有误");
					return resultMap;
				}
				String yearStr = expired.substring(2, 4);
				String monthStr = expired.substring(0, 2);
				req.setCardType("2"); // 1借记卡 2信用卡
				req.setCvn2(cv2); // 卡背面Cvn2数字
				req.setExpired(yearStr + monthStr); // 卡有效期
			}

			Bank bank = null;
			String bankName = "";
			TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(card.getCardNo()));
			if (cbc == null) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_053);
				resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_053));
				return resultMap;
			} else {
				bank = bankService.getBankByBankCode(cbc.getBankCode());
				if (bank == null) {
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_054));
					return resultMap;
				} else {
					bankName = bank.getBankName();
				}
			}

			String bankIco = bank.getBankIco();
			int location = bankIco.indexOf("_");
			String bankAddr = bankIco.substring(location + 1, bankIco.length()).toUpperCase(); // 银行代码大写
			// 判断卡是否在嘎吱支持的卡列表当中
			if (StringUtil.isBlank(GaZhiYinLianUtil.map.get(bankAddr))) {
				if (StringUtil.isBlank(GaZhiYinLianUtil.map2.get(bankName))) {
					// 因为通过银行编码进行匹配可能匹配不上，所以再次根据卡的名称是否有对应
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", "通道暂不支持该支付卡");
					return resultMap;
				} else {
					req.setBankCode(GaZhiYinLianUtil.map.get(GaZhiYinLianUtil.map2.get(bankName)));// 银行代码-数字
					req.setBankAbbr(GaZhiYinLianUtil.map2.get(bankName)); // 银行代号
				}

			} else {
				req.setBankCode(GaZhiYinLianUtil.map.get(bankAddr));// 银行代码
				req.setBankAbbr(bankAddr); // 银行代号
			}
			req.setPhoneno(card.getPhone()); // 银行预留手机号
			req.setCertType("01");
			req.setCertNo(user.getIdNo()); // 银行预留证件号
			req.setPageReturnUrl(frontUrl);
			req.setOfflineNotifyUrl(GaZhiYinLianUtil.bindCardNotifyUrl);

			JSONObject res = gaZhiYinLainService.bindCard(req);
			if (!res.getString("code").equals("000")) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "开通银行卡交易权限失败");
				return resultMap;
			}

			JSONObject resJson = JSONObject.parseObject(res.getString("message"));

			String activateStatus = resJson.getString("activateStatus");
			// 判断卡的状态 1 等待签约中 2 开通成功 3 开通失败 4 绑卡状态失效
			if (activateStatus.equals("1")) {
				// 等待签约中
				// 跳转至开通界面
				String openHtml = resJson.getString("html");
				String encryptHtml = "";
				try {
					encryptHtml = AESCodeUtil.encrypt(openHtml);
				} catch (Exception e) {
					e.printStackTrace();
				}
				resultMap.put("html", GaZhiYinLianUtil.bindCardResponseUrl + encryptHtml); // 测试用
				resultMap.put("type", "2");
				resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
				return resultMap;

			} else if (activateStatus.equals("2")) {
				// 开通成功
				// 更新卡的状态信息
				userCardService.updateIsOpenGaZhiYinLianQuickPay(card.getId(), "1");
				payStatus = true;
			} else if (activateStatus.equals("3")) {
				// 开通失败
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "该支付卡通道开通失败");
				return resultMap;

			} else if (activateStatus.equals("4")) {
				// 绑卡状态失效
				userCardService.updateIsOpenGaZhiYinLianQuickPay(card.getId(), "0");
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "支付异常，请重试");
				return resultMap;
			}
		} else if (card.getIsOpenGaZhiYinLianQuickPay().equals("1")) {
			payStatus = true;
		}

		// 4 检查是否可以进行支付
		if (payStatus || card.getIsOpenGaZhiYinLianQuickPay().equals("1")) {
			// 若已经开通过卡,走支付短信接口
			SendSMSForGaZhiYinLian req = new SendSMSForGaZhiYinLian();
			req.setTranType("PAYMSG");
			req.setMerNo(config.getString("gazhiyinlian.merNo"));
			req.setMerTrace("GZYL" + UUID.randomUUID().toString().substring(0, 8));
			req.setOrderId("GZYLZX" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			BigDecimal orderAmount = new BigDecimal(money * 100).setScale(0);
			req.setOrderAmount(orderAmount.toString()); // 订单金额以分为单位
			req.setRateCode(GaZhiYinLianUtil.rateCode);
			req.setCardNo(card.getCardNo()); // 银行卡号
			req.setAccountName(user.getRealName()); // 银行卡姓名

			// 判断选择的支付卡的卡类型
			if (card.getCardType().equals("J")) {
				req.setCardType("1"); // 1借记卡 2信用卡
			} else {
				req.setCardType("2"); // 1借记卡 2信用卡
			}

			Bank bank = null;
			String bankName = "";
			TcardBankConfig cbc = bankConfigService.isRealCardNo(ImportUtil.getDecCardNo(card.getCardNo()));
			if (cbc == null) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_053);
				resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_053));
				return resultMap;
			} else {
				bank = bankService.getBankByBankCode(cbc.getBankCode());
				if (bank == null) {
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", GlobalConstant.map.get(GlobalConstant.RESP_CODE_054));
					return resultMap;
				} else {
					bankName = bank.getBankName();
				}
			}
			String bankIco = bank.getBankIco();
			int location = bankIco.indexOf("_");
			String bankAddr = bankIco.substring(location + 1, bankIco.length()).toUpperCase(); // 银行代码大写
			// 判断卡是否在嘎吱支持的卡列表当中
			if (StringUtil.isBlank(GaZhiYinLianUtil.map.get(bankAddr))) {
				if (StringUtil.isBlank(GaZhiYinLianUtil.map2.get(bankName))) {
					// 因为通过银行编码进行匹配可能匹配不上，所以再次根据卡的名称是否有对应
					resultMap.put("flag", GlobalConstant.RESP_CODE_054);
					resultMap.put("flagMSG", "通道暂不支持该支付卡");
					return resultMap;
				} else {
					req.setBankCode(GaZhiYinLianUtil.map.get(GaZhiYinLianUtil.map2.get(bankName)));// 银行代码
																									// 数字
					req.setBankAbbr(GaZhiYinLianUtil.map2.get(bankName)); // 银行代号
				}
			} else {
				req.setBankCode(GaZhiYinLianUtil.map.get(bankAddr));// 银行代码
				req.setBankAbbr(bankAddr); // 银行代号
			}

			req.setPhoneno(card.getPhone());
			req.setCertType("01"); // 证件类型
			req.setCertNo(user.getIdNo()); // 银行预留证件号
			JSONObject res = gaZhiYinLainService.sendSMS(req);
			if (!res.getString("code").equals("000")) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "发送支付短信失败");
				return resultMap;
			}
			JSONObject resJson = JSONObject.parseObject(res.getString("message"));
			// 支付流水号
			String payNo = resJson.getString("payNo");
			String resultCode = resJson.getString("resultCode");
			String resultMsg = resJson.getString("resultMsg");
			if (!"000000".equals(resultCode) || !"SUCCESS".equals(resultMsg)) {
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "发送支付短信失败");
				return resultMap;
			}

			String orderId = req.getOrderId(); // 订单号
			desc = "嘎吱银联积分通道--" + user.getRealName() + "_" + user.getLoginName() + "正在用卡" + card.getCardNo() + "向" + cardJ.getCardNo() + "支付" + money + "元";
			try {
				// 由于验证码下单接口 createOnlineVerify
				// 中缺少一些参数，且该接口的参数需要前端返回，所以，尽管下单是在createOnlineVerify接口，但综合考虑，创建订单还是放在发送验证码接口中
				userOrderService.createTransOrder(user.getId(), orderId, null, null, UserOrder.trans_type.YLZXJ.getCode(), money, UserOrder.cd_type.D.name(), null, card, desc, transPayType, cpr.getChannel(), inputAccType, angentType);
				log.info("---银联积分---嘎吱通道----调用发送验证码接口---保存订单：" + orderId + "  ----成功");
			} catch (Exception e) {
				log.error("---银联积分---嘎吱通道----调用发送验证码接口---保存订单：" + orderId + "  ----失败：" + e);
				resultMap.put("flag", GlobalConstant.RESP_CODE_999);
				resultMap.put("flagMSG", "保存订单失败");
				return resultMap;
			}
			// 为1时表示要去调用验证码页面
			resultMap.put("type", "1");
			resultMap.put("transactionId", "GZ" + payNo); // 方便createOnlineVerify接口判断，加上字符
			resultMap.put("payAmount", req.getOrderAmount()); // 支付金额
			resultMap.put("payCardId", String.valueOf(card.getId())); // 支付卡的ID
			resultMap.put("orderId", orderId); // 订单
			resultMap.put("flag", GlobalConstant.RESP_CODE_SUCCESS);
			return resultMap;

		}
		return resultMap;
	}

	@Override
	public Map<String, String> sendOrderNumToChannelForSearchStatus(String orderNum) {
		try {
			UserOrder userOrder = userOrderService.findTodoUserOrderByOrderNum(orderNum);
			ChannelPayRef cpr = troughTrainServeice.getChannelPayRef(false, "550", userOrder.getUserId(), "", "GAZHIYINLIANJIFENZHITONGCHE");
			JSONObject channelConfig = cpr.getConfig();
			QueryOrderInfoForGaZhiYinLian req = new QueryOrderInfoForGaZhiYinLian();
			req.setTranType("PAYQRY");
			req.setMerNo(channelConfig.getString("gazhiyinlian.merNo"));
			req.setMerTrace("GZYL" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			req.setOrderId(userOrder.getOrderNum());
			JSONObject res = gaZhiYinLainService.queryOrderInfo(req);
			if (!res.getString("code").equals("000")) {
				JSONObject resJson = JSONObject.parseObject(res.getString("message"));
				// 交易成功
				PayOrder payOrder = new PayOrder();
				payOrder.setPayAmt(userOrder.getOrgAmt().divide(BigDecimal.valueOf(100))); // 用户的支付金额,订单金额以分为单位
				payOrder.setRealAmt(userOrder.getOrgAmt()); // 商户应收额
				payOrder.setPayFinishDate(resJson.getString("payDate")); // 支付时间
				payOrder.setPayNo(resJson.getString("payNo")); // 支付流水号
				Boolean flag = false;
				if (res.getString("orderStatus").equals("2")) {
					// 支付成功
					flag = true;
					payOrder.setStatus(PayOrder.pay_status.SUCCESS.getCode());
				} else if (res.getString("orderStatus").equals("5")) {
					flag = true;
					payOrder.setStatus(PayOrder.pay_status.FAILURE.getCode());
				}
				// 对于嘎吱的其它订单状态，系统不做处理
				if (flag) {
					userOrderService.finishInputOrderStatus(orderNum, payOrder);
				} else {
					log.info("----嘎吱银联积分--调用重新查询订单---订单状态为非成功和非失败状态，由于凭条处理逻辑中不包含该状态类型，默认不做处理，略过。 ");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {

		Double money = 5000.0;
		System.out.println(money * 100);
		BigDecimal orderAmount = new BigDecimal(money * 100).setScale(0);
		System.out.println(orderAmount);
		// 金额 money
		// req.setOrderAmount(orderAmount.toString()); // 订单金额以分为单位
		// for (int i = 0; i < 10; i++) {
		// String uuid = UUID.randomUUID().toString().substring(0, 8);
		// System.out.println(i + " - " + uuid);
		// }
	}
}
