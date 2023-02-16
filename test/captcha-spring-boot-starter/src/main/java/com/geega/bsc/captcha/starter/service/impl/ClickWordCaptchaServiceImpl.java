
package com.geega.bsc.captcha.starter.service.impl;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.captch.common.base.BizErrorEnum;
import com.geega.bsc.captch.common.base.BizException;
import com.geega.bsc.captch.common.enumeration.CaptchaTypeEnum;
import com.geega.bsc.captch.common.vo.CaptchaVO;
import com.geega.bsc.captch.common.vo.CheckCaptchaVO;
import com.geega.bsc.captch.common.vo.CheckResultVO;
import com.geega.bsc.captch.common.vo.PointVO;
import com.geega.bsc.captch.common.vo.VerifyResultVO;
import com.geega.bsc.captcha.starter.cache.ImageCache;
import com.geega.bsc.captcha.starter.constant.ConfigConst;
import com.geega.bsc.captcha.starter.factory.CaptchaServiceFactory;
import com.geega.bsc.captcha.starter.utils.AESUtil;
import com.geega.bsc.captcha.starter.utils.ClickWordUtils;
import com.geega.bsc.captcha.starter.utils.ImageUtils;
import com.geega.bsc.captcha.starter.utils.RandomUtils;
import com.geega.bsc.captcha.starter.utils.SnowFlakeUtils;
import com.geega.bsc.captcha.starter.utils.StringUtils;
import com.geega.bsc.captcha.starter.utils.WaterMarkUtils;
import lombok.extern.slf4j.Slf4j;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * 点选文字验证码
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
@Slf4j
public class ClickWordCaptchaServiceImpl extends AbstractCaptchaService {

    protected static String clickWordFontStr = "NotoSerif-Light.ttf";

    //点选文字字体
    protected Font clickWordFont;

    private ImageCache imageCache;

    @Override
    public String captchaType() {
        return CaptchaTypeEnum.CLICK_WORD.getCodeValue();
    }

    @Override
    public void init(Properties config) {
        super.init(config);
        initFont(config);
        this.imageCache = new ImageCache(this, getCacheNum(config), getCacheRefreshPeriod(config));
    }

    @Override
    public void clearCache() {
        imageCache.clearCache();
    }

    @Override
    public CaptchaVO generate() {
        BufferedImage bufferedImage = ImageUtils.getPicClick();
        if (null == bufferedImage) {
            return null;
        }
        return getImageData(bufferedImage);
    }

    private void initFont(Properties config) {
        clickWordFontStr = config.getProperty(ConfigConst.CAPTCHA_FONT_TYPE, "SourceHanSansCN-Normal.otf");
        try {
            int size = Integer.parseInt(config.getProperty(ConfigConst.CAPTCHA_FONT_SIZE, HAN_ZI_SIZE + ""));
            //汉字的大小
            HAN_ZI_SIZE = size;
            if (clickWordFontStr.toLowerCase().endsWith(".ttf")
                    || clickWordFontStr.toLowerCase().endsWith(".ttc")
                    || clickWordFontStr.toLowerCase().endsWith(".otf")) {
                this.clickWordFont = Font.createFont(Font.TRUETYPE_FONT,
                                Objects.requireNonNull(getClass().getResourceAsStream("/fonts/" + clickWordFontStr)))
                        .deriveFont(Font.BOLD, size);
            } else {
                int style = Integer.parseInt(config.getProperty(ConfigConst.CAPTCHA_FONT_STYLE, Font.BOLD + ""));
                //noinspection MagicConstant
                this.clickWordFont = new Font(clickWordFontStr, style, size);
            }
        } catch (Exception ex) {
            logger.error("load font error:", ex);
        }
        this.wordTotalCount = Integer.parseInt(config.getProperty(ConfigConst.CAPTCHA_WORD_COUNT, "4"));
    }

    @Override
    public void destroy(Properties config) {
        //do nothing
    }

    @Override
    public CaptchaVO get(String captchaType) {
        //限流处理
        super.get(captchaType);
        //获取数据
        CaptchaVO captcha = imageCache.get();
        captcha = copyNew(captcha);
        if (captchaIsNotValid(captcha)) {
            captcha = generate();
            if (captchaIsNotValid(captcha)) {
                throw new BizException(BizErrorEnum.API_CAPTCHA_ERROR);
            }
        }
        //这里重新设置一下aesSecret和token
        String secretKey = AESUtil.getKey();
        captcha.setSecretKey(secretKey);
        captcha.getPointList().forEach(pointVO -> pointVO.setSecretKey(secretKey));
        captcha.setToken(SnowFlakeUtils.instance.next());
        //将坐标信息存入缓存中
        String codeKey = String.format(REDIS_CAPTCHA_KEY, captcha.getToken(), captchaType());
        CaptchaServiceFactory.getCache(cacheType).set(codeKey, JSON.toJSONString(captcha.getPointList()), EXPIRATION_SECONDS);
        noNeedSetNull(captcha);
        return captcha;
    }

    private boolean captchaIsNotValid(CaptchaVO captcha) {
        return captcha == null || StringUtils.isBlank(captcha.getOriginalImageBase64());
    }

