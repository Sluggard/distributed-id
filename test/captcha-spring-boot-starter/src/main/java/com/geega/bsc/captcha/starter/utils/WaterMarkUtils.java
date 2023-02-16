package com.geega.bsc.captcha.starter.utils;

import java.awt.*;
import java.nio.charset.StandardCharsets;

/**
 * 水印工具类
 *
 * @author Jun.An3
 * @date 2021/11/29
 */
public class WaterMarkUtils {

    public static void waterMark(Graphics graphics,
                                 Color color,
                                 Font waterMarkFont,
                                 String waterMark,
                                 int width,
                                 int height) {
        graphics.setColor(color);
        graphics.setFont(waterMarkFont);
        graphics.drawString(waterMark, width, height);
    }

}
