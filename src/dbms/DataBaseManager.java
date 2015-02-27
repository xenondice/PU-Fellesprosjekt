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
	private static Connection connection;
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
			connection = DriverManager.getConnection(ci[0], ci[1], ci[2]);
		} catch (SQLException e) {
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
	 * adds the Entry to the DB. It will be treated as a new Entry.
	 * To edit an existing entry use editEntry(Entry e) instead.
	 * @param e
	 * @return true if the action was successful. False otherwise.
	 */
	public boolean addEntry(Entry e){
		String insert_entry = "INSERT INTO Entry (startTime, endTime, location, description, isActive, roomID) "
				+ "VALUES (?, ?, ?; ?; ?; ?)";
		
		PreparedStatement stmt;
		try {
			stmt = connection.prepareStatement(insert_entry);
			stmt.setString(1, e.getStartTime());
			stmt.setString(2, e.getEndTime());
			stmt.setString(3, e.getLocation());
			stmt.setString(4, e.getDescription());
			stmt.setBoolean(5, e.isActive());
			stmt.setString(6, e.getRoomID());
			
			stmt.executeQuery();
			
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
		return false;
	}
	
	/**
	 * adds the Room to the DB
	 * @param r
	 * @return true if the action was successful. False otherwise.
	 */
	public boolean addRoom(Room r){
		// TODO
		return false;
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
		ResultSet rset = executeStatement(connection, select_all_events_for_user, user.getUsername());
		
		
		
		CalendarBuilder calendarB = new CalendarBuilder();
		calendarB.addUser(user);
		
		try {
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
	
	/**
	 * prepares and executes an executes a SQL statement.
	 * Example of use: ResultSet result = executeStatement(connection, "INSERT INTO User VALUES (name=?, age=?, gender=?)", "Olav", "12", "male");
	 * @param con -> the connection to be used.
	 * @param statement -> eg "INSERT INTO User VALUES (name=?, age=?, gender=?)"
	 * @param arguments -> the string attributes represented by a ? in the statement string.
	 * @return A Result set containing the results of the query
	 */
	public static ResultSet executeStatement(Connection con, String statement, String... arguments){
		try {
			PreparedStatement stmt = con.prepareStatement(statement);
			
			for (int i = 0; i < arguments.length; i++){
				stmt.setObject(i+1, arguments[i]);
			}
			return stmt.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void createTables() {
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
					+ "		BOOLEAN,"
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
}
