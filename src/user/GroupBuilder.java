package user;

import java.util.ArrayList;

import interfaces.Builder;

public class GroupBuilder implements Builder<Group> {
	
	ArrayList<User> users = new ArrayList<>();
	
	public GroupBuilder() {
		
	}
	
	public void addUser(User u){
		this.users.add(u);
	}
	

	@Override
	public Group build() {
		User[] u  = new User[users.size()];
		return new Group(users.toArray(u));
	}

}
