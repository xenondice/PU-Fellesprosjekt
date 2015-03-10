package exceptions;

public class AlarmDoesNotExistException extends Exception {

	public AlarmDoesNotExistException(String username, long entry_id) {
		super("the alarm for '"+username+"' and entry '"+entry_id+"' does not exist");
	}
	
	public AlarmDoesNotExistException(){}

}
