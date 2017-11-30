package com.cosmetic.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cosmetic.delegate.AdminServiceDelegate;
import com.cosmetic.delegate.BackEndServiceDelegate;
import com.cosmetic.delegate.SecurityServiceDelegate;


@Service
@Path("/backEnd")
public class RestBackEndService {

	@Autowired
	private BackEndServiceDelegate backEndServiceDelegate;

/*	@GET
	@Path("/testsendEmail")
	@Produces("application/json;charset=utf-8")
	public Response testsendEmail(@QueryParam("token") String token)throws Exception {

		backEndServiceDelegate.testsendEmail();

		return Response.status(Status.OK).entity("").build();

	}*/
	@GET
	@Path("/unSubscribe")
	@Produces("application/json;charset=utf-8")
	public Response unSubscribe(@QueryParam("token") String token, @QueryParam("email") String email)throws Exception {

		Map<String,Object> result = backEndServiceDelegate.unSubscribe(token,email);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/shurackDailyProcess")
	@Produces("application/json;charset=utf-8")
	public Response shurackDailyProcess(@QueryParam("token") String token)throws Exception {

		Map<String,Object> result = backEndServiceDelegate.shurackDailyProcess(token);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/subOrderDailyProcess")
	@Produces("application/json;charset=utf-8")
	public Response subOrderDailyProcess(@QueryParam("token") String token)throws Exception {

		Map<String,Object> result = backEndServiceDelegate.subOrderDailyProcess(token);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/updateSubOrderInfo")
	@Produces("application/json;charset=utf-8")
	public Response updateSubOrderInfo(@QueryParam("token") String token,@QueryParam("user_id") Integer user_id,@QueryParam("sub_order_id")  String sub_order_id)throws Exception {

		Map<String,Object> result = backEndServiceDelegate.updateSubOrderInfo(user_id, sub_order_id);

		return Response.status(Status.OK).entity(result).build();

	}	
	
	@GET
	@Path("/shipShoe")
	@Produces("application/json;charset=utf-8")
	public Response shipShoe(@QueryParam("token") String token,@QueryParam("user_id") Integer user_id,@QueryParam("product_sku")  String product_sku,@QueryParam("shurack_id") Integer shurack_id,@QueryParam("so_id") Integer so_id)throws Exception {

		Map<String,Object> result = backEndServiceDelegate.shipShoe(token,user_id, product_sku,shurack_id,so_id);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/listOrderInfo")
	@Produces("application/json;charset=utf-8")
	public Response listOrderInfo(@QueryParam("token") String token,@QueryParam("user_id") Integer user_id,@QueryParam("pay_status") Integer pay_status
			,@QueryParam("donate_status") Integer donate_status,@QueryParam("ship_status") Integer ship_status,@QueryParam("page") Integer page
			,@QueryParam("number") Integer number)throws Exception {

		Map<String,Object> result = backEndServiceDelegate.listOrderInfo(token, user_id, pay_status, donate_status, ship_status, page, number);

		return Response.status(Status.OK).entity(result).build();

	}	
	
	@GET
	@Path("/resendMail")
	@Produces("application/json;charset=utf-8")
	public Response resendMail(@QueryParam("token") String token,@QueryParam("mail_id") Integer mail_id)throws Exception {

		Map<String,Object> result = backEndServiceDelegate.resendMail(token, mail_id);

		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/donateShoe")
	@Produces("application/json;charset=utf-8")
	public Response donateShoe(@QueryParam("token") String token,@QueryParam("so_id") Integer so_id)throws Exception {

		Map<String,Object> result = backEndServiceDelegate.donateShoe(token,so_id);

		return Response.status(Status.OK).entity(result).build();

	}	
}
