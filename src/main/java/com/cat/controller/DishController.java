package com.cat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cat.common.R;
import com.cat.dto.DishDto;
import com.cat.pojo.Category;
import com.cat.pojo.Dish;
import com.cat.pojo.DishFlavor;
import com.cat.service.CategoryService;
import com.cat.service.DishFlavorService;
import com.cat.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService service;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     * @param d
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody  DishDto d){
        log.info("新增菜品中");
        service.saveWithFlavor(d);
        return R.success("新增成功");
    }

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        service.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    /**
     * 查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable long id){
        DishDto dish = service.getByIdWithFlavor(id);
        return R.success(dish);
    }

    /**
     * 更新菜品
     * @param d
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto d){
        service.updateWithFlavor(d);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询多个菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>>  list(Dish dish){
        //添加条件构造器
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(Dish::getCategoryId, dish.getCategoryId());
        qw.eq(Dish::getStatus,1);
        qw.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
        //查询结果
        List<Dish> list = service.list(qw);

        List<DishDto> dishDtos = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = dishDto.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());

            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavors);

            return dishDto;
        }).collect(Collectors.toList());


        //返回成功结果
        return R.success(dishDtos);
    }

    /**
     * 修改菜品状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.in(ids != null, Dish::getId, ids);
        qw.orderByDesc(Dish::getPrice);
        //根据条件进行批量查询
        List<Dish> list = service.list(qw);

        //修改状态
        for (Dish dish : list) {
            if (dish != null) {
                //把浏览器传入的status参数赋值给菜品
                dish.setStatus(status);
                service.updateById(dish);
            }
        }
        return R.success("修改状态成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        service.removeWithFlavor(ids);
        return R.success("删除成功");
    }
}
