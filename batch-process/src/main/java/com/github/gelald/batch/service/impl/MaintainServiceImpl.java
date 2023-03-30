package com.github.gelald.batch.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.gelald.batch.dto.ExcelExportOption;
import com.github.gelald.batch.entity.Maintain;
import com.github.gelald.batch.mapper.MaintainMapper;
import com.github.gelald.batch.service.MaintainService;
import com.github.gelald.batch.util.HikariCPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    @Override
    public void resetAutoIncrement() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = HikariCPUtils.getConnection();
            String sql = "alter table batch_maintain auto_increment = 1";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            HikariCPUtils.close(preparedStatement, connection);
        }
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

    //2496189行，每个Sheet100w，分页查询每次查10w，每次写入时间2秒，文件232M，总时间321秒
    //2496189行，每个Sheet100w，分页查询每次查20w，每次写入时间4秒，文件232M，总时间239秒
    //2496189行，每个Sheet100w，分页查询每次查50w，每次写入时间10秒，文件232M，总时间138秒

    //2496189行，每个Sheet80w，分页查询每次查10w，每次写入时间2秒，文件232M，总时间312秒
    //2496189行，每个Sheet80w，分页查询每次查20w，每次写入时间4秒，文件232M，总时间223秒
    //2496189行，每个Sheet80w，分页查询每次查40w，每次写入时间10秒，文件232M，总时间176秒

    //1069056行，每个Sheet100w，分页查询每次查20w，每次写入时间4秒，总时间99M，总时间73秒


    //去掉日期索引后
    //1069056行，每个Sheet100w，分页查询每次查20w，每次写入时间4秒，文件99M，总时间64秒
    //2496189行，每个Sheet80w，分页查询每次查20w，每次写入时间4秒，文件232M，总时间118秒

    /*
    总共20000条记录

    SELECT * FROM excel_mass_data WHERE updated_date < '2016-06-01' LIMIT 0, 200000;
    没加日期索引前：0.49s
    加了日期索引后：1.33s

    SELECT * FROM excel_mass_data t1, (SELECT id FROM excel_mass_data WHERE updated_date < '2016-06-01' LIMIT 0, 200000) t2
     WHERE t1.id = t2.id;
    没加日期索引前：2.42s
    加了日期索引后：1.69s
     */

    /*
    总共10351条记录

    SELECT * FROM excel_mass_data WHERE updated_date < '2016-06-01' LIMIT 1000001, 200000;
    没加日期索引前：2.2s
    加了日期索引后：5.93s

    SELECT * FROM excel_mass_data t1, (SELECT id FROM excel_mass_data WHERE updated_date < '2016-06-01' LIMIT 1000001, 200000) t2
     WHERE t1.id = t2.id;
    没加日期索引前：1.25s
    加了日期索引后：0.27s
     */

    //修剪字段后
    //1069056行，每个Sheet100w，分页查询每次查20w，文件99M，总时间38秒
    //1069056行，每个Sheet100w，分页查询每次查10w，文件99M，总时间47秒

    /**
     * 500万条数据，每一个Sheet中不能存放太多数据，否则打开文件卡顿，所以规定1个Sheet存放100万条
     * 查询时为了防止一次查询的数据太大，使用分页查询，每页查询20万条
     * 需要计算写入多少个Sheet、每一个Sheet写入的次数、最后一个Sheet写入的数据量
     *
     * @param excelExportOption
     * @param response
     */
    @Override
    public void exportToExcel(ExcelExportOption excelExportOption, HttpServletResponse response) {
        log.info("每个Sheet写入的数据量是【{}】条", excelExportOption.getPerSheetRowCount());
        log.info("每次写入Sheet的数据量是【{}】条", excelExportOption.getPerWriteRowCount());
        log.info("每一Sheet需要写入【{}】条", excelExportOption.getPerSheetRowCount() / excelExportOption.getPerWriteRowCount());
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write();
        ExcelWriterSheetBuilder excelWriterSheetBuilder = EasyExcel.writerSheet();
        try (OutputStream outputStream = response.getOutputStream();
             ExcelWriter writer = excelWriterBuilder.excelType(ExcelTypeEnum.XLSX).head(Maintain.class).file(outputStream).build()) {
            StopWatch stopWatch = new StopWatch("export");
            stopWatch.start("获取满足条件的行数");
            // 根据条件获取需要查询的行数
            QueryWrapper<Maintain> queryWrapper = new QueryWrapper<>();
            // queryWrapper.lt("updated_date", LocalDate.of(excelExportOption.getYear(), 7, 1));
            long totalCount = this.count(queryWrapper);
            stopWatch.stop();
            log.info("满足条件的数据有【{}】", totalCount);
            stopWatch.start("计算需要使用的Sheet数以及最后一个Sheet写入数据的次数");
            // 根据总行数来计算需要存储多少个Sheet，每一个Sheet存储100w条，如果不能整除那就要再开一个Sheet
            long totalSheetCount;
            if (totalCount % excelExportOption.getPerSheetRowCount() == 0) {
                totalSheetCount = totalCount / excelExportOption.getPerSheetRowCount();
            } else {
                totalSheetCount = totalCount / excelExportOption.getPerSheetRowCount() + 1;
            }
            log.info("需要使用【{}】个Sheet来存储数据", totalSheetCount);
            // 计算每一页需要写入的次数（需要查询数据库的次数）
            long perSheetWriteTimes = excelExportOption.getPerSheetRowCount() / excelExportOption.getPerWriteRowCount();
            // 计算最后一页需要写入的次数
            long lastSheetWriteTimes;
            if (totalCount % excelExportOption.getPerSheetRowCount() == 0) {
                // 如果需要导出的总数据量能整除每一个Sheet写入的数据量，那么最后一个Sheet写入的数据量和前面的Sheet写入的数据量相同
                lastSheetWriteTimes = perSheetWriteTimes;
            } else {
                // 计算出最后一个Sheet前面需要写入的数据量
                long lastSheetWriteCount = totalCount - ((totalSheetCount - 1) * excelExportOption.getPerSheetRowCount());
                if (lastSheetWriteCount % excelExportOption.getPerWriteRowCount() == 0) {
                    lastSheetWriteTimes = lastSheetWriteCount / excelExportOption.getPerWriteRowCount();
                } else {
                    lastSheetWriteTimes = lastSheetWriteCount / excelExportOption.getPerWriteRowCount() + 1;
                }
            }
            log.info("最后一页需要写入的次数是【{}】次", lastSheetWriteTimes);
            stopWatch.stop();

            //开始分批查询分次写入
            //只查询这几个字段
            // queryWrapper.select("full_name", "age", "ip", "created_date", "updated_date");
            //注意这次的循环就需要进行嵌套循环了,外层循环是Sheet数目,内层循环是写入次数
            for (int i = 0; i < totalSheetCount; i++) {
                // 第一层循环是创建Sheet
                WriteSheet writeSheet = excelWriterSheetBuilder.sheetNo(i).sheetName("数据Sheet" + (i + 1)).build();
                log.info("当前插入第【{}】个Sheet", i + 1);
                for (int j = 0; j < (i != totalSheetCount - 1 ? perSheetWriteTimes : lastSheetWriteTimes); j++) {
                    stopWatch.start("第" + (i + 1) + "个Sheet第" + (j + 1) + "次分页查询");
                    // 第二层循环 分页查询数据，然后写入到Sheet中
                    IPage<Maintain> page = new Page<>(j + 1 + i * perSheetWriteTimes, excelExportOption.getPerWriteRowCount());
                    log.info("当前是第【{}】个Sheet插入的第【{}】次", i + 1, j + 1);
                    IPage<Maintain> pageResult = this.page(page, queryWrapper);
                    // 本轮需要插入的数据
                    List<Maintain> currentQueryResult = pageResult.getRecords();
                    log.info("本次插入数据有【{}】条", currentQueryResult.size());
                    stopWatch.stop();
                    stopWatch.start("第" + (i + 1) + "个Sheet第" + (j + 1) + "次数据写入");
                    writer.write(currentQueryResult, writeSheet);
                    stopWatch.stop();
                }
            }

            stopWatch.start("结束工作");
            // 下载EXCEL
            response.setHeader("Content-Disposition", "attachment;filename=ExportResult.xlsx");
            response.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            writer.finish();
            outputStream.flush();
            //导出时间结束
            stopWatch.stop();
            log.info("==========数据导出完成==========");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
