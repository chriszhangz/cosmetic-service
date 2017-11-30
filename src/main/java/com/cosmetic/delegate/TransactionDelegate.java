package com.cosmetic.delegate;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.QueryParam;

import net.paymentech.ws.ProfileAddElement;
import net.paymentech.ws.ProfileChangeElement;
import net.paymentech.ws.ProfileDeleteElement;
import net.paymentech.ws.ProfileResponseElement;
import net.paymentech.ws.PaymentechGateway.wsdl.PaymentechGateway_wsdl.PaymentechGatewayLocator;
import net.paymentech.ws.PaymentechGateway.wsdl.PaymentechGateway_wsdl.PaymentechGatewayPortType;

import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.cosmetic.common.CardType;
import com.cosmetic.common.DateUtil;
import com.cosmetic.common.ErrorCodeEnum;
import com.cosmetic.common.StringUtil;
import com.cosmetic.common.YamiConstant;
import com.cosmetic.common.YamiException;
import com.cosmetic.entity.Coupon;
import com.cosmetic.entity.CouponHistory;
import com.cosmetic.entity.OrderHistory;
import com.cosmetic.entity.OrderInfo;
import com.cosmetic.entity.OrderPaypal;
import com.cosmetic.entity.Shurack;
import com.cosmetic.entity.SubOrder;
import com.cosmetic.entity.Token;
import com.cosmetic.entity.UnSubOrder;
import com.cosmetic.entity.UserAddress;
import com.cosmetic.entity.UserProfile;
import com.cosmetic.entity.Users;
import com.cosmetic.entity.VerifyToken;
import com.cosmetic.service.OrderInfoService;
import com.cosmetic.service.PaymentService;
import com.cosmetic.service.ProductService;
import com.cosmetic.service.ShurackService;
import com.cosmetic.service.SubOrderService;
import com.cosmetic.service.UnSubOrderService;
import com.cosmetic.service.UserService;
import com.google.gson.Gson;
import com.paypal.base.rest.JSONFormatter;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.RateLimitException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;

@Service
public class TransactionDelegate {

	@Autowired
	private UserService userService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private ProductService productService;
	@Autowired
	private SubOrderService subOrderService;
	@Autowired
	private ShurackService shurackService;
	@Autowired
	private UnSubOrderService unSubOrderService;
	@Autowired
	private OrderInfoService orderInfoService;
	@Autowired
	private SecurityServiceDelegate securityServiceDelegate;
	@Autowired
	private PaymentServiceDelegate paymentServiceDelegate;
	@Autowired
	private TemplateEngine templateEngine;
	@Autowired
	private BackEndServiceDelegate backEndServiceDelegate;
	// @Autowired
	// private LogUtil logger;
	private Logger logger = LogManager.getLogger(this.getClass().getName());

	@Value("${STRIPE_API_KEY}")
	private String STRIPE_API_KEY;
	@Value("${TOPIC_SENDMAIL}")
	private String TOPIC_SENDMAIL;
	@Value("${OGWS_CONNECTION_USERNAME}")
	private String OGWS_CONNECTION_USERNAME;
	@Value("${OGWS_CONNECTION_PASSWORD}")
	private String OGWS_CONNECTION_PASSWORD;
	@Value("${OGWS_MERCHANT_ID}")
	private String OGWS_MERCHANT_ID;
	@Value("${OGWS_URL}")
	private String OGWS_URL;
	@Value("${OGWS_VERSION}")
	private String OGWS_VERSION;
	@Value("${OGWS_TERMINALID}")
	private String OGWS_TERMINALID;
	@Value("${OGWS_BIN_SALEM}")
	private String OGWS_BIN_SALEM;
	@Value("${OGWS_BIN_PNS}")
	private String OGWS_BIN_PNS;
	@Value("${OGWS_TRANSTYPE_AC}")
	private String OGWS_TRANSTYPE_AC;
	@Value("${OGWS_INDUSTRYTYPE_EC}")
	private String OGWS_INDUSTRYTYPE_EC;
	@Value("${OGWS_CUSTOMER_PROFILE_ORDER_OVERIDE_NO}")
	private String OGWS_CUSTOMER_PROFILE_ORDER_OVERIDE_NO;
	@Value("${OGWS_CUSTOMER_PROFILE_FROM_ORDER_S}")
	private String OGWS_CUSTOMER_PROFILE_FROM_ORDER_S;
	@Value("${OGWS_CUSTOMER_ACCOUNT_TYPE_CC}")
	private String OGWS_CUSTOMER_ACCOUNT_TYPE_CC;
	@Value("${USER_VERIFY_LINK}")
	private String USER_VERIFY_LINK;	

	public void transactionLogin(Users user, Token tokenIn) throws Exception {

		// update new password(MD5) and salt to DB.
		userService.setPasswordSalt(user);
		// TODO update the userId of cart and clear tempId.
		/*
		 * if (null != tokenIn) { String tempId = tokenIn.getData();
		 * cartRedisServiceDelegate
		 * .updateUserIdByTempid(tempId,user.getUserId()); }
		 */
	}

	public void transactionResetPassword(Users user) throws Exception {

		// update new password(MD5) and ecSalt to DB.
		userService.setPasswordSaltByUid(user);

	}

	public void transactionNewAddress(UserAddress userAddress) throws Exception {

		// insert new UserAddress to DB.
		userService.insertAddress(userAddress);
	}

	public void transactionEditAddress(UserAddress userAddress, String profile_id) throws Exception {

		// update UserAddress to DB.
		userService.updateAddressByPK(userAddress);
		// clear address_id of the profile when edit the address.
		userService.updateProfileByAddressId(userAddress.getUser_id(), userAddress.getAddress_id(),
				profile_id);
	}

	public void transactionDeleteAddress(int address_id, int uid, int is_primary, String profile_id)
			throws Exception {

		// delete UserAddress from DB.
		userService.deleteAddressByPK(address_id);

		// clear address_id of the profile when edit the address.
		userService.updateProfileByAddressId(uid, address_id, profile_id);
	}
	
	public void transactionInsertOrderHistory(OrderHistory orderHistory) throws Exception {

		// insert new OrderHistory to DB.
		paymentService.insertOrderHistory(orderHistory);
	}


	public void transactionAddOrderPaypal(OrderPaypal orderPaypal) throws Exception {

		// Update order_paypal.
		paymentService.insertOrderPaypal(orderPaypal);
	}

