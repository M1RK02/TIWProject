package dao;

import java.sql.*;
import java.util.*;

import beans.Group;

public class GroupDAO {
	private Connection connection;

	public GroupDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Group> findActiveGroupsByCreatorId(int creatorId) throws SQLException {
		List<Group> groups = new ArrayList<Group>();
		String query = "SELECT * FROM `Group` WHERE CreatorId = ? AND DATE_ADD(CreationDate, INTERVAL Duration DAY) >= CURDATE()";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, creatorId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Group group = getGroupFromResult(result);
					groups.add(group);
				}
			}
		}
		return groups;
	}
	
	public List<Group> findActiveGroupsByEntrantId(int entrantId) throws SQLException {
		List<Group> groups = new ArrayList<Group>();
		String query = "SELECT * FROM `Group` JOIN Entrant ON `Group`.Id = Entrant.GroupId WHERE UserId = ? AND DATE_ADD(CreationDate, INTERVAL Duration DAY) >= CURDATE()";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, entrantId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Group group = getGroupFromResult(result);
					groups.add(group);
				}
			}
		}
		return groups;
	}

	public Group findGroupById(int groupId) throws SQLException {
		String query= "SELECT * FROM `Group` WHERE Id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, groupId);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					return getGroupFromResult(result);
				}
			}
		}
	}

	public void addGroup(Group group, List<Integer> entrantIds) throws SQLException {
       try {
            connection.setAutoCommit(false);
            
    		String query = "INSERT INTO `Group` (CreatorId, Title, Duration, MinEntrants, MaxEntrants) VALUES (?, ?, ?, ?, ?)";
    		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
    			pstatement.setInt(1, group.getCreatorId());
    			pstatement.setString(2, group.getTitle());
    			pstatement.setInt(3, group.getDuration());
    			pstatement.setInt(4, group.getMinEntrants());
    			pstatement.setInt(5, group.getMaxEntrants());
    			pstatement.executeUpdate();
    		}
    		
    		query = "SET @group_id = LAST_INSERT_ID()";
    		Statement statement = connection.createStatement();
    		statement.executeUpdate(query);
    		
    		query = "INSERT INTO Entrant (GroupId, UserId) VALUES ";
    		for(Integer userId : entrantIds) {
    			query += "(@group_id, " + userId + "), ";
    		}
    		query = query.substring(0, query.length()-2);
    		statement = connection.createStatement();
    		statement.executeUpdate(query);

            connection.commit();
        } catch (SQLException e) {
        	connection.rollback();
        	throw new SQLException();
        } finally {
        	connection.setAutoCommit(true);
        }
	}
	
	private Group getGroupFromResult(ResultSet result) throws SQLException{
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