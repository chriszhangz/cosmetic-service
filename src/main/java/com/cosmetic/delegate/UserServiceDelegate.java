package com.cosmetic.delegate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.QueryParam;

import net.paymentech.ws.NewOrderRequestElement;
import net.paymentech.ws.NewOrderResponseElement;
import net.paymentech.ws.ProfileAddElement;
import net.paymentech.ws.ProfileResponseElement;
import net.paymentech.ws.PaymentechGateway.wsdl.PaymentechGateway_wsdl.PaymentechGatewayLocator;
import net.paymentech.ws.PaymentechGateway.wsdl.PaymentechGateway_wsdl.PaymentechGatewayPortType;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.cosmetic.common.CardType;
import com.cosmetic.common.DateUtil;
import com.cosmetic.common.ErrorCodeEnum;
import com.cosmetic.common.StringUtil;
import com.cosmetic.common.YamiConstant;
import com.cosmetic.common.YamiException;
import com.cosmetic.entity.Coupon;
import com.cosmetic.entity.DonateCode;
import com.cosmetic.entity.EmailToken;
import com.cosmetic.entity.Holiday;
import com.cosmetic.entity.OrderInfo;
import com.cosmetic.entity.PaymentHistory;
import com.cosmetic.entity.Product;
import com.cosmetic.entity.Profile;
import com.cosmetic.entity.Shurack;
import com.cosmetic.entity.SubOrder;
import com.cosmetic.entity.Token;
import com.cosmetic.entity.UserAddress;
import com.cosmetic.entity.UserProfile;
import com.cosmetic.entity.Users;
import com.cosmetic.entity.VerifyToken;
import com.cosmetic.service.BackEndService;
import com.cosmetic.service.OrderInfoService;
import com.cosmetic.service.ProductService;
import com.cosmetic.service.ShurackService;
import com.cosmetic.service.SubOrderService;
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
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.ChargeCollection;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import com.stripe.model.ExternalAccountCollection;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;

@Service
public class UserServiceDelegate {

	@Autowired
	private BackEndService backEndService;
	@Autowired
	private UserService userService;
	@Autowired
	private PaymentServiceDelegate paymentServiceDelegate;
	@Autowired
	private SubOrderService subOrderService;
	@Autowired
	private OrderInfoService orderInfoService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ShurackService shurackService;

	@Autowired
	private SecurityServiceDelegate securityServiceDelegate;

	@Autowired
	private TransactionDelegate transactionDelegate;
	
	@Autowired
	private BackEndServiceDelegate backEndServiceDelegate;

	@Autowired
	private TemplateEngine templateEngine;

	@Value("${STRIPE_API_KEY}")
	private String STRIPE_API_KEY;
	@Value("${USER_RESET_LINK_CN}")
	private String USER_RESET_LINK_CN;
	@Value("${USER_RESET_LINK_EN}")
	private String USER_RESET_LINK_EN;
	@Value("${USER_EMAIL_EXP}")
	private Integer USER_EMAIL_EXP;
	@Value("${USER_VERIFY_LINK}")
	private String USER_VERIFY_LINK;
	@Value("${DONATE_LINK}")
	private String DONATE_LINK;
	@Value("${BUY_PERIOD}")
	private Integer BUY_PERIOD;

	// @Autowired
	// private LogUtil logger;
	private Logger logger = LogManager.getLogger(this.getClass().getName());

	public String getUserName(int uid) {

		// add your business logic here

		return userService.getUserName(uid);
	}

	public Map<String, Object> getUserType(String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		result.put("token", token);
		if (tokenIn.getIsLogin() == 0) {
			result.put("user_type", 4);
			result.put("preset_type", 4);
			return result;
		}
		int uid = Integer.parseInt(tokenIn.getData());
		Map<String, String> map = userService.selectUsersTypeByUid(uid);
		if(map.get("user_type")==null){
			throw new YamiException(YamiConstant.ERRORCODE_ER1003, ErrorCodeEnum.ER1003.getMsg());
		}
		result.put("user_type", map.get("user_type"));
		result.put("preset_type", map.get("preset_type"));

		return result;
	}

	public Users selectUsersByID(int id) {

		// add your business logic here

		return userService.selectUsersByID(id);
	}

	public Map<String, Object> getUserInfo(String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int uid = Integer.parseInt(tokenIn.getData());

		// 获取邀请人信息及邀请码
		Users user = userService.selectUserInfoByUid(uid);
		if (null == user) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1308, ErrorCodeEnum.ER1308.getMsg());
		}
		SubOrder subOrder = subOrderService.selectSubOrderByUid(uid);
		if (null != subOrder) {
			result.put("sub_order_info", subOrder);
		} else {
			result.put("sub_order_info", "");
		}
		result.put("user_id", user.getUserId());
		result.put("email", user.getEmail());
		result.put("user_name", user.getUserName());
		result.put("first_name", user.getFirstName());
		result.put("last_name", user.getLastName());
		result.put("zipcode", user.getZipcode());
		result.put("user_type", user.getUserType());
		result.put("avatar", user.getAvatar());
		result.put("reffer_code", user.getInvitationCode());
		result.put("points", user.getPayPoints());
		result.put("mobile_phone", user.getMobilePhone());
		result.put("subscribed", user.getSubscribed());
		result.put("token", token);

		return result;
	}

	public Map<String, Object> getPayPoints(String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int uid = Integer.parseInt(tokenIn.getData());
		int pay_points = userService.selectPayPointsByUid(uid);

		result.put("token", token);
		result.put("pay_points", pay_points);
		return result;
	}

	public Map<String, Object> updatePhone(String token, String mobile_phone) {
		Map<String, Object> result = new HashMap<String, Object>();
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int uid = Integer.parseInt(tokenIn.getData());
		userService.updatePhoneByUid(uid, mobile_phone);

		result.put("token", token);
		result.put("status", YamiConstant.STATUS_OK);
		return result;
	}

	/*
	 * 发送邮件接口
	 * 
	 * @param String token
	 * 
	 * @param String FromMail
	 * 
	 * @param String toMail
	 * 
	 * @param String mailType 1、注册邀请邮件 2、单品分享邮件
	 * 
	 * @param String title
	 * 
	 * @param String content
	 * 
	 * @return Map<String,Object>
	 */

	public void testsendEmail() throws Exception {

		Context data = new Context();

		data.setVariable("user_name", "hahaha");
		// data.setVariable("reset_email_en", "ssss");
		try {
			String emailContent = templateEngine.process("CreateAccountEmail", data);
			HtmlEmail htmlEmail = new HtmlEmail();
			htmlEmail.setHostName("smtp.gmail.com");
			htmlEmail.addTo("chris.zhang@yamibuy.com", "chris");
			htmlEmail.setAuthenticator(new DefaultAuthenticator("shoobilee@shoobilee.com",
					"peterlin"));
			htmlEmail.setFrom("noreply@shoobilee.com", "Welcome to Shoobilee");
			htmlEmail.setSubject("test1");
			// htmlEmail.setSSLOnConnect(true);
			// htmlEmail.setSmtpPort(465);
			htmlEmail.setStartTLSEnabled(true);
			htmlEmail.setSmtpPort(587);
			// embed the image and get the content id
			// URL url = new
			// URL("http://www.apache.org/images/asf_logo_wide.gif");
			// String cid = htmlEmail.embed(url, "Apache logo");

			// set the html message
			htmlEmail.setHtmlMsg(emailContent);

			// send the email
			htmlEmail.send();
		} catch (EmailException e) {
			logger.fatal("Failed to send email");
			logger.fatal(e.toString());
		}
		// data.setVariable("reset_email_en", herf);
		// sendMail(newToken, "1",emailTemp,"CreateAccountEmail",
		// "New Shoobilee Account",userName,data);
	}

