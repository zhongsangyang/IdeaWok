package com.cn.flypay.model.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * JSON工具类（过滤null字段，可指定日期格式）
 * 
 * @author alex
 */
public class JSON {
	/**
	 * 标准日期格式
	 */
	public static final String DEFAULT_DATE_PATTERN = "yyyyMMddHHmmss";

	/**
	 * 默认实现 (java.util.Date使用标准日期格式的字符串表示)
	 */
	private static JSON DEFAULT = build(DEFAULT_DATE_PATTERN, null, false);

	/**
	 * 默认下划线命名策略实现
	 */
	private static JSON DEFAULT_WITH_SNAKE_CASE_NAME = build(DEFAULT_DATE_PATTERN,
			new PropertyNamingStrategy.SnakeCaseStrategy(), false);

	/**
	 * 默认全下划线命令策略实现
	 */
	private static JSON DEFAULT_WITH_FULL_SNAKE_CASE_NAME = build(DEFAULT_DATE_PATTERN,
			new PropertyNamingStrategy.SnakeCaseStrategy(), true);

	/**
	 * JACKSON转换器
	 */
	private ObjectMapper jacksonMapper;

	/**
	 * 格式化jackson转换器
	 */
	private ObjectMapper prettyJacksonMapper;

	/**
	 * 显示类型jackson转换器
	 */
	private ObjectMapper explictTypeJacksonMapper;

	/**
	 * 首字母大写JACKSON转换器
	 */
	private ObjectMapper upperCamelCaseJacksonMapper;

	/**
	 * 构造函数
	 */
	private JSON() {
	}

	/**
	 * 获得默认实现
	 * 
	 * @return
	 */
	public static JSON getDefault() {
		return DEFAULT;
	}

	/**
	 * 获得默认的下划线命名实现
	 *
	 * @return
	 */
	public static JSON getDefaultWithSnakeCaseName() {
		return DEFAULT_WITH_SNAKE_CASE_NAME;
	}

	public static JSON getDefaultWithFullSnakeCaseName() {
		return DEFAULT_WITH_FULL_SNAKE_CASE_NAME;
	}

	/**
	 * 指定日期格式并构建JSON对象
	 * 
	 * @param datePattern
	 *            日期格式
	 */
	public static JSON newInstance(String datePattern) {
		if (DEFAULT_DATE_PATTERN.equals(datePattern)) {
			return DEFAULT;
		}

		return build(datePattern, null, false);
	}

	private static ObjectMapper buildMapper(String datePattern) {
		ObjectMapper jacksonMapper = new ObjectMapper();
		jacksonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		jacksonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		jacksonMapper.setSerializationInclusion(Include.NON_NULL);
		if (datePattern != null) {
			DateFormat dateFormat = new SimpleDateFormat(datePattern);
			jacksonMapper.setDateFormat(dateFormat);
		}

		return jacksonMapper;
	}

	/**
	 * 构造JSON对象
	 * 
	 * @param datePattern
	 *            日期格式
	 */
	private static JSON build(String datePattern, PropertyNamingStrategy propertyNamingStrategy, boolean supportMap) {
		JSON json = new JSON();

		json.jacksonMapper = buildMapper(datePattern);

		json.prettyJacksonMapper = buildMapper(datePattern);
		json.prettyJacksonMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		json.explictTypeJacksonMapper = buildMapper(datePattern);
		json.explictTypeJacksonMapper.enableDefaultTyping(DefaultTyping.NON_FINAL);

		json.upperCamelCaseJacksonMapper = buildMapper(datePattern);
		json.upperCamelCaseJacksonMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);

		if (propertyNamingStrategy != null) {
			json.jacksonMapper.setPropertyNamingStrategy(propertyNamingStrategy);
			json.prettyJacksonMapper.setPropertyNamingStrategy(propertyNamingStrategy);
			json.explictTypeJacksonMapper.setPropertyNamingStrategy(propertyNamingStrategy);
			json.upperCamelCaseJacksonMapper.setPropertyNamingStrategy(propertyNamingStrategy);
		}

		if (supportMap) {
			SimpleModule simpleModule = new SimpleModule();
			simpleModule.addKeySerializer(String.class, new SnakeCaseKeySerializer());
			json.jacksonMapper.registerModule(simpleModule);
			json.prettyJacksonMapper.registerModule(simpleModule);
			json.explictTypeJacksonMapper.registerModule(simpleModule);
			json.upperCamelCaseJacksonMapper.registerModule(simpleModule);
		}

