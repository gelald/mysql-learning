package com.github.gelald.mpcommon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.gelald.mpcommon.entity.Employee;
import com.github.gelald.mpcommon.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author WuYingBin
 * date: 2023/3/16
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private EmployeeService employeeService;

    @PostMapping("/add")
    public boolean addEmployee(@RequestBody Employee employee) {
        return this.employeeService.save(employee);
    }

    @GetMapping("/findAll")
    public List<Employee> findAllEmployee() {
        List<Employee> list = this.employeeService.list();
        log.info("查询出{}条数据", list.size());
        return list;
    }

    @GetMapping("/findById/{id}")
    public Employee findById(@PathVariable("id") Long id) {
        log.info("方法入参:{}", id);
        Employee employee = this.employeeService.getById(id);
        log.info("查询出一条数据:{}", employee);
        return employee;
    }

    @GetMapping("/page")
    public Page<Employee> page(@RequestParam Integer currentPage,
                               @RequestParam Integer pageSize) {
        log.info("分页查询，当前页码:{}，每页{}条数据", currentPage, pageSize);
        Page<Employee> page = new Page<>(currentPage, pageSize);
        return this.employeeService.page(page);
    }

    @GetMapping("/deleteById/{id}")
    public boolean deleteById(@PathVariable("id") Long id) {
        boolean b = this.employeeService.removeById(id);
        log.info("删除用户:{}成功", id);
        return b;
    }

    @GetMapping("/delete-physically/{id}")
    public int deletePhysically(@PathVariable("id") Long id) {
        int count = this.employeeService.deletePhysically(id);
        log.info("物理删除成功");
        return count;
    }

    @GetMapping("/update-age")
    public int updateAge() {
        int count = this.employeeService.updateAge();
        log.info("更新年龄成功");
        return count;
    }

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}
