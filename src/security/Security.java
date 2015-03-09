
package security;

import user.User;
import calendar.CalendarEntry;

import com.mysql.jdbc.exceptions.NotYetImplementedException;


public class Security {
	
	public Security() {
		
	}
	
	/**
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
	 * @return true iff the user is allowed to edit the entry
	 */
	public boolean canEdit(User u, CalendarEntry e){
		// TODO
		throw new NotYetImplementedException();
	}
	
	/**
	 * 
	 * @param username
	 * @return true iff the user is logged in
	 */
	public boolean isLoggedIn(String username){
		// TODO
		throw new NotYetImplementedException();
	}
	
	public boolean checkPassword(String username, String pw){
		// TODO
		throw new NotYetImplementedException();
	}
	
	public String logIn(String username){
		// TODO
		throw new NotYetImplementedException();
	}
	
	public boolean logOut(String username){
		// TODO
		throw new NotYetImplementedException();
	}
}
