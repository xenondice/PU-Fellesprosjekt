package room_booking;

public class RoomReservation {

	
	private final Room room;
	private final long startTime; 
	private final long endTime;
	private final String username;
	
	public RoomReservation(Room room, long startTime, long endTime, String username){
		this.room = room;
		this.startTime = startTime;
		this.endTime = endTime;
		this.username = username;
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
	
	public String getUsername(){
		return username;
	}
	
}
