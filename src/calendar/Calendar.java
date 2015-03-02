package calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		
		System.out.println("Awating input:");
		
		while (true) {
			while (reader.ready()) {				
				String input = reader.readLine();
				System.out.println();
				
				List<String> splitted_input = new ArrayList<>();
				
				//Find all single words, "stuff in these" or 'stuff in these'
				Pattern regex_pattern = Pattern.compile("([^\\s\"']+)|\"([^\"]*)\"|'([^']*)'");
				Matcher matcher = regex_pattern.matcher(input);
				
				while (matcher.find())
					for (int i = 1; i <= 3; i++)
						if (matcher.group(i) != null)
							splitted_input.add(matcher.group(i));
				
				if (splitted_input.get(0).equals("calendar")) {
					
					if (splitted_input.size() != 2) {
						System.out.println("Invalid amount of arguments!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					User user = dbm.getUser(splitted_input.get(1));
					
					if (user == null)
						System.out.println("Couldn't find user!");
					else {
						Calendar cal = dbm.createCalendar(user);
						if (cal.equals(null))
							System.out.println("Failure!");
						else
							System.out.println(cal);
					}
				
				} else if (splitted_input.get(0).equals("adduser")) {
					
					if (splitted_input.size() != 5) {
						System.out.println("Invalid amount of arguments!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					UserBuilder ub = new UserBuilder();
						ub.setUsername(splitted_input.get(1));
						ub.setName(splitted_input.get(2));
						ub.setPassword(splitted_input.get(3));
						ub.setSalt("");
						ub.setEmail(splitted_input.get(4));
					
					if (dbm.addUser(ub.build()))
						System.out.println("Success!");
					else
						System.out.println("Failure!");
				
				} else if (splitted_input.get(0).equals("addroom")) {
					
					if (splitted_input.size() != 3) {
						System.out.println("Invalid amount of arguments!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					RoomBuilder rb = new RoomBuilder();
						rb.setRoom_id(splitted_input.get(1));
						
					try {
						rb.setSize(Integer.parseInt(splitted_input.get(2)));
					} catch (NumberFormatException e) {
						System.out.println("Invalid number!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					if (dbm.addRoom(rb.build()))
						System.out.println("Success!");
					else
						System.out.println("Failure!");
				
				} else if (splitted_input.get(0).equals("addentry")) {
					
					if (splitted_input.size() != 6) {
						System.out.println("Invalid amount of arguments!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					EntryBuilder eb = new EntryBuilder();

					try {
						eb.setStartTime(Long.parseLong(splitted_input.get(2)));
						eb.setEndTime(Long.parseLong(splitted_input.get(3)));
					} catch (NumberFormatException e) {
						System.out.println("Invalid number!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
						eb.setDescription(splitted_input.get(4));
						eb.setLocation(splitted_input.get(5));
					
					User user = dbm.getUser(splitted_input.get(1));
					
					if (user == null)
						System.out.println("Couldn't find user!");
					else {	
						if (dbm.addEntry(eb.build(), user))
							System.out.println("Success!");
						else
							System.out.println("Failure!");
					}
				
				} else if (splitted_input.get(0).equals("canedit")) {
					
					if (splitted_input.size() != 3) {
						System.out.println("Invalid amount of arguments!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					int entryid;
					try {
						entryid = Integer.valueOf(splitted_input.get(2));
					} catch (NumberFormatException e) {
						System.out.println("Invalid number!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					String username = splitted_input.get(1);
					
					if (dbm.getUser(username) == null)
						System.out.println("Couldn't find user!");
					else if (dbm.getEntry(entryid) == null)
						System.out.println("Couldn't find event!");
					else if (dbm.canEdit(username, entryid))
						System.out.println("User can edit entry!");
					else
						System.out.println("User can't edit entry!");
					
				} else if (splitted_input.get(0).equals("help")) {
					
					System.out.println("Commands:");
					System.out.println(" * calendar username");
					System.out.println(" * adduser username name (use quotes) password email");
					System.out.println(" * addroom roomid roomsize");
					System.out.println(" * addentry admin_username starttime (milliseconds since 1970 00:00) endtime (same) description location");
					System.out.println(" * canedit username entryid");
					System.out.println(" * exit");
				
				} else if(splitted_input.get(0).equals("exit")){
					
					System.out.println("Exiting...");
					System.exit(0);
				
				} else
					System.out.println("Invalid input, type help for a list over commands");
				
				System.out.println();
				System.out.println("Awating input:");
			}
		}
	}
}
