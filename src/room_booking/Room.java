package room_booking;

/**
 * Represents a Room
 */
public class Room {
	
	private final String room_id;
	private final int size;
	
	public Room(String room_id, int size){
		this.room_id = room_id;
		this.size = size;
	}
	
	public String getRoom_id() {
		return room_id;
	}
	public int getSize() {
		return size;
	}
	
	/**
	 * returns a complete (deep-)copy of this room.
	 */
	public Room clone(){
		return new Room(room_id, size);
	}

}
