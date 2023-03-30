package com.github.gelald.batch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author WuYingBin
 * date: 2023/3/28
 */
@SpringBootApplication(scanBasePackages = {
        "com.github.gelald.mysql.base",
        "com.github.gelald.batch"
})
@MapperScan(basePackages = "com.github.gelald.batch.mapper")
public class BatchProcessApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchProcessApplication.class, args);
    }
}
