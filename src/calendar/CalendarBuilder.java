package calendar;

import java.util.ArrayList;

import interfaces.Builder;
import user.User;

public class CalendarBuilder implements Builder<Calendar>{
	private ArrayList<Entry> entries = new ArrayList<Entry>();
	private ArrayList<User> users = new ArrayList<User>();
	
	public CalendarBuilder(){
		
	}
	
	@Override
	public Calendar build(){
		return new Calendar((Entry[]) entries.toArray(), (User[]) users.toArray());
	}
	
	public void addUser(User u){
		users.add(u);
	}
	
	public void addEntry(Entry e){
		entries.add(e);
	}
	
	public ArrayList<Entry> getEntries() {
		return entries;
	}
	 
	public ArrayList<User> getUsers() {
		return users;
	}
	 
}
