package com.chy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chy.reggie.dto.DishDto;
import com.chy.reggie.javabean.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    List<DishDto> getpage(@Param("start") Integer start, @Param("pageSize") Integer pageSize,@Param("name") String name);

}
