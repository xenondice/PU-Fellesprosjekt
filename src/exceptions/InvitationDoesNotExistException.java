package exceptions;

public class InvitationDoesNotExistException extends Exception {
	public InvitationDoesNotExistException() {
	}

	public InvitationDoesNotExistException(String string) {
		super(string);
	}
	
	public InvitationDoesNotExistException(String username, long entry_id) {
		super("The invitation for username: '"+username+"' and entryID: '"+entry_id+"' does not exists!");
	}
}
