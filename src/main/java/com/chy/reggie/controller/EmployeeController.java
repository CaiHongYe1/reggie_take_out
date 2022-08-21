package com.chy.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chy.reggie.common.R;
import com.chy.reggie.javabean.Employee;
import com.chy.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @param employee
     * @param request
     * @return
     */
    @RequestMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        log.info("成功进入EmployeeController的login方法");
        R<Employee> result = employeeService.login(employee);
//        如果返回结果为登录失败则直接返回结果
        if(result.getCode() == 0)
            return result;

//        如果登录状态是成功,则保存session,并把结果集返回
        HttpSession session = request.getSession();
        session.setAttribute("employee",result.getData().getId());
        return result;
    }


    /**
     * 退出登录
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    public R<String> logout(HttpServletRequest request){
//        清理Session中保存的当前员工的ID
        HttpSession session = request.getSession();
        session.removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     *  新建员工
     */
    @PostMapping
    public R<String> sava(@RequestBody Employee employee,HttpServletRequest request){
        log.info("新增员工信息：{}",employee.toString());
//        获得当前登录的用户的ID
        Long createAndupdateUserId = (Long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(createAndupdateUserId);
//        employee.setUpdateUser(createAndupdateUserId);
        R<String> result = employeeService.saveUser(employee);
        return result;
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @RequestMapping("/page")
    public R<Page> getpage(Integer page,Integer pageSize,String name){
        R<Page> result = employeeService.getpage(page, pageSize, name);
        return result;
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request){
//        获取当前登录的账号的ID
        Long LoginEmployeeId = (Long)request.getSession().getAttribute("employee");
//        设置修改时间和修改用户的ID
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(LoginEmployeeId);
//        执行根据id修改方法
        boolean result = employeeService.updateById(employee);
        if(!result)
            return R.error("修改失败");
        return R.success("修改成功");
    }

    /**
     * 通过ID查找员工
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public R<Employee> getEmpById(@PathVariable("id") Long id){
//        通过id查询员工
        Employee resultemp = employeeService.getById(id);

        if(resultemp == null){
            return R.error("查找失败");
        }

        return R.success(resultemp);
    }
}
