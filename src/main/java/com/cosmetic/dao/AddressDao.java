package com.cosmetic.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cosmetic.entity.Address;
import com.cosmetic.entity.UserAddress;

public interface AddressDao {

    public List<UserAddress> getAddressBookByType(@Param("user_id") int user_id,@Param("address_type") int address_type);
    public UserAddress getAddressBookByAddid(@Param("address_id") int address_id);
    public int insert(UserAddress userAddress);
    public int updateByPrimaryKey(UserAddress userAddress);
    public int deleteByPrimaryKey(int address_id);
    public int deleteByUid(@Param("user_id") int user_id,@Param("address_type") int address_type);
    
}
