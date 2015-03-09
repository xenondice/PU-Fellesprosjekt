package exceptions;

public class UserInGroupDoesNotExistsException extends Exception {
	public UserInGroupDoesNotExistsException() {
	}

	public UserInGroupDoesNotExistsException(String string) {
		super(string);
	}
	
	public UserInGroupDoesNotExistsException(String username, String groupname) {
		super("The user '"+username+"' in the group '"+groupname+"' does not exist!");
	}
}
