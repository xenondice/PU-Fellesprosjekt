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
			System.out.println("New user failed login");
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

	public synchronized static boolean makeAdmin(User requestor, User new_admin, CalendarEntry entry) throws HasNotTheRightsException, EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException {
		validate(requestor);
		if (!dbm.isAdmin(requestor.getUsername(), entry.getEntryID()))
			throw new HasNotTheRightsException();
		return dbm.makeAdmin(requestor.getUsername(), new_admin.getUsername(), entry.getEntryID());
	}
	
	/* ===============
	 * CalendarEntry functions
	 *================*/ 
	
	public synchronized static boolean createEntry(User requestor, CalendarEntry entry) throws UserDoesNotExistException, SessionExpiredException {
		validate(requestor);
		return dbm.addEntry(e, requestor.getUsername());
	}
	
	public synchronized static boolean deleteEntry(User requestor, CalendarEntry entry) throws SessionExpiredException, EntryDoesNotExistException, UserDoesNotExistException {
		validate(requestor);
		return dbm.deleteEntry(requestor.getUsername(), e.getEntryID());
	}
	
	public synchronized static boolean editEntry(User requestor, CalendarEntry entry) throws EntryDoesNotExistException, HasNotTheRightsException, UserDoesNotExistException, SessionExpiredException {
		validate(requestor);
		return dbm.editEntry(e, requestor.getUsername());
	}
	
	public synchronized static boolean kickUserFromEntry(User requestor, CalendarEntry entry) throws EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException {
		validate(requestor);
		return dbm.hideEvent(requestor.getUsername(), calendarEntry.getEntryID());
	}
	
	public synchronized static boolean kickGroupFromEntry(User requestor, Group group, CalendarEntry entry) throws GroupDoesNotExistException, UserInGroupDoesNotExistsException, EntryDoesNotExistException, SessionExpiredException {
		validate(requestor);
		return dbm.hideEventGroup(group.getName(), calendarEntry.getEntryID());
	}	
	
	public synchronized static void inviteUserToEntry(User requestor, User user, CalendarEntry entry) throws EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException {
		validate(requestor);
		dbm.inviteUser(admin.getUsername(), user.getUsername(), calendarEntry.getEntryID());
	}
	
	public synchronized static void inviteGroupToEntry(User requestor, Group group, CalendarEntry entry) throws GroupDoesNotExistException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException {
		validate(requestor);
		dbm.inviteGroup(admin.getUsername(), group.getName(), calendarEntry.getEntryID());
	}
	
	/* ===============
	 * Group functions
	 *================*/ 
	
	public synchronized static void createGroup(User requestor, Group group) throws UserDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException, SessionExpiredException {
		dbm.addGroup(group);
	}
	
	public synchronized static void addUserToGroup(User requestor, User user, Group group) throws UserDoesNotExistException, GroupDoesNotExistException, SessionExpiredException {
		dbm.addUserToGroup(user.getUsername(), group.getName());
	}
	
	public synchronized static void removeUserFromGroup(User requestor, User user, Group group) throws GroupDoesNotExistException, SessionExpiredException {
		dbm.removeUserFromGroup(user.getUsername(), group.getName());
	}
	
	
	/* ===============
	 * 'Calendar' functions
	 *================*/ 
	
	public synchronized static void createCalendar(User requestor) throws UserDoesNotExistException, SessionExpiredException {
		dbm.createCalendar(user.getUsername());
	}
	
	public synchronized static void invitationAnswer(User requestor, CalendarEntry entry, boolean answer) throws EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException {
		if (answer == true){
			dbm.going(u.getUsername(), e.getEntryID());
		}else{
			dbm.notGoing(u.getUsername(), e.getEntryID());
		}
	}
}
