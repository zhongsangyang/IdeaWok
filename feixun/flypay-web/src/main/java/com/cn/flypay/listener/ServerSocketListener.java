package com.cn.flypay.listener;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cn.flypay.service.payment.PingAnExpenseService;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.pingan.PingAnConfig;

public class ServerSocketListener implements ServletContextListener {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		int ping_feed_back_port = 8180;
		try {
			ServerSocket server = new ServerSocket(ping_feed_back_port);
			logger.info("开始监听，端口号：" + ping_feed_back_port);
			while (true) {
				Socket client = server.accept();// 监听获取客户端请求socket，accept方法是一个阻塞方法，在客户端和服务端之间建立联系之前一直等待阻塞
				logger.info(client.getInetAddress().getHostName());
				// 返回客户端地址
				byte requestbuffer[] = new byte[4096];// 准备读取客户端请求的数据，读取数据保存在buffer数组

				InputStream inp = client.getInputStream();
				int length = inp.read(requestbuffer, 0, requestbuffer.length);
				String requestString = new String(requestbuffer, PingAnConfig.encoding);
				if (StringUtil.isNotBlank(requestString)) {
					logger.info(requestString);// 返回请求数据
					try {
						WebApplicationContext context = WebApplicationContextUtils
								.getRequiredWebApplicationContext(arg0.getServletContext());
						PingAnExpenseService pingAnPayService = (PingAnExpenseService) context
								.getBean("pingAnExpenseService");
						String result = requestString.substring(93, 143).trim();
						if (result.contains("成功")) {
							String body = requestString.substring(requestString.indexOf("<Result>"));
							pingAnPayService.dealFile04FeedBack(body);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ServerSocketListener l = new ServerSocketListener();
		l.contextInitialized(null);
	}
}