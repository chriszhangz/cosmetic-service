package com.cosmetic.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosmetic.dao.UnSubOrderDao;
import com.cosmetic.entity.UnSubOrder;
import com.cosmetic.service.UnSubOrderService;

@Service
public class UnSubOrderServiceImpl implements UnSubOrderService{

	@Autowired
	private UnSubOrderDao UnSubOrderDao;

	@Override
	public UnSubOrder selectUnSubOrderById(String sub_order_id) {
		// TODO Auto-generated method stub
		return UnSubOrderDao.selectUnSubOrderById(sub_order_id);
	}

	@Override
	public List<UnSubOrder> selectUnSubOrderByUnsSubTime(int sub_time) {
		// TODO Auto-generated method stub
		return UnSubOrderDao.selectUnSubOrderByUnsSubTime(sub_time);
	}

	@Override
	public int insertUnSubOrder(UnSubOrder unSubOrder) {
		// TODO Auto-generated method stub
		return UnSubOrderDao.insertUnSubOrder(unSubOrder);
	}


}
