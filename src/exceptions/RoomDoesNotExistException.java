package exceptions;

public class RoomDoesNotExistException extends Exception {

	public RoomDoesNotExistException(String room_id) {
		super("The room '"+room_id+"' does not exist in the DB");
	}

}
