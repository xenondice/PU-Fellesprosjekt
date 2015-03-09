package calendar;

import java.util.ArrayList;

import interfaces.Builder;
import user.User;

/**
 * This class is here to incrementally build a immutable calendar. 
 *
 */
public class CalendarBuilder implements Builder<Calendar>{
	private ArrayList<CalendarEntry> calendarEntries = new ArrayList<CalendarEntry>();
	private ArrayList<User> users = new ArrayList<User>();
	
	public CalendarBuilder(){
		
	}
	
	@Override
	public Calendar build(){
		CalendarEntry[] earary = new CalendarEntry[calendarEntries.size()];
		User[] uarary = new User[users.size()];
		return new Calendar(calendarEntries.toArray(earary), users.toArray(uarary));
	}

	
	/**
	 * adds a User to the calendar
	 * @param u
	 */
	public void addUser(User u){
		users.add(u);
	}
	
	/**
	 * Adsd an entry to the calendar
	 * @param e
	 */
	public void addEntry(CalendarEntry e){
		calendarEntries.add(e);
	}
	
	/**
	 * 
	 * @return: the entry arrayList of this builder
	 */
	public ArrayList<CalendarEntry> getEntries() {
		return calendarEntries;
	}
	
	/**
	 * 
	 * @return the user arryList of this builder
	 */
	public ArrayList<User> getUsers() {
		return users;
	}
	 
}
