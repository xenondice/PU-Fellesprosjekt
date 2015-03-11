package room_booking;

public class RoomReservation {

	
	private final Room room;
	private final long startTime; 
	private final long endTime;
	
	public RoomReservation(Room room, long startTime, long endTime){
		this.room = room;
		this.startTime = startTime;
		this.endTime = endTime;
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
}
