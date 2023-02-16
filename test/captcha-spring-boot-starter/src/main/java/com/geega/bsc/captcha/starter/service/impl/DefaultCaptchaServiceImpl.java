
package com.geega.bsc.captcha.starter.service.impl;

import com.geega.bsc.captch.common.base.BizErrorEnum;
import com.geega.bsc.captch.common.base.BizException;
import com.geega.bsc.captch.common.vo.CaptchaVO;
import com.geega.bsc.captch.common.vo.CheckCaptchaVO;
import com.geega.bsc.captch.common.vo.CheckResultVO;
import com.geega.bsc.captch.common.vo.VerifyResultVO;
import com.geega.bsc.captcha.starter.factory.CaptchaServiceFactory;
import com.geega.bsc.captcha.starter.service.CaptchaService;
import java.util.Properties;

/**
 * @author Jun.An3
 * @date 2021/11/22
 */
@SuppressWarnings("unused")
public class DefaultCaptchaServiceImpl extends AbstractCaptchaService {

    @Override
    public String captchaType() {
        return "default";
    }

    @Override
    public void init(Properties config) {
        for (String captchaType : CaptchaServiceFactory.instances.keySet()) {
            if (captchaType().equals(captchaType)) {
                super.init(config);
                continue;
            }
            getService(captchaType).init(config);
        }
    }

    @Override
    public void destroy(Properties config) {
        for (String s : CaptchaServiceFactory.instances.keySet()) {
            if (captchaType().equals(s)) {
                continue;
            }
            getService(s).destroy(config);
        }
    }

    private CaptchaService getService(String captchaType) {
        CaptchaService captchaService = CaptchaServiceFactory.instances.get(captchaType);
        if (captchaService == null) {
            throw new BizException(BizErrorEnum.RESOURCES_TYPE_NULL);
        }
        return captchaService;
    }

    @Override
    public CaptchaVO get(String captchaType) {
        return getService(captchaType).get(captchaType);
    }

    @Override
    public CheckResultVO check(CheckCaptchaVO captchaVO) {
        return getService(captchaVO.getCaptchaType()).check(captchaVO);
    }

    @Override
    public VerifyResultVO verify(String captchaVerification) {
        try {
            String codeKey = String.format(REDIS_SECOND_CAPTCHA_KEY, captchaVerification);
            if (CaptchaServiceFactory.getCache(cacheType).noneExists(codeKey)) {
                throw new BizException(BizErrorEnum.API_CAPTCHA_INVALID);
            }
            //二次校验取值后，即刻失效
            CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
        } catch (Exception e) {
            throw new BizException(BizErrorEnum.API_CAPTCHA_INVALID);
        }
        return VerifyResultVO.builder()
                .result(true)
                .build();
    }

}
