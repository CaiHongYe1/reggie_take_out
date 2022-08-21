package com.chy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chy.reggie.javabean.SetmealDish;
import com.chy.reggie.mapper.SetmealDishMapper;
import com.chy.reggie.mapper.SetmealMapper;
import com.chy.reggie.service.SetmealDishService;
import com.chy.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
    @Autowired
    private SetmealDishMapper setmealDishMapper;
}
