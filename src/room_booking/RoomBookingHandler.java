package room_booking;

public class RoomBookingHandler {

	public static void bookRoom(Room room){
	//TODO
	}
	
	private boolean isInbetween(long start1, long end1, long start2, long end2){
	
		if (start2 >= start1 && end1 <= end2){
			return true;
		}else if (start2 >= start1 && end1 >= end2){
			return true;
		}else if (start2 <= start1 && end1 <= end2){
			return true;
		}
		return false;
	}
	
	public static boolean checkIfFree(Room room, long startTime, long endTime){
		
		//TODO
		return true;
	}
	
	public static void releaseRoom(Room room, long startTime, long endTime){
		//TODO
	}
}
