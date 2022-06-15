package es.um.sisdist.videofaces.backend.dao.video;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Base64;
import java.nio.file.Files;
import es.um.sisdist.videofaces.backend.dao.models.Video;
import es.um.sisdist.videofaces.backend.dao.models.Video.PROCESS_STATUS;
import es.um.sisdist.videofaces.backend.dao.models.Face;
import java.sql.Blob;


public class SQLVideoDAO implements IVideoDAO {
	Connection conn;
	
	public SQLVideoDAO()
	{
        try
		{
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			
			// Si el nombre del host se pasa por environment, se usa aquí.
			// Si no, se usa localhost. Esto permite configurarlo de forma
			// sencilla para cuando se ejecute en el contenedor, y a la vez
			// se pueden hacer pruebas locales
			Optional<String> sqlServerName = 
					Optional.ofNullable(System.getenv("SQL_SERVER"));
			conn = DriverManager.getConnection("jdbc:mysql://" +
					sqlServerName.orElse("localhost") +
					"/videofaces?user=root&password=root");
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	@Override
	public Optional<Video> saveVideo(String userid, LocalDateTime date, String filename, InputStream inputStream) {
		// Get the max ID

		String queryID = "SELECT max(CAST(id AS UNSIGNED)) FROM videos";
		PreparedStatement preparedStmtID;
		try {
			preparedStmtID = conn.prepareStatement(queryID);
			ResultSet rs = preparedStmtID.executeQuery();
			rs.next();
			String id;
			// Si no hay ninguna id asignamos al primer video la id 0, a partir de ahi cada usuario tendra la id del anterior más 1
			
			if(rs.getString(1) == null)
				id = "0";
			else
				id = String.valueOf(Long.valueOf(rs.getString(1)) + 1);
			
			System.out.println("-->" + id);

			// Query de inserción mysql
			String query = " insert into videos (id, userid, date, filename, process_status, videodata)"
					+ " values (?, ?, ?, ?, ?, ?)";

			// Preparamos los datos que vamos a insertar en la query
			PreparedStatement preparedStmt;
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);
			preparedStmt.setString(2, userid);
			preparedStmt.setString(3, String.valueOf(date));
			preparedStmt.setString(4, filename);
			preparedStmt.setInt(5, 0);
			preparedStmt.setBlob(6, inputStream);
			preparedStmt.executeUpdate();
			
			return Optional.of(new Video(id, userid, PROCESS_STATUS.PROCESSING, String.valueOf(date), filename));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}
	@Override
	public void saveImage(String vid, InputStream datosImagen) {
		// Get the max ID

		String queryID = "SELECT max(CAST(id AS UNSIGNED)) FROM faces";
		PreparedStatement preparedStmtID;
		try {
			preparedStmtID = conn.prepareStatement(queryID);
			ResultSet rs = preparedStmtID.executeQuery();
			rs.next();
			String pid;
			
			if(rs.getString(1) == null)
				pid = "0";
			else
				pid = String.valueOf(Long.valueOf(rs.getString(1)) + 1);
			
			//System.out.println("-->" + pid);

			// Query de inserción mysql
			String query = " insert into faces (id, videoid, imagedata)"
					+ " values (?, ?, ?)";

			// Preparamos los datos que vamos a insertar en la query
			PreparedStatement preparedStmt;
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, pid);
			preparedStmt.setString(2, vid);
			preparedStmt.setBlob(3, datosImagen);
			preparedStmt.executeUpdate();
			
			return;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return;	
	}
	@Override
	public LinkedList<Video> getVideosByUid(String uid) {
		String queryID = "SELECT max(CAST(id AS UNSIGNED)) FROM videos";
		PreparedStatement preparedStmtID;
		try{
			PreparedStatement statement;
			statement = conn.prepareStatement("SELECT * FROM videos WHERE userid=?");
	        statement.setString(1, uid);
	        ResultSet rs = statement.executeQuery();
	        LinkedList<Video> listaVideos = new LinkedList<Video>();
	        while(rs.next()){
	        	String id = rs.getString("id");
	        	String date = rs.getString("date");
	        	String filename = rs.getString("filename");
	        	PROCESS_STATUS pstatus;
	        	if(rs.getInt("process_status") == 0)
	        		pstatus = PROCESS_STATUS.PROCESSING;
	        	else
	        		pstatus = PROCESS_STATUS.PROCESSED;
	            Video videoAdd = new Video(id, uid, pstatus, date, filename);
	            listaVideos.add(videoAdd);
	        }
	        return listaVideos;
	        } catch(Exception e){
	            System.out.print(e);
	        }
		return null;
		
	}
	
	@Override
	public LinkedList<Face> getFacesByVid(String vid) {
		String queryID = "SELECT max(CAST(id AS UNSIGNED)) FROM videos";
		PreparedStatement preparedStmtID;
		try{
			PreparedStatement statement;
			statement = conn.prepareStatement("SELECT * FROM faces WHERE videoid=? ORDER BY CAST(id as unsigned)");
	        statement.setString(1, vid);
	        ResultSet rs = statement.executeQuery();
	        LinkedList<Face> listaFaces = new LinkedList<Face>();
	        while(rs.next()){
	        	String fid = rs.getString("id");
	        	Blob imageData = rs.getBlob("imagedata");
	        	
	        	InputStream imageStream = imageData.getBinaryStream();
	        	String imageString = new String(imageData.getBytes(1, (int) imageData.length()));
	        	String encodedString = Base64.getEncoder().encodeToString(imageStream.readAllBytes());
	        	
	        	Face faceAdd = new Face(fid, vid, encodedString);
	        	listaFaces.add(faceAdd);
	        }
	        System.out.println("---------------------------------------------TAMAÑO LISTA FACES ---------"+listaFaces.size());
	        return listaFaces;
	        } catch(Exception e){
	            System.out.print(e);
	        }
		System.out.println("---------------------------------------------HA DEVUELTO NULL---------");
		return null;
	}
	/*@Override
	public LinkedList<Video> getVideosByUid(String uid) {
		PreparedStatement statement;
		try{
			statement = conn.prepareStatement("SELECT * FROM videos WHERE userid=?");
	        statement.setString(1, uid);
	        ResultSet rs = statement.executeQuery();
			rs.next();
	        LinkedList<Video> listaVideos = new LinkedList<Video>();
	        while(rs.next()){
	        	String id = rs.getString(1);
	        	String date = rs.getString(3);
	        	String filename = rs.getString(4);
	        	PROCESS_STATUS pstatus;
	        	if(rs.getString(5) == "0")
	        		pstatus = PROCESS_STATUS.PROCESSING;
	        	else
	        		pstatus = PROCESS_STATUS.PROCESSED;
	            Video videoAdd = new Video(id, uid, pstatus, date, filename);
	            listaVideos.add(videoAdd);
	        }
	        System.out.println("---------------------------------------------TAMAÑO LISTA VIDEOS ---------"+listaVideos.size());
	        return listaVideos;
	        } catch(Exception e){
	            System.out.print(e);
	        }
		System.out.println("---------------------------------------------HA DEVUELTO NULL---------");
		return null;
		
	}*/
	@Override
	public Optional<Video> getVideoById(String id) {
		PreparedStatement statement;
		try{
			statement = conn.prepareStatement("SELECT * FROM videos WHERE id=?");
	        statement.setString(1, id);
	        ResultSet rs = statement.executeQuery();
			//rs.next();
	        Video video;
	        while(rs.next()){
	        	String uid = rs.getString(2);
	        	String date = rs.getString(3);
	        	String filename = rs.getString(4);
	        	PROCESS_STATUS pstatus;
	        	if(rs.getString(5) == "0")
	        		pstatus = PROCESS_STATUS.PROCESSING;
	        	else
	        		pstatus = PROCESS_STATUS.PROCESSED;
	            return Optional.of(new Video(id, uid, pstatus, date, filename));
	        }
	        } catch(Exception e){
	            System.out.print(e);
	        }


		return Optional.empty();
		
	}
	
	/*@Override
	public Optional<Video> getVideoByUid(String uid) {
		PreparedStatement statement;
	    try{
			Video video;
	    	statement = conn.prepareStatement("SELECT * FROM videos WHERE userid=?");
			ResultSet rs = statement.executeQuery();
	        statement.setString(1, uid);
			rs.next();
	        while(rs.next()){
	        	
	            video.id = rs.getString(1);
	            video.userid = rs.getString(2);
	            video.date = rs.getString(3);
	            video.filename = rs.getString(4);
	            video.pstatus = rs.getInt(5);
	        return Optional.of(video);
	        } catch(Exception e){
	            System.out.print(e);
	        }		

		return Optional.empty();
		
	}*/

	@Override
	public InputStream getStreamForVideo(String id) {
		PreparedStatement statement;
		Blob videoData;
	    try{
			statement = conn.prepareStatement("SELECT * FROM videos WHERE id=?");
	        statement.setString(1, id);
	        ResultSet rs = statement.executeQuery();
			rs.next();
			videoData = rs.getBlob(6);
			return videoData.getBinaryStream();
	        } catch(Exception e){
	            System.out.print(e);
	        }		
	    return null;
	}

	@Override
	public PROCESS_STATUS getVideoStatus(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
