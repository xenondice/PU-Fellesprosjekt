package calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Calendar for users [");
		for(User u: users){
			sb.append(u.toString());
			sb.append(", ");
		}
		sb.delete(sb.length()-2, sb.length());
		sb.append("]\n");
		sb.append("\n");
		sb.append("Entries: \n");
		for(Entry e : entries){
			sb.append(e.toString());
			sb.append("\n");
		}
		sb.append("---------------");
		return sb.toString();
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
					eb.setStartTime(new Date(Long.parseLong(parted[2])));
					eb.setEndTime(new Date(Long.parseLong(parted[3])));
					eb.setDescription(parted[4]);
					eb.setLocation(parted[5]);
					if (dbm.addEntry(eb.build(), dbm.getUser(parted[1]))) System.out.println("Success!");
					else System.out.println("Failure!");
				} else if (parted[0].equals("help")) {
					System.out.println("Commands:\n"
							+ "calendar username\n"
							+ "adduser username name(only one name) password email\n"
							+ "addroom roomid roomsize\n"
							+ "addentry admin_username starttime(milliseconds since 1960 00:00) endtime(same) description location"
							+ "canEdit username entryID\n"
							+ "exit");
				} else if(parted[0].equals("exit")){
					System.out.println("exiting...");
					System.exit(0);
				}else if(parted[0].equals("canEdit")){
					boolean res = dbm.canEdit(parted[1], Integer.valueOf(parted[2]));
					System.out.println(res);
				}else{
					System.out.println("Invalid input");
				}
			}
		}
	}
}
