package com.geega.bsc.id.common.utils;

/**
 * @author Jun.An3
 * @date 2022/07/20
 */
public class SleepUtil {

    public static void waitMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {
            //do nothing
        }
    }

}
