/**
 * 
 */
package es.um.sisdist.videofaces.backend.dao.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import es.um.sisdist.videofaces.backend.dao.models.User;

/**
 * @author dsevilla
 *
 */
@SuppressWarnings("deprecation")
public class SQLUserDAO implements IUserDAO
{
	Connection conn;
	
	public SQLUserDAO()
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
	public Optional<User> getUserById(String id)
	{
		PreparedStatement stm;
		try
		{
			stm = conn.prepareStatement("SELECT * from users WHERE id = ?");
			stm.setString(1, id);
			ResultSet result = stm.executeQuery();
			if (result.next())
				return createUser(result);
		} catch (SQLException e)
		{
			// Fallthrough
		}
		return Optional.empty();
	}

	@Override
	public Optional<User> getUserByEmail(String id)
	{
		PreparedStatement stm;
		try
		{
			stm = conn.prepareStatement("SELECT * from users WHERE email = ?");
			stm.setString(1, id);
			ResultSet result = stm.executeQuery();
			if (result.next())
				return createUser(result);
		} catch (SQLException e)
		{
			// Fallthrough
		}
		return Optional.empty();
	}

	private Optional<User> createUser(ResultSet result)
	{
		try
		{
			return Optional.of(new User(result.getString(1), // id
					result.getString(2), // email
					result.getString(3), // pwhash
					result.getString(4), // name
					result.getString(5), // token
					result.getInt(6)));  // visits
		} catch (SQLException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public Optional<User> createUser(String email, String username, String password) {
		// Get the max ID

		String queryID = "SELECT max(CAST(id AS UNSIGNED)) FROM users";
		PreparedStatement preparedStmtID;
		try {
			preparedStmtID = conn.prepareStatement(queryID);
			ResultSet rs = preparedStmtID.executeQuery();
			rs.next();
			String id;
			// Si no hay ninguna id asignamos al primer usuario la id 0, a partir de ahi cada usuario tendra la id del anterior más 1
			
			if(rs.getString(1) == null)
				id = "0";
			else
				id = String.valueOf(Long.valueOf(rs.getString(1)) + 1);
			
			System.out.println("-->" + id);

			// Query de inserción mysql
			String query = " insert into users (id, email, name, password_hash, token, visits)"
					+ " values (?, ?, ?, ?, ?, ?)";

			// Preparamos los datos que vamos a insertar en la query
			PreparedStatement preparedStmt;
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);
			preparedStmt.setString(2, email);
			preparedStmt.setString(3, username);
			preparedStmt.setString(4, User.md5pass(password));
			//String token = User.generateToken(id, username);
			String token = "alumno";
			preparedStmt.setString(5, token);
			// Numero de visitas
			preparedStmt.setInt(6, 0);
			preparedStmt.execute();
			return Optional.of(new User(id, email, User.md5pass(password), username, token, 0));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}

	public void deleteUsers() {
		String query = "delete from users";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
