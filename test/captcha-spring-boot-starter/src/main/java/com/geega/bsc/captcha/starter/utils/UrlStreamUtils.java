/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.geega.bsc.captcha.starter.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * url转成base64字符串
 *
 * @author Jun.An3
 * @date 2021/12/03
 */
@Slf4j
public class UrlStreamUtils {

    public static String url2Base64(String url) {
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(getInputStreamByUrl(url));
            return Base64Utils.encodeToString(bytes);
        } catch (Exception e) {
            log.error("下载文件失败,url:{}", url);
        }
        return null;
    }

    public static InputStream getInputStreamByUrl(String urlOrPath) throws IOException {
        URL url = new URL(urlOrPath);
        return url.openStream();
    }

}
