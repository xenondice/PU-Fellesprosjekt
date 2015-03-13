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
	
	// TODO add notification feature where it is needed!
	
	// TODO how to answer an invitation? 
	
	private static DataBaseManager dbm;
	private static ServerSocket server;
	private static Set<ServerClientHandler> currently_connected;
	private static RoomBookingHandler rbh;

	
	public static final int PORT = 80;
	public static final long CHECK_FOR_EXPECTED_INPUT_INTERVAL = 100;
	public static final long WAIT_BEFORE_TIMOUT = 1440000;
	
	public static final Object ADD_ENTRY_LOCK = new Object();
	
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
			System.out.println(server.getLocalSocketAddress());
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
			for (ServerClientHandler handler : currently_connected)
				handler.close();
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
		// TODO does the request handler have to make sure that the client really disconnects? -- lukas
	}
	
	/**
	 * sends the message to all persons who can see the entry.
	 * @param message
	 * @param entry_id
	 */
	private static void sendToInvitedIfLoggedIn(String message, long entry_id) {
		Set<String> invitedUsernames = dbm.getInvitedUsersForEntry(entry_id);
		
		for (ServerClientHandler handler : currently_connected) {
			if (handler != null && handler.getUser() != null && invitedUsernames.contains(handler.getUser().getUsername())){
				handler.sendNotification(message);
			}
		}
		
		// To delete:
//		for (User user : users) {
//			ServerClientHandler handler = getUserHandlerIfActive(user.getUsername());
//			if (handler != null) {
//				handler.sendNotification(message);
//			}
//		}
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
		try {
			User existing_user = dbm.getUser(username);
			if (password.equals(existing_user.getPassword())) {
				System.out.println("New user verified as " + existing_user.getUsername());
				return existing_user; //TODO: Make better login
			}
			throw new WrongPasswordException();
		} catch (UserDoesNotExistException | WrongPasswordException e) {
			System.out.println("New user failed login");
			throw e;
		}
	}
	
	/**
	 * checks whether the requestor is a valid user. (not null and username is not null and he exists in the db)
	 * @param requestor
	 * @throws SessionExpiredException
	 * @throws UserDoesNotExistException 
	 */
	private synchronized static void validate(User requestor) throws SessionExpiredException, UserDoesNotExistException {
		dbm.getUser(requestor.getUsername());
		
		if (requestor == null || requestor.getUsername() == null || requestor.getUsername().equals("")) {
			System.out.println("Request from unverified user denied");
			throw new SessionExpiredException();
		}
		System.out.println("Request from user " + requestor.getUsername() + " validated");
		//TODO: Make validation function.
	}
	
	/**
	 * adds the user to the DB
	 * @param user
	 * @return
	 * @throws UsernameAlreadyExistsException
	 */
	public synchronized static boolean createUser(User user) throws UsernameAlreadyExistsException {
		if(user == null || user.getUsername() == null || user.getUsername().equals("")){
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
	public synchronized static boolean editUser(User requestor, User updated_user) throws UserDoesNotExistException, SessionExpiredException, HasNotTheRightsException {
		validate(requestor);
		if (! updated_user.getUsername().equals(requestor.getUsername())){
			throw new HasNotTheRightsException();
		}else if(updated_user.getUsername() == null){
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
	public synchronized static boolean makeAdmin(User requestor, String username, int entry_id) throws HasNotTheRightsException, EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException {
		validate(requestor);
		if (!dbm.isAdmin(requestor.getUsername(), entry_id)){
			throw new HasNotTheRightsException();
		}
		return dbm.makeAdmin(username, entry_id);
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
		if(entry == null){return false;}
		try {
			synchronized (ADD_ENTRY_LOCK) { // only one thread can be in this block

				long entry_id = dbm.addEntry(entry);
				if (entry_id > -1) {
					dbm.makeAdmin(entry.getCreator(), entry_id);
					// TODO is there someone invited if the entry is just created?
					sendToInvitedIfLoggedIn("You have just been invited to a newly created entry! Type \"inbox\" to see.",entry.getEntryID());
					dbm.addInvitation(new Invitation(true, true, entry
							.getCreator(), entry_id));
					
				}
				return true;
			}
		} catch (EntryDoesNotExistException | InvitationAlreadyExistsException e) {
			e.printStackTrace();
			return false;
		}
		
		//TODO: make sure CalendarEntryBuilder's field creator is set everywhere!!!
		
		
		//Can be deleted: 
//		boolean result = dbm.addEntry(entry); 
//		if (result) sendToInvitedIfLoggedIn("You have just been invited to a newly created entry! Type \"inbox\" to see.", entry.getEntryID());
//		return result;
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
	public synchronized static boolean deleteEntry(User requestor, int entry_id) throws SessionExpiredException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException {
		validate(requestor);
		if (!dbm.isAdmin(requestor.getUsername(), entry_id)){
			throw new HasNotTheRightsException();
		}
		boolean result = dbm.deleteEntry(requestor.getUsername(), entry_id);
		if (result) sendToInvitedIfLoggedIn("An entry you are invited to has just been deleted! Type \"inbox\" to see.", entry_id);
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
	public synchronized static boolean editEntry(User requestor, CalendarEntry newEntry) throws EntryDoesNotExistException, HasNotTheRightsException, UserDoesNotExistException, SessionExpiredException {
		validate(requestor);
		if(newEntry == null || newEntry.getEntryID() <= 0){
			return false;
		}
		dbm.checkIfisAdmin(requestor.getUsername(), newEntry.getEntryID());
		boolean result = dbm.editEntry(newEntry, requestor.getUsername());
		if (result) sendToInvitedIfLoggedIn("An entry you are invited to has just changed! Type \"inbox\" to see.", newEntry.getEntryID());
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
	public synchronized static boolean kickUserFromEntry(User requestor, String username, int entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException, HasNotTheRightsException, InvitationDoesNotExistException {
		validate(requestor);
		if(requestor.equals(username)){
			return false;
		}
		if (!dbm.isAllowedToEdit(requestor.getUsername(), entry_id)){
			throw new HasNotTheRightsException();
		}
		return dbm.hideEvent(username, entry_id); //TODO: Add send notification
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
	public synchronized static boolean kickGroupFromEntry(User requestor, String groupname, int entry_id) throws GroupDoesNotExistException, UserInGroupDoesNotExistsException, EntryDoesNotExistException, SessionExpiredException, UserDoesNotExistException, HasNotTheRightsException, InvitationDoesNotExistException {
		validate(requestor);
		if(! dbm.isAdmin(requestor.getUsername(), entry_id)){
			throw new HasNotTheRightsException();
		}
		for (User user : dbm.getGroup(groupname).getUsers()){
			RequestHandler.kickUserFromEntry(requestor, user.getUsername(), entry_id);
			
		}
		return true;
		//TODO: Add send notification
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
	public synchronized static boolean inviteUserToEntry(User requestor, String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException, InvitationAlreadyExistsException {
		validate(requestor);
		if (!dbm.isAllowedToEdit(requestor.getUsername(), entry_id)){
			throw new HasNotTheRightsException();
		}
		
		InvitationBuilder invitation_builder = new InvitationBuilder();
		invitation_builder.setEntry_id(entry_id);
		invitation_builder.setGoing(true);
		invitation_builder.setShowing(true);
		invitation_builder.setUsername(username);
		Invitation inv = invitation_builder.build();
		return dbm.addInvitation(inv);
		//TODO: Add send notification
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
	public synchronized static boolean inviteGroupToEntry(User requestor, String groupname, long entry_id) throws GroupDoesNotExistException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException, InvitationAlreadyExistsException {
		validate(requestor);
		if (!dbm.isAllowedToEdit(requestor.getUsername(), entry_id)){
			throw new HasNotTheRightsException();
		}
		
		boolean could_add_all = true;
		for (User user : dbm.getGroup(groupname).getUsers()) {
			InvitationBuilder invitation_builder = new InvitationBuilder();
			invitation_builder.setEntry_id(entry_id);
			invitation_builder.setGoing(true);
			invitation_builder.setShowing(true);
			invitation_builder.setUsername(user.getUsername());
			Invitation inv = invitation_builder.build();
			
			try{
				could_add_all = could_add_all && dbm.addInvitation(inv);
				// TODO may return the correct boolean.
			}catch(InvitationAlreadyExistsException e){	}
		}
		return could_add_all;
		 //TODO: Add send notification
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
	public synchronized static boolean createGroup(User requestor, Group group) throws UserDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException, SessionExpiredException {
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
	public synchronized static boolean addUserToGroup(User requestor, String username, String groupname) throws UserDoesNotExistException, GroupDoesNotExistException, SessionExpiredException, HasNotTheRightsException {
		validate(requestor);
		return dbm.addUserToGroup(username, groupname);
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
	public synchronized static boolean addGroupToGroup(User requestor, Group invitedGroup, String inviteeGroupname) throws UserDoesNotExistException, GroupDoesNotExistException, SessionExpiredException, HasNotTheRightsException {
		boolean could_add_all = true;
		for (User user : invitedGroup.getUsers()) {
			could_add_all = could_add_all && addUserToGroup(requestor, user.getUsername(), inviteeGroupname);
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
	public synchronized static boolean removeUserFromGroup(User requestor, String username, String groupname) throws GroupDoesNotExistException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException {
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
	public synchronized static boolean removeGroupFromGroup(User requestor, Group kickedGroup, String groupname) throws GroupDoesNotExistException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException {
		boolean could_add_all = true;
		for (User user : kickedGroup.getUsers()) {
			could_add_all = could_add_all && removeUserFromGroup(requestor, user.getUsername(), groupname);
		}
		return true;
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
	 */
	public synchronized static void bookRoom(Room room, long startTime, long endTime, long entryID) throws RoomAlreadyBookedException{
		rbh.bookRoom(room, startTime, endTime, entryID);
	}
	
	/**
	 * cancels a booking.
	 * @param room
	 * @param startTime
	 * @param endTime
	 */
	public synchronized static void releaseRoom(Room room, long startTime, long endTime){
		rbh.releaseRoom(room, startTime, endTime);
	}
	
	
	
	
	/* ===============
	 * 'Calendar' functions
	 *================*/ 
	
	/**
	 * 
	 * @param requestor
	 * @return a calendar containing all entries the suer can see
	 * @throws UserDoesNotExistException
	 * @throws SessionExpiredException
	 */
	public synchronized static Calendar createCalendar(User requestor) throws UserDoesNotExistException, SessionExpiredException {
		validate(requestor);
		return dbm.createCalendar(requestor.getUsername());
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
	 */
	public synchronized static boolean invitationAnswer(User requestor, int entry_id, boolean answer) throws EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException {
		validate(requestor);
		
		// return answer? dbm.going(requestor.getUsername(), entry_id) : dbm.notGoing(requestor.getUsername(), entry_id);
		
		if (answer){
			return dbm.going(requestor.getUsername(), entry_id);
		}
		else{
			return dbm.notGoing(requestor.getUsername(), entry_id);
		}
	}
	
	/**
	 * returns all notifications for a user.
	 * @param username
	 * @return
	 * @throws UserDoesNotExistException
	 */
	public synchronized static HashSet<Notification> getNotifications(String username) throws UserDoesNotExistException {
		return dbm.getNotificationsForUser(username);
	}
	
	/**
	 * returns all invitations the user has.
	 * @param username
	 * @return
	 * @throws UserDoesNotExistException
	 */
	public synchronized static HashSet<Invitation> getInvitations(String username) throws UserDoesNotExistException {
		return dbm.getInvitationsForUser(username);
	}
}
