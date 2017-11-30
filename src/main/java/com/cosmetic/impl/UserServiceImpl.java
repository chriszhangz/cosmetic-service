package com.cosmetic.impl;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosmetic.dao.AddressDao;
import com.cosmetic.dao.CouponDao;
import com.cosmetic.dao.CouponHistoryDao;
import com.cosmetic.dao.ProfileDao;
import com.cosmetic.dao.ShipZoneDao;
import com.cosmetic.dao.TemplateDao;
import com.cosmetic.dao.UsersDao;
import com.cosmetic.entity.Coupon;
import com.cosmetic.entity.CouponHistory;
import com.cosmetic.entity.Profile;
import com.cosmetic.entity.Template;
import com.cosmetic.entity.UserAddress;
import com.cosmetic.entity.UserProfile;
import com.cosmetic.entity.Users;
import com.cosmetic.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UsersDao usersDao;
	@Autowired
	private AddressDao addressDao;
	
	@Autowired
	private ProfileDao profileDao;
	
	@Autowired
	private TemplateDao templateDao;
	
	@Autowired	
	private CouponDao couponDao;
	@Autowired	
	private CouponHistoryDao couponHistoryDao;
	@Autowired
	private ShipZoneDao shipZoneDao;
	
	
	public String getUserName(int uid) {
		return usersDao.getUserName(uid);
	}

	public Users selectUsersByID(int id) {
		return usersDao.selectUsersByID(id);
	}
	
	public Users getPasswordSalt(String email) {
		return usersDao.getPasswordSalt(email);
	}
	
	public String getPassword(int id) {
		return usersDao.getPassword(id);
	}
	
	public void setPasswordSalt(Users user) {
		usersDao.setPasswordSalt(user);
	}
	
	public void setPasswordSaltByUid(Users user) {
		usersDao.setPasswordSaltByUid(user);
	}
	
	public List<UserAddress> getAddressBookByType(int user_id,int user_type) {
		return addressDao.getAddressBookByType(user_id,user_type);
	}		
	
	public UserProfile getProfileByUid(int id) {
		return profileDao.getProfileByUid(id);
	}	
	
    public int insertProfile(UserProfile profile){
    	return profileDao.insert(profile);
    }
    
    public int updateProfileByPK(UserProfile profile){
    	return profileDao.updateByPrimaryKey(profile);
    }
    
    public int deleteProfileByPK(String profile_id){
    	return profileDao.deleteByPrimaryKey(profile_id);
    }    

    public int countProfileByPK(String profile_id){
    	return profileDao.countByPrimaryKey(profile_id);    	
    }

    public int countProfileByUid(int user_id){
    	return profileDao.countByUid(user_id);    	
    }
    
    public UserAddress getAddressBookByAddId(int id){
    	return addressDao.getAddressBookByAddid(id);
    }




	public int insertAddress(UserAddress userAddress) {
    	return addressDao.insert(userAddress);
	}

	public int updateAddressByPK(UserAddress userAddress) {
		return addressDao.updateByPrimaryKey(userAddress);
	}

	public int deleteAddressByPK(int address_id) {
		return addressDao.deleteByPrimaryKey(address_id);
	}

	public int updateProfileAddressIdByPK(int address_id,String profile_id) {
		return profileDao.updateAddressIdByPK(address_id,profile_id);
	}
	

	
	public UserProfile getProfileByPid(String profile_id){
		return profileDao.selectByPrimaryKey(profile_id);

	}


	public int updateProfileByAddressId(int user_id,int address_id,String profile_id) {
		return profileDao.updateByAddressId(user_id,address_id,profile_id);
	}   
	

	
	public void insertUsersByEmail(String email, String password,String user_name,String newSalt,int parent_id) {
		// TODO Auto-generated method stub
		usersDao.insertUsersByEmail(email,password,user_name,newSalt,parent_id);
	}
	
	public int selectUserCountByUserName(String user_name) {

		return usersDao.selectUserCountByUserName(user_name);
	}
	
	public Integer selectUIdByEmail(String email) {
		// TODO Auto-generated method stub
		return usersDao.selectUIdByEmail(email);
	}



	public Template selectTemplateById(int tid) {
		return templateDao.selectTemplateById(tid);
	}





	public Users selectUserInfoByUid(int user_id){
		return usersDao.selectUserInfoByID(user_id);
	}
	
    public int updateUserNameByUid(int user_id,String user_name){
    	return usersDao.updateUserNameByUid(user_id, user_name);
    }
    
    public int updateSexByUid(int user_id,int sex,String avatar){
    	return usersDao.updateSexByUid(user_id, sex, avatar);
    }
    
    public int selectCountByUserName(int user_id,String user_name){
    	return usersDao.selectCountByUserName(user_id,user_name);
    }
    
    
 
    
    public int updatePointByUid(int user_id, Long point){
    	return usersDao.updatePointByUid(user_id, point);
    }
    public int updatePhoneByUid(int user_id,String mobile_phone){
    	return usersDao.updatePhoneByUid(user_id, mobile_phone);
    }
    public int updateTureNameByUid(int user_id,String truename){
    	return usersDao.updateTureNameByUid(user_id, truename);
    }
    public int updateEmail(int user_id,String email){
    	return usersDao.updateEmail(user_id, email);
    }

	public void insertUsersByEmail(String email, Users users) {
		// TODO Auto-generated method stub
		usersDao.insertUsers(users);
	}

	public String getRegisterPoints() {
		// TODO Auto-generated method stub
		return usersDao.getRegisterPoints();
	}


    public int updateUserInfoByUid(int user_id,String user_name,String first_name,String last_name,String zipcode,int sex,Date birthday,String phone,String avatar){
    	return usersDao.updateUserInfoByUid(user_id, user_name, first_name, last_name,zipcode , sex, birthday, phone, avatar);
    }

    public int updateRemindEmail(int userId,String email){
    	return usersDao.updateRemindEmail(userId, email);
    }
 
    public int updateInviteCodeByUid(int user_id,String invite_code){
    	return usersDao.updateInviteCodeByUid(user_id, invite_code);
    }
    public int selectUsersByInviteCode(String invite_code){
    	return usersDao.selectUsersByInviteCode(invite_code);
    }
    public Users selectUserInfoByInviteCode(String invite_code){
    	return usersDao.selectUserInfoByInviteCode(invite_code);
    }
    

    public int updateFirstOrderTimeByUid(int user_id){
    	return usersDao.updateFirstOrderTimeByUid(user_id);
    }
    
 

	@Override
	public int getUserIdByInvite(String invitation_code) {
		// TODO Auto-generated method stub
		return usersDao.getUserIdByInvite(invitation_code);
	}

	@Override
	public int selectUserCountByInvite(String invitation_code) {
		// TODO Auto-generated method stub
		return usersDao.selectUserCountByInvite(invitation_code);
	}

	@Override
	public int updatePayPointsByUid(int user_id, int pay_points) {
		// TODO Auto-generated method stub
		return usersDao.updatePayPointsByUid(user_id, pay_points);
	}

	@Override
	public int updateUserTypeByUid(int user_id, int user_type, int preset_type) {
		// TODO Auto-generated method stub
		return usersDao.updateUserTypeByUid(user_id, user_type,preset_type);
	}

	@Override
	public int deleteAddressByUid(int user_id, int address_type) {
		// TODO Auto-generated method stub
		return addressDao.deleteByUid(user_id, address_type);
	}

	@Override
	public Map<String,String> selectUsersTypeByUid(int user_id) {
		// TODO Auto-generated method stub
		return usersDao.selectUsersTypeByUid(user_id);
	}

	@Override
	public int addPayPointsByUid(int user_id) {
		// TODO Auto-generated method stub
		return usersDao.addPayPointsByUid(user_id);
	}

	@Override
	public int subPayPointsByUid(int user_id) {
		// TODO Auto-generated method stub
		return usersDao.subPayPointsByUid(user_id);
	}

	@Override
	public int selectPayPointsByUid(int user_id) {
		// TODO Auto-generated method stub
		return usersDao.selectPayPointsByUid(user_id);
	}

	@Override
	public int updateUserPresetTypeByUid(int user_id, int preset_type) {
		// TODO Auto-generated method stub
		return usersDao.updateUserPresetTypeByUid(user_id, preset_type);
	}

	@Override
	public int updateRealNameByUid(int user_id, String first_name, String last_name) {
		// TODO Auto-generated method stub
		return usersDao.updateRealNameByUid(user_id, first_name, last_name);
	}

	@Override
	public int deleteProfileByUid(int user_id) {
		// TODO Auto-generated method stub
		return profileDao.deleteByUid(user_id);
	}

	@Override
	public String selectEmailByName(String first_name, String last_name, String mobile_phone) {
		// TODO Auto-generated method stub
		return usersDao.selectEmailByName(first_name, last_name, mobile_phone);
	}

	@Override
	public Coupon selectCouponInfo(String coupon_id) {
		// TODO Auto-generated method stub
		return couponDao.selectCouponInfo(coupon_id);
	}

	@Override
	public Coupon selectCouponByCode(String code) {
		// TODO Auto-generated method stub
		return couponDao.selectCouponByCode(code);
	}

	@Override
	public void verifyEmail(Users user) {
		// TODO Auto-generated method stub
		usersDao.verifyEmail(user);
	}

	@Override
	public int updateSubscribeByUid(int user_id, int subscribed) {
		// TODO Auto-generated method stub
		return usersDao.updateSubscribeByUid(user_id, subscribed);
	}

	@Override
	public List<Integer> selectShipZoneList() {
		// TODO Auto-generated method stub
		return shipZoneDao.selectShipZoneList();
	}

	@Override
	public void insertCouponHistory(CouponHistory couponHistory) {
		couponHistoryDao.insertCouponHistory(couponHistory);
	}

	@Override
	public int selectRankPointsByUid(int user_id) {
		return usersDao.selectRankPointsByUid(user_id);
	}

	@Override
	public int addRankPointsByUid(int user_id) {
		return usersDao.addRankPointsByUid(user_id);
	}

	@Override
	public int subRankPointsByUid(int user_id) {
		return usersDao.subRankPointsByUid(user_id);
	}

	@Override
	public String selectConfigByKey(String key) {
		return usersDao.selectConfigByKey(key);
	}

	


}
