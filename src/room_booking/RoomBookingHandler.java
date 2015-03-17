package room_booking;

import java.util.HashSet;

import dbms.DataBaseManager;
import exceptions.RoomAlreadyBookedException;

public class RoomBookingHandler {

	
	/**
	 * This class is here to handle the Room Bookings
	 *
	 */
	
	private DataBaseManager dbm;
	
	public RoomBookingHandler(DataBaseManager dbm){
		this.dbm = dbm;
	}


	/**
	 * This method books rooms. Input arguments are the Room, starttime (long), endtime (long) and entryID
	 * of the entry you want the room booked for. 
	 *
	 */

	public boolean bookRoom(Room room, long startTime, long endTime, long entryID) throws RoomAlreadyBookedException{
		if(checkIfFree(room, startTime, endTime)){
			RoomReservation rr = new RoomReservation(room, startTime, endTime, entryID);
			return dbm.addRoomReservation(rr);
		}else{
			throw new IllegalArgumentException("Room is not available");
		}
	}
	
	/**
	 * Checks whether the given timespan is at the same time as another timespan.  
	 *
	 */
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
	
	// sjekke om det finnes RoomReservation rr;
	public boolean checkIfFree(Room room, long startTime, long endTime){
		HashSet<RoomReservation> rr = dbm.getReservationsForRoom(room);
		for(RoomReservation res : rr){
			return isInbetween(startTime, res.getStartTime(),  endTime, res.getEndTime());
		}
		return true;
	}
	
	
	public boolean releaseRoom(Room room, long startTime, long endTime){
		boolean could_release_all = true;
		if (dbm.getReservationsForRoom(room) != null){
			HashSet<RoomReservation> rr = dbm.getReservationsForRoom(room);
			for(RoomReservation res : rr){
				if ( isInbetween(startTime, res.getStartTime(),  endTime, res.getEndTime())){
					if (!dbm.deleteRoomReservation(res))
						could_release_all = false;
				}
			}
		}
		return could_release_all;
	}
}
