package exceptions;

public class GroupAlreadyExistsException extends Exception {
	public GroupAlreadyExistsException() {
	}
	
	public GroupAlreadyExistsException(String groupname){
		super("The group '"+groupname+"' is already taken");
	}
}
