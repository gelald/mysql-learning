package com.github.gelald.batch.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.gelald.batch.entity.Maintain;
import com.github.gelald.batch.mapper.MaintainMapper;
import com.github.gelald.batch.service.MaintainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author WuYingBin
 * date: 2022/12/26
 */
@Slf4j
@Service
public class MaintainServiceImpl extends ServiceImpl<MaintainMapper, Maintain> implements MaintainService {
    private static final Long SHEET_MAX_SIZE = 1000000L;

    @Override
    public void clearDataAndResetAutoIncrement() {
        this.baseMapper.clearData();
        this.baseMapper.resetAutoIncrement();
    }

    @Override
    public List<Maintain> generatingMaintainList(int size) {
        LocalDateTime generationStart = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        LocalDateTime generationEnd = LocalDateTime.now();
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        List<Maintain> maintains = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {

            LocalDateTime startDateTime = LocalDateTime.ofEpochSecond(threadLocalRandom.nextLong(generationStart.toEpochSecond(ZoneOffset.ofHours(8)), generationEnd.toEpochSecond(ZoneOffset.ofHours(8))), 0, ZoneOffset.ofHours(8));
            LocalDateTime endDateTime = LocalDateTime.ofEpochSecond(threadLocalRandom.nextLong(startDateTime.toEpochSecond(ZoneOffset.ofHours(8)), generationEnd.toEpochSecond(ZoneOffset.ofHours(8))), 0, ZoneOffset.ofHours(8));

            Maintain maintain = new Maintain(
                    String.valueOf(Math.abs(threadLocalRandom.nextInt(77777777))),
                    "保修单" + (i + 1),
                    Math.abs(threadLocalRandom.nextLong(9999999)),
                    Math.abs(threadLocalRandom.nextInt(88888)),
                    "责任人" + (i + 1),
                    "2个月1次",
                    startDateTime,
                    endDateTime,
                    threadLocalRandom.nextBoolean()
            );
            maintains.add(maintain);
        }
        return maintains;
    }

    @Override
    public void exportToExcel(Long perReadSize, HttpServletResponse response) {
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write();
        ExcelWriterSheetBuilder excelWriterSheetBuilder = EasyExcel.writerSheet();
        StopWatch stopWatch = new StopWatch("export");
        try (OutputStream outputStream = response.getOutputStream();
             ExcelWriter writer = excelWriterBuilder.excelType(ExcelTypeEnum.XLSX).head(Maintain.class).file(outputStream).build()) {
            // 根据条件获取需要查询的行数
            long totalCount = this.count();
            log.info("满足条件的数据有【{}】", totalCount);
            // stopWatch.start("计算需要使用的Sheet数以及最后一个Sheet写入数据的次数");
            // 根据总行数来计算需要存储多少个Sheet，每一个Sheet存储100w条，如果不能整除那就要再开一个Sheet
            // 计算每一页需要写入的次数（需要查询数据库的次数）
            long totalSheetCount;
            long perSheetWriteTimes;
            long lastSheetWriteTimes;
            if (totalCount < SHEET_MAX_SIZE) {
                //如果总行数小于一个Sheet最大容纳行数，那么只需要一个Sheet
                totalSheetCount = 1;
                perSheetWriteTimes = totalCount / perReadSize;
                lastSheetWriteTimes = perSheetWriteTimes;
            } else {
                perSheetWriteTimes = SHEET_MAX_SIZE / perReadSize;
                if (totalCount % SHEET_MAX_SIZE == 0) {
                    //如果总行数大于一个Sheet最大容纳行数，且能整除这个最大行数，那么直接除法
                    totalSheetCount = totalCount / SHEET_MAX_SIZE;
                    lastSheetWriteTimes = perSheetWriteTimes;
                } else {
                    //如果总行数大于一个Sheet最大容纳行数，且不能整除这个最大行数，那么说明有余数，那么需要再额外增加一个Sheet来存储
                    totalSheetCount = totalCount / SHEET_MAX_SIZE + 1;
                    lastSheetWriteTimes = (totalCount - ((totalSheetCount - 1) * SHEET_MAX_SIZE)) / perReadSize;
                }
            }
            log.info("需要使用【{}】个Sheet来存储数据", totalSheetCount);

            //开始分批查询分次写入
            //注意这次的循环就需要进行嵌套循环了,外层循环是Sheet数目,内层循环是写入次数
            for (int i = 0; i < totalSheetCount; i++) {
                // 第一层循环是创建Sheet
                WriteSheet writeSheet = excelWriterSheetBuilder.sheetNo(i).sheetName("数据Sheet" + (i + 1)).build();
                log.info("当前插入第【{}】个Sheet", i + 1);
                for (int j = 0; j < (i != totalSheetCount - 1 ? perSheetWriteTimes : lastSheetWriteTimes); j++) {
                    stopWatch.start("第" + (i + 1) + "个Sheet第" + (j + 1) + "次分页查询");
                    // 第二层循环 分页查询数据，然后写入到Sheet中
                    long current = j + 1 + i * perSheetWriteTimes;
                    IPage<Maintain> page = new Page<>(current, perReadSize);
                    log.info("当前是第【{}】个Sheet插入的第【{}】次", i + 1, j + 1);
                    IPage<Maintain> pageResult = this.page(page);
                    // 本轮需要插入的数据
                    List<Maintain> currentQueryResult = pageResult.getRecords();
                    log.info("本次插入数据有【{}】条", currentQueryResult.size());
                    stopWatch.stop();
                    stopWatch.start("第" + (i + 1) + "个Sheet第" + (j + 1) + "次数据写入");
                    writer.write(currentQueryResult, writeSheet);
                    stopWatch.stop();
                }
            }

            // 下载EXCEL
            response.setHeader("Content-Disposition", "attachment;filename=ExportResult.xlsx");
            response.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            writer.finish();
            outputStream.flush();
            //导出时间结束
            log.info("==========数据导出完成==========");
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            System.out.println(stopWatch.prettyPrint());
        }
    }
}
