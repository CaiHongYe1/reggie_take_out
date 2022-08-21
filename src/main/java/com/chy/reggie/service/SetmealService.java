package com.chy.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chy.reggie.dto.SetmealDto;
import com.chy.reggie.javabean.Setmeal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
//    保存新的套餐
    void saveSetmeal(SetmealDto setmealDto);
    Page<SetmealDto> getPage(Integer page, Integer pageSize, String name);
    SetmealDto getSetmealById(Long id);
    void updateSetmeal(SetmealDto setmealDto);
    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     * @param ids
     */
    void deleteWithDish(Long[] ids);

    /**
     * 改变在售状态
     * @param status
     * @param ids
     */
    void changeStatus(Integer status, Long[] ids);
}
