package com.geega.bsc.captcha.starter.config;

import com.geega.bsc.captcha.starter.constant.ConfigConst;
import com.geega.bsc.captcha.starter.factory.CaptchaServiceFactory;
import com.geega.bsc.captcha.starter.properties.CaptchaConfig;
import com.geega.bsc.captcha.starter.service.CaptchaService;
import com.geega.bsc.captcha.starter.utils.ImageUtils;
import com.geega.bsc.captcha.starter.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置类
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
@Configuration
@Slf4j
public class CaptchaServiceAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaServiceAutoConfiguration.class);

    @Bean
    @ConditionalOnClass(name = {"com.alibaba.cloud.nacos.NacosConfigAutoConfiguration"})
    public RefreshBean cloudRefreshBean(RefreshConfig imageConfig) {
        return new RefreshBean(imageConfig);
    }

    @Bean
    @ConditionalOnClass(name = "com.alibaba.boot.nacos.config.autoconfigure.NacosConfigAutoConfiguration")
    public RefreshBean bootRefreshBean(RefreshConfig imageConfig) {
        return new RefreshBean(imageConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public CaptchaService captchaService(CaptchaConfig prop, RefreshConfig refreshConfig) {
        LOGGER.info("自定义配置项：{}", prop);
        Properties config = new Properties();
        //预加载图片数量
        config.put(ConfigConst.CACHE_NUM, prop.getCacheNum());
        //预加载图片替换更新时间
        config.put(ConfigConst.CACHE_REFRESH_PERIOD, prop.getCacheRefreshPeriod());
        //缓存类型，默认：内存
        config.put(ConfigConst.CAPTCHA_CACHE_TYPE, prop.getCacheType());
        //底图水印，默认：没有
        config.put(ConfigConst.CAPTCHA_WATER_MARK, prop.getWaterMark());
        //点选文字验证码的文字字体，默认：文泉驿正黑
        config.put(ConfigConst.CAPTCHA_FONT_TYPE, prop.getFontType());
        //验证码类型，默认实例化了两种（滑块，点选），
        //如果要自己实现，可以自己实现接口，并在META-INF.services中配置
        config.put(ConfigConst.CAPTCHA_TYPE, prop.getType().getCodeValue());
        //滑动干扰项,默认2项
        config.put(ConfigConst.CAPTCHA_INTERFERENCE_OPTIONS, prop.getInterferenceOptions());
        config.put(ConfigConst.ORIGINAL_PATH_JIGSAW, prop.getJigsawClasspath());
        config.put(ConfigConst.ORIGINAL_PATH_PIC_CLICK, prop.getPicClickClasspath());
        config.put(ConfigConst.USE_DEFAULT_WHEN_CUSTOM_IMAGES_EMPTY, prop.isUseDefaultWhenCustomImagesEmpty() ? "1" : "0");
        config.put(ConfigConst.CAPTCHA_SLIP_OFFSET, prop.getSlipOffset());
        config.put(ConfigConst.CAPTCHA_WATER_FONT, prop.getWaterFont());
        config.put(ConfigConst.CAPTCHA_CACAHE_MAX_NUMBER, prop.getCacheNumber());
        config.put(ConfigConst.CAPTCHA_TIMING_CLEAR_SECOND, prop.getTimingClear());
        //是否清除资源
        config.put(ConfigConst.HISTORY_DATA_CLEAR_ENABLE, prop.isHistoryDataClearEnable() ? "1" : "0");
        //限流参数
        config.put(ConfigConst.REQ_FREQUENCY_LIMIT_ENABLE, prop.isReqFrequencyLimitEnable() ? "1" : "0");
        config.put(ConfigConst.REQ_GET_LOCK_LIMIT, prop.getReqGetLockLimit() + "");
        config.put(ConfigConst.REQ_GET_LOCK_SECONDS, prop.getReqGetLockSeconds() + "");
        config.put(ConfigConst.REQ_PERIOD_LIMIT, prop.getReqPeriodLimit() + "");
        config.put(ConfigConst.REQ_GET_MINUTE_LIMIT, prop.getReqGetMinuteLimit() + "");
        config.put(ConfigConst.REQ_CHECK_MINUTE_LIMIT, prop.getReqCheckMinuteLimit() + "");
        config.put(ConfigConst.REQ_VALIDATE_MINUTE_LIMIT, prop.getReqVerifyMinuteLimit() + "");

        config.put(ConfigConst.CAPTCHA_FONT_SIZE, prop.getFontSize() + "");
        config.put(ConfigConst.CAPTCHA_FONT_STYLE, prop.getFontStyle() + "");
        config.put(ConfigConst.CAPTCHA_WORD_COUNT, prop.getClickWordCount() + "");
        config.put(ConfigConst.CLIENT_ID_HEADER_KEY, StringUtils.isBlank(prop.getClientIdHeaderKey()) ? "client-id" : prop.getClientIdHeaderKey());

        config.put(ConfigConst.CAPTCHA_TEST, prop.isTest() ? "1" : "0");

        //加载默认的滑图缺块到内存中
        cacheDefaultSlidingBlockImage();

        //加载本地自定义图片
        String jigsawClasspath = config.getProperty(ConfigConst.ORIGINAL_PATH_JIGSAW);
        if (StringUtils.isNotBlank(jigsawClasspath)) {
            ImageUtils.cacheImage(getResourcesImagesFile(jigsawClasspath), null, null);
        }

        String picClickClasspath = config.getProperty(ConfigConst.ORIGINAL_PATH_PIC_CLICK);
        if (StringUtils.isNotBlank(picClickClasspath)) {
            ImageUtils.cacheImage(null, null, getResourcesImagesFile(picClickClasspath));
        }

        //从远程URL读取图片,缓存到内存中
        //1.滑块类型
        RefreshBean.refreshJigsawImages(refreshConfig.getJigsawUrls(), null);
        //2.点选类型
        RefreshBean.refreshPickClickImages(refreshConfig.getPicClickUrls(), null);

        //如果自定义配置图片都无法使用,去加载组件自带图片
        String useDefaultWhenCustomImagesEmpty = config.getProperty(ConfigConst.USE_DEFAULT_WHEN_CUSTOM_IMAGES_EMPTY);
        if ("1".equals(useDefaultWhenCustomImagesEmpty) && ImageUtils.jigsawIsEmpty()) {
            //加载默认的滑图
            cacheDefaultJigsawImage();
        }
        if ("1".equals(useDefaultWhenCustomImagesEmpty) && ImageUtils.picClickIsEmpty()) {
            //加载默认的点选
            cacheDefaultPicClickImage();
        }

        //最后再次检查是否有图片
        if (ImageUtils.picClickIsEmpty()) {
            log.warn("无点选底图!");
        }

        if (ImageUtils.jigsawIsEmpty()) {
            log.warn("无滑块底图!");
        }

        return CaptchaServiceFactory.getInstance(config);
    }

    private void cacheDefaultSlidingBlockImage() {
        ImageUtils.cacheImage(
                null,
                getResourcesImagesFile("classpath:default-images/jigsaw/slidingBlock/*.png"),
                null
        );
    }

    private void cacheDefaultJigsawImage() {
        ImageUtils.cacheImage(
                getResourcesImagesFile("classpath:default-images/jigsaw/original/*.png"),
                null,
                null
        );
    }

    private void cacheDefaultPicClickImage() {
        ImageUtils.cacheImage(
                null,
                null,
                getResourcesImagesFile("classpath:default-images/pic-click/*.png")
        );
    }

    public static Map<String, String> getResourcesImagesFile(String path) {
        Map<String, String> imgMap = new HashMap<>();
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(path);
            for (Resource resource : resources) {
                byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
                String string = Base64Utils.encodeToString(bytes);
                String filename = resource.getFilename();
                imgMap.put(filename, string);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("读取本地文件出错", e);
        }
        return imgMap;
    }

}
