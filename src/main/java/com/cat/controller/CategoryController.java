package com.cat.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cat.common.R;
import com.cat.pojo.Category;
import com.cat.pojo.Employee;
import com.cat.service.CategoryService;
import com.cat.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    CategoryService service;

    /**
     * 新增分类
     * @param c
     * @return
     */
    @PostMapping
    public R<String> saveCategory(@RequestBody Category c){
        service.save(c);
        return null;
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        log.info("page={},pageSize={},name={}",page,pageSize);

        //构造分页构造器
        Page<Category> page1 = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        //添加排序条件
        queryWrapper.orderByDesc(Category::getSort);

        //执行分页查询
        service.page(page1,queryWrapper);
        return R.success(page1);
    }

    /**
     * 使用ID删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除id为:{}",ids);
        service.removeById(ids);
        return R.success("删除成功");
    }


    /**
     * 使用ID修改
     * @param c
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category c){
        service.updateById(c);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询
     * @param c
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category c){
        //条件构造器
        LambdaQueryWrapper<Category> qw=new LambdaQueryWrapper();

        //添加条件
        qw.eq(c.getType()!=null,Category::getType,c.getType());

        //添加排序条件
        qw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = service.list(qw);

        return  R.success(list);
    }
}


