package dao;

import java.sql.*;
import java.util.*;

import beans.User;

public class UserDAO {
	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	public User checkCredentials(String username, String password) throws SQLException {
		String query = "SELECT  Id, Username, Email, Name, Surname FROM User WHERE Username = ? AND Password = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
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
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			pstatement.setString(3, email);
			pstatement.setString(4, name);
			pstatement.setString(5, surname);
			pstatement.executeUpdate();
		}
	}

	public List<User> findEntrantsByGroupId(int groupId) throws SQLException {
		List<User> users = new ArrayList<User>();
		String query = "SELECT UserId FROM Entrant WHERE GroupId = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, groupId);
			try (ResultSet result = pstatement.executeQuery()) {
				while (result.next()) {
					int userId = result.getInt("UserId");
					User user = findUserById(userId);
					users.add(user);
				}
			}
		}
		return users;

	}

	private User findUserById(int userId) throws SQLException {
		String query = "SELECT Id, Username, Email, Name, Surname FROM User WHERE Id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery()) {
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

	public boolean isUsernameUnique(String username) throws SQLException {
		String query = "SELECT Username FROM User WHERE Username = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return true;
				return false;
			}
		}
	}

	public List<User> findAllUsersOrderedBySurname() throws SQLException {
		List<User> users = new ArrayList<User>();
		String query = "SELECT Id, Username, Email, Name, Surname FROM User ORDERED BY Surname";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			try (ResultSet result = pstatement.executeQuery()) {
				while (result.next()) {
					User user = new User();
					user.setId(result.getInt("Id"));
					user.setUsername(result.getString("Username"));
					user.setEmail(result.getString("Email"));
					user.setName(result.getString("Name"));
					user.setSurname(result.getString("Surname"));
					users.add(user);
				}
			}
		}
		return users;
	}
}