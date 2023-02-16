package com.geega.bsc.captcha.starter.service;

import com.geega.bsc.captch.common.base.BizErrorEnum;
import com.geega.bsc.captch.common.base.BizException;
import com.geega.bsc.captch.common.vo.CheckCaptchaVO;
import com.geega.bsc.captcha.starter.constant.ConfigConst;
import com.geega.bsc.captcha.starter.context.ClientIdThreadLocal;
import com.geega.bsc.captcha.starter.utils.StringUtils;
import java.util.Objects;
import java.util.Properties;

/**
 * 限流实现类
 *
 * @author Jun.An3
 * @date 2021/11/25
 */
public interface FrequencyLimitHandler {

    String LIMIT_KEY = "CAPTCHA:REQ:LIMIT-%s-%s";

    String GET = "GET";

    String LOCK = "LOCK";

    String FAIL = "FAIL";

    String CHECK = "CHECK";

    String VERIFY = "VERIFY";

    String LIMIT_VALUE = "limit";

    String FAIL_VALUE = "fail";

    /**
     * get 接口限流
     */
    void validateGet();

    /**
     * check接口限流
     */
    void validateCheck(CheckCaptchaVO captchaVO);

    /**
     * verify接口限流
     */
    void validateVerify(String clientId);

    /***
     * 验证码接口限流:
     * 客户端ClientUid 组件实例化时设置一次，如：场景码+UUID，客户端可以本地缓存,保证一个组件只有一个值
     *
     * 针对同一个客户端的请求，做如下限制:
     * get
     * 	 10秒钟check失败5次，锁定5s
     * 	 10秒钟不能超过5次。
     * check:
     *   10秒钟不超过5次
     * verify:
     *   10秒钟不超过5次
     */
    class DefaultLimitHandler implements FrequencyLimitHandler {

        private final Properties config;

        //缓存接口,会有两种方式(本地缓存和分布式缓存)
        private final CaptchaCacheService cacheService;

        public DefaultLimitHandler(Properties config, CaptchaCacheService cacheService) {
            this.config = config;
            this.cacheService = cacheService;
        }

        private String getClientCId(String clientId, String type) {
            return String.format(LIMIT_KEY, type, clientId);
        }

        @Override
        public void validateGet() {

            String clientId = ClientIdThreadLocal.get();
            // 无客户端身份标识，不限制
            if (StringUtils.isEmpty(clientId)) {
                return;
            }

            // 失败次数验证
            String failKey = getClientCId(clientId, FAIL);
            String getKey = getClientCId(clientId, GET);
            String lockKey = getClientCId(clientId, LOCK);

            //先判断是否是锁定状态
            String value = cacheService.get(lockKey);
            if (Objects.nonNull(value)) {
                switch (value) {
                    case FAIL_VALUE:
                        throw new BizException(BizErrorEnum.API_REQ_LOCK_GET_ERROR);
                    case LIMIT_VALUE:
                        throw new BizException(BizErrorEnum.API_REQ_LIMIT_GET_ERROR);
                }
            }

            String failCents = cacheService.get(failKey);
            // 10秒钟失败5次，就要被锁定5s
            if (!Objects.isNull(failCents) && Long.parseLong(failCents) > Long.parseLong(config.getProperty(ConfigConst.REQ_GET_LOCK_LIMIT, "5"))) {
                // get接口锁定5s
                cacheService.delete(failKey);
                cacheService.set(lockKey, FAIL_VALUE, Long.parseLong(config.getProperty(ConfigConst.REQ_GET_LOCK_SECONDS, "5")));
                throw new BizException(BizErrorEnum.API_REQ_LOCK_GET_ERROR);
            }

            String getCents = cacheService.get(getKey);
            if (Objects.isNull(getCents)) {
                cacheService.set(getKey, "1", Long.parseLong(config.getProperty(ConfigConst.REQ_PERIOD_LIMIT, "10")));
                getCents = "1";
            }
            cacheService.increment(getKey, 1);
            // 10秒钟请求次数过多
            if (Long.parseLong(getCents) > Long.parseLong(config.getProperty(ConfigConst.REQ_GET_MINUTE_LIMIT, "5"))) {
                cacheService.delete(getKey);
                cacheService.set(lockKey, LIMIT_VALUE, Long.parseLong(config.getProperty(ConfigConst.REQ_GET_LOCK_SECONDS, "5")));
                throw new BizException(BizErrorEnum.API_REQ_LIMIT_GET_ERROR);
            }
        }

        @Override
        public void validateCheck(CheckCaptchaVO captchaVO) {
            String clientId = ClientIdThreadLocal.get();
            // 无客户端身份标识，不限制
            if (StringUtils.isEmpty(clientId)) {
                return;
            }
            String checkKey = getClientCId(clientId, CHECK);
            String v = cacheService.get(checkKey);
            if (Objects.isNull(v)) {
                cacheService.set(checkKey, "1", Long.parseLong(config.getProperty(ConfigConst.REQ_PERIOD_LIMIT, "10")));
                v = "1";
            }
            cacheService.increment(checkKey, 1);
            if (Long.parseLong(v) > Long.parseLong(config.getProperty(ConfigConst.REQ_CHECK_MINUTE_LIMIT, "5"))) {
                throw new BizException(BizErrorEnum.API_REQ_LOCK_GET_ERROR);
            }
        }

        @Override
        public void validateVerify(String clientId) {
            String verifyKey = getClientCId(clientId, VERIFY);
            String v = cacheService.get(verifyKey);
            if (Objects.isNull(v)) {
                cacheService.set(verifyKey, "1", Long.parseLong(config.getProperty(ConfigConst.REQ_PERIOD_LIMIT, "10")));
                v = "1";
            }
            cacheService.increment(verifyKey, 1);
            if (Long.parseLong(v) > Long.parseLong(config.getProperty(ConfigConst.REQ_VALIDATE_MINUTE_LIMIT, "5"))) {
                throw new BizException(BizErrorEnum.API_REQ_LOCK_GET_ERROR);
            }
        }

    }

}