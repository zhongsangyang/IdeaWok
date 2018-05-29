package com.cn.flypay.pageModel.payment.minsheng;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.cn.flypay.utils.XmlMapper;

/**
 * 商户入驻
 * 
 * @author sunyue
 * 
 */
public abstract class CommonSMZF {

	private String requestId;

	private String callBack;

	private String cooperator;

	public abstract String getOperateName();

	public static String getSMZFRequestMessage(CommonSMZF T) throws DocumentException {
		JSONObject jb = (JSONObject) JSONObject.toJSON(T);
		Map<String, String> map = JSONObject.toJavaObject(jb, Map.class);
		Document body = DocumentHelper.parseText(XmlMapper.map2Xml(map, "body"));

		Element xml = DocumentHelper.createElement("merchant");
		Document document = DocumentHelper.createDocument(xml);
		Element headEle = xml.addElement("head");
		headEle.addElement("version").setText("1.0.0");
		headEle.addElement("msgType").setText("01");
		headEle.addElement("reqDate").setText(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
		xml.add(body.getRootElement());
		return document.asXML();
	}

	public String getCooperator() {
		return cooperator;
	}

	public void setCooperator(String cooperator) {
		this.cooperator = cooperator;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getCallBack() {
		return callBack;
	}

	public void setCallBack(String callBack) {
		this.callBack = callBack;
	}

}
