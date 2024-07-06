package dao;

import java.sql.*;
import java.util.*;

import beans.Group;
import beans.User;

public class GroupDAO {
	private Connection connection;

	public GroupDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Group> findActiveGroupsByCreatorId(int creatorId) throws SQLException {
		List<Group> groups = new ArrayList<Group>();
		String query = "SELECT * FROM Group WHERE CreatorId = ? AND DATE_ADD(CreationDate, INTERVAL Duration DAY) < NOW()";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, creatorId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Group group = new Group();
					group.setId(result.getInt("Id"));
					group.setCreatorId(creatorId);
					group.setTitle(result.getString("Title"));
					group.setCreationDate(result.getDate("CreationDate"));
					group.setDuration(result.getInt("Duration"));
					group.setMinEntrants(result.getInt("MinEntrants"));
					group.setMaxEntrants(result.getInt("MaxEntrants"));
					groups.add(group);
				}
			}
		}
		return groups;
	}

	public Group findGroupById(int groupId) throws SQLException {
		String query= "SELECT * FROM Group WHERE Id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, groupId);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					Group group = new Group();
					group.setId(result.getInt("Id"));
					group.setCreatorId(result.getInt("CreatorId"));
					group.setTitle(result.getString("Title"));
					group.setCreationDate(result.getDate("CreationDate"));
					group.setDuration(result.getInt("Duration"));
					group.setMinEntrants(result.getInt("MinEntrants"));
					group.setMaxEntrants(result.getInt("MaxEntrants"));
					return group;
				}
			}
		}
	}

	public void addGroup(int creatorId, String title, int duration, int minEntrants, int maxEntrants, List<User> entrants) throws SQLException {
		String query = "INSERT INTO Group (CreatorId, Title, Duration, MinEntrants, MaxEntrants) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, creatorId);
			pstatement.setString(2, title);
			pstatement.setInt(3, duration);
			pstatement.setInt(4, minEntrants);
			pstatement.setInt(5, maxEntrants);
			pstatement.executeUpdate();
		}
	}
}