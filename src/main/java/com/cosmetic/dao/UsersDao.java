
package com.cosmetic.dao;
import org.apache.ibatis.annotations.Param;





import com.cosmetic.entity.Users;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.QueryParam;

public interface UsersDao {    
    public int updateSubscribeByUid(@Param("user_id") int user_id,@Param("subscribed") int subscribed);
    public Users selectUserInfoByID(@Param("user_id") int user_id);
    public String selectEmailByName(@Param("first_name") String first_name,@Param("last_name") String last_name,@Param("mobile_phone") String mobile_phone);
    public int updateRealNameByUid(@Param("user_id") int user_id,@Param("first_name") String first_name,@Param("last_name") String last_name);
    public int updateUserNameByUid(@Param("user_id") int user_id,@Param("user_name") String user_name);
    public int updateUserInfoByUid(@Param("user_id") int user_id,@Param("user_name") String user_name,@Param("first_name") String first_name,@Param("last_name") String last_name,@Param("zipcode") String zipcode,@Param("sex") int sex,@Param("birthday") Date birthday,@Param("mobile_phone") String mobile_phone,@Param("avatar") String avatar);
    public int updatePointByUid(@Param("user_id") int user_id,@Param("point") Long point);
    public int updateSexByUid(@Param("user_id") int user_id,@Param("sex") int sex,@Param("avatar") String avatar);
    public int updatePhoneByUid(@Param("user_id") int user_id,@Param("mobile_phone") String mobile_phone);
    public int updateTureNameByUid(@Param("user_id") int user_id,@Param("truename") String truename);
    public int updateEmail(@Param("user_id") int user_id,@Param("email") String email);
    public int updateRemindEmail(@Param("user_id") int user_id,@Param("email") String email);
    public int selectCountByUserName(@Param("user_id") int user_id,@Param("user_name") String user_name);
    public Map<String,String> selectUsersTypeByUid(@Param("user_id") int user_id);


	public void insertUsers(Users users);
	public String getRegisterPoints();
    public int updateInviteCodeByUid(@Param("user_id") int user_id,@Param("invite_code") String invite_code);
    public int selectUsersByInviteCode(@Param("invite_code") String invite_code);
    public Users selectUserInfoByInviteCode(@Param("invite_code") String invite_code);
    public int updateFirstOrderTimeByUid(@Param("user_id") int user_id);

    public String getUserName(int id);
    public Users selectUsersByID(int id);
    public Users getPasswordSalt(String email);
    public String getPassword(int id);
    public void setPasswordSalt(Users user);
    public void verifyEmail(Users user);
    public void setPasswordSaltByUid(Users user);
	public void insertUsersByEmail(@Param("email") String email, @Param("password") String password,@Param("user_name") String user_name,@Param("newSalt") String newSalt,@Param("parent_id") int parent_id);
	public int selectUserCountByUserName(@Param("user_name") String user_name);
	public Integer selectUIdByEmail(@Param("email") String email);
	public void insertUsersByEmailId( Users users);
	public int getUserIdByInvite(@Param("invitation_code") String invitation_code);
	public int selectUserCountByInvite(@Param("invitation_code") String invitation_code);	
    public int updatePayPointsByUid(@Param("user_id") int user_id,@Param("pay_points") int pay_points);
    public int updateUserTypeByUid(@Param("user_id") int user_id,@Param("user_type") int user_type,@Param("preset_type") int preset_type);
    public int updateUserPresetTypeByUid(@Param("user_id") int user_id,@Param("preset_type") int preset_type);
    public int selectPayPointsByUid(int user_id);
    public int addPayPointsByUid(int user_id);
    public int subPayPointsByUid(int user_id);
    public int selectRankPointsByUid(int user_id);
    public int addRankPointsByUid(int user_id);
    public int subRankPointsByUid(int user_id); 
    public String selectConfigByKey(String key);   
}