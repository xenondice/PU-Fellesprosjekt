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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((room_id == null) ? 0 : room_id.hashCode());
		result = prime * result + size;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Room)) {
			return false;
		}
		Room other = (Room) obj;
		if (room_id == null) {
			if (other.room_id != null) {
				return false;
			}
		} else if (!room_id.equals(other.room_id)) {
			return false;
		}
		if (size != other.size) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Room [room_id=");
		builder.append(room_id);
		builder.append(", size=");
		builder.append(size);
		builder.append("]");
		return builder.toString();
	}

}
