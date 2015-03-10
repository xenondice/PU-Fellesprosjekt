package exceptions;

public class NotificationDoesNotExistException extends Exception {

	public NotificationDoesNotExistException(long notification_id) {
		super("the notification '"+notification_id+"' does not exist.");
	}
	
	

}
