package com.cosmetic.rest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.cosmetic.common.NeedLogin;
import com.cosmetic.delegate.SecurityServiceDelegate;
import com.cosmetic.delegate.TransactionDelegate;
import com.cosmetic.delegate.UserServiceDelegate;
import com.cosmetic.delegate.UspsServiceDelegate;
import com.cosmetic.entity.Token;
import com.cosmetic.entity.Users;
import com.google.gson.Gson;

@Service
@Path("/users")
public class RestUserService {
	@Autowired
	private UserServiceDelegate userServiceDelegate;
	@Autowired
	private SecurityServiceDelegate securityServiceDelegate;
	
	@Autowired
	private TransactionDelegate transactionDelegate;

	@Autowired
	private UspsServiceDelegate uspsServiceDelegate;
	@Autowired 
	private RedisTemplate<String,String> redisTemplate;
	

	
	@GET
	@Path("/getUserById")
	@Produces("application/json;charset=utf-8")
	public Response getUserById(@QueryParam("uid") int uid) {

		Users result = userServiceDelegate.selectUsersByID(uid);

		return Response.status(Status.OK).entity(result).build();

	}


	
	@GET
	@NeedLogin
	@Path("/editUserName")
	@Produces("application/json;charset=utf-8")
	public Response editUserName(@QueryParam("token") String token,@QueryParam("username") String username)throws Exception {

		Map<String,Object> result = transactionDelegate.transactionEditUserName(token,username);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/editRealName")
	@Produces("application/json;charset=utf-8")
	public Response editRealName(@QueryParam("token") String token,@QueryParam("first_name") String first_name,@QueryParam("last_name") String last_name)throws Exception {

		Map<String,Object> result = transactionDelegate.transactionEditRealName(token,first_name,last_name);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/editUserGender")
	@Produces("application/json;charset=utf-8")
	public Response editUserGender(@QueryParam("token") String token,@QueryParam("gender") int gender)throws Exception {

		Map<String,Object> result = transactionDelegate.transactionEditSex(token,gender);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/editPhone")
	@Produces("application/json;charset=utf-8")
	public Response editPhone(@QueryParam("token") String token,@QueryParam("mobile_phone") String mobile_phone,@QueryParam("pwd") String pwd)throws Exception {

		Map<String,Object> result = transactionDelegate.transactionEditPhone(token,mobile_phone,pwd);

		return Response.status(Status.OK).entity(result).build();

	}
	

	@GET
	@NeedLogin
	@Path("/editAccountInfo")
	@Produces("application/json;charset=utf-8")
	public Response editAccountInfo(@QueryParam("token") String token,@QueryParam("user_name") String user_name,@QueryParam("first_name") String first_name,@QueryParam("last_name") String last_name,@QueryParam("gender") int gender,@QueryParam("birthday") String birthday,@QueryParam("mobile_phone") String mobile_phone)throws Exception {


		Map<String,Object> result = transactionDelegate.transactionUsersInfo(token, user_name, first_name, last_name, gender, birthday, mobile_phone);

		return Response.status(Status.OK).entity(result).build();

	}

	
	@GET
	@Path("/getUserNameById")
	@Produces("application/json;charset=utf-8")
	public Response getUserNameById(@QueryParam("token") String token,@QueryParam("uid") int uid) throws Exception {
		
		String result = userServiceDelegate.getUserName(uid);
		//KafkaProducer producer = new KafkaProducer();
		Map<String,String> message = new HashMap<String,String>();
		message.put("info", "user name:"+result);
		//producer.sendold("mytopic", message);
		return Response.status(Status.OK).entity(result).build();

	}

	@GET
	@Path("/getUserType")
	@Produces("application/json;charset=utf-8")
	public Response getUserType(@QueryParam("token") String token) throws Exception {
		
		Map<String,Object> result = userServiceDelegate.getUserType(token);
		//KafkaProducer producer = new KafkaProducer();
		//producer.sendold("mytopic", message);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getPaymentHistory")
	@Produces("application/json;charset=utf-8")
	public Response getPaymentHistory(@QueryParam("token") String token) throws Exception {
		
		Map<String,Object> result = userServiceDelegate.getPaymentHistory(token);
		//KafkaProducer producer = new KafkaProducer();
		//producer.sendold("mytopic", message);
		return Response.status(Status.OK).entity(result).build();

	}
/*	@GET
	@Path("/subscribe")
	@Produces("application/json;charset=utf-8")
	public Response subscribe(@QueryParam("token") String token,@QueryParam("preset_type") String preset_type,@QueryParam("profile_id") String profile_id
			,@QueryParam("cvv") String cvv,@QueryParam("promo_code") String promo_code) throws Exception {
		
		Map<String, Object> result = userServiceDelegate.subscribe(token, preset_type, profile_id,cvv,promo_code);
		return Response.status(Status.OK).entity(result).build();

	}*/
	
	@GET
	@Path("/subscribe")
	@Produces("application/json;charset=utf-8")
	public Response subscribe(@QueryParam("token") String token,@QueryParam("preset_type") String preset_type,@QueryParam("profile_id") String profile_id
			,@QueryParam("cvv") String cvv,@QueryParam("promo_code") String promo_code) throws Exception {
		
		Map<String, Object> result = userServiceDelegate.newSubscribe(token, preset_type, profile_id,cvv,promo_code);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/subscribeV2")
	@Produces("application/json;charset=utf-8")
	public Response subscribe(@QueryParam("token") String token,@QueryParam("preset_type") String preset_type,@QueryParam("first_name") String first_name
			,@QueryParam("last_name") String last_name,@QueryParam("address1") String address1,@QueryParam("address2") String address2
			,@QueryParam("city") String city,@QueryParam("state") String state,@QueryParam("zipcode") String zipcode
			,@QueryParam("phone") String phone,@QueryParam("name_of_card") String name_of_card,@QueryParam("account") String account
			,@QueryParam("exp_year") String exp_year,@QueryParam("exp_month") String exp_month,@QueryParam("billing_address1") String billing_address1
			,@QueryParam("billing_address2") String billing_address2,@QueryParam("billing_city") String billing_city,@QueryParam("billing_zip") String billing_zip
			,@QueryParam("billing_state") String billing_state,@QueryParam("billing_phone") String billing_phone
			,@QueryParam("cvv") String cvv,@QueryParam("promo_code") String promo_code) throws Exception {
		
		Map<String, Object> result = userServiceDelegate.newSubscribeV2(token, preset_type, promo_code, first_name, last_name,
				address1, address2, city, state, zipcode, phone, name_of_card, account, exp_year, exp_month, cvv,
				billing_address1, billing_address2, billing_city, billing_zip, billing_state, billing_phone);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getSubscription")
	@Produces("application/json;charset=utf-8")
	public Response getSubscription(@QueryParam("token") String token) throws Exception {
		
		Map<String, Object> result = userServiceDelegate.getSubscription(token);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/login")
	@Produces("application/json;charset=utf-8")
	public Response loginUser(@QueryParam("token") String token,@QueryParam("email") String email,@QueryParam("pwd") String pwd) throws Exception {
		
		Map<String, Object> result = userServiceDelegate.loginUser(token, email, pwd);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/getUserInfo")
	@Produces("application/json;charset=utf-8")
	public Response getUserInfo(@QueryParam("token") String token) {

		Map<String,Object> result = userServiceDelegate.getUserInfo(token);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/register_sendEmail")
	@Produces("application/json;charset=utf-8")
	public Response registerUser_sendEmail(@QueryParam("token") String token, @QueryParam("email") String email,
			@QueryParam("pwd") String pwd, @QueryParam("userName") String userName, @QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @QueryParam("zipcode") String zipcode, @QueryParam("sex") String sex, @QueryParam("birthday") String birthday, @QueryParam("subscribed") String subscribed, @QueryParam("phone") String phone) throws Exception {
		
		Map<String, Object> result = userServiceDelegate.registerUser_sendEmail(token, email, pwd, userName, firstName, lastName, zipcode,sex,birthday,subscribed,phone);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/register_sendEmailV2")
	@Produces("application/json;charset=utf-8")
	public Response register_sendEmailV2(@QueryParam("token") String token, @QueryParam("email") String email,
			@QueryParam("pwd") String pwd, @QueryParam("birthday") String birthday) throws Exception {
		
		Map<String, Object> result = userServiceDelegate.registerUser_sendEmailV2(token, email, pwd, birthday);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/register_resendEmail")
	@Produces("application/json;charset=utf-8")
	public Response register_resendEmail(@QueryParam("token") String token) throws Exception {
		
		Map<String, Object> result = userServiceDelegate.registerUser_resendEmail(token);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/register_verifyEmail")
	@Produces("application/json;charset=utf-8")
	public Response registerUser_verifyEmail(@QueryParam("token") String token, 
			@QueryParam("pwd") String pwd) throws Exception {
		
		Map<String, Object> result = userServiceDelegate.registerUser_verifyEmail(token,  pwd);
		return Response.status(Status.OK).entity(result).build();

	}
	

	@GET
	@NeedLogin
	@Path("/logout")
	@Produces("application/json;charset=utf-8")
	public Response logoutUser(@QueryParam("token") String token) throws Exception {

		Map<String, Object> result = userServiceDelegate.logoutUser(token);
		
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/forgetPassword")
	@Produces("application/json;charset=utf-8")
	public Response forgetPassword(@QueryParam("token") String token, @QueryParam("email") String email) throws Exception {

		Map<String, Object> result = userServiceDelegate.forgetPassword(token,email);
		
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/forgetEmail")
	@Produces("application/json;charset=utf-8")
	public Response forgetEmail(@QueryParam("token") String token, @QueryParam("first_name") String first_name, @QueryParam("last_name") String last_name, @QueryParam("mobile_phone") String mobile_phone) throws Exception {

		Map<String, Object> result = userServiceDelegate.forgetEmail(token, first_name, last_name, mobile_phone);
		
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/resetPassword")
	@Produces("application/json;charset=utf-8")
	public Response resetPassword(@QueryParam("token") String token, @QueryParam("password") String password, @QueryParam("code") String code) throws Exception {

		Map<String, Object> result = userServiceDelegate.resetPassword(token,password,code);
		
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/changePassword")
	@Produces("application/json;charset=utf-8")
	public Response changePassword(@QueryParam("token") String token, @QueryParam("old_password") String old_password, @QueryParam("new_password") String new_password) throws Exception {

		Map<String, Object> result = userServiceDelegate.changePassword(token, old_password, new_password);
		
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/getAddressBook")
	@Produces("application/json;charset=utf-8")
	public Response getAddressBook(@QueryParam("token") String token,@QueryParam("address_type") String address_type)throws Exception {

		Map<String,Object> result = userServiceDelegate.getAddressBook(token,address_type);

		return Response.status(Status.OK).entity(result).build();

	}	
	
	@GET
	@NeedLogin
	@Path("/newAddress")
	@Produces("application/json;charset=utf-8")
	public Response newAddress(@QueryParam("token") String token, @QueryParam("address_type") String address_type,
			@QueryParam("first_name") String first_name, @QueryParam("last_name") String last_name,
			@QueryParam("address1") String address1, @QueryParam("address2") String address2,
			@QueryParam("city") String city, @QueryParam("state") String state,
			@QueryParam("zipcode") String zipcode, @QueryParam("phone") String phone)
			throws Exception {

		Map<String, Object> result = userServiceDelegate.newAddress(token, address_type,first_name, last_name,
				address1, address2, city, state, zipcode, phone);

		return Response.status(Status.OK).entity(result).build();

	}

	@GET
	@NeedLogin
	@Path("/editAddress")
	@Produces("application/json;charset=utf-8")
	public Response editAddress(@QueryParam("token") String token,
			@QueryParam("address_id") String address_id,
			@QueryParam("first_name") String first_name, @QueryParam("last_name") String last_name,
			@QueryParam("address1") String address1, @QueryParam("address2") String address2,
			@QueryParam("city") String city, @QueryParam("state") String state,
			@QueryParam("zipcode") String zipcode, @QueryParam("phone") String phone,@QueryParam("profile_id") String profile_id)
			throws Exception {

		Map<String, Object> result = userServiceDelegate.editAddress(token, address_id, first_name,
				last_name, address1, address2, city, state, zipcode, phone,profile_id);

		return Response.status(Status.OK).entity(result).build();

	}

	@GET
	@NeedLogin
	@Path("/deleteAddress")
	@Produces("application/json;charset=utf-8")
	public Response deleteAddress(@QueryParam("token") String token,
			@QueryParam("address_id") Integer address_id)
			throws Exception {

		Map<String, Object> result = userServiceDelegate.deleteAddress(token, address_id);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/getPayment")
	@Produces("application/json;charset=utf-8")
	public Response getPayment(@QueryParam("token") String token)throws Exception {

		Map<String,Object> result = userServiceDelegate.getPayment(token);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/newPayment")
	@Produces("application/json;charset=utf-8")
	public Response newPayment(@QueryParam("token") String token,
			@QueryParam("name_of_card") String name_of_card,
			@QueryParam("account") String account,
			@QueryParam("cvv") String cvv,
			@QueryParam("exp_year") String exp_year, @QueryParam("exp_month") String exp_month,
			@QueryParam("address_line1") String address_line1, @QueryParam("address_line2") String address_line2,
			@QueryParam("address_city") String address_city, @QueryParam("address_zip") String address_zip,
			@QueryParam("address_state") String address_state, @QueryParam("address_country") String address_country,
			@QueryParam("phone") String phone)
			throws Exception {

		Map<String, Object> result = userServiceDelegate.newPayment(token, name_of_card,
				account,  exp_year, exp_month, cvv, address_line1,address_line2,address_city,address_zip,address_state,address_country,phone);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/editPayment")
	@Produces("application/json;charset=utf-8")
	public Response editPayment(@QueryParam("token") String token,
			@QueryParam("profile_id") String profile_id,
			@QueryParam("name_of_card") String name_of_card,
			@QueryParam("account") String account,
			@QueryParam("cvv") String cvv,
			@QueryParam("exp_year") String exp_year, @QueryParam("exp_month") String exp_month,
			@QueryParam("address_line1") String address_line1, @QueryParam("address_line2") String address_line2,
			@QueryParam("address_city") String address_city, @QueryParam("address_zip") String address_zip,
			@QueryParam("address_state") String address_state, @QueryParam("address_country") String address_country,
			@QueryParam("phone") String phone)
			throws Exception {

		Map<String, Object> result = userServiceDelegate.editPayment(token, profile_id, name_of_card,account,  exp_year, exp_month, cvv, address_line1,address_line2,address_city,address_zip,address_state,address_country,phone);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/editPaymentAddress")
	@Produces("application/json;charset=utf-8")
	public Response editPaymentAddress(@QueryParam("token") String token,
			@QueryParam("profile_id") String profile_id,
			@QueryParam("address_id") Integer address_id)
			throws Exception {

		Map<String, Object> result = userServiceDelegate.editPaymentAddress(token, profile_id, address_id);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/deletePayment")
	@Produces("application/json;charset=utf-8")
	public Response deletePayment(@QueryParam("token") String token, @QueryParam("profile_id") String profile_id) throws Exception {

		Map<String, Object> result = userServiceDelegate.deletePayment(token,profile_id);

		return Response.status(Status.OK).entity(result).build();

	}
	


	@GET
	@Path("/checkPromoCode")
	@Produces("application/json;charset=utf-8")
	public Response checkPromoCode(@QueryParam("token") String token,@QueryParam("promo_code") String promo_code)throws Exception {

		Map<String,Object> result = userServiceDelegate.checkPromoCode(token,promo_code);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getPayPoints")
	@Produces("application/json;charset=utf-8")
	public Response getPayPoints(@QueryParam("token") String token)throws Exception {

		Map<String,Object> result = userServiceDelegate.getPayPoints(token);

		return Response.status(Status.OK).entity(result).build();

	}


	
/*	@GET
	@NeedLogin
	@Path("/getOrderTracking")
	@Produces("application/json;charset=utf-8")
	public Response getOrderTracking(@QueryParam("token") String token,@QueryParam("order_id") int order_id) throws Exception {

		Map<String, Object> result = orderServiceDelegate.getOrderTracking(token,order_id);

		return Response.status(Status.OK).entity(result).build();

	}*/
	
	@GET
	@Path("/buyShoe")
	@Produces("application/json;charset=utf-8")
	public Response buyShoe(@QueryParam("token") String token,@QueryParam("product_sku") String product_sku,@QueryParam("profile_id") String profile_id,@QueryParam("use_card") String use_card)throws Exception {

		Map<String,Object> result = userServiceDelegate.buyShoe(token, product_sku, profile_id,  use_card);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/changePlan")
	@Produces("application/json;charset=utf-8")
	public Response changePlan(@QueryParam("token") String token,@QueryParam("preset_type") String preset_type,@QueryParam("sub_order_id") String sub_order_id)throws Exception {

		Map<String,Object> result = userServiceDelegate.changePlan(token, preset_type, sub_order_id);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/unSubscribe")
	@Produces("application/json;charset=utf-8")
	public Response unSubscribe(@QueryParam("token") String token,@QueryParam("unsub_type") String unsub_type,@QueryParam("unsub_msg") String unsub_msg,@QueryParam("sub_order_id") String sub_order_id)throws Exception {

		Map<String,Object> result = userServiceDelegate.unSubscribe(token,unsub_type,unsub_msg,sub_order_id);

		return Response.status(Status.OK).entity(result).build();

	}
	
/*	@GET
	@Path("/backEndOrder")
	@Produces("application/json;charset=utf-8")
	public Response backEndOrder()throws Exception {

		Map<String,Object> result = transactionDelegate.transactionBackEndOrder();

		return Response.status(Status.OK).entity(result).build();

	}*/	
	
	@GET
	@Path("/makeDonateLink")
	@Produces("application/json;charset=utf-8")
	public Response makeDonateLink(@QueryParam("token") String token,@QueryParam("user_id") Integer user_id,@QueryParam("product_sku") String product_sku,@QueryParam("shurack_id") Integer shurack_id)throws Exception {

		Map<String,Object> result = userServiceDelegate.makeDonateLink(token,user_id,product_sku,shurack_id);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/donateShoe")
	@Produces("application/json;charset=utf-8")
	public Response donateShoe(@QueryParam("token") String token,@QueryParam("code") String code)throws Exception {

		Map<String,Object> result = userServiceDelegate.donateShoe(token,code);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/subscribeEmail")
	@Produces("application/json;charset=utf-8")
	public Response subscribeEmail(@QueryParam("token") String token)throws Exception {

		Map<String,Object> result = userServiceDelegate.subscribeEmail(token);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/unSubscribeEmail")
	@Produces("application/json;charset=utf-8")
	public Response unSubscribeEmail(@QueryParam("token") String token)throws Exception {

		Map<String,Object> result = userServiceDelegate.unSubscribeEmail(token);

		return Response.status(Status.OK).entity(result).build();

	}
	
}
