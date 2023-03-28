package com.github.gelald.mpcommon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author WuYingBin
 * date: 2023/3/28
 */
@SpringBootApplication(scanBasePackages = {
        "com.github.gelald.mysql.base",
        "com.github.gelald.mpcommon"
})
@MapperScan(basePackages = "com.github.gelald.mpcommon.mapper")
public class MyBatisPlusCommonApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyBatisPlusCommonApplication.class, args);
    }
}
