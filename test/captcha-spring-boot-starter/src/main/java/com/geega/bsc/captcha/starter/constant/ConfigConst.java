package com.geega.bsc.captcha.starter.constant;

/**
 * 配置文件常量值
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
public interface ConfigConst {

    /**
     * 预加载图片数量
     */
    String CACHE_NUM = "captcha.cache-num";

    /**
     * 预加载图片替换更新时间
     */
    String CACHE_REFRESH_PERIOD = "captcha.cache-refresh-period";

    /**
     * 当自定义图片无效或者没配置时,默认使用组件自带的底图
     */
    String USE_DEFAULT_WHEN_CUSTOM_IMAGES_EMPTY = "use.default.when.custom.images.empty";

    /**
     * 滑块底图路径
     */
    String ORIGINAL_PATH_JIGSAW = "captcha.captchaOriginalPath.jigsaw";

    /**
     * 点选底图路径
     */
    String ORIGINAL_PATH_PIC_CLICK = "captcha.captchaOriginalPath.pic-click";

    /**
     * 缓存local/redis...
     */
    String CAPTCHA_CACHE_TYPE = "captcha.cacheType";

    /**
     * 右下角水印文字(我的水印)
     */
    String CAPTCHA_WATER_MARK = "captcha.water.mark";

    /**
     * 点选文字验证码的文字字体(宋体)
     */
    String CAPTCHA_FONT_TYPE = "captcha.font.type";

    String CAPTCHA_FONT_STYLE = "captcha.font.style";

    String CAPTCHA_FONT_SIZE = "captcha.font.size";

    /**
     * 验证码类型default两种都实例化。
     */
    String CAPTCHA_TYPE = "captcha.type";

    /**
     * 滑动干扰项(0/1/2)
     */
    String CAPTCHA_INTERFERENCE_OPTIONS = "captcha.interference.options";

    /**
     * 底图自定义初始化
     */
    @SuppressWarnings("unused")
    String CAPTCHA_INIT_ORIGINAL = "captcha.init.original";

    /**
     * 滑动误差偏移量
     */
    String CAPTCHA_SLIP_OFFSET = "captcha.slip.offset";

    /**
     * 右下角水印字体(宋体)
     */
    String CAPTCHA_WATER_FONT = "captcha.water.font";

    /**
     * local缓存的阈值
     */
    String CAPTCHA_CACAHE_MAX_NUMBER = "captcha.cache.number";

    /**
     * 定时清理过期local缓存，秒
     */
    String CAPTCHA_TIMING_CLEAR_SECOND = "captcha.timing.clear";

    /**
     * 历史资源清除开关 0禁用,1 开启
     */
    String HISTORY_DATA_CLEAR_ENABLE = "captcha.history.data.clear.enable";

    /**
     * 接口限流开关 0禁用 1启用
     */
    String REQ_FREQUENCY_LIMIT_ENABLE = "captcha.req.frequency.limit.enable";

    /**
     * get 接口 10秒钟请求次数限制
     */
    String REQ_PERIOD_LIMIT = "captcha.req.period.limit";

    /**
     * get 接口 10秒钟请求次数限制
     */
    String REQ_GET_MINUTE_LIMIT = "captcha.req.get.minute.limit";

    /**
     * 验证失败后，get接口锁定时间
     */
    String REQ_GET_LOCK_LIMIT = "captcha.req.get.lock.limit";

    /**
     * 验证失败后，get接口锁定时间
     */
    String REQ_GET_LOCK_SECONDS = "captcha.req.get.lock.seconds";

    /**
     * verify 接口 10秒钟请求次数限制
     */
    String REQ_VALIDATE_MINUTE_LIMIT = "captcha.req.verify.minute.limit";

    /**
     * check接口 10秒钟请求次数限制
     */
    String REQ_CHECK_MINUTE_LIMIT = "captcha.req.check.minute.limit";

    /***
     * 点选文字个数
     */
    String CAPTCHA_WORD_COUNT = "captcha.word.count";

    /**
     * http header中,表示客户端id的键
     */
    String CLIENT_ID_HEADER_KEY = "client.id.header.key";

    /**
     * 是否开启测试
     */
    String CAPTCHA_TEST  = "captcha.test";

}
