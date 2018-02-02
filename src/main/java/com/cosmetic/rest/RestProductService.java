package com.cosmetic.rest;

import java.util.HashMap;
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
import com.cosmetic.delegate.TransactionDelegate;

@Service
@Path("/product")
public class RestProductService {
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private ProductServiceDelegate productServiceDelegate;
	@Autowired
	private TransactionDelegate transactionDelegate;

	private Logger logger = LogManager.getLogger(this.getClass().getName());

	@GET
	@Path("/getIndexInfo")
	@Produces("application/json;charset=utf-8")
	public Response getIndexInfo(@QueryParam("token") String token,
			@QueryParam("type") String type) throws Exception {
		Map<String, Object> result = productServiceDelegate.selectIndexInfo(token, type);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/filterProductBySku")
	@Produces("application/json;charset=utf-8")
	public Response filterProductBySku(@QueryParam("token") String token,@QueryParam("style_type") String style_type,
			@QueryParam("size_us") String size_us,@QueryParam("color") String color,
			@QueryParam("page") String page) throws Exception {
		Map<String, Object> result = productServiceDelegate.filterProductBySku(token, style_type, size_us, color,page);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/listProductByPromo")
	@Produces("application/json;charset=utf-8")
	public Response listProductByPromo(@QueryParam("token") String token,@QueryParam("category") String category,@QueryParam("style_type") String style_type,
			@QueryParam("size_us") String size_us,@QueryParam("color") String color,
			@QueryParam("page") String page) throws Exception {
		Map<String, Object> result = productServiceDelegate.listProductByPromo(token, category,style_type, size_us, color,page);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getProductInfo")
	@Produces("application/json;charset=utf-8")
	public Response getProductInfo(@QueryParam("token") String token,
			@QueryParam("product_sku") String product_sku) throws Exception {
		//Map<String, Object> result = productServiceDelegate.selectProductBySku(token, product_sku);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("Status", "OK");
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getSimilarProduct")
	@Produces("application/json;charset=utf-8")
	public Response getSimilarProduct(@QueryParam("token") String token,@QueryParam("style_type") String style_type,
			@QueryParam("size_us") String size_us,@QueryParam("color") String color) throws Exception {
		Map<String, Object> result = productServiceDelegate.selectSimilarProduct(token, style_type, size_us, color);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getAlsoLike")
	@Produces("application/json;charset=utf-8")
	public Response selectAlsoLike(@QueryParam("token") String token,@QueryParam("product_sku") String product_sku) throws Exception {
		Map<String, Object> result = productServiceDelegate.selectAlsoLike(token, product_sku);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getAllStyle")
	@Produces("application/json;charset=utf-8")
	public Response getAllStyle(@QueryParam("token") String token) throws Exception {
		Map<String, Object> result = productServiceDelegate.getAllStyle(token);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getAllSize")
	@Produces("application/json;charset=utf-8")
	public Response getAllSize(@QueryParam("token") String token) throws Exception {
		Map<String, Object> result = productServiceDelegate.getAllSize(token);
		return Response.status(Status.OK).entity(result).build();

	}
	
	@GET
	@Path("/getAllColor")
	@Produces("application/json;charset=utf-8")
	public Response getAllColor(@QueryParam("token") String token) throws Exception {
		Map<String, Object> result = productServiceDelegate.getAllColor(token);
		return Response.status(Status.OK).entity(result).build();

	}	
	
	@GET
	@Path("/getPromoList")
	@Produces("application/json;charset=utf-8")
	public Response getPromoList(@QueryParam("token") String token) throws Exception {
		Map<String, Object> result = productServiceDelegate.getPromoList(token);
		return Response.status(Status.OK).entity(result).build();

	}		
}
