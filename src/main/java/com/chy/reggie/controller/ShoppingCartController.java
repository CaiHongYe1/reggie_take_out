package com.chy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chy.reggie.common.BaseContext;
import com.chy.reggie.common.R;
import com.chy.reggie.javabean.ShoppingCart;
import com.chy.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("");
//        设置用户id，指定当前是哪个用户的购物车数据
        Long currentUserId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentUserId);
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id",currentUserId);

////        查询菜品或套餐是否在购物车中
//        if(shoppingCart.getDishId()!=null){
////            当前项是菜品
//            shoppingCartQueryWrapper.eq("dish_id",shoppingCart.getDishId());
//            ShoppingCart one = shoppingCartService.getOne(shoppingCartQueryWrapper);
////        如果在购物车中已经存在，则判断口味是否相同
//            if(one != null){
//                //如果口味相同直接num++
//                if(one.getDishFlavor().equals(shoppingCart.getDishFlavor())){
//                    one.setNumber(one.getNumber()+1);
//                    one.setAmount(one.getAmount().add(shoppingCart.getAmount()));
//                    shoppingCartService.updateById(one);
//                    return R.success(one);
//                }
//                //如果口味不相同就在另外插入一条
//                else {
//                    shoppingCart.setNumber(1);
//                    shoppingCartService.save(shoppingCart);
//                    return R.success(shoppingCart);
//                }
//            }
//            else {
//                //如果菜品项不存在就另外插入一条
//                shoppingCart.setNumber(1);
//                shoppingCartService.save(shoppingCart);
//                return R.success(shoppingCart);
//            }
//
//        }
//        else {
////            当前项是套餐
//            shoppingCartQueryWrapper.eq("setmeal_id",shoppingCart.getSetmealId());
//            ShoppingCart one = shoppingCartService.getOne(shoppingCartQueryWrapper);
////            如果套餐已经存在,num++
//            if(one!=null){
//
//                one.setNumber(one.getNumber()+1);
//                one.setAmount(one.getAmount().add(shoppingCart.getAmount()));
//                shoppingCartService.updateById(one);
//            }
////            如果套餐不存在就另外插入一条
//            else {
//                shoppingCart.setNumber(1);
//                shoppingCartService.save(shoppingCart);
//                return R.success(shoppingCart);
//            }
//        }
//        return R.error("未知错误");
        //        查询菜品或套餐是否在购物车中
        if(shoppingCart.getDishId()!=null){
//            当前项是菜品
            shoppingCartQueryWrapper.eq("dish_id",shoppingCart.getDishId());
        }
        else {
            shoppingCartQueryWrapper.eq("setmeal_id",shoppingCart.getSetmealId());
        }
//        购物车项在购物车中是否已经存在
        ShoppingCart one = shoppingCartService.getOne(shoppingCartQueryWrapper);
        if (one != null) {
            //        购物车项在购物车中存在
            one.setNumber(one.getNumber() + 1);
//            one.setAmount(one.getAmount().add(shoppingCart.getAmount()));
            one.setCreateTime(LocalDateTime.now());
            shoppingCartService.updateById(one);
            return R.success(one);
        } else {
            //        购物车项在购物车中不存在
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            return R.success(shoppingCart);
        }

    }

    /**
     * 获取当前登录用户的购物车项
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long currentId = BaseContext.getCurrentId();
//        根据当前登录的user的id查询购物车项信息
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id",currentId)
                .orderByAsc("create_time");
        List<ShoppingCart> list = shoppingCartService.list(shoppingCartQueryWrapper);
        return R.success(list);
    }

    /**
     * 减少菜品或套餐数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        Long currentId = BaseContext.getCurrentId();
//        判断删除的是菜品还是套餐
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id",currentId);
        if(shoppingCart.getDishId() != null){
//            如果是菜品
            shoppingCartQueryWrapper.eq("dish_id",shoppingCart.getDishId());
        }else {
//            如果是套餐
            shoppingCartQueryWrapper.eq("setmeal_id",shoppingCart.getSetmealId());
        }
//        查询出要减少的菜品或套餐
        ShoppingCart one = shoppingCartService.getOne(shoppingCartQueryWrapper);
        if(one.getNumber() == 1){
            shoppingCartService.removeById(one.getId());
            return R.success("删除成功");
        }else {
            one.setNumber(one.getNumber()-1);
            shoppingCartService.updateById(one);
            return R.success("减少成功");
        }
    }

    /**
     * 清空当前登录用户的购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId = BaseContext.getCurrentId();
        UpdateWrapper<ShoppingCart> shoppingCartUpdateWrapper = new UpdateWrapper<>();
        shoppingCartUpdateWrapper.eq("user_id",currentId);
        shoppingCartService.remove(shoppingCartUpdateWrapper);
        return R.success("清空成功");
    }

}
