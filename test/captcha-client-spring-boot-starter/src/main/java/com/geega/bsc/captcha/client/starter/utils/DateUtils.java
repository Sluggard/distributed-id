package com.geega.bsc.captcha.client.starter.utils;


import org.apache.commons.lang3.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mingxing.Huang
 * @version V1.0.0
 * @description 时间工具类
 * @date 2021/8/5
 */
@SuppressWarnings("unused")
public class DateUtils {

    /**
     * 获取当前时间的毫秒级时间戳
     * 注意：文中都使用的时区都是东8区，也就是北京时间。这是为了防止服务器设置时区错误时导致时间不对，如果您是其他时区
     */
    public static long getNowSystemTimeStampMilliSecond() {
        return System.currentTimeMillis();
    }

    /**
     * 用于获取当前格式化的时间(默认取标砖格式)
     */
    public static String getStandardFormatCurrentTime() {
        return DateUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 用于将时间转字符串
     */
    public static String dateToString(Date date, String strDateFormat) {
        strDateFormat = StringUtils.isBlank(strDateFormat) ? "yyyy-MM-dd HH:mm:ss" : strDateFormat;
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(date);
    }


    /**
     * 用于基于某一时间戳计算过期时间的时间戳,expireSecond 过期时间为空，则默认取系统默认的的授权时间
     */
    public static long computeExpireTime(long nowTimeStamp, Long expireSecond, Long defSecond) {
        //过期时间的时间戳计算
        long timeStamp;
        if (expireSecond != null) {
            if (expireSecond <= 0) {
                timeStamp = -1;
            } else {
                timeStamp = nowTimeStamp + expireSecond * 1000;
            }
        } else {
            //添加默认得过期时间
            timeStamp = nowTimeStamp + defSecond * 1000;
        }
        return timeStamp;
    }

    /**
     * 当前时间加上时间(单位:秒)
     *
     * @param timestamp 时间(单位:秒)
     * @return 时间戳
     */
    public static long nowPlusTimeStamp(long timestamp) {
        if (timestamp <= 0) {
            return -1;
        }
        return currentTimeStamp() + timestamp * 1000;
    }

    /**
     * 获取当前时间戳
     */
    public static long currentTimeStamp() {
        return System.currentTimeMillis();
    }


    /**
     * 获取当前时间(单位:秒)
     */
    public static long currentTimeSecond() {
        return System.currentTimeMillis() / 1000;
    }

}
