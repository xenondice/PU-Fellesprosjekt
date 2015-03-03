package user;

public class Group {
	private User[] users;
	private String name;
	
	public Group(User[] users, String name){
		this.users = users;
		this.name = name;
	}
	
	public User[] getUsers(){
		User[] u = new User[users.length];
		for(int i = 0; i < u.length; i++){
			u[i] = users[i].clone();
		}
		return u;
	}
	
	public String getName() {
		return name;
	}
}
