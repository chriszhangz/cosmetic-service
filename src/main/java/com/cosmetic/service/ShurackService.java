package com.cosmetic.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Shurack;

public interface ShurackService {

	public List<Shurack> selectShipShurack(Integer user_id,Integer limit);
	public List<Shurack> selectMyShurack(Integer user_id);
	public List<Shurack> selectBackendShurack();
	public void updateShurack();
	public Shurack selectShurackByShurackId(Integer shurack_id);
	
	public List<Shurack> selectDonateShurack(Integer user_id);
	
	public List<Shurack> selectBuyShurack(Integer user_id);
	
	public List<Shurack> selectShurackByUserId(Integer user_id, Integer status);
	
	public List<Shurack> selectShurackByStatus(Integer status);
	
	public Integer countShurackByUserId(Integer user_id,Integer status);
	
	public Integer countMyShurackByUserId(Integer user_id);

	public Shurack selectShurackBySku(Integer user_id,Integer status, String product_sku);
	
	public Integer countShurackBySku(Integer user_id,Integer status, String product_sku);

	public Integer selectNextShip(Integer user_id);
	
	public void updateNextShip(Integer user_id);

	public void insertShurack(Shurack shurack);

	public void deleteShurack(Integer user_id, String product_sku);

	public void updateToTop(Integer user_id, Integer add_time, String product_sku);

	public void updateStatus(Integer shurack_id, Integer status);
	
	public void updateStatusAndShipTime(Integer shurack_id, Integer status, Integer ship_time);
	
	public void updateStatusAndDonateTime(Integer shurack_id, Integer status, Integer donate_time);
	
	public void updateStatusAndBuyTime(Integer shurack_id, Integer status, Integer buy_time);
	

}
