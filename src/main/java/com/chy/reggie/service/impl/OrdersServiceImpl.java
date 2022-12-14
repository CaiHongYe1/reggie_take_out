package com.chy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chy.reggie.common.BaseContext;
import com.chy.reggie.common.CustomException;
import com.chy.reggie.javabean.*;
import com.chy.reggie.mapper.OrdersMapper;
import com.chy.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 提交订单
     * @param orders
     * @return
     */
    @Transactional
    @Override
    public void submit(Orders orders) {
//        获取用户ID
        Long currentId = BaseContext.getCurrentId();
//        查询当前用户的购物车
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id",currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartQueryWrapper);
        if(shoppingCarts.size() ==0 || shoppingCarts == null){
            throw new CustomException("购物车为空,无法下单");
        }

//        查询用户数据
        User user = userService.getById(currentId);
//        查询地址数据
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook == null){
            throw new CustomException("地址信息有误,无法下单");
        }

        AtomicInteger amount = new AtomicInteger(0);


        long number = IdWorker.getId();//生成订单号

        List<OrderDetail> orderDetails = new ArrayList<>();
//        计算金额
        for (ShoppingCart item : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(number);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            orderDetails.add(orderDetail);
        }

//        向订单表插入数据，一条数据
        orders.setId(number);
        orders.setNumber(String.valueOf(number));
        orders.setUserId(currentId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null? "": addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail())
        );
        this.save(orders);
//        向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
//        清空购物车数据
        shoppingCartService.remove(shoppingCartQueryWrapper);
    }
}
