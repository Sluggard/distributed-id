package com.geega.bsc.captch.common.base;

/**
 * @Author : Lin.Lv2
 * @Time : 2021/5/25 14:50
 * @Description: 所有的枚举类都需要实现该接口，保持统一性
 **/
@SuppressWarnings("unused")
public interface IBizEnum {

    /**
     * 获取code
     */
    Integer getCode();

    /**
     * 获取文本
     */
    String getText();
}
