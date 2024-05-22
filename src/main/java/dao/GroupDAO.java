package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import beans.Group;

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
	//l'ho fatto un po' diverso, seguendo "TOPICdao" dell'esempio della bacheca messaggi
	public Group findGroupById(int GroupId) throws SQLException {
		Group group= null;
		String query= "SELECT * FROM Group WHERE id= ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, GroupId);
			result= pstatement.executeQuery(); 
				while (result.next()) {
					group = new Group();
					group.setId(result.getInt("id"));
					group.setCreatorId(result.getInt("creatorId"));
					group.setTitle(result.getString("title"));
					group.setCreationDate(result.getDate("creationDate"));
					group.setDuration(result.getInt("duration"));
					group.setMinEntrants(result.getInt("minEntrants"));
					group.setMaxEntrants(result.getInt("maxEntrants"));
				}
			}catch (SQLException e) {
				throw new SQLException(e);
	
			}finally {
				try {
					if (result != null) {
						result.close();
					}
				} catch (Exception e1) {
					throw new SQLException("Cannot close result");
				}
				try {
					if (pstatement != null) {
						pstatement.close();
					}
				} catch (Exception e1) {
					throw new SQLException("Cannot close statement");
				}
		}
		
		return group;
	}
	
	
	public void addGroup(int id, int creatorId,String title, Date creationDate,int duration, int minEntrants, int maxEntrants) throws SQLException {
		String query = "INSERT INTO Group (id, creatorId, title, creationDate, duration, minEntrants, maxEntrants) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, id);
			pstatement.setInt(2, creatorId);
			pstatement.setString(3, title);
			pstatement.setInt(4, duration);
			pstatement.setObject(5, creationDate.toInstant().atZone(ZoneId.of("Europe/Rome")).toLocalDate());
			pstatement.setInt(6, minEntrants);
			pstatement.setInt(7, maxEntrants);
			pstatement.executeUpdate();
		}
	}
	
}