package com.cn.flypay.utils.shenfu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 */
public class ApplicationBase {

	// 默认配置的是UTF-8
	public static String encoding_UTF8 = "UTF-8";

	public static String encoding_GBK = "GBK";

	public static final String MERCID = "307110059990026";
	public static final String PRIVATEKEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIih9uSbeXTTx1V51NyQyLUjMNjK5I8UP82f9C5/grvumS6MaQiCdAGRy9B1kqJ9gTAcIrjmdS7OuUCaF9LHtuONlrQLuipqw7kzfK8L2tPdYAA7N/PEe8gyCcGjpNxffOA/0LmVGmK+vZeF5oBnmG2R2EiGm8VsahYSC21rcn29AgMBAAECgYBxSoLSWYwi77s3x7ZVUQPrFgvZpppPfQXOdRPePkJjm9qN+0TYFnskFgT+i46ThydIgfvUftqpfiWjSGH6lziwTbOUCkg+xdjUWsNGyWG6Q0S21guYtdBjM8pPkdd2K/AYEzXJWc3qJKuvmLRkm8y0DIHtI/0epdzFqa1JlFFNTQJBAL4thGoGNoeB+l/IZVhUWj9FLHNFvYOLX4Yac4qwsVQszMhlneFMuPVMxrTGirHfVMJZrbnKrEoA09vP73RApOsCQQC37CS3iXubMwlomgHkqv3cIAcYI6oTgEmFjn9IwsJaymps3JkbLhySeAu523hNZr/e54GJERlFaDqq3NZs/l33AkA5cpx+PpQ4SN4uEuXyUcDVPfsYcye1VeOsKc9fRNK8X7AYlQGlLjzs9J1NvG/nHVSIRPjCl+OFk0xsKRrnXiOzAkAl/XgEFTjb8OnTmmQjuJIhQthUSyGZT5lgJfPyso/0svTSsapKa/bo5jT92fB6hGvDfixKhKakqIfdvSnpb403AkEAqvqQM1RWakZALt854ScJkF12yLukDv9mmlNcOmj3SceN0yisePchh8BLgML9KPdrm29T9b7DwkqWBTeH/tVSKw==";// 上游平台提供
	public static final String PUBLICKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIofbkm3l008dVedTckMi1IzDYyuSPFD/Nn/Quf4K77pkujGkIgnQBkcvQdZKifYEwHCK45nUuzrlAmhfSx7bjjZa0C7oqasO5M3yvC9rT3WAAOzfzxHvIMgnBo6TcX3zgP9C5lRpivr2XheaAZ5htkdhIhpvFbGoWEgtta3J9vQIDAQAB";
	public static final String CONSUMEURL = "https://www.shenfupay.com:8443/SFQuickPay/PublicConsume";
	public static final String CONSUMED0_URL = "https://www.shenfupay.com:8443/SFQuickPay1/v2.0/PublicConsume";
	public static final String SHENFU_ADDCREDIT_URL = "https://www.shenfupay.com:8443/SFQuickPay1/PublicMercOpenCard";
	public static final String SHENFU_VERIFYCARD_URL = "https://www.shenfupay.com:38443/SFQuickPayTool1/verifyBankCard";
	public static final String SHENFU_UPDATESETTLE_URL = "http://bp.shenfupay.com/SF/help/mercUpdate.action";
//	public static final String SHENFU_SEARCHORDER_URL = "https://www.shenfupay.com:8443/SFQuickPay1/QueryTrade";
	public static final String SHENFU_SEARCHORDER_URL = "http://114.141.132.88:48080/SFQuickPay/QueryTrade";
	public static final String SHENFU_DAIFU_URL = "http://114.141.132.75:28080/SFDaiFuZx/publicSingleDaiFu";
	public static final String SHENFU_AUTHCARD_URL ="https://www.shenfupay.com:8443/SFQuickPay/PublicAuthCard";
	public static final String SHENFU_DAIFU_MERCHID = "201712070000022";
	public static final String SHENFU_DAIFU_PRIVATEKEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKPI00Bol5NFfQkQIaFrM3m7ShqlLgJSrbKsX+UwMn6eb2oLmrSeXng/IX1z6Jys1jOaRuR1XzdCZqSk60txmE35h1ZeTHVGU6yJawx9BTvYOpAmjJJc2CdoIwBdErbGRZzs8+IbpQeJqbOt5WYWb02fALVbUHNkbcxtTjN4AzcDAgMBAAECgYBnF+4BSNTAGqlNnx3MIVJReZZ3lymSxMAZC2+BMAlYshmnQo6qylTkk7hc4yy0EgbyjFW7UDYS7nT1hemln2ciCxzCU/1+nLlvU2YUygn+7g7qWnoz0wxDG1MqrmKovPgwQMBPrwycfHrqPLLauBouHurNceyf0EFRIO1VGM8noQJBAN5Hrc1mvF6c5FilaRcpLtyAnnJD3VhEtLswq3d0fYZKwAfnY1D7nPmYRfhP07xQVQ+hQv8qnFSnOn3AiFso4M8CQQC8oXPJnn0kbkxkkahcm622T0bpv9fpS39Wf6lcMHyFzSf0KZwpSQ5c/GlRaqGmRcFr2Gg6Rr6hWxPlL9l+YYuNAkEAuAuxpPkDz3Movx12vm6jFoCIsSPwCj4ZR0UtNGOH68HI7sJwQYWUnDWw+cFfF/WL+L0VtCGb/5vXmnfob1KRkQI/bcmKms6kB5Aq5l3goAX8FSAPJf2CrUZD1xR2fjG6SCYgGvwc5+qdcYF3yutFKoFvElXiVAjY6xEXg01WSTh9AkAz9X83C3oXc+mlFiG3NWelVRVrHaYLH6rsB9i3iT0ffsTKuA76PYZEfXxm1/uMTy8XtJ5I4/cZ5NDai9BWmRGE";
	public static final String SHENFU_DAIFU_PUBLICKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjyNNAaJeTRX0JECGhazN5u0oapS4CUq2yrF/lMDJ+nm9qC5q0nl54PyF9c+icrNYzmkbkdV83QmakpOtLcZhN+YdWXkx1RlOsiWsMfQU72DqQJoySXNgnaCMAXRK2xkWc7PPiG6UHiamzreVmFm9NnwC1W1BzZG3MbU4zeAM3AwIDAQAB";

