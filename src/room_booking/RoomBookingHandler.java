package room_booking;

import dbms.DataBaseManager;

public class RoomBookingHandler {

	private static DataBaseManager dbm = new DataBaseManager();
	
	public static void bookRoom(Room room, long startTime, long endTime){
	//TODO  make RoomIsBookedException?
		if(checkIfFree(room, startTime, endTime)){
			RoomReservation rr = new RoomReservation(room, startTime, endTime);
		}else{
			throw new IllegalArgumentException("Room is not available");
		}
	}
	
	private boolean isInbetween(long checkStart, long checkEnd, long inputStart, long inputEnd){
	
		if (inputStart >= checkStart && checkEnd <= inputEnd){
			return true;
		}else if (inputStart >= checkStart && checkEnd >= inputEnd){
			return true;
		}else if (inputStart <= checkStart && checkEnd <= inputEnd){
			return true;
		}
		return false;
	}
	
	public static boolean checkIfFree(Room room, long startTime, long endTime){
		// sjekke om det finnes RoomReservation rr;
		
		if(isInBetween( startTime,rr.getStartTime,  endTime, rr.getEndTime())){
			
		}
		
		return true;
	}
	
	public static void releaseRoom(Room room, long startTime, long endTime){
		//TODO
	}
}
