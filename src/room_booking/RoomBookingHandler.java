package room_booking;

import java.util.HashSet;

import server_client.RequestHandler;
import dbms.DataBaseManager;
import exceptions.EntryDoesNotExistException;
import exceptions.RoomAlreadyBookedException;
import exceptions.RoomDoesNotExistException;

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
	 * @throws RoomDoesNotExistException 
	 */
	public boolean bookRoom(String room_id, long startTime, long endTime, long entryID) throws RoomAlreadyBookedException, RoomDoesNotExistException{
		if(checkIfFree(room_id, startTime, endTime)){
			RoomReservation rr = new RoomReservation(room_id, startTime, endTime, entryID);
			return dbm.addRoomReservation(rr);
		}else{
			throw new RoomAlreadyBookedException("Room is not available at this time.");
		}
	}
	
	/**
	 * checks whether the number is in between the two numbers (equlity results in false).</br>
	 * note that inBetween(10, 10, 20) returns false.
	 * @param number
	 * @param lowerBound
	 * @param upperBound
	 * @return
	 */
	public boolean isBetween(long number, long lowerBound, long upperBound){
		if(lowerBound > upperBound){return isBetween(number, upperBound, lowerBound);}
		return number > lowerBound && number < upperBound;
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
	public boolean doOverlap(long start1, long end1, long start2, long end2){

		return isBetween(start1, start2, end2)	|| isBetween(end1, start2, end2) 
				||isBetween(start2, start1, end1) || isBetween(end2, start1, end1) 
				|| start1 == start2 || end1 == end2;
	}
	
	// sjekke om det finnes RoomReservation rr;
	/**
	 * checks if there already is a RoomReservation at the given Room at the given timespan 
	 * @param room
	 * @param startTime
	 * @param endTime
	 * @return true if the Room is available
	 */
	public boolean checkIfFree(String room_id, long startTime, long endTime){
		HashSet<RoomReservation> reservations = dbm.getReservationsForRoom(room_id);
		if(reservations == null){return true;}
		for(RoomReservation res : reservations){
			if( doOverlap(startTime, endTime, res.getStartTime(), res.getEndTime())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * releases all reservations connected to the given entry id
	 * @param room
	 * @param entry_id
	 * @return
	 */
	public boolean releaseRoomEntry(String room_id, long entry_id){
		boolean could_release_all = true;
		HashSet<RoomReservation> reservations = dbm.getReservationsForRoom(room_id);
		if(reservations != null){
			for(RoomReservation res : reservations){
				if(res.getEntryID() == entry_id){
					if (!dbm.deleteRoomReservation(res)){
						could_release_all = false;
					}else{
						try {
							RequestHandler.notify(dbm.getEntry(entry_id).getCreator(), "The reservation '"+res.toString()+"' was released.");
						} catch (EntryDoesNotExistException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return could_release_all;
	}
	
	public static void main(String[] args) {
		// Tests
		RoomBookingHandler rbh = new RoomBookingHandler(new DataBaseManager());
		long t1 = 100;
		long t2 = 200;
		long t3 = 300;
		long t4 = 400;
		long t5 = 500;
		
		System.out.println(rbh.isBetween(t1, t2, t3)); // false
		System.out.println(rbh.isBetween(t4, t2, t3)); // false
		System.out.println(rbh.isBetween(t1, t3, t2)); // false
		System.out.println(rbh.isBetween(t1, t1, t3)); // false
		System.out.println(rbh.isBetween(t3, t2, t3)); // false
		System.out.println(rbh.isBetween(t3, t2, t4)); // true
		System.out.println(rbh.isBetween(t3, t4, t2)); // true
		
		System.out.println("----------------");
		
		System.out.println(rbh.doOverlap(t1, t2, t3, t4)); // false
		System.out.println(rbh.doOverlap(t3, t4, t1, t2)); // false
		System.out.println(rbh.doOverlap(t1, t3, t2, t4)); // true
		System.out.println(rbh.doOverlap(t1, t4, t2, t3)); // true
		System.out.println(rbh.doOverlap(t3, t5, t2, t4)); // true
		System.out.println();
		System.out.println(rbh.doOverlap(t3, t4, t1, t2)); // false
		System.out.println(rbh.doOverlap(t1, t2, t3, t4)); // false
		System.out.println(rbh.doOverlap(t2, t4, t1, t3)); // true
		System.out.println(rbh.doOverlap(t2, t3, t1, t4)); // true
		System.out.println(rbh.doOverlap(t2, t4, t3, t5)); // true
		
		System.out.println(rbh.doOverlap(t1, t2, t1, t2)); // true
		
		
	}
}
