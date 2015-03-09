package exceptions;

public class UserDoesNotExistException extends Exception {

	public UserDoesNotExistException() {
	}
	
	public UserDoesNotExistException(String username){
		super("'"+username+"' does not exist in the DB");
	}
}




