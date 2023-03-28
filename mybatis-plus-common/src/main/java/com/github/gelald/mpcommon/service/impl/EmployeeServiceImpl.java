package com.github.gelald.mpcommon.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.gelald.mpcommon.entity.Employee;
import com.github.gelald.mpcommon.mapper.EmployeeMapper;
import com.github.gelald.mpcommon.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author WuYingBin
 * date: 2023/3/16
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public int deletePhysically(Long id) {
        return this.getBaseMapper().physicalDelete(id);
    }

    @Override
    public int updateAge() {
        ThreadLocalRandom random = RandomUtil.getRandom();
        int age = random.nextInt(10, 30);
        this.getBaseMapper().updateAge(age);
        return age;
    }
}
