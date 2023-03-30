package com.github.gelald.batch.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.github.gelald.batch.dto.ExcelImportOption;
import com.github.gelald.batch.entity.Maintain;
import com.github.gelald.batch.factory.ImportStrategyFactory;
import com.github.gelald.batch.strategy.AbstractImportStrategy;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author WuYingBin
 * date: 2022/10/31
 */
public class ExcelDataListener implements ReadListener<Maintain> {
    private final int rowsBoundary;

    private final String importStrategy;

    private final List<Maintain> excelDataList;

    public ExcelDataListener(ExcelImportOption excelImportOption) {
        this.rowsBoundary = excelImportOption.getRowsBoundary();
        this.excelDataList = ListUtils.newArrayListWithExpectedSize(rowsBoundary);
        this.importStrategy = excelImportOption.getImportStrategy();
    }

    @Override
    public void invoke(Maintain data, AnalysisContext context) {
        excelDataList.add(data);
        //size大于预定义的数量就执行一次批量插入
        if (excelDataList.size() >= rowsBoundary) {
            saveData(excelDataList);
            //清理集合便于GC回收
            excelDataList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //如果最后遍历完，集合还有数据，那么就需要把这一部分的数据也写入
        if (!CollectionUtils.isEmpty(excelDataList)) {
            saveData(excelDataList);
            excelDataList.clear();
        }
    }

    private void saveData(List<Maintain> excelDataList) {
        ImportStrategyFactory instance = ImportStrategyFactory.getInstance();
        AbstractImportStrategy strategy = instance.getStrategy(importStrategy);
        strategy.doImport(excelDataList);
    }
}
