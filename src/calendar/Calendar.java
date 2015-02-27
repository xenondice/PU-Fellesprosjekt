package calendar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import room_booking.RoomBuilder;
import dbms.DataBaseManager;
import user.User;
import user.UserBuilder;

/**
 * This class is used to model a calendar with several Entries and users
 * The class is immutable. That means no instance of this class can change any of its attributes.
 */
public class Calendar {
	private final Entry[] entries;
	private final User[] users;
	
	/**
	 * 
	 * @param entries: the entries of this calendar.
	 * @param users; the users belonging to this calendar.
	 */
	public Calendar(Entry[] entries, User[] users){
		this.entries = entries;
		this.users = users;
	}
	
	/**
	 * 
	 * @return a copy entries of this calendar in an array.
	 */
	public Entry[] getEntries() {
		Entry[] clonedList = new Entry[entries.length];
		for(int i = 0; i < clonedList.length; i++){
			clonedList[i] = entries[i].clone();
		}
		return clonedList;
	}
	
	/**
	 * 
	 * @return a copy of the users of this calendar in an array.
	 */
	public User[] getUsers() {
		User[] clonedList = new User[users.length];
		for(int i = 0; i < clonedList.length; i++){
			clonedList[i] = users[i].clone();
		}
		return clonedList;
	}
	
	/**
	 * main function for testing and eventually running, should really be in request handler
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		DataBaseManager dbm = new DataBaseManager();
		
		while (true) {
			while (reader.ready()) {
				String input = reader.readLine();
				String[] parted = input.split(" ");
				if (parted[0].equals("calendar")) {
					Calendar cal = dbm.createCalendar(dbm.getUser(parted[1]));
					System.out.println(cal);
				} else if (parted[0].equals("adduser")) {
					UserBuilder ub = new UserBuilder();
					ub.setUsername(parted[1]);
					ub.setName(parted[2]);
					ub.setPassword(parted[3]);
					ub.setSalt("");
					ub.setEmail(parted[4]);
					if (dbm.addUser(ub.build())) System.out.println("Success!");
					else System.out.println("Failure!");
				} else if (parted[0].equals("addroom")) {
					RoomBuilder rb = new RoomBuilder();
					rb.setRoom_id(parted[1]);
					rb.setSize(Integer.parseInt(parted[2]));
					if (dbm.addRoom(rb.build())) System.out.println("Success!");
					else System.out.println("Failure!");
				} else if (parted[0].equals("addentry")) {
					EntryBuilder eb = new EntryBuilder();
					eb.setStartTime(new Date(Long.parseLong(parted[1])));
					eb.setEndTime(new Date(Long.parseLong(parted[2])));
					eb.setDescription(parted[3]);
					eb.setLocation(parted[3]);
					if (dbm.addEntry(eb.build())) System.out.println("Success!");
					else System.out.println("Failure!");
				} else if (parted[0].equals("help")) {
					System.out.println("Commands:\n"
							+ "adduser username name(only one name) password email\n"
							+ "addroom roomid roomsize\n"
							+ "addentry starttime(milliseconds since 1960 00:00) endtime(same) description location\n");
				} else {
					System.out.println("Invalid input");
				}
			}
		}
	}
}
