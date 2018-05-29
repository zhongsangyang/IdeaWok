package com.cn.flypay.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author sunyue
 *
 */
public class BeanUtils{
  public static <T> T map2Bean(Map<String, Object> map, Class<T> clazz){
    try{
      BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      for (PropertyDescriptor property : propertyDescriptors){
        String key = property.getName();
        if (map.containsKey(key)){
          Object value = map.get(key);
          Method setter = property.getWriteMethod();
          T obj = clazz.newInstance();
          setter.invoke(obj, new Object[] { value });
          return obj;
        }
      }
    } catch (Exception e) {
      System.out.println("transMap2Bean Error " + e);
    }
    return null;
  }
  
  public static Map<String, String> bean2Map(Object obj){
    Map<String, String> map = new HashMap<String, String>();
    try{
      BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      for (PropertyDescriptor property : propertyDescriptors){
        String key = property.getName();
        if (!key.equals("class")){
          Method getter = property.getReadMethod();
          Object value = getter.invoke(obj);
          if (value != null) {
            map.put(key, value.toString());
          }
        }
      }
    } catch (Exception e) {
      System.out.println("transBean2Map Error " + e);
    }
    return map;
  }
}
