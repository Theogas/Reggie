package com.cat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cat.common.CustomException;
import com.cat.dto.SetmealDto;
import com.cat.mapper.SetmealMapper;
import com.cat.pojo.Setmeal;
import com.cat.pojo.SetmealDish;
import com.cat.service.SetmealDishService;
import com.cat.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //修改套餐表
        this.save(setmealDto);
        //保存关系表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().peek((item)-> item.setSetmealId(setmealDto.getId())).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //封装条件
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(ids!=null,Setmeal::getId,ids);
        qw.eq(Setmeal::getStatus,1);


        int count = this.count(qw);
        //有在售则无法删除
        if(count>0){
            throw new CustomException("套餐在售，无法删除");
        }else {
            //先 删除Setmeal表
            this.removeByIds(ids);

            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,ids);

            //删除SetmealDish表
            setmealDishService.remove(queryWrapper);
        }
    }

    @Override
    @Transactional
    public SetmealDto getByIdWithDish(Long id) {
        //查询菜品的基本信息
        Setmeal setmeal = this.getById(id);

        //复制信息
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询关系表
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //更新Setmeal表
        this.updateById(setmealDto);

        //删除SetmealDish表中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //插入SetmealDish表中的数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }
}
