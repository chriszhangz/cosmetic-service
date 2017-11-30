package com.cosmetic.entity;



public class Coupon {

    private Integer rec_id;
	private String coupon_id;
	private String code;
    private Integer type;
    private Integer shoo_coin;
    private Integer status;

	public Integer getShoo_coin() {
		return shoo_coin;
	}

	public void setShoo_coin(Integer shoo_coin) {
		this.shoo_coin = shoo_coin;
	}

	public Integer getRec_id() {
		return rec_id;
	}

	public void setRec_id(Integer rec_id) {
		this.rec_id = rec_id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCoupon_id() {
		return coupon_id;
	}

	public void setCoupon_id(String coupon_id) {
		this.coupon_id = coupon_id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}


	

}
