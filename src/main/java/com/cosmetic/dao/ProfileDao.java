package com.cosmetic.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Address;
import com.cosmetic.entity.Profile;
import com.cosmetic.entity.UserProfile;

public interface ProfileDao {

    public UserProfile getProfileByUid(int user_id);
    public int insert(UserProfile profile);
    public int updateByPrimaryKey(UserProfile profile);
    public int deleteByPrimaryKey(String profile_id);
    public int deleteByUid(int user_id);
    public int countByPrimaryKey(String profile_id);
    public int countByUid(int user_id);
    public int updateByAddressId(@Param("user_id") int id,@Param("address_id") int address_id,@Param("profile_id") String profile_id);  
    public int updateAddressIdByPK(@Param("address_id") int address_id,@Param("profile_id") String profile_id);        

    public UserProfile selectByPrimaryKey(String profile_id);
}
