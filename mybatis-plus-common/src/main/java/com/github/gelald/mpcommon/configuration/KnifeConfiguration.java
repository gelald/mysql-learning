package com.github.gelald.mpcommon.configuration;

import com.github.gelald.mysql.base.configuration.BaseKnifeConfiguration;
import com.github.gelald.mysql.base.property.KnifeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j文档配置类
 *
 * @author WuYingBin
 * date: 2023/3/16
 */
@Configuration
public class KnifeConfiguration extends BaseKnifeConfiguration {
    private KnifeProperties knifeProperties;

    @Override
    public KnifeProperties properties() {
        return this.knifeProperties;
    }

    @Autowired
    public void setKnifeProperties(KnifeProperties knifeProperties) {
        this.knifeProperties = knifeProperties;
    }
}
