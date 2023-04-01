package com.github.gelald.batch.controller;

import com.alibaba.excel.EasyExcel;
import com.github.gelald.batch.entity.Maintain;
import com.github.gelald.batch.enums.ImportStrategyEnum;
import com.github.gelald.batch.factory.ImportStrategyFactory;
import com.github.gelald.batch.listener.ExcelDataListener;
import com.github.gelald.batch.mapper.MaintainMapper;
import com.github.gelald.batch.service.MaintainService;
import com.github.gelald.batch.strategy.AbstractImportStrategy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.List;

/**
 * @author WuYingBin
 * date: 2022/12/26
 */
@Api("批量操作")
@RestController
@RequestMapping("/maintain")
public class MaintainController {
    private MaintainMapper maintainMapper;

    private MaintainService maintainService;

    @ApiOperation("清除数据并预热连接池")
    @GetMapping("/clear")
    public String clearData() {
        //删除数据的同时激活两个连接池
        //使用了MyBatisPlus创建的连接池
        this.maintainMapper.clearData();
        //使用了自定义的连接池
        this.maintainService.resetAutoIncrement();
        return "success";
    }

    @ApiOperation("程序生成并批量导入数据")
    @GetMapping("/generate-and-import")
    public String generateData(@RequestParam("size") @Min(1) @Max(400000) Integer size,
                               @RequestParam("enum") ImportStrategyEnum importStrategyEnum) {
        List<Maintain> maintains = this.maintainService.generatingMaintainList(size);
        String strategyName = importStrategyEnum.getStrategyName();
        ImportStrategyFactory instance = ImportStrategyFactory.getInstance();
        AbstractImportStrategy strategy = instance.getStrategy(strategyName);
        strategy.doImport(maintains);
        return "success";
    }

    @ApiOperation("Excel数据批量导入到数据库")
    @PostMapping(value = "/import-from-excel", consumes = "multipart/form-data;charset=UTF-8")
    public String importFromExcel(@RequestPart("file") MultipartFile multipartFile,
                                  @RequestParam("savePerScanRow") Integer savePerScanRow,
                                  @RequestParam("importStrategy") ImportStrategyEnum importStrategyEnum) throws IOException {
        EasyExcel.read(multipartFile.getInputStream(), Maintain.class, new ExcelDataListener(savePerScanRow, importStrategyEnum)).doReadAll();
        return "success";
    }

    @ApiOperation("数据批量导出到Excel文件")
    @GetMapping(value = "/export-to-excel", produces = "multipart/form-data;charset=UTF-8")
    public void exportToExcel(@RequestParam("perReadSize") Long perReadSize, HttpServletResponse response) {
        this.maintainService.exportToExcel(perReadSize, response);
    }

    @Autowired
    public void setMaintainMapper(MaintainMapper maintainMapper) {
        this.maintainMapper = maintainMapper;
    }

    @Autowired
    public void setMaintainService(MaintainService maintainService) {
        this.maintainService = maintainService;
    }
}