	public String transactionNewPayment(UserProfile profile, String account, boolean addBlacklist)
			throws Exception {
		Boolean isUpdate = false;
		// Insert new profile to DB.
		UserProfile ex = userService.getProfileByUid(profile.getUser_id());
		if(ex!=null){
			isUpdate = true;
			userService.deleteProfileByUid(profile.getUser_id());
		}
		//userService.insertProfile(profile);
		
		Stripe.apiKey = STRIPE_API_KEY;

		Map<String, Object> cardParams = new HashMap<String, Object>();
		Map<String, Object> customerParams = new HashMap<String, Object>();
		customerParams.put("description", profile.getUser_id());
		cardParams.put("number", profile.getNumber());
		cardParams.put("exp_month", profile.getExp_month());
		cardParams.put("exp_year", profile.getExp_year());
		cardParams.put("cvc", profile.getCvc());
		cardParams.put("name", profile.getName_of_card());
		cardParams.put("address_line1", profile.getAddress_line1());
		if(profile.getAddress_line2()!=null&&!"".equals(profile.getAddress_line2().trim())){
			cardParams.put("address_line2", profile.getAddress_line2());			
		}
		cardParams.put("address_city", profile.getAddress_city());
		cardParams.put("address_zip", profile.getAddress_zip());
		cardParams.put("address_state", profile.getAddress_state());
		cardParams.put("address_country", profile.getAddress_country());
		//cardParams.put("phone", profile.getPhone());
		customerParams.put("card", cardParams);

		try {
			if(isUpdate){
				Customer customer = Customer.retrieve(ex.getProfile_id());
				customer.update(customerParams);
				logger.info("Update Customer:"+JSONFormatter.toJSON(customer));
				profile.setProfile_id(customer.getId());
			}else{
				Customer customer = Customer.create(customerParams);
				logger.info("Create Customer:"+JSONFormatter.toJSON(customer));
				profile.setProfile_id(customer.getId());
			}
			} catch (CardException e) {
			  // Since it's a decline, CardException will be caught
			  logger.error("Status is: " + e.getCode());
			  logger.error("Message is: " + e.getMessage());
			  throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (RateLimitException e) {
			  // Too many requests made to the API too quickly
				logger.error("Too many requests made to the API too quickly. Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (InvalidRequestException e) {
			  // Invalid parameters were supplied to Stripe's API
				logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (AuthenticationException e) {
			  // Authentication with Stripe's API failed
			  // (maybe you changed API keys recently)
				logger.error("Authentication with Stripe's API failed. Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (APIConnectionException e) {
			  // Network communication with Stripe failed
				logger.error("Network communication with Stripe failed. Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (StripeException e) {
			  // Display a very generic error to the user, and maybe send
			  // yourself an email
				logger.error("Stripe Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (Exception e) {
			  // Something else happened, completely unrelated to Stripe
				logger.error("Completely unrelated to Stripe. Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			}
		userService.insertProfile(profile);
		return profile.getProfile_id();
	}

	public void transactionEditPayment(UserProfile profile) throws Exception {
		// Insert new profile to DB.
		userService.updateProfileByPK(profile);
		Stripe.apiKey = STRIPE_API_KEY;

		Map<String, Object> cardParams = new HashMap<String, Object>();
		Map<String, Object> customerParams = new HashMap<String, Object>();
		if(!profile.getNumber().contains("*")){
			cardParams.put("number", profile.getNumber());			
		}
		//customerParams.put("description", "Customer for test.com");
		cardParams.put("exp_month", profile.getExp_month());
		cardParams.put("exp_year", profile.getExp_year());
		cardParams.put("cvc", profile.getCvc());
		cardParams.put("name", profile.getName_of_card());
		cardParams.put("address_line1", profile.getAddress_line1());
		cardParams.put("address_line2", profile.getAddress_line2());
		cardParams.put("address_city", profile.getAddress_city());
		cardParams.put("address_zip", profile.getAddress_zip());
		cardParams.put("address_state", profile.getAddress_state());
		cardParams.put("address_country", profile.getAddress_country());
		//cardParams.put("phone", profile.getPhone());
		customerParams.put("card", cardParams);

		try {
				Customer customer = Customer.retrieve(profile.getProfile_id());
				customer.update(customerParams);
				logger.info("Customer:"+JSONFormatter.toJSON(customer));
				
		} catch (CardException e) {
			  // Since it's a decline, CardException will be caught
			  logger.error("Status is: " + e.getCode());
			  logger.error("Message is: " + e.getMessage());
			  throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (RateLimitException e) {
			  // Too many requests made to the API too quickly
				logger.error("Too many requests made to the API too quickly. Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (InvalidRequestException e) {
			  // Invalid parameters were supplied to Stripe's API
				logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (AuthenticationException e) {
			  // Authentication with Stripe's API failed
			  // (maybe you changed API keys recently)
				logger.error("Authentication with Stripe's API failed. Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (APIConnectionException e) {
			  // Network communication with Stripe failed
				logger.error("Network communication with Stripe failed. Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (StripeException e) {
			  // Display a very generic error to the user, and maybe send
			  // yourself an email
				logger.error("Stripe Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			} catch (Exception e) {
			  // Something else happened, completely unrelated to Stripe
				logger.error("Completely unrelated to Stripe. Error Message is: " + e.getMessage());
				throw new YamiException(YamiConstant.ERRORCODE_ER1311, ErrorCodeEnum.ER1311.getMsg());
			}
	}

	public void transactionEditPaymentAddress(int address_id, String profile_id) throws Exception {

		// Update AddressId.
		userService.updateProfileAddressIdByPK(address_id, profile_id);

	}

	public void transactionDeletePayment(String profile_id, int uid) throws Exception {

		// Insert new profile to DB.
		userService.deleteProfileByPK(profile_id);
		// If the profile which will be deleted is a primary profile.
		/*
		 * //Send Profile Add Request to OrbitalGatewayWebService.
		 * ProfileResponseElement authResponse = null; //First Get the service
		 * PaymentechGatewayLocator service = new PaymentechGatewayLocator();
		 * //Create a Auth request ProfileDeleteElement authBean = new
		 * ProfileDeleteElement(); //TODO Next create a port from the service
		 * PaymentechGatewayPortType portType = service.getPaymentechGateway(new
		 * URL( OGWS_URL)); //Testing merchant
		 * authBean.setOrbitalConnectionUsername(OGWS_CONNECTION_USERNAME);
		 * authBean.setOrbitalConnectionPassword(OGWS_CONNECTION_PASSWORD);
		 * authBean.setMerchantID(OGWS_MERCHANT_ID); //Next create a port from
		 * the service PaymentechGatewayPortType portType =
		 * service.getPaymentechGateway(new URL( YamiConstant.OGWS_URL)); //Live
		 * merchant authBean.setOrbitalConnectionUsername(YamiConstant.
		 * OGWS_CONNECTION_USERNAME);
		 * authBean.setOrbitalConnectionPassword(YamiConstant
		 * .OGWS_CONNECTION_PASSWORD);
		 * authBean.setMerchantID(YamiConstant.OGWS_MERCHANT_ID);
		 * 
		 * authBean.setVersion(OGWS_VERSION); authBean.setBin(OGWS_BIN_PNS);
		 * authBean.setCustomerRefNum(profile_id.toString()); //Invoke the
		 * newOrder service and print reponse try { authResponse =
		 * portType.profileDelete(authBean); } catch (Exception ie) {
		 * logger.error("######### Error Response ##############");
		 * logger.error(ie.toString()); throw new
		 * YamiException(YamiConstant.ERRORCODE_ER1311
		 * ,ErrorCodeEnum.ER1311.getMsg()); }
		 * logger.info("###Response Received ###"); logger.info(" ProcStatus: "
		 * + authResponse.getProcStatus()); logger.info(" ProcStatusMessage: " +
		 * authResponse.getProcStatusMessage());
		 */
	}

	/*
	 * 淇敼鐢ㄦ埛鍚�
	 * 
	 * @param String token
	 * 
	 * @param int gid
	 * 
	 * @return int
	 */
	public Map<String, Object> transactionEditUserName(String token, String username)
			throws Exception {
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int uid = Integer.parseInt(tokenIn.getData());
		Map<String, Object> result = new HashMap<String, Object>();
		int count = userService.selectCountByUserName(uid, username);
		if (count == 0) {
			userService.updateUserNameByUid(uid, username);
		} else {
			throw new YamiException(YamiConstant.ERRORCODE_ER1303, ErrorCodeEnum.ER1303.getMsg());
		}
		result.put("token", token);
		result.put("status", 1);
		return result;
	}

	public Map<String, Object> transactionEditRealName(String token, String first_name,String last_name)
			throws Exception {
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int uid = Integer.parseInt(tokenIn.getData());
		Map<String, Object> result = new HashMap<String, Object>();
		userService.updateRealNameByUid(uid, first_name,last_name);

		result.put("token", token);
		result.put("status", 1);
		return result;
	}
	
	/*
	 * 淇敼鐢ㄦ埛鎬у埆
	 * 
	 * @param String token
	 * 
	 * @param int gid
	 * 
	 * @return int
	 */
	public Map<String, Object> transactionEditSex(String token, int gender) throws Exception {
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int uid = Integer.parseInt(tokenIn.getData());
		Map<String, Object> result = new HashMap<String, Object>();
		String avatar = YamiConstant.STRING_EMPTY;
		if (gender == 1) {
			avatar = "images/avatar/m1.png";
		} else if (gender == 2) {
			avatar = "images/avatar/f1.png";
		} else {
			avatar = "images/avatar/s1.png";
		}
		userService.updateSexByUid(uid, gender, avatar);
		result.put("token", token);
		result.put("status", 1);
		return result;
	}

	/*
	 * 淇敼鐢ㄦ埛鎵嬫満鍙�
	 * 
	 * @param String token
	 * 
	 * @param int gid
	 * 
	 * @return int
	 */
	public Map<String, Object> transactionEditPhone(String token, String mobile_phone,String pwd) throws Exception {
		if (null == mobile_phone || mobile_phone.equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1605, ErrorCodeEnum.ER1605.getMsg());
		}
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		Integer uid = Integer.valueOf(tokenIn.getData());
		Users user = userService.selectUserInfoByUid(uid);
		String password = StringUtil.EncoderByMd5(user.getSalt(), pwd, YamiConstant.ENCTIMES);
		

		// logger.info("password(MD5)="+password);

		// 3.compare password(MD5).
		if (!password.equals(user.getPassword())) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1301, ErrorCodeEnum.ER1301.getMsg());
		}

		Map<String, Object> result = new HashMap<String, Object>();
		userService.updatePhoneByUid(uid, mobile_phone);
		result.put("token", token);
		result.put("status", 1);
		return result;
	}

	/*
	 * 淇敼鐢ㄦ埛淇℃伅
	 * 
	 * @param String token
	 * 
	 * @param int gid
	 * 
	 * @return int
	 */
	public Map<String, Object> transactionUsersInfo(String token, String user_name,
			String first_name, String last_name, int gender, String birthday, String phone) {

		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int uid = Integer.parseInt(tokenIn.getData());
		Map<String, Object> result = new HashMap<String, Object>();
		int count = userService.selectCountByUserName(uid, user_name);
		if (count == 0) {
			userService.updateUserNameByUid(uid, user_name);
		} else {
			throw new YamiException(YamiConstant.ERRORCODE_ER1303, ErrorCodeEnum.ER1303.getMsg());
		}
		String avatar = YamiConstant.STRING_EMPTY;
		if (gender == 1) {
			avatar = "images/avatar/m1.png";
		} else if (gender == 2) {
			avatar = "images/avatar/f1.png";
		} else {
			avatar = "images/avatar/s1.png";
		}
		userService.updateUserInfoByUid(uid, user_name, first_name, last_name, "",gender, new Date(birthday),
				phone, avatar);
		result.put("token", token);
		result.put("status", 1);
		return result;
	}
	public void transactionShipShoe(Integer shurack_id, String product_sku,Integer so_id) throws Exception {
		Integer time = Integer.valueOf(DateUtil.getNowLong().toString());
		// update my shurack
		shurackService.updateStatusAndShipTime(shurack_id, 1,time);
		// subtract quantity
		productService.subQuantityBySku(product_sku);
		orderInfoService.updateShipInfoById(time, 1, so_id);
		
	}
	public void transactionShurackShipNext(Integer shurack_id, Integer status) throws Exception {
		// check user type
		/*int userType = Integer.valueOf(userService.selectUsersTypeByUid(uid).get("user_type"));*/

		// update my shurack
		shurackService.updateStatus(shurack_id, status);
		//shurackService.updateNextShip(uid);
		// calculate shoes need ship
/*		int slot = 0;
		shurackService.countShurackByUserId(uid, status);*/
	}
	public void transactionShurackOverdue(Integer shurack_id) throws Exception {

		shurackService.updateStatusAndBuyTime(shurack_id, 3,Integer.valueOf(DateUtil.getNowLong().toString()));
	}
	public void transactionDonateShoe(Integer so_id,Integer shurack_id) throws Exception {

		Integer time = Integer.valueOf(DateUtil.getNowLong().toString());
	
		// update my shurack
		shurackService.updateStatusAndDonateTime(shurack_id, 21,time);
		orderInfoService.updateDonateInfoById(time, 1, so_id);
		
	}	
	public void transactionBuyShoe(Integer uid, String profile_id,  String product_sku,
			Double amount, String use_card,Integer shurack_id) throws Exception {

		if ("1".equals(use_card)) {
			UserProfile userProfile = userService.getProfileByPid(profile_id);
			if(userProfile==null){
				throw new YamiException(YamiConstant.ERRORCODE_ER1312,ErrorCodeEnum.ER1312.getMsg());			
			}
			// create sub order	
/*			OrderInfo createOrder = new OrderInfo();
			createOrder.setCreate_time(Integer.valueOf(DateUtil.getNowLong().toString()));
			createOrder.setPay_status(0);
			createOrder.setPay_time(0);
			createOrder.setUser_id(uid);
			createOrder.setShurack_id(shurack_id);
			int orderId = orderInfoService.insertOrderInfo(createOrder);*/
			OrderInfo updateOrder = orderInfoService.selectOrderInfo(uid, shurack_id, product_sku);
			// pay
			OrderHistory orderHistory = paymentServiceDelegate.doPayment(uid,userProfile.getProfile_id(), updateOrder.getSo_id().toString(),  amount,
					"dollar");
			updateOrder.setAmount(orderHistory.getAmount());
			updateOrder.setCard_type(orderHistory.getBrand());
			updateOrder.setExp_month(orderHistory.getExp_month());
			updateOrder.setExp_year(orderHistory.getExp_year());
			updateOrder.setPay_status(1);
			updateOrder.setPay_time(orderHistory.getCreated());
			updateOrder.setProfile_id(profile_id);
			updateOrder.setTail(orderHistory.getLast4());
			updateOrder.setName_of_card(orderHistory.getName());
			updateOrder.setUse_card(1);
			orderInfoService.updateOrderInfo(updateOrder);
/*			// subtract quantity
			productService.subQuantityBySku(product_sku);*/
		} else {
/*			OrderInfo createOrder = new OrderInfo();
			createOrder.setCreate_time(Integer.valueOf(DateUtil.getNowLong().toString()));
			createOrder.setPay_status(0);
			createOrder.setPay_time(0);
			createOrder.setUser_id(uid);
			createOrder.setShurack_id(shurack_id);
			int orderId = orderInfoService.insertOrderInfo(createOrder);*/
			// subtract pay points
			userService.subPayPointsByUid(uid);
/*			// subtract quantity
			productService.subQuantityBySku(product_sku);*/
			OrderInfo updateOrder = orderInfoService.selectOrderInfo(uid, shurack_id, product_sku);

			updateOrder.setPay_status(2);
			updateOrder.setPay_time(Integer.valueOf(DateUtil.getNowLong().toString()));
			updateOrder.setUse_card(0);
			orderInfoService.updateOrderInfo(updateOrder);
		}
		shurackService.updateStatusAndBuyTime(shurack_id, 31,Integer.valueOf(DateUtil.getNowLong().toString()));
	}
	
	public void transactionNoInventory(Integer shurack_id,Integer so_id) throws Exception {
		shurackService.updateStatus(shurack_id, 91);
		Integer time = Integer.valueOf(DateUtil.getNowLong().toString());
		orderInfoService.updateShipInfoById(time, 91, so_id);
	}
	
	public void transactionSubscribe(Integer user_id,Integer subscribed) throws Exception {
		userService.updateSubscribeByUid(user_id, subscribed);
	}
	
	public int transactionShipOrder(Shurack shurack) throws Exception {
		UserAddress address = userService.getAddressBookByType(shurack.getUser_id(), 0).get(0);
		//for(Shurack shurack:shurackList){
			// create sub order
		
			try{
			if(productService.selectQuantityBySku(shurack.getProduct_sku())<=0){
				shurackService.updateStatus(shurack.getShurack_id(), 91);
				logger.error("The product:("+shurack.getProduct_sku()+") for shurack:("+shurack.getShurack_id()+") is no inventory.");
				return 1;
			}
			OrderInfo createOrder = new OrderInfo();
			createOrder.setCreate_time(Integer.valueOf(DateUtil.getNowLong().toString()));
			createOrder.setPay_status(0);
			createOrder.setPay_time(0);
			createOrder.setUser_id(shurack.getUser_id());
			createOrder.setShurack_id(shurack.getShurack_id());
			createOrder.setAddress(address.getAddress());
			createOrder.setAddress2(address.getAddress2());
			createOrder.setAddress_id(address.getAddress_id());
			createOrder.setCity(address.getCity());
			createOrder.setCountry(address.getCountry());
			createOrder.setPhone(address.getPhone());
			createOrder.setProduct_sku(shurack.getProduct_sku());
			createOrder.setState(address.getState());
			createOrder.setZipcode(address.getZipcode());
			int orderId = orderInfoService.insertOrderInfo(createOrder);
			shurackService.updateStatus(shurack.getShurack_id(), 11);
			logger.info("Make new ship order:("+orderId+") for shurack:"+shurack.getShurack_id());
			}catch(Exception e){
				logger.fatal("Failed to make new ship order for shurack:"+shurack.getShurack_id());				
			}
		//}
			return 0;
	}
	
/*	public Map<String, Object> transactionSubscribe(String token,String preset_type,String profile_id,String cvv,String promo_code) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> model = new HashMap<String, Object>();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		Integer uid = Integer.valueOf(tokenIn.getData());
		Users user = userService.selectUserInfoByUid(uid);
		Double amount = new Double(0);
		switch (preset_type){
		case "21":
			amount =39.9;
			break;
		case "22":
			amount =49.9;
			break;
		case "23":
			amount =59.9;
			break;
		default:
			amount =39.9;
		}
		// create sub order
		SubOrder createOrder = new SubOrder();
		createOrder.setSub_create_time(Integer.valueOf(DateUtil.getNowLong().toString()));
		createOrder.setSub_pay_status(0);
		createOrder.setSub_time(0);
		createOrder.setSub_type(Integer.valueOf(preset_type));
		createOrder.setUser_id(uid);
		createOrder.setSub_exc_status(0);
		subOrderService.insertSubOrder(createOrder);
		int orderId = createOrder.getSub_order_id();
		// pay
		paymentServiceDelegate.doPayment(uid,profile_id, String.valueOf(orderId), cvv, amount, "dollar");
		
		// update sub order
		Date date = new Date();
		//Long time = date.getTime()/1000;
		subOrderService.updateSubOrderById(DateUtil.getTimesday(), 1, orderId,1);	
		// update user type
		Integer userType;
		if(user.getUserType()==1){
			switch (preset_type){
			case "21":
				userType =31;
				break;
			case "22":
				userType =32;
				break;
			case "23":
				userType =33;
				break;
			default:
				userType =31;
			}
		}else{
			userType = createOrder.getSub_type();
		}
		userService.updateUserTypeByUid(uid, userType,userType,DateUtil.addDate(date, 30));
		// check promo code
		Users inviteUser = userService.selectUserInfoByInviteCode(promo_code);
		if(null!=inviteUser){
			userService.updatePayPointsByUid(inviteUser.getUserId(), inviteUser.getPayPoints()+1);
			userService.updatePayPointsByUid(uid, user.getPayPoints()+1);
		}
		String invitation_code;
		// update invite code
		do{
			 invitation_code = StringUtil.getRandomString(6);
		} while (userService.selectUserCountByInvite(invitation_code)!=0);
		userService.updateInviteCodeByUid(uid, invitation_code);
			
		model.put("token", token);
		return model;
		
	}*/	
	
	public Map<String, Object> transactionNewSubscribeV2(String token, String preset_type,  String promo_code, String first_name,
			String last_name, String address1, String address2, String city, String state,
			String zipcode, String phone, String name_of_card, String account,
			String exp_year, String exp_month, String cvv, String billing_address1,
			String billing_address2, String billing_city, String billing_zip, String billing_state,
			String billing_phone) throws Exception {
		
		Map<String, Object> model = new HashMap<String, Object>();
		//new Address
		/*
		 * if(null==first_name||first_name.trim().equals("")){ throw new
		 * YamiException
		 * (YamiConstant.ERRORCODE_ER1607,ErrorCodeEnum.ER1607.getMsg()); }
		 * if(null==last_name||last_name.trim().equals("")){ throw new
		 * YamiException
		 * (YamiConstant.ERRORCODE_ER1606,ErrorCodeEnum.ER1606.getMsg()); }
		 */
		if (null == address1 || address1.trim().equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1608, ErrorCodeEnum.ER1608.getMsg());
		}
		if (null == city || city.trim().equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1609, ErrorCodeEnum.ER1609.getMsg());
		}
		if (null == zipcode || !StringUtil.checkZipcode(zipcode)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1610, ErrorCodeEnum.ER1610.getMsg());
		}
		
		
/*		List<Integer> zoneList = userService.selectShipZoneList();
		if (!zoneList.contains(Integer.valueOf(zipcode.trim()))) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1614, ErrorCodeEnum.ER1614.getMsg());
		}*/
		/*
		 * if(null==phone||phone.trim().equals("")){ throw new
		 * YamiException(YamiConstant
		 * .ERRORCODE_ER1611,ErrorCodeEnum.ER1611.getMsg()); }
		 */
		/*
		 * if(StringUtil.filterChinese(first_name).length()!=0||StringUtil.
		 * filterChinese(last_name).length()!=0){ throw new
		 * YamiException(YamiConstant
		 * .ERRORCODE_ER1614,ErrorCodeEnum.ER1614.getMsg()); }
		 */
		if (StringUtil.filterChinese(address1).length() != 0
				|| StringUtil.filterChinese(address2).length() != 0) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1615, ErrorCodeEnum.ER1615.getMsg());
		}
		
		if (null == name_of_card || name_of_card.trim().equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1607, ErrorCodeEnum.ER1607.getMsg());
		}

		if (null == account || !StringUtil.checkAccount(account.trim())) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1613, ErrorCodeEnum.ER1613.getMsg());
		}

		if (null == billing_address1 || billing_address1.trim().equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1608, ErrorCodeEnum.ER1608.getMsg());
		}
		if (null == billing_city || billing_city.trim().equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1609, ErrorCodeEnum.ER1609.getMsg());
		}
		if (null == billing_zip || !StringUtil.checkZipcode(billing_zip)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1610, ErrorCodeEnum.ER1610.getMsg());
		}

		if (StringUtil.filterChinese(billing_address1).length() != 0
				|| StringUtil.filterChinese(billing_address2).length() != 0) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1615, ErrorCodeEnum.ER1615.getMsg());
		}

		String cardNum = account.trim();
		Integer tail = Integer.valueOf(cardNum.substring(cardNum.length() - 4));
		String cardType = CardType.detect(account).toString();
		if (cardType.equals(YamiConstant.OGWS_CARD_TYPE_UNKN)
				|| cardType.equals(YamiConstant.OGWS_CARD_TYPE_DINE)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1616, ErrorCodeEnum.ER1616.getMsg());
		}
		
		Gson gson = new Gson();
		
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		UserAddress address = new UserAddress();
		int userId = Integer.valueOf(tokenIn.getData());
		address.setAddress(address1);
		address.setAddress2(address2);
		address.setCity(city);
		address.setFirst_name(first_name);
		address.setLast_name(last_name);
		address.setCountry(YamiConstant.COUNTRY_UNITEDSTATES);
		address.setState(state);
		address.setPhone(null == phone ? null : phone);
		address.setUser_id(userId);
		address.setZipcode(zipcode);
		// for not used column but used by old PC logic
		address.setAddress_type(0);

		// ransactionDelegate.transactionNewAddress(address);
		userService.deleteAddressByUid(userId, 0);
		userService.insertAddress(address);

		model.put("address_id", address.getAddress_id());
		model.put("first_name", address.getFirst_name());
		model.put("last_name", address.getLast_name());
		model.put("address1", address.getAddress());
		model.put("address2", address.getAddress2());
		model.put("city", address.getCity());
		model.put("state", address.getState());
		model.put("zipcode", address.getZipcode());
		model.put("phone", address.getPhone());
		model.put("token", token);
		
		//new Payment		
		
		UserProfile profile = new UserProfile();
		//Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		//Integer uid = Integer.valueOf(tokenIn.getData());
		Boolean addBlacklist = false;
		/*
		 * int countBlackCardNum =
		 * blacklistService.selectCountByBankCard(cardNum); if
		 * (countBlackCardNum != 0) { Integer countBlackUserId =
		 * blacklistService.selectCountByUserId(uid); if (countBlackUserId == 0)
		 * addBlacklist = true; }
		 */

		// String profileId = uid + "M" + DateUtil.getNowLong().toString();
		// int countProfile = userService.countProfileByUid(uid);
		// profile.setAddress_id(Integer.valueOf(address_id));
		profile.setCard_type(cardType);
		profile.setExp_year(exp_year);
		profile.setExp_month(exp_month);
		profile.setName_of_card(name_of_card);
		profile.setNumber(cardNum);
		// profile.setProfile_id(profileId);
		profile.setTail(tail);
		profile.setUser_id(userId);
		profile.setAddress_line1(billing_address1);
		profile.setAddress_line2(billing_address2);
		profile.setAddress_city(billing_city);
		profile.setAddress_country("USA");
		profile.setAddress_state(billing_state);
		profile.setAddress_zip(billing_zip);
		profile.setPhone(phone);
		profile.setCvc(cvv);
		String profile_id = transactionNewPayment(profile, account,
				addBlacklist);
		//model.put("token", token);
		// model.put("address_id", address_id);
		model.put("name_of_card", name_of_card);
		model.put("profile_id", profile_id);
		model.put("type", cardType);
		model.put("tail", tail);
		model.put("exp_year", exp_year);
		model.put("exp_month", exp_month);

		//new subscribe
		//Map<String, Object> result = new HashMap<String, Object>();
		transactionNewSubscribe(token, preset_type, profile_id, cvv,	
				promo_code);

		// Map<String, Object> model = new HashMap<String, Object>();
		Users user = userService.selectUsersByID(userId);
		userService.updateUserInfoByUid(userId, user.getUserName(), address.getFirst_name(), address.getLast_name(), address.getZipcode(), user.getSex(), user.getBirthday(),
				address.getPhone(), user.getAvatar());
		// Random random = new Random();
		// String newSalt =
		// String.valueOf(random.nextInt(YamiConstant.RANDOM_SALT_RANGE));
		// String password = StringUtil.EncoderByMd5(newSalt, pwd,
		// YamiConstant.ENCTIMES);
		//String password = user.getPassword();

