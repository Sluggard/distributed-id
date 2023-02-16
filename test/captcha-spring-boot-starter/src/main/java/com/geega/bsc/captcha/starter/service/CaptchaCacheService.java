
package com.geega.bsc.captcha.starter.service;

/**
 * 缓存接口
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
public interface CaptchaCacheService {

    void set(String key, String value, long expiresInSeconds);

    boolean noneExists(String key);

    void delete(String key);

    String get(String key);

    String type();

    default void increment(String key, long val) {
    }

}
