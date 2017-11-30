package com.cosmetic.delegate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.cosmetic.entity.DonateCode;
import com.cosmetic.entity.EmailToken;
import com.cosmetic.entity.Holiday;
import com.cosmetic.entity.OrderInfo;
import com.cosmetic.entity.Product;
import com.cosmetic.entity.Profile;
import com.cosmetic.entity.Sendmail;
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
import com.cosmetic.service.UnSubOrderService;
import com.cosmetic.service.UserService;
import com.google.gson.Gson;
import com.stripe.model.Subscription;

@Service
public class BackEndServiceDelegate {

	@Autowired
	private UserService userService;
	@Autowired
	private ShurackService shurackService;
	@Autowired
	private PaymentServiceDelegate paymentServiceDelegate;
	@Autowired
	private SubOrderService subOrderService;
	@Autowired
	private UnSubOrderService unSubOrderService;
	@Autowired
	private OrderInfoService orderInfoService;
	@Autowired
	private ProductService productService;
	@Autowired
	private BackEndService backEndService;

	@Autowired
	private SecurityServiceDelegate securityServiceDelegate;

	@Autowired
	private TransactionDelegate transactionDelegate;

	@Autowired
	private UserServiceDelegate userServiceDelegate;

	@Autowired
	private TemplateEngine templateEngine;

	@Value("${USER_RESET_LINK_CN}")
	private String USER_RESET_LINK_CN;
	@Value("${USER_RESET_LINK_EN}")
	private String USER_RESET_LINK_EN;
	@Value("${USER_EMAIL_EXP}")
	private Integer USER_EMAIL_EXP;
	@Value("${USER_VERIFY_LINK}")
	private String USER_VERIFY_LINK;
	@Value("${BUY_PERIOD}")
	private Integer BUY_PERIOD;
	@Value("${DONATE_PERIOD}")
	private Integer DONATE_PERIOD;
	@Value("${OVERDUE_PERIOD}")
	private Integer OVERDUE_PERIOD;

	// @Autowired
	// private LogUtil logger;
	private Logger logger = LogManager.getLogger(this.getClass().getName());

