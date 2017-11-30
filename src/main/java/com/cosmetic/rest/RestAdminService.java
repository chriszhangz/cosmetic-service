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
import com.cosmetic.delegate.SecurityServiceDelegate;


@Service
@Path("/admin")
public class RestAdminService {

	@Autowired
	private AdminServiceDelegate adminServiceDelegate;
	@POST
	@Path("/importCsv")
	@Produces("application/json")
	public Response importCsv(MultipartFormDataInput input)throws Exception {
		Map<String, Object> result = adminServiceDelegate.importCsv(input);
		return Response.status(Status.OK).entity(result).build();

	}

}
