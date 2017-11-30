package com.cosmetic.delegate;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.paymentech.ws.NewOrderRequestElement;
import net.paymentech.ws.NewOrderResponseElement;
import net.paymentech.ws.PaymentechGateway.wsdl.PaymentechGateway_wsdl.PaymentechGatewayLocator;
import net.paymentech.ws.PaymentechGateway.wsdl.PaymentechGateway_wsdl.PaymentechGatewayPortType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentReq;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentRequestType;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsReq;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.AddressType;
import urn.ebay.apis.eBLBaseComponents.CountryCodeType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.DoExpressCheckoutPaymentRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.PaymentInfoType;
import urn.ebay.apis.eBLBaseComponents.SellerDetailsType;
import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;

import com.cosmetic.common.DateUtil;
import com.cosmetic.common.ErrorCodeEnum;
import com.cosmetic.common.StringUtil;
import com.cosmetic.common.YamiConstant;
import com.cosmetic.common.YamiException;
import com.cosmetic.entity.ExpressCheckout;
import com.cosmetic.entity.OrderHistory;
import com.cosmetic.entity.OrderPaypal;
import com.cosmetic.entity.SubOrder;
import com.cosmetic.entity.Token;
import com.cosmetic.entity.UserAddress;
import com.cosmetic.entity.UserProfile;
import com.cosmetic.service.PaymentService;
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
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import com.stripe.model.Subscription;
import com.stripe.net.RequestOptions;

@Service
public class PaymentServiceDelegate {

	@Autowired private RedisTemplate<String,String> redisTemplate4EC;
	
	@Autowired
	private TransactionDelegate transactionDelegate;

	@Autowired
	private PaymentService paymentService;
	@Autowired
	private SubOrderService subOrderService;

	
	@Autowired
	private UserService userService;

	//@Autowired
	//private LogUtil logger;
	private Logger logger = LogManager.getLogger(this.getClass().getName());

	@Value("${STRIPE_API_KEY}")
	private String STRIPE_API_KEY;
	@Value("${PAYPAL_RETURN_URL}")
	private String PAYPAL_RETURN_URL;
	@Value("${PAYPAL_CANCEL_URL}")
	private String PAYPAL_CANCEL_URL;
	@Value("${PAYPAL_PCRETURN_URL_EN}")
	private String PAYPAL_PCRETURN_URL_EN;
	@Value("${PAYPAL_PCCANCEL_URL_EN}")
	private String PAYPAL_PCCANCEL_URL_EN;	
	@Value("${PAYPAL_PCRETURN_URL_CN}")
	private String PAYPAL_PCRETURN_URL_CN;
	@Value("${PAYPAL_PCCANCEL_URL_CN}")
	private String PAYPAL_PCCANCEL_URL_CN;
	@Value("${PAYPAL_RS_STATE_APPROVED}")
	private String PAYPAL_RS_STATE_APPROVED;
	@Value("${PAYPAL_ERRORCODE_11607}")
	private String PAYPAL_ERRORCODE_11607;
	@Value("${PAYPAL_VERIFY_ERROR}")
	private String PAYPAL_VERIFY_ERROR;
	@Value("${PAYPAL_TIMEOUT_ERROR}")
	private String PAYPAL_TIMEOUT_ERROR;
	@Value("${PAYPAL_RS_PAYMENT_MODE}")
	private String PAYPAL_RS_PAYMENT_MODE;
	@Value("${SDK_CONFIGURATION_FILE}")
	private String SDK_CONFIGURATION_FILE;
	@Value("${PAYPAL_RESPONSE_ACK_SUCCESS}")
	private String PAYPAL_RESPONSE_ACK_SUCCESS;
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


	/**
     * send NewOrderRequest to CHASE
     * @param amount Transaction Amount
     * @throws Exception
     * @author chris
     */
	public OrderHistory doPayment(Integer user_id,String profile_id,String purchase_id,Double amount,String currency) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		
	


		DecimalFormat df1 = new DecimalFormat(YamiConstant.FMT_NO_DECIMAL_POINT);
		String money = df1.format(amount*100);
		
		Stripe.apiKey = STRIPE_API_KEY;

		Map<String, Object> chargeParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		chargeParams.put("amount", money);
		chargeParams.put("currency", "usd");
		chargeParams.put("customer", profile_id); // obtained with Stripe.js
		chargeParams.put("description", "order id:"+purchase_id);

