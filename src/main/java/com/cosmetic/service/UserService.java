package com.cosmetic.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;





import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Coupon;
import com.cosmetic.entity.CouponHistory;
import com.cosmetic.entity.Profile;
import com.cosmetic.entity.Template;
import com.cosmetic.entity.UserAddress;
import com.cosmetic.entity.UserProfile;
import com.cosmetic.entity.Users;

public interface UserService {
    public List<Integer> selectShipZoneList();
    public int updateSubscribeByUid(int user_id,int subscribed);
    public List<UserAddress> getAddressBookByType(int user_id,int address_type);   
    public String selectEmailByName(String first_name,String last_name,String mobile_phone);
    public UserAddress getAddressBookByAddId(int id);  
    public int insertAddress(UserAddress userAddress);
    
    public int updateAddressByPK(UserAddress userAddress);
    
    public int deleteAddressByPK(int address_id);
    public int deleteAddressByUid(int user_id,int address_type);
    
    public int updatePayPointsByUid(int user_id,int pay_points);
    public int updateUserTypeByUid(int user_id,int user_type,int preset_type);
    public int updateUserPresetTypeByUid(int user_id,int preset_type);
    public Map<String,String> selectUsersTypeByUid(int user_id);
    
    
    
    
	public int selectUserCountByInvite(String invitation_code);
	public int getUserIdByInvite(String invitation_code);
	
	public String getUserName(int uid);
	
    public Users selectUsersByID(int id);
	
    public Users getPasswordSalt(String email);

    public String getPassword(int id);
    
    public void setPasswordSalt(Users user);

    public void verifyEmail(Users user);
    public void setPasswordSaltByUid(Users user);
 

    public UserProfile getProfileByUid(int id);
    
    public int insertProfile(UserProfile profile);
    
    public int updateProfileByPK(UserProfile profile);
    
    public int deleteProfileByPK(String profile_id); 
    
    public int deleteProfileByUid(int user_id);    

    public int countProfileByPK(String profile_id);    
    
    public int countProfileByUid(int user_id);

    public int updateProfileByAddressId(int user_id,int address_id,String profile_id);  
    
   

    


    public int updateProfileAddressIdByPK(int address_id,String profile_id);  
    

    
    public UserProfile getProfileByPid(String profile_id);
    




	public void insertUsersByEmail(String email, String password,String user_name,String newSalt,int parent_id);

	public int selectUserCountByUserName(String user_name);

	public Integer selectUIdByEmail(String email);


	public Template selectTemplateById(int tid);



	public Users selectUserInfoByUid(int user_id);

    public int updateRealNameByUid(int user_id,String first_name,String last_name);
    public int updateUserNameByUid(int user_id,String user_name);
    
    public int updateSexByUid(int user_id,int sex,String avatar);
    
    public int selectCountByUserName(int user_id,String user_name);
    

    
    public int updatePointByUid(int user_id, Long point);
    
    public int updatePhoneByUid(int user_id,String mobile_phone);
    public int updateTureNameByUid(int user_id,String truename);
    public int updateEmail(int user_id,String email);

	public void insertUsersByEmail(String email, Users users);


	
    public int updateUserInfoByUid(int user_id,String user_name,String first_name,String last_name,String zipcode,int sex,Date birthday,String phone,String avatar);
    

    
	
    public int updateInviteCodeByUid(int user_id,String invite_code);
    public int selectUsersByInviteCode(String invite_code);
    
    public Users selectUserInfoByInviteCode(String invite_code);
    public int selectPayPointsByUid(int user_id);
    public int addPayPointsByUid(int user_id);
    public int subPayPointsByUid(int user_id);

    public Coupon selectCouponInfo(String coupon_id);
    public Coupon selectCouponByCode(String code);
    public void insertCouponHistory(CouponHistory couponHistory);
    public int selectRankPointsByUid(int user_id);
    public int addRankPointsByUid(int user_id);
    public int subRankPointsByUid(int user_id);    

    public String selectConfigByKey(String key);  
}
