package calendar;


import dbms.DataBaseManager;
import user.User;

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
	 */
	public static void main(String[] args) {
		new DataBaseManager().createTables();
	}
}
