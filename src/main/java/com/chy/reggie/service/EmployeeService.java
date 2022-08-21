package com.chy.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chy.reggie.common.R;
import com.chy.reggie.javabean.Employee;

public interface EmployeeService extends IService<Employee> {
    R<Employee> login(Employee employee);
    R<String> saveUser(Employee employee);
    R<Page> getpage(Integer page,Integer pageSize,String name);

}
