package com.chy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chy.reggie.common.R;
import com.chy.reggie.dto.DishDto;
import com.chy.reggie.javabean.Category;
import com.chy.reggie.javabean.Dish;
import com.chy.reggie.javabean.DishFlavor;
import com.chy.reggie.service.CategoryService;
import com.chy.reggie.service.DishFlavorService;
import com.chy.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品管理
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @Transactional
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
//        保存菜品的基本信息到菜品表
        dishService.save(dishDto);

//        保存菜品的口味到口味表
        dishFlavorService.savaDishFlavor(dishDto);
        return R.success("新建菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){
//        构造分页构造器对象
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

//        构造条件构造器
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
//        name如果不为null 添加过滤条件
        dishQueryWrapper.like(name!=null ,"name",name);
//        添加排序条件
        dishQueryWrapper.orderByDesc("update_time");
//      执行分页查询
        dishService.page(dishPage,dishQueryWrapper);

//        对象拷贝
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        List<Dish> records = dishPage.getRecords();

        List<DishDto> dishDtos = new ArrayList<>();
        for (Dish record : records) {
//            将record的普通属性拷贝到dishDto中
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);
//            通过record的category_id查询出 category的name 并赋值给dishDto
            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());
//            将dishDto加入到dishDtos集合中
            dishDtos.add(dishDto);
        }

        dishDtoPage.setRecords(dishDtos);

        return R.success(dishDtoPage);
    }

    /**
     * 根据菜品Id返回菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable("id") Long id){
        log.info("菜品的ID为：{}",id);
//        根据id查询dish的信息
        Dish dish = dishService.getById(id);

//        将dish的基本属性拷贝给dto
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

//        通过dish的Id查询对应菜品的口味
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id",dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        return R.success(dishDto);
    }

    /**
     * 更新菜品
     * @param dishDto
     * @return
     */
    @Transactional
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
//        更新菜品的基本信息到菜品表
        UpdateWrapper<Dish> dishUpdateWrapper = new UpdateWrapper<>();
        dishUpdateWrapper.eq("id",dishDto.getId());
        dishService.update(dishDto,dishUpdateWrapper);

//        更新菜品的口味到口味表
        dishFlavorService.updateDishFlavor(dishDto);
        return R.success("更新菜品信息成功");

    }

    /**
     * 修改菜品状态
     * @param status
     * @param ids
     * @return
     */
    @Transactional
    @PostMapping(value = "/status/{status}")
    public R<String> changeStatus(@PathVariable("status") Integer status,Long[] ids){
        log.info("商品状态{}，商品id{}",status,ids);
        dishService.changeStatus(status,ids);
        return R.success("修改状态成功");
    }

    @Transactional
    @DeleteMapping
    public R<String> deleteOne(@RequestParam("ids")Long[] ids){
        log.info("要删除的菜品id：{}",ids);
        dishService.delete(ids);
        return R.success("删除菜品成功");
    }

    @RequestMapping("/list")
    public R<List<DishDto>> getDishByCategoryId(Long categoryId, String name) {

        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
//            添加查询条件
        dishQueryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        dishQueryWrapper.eq("status", 1);
        dishQueryWrapper.eq(StringUtils.isBlank(name), "category_id", categoryId);
//            添加排序条件
        dishQueryWrapper.orderByAsc("sort").orderByDesc("update_time");
        List<Dish> list = dishService.list(dishQueryWrapper);
        List<DishDto> dishDtos = new ArrayList<>();
        for (Dish dish : list) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            dishDtos.add(dishDto);
        }


        for (DishDto dishDto : dishDtos) {
            //        通过dish的Id查询对应菜品的口味
            QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("dish_id",dishDto.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavorList);
        }
        return R.success(dishDtos);
    }

}