	public Map<String, Object> shurackDailyProcess(String token) throws Exception {
		logger.info("shurackDailyProcess start...");
		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<String, Object>();
		subOrderDailyProcess(token);
		
		List<Shurack> shurackList = shurackService.selectBackendShurack();
		List<Holiday> holidays = backEndService.getHolidays();
		long now = DateUtil.timeFormat(DateUtil.getNowDateTimeAllString());
		for (int i = 0; i < shurackList.size(); i++) {
			Shurack shurack = shurackList.get(i);
			if (shurack.getStatus() == 21) {
				if (DateUtil.getWorkingDaysBetweenTwoDates(shurack.getDonate_time(), now, holidays) >= DONATE_PERIOD) {
					try {
						transactionDelegate.transactionShurackShipNext(shurack.getShurack_id(), 2);
					} catch (Exception e) {
						logger.fatal("Failed to Process Product:(" + shurack.getProduct_sku()
								+ ") user:(" + shurack.getUser_id() + ")");
					}
				}
			} else if (shurack.getStatus() == 31) {
				if (DateUtil.getWorkingDaysBetweenTwoDates(shurack.getBuy_time(), now, holidays) >= BUY_PERIOD) {
					try {
						transactionDelegate.transactionShurackShipNext(shurack.getShurack_id(), 3);
					} catch (Exception e) {
						logger.fatal("Failed to Process Product:(" + shurack.getProduct_sku()
								+ ") user:(" + shurack.getUser_id() + ")");
					}
				}
			} else if (shurack.getStatus() == 1) {
				if (DateUtil.getWorkingDaysBetweenTwoDates(shurack.getShip_time(), now, holidays) >= OVERDUE_PERIOD) {
					try {
						transactionDelegate.transactionShurackOverdue(shurack.getShurack_id());
						OrderInfo updateOrder = orderInfoService.selectOrderInfo(
								shurack.getUser_id(), shurack.getShurack_id(),
								shurack.getProduct_sku());
						updateOrder.setPay_status(3);
						updateOrder.setPay_time(Integer.valueOf(DateUtil.getNowLong().toString()));
						updateOrder.setUse_card(0);
						orderInfoService.updateOrderInfo(updateOrder);
						
						//List<Shurack> shurackListNext = shurackService.selectShipShurack(shurack.getUser_id(), 1);
						Users user = userService.selectUserInfoByUid(shurack.getUser_id());
						Product product = productService.selectBySku(shurack.getProduct_sku());
						Context data = new Context();
						data.setVariable("user_name", user.getFirstName());	
						data.setVariable("image_url", "https://shoobilee.com"+product.getImage_url());	
						data.setVariable("product_name", product.getProduct_name());		
/*						if (shurackListNext != null){
							Product product = productService.selectBySku(shurackListNext.get(0).getProduct_sku());
							data.setVariable("image_url", "https://shoobilee.com"+product.getImage_url());	
							data.setVariable("product_name", product.getProduct_name());	
							//data.setVariable("product_sku", "https://www.shoobilee.com/product.php?sku="+product.getProduct_sku());	
						}else{
							data.setVariable("image_url", "");	
							data.setVariable("product_name", "");	
							data.setVariable("product_sku", "");
							
						}*/
						//data.setVariable("buy_name", shurack.getProduct_name());	
						sendMail( user.getEmail(),"email-shoes-overdue", "It’s on us!",user.getFirstName(),data);
					} catch (Exception e) {
						logger.fatal("Failed to Process Product:(" + shurack.getProduct_sku()
								+ ") user:(" + shurack.getUser_id() + ")");
					}
				}
			}
		}
		shipDailyProcess(token);
		shurackService.updateShurack();
		
		result.put("status", YamiConstant.STATUS_OK);
		//result.put("token", token);
		logger.info("shurackDailyProcess end...");
		return result;

	}

	public Map<String, Object> subOrderDailyProcess(String token) throws Exception {
		logger.info("subOrderDailyProcess start...");
		Map<String, Object> result = new HashMap<String, Object>();
		List<SubOrder> subOrderList = subOrderService.selectDailySubOrder();

		for (SubOrder subOrder : subOrderList) {
			try {
				updateSubOrderInfo(subOrder.getUser_id(), subOrder.getSub_order_id());
			} catch (Exception e) {
				logger.fatal("Daily update subscription failed for sub order id:"
						+ subOrder.getSub_order_id() + " error:" + e.getMessage());
			}
		}
		logger.info("subOrderDailyProcess end...");
		return result;
	}

