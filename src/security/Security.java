package security;

import com.mysql.jdbc.exceptions.NotYetImplementedException;

import calendar.Entry;
import user.User;

public class Security {
	
	public Security() {
		
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
