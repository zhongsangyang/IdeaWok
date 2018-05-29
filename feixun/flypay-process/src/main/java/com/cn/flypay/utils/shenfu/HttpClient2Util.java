package com.cn.flypay.utils.shenfu;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClient2Util {
	public static String doPost(String url, byte[] content, String contentType) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new ByteArrayEntity(content));
		if (contentType != null) {
			httpPost.setHeader("Content-type", contentType);
		}
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
			HttpEntity entityResponse = httpResponse.getEntity();
			int contentLength = (int) entityResponse.getContentLength();
			if (contentLength <= 0) {
				throw new IOException("No response");
			}

			byte[] respBuffer = new byte[contentLength];
			if (entityResponse.getContent().read(respBuffer) != respBuffer.length) {
				throw new IOException("Read response buffer error");
			}

			String res = new String(respBuffer);
			System.out.println("res122:" + res);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpResponse.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		return null;
	}

	public static String doPost(String url, Map<String, Object> maps, String contentType) {
		ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objOs = new ObjectOutputStream(byteOs);
			objOs.writeObject(maps);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byteOs.toByteArray();
		return doPost(url, byteOs.toByteArray(), contentType);
	}

}
