package server_client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import calendar.Entry;
import dbms.DataBaseManager;
import exceptions.EntryDoesNotExistException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.UserDoesNotExistException;
import exceptions.UsernameAlreadyExistsException;
import user.Group;
import user.User;

public class RequestHandler implements Runnable{

	DataBaseManager dbm = new DataBaseManager();
	
	
	
	
	public void logIn(User user){
		// TODO
	}
	
	@Override
	public void run() {
		
		try {
			ServerSocket ss = new ServerSocket(80);
			
			while (true){
				Socket so = ss.accept();
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	/* ===============
	 * User functions
	 *================*/ 
	
	
	public void createUser(User user) throws UsernameAlreadyExistsException{
		dbm.addUser(user);
	}
	
	public void editUser(User u) throws UserDoesNotExistException{
		dbm.editUser(u);
	}
	

	public void makeAdmin(User admin, User newAdmin, Entry entry) throws HasNotTheRightsException {
		dbm.makeAdmin(admin.getUsername(), newAdmin.getUsername(), entry.getEntryID());
	}
	
	
	/* ===============
	 * Entry functions
	 *================*/ 
	
	public void createEntry(Entry e, User user) throws UserDoesNotExistException{
		dbm.addEntry(e, user.getUsername());
	}
	
	public void deleteEntry(Entry e){
		dbm.deleteEntry(e.getEntryID());
	}
	
<<<<<<< HEAD
	public void editEntry(Entry e, User user) throws EntryDoesNotExistException, HasNotTheRightsException{
=======
	public void editEntry(Entry e, User user) throws EntryDoesNotExistException, HasNotTheRightsException {
>>>>>>> 02adc14e308a22b6c8938c88101df7ced5a0f480
		dbm.editEntry(e, user.getUsername());
	}
	
	public void kickUserFromEntry(User admin, User user, Entry entry){
		dbm.hideEvent(user.getUsername(), entry.getEntryID());
	}
	
	public void kickGroupFromEntry(User admin, Group group, Entry entry) throws GroupDoesNotExistException{
		dbm.hideEventGroup(group.getName(), entry.getEntryID());
	}	
	
	public void inviteUserToEntry(User admin, User user, Entry entry){
		dbm.inviteUser(admin.getUsername(), user.getUsername(), entry.getEntryID());
	}
	
	public void inviteGroupToEntry(User admin, Group group, Entry entry) throws GroupDoesNotExistException{
		dbm.inviteGroup(admin.getUsername(), group.getName(), entry.getEntryID());
	}
	
	/* ===============
	 * Group functions
	 *================*/ 
	
	public void createGroup(Group group){
		dbm.addGroup(group);
	}
	
	public void addUserToGroup(User user, Group group){
		dbm.addUserToGroup(user.getUsername(), group.getName());
	}
	
	public void removeUserFromGroup(User user, Group group){
		dbm.removeUserFromGroup(user.getUsername(), group.getName());
	}
	
	
	/* ===============
	 * 'Calendar' functions
	 *================*/ 
	
	public void createCalendar( User user) throws UserDoesNotExistException{
		dbm.createCalendar(user.getUsername());
	}
	
	public void invitationAnswer(User u, Entry e, boolean answer){
		if (answer == true){
			dbm.going(u.getUsername(), e.getEntryID());
		}else{
			dbm.notGoing(u.getUsername(), e.getEntryID());
		}
	}

	
}
