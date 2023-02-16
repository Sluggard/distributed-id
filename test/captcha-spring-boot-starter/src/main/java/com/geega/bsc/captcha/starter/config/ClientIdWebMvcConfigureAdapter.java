package com.geega.bsc.captcha.starter.config;

import com.geega.bsc.captcha.starter.interceptor.ClientIdInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通用拦截器
 *
 * @author Jun.An3
 * @date 2021/11/29
 */
@Configuration
public class ClientIdWebMvcConfigureAdapter implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 获取验证码,一次校验,二次校验时,是用户自己去定义一个拦截
        String[] signPathPatterns = {
                "/**"};
        registry.addInterceptor(clientIdInterceptor()).addPathPatterns(signPathPatterns);
    }

    @Bean
    public ClientIdInterceptor clientIdInterceptor() {
        return new ClientIdInterceptor();
    }

}
