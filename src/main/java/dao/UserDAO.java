package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;

public class UserDAO {
	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	public User checkCredentials(String username, String password) throws SQLException {
		String query = "SELECT  Id, Username, Email, Name, Surname FROM User WHERE Username = ? AND Password = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("Id"));
					user.setUsername(result.getString("Username"));
					user.setEmail(result.getString("Email"));
					user.setName(result.getString("Name"));
					user.setSurname(result.getString("Surname"));
					return user;
				}
			}
		}
	}
	
	public void addUser(String username, String password, String email, String name, String surname) throws SQLException {
		String query = "INSERT INTO User (Username, Password, Email, Name, Surname) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			pstatement.setString(3, email);
			pstatement.setString(4, name);
			pstatement.setString(5, surname);
			pstatement.executeUpdate();
		}
	}
}