package exceptions;

public class GroupDoesNotExistException extends Exception {

	public GroupDoesNotExistException() {
	}

	public GroupDoesNotExistException(String groupname) {
		super("The group '"+groupname+"' does not exist in the DB");
	}
}
