package room_booking;

import interfaces.Builder;

/**
 * This class is here to incrementally build a immutable Room. 
 *
 */
public class RoomBuilder implements Builder<Room> {
	
	private String room_id = null;
	private int size = 0;
	
	public RoomBuilder() {
		
	}
	
	public void setRoom_id(String room_id) {
		this.room_id = room_id;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getRoom_id() {
		return room_id;
	}
	public int getSize() {
		return size;
	}

	@Override
	public Room build() {
		return new Room(room_id, size);
	}

}
