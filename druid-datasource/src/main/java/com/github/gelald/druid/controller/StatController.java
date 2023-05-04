package com.github.gelald.druid.controller;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author WuYingBin
 * date: 2023/5/3
 */
@RestController
@RequestMapping(value = "/druid")
public class StatController {
    @GetMapping("/stat")
    public List<Map<String, Object>> druidStat() {
        // 获取数据源的监控数据
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }
}
