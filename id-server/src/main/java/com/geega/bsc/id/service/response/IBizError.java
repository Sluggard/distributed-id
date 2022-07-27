package com.geega.bsc.id.service.response;

/**
 * @author Jun.An3
 * @date 2021/04/30
 */
public interface IBizError {

    /**
     * 状态码
     *
     * @return 状态码
     */
    String getCode();

    /**
     * 消息
     *
     * @return 消息
     */
    String getMsg();

}
