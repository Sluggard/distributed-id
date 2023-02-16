package com.geega.bsc.captcha.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import java.util.List;

/**
 * 用于刷新图片的配置
 * 目前只限于nacos的自动刷新
 *
 * @author Jun.An3
 * @date 2021/11/24
 */
@Data
@RefreshScope
@ConfigurationProperties(RefreshConfig.PREFIX)
public class RefreshConfig {

    public static final String PREFIX = "captcha";

    /**
     * 原始滑动图片地址列表
     */
    private List<String> jigsawUrls;

    /**
     * 原始点选图片地址列表
     */
    private List<String> picClickUrls;

}
