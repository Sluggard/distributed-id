
package com.geega.bsc.captcha.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 人机验证服务启动类
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
@EnableConfigurationProperties
@SpringBootApplication
public class CaptchaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaptchaServiceApplication.class, args);
    }

}
