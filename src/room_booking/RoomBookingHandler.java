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
	 * @param room
	 * @param startTime
	 * @param endTime
	 * @param entryID
	 * @return true if the Room is successfully booked.
	 * @throws RoomAlreadyBookedException
	 */
	public boolean bookRoom(Room room, long startTime, long endTime, long entryID) throws RoomAlreadyBookedException{
		if(checkIfFree(room, startTime, endTime)){
			RoomReservation rr = new RoomReservation(room, startTime, endTime, entryID);
			return dbm.addRoomReservation(rr);
		}else{
			throw new RoomAlreadyBookedException("Room is not available at this time.");
		}
	}
	
	/**
	 * Checks whether two timespans overlap.</br>
	 * 
	 * @param start1
	 * @param start2
	 * @param end1
	 * @param end2
	 * @return true if the input timespan overlaps with the other timespan.
	 */
	private boolean isInbetween(long start1, long start2, long end1, long end2){
	
		if (start2 >= start1 && end1 <= end2){
			return true;
		}else if (start2 >= start1 && end1 >= end2){
			return true;
		}else if (start2 <= start1 && end1 <= end2){
			return true;
		}
		return false;
	}
	
	// sjekke om det finnes RoomReservation rr;
	/**
	 * checks if there already is a RoomReservation at the given Room at the given timespan 
	 * @param room
	 * @param startTime
	 * @param endTime
	 * @return true if the Room is available
	 */
	public boolean checkIfFree(Room room, long startTime, long endTime){
		HashSet<RoomReservation> rr = dbm.getReservationsForRoom(room);
		for(RoomReservation res : rr){
			return isInbetween(startTime, res.getStartTime(),  endTime, res.getEndTime());
		}
		return true;
	}
	
	/**
	 * cancels a RoomReservation
	 * @param room
	 * @param startTime
	 * @param endTime
	 * @return true if the Room is successfully released.
	 */
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
