package user;

import interfaces.Builder;

/**
 * This class is here to incrementally build a immutable User. 
 *
 */
public class UserBuilder implements Builder<User>{
	private String username = null;
	private String name = null; 
	private String password = null;
	private String salt = null;
	private String email = null;
	
	public UserBuilder(){
		
	}
	
	public void setUsername(String newusername){
		this.username = newusername;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}

	@Override
	public User build() {
		return new User(username, name, password, salt, email);
	}
}
