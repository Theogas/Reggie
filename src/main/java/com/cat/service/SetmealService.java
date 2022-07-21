package com.cat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cat.dto.SetmealDto;
import com.cat.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);
}
