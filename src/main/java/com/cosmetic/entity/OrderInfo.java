package com.cosmetic.entity;

import java.math.BigDecimal;
import java.util.List;

public class OrderInfo {
	private Integer so_id;

	private Integer user_id;

	private Integer shurack_id;

	private Integer duplicate_id;
	
	private Integer create_time;

	private Integer pay_time;

	private Integer pay_status;	

	private Integer donate_time;

	private Integer donate_status;	

	private Integer ship_time;

	private Integer ship_status;
	
	private String product_sku;
	
	private BigDecimal amount;
	
	private Integer use_card;
	
	private String profile_id;
	
	private String name_of_card;
	
	private String card_type;

	private String tail;

	private String exp_year;

	private String exp_month;

	private Integer address_id;

	private String country;
	
	private String address;

	private String address2;

	private String city;

	private String state;

	private String zipcode;

	private String phone;



	public OrderInfo() {
		this.user_id = 0;
		this.shurack_id = 0;
		this.duplicate_id = 0;
		this.create_time = 0;
		this.pay_time = 0;
		this.pay_status = 0;
		this.donate_time = 0;
		this.donate_status = 0;
		this.ship_time = 0;
		this.ship_status = 0;
		this.product_sku = "";
		this.amount = new BigDecimal(0);
		this.use_card = 0;
		this.profile_id = "";
		this.name_of_card = "";
		this.card_type = "";
		this.tail = "";
		this.exp_year = "";
		this.exp_month = "";
		this.address_id = 0;
		this.country = "";
		this.address = "";
		this.address2 = "";
		this.city = "";
		this.state = "";
		this.zipcode = "";
		this.phone = "";
	}

	public Integer getDuplicate_id() {
		return duplicate_id;
	}

	public void setDuplicate_id(Integer duplicate_id) {
		this.duplicate_id = duplicate_id;
	}

	public Integer getShurack_id() {
		return shurack_id;
	}

	public void setShurack_id(Integer shurack_id) {
		this.shurack_id = shurack_id;
	}

	public Integer getDonate_time() {
		return donate_time;
	}

	public void setDonate_time(Integer donate_time) {
		this.donate_time = donate_time;
	}

	public Integer getDonate_status() {
		return donate_status;
	}

	public void setDonate_status(Integer donate_status) {
		this.donate_status = donate_status;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getShip_time() {
		return ship_time;
	}

	public void setShip_time(Integer ship_time) {
		this.ship_time = ship_time;
	}

	public Integer getShip_status() {
		return ship_status;
	}

	public void setShip_status(Integer ship_status) {
		this.ship_status = ship_status;
	}

	public Integer getSo_id() {
		return so_id;
	}

	public void setSo_id(Integer so_id) {
		this.so_id = so_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}

	public Integer getPay_time() {
		return pay_time;
	}

	public void setPay_time(Integer pay_time) {
		this.pay_time = pay_time;
	}

	public Integer getPay_status() {
		return pay_status;
	}

	public void setPay_status(Integer pay_status) {
		this.pay_status = pay_status;
	}

	public String getProduct_sku() {
		return product_sku;
	}

	public void setProduct_sku(String product_sku) {
		this.product_sku = product_sku;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getUse_card() {
		return use_card;
	}

	public void setUse_card(Integer use_card) {
		this.use_card = use_card;
	}

	public String getProfile_id() {
		return profile_id;
	}

	public void setProfile_id(String profile_id) {
		this.profile_id = profile_id;
	}

	public String getName_of_card() {
		return name_of_card;
	}

	public void setName_of_card(String name_of_card) {
		this.name_of_card = name_of_card;
	}

	public String getCard_type() {
		return card_type;
	}

	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}

	public String getTail() {
		return tail;
	}

	public void setTail(String tail) {
		this.tail = tail;
	}

	public String getExp_year() {
		return exp_year;
	}

	public void setExp_year(String exp_year) {
		this.exp_year = exp_year;
	}

	public String getExp_month() {
		return exp_month;
	}

	public void setExp_month(String exp_month) {
		this.exp_month = exp_month;
	}

	public Integer getAddress_id() {
		return address_id;
	}

	public void setAddress_id(Integer address_id) {
		this.address_id = address_id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}