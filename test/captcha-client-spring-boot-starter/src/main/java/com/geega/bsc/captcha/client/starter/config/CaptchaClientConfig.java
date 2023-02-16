package com.geega.bsc.captcha.client.starter.config;

import com.geega.bsc.captch.common.base.BizErrorEnum;
import com.geega.bsc.captch.common.base.BizException;
import com.geega.bsc.captcha.client.starter.feign.CaptchaClientServiceFallBack;
import com.geega.bsc.captcha.client.starter.service.CaptchaClient;
import com.geega.bsc.captcha.client.starter.service.CaptchaClientCloudService;
import com.geega.bsc.captcha.client.starter.service.CaptchaClientHttpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/**
 * 配置文件
 *
 * @author Jun.An3
 * @date 2021/11/26
 */
@Slf4j
public class CaptchaClientConfig {

    /**
     * 服务地址前缀
     * http://localhost:9107
     */
    public static String serviceUrlPrefix;

    @Value("${captcha.service-url-prefix:}")
    public void setServiceUrlPrefix(String serviceUrlPrefix) {
        CaptchaClientConfig.serviceUrlPrefix = serviceUrlPrefix;
    }

    @Bean
    @Order(1)
    public CaptchaClient captchaClient() {
        try {
            Class.forName("org.springframework.cloud.openfeign.FeignAutoConfiguration");
            log.info("人机验证客户端启动成功，选用Feign模式");
            return new CaptchaClientCloudService();
        } catch (ClassNotFoundException ignored) {
        }
        if (StringUtils.isNotBlank(serviceUrlPrefix)) {
            log.info("人机验证客户端启动成功，选用Http模式");
            return new CaptchaClientHttpService();
        }
        throw new BizException(BizErrorEnum.SYSTEM_ERROR.getCode(), "人机验证客户端初始化失败");
    }

    @Bean
    public CaptchaClientServiceFallBack captchaClientServiceFallBack() {
        return new CaptchaClientServiceFallBack();
    }

}
