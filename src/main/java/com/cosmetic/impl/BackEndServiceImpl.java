package com.cosmetic.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosmetic.dao.HolidayDao;
import com.cosmetic.dao.OrderInfoDao;
import com.cosmetic.dao.SendMailDao;
import com.cosmetic.dao.SubOrderDao;
import com.cosmetic.entity.Holiday;
import com.cosmetic.entity.OrderInfo;
import com.cosmetic.entity.Sendmail;
import com.cosmetic.entity.SubOrder;
import com.cosmetic.service.BackEndService;
import com.cosmetic.service.OrderInfoService;
import com.cosmetic.service.SubOrderService;

@Service
public class BackEndServiceImpl implements BackEndService{

	@Autowired
	private HolidayDao holidayDao;
	
	@Autowired
	private SendMailDao sendMailDao;

	@Override
	public List<Holiday> getHolidays() {
		return holidayDao.getHolidays();
	}

	@Override
	public int insertMail(Sendmail sendmail) {
		// TODO Auto-generated method stub
		return sendMailDao.insert(sendmail);
	}

	@Override
	public List<Sendmail> selectEmailList(int send_time, int status) {
		// TODO Auto-generated method stub
		return sendMailDao.selectEmailList(send_time, status);
	}

	@Override
	public Sendmail selectEmail(int rec_id) {
		// TODO Auto-generated method stub
		return sendMailDao.selectEmail(rec_id);
	}


	

}