	public Map<String, Object> updateSubOrderInfo(Integer user_id, String sub_order_id)
			throws Exception {
		logger.info("updateSubOrderInfo start...");
		Map<String, Object> result = new HashMap<String, Object>();
		Integer userType = 21;

		Users user = userService.selectUserInfoByUid(user_id);
		try {
			Subscription subscription = paymentServiceDelegate.getSubscribe(sub_order_id);
/*			if(user.getRankPoints()>0&&!"free-onetime".equals(subscription.getDiscount().getCoupon().getId())){
				paymentServiceDelegate.updateFreeCoupon(sub_order_id, user_id,subscription);
			}*/
			if (user.getUserType() == 21 || user.getUserType() == 22 || user.getUserType() == 23) {
				switch (subscription.getPlan().getId()) {
				case YamiConstant.ONE_PAIR_PLAN:
					userType = 21;
					break;
				case "2PAIRS":
					userType = 22;
					break;
				case "3PAIRS":
					userType = 23;
					break;
				default:
					userType = 21;
					logger.fatal("Daily update subscription failed get plan id:"
							+ subscription.getPlan().getId());
				}
			} else {
				switch (subscription.getPlan().getId()) {
				case YamiConstant.ONE_PAIR_PLAN:
					userType = 31;
					break;
				case "2PAIRS":
					userType = 32;
					break;
				case "3PAIRS":
					userType = 33;
					break;
				default:
					userType = 31;
					logger.fatal("Daily update subscription failed get plan id:"
							+ subscription.getPlan().getId());
				}
			}
			if(subscription.getStatus().equals("canceled")){
				userType=1;
				if(null!=unSubOrderService.selectUnSubOrderById(sub_order_id)){
					Context data = new Context();
					data.setVariable("user_name", user.getFirstName());
					sendMail( user.getEmail(), "email-suspended", "Your account has been suspended",
							user.getFirstName(), data);
				}
			}
			SubOrder updateOrder = new SubOrder();
			updateOrder.setAmount(new BigDecimal(subscription.getPlan().getAmount())
					.divide(new BigDecimal(100)));
			updateOrder.setCancel_at_period_end(subscription.getCancelAtPeriodEnd().toString());
			updateOrder.setCanceled_at(subscription.getCanceledAt() == null ? 0 : Integer
					.valueOf(subscription.getCanceledAt().toString()));
			updateOrder.setCreated(Integer.valueOf(subscription.getCreated().toString()));
			updateOrder.setCurrent_period_end(Integer.valueOf(subscription.getCurrentPeriodEnd()
					.toString()));
			updateOrder.setCurrent_period_start(Integer.valueOf(subscription
					.getCurrentPeriodStart().toString()));
			updateOrder.setCustomer(subscription.getCustomer());
			updateOrder.setEnded_at(subscription.getEndedAt() == null ? 0 : Integer
					.valueOf(subscription.getEndedAt().toString()));
			updateOrder.setPlan_id(subscription.getPlan().getId());
			updateOrder.setStart(Integer.valueOf(subscription.getStart().toString()));
			updateOrder.setStatus(subscription.getStatus());
			updateOrder.setSub_order_id(subscription.getId());
			updateOrder.setSub_type(userType);
			updateOrder.setUser_id(user_id);
			subOrderService.updateSubOrderById(updateOrder);
			userService.updateUserTypeByUid(user_id, userType, userType);
			if(subscription.getStatus().equals("past_due")||subscription.getStatus().equals("unpaid")){
				Context data = new Context();
				data.setVariable("user_name", user.getFirstName());
				sendMail( user.getEmail(), "email-shoes-payment-fail", "Oops, looks like something went wrong",
						user.getFirstName(), data);
			}
		} catch (Exception e) {
			logger.fatal("Daily update subscription failed for sub order id:" + sub_order_id
					+ " error:" + e.getMessage());
		}
		result.put("status", YamiConstant.STATUS_OK);
		logger.info("updateSubOrderInfo end...");
		return result;
	}

	public Map<String, Object> shipDailyProcess(String token) throws Exception {
		logger.info("shipDailyProcess start...");
		Map<String, Object> result = new HashMap<String, Object>();
		List<SubOrder> subOrderList = subOrderService.selectShipSubOrder();

		for (SubOrder subOrder : subOrderList) {
			try {
				updateShipInfo(subOrder.getUser_id(), subOrder.getSub_order_id());
			} catch (Exception e) {
				logger.fatal("Daily update subscription failed for sub order id:"
						+ subOrder.getSub_order_id() + " error:" + e.getMessage());
			}
		}
		logger.info("shipDailyProcess end...");
		return result;
	}

