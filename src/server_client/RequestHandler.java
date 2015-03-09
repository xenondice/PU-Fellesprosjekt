package server_client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import calendar.Entry;
import dbms.DataBaseManager;
import exceptions.EntryDoesNotExistException;
import exceptions.GroupAlreadyExistsException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import exceptions.UsernameAlreadyExistsException;
import user.Group;
import user.User;

public class RequestHandler{

	static DataBaseManager dbm = new DataBaseManager();
	public static final int port = 80;
	
	
	public static void logIn(User user){
		// TODO
	}
	
	public static void main(String[] args) {
		RequestHandler rh = new RequestHandler();
		try {
			ServerSocket ss = new ServerSocket(port);
			
			while (true){
				Socket so = ss.accept();
				ServerClientHandler sch = new ServerClientHandler(so);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	/* ===============
	 * User functions
	 *================*/ 
	
	
	public static void createUser(User user) throws UsernameAlreadyExistsException{
		dbm.addUser(user);
	}
	
	public static void editUser(User u) throws UserDoesNotExistException{
		dbm.editUser(u);
	}
	

	public static void makeAdmin(User admin, User newAdmin, Entry entry) throws HasNotTheRightsException, EntryDoesNotExistException, UserDoesNotExistException {
		dbm.makeAdmin(admin.getUsername(), newAdmin.getUsername(), entry.getEntryID());
	}
	
	
	/* ===============
	 * Entry functions
	 *================*/ 
	
	public static void createEntry(Entry e, User user) throws UserDoesNotExistException{
		dbm.addEntry(e, user.getUsername());
	}
	
	public static void deleteEntry(Entry e){
		dbm.deleteEntry(e.getEntryID());
	}
	
	public static void editEntry(Entry e, User user) throws EntryDoesNotExistException, HasNotTheRightsException, UserDoesNotExistException {
		dbm.editEntry(e, user.getUsername());
	}
	
	public static void kickUserFromEntry(User admin, User user, Entry entry) throws EntryDoesNotExistException, UserDoesNotExistException{
		dbm.hideEvent(user.getUsername(), entry.getEntryID());
	}
	
	public static void kickGroupFromEntry(User admin, Group group, Entry entry) throws GroupDoesNotExistException, UserInGroupDoesNotExistsException, EntryDoesNotExistException{
		dbm.hideEventGroup(group.getName(), entry.getEntryID());
	}	
	
	public static void inviteUserToEntry(User admin, User user, Entry entry) throws EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException{
		dbm.inviteUser(admin.getUsername(), user.getUsername(), entry.getEntryID());
	}
	
	public static void inviteGroupToEntry(User admin, Group group, Entry entry) throws GroupDoesNotExistException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException{
		dbm.inviteGroup(admin.getUsername(), group.getName(), entry.getEntryID());
	}
	
	/* ===============
	 * Group functions
	 *================*/ 
	
	public static void createGroup(Group group) throws UserDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException{
		dbm.addGroup(group);
	}
	
	public static void addUserToGroup(User user, Group group) throws UserDoesNotExistException, GroupDoesNotExistException{
		dbm.addUserToGroup(user.getUsername(), group.getName());
	}
	
	public static void removeUserFromGroup(User user, Group group) throws GroupDoesNotExistException{
		dbm.removeUserFromGroup(user.getUsername(), group.getName());
	}
	
	
	/* ===============
	 * 'Calendar' functions
	 *================*/ 
	
	public static void createCalendar( User user) throws UserDoesNotExistException{
		dbm.createCalendar(user.getUsername());
	}
	
	public static void invitationAnswer(User u, Entry e, boolean answer) throws EntryDoesNotExistException, UserDoesNotExistException{
		if (answer == true){
			dbm.going(u.getUsername(), e.getEntryID());
		}else{
			dbm.notGoing(u.getUsername(), e.getEntryID());
		}
	}

	
}
