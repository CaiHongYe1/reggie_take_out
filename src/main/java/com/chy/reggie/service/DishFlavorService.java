package com.chy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chy.reggie.dto.DishDto;
import com.chy.reggie.javabean.DishFlavor;

public interface DishFlavorService extends IService<DishFlavor> {
    void savaDishFlavor(DishDto dishDto);
    void updateDishFlavor(DishDto dishDto);

}