	public Map<String, Object> updateShipInfo(Integer user_id, String sub_order_id)
			throws Exception {
		logger.info("updateShipInfo start...");
		Map<String, Object> result = new HashMap<String, Object>();
		Users user = userService.selectUserInfoByUid(user_id);
		if (user.getUserType() != 31 && user.getUserType() != 32 && user.getUserType() != 33) {
			logger.error("User is not eligible for ship next shoes. user_type="
					+ user.getUserType() + " sub_order_id=" + sub_order_id);
			return result;
		}
		Integer slot = 0;
		switch (user.getUserType()) {
		case 31:
			slot = 1;
			break;
		case 32:
			slot = 2;
			break;
		case 33:
			slot = 3;
			break;
		default:
			slot = 1;
			logger.error("User type abnormal. User_type=" + user.getUserType() + " sub_order_id="
					+ sub_order_id);
		}

		// count shurack with status =1 or 21 or 31 or 11
		Integer alreadyUse = shurackService.countMyShurackByUserId(user_id);
		Integer needShip = slot - alreadyUse;
		Boolean needMail = false;
		String product_sku = "";
		if (needShip > 0) {
			for (int i = 0; i < needShip; i++) {
				List<Shurack> shurackList = shurackService.selectShipShurack(user_id, 1);
				if (shurackList != null && shurackList.size() != 0) {
					if(needMail){
						Product shipProduct = productService.selectBySku(product_sku);

						//Users user = userService.selectUserInfoByUid(shurackList.get(0).getUser_id());
						Context data = new Context();
						data.setVariable("user_name", user.getFirstName());		

							//Product product = productService.selectBySku(shurackList.get(0).getProduct_sku());
							data.setVariable("image_url", "https://shoobilee.com"+shipProduct.getImage_url());	
							data.setVariable("product_name", shipProduct.getProduct_name());	
							//data.setVariable("product_sku", "https://www.shoobilee.com/product.php?sku="+product.getProduct_sku());	

						//data.setVariable("ship_name", shipProduct.getProduct_name());	
						sendMail( user.getEmail(),"email-shoes-oos", "Uh-oh, it’s out of stock",user.getFirstName(),data);
						needMail = false;
					}
					if (transactionDelegate.transactionShipOrder(shurackList.get(0)) == 1) {
						i--;
						needMail = true;
						product_sku = shurackList.get(0).getProduct_sku();
					}
				} else {
					if(needMail){
						Product shipProduct = productService.selectBySku(product_sku);
						Context data = new Context();
						data.setVariable("image_url", "https://shoobilee.com"+shipProduct.getImage_url());	
						data.setVariable("product_name", shipProduct.getProduct_name());	
						//data.setVariable("product_sku", "");					
						data.setVariable("user_name", user.getFirstName());	
						//data.setVariable("ship_name", shipProduct.getProduct_name());	
						sendMail( user.getEmail(),"email-shoes-oos", "Uh-oh, it’s out of stock",user.getFirstName(),data);
					}
					logger.error("User has no available waiting shoes to ship. user_id=" + user_id);
					break;
				}
			}
		}
		result.put("status", YamiConstant.STATUS_OK);
		logger.info("updateShipInfo end...");
		return result;
	}

