package com.cn.flypay.utils.gazhi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
/**
 * 嘎吱网络 Http请求
 * @author liangchao
 *
 */
public class GaZhiHttpReqUtils {

	/**
	 * 以post方式发送http请求，并带请求参数
	 * @param url   发送目的地
	 * @param parameters    请求参数
	 */
	public static String httpPostRequest(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer result;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			System.out.println("发送HTTP请求报文地址:[{"+url+"}]内容:[{"+param+"}]");
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
			String line;
			result = new StringBuffer();
			while ((line = in.readLine()) != null)
				result.append(line);
			System.out.println("接受返回信息:{"+result+"}");
			return result.toString();
		} catch (Exception e) {
			System.out.println("异常");
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

}
