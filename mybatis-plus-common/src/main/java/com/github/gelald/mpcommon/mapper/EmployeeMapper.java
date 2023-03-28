package com.github.gelald.mpcommon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.gelald.mpcommon.entity.Employee;
import org.springframework.stereotype.Repository;

/**
 * @author WuYingBin
 * date: 2023/3/16
 */
@Repository
public interface EmployeeMapper extends BaseMapper<Employee> {
    int physicalDelete(Long id);

    int updateAge(Integer age);
}
