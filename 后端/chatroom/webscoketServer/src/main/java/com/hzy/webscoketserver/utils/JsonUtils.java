package com.hzy.webscoketserver.utils;

import com.alibaba.fastjson.JSON;

/**
 * @title: JsonUtils
 * @Author zxwyhzy
 * @Date: 2023/5/7 21:03
 * @Version 1.0
 */
public class JsonUtils {
    public static String toJson(Object object) throws Exception {
        return JSON.toJSONString(object);
    }

    public static <T> T parse(String string, Class<T> resultClass) {
        return JSON.parseObject(string, resultClass);
    }
}
