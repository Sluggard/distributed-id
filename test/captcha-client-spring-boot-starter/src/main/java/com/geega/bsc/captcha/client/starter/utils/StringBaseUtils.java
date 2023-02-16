package com.geega.bsc.captcha.client.starter.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Mingxing.Huang
 * @version V1.0.0
 * @description 字符串工具类
 * @date 2021/8/4
 */
public class StringBaseUtils {

    /**
     * 用于快速多个字符串的拼接
     */
    public static String contact(String... arg) {
        StringBuilder stringBuffer = new StringBuilder();
        for (String s : arg) {
            if (StringUtils.isNotBlank(s)) {
                stringBuffer.append(s);
            }
        }
        return stringBuffer.toString();
    }

}

