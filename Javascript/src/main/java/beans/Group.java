package beans;

import java.sql.Date;

public class Group {
	private int id;
	private int creatorId;
	private String title;
	private Date creationDate;
	private int duration;
	private int minEntrants;
	private int maxEntrants;

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getCreatorId() {
		return creatorId;
	}
	
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public int getMinEntrants() {
		return minEntrants;
	}
	
	public void setMinEntrants(int minEntrants) {
		this.minEntrants = minEntrants;
	}
	
	public int getMaxEntrants() {
		return maxEntrants;
	}
	
	public void setMaxEntrants(int maxEntrants) {
		this.maxEntrants = maxEntrants;
	}
}