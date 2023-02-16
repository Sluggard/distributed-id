package com.geega.bsc.captch.common.base;

/**
 * 业务异常
 * */
public class BizException extends RuntimeException implements IBizError {

    private String code;
    private String msg;

    public BizException(IBizError iBizError) {
        super(iBizError.getMsg());

        this.code = iBizError.getCode();
        this.msg = iBizError.getMsg();
    }

    @SuppressWarnings("unused")
    public BizException(IBizError iBizError, Object ...args) {
        super(mergeCodeMsg(iBizError.getCode(), formatMsg(iBizError.getMsg(), args)));

        this.code = iBizError.getCode();
        this.msg = formatMsg(iBizError.getMsg(), args);
    }

    public BizException(String code, String msg) {
        super(mergeCodeMsg(code, msg));

        this.code = code;
        this.msg = msg;
    }

    @SuppressWarnings("unused")
    public BizException(String code, String msg, Object ...args) {
        super(mergeCodeMsg(code, formatMsg(msg, args)));

        this.code = code;
        this.msg = formatMsg(msg, args);
    }

    private static String mergeCodeMsg(String code, String msg) {
        return String.format("%s %s", code, msg);
    }

    private static String formatMsg(String msg, Object ...args) {
        if (msg == null) {
            return null;
        }

        return String.format(msg, args);
    }


    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @SuppressWarnings("unused")
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
