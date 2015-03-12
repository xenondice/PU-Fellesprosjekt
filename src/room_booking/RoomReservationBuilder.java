package room_booking;

import interfaces.Builder;

public class RoomReservationBuilder implements Builder<RoomReservation> {

	
	private Room room = null;
	private long startTime  = 0; 
	private long endTime = 0;
	private long entryID = 0;
	
	public RoomReservationBuilder(){
		
	}

	public void setEntryID(long entryID) {
		this.entryID = entryID;
	}

	public void setRoom(Room room) {
		this.room = room;
	}


	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}


	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	
	@Override
	public RoomReservation build() {
		return new RoomReservation(room, startTime, endTime, entryID);
	}
}
