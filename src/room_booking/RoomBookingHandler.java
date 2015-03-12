package room_booking;

import java.util.HashSet;
import dbms.DataBaseManager;

public class RoomBookingHandler {

	private static DataBaseManager dbm = new DataBaseManager();
	
	public void bookRoom(Room room, long startTime, long endTime, long entryID){
	//TODO  make RoomIsAllreadyBookedException?
		if(checkIfFree(room, startTime, endTime)){
			RoomReservation rr = new RoomReservation(room, startTime, endTime, entryID);
			dbm.addRoomReservation(rr);
		}else{
			throw new IllegalArgumentException("Room is not available");
		}
	}
	
	private boolean isInbetween(long checkStart, long inputStart, long checkEnd, long inputEnd){
	
		if (inputStart >= checkStart && checkEnd <= inputEnd){
			return true;
		}else if (inputStart >= checkStart && checkEnd >= inputEnd){
			return true;
		}else if (inputStart <= checkStart && checkEnd <= inputEnd){
			return true;
		}
		return false;
	}
	
	public boolean checkIfFree(Room room, long startTime, long endTime){
		// sjekke om det finnes RoomReservation rr;
		
		HashSet<RoomReservation> rr = dbm.getReservationsForRoom(room);
		for(RoomReservation res : rr){
			return isInbetween(startTime, res.getStartTime(),  endTime, res.getEndTime());
		}
		return true;
	}
	
	
	public void releaseRoom(Room room, long startTime, long endTime){
		//TODO
		if (dbm.getReservationsForRoom(room) != null){
			HashSet<RoomReservation> rr = dbm.getReservationsForRoom(room);
			for(RoomReservation res : rr){
				if ( isInbetween(startTime, res.getStartTime(),  endTime, res.getEndTime())){
					dbm.deleteRoomReservation(res);
				}
			}
		}
		//TODO
		// throw an exception if the room doesn't have any reservation  ?
	}
}