		RequestOptions options = RequestOptions
		  .builder()
		  .setIdempotencyKey(purchase_id)
		  .build();
		Charge charge = new Charge();
		try{
			//cardParams.put("cvc", cvc);	
			chargeParams.put("source", cardParams);		
			charge = Charge.create(chargeParams, options);
			logger.info(" authResponse: " + JSONFormatter.toJSON(charge));
		} catch (CardException e) {
		  // Since it's a decline, CardException will be caught
		  logger.error("Status is: " + e.getCode());
		  logger.error("Message is: " + e.getMessage());
		  throw new YamiException(YamiConstant.ERRORCODE_ER1333, ErrorCodeEnum.ER1333.getMsg());
		} catch (RateLimitException e) {
		  // Too many requests made to the API too quickly
			logger.error("Too many requests made to the API too quickly. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (InvalidRequestException e) {
		  // Invalid parameters were supplied to Stripe's API
			logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (AuthenticationException e) {
		  // Authentication with Stripe's API failed
		  // (maybe you changed API keys recently)
			logger.error("Authentication with Stripe's API failed. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (APIConnectionException e) {
		  // Network communication with Stripe failed
			logger.error("Network communication with Stripe failed. Error Message is: " + e.getMessage());
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
		//String zipCode = userAddress.getZipcode();
		OrderHistory orderHistory = new OrderHistory();
		try{
		ExternalAccount account = charge.getSource();
		Card card = (Card)account;	
		orderHistory.setAddress_city(card.getAddressCity());
		orderHistory.setAddress_country(card.getAddressCountry());
		orderHistory.setAddress_line1(card.getAddressLine1());
		orderHistory.setAddress_line1_check(card.getAddressLine1Check());
		orderHistory.setAddress_line2(card.getAddressLine2());
		orderHistory.setAddress_state(card.getAddressState());
		orderHistory.setAddress_zip(card.getAddressZip());
		orderHistory.setAddress_zip_check(card.getAddressZipCheck());
		orderHistory.setAmount(new BigDecimal(charge.getAmount()).divide(new BigDecimal(100)));
		orderHistory.setBrand(card.getBrand());
		orderHistory.setCard_id(card.getId());
		orderHistory.setCharge_id(charge.getId());
		orderHistory.setCountry(card.getCountry());
		orderHistory.setCreated(Integer.valueOf(charge.getCreated().toString()));
		orderHistory.setCurrency(charge.getCurrency());
		orderHistory.setCustomer(charge.getCustomer());
		orderHistory.setDescription(charge.getDescription());
		orderHistory.setExp_month(card.getExpMonth().toString());
		orderHistory.setExp_year(card.getExpYear().toString());
		orderHistory.setLast4(card.getLast4());
		orderHistory.setName(card.getName());
		orderHistory.setSo_id(Integer.valueOf(purchase_id));
		orderHistory.setStatus(charge.getStatus());
		orderHistory.setUser_id(user_id);
		transactionDelegate.transactionInsertOrderHistory(orderHistory);
		}catch(Exception e){
			logger.error("Failed to insert order_history:"+e.getMessage());
		}
	

		model.put("status", YamiConstant.STATUS_OK);

		return orderHistory;

	}
	public Subscription getSubscribe(String sub_order_id) throws Exception {
		//Map<String, Object> model = new HashMap<String, Object>();
		//TODO check weather there is only one subscription per customer?
		
		Stripe.apiKey = STRIPE_API_KEY;
		Subscription subscription = new Subscription();

		
		try{
			subscription = Subscription.retrieve(sub_order_id);
			logger.info(" authResponse: " + JSONFormatter.toJSON(subscription));
		} catch (CardException e) {
		  // Since it's a decline, CardException will be caught
		  logger.error("Status is: " + e.getCode());
		  logger.error("Message is: " + e.getMessage());
		  throw new YamiException(YamiConstant.ERRORCODE_ER1333, ErrorCodeEnum.ER1333.getMsg());
		} catch (RateLimitException e) {
		  // Too many requests made to the API too quickly
			logger.error("Too many requests made to the API too quickly. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (InvalidRequestException e) {
		  // Invalid parameters were supplied to Stripe's API
			logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (AuthenticationException e) {
		  // Authentication with Stripe's API failed
		  // (maybe you changed API keys recently)
			logger.error("Authentication with Stripe's API failed. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (APIConnectionException e) {
		  // Network communication with Stripe failed
			logger.error("Network communication with Stripe failed. Error Message is: " + e.getMessage());
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

		return subscription;

	}
	
	public Subscription subscribe(Integer user_id,String profile_id,String preset_type,String coupon,SubOrder user_sub) throws Exception {
		//Map<String, Object> model = new HashMap<String, Object>();
		//TODO check weather there is only one subscription per customer?
		UserProfile userProfile = userService.getProfileByPid(profile_id);
		if(userProfile==null){
			throw new YamiException(YamiConstant.ERRORCODE_ER1312,ErrorCodeEnum.ER1312.getMsg());			
		}

		String plan_id = "";
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
		Stripe.apiKey = STRIPE_API_KEY;
		Subscription subscription = new Subscription();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("plan", plan_id);
		if(!"".equals(coupon)){
			params.put("coupon", coupon);			
		}

		params.put("customer", profile_id);
		
		try{
			subscription = Subscription.create(params);
			logger.info(" authResponse: " + JSONFormatter.toJSON(subscription));
			if(user_sub!=null){
				Map<String, Object> updateParams = new HashMap<String, Object>();
				updateParams.put("coupon", "free-onetime");	
/*				Subscription sub = Subscription.retrieve(reuser_sub.getSub_order_id());
				if("free-onetime".equals(sub.getDiscount().getCoupon().getId())){
					userService.addRankPointsByUid(reuser_sub.getUser_id());
				}else{
					sub.update(updateParams);					
				}*/
				Subscription sub1 = Subscription.retrieve(subscription.getId());
				sub1.update(updateParams);				
			}
		} catch (CardException e) {
		  // Since it's a decline, CardException will be caught
		  logger.error("Status is: " + e.getCode());
		  logger.error("Message is: " + e.getMessage());
		  throw new YamiException(YamiConstant.ERRORCODE_ER1333, ErrorCodeEnum.ER1333.getMsg());
		} catch (RateLimitException e) {
		  // Too many requests made to the API too quickly
			logger.error("Too many requests made to the API too quickly. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (InvalidRequestException e) {
		  // Invalid parameters were supplied to Stripe's API
			logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (AuthenticationException e) {
		  // Authentication with Stripe's API failed
		  // (maybe you changed API keys recently)
			logger.error("Authentication with Stripe's API failed. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (APIConnectionException e) {
		  // Network communication with Stripe failed
			logger.error("Network communication with Stripe failed. Error Message is: " + e.getMessage());
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

		return subscription;

	}
	public void updateFreeCoupon(String sub_order_id,int user_id,Subscription sub){
		Map<String, Object> updateParams = new HashMap<String, Object>();
		Stripe.apiKey = STRIPE_API_KEY;
		updateParams.put("coupon", "free-onetime");
		try {
			//Subscription sub = Subscription.retrieve(sub_order_id);
			
				sub.update(updateParams);
				userService.subRankPointsByUid(user_id);
				logger.info("User:"+user_id+" use 1 point to get 1 free month coupon!");
			
		} catch (CardException e) {
			// Since it's a decline, CardException will be caught
			logger.error("Status is: " + e.getCode());
			logger.error("Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1333, ErrorCodeEnum.ER1333.getMsg());
		} catch (RateLimitException e) {
			// Too many requests made to the API too quickly
			logger.error("Too many requests made to the API too quickly. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (InvalidRequestException e) {
			// Invalid parameters were supplied to Stripe's API
			logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (AuthenticationException e) {
			// Authentication with Stripe's API failed
			// (maybe you changed API keys recently)
			logger.error("Authentication with Stripe's API failed. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (APIConnectionException e) {
			// Network communication with Stripe failed
			logger.error("Network communication with Stripe failed. Error Message is: " + e.getMessage());
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
	}
	
	public Subscription updateSubscribe(Integer user_id,String preset_type,String sub_order_id) throws Exception {
		//Map<String, Object> model = new HashMap<String, Object>();


		String plan_id = "";
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
		Stripe.apiKey = STRIPE_API_KEY;
		Subscription subscription = new Subscription();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("plan", plan_id);
		params.put("prorate", "false");
		
		try{
			subscription = Subscription.retrieve(sub_order_id);
			subscription.update(params);
			logger.info(" authResponse: " + JSONFormatter.toJSON(subscription));
		} catch (CardException e) {
		  // Since it's a decline, CardException will be caught
		  logger.error("Status is: " + e.getCode());
		  logger.error("Message is: " + e.getMessage());
		  throw new YamiException(YamiConstant.ERRORCODE_ER1333, ErrorCodeEnum.ER1333.getMsg());
		} catch (RateLimitException e) {
		  // Too many requests made to the API too quickly
			logger.error("Too many requests made to the API too quickly. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (InvalidRequestException e) {
		  // Invalid parameters were supplied to Stripe's API
			logger.error("Invalid parameters were supplied to Stripe's API. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (AuthenticationException e) {
		  // Authentication with Stripe's API failed
		  // (maybe you changed API keys recently)
			logger.error("Authentication with Stripe's API failed. Error Message is: " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331, ErrorCodeEnum.ER1331.getMsg());
		} catch (APIConnectionException e) {
		  // Network communication with Stripe failed
			logger.error("Network communication with Stripe failed. Error Message is: " + e.getMessage());
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

		return subscription;

	}
/*	public Map<String, Object> verifyPayment(String token, String pay_id, int purchase_id) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		InputStream is = PaymentServiceDelegate.class.getResourceAsStream(SDK_CONFIGURATION_FILE);
		try {
			PayPalResource.initConfig(is);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1331,ErrorCodeEnum.ER1331.getMsg(),e.getMessage());	
		}
		String clientID = ConfigManager.getInstance().getValue(Constants.CLIENT_ID);
		String clientSecret = ConfigManager.getInstance().getValue(Constants.CLIENT_SECRET);
		OAuthTokenCredential tokenCredential = new OAuthTokenCredential(clientID, clientSecret);

		String accessToken = tokenCredential.getAccessToken();
		String updateTime = YamiConstant.STRING_EMPTY;
		String payerId = YamiConstant.STRING_EMPTY;
		String paymentStatus = YamiConstant.STRING_EMPTY;
		String paymentType = YamiConstant.STRING_EMPTY;
		BigDecimal paypalCharge = new BigDecimal(0);
		BigDecimal totalAmount = new BigDecimal(0);
		String transactionId = YamiConstant.STRING_EMPTY;
		String transactionType = "verify-payment";
		String merchantAccount = YamiConstant.STRING_EMPTY;
		try {
			Payment payment = Payment.get(accessToken, pay_id);
			totalAmount = new BigDecimal(payment.getTransactions().get(0).getAmount()
					.getTotal());
			BigDecimal amount = orderInfoService.selectOrderAmountByPurchaseId(purchase_id);
			updateTime = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getUpdateTime();
			payerId = payment.getPayer().getPayerInfo().getPayerId();
			paymentStatus = payment.getState();
			paymentType = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getPaymentMode();
			if(null!=payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getTransactionFee()){
				paypalCharge = new BigDecimal(payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getTransactionFee().getValue());
			}
			transactionId = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();
			
			logger.info(" totalAmount: " + totalAmount);
			logger.info(" amount: " + amount);
			logger.info(" PaymentStatus: " + payment.getState());
			logger.info(" payment: " + payment);
			if (!PAYPAL_RS_STATE_APPROVED.equals(payment.getState()) || !totalAmount.equals(amount)||!"USD".equals(payment.getTransactions().get(0).getAmount().getCurrency())
					||!PAYPAL_RS_PAYMENT_MODE.equals(payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getPaymentMode())) {
				try {
					logger.error("Verify Payment Not Match!!!");
					if (!totalAmount.equals(amount)) {
						merchantAccount = String.valueOf(amount);
						paymentStatus = "Amount not equal";
						logger.fatal(" Verify Payment Error: " + ErrorCodeEnum.ER1332.getMsg()[0]);
					}else{
						logger.fatal(" Verify Payment Error: " + ErrorCodeEnum.ER1331.getMsg()[0]);						
					}
					transactionDelegate.transactionUpdateAbnormalByPurchaseId(
							Integer.valueOf(PAYPAL_VERIFY_ERROR), purchase_id);
				} catch (Exception ie) {
					logger.fatal("Failed to update abnormal to order_info");
					logger.fatal(ie.toString());
				}
			}
		
		} catch (PayPalRESTException e) {
			logger.fatal("Verify Payment Error:" + e.getMessage());
			merchantAccount = String.valueOf(e.getResponsecode());
			transactionDelegate.transactionUpdateAbnormalByPurchaseId(
					Integer.valueOf(PAYPAL_VERIFY_ERROR), purchase_id);
		} catch (Exception e) {
			logger.fatal("Verify Payment Error:" + e.getMessage());
			if(e.getMessage().length()>30){
				merchantAccount = e.getMessage().substring(0, 30);
			}else{
				merchantAccount = e.getMessage();
			}
			transactionDelegate.transactionUpdateAbnormalByPurchaseId(
					Integer.valueOf(PAYPAL_VERIFY_ERROR), purchase_id);
		}finally{
			OrderPaypal orderPaypal = new OrderPaypal();
			orderPaypal.setMerchantAccount(merchantAccount==null?YamiConstant.STRING_EMPTY:merchantAccount);
			orderPaypal.setOrderSn(String.valueOf(purchase_id));
			orderPaypal.setPayDate(updateTime==null?YamiConstant.STRING_EMPTY:updateTime);
			orderPaypal.setPayerId(payerId==null?YamiConstant.STRING_EMPTY:payerId);
			orderPaypal.setPaymentStatus(paymentStatus==null?YamiConstant.STRING_EMPTY:paymentStatus);
			orderPaypal.setPaymentType(paymentType==null?YamiConstant.STRING_EMPTY:paymentType);
			orderPaypal.setPaypalCharge(paypalCharge);
			orderPaypal.setToken(pay_id);
			orderPaypal.setTotalAmount(totalAmount);
			orderPaypal.setTransactionId(transactionId==null?YamiConstant.STRING_EMPTY:transactionId);
			orderPaypal.setTransactionType(transactionType==null?YamiConstant.STRING_EMPTY:transactionType);
			// Update order_orbital_paypal
			try {
				transactionDelegate.transactionAddOrderPaypal(orderPaypal);
			} catch (Exception e) {
				logger.fatal("Failed to update order_orbital_gateway :"+orderPaypal.getOrderSn());
				logger.fatal(e.toString());
			}
		}
		//logger.info(" PaymentResponse: " + Payment.getLastResponse());
		model.put("status", YamiConstant.STATUS_OK);
		model.put("token", token);
		return model;
	}*/
	
    public Map<String, Object> setExpressCheckout(String token, Double amount,
			String currency,String source_flg,String language) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		DecimalFormat df1 = new DecimalFormat(YamiConstant.FMT_TWO_DECIMAL_POINT);
		String money = df1.format(amount);

		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int user_id = Integer.valueOf(tokenIn.getData());
		// ## SetExpressCheckoutReq
		SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();

		// URL to which the buyer's browser is returned after choosing to pay
		// with PayPal. For digital goods, you must add JavaScript to this page
		// to close the in-context experience.
		// `Note:
		// PayPal recommends that the value be the final review page on which
		// the buyer confirms the order and payment or billing agreement.`
		setExpressCheckoutRequestDetails
				.setReturnURL(PAYPAL_RETURN_URL);

		// URL to which the buyer is returned if the buyer does not approve the
		// use of PayPal to pay you. For digital goods, you must add JavaScript
		// to this page to close the in-context experience.
		// `Note:
		// PayPal recommends that the value be the original page on which the
		// buyer chose to pay with PayPal or establish a billing agreement.`
		setExpressCheckoutRequestDetails
				.setCancelURL(PAYPAL_CANCEL_URL);
		if(source_flg!=null && source_flg.equals("2")){
			// request came from PC
			if(language!=null && "en".equals(language)){
				setExpressCheckoutRequestDetails.setReturnURL(PAYPAL_PCRETURN_URL_EN);
				setExpressCheckoutRequestDetails.setCancelURL(PAYPAL_PCCANCEL_URL_EN);
			}else{
				setExpressCheckoutRequestDetails.setReturnURL(PAYPAL_PCRETURN_URL_CN);
				setExpressCheckoutRequestDetails.setCancelURL(PAYPAL_PCCANCEL_URL_CN);				
			}
		}
		// ### Payment Information
		// list of information about the payment
		List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();

		// information about the first payment
		PaymentDetailsType paymentDetails1 = new PaymentDetailsType();

		// Total cost of the transaction to the buyer. If shipping cost and tax
		// charges are known, include them in this value. If not, this value
		// should be the current sub-total of the order.
		//
		// If the transaction includes one or more one-time purchases, this
		// field must be equal to
		// the sum of the purchases. Set this field to 0 if the transaction does
		// not include a one-time purchase such as when you set up a billing
		// agreement for a recurring payment that is not immediately charged.
		// When the field is set to 0, purchase-specific fields are ignored.
		// 
		// * `Currency Code` - You must set the currencyID attribute to one of
		// the
		// 3-character currency codes for any of the supported PayPal
		// currencies.
		// * `Amount`
		BasicAmountType orderTotal1 = new BasicAmountType(CurrencyCodeType.USD,money);
		paymentDetails1.setOrderTotal(orderTotal1);

		// How you want to obtain payment. When implementing parallel payments,
		// this field is required and must be set to `Order`. When implementing
		// digital goods, this field is required and must be set to `Sale`. If
		// the
		// transaction does not include a one-time purchase, this field is
		// ignored. It is one of the following values:
		// 
		// * `Sale` - This is a final sale for which you are requesting payment
		// (default).
		// * `Authorization` - This payment is a basic authorization subject to
		// settlement with PayPal Authorization and Capture.
		// * `Order` - This payment is an order authorization subject to
		// settlement with PayPal Authorization and Capture.
		// `Note:
		// You cannot set this field to Sale in SetExpressCheckout request and
		// then change the value to Authorization or Order in the
		// DoExpressCheckoutPayment request. If you set the field to
		// Authorization or Order in SetExpressCheckout, you may set the field
		// to Sale.`
		paymentDetails1.setPaymentAction(PaymentActionCodeType.SALE);

		// Unique identifier for the merchant. For parallel payments, this field
		// is required and must contain the Payer Id or the email address of the
		// merchant.
		//SellerDetailsType sellerDetails1 = new SellerDetailsType();
		//sellerDetails1.setPayPalAccountID("jb-us-seller@paypal.com");
		//paymentDetails1.setSellerDetails(sellerDetails1);

		// A unique identifier of the specific payment request, which is
		// required for parallel payments.
		//paymentDetails1.setPaymentRequestID("PaymentRequest1");

		// `Address` to which the order is shipped, which takes mandatory
		// params:
		// 
		// * `Street Name`
		// * `City`
		// * `State`
		// * `Country`
		// * `Postal Code`
		//AddressType shipToAddress1 = new AddressType();
		//shipToAddress1.setStreet1("Ape Way");
		//shipToAddress1.setCityName("Austin");
		//shipToAddress1.setStateOrProvince("TX");
		//shipToAddress1.setCountry(CountryCodeType.US);
		//shipToAddress1.setPostalCode("78750");

		// Your URL for receiving Instant Payment Notification (IPN) about this
		// transaction. If you do not specify this value in the request, the
		// notification URL from your Merchant Profile is used, if one exists.
		//paymentDetails1.setNotifyURL("http://localhost/ipn");

		//paymentDetails1.setShipToAddress(shipToAddress1);


		paymentDetailsList.add(paymentDetails1);

		setExpressCheckoutRequestDetails.setPaymentDetails(paymentDetailsList);

		SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
		SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(
				setExpressCheckoutRequestDetails);

		setExpressCheckoutReq
				.setSetExpressCheckoutRequest(setExpressCheckoutRequest);

		// ## Creating service wrapper object
		// Creating service wrapper object to make API call and loading
		// configuration file for your credentials and endpoint
		PayPalAPIInterfaceServiceService service = null;
		try {
			InputStream is = PaymentServiceDelegate.class.getResourceAsStream(SDK_CONFIGURATION_FILE);
			service = new PayPalAPIInterfaceServiceService(is);
		} catch (IOException e) {
			logger.error("PayPal Get Service Error Message : " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1401,ErrorCodeEnum.ER1401.getMsg(),e.getMessage());	
		}
		SetExpressCheckoutResponseType setExpressCheckoutResponse = null;
		try {
			// ## Making API call
			// Invoke the appropriate method corresponding to API in service
			// wrapper object
			setExpressCheckoutResponse = service
					.setExpressCheckout(setExpressCheckoutReq);
		} catch (Exception e) {
			logger.error("PayPal SetExpressCheckout Error Message : " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1402,ErrorCodeEnum.ER1402.getMsg());	
		}
		
		// ## Accessing response parameters
		// You can access the response parameters using getter methods in
		// response object as shown below
		// ### Success values
		if (setExpressCheckoutResponse.getAck().getValue()
				.equalsIgnoreCase(PAYPAL_RESPONSE_ACK_SUCCESS)) {

			// ### Redirecting to PayPal for authorization
			// Once you get the "Success" response, needs to authorise the
			// transaction by making buyer to login into PayPal. For that,
			// need to construct redirect url using EC token from response.
			// For example,
			// `redirectURL="https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+setExpressCheckoutResponse.getToken();`
			
			// Express Checkout Token
			logger.info("PayPal EC Token:" + setExpressCheckoutResponse.getToken());
			// Delete this user's record from express_checkout
			// Insert record to express_checkout
			//transactionDelegate.transactionSetExpressCheckout(token, user_id, setExpressCheckoutResponse.getToken());
			// Insert record to redis
				ExpressCheckout expressCheckout = new ExpressCheckout();
				expressCheckout.setPayer_id(YamiConstant.STRING_EMPTY);
				expressCheckout.setSession_id(YamiConstant.STRING_EMPTY);
				expressCheckout.setToken(setExpressCheckoutResponse.getToken());
				expressCheckout.setUser_id(user_id);
				String strResult = gson.toJsonTree(expressCheckout).toString();
				redisTemplate4EC.opsForValue().set(YamiConstant.KEY_EXPRESSCHECKOUT_REDIS+":"+user_id, strResult,3600,TimeUnit.SECONDS);
		}
		// ### Error Values
		// Access error values from error list using getter methods
		else {
			List<ErrorType> errorList = setExpressCheckoutResponse.getErrors();
			logger.error("PayPal API Error Message : "
					+ errorList.get(0).getLongMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1403,ErrorCodeEnum.ER1403.getMsg(),errorList.get(0).getLongMessage());	
		}
		model.put("status", YamiConstant.STATUS_OK);
		model.put("token", token);
		model.put("ec_token", setExpressCheckoutResponse.getToken());
		logger.info(" model: " + model);
		return model;
	}


	public Map<String, Object> getExpressCheckout(String token) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
 		String ec_token = "";
 		String payer_id = "";
		// ## GetExpressCheckoutDetailsReq
		GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();
		ExpressCheckout expressCheckout = new ExpressCheckout();
		
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int user_id = Integer.valueOf(tokenIn.getData());

		if(null!=redisTemplate4EC.opsForValue().get(YamiConstant.KEY_EXPRESSCHECKOUT_REDIS+":"+user_id)){
			String sResult = redisTemplate4EC.opsForValue().get(YamiConstant.KEY_EXPRESSCHECKOUT_REDIS+":"+user_id);
			expressCheckout  = gson.fromJson(sResult, ExpressCheckout.class);
			// get token of PayPal.
			if(expressCheckout==null||expressCheckout.getToken().equals(YamiConstant.STRING_EMPTY)){
				throw new YamiException(YamiConstant.ERRORCODE_ER1405,ErrorCodeEnum.ER1405.getMsg());				
			}
			ec_token = expressCheckout.getToken();
		}else{
			throw new YamiException(YamiConstant.ERRORCODE_ER1405,ErrorCodeEnum.ER1405.getMsg());				
		}

		// A timestamped token, the value of which was returned by
		// `SetExpressCheckout` response.
		GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest = new GetExpressCheckoutDetailsRequestType(
				ec_token);

		getExpressCheckoutDetailsReq
				.setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);

		// ## Creating service wrapper object
		// Creating service wrapper object to make API call and loading
		// configuration file for your credentials and endpoint
		PayPalAPIInterfaceServiceService service = null;
		try {
			InputStream is = PaymentServiceDelegate.class.getResourceAsStream(SDK_CONFIGURATION_FILE);
			service = new PayPalAPIInterfaceServiceService(is);
		} catch (IOException e) {
			logger.error("PayPal Error Message : " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1401,ErrorCodeEnum.ER1401.getMsg(),e.getMessage());	
		}

		GetExpressCheckoutDetailsResponseType getExpressCheckoutDetailsResponse = null;
		try {
			// ## Making API call
			// Invoke the appropriate method corresponding to API in service
			// wrapper object
			 getExpressCheckoutDetailsResponse = service
					.getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
		} catch (Exception e) {
			logger.error("PayPal Error Message : " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1402,ErrorCodeEnum.ER1402.getMsg(),e.getMessage());	
		}
		// ## Accessing response parameters
		// You can access the response parameters using getter methods in
		// response object as shown below
		// ### Success values
		if (getExpressCheckoutDetailsResponse.getAck().getValue()
				.equalsIgnoreCase(PAYPAL_RESPONSE_ACK_SUCCESS)) {
			payer_id = getExpressCheckoutDetailsResponse
					.getGetExpressCheckoutDetailsResponseDetails()
					.getPayerInfo().getPayerID();
			if(payer_id==null){
				throw new YamiException(YamiConstant.ERRORCODE_ER1403,ErrorCodeEnum.ER1403.getMsg());				
			}
			//transactionDelegate.transactionGetExpressCheckout(token, user_id, payer_id);
			expressCheckout.setPayer_id(payer_id);
			expressCheckout.setUser_id(user_id);
			String strResult = gson.toJsonTree(expressCheckout).toString();
			redisTemplate4EC.opsForValue().set(YamiConstant.KEY_EXPRESSCHECKOUT_REDIS+":"+user_id, strResult,3600,TimeUnit.SECONDS);
			// Unique PayPal Customer Account identification number. This
			// value will be null unless you authorize the payment by
			// redirecting to PayPal after `SetExpressCheckout` call.
			logger.info("PayPal PayerID : "
					+ getExpressCheckoutDetailsResponse
							.getGetExpressCheckoutDetailsResponseDetails()
							.getPayerInfo().getPayerID());
			logger.info("PayPal Address : "
					+ getExpressCheckoutDetailsResponse
							.getGetExpressCheckoutDetailsResponseDetails()
							.getPayerInfo().getAddress().toXMLString("addr-", "address"));

		}
		// ### Error Values
		// Access error values from error list using getter methods
		else {
			List<ErrorType> errorList = getExpressCheckoutDetailsResponse
					.getErrors();
			logger.error("PayPal API Error Message : "
					+ errorList.get(0).getLongMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1403,ErrorCodeEnum.ER1403.getMsg(),errorList.get(0).getLongMessage());	
		}	
		model.put("status", YamiConstant.STATUS_OK);
		model.put("token", token);
		model.put("payer", getExpressCheckoutDetailsResponse
				.getGetExpressCheckoutDetailsResponseDetails()
				.getPayerInfo().getPayer());
		logger.info(" model: " + model);
		return model;
	}
	
	public Map<String, Object> doExpressCheckout(String token, Double amount, String currency, Integer purchase_id) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		DecimalFormat df1 = new DecimalFormat(YamiConstant.FMT_TWO_DECIMAL_POINT);
		String money = df1.format(amount);
 		String ec_token = "";
 		String payer_id = "";
 		
		Gson gson = new Gson();
		Token tokenIn = gson.fromJson(StringUtil.decode(token), Token.class);
		int user_id = Integer.valueOf(tokenIn.getData());
	
		if(null!=redisTemplate4EC.opsForValue().get(YamiConstant.KEY_EXPRESSCHECKOUT_REDIS+":"+user_id)){
			String sResult = redisTemplate4EC.opsForValue().get(YamiConstant.KEY_EXPRESSCHECKOUT_REDIS+":"+user_id);
			ExpressCheckout expressCheckout  = gson.fromJson(sResult, ExpressCheckout.class);
			// get token of PayPal.
			if(expressCheckout==null||expressCheckout.getToken().equals(YamiConstant.STRING_EMPTY)){
				throw new YamiException(YamiConstant.ERRORCODE_ER1405,ErrorCodeEnum.ER1405.getMsg());				
			}
			ec_token = expressCheckout.getToken();
			payer_id = expressCheckout.getPayer_id();
			if(ec_token==null||payer_id==null||payer_id.equals(YamiConstant.STRING_EMPTY)){
				throw new YamiException(YamiConstant.ERRORCODE_ER1402,ErrorCodeEnum.ER1402.getMsg());				
			}
		}else{
			throw new YamiException(YamiConstant.ERRORCODE_ER1405,ErrorCodeEnum.ER1405.getMsg());				
		}

		// ## DoExpressCheckoutPaymentReq
		DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();

		DoExpressCheckoutPaymentRequestDetailsType doExpressCheckoutPaymentRequestDetails = new DoExpressCheckoutPaymentRequestDetailsType();

		// The timestamped token value that was returned in the
		// `SetExpressCheckout` response and passed in the
		// `GetExpressCheckoutDetails` request.
		doExpressCheckoutPaymentRequestDetails.setToken(ec_token);

		// Unique paypal buyer account identification number as returned in
		// `GetExpressCheckoutDetails` Response
		doExpressCheckoutPaymentRequestDetails.setPayerID(payer_id);

		// ### Payment Information
		// list of information about the payment
		List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();

		// information about the first payment
		PaymentDetailsType paymentDetails1 = new PaymentDetailsType();

		// Total cost of the transaction to the buyer. If shipping cost and tax
		// charges are known, include them in this value. If not, this value
		// should be the current sub-total of the order. 
		// 
		// If the transaction includes one or more one-time purchases, this field must be equal to
		// the sum of the purchases. Set this field to 0 if the transaction does
		// not include a one-time purchase such as when you set up a billing
		// agreement for a recurring payment that is not immediately charged.
		// When the field is set to 0, purchase-specific fields are ignored.
		// 
		// * `Currency Code` - You must set the currencyID attribute to one of the
		// 3-character currency codes for any of the supported PayPal
		// currencies.
		// * `Amount`
		BasicAmountType orderTotal1 = new BasicAmountType(CurrencyCodeType.USD,
				money);
		paymentDetails1.setOrderTotal(orderTotal1);
		paymentDetails1.setOrderDescription("Purchase# " + String.valueOf(purchase_id));

		// How you want to obtain payment. When implementing parallel payments,
		// this field is required and must be set to `Order`. When implementing
		// digital goods, this field is required and must be set to `Sale`. If the
		// transaction does not include a one-time purchase, this field is
		// ignored. It is one of the following values:
		// 
		// * `Sale` - This is a final sale for which you are requesting payment
		// (default).
		// * `Authorization` - This payment is a basic authorization subject to
		// settlement with PayPal Authorization and Capture.
		// * `Order` - This payment is an order authorization subject to
		// settlement with PayPal Authorization and Capture.
		// Note:
		// You cannot set this field to Sale in SetExpressCheckout request and
		// then change the value to Authorization or Order in the
		// DoExpressCheckoutPayment request. If you set the field to
		// Authorization or Order in SetExpressCheckout, you may set the field
		// to Sale.
		paymentDetails1.setPaymentAction(PaymentActionCodeType.SALE);

		// Unique identifier for the merchant. For parallel payments, this field
		// is required and must contain the Payer Id or the email address of the
		// merchant.
		//SellerDetailsType sellerDetails1 = new SellerDetailsType();
		//sellerDetails1.setPayPalAccountID("jb-us-seller@paypal.com");
		//paymentDetails1.setSellerDetails(sellerDetails1);

		// A unique identifier of the specific payment request, which is
		// required for parallel payments.
		//paymentDetails1.setPaymentRequestID("PaymentRequest1");
		
		// Your URL for receiving Instant Payment Notification (IPN) about this transaction. If you do not specify this value in the request, the notification URL from your Merchant Profile is used, if one exists.
		//paymentDetails1.setNotifyURL("http://localhost/ipn");
		
		paymentDetailsList.add(paymentDetails1);
		doExpressCheckoutPaymentRequestDetails
				.setPaymentDetails(paymentDetailsList);
		DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest = new DoExpressCheckoutPaymentRequestType(
				doExpressCheckoutPaymentRequestDetails);
		doExpressCheckoutPaymentReq
				.setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);

		// ## Creating service wrapper object
		// Creating service wrapper object to make API call and loading
		// configuration file for your credentials and endpoint
		PayPalAPIInterfaceServiceService service = null;
		try {
			InputStream is = PaymentServiceDelegate.class.getResourceAsStream(SDK_CONFIGURATION_FILE);
			service = new PayPalAPIInterfaceServiceService(is);
		} catch (IOException e) {
			logger.fatal("PayPal Error Message : " + e.getMessage());
			if("Read timed out".equals(e.getMessage())){
/*				transactionDelegate.transactionUpdateAbnormalByPurchaseId(
						Integer.valueOf(PAYPAL_TIMEOUT_ERROR), purchase_id);*/
				throw new YamiException(YamiConstant.ERRORCODE_ER1406,ErrorCodeEnum.ER1406.getMsg(),e.getMessage());
			}else{
				throw new YamiException(YamiConstant.ERRORCODE_ER1401,ErrorCodeEnum.ER1401.getMsg(),e.getMessage());
			}
		}
		DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponse = null;
		try {
			// ## Making API call
			// Invoke the appropriate method corresponding to API in service
			// wrapper object
			 doExpressCheckoutPaymentResponse = service
					.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);
		} catch (Exception e) {
			logger.error("PayPal Error Message : " + e.getMessage());
			throw new YamiException(YamiConstant.ERRORCODE_ER1402,ErrorCodeEnum.ER1402.getMsg(),e.getMessage());	
		}

		// ## Accessing response parameters
		// You can access the response parameters using getter methods in
		// response object as shown below
		// ### Success values
		if (doExpressCheckoutPaymentResponse.getAck().getValue()
				.equalsIgnoreCase(PAYPAL_RESPONSE_ACK_SUCCESS)) {

			// Transaction identification number of the transaction that was
			// created.
			// This field is only returned after a successful transaction
			// for DoExpressCheckout has occurred.
			if (doExpressCheckoutPaymentResponse
					.getDoExpressCheckoutPaymentResponseDetails()
					.getPaymentInfo() != null) {
				Iterator<PaymentInfoType> paymentInfoIterator = doExpressCheckoutPaymentResponse
						.getDoExpressCheckoutPaymentResponseDetails()
						.getPaymentInfo().iterator();
				while (paymentInfoIterator.hasNext()) {
					PaymentInfoType paymentInfo = paymentInfoIterator
							.next();
					logger.info("PayPal Transaction ID : "
							+ paymentInfo.getTransactionID());
				}
			}
		}
		// ### Error Values
		// Access error values from error list using getter methods
		else {
			List<ErrorType> errorList = doExpressCheckoutPaymentResponse
					.getErrors();
			logger.error("PayPal API Error Message : "
					+ errorList.get(0).getLongMessage());
			if (PAYPAL_ERRORCODE_11607.equals(errorList.get(0).getErrorCode())) {
				throw new YamiException(YamiConstant.ERRORCODE_ER1404,ErrorCodeEnum.ER1404.getMsg());
			} else {
				throw new YamiException(YamiConstant.ERRORCODE_ER1403,ErrorCodeEnum.ER1403.getMsg(),errorList.get(0).getLongMessage());
			}
		}
		PaymentInfoType paymentInfo =  doExpressCheckoutPaymentResponse.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo().get(0); 
		try {
			logger.info(" paymentInfo: " + JSONFormatter.toJSON(paymentInfo));
		} catch (Exception e) {
			logger.error("doExpressCheckout Response toJSON Error Message : " + e.getMessage());
		}
		OrderPaypal orderPaypal = new OrderPaypal();
		orderPaypal.setMerchantAccount(paymentInfo.getSellerDetails().getSecureMerchantAccountID());
		orderPaypal.setOrderSn(String.valueOf(purchase_id));
		orderPaypal.setPayDate(paymentInfo.getPaymentDate());
		orderPaypal.setPayerId(payer_id);
		orderPaypal.setPaymentStatus(paymentInfo.getPaymentStatus().getValue());
		orderPaypal.setPaymentType(paymentInfo.getPaymentType().getValue());
		orderPaypal.setPaypalCharge(new BigDecimal(paymentInfo.getFeeAmount().getValue()));
		orderPaypal.setToken(ec_token);
		orderPaypal.setTotalAmount(new BigDecimal(paymentInfo.getGrossAmount().getValue()));
		orderPaypal.setTransactionId(paymentInfo.getTransactionID());
		orderPaypal.setTransactionType(paymentInfo.getTransactionType().getValue());
		// Update order_orbital_paypal
		try {
			transactionDelegate.transactionAddOrderPaypal(orderPaypal);
		} catch (Exception e) {
			logger.fatal("Failed to update order_orbital_gateway :"+orderPaypal.getOrderSn());
			logger.fatal(e.toString());
		}
    	String redisKey = YamiConstant.KEY_EXPRESSCHECKOUT_REDIS+":"+user_id;
    	redisTemplate4EC.delete(redisKey);
		model.put("status", YamiConstant.STATUS_OK);
		model.put("token", token);
		logger.info(" model: " + model);
		return model;
	}
	
	
	
}
