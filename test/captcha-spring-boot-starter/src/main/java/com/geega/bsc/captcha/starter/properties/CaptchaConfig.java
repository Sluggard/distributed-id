package com.geega.bsc.captcha.starter.properties;

import com.geega.bsc.captch.common.enumeration.CaptchaTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import java.awt.*;
import static com.geega.bsc.captcha.starter.properties.CaptchaConfig.PREFIX;

/**
 * 配置类
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
@Data
@ConfigurationProperties(PREFIX)
@RefreshScope
public class CaptchaConfig {

    private String cacheNum = "30";

    private boolean test = false;

    public static final String PREFIX = "captcha";

    private String cacheRefreshPeriod = "60";

    /**
     * 验证码类型
     */
    private CaptchaTypeEnum type = CaptchaTypeEnum.DEFAULT;

    private String clientIdHeaderKey = "client-id";

    /**
     * 本地滑块底图地址
     */
    private String jigsawClasspath = "";

    /**
     * 本地点选底图地址
     */
    private String picClickClasspath = "";

    /**
     * 当自定义图片无效或者没配置时,默认使用默认的底图
     */
    private boolean useDefaultWhenCustomImagesEmpty = true;

    /**
     * 右下角水印文字
     * 默认：没有水印
     */
    private String waterMark = "";

    /**
     * 右下角水印字体(文泉驿正黑).
     */
    private String waterFont = "WenQuanZhengHei.ttf";

    /**
     * 点选文字验证码的文字字体(文泉驿正黑).
     */
    private String fontType = "WenQuanZhengHei.ttf";

    /**
     * 校验滑动拼图允许误差偏移量(默认5像素).
     */
    private String slipOffset = "5";

    /**
     * 滑块干扰项(0/1/2)
     */
    private String interferenceOptions = "2";

    /**
     * local缓存的阈值
     */
    private String cacheNumber = "1000";

    /**
     * 定时清理过期local缓存(单位秒)
     */
    private String timingClear = "180";

    /**
     * 缓存类型 redis、local
     * 默认：local
     */
    private String cacheType = "local";

    /**
     * 历史数据清除开关
     */
    private boolean historyDataClearEnable = false;

    /**
     * 一分钟内接口请求次数限制 开关
     */
    private boolean reqFrequencyLimitEnable = false;

    /***
     * 一分钟内check接口失败次数
     */
    private int reqGetLockLimit = 5;

    /**
     * 锁定5s
     */
    private int reqGetLockSeconds = 5;

    /***
     * get接口一分钟内限制访问数
     */
    private int reqGetMinuteLimit = 5;

    /**
     * 超限范围（默认10s）
     */
    private int reqPeriodLimit = 10;

    /**
     * 一次校验接口一分钟内限制访问数
     */
    private int reqCheckMinuteLimit = 5;

    /**
     * 二次校验接口一分钟内限制访问数
     */
    private int reqVerifyMinuteLimit = 5;

    /**
     * 点选字体样式
     */
    private int fontStyle = Font.BOLD;

    /**
     * 点选字体大小
     */
    private int fontSize = 25;

    /**
     * 点选文字个数，存在问题，暂不要使用
     */
    private int clickWordCount = 4;

}
