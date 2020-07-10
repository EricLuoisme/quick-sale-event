package com.selfStudy.quicksaleevent.utils;

import com.alibaba.fastjson.JSON;

public class BeanStringConvert {

    public static <T> String beanToString(T value) {
        /**
         * convert Object into String for storing into Redis
         */
        if (value == null)
            return null;
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class
                || clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else {
            return JSON.toJSONString(value); // when T is an object
        }
    }

    public static <T> T stringToBean(String str, Class<T> clazz) {
        /**
         * convert string we get from Redis to Object
         */
        if (str == null || str.length() <= 0 || clazz == null)
            return null;
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz); // when T is a object class
        }
    }
}
