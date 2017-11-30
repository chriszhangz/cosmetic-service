package com.cosmetic.entity;

import java.math.BigDecimal;



public class Shurack {
	
	private Integer shurack_id;
	private Integer user_id;

    private String product_sku;

    private Integer add_time;
    private Integer donate_time;
    private Integer buy_time;
    private Integer ship_time;
    private Integer status;
    private String style_type;
    private Integer quantity;
    private String size_us;
    private String color;
    private String image_url;
    private String product_name;  
    private BigDecimal member_price;
    
	public Integer getShip_time() {
		return ship_time;
	}
	public void setShip_time(Integer ship_time) {
		this.ship_time = ship_time;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public BigDecimal getMember_price() {
		return member_price;
	}
	public void setMember_price(BigDecimal member_price) {
		this.member_price = member_price;
	}
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getStyle_type() {
		return style_type;
	}
	public void setStyle_type(String style_type) {
		this.style_type = style_type;
	}
	public String getSize_us() {
		return size_us;
	}
	public void setSize_us(String size_us) {
		this.size_us = size_us;
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
	public Integer getShurack_id() {
		return shurack_id;
	}
	public void setShurack_id(Integer shurack_id) {
		this.shurack_id = shurack_id;
	}
	public String getProduct_sku() {
		return product_sku;
	}
	public void setProduct_sku(String product_sku) {
		this.product_sku = product_sku;
	}
	public Integer getAdd_time() {
		return add_time;
	}
	public void setAdd_time(Integer add_time) {
		this.add_time = add_time;
	}
	public Integer getDonate_time() {
		return donate_time;
	}
	public void setDonate_time(Integer donate_time) {
		this.donate_time = donate_time;
	}
	public Integer getBuy_time() {
		return buy_time;
	}
	public void setBuy_time(Integer buy_time) {
		this.buy_time = buy_time;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

   
		

}
