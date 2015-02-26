package dbms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import room_booking.Room;
import user.User;
import calendar.Calendar;
import calendar.CalendarBuilder;
import calendar.Entry;
import calendar.EntryBuilder;

public class DataBaseManager {
	private Connection connection;
	
	
	public DataBaseManager(){
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://mysql.stud.ntnu.no/mariessa_pu", "mariessa_pu", "fellesprosjekt");		
		
		
		} catch (SQLException e) { 
			e.printStackTrace();
			connection = null;
		}
		
	}
	
	public boolean addEntry(Entry e){
		// @TODO
		return false;
	}
	
	public boolean addUser(User u){
		// @TODO
		return false;
	}
	
	public boolean addRoom(Room r){
		// @TODO
		return false;
	}
	
	public Calendar createCalendar(User user){
		
		
		// @TODO better with JOIN?
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
		} catch (SQLException e) {e.printStackTrace();}
		
		return calendarB.build();
	}
	
	/**
	 * prepares and executes an executes a SQL statement.
	 * Example of use: ResultSet result = executeStatement(connection, "INSERT INTO User VALUES (name=?, age=?, gender=?)", "Olav", "12", "male");
	 * @param con -> the connection to be used.
	 * @param statement -> eg "INSERT INTO User VALUES (name=?, age=?, gender=?)"
	 * @param arguments -> the string attributes represented by a ? in the statement string.
	 * @return 
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
	
	
	
	
	public static void main(String[] args) {
		
	}
}
