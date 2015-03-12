package dbms;

import java.io.BufferedReader;
import java.io.Closeable;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.jdbc.ScriptRunner;

import room_booking.Room;
import room_booking.RoomBuilder;
import room_booking.RoomReservation;
import user.Group;
import user.GroupBuilder;
import user.User;
import user.UserBuilder;
import calendar.Alarm;
import calendar.AlarmBuilder;
import calendar.Calendar;
import calendar.CalendarBuilder;
import calendar.CalendarEntry;
import calendar.CalendarEntryBuilder;
import calendar.Invitation;
import calendar.InvitationBuilder;
import calendar.Notification;
import calendar.NotificationBuilder;

import com.mysql.jdbc.exceptions.NotYetImplementedException;

import exceptions.AlarmAlreadyExistsException;
import exceptions.AlarmDoesNotExistException;
import exceptions.EntryDoesNotExistException;
import exceptions.GroupAlreadyExistsException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.InvitationAlreadyExistsException;
import exceptions.InvitationDoesNotExistException;
import exceptions.NotAllowedException;
import exceptions.NotificationDoesNotExistException;
import exceptions.RoomAlreadyExistsException;
import exceptions.RoomDoesNotExistException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import exceptions.UsernameAlreadyExistsException;

/**
 * This class is the connection to the Data Base.
 * It provides all interactions with the DB.
 * It also parses DB entries to java instances.
 *
 */
public class DataBaseManager implements Closeable {
	private Connection connection;
	
