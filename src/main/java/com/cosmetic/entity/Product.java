package com.cosmetic.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Product {
	
	    private Integer product_id;	    
	    private String product_sku;   
	    private String product_name;  
	    private String product_name_en;  
	    private BigDecimal upc; 
	    private String product_description;
	    private String product_description_en;
	    private Integer brand_id;
	    private String brand_name;
	    private String brand_name_en;
	    private String type;
	    private BigDecimal standard_price;
	    private BigDecimal member_price;
	    private Integer quantity;
	    private BigDecimal weight;
	    private String size;
	    private String material;
	    private String color;
	    private String image_url;
	    private String detail;
	    private Integer not_show;
	    private List<Image> images;
	    
		public List<Image> getImages() {
			return images;
		}

		public Integer getProduct_id() {
			return product_id;
		}

		public void setProduct_id(Integer product_id) {
			this.product_id = product_id;
		}

		public String getProduct_sku() {
			return product_sku;
		}

		public void setProduct_sku(String product_sku) {
			this.product_sku = product_sku;
		}

		public String getProduct_name() {
			return product_name;
		}

		public void setProduct_name(String product_name) {
			this.product_name = product_name;
		}

		public String getProduct_name_en() {
			return product_name_en;
		}

		public void setProduct_name_en(String product_name_en) {
			this.product_name_en = product_name_en;
		}

		public BigDecimal getUpc() {
			return upc;
		}

		public void setUpc(BigDecimal upc) {
			this.upc = upc;
		}

		public String getProduct_description() {
			return product_description;
		}

		public void setProduct_description(String product_description) {
			this.product_description = product_description;
		}

		public String getProduct_description_en() {
			return product_description_en;
		}

		public void setProduct_description_en(String product_description_en) {
			this.product_description_en = product_description_en;
		}

		public Integer getBrand_id() {
			return brand_id;
		}

		public void setBrand_id(Integer brand_id) {
			this.brand_id = brand_id;
		}

		public String getBrand_name() {
			return brand_name;
		}

		public void setBrand_name(String brand_name) {
			this.brand_name = brand_name;
		}

		public String getBrand_name_en() {
			return brand_name_en;
		}

		public void setBrand_name_en(String brand_name_en) {
			this.brand_name_en = brand_name_en;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public BigDecimal getStandard_price() {
			return standard_price;
		}

		public void setStandard_price(BigDecimal standard_price) {
			this.standard_price = standard_price;
		}

		public BigDecimal getMember_price() {
			return member_price;
		}

		public void setMember_price(BigDecimal member_price) {
			this.member_price = member_price;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public BigDecimal getWeight() {
			return weight;
		}

		public void setWeight(BigDecimal weight) {
			this.weight = weight;
		}

		public String getSize() {
			return size;
		}

		public void setSize(String size) {
			this.size = size;
		}

		public String getMaterial() {
			return material;
		}

		public void setMaterial(String material) {
			this.material = material;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getImage_url() {
			return image_url;
		}

		public void setImage_url(String image_url) {
			this.image_url = image_url;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public Integer getNot_show() {
			return not_show;
		}

		public void setNot_show(Integer not_show) {
			this.not_show = not_show;
		}

		public void setImages(List<Image> images) {
			this.images = images;
		}
		


	  


}
