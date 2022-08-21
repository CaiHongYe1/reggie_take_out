package com.chy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chy.reggie.dto.DishDto;
import com.chy.reggie.javabean.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    List<DishDto> getpage(Integer page,Integer pageSize,String name);

//    批量改变菜品状态
    void changeStatus(Integer status,Long[] ids);

//    批量删除菜品
    void delete(Long[] ids);

//    通过菜品分类id查询菜品信息
    List<Dish> getDishByCategoryId(Long categoryId);
}
