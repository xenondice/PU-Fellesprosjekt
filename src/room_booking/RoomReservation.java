package room_booking;

public class RoomReservation {

	
	private final Room room;
	private final long startTime; 
	private final long endTime;
	private final long entryID;
	
	public RoomReservation(Room room, long startTime, long endTime, long entryID){
		this.room = room;
		this.startTime = startTime;
		this.endTime = endTime;
		this.entryID  = entryID;
	}
	
	public Room getRoom(){
		return room;
	}
	
	public long getStartTime(){
		return startTime;
	}
	
	public long getEndTime(){
		return endTime;
	}
	
	public long getEntryID(){
		return entryID;
	}
	
}
