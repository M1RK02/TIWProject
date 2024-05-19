package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
}