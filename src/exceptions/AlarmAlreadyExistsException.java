package exceptions;


public class AlarmAlreadyExistsException extends Exception {

	public AlarmAlreadyExistsException(String username, long entry_id) {
		super("the alarm for '"+username+"' and entry '"+entry_id+"' does already exist. There can be only one!");
	}
	
	public AlarmAlreadyExistsException(){}

}
