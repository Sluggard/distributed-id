package com.geega.bsc.captcha.client.starter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geega.bsc.captch.common.base.BizErrorEnum;
import com.geega.bsc.captch.common.base.BizException;

/**
 * @author Mingxing.Huang
 * @version V1.0.0
 * @description jackson 工具类
 * @date 2021/8/6
 */
@SuppressWarnings({"unused", "unchecked"})
public class JacksonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 用于符串转对象
     */
    public static <T> T jsonToObject(String json, Class<T> tClass) throws JsonProcessingException {
        return objectMapper.readValue(json, tClass);
    }

    /**
     * 用于对象转字符串
     */
    public static String objectToString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * 将不同类型转换成想要的类型
     */
    public static <T> T objectToObject(Object obj, Class<T> tClass) throws JsonProcessingException {
        if (obj == null) {
            throw new BizException(BizErrorEnum.OBJECT_EMPTY);
        }
        if (obj.getClass().equals(tClass)) {
            return (T) obj;
        }

        return objectMapper.readValue(objectMapper.writeValueAsString(obj), tClass);
    }

}
