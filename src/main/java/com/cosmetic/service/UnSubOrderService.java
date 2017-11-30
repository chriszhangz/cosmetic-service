package com.cosmetic.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.SubOrder;
import com.cosmetic.entity.UnSubOrder;



public interface UnSubOrderService {

    public UnSubOrder selectUnSubOrderById(String sub_order_id);
    public List<UnSubOrder> selectUnSubOrderByUnsSubTime(int sub_time);
    public int insertUnSubOrder(UnSubOrder unSubOrder);
 

}
