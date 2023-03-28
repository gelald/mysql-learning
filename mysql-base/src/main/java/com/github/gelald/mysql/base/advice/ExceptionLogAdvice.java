package com.github.gelald.mysql.base.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author WuYingBin
 * date: 2023/3/15
 */
@Slf4j
@Aspect
@Component
public class ExceptionLogAdvice {
    //基于异常拦截器注解，更准确
    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public void exceptionHandler() {

    }

    @Before(value = "exceptionHandler()")
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Exception) {
            Exception exception = (Exception) args[0];
            log.error(exception.getMessage());
        }
    }
}
