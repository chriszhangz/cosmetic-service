package com.cosmetic.impl;



import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosmetic.dao.IndexDao;
import com.cosmetic.dao.ProductDao;
import com.cosmetic.entity.ColorCode;
import com.cosmetic.entity.Index;
import com.cosmetic.entity.Product;
import com.cosmetic.entity.ProductForList;
import com.cosmetic.entity.Size;
import com.cosmetic.entity.Style;
import com.cosmetic.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductDao productDao;
	@Autowired
	private IndexDao indexDao;	

	@Override
	public Product selectBySku(String product_sku) {
		// TODO Auto-generated method stub
		return productDao.selectBySku(product_sku);
	}
	@Override
	public int updateProductNum(int quantity, String product_sku) {
		// TODO Auto-generated method stub
		return productDao.updateProductNum(quantity, product_sku);
	}
	@Override
	public List<Product> selectByParentSku(String parent_sku) {
		// TODO Auto-generated method stub
		return productDao.selectByParentSku(parent_sku);
	}
	@Override
	public List<ProductForList> selectSimilarBySCC(String style_type, String size_us, String color) {
		// TODO Auto-generated method stub
		return productDao.selectSimilarBySCC(style_type, size_us, color);
	}
	@Override
	public List<ProductForList> selectAlsoLike(String product_sku) {
		// TODO Auto-generated method stub
		return productDao.selectAlsoLike(product_sku);
	}	
	@Override
	public List<Product> filterBySSC(Integer start, Integer leng, String style_type, String size_us,
			String color) {
		// TODO Auto-generated method stub
		return productDao.filterBySSC(start, leng, style_type, size_us, color);
	}
	@Override
	public List<Map<String,String>> selectSizeByColor(String parent_sku, String color) {
		// TODO Auto-generated method stub
		return productDao.selectSizeByColor(parent_sku, color);
	}
	@Override
	public List<Map<String,String>> selectColorByParentSku(String parent_sku) {
		// TODO Auto-generated method stub
		return productDao.selectColorByParentSku(parent_sku);
	}
	@Override
	public List<String> selectIndexInfo(Integer type) {
		// TODO Auto-generated method stub
		return indexDao.selectIndexInfo(type);
	}
	@Override
	public Map<String, Object> selectPriceBySku(String parent_sku) {
		// TODO Auto-generated method stub
		return productDao.selectPriceBySku(parent_sku);
	}
	@Override
	public int subQuantityBySku(String parent_sku) {
		// TODO Auto-generated method stub
		return productDao.subQuantityBySku(parent_sku);
	}
	@Override
	public void insertBatch(List<Product> list) {
		// TODO Auto-generated method stub
		productDao.insertBatch(list);
	}
	@Override
	public int selectQuantityBySku(String product_sku) {
		// TODO Auto-generated method stub
		return productDao.selectQuantityBySku(product_sku);
	}
	@Override
	public List<Product> listByPromo(String category, Integer start, Integer leng, String style_type,
			String size_us, String color) {
		// TODO Auto-generated method stub
		return productDao.listByPromo(category, start, leng, style_type, size_us, color);
	}
	@Override
	public int countBySSC(String style_type, String size_us, String color) {
		// TODO Auto-generated method stub
		return productDao.countBySSC(style_type, size_us, color);
	}
	@Override
	public int countByPromo(String category, String style_type, String size_us, String color) {
		// TODO Auto-generated method stub
		return productDao.countByPromo(category, style_type, size_us, color);
	}
	@Override
	public List<String> getAllSize() {
		// TODO Auto-generated method stub
		return productDao.getAllSize();
	}
	@Override
	public List<String> getAllStyle() {
		// TODO Auto-generated method stub
		return productDao.getAllStyle();
	}
	@Override
	public List<String> getAllColor() {
		// TODO Auto-generated method stub
		return productDao.getAllColor();
	}
	@Override
	public String getPurchaseName(int so_id) {
		// TODO Auto-generated method stub
		return productDao.getPurchaseName(so_id);
	}
	@Override
	public List<String> getPromoList() {
		// TODO Auto-generated method stub
		return productDao.getPromoList();
	}

}
