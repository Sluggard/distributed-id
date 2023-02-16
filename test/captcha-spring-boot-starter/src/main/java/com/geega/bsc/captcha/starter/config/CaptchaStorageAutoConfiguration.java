package com.geega.bsc.captcha.starter.config;

import com.geega.bsc.captcha.starter.factory.CaptchaServiceFactory;
import com.geega.bsc.captcha.starter.properties.CaptchaConfig;
import com.geega.bsc.captcha.starter.service.CaptchaCacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 存储策略自动配置
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
@Configuration
public class CaptchaStorageAutoConfiguration {

    @SuppressWarnings("unused")
    @Bean(name = "captchaCacheService")
    public CaptchaCacheService captchaCacheService(CaptchaConfig captchaConfig) {
        return CaptchaServiceFactory.getCache(captchaConfig.getCacheType());
    }

}
