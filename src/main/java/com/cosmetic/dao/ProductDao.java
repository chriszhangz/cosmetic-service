
package com.cosmetic.dao;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Product;
import com.cosmetic.entity.ProductForList;

public interface ProductDao {  
	
    public Product selectBySku(@Param("product_sku") String product_sku);
    public int updateProductNum(@Param("quantity") int quantity,@Param("product_sku") String product_sku); 
    public int selectQuantityBySku(@Param("product_sku") String product_sku);   
    public List<Product> selectByParentSku(String parent_sku);  
    public List<Map<String,String>> selectColorByParentSku(String parent_sku);
    public List<ProductForList> selectSimilarBySCC(@Param("style_type") String style_type,@Param("size_us") String size_us,@Param("color") String color);    
    public List<ProductForList> selectAlsoLike(String product_sku);  
    public int countBySSC(@Param("style_type") String style_type,@Param("size_us") String size_us,@Param("color") String color);
    public int countByPromo(@Param("category") String category,@Param("style_type") String style_type,@Param("size_us") String size_us,@Param("color") String color);
    public List<Product> filterBySSC(@Param("start") Integer start,@Param("leng") Integer leng,@Param("style_type") String style_type,@Param("size_us") String size_us,@Param("color") String color);
    public List<Product> listByPromo(@Param("category") String category,@Param("start") Integer start,@Param("leng") Integer leng,@Param("style_type") String style_type,@Param("size_us") String size_us,@Param("color") String color);
    public List<Map<String,String>> selectSizeByColor(@Param("parent_sku") String parent_sku,@Param("color") String color);
    public Map<String,Object> selectPriceBySku(String parent_sku);
    public int subQuantityBySku(String product_sku);
    public void insertBatch(@Param("list") List<Product> list);  
    public List<String> getAllSize();    
    public List<String> getAllStyle();    
    public List<String> getAllColor();  
    public String getPurchaseName(int so_id);  
    public List<String> getPromoList();  
    
  
}
