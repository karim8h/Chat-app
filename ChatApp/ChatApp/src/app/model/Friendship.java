package app.model;

import javax.persistence.Column;
import javax.persistence.Id;

public class Friendship {
	
	@Id
	private int id;
	@Column
	private int userId;
	@Column
	private int friendOf;
	
	
	public int getId() {return id;}
	public int getUserId() {return userId;}
	public int getFriendOf() {return friendOf;}
	
	public void setId(int id) {this.id = id;}
	public void setUserId(int userId) {this.userId = userId;}
	public void setFriendOf(int friendOf) {this.friendOf = friendOf;}	
	
}
