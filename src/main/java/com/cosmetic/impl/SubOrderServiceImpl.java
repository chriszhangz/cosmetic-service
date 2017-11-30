package com.cosmetic.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosmetic.dao.SubOrderDao;
import com.cosmetic.entity.SubOrder;
import com.cosmetic.service.SubOrderService;

@Service
public class SubOrderServiceImpl implements SubOrderService{

	@Autowired
	private SubOrderDao subOrderDao;

	@Override
	public SubOrder selectSubOrderById(int sub_order_id) {
		return subOrderDao.selectSubOrderById(sub_order_id);
	}

	@Override
	public int insertSubOrder(SubOrder subOrder) {
		return subOrderDao.insertSubOrder(subOrder);
	}

	@Override
	public int updateSubOrderById(SubOrder subOrder) {
		// TODO Auto-generated method stub
		return subOrderDao.updateBysubOrderId(subOrder);
	}

	@Override
	public List<SubOrder> selectDailySubOrder() {
		// TODO Auto-generated method stub
		return subOrderDao.selectDailySubOrder();
	}

	@Override
	public List<SubOrder> selectShipSubOrder() {
		// TODO Auto-generated method stub
		return subOrderDao.selectShipSubOrder();
	}

	@Override
	public SubOrder selectSubOrderByUid(int user_id) {
		// TODO Auto-generated method stub
		return subOrderDao.selectSubOrderByUid(user_id);
	}


	

}
