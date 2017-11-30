package com.cosmetic.rest;


import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosmetic.common.NeedLogin;
import com.cosmetic.delegate.PaymentServiceDelegate;


@Service
@Path("/pay")
public class RestPaymentService {
	@Autowired
	private PaymentServiceDelegate paymentServiceDelegate;
/*	@GET
	@NeedLogin
	@Path("/doPayment")
	@Produces("application/json")
	public Response doPayment(@QueryParam("token") String token,
			@QueryParam("profile_id") String profile_id,
			@QueryParam("purchase_id") String purchase_id, @QueryParam("cvc") String cvc,
			@QueryParam("amount") Double amount, @QueryParam("currency") String currency)
			throws Exception {

		Map<String, Object> result = paymentServiceDelegate.doPayment(profile_id,
				purchase_id, cvc, amount, currency);

		return Response.status(Status.OK).entity(result).build();

	}*/


/*	@GET
	@NeedLogin
	@Path("/verifyPayment")
	@Produces("application/json")
	public Response verifyPayment(@QueryParam("token") String token,
			@QueryParam("pay_id") String pay_id, @QueryParam("purchase_id") int purchase_id)
			throws Exception {

		Map<String, Object> result = paymentServiceDelegate.verifyPayment(token, pay_id, purchase_id);

		return Response.status(Status.OK).entity(result).build();

	}	*/
	
	@GET
	@NeedLogin
	@Path("/setExpressCheckout")
	@Produces("application/json")
	public Response setExpressCheckout(@QueryParam("token") String token,
			@QueryParam("amount") Double amount, @QueryParam("currency") String currency,@QueryParam("source_flag") String source_flag,@QueryParam("language") String language)
			throws Exception {


		Map<String, Object> result = paymentServiceDelegate.setExpressCheckout(token, amount, currency, source_flag, language);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/getExpressCheckout")
	@Produces("application/json")
	public Response getExpressCheckout(@QueryParam("token") String token) throws Exception {

		Map<String, Object> result = paymentServiceDelegate.getExpressCheckout(token);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@NeedLogin
	@Path("/doExpressCheckout")
	@Produces("application/json")
	public Response doExpressCheckout(@QueryParam("token") String token, @QueryParam("amount") Double amount,
			@QueryParam("currency") String currency, @QueryParam("purchase_id") Integer purchase_id) throws Exception {

		Map<String, Object> result = paymentServiceDelegate.doExpressCheckout(token,
				amount, currency, purchase_id);

		return Response.status(Status.OK).entity(result).build();

	}
	
}
