package com.geega.bsc.captcha.client.starter.service;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.captch.common.base.BizException;
import com.geega.bsc.captch.common.base.BizResult;
import com.geega.bsc.captch.common.vo.VerifyResultVO;
import com.geega.bsc.captch.common.vo.VerifyVO;
import com.geega.bsc.captcha.client.starter.config.CaptchaClientConfig;
import com.geega.bsc.captcha.client.starter.config.CaptchaUriConfig;
import com.geega.bsc.captcha.client.starter.utils.JacksonUtils;
import com.geega.bsc.captcha.client.starter.utils.OKHttpUtils;
import com.geega.bsc.captcha.client.starter.utils.StringBaseUtils;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;

/**
 * http方式调用实现类
 *
 * @author Jun.An3
 * @date 2021/11/26
 */
@Slf4j
public class CaptchaClientHttpService implements CaptchaClient {

    @Override
    public Boolean verify(String verification) {
        try {
            VerifyVO verifyVO = VerifyVO.builder()
                    .verification(verification)
                    .build();
            String url = StringBaseUtils.contact(
                    CaptchaClientConfig.serviceUrlPrefix,
                    CaptchaUriConfig.API_V1_CAPTCHA_VERIFY
            );
            //noinspection rawtypes
            BizResult bizResult = OKHttpUtils.doPost(url, BizResult.class, JSON.toJSONString(verifyVO).getBytes(StandardCharsets.UTF_8));
            if (!BizResult.checkIsSuccess(bizResult)) {
                return false;
            }
            if (bizResult.getData() != null) {
                return JacksonUtils.objectToObject(bizResult.getData(), VerifyResultVO.class).getResult();
            }
        } catch (BizException e) {
            log.error("二次校验异常,异常信息：", e);
        } catch (Exception e) {
            log.error("二次校验异常：异常信息：", e);
        }
        return false;
    }

}
