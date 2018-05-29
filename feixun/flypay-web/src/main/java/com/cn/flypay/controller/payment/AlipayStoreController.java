package com.cn.flypay.controller.payment;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.FileItem;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayOfflineMarketApplyorderBatchqueryRequest;
import com.alipay.api.request.AlipayOfflineMarketShopCategoryQueryRequest;
import com.alipay.api.request.AlipayOfflineMarketShopCreateRequest;
import com.alipay.api.request.AlipayOfflineMarketShopQuerydetailRequest;
import com.alipay.api.request.AlipayOfflineMaterialImageUploadRequest;
import com.alipay.api.request.AlipayOpenAuthTokenAppQueryRequest;
import com.alipay.api.request.AlipayOpenAuthTokenAppRequest;
import com.alipay.api.response.AlipayOfflineMarketApplyorderBatchqueryResponse;
import com.alipay.api.response.AlipayOfflineMarketShopCategoryQueryResponse;
import com.alipay.api.response.AlipayOfflineMarketShopCreateResponse;
import com.alipay.api.response.AlipayOfflineMarketShopQuerydetailResponse;
import com.alipay.api.response.AlipayOfflineMaterialImageUploadResponse;
import com.alipay.api.response.AlipayOpenAuthTokenAppQueryResponse;
import com.alipay.api.response.AlipayOpenAuthTokenAppResponse;
import com.cn.flypay.utils.ApplicatonStaticUtil;
import com.cn.flypay.utils.alipay.Alipay;

