package com.cn.flypay.utils.weilianbao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class WeiLianBaoPayUtil {

	private static final Logger LOG = LoggerFactory.getLogger(WeiLianBaoPayUtil.class);

	public static String CHANNEL_NO = "C2544348799";
	public static String CHANNEL_NAME = "上海涩零企业营销策划有限公司";

	public static String SIGN_KEY = "5UA6x155djd561383Goa447w54j0ue32";
	public static String DES_KEY = "6e77zW5BhU7dt02876n4i89E";
	// 商户入网(大商户)：
	// private static String inUrl = "http://api.izhongyin.com/middlepayportal/merchant/in";
	// real.izhongyin.com
	// pay.feifanzhichuang.com
	// 商户入网(一户一码)
	private static String in2Url = "http://real.izhongyin.com/middlepayportal/merchant/in2";
	// 商户秘钥查询
	private static String querykeysUrl = "http://real.izhongyin.com/middlepayportal/merchant/querykeys";
	// 商户资料变更
	private static String modifyUrl = "http://real.izhongyin.com/middlepayportal/merchant/modify";
	// 商户费率增加
	private static String addFeeUrl = "http://real.izhongyin.com/middlepayportal/merchant/addFee";
	// 商户费率修改
	private static String modifyProductFeeUrl = "http://real.izhongyin.com/middlepayportal/merchant/modifyProductFee";
	// 银联支付接口地址：
	private static String kuaiPayUrl = "http://real.izhongyin.com/middlepaytrx/kuaiPay";

	// 微信扫码支付,QQ钱包扫码支付，京东支付：
	// http://real.izhongyin.com/middlepaytrx/wx/scanCode
	// 微信公众号,QQ钱包：http://real.izhongyin.com/middlepaytrx/wx/scanCommonCode
	// 微信条码支付,QQ钱包条码支付，京东条码支付：
	// http://real.izhongyin.com/middlepaytrx/wx/passivePay
	// 支付宝扫码支付：http://real.izhongyin.com/middlepaytrx/alipay/scanCode
	// 支付宝公众号支付：http://real.izhongyin.com/middlepaytrx/alipay/scanCommonCode
	// 支付宝条码支付：http://real.izhongyin.com/middlepaytrx/alipay/passivePay
	// 银联二维码扫码：http://real.izhongyin.com/middlepaytrx/unipay/scanCode
	// 银联二维码条码支付：http://real.izhongyin.com/middlepaytrx/unipay/passivePay
	// 查询支付：http://real.izhongyin.com/middlepaytrx/online/query

	public static JSONObject registerSubMerchant(Map<String, String> map) throws Exception {
		return HttpUtil.post(map, in2Url);
		// return post(map, in2Url);
	}

	public static String registerSubMerchant2(String data) throws Exception {
		return HttpUtil.sendPost(in2Url, data, HttpUtil.CONTENT_TYPE_FROM);
	}

	public static String addFee(String data) throws Exception {
		return HttpUtil.sendPost(addFeeUrl, data, HttpUtil.CONTENT_TYPE_FROM);
	}

	public static JSONObject openCard(Map<String, String> map) throws Exception {
		return post(map, kuaiPayUrl);
	}

	public static JSONObject sendSmsCode(Map<String, String> map) throws Exception {
		return post(map, kuaiPayUrl);
	}

	public static JSONObject consume(Map<String, String> map) throws Exception {
		return post(map, kuaiPayUrl);
	}

	private static ConnectionKeepAliveStrategy myStrategy = null;
	static {
		myStrategy = new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if ((value != null) && (param.equalsIgnoreCase("timeout"))) {
						return Long.parseLong(value) * 1000L;
					}
				}
				return 5000L;
			}
		};
	}

	private static CloseableHttpClient httpclient = HttpClientBuilder.create().setMaxConnTotal(90).setMaxConnPerRoute(15).setKeepAliveStrategy(myStrategy).build();

	public static JSONObject post(Map<String, String> params, String url) throws Exception {
		HttpPost post = new HttpPost(url);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			list.add(new BasicNameValuePair(key, (String) params.get(key)));
		}
		post.setEntity(new UrlEncodedFormEntity(list, Consts.UTF_8));
		CloseableHttpResponse response = httpclient.execute(post);
		JSONObject json = null;
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			String str = EntityUtils.toString(entity);
			if (entity != null)
				json = JSONObject.parseObject(str);
		} else {
			EntityUtils.consume(response.getEntity());
		}
		return json;
	}
}
