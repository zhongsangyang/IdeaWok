package com.cn.flypay.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * xml和map相互转换工具
 * 
 * @author felix.wu
 * 
 */
public class XmlMapper {
	public static String map2Xml(Map<String, String> map) {
		Document document = DocumentHelper.createDocument();
		Element nodeElement = document.addElement("xml");
		for (String key : map.keySet()) {
			if (map.get(key) != null) {
				Element keyElement = nodeElement.addElement(key);
				keyElement.setText(String.valueOf(map.get(key)));
			}
		}
		return map2String(document);
	}

	public static String map2Xml(Map<String, String> map, String rootName) {
		Document document = DocumentHelper.createDocument();
		Element nodeElement = document.addElement(rootName);
		for (String key : map.keySet()) {
			if (map.get(key) != null) {
				Element keyElement = nodeElement.addElement(key);
				keyElement.setText(String.valueOf(map.get(key)));
			}
		}
		return map2String(document);
	}

	public static String mapObject2Xml(Map<String, Object> map, String rootName) throws UnsupportedEncodingException {
		Document document = DocumentHelper.createDocument();
		Element nodeElement = document.addElement(rootName);
		for (String key : map.keySet()) {
			if (map.get(key) != null) {
				Element keyElement = nodeElement.addElement(key);
				String t = (String) map.get(key);
				keyElement.setText(t);
			}
		}
		String s = "";
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputFormat format = new OutputFormat(" ", true, "GBK");
			XMLWriter writer = new XMLWriter(out, format);
			writer.write(document);
			s = out.toString("GBK");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return s;
	}

	public static String map2Xml(Map<String, String[]> map, Boolean isMul) {
		Document document = DocumentHelper.createDocument();
		Element nodeElement = document.addElement("xml");
		for (String key : map.keySet()) {
			if (map.get(key) != null && key.length() > 0) {
				Element keyElement = nodeElement.addElement(key);
				keyElement.setText(String.valueOf(map.get(key)[0]));
			}
		}

		return map2String(document);
	}

	public static Map<String, String> xml2Map(String xml) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			if (StringUtil.isNotBlank(xml)) {
				Document document = DocumentHelper.parseText(xml);
				Element nodesElement = document.getRootElement();
				for (Object node : nodesElement.elements()) {
					Element elm = (Element) node;
					if (elm.elements() != null && elm.elements().size() > 0) {
						for (Object node2 : elm.elements()) {
							Element elm2 = (Element) node2;
							map.put(elm2.getName(), elm2.getText());
						}
					} else {
						map.put(elm.getName(), elm.getText());
					}
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String xml = "<?xml version=\"1.0\" encoding=\"GBK\" ?><Result><OrderNumber>16081515450200000001</OrderNumber><BussFlowNo>71608156335878</BussFlowNo></Result>";
		Map<String, String> map = xml2Map(xml);
		System.out.println(map.get("OrderNumber"));
	}

	private static String map2String(Document document) {
		String s = "";
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputFormat format = new OutputFormat(" ", true, "UTF-8");
			XMLWriter writer = new XMLWriter(out, format);
			writer.write(document);
			s = out.toString("UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return s;
	}
}
