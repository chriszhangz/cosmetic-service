package com.cosmetic.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Holiday;
import com.cosmetic.entity.OrderInfo;

public interface HolidayDao {

    public List<Holiday> getHolidays();
    
}
