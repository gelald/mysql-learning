package com.github.gelald.batch.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate类型在EasyExcel组件中的转换器
 *
 * @author WuYingBin
 * date: 2022/10/28
 */
public class LocalDateConverter implements Converter<LocalDate> {

    /**
     * 从Excel中读取出来的日期不是一个日期类型，而是一个数字类型的数据，代表着1900年之后的N天
     */
    private static final LocalDate START_DATE = LocalDate.of(1900, Month.JANUARY, 1);
    /**
     * Excel计算日期的误差bug，1900年1月1日加上那个数字计算出来的日期要比原日期后两天，计算时要减去这个误差
     */
    private static final int CALCULATION_ERROR = 2;
    /**
     * 导出时日期格式化
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDate convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                       GlobalConfiguration globalConfiguration) {
        long days = cellData.getNumberValue().longValue();
        return START_DATE.plusDays(days - CALCULATION_ERROR);
    }

    @Override
    public WriteCellData<?> convertToExcelData(LocalDate value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(value.format(DATE_FORMATTER));
    }
}
