package com.geega.bsc.captcha.starter.interceptor;

import com.geega.bsc.captcha.starter.constant.HttpHeaderConst;
import com.geega.bsc.captcha.starter.context.ClientIdThreadLocal;
import com.geega.bsc.captcha.starter.properties.CaptchaConfig;
import com.geega.bsc.captcha.starter.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于获取客户端ID
 *
 * @author Jun.An3
 * @date 2021/10/21
 */
@Slf4j
public class ClientIdInterceptor implements HandlerInterceptor {

    @SuppressWarnings("unused")
    @Autowired
    private CaptchaConfig captchaConfig;

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIdHeaderKey = captchaConfig.getClientIdHeaderKey();
        String clientId = request.getHeader(clientIdHeaderKey);
        if (StringUtils.isBlank(clientId)) {
            clientId = getClientId(request);
        }
        ClientIdThreadLocal.set(clientId);
        return true;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        //释放ThreadLocal的数据
        ClientIdThreadLocal.reset();
    }

    /**
     * 从header中组装客户端id
     */
    protected String getClientId(HttpServletRequest request) {
        String fwd = request.getHeader(HttpHeaderConst.X_FORWARDED_FOR);
        String ip = getRemoteIpFromFwd(fwd);
        String userAgent = request.getHeader(HttpHeaderConst.USER_AGENT);
        if (StringUtils.isNotBlank(ip)) {
            return ip + userAgent;
        }
        return request.getRemoteAddr() + userAgent;
    }

    protected String getRemoteIpFromFwd(String fwd) {
        if (StringUtils.isNotBlank(fwd)) {
            String[] ipList = fwd.split(HttpHeaderConst.COMMA);
            return StringUtils.trim(ipList[0]);
        }
        return null;
    }

}
