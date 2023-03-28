package com.github.gelald.mysql.base.configuration;

import com.github.gelald.mysql.base.property.KnifeProperties;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author WuYingBin
 * date: 2023/3/15
 */
public abstract class BaseKnifeConfiguration {
    @Bean
    public Docket createRestApi() {
        KnifeProperties knifeProperties = properties();
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo(knifeProperties))
                .select()
                .apis(RequestHandlerSelectors.basePackage(knifeProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(KnifeProperties knifeProperties) {
        return new ApiInfoBuilder()
                .title(knifeProperties.getTitle())
                .description(knifeProperties.getTitle())
                .contact(new Contact("gelald", "https://gelald.github.io/javrin/", "yb.ng@foxmail.com"))
                .version("0.0.1-SNAPSHOT")
                .build();
    }

    public abstract KnifeProperties properties();
}
