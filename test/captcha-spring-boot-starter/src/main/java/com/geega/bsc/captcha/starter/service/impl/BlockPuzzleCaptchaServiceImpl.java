
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
import com.geega.bsc.captcha.starter.factory.CaptchaServiceFactory;
import com.geega.bsc.captcha.starter.utils.AESUtil;
import com.geega.bsc.captcha.starter.utils.BlockPuzzleUtils;
import com.geega.bsc.captcha.starter.utils.ImageUtils;
import com.geega.bsc.captcha.starter.utils.SnowFlakeUtils;
import com.geega.bsc.captcha.starter.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Properties;

/**
 * @author Jun.An3
 * @date 2021/11/25
 */
@Slf4j
public class BlockPuzzleCaptchaServiceImpl extends AbstractCaptchaService {

    private ImageCache imageCache;

    @Override
    public void init(Properties config) {
        super.init(config);
        this.imageCache = new ImageCache(this, getCacheNum(config), getCacheRefreshPeriod(config));
    }

    @Override
    public void clearCache() {
        imageCache.clearCache();
    }

    /**
     * 生产做好的图
     */
    @Override
    public CaptchaVO generate() {
        try {
            //原生图片
            BufferedImage originalImage = ImageUtils.getOriginal();
            if (null == originalImage) {
                return null;
            }
            if (!"".equals(waterMark)) {
                //设置水印
                Graphics backgroundGraphics = originalImage.getGraphics();
                int width = originalImage.getWidth();
                int height = originalImage.getHeight();
                backgroundGraphics.setFont(waterMarkFont);
                backgroundGraphics.setColor(Color.white);
                backgroundGraphics.drawString(waterMark, width - getEnOrChLength(waterMark), height - (HAN_ZI_SIZE >> 1) + 7);
            }
            //抠图图片
            String jigsawImageBase64 = ImageUtils.getSlidingBlock();
            BufferedImage jigsawImage = ImageUtils.base64StrToImage(jigsawImageBase64);
            if (null == jigsawImage) {
                logger.error("滑动底图未初始化成功，请检查路径");
                return null;
            }
            return pictureTemplatesCut(originalImage, jigsawImage, jigsawImageBase64);
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public void destroy(Properties config) {
        //do nothing
    }

    @Override
    public String captchaType() {
        return CaptchaTypeEnum.BLOCK_PUZZLE.getCodeValue();
    }

    @Override
    public CaptchaVO get(String captchaType) {
        //父类只做限流
        super.get(captchaType);
        //从缓存中获取图片数据
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
        captcha.getPoint().setSecretKey(secretKey);
        captcha.setToken(SnowFlakeUtils.instance.next());
        //将坐标信息存入缓存中
        String codeKey = String.format(REDIS_CAPTCHA_KEY, captcha.getToken(), captchaType());
        CaptchaServiceFactory.getCache(cacheType).set(codeKey, JSON.toJSONString(captcha.getPoint()), EXPIRATION_SECONDS);
        //返回结果
        noNeedSetNull(captcha);
        return captcha;
    }

    private boolean captchaIsNotValid(CaptchaVO captcha) {
        return captcha == null
                || StringUtils.isBlank(captcha.getJigsawImageBase64())
                || StringUtils.isBlank(captcha.getOriginalImageBase64());
    }

    @Override
    public CheckResultVO check(CheckCaptchaVO captchaVO) {
        super.check(captchaVO);
        //取坐标信息
        String codeKey = String.format(REDIS_CAPTCHA_KEY, captchaVO.getToken(), captchaVO.getCaptchaType());
        String cachePointJson = CaptchaServiceFactory.getCache(cacheType).get(codeKey);
        if (StringUtils.isBlank(cachePointJson)) {
            throw new BizException(BizErrorEnum.API_CAPTCHA_INVALID);
        }
        //验证码只用一次，即刻失效
        CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
        PointVO point;
        PointVO point1;
        String pointJson;
        try {
            point = JSON.parseObject(cachePointJson, PointVO.class);
            //aes解密
            pointJson = decrypt(captchaVO.getPointJson(), point.getSecretKey());
            point1 = JSON.parseObject(pointJson, PointVO.class);
        } catch (Exception e) {
            logger.error("验证码坐标解析失败", e);
            afterValidateFail();
            throw new BizException(BizErrorEnum.API_CAPTCHA_COORDINATE_ERROR);
        }

        //检查行为轨迹是否正确
        boolean isRight = BlockPuzzleUtils.checkPoint(point, point1, Integer.parseInt(slipOffset));
        if (!isRight) {
            afterValidateFail();
            throw new BizException(BizErrorEnum.API_CAPTCHA_COORDINATE_ERROR);
        }

        //校验成功，将信息存入缓存
        String secretKey = point.getSecretKey();
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

        //返回结果,返回了是否成功和一次校验时的下次校验的验证
        return CheckResultVO.builder()
                .result(true)
                .verification(verification)
                .build();
    }

    @Override
    public VerifyResultVO verify(String captchaVerification) {
        super.verify(captchaVerification);
        try {
            String secondKey = String.format(REDIS_SECOND_CAPTCHA_KEY, captchaVerification);
            if (CaptchaServiceFactory.getCache(cacheType).noneExists(secondKey)) {
                throw new BizException(BizErrorEnum.API_CAPTCHA_INVALID);
            }
            //二次校验取值后，即刻失效
            CaptchaServiceFactory.getCache(cacheType).delete(captchaVerification);
        } catch (Exception e) {
            logger.error("验证码坐标解析失败", e);
            throw new BizException(BizErrorEnum.API_CAPTCHA_INVALID);
        }
        return VerifyResultVO.builder()
                .result(true)
                .build();
    }

    /**
     * 根据模板切图
     */
    public CaptchaVO pictureTemplatesCut(BufferedImage originalImage, BufferedImage jigsawImage, String jigsawImageBase64) {
        try {
            CaptchaVO dataVO = new CaptchaVO();

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int jigsawWidth = jigsawImage.getWidth();
            int jigsawHeight = jigsawImage.getHeight();

            //随机生成拼图坐标
            PointVO point = BlockPuzzleUtils.generateJigsawPoint(originalWidth, originalHeight, jigsawWidth, jigsawHeight);
            int x = point.getX();
            //生成新的拼图图像
            BufferedImage newJigsawImage = new BufferedImage(jigsawWidth, jigsawHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = newJigsawImage.createGraphics();
            //如果需要生成RGB格式，需要做如下配置,Transparency 设置透明
            newJigsawImage = graphics.getDeviceConfiguration().createCompatibleImage(jigsawWidth, jigsawHeight, Transparency.TRANSLUCENT);
            // 新建的图像根据模板颜色赋值,源图生成遮罩
            BlockPuzzleUtils.cutByTemplate(originalImage, jigsawImage, newJigsawImage, x, 0);
            // 设置“抗锯齿”的属性
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawImage(newJigsawImage, 0, 0, null);
            graphics.dispose();
            //新建流。
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            //利用ImageIO类提供的write方法，将bi以png图片的数据模式写入流。
            ImageIO.write(newJigsawImage, IMAGE_TYPE_PNG, os);
            byte[] jigsawImages = os.toByteArray();
            //新建流
            ByteArrayOutputStream oriImagesOs = new ByteArrayOutputStream();
            //利用ImageIO类提供的write方法，将bi以jpg图片的数据模式写入流
            ImageIO.write(originalImage, IMAGE_TYPE_PNG, oriImagesOs);
            byte[] oriCopyImages = oriImagesOs.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            dataVO.setOriginalImageBase64(encoder.encodeToString(oriCopyImages).replaceAll(BASE64_REPLACE_REGEX, BASE64_REPLACE));
            dataVO.setJigsawImageBase64(encoder.encodeToString(jigsawImages).replaceAll(BASE64_REPLACE_REGEX, BASE64_REPLACE));
            dataVO.setPoint(point);
            dataVO.setCaptchaType(captchaType());
            //将坐标信息存入缓存中
            logger.debug("token：{},point:{}", dataVO.getToken(), JSON.toJSONString(point));
            return dataVO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
