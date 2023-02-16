
package com.geega.bsc.captcha.starter.controller;

import com.geega.bsc.captch.common.base.BizResult;
import com.geega.bsc.captch.common.vo.CaptchaVO;
import com.geega.bsc.captch.common.vo.CheckCaptchaVO;
import com.geega.bsc.captch.common.vo.CheckResultVO;
import com.geega.bsc.captch.common.vo.VerifyResultVO;
import com.geega.bsc.captch.common.vo.VerifyVO;
import com.geega.bsc.captcha.starter.aspect.ControllerMethodTitle;
import com.geega.bsc.captcha.starter.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 人机验证Restful接口
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
@Validated
@RestController
@RequestMapping("/api/v1/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    /**
     * 获取验证码
     *
     * @param captchaType 验证类型,支持两种,分别为: clickWord,blockPuzzle,default;默认:default
     */
    @GetMapping("/get")
    @ControllerMethodTitle("获取验证码接口")
    public BizResult<CaptchaVO> get(@RequestParam(value = "captchaType") String captchaType) {
        return BizResult.success(captchaService.get(captchaType));
    }

    /**
     * 一次校验
     */
    @PostMapping("/check")
    @ControllerMethodTitle("一次校验接口")
    public BizResult<CheckResultVO> check(@RequestBody CheckCaptchaVO captchaVO) {
        return BizResult.success(captchaService.check(captchaVO));
    }

    /**
     * 二次校验
     */
    @PostMapping("/verify")
    @ControllerMethodTitle("二次校验接口")
    public BizResult<VerifyResultVO> verify(@RequestBody VerifyVO verifyVO) {
        VerifyResultVO verify = captchaService.verify(verifyVO.getVerification());
        return BizResult.success(VerifyResultVO.builder()
                .result(verify.getResult())
                .build());
    }

}