/*	public Map<String, Object> sendMail(String token, String mailType, String toMail,
			String mailCategory, String title, String userName, Context data) {
		Map<String, Object> result = new HashMap<String, Object>();
		//Gson gson = new Gson();
		// Token tokenIn = gson.fromJson( StringUtil.decode(token),
		// Token.class);
		// int uid = Integer.parseInt(tokenIn.getData());
		// String email = new String();

		// 获取邀请人信息及邀请码
		// Users users = userService.selectUserInfoByUid(uid);
		try {
			String emailContent = "";
			HtmlEmail htmlEmail = new HtmlEmail();
			switch (mailType) {
			// 1、注册验证邮件
			case "1":

				emailContent = templateEngine.process(mailCategory, data);
				htmlEmail.setHostName("smtp.gmail.com");
				htmlEmail.addTo(toMail, userName);
				htmlEmail.setAuthenticator(new DefaultAuthenticator("shoobilee@shoobilee.com",
						"peterlin"));
				htmlEmail.setFrom("noreply@shoobilee.com", "Shoobilee");
				htmlEmail.setSubject(title);
				// htmlEmail.setSSLOnConnect(true);
				htmlEmail.setStartTLSEnabled(true);
				htmlEmail.setSmtpPort(587);
				// embed the image and get the content id

				htmlEmail.setHtmlMsg(emailContent);

				// send the email
				htmlEmail.send();

				break;
			// 2、shipped成功邮件
			case "2":

				emailContent = templateEngine.process(mailCategory, data);
				//HtmlEmail htmlEmail = new HtmlEmail();
				htmlEmail.setHostName("smtp.gmail.com");
				htmlEmail.addTo(toMail, userName);
				htmlEmail.setAuthenticator(new DefaultAuthenticator("shoobilee@shoobilee.com",
						"peterlin"));
				htmlEmail.setFrom("noreply@shoobilee.com", "Shoobilee");
				htmlEmail.setSubject(title);
				// htmlEmail.setSSLOnConnect(true);
				htmlEmail.setStartTLSEnabled(true);
				htmlEmail.setSmtpPort(587);
				// embed the image and get the content id

				htmlEmail.setHtmlMsg(emailContent);

				// send the email
				htmlEmail.send();

				break;
			default:
				logger.fatal("Can't read mailType:"+mailType);
			}
			
		} catch (EmailException e) {
			logger.fatal("Failed to send email");
			logger.fatal(e.toString());
		}finally{
			
		}

		result.put("token", token);
		result.put("status", 1);

		return result;
	}*/

	public Map<String, Object> registerUser_resendEmail(String token) throws Exception {
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int uid = Integer.parseInt(tokenIn.getData());

		Map<String, Object> model = new HashMap<String, Object>();
		Users user = userService.selectUsersByID(uid);

		// Random random = new Random();
		// String newSalt =
		// String.valueOf(random.nextInt(YamiConstant.RANDOM_SALT_RANGE));
		// String password = StringUtil.EncoderByMd5(newSalt, pwd,
		// YamiConstant.ENCTIMES);
		//String password = user.getPassword();

		String auth = StringUtil.EncoderByMd5(user.getSalt(), String.valueOf(uid),
				YamiConstant.ENCTIMES);
		String newToken = securityServiceDelegate.getToken(uid, user.getSalt(), auth);
		Context data = new Context();
		VerifyToken verifyToken = new VerifyToken();
		verifyToken.setUid(uid);
		verifyToken.setSalt(user.getSalt());
		verifyToken.setCode(auth);
		String herf = USER_VERIFY_LINK + StringUtil.encode(gson.toJson(verifyToken));;
		data.setVariable("user_name", user.getFirstName());
		data.setVariable("reset_email_en", herf);
		backEndServiceDelegate.sendMail( user.getEmail(), "email-confirm-email", "Activate your Shoobilee account",
				user.getUserName(), data);

		model.put("status", YamiConstant.STATUS_OK);
		model.put("token", newToken);
		return model;
	}

	public Map<String, Object> registerUser_sendEmail(String token, String emailTemp, String pwd,
			String userName, String firstName, String lastName, String zipcode, String sex,
			String birthday, String subscribed, String phone) throws Exception {
		if (!StringUtil.checkEmail(emailTemp)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1612, ErrorCodeEnum.ER1612.getMsg());
		}
		if (!StringUtil.checkMoneLength(pwd, 6)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1604, ErrorCodeEnum.ER1604.getMsg());
		}
		if (!StringUtil.checkZipcode(zipcode)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1610, ErrorCodeEnum.ER1610.getMsg());
		}

		// 如果有email重名 报错
		Users user = userService.getPasswordSalt(emailTemp);
		if (user != null) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1302, ErrorCodeEnum.ER1302.getMsg());
		}
		Map<String, Object> model = new HashMap<String, Object>();
		//if (userService.selectUserCountByUserName(userName) > 0) {
		//	throw new YamiException(YamiConstant.ERRORCODE_ER1303, ErrorCodeEnum.ER1303.getMsg());
		//}
		Random random = new Random();
		String newSalt = String.valueOf(random.nextInt(YamiConstant.RANDOM_SALT_RANGE));
		String password = StringUtil.EncoderByMd5(newSalt, pwd, YamiConstant.ENCTIMES);

		// 添加 user数据库

		long time = System.currentTimeMillis() / 1000;

		Users users = new Users();

		// String avatar= "images/avatar/s1.png";

		String temp = "";
		temp = String.valueOf(time);
		Integer date = Integer.valueOf(temp);
		users.setRegTime(date);
		users.setLastLogin(date);

		// users.setAvatar(avatar);
		users.setEmail(emailTemp);
		users.setPassword(password);
		users.setUserName(userName);
		users.setFirstName(firstName);
		users.setLastName(lastName);
		users.setUserType(0);
		users.setZipcode(zipcode);
		users.setSex(Integer.valueOf(sex));
		users.setBirthday(DateUtil.parseDate(birthday));
		users.setSubscribed(Integer.valueOf(subscribed));
		users.setMobilePhone(phone);
		// users.setParentId(parent_id);
		users.setSalt(newSalt);
		users.setPayPoints(YamiConstant.INT_ZERO);

		userService.insertUsersByEmail(users.getEmail(), users);

		int uid = userService.selectUIdByEmail(users.getEmail());
		String auth = StringUtil.EncoderByMd5(users.getSalt(), String.valueOf(uid),
				YamiConstant.ENCTIMES);
		String newToken = securityServiceDelegate.getToken(uid, users.getSalt(), auth);
		Context data = new Context();
		String herf = USER_VERIFY_LINK + password;
		data.setVariable("user_name", firstName);
		data.setVariable("reset_email_en", herf);
		backEndServiceDelegate.sendMail( emailTemp, "CreateAccountEmail", "Welcome to Shoobilee!", userName,
				data);

		model.put("status", YamiConstant.STATUS_OK);
		model.put("token", newToken);
		return model;
	}
	
	public Map<String, Object> registerUser_sendEmailV2(String token, String emailTemp, String pwd,
			 	String birthday ) throws Exception {
		if (!StringUtil.checkEmail(emailTemp)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1612, ErrorCodeEnum.ER1612.getMsg());
		}
		if (!StringUtil.checkMoneLength(pwd, 6)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1604, ErrorCodeEnum.ER1604.getMsg());
		}
