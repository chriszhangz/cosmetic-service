package com.cosmetic.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosmetic.dao.OrderInfoDao;
import com.cosmetic.dao.SubOrderDao;
import com.cosmetic.entity.OrderInfo;
import com.cosmetic.entity.ShipInfo;
import com.cosmetic.entity.SubOrder;
import com.cosmetic.service.OrderInfoService;
import com.cosmetic.service.SubOrderService;

@Service
public class OrderInfoServiceImpl implements OrderInfoService{

	@Autowired
	private OrderInfoDao orderInfoDao;

	@Override
	public OrderInfo selectOrderInfoById(int so_id) {
		// TODO Auto-generated method stub
		return orderInfoDao.selectOrderInfoById(so_id);
	}

	@Override
	public List<ShipInfo> listOrderInfo(int user_id, int pay_status,int donate_status,int ship_status, Integer start, Integer leng) {
		// TODO Auto-generated method stub
		return orderInfoDao.listOrderInfo(user_id, pay_status,donate_status,ship_status, start, leng);
	}

	@Override
	public int insertOrderInfo(OrderInfo orderInfo) {
		// TODO Auto-generated method stub
		return orderInfoDao.insertOrderInfo(orderInfo);
	}

	@Override
	public int updateOrderInfoById(int pay_time, int pay_status, int so_id) {
		// TODO Auto-generated method stub
		return orderInfoDao.updateOrderInfoById(pay_time, pay_status, so_id);
	}

	@Override
	public OrderInfo selectOrderInfo(int user_id, int shurack_id, String product_sku) {
		// TODO Auto-generated method stub
		return orderInfoDao.selectOrderInfo(user_id, shurack_id, product_sku);
	}

	@Override
	public int updateOrderInfo(OrderInfo orderInfo) {
		// TODO Auto-generated method stub
		return orderInfoDao.updateOrderInfo(orderInfo);
	}

	@Override
	public int updateShipInfoById(int ship_time, int ship_status, int so_id) {
		// TODO Auto-generated method stub
		return orderInfoDao.updateShipInfoById(ship_time, ship_status, so_id);
	}

	@Override
	public int updateDonateInfoById(int donate_time, int donate_status, int so_id) {
		// TODO Auto-generated method stub
		return orderInfoDao.updateDonateInfoById(donate_time, donate_status, so_id);
	}
	

}
