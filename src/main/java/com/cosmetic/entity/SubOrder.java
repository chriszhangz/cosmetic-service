package com.cosmetic.entity;

import java.math.BigDecimal;
import java.util.List;

public class SubOrder {
	private Integer rec_id;

	private String sub_order_id;
	private Integer sub_type;

	private Integer user_id;

	private String customer;
	private String cancel_at_period_end;
	private Integer canceled_at;
	private Integer created;
	private Integer current_period_start;
	private Integer current_period_end;
	private Integer ended_at;
	private String plan_id;
	private BigDecimal amount;
	private Integer start;
	private String status;

	public Integer getRec_id() {
		return rec_id;
	}

	public void setRec_id(Integer rec_id) {
		this.rec_id = rec_id;
	}

	public String getSub_order_id() {
		return sub_order_id;
	}

	public void setSub_order_id(String sub_order_id) {
		this.sub_order_id = sub_order_id;
	}

	public Integer getSub_type() {
		return sub_type;
	}

	public void setSub_type(Integer sub_type) {
		this.sub_type = sub_type;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCancel_at_period_end() {
		return cancel_at_period_end;
	}

	public void setCancel_at_period_end(String cancel_at_period_end) {
		this.cancel_at_period_end = cancel_at_period_end;
	}

	public Integer getCanceled_at() {
		return canceled_at;
	}

	public void setCanceled_at(Integer canceled_at) {
		this.canceled_at = canceled_at;
	}

	public Integer getCreated() {
		return created;
	}

	public void setCreated(Integer created) {
		this.created = created;
	}

	public Integer getCurrent_period_start() {
		return current_period_start;
	}

	public void setCurrent_period_start(Integer current_period_start) {
		this.current_period_start = current_period_start;
	}

	public Integer getCurrent_period_end() {
		return current_period_end;
	}

	public void setCurrent_period_end(Integer current_period_end) {
		this.current_period_end = current_period_end;
	}

	public Integer getEnded_at() {
		return ended_at;
	}

	public void setEnded_at(Integer ended_at) {
		this.ended_at = ended_at;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}