package dbms;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import room_booking.Room;
import user.User;
import calendar.Calendar;
import calendar.CalendarBuilder;
import calendar.Entry;
import calendar.EntryBuilder;

/**
 * This class is the connection to the Data Base.
 * It provides all interactions with the DB.
 * It also parses DB entries to java instances.
 *
 */
public class DataBaseManager {
	private Connection connection;
	//private final String DB_url = "jdbc:mysql://mysql.stud.ntnu.no/mariessa_pu";
	//private final String username = "mariessa_pu";
	//private final String password = "fellesprosjekt";
	
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

	/**
	 *  To edit an existing entry use editEntry(Entry e) instead.
	 * @return true if the action was successful. False otherwise.
	 * @param e the entry
	 * @param u the user creating the entry
	 */
	public boolean addEntry(Entry e, User u){
		
		
		try {
			// add the entry
			String insert_entry = "INSERT INTO Entry (startTime, endTime, location, description, isActive, roomID) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement addEntry_stmt = connection.prepareStatement(insert_entry);
			addEntry_stmt.setString(1, e.getStartTime());
			addEntry_stmt.setString(2, e.getEndTime());
			addEntry_stmt.setString(3, e.getLocation());
			addEntry_stmt.setString(4, e.getDescription());
			addEntry_stmt.setBoolean(5, e.isActive());
			addEntry_stmt.setString(6, e.getRoomID());
			
			addEntry_stmt.executeUpdate();
			
			// get entry_id of the just added entry
			String get_id = "SELECT MAX(entryID) FROM Entry;";
			Statement get_id_stmt = connection.createStatement();
			ResultSet rsetID = get_id_stmt.executeQuery(get_id);
			rsetID.next();
			int entry_id = rsetID.getInt(1);
			System.out.println(entry_id);
			
			// add the user-entry relation
			String add_isAdmin = "INSERT INTO IsAdmin VALUES (?, ?);";
			PreparedStatement addisAdmin_stmt = connection.prepareStatement(add_isAdmin);
			addisAdmin_stmt.setString(1, u.getUsername());
			addisAdmin_stmt.setInt(2, entry_id);
			
			addisAdmin_stmt.executeUpdate();
			
			//add the users status to that event.
			String add_status = "INSERT INTO Status (isGoing, isShowing, username, entryID) VALUES (1, 1, ?, ?);";
			PreparedStatement addStatus_stmt = connection.prepareStatement(add_status);
			addStatus_stmt.setString(1, u.getUsername());
			addStatus_stmt.setInt(2, entry_id);
			
			addStatus_stmt.executeUpdate();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;	
	}
	
	/**
	 * Changes the entry in the DB with the same entryID as the specified entry e.
	 * @param e
	 * @return true iff the action was successful.
	 */
	public boolean editEntry(Entry e){
		// TODO
		return false;
	}
	
	
	
	/**
	 * adds the User to the DB
	 * @param u
	 * @return true if the action was successful. False otherwise.
	 */
	public boolean addUser(User u){
		// TODO
		String addUser = "insert INTO User VALUES ("+u.getUsername() + "," + u.getName() + "," + u.getPassword() +"," + u.getSalt() + "," + u.getEmail() +");";
		try {
			Statement stm = connection.createStatement();
			stm.execute(addUser);
			stm.close();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * adds the Room to the DB
	 * @param r
	 * @return true if the action was successful. False otherwise.
	 */
	public boolean addRoom(Room r){
		String addRoom = "insert INTO Room VALUES ("+r.getRoom_id() + "," + r.getSize() +");";
		try {
			Statement stm = connection.createStatement();
			stm.execute(addRoom);
			stm.close();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Creates a Calendar with all the entries the user is allowed to see.
	 * @param user
	 * @return a Calendar instance wit the entries of the given user. An empty calendar if some error occurred.
	 */
	public Calendar createCalendar(User user){
		// TODO conversion from Timestamp to string
		// String S = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(myTimestamp);
		
		// TODO better with JOIN?
		String select_all_events_for_user = "SELECT E.* "
										  + "FROM Entry E, User U, Status S "
										  + "WHERE S.isShowing = 1 "
										  	+ "AND E.eventID = S.eventID "
										  	+ "AND U.username = S.username"
										  	+ "AND U.username=?";
		
		
		CalendarBuilder calendarB = new CalendarBuilder();
		calendarB.addUser(user);
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement(select_all_events_for_user);
			stmt.setString(1, user.getUsername());
			ResultSet rset = stmt.executeQuery();
			
			while(rset.next()){
				EntryBuilder entryB = new EntryBuilder();
				
				entryB.setEventID(rset.getString("eventID"));
				entryB.setStartTime(rset.getString("startTime"));
				entryB.setEndTime(rset.getString("endTime"));
				entryB.setLocation(rset.getString("location"));
				entryB.setDescription(rset.getString("description"));
				entryB.setIsActive(rset.getBoolean("isActive"));
				entryB.setRoomID(rset.getString("roomID"));
								
				calendarB.addEntry(entryB.build());
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return new CalendarBuilder().build();
		}
		
		return calendarB.build();
	}
	
	public void createTables() {
		try {
			Statement statement = connection.createStatement();
			
			statement.execute(""
					+ "CREATE TABLE User ("
					+ "	username"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		PRIMARY KEY,"
					+ "	name"
					+ "		VARCHAR(30)"
					+ "		NOT NULL,"
					+ "	password"
					+ "		VARCHAR(20)"
					+ "		NOT NULL,"
					+ "	salt"
					+ "		VARCHAR(30)"
					+ "		NOT NULL,"
					+ "	email"
					+ "		VARCHAR(50)"
					+ "		NOT NULL"
					+ ");");
			
			statement.execute(""
					+ "CREATE TABLE Room ("
					+ "	roomID"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		PRIMARY KEY,"
					+ "	size"
					+ "		INT"
					+ "		NOT NULL"
					+ ");");
			
			statement.execute(""
					+ "CREATE TABLE Entry ("
					+ "	entryID"
					+ "		INT"
					+ "		NOT NULL"
					+ "		PRIMARY KEY"
					+ "		AUTO_INCREMENT,"
					+ "	startTime"
					+ "		TIMESTAMP"
					+ "		NOT NULL,"
					+ "	endTime"
					+ "		TIMESTAMP"
					+ "		NOT NULL,"
					+ "	location"
					+ "		VARCHAR(20),"
					+ "	description"
					+ "		VARCHAR(100)"
					+ "		NOT NULL,"
					+ "	isActive"
					+ "		BOOLEAN"
					+ "		DEFAULT TRUE,"
					+ "	roomID"
					+ "		VARCHAR(10)"
					+ "		REFERENCES Room"
					+ ");");
			
			statement.execute(""
					+ "CREATE TABLE Status ("
					+ "	isGoing"
					+ "		BOOLEAN, "
					+ "		DEFAULT TRUE"
					+ "	isShowing"
					+ "		BOOLEAN"
					+ "		DEFAULT TRUE,"
					+ "	username"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		REFERENCES User"
					+ "		ON DELETE CASCADE"
					+ "		ON UPDATE CASCADE,"
					+ "	entryID"
					+ "		INT"
					+ "		NOT NULL"
					+ "		REFERENCES Entry"
					+ "		ON DELETE CASCADE"
					+ "		ON UPDATE CASCADE,"
					+ "	PRIMARY KEY (username, entryID)"
					+ ");");
			
			statement.execute(""
					+ "CREATE TABLE Notification ("
					+ "	description"
					+ "		VARCHAR(100)"
					+ "		NOT NULL,"
					+ "	isOpened"
					+ "		BOOLEAN"
					+ "		DEFAULT FALSE,"
					+ "	time"
					+ "		TIMESTAMP"
					+ "		NOT NULL,"
					+ "	username"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		REFERENCES User"
					+ "		ON DELETE CASCADE"
					+ "		ON UPDATE CASCADE,"
					+ "	entryID"
					+ "		INT"
					+ "		NOT NULL"
					+ "		REFERENCES Entry"
					+ "		ON UPDATE CASCADE"
					+ "		ON DELETE CASCADE,"
					+ "	PRIMARY KEY (username, entryID)"
					+ ");");
			
			statement.execute(""
					+ "CREATE TABLE IsAdmin ("
					+ "	entryID"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		REFERENCES Entry"
					+ "		ON UPDATE CASCADE"
					+ "		ON DELETE CASCADE,"
					+ "	username"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		REFERENCES User"
					+ "		ON DELETE CASCADE"
					+ "		ON UPDATE CASCADE,"
					+ "	PRIMARY KEY (username, entryID)"
					+ ");");
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void main(String[] args) {
		Entry e = new Entry("1", "1992-02-02 11:00:00", "1992-02-02 12:00:00", "forrest", "happy meeting with a tree", true, "");
		User u = new User("lukasap", "Luaks", "222", "", "lukasap@stud.ntnu.no");
		DataBaseManager dbms = new DataBaseManager();
		dbms.addEntry(e, u);
	}
}
