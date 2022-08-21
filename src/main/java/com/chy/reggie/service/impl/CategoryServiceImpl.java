package com.chy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chy.reggie.common.CustomException;
import com.chy.reggie.common.R;
import com.chy.reggie.javabean.Category;
import com.chy.reggie.javabean.Dish;
import com.chy.reggie.javabean.Setmeal;
import com.chy.reggie.mapper.CategoryMapper;
import com.chy.reggie.service.CategoryService;
import com.chy.reggie.service.DishService;
import com.chy.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类 ,删除之前需要进行判断 是否关联了菜品或套餐
     * @param id
     * @return
     */
    public R<String> remove(Long id){
//        查询是否关联了菜品,如果已经关联
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.eq("category_id", id);
        int count1 = dishService.count(dishQueryWrapper);
        if(count1 > 0){
//            return R.error("此菜品分类关联了菜品,无法删除");
            throw new CustomException("此菜品分类关联了菜品,无法删除");
        }


//        查询是否关联了套餐,如果已经关联
        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<>();
        setmealQueryWrapper.eq("category_id", id);
        int count2 = setmealService.count(setmealQueryWrapper);
        if(count2 > 0){
//            return R.error("此套餐分类关联了套餐,无法删除");
            throw new CustomException("此套餐分类关联了套餐,无法删除");
        }


//        正常删除
        categoryMapper.deleteById(id);
        return R.success("删除成功");
    }


}
