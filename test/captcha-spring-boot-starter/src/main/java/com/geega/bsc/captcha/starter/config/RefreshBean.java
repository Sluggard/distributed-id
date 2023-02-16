package com.geega.bsc.captcha.starter.config;

import com.geega.bsc.captch.common.enumeration.CaptchaTypeEnum;
import com.geega.bsc.captcha.starter.factory.CaptchaServiceFactory;
import com.geega.bsc.captcha.starter.service.CaptchaService;
import com.geega.bsc.captcha.starter.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static com.geega.bsc.captcha.starter.utils.UrlStreamUtils.getInputStreamByUrl;

/**
 * 图片刷新Bean
 *
 * @author Jun.An3
 * @date 2021/11/30
 */
@Slf4j
public class RefreshBean {

    private final RefreshConfig imageConfig;

    /**
     * 上次存放点选文件地址
     */
    private List<String> lastPickImages;

    /**
     * 上次存放滑块文件地址
     */
    private List<String> lastJigsawImages;

    public RefreshBean(RefreshConfig imageConfig) {
        this.imageConfig = imageConfig;
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(
                this::check,
                //延迟10s执行
                10000,
                //间隔10s执行任务
                10000,
                TimeUnit.MILLISECONDS
        );
    }

    private void check() {
        try {

            //最新的图片链接
            List<String> pickClickImages = imageConfig.getPicClickUrls();
            List<String> jigsawImages = imageConfig.getJigsawUrls();

            //从远程URL读取,转成base64字符串,刷新到内存中
            List<String> deleteUrls = refreshPickClickImages(pickClickImages, lastPickImages);
            if (!deleteUrls.isEmpty()) {
                CaptchaService captchaService = CaptchaServiceFactory.instances.get(CaptchaTypeEnum.CLICK_WORD.getCodeValue());
                captchaService.clearCache();
            }

            deleteUrls = refreshJigsawImages(jigsawImages, lastJigsawImages);
            if (!deleteUrls.isEmpty()) {
                CaptchaService captchaService = CaptchaServiceFactory.instances.get(CaptchaTypeEnum.BLOCK_PUZZLE.getCodeValue());
                captchaService.clearCache();
            }

            //设置最近一次图片地址
            lastPickImages = copyNew(pickClickImages);
            lastJigsawImages = copyNew(jigsawImages);

        } catch (Exception e) {
            log.error("更新图片异常:", e);
        }
    }

    /**
     * 刷新点选底图
     */
    public synchronized static List<String> refreshPickClickImages(List<String> pickClickImages, List<String> lastPickImages) {

        pickClickImages = nullList(pickClickImages);
        lastPickImages = nullList(lastPickImages);

        List<String> addUrls = new ArrayList<>();
        List<String> base64s = new ArrayList<>();
        List<String> deleteUrls = new ArrayList<>();

        for (String pickImageUrl : pickClickImages) {
            if (!lastPickImages.contains(pickImageUrl)) {
                try {
                    byte[] bytes = FileCopyUtils.copyToByteArray(getInputStreamByUrl(pickImageUrl));
                    String base64 = Base64Utils.encodeToString(bytes);
                    base64s.add(base64);
                    addUrls.add(pickImageUrl);
                } catch (Exception e) {
                    log.error("下载文件失败,url:{}", pickImageUrl);
                }
            }
        }

        ImageUtils.addPickImages(addUrls, base64s);

        //移除图片
        for (String pickImageUrl : lastPickImages) {
            if (!pickClickImages.contains(pickImageUrl)) {
                deleteUrls.add(pickImageUrl);
            }
        }

        ImageUtils.removePickImages(deleteUrls);
        return deleteUrls;
    }

    /**
     * 刷新滑图底图
     */
    public synchronized static List<String> refreshJigsawImages(List<String> jigsawImages, List<String> lastOriginImages) {

        jigsawImages = nullList(jigsawImages);
        lastOriginImages = nullList(lastOriginImages);

        List<String> addUrls = new ArrayList<>();
        List<String> base64s = new ArrayList<>();
        List<String> deleteUrls = new ArrayList<>();

        //新增图片
        for (String originImageUrl : jigsawImages) {
            if (!lastOriginImages.contains(originImageUrl)) {
                try {
                    byte[] bytes = FileCopyUtils.copyToByteArray(getInputStreamByUrl(originImageUrl));
                    String base64 = Base64Utils.encodeToString(bytes);
                    base64s.add(base64);
                    addUrls.add(originImageUrl);
                } catch (Exception e) {
                    log.error("下载文件失败,url:{}", originImageUrl);
                }
            }
        }

        ImageUtils.addOriginalImages(addUrls, base64s);

        //移除图片
        for (String originImage : lastOriginImages) {
            if (!jigsawImages.contains(originImage)) {
                deleteUrls.add(originImage);
            }
        }

        ImageUtils.removeOriginalImages(deleteUrls);
        return deleteUrls;
    }

    private static List<String> nullList(List<String> list) {
        return list == null ? new ArrayList<>() : list;
    }

    private static List<String> copyNew(List<String> list) {
        List<String> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            result.addAll(list);
        }
        return result;
    }

}