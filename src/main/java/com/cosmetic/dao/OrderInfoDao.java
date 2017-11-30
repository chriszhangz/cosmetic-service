package com.cosmetic.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.OrderInfo;
import com.cosmetic.entity.ShipInfo;

public interface OrderInfoDao {

    public OrderInfo selectOrderInfoById(@Param("so_id") int so_id);
    public OrderInfo selectOrderInfo(@Param("user_id") int user_id,@Param("shurack_id") int shurack_id,@Param("product_sku") String product_sku);
    public List<ShipInfo> listOrderInfo(@Param("user_id") int user_id,@Param("pay_status") int pay_status,@Param("donate_status") int donate_status,@Param("ship_status") int ship_status,@Param("start") Integer start,@Param("leng") Integer leng);
    public int insertOrderInfo(OrderInfo orderInfo);
    public int updateOrderInfoById(@Param("pay_time") int pay_time,@Param("pay_status") int pay_status,@Param("so_id") int so_id);
    public int updateShipInfoById(@Param("ship_time") int ship_time,@Param("ship_status") int ship_status,@Param("so_id") int so_id);
    public int updateDonateInfoById(@Param("donate_time") int donate_time,@Param("donate_status") int donate_status,@Param("so_id") int so_id);
    public int updateOrderInfo(OrderInfo orderInfo);
    
}