@Controller
@RequestMapping("/alipayStore")
public class AlipayStoreController {
	private  final static Logger logger = LoggerFactory.getLogger(AlipayStoreController.class);
	
	
	/*支付宝创建口碑门店参数 --沙箱环境*/
	//private String serverUrl = "https://openapi.alipaydev.com/gateway.do";	//沙箱
	//private String appId = "2016080700186009";   //沙箱账号
	//老版本 private String privateKey ="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQClW+k1u64lQx1PRVNHtRxeGeRrNniAJgUak64zLA4DoX2auv1UacaLvf09C5B5Ws8RduL3kmiMBvxzAV5NxQrHDjlxN1P7L3oa1IgEWljDaykZqS29F7+pKfN7ehRba340/RRS9qnSIsSZs2N4IQyjQB0Mn3iFo+2AxqN+IaT8wexz2LxPL2Q1NjSplLHfi3L4pbSArbL1miHyo6pZet4sT/2cyeqBKeeZQP1kXK+67StQns8sAKui/8riyOQLvsm4T3OjrlNfytqA1Ms7Yc4XrsqMggX6fiQH4frjy32iFgsn0txodokj7yNRjljnpTvMFt4h2jeeQaGV8dIzGKrxAgMBAAECggEAZ/0a9d2nMGu6fOV97/RneI4T5ZCFyyEdyI7i1+92iLYABVReh71VTQAXx1IEcyy2V6dKKKL7+ENUcwaDRakKlA/P8/D1cVt0EHdjakle1NYJLMgFqYrLzxhAtIAbWTqKTcxyAZJ5TVrsBDSZ8yvlhKyHGC8ZXgGvFHLW0jVaSlCuoXPJL+sWmnzUJIPyF8I63Zv8d0dUo8fwDqwNuYn2vSaryKoP6FSGBo1mkcSIdhgmo9N1Unh8qv0L097i4Lpk+4X4cjuPQwsVBzULcd6mv4Wb5m1cdcFKm+gesxnwWxP22RPrw4SxaNjBDmO3dE/+OSmjR+ZcPoKh7/UFLwocOQKBgQDaez3/vwgi+4ApsOZwqyQvyswP6PPr18aOvko9STwdqARKEPOB9+5eDIjc1OoF1sa2/tHRBtP35cVYe6GIiCH3awIcunLORrZcoHcqTsqMlMvguqRfW3Fy31Iunw9s02qN3+gW5nX2Fk6r038PgM0UGVDvDuPZ9O3NmKaYf/k/rwKBgQDBwVUD0rORpFQYpzXY+lYglQeulday70lVUmmGEss0TeYzLxODartEm6SN1oAalqO5FK3duMK2+O9R9f0Qp94IC1FdXqeA4gHFFUMjiJXcVmwg6lf5adTGJVPEMsq6Zjcyz6uh/no9OdB1EBY7bPwXZGj1Kgi2tC3/VFkYQbfHXwKBgEu97gB7eZanGgdaDImCy0jGRXg+ilF4vJ0/2vzkJrMhTFthBQpJqZlxPwFPztG3Y6yNKrAMbe/C+Gd0dMugZiYgMttCyAo6+X2jI6zHI/EYpJWEHvfv+6SXRyZifjZ8FLSaqxtFSh/GG//1klcNkAsnu6ckwlkNUGO2SAsWUpKBAoGAYAlX/kEdyH7PQDkBM79BmXzFBgA3HxIEI0pqC46dMhu6knwvC+CHWgZblgJQFN294Ssi0teVi+Pvm4x2Fx9dqlcRyVn1ZKihf/J+CMrWnRQpHFeLq5CNanlt/729Ro8LpR/STsU4CcjyTlPe8S2f9mhKrdpb31OHyRsh3igU07kCgYEA1uKxSGZzg858BDe6xxVaPmRhmpfyzswuRwuJm4/INwBDhQgHhu8QoxGzDqmrCoshGXIMA2y0NHIz4g8lZ5qDzYcKWzljxYft8IwBx1Tp6EziDcEu/k+WsU6U7ARhnHPjF29NlRp4hyI9/RogQOMTnPfslZ1k2xzvU59e2IYGqxQ=";
	//老版本private String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3yiwq/Wl3ztKNim+iuUGzHkQi3Dv5mF/YZ7CEWLCC0ZFG47xjvWPU1V7vtA1FpuBUfbzGbNfhR2+czU1XBIekjaux4rTovFzbfqJUz5umkwDY7/yTl0Ne+arAXWaDmRqqIHhgz887o9x2sUCCyJtkcMNFnc19paTUD446WxCJfQQUwuhlKu0Qxkf0IZRmO4VqEZhnqNAbJBX9FysDEKaIdxKXTpiggFBA69dGYPK3NACw6V3KC8gezXIQVPr7SIw5kLtraSEsW1k6uZqtqUDCW+5VbJvPt3Dx4jaipA9ZNbQN3PkHp+InClaf4dI+PnSIsL/8hD74o7bZHwY7rTpsQIDAQAB";
	//老版本private String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAli9LHZw48fw76RPfeJnZjiOkTrO961Wn59IsoE8/CDChTwuUg6PMhyMWvzYlEHpp3C2S0bHQe/zH3+gLAcUBc05QYs6i6xKxWr1hquLLMCNXwqcTEQrF8J3yNpw/LhgIgWjCfJfQRqVBzQ7EeFe71LLyA5Izp7skF7LRi3MofuONOHvBs1+pZdFzYT/CHfNiKWKVCRgdtSwOn36jDbV5/Pnzd/mHAJJHQ+hBElI/006IHN51DEET86av+xoyNcZB+bTIlJ8Fe6kA0LRzYbOhRVMa2u7FwrSmtvK2B6QMTXz1GoUiPXYG59X4ToFa2JYEoUyOy3kvQFFt24U4awcUKQIDAQAB";
	
