
package com.geega.bsc.captch.common.vo;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用于一次校验的VO
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
@Data
public class CheckCaptchaVO implements Serializable {

    /**
     * 验证码类型
     * (clickWord,blockPuzzle)
     */
    @NotBlank(message = "captchaType不能为空")
    private String captchaType;

    /**
     * 点坐标(base64加密传输)
     */
    @NotBlank(message = "pointJson不能为空")
    private String pointJson;

    /**
     * 每次请求的验证码唯一标识，使用雪花算法生成
     */
    @NotBlank(message = "token不能为空")
    private String token;

}
