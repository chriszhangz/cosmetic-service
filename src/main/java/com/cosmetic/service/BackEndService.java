package com.cosmetic.service;

import java.math.BigDecimal;
import java.util.List;




import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Holiday;
import com.cosmetic.entity.Sendmail;

public interface BackEndService {

    public List<Holiday> getHolidays();

    public int insertMail(Sendmail sendmail);
    public List<Sendmail> selectEmailList(int send_time,int status);
    public Sendmail selectEmail(int rec_id);
}
