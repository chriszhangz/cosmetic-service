package com.cosmetic.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;









import com.cosmetic.entity.ColorCode;
import com.cosmetic.entity.Index;
import com.cosmetic.entity.Product;
import com.cosmetic.entity.ProductForList;
import com.cosmetic.entity.Size;
import com.cosmetic.entity.Style;

public interface ProductService {
	public Product selectBySku(String product_sku);
    public int updateProductNum(int quantity,String product_sku);  
    public int selectQuantityBySku(String product_sku);    
    public List<Product> selectByParentSku(String parent_sku);
    public List<Map<String,String>> selectColorByParentSku(String parent_sku);
    public List<ProductForList> selectSimilarBySCC(String style_type,String size_us,String color); 
    public List<ProductForList> selectAlsoLike(String product_sku);
    public int countBySSC(String style_type, String size_us, String color);
    public int countByPromo(String category,String style_type, String size_us, String color);
    public List<Product> filterBySSC(Integer start,Integer leng,String style_type, String size_us, String color);
    public List<Product> listByPromo(String category,Integer start,Integer leng,String style_type, String size_us, String color);
    public List<Map<String,String>> selectSizeByColor(String parent_sku, String color);

	public List<String> selectIndexInfo(Integer type);
    public Map<String,Object> selectPriceBySku(String parent_sku);
    public int subQuantityBySku(String product_sku);
    public void insertBatch(List<Product> list); 
    public List<String> getAllSize();    
    public List<String> getAllStyle();    
    public List<String> getAllColor();  
    public String getPurchaseName(int so_id);
    public List<String> getPromoList();  
    
}
