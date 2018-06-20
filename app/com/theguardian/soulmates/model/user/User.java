package com.theguardian.soulmates.model.user;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity("users")
@Indexes(
    @Index(fields = @Field("username"))
)
public class User {
    @Id
    private ObjectId id;
    private String username;
    private String password;
	
    public User() {
    	//required by ORM
	}
    
    public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
    
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public ObjectId getId() {
		return id;
	}

}
