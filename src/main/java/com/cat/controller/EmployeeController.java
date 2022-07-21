package com.cat.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cat.common.R;
import com.cat.pojo.Employee;
import com.cat.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    /**
     * 员工登录
     * @param request
     * @param e
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee e){
        //1.对密码进行md5加密
        String password = e.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        //2.查询数据库
        LambdaQueryWrapper<Employee> qw=new LambdaQueryWrapper<>();
        qw.eq(Employee::getUsername,e.getUsername());
        Employee employee = service.getOne(qw);

        //3.未查询到则返回失败
        if(employee==null){
            return R.error("账号不存在");
        }

        //4.对比密码，不同则返回失败
        if(!employee.getPassword().equals(password)){
            return R.error("登录失败，密码错误");
        }

        //5.检测是否被禁用
        if(employee.getStatus()==0){
            return R.error("账号被禁用");
        }

        //6.登录成功,将id存入session
        request.getSession().setAttribute("employee",employee.getId());
        return R.success(employee);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param e
     * @param request
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee e){
        log.info("添加员工:{}",e.toString());
        //添加默认数据
        String s = DigestUtils.md5DigestAsHex("123456".getBytes());
        e.setPassword(s);
        //2.存入数据库
        service.save(e);

        //3.返回成功信息
        return R.success("添加成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //构造分页构造器
        Page page1 = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行分页查询
        service.page(page1,queryWrapper);
        return R.success(page1);
    }


    /**
     * 修改员工信息
     * @param e
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee e){
        log.info("修改中");
        //更新数据
        service.updateById(e);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        log.info("根据ID查询");
        Employee e = service.getById(id);
        //判断此用户是否存在
        if(e!=null){
            return R.success(e);
        }
        return  R.error("不存在此用户");
    }
}
