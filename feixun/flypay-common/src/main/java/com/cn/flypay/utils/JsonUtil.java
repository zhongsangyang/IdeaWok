package com.cn.flypay.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhoujifeng1 on 16/8/2.
 */
public class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper FROM_OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectMapper TO_OBJECT_MAPPER = new ObjectMapper();
    public static <E> E toObject(String jsonString, Class<E> type) {
        E returnObject = null;
        try {
            returnObject = FROM_OBJECT_MAPPER.readValue(jsonString, type);
        } catch (JsonParseException e) {
            e.printStackTrace();
            LOGGER.error("请求字符串：{} 解析异常", jsonString);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            LOGGER.error("请求字符串：{} 映射对象{}异常", jsonString, type.getName());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("请求字符串：{} IO异常", jsonString);
        }

        return returnObject;
    }


    /**
     *
     * @param obj
     * @return
     */
    public static String fromObject(Object obj) {
        StringWriter out = new StringWriter();
        try {
            TO_OBJECT_MAPPER.writeValue(out, obj);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            LOGGER.error("返回字符串：{} 生成json数据异常", obj);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            LOGGER.error("返回字符串,映射对象{}异常", obj);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("返回字符串,映射对象{}, IO异常", obj);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toString();
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Map<String, Object>> jsonToMap(String jsonStr){
        Map<String, Map<String, Object>> maps =new HashMap<String, Map<String,Object>>();
        try {
            maps= TO_OBJECT_MAPPER.readValue(jsonStr, Map.class);
        } catch (JsonParseException e) {
            LOGGER.error("返回字符串：{} 生成json数据异常", jsonStr);
            e.printStackTrace();
        } catch (JsonMappingException e) {
            LOGGER.error("返回字符串,映射对象{}异常", jsonStr);
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error("返回字符串,映射对象{}, IO异常", jsonStr);
            e.printStackTrace();
        }
        return maps;
    }

    /**
     *
     * @param json
     * @param c
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static<T> List<T> toList(String json, Class<T> c) {
        List<T> list = null;
        try {
            list =  TO_OBJECT_MAPPER.readValue(json,new TypeReference<List<T>>() {});
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