    @Override
    public CheckResultVO check(CheckCaptchaVO captchaVO) {
        //限流判断
        super.check(captchaVO);

        //取坐标信息键
        String codeKey = String.format(REDIS_CAPTCHA_KEY, captchaVO.getToken(), captchaVO.getCaptchaType());
        String cachePointJson = CaptchaServiceFactory.getCache(cacheType).get(codeKey);
        if (StringUtils.isBlank(cachePointJson)) {
            throw new BizException(BizErrorEnum.API_CAPTCHA_INVALID);
        }
        //验证码只用一次，即刻失效
        CaptchaServiceFactory.getCache(cacheType).delete(codeKey);

        List<PointVO> point;
        List<PointVO> point1;
        String pointJson;
        try {
            point = JSON.parseArray(cachePointJson, PointVO.class);
            //aes解密
            pointJson = decrypt(captchaVO.getPointJson(), point.get(0).getSecretKey());
            point1 = JSON.parseArray(pointJson, PointVO.class);
        } catch (Exception e) {
            logger.error("验证码坐标解析失败", e);
            afterValidateFail();
            throw new BizException(BizErrorEnum.API_CAPTCHA_INVALID);
        }

        //误差范围为一个汉字的大小
        boolean success = ClickWordUtils.checkPoints(point, point1, HAN_ZI_SIZE);
        if (!success) {
            afterValidateFail();
            throw new BizException(BizErrorEnum.API_CAPTCHA_COORDINATE_ERROR);
        }

        //校验成功，将信息存入缓存
        String secretKey = point.get(0).getSecretKey();
        String verification;
        try {
            verification = AESUtil.aesEncrypt(captchaVO.getToken().concat("---").concat(pointJson), secretKey);
        } catch (Exception e) {
            logger.error("AES加密失败", e);
            afterValidateFail();
            throw new BizException(BizErrorEnum.API_CAPTCHA_COORDINATE_ERROR);
        }
        String secondKey = String.format(REDIS_SECOND_CAPTCHA_KEY, verification);
        CaptchaServiceFactory.getCache(cacheType).set(secondKey, captchaVO.getToken(), EXPIRATION_THREE);

        //返回结果
        return CheckResultVO.builder()
                .result(true)
                .verification(verification)
                .build();
    }

    @Override
    public VerifyResultVO verify(String captchaVerification) {
        super.verify(captchaVerification);
        try {
            String codeKey = String.format(REDIS_SECOND_CAPTCHA_KEY, captchaVerification);
            if (CaptchaServiceFactory.getCache(cacheType).noneExists(codeKey)) {
                throw new BizException(BizErrorEnum.API_CAPTCHA_INVALID);
            }
            //二次校验取值后，即刻失效
            CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
        } catch (Exception e) {
            throw new BizException(BizErrorEnum.API_CAPTCHA_INVALID);
        }
        return VerifyResultVO.builder()
                .result(true)
                .build();
    }

    public int getWordTotalCount() {
        return wordTotalCount;
    }

    public boolean isFontColorRandom() {
        return fontColorRandom;
    }

    /**
     * 点选文字 字体总个数
     */
    private int wordTotalCount = 4;

    /**
     * 点选文字 字体颜色是否随机
     */
    private final boolean fontColorRandom = Boolean.TRUE;

    private CaptchaVO getImageData(BufferedImage backgroundImage) {
        CaptchaVO captchaVO = new CaptchaVO();
        List<String> wordList = new ArrayList<>();
        List<PointVO> pointList = new ArrayList<>();

        Graphics2D graphics = (Graphics2D) backgroundImage.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int width = backgroundImage.getWidth();
        int height = backgroundImage.getHeight();

        //随机多少个字
        int wordCount = getWordTotalCount();
        int num = RandomUtils.getRandomInt(1, wordCount);
        Set<String> currentWords = ClickWordUtils.getRandomWords(wordCount);

        int i = 0;
        for (String word : currentWords) {

            //随机字体坐标
            PointVO point = ClickWordUtils.randomWordPoint(width, height, i, wordCount, HAN_ZI_SIZE);
            Color color;
            if (isFontColorRandom()) {
                //随机字体颜色
                color = new Color(RandomUtils.getRandomInt(1, 255), RandomUtils.getRandomInt(1, 255), RandomUtils.getRandomInt(1, 255));
            } else {
                //固定颜色
                color = Color.BLACK;
            }
            //设置
            WaterMarkUtils.waterMark(
                    graphics,
                    color,
                    //随机设置角度(-45度 -> 45度)
                    ClickWordUtils.fontRandomRotate(clickWordFont),
                    //水印字体
                    word,
                    //位置X
                    point.getX(),
                    //位置Y
                    point.getY()
            );

            //选择num-1个字,用于选择
            if ((num - 1) != i) {
                wordList.add(word);
                //缩小point的误差
                point.setX(point.getX() + (HAN_ZI_SIZE >> 1));
                point.setY(point.getY() - (HAN_ZI_SIZE >> 1));
                pointList.add(point);
            }
            i++;
        }

        //设置水印
        if (StringUtils.isNotBlank(waterMark)) {
            WaterMarkUtils.waterMark(
                    graphics,
                    Color.white,
                    waterMarkFont,
                    waterMark,
                    width - getEnOrChLength(waterMark),
                    height - (HAN_ZI_SIZE >> 1) + 7
            );
        }
        graphics.dispose();
        //创建合并图片
        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics combinedGraphics = combinedImage.getGraphics();
        combinedGraphics.drawImage(backgroundImage, 0, 0, null);
        captchaVO.setWordList(wordList);
        captchaVO.setPointList(pointList);
        captchaVO.setOriginalImageBase64(ImageUtils.getImageToBase64Str(backgroundImage).replaceAll(BASE64_REPLACE_REGEX, ""));
        captchaVO.setCaptchaType(captchaType());
        return captchaVO;
    }

}