/*		String auth = StringUtil.EncoderByMd5(user.getSalt(), String.valueOf(uid),
				YamiConstant.ENCTIMES);
		String newToken = securityServiceDelegate.getToken(uid, user.getSalt(), auth);*/
		String auth = StringUtil.EncoderByMd5(user.getSalt(), String.valueOf(userId),
				YamiConstant.ENCTIMES);		
		Context data = new Context();
		VerifyToken verifyToken = new VerifyToken();
		verifyToken.setUid(userId);
		verifyToken.setSalt(user.getSalt());
		verifyToken.setCode(auth);
		String herf = USER_VERIFY_LINK + StringUtil.encode(gson.toJson(verifyToken));		
		//String herf = USER_VERIFY_LINK + password;
		data.setVariable("user_name", address.getFirst_name());
		data.setVariable("reset_email_en", herf);
		backEndServiceDelegate.sendMail( user.getEmail(), "email-subscribe",
				"You’re a Shoobilee subscriber!", address.getFirst_name(), data);
		
		return model;
	}
	public Map<String, Object> transactionNewSubscribe(String token,String preset_type,String profile_id,String cvv,String promo_code) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> model = new HashMap<String, Object>();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		Integer uid = Integer.valueOf(tokenIn.getData());
		Users user = userService.selectUserInfoByUid(uid);
		if(user.getUserType()==21||user.getUserType()==22||user.getUserType()==23||user.getUserType()==31||user.getUserType()==32||user.getUserType()==33){
			throw new YamiException(YamiConstant.ERRORCODE_ER1338, ErrorCodeEnum.ER1338.getMsg());
		}
		String plan_id = "";
		String coupon_id ="";
		switch (preset_type){
		case "21":
			plan_id =YamiConstant.ONE_PAIR_PLAN;
			break;
		case "22":
			plan_id ="2PAIRS";
			break;
		case "23":
			plan_id ="3PAIRS";
			break;
		default:
			plan_id =YamiConstant.ONE_PAIR_PLAN;
		}
		String[] promos = promo_code.split(",");
		Coupon couponUse = new Coupon();
		Boolean HasCoupon = false; 
		SubOrder user_sub = null;
		//SubOrder reuser_sub = null;
		for (int i = 0; i < promos.length; i++) {
			String promoCode = promos[i];
			Users inviteUser = userService.selectUserInfoByInviteCode(promoCode);
			if (null != inviteUser) {
				int point = StringUtil.getReferPoint();
				int referer_point = StringUtil.getRefererPoint();
				userService.updatePayPointsByUid(inviteUser.getUserId(),
						inviteUser.getPayPoints() + referer_point);
				userService.updatePayPointsByUid(uid, user.getPayPoints() + point);
				user_sub = subOrderService.selectSubOrderByUid(uid);
				//reuser_sub = subOrderService.selectSubOrderByUid(inviteUser.getUserId());
				
			} else {
				Coupon coupon = userService.selectCouponByCode(promoCode);
				if (null != coupon) {
					if (coupon.getStatus() == 1) {
						HasCoupon = true;
						couponUse = coupon;
						/*
						 * coupon_id = coupon.getCoupon_id();
						 * userService.updatePayPointsByUid(uid,
						 * user.getPayPoints()+1);
						 */
					}
				}
			}
		}
		if(HasCoupon){
			if(couponUse.getType()==1){
			coupon_id = couponUse.getCoupon_id();
			}
			if(couponUse.getType()==0||couponUse.getType()==2){
				//Type=0:0为打折加赠送point;1为只有打折
				userService.updatePayPointsByUid(uid, user.getPayPoints()+couponUse.getShoo_coin());
			}
			
		}

		// pay
		Subscription subscription = new Subscription();
		try{
			subscription = paymentServiceDelegate.subscribe(uid,profile_id,preset_type,coupon_id,user_sub);
		}catch(YamiException e){
			try{
			Customer customer = Customer.retrieve(profile_id);
			customer.delete();
			}catch(Exception e1){
				logger.error("Delete customer failed:"+profile_id);
			}
			throw e;
		}catch(Exception e){
			try{
			Customer customer = Customer.retrieve(profile_id);
			customer.delete();
			}catch(Exception e1){
				logger.error("Delete customer failed:"+profile_id);
			}
			throw e;
		}

			// create sub order
			SubOrder createOrder = new SubOrder();
			createOrder.setAmount(new BigDecimal(subscription.getPlan().getAmount()).divide(new BigDecimal(100)));
			createOrder.setCancel_at_period_end(subscription.getCancelAtPeriodEnd().toString());
			createOrder.setCanceled_at(subscription.getCanceledAt()==null?0:Integer.valueOf(subscription.getCanceledAt().toString()));
			createOrder.setCreated(Integer.valueOf(subscription.getCreated().toString()));
			createOrder.setCurrent_period_end(Integer.valueOf(subscription.getCurrentPeriodEnd().toString()));
			createOrder.setCurrent_period_start(Integer.valueOf(subscription.getCurrentPeriodStart().toString()));
			createOrder.setCustomer(subscription.getCustomer());
			createOrder.setEnded_at(subscription.getEndedAt()==null?0:Integer.valueOf(subscription.getEndedAt().toString()));
			createOrder.setPlan_id(subscription.getPlan().getId());
			createOrder.setStart(Integer.valueOf(subscription.getStart().toString()));
			createOrder.setStatus(subscription.getStatus());
			createOrder.setSub_order_id(subscription.getId());
			createOrder.setSub_type(Integer.valueOf(preset_type));
			createOrder.setUser_id(uid);
			subOrderService.insertSubOrder(createOrder);
			//transactionDelegate.transactionInsertOrderHistory(orderHistory);
			if(HasCoupon){
				CouponHistory couponHistory = new CouponHistory();
				couponHistory.setCode(couponUse.getCode());
				couponHistory.setCoupon_id(couponUse.getCoupon_id());
				couponHistory.setCreated(createOrder.getCreated());
				couponHistory.setStatus(couponUse.getStatus());
				couponHistory.setSub_order_id(subscription.getId());
				couponHistory.setType(couponUse.getType());
				couponHistory.setUser_id(uid);
				userService.insertCouponHistory(couponHistory);
			}
		// TODO update sub order
		//Date date = new Date();
		//Long time = date.getTime()/1000;
		//subOrderService.updateSubOrderById(DateUtil.getTimesday(), 1, orderId,1);	
		// update user type
		Integer userType;
		if(user.getUserType()==1){
			switch (preset_type){
			case "21":
				userType =31;
				break;
			case "22":
				userType =32;
				break;
			case "23":
				userType =33;
				break;
			default:
				userType =31;
			}
		}else{
			userType = createOrder.getSub_type();
		}
		userService.updateUserTypeByUid(uid, userType,userType);

		String invitation_code;
		// update invite code
		do{
			 invitation_code = StringUtil.getRandomString(6);
		} while (userService.selectUserCountByInvite(invitation_code)!=0);
		userService.updateInviteCodeByUid(uid, invitation_code);
			
		model.put("token", token);
		return model;
		
	}
	public Map<String, Object> transactionChangePlan(String token,String preset_type,String sub_order_id) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> model = new HashMap<String, Object>();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		Integer uid = Integer.valueOf(tokenIn.getData());
		Users user = userService.selectUserInfoByUid(uid);
		Stripe.apiKey = STRIPE_API_KEY;
		//Double amount = new Double(0);
		//cancel plan

		Subscription subscription = paymentServiceDelegate.updateSubscribe(uid, preset_type, sub_order_id);
		// create sub order
