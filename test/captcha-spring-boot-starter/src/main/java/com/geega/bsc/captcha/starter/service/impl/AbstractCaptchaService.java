
package com.geega.bsc.captcha.starter.service.impl;

import com.geega.bsc.captch.common.vo.CaptchaVO;
import com.geega.bsc.captch.common.vo.CheckCaptchaVO;
import com.geega.bsc.captch.common.vo.CheckResultVO;
import com.geega.bsc.captch.common.vo.VerifyResultVO;
import com.geega.bsc.captcha.starter.constant.ConfigConst;
import com.geega.bsc.captcha.starter.context.ClientIdThreadLocal;
import com.geega.bsc.captcha.starter.factory.CaptchaServiceFactory;
import com.geega.bsc.captcha.starter.service.CaptchaCacheService;
import com.geega.bsc.captcha.starter.service.CaptchaService;
import com.geega.bsc.captcha.starter.service.FrequencyLimitHandler;
import com.geega.bsc.captcha.starter.utils.AESUtil;
import com.geega.bsc.captcha.starter.utils.CacheUtil;
import com.geega.bsc.captcha.starter.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Jun.An3
 * @date 2021/11/22
 */
public abstract class AbstractCaptchaService implements CaptchaService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    static final String BASE64_REPLACE_REGEX = "\r|\n";

    static final String BASE64_REPLACE = "";

    protected static final String IMAGE_TYPE_PNG = "png";

    protected static int HAN_ZI_SIZE = 18;

    //check校验坐标
    protected static String REDIS_CAPTCHA_KEY = "RUNNING:CAPTCHA:%s:%s";

    //后台二次校验坐标
    protected static String REDIS_SECOND_CAPTCHA_KEY = "RUNNING:CAPTCHA:second-%s";

    protected static Long EXPIRATION_SECONDS = 2 * 60L;

    protected static Long EXPIRATION_THREE = 3 * 60L;

    protected static String waterMark;

    protected static String waterMarkFontStr = "WenQuanZhengHei.ttf";

    //水印字体
    protected Font waterMarkFont;

    protected static String slipOffset = "5";

    protected static String clickWordFontStr = "WenQuanZhengHei.ttf";

    protected static String cacheType = "local";

    protected static int captchaInterferenceOptions = 0;

    private static Properties config;

    @Override
    public void init(final Properties config) {
        AbstractCaptchaService.config = config;
        //初始化底图信息
        logger.info("初始化验证码底图:{}", captchaType());
        waterMark = config.getProperty(ConfigConst.CAPTCHA_WATER_MARK);
        slipOffset = config.getProperty(ConfigConst.CAPTCHA_SLIP_OFFSET, "5");
        waterMarkFontStr = config.getProperty(ConfigConst.CAPTCHA_WATER_FONT, "WenQuanZhengHei.ttf");
        clickWordFontStr = config.getProperty(ConfigConst.CAPTCHA_FONT_TYPE, "WenQuanZhengHei.ttf");
        cacheType = config.getProperty(ConfigConst.CAPTCHA_CACHE_TYPE, "local");
        captchaInterferenceOptions = Integer.parseInt(config.getProperty(ConfigConst.CAPTCHA_INTERFERENCE_OPTIONS, "0"));
        //部署在linux中，如果没有安装中文字段，水印和点选文字，中文无法显示，
        //通过加载resources下的font字体解决，无需在linux中安装字体
        loadWaterMarkFont();
        //是否开启本地缓存(用于限流数据和临时存放验证数据)
        if ("local".equals(cacheType)) {
            logger.info("初始化local缓存");
            CacheUtil.init(Integer.parseInt(config.getProperty(ConfigConst.CAPTCHA_CACAHE_MAX_NUMBER, "1000")),
                    Long.parseLong(config.getProperty(ConfigConst.CAPTCHA_TIMING_CLEAR_SECOND, "180")));
        }
        //是否需要销毁验证实现类中的资源
        if ("1".equals(config.getProperty(ConfigConst.HISTORY_DATA_CLEAR_ENABLE, "0"))) {
            logger.info("历史资源清除开关开启:{}", captchaType());
            Runtime.getRuntime().addShutdownHook(new Thread(() -> destroy(config)));
        }
        //是否需要限流
        if ("1".equals(config.getProperty(ConfigConst.REQ_FREQUENCY_LIMIT_ENABLE, "0"))) {
            if (limitHandler == null) {
                logger.info("接口分钟内限流开关开启");
                limitHandler = new FrequencyLimitHandler.DefaultLimitHandler(config, getCacheService(cacheType));
            }
        }
    }

    @Override
    public CaptchaVO generate() {
        //do nothing
        return null;
    }

    @Override
    public void clearCache() {
        //do nothing
    }

    protected CaptchaCacheService getCacheService(String cacheType) {
        return CaptchaServiceFactory.getCache(cacheType);
    }

    @Override
    public void destroy(Properties config) {
        //do nothing
    }

    //限流组件
    private static FrequencyLimitHandler limitHandler;

    @Override
    public CaptchaVO get(String captchaType) {
        if (limitHandler != null) {
            limitHandler.validateGet();
        }
        return null;
    }

    @Override
    public CheckResultVO check(CheckCaptchaVO captchaVO) {
        return null;
    }

    @Override
    public VerifyResultVO verify(String captchaVerification) {
        return null;
    }

    protected void afterValidateFail() {
        String clientId = ClientIdThreadLocal.get();
        if (StringUtils.isNotBlank(clientId) && limitHandler != null) {
            // 验证失败分钟内计数
            String fails = String.format(FrequencyLimitHandler.LIMIT_KEY, "FAIL", clientId);
            CaptchaCacheService cs = getCacheService(cacheType);
            if (cs.noneExists(fails)) {
                cs.set(fails, "1", Long.parseLong(config.getProperty(ConfigConst.REQ_PERIOD_LIMIT, "10")));
            }
            cs.increment(fails, 1);
        }
    }

    /**
     * 加载resources下的font字体，add by lide1202@hotmail.com
     * 部署在linux中，如果没有安装中文字段，水印和点选文字，中文无法显示，
     * 通过加载resources下的font字体解决，无需在linux中安装字体
     */
    private void loadWaterMarkFont() {
        try {
            if (waterMarkFontStr.toLowerCase().endsWith(".ttf") || waterMarkFontStr.toLowerCase().endsWith(".ttc")
                    || waterMarkFontStr.toLowerCase().endsWith(".otf")) {
                this.waterMarkFont = Font.createFont(Font.TRUETYPE_FONT,
                                Objects.requireNonNull(getClass().getResourceAsStream("/fonts/" + waterMarkFontStr)))
                        .deriveFont(Font.BOLD, HAN_ZI_SIZE >> 1);
            } else {
                this.waterMarkFont = new Font(waterMarkFontStr, Font.BOLD, HAN_ZI_SIZE >> 1);
            }
        } catch (Exception e) {
            logger.error("load font error:", e);
        }
    }

    /**
     * 解密前端坐标aes加密
     *
     * @param point 坐标json
     * @return 加密字符串
     * @throws Exception 加密异常
     */
    public static String decrypt(String point, String key) throws Exception {
        return AESUtil.aesDecrypt(point, key);
    }

    protected Integer getCacheNum(Properties config) {
        return Integer.valueOf(config.getProperty(ConfigConst.CACHE_NUM, "30"));
    }

    protected Integer getCacheRefreshPeriod(Properties config) {
        return Integer.valueOf(config.getProperty(ConfigConst.CACHE_REFRESH_PERIOD, "60"));
    }

    protected static int getEnOrChLength(String words) {
        int enCount = 0;
        int chCount = 0;
        for (int i = 0; i < words.length(); i++) {
            int length = String.valueOf(words.charAt(i)).getBytes(StandardCharsets.UTF_8).length;
            if (length > 1) {
                chCount++;
            } else {
                enCount++;
            }
        }
        int chOffset = (HAN_ZI_SIZE >> 1) * chCount + 5;
        int enOffset = enCount * 8;
        return chOffset + enOffset;
    }

    protected CaptchaVO copyNew(CaptchaVO captcha) {
        if (captcha == null) {
            return null;
        }
        CaptchaVO captchaVO = new CaptchaVO();
        BeanUtils.copyProperties(captcha, captchaVO);
        return captchaVO;
    }

    protected void noNeedSetNull(CaptchaVO captcha) {
        if (captcha != null && "0".equals(config.getProperty(ConfigConst.CAPTCHA_TEST))) {
            captcha.setPoint(null);
            captcha.setPointList(null);
        }
    }

}
