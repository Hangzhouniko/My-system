package com.cqq.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqq.reggie.common.Result;
import com.cqq.reggie.pojo.Employee;
import com.cqq.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);
        if(emp==null){
            return Result.error("登录失败！用户不存在！");
        }
        if(!emp.getPassword().equals(password)){
            return Result.error("密码错误！");
        }
        if(emp.getStatus()==0){
            return Result.error("用户已禁用！");
        }

        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }


    /**
     * 登出账户
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功！");

    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());

//        employee.setUpdateTime(LocalDateTime.now());
//        long id = (long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);

//        try {
            employeeService.save(employee);
//        } catch (Exception e) {
//            log.error("新增失败！员工账户重复！");
//            log.error(e.getMessage());
//            Result.error("新增失败！");
//        }
        log.info("新增员工成功！");
        return Result.success("新增员工成功！");
    }


    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);




        Page<Employee> pageInfo =new Page(page,pageSize);

        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();

        queryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name);

        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo, queryWrapper);

        return Result.success(pageInfo);
    }


    /**
     * 根据id修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

//        Long id = (Long) request.getSession().getAttribute("employee");

//        employee.setUpdateUser(id);
//        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);

        return Result.success("修改成功！");
    }


    /**
     * 跳转员工信息修改页面
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable("id")Long id){
        Employee emp = employeeService.getById(id);
        if(emp==null){
            return Result.error("查询失败！");
        }
        log.info("返回查询结果！");
        return Result.success(emp);

    }


}
