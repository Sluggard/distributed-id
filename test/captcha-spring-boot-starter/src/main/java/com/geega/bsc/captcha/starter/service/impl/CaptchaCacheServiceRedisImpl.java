package com.geega.bsc.captcha.starter.service.impl;

import com.geega.bsc.captcha.starter.service.CaptchaCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;

/**
 * 单体服务使用堆内内存就ok
 * 但是多服务情况下，必须使用分布式缓存redis
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
public class CaptchaCacheServiceRedisImpl implements CaptchaCacheService {

    @Override
    public String type() {
        return "redis";
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        stringRedisTemplate.opsForValue().set(key, value, expiresInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean noneExists(String key) {
        return !Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void increment(String key, long val) {
        stringRedisTemplate.opsForValue().increment(key, val);
    }

}
