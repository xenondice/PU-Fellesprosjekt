package server_client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import room_booking.Room;
import room_booking.RoomBookingHandler;
import calendar.Calendar;
import calendar.CalendarEntry;
import calendar.Invitation;
import calendar.InvitationBuilder;
import calendar.Notification;
import calendar.NotificationBuilder;
import dbms.DataBaseManager;
import exceptions.EntryDoesNotExistException;
import exceptions.GroupAlreadyExistsException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.InvitationAlreadyExistsException;
import exceptions.InvitationDoesNotExistException;
import exceptions.RoomAlreadyBookedException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import exceptions.UsernameAlreadyExistsException;
import exceptions.WrongPasswordException;
import user.Group;
import user.User;

public class RequestHandler{
	
	// TODO which methods must be synchronized?
	// TODO synchronize all DatabaseManager functions.
	
	// TODO add functions for 'get all rooms' 'get all events' 'get all notifications' (for user) etc.
	
	// TODO add command 'add-group-to-group' (in server_client commands)
	
	// TODO make entryID in the notification Table an optional argument. (And update the java code accordingly).
	
	// TODO if neccessary: implement alarm stuff (change entry -> change alarm; command change alarm)
	
	private static DataBaseManager dbm;
	private static ServerSocket server;
	private static Set<ServerClientHandler> currently_connected;
	private static RoomBookingHandler rbh;

	
	public static int PORT = 56692;
	public static final long CHECK_FOR_EXPECTED_INPUT_INTERVAL = 100;
	public static final long WAIT_BEFORE_TIMOUT = 1440000;
	
