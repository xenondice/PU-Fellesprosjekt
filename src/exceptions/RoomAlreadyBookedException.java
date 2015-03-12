package exceptions;

public class RoomAlreadyBookedException extends Exception {

	public RoomAlreadyBookedException(String room_id) {
		super("The room '"+room_id+"' is already booked for this timespan");
	}

}