/*		if (!StringUtil.checkZipcode(zipcode)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1610, ErrorCodeEnum.ER1610.getMsg());
		}*/
		if(!StringUtil.checkBirthday(birthday)){
			throw new YamiException(YamiConstant.ERRORCODE_ER1644, ErrorCodeEnum.ER1644.getMsg());
		}
		// 如果有email重名 报错
		Users user = userService.getPasswordSalt(emailTemp);
		if (user != null) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1302, ErrorCodeEnum.ER1302.getMsg());
		}
		Map<String, Object> model = new HashMap<String, Object>();
		//if (userService.selectUserCountByUserName(userName) > 0) {
		//	throw new YamiException(YamiConstant.ERRORCODE_ER1303, ErrorCodeEnum.ER1303.getMsg());
		//}
		Random random = new Random();
		String newSalt = String.valueOf(random.nextInt(YamiConstant.RANDOM_SALT_RANGE));
		String password = StringUtil.EncoderByMd5(newSalt, pwd, YamiConstant.ENCTIMES);

		// 添加 user数据库

		long time = System.currentTimeMillis() / 1000;

		Users users = new Users();

		// String avatar= "images/avatar/s1.png";

		String temp = "";
		temp = String.valueOf(time);
		Integer date = Integer.valueOf(temp);
		users.setRegTime(date);
		users.setLastLogin(date);

		// users.setAvatar(avatar);
		users.setEmail(emailTemp);
		users.setPassword(password);
		//users.setUserName(userName);
		//users.setFirstName(firstName);
		//users.setLastName(lastName);
		users.setUserType(0);
		//users.setZipcode(zipcode);
		users.setSex(2);
		users.setBirthday(DateUtil.parseDate(birthday));
		users.setSubscribed(1);
		//users.setMobilePhone(phone);
		// users.setParentId(parent_id);
		users.setSalt(newSalt);
		users.setPayPoints(YamiConstant.INT_ZERO);

		userService.insertUsersByEmail(users.getEmail(), users);
		VerifyToken verifyToken = new VerifyToken();
		int uid = userService.selectUIdByEmail(users.getEmail());
		String auth = StringUtil.EncoderByMd5(users.getSalt(), String.valueOf(uid),
				YamiConstant.ENCTIMES);
		String newToken = securityServiceDelegate.getToken(uid, users.getSalt(), auth);
		Context data = new Context();
		verifyToken.setUid(uid);
		verifyToken.setSalt(newSalt);
		verifyToken.setCode(auth);
		Gson gson = new Gson();
		String herf = USER_VERIFY_LINK + StringUtil.encode(gson.toJson(verifyToken));
		//data.setVariable("user_name", "member");
		data.setVariable("reset_email_en", herf);
		backEndServiceDelegate.sendMail( emailTemp, "CreateAccountEmail", "Welcome to Shoobilee!", "member",
				data);

		model.put("status", YamiConstant.STATUS_OK);
		model.put("token", newToken);
		return model;
	}
	
	public Map<String, Object> registerUser_verifyEmail(String token, String pwd) throws Exception {
		String newSalt = "";
		String invitation_code = "";
		Gson gson = new Gson();
		//Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		VerifyToken verifyToken = new VerifyToken();
		try {
			verifyToken = gson.fromJson(StringUtil.decode(pwd), verifyToken.getClass());

		} catch (Exception e) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1006, ErrorCodeEnum.ER1006.getMsg());
		}
		int uid = verifyToken.getUid();
		String salt = verifyToken.getSalt();
		String code = verifyToken.getCode();
		String check = StringUtil.EncoderByMd5(salt, String.valueOf(uid), YamiConstant.ENCTIMES);
		Map<String, Object> model = new HashMap<String, Object>();
		Users user = userService.selectUsersByID(uid);
		//salt = user.getSalt();
		//String password = user.getPassword();
		if(user.getIsValidated()==1){
			throw new YamiException(YamiConstant.ERRORCODE_ER1341, ErrorCodeEnum.ER1341.getMsg());
		}
		if (!check.equals(code)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1006, ErrorCodeEnum.ER1006.getMsg());
		}
		//Random random = new Random();
