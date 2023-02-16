package com.geega.bsc.captcha.client.starter.service;

import com.geega.bsc.captch.common.base.BizErrorEnum;
import com.geega.bsc.captch.common.base.BizResult;
import com.geega.bsc.captch.common.vo.VerifyResultVO;
import com.geega.bsc.captch.common.vo.VerifyVO;
import com.geega.bsc.captcha.client.starter.feign.CaptchaClientServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * feign方式
 *
 * @author Mingxing.Huang
 * @author Jun.An3
 * @date 2021/11/26
 */
@Slf4j
public class CaptchaClientCloudService implements CaptchaClient {

    @SuppressWarnings("unused")
    @Autowired
    private CaptchaClientServiceFeign service;

    @Override
    public Boolean verify(String verification) {
        try {
            VerifyVO verifyVO = VerifyVO.builder().verification(verification).build();
            BizResult<VerifyResultVO> bizResult = service.verify(verifyVO);
            if (!BizResult.checkIsSuccess(bizResult)) {
                String msg = bizResult != null ? bizResult.getMsg() : BizErrorEnum.SYSTEM_ERROR.getMsg();
                log.error("二次校验失败#描述：{}#方法：verify", msg);
                return false;
            }
            if (bizResult.getData() != null) {
                return bizResult.getData().getResult();
            }
        } catch (Exception e) {
            log.error("校验异常:", e);
        }
        return false;
    }

}
