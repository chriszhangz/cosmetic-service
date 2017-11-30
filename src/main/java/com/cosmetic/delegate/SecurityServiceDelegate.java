package com.cosmetic.delegate;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cosmetic.common.DateUtil;
import com.cosmetic.common.StringUtil;
import com.cosmetic.entity.Token;
import com.cosmetic.service.UserService;
import com.google.gson.Gson;

@Service
public class SecurityServiceDelegate {

	@Value("${USER_TOKEN_EXP}")
	private Integer USER_TOKEN_EXP;
	
	@Autowired
	private UserService userService;
	
	/**
     * 获取未登陆状态下token
     * @param tempid 临时ID
     * @param token 
     * @throws NoSuchAlgorithmException,UnsupportedEncodingException
     * @author james.wu
     */
	public Map<String, Object> getToken4Tempid(String tempid) throws Exception {
		// 暂时设置tempId生成token超时时间为1年
		long timeOut4TempId = 3600L * 24L * 365L;
		long exp = DateUtil.timeFormat(DateUtil.getNowDateTimeAllString()) + timeOut4TempId;
		Map<String, Object> result = new HashMap<String, Object>();
		int enctimes = 2;
		String data = tempid;
		String ecSalt = tempid.substring(tempid.lastIndexOf("-"), tempid.length() - 1);
		String auth = StringUtil.EncoderByMd5(ecSalt, tempid, enctimes);
		Token tokenNew = new Token();
		tokenNew.setExp(exp);
		tokenNew.setData(data);
		tokenNew.setAuth(auth);
		Gson gson = new Gson();
		result.put("token", StringUtil.encode(gson.toJson(tokenNew)));

		return result;

	}
	/**
     * 检查token
     * @param token 
     * @throws NoSuchAlgorithmException,UnsupportedEncodingException
     * @author james.wu
     */
	public Boolean checkToken(String token)throws Exception{
		
		Boolean result = false;
		Gson gson = new Gson();  
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		
		//验证token是否过期
		//token未过期处理
		if(tokenIn.getExp()>DateUtil.timeFormat(DateUtil.getNowDateTimeAllString())){
			//未登陆token验证
			if(tokenIn.getIsLogin()==0){
				String ecSalt = tokenIn.getData().substring(tokenIn.getData().lastIndexOf("-"),tokenIn.getData().length()-1);
				 String	auth =  StringUtil.EncoderByMd5(ecSalt,tokenIn.getData(),2);
				 if(tokenIn.getAuth().equals(auth)){
					 result = true; 
				 }else{
					 result = false; 
				 }
			}
			//登陆token验证
			else if(tokenIn.getIsLogin()==1){
				
			 }
		}
		//token过期处理，生成新token	
		else{
			result = false;
		}
		
		return result;  
   
 
	}
	
	/**
	 * getToken
	 * 
	 * @param uid
	 * @param auth
	 * @throws Exception
	 * @author chris
	 */
	public String getToken(int uid, String salt, String auth) throws Exception {

		int timeout = 3600*USER_TOKEN_EXP;
		long exp = DateUtil.timeFormat(DateUtil.getNowDateTimeAllString()) + timeout;
		String data = null;

		data = String.valueOf(uid);

		Token tokenNew = new Token();
		tokenNew.setExp(exp);
		tokenNew.setData(data);
		tokenNew.setAuth(auth);
		tokenNew.setSalt(salt);
		tokenNew.setIsLogin(1);
		Gson gson = new Gson();
		String result = StringUtil.encode(gson.toJson(tokenNew));
		return result;

	}
	/**
     * getTempId
     * @param N/A
     * @author chris
     */
	public String getTempId() {

		return UUID.randomUUID().toString();

	}
	/**
     * checkVersion
     * @param token String
     * @param version String
     * @author chris
     */

	
	

}