		return json;
	}

	/**
	 * 将对象转换为JSON字符串
	 * 
	 * @param obj
	 * @return
	 */
	public String toJSONString(Object obj) {
		try {
			return jacksonMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("To json string error", e);
		}
	}

	public byte[] toJSONBytes(Object obj) {
		try {
			return jacksonMapper.writeValueAsBytes(obj);
		} catch (Exception e) {
			throw new RuntimeException("To json bytes error", e);
		}
	}

	/**
	 * 将对象写入JSON流。
	 * 
	 * @param out
	 * @param obj
	 */
	public void writeObjectToJSONStream(OutputStream out, Object obj) {
		try {
			jacksonMapper.writeValue(out, obj);
		} catch (Exception e) {
			throw new RuntimeException("To json bytes error", e);
		}
	}

	/**
	 * 将对象转换为格式化可读的JSON字符串
	 * 
	 * @param obj
	 * @return
	 */
	public String toPrettyJSONString(Object obj) {
		try {
			return prettyJacksonMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("To json string error", e);
		}
	}

	/**
	 * 转成显示类型的JSON字符串
	 * 
	 * @param obj
	 * @return
	 */
	public String toExplictJSONString(Object obj) {
		try {
			return explictTypeJacksonMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("to explict json string error", e);
		}
	}

	/**
	 * 转成属性名称首字母大写的JSON字符串
	 * 
	 * @param obj
	 * @return
	 */
	public String toUpperCamelCaseJSONString(Object obj) {
		try {
			return upperCamelCaseJacksonMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("to explict json string error", e);
		}
	}

	/**
	 * 将JSON字符串转换为对象
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public <T> T parseToObject(String json, Class<T> clazz) {
		try {
			return jacksonMapper.readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Parse json str to object error", e);
		}
	}

	/**
	 * 从JSON流中读取对象
	 * 
	 * @param in
	 * @param clazz
	 * @return
	 */
	public <T> T readObjectFromJSONStream(InputStream in, Class<T> clazz) {
		try {
			return jacksonMapper.readValue(in, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Parse json str to object error", e);
		}
	}

	/**
	 * 将JSON字节串转换为对象
	 * 
	 * @param jsonData
	 * @param offset
	 * @param length
	 * @param clazz
	 * @return
	 */
	public <T> T parseToObject(byte[] jsonData, int offset, int length, Class<T> clazz) {
		try {
			return jacksonMapper.readValue(jsonData, offset, length, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Parse json str to object error", e);
		}
	}

	/**
	 * 将JSON显示类型字符串转换为对象
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public <T> T parseToExplictObject(String json, Class<T> clazz) {
		try {
			return explictTypeJacksonMapper.readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Parse explict json str to object error", e);
		}
	}
	
	/**
	 * 将首字母大写的JSON字符串转换为对象
	 * 
	 * @param uccJson
	 * @param clazz
	 * @return
	 */
	public <T> T parseToUpperCamelCaseObject(String uccJson, Class<T> clazz) {
		try {
			return upperCamelCaseJacksonMapper.readValue(uccJson, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Parse upper camel case json str to object error", e);
		}
	}

	/**
	 * 将JSON字符串转换为对象
	 * 
	 * @param json
	 * @param type
	 * @return
	 */
	public <T> T parseToObject(String json, final Type type) {
		try {
			return jacksonMapper.readValue(json, new TypeReference<T>() {
				public Type getType() {
					return type;
				}
			});
		} catch (Exception e) {
			throw new RuntimeException("Parse json str to object error", e);
		}
	}

	/**
	 * 从JSON流中读取对象
	 * 
	 * @param in
	 * @param type
	 * @return
	 */
	public <T> T readObjectFromJSONStream(InputStream in, final Type type) {
		try {
			return jacksonMapper.readValue(in, new TypeReference<T>() {
				public Type getType() {
					return type;
				}
			});
		} catch (Exception e) {
			throw new RuntimeException("Parse json str to object error", e);
		}
	}

	/**
	 * 将JSON显示类型字符串转换为对象
	 * 
	 * @param json
	 * @param type
	 * @return
	 */
	public <T> T parseToExplictObject(String json, final Type type) {
		try {
			return explictTypeJacksonMapper.readValue(json, new TypeReference<T>() {
				public Type getType() {
					return type;
				}
			});
		} catch (Exception e) {
			throw new RuntimeException("Parse explict json str to object error", e);
		}
	}
	
	/**
	 * 将首字母大写的JSON字符串转换为对象
	 * 
	 * @param uccJson
	 * @param type
	 * @return
	 */
	public <T> T parseToUpperCamelCaseObject(String uccJson, final Type type) {
		try {
			return upperCamelCaseJacksonMapper.readValue(uccJson, new TypeReference<T>() {
				public Type getType() {
					return type;
				}
			});
		} catch (Exception e) {
			throw new RuntimeException("Parse upper camel case json str to object error", e);
		}
	}
}
