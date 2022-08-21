package com.chy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chy.reggie.common.R;
import com.chy.reggie.javabean.Category;
import com.chy.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public R<String> addCategory(@RequestBody Category category){
        log.info(category.toString());
        boolean save = categoryService.save(category);
        if(save)
            return R.success("添加成功");
        return R.error("添加失败");
    }

    /**
     * 获取分页的方法
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/page",method = RequestMethod.GET)
    public R<Page> page(Integer page,Integer pageSize){
        Page<Category> categoryPage = new Page<>(page,pageSize);
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("update_time");
        categoryService.page(categoryPage,queryWrapper);
        return R.success(categoryPage);
    }

    /**
     * 删除菜品分类或套餐分类
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public R<String> delete(Long id){
        log.info(id.toString());
        R<String> result = categoryService.remove(id);
        return result;
    }

    /**
     * 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        boolean result = categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    /**
     * 通过type 1/2  查询菜品分类/套餐分类 数据
     * @param category
     * @return
     */
    @RequestMapping("/list")
    public R<List<Category>>  listCategoryByType(Category category){
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,"type",category.getType());
        queryWrapper.orderByAsc("sort").orderByDesc("update_time");
        List<Category> result = categoryService.list(queryWrapper);
        return R.success(result);
    }

}
