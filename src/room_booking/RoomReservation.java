package room_booking;

public class RoomReservation {

	/**
	 * Represents a roomreservation for a calendar entry at the given timespan in the system
	 */
	private final String room_id;
	private final long startTime; 
	private final long endTime;
	private final long entryID;
	
	/**
	 * Makes an instance of a RoomReservation (constructor)
	 * @param room
	 * @param startTime
	 * @param endTime
	 * @param entryID
	 */
	public RoomReservation(String room_id, long startTime, long endTime, long entryID){
		this.room_id = room_id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.entryID  = entryID;
	}
	
	/**
	 * @return  the Room for the given RoomReservation
	 */
	public String getRoomID(){
		return room_id;
	}
	
	/**
	 * @return the start time (long) for the given RoomReservation
	 */
	public long getStartTime(){
		return startTime;
	}
	
	/**
	 * @return the end time (long) for the given RoomReservation
	 */
	public long getEndTime(){
		return endTime;
	}
	/**
	 * 
	 * @return the entryID (long) for the CalendarEntry at the given RoomReservation
	 */
	public long getEntryID(){
		return entryID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoomReservation [room_id=");
		builder.append(room_id);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", entryID=");
		builder.append(entryID);
		builder.append("]");
		return builder.toString();
	}
	
}
