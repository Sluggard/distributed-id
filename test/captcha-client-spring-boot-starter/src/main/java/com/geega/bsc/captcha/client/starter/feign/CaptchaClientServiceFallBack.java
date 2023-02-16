package com.geega.bsc.captcha.client.starter.feign;

import com.geega.bsc.captch.common.base.BizErrorEnum;
import com.geega.bsc.captch.common.base.BizException;
import com.geega.bsc.captch.common.base.BizResult;
import com.geega.bsc.captch.common.vo.VerifyResultVO;
import com.geega.bsc.captch.common.vo.VerifyVO;
import com.geega.bsc.captcha.client.starter.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Feign降级类
 *
 * @author Jun.An3
 * @date 2021/11/29
 */
@Slf4j
public class CaptchaClientServiceFallBack implements CaptchaClientServiceFeign {

    @Override
    public BizResult<VerifyResultVO> verify(VerifyVO verifyVO) {
        log.error("captcha-client 调用远程服务失败！当前时间：{}", DateUtils.getStandardFormatCurrentTime());
        throw new BizException(BizErrorEnum.REMOTE_CALL_FAIL);
    }

}
