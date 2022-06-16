package es.um.sisdist.videofaces.backend.Service;

import java.util.Optional;

import es.um.sisdist.videofaces.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.videofaces.backend.dao.models.User;
import es.um.sisdist.videofaces.models.UserDTO;
import es.um.sisdist.videofaces.models.UserDTOUtils;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

// POJO, no interface no extends

@Path("/register")
public class RegisterEndpoint
{
    private AppLogicImpl impl = AppLogicImpl.getInstance();
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(UserDTO userDTO)
    {
    	//Optional<User> u 
        Optional<User> u = impl.checkRegister(userDTO.getEmail());
        if (u.isPresent())
        	// El usuario ya ha sido creado
        	return Response.status(Status.CONFLICT).build();
        // Registrar
		Optional<User> user = impl.registerUser(userDTO.getEmail(), userDTO.getName(), userDTO.getPassword());
		System.out.println(user.get().getEmail() + "|| " + user.get().getName() + " || " + user.get().getPassword_hash()
				+ " || " + user.get().getId());
		JsonObject value = Json.createObjectBuilder().add("userid", user.get().getId()).add("name", user.get().getName())
				.add("email", user.get().getEmail()).add("password", user.get().getPassword_hash()).build();
		
		return Response.ok(value).status(Status.CREATED).build();

    }
}
