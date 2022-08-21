package com.chy.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chy.reggie.javabean.Orders;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrdersService extends IService<Orders> {
    /**
     * 提交订单
     * @param orders
     * @return
     */
    void submit(Orders orders);
}
