package dbms;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import room_booking.Room;
import user.Group;
import user.GroupBuilder;
import user.User;
import user.UserBuilder;
import calendar.Calendar;
import calendar.CalendarBuilder;
import calendar.Entry;
import calendar.EntryBuilder;
import calendar.Invitation;
import calendar.InvitationBuilder;

import org.apache.ibatis.jdbc.ScriptRunner;

import com.mysql.jdbc.exceptions.NotYetImplementedException;

import exceptions.EntryDoesNotExistException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.UserDoesNotExistException;
import exceptions.UsernameAlreadyExistsException;

/**
 * This class is the connection to the Data Base.
 * It provides all interactions with the DB.
 * It also parses DB entries to java instances.
 *
 */
public class DataBaseManager {
	private Connection connection;
	
	/**
	 * opens a connection to the DB.
	 */
	public DataBaseManager(){
		try {
			String[] ci = readConnectionInformation();
			if (ci.equals(null)) throw new IllegalArgumentException("Something is wrong with your db_id file and a connection can't be established");
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(ci[0], ci[1], ci[2]);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * reads the id_db file.
	 */
	private String[] readConnectionInformation() {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("db_id.txt"));
			List<String> sl = new ArrayList<>();
			
			while (br.ready()) {
				sl.add(br.readLine());
			}
			
			br.close();
			
			if (sl.size() != 3) return null;
		
			return sl.toArray(new String[3]);
		} catch (IOException e) {
			return null;
		}
	}

/*==============================
 * User functions
 *==============================*/
	
	private boolean doesUserExist(String username){
		try {
			getUser(username);
			// at this point it is clear that the username is taken (user does exist)
			return true;
		} catch (UserDoesNotExistException e1) {
			// if it is catched then the username is not taken (user does not exist)
			return false;
		} 
	}
	
	private boolean doesEntryExist(int entryID){
		try {
			getEntry(entryID);
			// entry does exist
			return true;
		} catch (EntryDoesNotExistException e1) {
			// entry does not exist
			return false;
		} 
	}

