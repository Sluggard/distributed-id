package com.geega.bsc.captcha.starter.aspect;

import com.alibaba.fastjson.JSON;
import com.geega.bsc.captcha.starter.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Aspect
@Slf4j
@Order(-98)
public class LogAspect {

    /**
     * 拦截controller
     */
    @Pointcut("execution(public * com.geega.bsc.captcha.starter.controller..*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        //请求controller名称，使用@ControllerMethodTitle注解
        String controllerTitle = getControllerMethodTitle(joinPoint);
        //方法路径
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        //IP地址
        String iP = getIp(request);
        //请求入参
        String requestParam = JSON.toJSONString(Arrays.stream(joinPoint.getArgs())
                .filter(param -> !(param instanceof HttpServletRequest)
                        && !(param instanceof HttpServletResponse)
                        && !(param instanceof MultipartFile)
                        && !(param instanceof MultipartFile[])
                ).collect(Collectors.toList()));

        log.info("\n[Controller start], {}, methodName->{}, IP->{}, requestParam->{},\n", controllerTitle, methodName, iP, requestParam);

        long begin = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        log.info("\n[Controller end], {}, 耗时->{}ms\n", controllerTitle, System.currentTimeMillis() - begin);
        return result;
    }

    /**
     * 获取Controller的方法名
     */
    private String getControllerMethodTitle(ProceedingJoinPoint joinPoint) {
        Method[] methods = joinPoint.getSignature().getDeclaringType().getMethods();
        for (Method method : methods) {
            if (StringUtils.equalsIgnoreCase(method.getName(), joinPoint.getSignature().getName())) {
                ControllerMethodTitle annotation = method.getAnnotation(ControllerMethodTitle.class);
                if (ObjectUtils.isNotEmpty(annotation)) {
                    return annotation.value();
                }
            }
        }
        return "空";
    }

    /**
     * 获取目标主机的ip
     */
    private String getIp(HttpServletRequest request) {
        List<String> ipHeadList = Stream.of("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "X-Real-IP").collect(Collectors.toList());
        for (String ipHead : ipHeadList) {
            if (checkIP(request.getHeader(ipHead))) {
                return request.getHeader(ipHead).split(",")[0];
            }
        }
        return "0:0:0:0:0:0:0:1".equals(request.getRemoteAddr()) ? "127.0.0.1" : request.getRemoteAddr();
    }

    /**
     * 检查ip存在
     */
    private boolean checkIP(String ip) {
        return !(null == ip || 0 == ip.length() || "unknown".equalsIgnoreCase(ip));
    }

}