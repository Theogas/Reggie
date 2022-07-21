package com.cat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cat.pojo.Category;

public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
