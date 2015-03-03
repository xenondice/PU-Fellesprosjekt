package server_client;

import calendar.Entry;
import dbms.DataBaseManager;
import user.Group;
import user.User;

public class RequestHandler {

	DataBaseManager dbm = new DataBaseManager();
	User user;
	
	public void logIn(User user){
	
		// TODO
		this.user =user;
	}

	
	public void createUser(String username, String name, String password, String salt, String email){
		User user = new User(username, name, password, salt, email);
		dbm.addUser(user);
	}
	
	public void editUser(User u){
		// TODO
	}
	
	
	public void createEvent(int entryID , long startTime, long endTime, String location, String description, boolean isActive, String roomID){
		Entry event = new Entry(entryID, startTime, endTime,location, description, isActive, roomID);
		dbm.addEntry(event, user.getUsername());
	}
	
	public void deleteEntry(Entry e){
		// TODO
	}
	
	public void editEntry(Entry e){
		// TODO
	}
	
	public void createGroup(User[] users, String name){
		Group group = new Group(users, name);
		dbm.addGroup(group);
	}
	
	public void addUserToGroup(User user, Group group){
		dbm.addUserToGroup(user.getUsername(), group.getName());
	}
	
	public void removeUserFromGroup(User user, Group group){
		dbm.removeUserFromGroup(user.getUsername(), group.getName());
	}
	
	public void createCalendar( User user){
//		dbm.createCalendar(user.getUsername());
	}
}
