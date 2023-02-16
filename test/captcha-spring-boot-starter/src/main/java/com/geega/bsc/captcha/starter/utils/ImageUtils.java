
package com.geega.bsc.captcha.starter.utils;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.captch.common.enumeration.CaptchaBaseMapEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证图片的工具类
 * 图片数据都存储在这里
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
public class ImageUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    //滑块底图
    private static final Map<String, String> ORIGINAL_CACHE_MAP = new ConcurrentHashMap<>();

    //滑块
    private static final Map<String, String> SLIDING_BLOCK_CACHE_MAP = new ConcurrentHashMap<>();

    //点选文字
    private static final Map<String, String> PIC_CLICK_CACHE_MAP = new ConcurrentHashMap<>();

    //文件名
    private static final Map<String, List<String>> FILE_NAME_MAP = new ConcurrentHashMap<>();

    /**
     * 超时1s,获取不到,就返回
     */
    private static final long TIME_OUT = 1000;

    /**
     * 滑块是否有数据
     */
    public static boolean jigsawIsEmpty() {
        return ORIGINAL_CACHE_MAP.isEmpty();
    }

    /**
     * 点选是否有数据
     */
    public static boolean picClickIsEmpty() {
        return PIC_CLICK_CACHE_MAP.isEmpty();
    }

    public static void cacheImage(Map<String, String> originalMap, Map<String, String> slidingBlockMap, Map<String, String> picClickMap) {
        //缓存文件名与文件base64数据映射图
        if (originalMap != null) {
            ORIGINAL_CACHE_MAP.putAll(originalMap);
            fileNamePutAll(CaptchaBaseMapEnum.ORIGINAL.getCodeValue(), ORIGINAL_CACHE_MAP.keySet());
        }
        if (slidingBlockMap != null) {
            SLIDING_BLOCK_CACHE_MAP.putAll(slidingBlockMap);
            fileNamePutAll(CaptchaBaseMapEnum.SLIDING_BLOCK.getCodeValue(), SLIDING_BLOCK_CACHE_MAP.keySet());
        }
        if (picClickMap != null) {
            PIC_CLICK_CACHE_MAP.putAll(picClickMap);
            fileNamePutAll(CaptchaBaseMapEnum.PIC_CLICK.getCodeValue(), PIC_CLICK_CACHE_MAP.keySet());
        }
        logger.info("缓存图:{}", JSON.toJSONString(FILE_NAME_MAP));
    }

    private static void fileNamePutAll(String key, Set<String> values) {
        List<String> fileNames = FILE_NAME_MAP.get(key);
        if (fileNames == null || fileNames.size() == 0) {
            fileNames = new ArrayList<>();
            FILE_NAME_MAP.put(key, fileNames);
        }
        if (values != null && values.size() > 0) {
            fileNames.addAll(values);
        }
    }

    @SuppressWarnings("unused")
    public static BufferedImage getOriginal(long timeout) {
        synchronized (FILE_NAME_MAP) {
            return innerGetOriginal(timeout);
        }
    }

    public static BufferedImage getOriginal() {
        synchronized (FILE_NAME_MAP) {
            return innerGetOriginal(TIME_OUT);
        }
    }

    private static BufferedImage innerGetOriginal(long timeout) {
        long left = timeout;
        long begin = DateUtils.currentTimeStamp();
        while (left > 0) {
            BufferedImage original = innerGetOriginal();
            if (original != null) {
                return original;
            } else {
                try {
                    //睡眠20ms
                    Thread.sleep(20);
                    left -= (DateUtils.currentTimeStamp() - begin);
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    public static BufferedImage innerGetOriginal() {
        try {
            List<String> fileNames = FILE_NAME_MAP.get(CaptchaBaseMapEnum.ORIGINAL.getCodeValue());
            if (null == fileNames || fileNames.size() == 0) {
                return null;
            }
            int randomInt = RandomUtils.getRandomInt(0, fileNames.size());
            String imageBase64 = ORIGINAL_CACHE_MAP.get(fileNames.get(randomInt));
            return base64StrToImage(imageBase64);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取滑块类型底图异常:", e);
        }
        return null;
    }

    public static String getSlidingBlock() {
        try {
            synchronized (FILE_NAME_MAP) {
                List<String> fileNames = FILE_NAME_MAP.get(CaptchaBaseMapEnum.SLIDING_BLOCK.getCodeValue());
                if (null == fileNames || fileNames.size() == 0) {
                    return null;
                }
                int randomInt = RandomUtils.getRandomInt(0, fileNames.size());
                return SLIDING_BLOCK_CACHE_MAP.get(fileNames.get(randomInt));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取滑块异常:", e);
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static BufferedImage getPicClick(long timeout) {
        synchronized (FILE_NAME_MAP) {
            return innerGetPicClick(timeout);
        }
    }

    public static BufferedImage getPicClick() {
        synchronized (FILE_NAME_MAP) {
            return innerGetPicClick(TIME_OUT);
        }
    }

    public static BufferedImage innerGetPicClick(long timeout) {
        long left = timeout;
        long begin = DateUtils.currentTimeStamp();
        while (left > 0) {
            BufferedImage picClick = innerGetPicClick();
            if (picClick != null) {
                return picClick;
            } else {
                try {
                    //睡眠20ms
                    Thread.sleep(20);
                    left -= (DateUtils.currentTimeStamp() - begin);
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    public static BufferedImage innerGetPicClick() {
        try {
            List<String> fileNames = FILE_NAME_MAP.get(CaptchaBaseMapEnum.PIC_CLICK.getCodeValue());
            if (null == fileNames || fileNames.size() == 0) {
                return null;
            }
            int randomInt = RandomUtils.getRandomInt(0, fileNames.size());
            String s = PIC_CLICK_CACHE_MAP.get(fileNames.get(randomInt));
            return base64StrToImage(s);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取点选图片底图异常:", e);
        }
        return null;
    }

    /**
     * 图片转base64 字符串
     */
    public static String getImageToBase64Str(BufferedImage templateImage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(templateImage, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = outputStream.toByteArray();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(bytes).trim();
    }

    /**
     * base64 字符串转图片
     */
    public static BufferedImage base64StrToImage(String base64String) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] bytes = decoder.decode(base64String);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("base64StrToImage，base64转图片异常");
        }
        return null;
    }

    public static void removePickImages(List<String> urls) {
        if (isNotBlank(urls)) {
            try {
                synchronized (FILE_NAME_MAP) {
                    logger.info("从nacos移除了点选底图:" + JacksonUtils.objectToString(urls));
                    List<String> fileNames = FILE_NAME_MAP.get(CaptchaBaseMapEnum.PIC_CLICK.getCodeValue());
                    for (String url : urls) {
                        PIC_CLICK_CACHE_MAP.remove(url);
                        if (!fileNames.isEmpty()) {
                            fileNames.remove(url);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("移除点选底图异常,", e);
            }
        }
    }

    public static void removeOriginalImages(List<String> urls) {
        if (isNotBlank(urls)) {
            try {
                synchronized (FILE_NAME_MAP) {
                    logger.info("从nacos移除了滑块底图:" + JacksonUtils.objectToString(urls));
                    List<String> fileNames = FILE_NAME_MAP.get(CaptchaBaseMapEnum.ORIGINAL.getCodeValue());
                    for (String url : urls) {
                        ORIGINAL_CACHE_MAP.remove(url);
                        if (!fileNames.isEmpty()) {
                            fileNames.remove(url);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("移除滑块底图异常,", e);
            }
        }
    }

    public static void addPickImages(List<String> urls, List<String> base64s) {
        if (isNotBlank(urls)) {
            try {
                synchronized (FILE_NAME_MAP) {
                    logger.info("从nacos加载了点选底图:{}", JacksonUtils.objectToString(urls));
                    FILE_NAME_MAP.putIfAbsent(CaptchaBaseMapEnum.PIC_CLICK.getCodeValue(), new ArrayList<>());
                    for (int i = 0; i < urls.size(); i++) {
                        String url = urls.get(i);
                        PIC_CLICK_CACHE_MAP.put(url, base64s.get(i));
                        boolean contains = FILE_NAME_MAP.get(CaptchaBaseMapEnum.PIC_CLICK.getCodeValue()).contains(url);
                        if (!contains) {
                            FILE_NAME_MAP.get(CaptchaBaseMapEnum.PIC_CLICK.getCodeValue()).add(url);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("加载点选底图异常,", e);
            }
        }
    }

    public static void addOriginalImages(List<String> urls, List<String> base64s) {
        if (isNotBlank(urls)) {
            try {
                synchronized (FILE_NAME_MAP) {
                    logger.info("从nacos加载了滑块底图:{}", JacksonUtils.objectToString(urls));
                    FILE_NAME_MAP.putIfAbsent(CaptchaBaseMapEnum.ORIGINAL.getCodeValue(), new ArrayList<>());
                    for (int i = 0; i < urls.size(); i++) {
                        String url = urls.get(i);
                        ORIGINAL_CACHE_MAP.put(url, base64s.get(i));
                        boolean contains = FILE_NAME_MAP.get(CaptchaBaseMapEnum.ORIGINAL.getCodeValue()).contains(url);
                        if (!contains) {
                            FILE_NAME_MAP.get(CaptchaBaseMapEnum.ORIGINAL.getCodeValue()).add(url);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("加载滑块底图异常,", e);
            }
        }
    }

    private static boolean isNotBlank(List<String> list) {
        return list != null && !list.isEmpty();
    }

}
