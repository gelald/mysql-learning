package com.github.gelald.batch.enums;

import lombok.Getter;

/**
 * @author WuYingBin
 * date: 2022/12/26
 */
@Getter
public enum ImportStrategyEnum {
    JDBC_BATCH_STRATEGY("jdbc-batch"),
    MYBATIS_BATCH_STRATEGY("mybatis-batch"),
    MYBATIS_PLUS_BATCH_STRATEGY("mybatis-plus-batch");
    private final String strategyName;

    ImportStrategyEnum(String strategyName) {
        this.strategyName = strategyName;
    }
}
