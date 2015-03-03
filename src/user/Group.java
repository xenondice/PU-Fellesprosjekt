<<<<<<< HEAD
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
=======
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
>>>>>>> 00fa8995f9372bcb95ea73d619eb541aa17214c0
