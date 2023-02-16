package com.geega.bsc.captcha.client.starter.feign;

import com.geega.bsc.captch.common.base.BizResult;
import com.geega.bsc.captch.common.vo.VerifyResultVO;
import com.geega.bsc.captch.common.vo.VerifyVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * feign接口
 *
 * @author Jun.An3
 * @date 2021/11/26
 */
@Lazy
@FeignClient(url = "${captcha.service-url-prefix:}", path = "/api/v1", name = "captcha-service", fallback = CaptchaClientServiceFallBack.class)
public interface CaptchaClientServiceFeign {

    /**
     * 二次校验
     */
    @PostMapping(value = "/captcha/verify")
    BizResult<VerifyResultVO> verify(@RequestBody VerifyVO verifyVO);

}
