
package com.cosmetic.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Shurack;

public interface ShurackDao {    
	public List<Shurack> selectShipShurack(@Param("user_id") Integer user_id,@Param("limit") Integer limit);
	public List<Shurack> selectMyShurack(@Param("user_id") Integer user_id);
	public Shurack selectShurackByShurackId(@Param("shurack_id") Integer shurack_id);
	public List<Shurack> selectBackendShurack();
	public void updateShurack();
	public List<Shurack> selectDonateShurack(@Param("user_id") Integer user_id);
	public List<Shurack> selectBuyShurack(@Param("user_id") Integer user_id);
	public List<Shurack> selectShurackByUserId(@Param("user_id") Integer user_id,@Param("status") Integer status);
	public List<Shurack> selectShurackByStatus(@Param("status") Integer status);
	public Integer countMyShurackByUserId(@Param("user_id") Integer user_id);
	public Integer countShurackByUserId(@Param("user_id") Integer user_id,@Param("status") Integer status);
	public Integer countShurackBySku(@Param("user_id") Integer user_id,@Param("status") Integer status,@Param("product_sku") String product_sku);
	public Shurack selectShurackBySku(@Param("user_id") Integer user_id,@Param("status") Integer status,@Param("product_sku") String product_sku);
	public Integer selectNextShip(Integer user_id);
	public void updateNextShip(@Param("user_id") Integer user_id);
	public void insertShurack(Shurack shurack);
	public void deleteShurack(@Param("user_id") Integer user_id,@Param("product_sku") String product_sku);
	public void updateToTop(@Param("user_id") Integer user_id,@Param("add_time") Integer add_time,@Param("product_sku") String product_sku);
	public void updateStatus(@Param("shurack_id") Integer shurack_id,@Param("status") Integer status);
	public void updateStatusAndShipTime(@Param("shurack_id") Integer shurack_id,@Param("status") Integer status,@Param("ship_time") Integer ship_time);
	public void updateStatusAndDonateTime(@Param("shurack_id") Integer shurack_id,@Param("status") Integer status,@Param("donate_time") Integer donate_time);
	public void updateStatusAndBuyTime(@Param("shurack_id") Integer shurack_id,@Param("status") Integer status,@Param("buy_time") Integer buy_time);
}