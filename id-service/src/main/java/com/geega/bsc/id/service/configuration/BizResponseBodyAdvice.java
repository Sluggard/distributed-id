package com.geega.bsc.id.service.configuration;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.id.service.response.BizResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import java.net.URI;

/**
 * 返回值封装处理类
 *
 * @author Jun.An3
 * @date 2021/04/30
 */
@SuppressWarnings("NullableProblems")
@Slf4j
@ControllerAdvice
public class BizResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 判断是否是BizResult类
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.getParameterType() != BizResult.class;
    }

    /**
     * 将返回值封装为BizResult
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        //排除swagger相关接口
        final URI uri = request.getURI();
        if (uri.getPath().contains("/favicon.ico")
                || uri.getPath().contains("/v2/api-docs")
                || uri.getPath().contains("/swagger-resources")) {
            return body;
        }
        //因为handler处理类的返回类型是String，为了保证一致性，这里需要将BizResult转回去
        final BizResult<?> result = BizResult.success(body);
        if (body instanceof String) {
            return JSON.toJSONString(result);
        }
        return result;
    }

}
