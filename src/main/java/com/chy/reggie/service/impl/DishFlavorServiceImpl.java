package com.chy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chy.reggie.dto.DishDto;
import com.chy.reggie.javabean.DishFlavor;
import com.chy.reggie.mapper.DishFlavorMapper;
import com.chy.reggie.service.DishFlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 保存菜品口味
     * @param dishDto
     */
    @Override
    public void savaDishFlavor(DishDto dishDto) {
        //        保存菜品的口味到口味表
        List<DishFlavor> flavors = dishDto.getFlavors();
        //为口味设置菜品Id
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }
        this.saveBatch(flavors);
    }
    /**
     * 更新菜品口味
     * @param dishDto
     */
    @Override
    public void updateDishFlavor(DishDto dishDto) {
//        先清除当前菜品对应的口味数据
        UpdateWrapper<DishFlavor> dishFlavorUpdateWrapper = new UpdateWrapper<>();
        dishFlavorUpdateWrapper.eq("dish_id",dishDto.getId());
        this.remove(dishFlavorUpdateWrapper);
//        再添加当前提交过来的口味数据
        //        保存菜品的口味到口味表
        List<DishFlavor> flavors = dishDto.getFlavors();
        //为口味设置菜品Id
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }
        this.saveBatch(flavors);
    }
}
