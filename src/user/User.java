package user;

public class User {
	private String username;
	
	public User(String username){
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	/**
	 * returns identical (but new) instance of this user.
	 */
	public User clone(){
		return new User(username);
	}
	
}
