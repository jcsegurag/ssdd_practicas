package es.um.sisdist.videofaces.backend.Service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import es.um.sisdist.videofaces.backend.dao.models.User;

/*class AppLogicImplTest
{
	public static void main(String[] args) {
		
		AppLogicImplTest AppLogicImplTest = new AppLogicImplTest();
		try {
			System.out.println("Sleeping...");
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String idUser = "";
		try {
			
			Optional<User> idOpt = AppLogicImplTest.registerUser("jconesa", "a");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public Optional<User> registerUser(String username, String password) throws ClientProtocolException, IOException{
		String uri = "http://localhost:8080/rest/apiexterna/register";
		Optional<String> passwordHash = Optional.of(User.md5pass(password));
		if (!passwordHash.isPresent())
			return Optional.empty();
		String passwordRegister = passwordHash.get();
		System.out.println("Intentando registrar usuario...");
		
		HttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(uri);
		
		String json = "{\"username\":\"" + username + "\",\"password\":\"" + passwordHash + "\"}";
		StringEntity entity = new StringEntity(json);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(httpPost);
		System.out.println("Response = " + response.getStatusLine().getStatusCode());
		if(response.getStatusLine().getStatusCode()!=200)  return Optional.empty();
		String authToken = response.getFirstHeader("Auth-Token").getValue();
		System.out.println("Auth-Token = " + token);
		this.authToken = authToken;
		client.close();
		
	}
}*/
