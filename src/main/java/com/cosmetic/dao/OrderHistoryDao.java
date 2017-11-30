package com.cosmetic.dao;


import java.util.List;

import com.cosmetic.entity.OrderHistory;

public interface OrderHistoryDao {

    public int insert(OrderHistory orderHistory);
    public OrderHistory selectOrderHistoryByChargeId(int charge_id);
    public List<OrderHistory> selectOrderHistoryByUserId(int user_id);
    
}