/*		do {
			newSalt = String.valueOf(random.nextInt(YamiConstant.RANDOM_SALT_RANGE));
		} while (newSalt.equals(salt));*/
		//password = StringUtil.EncoderByMd5(newSalt, pwd, YamiConstant.ENCTIMES);
		//user.setSalt(newSalt);
		//user.setPassword(password);
		user.setLastLogin(Integer.parseInt(DateUtil.getNowLong().toString()));
		Integer user_type = user.getUserType();
		switch (user_type) {
		case 21:
			user.setUserType(31);
			break;
		case 22:
			user.setUserType(32);
			break;
		case 23:
			user.setUserType(33);
			break;
		case 0:
		default:
			user.setUserType(1);
		}

		// transactionDelegate.transactionLogin(user,tokenIn);
		userService.verifyEmail(user);
		Context data = new Context();

		data.setVariable("user_name", user.getFirstName());
		backEndServiceDelegate.sendMail( user.getEmail(), "email-verified", "Welcome to your dream shoe closet!", user.getFirstName(),
				data);
		String auth = StringUtil.EncoderByMd5(newSalt, String.valueOf(uid), YamiConstant.ENCTIMES);
		String newToken = securityServiceDelegate.getToken(uid, newSalt, auth);
		model.put("status", YamiConstant.STATUS_OK);
		model.put("token", newToken);
		return model;
	}

	public Map<String, Object> loginUser(String token, String email, String pwd) throws Exception {

		if (!StringUtil.checkEmail(email)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1601, ErrorCodeEnum.ER1601.getMsg());
		}
		if (!StringUtil.checkMoneLength(pwd, 6)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1602, ErrorCodeEnum.ER1602.getMsg());
		}

		String newSalt = null;
		Map<String, Object> model = new HashMap<String, Object>();
		Gson gson = new Gson();

		// 1.get ecSalt and password(MD5) from DB.
		Users user = userService.getPasswordSalt(email);

		// logger.info("token="+token+";email="+email+";pwd="+pwd);
		if (null == user) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1301, ErrorCodeEnum.ER1301.getMsg());
		}
		String salt = user.getSalt();

		// 2.calculate the password(MD5) with password(parameter) and
		// ecSalt(DB).
		String password = StringUtil.EncoderByMd5(salt, pwd, YamiConstant.ENCTIMES);
		int uid = user.getUserId();

		// logger.info("password(MD5)="+password);

		// 3.compare password(MD5).
		if (!password.equals(user.getPassword())) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1301, ErrorCodeEnum.ER1301.getMsg());
		}

		// 4.calculate the NEW password(MD5) with password(parameter) and NEW
		// ecSalt(random).
		Random random = new Random();
		do {
			newSalt = String.valueOf(random.nextInt(YamiConstant.RANDOM_SALT_RANGE));
		} while (newSalt.equals(salt));
		password = StringUtil.EncoderByMd5(newSalt, pwd, YamiConstant.ENCTIMES);

		// logger.info("salt="+salt);
		// logger.info("new salt="+ newSalt);
		// logger.info("new password="+password);
		user.setSalt(newSalt);
		user.setPassword(password);
		user.setEmail(email);
		user.setLastLogin(Integer.parseInt(DateUtil.getNowLong().toString()));

		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);

		String auth = StringUtil.EncoderByMd5(newSalt, String.valueOf(uid), YamiConstant.ENCTIMES);
		// 5.return userId, userName and new token.
		String newToken = securityServiceDelegate.getToken(uid, newSalt, auth);
		model.put("uid", uid);
		model.put("name", user.getUserName());
		model.put("token", newToken);

		// logger.info("retrun model="+model);
		// 6.update new password(MD5) and ecSalt to DB.
		// 7.update the userId of cart and clear tempId.
		transactionDelegate.transactionLogin(user, tokenIn);

		return model;

	}

	public Map<String, Object> logoutUser(String token) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();
		String tempid = securityServiceDelegate.getTempId();
		result = securityServiceDelegate.getToken4Tempid(tempid);
		return result;

	}

	public Map<String, Object> forgetPassword(String token, String email) throws Exception {
		EmailToken emailToken = new EmailToken();
		Map<String, Object> model = new HashMap<String, Object>();
		Gson gson = new Gson();
		Users user = userService.getPasswordSalt(email);
		if (user == null) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1307, ErrorCodeEnum.ER1307.getMsg());
		}
		String uname = user.getFirstName();
		Integer user_id = user.getUserId();
		int timeout = 3600 * USER_EMAIL_EXP;
		long exp = DateUtil.timeFormat(DateUtil.getNowDateTimeAllString()) + timeout;
		String hash = StringUtil.EncoderByMd5(user.getSalt(), String.valueOf(user_id));
		emailToken.setUid(user_id);
		emailToken.setExp(exp);
		emailToken.setHash(hash);
		String link_en = USER_RESET_LINK_EN + "&code=" + StringUtil.encode(gson.toJson(emailToken));
		// transactionDelegate.transactionResetPassword(email,user_id,uname,link_cn,link_en);
		Context data = new Context();
		data.setVariable("user_name", uname);
		data.setVariable("reset_email_en", link_en);
		backEndServiceDelegate.sendMail(email, "email-reset-pw", "Reset Your Password", uname, data);
		// userService.setPasswordSaltByUid(user);
		model.put("token", token);
		model.put("email", email);
		return model;
	}

	public Map<String, Object> forgetEmail(String token, String first_name, String last_name,
			String mobile_phone) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String email = userService.selectEmailByName(first_name, last_name, mobile_phone);
		if (email == null) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1335, ErrorCodeEnum.ER1335.getMsg());
		}
		model.put("token", token);
		model.put("email", email);
		return model;
	}

	public Map<String, Object> resetPassword(String token, String pwd, String code)
			throws Exception {

		Gson gson = new Gson();
		Random random = new Random();
		EmailToken emailToken = new EmailToken();
		Integer user_id;
		try {
			emailToken = gson.fromJson(StringUtil.decode(code), emailToken.getClass());

		} catch (Exception e) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1305, ErrorCodeEnum.ER1305.getMsg());
		}
		user_id = emailToken.getUid();
		String salt = userService.selectUsersByID(user_id).getSalt();
		long exp = emailToken.getExp();
		String hash = emailToken.getHash();
		if (!hash.equals(StringUtil.EncoderByMd5(salt, String.valueOf(user_id)))) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1305, ErrorCodeEnum.ER1305.getMsg());
		}
		if (exp < DateUtil.timeFormat(DateUtil.getNowDateTimeAllString())) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1306, ErrorCodeEnum.ER1306.getMsg());
		}
		String newSalt = String.valueOf(random.nextInt(YamiConstant.RANDOM_SALT_RANGE));
		String password = StringUtil.EncoderByMd5(newSalt, pwd, YamiConstant.ENCTIMES);

		// logger.info("new salt="+ newSalt);
		// logger.info("new password="+password);
		Users user = new Users();
		user.setSalt(newSalt);
		user.setPassword(password);
		user.setUserId(user_id);

		// transactionDelegate.transactionResetPassword(user);
		userService.setPasswordSaltByUid(user);
		String tempid = securityServiceDelegate.getTempId();
		Map<String, Object> result = securityServiceDelegate.getToken4Tempid(tempid);
		return result;
	}

	public Map<String, Object> changePassword(String token, String oldpwd, String newpwd)
			throws Exception {

		String newSalt = null;
		Map<String, Object> model = new HashMap<String, Object>();
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int uid = Integer.parseInt(tokenIn.getData());

		// 1.get ecSalt and password(MD5) from DB.
		Users user = userService.selectUsersByID(uid);

		// logger.info("token="+token+";email="+email+";pwd="+pwd);
		if (null == user) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1301, ErrorCodeEnum.ER1301.getMsg());
		}
		String salt = user.getSalt();

		// 2.calculate the password(MD5) with password(parameter) and
		// ecSalt(DB).
		String password = StringUtil.EncoderByMd5(salt, oldpwd, YamiConstant.ENCTIMES);

		// logger.info("password(MD5)="+password);

		// 3.compare password(MD5).
		if (!password.equals(user.getPassword())) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1301, ErrorCodeEnum.ER1301.getMsg());
		}

		// 4.calculate the NEW password(MD5) with password(parameter) and NEW
		// ecSalt(random).
		Random random = new Random();
		do {
			newSalt = String.valueOf(random.nextInt(YamiConstant.RANDOM_SALT_RANGE));
		} while (newSalt.equals(salt));
		password = StringUtil.EncoderByMd5(newSalt, newpwd, YamiConstant.ENCTIMES);

		// logger.info("salt="+salt);
		// logger.info("new salt="+ newSalt);
		// logger.info("new password="+password);
		user.setSalt(newSalt);
		user.setPassword(password);
		user.setEmail(user.getEmail());
		user.setLastLogin(Integer.parseInt(DateUtil.getNowLong().toString()));

		String auth = StringUtil.EncoderByMd5(newSalt, String.valueOf(uid), YamiConstant.ENCTIMES);
		// 5.return userId, userName and new token.
		String newToken = securityServiceDelegate.getToken(uid, newSalt, auth);
		model.put("uid", uid);
		model.put("name", user.getUserName());
		model.put("token", newToken);

		// logger.info("retrun model="+model);
		// 6.update new password(MD5) and ecSalt to DB.
		// 7.update the userId of cart and clear tempId.
		transactionDelegate.transactionLogin(user, tokenIn);

		return model;
	}

	public Map<String, Object> getAddressBook(String token, String address_type) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> model = new HashMap<String, Object>();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		Integer addressType = null == address_type ? 9 : Integer.valueOf(address_type);
		List<UserAddress> address = userService.getAddressBookByType(
				Integer.valueOf(tokenIn.getData()), addressType);
		model.put("addrList", address);
		model.put("token", token);
		return model;

	}

	public Map<String, Object> subscribeEmail(String token) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> model = new HashMap<String, Object>();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		transactionDelegate.transactionSubscribe(Integer.valueOf(tokenIn.getData()), 1);

		model.put("status", YamiConstant.STATUS_OK);
		model.put("token", token);
		return model;

	}
	
	public Map<String, Object> unSubscribeEmail(String token) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> model = new HashMap<String, Object>();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		transactionDelegate.transactionSubscribe(Integer.valueOf(tokenIn.getData()), 0);

		model.put("status", YamiConstant.STATUS_OK);
		model.put("token", token);
		return model;

	}
	
	public Map<String, Object> newAddress(String token, String address_type, String first_name,
			String last_name, String address1, String address2, String city, String state,
			String zipcode, String phone) throws Exception {

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
		Gson gson = new Gson();
		Map<String, Object> model = new HashMap<String, Object>();
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
		address.setAddress_type(Integer.valueOf(address_type));

		// ransactionDelegate.transactionNewAddress(address);
		userService.deleteAddressByUid(userId, Integer.valueOf(address_type));
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
		return model;

	}

	public Map<String, Object> editAddress(String token, String address_id, String first_name,
			String last_name, String address1, String address2, String city, String state,
			String zipcode, String phone, String profile_id) throws Exception {

		/*
		 * if(null==first_name||first_name.equals("")){ throw new
		 * YamiException(YamiConstant
		 * .ERRORCODE_ER1607,ErrorCodeEnum.ER1607.getMsg()); }
		 * if(null==last_name||last_name.trim().equals("")){ throw new
		 * YamiException
		 * (YamiConstant.ERRORCODE_ER1606,ErrorCodeEnum.ER1606.getMsg()); }
		 */
		if (null == address1 || address1.equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1608, ErrorCodeEnum.ER1608.getMsg());
		}
		if (null == city || city.equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1609, ErrorCodeEnum.ER1609.getMsg());
		}
		if (null == zipcode || !StringUtil.checkZipcode(zipcode)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1610, ErrorCodeEnum.ER1610.getMsg());
		}
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
		 * if(StringUtil.filterChinese
		 * (address1).length()!=0||StringUtil.filterChinese
		 * (address2).length()!=0){ throw new
		 * YamiException(YamiConstant.ERRORCODE_ER1615
		 * ,ErrorCodeEnum.ER1615.getMsg()); }
		 */
		Gson gson = new Gson();
		Map<String, Object> model = new HashMap<String, Object>();

		// Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		UserAddress address = new UserAddress();
		address.setAddress_id(Integer.valueOf(address_id));
		address.setAddress(address1);
		address.setAddress2(address2);
		address.setCity(city);
		address.setFirst_name(first_name);
		address.setLast_name(last_name);
		address.setCountry(YamiConstant.COUNTRY_UNITEDSTATES);
		address.setState(state);
		address.setPhone(null == phone ? null : phone);
		address.setZipcode(zipcode);

		// transactionDelegate.transactionEditAddress(address,profile_id);
		userService.updateAddressByPK(address);

		model.put("status", YamiConstant.STATUS_OK);
		model.put("first_name", address.getFirst_name());
		model.put("last_name", address.getLast_name());
		model.put("address1", address.getAddress());
		model.put("address2", address.getAddress2());
		model.put("city", address.getCity());
		model.put("state", address.getState());
		model.put("zipcode", address.getZipcode());
		model.put("phone", address.getPhone());
		model.put("token", token);
		return model;

	}

	public Map<String, Object> deleteAddress(String token, int address_id) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		UserAddress userAddress = userService.getAddressBookByAddId(address_id);
		if (userAddress == null) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1321, ErrorCodeEnum.ER1321.getMsg());
		}
		/*
		 * Gson gson = new Gson(); Token tokenIn =
		 * gson.fromJson(StringUtil.decode(token), Token.class); Integer uid =
		 * Integer.valueOf(tokenIn.getData());
		 */
		// transactionDelegate.transactionDeleteAddress(address_id,uid,userAddress.getIs_primary(),null);
		userService.deleteAddressByPK(address_id);
		model.put("status", YamiConstant.STATUS_OK);
		model.put("token", token);
		return model;

	}

	public Map<String, Object> getPayment(String token) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		UserProfile profile = userService.getProfileByUid(Integer.valueOf(tokenIn.getData()));
		Stripe.apiKey = STRIPE_API_KEY;

		try {
			Customer customer = Customer.retrieve(profile.getProfile_id());
			ExternalAccount account = customer.getSources().getData().get(0);
			Card card = (Card) account;
			profile.setAddress_city(card.getAddressCity());
			profile.setAddress_country(card.getAddressCountry());
			profile.setAddress_line1(card.getAddressLine1());
			profile.setAddress_line2(card.getAddressLine2());
			profile.setAddress_state(card.getAddressState());
			profile.setAddress_zip(card.getAddressZip());
			profile.setExp_month(card.getExpMonth().toString());
			profile.setExp_year(card.getExpYear().toString());
			logger.info("Customer:" + JSONFormatter.toJSON(customer));

		} catch (CardException e) {
			// Since it's a decline, CardException will be caught
			logger.error("Status is: " + e.getCode());
			logger.error("Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1336, ErrorCodeEnum.ER1336.getMsg());
		} catch (RateLimitException e) {
			// Too many requests made to the API too quickly
			logger.error("Too many requests made to the API too quickly. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1336, ErrorCodeEnum.ER1336.getMsg());
		} catch (InvalidRequestException e) {
			// Invalid parameters were supplied to Stripe's API
			logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1336, ErrorCodeEnum.ER1336.getMsg());
		} catch (AuthenticationException e) {
			// Authentication with Stripe's API failed
			// (maybe you changed API keys recently)
			logger.error("Authentication with Stripe's API failed. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1336, ErrorCodeEnum.ER1336.getMsg());
		} catch (APIConnectionException e) {
			// Network communication with Stripe failed
			logger.error("Network communication with Stripe failed. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1336, ErrorCodeEnum.ER1336.getMsg());
		} catch (StripeException e) {
			// Display a very generic error to the user, and maybe send
			// yourself an email
			logger.error("Stripe Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1336, ErrorCodeEnum.ER1336.getMsg());
		} catch (Exception e) {
			// Something else happened, completely unrelated to Stripe
			logger.error("Completely unrelated to Stripe. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1336, ErrorCodeEnum.ER1336.getMsg());
		}
		model.put("payment", profile);
		model.put("token", token);
		return model;

	}

	public Map<String, Object> newPayment(String token, String name_of_card, String account,
			String exp_year, String exp_month, String cvv, String address_line1,
			String address_line2, String address_city, String address_zip, String address_state,
			String address_country, String phone) throws Exception {
		if (null == name_of_card || name_of_card.trim().equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1607, ErrorCodeEnum.ER1607.getMsg());
		}

		if (null == account || !StringUtil.checkAccount(account.trim())) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1613, ErrorCodeEnum.ER1613.getMsg());
		}

		if (null == address_line1 || address_line1.trim().equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1608, ErrorCodeEnum.ER1608.getMsg());
		}
		if (null == address_city || address_city.trim().equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1609, ErrorCodeEnum.ER1609.getMsg());
		}
		if (null == address_zip || !StringUtil.checkZipcode(address_zip)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1610, ErrorCodeEnum.ER1610.getMsg());
		}

		if (StringUtil.filterChinese(address_line1).length() != 0
				|| StringUtil.filterChinese(address_line2).length() != 0) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1615, ErrorCodeEnum.ER1615.getMsg());
		}
		Map<String, Object> model = new HashMap<String, Object>();
		Gson gson = new Gson();
		UserProfile profile = new UserProfile();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		String cardNum = account.trim();
		Integer uid = Integer.valueOf(tokenIn.getData());
		Boolean addBlacklist = false;
		/*
		 * int countBlackCardNum =
		 * blacklistService.selectCountByBankCard(cardNum); if
		 * (countBlackCardNum != 0) { Integer countBlackUserId =
		 * blacklistService.selectCountByUserId(uid); if (countBlackUserId == 0)
		 * addBlacklist = true; }
		 */
		Integer tail = Integer.valueOf(cardNum.substring(cardNum.length() - 4));
		String cardType = CardType.detect(account).toString();
		if (cardType.equals(YamiConstant.OGWS_CARD_TYPE_UNKN)
				|| cardType.equals(YamiConstant.OGWS_CARD_TYPE_DINE)) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1616, ErrorCodeEnum.ER1616.getMsg());
		}
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
		profile.setUser_id(uid);
		profile.setAddress_line1(address_line1);
		profile.setAddress_line2(address_line2);
		profile.setAddress_city(address_city);
		profile.setAddress_country(address_country);
		profile.setAddress_state(address_state);
		profile.setAddress_zip(address_zip);
		profile.setPhone(phone);
		profile.setCvc(cvv);
		String profile_id = transactionDelegate.transactionNewPayment(profile, account,
				addBlacklist);
		model.put("token", token);
		// model.put("address_id", address_id);
		model.put("name_of_card", name_of_card);
		model.put("profile_id", profile_id);
		model.put("type", cardType);
		model.put("tail", tail);
		model.put("exp_year", exp_year);
		model.put("exp_month", exp_month);
		// logger.info("retrun model=" + model);
		return model;

	}

	public Map<String, Object> editPayment(String token, String profile_id, String name_of_card,
			String account, String exp_year, String exp_month, String cvc, String address_line1,
			String address_line2, String address_city, String address_zip, String address_state,
			String address_country, String phone) throws Exception {

		if (null == name_of_card || name_of_card.equals("")) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1607, ErrorCodeEnum.ER1607.getMsg());
		}

		Map<String, Object> model = new HashMap<String, Object>();
		Gson gson = new Gson();
		if (userService.countProfileByPK(profile_id) == YamiConstant.NO_RECORD) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1312, ErrorCodeEnum.ER1312.getMsg());
		}
		UserProfile profile = new UserProfile();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		String cardNum = String.valueOf(account);
		Integer tail = Integer.valueOf(cardNum.substring(cardNum.length() - 4));
		Integer uid = Integer.valueOf(tokenIn.getData());
		String cardType = CardType.detect(String.valueOf(account)).toString();
		// profile.setAddress_id(address_id);
		// profile.setCard_type(cardType);
		profile.setCard_type(cardType);
		profile.setExp_year(exp_year);
		profile.setExp_month(exp_month);
		profile.setName_of_card(name_of_card);
		profile.setNumber(account);
		// profile.setProfile_id(profileId);
		profile.setTail(tail);
		profile.setUser_id(uid);
		profile.setAddress_line1(address_line1);
		profile.setAddress_line2(address_line2);
		profile.setAddress_city(address_city);
		profile.setAddress_country(address_country);
		profile.setAddress_state(address_state);
		profile.setAddress_zip(address_zip);
		profile.setPhone(phone);
		profile.setCvc(cvc);
		transactionDelegate.transactionEditPayment(profile);
		model.put("token", token);
		// model.put("address_id", address_id);
		model.put("name_of_card", name_of_card);
		model.put("profile_id", String.valueOf(profile_id));
		model.put("exp_year", exp_year);
		model.put("exp_month", exp_month);
		// logger.info("retrun model=" + model);
		return model;

	}

	public Map<String, Object> editPaymentAddress(String token, String profile_id,
			Integer address_id) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();

		if (userService.countProfileByPK(profile_id) == YamiConstant.NO_RECORD) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1312, ErrorCodeEnum.ER1312.getMsg());
		}

		transactionDelegate.transactionEditPaymentAddress(address_id, profile_id);
		model.put("token", token);
		model.put("address_id", address_id);
		model.put("profile_id", String.valueOf(profile_id));
		// logger.info("retrun model=" + model);
		return model;

	}

	public Map<String, Object> deletePayment(String token, String profile_id) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		UserProfile profile = userService.getProfileByPid(profile_id);
		if (profile == null) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1312, ErrorCodeEnum.ER1312.getMsg());
		}
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		Integer uid = Integer.valueOf(tokenIn.getData());
		transactionDelegate.transactionDeletePayment(profile_id, uid);
		model.put("token", token);
		model.put("status", YamiConstant.STATUS_OK);
		// logger.info("retrun model=" + model);
		return model;

	}

	public Map<String, Object> checkPromoCode(String token, String promo_code) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();

		/*
		 * if(!(userService.selectUsersByInviteCode(promo_code)>0)){ throw new
		 * YamiException
		 * (YamiConstant.ERRORCODE_ER1103,ErrorCodeEnum.ER1103.getMsg()); }
		 */
		// Gson gson = new Gson();
		// Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		// Integer uid = Integer.valueOf(tokenIn.getData());
		Users inviteUser = userService.selectUserInfoByInviteCode(promo_code);
		if (null != inviteUser) {
			model.put("code_type", "referral");
			model.put("shoo_coin", StringUtil.getReferPoint());
		} else {
			Coupon coupon = userService.selectCouponByCode(promo_code);
			if (null == coupon) {
				throw new YamiException(YamiConstant.ERRORCODE_ER1339,
						ErrorCodeEnum.ER1339.getMsg());
			} else {
				if (coupon.getStatus() == 0) {
					throw new YamiException(YamiConstant.ERRORCODE_ER1340,
							ErrorCodeEnum.ER1340.getMsg());
				} else {
					model.put("code_type", coupon.getType());
					model.put("coupon", coupon.getCoupon_id());
					model.put("shoo_coin", coupon.getShoo_coin());

				}
			}
		}
		model.put("token", token);
		model.put("status", YamiConstant.STATUS_OK);
		// logger.info("retrun model=" + model);
		return model;

	}

	/*
	 * public Map<String, Object> subscribe(String token,String
	 * preset_type,String profile_id,String cvv,String promo_code) throws
	 * Exception {
	 * 
	 * return transactionDelegate.transactionSubscribe(token, preset_type,
	 * profile_id, cvv,promo_code);
	 * 
	 * }
	 */

	public Map<String, Object> newSubscribe(String token, String preset_type, String profile_id,
			String cvv, String promo_code) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result = transactionDelegate.transactionNewSubscribe(token, preset_type, profile_id, cvv,	
				promo_code);
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int uid = Integer.parseInt(tokenIn.getData());

		// Map<String, Object> model = new HashMap<String, Object>();
		Users user = userService.selectUsersByID(uid);

		// Random random = new Random();
		// String newSalt =
		// String.valueOf(random.nextInt(YamiConstant.RANDOM_SALT_RANGE));
		// String password = StringUtil.EncoderByMd5(newSalt, pwd,
		// YamiConstant.ENCTIMES);
		String password = user.getPassword();

