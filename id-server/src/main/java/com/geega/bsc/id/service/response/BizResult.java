package com.geega.bsc.id.service.response;

import java.io.Serializable;

/**
 * @author Jun.An3
 * @date 2021/04/30
 */
public class BizResult<T> implements Serializable, IBizError {

    private T result;

    private String code;

    private String msg;

    public BizResult() {
    }

    public BizResult(T result, String code, String msg) {
        this.result = result;
        this.code = code;
        this.msg = msg;
    }

    public BizResult(IBizError iBizError) {
        this.code = iBizError.getCode();
        this.msg = iBizError.getMsg();
    }

    public BizResult(T result, IBizError iBizError) {
        this.code = iBizError.getCode();
        this.msg = iBizError.getMsg();
        this.result = result;
    }

    public static <T> BizResult<T> success() {
        return new BizResult<>(BizError.SUCCESS);
    }

    public static <T> BizResult<T> success(T data) {
        BizResult<T> bizResult = new BizResult<>(BizError.SUCCESS);
        bizResult.setResult(data);
        return bizResult;
    }

    public static <T> BizResult<T> error(IBizError iBizError) {
        return new BizResult<>(iBizError);
    }

    public static <T> BizResult<T> error(String code, String msg) {
        return new BizResult<>(null, code, msg);
    }

    public static <T> BizResult<T> error(T result, IBizError iBizError) {
        return new BizResult<>(result, iBizError);
    }

    public static <T> BizResult<T> error(T result, String code, String msg) {
        return new BizResult<>(result, code, msg);
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
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

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
