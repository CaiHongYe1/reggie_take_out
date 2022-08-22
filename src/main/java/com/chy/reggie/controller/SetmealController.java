package com.chy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chy.reggie.common.R;
import com.chy.reggie.dto.SetmealDto;
import com.chy.reggie.javabean.Setmeal;
import com.chy.reggie.service.SetmealDishService;
import com.chy.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @Transactional
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto.toString());
        setmealService.saveSetmeal(setmealDto);
        return R.success("新建套餐成功");
    }

    /**
     * 获取分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
        log.info("当前页数：{},分页大小{},名字：{}",page,pageSize,name);
        Page<SetmealDto> result = setmealService.getPage(page, pageSize, name);
        return R.success(result);
    }

    /**
     * 通过id查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealById(@PathVariable Long id){
        log.info("id:{}",id);
        SetmealDto setmealDto = setmealService.getSetmealById(id);
        return R.success(setmealDto);
    }

    @Transactional
    @PutMapping()
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
        log.info("{}",setmealDto.toString());
        setmealService.updateSetmeal(setmealDto);
        return R.success("修改套餐信息成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @Transactional
    @DeleteMapping
    public R<String> deleteBatch(@RequestParam("ids") Long[] ids){
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 修改套餐状态
     * @param status
     * @param ids
     * @return
     */
    @Transactional
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable("status") Integer status,Long[] ids){
        log.info("status:{},ids:{}",status,ids);
        setmealService.changeStatus(status,ids);
        return R.success("修改在售状态成功");
    }

    /**
     * 通过套餐分类id查询套餐信息
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> getSetmealByCategoryId(Setmeal setmeal){
        //查询redis缓存中是否已经有这个分类的套餐数据
        String key = "setmeal_"+setmeal.getCategoryId();
        List<Setmeal> list = (List<Setmeal>)redisTemplate.opsForValue().get(key);

        //如果有就直接返回
        if(list != null){
            return R.success(list);
        }

        //如果没有就查询数据库，如何再将结果加到redis中缓存

        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<>();
        setmealQueryWrapper.eq("category_id",setmeal.getCategoryId());
        setmealQueryWrapper.eq(setmeal.getStatus() != null,"status",setmeal.getStatus());
        setmealQueryWrapper.orderByDesc("update_time");
        list = setmealService.list(setmealQueryWrapper);
        //将查询结果加入到redis缓存中
        redisTemplate.opsForValue().set(key,list,5, TimeUnit.MINUTES);
        return R.success(list);
    }


}
