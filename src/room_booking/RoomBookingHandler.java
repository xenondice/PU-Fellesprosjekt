package room_booking;

import java.util.HashSet;
import java.util.Random;

import server_client.RequestHandler;
import dbms.DataBaseManager;
import exceptions.EntryDoesNotExistException;
import exceptions.RoomAlreadyBookedException;
import exceptions.RoomDoesNotExistException;
import exceptions.StartTimeIsLaterTanEndTimeException;

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
	 * @throws StartTimeIsLaterTanEndTimeException 
	 */
	public boolean bookRoom(String room_id, long startTime, long endTime, long entryID) throws RoomAlreadyBookedException, RoomDoesNotExistException, StartTimeIsLaterTanEndTimeException{
			if(checkIfFree(room_id, startTime, endTime)){
				RoomReservation rr = new RoomReservation(room_id, startTime, endTime, entryID);
				return dbm.addRoomReservation(rr);
			}else{
				throw new RoomAlreadyBookedException("Room is not available at this time.");
			}
		
	}
	
	/**
	 * Checks whether two timespans overlap.</br>
	 * 
	 * @param start1 must be smaller than end1
	 * @param start2 must be smaller than end2
	 * @param end1
	 * @param end2
	 * @return true if the input timespan overlaps with the other timespan.
	 * @throws StartTimeIsLaterTanEndTimeException 
	 */
	public boolean doOverlap(long start1, long end1, long start2, long end2) throws StartTimeIsLaterTanEndTimeException{
		if(start1 > end1 || start2 > end2){throw new StartTimeIsLaterTanEndTimeException();}
		return ! (end1 <= start2 || end2 <= start1);
	}
	
	// sjekke om det finnes RoomReservation rr;
	/**
	 * checks if there already is a RoomReservation at the given Room at the given timespan 
	 * @param room
	 * @param startTime
	 * @param endTime
	 * @return true if the Room is available
	 * @throws StartTimeIsLaterTanEndTimeException 
	 */
	public boolean checkIfFree(String room_id, long startTime, long endTime) throws StartTimeIsLaterTanEndTimeException{
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
		
		System.out.println("----------------");
		
		
		
		
		try {
			System.out.println(rbh.doOverlap(t1, t2, t3, t4));
		
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
		Random r = new Random();
			for (int i = 0; i < 100; i++) {
				try {
					System.out.println(i
							+ ") "
							+ rbh.doOverlap(r.nextLong() % 200000,
									r.nextLong() % 200000,
									r.nextLong() % 200000,
									r.nextLong() % 200000));

				} catch (StartTimeIsLaterTanEndTimeException e) {
					System.out.println("time wrong");
				}
			}
		} catch (StartTimeIsLaterTanEndTimeException e) {
			e.printStackTrace();
		}
	}
}
