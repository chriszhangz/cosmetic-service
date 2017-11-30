package com.cosmetic.delegate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;















import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
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
public class AdminServiceDelegate {
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
		result.put("image_url", productTemp.getImage_url());
		result.put("product_description", productTemp.getProduct_description());
		result.put("quantity", productTemp.getQuantity());
		result.put("brand_name", productTemp.getBrand_name());
		result.put("standard_price", productTemp.getStandard_price());
		result.put("member_price", productTemp.getMember_price());
		result.put("detail", productTemp.getDetail());
		//result.put("desingner_note", productTemp.getDesingner_note());

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
		Integer start = (pageInt-1)*9;
		List<Product> products = productService.filterBySSC(start, 9, style_type, size_us, color);
		if(null==products||products.size()==0){
			throw new YamiException(YamiConstant.ERRORCODE_ER1101,ErrorCodeEnum.ER1101.getMsg());
		}
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
		product.put("image_url", productTemp.getImage_url());
		product.put("product_description", productTemp.getProduct_description());
		product.put("quantity", productTemp.getQuantity());
		product.put("brand_name", productTemp.getBrand_name());
		product.put("standard_price", productTemp.getStandard_price());
		product.put("member_price", productTemp.getMember_price());
		product.put("detail", productTemp.getDetail());
		//product.put("desingner_note", productTemp.getDesingner_note());
		product.put("colors", colors);
		product.put("size_us", sizes);
		images = productTemp.getImages();
		product.put("images", images);
		productList.add(product);
		}
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
	
	public Map<String,Object> importCsv(MultipartFormDataInput input) throws Exception{
		Map<String,Object>  result = new HashMap<String, Object>();

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		//validate token
		List<InputPart> inputParts = uploadForm.get("uploadedFile");
		List<Product> productList = new ArrayList<Product>();
		String fileName="";
		String fullpath="";
		String fullpath1="";
		String csvSplitBy = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
		BufferedReader br=null;

		for (InputPart inputPart : inputParts) 
		{
			MultivaluedMap<String, String> header = inputPart.getHeaders();
			fileName = getFileName(header);
			System.out.println(fileName);
			//convert the uploaded file to inputstream
			InputStream inputStream = inputPart.getBody(InputStream.class,null);
			br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
			String line = ""; 
			br.readLine();
            while ((line = br.readLine()) != null) { 
            	String[] info = line.split(csvSplitBy);
            	Product product = new Product();
            	product.setProduct_sku(info[0].trim());
            product.setProduct_sku(info[0].trim());
            product.setProduct_name(info[1].trim());
            product.setUpc(new BigDecimal(info[2].trim()));
            product.setProduct_description("".equals(info[3].trim())?"":info[3].trim());
            product.setBrand_id("".equals(info[4].trim())?0:Integer.valueOf(info[4].trim()));
            product.setBrand_name("".equals(info[5].trim())?"":info[5].trim());
            product.setStandard_price("".equals(info[8].trim())?new BigDecimal(0):new BigDecimal(info[8].trim()));
            product.setMember_price("".equals(info[9].trim())?new BigDecimal(0):new BigDecimal(info[9].trim()));
            product.setQuantity("".equals(info[10].trim())?0:Integer.valueOf(info[10].trim()));
            product.setWeight("".equals(info[11].trim())?new BigDecimal(0):new BigDecimal(info[11].trim()));

            product.setMaterial("".equals(info[13].trim())?"":info[13].trim());
            product.setColor("".equals(info[14].trim())?"":info[14].trim());

            product.setImage_url("".equals(info[17].trim())?"":info[17].trim());
            product.setDetail("".equals(info[23].trim())?"":info[23].trim());
            product.setNot_show("".equals(info[24].trim())?0:Integer.valueOf(info[24].trim()));
            productList.add(product);
            }
            productService.insertBatch(productList);
		}
		
		result.put("status", YamiConstant.STATUS_OK);
		
		return result;
		
	}
		
		private String getFileName(MultivaluedMap<String, String> header) {
			String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
			for (String filename : contentDisposition) {
				if ((filename.trim().startsWith("filename"))) {
					String[] name = filename.split("=");
					String finalFileName = name[1].trim().replaceAll("\"", "");
					return finalFileName;
				}
			}
			return "unknown";
		}
}
