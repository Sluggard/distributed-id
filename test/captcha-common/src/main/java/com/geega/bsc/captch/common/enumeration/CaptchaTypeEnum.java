package com.geega.bsc.captch.common.enumeration;

/**
 * @author Jun.An3
 * @date 2021/11/22
 */
@SuppressWarnings("unused")
public enum CaptchaTypeEnum {

    /**
     * 滑块拼图.
     */
    BLOCK_PUZZLE("blockPuzzle", "滑块拼图"),

    /**
     * 文字点选.
     */
    CLICK_WORD("clickWord", "文字点选"),

    /**
     * 默认.
     */
    DEFAULT("default", "默认");

    private final String codeValue;
    private final String codeDesc;

    CaptchaTypeEnum(String codeValue, String codeDesc) {
        this.codeValue = codeValue;
        this.codeDesc = codeDesc;
    }

    public String getCodeValue() {
        return this.codeValue;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    //根据codeValue获取枚举
    public static CaptchaTypeEnum parseFromCodeValue(String codeValue) {
        for (CaptchaTypeEnum e : CaptchaTypeEnum.values()) {
            if (e.codeValue.equals(codeValue)) {
                return e;
            }
        }
        return null;
    }

    //根据codeValue获取描述
    public static String getCodeDescByCodeValue(String codeValue) {
        CaptchaTypeEnum enumItem = parseFromCodeValue(codeValue);
        return enumItem == null ? "" : enumItem.getCodeDesc();
    }

    //验证codeValue是否有效
    public static boolean validateCodeValue(String codeValue) {
        return parseFromCodeValue(codeValue) != null;
    }

    //列出所有值字符串
    public static String getString() {
        StringBuilder buffer = new StringBuilder();
        for (CaptchaTypeEnum e : CaptchaTypeEnum.values()) {
            buffer.append(e.codeValue).append("--").append(e.getCodeDesc()).append(", ");
        }
        buffer.deleteCharAt(buffer.lastIndexOf(","));
        return buffer.toString().trim();
    }

}
