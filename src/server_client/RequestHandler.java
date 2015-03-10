package server_client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import calendar.CalendarEntry;
import dbms.DataBaseManager;
import exceptions.EntryDoesNotExistException;
import exceptions.GroupAlreadyExistsException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import exceptions.UsernameAlreadyExistsException;
import exceptions.WrongPasswordException;
import user.Group;
import user.User;

public class RequestHandler{

	private static DataBaseManager dbm;
	private static ServerSocket server;
	private static Set<ServerClientHandler> currently_connected;
	
	public static final int PORT = 80;
	public static final long CHECK_FOR_EXPECTED_INPUT_INTERVAL = 500;
	public static final long WAIT_BEFORE_TIMOUT = 60000;
	public static final char STATUS_OK = 'a';
	public static final char STATUS_DISCONNECTED = 's';
	
	public static void main(String[] args) {
		init();
		acceptClients();
		dispose();
	}
	
	private static void init() {
		System.out.println("Staring server...");
		try {
			currently_connected = new HashSet<>();
			dbm = new DataBaseManager();
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			dispose();
			System.exit(-1);
		}
		System.out.println("Server started");
	}
	
	private static void dispose() {
		System.out.println("Shutting down server...");
		try {
			for (ServerClientHandler handler : currently_connected)
				handler.close();
			server.close();
			dbm.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Server down");
	}
	
	private static void acceptClients() {
		System.out.println("Accepting new clients");
		try {
			while (!server.isClosed()) {
				Socket new_client = server.accept();
				System.out.println("New client connected, awaiting identification...");
				ServerClientHandler client_handler = new ServerClientHandler(new_client);
				currently_connected.add(client_handler);
				Thread client_handler_thread = new Thread(client_handler);
				client_handler_thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			dispose();
			System.exit(-1);
		}
	}
	
	public static boolean isLoggedIn(String username) {
		for (ServerClientHandler handler : currently_connected) {
			if (handler.getUser() != null && handler.getUser().getUsername().equals(username)) return true;
		}
		return false;
	}
	
	public static void disconnectUser(ServerClientHandler client) {
		System.out.print("Disconnecting ");
		if (client.getUser() == null ) System.out.println("not identified client");
		else System.out.println(client.getUser().getUsername());
		currently_connected.remove(client);
	}
	
	/* ===============
	 * User functions
	 *================*/ 
	
	public static User logIn(User user) throws UserDoesNotExistException, WrongPasswordException {
		try {
			User existing_user = dbm.getUser(user.getUsername());
			if (user.getPassword().equals(existing_user.getPassword())) {
				System.out.println("New user verified as " + existing_user.getUsername());
				return existing_user; //TODO: Make better login
			}
			throw new WrongPasswordException();
		} catch (UserDoesNotExistException | WrongPasswordException e) {
			System.out.println("New user failed login");
			throw e;
		}
	}
	
	private synchronized static void validate(User requestor) throws SessionExpiredException {
		System.out.println("Request from user " + requestor.getUsername() + " validated");
		//TODO: Make validation function
	}
	
	public synchronized static boolean createUser(User user) throws UsernameAlreadyExistsException {
		return dbm.addUser(user);
	}
	
	public synchronized static boolean editUser(User requestor, User updated_user) throws UserDoesNotExistException, SessionExpiredException, HasNotTheRightsException {
		validate(requestor);
		if (updated_user.getUsername() != requestor.getUsername())
			throw new HasNotTheRightsException();
		return dbm.editUser(updated_user);
	}

	public synchronized static boolean makeAdmin(User requestor, String username, int entry_id) throws HasNotTheRightsException, EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException {
		validate(requestor);
		if (!dbm.isAdmin(requestor.getUsername(), entry_id))
			throw new HasNotTheRightsException();
		return dbm.makeAdmin(requestor.getUsername(), username, entry_id);
	}
	
	/* ===============
	 * CalendarEntry functions
	 *================*/
	
	public synchronized static boolean createEntry(User requestor, CalendarEntry entry) throws UserDoesNotExistException, SessionExpiredException {
		validate(requestor);
		return dbm.addEntry(entry, requestor.getUsername());
	}
	
	public synchronized static boolean deleteEntry(User requestor, int entry_id) throws SessionExpiredException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException {
		validate(requestor);
		if (!dbm.isAdmin(requestor.getUsername(), entry_id))
			throw new HasNotTheRightsException();
		return dbm.deleteEntry(requestor.getUsername(), entry_id);
	}
	
	public synchronized static boolean editEntry(User requestor, CalendarEntry entry) throws EntryDoesNotExistException, HasNotTheRightsException, UserDoesNotExistException, SessionExpiredException {
		validate(requestor);
		if (!dbm.isAllowedToEdit(requestor.getUsername(), entry.getEntryID()))
			throw new HasNotTheRightsException();
		return dbm.editEntry(entry, requestor.getUsername());
	}
	
	public synchronized static boolean kickUserFromEntry(User requestor, String username, int entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException, HasNotTheRightsException {
		validate(requestor);
		if (!dbm.isAdmin(requestor.getUsername(), entry_id))
			throw new HasNotTheRightsException();
		return dbm.hideEvent(username, entry_id);
	}
	
	public synchronized static boolean kickGroupFromEntry(User requestor, String groupname, int entry_id) throws GroupDoesNotExistException, UserInGroupDoesNotExistsException, EntryDoesNotExistException, SessionExpiredException, UserDoesNotExistException, HasNotTheRightsException {
		validate(requestor);
		if (!dbm.isAdmin(requestor.getUsername(), entry_id))
			throw new HasNotTheRightsException();
		return dbm.hideEventGroup(groupname, entry_id);
	}	
	
	public synchronized static boolean inviteUserToEntry(User requestor, String username, int entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException {
		validate(requestor);
		dbm.inviteUser(admin.getUsername(), user.getUsername(), calendarEntry.getEntryID());
	}
	
	public synchronized static boolean inviteGroupToEntry(User requestor, String groupname, int entry_id) throws GroupDoesNotExistException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException {
		validate(requestor);
		dbm.inviteGroup(admin.getUsername(), group.getName(), calendarEntry.getEntryID());
	}
	
	/* ===============
	 * Group functions
	 *================*/ 
	
	public synchronized static boolean createGroup(User requestor, Group group) throws UserDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException, SessionExpiredException {
		dbm.addGroup(group);
	}
	
	public synchronized static boolean addUserToGroup(User requestor, String username, String groupname) throws UserDoesNotExistException, GroupDoesNotExistException, SessionExpiredException {
		dbm.addUserToGroup(user.getUsername(), group.getName());
	}
	
	public synchronized static boolean removeUserFromGroup(User requestor, String username, String groupname) throws GroupDoesNotExistException, SessionExpiredException {
		dbm.removeUserFromGroup(user.getUsername(), group.getName());
	}
	
	
	/* ===============
	 * 'Calendar' functions
	 *================*/ 
	
	public synchronized static boolean createCalendar(User requestor) throws UserDoesNotExistException, SessionExpiredException {
		dbm.createCalendar(user.getUsername());
	}
	
	public synchronized static boolean invitationAnswer(User requestor, int entry_id, boolean answer) throws EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException {
		if (answer == true){
			dbm.going(u.getUsername(), e.getEntryID());
		}else{
			dbm.notGoing(u.getUsername(), e.getEntryID());
		}
	}
}
