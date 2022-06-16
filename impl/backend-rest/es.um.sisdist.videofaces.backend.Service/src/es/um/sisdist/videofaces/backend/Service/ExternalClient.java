package es.um.sisdist.videofaces.backend.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.glassfish.jersey.client.ClientConfig;
import java.time.LocalDateTime;
import es.um.sisdist.videofaces.backend.dao.models.User;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import es.um.sisdist.videofaces.backend.Service.impl.AppLogicImpl;
public class ExternalClient {
	public static void main(String[] args) {

			try {
				Optional<User> user = AppLogicImpl.getInstance().registerUser("jconesa@um.es", "jconesa", "a");
				Response response = ExternalClient.videoAuthToken(user.get());
				System.out.println("La autenticación es correcta: " +response.getStatus());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	
	// Intentamos registrar el usuario de esta manera pero luego pensamos que seria mas sencillo utilizar el de AppLogicImpl para extraer los datos del usuario

	/*public static void registerUser(String email, String username, String password)
			throws ClientProtocolException, IOException {
		String url = "http://localhost:8080/rest/register";
		String passwordRegister = password;
		System.out.println("Intentando registrar usuario...");

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		String json = "{\"email\":\"" + email + "\",\"name\":\"" + username + "\",\"password\":\"" + password + "\"}";
		StringEntity entity = new StringEntity(json);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(httpPost);
		System.out.println("Response = " + response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() != 201)
			return;
		else
			System.out.println("------------------TODO VA BIEN-------------");

		client.close();

	}*/
	
	public static Response videoAuthToken(User usuario) {
		String url = "http://localhost:8080/rest/apiexterna/videos";
		LocalDateTime localDate = LocalDateTime.now();
		String date = localDate.toString();
		String authToken = ExternalClient.generateAuthtoken(url, usuario.getTOKEN(), date);
		System.out.println("Intentando subir video con authToken...");
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
		WebTarget webTarget = client.target(url);
		String json = "{\"name\":\"" + usuario.getName() +"\"}";
		Response response = webTarget.request().header("User", usuario.getId()).header("Date", date)
                .header("Auth-Token", authToken).post(Entity.entity(json, MediaType.APPLICATION_JSON));
    	System.out.println("Token:"+usuario.getTOKEN());
    	System.out.println("Auth-Token: "+authToken);
    	System.out.println(url);
    	System.out.println(date);
		return response;
	}
	
	public static String generateAuthtoken(String url, String token, String date) {
		
			String cadenaAuth = url+date+token;
	    	String authHash = User.md5pass(cadenaAuth);
		    return authHash;
		    
		}
}