package com.cn.flypay.service.payment.impl;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PinganFeedbackServerImpl {
	public static void main(String[] args) throws Exception {
		int port = 8180;
		ServerSocket server = new ServerSocket(port);
		// 自定端口，创建服务端Socket
		System.out.println("开始监听，端口号：" + port);
		while (true) {
			Socket client = server.accept();// 监听获取客户端请求socket，accept方法是一个阻塞方法，在客户端和服务端之间建立联系之前一直等待阻塞
			System.out.println(client.getInetAddress());
			// 返回客户端地址
			byte requestbuffer[] = new byte[4096];// 准备读取客户端请求的数据，读取数据保存在buffer数组

			InputStream inp = client.getInputStream();
			int length = inp.read(requestbuffer, 0, requestbuffer.length);
			String requestString = new String(requestbuffer, "UTF-8");
			System.out.println(requestString);// 返回请求数据

		}

	}
}