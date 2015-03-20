
package user;

import java.util.Arrays;

/**
 * Represents a Group in the system
 * @author Marie
 *
 */
public class Group {
	private User[] users;
	private String name;
	
	/**
	 * The Group class' constructor
	 * @param users
	 * @param name
	 */
	public Group(User[] users, String name){
		this.users = users;
		this.name = name;
	}
	
	/**
	 * 
	 * @return true iff no users are in the group.
	 */
	public boolean isEmpty(){
		return users.length == 0;
	}
	
	/**
	 * 
	 * @return a list of the Users in the given group
	 */
	public User[] getUsers(){
		User[] u = new User[users.length];
		for(int i = 0; i < u.length; i++){
			u[i] = users[i].clone();
		}
		return u;
	}
	/**
	 * 
	 * @return the name (String) of the group
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Groupname= ");
		builder.append(name);
		builder.append("\n");
		for(User u : users){
			builder.append("-> ");
			builder.append(u.toString());
			builder.append("\n");
		}
		return builder.toString();
	}
}