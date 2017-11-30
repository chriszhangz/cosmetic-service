package com.cosmetic.delegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;









import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.cosmetic.common.ErrorCodeEnum;
import com.cosmetic.common.YamiConstant;
import com.cosmetic.common.YamiException;
import com.cosmetic.entity.Image;
import com.cosmetic.entity.Index;
import com.cosmetic.entity.Product;
import com.cosmetic.entity.ProductForList;
import com.cosmetic.service.ProductService;

@Service
public class ProductServiceDelegate {
	@Autowired
	private ProductService productService;
	@Autowired 
	private RedisTemplate<String,String> redisTemplate;

	@Autowired
	private TransactionDelegate transactionDelegate;
	
	public Map<String,Object> selectIndexInfo(String token, String type) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		Integer typeInteger = Integer.valueOf(type);
		List<String> Indexs= productService.selectIndexInfo(typeInteger);
		
		result.put("token", token);
		result.put("indexs", Indexs);
		
		return result;
		
	}
	
	public Map<String,Object> selectProductBySku(String token, String product_sku) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		List<Map<String,String>> colors = new ArrayList<Map<String,String>>();
		List<Map<String,String>> sizes = new ArrayList<Map<String,String>>();
		List<Image> images = new ArrayList<Image>();
		//从编辑表取得数据
		//result.put("token", token);
		Product productTemp = productService.selectBySku(product_sku);
		if(null==productTemp){
			throw new YamiException(YamiConstant.ERRORCODE_ER1101,ErrorCodeEnum.ER1101.getMsg());
		}
		result.put("token", token);
		result.put("product_sku", product_sku);
		result.put("product_name", productTemp.getProduct_name());
		result.put("image_url", YamiConstant.WEB_PREFIX+productTemp.getImage_url());
		result.put("product_description", productTemp.getProduct_description());
		result.put("quantity", productTemp.getQuantity());
		result.put("brand_name", productTemp.getBrand_name());
		result.put("standard_price", productTemp.getStandard_price());
		result.put("member_price", productTemp.getMember_price());
		result.put("detail", productTemp.getDetail());
		//result.put("desingner_note", productTemp.getDesingner_note());
		result.put("colors", colors);

		result.put("size_us", sizes);
		images = productTemp.getImages();
		result.put("images", images);
		
		return result;
	}

	public Map<String,Object> filterProductBySku(String token, String style_type, String size_us, String color, String page) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		List<Map<String,Object>>  productList = new ArrayList<Map<String, Object>>();
		Integer pageInt = Integer.valueOf(page);
		if(pageInt==0){
			pageInt=1;
		}
		Integer start = (pageInt-1)*YamiConstant.ITEMS_PER_PAGE;
		if(null!=style_type&&"".equals(style_type.trim())){
			style_type = null;
		}
		if(null!=size_us&&"".equals(size_us.trim())){
			size_us = null;
		}
		if(null!=color&&"".equals(color.trim())){
			color = null;
		}
		List<Product> products = productService.filterBySSC(start, YamiConstant.ITEMS_PER_PAGE, style_type, size_us, color);
		if(null==products||products.size()==0){
			throw new YamiException(YamiConstant.ERRORCODE_ER1101,ErrorCodeEnum.ER1101.getMsg());
		}
		int total = productService.countBySSC(style_type, size_us, color);
		for(int i = 0;i<products.size();i++){
		Map<String,Object>  product = new HashMap<String, Object>();
		List<Map<String,String>> colors = new ArrayList<Map<String,String>>();
		List<Map<String,String>> sizes = new ArrayList<Map<String,String>>();
		List<Image> images = new ArrayList<Image>();
		//从编辑表取得数据
		//result.put("token", token);
		Product productTemp = products.get(i);

		product.put("product_sku", productTemp.getProduct_sku());
		product.put("product_name", productTemp.getProduct_name());
		product.put("image_url", YamiConstant.WEB_PREFIX+productTemp.getImage_url());
		product.put("product_description", productTemp.getProduct_description());
		product.put("quantity", productTemp.getQuantity());
		product.put("brand_name", productTemp.getBrand_name());
		product.put("standard_price", productTemp.getStandard_price());
		product.put("member_price", productTemp.getMember_price());
		product.put("detail", productTemp.getDetail());
		//product.put("desingner_note", productTemp.getDesingner_note());
		product.put("color", productTemp.getColor());
		product.put("colors", colors);
		product.put("sizes", sizes);
		images = productTemp.getImages();
		product.put("images", images);
		productList.add(product);
		}
		result.put("token", token);
		result.put("productList", productList);
		int totalPage = 0;
		if(total % YamiConstant.ITEMS_PER_PAGE ==0){
			totalPage = total/YamiConstant.ITEMS_PER_PAGE;
		}else{
			totalPage = total/YamiConstant.ITEMS_PER_PAGE+1;
		}
		result.put("total", total);
		result.put("totalPage", totalPage);
		return result;
	}
	public Map<String,Object> listProductByPromo(String token, String category, String style_type, String size_us, String color, String page) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		List<Map<String,Object>>  productList = new ArrayList<Map<String, Object>>();
		Integer pageInt = Integer.valueOf(page);
		if(pageInt==0){
			pageInt=1;
		}
		Integer start = (pageInt-1)*YamiConstant.ITEMS_PER_PAGE;
		if(null!=style_type&&"".equals(style_type.trim())){
			style_type = null;
		}
		if(null!=size_us&&"".equals(size_us.trim())){
			size_us = null;
		}
		if(null!=color&&"".equals(color.trim())){
			color = null;
		}
		List<Product> products = productService.listByPromo(category,start, YamiConstant.ITEMS_PER_PAGE, style_type, size_us, color);
		if(null==products||products.size()==0){
			throw new YamiException(YamiConstant.ERRORCODE_ER1101,ErrorCodeEnum.ER1101.getMsg());
		}
		int total = productService.countByPromo(category, style_type, size_us, color);
		for(int i = 0;i<products.size();i++){
		Map<String,Object>  product = new HashMap<String, Object>();
		List<Map<String,String>> colors = new ArrayList<Map<String,String>>();
		List<Map<String,String>> sizes = new ArrayList<Map<String,String>>();
		List<Image> images = new ArrayList<Image>();
		//从编辑表取得数据
		//result.put("token", token);
		Product productTemp = products.get(i);

		product.put("product_sku", productTemp.getProduct_sku());
		product.put("product_name", productTemp.getProduct_name());
		product.put("image_url", YamiConstant.WEB_PREFIX+productTemp.getImage_url());
		product.put("product_description", productTemp.getProduct_description());
		product.put("quantity", productTemp.getQuantity());
		product.put("brand_name", productTemp.getBrand_name());
		product.put("standard_price", productTemp.getStandard_price());
		product.put("member_price", productTemp.getMember_price());
		product.put("detail", productTemp.getDetail());
		//product.put("desingner_note", productTemp.getDesingner_note());
		product.put("color", productTemp.getColor());
		product.put("colors", colors);

		product.put("sizes", sizes);
		images = productTemp.getImages();
		product.put("images", images);
		productList.add(product);
		}
		int totalPage = 0;
		if(total % YamiConstant.ITEMS_PER_PAGE ==0){
			totalPage = total/YamiConstant.ITEMS_PER_PAGE;
		}else{
			totalPage = total/YamiConstant.ITEMS_PER_PAGE+1;
		}
		result.put("total", total);
		result.put("totalPage", totalPage);		
		result.put("token", token);
		result.put("productList", productList);
		return result;
	}
	public Map<String,Object> selectSimilarProduct(String token, String style_type, String size_us, String color) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		List<ProductForList> products= productService.selectSimilarBySCC(style_type, size_us, color);
		result.put("token", token);
		result.put("products", products);
		
		return result;
		
	}
	
	public Map<String,Object> selectAlsoLike(String token, String product_sku) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		List<ProductForList> products= productService.selectAlsoLike(product_sku);
		result.put("token", token);
		result.put("products", products);
		
		return result;
		
	}
	
	public Map<String,Object> getAllStyle(String token) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		List<String> styleList= productService.getAllStyle();
		result.put("token", token);
		result.put("styleList", styleList);
		
		return result;	
	}
	public Map<String,Object> getAllSize(String token) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		List<String> sizeList= productService.getAllSize();
		result.put("token", token);
		result.put("sizeList", sizeList);
		
		return result;	
	}
	public Map<String,Object> getAllColor(String token) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		List<String> colorList= productService.getAllColor();
		result.put("token", token);
		result.put("colorList", colorList);
		
		return result;	
	}	
	public Map<String,Object> getPromoList(String token) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();
		List<String> promoList= productService.getPromoList();
		result.put("token", token);
		result.put("promoList", promoList);
		
		return result;	
	}		
}
