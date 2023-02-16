package com.geega.bsc.captcha.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geega.bsc.captch.common.base.BizErrorEnum;
import com.geega.bsc.captch.common.base.BizException;
import com.geega.bsc.captch.common.base.BizResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author Lin.Lv2
 * @description 从框架上抄来的统一返回值
 */
@RestControllerAdvice(basePackages = "com.geega")
@Slf4j
public class BizResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    ObjectMapper objectMapper;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.getParameterType() != BizResult.class;
    }

    @SuppressWarnings("NullableProblems")
    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (returnType.getParameterType().equals(String.class)) {
            BizResult<Object> bizResult = BizResult.success(body);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return objectMapper.writeValueAsString(bizResult);
        } else {
            return BizResult.success(body);
        }
    }

    @SuppressWarnings("rawtypes")
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public BizResult errorHandler(Exception ex) {
        log.error("请求id:{}#异常Code：{}#异常描述：{}", "no", BizErrorEnum.SYSTEM_ERROR.getCode(), ex.getMessage());
        ex.printStackTrace();
        if (ex instanceof BizException) {
            BizException bizException = (BizException) ex;
            return BizResult.error(bizException.getCode(), bizException.getMsg());
        }
        return BizResult.error(BizErrorEnum.SYSTEM_ERROR.getCode(), ex.getLocalizedMessage());
    }

}
