package com.geega.bsc.captcha.starter.service.impl;

import com.geega.bsc.captcha.starter.service.CaptchaCacheService;
import com.geega.bsc.captcha.starter.utils.CacheUtil;
import java.util.Objects;

/**
 * 单体服务使用堆内内存就ok
 * 但是多服务情况下，必须使用分布式缓存redis
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
public class CaptchaCacheServiceMemImpl implements CaptchaCacheService {

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        CacheUtil.set(key, value, expiresInSeconds);
    }

    @Override
    public boolean noneExists(String key) {
        return !CacheUtil.exists(key);
    }

    @Override
    public void delete(String key) {
        CacheUtil.delete(key);
    }

    @Override
    public String get(String key) {
        return CacheUtil.get(key);
    }

    @Override
    public void increment(String key, long val) {
        Long ret = Long.parseLong(Objects.requireNonNull(CacheUtil.get(key))) + val;
        CacheUtil.set(key, ret + "", 0);
    }

    @Override
    public String type() {
        return "local";
    }

}
