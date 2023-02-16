package com.geega.bsc.captcha.client.starter.utils;

import com.geega.bsc.captch.common.vo.PointVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 替换掉fastjson，自定义实现相关方法
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
public class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    public static String toJSONString(Object object) {
        if (object == null) {
            return "{}";
        }
        if (object instanceof PointVO) {
            PointVO t = (PointVO) object;
            return t.toJsonString();
        }
        if (object instanceof List) {
            //noinspection unchecked
            List<PointVO> list = (List<PointVO>) object;
            StringBuilder buf = new StringBuilder("[");
            list.forEach(t -> buf.append(t.toJsonString()).append(","));
            return buf.deleteCharAt(buf.lastIndexOf(",")).append("]").toString();
        }
        if (object instanceof Map) {
            //noinspection rawtypes
            return ((Map) object).entrySet().toString();
        }
        throw new UnsupportedOperationException("不支持的输入类型:"
                + object.getClass().getSimpleName());
    }
}
