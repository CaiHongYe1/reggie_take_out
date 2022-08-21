package com.chy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chy.reggie.common.R;
import com.chy.reggie.javabean.Employee;
import com.chy.reggie.mapper.EmployeeMapper;
import com.chy.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;

    /*
     *用户登录
     */
    @Override
    public R<Employee> login(Employee employee) {

//        将页面提交的密码password进行MD5加密处理
        String password = employee.getPassword();
        String username = employee.getUsername();

        password = DigestUtils.md5DigestAsHex(password.getBytes());

//        根据页面提交的用户名username查询数据库
        QueryWrapper<Employee> employeeQueryWrapper = new QueryWrapper<>();
        employeeQueryWrapper.eq("username",username);
        Employee empresult = employeeMapper.selectOne(employeeQueryWrapper);

//        如果没有查询到则返回用户不存在
        if(empresult == null)
            return R.error("用户不存在");

//        密码比对，如果不一致则返回密码错误
        if(!empresult.getPassword().equals(password))
            return R.error("密码错误");

//        查看员工状态，如果为1就是正常，如果是0就是禁用状态
        if(empresult.getStatus() == 0)
            return R.error("用户处于禁用状态");

//        查验全部通过, 可以成功登录
        return R.success(empresult);
    }

    /**
     * 测试案例 zhangsan 13487642314 330381200010033437
     * @param employee
     * @return
     */
    @Override
    public R<String> saveUser(Employee employee) {
        //        设置初始密码123456 需要进行MD5的加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        int insert = employeeMapper.insert(employee);
        return R.success("新增员工成功");
    }

    @Override
    public R<Page> getpage(Integer page, Integer pageSize, String name) {
//        判断name属性是否为空，如果为空就直接分页查询
        if(StringUtils.isEmpty(name)){
//            构造分页构造器
            Page<Employee> pageresult = new Page<>(page,pageSize);
//            构造条件过滤器
            QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("update_time");
            employeeMapper.selectPage(pageresult, queryWrapper);
            return R.success(pageresult);
        }

//        如果不为空,还要加上查询条件
//            构造分页构造器
        Page<Employee> pageresult = new Page<>(1,pageSize);
//            构造条件过滤器
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("update_time");
        queryWrapper.like("name",name);
        employeeMapper.selectPage(pageresult,queryWrapper);
        return R.success(pageresult);
    }
}
