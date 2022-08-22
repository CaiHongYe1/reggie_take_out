package com.chy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chy.reggie.dto.DishDto;
import com.chy.reggie.javabean.Dish;
import com.chy.reggie.javabean.DishFlavor;
import com.chy.reggie.mapper.DishMapper;
import com.chy.reggie.service.DishFlavorService;
import com.chy.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<DishDto> getpage(Integer page,Integer pageSize,String name) {

        return null;
    }

    /**
     * 批量起售、停售
     * @param status
     * @param ids
     */
    @Override
    public void changeStatus(Integer status, Long[] ids) {
        for (Long id : ids) {
            //通过id查询dish信息并返回
            QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
            dishQueryWrapper.eq("id", id);
            Dish dish = this.getOne(dishQueryWrapper);

            //        删除菜品对应分类在redis中的缓存
            String key = "dish_"+dish.getCategoryId();
            redisTemplate.delete(key);

            //修改dish状态
            dish.setStatus(status);
            //保存dish
            UpdateWrapper<Dish> dishUpdateWrapper = new UpdateWrapper<>();
            dishUpdateWrapper.eq("id", id);
            this.update(dish, dishUpdateWrapper);
        }
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        //删除ids数组中对应的每一个dish和dish_flavor
        for (Long id : ids) {
//          删除改菜品的分类在redis中的缓存

            Dish thisdish = this.getById(id);
            String key = "dish_"+thisdish.getCategoryId();
            redisTemplate.delete(key);

            //从dish菜品表中根据id删除菜品
            this.removeById(id);
            //删除跟该dish相关联的口味dish_flavor
            UpdateWrapper<DishFlavor> dishFlavorUpdateWrapper = new UpdateWrapper<>();
            dishFlavorUpdateWrapper.eq("dish_id", id);
            dishFlavorService.remove(dishFlavorUpdateWrapper);
        }

    }

    /**
     * 通过菜品分类id查询菜品信息
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getDishByCategoryId(Long categoryId) {
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.eq("category_id",categoryId);
        dishQueryWrapper.eq("status",1);
//        通过菜品分类id查询菜品信息
        List<Dish> list = this.list(dishQueryWrapper);
        return list;
    }
}
