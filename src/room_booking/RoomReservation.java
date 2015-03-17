package room_booking;

public class RoomReservation {

	/**
	 * Represents a roomreservation for a calendar entry at the given timespan in the system
	 */
	private final Room room;
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
	public RoomReservation(Room room, long startTime, long endTime, long entryID){
		this.room = room;
		this.startTime = startTime;
		this.endTime = endTime;
		this.entryID  = entryID;
	}
	
	/**
	 * @return  the Room for the given RoomReservation
	 */
	public Room getRoom(){
		return room;
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
	
}
