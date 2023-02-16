
package com.geega.bsc.captcha.starter.service;

import com.geega.bsc.captch.common.vo.CaptchaVO;
import com.geega.bsc.captch.common.vo.CheckCaptchaVO;
import com.geega.bsc.captch.common.vo.CheckResultVO;
import com.geega.bsc.captch.common.vo.VerifyResultVO;
import java.util.Properties;

/**
 * @author Jun.An3
 * @date 2021/11/22
 */
public interface CaptchaService {

    /**
     * 获取做好的图
     */
    CaptchaVO generate();

    /**
     * 配置初始化
     */
    void init(Properties config);

    /**
     * 获取验证码
     */
    CaptchaVO get(String captchaType);

    /**
     * 核对验证码(前端)
     */
    CheckResultVO check(CheckCaptchaVO captchaVO);

    /**
     * 二次校验验证码(后端)
     */
    VerifyResultVO verify(String captchaVerification);

    /***
     * 验证码类型
     * 通过java SPI机制，接入方可自定义实现类，实现新的验证类型
     */
    String captchaType();

    /**
     * 历史资源清除(过期的图片文件，生成的临时图片...)
     *
     * @param config 配置项 控制资源清理的粒度
     */
    void destroy(Properties config);

    void clearCache();

}
