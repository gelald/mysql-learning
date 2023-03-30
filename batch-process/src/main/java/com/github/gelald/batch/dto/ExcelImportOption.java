package com.github.gelald.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author WuYingBin
 * date: 2022/11/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelImportOption implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 每读取多少行保存一次
     */
    private Integer rowsBoundary = 50000;

    /**
     * 执行导入的策略
     */
    private String importStrategy;
}