	public Map<String, Object> shipShoe(String token, Integer uid, String product_sku,
			Integer shurack_id, Integer so_id) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		// check inventory
		int num = productService.selectQuantityBySku(product_sku);
		Users user = userService.selectUserInfoByUid(uid);
		if (num <= 0) {
			transactionDelegate.transactionNoInventory(shurack_id,so_id);
			logger.error("Can not ship casue no inventory available. Product_sku is: "
					+ product_sku);
			for (int i = 0; i < 1; i++) {
				List<Shurack> shurackList = shurackService.selectShipShurack(uid, 1);
				if (shurackList != null && shurackList.size() != 0) {
					if (transactionDelegate.transactionShipOrder(shurackList.get(0)) == 1) {
						i--;
					}
					Product shipProduct = productService.selectBySku(product_sku);

					//Users user = userService.selectUserInfoByUid(shurackList.get(0).getUser_id());
					Context data = new Context();
					data.setVariable("user_name", user.getFirstName());		

						//Product product = productService.selectBySku(shurackList.get(0).getProduct_sku());
						data.setVariable("image_url", "https://shoobilee.com"+shipProduct.getImage_url());	
						data.setVariable("product_name", shipProduct.getProduct_name());	
						//data.setVariable("product_sku", "https://www.shoobilee.com/product.php?sku="+product.getProduct_sku());	

					//data.setVariable("ship_name", shipProduct.getProduct_name());	
					sendMail( user.getEmail(),"email-shoes-oos", "Uh-oh, it’s out of stock",user.getFirstName(),data);
					product_sku = shurackList.get(0).getProduct_sku();
				} else {
					Product shipProduct = productService.selectBySku(product_sku);
					Context data = new Context();
					data.setVariable("image_url", "https://shoobilee.com"+shipProduct.getImage_url());	
					data.setVariable("product_name", shipProduct.getProduct_name());	
					//data.setVariable("product_sku", "");					
					data.setVariable("user_name", user.getFirstName());	
					//data.setVariable("ship_name", shipProduct.getProduct_name());	
					sendMail( user.getEmail(),"email-shoes-oos", "Uh-oh, it’s out of stock",user.getFirstName(),data);
					logger.error("User has no available waiting shoes to ship. user_id=" + uid);
					throw new YamiException(YamiConstant.ERRORCODE_ER1403,
							ErrorCodeEnum.ER1403.getMsg());
				}
			}
			throw new YamiException(YamiConstant.ERRORCODE_ER1401, ErrorCodeEnum.ER1401.getMsg());
		} else {
			transactionDelegate.transactionShipShoe(shurack_id, product_sku, so_id);

			Product product = productService.selectBySku(product_sku);
			//Users user = userService.selectUserInfoByUid(uid);
			Context data = new Context();
			data.setVariable("user_name", user.getFirstName());
			data.setVariable("image_url", "https://shoobilee.com" + product.getImage_url());
			data.setVariable("product_name", product.getProduct_name());
			//data.setVariable("product_sku",
			//		"https://www.shoobilee.com/product.php?sku=" + product.getProduct_sku());
			sendMail( user.getEmail(), "email-shoes-shipped",
					"Your shoes are on its way!", user.getFirstName(), data);

			result.put("status", YamiConstant.STATUS_OK);
		}
		return result;
	}

	public Map<String, Object> listOrderInfo(String token, Integer user_id, Integer pay_status,
			Integer donate_status, Integer ship_status, Integer page, Integer number)
			throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		// check inventory
		if (user_id == null) {
			user_id = 0;
		}
		if (pay_status == null) {
			pay_status = 99;
		}
		if (donate_status == null) {
			donate_status = 99;
		}
		if (ship_status == null) {
			ship_status = 99;
		}
		if (page == null) {
			page = 1;
		}
		if (number == null) {
			number = 100;
		}
		int index = (page - 1) * number;
		result.put("list", orderInfoService.listOrderInfo(user_id, pay_status, donate_status,
				ship_status, index, number));
		return result;
	}

	public Map<String, Object> sendMail(String toMail, String mailCategory,
			String title, String userName, Context data) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		// Gson gson = new Gson();
		// Token tokenIn = gson.fromJson( StringUtil.decode(token),
		// Token.class);
		// int uid = Integer.parseInt(tokenIn.getData());
		// String email = new String();

		// 获取邀请人信息及邀请码
		// Users users = userService.selectUserInfoByUid(uid);
		String emailContent = "";
		HtmlEmail htmlEmail = new HtmlEmail();
		Integer Status = 1;
		try {

			emailContent = templateEngine.process(mailCategory, data);
			htmlEmail.setHostName("smtp.gmail.com");
			htmlEmail.addTo(toMail, userName);
			htmlEmail.setAuthenticator(new DefaultAuthenticator("shoobilee@shoobilee.com",
					"allyoucanwear"));
			htmlEmail.setFrom("DoNotReply@shoobilee.com", "Shoobilee");
			htmlEmail.setSubject(title);
			// htmlEmail.setSSLOnConnect(true);
			htmlEmail.setStartTLSEnabled(true);
			htmlEmail.setSmtpPort(587);
			// embed the image and get the content id

			htmlEmail.setHtmlMsg(emailContent);

			// send the email
			htmlEmail.send();

		} catch (EmailException e) {
			logger.fatal("Failed to send email");
			logger.fatal(e.toString());
			Status = 0;
		} finally {
			Sendmail sendmail = new Sendmail();
			sendmail.setContent(emailContent);
			sendmail.setMail_category(mailCategory);
			sendmail.setMail_title(title);
			sendmail.setMail_to(toMail);
			sendmail.setSend_time(Integer.valueOf(DateUtil.getNowLong().toString()));
			sendmail.setStatus(Status);
			sendmail.setUser_name(userName);
			backEndService.insertMail(sendmail);
		}

		//result.put("token", token);
		result.put("status", 1);

		return result;
	}

	public Map<String, Object> resendMail(String token, Integer mail_id) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		// Gson gson = new Gson();
		// Token tokenIn = gson.fromJson( StringUtil.decode(token),
		// Token.class);
		// int uid = Integer.parseInt(tokenIn.getData());
		// String email = new String();

		// 获取邀请人信息及邀请码
		// Users users = userService.selectUserInfoByUid(uid);
		// String emailContent = "";
		HtmlEmail htmlEmail = new HtmlEmail();
		Sendmail sendmail = backEndService.selectEmail(mail_id);
		try {

			// emailContent =
			// templateEngine.process(sendmail.getMail_category(), data);
			htmlEmail.setHostName("smtp.gmail.com");
			htmlEmail.addTo(sendmail.getMail_to(), sendmail.getUser_name());
			htmlEmail.setAuthenticator(new DefaultAuthenticator("shoobilee@shoobilee.com",
					"peterlin"));
			htmlEmail.setFrom("noreply@shoobilee.com", "Shoobilee");
			htmlEmail.setSubject(sendmail.getMail_title());
			// htmlEmail.setSSLOnConnect(true);
			htmlEmail.setStartTLSEnabled(true);
			htmlEmail.setSmtpPort(587);
			// embed the image and get the content id

			htmlEmail.setHtmlMsg(sendmail.getContent());

			// send the email
			htmlEmail.send();

		} catch (EmailException e) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1402, ErrorCodeEnum.ER1402.getMsg());
		}

		//result.put("token", token);
		result.put("status", 1);

		return result;
	}

	public Map<String, Object> donateShoe(String token, Integer so_id) throws Exception {

		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<String, Object>();
		/*
		 * DonateCode codeIn = gson.fromJson(StringUtil.decode(code),
		 * DonateCode.class); Integer uid = codeIn.getData(); String product_sku
		 * = codeIn.getProduct(); Integer shurack_id = codeIn.getShurack(); if
		 * (shurackService.countShurackBySku(uid, 1, product_sku) <= 0) { throw
		 * new YamiException(YamiConstant.ERRORCODE_ER1107,
		 * ErrorCodeEnum.ER1107.getMsg()); } if (codeIn.getExp() <
		 * DateUtil.timeFormat(DateUtil.getNowDateTimeAllString())) { throw new
		 * YamiException(YamiConstant.ERRORCODE_ER1108,
		 * ErrorCodeEnum.ER1108.getMsg()); }
		 */
		OrderInfo orderinfo = orderInfoService.selectOrderInfoById(so_id);

		if (orderinfo == null) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1107, ErrorCodeEnum.ER1107.getMsg());
		}
		if (orderinfo.getDonate_status() == 1) {
			throw new YamiException(YamiConstant.ERRORCODE_ER1109, ErrorCodeEnum.ER1109.getMsg());
		}	
		Shurack shurack = shurackService.selectShurackByShurackId(orderinfo.getShurack_id());
		if(shurack == null || shurack.getStatus()!=1){
			throw new YamiException(YamiConstant.ERRORCODE_ER1107, ErrorCodeEnum.ER1107.getMsg());			
		}
		transactionDelegate.transactionDonateShoe(so_id, orderinfo.getShurack_id());

		Product donateProduct = productService.selectBySku(orderinfo.getProduct_sku());
		//List<Shurack> shurackList = shurackService.selectShipShurack(orderinfo.getUser_id(), 1);

		Users user = userService.selectUserInfoByUid(orderinfo.getUser_id());
		Context data = new Context();
		data.setVariable("user_name", user.getFirstName());
		data.setVariable("image_url", "https://shoobilee.com" + donateProduct.getImage_url());
		result.put("user_name", user.getFirstName());
		result.put("image_url", "https://shoobilee.com" + donateProduct.getImage_url());
		result.put("product_name", donateProduct.getProduct_name());		
