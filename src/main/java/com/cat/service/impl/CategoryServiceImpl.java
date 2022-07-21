package com.cat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cat.common.CustomException;
import com.cat.mapper.CategoryMapper;
import com.cat.pojo.Category;
import com.cat.pojo.Dish;
import com.cat.pojo.Setmeal;
import com.cat.service.CategoryService;
import com.cat.service.DishService;
import com.cat.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据ID删除
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(Dish::getCategoryId,ids);
        int count = dishService.count(queryWrapper);
        if(count>0){
            throw new CustomException("当前分类关联了菜品，无法删除");
        }
        LambdaQueryWrapper<Setmeal> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(Setmeal::getCategoryId,ids);
        int count1 = setmealService.count(queryWrapper1);
        if(count1>0){
            throw new CustomException("当前分类关联了套餐，无法删除");
        }

        super.removeById(ids);
    }
}
