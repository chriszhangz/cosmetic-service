package com.cosmetic.dao;

import java.util.List;

import com.cosmetic.entity.Coupon;


public interface CouponDao {

    public Coupon selectCouponInfo(String coupon_id);
    public Coupon selectCouponByCode(String code);
    
}