	/*支付宝创建口碑门店参数 --正式环境*/
	private String serverUrl = "https://openapi.alipay.com/gateway.do";	//正式支付宝网关
	private String appId = "2015110300669781";	//正式账号
	private String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCZqkNmp8rUBGUBvoWI2eqLUih5OFGvjTyeczMAvvBCIFjc5QdcTrY6KyGS0/CLmP8GYnSa97HgfT+QABXqPeSKZCW4L5DZj9t/wFa3TgIwi9Xxm4kfIEHK5nKupIK3xfkmpx9BY4YjGdtjF8vzcwHyJZg2APhlrh7OEbAbtL/K6HnBJn8CAugdoSZTQf+acAQrwNFQS2qwcNqmMPz5xtpDJ8XvLbTsFusxYbnQwx80vv42HTCk/vhcMvRZXhlcgYtHUWOLE8YGKUqvPbPLUIO8E5N9GaxpDtpAsrFyciMHSRdpK7O3ogSmvVlwgfio3CRIQA56ZTRe+J3Zr08ZyevnAgMBAAECggEASUPb1QMlescJnbZIFsLHyFuIvpsynz/46hdE9xMSnIStucxdIQ7KZZuQ61+vre9pkJK6w/RbVAyDlNXlnQSbhbxQG6xaJNEpN5TuA4HnG1xgykx9KWEI3YPYpBn91T9IFrzR8yxPlUbLykhbTpzEDb8M/pqh1GEbgOcAD4WzeDt1jHtKP9OKIE9ohS/uPEpd8qEbQfRDxBnQp5Djd9pKgKK2wRQSnDt7dKRzK7WFMJ015lB5uWkH7oSPLPuZM5JcB7SrTt0SW/8N98ejPsvv4XkNaQKDEXyrHfSmfZ6/TOIQ/afpznhIzGQDPl23K+/4q1u69vALPmruCuNSl2c4+QKBgQDrCRTBKDtsxpPyqok549vJezh/9dNrpnpI9G3fOBa/732kDavd4HiCfOSso2MA6X3x0yWCUaEqosLctFsZ6oOuHxZOLDjt12PerD7Ok9gGkSHPsZ0Poc5ByyDHN0WzyMCQhRjZteJP2YHd/qxAyOUSomgWHsPf1i7VPtMQEbhKtQKBgQCnXyE2N9m8V8Sl2nsmoc4sxSMMNK3oNfhVI2yoUKueVGAl9my/A3EOaUu9mXL0UjsnT932pwqWMG0Fx6T77d7EcGEB/X8yuHr3Vu8bkGbgiOIolP9HTZEokLTWT/C55mRuN22GizTo+dwpgWjfaGxJTpIfvk3WBpZHrTFbbhIRqwKBgQCKJ7VecQK1RujSXLH7KFgaT+b/OIXFIn+BAMTQ0Ex3GBSXaT3cV0UfFsDoRcY9VAKnMkE7IDaHNFMsnotqaXJr2HQpEU+JZvGag08L6NWzzn28cx/qf2cZP4iN8Yx0SI4ApWvdFU/pNeZjkn64Oh6E3JgoYH8sxN6UlLBGItRcfQKBgAas/rAg7iha/9b1re/OiGC0xD8aYs1eSbdroEveW9oG14bsG05LHtlKTeEUG8eu9/kLcHkEFKIE8HRIkEYUKMyBBv2DVO16+jyfVQ4uZkwSaaLgvoXjjCSoEO5zZdCkZeEXiODS8JtSS/vD06EFkzwhTCbD+mrM/HWDbPKOo13dAoGARlbfm2fMGOt/lYXzYytYuEpZ+O+O8XmYF9DT7Tn5LiQVtL7jilas15vyuLj3r442UVuDkTc/ATQJV4I8JYFHRUSKtgi7ENPjLcbeGCbje6ujIpY+CCwUcZEw7edi8TORUq6H7WvQLCqXgONZbJB/wV+tGRiljl0ag4qu2NnEO+E=";
	private String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmapDZqfK1ARlAb6FiNnqi1IoeThRr408nnMzAL7wQiBY3OUHXE62OishktPwi5j/BmJ0mvex4H0/kAAV6j3kimQluC+Q2Y/bf8BWt04CMIvV8ZuJHyBByuZyrqSCt8X5JqcfQWOGIxnbYxfL83MB8iWYNgD4Za4ezhGwG7S/yuh5wSZ/AgLoHaEmU0H/mnAEK8DRUEtqsHDapjD8+cbaQyfF7y207BbrMWG50MMfNL7+Nh0wpP74XDL0WV4ZXIGLR1FjixPGBilKrz2zy1CDvBOTfRmsaQ7aQLKxcnIjB0kXaSuzt6IEpr1ZcIH4qNwkSEAOemU0Xvid2a9PGcnr5wIDAQAB";
	private String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAli9LHZw48fw76RPfeJnZjiOkTrO961Wn59IsoE8/CDChTwuUg6PMhyMWvzYlEHpp3C2S0bHQe/zH3+gLAcUBc05QYs6i6xKxWr1hquLLMCNXwqcTEQrF8J3yNpw/LhgIgWjCfJfQRqVBzQ7EeFe71LLyA5Izp7skF7LRi3MofuONOHvBs1+pZdFzYT/CHfNiKWKVCRgdtSwOn36jDbV5/Pnzd/mHAJJHQ+hBElI/006IHN51DEET86av+xoyNcZB+bTIlJ8Fe6kA0LRzYbOhRVMa2u7FwrSmtvK2B6QMTXz1GoUiPXYG59X4ToFa2JYEoUyOy3kvQFFt24U4awcUKQIDAQAB";
	
	
	
