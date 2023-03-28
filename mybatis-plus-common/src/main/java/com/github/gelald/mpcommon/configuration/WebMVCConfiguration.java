package com.github.gelald.mpcommon.configuration;

import com.github.gelald.mpcommon.interceptor.CurrentUserInterceptor;
import com.github.gelald.mpcommon.interceptor.LogInterceptor;
import com.github.gelald.mysql.base.configuration.BaseWebMVCConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * MVC拦截器配置类
 *
 * @author WuYingBin
 * date: 2023/3/16
 */
@Configuration
public class WebMVCConfiguration extends BaseWebMVCConfiguration {
    private CurrentUserInterceptor currentUserInterceptor;
    private LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(currentUserInterceptor)
                .addPathPatterns("/**/add**/**")
                .addPathPatterns("/**/update**/**")
                //因为引入了逻辑删除
                .addPathPatterns("/**/delete**/**");
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**")
                //最高级
                .order(-1);
    }

    @Autowired
    public void setCurrentUserInterceptor(CurrentUserInterceptor currentUserInterceptor) {
        this.currentUserInterceptor = currentUserInterceptor;
    }

    @Autowired
    public void setLogInterceptor(LogInterceptor logInterceptor) {
        this.logInterceptor = logInterceptor;
    }
}
