package com.cat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cat.dto.DishDto;
import com.cat.pojo.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，并插入口味
    void  saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    void removeWithFlavor(List<Long> ids);
}