/*		String auth = StringUtil.EncoderByMd5(user.getSalt(), String.valueOf(uid),
				YamiConstant.ENCTIMES);
		String newToken = securityServiceDelegate.getToken(uid, user.getSalt(), auth);*/
		Context data = new Context();
		String herf = USER_VERIFY_LINK + password;
		data.setVariable("user_name", user.getFirstName());
		data.setVariable("reset_email_en", herf);
		backEndServiceDelegate.sendMail( user.getEmail(), "email-subscribe",
				"Congratulations For Subscribing To Shoobilee", user.getUserName(), data);
		return result;

	}
	
	public Map<String, Object> newSubscribeV2(String token, String preset_type,  String promo_code, String first_name,
			String last_name, String address1, String address2, String city, String state,
			String zipcode, String phone, String name_of_card, String account,
			String exp_year, String exp_month, String cvv, String billing_address1,
			String billing_address2, String billing_city, String billing_zip, String billing_state,
			String billing_phone) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result = transactionDelegate.transactionNewSubscribeV2(token, preset_type, promo_code, first_name, last_name,
				address1, address2, city, state, zipcode, phone, name_of_card, account, exp_year, exp_month, cvv,
				billing_address1, billing_address2, billing_city, billing_zip, billing_state, billing_phone);

		return result;

	}
	
	public Map<String, Object> buyShoe(String token, String product_sku, String profile_id,
			String use_card) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<String, Object>();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		Integer uid = Integer.valueOf(tokenIn.getData());
		Shurack shurack = shurackService.selectShurackBySku(uid, 1, product_sku);
		if (shurack == null) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1106, ErrorCodeEnum.ER1106.getMsg());
		}
		Product productTemp = productService.selectBySku(product_sku);
		//Integer quantity = productTemp.getQuantity();
		Double amount = new Double(productTemp.getMember_price().toString());
		Users user = userService.selectUserInfoByUid(uid);
