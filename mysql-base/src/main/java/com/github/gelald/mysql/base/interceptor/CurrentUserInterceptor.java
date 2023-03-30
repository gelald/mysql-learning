package com.github.gelald.mysql.base.interceptor;

import cn.hutool.core.util.StrUtil;
import com.github.gelald.mysql.base.context.CurrentUserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器 把当前登录人存放到内存中
 *
 * @author WuYingBin
 * date: 2023/3/16
 */
public class CurrentUserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从request中获取当前的用户id
        String userId = request.getHeader("userId");
        if (StrUtil.isBlankIfStr(userId)) {
            throw new RuntimeException("请求头中没有携带userId，请求不能通过");
        }
        // 把用户id放到当前上下文中
        try {
            CurrentUserContext.setId(Long.parseLong(userId));
        } catch (NumberFormatException numberFormatException) {
            throw new RuntimeException("userId不是数字格式，请求不能通过");
        }
        // 过滤器链继续执行
        return true;
    }
}
