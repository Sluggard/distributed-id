
package com.geega.bsc.captch.common.vo;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author Jun.An3
 * @date 2021/11/22
 */
@Data
public class CaptchaVO implements Serializable {

    /**
     * 验证码类型
     * (clickWord,blockPuzzle)
     */
    private String captchaType;

    /**
     * aes加密秘钥
     */
    private String secretKey;

    /**
     * 原生图片base64
     */
    private String originalImageBase64;

    /**
     * 滑块图片base64
     */
    private String jigsawImageBase64;

    /**
     * 唯一ID
     */
    private String token;

    /**
     * 坐标信息集合
     */
    private List<PointVO> pointList;

    /**
     * 汉字字符集合
     */
    private List<String> wordList;

    /**
     * 单坐标位置
     */
    private PointVO point;

}
