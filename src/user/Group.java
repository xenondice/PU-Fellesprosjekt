package user;

public class Group {
	private User[] users;
	
	public Group(User[] users){
		this.users = users;
	}
	
	public User[] getUsers(){
		User[] u = new User[users.length];
		for(int i = 0; i < u.length; i++){
			u[i] = users[i].clone();
		}
		return u;
	}
}
