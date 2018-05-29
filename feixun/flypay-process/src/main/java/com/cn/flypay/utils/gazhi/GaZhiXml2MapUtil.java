package com.cn.flypay.utils.gazhi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 嘎吱网络  xml与Mpa转化
 * @author liangchao
 *
 */
public class GaZhiXml2MapUtil {
	public static Map<String, String> xml2map(String xml) throws DocumentException {
		if (StringUtils.isBlank(xml))
			return null;
		Map<String, String> map = new HashMap<String, String>(1);
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		xml2map(map, root);
		return map;
	}

	private static void xml2map(Map<String, String> map, Element element) {
		@SuppressWarnings("unchecked")
		List<Element> list = element.elements();
		if (list.size() != 0) {
			for (Element ele : list) {
				xml2map(map, ele);
			}
		} else {
			map.put(element.getName(),element.getTextTrim());
		}
	}
}
