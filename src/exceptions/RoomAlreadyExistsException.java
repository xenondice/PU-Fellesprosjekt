package exceptions;

public class RoomAlreadyExistsException extends Exception {

	public RoomAlreadyExistsException(String room_id) {
		super("The room '"+room_id+"' already exists. There can only be one!");
	}

}
