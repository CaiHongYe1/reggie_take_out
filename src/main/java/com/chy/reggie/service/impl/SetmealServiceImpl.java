package com.chy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chy.reggie.common.BaseContext;
import com.chy.reggie.common.CustomException;
import com.chy.reggie.dto.SetmealDto;
import com.chy.reggie.javabean.Category;
import com.chy.reggie.javabean.Employee;
import com.chy.reggie.javabean.Setmeal;
import com.chy.reggie.javabean.SetmealDish;
import com.chy.reggie.mapper.SetmealMapper;
import com.chy.reggie.service.CategoryService;
import com.chy.reggie.service.SetmealDishService;
import com.chy.reggie.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void saveSetmeal(SetmealDto setmealDto) {
//      将套餐  写入到  setmeal表中
        this.save(setmealDto);
//      将套餐菜品 写入到  setmeal_dish表中
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
//      得到新插入套餐表的套餐的id
        Long setmealid = setmealDto.getId();
//      为每个套餐内的菜品设置套餐id，同时写入到 套餐菜品关系表setmeal_dish中
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealid);
            setmealDishService.save(setmealDish);
        }

    }

    /**
     * 套餐分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<SetmealDto> getPage(Integer page, Integer pageSize, String name) {
        Page<Setmeal> Page = new Page<>(page,pageSize);
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
//      构造查询条件
        queryWrapper.orderByDesc("update_time");
        queryWrapper.like(StringUtils.isNotBlank(name),"name",name);
        Page<Setmeal> setmealPage = setmealMapper.selectPage(Page, queryWrapper);
//      构造Dto的集合

//       复制给setmealDtoPage  除了records
        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(Page,setmealDtoPage,"records");

//        遍历Setmeal 将其转换为SetmealDto
        List<Setmeal> records = Page.getRecords();

        List<SetmealDto> result = new ArrayList<>();
        for (Setmeal record : records) {
//            通过category_id 查询出套餐分类名 并封装成SetmealDto 加入到result集合中
            Category category = categoryService.getById(record.getCategoryId());
            SetmealDto setmealDto = new SetmealDto();
//            将Setmeal的基本属性复制给SetmealDto
            BeanUtils.copyProperties(record,setmealDto);
            setmealDto.setCategoryName(category.getName());

            result.add(setmealDto);
        }

        setmealDtoPage.setRecords(result);

        return setmealDtoPage;
    }

    /**
     * 通过Id查询 套餐信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getSetmealById(Long id) {
        Setmeal setmeal = this.getById(id);
//      构造套餐下的菜品 的查询构造器
        QueryWrapper<SetmealDish> setmealDishQueryWrapper = new QueryWrapper<>();
        setmealDishQueryWrapper.eq("setmeal_id",id);
        setmealDishQueryWrapper.orderByAsc("sort").orderByDesc("update_time");
//        查询得到套餐下菜品的集合
        List<SetmealDish> list = setmealDishService.list(setmealDishQueryWrapper);

        SetmealDto setmealDto = new SetmealDto();
//        把setmeal的基本信息拷贝给setmealDto
        BeanUtils.copyProperties(setmeal,setmealDto);

        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    /**
     * 更新套餐信息
     * @param setmealDto
     */
    @Override
    public void updateSetmeal(SetmealDto setmealDto) {
        this.updateById(setmealDto);

//        删除套餐菜品表内 属于该套餐的菜品信息
        UpdateWrapper<SetmealDish> setmealDishUpdateWrapper = new UpdateWrapper<>();
        setmealDishUpdateWrapper.eq("setmeal_id",setmealDto.getId());
        setmealDishService.remove(setmealDishUpdateWrapper);

//        插入新的属于该套餐的菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    public void deleteWithDish(Long[] ids) {
//        查询套餐状态，确定是否可以删除
        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<>();
        setmealQueryWrapper.in("id",Arrays.asList(ids))
                .eq("status",1);
//        查询出属于在售状态并且需要删除的套餐数量
        int count = this.count(setmealQueryWrapper);
//        如果不能删除，抛出一个业务异常
        if(count>0)
            throw new CustomException("存在在售状态的套餐，无法删除");
//        如果可以删除，先删除套餐表中的数据
        this.removeByIds(Arrays.asList(ids));
//        再删除套餐关联的菜品信息
        UpdateWrapper<SetmealDish> setmealDishUpdateWrapper = new UpdateWrapper<>();
        setmealDishUpdateWrapper.in("setmeal_id",Arrays.asList(ids));
        setmealDishService.remove(setmealDishUpdateWrapper);
    }
    /**
     * 改变在售状态
     * @param status
     * @param ids
     */
    @Override
    public void changeStatus(Integer status, Long[] ids) {
//        构造更新条件
        UpdateWrapper<Setmeal> setmealUpdateWrapper = new UpdateWrapper<>();
        setmealUpdateWrapper.set("status",status)
                .in("id",Arrays.asList(ids))
                .set("update_time", LocalDateTime.now())
                .set("update_user", BaseContext.getCurrentId());
//        执行更新
        this.update(setmealUpdateWrapper);
    }
}