/*		if (shurackList != null&&shurackList.size()!=0) {
			Product product = productService.selectBySku(shurackList.get(0).getProduct_sku());
			data.setVariable("image_url", "https://shoobilee.com" + product.getImage_url());
			data.setVariable("product_name", product.getProduct_name());
			data.setVariable("product_sku",
					"https://www.shoobilee.com/product.php?sku=" + product.getProduct_sku());
		} else {
			data.setVariable("image_url", "");
			data.setVariable("product_name", "");
			data.setVariable("product_sku", "");

		}*/
		data.setVariable("product_name", donateProduct.getProduct_name());
		sendMail( user.getEmail(), "email-shoes-donated",
				"Thanks for your donation!", user.getFirstName(), data);

		result.put("token", token);
		result.put("status", YamiConstant.STATUS_OK);
		return result;

	}
	
	public Map<String, Object> unSubscribe(String token, String email) throws Exception {
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		Integer adminUid = Integer.valueOf(tokenIn.getData());
		if(adminUid!=159){
			throw new YamiException(YamiConstant.ERRORCODE_ER1108, ErrorCodeEnum.ER1108.getMsg());				
		}
		Integer uid = userService.selectUIdByEmail(email);
		Users user = userService.selectUserInfoByUid(uid);
		String sub_order_id = subOrderService.selectSubOrderByUid(uid).getSub_order_id();
		Map<String, Object>	result = transactionDelegate.transactionUnSubscribeByAdmin(token,uid, "99", "Canceled by Admin",
				sub_order_id);
		Long currentPeriodEnd = (Long)result.get("currentPeriodEnd");
		Context data = new Context();
		data.setVariable("user_name", user.getFirstName());
		data.setVariable("billing_cycle_date", DateUtil.formateUTC2(currentPeriodEnd));
		sendMail( user.getEmail(),"email-shoes-cancel", "You are now unsubscribed",user.getFirstName(),data);
		return result;
	}
	
	public void testsendEmail() throws Exception {
		Context data = new Context();
		data.setVariable("user_name", "test");
		data.setVariable("billing_cycle_date", DateUtil.formateUTC2(1471299166));
		//sendMail( "chriszhang@shoobilee.com","email-shoes-cancel", "You are now unsubscribed","test",data);
		
		
		VerifyToken verifyToken = new VerifyToken();
		verifyToken.setUid(119);
		verifyToken.setSalt("1234");
		verifyToken.setCode("4444");
		Gson gson = new Gson();
		String herf = USER_VERIFY_LINK + StringUtil.encode(gson.toJson(verifyToken));		
		//String herf = USER_VERIFY_LINK + password;
		data.setVariable("reset_email_en", herf);
		//sendMail( "chriszhang@shoobilee.com", "email-subscribe",
		//		"You’re a Shoobilee subscriber!", "test", data);
		data.setVariable("so_date", "11/09/17");	
		data.setVariable("image_url", "https://shoobilee.com/222");	
		data.setVariable("product_name", "333");	
		if(null!=unSubOrderService.selectUnSubOrderById("sub_9aAXSsgrwku58J")){
		sendMail( "chriszhang@shoobilee.com","email-suspended", "Your account has been suspended", "member",
				data);
		}
	}
}
