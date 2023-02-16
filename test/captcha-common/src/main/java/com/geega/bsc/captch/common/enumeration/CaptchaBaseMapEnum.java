package com.geega.bsc.captch.common.enumeration;

/**
 * 底图类型枚举
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
@SuppressWarnings("unused")
public enum CaptchaBaseMapEnum {

    ORIGINAL("ORIGINAL", "滑动拼图底图"),
    SLIDING_BLOCK("SLIDING_BLOCK", "滑动拼图滑块底图"),
    PIC_CLICK("PIC_CLICK", "文字点选底图");

    private final String codeValue;
    private final String codeDesc;

    CaptchaBaseMapEnum(String codeValue, String codeDesc) {
        this.codeValue = codeValue;
        this.codeDesc = codeDesc;
    }

    public String getCodeValue() {
        return this.codeValue;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

}
