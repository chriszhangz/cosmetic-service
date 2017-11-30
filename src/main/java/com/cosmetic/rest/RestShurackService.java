package com.cosmetic.rest;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;




import com.cosmetic.delegate.ProductServiceDelegate;
import com.cosmetic.delegate.ShurackServiceDelegate;
import com.cosmetic.delegate.TransactionDelegate;

@Service
@Path("/shurack")
public class RestShurackService {
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private ShurackServiceDelegate shurackServiceDelegate;
	@Autowired
	private TransactionDelegate transactionDelegate;

	private Logger logger = LogManager.getLogger(this.getClass().getName());


	@GET
	@Path("/countShurackByUid")
	@Produces("application/json;charset=utf-8")
	public Response countShurackByUid(@QueryParam("token") String token,
			@QueryParam("status") String status) throws Exception {
		Map<String, Object> result = shurackServiceDelegate.countShurackByUserId(token, status);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getShurackByUid")
	@Produces("application/json;charset=utf-8")
	public Response getShurackByUid(@QueryParam("token") String token,
			@QueryParam("status") String status) throws Exception {
		Map<String, Object> result = shurackServiceDelegate.selectShurackByUid(token, status);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getMyShurack")
	@Produces("application/json;charset=utf-8")
	public Response getMyShurack(@QueryParam("token") String token) throws Exception {
		Map<String, Object> result = shurackServiceDelegate.selectMyShurack(token);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getDonateShurack")
	@Produces("application/json;charset=utf-8")
	public Response getDonateShurack(@QueryParam("token") String token) throws Exception {
		Map<String, Object> result = shurackServiceDelegate.selectDonateShurack(token);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getBuyShurack")
	@Produces("application/json;charset=utf-8")
	public Response getBuyShurack(@QueryParam("token") String token) throws Exception {
		Map<String, Object> result = shurackServiceDelegate.selectBuyShurack(token);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/insertShurack")
	@Produces("application/json;charset=utf-8")
	public Response insertShurack(@QueryParam("token") String token,
			@QueryParam("product_sku") String product_sku,@QueryParam("image_url") String image_url,@QueryParam("product_name") String product_name,@QueryParam("size_us") String size_us) throws Exception {
		Map<String, Object> result = shurackServiceDelegate.insertShurack(token, product_sku,image_url,product_name,size_us);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/deleteShurack")
	@Produces("application/json;charset=utf-8")
	public Response deleteShurack(@QueryParam("token") String token,
			@QueryParam("product_sku") String product_sku) throws Exception {
		Map<String, Object> result = shurackServiceDelegate.deleteShurack(token, product_sku);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/insertTopOfShurack")
	@Produces("application/json;charset=utf-8")
	public Response insertTopOfShurack(@QueryParam("token") String token,
			@QueryParam("product_sku") String product_sku,@QueryParam("image_url") String image_url,@QueryParam("product_name") String product_name,@QueryParam("size_us") String size_us) throws Exception {
		Map<String, Object> result = shurackServiceDelegate.insertTopOfShurack(token, product_sku,image_url,product_name,size_us);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/updateToTop")
	@Produces("application/json;charset=utf-8")
	public Response updateToTop(@QueryParam("token") String token,
			@QueryParam("product_sku") String product_sku) throws Exception {
		Map<String, Object> result = shurackServiceDelegate.updateToTop(token, product_sku);
		return Response.status(Status.OK).entity(result).build();

	}	
}
