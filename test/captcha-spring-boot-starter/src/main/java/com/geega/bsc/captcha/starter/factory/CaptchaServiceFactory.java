package com.geega.bsc.captcha.starter.factory;

import com.geega.bsc.captch.common.base.BizException;
import com.geega.bsc.captcha.starter.constant.ConfigConst;
import com.geega.bsc.captcha.starter.service.CaptchaCacheService;
import com.geega.bsc.captcha.starter.service.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * 缓存实现,验证类型实现工厂类
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
public class CaptchaServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaServiceFactory.class);

    public volatile static Map<String, CaptchaService> instances = new HashMap<>();

    public volatile static Map<String, CaptchaCacheService> cacheService = new HashMap<>();

    public static CaptchaService getInstance(Properties config) {
        String captchaType = config.getProperty(ConfigConst.CAPTCHA_TYPE, "default");
        CaptchaService captchaService = instances.get(captchaType);
        if (captchaService == null) {
            throw new BizException("500", "未找到验证类型相应的实现类,验证类型:" + captchaType);
        }
        captchaService.init(config);
        return captchaService;
    }

    public static CaptchaCacheService getCache(String cacheType) {
        return cacheService.get(cacheType);
    }

    static {
        ServiceLoader<CaptchaCacheService> cacheServices = ServiceLoader.load(CaptchaCacheService.class);
        for (CaptchaCacheService item : cacheServices) {
            cacheService.put(item.type(), item);
        }
        LOGGER.info("支持的缓存类型:{}", cacheService.keySet());
        ServiceLoader<CaptchaService> services = ServiceLoader.load(CaptchaService.class);
        for (CaptchaService item : services) {
            instances.put(item.captchaType(), item);
        }
        LOGGER.info("支持的验证类型:{}", instances.keySet());
    }
}
