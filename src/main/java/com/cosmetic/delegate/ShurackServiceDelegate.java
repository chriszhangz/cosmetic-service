package com.cosmetic.delegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;











import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.cosmetic.common.DateUtil;
import com.cosmetic.common.ErrorCodeEnum;
import com.cosmetic.common.StringUtil;
import com.cosmetic.common.YamiConstant;
import com.cosmetic.common.YamiException;
import com.cosmetic.entity.Image;
import com.cosmetic.entity.Product;
import com.cosmetic.entity.ProductForList;
import com.cosmetic.entity.Shurack;
import com.cosmetic.entity.Token;
import com.cosmetic.service.ProductService;
import com.cosmetic.service.ShurackService;
import com.google.gson.Gson;

@Service
public class ShurackServiceDelegate {
	@Autowired
	private ShurackService shurackService;
	@Autowired
	private ProductService productService;
	@Autowired
	private TransactionDelegate transactionDelegate;
	
	public Map<String,Object> countShurackByUserId(String token, String status) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		Gson gson = new Gson(); 
		Token tokenIn = gson.fromJson( StringUtil.decode(token), Token.class);
		int userId = Integer.parseInt(tokenIn.getData());	
		Integer st = Integer.valueOf(status);
		Integer count = shurackService.countShurackByUserId(userId, st);

		result.put("token", token);
		result.put("count", count);
		
		return result;
	}
	
	public Map<String,Object> selectShurackByUid(String token, String status) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		Gson gson = new Gson(); 
		Token tokenIn = gson.fromJson( StringUtil.decode(token), Token.class);
		int userId = Integer.parseInt(tokenIn.getData());	
		Integer st = Integer.valueOf(status);
		List<Shurack> shurack = shurackService.selectShurackByUserId(userId, st);
		if(null==shurack){
			throw new YamiException(YamiConstant.ERRORCODE_ER1201,ErrorCodeEnum.ER1201.getMsg());
		}
		result.put("token", token);
		result.put("shurack", shurack);
		
		return result;
	}

	public Map<String,Object> selectMyShurack(String token) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		Gson gson = new Gson(); 
		Token tokenIn = gson.fromJson( StringUtil.decode(token), Token.class);
		int userId = Integer.parseInt(tokenIn.getData());	
		List<Shurack> shurack = shurackService.selectMyShurack(userId);
		if(null==shurack){
			throw new YamiException(YamiConstant.ERRORCODE_ER1201,ErrorCodeEnum.ER1201.getMsg());
		}
/*		for(Shurack sck:shurack){
			if(sck.getShip_time()!=0){
			sck.setShip_time(DateUtil.formateShiptime(sck.getShip_time()));
			}
		}*/
		result.put("token", token);
		result.put("shurack", shurack);
		
		return result;
	}
	
	public Map<String,Object> selectDonateShurack(String token) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		Gson gson = new Gson(); 
		Token tokenIn = gson.fromJson( StringUtil.decode(token), Token.class);
		int userId = Integer.parseInt(tokenIn.getData());	
		List<Shurack> shurack = shurackService.selectDonateShurack(userId);
		if(null==shurack){
			throw new YamiException(YamiConstant.ERRORCODE_ER1201,ErrorCodeEnum.ER1201.getMsg());
		}
		result.put("token", token);
		result.put("shurack", shurack);
		
		return result;
	}
	
	public Map<String,Object> selectBuyShurack(String token) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		Gson gson = new Gson(); 
		Token tokenIn = gson.fromJson( StringUtil.decode(token), Token.class);
		int userId = Integer.parseInt(tokenIn.getData());	
		List<Shurack> shurack = shurackService.selectBuyShurack(userId);
		if(null==shurack){
			throw new YamiException(YamiConstant.ERRORCODE_ER1201,ErrorCodeEnum.ER1201.getMsg());
		}
		result.put("token", token);
		result.put("shurack", shurack);
		
		return result;
	}
	
	public Map<String,Object> insertShurack(String token, String product_sku,String image_url,String product_name,String size_us) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		Gson gson = new Gson(); 
		Token tokenIn = gson.fromJson( StringUtil.decode(token), Token.class);
		int userId = Integer.parseInt(tokenIn.getData());
		if(shurackService.countShurackBySku(userId, 0, product_sku)!=0){
			throw new YamiException(YamiConstant.ERRORCODE_ER1102,ErrorCodeEnum.ER1102.getMsg());
		}
		if(productService.selectQuantityBySku(product_sku)<=0){
			throw new YamiException(YamiConstant.ERRORCODE_ER1404,ErrorCodeEnum.ER1404.getMsg());			
		}
		Shurack shurack = new Shurack();
		shurack.setAdd_time(Integer.parseInt(DateUtil.getNowLong().toString()));
		shurack.setProduct_sku(product_sku);
		shurack.setStatus(0);
		shurack.setUser_id(userId);
		shurackService.insertShurack(shurack);
		result.put("image_url", image_url);
		result.put("product_name", product_name);
		result.put("size_us", size_us);
		result.put("token", token);
		result.put("status", YamiConstant.STATUS_OK);
		
		return result;
	}
	
	public Map<String,Object> updateToTop(String token,String product_sku) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		Gson gson = new Gson(); 
		Token tokenIn = gson.fromJson( StringUtil.decode(token), Token.class);
		int userId = Integer.parseInt(tokenIn.getData());
		Integer addTime = shurackService.selectNextShip(userId);
		if(addTime-100<0){
			addTime=0;		
		}else{
			addTime=addTime-100;			
		}
		shurackService.updateToTop(userId,addTime,product_sku);
		result.put("token", token);
		result.put("status", YamiConstant.STATUS_OK);
		
		return result;
	}

	public Map<String,Object> insertTopOfShurack(String token, String product_sku,String image_url,String product_name,String size_us) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		Gson gson = new Gson(); 
		Token tokenIn = gson.fromJson( StringUtil.decode(token), Token.class);
		int userId = Integer.parseInt(tokenIn.getData());
		if(shurackService.countShurackBySku(userId, 0, product_sku)!=0){
			throw new YamiException(YamiConstant.ERRORCODE_ER1102,ErrorCodeEnum.ER1102.getMsg());
		}		
		Shurack shurack = new Shurack();
		Integer addTime = shurackService.selectNextShip(userId);
		if(addTime-100<0){
			shurack.setAdd_time(0);			
		}else{
			shurack.setAdd_time(addTime-100);			
		}
		shurack.setProduct_sku(product_sku);
		shurack.setStatus(0);
		shurack.setUser_id(userId);
		shurackService.insertShurack(shurack);
		result.put("image_url", image_url);
		result.put("product_name", product_name);
		result.put("size_us", size_us);
		result.put("token", token);
		result.put("status", YamiConstant.STATUS_OK);
		
		return result;
	}
	
	public Map<String,Object> deleteShurack(String token,String product_sku) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		Gson gson = new Gson(); 
		Token tokenIn = gson.fromJson( StringUtil.decode(token), Token.class);
		int userId = Integer.parseInt(tokenIn.getData());

		shurackService.deleteShurack(userId, product_sku);
		result.put("token", token);
		result.put("status", YamiConstant.STATUS_OK);
		
		return result;
	}
}
