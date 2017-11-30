package com.cosmetic.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosmetic.dao.ExpressCheckoutDao;
import com.cosmetic.dao.OrderHistoryDao;
import com.cosmetic.dao.OrderPaypalDao;
import com.cosmetic.entity.ExpressCheckout;
import com.cosmetic.entity.OrderHistory;
import com.cosmetic.entity.OrderPaypal;
import com.cosmetic.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private ExpressCheckoutDao expressCheckoutDao;
	@Autowired
	private OrderHistoryDao orderHistoryDao;
	@Autowired
	private OrderPaypalDao orderPaypalDao;
	
	public int insertExpressCheckout(ExpressCheckout expressCheckout) {
		return expressCheckoutDao.insert(expressCheckout);
	}

	public ExpressCheckout selectExpressCheckoutByUid(int user_id) {
		return expressCheckoutDao.selectByUid(user_id);
	}

	public int updatePayerIdByUid(ExpressCheckout expressCheckout) {
		return expressCheckoutDao.updatePayerIdByUid(expressCheckout);
	}

	public int deleteExpressCheckoutByUid(int user_id) {
		return expressCheckoutDao.deleteByUid(user_id);
	}

	public int insertOrderPaypal(OrderPaypal orderPaypal) {
		return orderPaypalDao.insert(orderPaypal);
	}

	public OrderPaypal selectOrderPaypalByOrderSn(String order_sn) {
		return orderPaypalDao.selectOrderPaypalByOrderSn(order_sn);
	}

	public int countExpressCheckoutByUid(int user_id) {
		return expressCheckoutDao.selectCountByUid(user_id);
	}

	@Override
	public int insertOrderHistory(OrderHistory orderHistory) {
		// TODO Auto-generated method stub
		return orderHistoryDao.insert(orderHistory);
	}

	@Override
	public OrderHistory selectOrderHistoryByChargeId(int charge_id) {
		// TODO Auto-generated method stub
		return orderHistoryDao.selectOrderHistoryByChargeId(charge_id);
	}

	@Override
	public List<OrderHistory> selectOrderHistoryByUserId(int user_id) {
		// TODO Auto-generated method stub
		return orderHistoryDao.selectOrderHistoryByUserId(user_id);
	}

}