/*		SubOrder createOrder = new SubOrder();
		createOrder.setAmount(new BigDecimal(subscription.getPlan().getAmount()).divide(new BigDecimal(100)));
		createOrder.setCancel_at_period_end(subscription.getCancelAtPeriodEnd().toString());
		createOrder.setCanceled_at(subscription.getCanceledAt()==null?0:Integer.valueOf(subscription.getCanceledAt().toString()));
		createOrder.setCreated(Integer.valueOf(subscription.getCreated().toString()));
		createOrder.setCurrent_period_end(Integer.valueOf(subscription.getCurrentPeriodEnd().toString()));
		createOrder.setCurrent_period_start(Integer.valueOf(subscription.getCurrentPeriodStart().toString()));
		createOrder.setCustomer(subscription.getCustomer());
		createOrder.setEnded_at(subscription.getEndedAt()==null?0:Integer.valueOf(subscription.getEndedAt().toString()));
		createOrder.setPlan_id(subscription.getPlan().getId());
		createOrder.setStart(Integer.valueOf(subscription.getStart().toString()));
		createOrder.setStatus(subscription.getStatus());
		createOrder.setSub_order_id(subscription.getId());
		createOrder.setSub_type(Integer.valueOf(preset_type));
		createOrder.setUser_id(uid);
		subOrderService.updateSubOrderById(createOrder);*/
		// pay
		Integer userType;
		Integer num=1;
		String plan_name="";
		if(user.getUserType()==21||user.getUserType()==22||user.getUserType()==23){
			userType = Integer.valueOf(preset_type);
		}else{
			switch (preset_type){
			case "21":
				userType =31;
				num = 1;
				plan_name = "1 PAIR1 MONTHLY PLAN";
				break;
			case "22":
				userType =32;
				num = 2;
				plan_name = "2 PAIRS MONTHLY PLAN";
				break;
			case "23":
				userType =33;
				num = 3;
				plan_name = "3 PAIRS MONTHLY PLAN";
				break;
			default:
				userType =31;
				num = 1;
				plan_name = "1 PAIR1 MONTHLY PLAN";
			}
		}
		// update sub order
		//Date date = new Date();
		//subOrderService.updateSubOrderById(DateUtil.getTimesday()+24*3600*30, 1, orderId,0);	
		// update user PreType
		userService.updateUserPresetTypeByUid(uid, userType);
		Context data = new Context();
		data.setVariable("user_name", user.getFirstName());
		data.setVariable("plan_name", plan_name);
		data.setVariable("num", num);
		data.setVariable("date", DateUtil.formateUTC2Date(subscription.getCurrentPeriodEnd()+24*3600));
		backEndServiceDelegate.sendMail( user.getEmail(), "email-change-plan",
				"Change In Monthly Plan", user.getUserName(), data);
			
		model.put("token", token);
		return model;
	}	
	
	public Map<String, Object> transactionUnSubscribe(String token,String unsub_type,String unsub_msg,String sub_order_id) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> model = new HashMap<String, Object>();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		Integer uid = Integer.valueOf(tokenIn.getData());
		//Users user = userService.selectUserInfoByUid(uid);
		//TODO update at the period end?
		userService.updateUserPresetTypeByUid(uid, 1);
		//cancel plan
		// create sub order

		Stripe.apiKey = STRIPE_API_KEY;
		Subscription subscription = new Subscription();	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("at_period_end", "true");		
		try{
			subscription = Subscription.retrieve(sub_order_id);
			subscription = subscription.cancel(params);
			logger.info(" authResponse: " + JSONFormatter.toJSON(subscription));
		} catch (CardException e) {
		  // Since it's a decline, CardException will be caught
		  logger.error("Status is: " + e.getCode());
		  logger.error("Message is: " + e.getMessage());
		  throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (RateLimitException e) {
		  // Too many requests made to the API too quickly
			logger.error("Too many requests made to the API too quickly. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (InvalidRequestException e) {
		  // Invalid parameters were supplied to Stripe's API
			logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (AuthenticationException e) {
		  // Authentication with Stripe's API failed
		  // (maybe you changed API keys recently)
			logger.error("Authentication with Stripe's API failed. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (APIConnectionException e) {
		  // Network communication with Stripe failed
			logger.error("Network communication with Stripe failed. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (StripeException e) {
		  // Display a very generic error to the user, and maybe send
		  // yourself an email
			logger.error("Stripe Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (Exception e) {
		  // Something else happened, completely unrelated to Stripe
			logger.error("Completely unrelated to Stripe. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		}

		UnSubOrder unSubOrder = new UnSubOrder();
		unSubOrder.setSub_order_id(sub_order_id);
		unSubOrder.setUnsub_time(subscription.getCanceledAt()==null?Integer.valueOf(DateUtil.getNowLong().toString()):Integer.valueOf(subscription.getCanceledAt().toString()));
		unSubOrder.setUnsub_type(Integer.valueOf(unsub_type));
		unSubOrder.setUser_id(uid);
		unSubOrder.setUnsub_msg(unsub_msg);
		unSubOrderService.insertUnSubOrder(unSubOrder);
		
		model.put("status", YamiConstant.STATUS_OK);
		model.put("currentPeriodEnd", subscription.getCurrentPeriodEnd());
		model.put("token", token);
		return model;
	}	
	
	public Map<String, Object> transactionUnSubscribeByAdmin(String token,Integer uid,String unsub_type,String unsub_msg,String sub_order_id) throws Exception {

		//Gson gson = new Gson();
		Map<String, Object> model = new HashMap<String, Object>();
		//Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		//Integer uid = Integer.valueOf(tokenIn.getData());
		//Users user = userService.selectUserInfoByUid(uid);
		//TODO update at the period end?
		userService.updateUserPresetTypeByUid(uid, 1);
		//cancel plan
		// create sub order

		Stripe.apiKey = STRIPE_API_KEY;
		Subscription subscription = new Subscription();	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("at_period_end", "true");		
		try{
			subscription = Subscription.retrieve(sub_order_id);
			subscription = subscription.cancel(params);
			logger.info(" authResponse: " + JSONFormatter.toJSON(subscription));
		} catch (CardException e) {
		  // Since it's a decline, CardException will be caught
		  logger.error("Status is: " + e.getCode());
		  logger.error("Message is: " + e.getMessage());
		  throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (RateLimitException e) {
		  // Too many requests made to the API too quickly
			logger.error("Too many requests made to the API too quickly. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (InvalidRequestException e) {
		  // Invalid parameters were supplied to Stripe's API
			logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (AuthenticationException e) {
		  // Authentication with Stripe's API failed
		  // (maybe you changed API keys recently)
			logger.error("Authentication with Stripe's API failed. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (APIConnectionException e) {
		  // Network communication with Stripe failed
			logger.error("Network communication with Stripe failed. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (StripeException e) {
		  // Display a very generic error to the user, and maybe send
		  // yourself an email
			logger.error("Stripe Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		} catch (Exception e) {
		  // Something else happened, completely unrelated to Stripe
			logger.error("Completely unrelated to Stripe. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1337, ErrorCodeEnum.ER1337.getMsg());
		}

		UnSubOrder unSubOrder = new UnSubOrder();
		unSubOrder.setSub_order_id(sub_order_id);
		unSubOrder.setUnsub_time(subscription.getCanceledAt()==null?Integer.valueOf(DateUtil.getNowLong().toString()):Integer.valueOf(subscription.getCanceledAt().toString()));
		unSubOrder.setUnsub_type(Integer.valueOf(unsub_type));
		unSubOrder.setUser_id(uid);
		unSubOrder.setUnsub_msg(unsub_msg);
		unSubOrderService.insertUnSubOrder(unSubOrder);
		
		model.put("status", YamiConstant.STATUS_OK);
		model.put("currentPeriodEnd", subscription.getCurrentPeriodEnd());
		model.put("token", token);
		return model;
	}	
/*	public Map<String, Object> transactionBackEndOrder() throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		List<SubOrder> subOrderList = subOrderService.selectDailySubOrder();
		
		for(SubOrder subOrder:subOrderList){
			try{
			paymentServiceDelegate.getSubscribe(subOrder);
			}catch(Exception e){
				logger.fatal("Daily update subscription failed for sub order id:"+subOrder.getSub_order_id()+" error:"+e.getMessage());
			}
		}
		//TODO auto resubscribe

		model.put("status", YamiConstant.STATUS_OK);
		return model;
	}	*/
	
	
}