/*		if (quantity <= 0) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1104, ErrorCodeEnum.ER1104.getMsg());
		}*/
		if ("0".equals(use_card) && user.getPayPoints() <= 0) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1105, ErrorCodeEnum.ER1105.getMsg());
		}
		transactionDelegate.transactionBuyShoe(uid, profile_id, product_sku, amount, use_card,
				shurack.getShurack_id());

		result.put("amount", amount);
		if ("1".equals(use_card)) {
			result.put("pay_points", user.getPayPoints());
		} else {
			result.put("pay_points", user.getPayPoints() - 1);
		}
		result.put("product_sku", product_sku);
		result.put("product_name", productTemp.getProduct_name());
		result.put("image_url", productTemp.getImage_url());
		result.put("token", token);
		
		//Product buyProduct = productService.selectBySku(product_sku);
		//List<Shurack> shurackList = shurackService.selectShipShurack(uid, 1);
		Date now = new Date();
		List<Holiday> holidays = backEndService.getHolidays();
		String so_date = DateUtil.getDateAfterWorkingDays(now.getTime(), BUY_PERIOD, holidays);
		Context data = new Context();
		data.setVariable("so_date", so_date);	
		data.setVariable("user_name", user.getFirstName());		
/*		if (shurackList != null&&shurackList.size()>0){
			Product product = productService.selectBySku(shurackList.get(0).getProduct_sku());
			data.setVariable("image_url", "https://shoobilee.com"+product.getImage_url());	
			data.setVariable("product_name", product.getStyle_name());	
			data.setVariable("product_sku", "https://www.shoobilee.com/product.php?sku="+product.getProduct_sku());	
		}else{
			data.setVariable("image_url", "");	
			data.setVariable("product_name", "");	
			data.setVariable("product_sku", "");
			
		}*/
		data.setVariable("image_url", "https://shoobilee.com"+productTemp.getImage_url());	
		data.setVariable("product_name", productTemp.getProduct_name());	
		backEndServiceDelegate.sendMail( user.getEmail(),"email-purchase-shoes", "It’s yours forever!",user.getFirstName(),data);
		
		return result;

	}

	public Map<String, Object> donateShoe(String token, String code) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();
		logger.fatal("testfatal");
