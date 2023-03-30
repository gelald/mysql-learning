package com.github.gelald.mysql.base.interceptor;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 给业务链路加上TRACE_ID
 *
 * @author WuYingBin
 * date: 2023/3/16
 */
public class LogInterceptor implements HandlerInterceptor {
    private static final String TRACE_ID = "TRACE_ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tid = UUID.randomUUID().toString().replace("-", "");
        //可以考虑让客户端传入链路ID，但需保证一定的复杂度唯一性；如果没使用默认UUID自动生成
        if (!StrUtil.isBlankIfStr(request.getHeader("TRACE_ID"))) {
            tid = request.getHeader("TRACE_ID");
        }
        MDC.put(TRACE_ID, tid);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(TRACE_ID);
    }
}