	public static final String SHENFU_MercRateconf_ACTION = "http://bp.shenfupay.com/SF/help/mercRateconf.action";
	
	
	// public static final String
	// CONSUMEUNDOURL="https://www.shenfupay.com:28443/SFQuickPay1/PublicConsumeUndo";
	// public static final String
	// REFUNDURL="https://www.shenfupay.com:28443/SFQuickPay1/PublicRefund";
	// public static final String
	// OPENCARDURL="https://www.shenfupay.com:28443/SFQuickPay1/PublicOpenCard";

	// 商户发送交易时间 格式:YYYYMMDDhhmmss
	public static String getCurrentTime() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	// AN8..40 商户订单号，不能含"-"或"_"
	public static String getOrderId() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	/*
	 * 参数名按ASCII码从小到大排序，使用”&“连接，参数名signature不参与拼接，
	 */
	public static String coverMap2String(Map<String, String> data) {
		TreeMap<String, String> tree = new TreeMap<String, String>();
		Iterator<Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			if ("signature".equals(en.getKey().trim())) {
				continue;
			}
			tree.put(en.getKey(), en.getValue());
		}
		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			sf.append(en.getKey() + "=" + en.getValue() + "&");
		}
		return sf.substring(0, sf.length() - 1);
	}

	public static String coverTreeMap2String(Map<String, String> data) {
		Iterator<Entry<String, String>> it = data.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			if ("signature".equals(entry.getKey())) {
				continue;
			}
			sf.append(entry.getKey() + "=" + entry.getValue() + "&");
		}
		return sf.substring(0, sf.length() - 1);
	}

}