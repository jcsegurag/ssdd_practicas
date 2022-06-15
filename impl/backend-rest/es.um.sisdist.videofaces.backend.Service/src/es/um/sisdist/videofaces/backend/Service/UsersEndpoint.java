package es.um.sisdist.videofaces.backend.Service;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Optional;

import java.util.logging.Logger;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import es.um.sisdist.videofaces.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.videofaces.backend.dao.models.Face;
import es.um.sisdist.videofaces.backend.dao.models.User;
import es.um.sisdist.videofaces.backend.dao.models.Video;
import es.um.sisdist.videofaces.models.FaceDTO;
import es.um.sisdist.videofaces.models.FaceDTOUtils;
import es.um.sisdist.videofaces.models.UserDTO;
import es.um.sisdist.videofaces.models.UserDTOUtils;
import es.um.sisdist.videofaces.models.VideoDTO;
import es.um.sisdist.videofaces.models.VideoDTOUtils;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

// POJO, no interface no extends

@Path("/users")
public class UsersEndpoint
{
    private AppLogicImpl impl = AppLogicImpl.getInstance();
    
    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserInfo(@PathParam("username") String username)
    {
    	return UserDTOUtils.toDTO(impl.getUserByEmail(username).orElse(null));    	
    }
    
    @POST
    @Path("/{userid}/videos")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadVideo(@FormDataParam("video") InputStream fileInputStream,
            @FormDataParam("video") FormDataContentDisposition fileMetaData, @PathParam ("userid") String uid) throws Exception
    {
    	System.out.println("ESTAMOS EN EL UPLOAD ENDPOINT ----------------------------------------------------------------------------");
    	String filename = fileMetaData.getFileName();
    	impl.uploadVideo(filename, uid, fileInputStream);
        return Response.ok(fileMetaData.getFileName()).build();
    }
    
    @GET
    @Path("/{userid}/videos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVideosUser(@PathParam("userid") String uid) {
    	LinkedList<Video> listaVideos = impl.getVideosByUid(uid);
    	LinkedList<VideoDTO> listaVideosDTO = new LinkedList<VideoDTO>();
    	for(Video v : listaVideos) {
    		listaVideosDTO.add(VideoDTOUtils.toDTO(v));
    	}
    	System.out.println("ESTAMOS EN USERS ANTES DE RESPONSE----------------------------------------------------------------------------"+uid);
    	
    	return Response.status(Response.Status.OK).entity(listaVideosDTO).type(MediaType.APPLICATION_JSON).build();
    	//return Response.ok(listaVideosDTO).status(Status.OK).build();

    }
    
    @GET
    @Path("/{userid}/videos/{idVideo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFacesByVid(@PathParam("idVideo") String vid) {
    	//Optional<Video> video = impl.getVideoById(vid);
    	
    	LinkedList<Face> listaFaces = impl.getFacesByVid(vid);
    	LinkedList<FaceDTO> listaFacesDTO = new LinkedList<FaceDTO>();
    	for(Face f : listaFaces) {
    		System.out.println("ESTAMOS EN EL LOOP DE LISTA FACES ----------------------------------------------------------------------------" + f.getFid());
    		listaFacesDTO.add(FaceDTOUtils.toDTO(f));
    	}
    	return Response.status(Response.Status.OK).entity(listaFacesDTO).type(MediaType.APPLICATION_JSON).build();
    }
    
    /*public VideoDTO getVideoInfo(@PathParam("uid") String uid)
    {
    	return VideoDTOUtils.toDTO(impl.getVideoByUid(uid))
    	return UserDTOUtils.toDTO(impl.getUserByEmail(username).orElse(null));    	
    }*/
}
