package com.cn.flypay.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.flypay.model.payment.request.FundOutRequest;
/**
 * Created by zhoujifeng1 on 16/8/2.
 */
public class ApiUtil {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * get 请求，url为完整的链接地址
     */
    public String doGet(String url) {

        String resp = "";

        HttpClient client = null;
        GetMethod method = null;
        try {
            client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            method = new GetMethod(url);
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                logger.error("发起HTTP GET请求失败,返回值：{}, url:{} ", statusCode, url);
            }
            resp = method.getResponseBodyAsString();

        } catch (Exception e) {
//            logger.error("发起HTTP GET请求异常,返回值：{}, url:{} ", new Object[] { method.getStatusLine(), url }, e);
            e.printStackTrace();
        } finally {
            if (method != null)
                method.releaseConnection();
        }
        logger.info("发起HTTP GET请求url:{},返回信息：{} ", url, resp);
        return resp;
    }

    /**
     * Post 请求，url为请求路径，str为请求参数
     */
    public String doPost(String url, String str) {
        String resp = "";

        HttpClient client = null;
        PostMethod method = null;

        try {
            client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            method = new PostMethod(url);
            method.setRequestEntity(new StringRequestEntity(str, "application/json", "UTF-8"));

            method.setRequestHeader("Accept", "*/*");
            method.setRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; MOBIM)");
            method.setRequestHeader("Content-Length", String.valueOf(str.getBytes().length));
            method.setRequestHeader("Content-type", "application/json; charset=utf-8");

            client.executeMethod(method);

            int httpStatusCode = method.getStatusCode();
            resp = method.getResponseBodyAsString();

            if (httpStatusCode != HttpStatus.SC_OK) {
                logger.error("发起HTTP POST 请求失败,返回值：{}, url:{} ", httpStatusCode, url);
            }

        } catch (Exception e) {
            logger.error("发起HTTP POST 请求异常,返回值：{}, url:{} ", new Object[] { method.getStatusLine(), url }, e);
            e.printStackTrace();
        } finally {
            if (method != null)
                method.releaseConnection();
        }
        logger.info("发起HTTP POST 请求url:{}?{}, 返回内容：{}", new String[] { url, str, resp });
        return resp;
    }

    public  String  onlinePost(String url , FundOutRequest tClass){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,20000);
        HttpPost post = new HttpPost(url);
        HttpResponse response = null;
        String rsp = "";
        try{
            String str = JsonUtil.fromObject(tClass);
            System.out.println(str);
            StringEntity seReq = new StringEntity(str , "utf-8");
            post.setEntity(seReq);
             response = httpClient.execute(post);
             rsp = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode != HttpStatus.SC_OK){
                post.abort();
                throw new Exception("HttpClient,error status code :" +statusCode);
            }

        }catch(Exception e){
            logger.error("发起HTTP POST 请求异常,返回值：{}, url:{} ", new Object[] { response.getStatusLine(), url }, e);
        }finally {
            httpClient.getConnectionManager().shutdown();
        }
        logger.info("发起HTTP POST ,返回值：{},",rsp);
        return rsp;

    }
}
