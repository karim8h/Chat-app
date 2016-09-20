package app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class UserProfile {
	
	@Id
	@GeneratedValue(generator="gen")
	@GenericGenerator(name="gen", strategy="increment")
	private int id;
	@Column
	private String firstName;
	@Column
	private String lastName;
	@Column
	private String userName;
	@Column
	private String password;
	
	public int getId() {return id;}
	public String getFirstName() {return firstName;}
	public String getLastName() {return lastName;}
	public String getUserName() {return userName;}
	public String getPassword() {return password;}
	
	public void setId(int id) {this.id = id;}
	public void setFirstName(String firstName) {this.firstName = firstName;}
	public void setLastName(String lastName) {this.lastName = lastName;}
	public void setUserName(String userName) {this.userName = userName;}
	public void setPassword(String password) {this.password = password;}
	
}
