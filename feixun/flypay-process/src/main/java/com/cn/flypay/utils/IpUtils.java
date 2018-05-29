package com.cn.flypay.utils;

import javax.servlet.http.HttpServletRequest;

public class IpUtils {
	public static String getIpAddress(HttpServletRequest request) throws Exception {
		String ip = request.getHeader("X-Real-IP");
		if ((!StringUtil.isBlank(ip)) && (!"unknown".equalsIgnoreCase(ip))) {
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		if ((!StringUtil.isBlank(ip)) && (!"unknown".equalsIgnoreCase(ip))) {
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index);
			}
			return ip;
		}

		return request.getRemoteAddr().equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}
}