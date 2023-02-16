package com.geega.bsc.captcha.starter.config;

import com.geega.bsc.captcha.starter.properties.CaptchaConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 启动配置类
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
@Configuration
@EnableConfigurationProperties({CaptchaConfig.class, RefreshConfig.class})
@ComponentScan("com.geega.bsc.captcha")
@Import({CaptchaServiceAutoConfiguration.class, CaptchaStorageAutoConfiguration.class})
public class CaptchaAutoConfiguration {

}
