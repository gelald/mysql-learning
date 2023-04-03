package com.github.gelald.batch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.gelald.batch.entity.Maintain;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author WuYingBin
 * date: 2022/12/26
 */
public interface MaintainService extends IService<Maintain> {
    /**
     * 删除数据的同时激活连接池
     */
    void clearDataAndResetAutoIncrement();

    List<Maintain> generatingMaintainList(int size);

    void exportToExcel(Long perReadSize, HttpServletResponse response);
}
