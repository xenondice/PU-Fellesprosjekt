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
import org.apache.ibatis.jdbc.ScriptRunner;

import com.mysql.jdbc.exceptions.NotYetImplementedException;

import exceptions.EntryDoesNotExistException;
import exceptions.GroupDoesNotExistException;
import exceptions.UserDoesNotExistException;

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

	/**
	 * adds the User to the DB
	 * @param u
	 * @return true if the action was successful. False otherwise.
	 */
	public boolean addUser(User u){
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
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * u replaces the user with the same username in the DB
	 * @param u
	 * @return true iff successful.
	 */
	public boolean editUser(User u){
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
			stm = connection
					.prepareStatement("SELECT * FROM User WHERE username=?");

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
			} else
				throw new UserDoesNotExistException("");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
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
			} else
				throw new EntryDoesNotExistException("");
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
	 */
	public boolean addEntry(Entry e, String username){
		if(addIntoEntry(e)){
			int entryID = getLastEntryID();
			return addIntoIsAdmin(username, entryID) && addIntoStatus(true, true, username, entryID);
		}else{
			return false;
		}
	}
	
	/**
	 * newEntry replaces the entry in the DB with the same entry_id as newEntry. the entry_id stays the same.
	 * @param newEntry the new entry. replaces the old one
	 * @return true iff the action was successful.
	 */
	public boolean editEntry(Entry newEntry, String username){
		// TODO maybe make it that only the not Null attributes of the newEntry are changed from the old one.
		// TODO handle the Status and IsAdmin tables
		
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

	private boolean addIntoIsAdmin(String username, int entry_id) {
		// add the user-entry relation
		try{
			String add_isAdmin = "INSERT INTO IsAdmin VALUES (?, ?);";
			PreparedStatement addIsAdmin_stmt = connection
					.prepareStatement(add_isAdmin);
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
		String add_status = "INSERT INTO Status (isGoing, isShowing, username, entryID) VALUES (?, ?, ?, ?);";
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
				// TODO change groupID to groupName in DataBase! And varchar(100) not (10)
				
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
		PreparedStatement stm;
		try {
			stm = connection.prepareStatement("INSERT INTO MemberOf () Values (?, ?)");
			// TODO
			stm.setString(1, name);
			
		catch (SQLException e) {
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
		// TODO 
		return false;
	}

	/**
	 * removes the group from the database.
	 * @param groupname
	 * @return
	 */
	public boolean deleteGroup(String groupname){
		// TODO
		return false;
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
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean deleteRoom(String roomID){
		// TODO 
		throw new NotYetImplementedException();
	}
	
	public boolean editRoom(String roomID){
		// TODO 
		throw new NotYetImplementedException();
	}

	/*==============================
	 * Authorisation functions
	 *==============================*/
	
	
	/**
	 * 
	 * @param u
	 * @param e
	 * @return true iff the user is allowed to see the given entry
	 */
	public boolean isAllowedToSee(String username, int entry_id){
		// TODO
		throw new NotYetImplementedException();
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
		// TODO conversion from Timestamp to string
		// String S = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(myTimestamp);
		
		// TODO check if the user actually exists in the DB
		
		// TODO better with JOIN?
		String select_all_events_for_user = "SELECT E.* "
										  + "FROM Entry E, User U, Status S "
										  + "WHERE S.isShowing = 1 "
										  	+ "AND E.entryID = S.entryID "
										  	+ "AND U.username = S.username "
										  	+ "AND U.username = ? ;";
		
		CalendarBuilder calendarB = new CalendarBuilder();
		
		
		try {
			calendarB.addUser(this.getUser(username));
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
								
				calendarB.addEntry(entryB.build());
				
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			
			return new CalendarBuilder().build();
		}
		
		return calendarB.build();
	}
	
	public void addSQL(String filename) throws IOException {
		ScriptRunner runner=new ScriptRunner(connection);
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
		runner.runScript(reader);
		reader.close();
	}
}
