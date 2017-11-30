package com.cosmetic.impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosmetic.dao.ShurackDao;
import com.cosmetic.entity.Shurack;
import com.cosmetic.service.ShurackService;

@Service
public class ShurackServiceImpl implements ShurackService{

	@Autowired
	private ShurackDao shurackDao;

	@Override
	public List<Shurack> selectShurackByUserId(Integer user_id, Integer status) {
		// TODO Auto-generated method stub
		return shurackDao.selectShurackByUserId(user_id, status);
	}

	@Override
	public Integer selectNextShip(Integer user_id) {
		// TODO Auto-generated method stub
		return shurackDao.selectNextShip(user_id);
	}

	@Override
	public void insertShurack(Shurack shurack) {
		// TODO Auto-generated method stub
		shurackDao.insertShurack(shurack);
	}

	@Override
	public void deleteShurack(Integer user_id, String product_sku) {
		// TODO Auto-generated method stub
		shurackDao.deleteShurack(user_id, product_sku);
	}

	@Override
	public void updateToTop(Integer user_id, Integer add_time, String product_sku) {
		// TODO Auto-generated method stub
		shurackDao.updateToTop(user_id, add_time, product_sku);
	}

	@Override
	public void updateStatus(Integer shurack_id, Integer status) {
		// TODO Auto-generated method stub
		shurackDao.updateStatus(shurack_id, status);
	}
	
	@Override
	public void updateStatusAndShipTime(Integer shurack_id, Integer status, Integer ship_time) {
		// TODO Auto-generated method stub
		shurackDao.updateStatusAndShipTime(shurack_id, status,ship_time);
	}
	
	@Override
	public Integer countShurackByUserId(Integer user_id, Integer status) {
		// TODO Auto-generated method stub
		return shurackDao.countShurackByUserId(user_id, status);
	}

	@Override
	public Integer countShurackBySku(Integer user_id, Integer status, String product_sku) {
		// TODO Auto-generated method stub
		return shurackDao.countShurackBySku(user_id, status, product_sku);
	}

	@Override
	public void updateStatusAndDonateTime(Integer shurack_id, Integer status,
			Integer donate_time) {
		// TODO Auto-generated method stub
		shurackDao.updateStatusAndDonateTime(shurack_id, status, donate_time);
	}

	@Override
	public void updateStatusAndBuyTime(Integer shurack_id, Integer status,
			Integer buy_time) {
		// TODO Auto-generated method stub
		shurackDao.updateStatusAndBuyTime(shurack_id, status, buy_time);
	}

	@Override
	public List<Shurack> selectShurackByStatus(Integer status) {
		// TODO Auto-generated method stub
		return shurackDao.selectShurackByStatus(status);
	}

	@Override
	public List<Shurack> selectMyShurack(Integer user_id) {
		// TODO Auto-generated method stub
		return shurackDao.selectMyShurack(user_id);
	}

	@Override
	public void updateNextShip(Integer user_id) {
		// TODO Auto-generated method stub
		shurackDao.updateNextShip(user_id);
	}

	@Override
	public List<Shurack> selectDonateShurack(Integer user_id) {
		// TODO Auto-generated method stub
		return shurackDao.selectDonateShurack(user_id);
	}

	@Override
	public List<Shurack> selectBuyShurack(Integer user_id) {
		// TODO Auto-generated method stub
		return shurackDao.selectBuyShurack(user_id);
	}

	@Override
	public Integer countMyShurackByUserId(Integer user_id) {
		// TODO Auto-generated method stub
		return shurackDao.countMyShurackByUserId(user_id);
	}

	@Override
	public List<Shurack> selectShipShurack(Integer user_id,Integer limit) {
		// TODO Auto-generated method stub
		return shurackDao.selectShipShurack(user_id, limit);
	}

	@Override
	public Shurack selectShurackBySku(Integer user_id, Integer status, String product_sku) {
		// TODO Auto-generated method stub
		return shurackDao.selectShurackBySku(user_id, status, product_sku);
	}

	@Override
	public List<Shurack> selectBackendShurack() {
		// TODO Auto-generated method stub
		return shurackDao.selectBackendShurack();
	}

	@Override
	public void updateShurack() {
		// TODO Auto-generated method stub
		shurackDao.updateShurack();
	}

	@Override
	public Shurack selectShurackByShurackId(Integer shurack_id) {
		// TODO Auto-generated method stub
		return shurackDao.selectShurackByShurackId(shurack_id);
	}
	
	
	

}
