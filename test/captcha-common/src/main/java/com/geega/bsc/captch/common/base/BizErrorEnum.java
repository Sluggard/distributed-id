package com.geega.bsc.captch.common.base;

/**
 * @Author : Lin.Lv2
 * @Time : 2021/5/24 13:18
 * @Description:
 **/
public enum BizErrorEnum implements IBizError {
    /**
     * 1000以下为系统级错误
     */
    SUCCESS("0000", "success"),
    SYSTEM_ERROR("0001", "system error"),
    OBJECT_EMPTY("1005", "对象不能为空！"),
    REMOTE_CALL_FAIL("1006", "远程调用失败！"),

    /**
     * 0001 - 0099 网关应答码
     */
    API_CAPTCHA_INVALID("6110", "验证码已失效，请重新获取"),
    API_CAPTCHA_COORDINATE_ERROR("6111", "验证失败"),
    API_CAPTCHA_ERROR("6112", "获取验证码失败,请联系管理员"),

    API_REQ_LIMIT_GET_ERROR("6201", "获取验证码请求次数超限！"),
    API_REQ_LOCK_GET_ERROR("6202", "接口验证失败次数过多！"),

    RESOURCES_TYPE_NULL("1000", "未配置验证图片类型！"),
    ;

    BizErrorEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final String code;

    private final String msg;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
