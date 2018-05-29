package com.cn.flypay.service.common.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.service.common.CommonService;
import com.cn.flypay.service.sys.UserService;
import com.cn.flypay.utils.DateUtil;
import com.cn.flypay.utils.StringUtil;
import com.cn.flypay.utils.XmlMapper;
import com.cn.flypay.utils.pingan.MsgInfoDESUtil;

@Service
public class CommonServiceImpl implements CommonService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserService userService;

	@Override
	public JSONObject getRequstBody(HttpServletRequest request) throws Exception {
		String info = getBodyFromRequst(request);
		JSONObject json = new JSONObject();
		if (info.startsWith("<")) {
			Map<String, String> xml = XmlMapper.xml2Map(info);
			json.putAll(xml);
		} else if (info.startsWith("{")) {
			json = JSON.parseObject(info);
			if (json.containsKey("flypayContent") && StringUtil.isNotBlank(json.getString("flypayContent"))) {
				String dec = null;
				String type = json.getString("type");
				if(type==null){
					dec = URLDecoder.decode(MsgInfoDESUtil.DecryptDoNet(json.getString("flypayContent")), "utf-8");
				}else{
					HttpSession session = request.getSession();
					session.setAttribute("TypeDes", type);
					dec = URLDecoder.decode(MsgInfoDESUtil.DecryptDoNetTwo(json.getString("flypayContent")), "utf-8");
				}
				json = JSON.parseObject(dec);
			}
		} else {
			json.put("body", info);
		}
		logger.info(json.toJSONString());
		return json;
	}

	@Override
	public JSONObject getEncryptBody(JSONObject json) {
		try {
			logger.info(json.toJSONString());
			String encryptBody = null;
			HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest(); 
			HttpSession session = request.getSession();
			String type = (String)session.getAttribute("TypeDes");
			if(type==null){
				encryptBody = MsgInfoDESUtil.EncryptAsDoNet(URLEncoder.encode(json.toJSONString(), "utf-8"));
			}else{
				encryptBody = MsgInfoDESUtil.EncryptAsDoNetTwo(URLEncoder.encode(json.toJSONString(), "utf-8"));
			}
			
			JSONObject newJson = new JSONObject();
			newJson.put("flypayContent", encryptBody);
			return newJson;
		} catch (Exception e) {
			logger.error("加密失败", e);
		}
		return json;
	}

	@Override
	public String getBodyFromRequst(HttpServletRequest request) throws Exception {
		return getBodyFromRequst(request, "UTF-8");
	}

	@Override
	public String getBodyFromRequst(HttpServletRequest request, String encoding) throws Exception {
		InputStream inputStream = request.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encoding);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		StringBuilder buffer = new StringBuilder();
		String str;
		while ((str = bufferedReader.readLine()) != null) {
			buffer.append(str);
		}
		bufferedReader.close();
		inputStreamReader.close();
		inputStream.close();
		return buffer.toString();
	}

	@Override
	public String getUniqueOrderByType(String transType, Long userId) {
		String out_trade_no = transType + DateUtil.convertCurrentDateTimeToString()
				+ StringUtils.leftPad(String.valueOf(RandomUtils.nextInt(1000)), 3, "0")
				+ StringUtils.leftPad(userId.toString(), 10, "0");
		return out_trade_no;
	}

	@Override
	public String getUniqueOrderByUserId(Long userId) {
		String out_trade_no = DateUtil.getDateTime("yyMMddHHmmss", new Date())
				+ StringUtils.leftPad(userId.toString(), 6, "0")
				+ StringUtils.leftPad(String.valueOf(RandomUtils.nextInt(100)), 2, "0");

		return out_trade_no;
	}

	@Override
	public String getUniqueTradeSn() {
		return get20LengthTimeMillisSequence();
	}

	private String get20LengthTimeMillisSequence() {
		long nanoTime = System.nanoTime();
		String preFix = "";
		Long max = 999999999999999l;
		if (nanoTime < 0) {
			preFix = "A";// 负数补位A保证负数排在正数Z前面,解决正负临界值(如A9223372036854775807至Z0000000000000000000)问题。
			nanoTime = nanoTime + max + 1;
		} else {
			preFix = "B";
		}
		String nanoTimeStr = String.valueOf(nanoTime);

		int difBit = String.valueOf(Long.MAX_VALUE).length() - nanoTimeStr.length();
		for (int i = 0; i < difBit; i++) {
			preFix = preFix + "0";
		}
		nanoTimeStr = preFix + nanoTimeStr;
		return StringUtils.leftPad(nanoTimeStr, 20, "0");
	}
}