	public static final Object ADD_DB_LOCK = new Object();
	
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
			rbh = new RoomBookingHandler(dbm);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't start server! Is the port available?");
			dispose();
			System.exit(-1);
		}
		
		System.out.println("Server started");
	}
	
	private static void dispose() {
		
		System.out.println("Shutting down server...");
		
		try {
			for (ServerClientHandler handler : currently_connected) handler.close();
			if (server != null) server.close();
			if (dbm != null) dbm.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Server down");
	}
	
	/* ===============
	 * User functions
	 *================*/ 
	
	private static void acceptClients() {
		
		System.out.println("Accepting new clients");
		
		try {
			while (!server.isClosed()) {
				
				Socket new_client = server.accept();
				
				System.out.println("New client connected");
				
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
	
	/**
	 * @param username
	 * @return the UserHandler if the user is online, else returns null.
	 */
	public static ServerClientHandler getUserHandlerIfActive(String username) {
		
		for (ServerClientHandler handler : currently_connected) {
			if (handler.getUser() != null && handler.getUser().getUsername().equals(username)) return handler;
		}
		
		return null;
	}
	
	/**
	 * removes the user from the "online set"
	 * @param client
	 */
	public static void disconnectUser(ServerClientHandler client) {
		
		System.out.print("Disconnecting ");
		
		if (client.getUser() == null ) System.out.println("not identified client");
		else System.out.println(client.getUser().getUsername());
		
		currently_connected.remove(client);
	}
	
	/**
	 * send message to a user if online
	 * @param message
	 * @param entry_id
	 */
	private static void sendIfActive(String username, String message) {
		
		ServerClientHandler handler = getUserHandlerIfActive(username);
		
		if (handler != null && handler.getUser() != null) {
			handler.sendNotification(message);
		}
	}
	
	/**
	 * Send an update to all users invited to an entry
	 * @param entry_id
	 * @param message
	 */
	public static void provideUpdate(long entry_id, String message) {
		
		Set<String> usernames;
		synchronized (ADD_DB_LOCK) {
			usernames = dbm.getInvitedUsersForEntry(entry_id);
		}
		
		if (usernames != null)
			for (String username : usernames) {
				notify(username, entry_id, message);
			}
	}
	
	/**
	 * Notify an user with a notification
	 * @param username
	 * @param entry_id
	 * @param message
	 * @return
	 */
	public static boolean notify(String username, long entry_id, String message) {
		
		// TODO give other name? (the function notify exists).
		
		NotificationBuilder notification_builder = new NotificationBuilder();
		notification_builder.setDescription(message);
		notification_builder.setOpened(false);
		notification_builder.setEntry_id(entry_id);
		notification_builder.setTime(System.currentTimeMillis());
		notification_builder.setUsername(username);
		
		boolean result;
		synchronized (ADD_DB_LOCK) {
			try {
				result = dbm.addNotification(notification_builder.build());
			} catch (EntryDoesNotExistException | UserDoesNotExistException e) {
				result = false;
			}
		}
		
		if (result) sendIfActive(username, "You have a new message! Type \"inbox\" to see.");
		return result;
	}
	
	/**
	 * Send an invitation to a user
	 * @param username
	 * @param entry_id
	 * @param is_going
	 * @return
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException
	 */
	private static boolean invite(String username, long entry_id, boolean is_going) throws EntryDoesNotExistException, UserDoesNotExistException{
		
		InvitationBuilder invitation_builder = new InvitationBuilder();
		invitation_builder.setEntry_id(entry_id);
		invitation_builder.setUsername(username);
		invitation_builder.setGoing(is_going);
		invitation_builder.setShowing(true);
		
		boolean result;
		synchronized (ADD_DB_LOCK) {
			try {
				result = dbm.addInvitation(invitation_builder.build());
			} catch (EntryDoesNotExistException | UserDoesNotExistException e) {
				result = false;
			} catch (InvitationAlreadyExistsException e){
				try {
				result = dbm.going(username, entry_id);
				result = dbm.allowToSee(username, entry_id) && result;
				} catch (InvitationDoesNotExistException e1) {
					// should never happen!
					result = false;
				}
			}
		}
		
		if (result) sendIfActive(username, "You have a new invitation! Type \"inbox\" to see.");
		return result;
	}
	
	/**
	 * checks if the password is correct for the user.
	 * @param username
	 * @param password
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws WrongPasswordException
	 */
	public static User logIn(String username, String password) throws UserDoesNotExistException, WrongPasswordException {
		
		User existing_user;
		synchronized (ADD_DB_LOCK) {
			existing_user = dbm.getUser(username);
		}
			
		if (password.equals(existing_user.getPassword())) {
			System.out.println("New user verified as " + existing_user.getUsername());
			return existing_user; 
			//TODO: Make better login
		}
			
		throw new WrongPasswordException();
	}
	
	/**
	 * checks whether the username is "legal". (not null and not empty)
	 * @param username
	 * @return
	 */
	private static boolean isValidUsername(String username){
		return username == null ||  username.equals("");
	}
	
	/**
	 * checks whether the requestor is a valid user. (not null and username is not null and he exists in the db)
	 * @param requestor
	 * @throws SessionExpiredException
	 * @throws UserDoesNotExistException 
	 */
	private static void validate(User requestor) throws SessionExpiredException {
		
		if (requestor == null || ! isValidUsername(requestor.getUsername())) {
			System.out.println("Request from unverified user denied");
			throw new SessionExpiredException();
		}
		
		synchronized (ADD_DB_LOCK) {
			try {
				dbm.getUser(requestor.getUsername());
			} catch (UserDoesNotExistException e) {
				System.out.println("Request from unverified user denied");
				throw new SessionExpiredException();
			}
		}
		
		System.out.println("Request from user " + requestor.getUsername() + " validated");
	}
	
	/**
	 * adds the user to the DB
	 * @param user
	 * @return
	 * @throws UsernameAlreadyExistsException
	 */
	public static boolean createUser(User user) throws UsernameAlreadyExistsException {
		
		if (user == null || user.getUsername() == null || user.getUsername().equals("")) {
			return false;
		}
		
		synchronized (ADD_DB_LOCK) {
			return dbm.addUser(user);
		}
	}
	
	/**
	 * edits the user. Note that the requestor and updated_user must have the same username.
	 * @param requestor
	 * @param updated_user
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException
	 * @throws HasNotTheRightsException
	 */
	public static boolean editUser(User requestor, User updated_user) throws UserDoesNotExistException, SessionExpiredException, HasNotTheRightsException {
		
		validate(requestor);
		
		if (!updated_user.getUsername().equals(requestor.getUsername())) {
			throw new HasNotTheRightsException();
		} else if (updated_user.getUsername() == null) {
			return false;
		}
		
		synchronized (ADD_DB_LOCK) {
			return dbm.editUser(updated_user);
		}
	}
	
	/**
	 * makes the user(name) admin for the given entry if the requestor itself is already admin.
	 * @param requestor
	 * @param username
	 * @param entry_id
	 * @return
	 * @throws HasNotTheRightsException if the requestor is not admin of the entry
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException
	 */
	public static boolean makeAdmin(User requestor, String username, long entry_id) throws HasNotTheRightsException, EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException {
		
		validate(requestor);
		
		synchronized (ADD_DB_LOCK) {
			if (!dbm.isAdmin(requestor.getUsername(), entry_id)) {
				throw new HasNotTheRightsException();
			}
			
			if(dbm.makeAdmin(username, entry_id)){
				notify(username, entry_id, "You are now admin to the entry with the id "+entry_id);
				return true;
			}else{ return false;}
		}
	}
	
	/* ===============
	 * CalendarEntry functions
	 *================*/
	
	/**
	 * adds the entry and makes the creator admin and creates an invitation for the creator-entry pair to the DB.
	 * 
	 * @param requestor
	 * @param entry
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException
	 */
	public static boolean createEntry(User requestor, CalendarEntry entry) throws UserDoesNotExistException, SessionExpiredException {
		
		validate(requestor);
		
		if (entry == null) return false;
		
		try {
			synchronized (ADD_DB_LOCK) {
				long entry_id = dbm.addEntry(entry);
				
				if (entry_id > -1) {
					dbm.makeAdmin(entry.getCreator(), entry_id);
					invite(requestor.getUsername(), entry_id, true);
					return true;
				} else {
					return false;
				}
			}
		} catch (EntryDoesNotExistException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * removes the entry from the DB iff the requestor has admin rights.
	 * And notifies all logged in users.
	 * @param requestor
	 * @param entry_id
	 * @return
	 * @throws SessionExpiredException
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 * @throws HasNotTheRightsException
	 */
	public static boolean deleteEntry(User requestor, long entry_id) throws SessionExpiredException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException {
		
		validate(requestor);
		
		boolean result;
		Set<String> invited_users = null;
		synchronized (ADD_DB_LOCK) {
			if (!dbm.isAdmin(requestor.getUsername(), entry_id)){
				throw new HasNotTheRightsException();
			}
			
			try {
				invited_users = dbm.getInvitedUsersForEntry(entry_id);
			} catch (Exception e) {
			}
			
			result = dbm.deleteEntry(requestor.getUsername(), entry_id);
		}
		
		if (result && invited_users != null){
			for (String username : invited_users){
				notify(username, entry_id, "The entry has just been deleted!");
			}
		}
		
		return result;
	}
	
	/**
	 * edit an already existing entry
	 * @param requestor
	 * @param newEntry
	 * @return
	 * @throws EntryDoesNotExistException
	 * @throws HasNotTheRightsException
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException
	 */
	public static boolean editEntry(User requestor, CalendarEntry new_entry) throws EntryDoesNotExistException, HasNotTheRightsException, UserDoesNotExistException, SessionExpiredException {
		
		validate(requestor);
		
		if(new_entry == null || new_entry.getEntryID() <= 0){
			return false;
		}
		
		boolean result;
		synchronized (ADD_DB_LOCK) {
			dbm.checkIfisAdmin(requestor.getUsername(), new_entry.getEntryID());
			
			result = dbm.editEntry(new_entry, requestor.getUsername());
		}
		
		if (result) provideUpdate(new_entry.getEntryID(), "The entry information has changed!");
		
		return result;
	}
	
	/**
	 * makes that the user(name) can not see the entry anymore.
	 * Note that requestor and username can not be the same.
	 * @param requestor
	 * @param username
	 * @param entry_id
	 * @return
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException
	 * @throws HasNotTheRightsException
	 * @throws InvitationDoesNotExistException
	 */
	public static boolean kickUserFromEntry(User requestor, String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException, HasNotTheRightsException, InvitationDoesNotExistException {
		
		validate(requestor);
		
		if(requestor.equals(username) || dbm.isCreator(username, entry_id)) {
			return false;
		}
		
		boolean result;
		synchronized (ADD_DB_LOCK) {
			if (!dbm.isAllowedToEdit(requestor.getUsername(), entry_id)) {
				throw new HasNotTheRightsException();
			}
			
			result = dbm.hideEvent(username, entry_id) && dbm.notGoing(username, entry_id);
			
			// remove adminrights if he is admin.
			if(dbm.isAdmin(username, entry_id)){
				result = dbm.revokeAdmin(username, entry_id) && result;
			}
		}
		
		if (result) notify(username, entry_id, "You have just been kicked from the entry!");
		
		return result;
	}
	
	/**
	 * the same as {@link RequestHandler#kickUserFromEntry(User, String, int)} but for all users in the group
	 * @param requestor
	 * @param groupname
	 * @param entry_id
	 * @return
	 * @throws GroupDoesNotExistException
	 * @throws UserInGroupDoesNotExistsException
	 * @throws EntryDoesNotExistException
	 * @throws SessionExpiredException
	 * @throws UserDoesNotExistException
	 * @throws HasNotTheRightsException
	 * @throws InvitationDoesNotExistException
	 */
	public static boolean kickGroupFromEntry(User requestor, String groupname, long entry_id) throws GroupDoesNotExistException, UserInGroupDoesNotExistsException, EntryDoesNotExistException, SessionExpiredException, UserDoesNotExistException, HasNotTheRightsException {
		
		Group group;
		synchronized (ADD_DB_LOCK) {
			group = dbm.getGroup(groupname);
		}
		
		// TODO cast long to integer somewhere. fix it.
		
		for (User user : group.getUsers()) {
			try {
				kickUserFromEntry(requestor, user.getUsername(), entry_id);
			} catch (InvitationDoesNotExistException e) {
				
			}	
		}
		
		return true;
	}
	
	/**
	 * invites a user to an entry.
	 * @param requestor
	 * @param username
	 * @param entry_id
	 * @return
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 * @throws HasNotTheRightsException
	 * @throws SessionExpiredException
	 * @throws InvitationAlreadyExistsException
	 */
	public static boolean inviteUserToEntry(User requestor, String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException {
		
		validate(requestor);
		
		synchronized (ADD_DB_LOCK) {
			if (!dbm.isAllowedToEdit(requestor.getUsername(), entry_id)){
				throw new HasNotTheRightsException();
			}
		}
		
		return invite(username, entry_id, false);
	}
	
	/**
	 * invite all users of a group.
	 * @param requestor
	 * @param groupname
	 * @param entry_id
	 * @return
	 * @throws GroupDoesNotExistException
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 * @throws HasNotTheRightsException
	 * @throws SessionExpiredException
	 * @throws InvitationAlreadyExistsException
	 */
	public static boolean inviteGroupToEntry(User requestor, String groupname, long entry_id) throws GroupDoesNotExistException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException {
		
		validate(requestor);
		
		Group group;
		synchronized (ADD_DB_LOCK) {
			if (!dbm.isAllowedToEdit(requestor.getUsername(), entry_id)){
				throw new HasNotTheRightsException();
			}
			
			group = dbm.getGroup(groupname);
		}
		
		boolean could_add_all = true;
		for (User user : group.getUsers()){
			if (!invite(user.getUsername(), entry_id, false)){
				could_add_all = false;
			}
		}
		
		return could_add_all;
	}
	
	/* ===============
	 * Group functions
	 *================*/
	
	/**
	 * adds the group to the DB
	 * @param requestor
	 * @param group
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws GroupAlreadyExistsException
	 * @throws UserInGroupDoesNotExistsException
	 * @throws SessionExpiredException
	 */
	public static boolean createGroup(User requestor, Group group) throws UserDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException, SessionExpiredException {
		
		validate(requestor);
		
		synchronized (ADD_DB_LOCK) {
			return dbm.addGroup(group);
		}
	}
	
	/**
	 * add a user to the group
	 * @param requestor
	 * @param username
	 * @param groupname
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws GroupDoesNotExistException
	 * @throws SessionExpiredException
	 * @throws HasNotTheRightsException
	 */
	public static boolean addUserToGroup(User requestor, String username, String groupname) throws UserDoesNotExistException, GroupDoesNotExistException, SessionExpiredException {
		
		
		validate(requestor);
		
		synchronized (ADD_DB_LOCK) {
			if(dbm.addUserToGroup(username, groupname)){
				notify(username, -1, "You have been added to the group "+groupname);
				return true;
			}else{
				return false;
			}
		}
	}
	
	/**
	 * adds all users from one group to another group
	 * @param requestor
	 * @param invitedGroup
	 * @param inviteeGroupname
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws GroupDoesNotExistException
	 * @throws SessionExpiredException
	 * @throws HasNotTheRightsException
	 */
	public static boolean addGroupToGroup(User requestor, Group invited_group, String invitee_groupname) throws UserDoesNotExistException, GroupDoesNotExistException, SessionExpiredException, HasNotTheRightsException {
		
		boolean could_add_all = true;
		for (User user : invited_group.getUsers()){
			if (!addUserToGroup(requestor, user.getUsername(), invitee_groupname)){
				could_add_all = false;
			}
		}
		
		return could_add_all;
	}
	
	/**
	 * removes a user from a group
	 * @param requestor
	 * @param username
	 * @param groupname
	 * @return
	 * @throws GroupDoesNotExistException
	 * @throws SessionExpiredException
	 * @throws HasNotTheRightsException
	 * @throws UserDoesNotExistException
	 */
	public static boolean removeUserFromGroup(User requestor, String username, String groupname) throws GroupDoesNotExistException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException {
		
		validate(requestor);
		
		synchronized (ADD_DB_LOCK) {
			return dbm.removeUserFromGroup(username, groupname);
		}
	}
	
	/**
	 * removes all users from one group from another group.
	 * @param requestor
	 * @param kickedGroup
	 * @param groupname
	 * @return
	 * @throws GroupDoesNotExistException
	 * @throws SessionExpiredException
	 * @throws HasNotTheRightsException
	 * @throws UserDoesNotExistException
	 */
	public static boolean removeGroupFromGroup(User requestor, Group kickedGroup, String groupname) throws GroupDoesNotExistException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException {
		
		boolean could_add_all = true;
		for (User user : kickedGroup.getUsers()){
			if (!removeUserFromGroup(requestor, user.getUsername(), groupname)){
				could_add_all = false;
			}
		}
		
		return could_add_all;
	}

	/* ===============
	 * Room functions
	 *================*/ 
	
	/**
	 * books the given room for the given time period.
	 * @param room
	 * @param startTime
	 * @param endTime
	 * @param entryID
	 * @throws RoomAlreadyBookedException
	 * @throws UserDoesNotExistException 
	 * @throws SessionExpiredException 
	 */
	public static boolean bookRoom(User requestor, Room room, long start_time, long end_time, long entry_id) throws RoomAlreadyBookedException, SessionExpiredException {
		
		validate(requestor);
		
		synchronized (ADD_DB_LOCK) {
			return rbh.bookRoom(room, start_time, end_time, entry_id);
		}
	}
	
	/**
	 * cancels a booking.
	 * @param room
	 * @param startTime
	 * @param endTime
	 * @throws SessionExpiredException 
	 */
	public synchronized static boolean releaseRoom(User requestor, Room room, long start_time, long end_time) throws SessionExpiredException {
		
		validate(requestor);
		
		synchronized (ADD_DB_LOCK) {
			return rbh.releaseRoom(room, start_time, end_time);
		}
	}
	
	/* ===============
	 * 'Calendar' functions
	 *================*/ 
	
	/**
	 * Gets the specified entry
	 * @param requestor
	 * @param entry_id
	 * @return
	 * @throws SessionExpiredException
	 * @throws EntryDoesNotExistException
	 */
	public static CalendarEntry getEntry(User requestor, long entry_id) throws SessionExpiredException, EntryDoesNotExistException {
		
		validate(requestor);
		
		synchronized (ADD_DB_LOCK) {
			return dbm.getEntry(entry_id);
		}
	}
	
	/**
	 * Get the calendar of a given user
	 * @param requestor
	 * @return a calendar containing all entries the suer can see
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException
	 */
	public static Calendar createCalendar(User requestor) throws UserDoesNotExistException, SessionExpiredException {
		
		validate(requestor);
		
		synchronized (ADD_DB_LOCK) {
			return dbm.createCalendar(requestor.getUsername());
		}
	}
	
	/**
	 * 
	 * @param requestor
	 * @param entry_id
	 * @param answer
	 * @return
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException
	 * @throws InvitationDoesNotExistException 
	 */
	public static boolean invitationAnswer(User requestor, long entry_id, boolean answer) throws EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException, InvitationDoesNotExistException {
		
		validate(requestor);
		
		// shorter version:
		// return answer? dbm.going(requestor.getUsername(), entry_id) : dbm.notGoing(requestor.getUsername(), entry_id);
		
		synchronized (ADD_DB_LOCK) {
			if (answer){
				return dbm.going(requestor.getUsername(), entry_id);
			}else{
				String creator = dbm.getEntry(entry_id).getCreator();
				notify(creator, entry_id, requestor+" refused to participate in the event with id "+entry_id);
				return dbm.notGoing(requestor.getUsername(), entry_id);
			}
		}
	}
	
	/**
	 * returns all notifications for the user.
	 * @param username
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException 
	 */
	public static HashSet<Notification> getNotifications(User requestor) throws UserDoesNotExistException, SessionExpiredException {
		
		validate(requestor);
		
		synchronized (ADD_DB_LOCK) {
			return dbm.getNotificationsForUser(requestor.getUsername());
		}
	}
	
	/**
	 * returns all invitations the user has.
	 * @param username
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException 
	 */
	public static HashSet<Invitation> getInvitations(User requestor) throws UserDoesNotExistException, SessionExpiredException {
		
		validate(requestor);
		
		synchronized (ADD_DB_LOCK) {
			return dbm.getInvitationsForUser(requestor.getUsername());
		}
	}
}
