package com.cosmetic.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Address;
import com.cosmetic.entity.SubOrder;
import com.cosmetic.entity.UserAddress;

public interface SubOrderDao {

    public SubOrder selectSubOrderById(@Param("sub_order_id") int sub_order_id);
    public SubOrder selectSubOrderByUid(@Param("user_id") int user_id);
    //public List<SubOrder> selectSubOrderBySubTime(@Param("sub_time") int sub_time);
    public int insertSubOrder(SubOrder subOrder);
    public int updateBysubOrderId(SubOrder subOrder);
    public List<SubOrder> selectDailySubOrder();
    public List<SubOrder> selectShipSubOrder();
    //public int updateExcStatusById(@Param("sub_order_id") int sub_order_id,@Param("sub_exc_status") int sub_exc_status);
}
