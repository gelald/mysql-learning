package com.github.gelald.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 控制导出数据到Excel文件的DTO
 *
 * @author WuYingBin
 * date: 2022/10/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelExportOption implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 每个Sheet写入多少条数据
     */
    private Integer perSheetRowCount = 20000;
    /**
     * 每次写入Sheet时写入多少条数据
     * <br/>
     * 因为一次查询一个Sheet的数据量可能有点大，需要分页查询，多次写入Sheet中
     */
    private Integer perWriteRowCount = 10000;
}