	//测试应用网关   成功返回success字符
	@RequestMapping(value = "/applicationGateway")
	public String testApplicationGateway(HttpServletRequest request){
		logger.info("testApplicationGateway URL is "+conversionRequestToString(request));
		/*Map<String,String> params = getParamsFromRequest(request);
		//支付宝公钥
		String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAli9LHZw48fw76RPfeJnZjiOkTrO961Wn59IsoE8/CDChTwuUg6PMhyMWvzYlEHpp3C2S0bHQe/zH3+gLAcUBc05QYs6i6xKxWr1hquLLMCNXwqcTEQrF8J3yNpw/LhgIgWjCfJfQRqVBzQ7EeFe71LLyA5Izp7skF7LRi3MofuONOHvBs1+pZdFzYT/CHfNiKWKVCRgdtSwOn36jDbV5/Pnzd/mHAJJHQ+hBElI/006IHN51DEET86av+xoyNcZB+bTIlJ8Fe6kA0LRzYbOhRVMa2u7FwrSmtvK2B6QMTXz1GoUiPXYG59X4ToFa2JYEoUyOy3kvQFFt24U4awcUKQIDAQAB";
		String charset = "utf-8";
		try {
			if(AlipaySignature.rsaCheckV1(params,publicKey,charset)){
				System.out.println("Check sign pass");
				return "success";
				
			}else{
				logger.info("Check sign fail");
				return "failure";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Check sign hava exceptions", e);
			return "failure";
		}*/
		
		
		
		
		//---按照文档上的代码示例进行测试-------------
		String charset = "gbk";
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
		    String name = (String) iter.next();
		    String[] values = (String[]) requestParams.get(name);
		    String valueStr = "";
		    for (int i = 0; i <values.length; i++) {
		        valueStr = (i == values.length - 1) ? valueStr + values[i]: valueStr + values[i] + ",";
		    }
		  //乱码解决，这段代码在出现乱码时使用。
		  try {
			  	valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
		  } catch (UnsupportedEncodingException e) {
			  	// TODO Auto-generated catch block
			  	e.printStackTrace();
		  }
		  params.put(name, valueStr);
		 }
		 //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
		 //boolean AlipaySignature.rsaCheckV1(Map<String, String>params, String publicKey, String charset, String sign_type)
		try {
			boolean flag = AlipaySignature.rsaCheckV1(params, alipayPublicKey, charset);
			if(flag){
				return "success";
			}else{
				return "fail";
			}
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "fail";
		}
	}
	
	//第三方应用授权回调
	@RequestMapping(value = "/thirdPartyAuthInfo")
	public void thirdPartyAuthInfo(String app_id,String app_auth_code,HttpServletRequest request){
		/*logger.info("Third-party application authorization callback URL is "+conversionRequestToString(request));
		//验证签名
		Map<String,String> params = getParamsFromRequest(request);
		String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApVvpNbuuJUMdT0VTR7UcXhnkazZ4gCYFGpOuMywOA6F9mrr9VGnGi739PQuQeVrPEXbi95JojAb8cwFeTcUKxw45cTdT+y96GtSIBFpYw2spGaktvRe/qSnze3oUW2t+NP0UUvap0iLEmbNjeCEMo0AdDJ94haPtgMajfiGk/MHsc9i8Ty9kNTY0qZSx34ty+KW0gK2y9Zoh8qOqWXreLE/9nMnqgSnnmUD9ZFyvuu0rUJ7PLACrov/K4sjkC77JuE9zo65TX8ragNTLO2HOF67KjIIF+n4kB+H648t9ohYLJ9LcaHaJI+8jUY5Y56U7zBbeIdo3nkGhlfHSMxiq8QIDAQAB";
		String charset = "utf-8";
		try {
			if(AlipaySignature.rsaCheckV1(params,publicKey,charset)){
				System.out.println("Check sign pass");
				
			}else{
				logger.info("Check sign fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Check sign hava exceptions", e);
		}*/
		logger.info("app_id = "+app_id+", app_auth_code = "+app_auth_code);
		//保存数据操作
	}
	
	
	//异步post返回门店审核状态信息
	@RequestMapping(value = "/asynReturnMarketShopAuditInfo")
	public void asynchronousReturnMarketShopAuditInfo (HttpServletRequest request) throws Exception{
		logger.info("asynchronous return market shop audit info begin ");
		//1,获取请求链接
		logger.info("Asynchronous return market shop audit info request URL is "+conversionRequestToString(request));
		//2,获取请求参数
		Map<String,String> params = getParamsFromRequest(request);
		//打印出参数信息
		for(String key:params.keySet()){
			logger.info("asynReturnMarketShopAuditInfo key = "+key+", value = "+params.get(key));
		}
		logger.info("asynchronous return market shop audit info over ");
		/*//验签
		String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApVvpNbuuJUMdT0VTR7UcXhnkazZ4gCYFGpOuMywOA6F9mrr9VGnGi739PQuQeVrPEXbi95JojAb8cwFeTcUKxw45cTdT+y96GtSIBFpYw2spGaktvRe/qSnze3oUW2t+NP0UUvap0iLEmbNjeCEMo0AdDJ94haPtgMajfiGk/MHsc9i8Ty9kNTY0qZSx34ty+KW0gK2y9Zoh8qOqWXreLE/9nMnqgSnnmUD9ZFyvuu0rUJ7PLACrov/K4sjkC77JuE9zo65TX8ragNTLO2HOF67KjIIF+n4kB+H648t9ohYLJ9LcaHaJI+8jUY5Y56U7zBbeIdo3nkGhlfHSMxiq8QIDAQAB";
		String charset = "utf-8";
		try {
			if(AlipaySignature.rsaCheckV1(params,publicKey,charset)){
				System.out.println("Check sign pass");
				
			}else{
				logger.info("Check sign fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Check sign hava exceptions", e);
		}*/
	}
	
	//换取应用授权令牌
	@RequestMapping(value = "/exchangeAuthToken")
	public void exchangeAuthToken() throws Exception {
		/*参数    start */
		//授权类型   authorization_code表示换取app_auth_token。 	refresh_token表示刷新app_auth_token
		String grant_type = "authorization_code";
		
		String code = in_scanner("请输入你得到的授权码code");
		//String code	= "af96e9fba1984ed0a63e33c50a64eX38";			//授权码
		/*参数    end */
		
		//实例请求对象
		AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", "utf-8", alipayPublicKey, "RSA2");
		//初始化请求对象实例
		AlipayOpenAuthTokenAppRequest request = new AlipayOpenAuthTokenAppRequest();
		JSONObject alipayJson = new JSONObject();
		alipayJson.put("grant_type", grant_type);
		alipayJson.put("code", code);
		request.setBizContent(alipayJson.toString());
		AlipayOpenAuthTokenAppResponse response = alipayClient.execute(request);
		
		if(response.getCode().equals(Alipay.RETURN_CODE_SUCCESS)){
			logger.info("ExchangeAuthToken run success,the authorization code is "+code);
			//测试，直接输出到控制台
			logger.info(response.getBody());
		}else{
			logger.info("ExchangeAuthToken run hava some exceptions");
			logger.info(response.getBody());
		}
	}
	
	//查询应用授权信息
	@RequestMapping(value = "/queryAuthTokenInfo")
	public void queryAuthTokenInfo() throws Exception{
		/*参数    start */
		
		/*商户授权令牌token*/
		String appAuthToken = in_scanner("请输入你得到的商户令牌token");
		//String appAuthToken = "201707BBb5e2e80785d643f4ac0c8d02a6f5fE38";
		/*参数    end */
		
		AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", "utf-8", alipayPublicKey, "RSA2");
		AlipayOpenAuthTokenAppQueryRequest request = new AlipayOpenAuthTokenAppQueryRequest();
		JSONObject alipayJson = new JSONObject();
		alipayJson.put("app_auth_token",appAuthToken);
		request.setBizContent(alipayJson.toString());
		
		AlipayOpenAuthTokenAppQueryResponse response = alipayClient.execute(request);
		if(response.getCode().equals(Alipay.RETURN_CODE_SUCCESS)){
			logger.info("QueryAuthToken run success,the appAuthToken is "+appAuthToken);
			logger.info(response.getBody());
		}else{
			logger.info("ExchangeAuthToken run hava some exceptions");
			logger.info(response.getBody());
		}
		
	}
	
	//上传商户申请门店的材料图片
	@RequestMapping(value = "/uploadMarketShopImage")
	public void uploadMarketShopImage() throws Exception {
		/*参数    start */
		/*商户授权令牌token*/
		//String appAuthToken = in_scanner("请输入你得到的商户令牌token");
		String appAuthToken = "201707BBdd12c278f9514aa8b13a798c84093B88";
		
		/*商户资料的图片信息*/
		String imageType = "jpg";
		String imageName ="interior1";
		//String imageName = in_scanner("请输入上传后的图片名称,类似于梧桐水疗馆_门头");
		String address = "D:/something_for_liangchao/wutongshuiliao/interior1.jpg";
		//String address = in_scanner("请输入图片所在地址 ，类似于 C:/Users/Administrator/Desktop/工作/支付宝对接开发/创建门户/店大门.jpg ");
		FileItem imageContent =new FileItem(address);//图片的二进制信息
		
		/*参数    end */
		
		
		//实例请求对象
		AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", "utf-8", alipayPublicKey, "RSA2");
		//初始化请求对象实例
		AlipayOfflineMaterialImageUploadRequest request = new AlipayOfflineMaterialImageUploadRequest();
		//AppAuthToken的值获取方式参考第三方应用授权
		request.putOtherTextParam("app_auth_token", appAuthToken);
		request.setImageType(imageType);
		request.setImageName(imageName);
		request.setImageContent(imageContent);
		//初始化返回对象实例并获取返回信息
	    AlipayOfflineMaterialImageUploadResponse response = alipayClient.execute(request);
	    
	    
	    if (response.getCode().equals(Alipay.RETURN_CODE_SUCCESS)) {
	    	logger.info("Upload merchant material image Success,the image local address is "+ address);
	    	//图片/视频在商家中心的唯一标识
	    	String image_id = response.getImageId();
	    	//图片/视频的访问地址
	    	String image_url = response.getImageUrl();
	    	logger.info("image_id is "+ image_id +", image_url is "+image_url);
	    	logger.info(response.getBody());
	    }else{
	    	logger.info("Upload merchant material image fail");
	    }
	    logger.info(response.getBody());
	}
	
	//门店类目配置查询
	@RequestMapping(value = "/queryMarketShopCategory")
	public void queryMarketShopCategory()throws Exception{
		
		//请求参数
		//String category_id = "";	//类目ID，如果为空则查询全部类目
		String op_role = "ISV";		//表示接口业务的调用方身份,默认不填标识为ISV。     //测试下如果为空的时候，是什么反应
		/*参数    end */
		
		
		//实例请求对象
		AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", "utf-8", alipayPublicKey, "RSA2");
		//需要导入最新的ali包
		AlipayOfflineMarketShopCategoryQueryRequest request = new AlipayOfflineMarketShopCategoryQueryRequest();
		JSONObject alipayJson = new JSONObject();
		//alipayJson.put("category_id", category_id);
		alipayJson.put("op_role", op_role);
		request.setBizContent(alipayJson.toString());
		AlipayOfflineMarketShopCategoryQueryResponse response = alipayClient.execute(request);
		
		if(response.isSuccess()){
			logger.info("Query Market Shop Category Success");
		}else{
			logger.info("Query Market Shop Category Fail");
		}
		logger.info(response.getBody());
	}
	
	
	//创建商户门店
	@RequestMapping(value = "/createMarketShop")
	public void createMarketShop() throws Exception{
		/*参数     */
		/*商户授权令牌token*/
		//String appAuthToken = in_scanner("请输入你的商户授权的token");
		String appAuthToken = "201707BBdd12c278f9514aa8b13a798c84093B88";
		
		/*门店信息  api中声明必填的*/
		String store_id= "wtsl_03";					//外部门店编号
		String category_id = "2015090700039570";	//类目id
		String main_shop_name ="梧桐水疗体验馆";			//主门店名
		String province_code = "310000";	//省份编码
		String city_code = "310100";  //城市编码
		String district_code = "310115";	//区县编码
		String address = "上海市浦东新区纳贤路800号3栋";	//门店详细地址
		String longitude = "121.604848";	//经度
		String latitude = "31.178455";	//纬度
		String contact_number = "13816111195";		//门店电话号码
		//String main_image = in_scanner("请输入门店首图的上传后的id");
		String main_image = "p-hWXtrbRkO3M2BN4ALLmwAAACMAAQQD";		//门店首图
		String audit_images = "p-hWXtrbRkO3M2BN4ALLmwAAACMAAQQD,_JfTjUEMRo-hHbKfJZqj3gAAACMAAQQD,mlO8SZKUSOKMpEuzj-jQsAAAACMAAQQD,Xie9gMXBSHS5w9ivu4vazAAAACMAAQQD";	//门店审核时需要的图片,至少包含一张门头照片，两张内景照片,多个图片之间以英文逗号分隔。
		String operate_notify_url = "http://flipped1212.iok.la:41746/flypayfx/payment/asynReturnMarketShopAuditInfo";	//当商户的门店审核状态发生变化时，会向该地址推送消息。
		String isv_uid = (String) ApplicatonStaticUtil.getAppStaticData("alipay.isv.pid");	//ISV返佣id
		String request_id = "2017072435324542";		//不允许重复
		String op_role = "ISV";		//表示以系统集成商的身份开店
		String biz_version = "2.0";	//店铺接口业务版本号   新接入的ISV，请统一传入2.0。
		
		/*参数    end */
		
		//实例请求对象
		AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", "utf-8", alipayPublicKey, "RSA2");
		//初始化请求对象实例
		AlipayOfflineMarketShopCreateRequest request = new AlipayOfflineMarketShopCreateRequest();
		//AppAuthToken的值获取方式参考第三方应用授权
		request.putOtherTextParam("app_auth_token", appAuthToken);
		
		
		JSONObject alipayJson = new JSONObject();
		//ISV返佣id
		alipayJson.put("store_id", store_id);
		alipayJson.put("category_id", category_id);
		alipayJson.put("main_shop_name", main_shop_name);
		alipayJson.put("province_code", province_code);
		alipayJson.put("city_code", city_code);
		alipayJson.put("district_code", district_code);
		alipayJson.put("address", address);
		alipayJson.put("longitude", longitude);
		alipayJson.put("latitude", latitude);
		alipayJson.put("contact_number", contact_number);
		alipayJson.put("main_image", main_image);
		alipayJson.put("audit_images", audit_images);
		alipayJson.put("operate_notify_url", operate_notify_url);
		alipayJson.put("isv_uid", isv_uid);
		alipayJson.put("request_id", request_id);
		alipayJson.put("op_role", op_role);
		alipayJson.put("biz_version", biz_version);
		request.setBizContent(alipayJson.toJSONString());
		request.setBizContent(alipayJson.toString());
		
		
		AlipayOfflineMarketShopCreateResponse response = alipayClient.execute(request);
		System.out.print(response.getBody());
		if(response.getCode().equals(Alipay.RETURN_CODE_SUCCESS)){
			System.out.print(response.getBody());
			logger.info("CreateMarketShop run success,the store_id(External store number) is "+ store_id);
			logger.info("The apply_id is "+response.getApplyId()); 	//申请流水id，用来手动查询审核状态
		}else{
			logger.info("CreateMarketShop run hava some exceptions");
			logger.info(response.getBody());
		}
		
		
	}
	
	
	//查询单个商户门店信息
	@RequestMapping(value = "/queryMarketShopDetail")
	public void queryMarketShopDetail() throws Exception{
		/*参数*/
		
		/*参数  end*/
		
		//实例请求对象
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, privateKey, "json", "utf-8", alipayPublicKey, "RSA2"); 
		//初始化请求对象实例 
		AlipayOfflineMarketShopQuerydetailRequest request = new AlipayOfflineMarketShopQuerydetailRequest();
		//AppAuthToken的值获取方式参考第三方应用授权    
		request.putOtherTextParam("app_auth_token", "201707BBdd12c278f9514aa8b13a798c84093B88");
		JSONObject alipayJson = new JSONObject();
		//支付宝门店Id
		String shop_id = in_scanner("请输入支付宝门店的id");
		//String shop_id ="";
		
		
		alipayJson.put("shop_id", shop_id);
		request.setBizContent(alipayJson.toString());
		AlipayOfflineMarketShopQuerydetailResponse response = alipayClient.execute(request);
		if(response.getCode().equals(Alipay.RETURN_CODE_SUCCESS)){
			logger.info("QueryMarketShopDetail run success,the shop_id is "+ shop_id);
		}else{
			logger.info("QueryMarketShopDetail run fail");
		}
		System.out.print(response.getBody());
	}
	
	
	//业务流水批量查询接口
	@RequestMapping(value = "/bathQueryApplyOrder")
	public void bathQueryApplyOrder() throws Exception{
		/*参数*/
		//一定要传入app_auth_token         业务主体id  biz_id 就是 shop_id
		/*参数  end*/
		try {
			AlipayClient alipayClient = new DefaultAlipayClient(serverUrl,appId,privateKey,"json","utf-8",alipayPublicKey,"RSA2");
			AlipayOfflineMarketApplyorderBatchqueryRequest request = new AlipayOfflineMarketApplyorderBatchqueryRequest();
			JSONObject alipayJson = new JSONObject();
			//String[] apply_ids = {"2017072500107000000064746245"}; //支付宝流水ID
			//alipayJson.put("apply_ids", apply_ids);
			
			String biz_type = "SHOP";	//业务类型：SHOP-店铺，ITEM-商品
			alipayJson.put("biz_type", biz_type);
			
			//String op_id = "2088902100189888";	//操作用户的支付账号id
			//alipayJson.put("op_id", op_id);
			
			String op_role = "ISV";
			alipayJson.put("op_role", op_role);
			
			String start_time = "2017-06-27 10:51:57";
			String end_time = "2017-07-27 10:51:57";
			
			alipayJson.put("start_time", start_time);
			alipayJson.put("end_time", end_time);
			
			//打印请求参数
			System.out.println(alipayJson.toJSONString());
			
			request.setBizContent(alipayJson.toString());
			request.putOtherTextParam("app_auth_token", "201707BBdd12c278f9514aa8b13a798c84093B88");
			/*request.setBizContent("{" +
					"\"apply_ids\":[" +
					"\"2017072500107000000064746245\"" +
					"]," +
					"\"biz_type\":\"SHOP\"," +
					"\"biz_id\":\"2017072500077000000044103882\"," +
					"\"op_role\":\"ISV\"" +
					"}");*/
			AlipayOfflineMarketApplyorderBatchqueryResponse response = alipayClient.execute(request);
			if(response.getCode().equals(Alipay.RETURN_CODE_SUCCESS)){
				System.out.print(response.getBody());
				System.out.println(response.getBizOrderInfos());
			}
			System.out.print(response.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	//获取请求中的参数  Map<String,String>
	private Map<String,String> getParamsFromRequest(HttpServletRequest request){
		Map<String, String[]> params = request.getParameterMap();
		Map<String,String> params2 = new HashMap<String, String>();
		for (String key : params.keySet()) {
			String[] values = params.get(key);
			params2.put(key, values[0]);
		}
		return params2;
	}
	
	//公用测试输入变化参数
	private String in_scanner(String name){
		Scanner sc = new Scanner(System.in);
		System.out.println(name);
		String sys_in = sc.nextLine();
		return sys_in;
	}
	
	
	//获取请求url
	private String conversionRequestToString(HttpServletRequest request){
		Map<String, String[]> params = request.getParameterMap();
		String queryString = "";
		for (String key : params.keySet()) {
			String[] values = params.get(key);
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				queryString += key + "=" + value + "&";
			}
		}
		// 去掉最后一个空格
		queryString = queryString.substring(0, queryString.length() - 1);
		String resultUrl = "POST " + request.getRequestURL() + "?" + queryString;
		return resultUrl;
	}
	
}
