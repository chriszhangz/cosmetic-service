package com.cosmetic.rest;

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

import com.cosmetic.delegate.SecurityServiceDelegate;


@Service
@Path("/security")
public class RestSecurityService {

	@Autowired
	private SecurityServiceDelegate securityServiceDelegate;
	@GET
	@Path("/getToken")
	@Produces("application/json")
	public Response getToken4TempId(@Context HttpServletRequest req)throws Exception {
		String tempid = securityServiceDelegate.getTempId();
		Map<String, Object> result = securityServiceDelegate.getToken4Tempid(tempid);
		return Response.status(Status.OK).entity(result).build();

	}

}
