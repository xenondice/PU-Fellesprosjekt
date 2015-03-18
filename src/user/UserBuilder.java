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
	private String email = null;
	
	public UserBuilder(){
		
	}
	
	public UserBuilder(User user) {
		setUsername(user.getUsername());
		setEmail(user.getEmail());
		setName(user.getName());
		setPassword(user.getPassword());
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
	


	@Override
	public User build() {
		return new User(username, name, password, email);
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}
}
