package com.cosmetic.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.SubOrder;



public interface SubOrderService {
	
    public SubOrder selectSubOrderById(int sub_order_id);
    public SubOrder selectSubOrderByUid(int user_id);
    //public List<SubOrder> selectSubOrderBySubTime(int sub_time);
    public int insertSubOrder(SubOrder subOrder);
    public int updateSubOrderById(SubOrder subOrder);
    public List<SubOrder> selectDailySubOrder();
    public List<SubOrder> selectShipSubOrder();
    //public int updateExcStatusById(int sub_order_id,int sub_exc_status);
 

}
