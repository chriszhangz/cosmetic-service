package com.cosmetic.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Coupon;
import com.cosmetic.entity.Sendmail;


public interface SendMailDao {

    public int insert(Sendmail sendmail);
    public List<Sendmail> selectEmailList(@Param("send_time") int send_time,@Param("status") int status);
    public Sendmail selectEmail(int rec_id);
    
}
