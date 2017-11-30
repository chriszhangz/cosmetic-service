package com.cosmetic.entity;

import java.math.BigDecimal;
import java.util.List;

public class UnSubOrder {
    private String sub_order_id;

    private Integer unsub_type;

    private Integer user_id;

    private Integer unsub_time;
    
    private String unsub_msg;

	public String getSub_order_id() {
		return sub_order_id;
	}

	public void setSub_order_id(String sub_order_id) {
		this.sub_order_id = sub_order_id;
	}

	public Integer getUnsub_type() {
		return unsub_type;
	}

	public void setUnsub_type(Integer unsub_type) {
		this.unsub_type = unsub_type;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getUnsub_time() {
		return unsub_time;
	}

	public void setUnsub_time(Integer unsub_time) {
		this.unsub_time = unsub_time;
	}

	public String getUnsub_msg() {
		return unsub_msg;
	}

	public void setUnsub_msg(String unsub_msg) {
		this.unsub_msg = unsub_msg;
	}




}