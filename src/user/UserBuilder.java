package user;

import interfaces.Builder;

public class UserBuilder implements Builder<User>{
	private String username = null;
	
	public UserBuilder(){
		
	}
	
	public void setUsername(String newusername){
		this.username = newusername;
	}

	@Override
	public User build() {
		// TODO Auto-generated method stub
		return null;
	}
}
