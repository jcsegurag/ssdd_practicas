package es.um.sisdist.videofaces.backend.dao.video;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import es.um.sisdist.videofaces.backend.dao.models.Video;
import es.um.sisdist.videofaces.backend.dao.models.Video.PROCESS_STATUS;

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
	public Optional<Video> getVideoById(String id) {
	    try{
			statement = con.prepareStatement("SELECT * FROM videos WHERE id=?");
	        statement.setInt(1, guestID);
			rs.next();
	        ResultSet rs = statement.executeQuery();
	        Video video;
	        while(rs.next()){
	        	
	            video.id = rs.getString(1);
	            video.userid = rs.getString(2);
	            video.date = rs.getString(3);
	            video.filename = rs.getString(4);
	            video.pstatus = rs.getInt(5);
	        } catch(Exception e){
	            System.out.print(e);
	        }
		
		return Optional.of(video);
		

		
	}

	@Override
	public InputStream getStreamForVideo(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PROCESS_STATUS getVideoStatus(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