	/**
	 * adds the User to the DB. Does not work if a user with the same username already exists.
	 * @param u
	 * @return true if the action was successful. False otherwise.
	 * @throws UsernameAlreadyExistsException if the username is already taken.
	 */
	public boolean addUser(User u) throws UsernameAlreadyExistsException{
		
		// check if the username already exists.
		if(doesUserExist(u.getUsername())){throw new UsernameAlreadyExistsException();}
		
		String addUser = "INSERT INTO User VALUES (?, ?, ?, ?, ?);";
			
		
		try {
			PreparedStatement stm = connection.prepareStatement(addUser);
			stm.setString(1, u.getUsername());
			stm.setString(2, u.getName());
			stm.setString(3, u.getPassword());
			stm.setString(4, u.getSalt());
			stm.setString(5, u.getEmail());
			stm.execute();
			stm.close();
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * u replaces the user with the same username in the DB. Note that the username can not be changed
	 * @param u
	 * @return true iff successful. false otherwise.
	 * @throws UserDoesNotExistException if the user does not exist
	 */
	public boolean editUser(User u) throws UserDoesNotExistException{
		if(! doesUserExist(u.getUsername())){throw new UserDoesNotExistException("");}	
		
		String edit_entry = "UPDATE User "
				+ "SET name = ?, password = ?, salt = ?, email = ?"
				+ "WHERE username = ?; ";
		
		try {
			PreparedStatement editUser_stmt = connection.prepareStatement(edit_entry);
			int i = 0;
			
			editUser_stmt.setString(++i, u.getName());
			editUser_stmt.setString(++i, u.getPassword());
			editUser_stmt.setString(++i, u.getSalt());
			editUser_stmt.setString(++i, u.getEmail());
			editUser_stmt.setString(++i, u.getUsername());
			
			editUser_stmt.executeUpdate();
			editUser_stmt.close();
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @param username
	 * @return the user if he exists, null if something went wrong.
	 * @throws UserDoesNotExistException if the user does not exist
	 */
	public User getUser(String username) throws UserDoesNotExistException {
		
		PreparedStatement stm;
		try {
			stm = connection.prepareStatement("SELECT * FROM User WHERE username=?");

			stm.setString(1, username);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				UserBuilder ub = new UserBuilder();
				ub.setUsername(username);
				ub.setName(rs.getString("name"));
				ub.setPassword(rs.getString("password"));
				ub.setSalt(rs.getString("salt"));
				ub.setEmail(rs.getString("email"));
				return ub.build();
			} else{
				throw new UserDoesNotExistException("");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * makes the user an admin of the given entry. check also if the issuing user has the rights to do that
	 * @param admin the one issuing the admin-rights
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 * @throws HasNotTheRightsException 
	 */
	public boolean makeAdmin(String admin, String username, int entry_id) throws HasNotTheRightsException{
		if(! isAdmin(admin, entry_id)){throw new HasNotTheRightsException();}
		return addIntoIsAdmin(username, entry_id);
	}
	
	/**
	 * 
	 * @param username
	 * @param entry_id
	 * @return true iff the user has adminrights to the entry.
	 */
	private boolean isAdmin(String username, int entry_id){

		try {
			PreparedStatement stm = connection.prepareStatement(""
					+ "SELECT * "
					+ "FROM isAdmin "
					+ "WHERE username=? "
						+ "AND entryID = ?;");

			stm.setString(1, username);
			stm.setInt(2, entry_id);
			ResultSet rs = stm.executeQuery();
			return rs.next();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * sets the isShowing flag in the Invitation to the newValue.
	 * @param username
	 * @param entry_id
	 * @param newValue
	 * @return true iff the action was successful.
	 */
	private boolean setIsShowing(String username, int entry_id, boolean newValue){
		String setValue = "UPDATE Invitation "
				+ "SET isShowing = ? "
				+ "WHERE username = ? and entryID = ?; ";
		
		try {
			PreparedStatement set_stmt = connection.prepareStatement(setValue);
			int i = 0;
			
			set_stmt.setBoolean(++i, newValue);
			set_stmt.setString(++i, username);
			set_stmt.setInt(++i, entry_id);
			
			set_stmt.executeUpdate();
			set_stmt.close();
			
			return true;
			
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * makes the user be able to see the given entry
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 */
	public boolean allowToSee(String username, int entry_id){
		return setIsShowing(username, entry_id, true);
	}
	
	/**
	 * makes all users of the group be able to see the given entry
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 * @throws GroupDoesNotExistException 
	 */
	public boolean allowToSeeGroup(String groupname, int entry_id) throws GroupDoesNotExistException{
		for(User u : getGroup(groupname).getUsers()){
			if (! allowToSee(u.getUsername(), entry_id)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 *hides the event from the given user
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 */
	public boolean hideEvent(String username, int entry_id){
		return setIsShowing(username, entry_id, false);
	}
	
	/**
	 * hides the event from all the users in the given group
	 * @param groupname
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 * @throws GroupDoesNotExistException
	 */
	public boolean hideEventGroup(String groupname, int entry_id) throws GroupDoesNotExistException{
		for(User u : getGroup(groupname).getUsers()){
			if (! hideEvent(u.getUsername(), entry_id)){
				return false;
			}
		}
		return true;
		
	}
	
	/**
	 * sets the 'isGoing' flag to true for the user in the entry
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 */
	public boolean going(String username, int entry_id){
		return setIsGoing(username, entry_id, true);
	}
	
	/**
	 * sets the 'isGoing' flag to false for the user in the entry
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 */
	public boolean notGoing(String username, int entry_id){
		return setIsGoing(username, entry_id, false);
	}
	
	/**
	 * sets the 'isGoing' flag for the user in the entry.
	 * @param username
	 * @param entry_id
	 * @param newValue the value it should take
	 * @return true iff the action was successful. false otherwise
	 */
	private boolean setIsGoing(String username, int entry_id, boolean newValue){
		String setValue = "UPDATE Invitation "
				+ "SET isGoing = ? "
				+ "WHERE username = ? and entryID = ?; ";
		
		try {
			PreparedStatement set_stmt = connection.prepareStatement(setValue);
			int i = 0;
			
			set_stmt.setBoolean(++i, newValue);
			set_stmt.setString(++i, username);
			set_stmt.setInt(++i, entry_id);
			
			set_stmt.executeUpdate();
			set_stmt.close();
			
			return true;
			
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * sets the isActive flag to true in the event
	 * @param entry_id
	 * @param newValue
	 * @return true iff the action was successful. false otherwise
	 * @throws EntryDoesNotExistException 
	 * @throws HasNotTheRightsException 
	 */
	public boolean isActive(String username, int entry_id) throws EntryDoesNotExistException, HasNotTheRightsException{
		return setIsActive(username, entry_id, true);
	}
	
	/**
	 * sets the isActive flag to false in the event
	 * @param entry_id
	 * @param newValue
	 * @return true iff the action was successful. false otherwise
	 * @throws EntryDoesNotExistException 
	 * @throws HasNotTheRightsException 
	 */
	public boolean isNotActive(String username, int entry_id) throws EntryDoesNotExistException, HasNotTheRightsException{
		return setIsActive(username, entry_id, false);
	}
	
	/**
	 * sets the isActive flag in the entry
	 * @param username
	 * @param entry_id
	 * @param newValue
	 * @return true iff the action was successful. false otherwise
	 * @throws EntryDoesNotExistException 
	 * @throws HasNotTheRightsException 
	 */
	private boolean setIsActive(String username, int entry_id, boolean newValue) throws EntryDoesNotExistException, HasNotTheRightsException{
		EntryBuilder eb = new EntryBuilder(getEntry(entry_id));
		eb.setIsActive(newValue);
		return editEntry(eb.build(), username);		
	}
	
	
	
	
	

	
	/*==============================
	 * Entry functions
	 *==============================*/
	
	/**
	 * returns the entry with the specified entryId from the database.
	 * @param entry_id
	 * @return the Entry instance from the DB with the specified id.
	 * @throws EntryDoesNotExistException if the entry does not exist.
	 */
	public Entry getEntry(int entry_id) throws EntryDoesNotExistException {
	
		PreparedStatement stm;
		try {
			stm = connection.prepareStatement("SELECT * FROM Entry WHERE entryID=?");
	
			stm.setLong(1, entry_id);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				EntryBuilder ub = new EntryBuilder();
				ub.setEventID(entry_id);
				ub.setDescription(rs.getString("description"));
				ub.setEndTime(rs.getLong("endTime"));
				ub.setStartTime(rs.getLong("startTime"));
				ub.setRoomID(rs.getString("roomID"));
				ub.setIsActive(rs.getBoolean("isActive"));
				ub.setLocation(rs.getString("location"));
				return ub.build();
			} else{
				throw new EntryDoesNotExistException("");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * removes the entry with the given id from the DB.
	 * Does nothing if no entry with the given id exists.
	 * @param entry_id
	 * @return true iff the action was successful. Otherwise false.
	 */
	public boolean deleteEntry(int entry_id){
		try {
			PreparedStatement stm = connection.prepareStatement("DELETE FROM Entry WHERE entryID = ?");
			stm.setLong(1, entry_id);
			stm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}

	/**
	 * Adds the given Entry as a new entry into the DB
	 * To edit an existing entry use editEntry(Entry e) instead.
	 * @return true if the action was successful. False otherwise.
	 * @param e the entry
	 * @param u the user creating the entry
	 * @throws UserDoesNotExistException 
	 */
	public boolean addEntry(Entry e, String username) throws UserDoesNotExistException{
		if(! doesUserExist(username)){throw new UserDoesNotExistException("");}	
		if(addIntoEntry(e)){
			int entryID = getLastEntryID();
			return addIntoIsAdmin(username, entryID) && addIntoStatus(true, true, username, entryID);
		}else{
			return false;
		}
	}
	
	/**
	 * newEntry replaces the entry in the DB with the same entry_id as newEntry. the entry_id stays the same.</br>
	 * Note that the IsAdmin and the Invitation Tables stay unchanged.
	 * @param newEntry the new entry. replaces the old one
	 * @return true iff the action was successful.
	 * @throws EntryDoesNotExistException if the entryID is not in the database
	 * @throws HasNotTheRightsException if the user is not Admin of the entry
	 */
	public boolean editEntry(Entry newEntry, String username) throws EntryDoesNotExistException, HasNotTheRightsException{
				
		// checks
		if(! doesEntryExist(newEntry.getEntryID())){throw new EntryDoesNotExistException("there is no entry with id "+newEntry.getEntryID());};
		if(! isAdmin(username, newEntry.getEntryID())){throw new HasNotTheRightsException();}
		
		String edit_entry = "UPDATE Entry "
				+ "SET startTime = ?, endTime = ?, location = ?, description = ?, isActive = ?, roomID = ? "
				+ "WHERE entryID = ?; ";
		
		try {
			PreparedStatement editEntry_stmt = connection.prepareStatement(edit_entry);
			int i = 0;
			editEntry_stmt.setTimestamp(++i, new java.sql.Timestamp(newEntry.getStartTime()));
			editEntry_stmt.setTimestamp(++i, new java.sql.Timestamp(newEntry.getEndTime()));
			editEntry_stmt.setString(++i, newEntry.getLocation());
			editEntry_stmt.setString(++i, newEntry.getDescription());
			editEntry_stmt.setBoolean(++i, newEntry.isActive());
			editEntry_stmt.setString(++i, newEntry.getRoomID());
			editEntry_stmt.setInt(++i, newEntry.getEntryID());
			
			editEntry_stmt.executeUpdate();
			editEntry_stmt.close();
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean removeNotification(String username, int entry_id){
		// TODO
		throw new NotYetImplementedException();
	}
	
	public boolean addNotification(String username, int entry_id, String description, boolean isOpened, long timestamp){
		// TODO
		throw new NotYetImplementedException();
	}
	
	public boolean editNotification(String username, int entry_id, String description, boolean isOpened, long timestamp){
		// TODO
		throw new NotYetImplementedException();
	}

	/**
	 * adds the Entry as a new Entry (with unique id) into the Entry Table.
	 * 
	 * @param e
	 * @return true iff the action was successful, false otherwise
	 */
	private boolean addIntoEntry(Entry e) {
		
		String insert_entry = "INSERT INTO Entry (startTime, endTime, location, description, isActive, roomID) "
				+ "VALUES (?, ?, ?, ?, ?, ?)"; // without setting entryID -> default value

		try {
			PreparedStatement addEntry_stmt = connection.prepareStatement(insert_entry);
			
			int i = 0;

			addEntry_stmt.setTimestamp(++i,
					new java.sql.Timestamp(e.getStartTime()));
			addEntry_stmt.setTimestamp(++i,
					new java.sql.Timestamp(e.getEndTime()));
			addEntry_stmt.setString(++i, e.getLocation());
			addEntry_stmt.setString(++i, e.getDescription());
			addEntry_stmt.setBoolean(++i, e.isActive());
			addEntry_stmt.setString(++i, e.getRoomID());

			addEntry_stmt.executeUpdate();
			addEntry_stmt.close();
			return true;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}

	}
	
	/**
	 * 
	 * @return the entryID of the last added entry
	 */
	private int getLastEntryID(){
		// get entry_id of the just added entry
		String get_id = "SELECT MAX(entryID) FROM Entry;";
		Statement get_id_stmt;
		try {
		get_id_stmt = connection.createStatement();
		
		ResultSet rsetID = get_id_stmt.executeQuery(get_id);
		rsetID.next();
		int entry_id = rsetID.getInt(1);
		get_id_stmt.close();
		return entry_id;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * makes the user admin of the given entry.
	 * @param username
	 * @param entry_id
	 * @return
	 */
	private boolean addIntoIsAdmin(String username, int entry_id) {
		// add the user-entry relation
		try{
			String add_isAdmin = "INSERT INTO IsAdmin (entryID, username) VALUES (?, ?);";
			PreparedStatement addIsAdmin_stmt = connection.prepareStatement(add_isAdmin);
			addIsAdmin_stmt.setInt(1, entry_id);
			addIsAdmin_stmt.setString(2, username);
			
	
			addIsAdmin_stmt.executeUpdate();
			addIsAdmin_stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean addIntoStatus(boolean isGoing, boolean isShowing, String username, int entry_id){
		//add the users status to that event.
		String add_status = "INSERT INTO Invitation (isGoing, isShowing, username, entryID) VALUES (?, ?, ?, ?);";
		try {
		PreparedStatement addStatus_stmt = connection.prepareStatement(add_status);
		
		addStatus_stmt.setBoolean(1, isGoing);
		addStatus_stmt.setBoolean(2, isShowing);
		addStatus_stmt.setString(3, username);
		addStatus_stmt.setInt(4, entry_id);
		
		addStatus_stmt.executeUpdate();
		addStatus_stmt.close();
		return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/*==============================
	 * Group functions
	 *==============================*/
	
	/**
	 * adds the given group to the DB
	 * If a group with this name already exists then it returns false and nothing is done.
	 * @param g
	 * @return true iff the action was successful. false otherwise.
	 */
	public boolean addGroup(Group g){
		try {
			getGroup(g.getName());
			return false;
		} catch (GroupDoesNotExistException group_e) {
			// this means the group does not yet exists, which is good.
			
			try {
				
				// Create the group
				String addGroup = "INSERT INTO Gruppe VALUES (?);";
				PreparedStatement addGroup_stm = connection.prepareStatement(addGroup);
				addGroup_stm.setString(1, g.getName());
				addGroup_stm.execute();
				addGroup_stm.close();
				
				// add people to the group
				for(User u: g.getUsers()){
					addUserToGroup(u.getUsername(), g.getName());
				}
				
			} catch (SQLException sql_e) {
				sql_e.printStackTrace();
				return false;
			}
			return true;
		}
		
	}
	
	/**
	 * 
	 * @param name
	 * @return the group instance corresponding to the given name from the DB
	 * @throws GroupDoesNotExistException 
	 */
	public Group getGroup(String name) throws GroupDoesNotExistException{
		PreparedStatement stm;
		try {
			stm = connection.prepareStatement("SELECT username FROM MemberOf WHERE groupname=?");

			stm.setString(1, name);
			ResultSet rs = stm.executeQuery();
			
			GroupBuilder gb = new GroupBuilder();
			gb.setName(name);
			while(rs.next()){
				User u = getUser(rs.getString("username"));
				gb.addUser(u);
			}
			
			return gb.build();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (UserDoesNotExistException e) {
			// Should never happen or something went horrible wrong :P
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * adds the user with the username to the Group with the groupname.
	 * Does nothing if the user is already in the group.
	 * @param username
	 * @return
	 */
	public boolean addUserToGroup(String username, String groupname){
		PreparedStatement isertUser_stm;
		try {
			isertUser_stm = connection.prepareStatement("INSERT INTO MemberOf (groupname, username) Values (?, ?)");
			isertUser_stm.setString(1, groupname);
			isertUser_stm.setString(2, username);
			
			isertUser_stm.executeUpdate();
			isertUser_stm.close();
			return true;
			
			
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * adds the user with the username to the Group with the groupname.
	 * Does nothing if the user is not in the group.
	 * @param username
	 * @param groupname
	 * @return
	 */
	public boolean removeUserFromGroup(String username, String groupname){
		PreparedStatement removeUser_stm;
		try {
			removeUser_stm = connection.prepareStatement("DELETE FROM MemberOf WHERE username = ? AND groupname = ?; ");
			removeUser_stm.setString(1, username);
			removeUser_stm.setString(2, groupname);
			
			removeUser_stm.executeUpdate();
			removeUser_stm.close();
			return true;
			
			
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * removes the group from the database.
	 * @param groupname
	 * @return
	 */
	public boolean deleteGroup(String groupname){
		PreparedStatement removeGroupFromMemberOf_stm;
		PreparedStatement deleteGroup_stm;
		try {
			// delete member assoziations from MemberOf
			removeGroupFromMemberOf_stm = connection.prepareStatement("DELETE FROM MemberOf WHERE groupname = ?");
			removeGroupFromMemberOf_stm.setString(1, groupname);
			removeGroupFromMemberOf_stm.executeUpdate();
			removeGroupFromMemberOf_stm.close();
			
			// delete the group
			deleteGroup_stm = connection.prepareStatement("DELETE FROM Gruppe WHERE groupname = ?");
			deleteGroup_stm.setString(1, groupname);
			deleteGroup_stm.executeUpdate();
			deleteGroup_stm.close();
			
			return true;
			
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*==============================
	 * Room functions
	 *==============================*/
	
	/**
	 * adds the Room to the DB
	 * @param r
	 * @return true if the action was successful. False otherwise.
	 */
	public boolean addRoom(Room r){
		String addRoom = "INSERT INTO Room VALUES (?, ?)";
		try {
			PreparedStatement stm = connection.prepareStatement(addRoom);
			stm.setString(1, r.getRoom_id());
			stm.setInt(2, r.getSize());
			stm.execute();
			stm.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteRoom(String roomID){
		String deleteRoom = "DELETE FROM Room	WHERE roomID = ?";
		try {
			PreparedStatement stm = connection.prepareStatement(deleteRoom);
			stm.setString(1, roomID);
			stm.execute();
			stm.close();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean editRoom(Room r){
		String editRoom = "UPDATE Room "
				+ "SET roomID = ?, size = ? "
				+ "WHERE roomID = ?; ";
		try {
			PreparedStatement stm = connection.prepareStatement(editRoom);
			stm.setString(1, r.getRoom_id());
			stm.setInt(2, r.getSize());
			stm.execute();
			stm.close();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Invites the user to the given event. The admin is the one inviting.
	 * @param admin
	 * @param username
	 * @param entry_id
	 * @return
	 */
	public boolean inviteUser(String admin, String username, int entry_id){
		// TODO check if admin is allowed to do that
		// TODO
		throw new NotYetImplementedException();
	}
	
	/**
	 * Invites all users in the group to the given event. The admin is the one inviting.
	 * @param admin
	 * @param groupname
	 * @param entry_id
	 * @return 
	 * @throws GroupDoesNotExistException
	 */
	public boolean inviteGroup(String admin, String groupname, int entry_id) throws GroupDoesNotExistException{
		for(User u : getGroup(groupname).getUsers()){
			inviteUser(admin, u.getUsername(), entry_id);
		}
		return true;
	}
	
	private Invitation getInvitation(String username, long entry_id) throws EntryDoesNotExistException{
		// TODO returns only one Invitation, maybe there are more. CHange DB if necessary
		
		PreparedStatement stm;
		try {
			stm = connection.prepareStatement("SELECT * FROM Invitation WHERE username=? and entryID = ?; ");
	
			stm.setLong(1, entry_id);
			ResultSet rs = stm.executeQuery();
			
			
			if (rs.next()) {
				InvitationBuilder ib = new InvitationBuilder();
				ib.setUsername(rs.getString("username"));
				ib.setEntry_id(rs.getLong("entryID"));
				ib.setGoing(rs.getBoolean("isGoing"));
				ib.setShowing(rs.getBoolean("isShowing"));
				
				return ib.build();
			} else{
				throw new EntryDoesNotExistException("");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*==============================
	 * Authorisation functions
	 *==============================*/
	
	
	/**
	 * 
	 * @param username
	 * @param entry_id
	 * @return true iff the user is allowed to see the given entry
	 */
	public boolean isAllowedToSee(String username, int entry_id){
		// TODO ev. create class Invitation
		String getIsShowing = ""
				+ "SELECT isShowing "
				+ "FROM Invitation "
				+ "WHERE username = ? AND entryID = ?; ";
		
		try {
			PreparedStatement stmt = connection.prepareStatement(getIsShowing);
		
			int i = 0;
			stmt.setString(++i, username);
			stmt.setLong(++i, entry_id);
			
			ResultSet rset = stmt.executeQuery();
			return rset.next() && rset.getBoolean("isShowing");
			
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param u
	 * @param e
	 * @return true if the user is allowed to edit the entry
	 */
	public boolean canEdit(String username, int entryID){
		String get_is_admin = "SELECT COUNT(*) "
				+ "FROM IsAdmin A, User U, Entry E "
				+ "WHERE A.username = U.username "
				+ "AND A.entryID = E.EntryID "
				+ "AND U.username = ? "
				+ "AND E.entryID = ?";
		
		try {
			PreparedStatement getIsAdmin_stmt = connection.prepareStatement(get_is_admin);
			getIsAdmin_stmt.setString(1, username);
			getIsAdmin_stmt.setInt(2, entryID);
			
			ResultSet rset = getIsAdmin_stmt.executeQuery();
			
			rset.next();
			return rset.getInt(1) > 0;
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		} 
	}
	
	/*==============================
	 * Calendar functions
	 *==============================*/	
	
	/**
	 * Creates a Calendar with all the entries the user is allowed to see.
	 * @param user
	 * @return a Calendar instance wit the entries of the given user. An empty calendar if some error occurred.
	 * @throws UserDoesNotExistException 
	 * @throws SQLException 
	 */
	public Calendar createCalendar(String username) throws UserDoesNotExistException{
		
		if(! doesUserExist(username)){throw new UserDoesNotExistException("");}
		
		String select_all_events_for_user = "SELECT E.* "
										  + "FROM Entry E, User U, Status S "
										  + "WHERE S.isShowing = 1 "
										  	+ "AND E.entryID = S.entryID "
										  	+ "AND U.username = S.username "
										  	+ "AND U.username = ? ;";
		
		CalendarBuilder cb = new CalendarBuilder();
		
		
		try {
			cb.addUser(this.getUser(username));
			PreparedStatement stmt = connection.prepareStatement(select_all_events_for_user);
			stmt.setString(1, username);
			ResultSet rset = stmt.executeQuery();
			
			
			while(rset.next()){
				EntryBuilder entryB = new EntryBuilder();
				
				entryB.setEventID(rset.getInt("entryID"));
				entryB.setStartTime(rset.getLong("startTime"));
				entryB.setEndTime(rset.getLong("endTime"));
				entryB.setLocation(rset.getString("location"));
				entryB.setDescription(rset.getString("description"));
				entryB.setIsActive(rset.getBoolean("isActive"));
				entryB.setRoomID(rset.getString("roomID"));
								
				cb.addEntry(entryB.build());
				
			}
			stmt.close();
			return cb.build();
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			return new CalendarBuilder().build();
		}
	}
	
	public void addSQL(String filename) throws IOException {
		ScriptRunner runner=new ScriptRunner(connection);
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
		runner.runScript(reader);
		reader.close();
	}
}
