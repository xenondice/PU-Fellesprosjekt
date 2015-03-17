package server_client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import com.mysql.jdbc.UpdatableResultSet;

import room_booking.Room;
import room_booking.RoomBookingHandler;
import calendar.Calendar;
import calendar.CalendarEntry;
import calendar.CalendarEntryBuilder;
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
import exceptions.RoomDoesNotExistException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import exceptions.UsernameAlreadyExistsException;
import exceptions.WrongPasswordException;
import user.Group;
import user.User;

public class RequestHandler{		
	// TODO add functions for 'get all rooms' 'get all events' 'get all notifications' (for user) etc.
	
	// TODO add command 'add-group-to-group' (in server_client commands)
		
	// TODO check colisions/overlappings of entries???
	
	// TODO should not be able to use commands when not logged in!
	
	
	private static DataBaseManager dbm;
	private static ServerSocket server;
	private static Set<ServerClientHandler> currently_connected;
	private static RoomBookingHandler rbh;

	
	public static int PORT = 56692;
	public static final long CHECK_FOR_EXPECTED_INPUT_INTERVAL = 100;
	public static final long WAIT_BEFORE_TIMOUT = 1440000;
	
	
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
			if (handler.getUsername() != null && handler.getUsername().equals(username)) return handler;
		}
		
		return null;
	}
	
	/**
	 * removes the user from the "online set"
	 * @param client
	 */
	public static void disconnectUser(ServerClientHandler client) {
		
		System.out.print("Disconnecting ");
		
		if (client.getUsername() == null ) System.out.println("not identified client");
		else System.out.println(client.getUsername());
		
		currently_connected.remove(client);
	}
	
	/**
	 * send message to a user if online
	 * @param message
	 * @param entry_id
	 */
	private static void sendIfActive(String username, String message) {
		
		ServerClientHandler handler = getUserHandlerIfActive(username);
		
		if (handler != null && handler.getUsername() != null) {
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
		usernames = dbm.getInvitedUsersForEntry(entry_id);
		
		if (usernames != null){
			for (String username : usernames) {
				notify(username, message);
			}
		}
	}
	
	/**
	 * Notify an user with a notification
	 * @param username
	 * @param entry_id
	 * @param message
	 * @return
	 */
	public static boolean notify(String username, String message) {
				
		NotificationBuilder notification_builder = new NotificationBuilder();
		notification_builder.setDescription(message);
		notification_builder.setOpened(false);
		notification_builder.setTime(System.currentTimeMillis());
		notification_builder.setUsername(username);

		try {
			if(dbm.addNotification(notification_builder.build())){
				sendIfActive(username, "You have a new message! Type \"inbox\" to see.");
				return true;
			}else{
				return false;
			}
		} catch (UserDoesNotExistException e) {
			return false;
		}

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
	private synchronized static boolean invite(String username, long entry_id, boolean is_going) throws EntryDoesNotExistException, UserDoesNotExistException{
		
		InvitationBuilder invitation_builder = new InvitationBuilder();
		invitation_builder.setEntry_id(entry_id);
		invitation_builder.setUsername(username);
		invitation_builder.setGoing(is_going);
		invitation_builder.setShowing(true);
		
		boolean result;
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
		existing_user = dbm.getUser(username);
			
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
		return ! (username == null ||  username.equals(""));
	}
	
	/**
	 * checks whether the requestor is a valid user. (not null and username is not null and he exists in the db)
	 * @param requestor
	 * @throws SessionExpiredException
	 * @throws UserDoesNotExistException 
	 */
	private static void validate(String requestor) throws SessionExpiredException {
		
		if (requestor == null || ! isValidUsername(requestor)) {
			System.out.println("Request from unverified user denied (not valid name "+requestor+")");
			throw new SessionExpiredException();
		}
		
		try {
			dbm.getUser(requestor);
		} catch (UserDoesNotExistException e) {
			System.out.println("Request from unverified user denied (user not in DB)");
			throw new SessionExpiredException();
		}
		
		System.out.println("Request from user " + requestor + " validated");
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
		return dbm.addUser(user);
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
	public static boolean editUser(String requestor, User updated_user) throws UserDoesNotExistException, SessionExpiredException, HasNotTheRightsException {
		
		validate(requestor);
		
		if (!updated_user.getUsername().equals(requestor)) {
			throw new HasNotTheRightsException();
		} else if (updated_user.getUsername() == null) {
			return false;
		}
		
		return dbm.editUser(updated_user);
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
	public synchronized static boolean makeAdmin(String requestor, String username, long entry_id) throws HasNotTheRightsException, EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException {
		
		validate(requestor);

		if (!dbm.isAdmin(requestor, entry_id)) {
			throw new HasNotTheRightsException();
		}

		if (dbm.makeAdmin(username, entry_id)) {
			notify(username, "You are now admin to the entry with the id "+ entry_id);
			return true;
		} else {
			return false;
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
	 * @throws RoomDoesNotExistException 
	 * @throws RoomAlreadyBookedException 
	 */
	public synchronized static boolean createEntry(String requestor, CalendarEntry entry) throws UserDoesNotExistException, SessionExpiredException, RoomDoesNotExistException, RoomAlreadyBookedException {
		
		validate(requestor);

		if (entry == null)
			return false;

		try {
			long entry_id = dbm.addEntry(entry);

			if (entry_id > 0) {
				dbm.makeAdmin(entry.getCreator(), entry_id);
				invite(requestor, entry_id, true);
				if(entry.getRoomID() != null && ! entry.getRoomID().equals("")){
					rbh.bookRoom(entry.getRoomID(), entry.getStartTime(), entry.getEndTime(), entry_id);
				}
				
				return true;
			} else {
				return false;
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
	public synchronized static boolean deleteEntry(String requestor, long entry_id) throws SessionExpiredException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException {
		
		validate(requestor);

		boolean result;
		Set<String> invited_users = null;

		if (!dbm.isAdmin(requestor, entry_id)) {
			throw new HasNotTheRightsException();
		}

		try {
			invited_users = dbm.getInvitedUsersForEntry(entry_id);
		} catch (Exception e) {
		}

		result = dbm.deleteEntry(requestor, entry_id);

		if (result && invited_users != null) {
			for (String username : invited_users) {
				notify(username, "The entry " + entry_id
						+ " has just been deleted!");
			}
		}

		return result;
	}
	
	/**
	 * edit an already existing entry</br>
	 * All attributes that are 'null' will be kept from the old entry.
	 * @param requestor
	 * @param newEntry
	 * @return
	 * @throws EntryDoesNotExistException
	 * @throws HasNotTheRightsException
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException
	 * @throws RoomAlreadyBookedException 
	 * @throws RoomDoesNotExistException 
	 */
	public synchronized static boolean editEntry(String requestor, CalendarEntry new_entry) throws EntryDoesNotExistException, HasNotTheRightsException, UserDoesNotExistException, SessionExpiredException, RoomAlreadyBookedException, RoomDoesNotExistException {
		
		validate(requestor);
		
		if(new_entry == null || new_entry.getEntryID() <= 0){
			return false;
		}
			dbm.checkIfisAdmin(requestor, new_entry.getEntryID());
			CalendarEntry old_entry = dbm.getEntry(new_entry.getEntryID());
			
			CalendarEntryBuilder eb = new CalendarEntryBuilder(old_entry);
			
			// update the entry
			if(new_entry.getStartTime() > 0){ eb.setStartTime(new_entry.getStartTime());}
			if(new_entry.getEndTime() > 0){ eb.setEndTime(new_entry.getEndTime());}
			if(new_entry.getDescription() != null){eb.setDescription(new_entry.getDescription());}
			if(new_entry.getLocation() != null){eb.setLocation(new_entry.getLocation());}
			if(new_entry.getRoomID() != null){eb.setRoomID(new_entry.getRoomID());}
			
			CalendarEntry new_entry_final = eb.build();
			System.out.println(old_entry);
			System.out.println(new_entry_final);
			updateRoomReservation(old_entry, new_entry_final);
			
						
			if(dbm.editEntry(new_entry_final, requestor)){
				provideUpdate(new_entry_final.getEntryID(), "The entry information has changed!");
				return true;
			}else{
				return false;
			}
		
	}
	
	/**
	 * 
	 * @param old_entry
	 * @param new_entry
	 * @return
	 * @throws RoomAlreadyBookedException
	 * @throws RoomDoesNotExistException
	 */
	private static boolean updateRoomReservation(CalendarEntry old_entry, CalendarEntry new_entry) throws RoomAlreadyBookedException, RoomDoesNotExistException{
		boolean change_room_reservation = old_entry.getStartTime() != new_entry.getStartTime() 
										|| old_entry.getEndTime() != new_entry.getEndTime() 
										|| old_entry.getRoomID() != new_entry.getRoomID();
		
		
		if(change_room_reservation){
			long entry_id = new_entry.getEntryID();
			
			// remove old reservation
			rbh.releaseRoomEntry(old_entry.getRoomID(), entry_id);
				
			// add new reservation
			rbh.bookRoom(new_entry.getRoomID(), new_entry.getStartTime(), new_entry.getEndTime(), entry_id);
			
		}
		return true;
	}
	
	/**
	 * deletes the Invitation to the entry for the user.
	 * Note that the creator of an entry can not be kicked
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
	public synchronized static boolean kickUserFromEntry(String requestor, String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException, HasNotTheRightsException, InvitationDoesNotExistException {
		
		validate(requestor);
		
			if (dbm.isCreator(username, entry_id))
				throw new HasNotTheRightsException();
	
			if (!dbm.isAllowedToEdit(requestor, entry_id))
				throw new HasNotTheRightsException();
	
			if (dbm.deleteInvitation(requestor, entry_id)) {
				boolean allOk = true;

				if (dbm.isAdmin(username, entry_id))
					allOk = dbm.revokeAdmin(username, entry_id);
				
				notify(username, "You have just been kicked from the entry "+entry_id+"!");
				
				return allOk;
			} else
				return false;
		
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
	public synchronized static boolean kickGroupFromEntry(String requestor, String groupname, long entry_id) throws GroupDoesNotExistException, UserInGroupDoesNotExistsException, EntryDoesNotExistException, SessionExpiredException, UserDoesNotExistException, HasNotTheRightsException {
		
		validate(requestor);
		
			Group group = dbm.getGroup(groupname);
			
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
	public static boolean inviteUserToEntry(String requestor, String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException {
		
		validate(requestor);
		
		if (!dbm.isAllowedToEdit(requestor, entry_id)){
			throw new HasNotTheRightsException();
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
	public synchronized static boolean inviteGroupToEntry(String requestor, String groupname, long entry_id) throws GroupDoesNotExistException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException {
		
		validate(requestor);
		
		Group group;
			if (!dbm.isAllowedToEdit(requestor, entry_id)){
				throw new HasNotTheRightsException();
			}
			
			group = dbm.getGroup(groupname);
		
		
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
	
	// TODO make it more consistant to use the groups. Eg new table in DB linking groups to other groups.
	
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
	public static boolean createGroup(String requestor, Group group) throws UserDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException, SessionExpiredException {
		
		validate(requestor);
		
		return dbm.addGroup(group);
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
	public static boolean addUserToGroup(String requestor, String username, String groupname) throws UserDoesNotExistException, GroupDoesNotExistException, SessionExpiredException {
		
		validate(requestor);		

		if(dbm.addUserToGroup(username, groupname)) {
			notify(username, "You have been added to the group "+groupname);
			return true;
		} else
			return false;
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
	public static boolean addGroupToGroup(String requestor, String invited_groupname, String invitee_groupname) throws UserDoesNotExistException, GroupDoesNotExistException, SessionExpiredException, HasNotTheRightsException {
		
		boolean could_add_all = true;
		for (User user : dbm.getGroup(invited_groupname).getUsers()){
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
	public static boolean removeUserFromGroup(String requestor, String username, String groupname) throws GroupDoesNotExistException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException {
		
		validate(requestor);
		
		return dbm.removeUserFromGroup(username, groupname);
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
	public static boolean removeGroupFromGroup(String requestor, Group kickedGroup, String groupname) throws GroupDoesNotExistException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException {
		
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
	 * @throws RoomDoesNotExistException 
	 */
	public static boolean bookRoom(String requestor, String room_id, long start_time, long end_time, long entry_id) throws RoomAlreadyBookedException, SessionExpiredException, RoomDoesNotExistException {
		
		validate(requestor);
		
		return rbh.bookRoom(room_id, start_time, end_time, entry_id);
	}
	
//	/**
//	 * cancels all bookings overlapping with the given timeperiod.
//	 * @param room
//	 * @param startTime
//	 * @param endTime
//	 * @throws SessionExpiredException 
//	 */
//	public static boolean releaseRoom(String requestor, Room room, long start_time, long end_time) throws SessionExpiredException {
//		
//		validate(requestor);
//		
//		return rbh.releaseRoom(room, start_time, end_time);
//	}
	
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
	public static CalendarEntry getEntry(String requestor, long entry_id) throws SessionExpiredException, EntryDoesNotExistException {
		
		validate(requestor);
		
		return dbm.getEntry(entry_id);
	}
	
	/**
	 * Get the calendar of a given user
	 * @param requestor
	 * @return a calendar containing all entries the suer can see
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException
	 */
	public static HashSet<CalendarEntry> getAllEntriesForUser(String requestor) throws UserDoesNotExistException, SessionExpiredException {
		
		validate(requestor);
		
		return dbm.getAllEntriesForUser(requestor);
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
	public static boolean invitationAnswer(String requestor, long entry_id, boolean going, boolean showing) throws EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException, InvitationDoesNotExistException {
		
		validate(requestor);
		
		boolean allOk = true;
		if(going){
			allOk = dbm.going(requestor, entry_id);
		}else{
			allOk = dbm.notGoing(requestor, entry_id);
			String creator = dbm.getEntry(entry_id).getCreator();
			notify(creator, requestor+"refused to participate in the event with id "+ entry_id);
		}
		
		if(showing){
			allOk = dbm.allowToSee(requestor, entry_id) && allOk;
		}else{
			allOk = dbm.hideEvent(requestor, entry_id) && allOk;
		}
		
		return allOk;
	}
	
	/**
	 * returns all notifications for the user.
	 * @param username
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException 
	 */
	public static HashSet<Notification> getNotifications(String requestor) throws UserDoesNotExistException, SessionExpiredException {
		
		validate(requestor);
		
		return dbm.getNotificationsForUser(requestor);
	}
	
	/**
	 * returns all invitations the user has.
	 * @param username
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException 
	 */
	public static HashSet<Invitation> getInvitations(String requestor) throws UserDoesNotExistException, SessionExpiredException {
		
		validate(requestor);
		
		return dbm.getInvitationsForUser(requestor);
	}
}
