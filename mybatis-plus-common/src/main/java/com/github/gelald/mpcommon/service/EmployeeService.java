package com.github.gelald.mpcommon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.gelald.mpcommon.entity.Employee;

/**
 * @author WuYingBin
 * date: 2023/3/16
 */
public interface EmployeeService extends IService<Employee> {
    int deletePhysically(Long id);

    int updateAge();
}