/*		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<String, Object>();
		DonateCode codeIn = gson.fromJson(StringUtil.decode(code), DonateCode.class);
		Integer uid = codeIn.getData();
		String product_sku = codeIn.getProduct();
		Integer shurack_id = codeIn.getShurack();
		if (shurackService.countShurackBySku(uid, 1, product_sku) <= 0) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1107, ErrorCodeEnum.ER1107.getMsg());
		}
		if (codeIn.getExp() < DateUtil.timeFormat(DateUtil.getNowDateTimeAllString())) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1108, ErrorCodeEnum.ER1108.getMsg());
		}
		transactionDelegate.transactionDonateShoe(shurack_id);

		result.put("token", token);
		result.put("status", YamiConstant.STATUS_OK);*/
		return result;

	}

	public Map<String, Object> makeDonateLink(String token, Integer user_id, String product_sku,
			Integer shurack_id) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<String, Object>();
		DonateCode codeIn = new DonateCode();
		// Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		long timeout = 3600 * 24 * 7 * 6;
		long exp = DateUtil.timeFormat(DateUtil.getNowDateTimeAllString()) + timeout;
		codeIn.setData(user_id);
		codeIn.setProduct(product_sku);
		codeIn.setShurack(shurack_id);
		codeIn.setExp(exp);

		String code = StringUtil.encode(gson.toJson(codeIn));
		String donate_link = DONATE_LINK + code;

		result.put("token", token);
		result.put("donate_link", donate_link);
		return result;

	}

	public Map<String, Object> changePlan(String token, String preset_type, String sub_order_id)
			throws Exception {

		return transactionDelegate.transactionChangePlan(token, preset_type, sub_order_id);

	}

	public Map<String, Object> getSubscription(String token) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<String, Object>();
		// DonateCode codeIn = new DonateCode();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		UserProfile profile = userService.getProfileByUid(Integer.valueOf(tokenIn.getData()));
		Stripe.apiKey = STRIPE_API_KEY;
		SubscriptionCollection subscriptions = new SubscriptionCollection();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("customer", profile.getProfile_id());

		try {
			subscriptions = Subscription.list(params);
			logger.info(" authResponse: " + JSONFormatter.toJSON(subscriptions));
		} catch (CardException e) {
			// Since it's a decline, CardException will be caught
			logger.error("Status is: " + e.getCode());
			logger.error("Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1333, ErrorCodeEnum.ER1333.getMsg());
		} catch (RateLimitException e) {
			// Too many requests made to the API too quickly
			logger.error("Too many requests made to the API too quickly. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (InvalidRequestException e) {
			// Invalid parameters were supplied to Stripe's API
			logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (AuthenticationException e) {
			// Authentication with Stripe's API failed
			// (maybe you changed API keys recently)
			logger.error("Authentication with Stripe's API failed. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (APIConnectionException e) {
			// Network communication with Stripe failed
			logger.error("Network communication with Stripe failed. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (StripeException e) {
			// Display a very generic error to the user, and maybe send
			// yourself an email
			logger.error("Stripe Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (Exception e) {
			// Something else happened, completely unrelated to Stripe
			logger.error("Completely unrelated to Stripe. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		}
		result.put("token", token);
		result.put("subscriptions", subscriptions);
		return result;

	}

	public Map<String, Object> getPaymentHistory(String token) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<String, Object>();
		// DonateCode codeIn = new DonateCode();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		UserProfile profile = userService.getProfileByUid(Integer.valueOf(tokenIn.getData()));
		//UserProfile profile = userService.getProfileByUid(168);
		Stripe.apiKey = STRIPE_API_KEY;
		
		ChargeCollection charges = new ChargeCollection();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("customer", profile.getProfile_id());
		List<PaymentHistory> paymentHistoryList = new ArrayList<PaymentHistory>();
		
		try {
			charges = Charge.list(params);
			//logger.info(" authResponse: " + JSONFormatter.toJSON(charges));
			for(int i=0;i<charges.getData().size();i++){
				PaymentHistory paymentHistory = new PaymentHistory();
				paymentHistory.setDate(DateUtil.formateUTC(charges.getData().get(i).getCreated()));
				String description = charges.getData().get(i).getDescription();
				if(description!=null && description.indexOf("order id")!=-1){
					String so_id = description.substring(9);
					String purchaseName = productService.getPurchaseName(Integer.valueOf(so_id));
					paymentHistory.setPurchase(purchaseName);
				}else{
					paymentHistory.setPurchase("Monthly Subscription");
				}
				paymentHistory.setAmount(charges.getData().get(i).getAmount().doubleValue()/100);
				paymentHistoryList.add(paymentHistory);
			}
		} catch (CardException e) {
			// Since it's a decline, CardException will be caught
			logger.error("Status is: " + e.getCode());
			logger.error("Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1333, ErrorCodeEnum.ER1333.getMsg());
		} catch (RateLimitException e) {
			// Too many requests made to the API too quickly
			logger.error("Too many requests made to the API too quickly. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (InvalidRequestException e) {
			// Invalid parameters were supplied to Stripe's API
			logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (AuthenticationException e) {
			// Authentication with Stripe's API failed
			// (maybe you changed API keys recently)
			logger.error("Authentication with Stripe's API failed. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (APIConnectionException e) {
			// Network communication with Stripe failed
			logger.error("Network communication with Stripe failed. Error Message is: "
					+ e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (StripeException e) {
			// Display a very generic error to the user, and maybe send
			// yourself an email
			logger.error("Stripe Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (Exception e) {
			// Something else happened, completely unrelated to Stripe
			logger.error("Completely unrelated to Stripe. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		}
		result.put("token", token);
		result.put("paymentHistoryList", paymentHistoryList);
		return result;

	}
	
	public Map<String, Object> unSubscribe(String token, String unsub_type, String unsub_msg,
			String sub_order_id) throws Exception {

		Map<String, Object>	result = transactionDelegate.transactionUnSubscribe(token, unsub_type, unsub_msg,
				sub_order_id);
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		Integer uid = Integer.valueOf(tokenIn.getData());
		Users user = userService.selectUserInfoByUid(uid);
		Long currentPeriodEnd = (Long)result.get("currentPeriodEnd");
		Context data = new Context();
		data.setVariable("user_name", user.getFirstName());
		data.setVariable("billing_cycle_date", DateUtil.formateUTC2(currentPeriodEnd));
		backEndServiceDelegate.sendMail( user.getEmail(),"email-shoes-cancel", "You are now unsubscribed",user.getFirstName(),data);
		backEndServiceDelegate.sendMail( "happytohelp@shoobilee.com","email-shoes-cancel", "User:"+uid+" unsubscribed",user.getFirstName(),data);
		return result;
	}

	@Test
	public void testDate() throws Exception {
		Integer start = 1470953566;
		Integer end = 1471299166;
		ArrayList<Holiday> list = new ArrayList();
		Holiday holiday = new Holiday();
		holiday.setMonth(8);
		holiday.setDay(12);
		list.add(holiday);
		Date now = new Date();
		int days = DateUtil.getWorkingDaysBetweenTwoDates(start, end, list);
		String mm = DateUtil.getDateAfterWorkingDays(now.getTime()/1000, 2, list);
		String m1 = DateUtil.formateUTC2(1474869715);
		System.out.println("days=" + m1);
	}
}
