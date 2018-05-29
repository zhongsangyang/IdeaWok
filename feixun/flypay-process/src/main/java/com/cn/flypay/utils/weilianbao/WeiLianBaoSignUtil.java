package com.cn.flypay.utils.weilianbao;

import java.util.Map;

import com.cn.flypay.utils.StringUtil;

public class WeiLianBaoSignUtil {

	public static String signMd5(String source) {
		System.out.println("原报文串source===============" + source);
		String md5Value = Disguiser.disguiseMD5(source);
		return md5Value;
	}

	public static String signMd5(Map<String, String> map,String signKey) {
		StringBuilder builder = new StringBuilder();
		
		for (String key : map.keySet()) {
			String val = (String)map.get(key);
			if(StringUtil.isEmpty(val)) {
				continue;
			}
			builder.append("#").append(val);
		}
		builder.append("#").append(signKey);
		String source = builder.toString();
		return signMd5(source);
	}
}
