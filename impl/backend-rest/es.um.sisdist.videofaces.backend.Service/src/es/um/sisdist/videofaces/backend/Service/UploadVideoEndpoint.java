package es.um.sisdist.videofaces.backend.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import es.um.sisdist.videofaces.backend.Service.impl.AppLogicImpl;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
/*@Path("/users/{userid}/videos/")//Cambiar el path para coger el user id
//@Path("/uploadVideo")//Cambiar el path para coger el user id
public class UploadVideoEndpoint
{
    private AppLogicImpl impl = AppLogicImpl.getInstance();

    @POST
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
}*/
