
package com.geega.bsc.captch.common.enumeration;

/**
 * @author Jun.An3
 * @date 2021/11/22
 */
public enum ResourcesTypeCodeEnum {

    DEFAULT("default", "默认"),
    CUSTOM("custom", "自定义"),
    COMBINE("combine", "两种方式"),
    ;
    private final String code;
    private final String desc;

    ResourcesTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    @SuppressWarnings("unused")
    public String getDesc() {
        return desc;
    }

    public String getName() {
        return this.name();
    }

    public static ResourcesTypeCodeEnum find(String code) {
        for (ResourcesTypeCodeEnum value : values()) {
            if (code != null && code.equals(value.code)) {
                return value;
            }
        }
        return null;
    }
}
