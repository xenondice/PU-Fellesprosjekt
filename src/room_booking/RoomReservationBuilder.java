package room_booking;

import interfaces.Builder;

public class RoomReservationBuilder implements Builder<RoomReservation> {

	
	private String room_id = null;
	private long startTime  = 0; 
	private long endTime = 0;
	private long entryID = 0;
	
	public RoomReservationBuilder(){
		
	}

	public void setEntryID(long entryID) {
		this.entryID = entryID;
	}

	public void setRoomID(String room_id) {
		this.room_id = room_id;
	}


	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}


	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	
	@Override
	public RoomReservation build() {
		return new RoomReservation(room_id, startTime, endTime, entryID);
	}

	public String getRoom_id() {
		return room_id;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getEntryID() {
		return entryID;
	}
}
