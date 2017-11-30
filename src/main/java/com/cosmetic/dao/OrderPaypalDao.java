package com.cosmetic.dao;


import com.cosmetic.entity.OrderPaypal;

public interface OrderPaypalDao {

    public int insert(OrderPaypal orderPaypal);
    public OrderPaypal selectOrderPaypalByOrderSn(String order_sn);
    
}
