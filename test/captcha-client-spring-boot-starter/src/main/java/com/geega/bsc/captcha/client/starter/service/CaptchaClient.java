package com.geega.bsc.captcha.client.starter.service;

/**
 * 人机验证接口
 *
 * @author Mingxing.Huang
 * @author Jun.An3
 * @date 2021/11/26
 */
public interface CaptchaClient {

    Boolean verify(String verification);

}
