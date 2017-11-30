package com.cosmetic.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Address;
import com.cosmetic.entity.SubOrder;
import com.cosmetic.entity.UnSubOrder;
import com.cosmetic.entity.UserAddress;

public interface UnSubOrderDao {

    public UnSubOrder selectUnSubOrderById(@Param("sub_order_id") String sub_order_id);
    public List<UnSubOrder> selectUnSubOrderByUnsSubTime(@Param("unsub_time") int sub_time);
    public int insertUnSubOrder(UnSubOrder unSubOrder);

}
