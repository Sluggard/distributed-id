package com.geega.bsc.id.service.response;

/**
 * @author Jun.An3
 * @date 2021/04/30
 */
public enum BizError implements IBizError {
    /**
     * 系统异常
     */
    SUCCESS("0000", "success"),
    ;

    BizError(String code, String msg) {
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
