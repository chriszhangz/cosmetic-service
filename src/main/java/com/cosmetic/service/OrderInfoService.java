package com.cosmetic.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.OrderInfo;
import com.cosmetic.entity.ShipInfo;
import com.cosmetic.entity.SubOrder;



public interface OrderInfoService {
	
    public OrderInfo selectOrderInfoById(int so_id);
    public OrderInfo selectOrderInfo(int user_id,int shurack_id,String product_sku);
    public List<ShipInfo> listOrderInfo(int user_id,int pay_status,int donate_status,int ship_status,Integer start,Integer leng);
    public int insertOrderInfo(OrderInfo orderInfo);
    public int updateOrderInfoById(int pay_time,int pay_status,int so_id);
    public int updateShipInfoById(int ship_time,int ship_status,int so_id);
    public int updateDonateInfoById(int donate_time,int donate_status,int so_id);
    public int updateOrderInfo(OrderInfo orderInfo); 

}