	/**
	 * opens a connection to the DB.
	 */
	public DataBaseManager() {
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
	
	
	//----------------------------------------------------------------------------------------------
	// Does Exist Methods
	
	/**
	 * 
	 * @param username
	 * @return true iff the username exists in the DB
	 */
	private boolean doesUserExist(String username){
		if(username == null){
			return false;
		}
		
		PreparedStatement findUser_stmt;
		try {
			findUser_stmt = connection.prepareStatement("SELECT * FROM User WHERE username = ?;");
			findUser_stmt.setString(1, username);
			ResultSet rset = findUser_stmt.executeQuery();
			return rset.next();
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param entryID
	 * @return true iff the entryID exists in the DB
	 */
	private boolean doesEntryExist(long entryID){
		PreparedStatement findEntry_stmt;
		try {
			findEntry_stmt = connection.prepareStatement("SELECT * FROM CalendarEntry WHERE entryID = ?;");
			findEntry_stmt.setLong(1, entryID);
			ResultSet rset = findEntry_stmt.executeQuery();
			return rset.next();
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean doesAlarmExist(String username, long entry_id){
		PreparedStatement findAlarm_stmt;
		try {
			findAlarm_stmt = connection.prepareStatement("SELECT * FROM Alarm WHERE entryID = ? AND username = ?;");
			findAlarm_stmt.setLong(1, entry_id);
			findAlarm_stmt.setString(2, username);
			ResultSet rset = findAlarm_stmt.executeQuery();
			return rset.next();
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Checks if the invitation for the user and entryID exists
	 * @param username
	 * @param entry_id
	 * @return true iff the invitation exists in the DB
	 */
	private boolean doesInvitationExist(String username, long entry_id){
		PreparedStatement findInvitation_stmt;
		try {
			findInvitation_stmt = connection.prepareStatement("SELECT * FROM Invitation WHERE entryID = ? AND username = ?;");
			findInvitation_stmt.setLong(1, entry_id);
			findInvitation_stmt.setString(2, username);
			ResultSet rset = findInvitation_stmt.executeQuery();
			return rset.next();
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean doesNotificationExist(long notification_id){
		PreparedStatement findNotiction_stmt;
		try {
			findNotiction_stmt = connection.prepareStatement("SELECT * FROM Notification WHERE notificationID = ?;");
			findNotiction_stmt.setLong(1, notification_id);
			ResultSet rset = findNotiction_stmt.executeQuery();
			return rset.next();
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param groupname
	 * @return true iff the group exists in the DB
	 */
	private boolean doesGroupExist(String groupname){
		PreparedStatement findGroup_stmt;
		try {
			findGroup_stmt = connection.prepareStatement("SELECT * FROM Gruppe WHERE groupname = ?;");
			findGroup_stmt.setString(1, groupname);
			ResultSet rset = findGroup_stmt.executeQuery();
			return rset.next();
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param room_id
	 * @return true iff the room exists in the DB
	 */
	private boolean doesRoomExist(String room_id){
		PreparedStatement findGroup_stmt;
		try {
			findGroup_stmt = connection.prepareStatement("SELECT * FROM Room WHERE roomID = ?;");
			findGroup_stmt.setString(1, room_id);
			ResultSet rset = findGroup_stmt.executeQuery();
			return rset.next();
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param username
	 * @param entry_id
	 * @return true iff the user has adminrights to the entry and/or is the creator. </br>
	 * false if an error occurs or the user has not the admin rights.
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 */
	public boolean isAdmin(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException{
		
		if (isCreator(username, entry_id)){
			return true;
		}
		
		try {
			PreparedStatement stm = connection.prepareStatement(""
					+ "SELECT * "
					+ "FROM IsAdmin "
					+ "WHERE username=? "
						+ "AND entryID = ?;");
	
			stm.setString(1, username);
			stm.setLong(2, entry_id);
			ResultSet rs = stm.executeQuery();
			return rs.next();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param username
	 * @param entry_id
	 * @return true iff the user is the creator of the given entry
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 */
	private boolean isCreator(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException{
		checkUserAndEntry(username, entry_id);
		try {
			PreparedStatement stm = connection.prepareStatement(""
					+ "SELECT creator "
					+ "FROM CalendarEntry "
					+ "WHERE entryID = ?;");
	
			stm.setLong(1, entry_id);
			ResultSet rs = stm.executeQuery();
			rs.next();
			return rs.getString("creator").equals(username);
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @param username
	 * @param entry_id
	 * @return true iff the user is allowed to see the given entry
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 */
	public boolean isAllowedToSee(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException{
		
		checkUserAndEntry(username, entry_id);
		
		
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

	//----------------------------------------------------------------------------------------------
	// Check if Exist Methods
	
	
	/**
	 * 
	 * @param u
	 * @param e
	 * @return true if the user is allowed to edit the entry
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 */
	public boolean isAllowedToEdit(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException{
		return isAdmin(username, entry_id);
	}

	/**
	 * 
	 * @param username
	 * @throws UserDoesNotExistException if the user does not exists
	 */
	private void checkIfUserExists(String username) throws UserDoesNotExistException{
		if(! doesUserExist(username)){throw new UserDoesNotExistException(username);}
	}
	
	/**
	 * 
	 * @param entry_id
	 * @throws EntryDoesNotExistException if the entry does not exists
	 */
	private void checkIfEntryExists(long entry_id) throws EntryDoesNotExistException{
		if(! doesEntryExist(entry_id)){throw new EntryDoesNotExistException(entry_id);}
	}
	
	/**
	 * 
	 * @param groupname
	 * @throws GroupDoesNotExistException
	 */
	private void checkIfGroupExists(String groupname) throws GroupDoesNotExistException{
		if(! doesGroupExist(groupname)){throw new GroupDoesNotExistException(groupname);}
	}
	
	/**
	 * 
	 * @param room_id
	 * @throws RoomDoesNotExistException
	 */
	private void checkIfRoomExists(String room_id) throws RoomDoesNotExistException{
		if(! doesRoomExist(room_id)){throw new RoomDoesNotExistException(room_id);}
	}
	
	/**
	 * 
	 * @param username
	 * @param entry_id
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 * @throws AlarmDoesNotExistException
	 */
	private void checkIfAlarmExists(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, AlarmDoesNotExistException{
		checkUserAndEntry(username, entry_id);
		if(! doesAlarmExist(username, entry_id)){ throw new AlarmDoesNotExistException(username, entry_id);}
	}
	
	/**
	 * 
	 * @param groupname
	 * @throws GroupDoesNotExistException
	 * @throws UserInGroupDoesNotExistsException
	 */
	private void checkIfAllUserInGroupExist(String groupname) throws GroupDoesNotExistException, UserInGroupDoesNotExistsException{
		User[] users = this.getGroup(groupname).getUsers();
		for(User u: users){
			try {
				checkIfUserExists(u.getUsername());
			} catch (UserDoesNotExistException e) {
				throw new UserInGroupDoesNotExistsException(u.getUsername(), groupname);
			}
		}
	}
	
	/**
	 * checks first if the user exists then if the entry exists. throws the appropriate exception.
	 * @see DataBaseManager#checkIfEntryExists(long)
	 * @see DataBaseManager#checkIfUserExists(String)
	 * @param username
	 * @param entry_id
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 */
	private void checkUserAndEntry(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException{
		checkIfUserExists(username);
		checkIfEntryExists(entry_id);
		
	}
	
	/**
	 * Checks first if the user and the entry exist. then if the invitation exists. </br>
	 * Throws the appropriate exception if one of these does not exist.
	 * @see DataBaseManager#checkUserAndEntry(String, long)
	 * @param username
	 * @param entry_id
	 * @throws InvitationDoesNotExistException
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 */
	private void checkIfInvitationExists(String username, long entry_id) throws InvitationDoesNotExistException, EntryDoesNotExistException, UserDoesNotExistException{
		checkUserAndEntry(username, entry_id);
		if(! doesInvitationExist(username, entry_id)){throw new InvitationDoesNotExistException(username, entry_id);}
	}
	
	private void checkIfNotificationExists(long notification_id) throws NotificationDoesNotExistException{
		if(! doesNotificationExist(notification_id)){throw new NotificationDoesNotExistException(notification_id);}
	}

	
	/**
	 * @see DataBaseManager#checkUserAndEntry(String, long)
	 * @param admin
	 * @param entry_id
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 * @throws HasNotTheRightsException if the admin has not the admin rights for the entry.
	 */
	public void checkIfisAdmin(String admin, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException{
		checkUserAndEntry(admin, entry_id);
		if(! isAdmin(admin, entry_id)){throw new HasNotTheRightsException(admin, entry_id);}
	}
	
	//----------------------------------------------------------------------------------------------
	// Add Into Table Methods
	
	private boolean addIntoGroup(Group g) throws GroupAlreadyExistsException, UserInGroupDoesNotExistsException {
		try {
			checkIfGroupExists(g.getName());
			throw new GroupAlreadyExistsException(g.getName());
			
		} catch (GroupDoesNotExistException e) {
			// the groupname is not taken.
			try {
				
				// Create the group
				String addGroup = "INSERT INTO Gruppe VALUES (?);";
				PreparedStatement addGroup_stm = connection.prepareStatement(addGroup);
				addGroup_stm.setString(1, g.getName());
				addGroup_stm.execute();
				addGroup_stm.close();
				
				// add people to the group
				for(User u: g.getUsers()){
					try {
						addUserToGroup(u.getUsername(), g.getName());
					} catch (GroupDoesNotExistException e1) {
						// should never happen since we just created the group!
						e1.printStackTrace();
					}catch (UserDoesNotExistException e2){
						throw new UserInGroupDoesNotExistsException(u.getUsername(), g.getName());
					}
				}
				
			} catch (SQLException sql_e) {
				sql_e.printStackTrace();
				return false;
			}
			return true;
		}	
	}
	
	private boolean addIntoMemberOf(String groupname, String username) throws UserDoesNotExistException, GroupDoesNotExistException {
		checkIfUserExists(username);
		checkIfGroupExists(groupname);
		
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
	 * Adds a new Notification to the DB
	 * @param n
	 * @return
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 */
	private boolean addIntoNotification(Notification n) throws EntryDoesNotExistException, UserDoesNotExistException {
		
		checkUserAndEntry(n.getUsername(), n.getEntry_id());
		String addNotification = "INSERT INTO Notification (description, isOpened, time, username, entryID) VALUES (?, ?, ?, ? ,?)";
		try {
			PreparedStatement addNotction_stm = connection.prepareStatement(addNotification);
			int i = 0;
			addNotction_stm.setString(++i, n.getDescription());
			addNotction_stm.setBoolean(++i, n.isOpened());
			addNotction_stm.setTimestamp(++i, new Timestamp(n.getTime()));
			addNotction_stm.setString(++i, n.getUsername());
			addNotction_stm.setLong(++i, n.getEntry_id());
			addNotction_stm.execute();
			addNotction_stm.close();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean addIntoRoom(Room r) throws RoomAlreadyExistsException {
		if(doesRoomExist(r.getRoom_id())){throw new RoomAlreadyExistsException(r.getRoom_id());}
		
		String addRoom = "INSERT INTO Room VALUES (?, ?)";
		try {
			PreparedStatement stm = connection.prepareStatement(addRoom);
			int i = 0;
			stm.setString(++i, r.getRoom_id());
			stm.setInt(++i, r.getSize());
			stm.execute();
			stm.close();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean addIntoUser(User u) throws UsernameAlreadyExistsException {
		// check if the username already exists.
		if (doesUserExist(u.getUsername())) {
			throw new UsernameAlreadyExistsException();
		}

		String addUser = "INSERT INTO User VALUES (?, ?, ?, ?, ?);";

		try {
			PreparedStatement stm = connection.prepareStatement(addUser);
			int i = 0;
			
			stm.setString(++i, u.getUsername());
			stm.setString(++i, u.getName());
			stm.setString(++i, u.getPassword());
			stm.setString(++i, u.getSalt());
			stm.setString(++i, u.getEmail());
			stm.execute();
			stm.close();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * adds the CalendarEntry as a new CalendarEntry (with unique id) into the CalendarEntry Table.</br>
	 * Only the CalendarEntry Table is changed. 
	 * 
	 * @param e
	 * @return true if the action was successful, false otherwise
	 */
	private boolean addIntoEntry(CalendarEntry e) {
		
		String insert_entry = "INSERT INTO CalendarEntry (startTime, endTime, location, description, roomID, creator) "
				+ "VALUES (?, ?, ?, ?, ?, ?)"; // without setting entryID -> default value
	
		try {
			PreparedStatement addEntry_stmt = connection.prepareStatement(insert_entry);
			
			int i = 0;
	
			addEntry_stmt.setTimestamp(++i,new Timestamp(e.getStartTime()));
			addEntry_stmt.setTimestamp(++i,new Timestamp(e.getEndTime()));
			addEntry_stmt.setString(++i, e.getLocation());
			addEntry_stmt.setString(++i, e.getDescription());
			addEntry_stmt.setString(++i, e.getRoomID());
			addEntry_stmt.setString(++i, e.getCreator());
	
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
	 * @param inv the invitation to be added to the DB.</br>
	 * 
	 * @return true iff action was successful. false otherwise.
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 * @throws InvitationAlreadyExistsException if there is already an Invitation for this user-entry pair
	 */
	private boolean addIntoInvitation(Invitation inv) throws EntryDoesNotExistException, UserDoesNotExistException, InvitationAlreadyExistsException{
		
		try {
			checkIfInvitationExists(inv.getUsername(), inv.getEntry_id());  // throws an exception if the invitation does not exists.
			// the invitation already exists
			throw new InvitationAlreadyExistsException();
			
		} catch (InvitationDoesNotExistException e1) {
			// ok, the programm may proceed.
			//add the users status to that event.
			String add_status = "INSERT INTO Invitation (isGoing, isShowing, username, entryID) VALUES (?, ?, ?, ?);";
			try {
			PreparedStatement addStatus_stmt = connection.prepareStatement(add_status);
			
			addStatus_stmt.setBoolean(1, inv.isGoing());
			addStatus_stmt.setBoolean(2, inv.isShowing());
			addStatus_stmt.setString(3, inv.getUsername());
			addStatus_stmt.setLong(4, inv.getEntry_id());
			
			addStatus_stmt.executeUpdate();
			addStatus_stmt.close();
			return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * Adds a new alarm into the DB
	 * @param a
	 * @return true iff action was successful
	 * @throws AlarmAlreadyExistsException
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 */
	private boolean addIntoAlarm(Alarm a) throws AlarmAlreadyExistsException, EntryDoesNotExistException, UserDoesNotExistException{
		try {
			checkIfAlarmExists(a.getUsername(), a.getEntry_id());  // throws an exception if the alarm does not exists.
			// the alarm already exists
			throw new AlarmAlreadyExistsException();
			
		} catch (AlarmDoesNotExistException e1) {
			// ok, the programm may proceed.
			//add the new alarm
			String add_alarm = "INSERT INTO Alarm (alarmTime, username, entryID) VALUES (?, ?, ?);";
			try {
			PreparedStatement addAlarm_stmt = connection.prepareStatement(add_alarm);
			int i = 0;
			Timestamp  tstmp = new Timestamp(a.getAlarmTime());
			addAlarm_stmt.setTimestamp(++i, tstmp);
			addAlarm_stmt.setString(++i, a.getUsername());
			addAlarm_stmt.setLong(++i, a.getEntry_id());
			
			addAlarm_stmt.executeUpdate();
			addAlarm_stmt.close();
			
			return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * makes the user admin of the given entry.
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful, false otherwise
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 */
	private boolean addIntoIsAdmin(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException {
		
		checkUserAndEntry(username, entry_id);
		// add the user-entry relation
		try{
			String add_isAdmin = "INSERT INTO IsAdmin (entryID, username) VALUES (?, ?);";
			PreparedStatement addIsAdmin_stmt = connection.prepareStatement(add_isAdmin);
			addIsAdmin_stmt.setLong(1, entry_id);
			addIsAdmin_stmt.setString(2, username);
			
	
			addIsAdmin_stmt.executeUpdate();
			addIsAdmin_stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//----------------------------------------------------------------------------------------------
	// Set Flags Methods

	/**
	 * sets the isShowing flag in the Invitation to the newValue.</br>
	 * Does NOT check if the user is admin!
	 * @param username
	 * @param entry_id
	 * @param newValue
	 * @return true iff the action was successful. false otherwise.
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 * @throws InvitationDoesNotExistException 
	 */
	private boolean setIsShowing(String username, int entry_id, boolean newValue) throws EntryDoesNotExistException, UserDoesNotExistException, InvitationDoesNotExistException{
		
		checkUserAndEntry(username, entry_id);
		checkIfInvitationExists(username, entry_id);
		
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
	 * sets the 'isGoing' flag for the user in the entry.</br>
	 * Does NOT check if the user is admin!
	 * @param username
	 * @param entry_id
	 * @param newValue the value it should take
	 * @return true iff the action was successful. false otherwise
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 */
	private boolean setIsGoing(String username, int entry_id, boolean newValue) throws EntryDoesNotExistException, UserDoesNotExistException{
		
		checkUserAndEntry(username, entry_id);
		
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
	

	//----------------------------------------------------------------------------------------------
	// Other private Methods
	
	
	/**
	 * 
	 * @return the entryID of the last added entry (the highest entryID) -1 if something went wrong.
	 */
	private long getLastEntryID(){
		// get entry_id of the just added entry
		String get_id = "SELECT MAX(entryID) FROM CalendarEntry;";
		Statement get_id_stmt;
		try {
		get_id_stmt = connection.createStatement();
		
		ResultSet rsetID = get_id_stmt.executeQuery(get_id);
		rsetID.next();
		long entry_id = rsetID.getLong("MAX(entryID)");
		get_id_stmt.close();
		return entry_id;
		
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	//----------------------------------------------------------------------------------------------
	// Add Methods
	
	public boolean addAlarm(Alarm a) throws AlarmAlreadyExistsException, EntryDoesNotExistException, UserDoesNotExistException{
		return this.addIntoAlarm(a);
	}
	
	public boolean addInvitation(Invitation inv) throws EntryDoesNotExistException, UserDoesNotExistException, InvitationAlreadyExistsException{
		return this.addIntoInvitation(inv);
	}

	/**
	 * adds the User to the DB. </br>
	 * Throws an exception if a user with the same username already exists.
	 * @param u
	 * @return true if the action was successful. False otherwise.
	 * @throws UsernameAlreadyExistsException if the username is already taken.
	 */
	public boolean addUser(User u) throws UsernameAlreadyExistsException{
		return this.addIntoUser(u);
	}

	/**
	 * Adds the given CalendarEntry as a new entry into the DB</br>
	 * To edit an existing entry use editEntry(CalendarEntry e) instead.</br>
	 * @return true if the action was successful. False otherwise.
	 * @param e the entry
	 * @param u the user creating the entry
	 * @throws UserDoesNotExistException
	 * @see {@link DataBaseManager#editEntry(CalendarEntry, String)}
	 */
	public boolean addEntry(CalendarEntry e) throws UserDoesNotExistException{
		
		checkIfUserExists(e.getCreator());
		
		
		// TODO username and e.creator must be the same. Resolve it :P
		
		if(addIntoEntry(e)){
			long entryID = getLastEntryID();
			try {
				return addIntoIsAdmin(e.getCreator(), entryID) && addIntoInvitation(new Invitation(true, true, e.getCreator(), entryID));
			} catch (EntryDoesNotExistException | InvitationAlreadyExistsException e1) {
				// should never happen!
				e1.printStackTrace();
				return false;
			} 
		}else{
			return false;
		}
	}

	/**
	 * adds the Room to the DB
	 * @param r
	 * @return true if the action was successful. False otherwise.
	 * @throws RoomAlreadyExistsException 
	 */
	public boolean addRoom(Room r) throws RoomAlreadyExistsException{
		return this.addIntoRoom(r);
	}

	/**
	 * adds the given group to the DB
	 * If a group with this name already exists then it returns false and nothing is done.
	 * @param g
	 * @return true iff the action was successful. false otherwise.
	 * @throws GroupAlreadyExistsException if the groupname is already taken
	 * @throws UserInGroupDoesNotExistsException if a user in the group does not exist.
	 */
	public boolean addGroup(Group g) throws UserDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException{
		return this.addIntoGroup(g);
	}

	/**
	 * makes the user an admin of the given entry. check also if the issuing user has the rights to do that
	 * @param admin the one issuing the admin-rights
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise 
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 */
	public boolean makeAdmin(String username, int entry_id) throws EntryDoesNotExistException, UserDoesNotExistException{
		return addIntoIsAdmin(username, entry_id);
	}

	/**
	 * adds the user with the username to the Group with the groupname.
	 * Does nothing if the user is already in the group.
	 * @param username
	 * @return
	 * @throws UserDoesNotExistException 
	 * @throws GroupDoesNotExistException 
	 */
	public boolean addUserToGroup(String username, String groupname) throws UserDoesNotExistException, GroupDoesNotExistException{
		return this.addIntoMemberOf(groupname, username);
	}
	
	/**
	 * 
	 * @param n
	 * @return
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 */
	public boolean addNotification(Notification n) throws EntryDoesNotExistException, UserDoesNotExistException{
		return this.addIntoNotification(n);
	}
	
	//----------------------------------------------------------------------------------------------
	// Get Methods
	
	
	public Alarm getAlarm(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, AlarmDoesNotExistException{
		checkIfAlarmExists(username, entry_id);
		
		PreparedStatement getAlarm_stm;
		try {
			getAlarm_stm = connection.prepareStatement("SELECT * FROM Alarm WHERE username=? and entryID = ?; ");
	
			int i = 0;
			getAlarm_stm.setString(++i, username);
			getAlarm_stm.setLong(++i, entry_id);
			
			ResultSet rset = getAlarm_stm.executeQuery();
			
			if (rset.next()) {
				AlarmBuilder ab = new AlarmBuilder();
				Timestamp timestmp = rset.getTimestamp("alarmTime");
				ab.setAlarmTime(timestmp.getTime());
				ab.setUsername(username);
				ab.setEntry_id(entry_id);
				
				return ab.build();
			} else{
				throw new AlarmDoesNotExistException();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return new AlarmBuilder().build();
		}
	}
	
	/**
	 * 
	 * @param groupname
	 * @param username
	 * @return true iff the user is in the group. false if not or the group does not exist.
	 * @throws UserDoesNotExistException
	 */
	public boolean isMemberOf(String groupname, String username) throws UserDoesNotExistException{
		checkIfUserExists(username);
		if(! doesGroupExist(groupname)){return false;}
		
		PreparedStatement isMember_stm;
		try {
			isMember_stm = connection.prepareStatement("SELECT * FROM MemberOf WHERE username=? and groupname = ?; ");
	
			int i = 0;
			isMember_stm.setString(++i, username);
			isMember_stm.setString(++i, groupname);
			
			ResultSet rset = isMember_stm.executeQuery();
			
			return rset.next();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param username
	 * @return a HashSet with all notifications for the specified user
	 * @throws UserDoesNotExistException 
	 */
	public HashSet<Notification> getNotificationsForUser(String username) throws UserDoesNotExistException{
		checkIfUserExists(username);
		
		
		PreparedStatement getNotifications_stm;
		try {
			getNotifications_stm = connection.prepareStatement("SELECT * FROM Notification WHERE username=?; ");
	
			int i = 0;
			getNotifications_stm.setString(++i, username);
			
			ResultSet rset = getNotifications_stm.executeQuery();
			
			HashSet<Notification> notifics = new HashSet<>();
			while(rset.next()){
				NotificationBuilder nb = new NotificationBuilder();
				nb.setDescription(rset.getString("description"));
				nb.setEntry_id(rset.getLong("entryID"));
				nb.setNotifiationID(rset.getLong("notificationID"));
				nb.setOpened(rset.getBoolean("isOpened"));
				nb.setTime(rset.getTimestamp("time").getTime());
				nb.setUsername(username);
				notifics.add(nb.build());
			}
			
			return notifics;
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Room getRoom(String room_id) throws RoomDoesNotExistException{
		PreparedStatement getAlarm_stm;
		try {
			getAlarm_stm = connection.prepareStatement("SELECT * FROM Room WHERE roomID = ?; ");
	
			int i = 0;
			getAlarm_stm.setString(++i, room_id);
			
			ResultSet rset = getAlarm_stm.executeQuery();
			
			if (rset.next()) {
				RoomBuilder rb = new RoomBuilder();
				rb.setRoom_id(room_id);
				rb.setSize(rset.getInt("size"));
				
				
				return rb.build();
			} else{
				throw new RoomDoesNotExistException(room_id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	

	/**
	 * 
	 * @param username
	 * @param entry_id
	 * @return the invitation belonging to the username and entryID.
	 * @throws EntryDoesNotExistException
	 * @throws InvitationDoesNotExistException 
	 * @throws UserDoesNotExistException
	 */
	public Invitation getInvitation(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, InvitationDoesNotExistException{
		
		checkIfInvitationExists(username, entry_id);
		
		PreparedStatement stm;
		try {
			stm = connection.prepareStatement("SELECT * FROM Invitation WHERE username=? and entryID = ?; ");
			int i = 0;
			stm.setString(++i, username);
			stm.setLong(++i, entry_id);
			
			ResultSet rs = stm.executeQuery();
			
			if (rs.next()) {
				InvitationBuilder ib = new InvitationBuilder();
				ib.setUsername(rs.getString("username"));
				ib.setEntry_id(rs.getLong("entryID"));
				ib.setGoing(rs.getBoolean("isGoing"));
				ib.setShowing(rs.getBoolean("isShowing"));
				
				return ib.build();
			} else{
				throw new EntryDoesNotExistException();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param username
	 * @return the user if he exists, null if something went wrong.
	 * @throws UserDoesNotExistException if the user does not exist
	 */
	public User getUser(String username) throws UserDoesNotExistException {
		
		checkIfUserExists(username);
		
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
				// just to be sure.
				throw new UserDoesNotExistException();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * returns the entry with the specified entryId from the database.
	 * @param entry_id
	 * @return the CalendarEntry instance from the DB with the specified id.
	 * @throws EntryDoesNotExistException if the entry does not exist.
	 */
	public CalendarEntry getEntry(long entry_id) throws EntryDoesNotExistException {
		
		checkIfEntryExists(entry_id);
	
		PreparedStatement stm;
		try {
			stm = connection.prepareStatement("SELECT * FROM CalendarEntry WHERE entryID=?");
	
			stm.setLong(1, entry_id);
			ResultSet rset = stm.executeQuery();
			if (rset.next()) {
				CalendarEntryBuilder ub = new CalendarEntryBuilder();
				
				ub.setEventID(entry_id);
				ub.setDescription(rset.getString("description"));
				
				Timestamp sstmp = rset.getTimestamp("startTime");
				Timestamp estmp = rset.getTimestamp("endTime");
				ub.setEndTime(estmp.getTime());
				ub.setStartTime(sstmp.getTime());
				ub.setRoomID(rset.getString("roomID"));
				ub.setLocation(rset.getString("location"));
				ub.setCreator(rset.getString("creator"));
				return ub.build();
			} else{
				throw new EntryDoesNotExistException(); // just to be safe.
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param groupname
	 * @return the group instance corresponding to the given name from the DB
	 * @throws GroupDoesNotExistException 
	 */
	public Group getGroup(String groupname) throws GroupDoesNotExistException{
		checkIfGroupExists(groupname);
		
		PreparedStatement stm;
		try {
			stm = connection.prepareStatement("SELECT username FROM MemberOf WHERE groupname=?");
	
			stm.setString(1, groupname);
			ResultSet rs = stm.executeQuery();
			
			GroupBuilder gb = new GroupBuilder();
			gb.setName(groupname);
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
	
	public HashSet<RoomReservation> getReservationsForRoom(Room r){
		// TODO
		throw new NotYetImplementedException();
	}
	
	/**
	 * 
	 * @param username
	 * @param entry_id
	 * @return true iff the user is going to the event.
	 * @throws EntryDoesNotExistException
	 * @throws UserDoesNotExistException
	 */
	public boolean isGoing(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException{
		
		checkUserAndEntry(username, entry_id);
		
		
		String getIsShowing = ""
				+ "SELECT isGoing "
				+ "FROM Invitation "
				+ "WHERE username = ? AND entryID = ?; ";
		
		try {
			PreparedStatement stmt = connection.prepareStatement(getIsShowing);
		
			int i = 0;
			stmt.setString(++i, username);
			stmt.setLong(++i, entry_id);
			
			ResultSet rset = stmt.executeQuery();
			return rset.next() && rset.getBoolean("isGoing");
			
		
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//----------------------------------------------------------------------------------------------
	// Edit Table Methods

	/**
	 * takes the user and entry form the given alarm <i>a</i> and finds the alarm in the DB</br>
	 * then replaces the alarmTime in the database.</br>
	 * calls the editAlarm(String username, long entry_id, long newAlarmTime) method.
	 * @param a
	 * @return
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 * @see DataBaseManager#editAlarm(String, long, long)
	 */
	public boolean editAlarm(Alarm a) throws EntryDoesNotExistException, UserDoesNotExistException{
		return this.editAlarm(a.getUsername(), a.getEntry_id(), a.getAlarmTime());
	}
	
	/**
	 * changes the alarmTime for the alarm for the user-entry pair.
	 * If the alarm does not exist, it creates an new one.
	 * @param username
	 * @param entry_id
	 * @param newAlarmTime
	 * @return
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 */
	public boolean editAlarm(String username, long entry_id, long newAlarmTime) throws EntryDoesNotExistException, UserDoesNotExistException {

		try {
			boolean bool = addIntoAlarm(new Alarm(newAlarmTime, username, entry_id));
			return bool;
		} catch (AlarmAlreadyExistsException alarmExists) {
			// The alarm does already exist, so edit it:
			String edit_alarm =   "UPDATE Alarm " 
								+ "SET alarmTime = ? "
								+ "WHERE username = ? AND entryID = ?; ";

			try {
				PreparedStatement editAlarm_stmt = connection.prepareStatement(edit_alarm);
				int i = 0;
				Timestamp tstmp = new Timestamp(newAlarmTime);
				editAlarm_stmt.setTimestamp(++i, tstmp);
				editAlarm_stmt.setString(++i, username);
				editAlarm_stmt.setLong(++i, entry_id);
				editAlarm_stmt.executeUpdate();

				editAlarm_stmt.close();
				return true;

			} catch (SQLException sqlEx) {
				sqlEx.printStackTrace();
				return false;
			}
		}
	}
	
	/**
	 * replaces the notification in the DB with the same notificationID as the given notification <i>n</>.</br>
	 * Note that the ID, the user and the entry of the notification can not be changed.</br>
	 * 
	 * @param n
	 * @return
	 * @throws NotificationDoesNotExistException if the notification does not exist in the DB. 
	 */
	public boolean editNotification(Notification n) throws NotificationDoesNotExistException{
		checkIfNotificationExists(n.getNotificationID());
		
		String edit_notific = "UPDATE Notification "
				+ "SET description = ?, isOpened = ?, time = ? "
				+ "WHERE notificationID = ?; ";
		
		try {
			PreparedStatement editNotific_stmt = connection.prepareStatement(edit_notific);
			int i = 0;
			
			editNotific_stmt.setString(++i, n.getDescription());
			editNotific_stmt.setBoolean(++i, n.isOpened());
			editNotific_stmt.setTimestamp(++i, new Timestamp(n.getTime()));
			editNotific_stmt.setLong(++i, n.getNotificationID());
			
			editNotific_stmt.executeUpdate();
			editNotific_stmt.close();
			return true;
			
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * u replaces the user with the same username in the DB.</br>
	 * Note that the username can not be changed
	 * @param u
	 * @return true iff successful. false otherwise.
	 * @throws UserDoesNotExistException if the user does not exist
	 */
	public boolean editUser(User u) throws UserDoesNotExistException{
		checkIfUserExists(u.getUsername());	
		
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
	 * newEntry replaces the entry in the DB with the same entry_id as newEntry. the entry_id stays the same.</br>
	 * Note that the IsAdmin and the Invitation Tables stay unchanged.
	 * @param newEntry the new entry. replaces the old one
	 * @param username this is the user who wants to edit the entry
	 * @return true iff the action was successful.
	 * @throws EntryDoesNotExistException if the entryID is not in the database
	 * @throws UserDoesNotExistException 
	 */
	public boolean editEntry(CalendarEntry newEntry, String username) throws EntryDoesNotExistException, UserDoesNotExistException{
				
		// checks
		checkUserAndEntry(username, newEntry.getEntryID());
		
		String edit_entry = "UPDATE CalendarEntry "
				+ "SET startTime = ?, endTime = ?, location = ?, description = ?, roomID = ? "
				+ "WHERE entryID = ?; ";
		
		try {
			PreparedStatement editEntry_stmt = connection.prepareStatement(edit_entry);
			int i = 0;
			editEntry_stmt.setTimestamp(++i, new java.sql.Timestamp(newEntry.getStartTime()));
			editEntry_stmt.setTimestamp(++i, new java.sql.Timestamp(newEntry.getEndTime()));
			editEntry_stmt.setString(++i, newEntry.getLocation());
			editEntry_stmt.setString(++i, newEntry.getDescription());
			editEntry_stmt.setString(++i, newEntry.getRoomID());
			editEntry_stmt.setLong(++i, newEntry.getEntryID());
			
			editEntry_stmt.executeUpdate();
			editEntry_stmt.close();
			return true;
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Edits the room with the same room id as r. Note that the room id can not be changed.
	 * @param r
	 * @return
	 * @throws RoomDoesNotExistException
	 */
	public boolean editRoom(Room r) throws RoomDoesNotExistException{
		checkIfRoomExists(r.getRoom_id());
		
		String editRoom = "UPDATE Room "
				+ "SET size = ? "
				+ "WHERE roomID = ?; ";
		try {
			PreparedStatement stm = connection.prepareStatement(editRoom);
			int i = 0;
			stm.setInt(++i, r.getSize());
			stm.setString(++i, r.getRoom_id());
			
			stm.execute();
			stm.close();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * makes the user be able to see the given entry
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 * @throws InvitationDoesNotExistException 
	 */
	public boolean allowToSee(String username, int entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, InvitationDoesNotExistException{
		return setIsShowing(username, entry_id, true);
	}
	
	/**
	 * makes all users of the group be able to see the given entry
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 * @throws GroupDoesNotExistException 
	 * @throws UserInGroupDoesNotExistsException 
	 * @throws EntryDoesNotExistException 
	 * @throws InvitationDoesNotExistException 
	 */
	public boolean allowToSeeGroup(String groupname, int entry_id) throws GroupDoesNotExistException, UserInGroupDoesNotExistsException, EntryDoesNotExistException, InvitationDoesNotExistException{
		checkIfEntryExists(entry_id); // to discover an exception early.
		
		for(User u : getGroup(groupname).getUsers()){
			try {
				if (! allowToSee(u.getUsername(), entry_id)){
					return false;
				}
			} catch (UserDoesNotExistException e) {
				throw new UserInGroupDoesNotExistsException();
			}
		}
		return true;
	}
	
	/**
	 *hides the event from the given user
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 * @throws InvitationDoesNotExistException 
	 */
	public boolean hideEvent(String username, int entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, InvitationDoesNotExistException{
		return setIsShowing(username, entry_id, false);
	}
	
	/**
	 * hides the event from all the users in the given group
	 * @param groupname
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 * @throws GroupDoesNotExistException
	 * @throws UserInGroupDoesNotExistsException 
	 * @throws EntryDoesNotExistException 
	 * @throws InvitationDoesNotExistException 
	 */
	public boolean hideEventGroup(String groupname, int entry_id) throws GroupDoesNotExistException, UserInGroupDoesNotExistsException, EntryDoesNotExistException, InvitationDoesNotExistException{
		checkIfEntryExists(entry_id); // to detect an exception early.
		
		for(User u : getGroup(groupname).getUsers()){
			try {
				if (! hideEvent(u.getUsername(), entry_id)){
					return false;
				}
			} catch (UserDoesNotExistException e) {
				throw new UserInGroupDoesNotExistsException(u.getUsername());
			}
		}
		return true;
		
	}
	
	/**
	 * sets the 'isGoing' flag to true for the user in the entry
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 */
	public boolean going(String username, int entry_id) throws EntryDoesNotExistException, UserDoesNotExistException{
		return setIsGoing(username, entry_id, true);
	}
	
	/**
	 * sets the 'isGoing' flag to false for the user in the entry
	 * @param username
	 * @param entry_id
	 * @return true iff the action was successful. false otherwise
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 */
	public boolean notGoing(String username, int entry_id) throws EntryDoesNotExistException, UserDoesNotExistException{
		return setIsGoing(username, entry_id, false);
	}
	
	//----------------------------------------------------------------------------------------------
	// Delete Methods
	
	public boolean deleteAlarm(Alarm a) throws EntryDoesNotExistException, UserDoesNotExistException{
		return deleteAlarm(a.getUsername(), a.getEntry_id());
	}
	
	public boolean deleteAlarm(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException{
		checkUserAndEntry(username, entry_id);
		try {
			PreparedStatement stm = connection.prepareStatement("DELETE FROM Alarm WHERE entryID = ? AND username = ?");
			int i = 0;
			stm.setLong(++i, entry_id);
			stm.setString(++i, username);
			stm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean revokeAdmin(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException, NotAllowedException{
		if(isCreator(username, entry_id)){
			throw new NotAllowedException("The creator can not be removed as admin! ('"+username+"' is the creator of '"+entry_id+"')");
		}
		
		try {
			PreparedStatement stm = connection.prepareStatement("DELETE FROM IsAdmin WHERE entryID = ? AND username = ?");
			int i = 0;
			stm.setLong(++i, entry_id);
			stm.setString(++i, username);
			stm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteNotification(long notification_id){
		try {
			PreparedStatement stm = connection.prepareStatement("DELETE FROM Notification WHERE notificationID = ?;");
			int i = 0;
			stm.setLong(++i, notification_id);
			stm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	

	
	/**
	 * removes the entry with the given id from the DB.
	 * Does nothing if no entry with the given id exists.
	 * @param entry_id
	 * @return true iff the action was successful. Otherwise false.
	 * @throws UserDoesNotExistException 
	 * @throws EntryDoesNotExistException 
	 */
	public boolean deleteEntry(String username, long entry_id) throws EntryDoesNotExistException, UserDoesNotExistException{
		
		try {
			PreparedStatement stm = connection.prepareStatement("DELETE FROM CalendarEntry WHERE entryID = ?");
			stm.setLong(1, entry_id);
			stm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * adds the user with the username to the Group with the groupname.
	 * Does nothing if the user is not in the group or if he does not exists.
	 * @param username
	 * @param groupname
	 * @return true iff action was successful. false otherwise
	 * @throws GroupDoesNotExistException 
	 */
	public boolean removeUserFromGroup(String username, String groupname) throws GroupDoesNotExistException{
		checkIfGroupExists(groupname);
		
		// TODO check if the user exists? It does not matter if he exists or not in this method.
		
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
	 * removes the group from the database.</br>
	 * does nothing if the group does not exist.
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
	
	//----------------------------------------------------------------------------------------------
	// Functionalities
	
//	/**
//	 * Invites the user to the given event. The admin is the one inviting.
//	 * @param admin
//	 * @param username
//	 * @param entry_id
//	 * @return
//	 */
//	public boolean inviteUser(String admin, String username, int entry_id){
//		// TODO 
//		
//		throw new NotYetImplementedException();
//	}
//	
//
//	
//	/**
//	 * Invites all users in the group to the given event. The admin is the one inviting.
//	 * @param admin
//	 * @param groupname
//	 * @param entry_id
//	 * @return 
//	 * @throws GroupDoesNotExistException
//	 * @throws UserDoesNotExistException 
//	 * @throws EntryDoesNotExistException 
//	 */
//	public boolean inviteGroup(String admin, String groupname, int entry_id) throws GroupDoesNotExistException, EntryDoesNotExistException, UserDoesNotExistException{
//		checkIfGroupExists(groupname);
//		checkUserAndEntry(admin, entry_id);
//		
//		for(User u : getGroup(groupname).getUsers()){
//			inviteUser(admin, u.getUsername(), entry_id);
//		}
//		return true;
//	}
	
	/**
	 * Creates a Calendar with all the entries the user is allowed to see.
	 * @param user
	 * @return a Calendar instance wit the entries of the given user. An empty calendar if some error occurred.
	 * @throws UserDoesNotExistException 
	 * @throws SQLException 
	 */
	public Calendar createCalendar(String username) throws UserDoesNotExistException{
		
		checkIfUserExists(username);
		
		String select_all_events_for_user = "SELECT E.* "
										  + "FROM CalendarEntry E, User U, Status S "
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
				CalendarEntryBuilder entryB = new CalendarEntryBuilder();
				
				entryB.setEventID(rset.getInt("entryID"));
				entryB.setStartTime(rset.getLong("startTime"));
				entryB.setEndTime(rset.getLong("endTime"));
				entryB.setLocation(rset.getString("location"));
				entryB.setDescription(rset.getString("description"));
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
	
	/**
	 * closes the connection to the DB.
	 * @return
	 */
	@Override
	public void close() throws IOException {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addSQL(String filename) throws IOException {
		ScriptRunner runner=new ScriptRunner(connection);
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
		runner.runScript(reader);
		reader.close();
	}
	
	public static void main(String args[]){
		
		
		// drops the database and add it again. also inserts standard data
		DataBaseManager dbm = new DataBaseManager();
		try {
			dbm.addSQL("addtables.sql");
			dbm.addSQL("insertintotables.sql");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
