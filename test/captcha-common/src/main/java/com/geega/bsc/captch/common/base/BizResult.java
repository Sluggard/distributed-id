package com.geega.bsc.captch.common.base;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @Author : Lin.Lv2
 * @Time : 2021/8/3 15:05
 * @Description: 统一返回值
 **/
@SuppressWarnings({"rawtypes", "unchecked"})
@Data
@Slf4j
public class BizResult<T> implements Serializable {
    private String code;
    private T data;
    private String msg;

    public static <T> BizResult<T> success(T obj) {
        BizResult bizResult = new BizResult();
        bizResult.setCode(BizErrorEnum.SUCCESS.getCode());
        bizResult.setMsg(BizErrorEnum.SUCCESS.getMsg());
        bizResult.setData(obj);
        return bizResult;

    }

    public static <T> BizResult<T> error(T obj) {
        BizResult bizResult = new BizResult();
        bizResult.setCode(BizErrorEnum.SYSTEM_ERROR.getCode());
        bizResult.setMsg(BizErrorEnum.SYSTEM_ERROR.getMsg());
        bizResult.setData(obj);
        return bizResult;

    }

    public static <T> BizResult<T> error(String code, String msg) {
        BizResult bizResult = new BizResult();
        bizResult.setCode(code);
        bizResult.setMsg(msg);
        return bizResult;
    }

    /**
     * 判断是否返回成功,失败并打印日志
     */
    public static Boolean checkIsSuccess(@SuppressWarnings("rawtypes") BizResult bizResult) {
        return bizResult != null && bizResult.getCode().equals(BizErrorEnum.SUCCESS.getCode());
    }
}
