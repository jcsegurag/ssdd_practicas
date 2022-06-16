package es.um.sisdist.videofaces.backend.Service;

import java.util.Optional;

import es.um.sisdist.videofaces.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.videofaces.backend.dao.models.User;
import es.um.sisdist.videofaces.models.UserDTO;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.HttpHeaders; 
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import es.um.sisdist.videofaces.models.VideoDTO;
import es.um.sisdist.videofaces.models.VideoDTOUtils;
import jakarta.ws.rs.core.MultivaluedMap;
@Path("/apiexterna")
public class ExternalApiEndpoint {
    private AppLogicImpl impl = AppLogicImpl.getInstance();
    @POST
    @Path("/videos")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response videoAuth(@Context HttpHeaders headers) {
    	
    	MultivaluedMap<String, String> headerParams = headers.getRequestHeaders();
    	String userId = headerParams.get("User").get(0);
    	String authToken = headerParams.get("Auth-Token").get(0);
    	String date = headerParams.get("Date").get(0);
    	String url = "http://localhost:8080/rest/apiexterna/videos";
		if (impl.isAuthenticated(userId, authToken, url, date))
			return Response.ok().status(Status.OK).build();
		else
			return Response.status(Status.FORBIDDEN).build();

	}
}
