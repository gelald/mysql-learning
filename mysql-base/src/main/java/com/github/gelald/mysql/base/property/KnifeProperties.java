package com.github.gelald.mysql.base.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author WuYingBin
 * date: 2023/3/15
 */
@Data
@Component
@ConfigurationProperties(prefix = "knife")
public class KnifeProperties {
    /**
     * knife4j文档标题
     */
    private String title;
    /**
     * 需要生成knife4j文档的包路径
     */
    private String basePackage;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
