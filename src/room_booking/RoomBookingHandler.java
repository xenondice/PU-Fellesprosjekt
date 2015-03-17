package room_booking;

import java.util.HashSet;

import server_client.RequestHandler;
import dbms.DataBaseManager;
import exceptions.EntryDoesNotExistException;
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
				||isBetween(start2, start1, end1) || isBetween(end2, start1, end1) ;
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
		HashSet<RoomReservation> reservations = dbm.getReservationsForRoom(room);
		for(RoomReservation res : reservations){
			if( doOverlap(startTime, endTime, res.getStartTime(), res.getEndTime())){
				return false;
			}
		}
		return true;
	}
	
	/**
<<<<<<< HEAD
	 * deletes all reservations that overlap with the given timespan
	 * @param room
	 * @param startTime
	 * @param endTime
	 * @return
=======
	 * cancels a RoomReservation
	 * @param room
	 * @param startTime
	 * @param endTime
	 * @return true if the Room is successfully released.
>>>>>>> adc89ab30bf70a52acb7192161f91b2582a24da2
	 */
	public boolean releaseRoom(Room room, long startTime, long endTime){
		boolean could_release_all = true;
		if (dbm.getReservationsForRoom(room) != null){
			HashSet<RoomReservation> reservations = dbm.getReservationsForRoom(room);
			for(RoomReservation res : reservations){
				if ( doOverlap(startTime, endTime, res.getStartTime(), res.getEndTime())){
					if (!dbm.deleteRoomReservation(res)){
						could_release_all = false;
					}else{
						try {
							RequestHandler.notify(dbm.getEntry(res.getEntryID()).getCreator(), "The reservation '"+res.toString()+"' was released.");
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
		
		
	}
}
