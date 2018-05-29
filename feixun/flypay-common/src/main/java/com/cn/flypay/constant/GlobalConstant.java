package com.cn.flypay.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalConstant {

	public static final String SESSION_INFO = "sessionInfo";

	public static final Integer ONE = 1;
	public static final Integer ZERO = 0;

	public static final Integer ENABLE = 0; // 启用
	public static final Integer DISABLE = 1; // 禁用

	public static final Integer DEFAULT = 0; // 默认
	public static final Integer NOT_DEFAULT = 1; // 非默认

	public static final String SUCCESS = "SUCCESS"; // 默认
	public static final String FAILUER = "FAILUER"; // 非默认
	public static final String WAITING = "WAITING"; // 非默认

	public static final String RESP_CODE_SUCCESS = "000";

	public static final String RESP_CODE_001 = "001";
	public static final String RESP_CODE_002 = "002";
	public static final String RESP_CODE_003 = "003";
	public static final String RESP_CODE_004 = "004";
	public static final String RESP_CODE_005 = "005";
	public static final String RESP_CODE_006 = "006";
	public static final String RESP_CODE_007 = "007";
	public static final String RESP_CODE_008 = "008";
	public static final String RESP_CODE_009 = "009";
	public static final String RESP_CODE_010 = "010";
	public static final String RESP_CODE_011 = "011";
	public static final String RESP_CODE_012 = "012";
	public static final String RESP_CODE_013 = "013";
	public static final String RESP_CODE_014 = "014";
	public static final String RESP_CODE_015 = "015";
	public static final String RESP_CODE_021 = "021";
	public static final String RESP_CODE_022 = "022";
	public static final String RESP_CODE_023 = "023";
	public static final String RESP_CODE_024 = "024";
	public static final String RESP_CODE_025 = "025";
	public static final String RESP_CODE_026 = "026";
	public static final String RESP_CODE_027 = "027";
	public static final String RESP_CODE_028 = "028";
	public static final String RESP_CODE_029 = "029";
	public static final String RESP_CODE_030 = "030";
	public static final String RESP_CODE_031 = "031";
	public static final String RESP_CODE_032 = "032";
	public static final String RESP_CODE_033 = "033";
	public static final String RESP_CODE_034 = "034";
	public static final String RESP_CODE_035 = "035";
	public static final String RESP_CODE_040 = "040";
	public static final String RESP_CODE_041 = "041";
	public static final String RESP_CODE_042 = "042";
	public static final String RESP_CODE_043 = "043";
	public static final String RESP_CODE_044 = "044";
	public static final String RESP_CODE_045 = "045";
	public static final String RESP_CODE_046 = "046";
	public static final String RESP_CODE_047 = "047";
	public static final String RESP_CODE_048 = "048";
	public static final String RESP_CODE_049 = "049";
	public static final String RESP_CODE_050 = "050";
	public static final String RESP_CODE_051 = "051";
	public static final String RESP_CODE_052 = "052";
	public static final String RESP_CODE_053 = "053";
	public static final String RESP_CODE_054 = "054";
	public static final String RESP_CODE_055 = "055";
	public static final String RESP_CODE_056 = "056";
	public static final String RESP_CODE_057 = "057";
	public static final String RESP_CODE_058 = "058";
	public static final String RESP_CODE_059 = "059";
	public static final String RESP_CODE_060 = "060";
	public static final String RESP_CODE_066 = "066";
	public static final String RESP_CODE_067 = "067";
	public static final String RESP_CODE_068 = "068";
	public static final String RESP_CODE_069 = "069";
	public static final String RESP_CODE_070 = "070";
	public static final String RESP_CODE_074 = "074";
	public static final String RESP_CODE_075 = "075";
	public static final String RESP_CODE_078 = "078";
	public static final String RESP_CODE_079 = "079";
	public static final String RESP_CODE_080 = "080";
	public static final String RESP_CODE_081 = "081";
	public static final String RESP_CODE_082 = "082";
	public static final String RESP_CODE_083 = "083";
	public static final String RESP_CODE_084 = "084";
	public static final String RESP_CODE_085 = "085";
	public static final String RESP_CODE_086 = "086";
	public static final String RESP_CODE_087 = "087";
	public static final String RESP_CODE_100 = "100";
	public static final String RESP_CODE_500 = "500";
	public static final String RESP_CODE_994 = "994";
	public static final String RESP_CODE_995 = "995";
	public static final String RESP_CODE_996 = "996";
	public static final String RESP_CODE_997 = "997";
	public static final String RESP_CODE_998 = "998";
	public static final String RESP_CODE_999 = "999";
	public static final Map<String, String> map = new HashMap<String, String>();
	static {
		map.put("000", "成功");
		map.put("001", "验证码错误");
		map.put("002", "登录密码错误");
		map.put("003", "交易密码错");
		map.put("004", "原密码错误");
		map.put("005", "参数非法");
		map.put("006", "手机号不存在");
		map.put("007", "短信发送过频");
		map.put("008", "用户登录状态失效或在其他地方登录，请重新登录");
		map.put("009", "已实名认证");
		map.put("010", "上传图片超过大小");
		map.put("011", "上传图片太多");
		map.put("012", "商户不存在");
		map.put("013", "资金账户不存在");
		map.put("014", "余额不足");
		map.put("015", "交易已成功");
		map.put("021", "商户未实名认证");
		map.put("022", "商户结算状态非法");
		map.put("023", "商户状态非法");
		map.put("024", "结算卡不存在");
		map.put("025", "默认结算卡不可删除");
		map.put("026", "手机号已存在");
		map.put("027", "请使用新手机号");
		map.put("028", "交易金额超限");
		map.put("029", "未开启坐标定位");
		map.put("030", "未配置手续费");
		map.put("031", "未设置推广费率");
		map.put("032", "不允许交易");
		map.put("033", "卡类型错误");
		map.put("034", "交易密码未设置");
		map.put("035", "资金账户状态非法");
		map.put("040", "无可用推广码");
		map.put("041", "商户无隶属关系");
		map.put("042", "系统无积分数据");
		map.put("043", "金额非法");
		map.put("044", "请勿重复操作");
		map.put("045", "推广码不存在/无效");
		map.put("046", "订单不存在");
		map.put("047", "交易失败");
		map.put("048", "无服务商编号");
		map.put("049", "存在多个服务商");
		map.put("050", "身份证号与已认证的信息不一致");
		map.put("051", "订单创建失败");
		map.put("052", "交易卡信息与本人信息不比配");
		map.put("053", "银行卡号不正确");
		map.put("054", "本系统不支持该银行卡");
		map.put("055", "实名认证中");
		map.put("056", "今天实名认证次数已经达到上限，请明天再试");
		map.put("057", "商户未注册");
		map.put("058", "今天银行卡认证次数已经达到上限，请明天再试");
		map.put("059", "您用户升级购买订单过于频繁，请1分钟后再次尝试");
		map.put("060", "您用户已经升级成功，无需再次购买");
		map.put("066", "系统清算中，请稍后再试");
		map.put("067", "运营商不存在");
		map.put("068", "订单等待支付中");
		map.put("069", "金额必须为整数，且后三位不能相同");
		map.put("070", "运营商欠费已停止该服务，请联系客服");
		map.put("074", "积分不足");
		map.put("075", "积分账户状态非法");
		map.put("078", "用户降低费率异常，请重新登录");
		map.put("079", "交易通信异常，系统正在与银行确认");
		map.put("080", "超出订单查询天数限制，最多%s天");
		map.put("081", "您的实名认证的账户为非正常状态，请联系客服");
		map.put("082", "您的账户为非正常状态，请联系客服");
		map.put("083", "无银行卡cvv安全码信息，请重新绑卡");
		map.put("084", "无银行卡有效期信息，请重新绑卡");
		map.put("085", "商户没有报备信息");
		map.put("086", "信用卡没有报备信息,请稍后再试");
		map.put("087", "首笔交易请使用银联在线");
		map.put("100", "银行卡信息未通过实名认证");
		map.put("500", "暂无可用支付通道，请及时联系客服");
		map.put("994", "订单生成失败，请重新生成");
		map.put("995", "输入信息格式错误");
		map.put("996", "验证签名失败");
		map.put("997", "账号异地登录");
		map.put("998", "版本过低");
		map.put("999", "系统错误");
	}

	public static final String T_ORDER_CONFIG = "TR";

	public static final List<String> ZTC_LIST = new ArrayList<String>();
	static {
		ZTC_LIST.add("MINGSHENGZHITONGCHE");
		ZTC_LIST.add("XINKKEZHITONGCHE");
		ZTC_LIST.add("YILIANZHIFUZTC");
		// ZTC_LIST.add("YILIANZHIFU");
		ZTC_LIST.add("YILIANYINLIANJIFENZTC");
		ZTC_LIST.add("XINKEYINLIAN");
		ZTC_LIST.add("XINKKEYINLIAN");
		ZTC_LIST.add("PINGANPAYZHITONGCHE");
		ZTC_LIST.add("PINGANPAYZHITONGCHE_ZHIQING");
		ZTC_LIST.add("ZHEYANGZTC");
		ZTC_LIST.add("ZHEYANGJFZTC");
		ZTC_LIST.add("SHENFUZTC");
		ZTC_LIST.add("YIQIANGZTC");
		ZTC_LIST.add("YIBAOZHITONGCHE");
		ZTC_LIST.add("GAZHIYINLIANJIFENZHITONGCHE");
		ZTC_LIST.add("WEILIANBAOYINLIANJIFENZHITONGCHE");
		
	}
}
