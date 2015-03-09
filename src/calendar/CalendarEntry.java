
package calendar;

import java.util.Date;
/**
 * Represents an CalendarEntry in a Calendar
 * The class is immutable. That means no instance of this class can change any of its attributes.
 *
*/
public class CalendarEntry {
	
	private final  int entryID;
	private final  long startTime; 
	private final  long endTime; 
	private final  String location; 
	private final  String description; 
	private final  String roomID; 
	private final  String creator;
	
	public CalendarEntry(int entryID, long startTime, long endTime, String location,
			String description, String roomID, String creator){
		this.entryID = entryID; 
		this.startTime = startTime; 
		this.endTime = endTime; 
		this.location = location; 
		this.description = description; 
		this.roomID = roomID; 
		this.creator = creator;
	}
	
	/**
	 * @return a (immutable) clone of this instance
	 */
	public CalendarEntry clone(){
		return new CalendarEntry(entryID, startTime, endTime, location, description, roomID, creator);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CalendarEntry (id: ");
		sb.append(entryID); sb.append(")\n");
		sb.append("start: ");
		sb.append(new Date(startTime)); sb.append("\n");
		sb.append("end: ");
		sb.append(new Date(endTime)); sb.append("\n");
		sb.append("location: ");
		sb.append(location); sb.append("\n");
		sb.append("description: ");
		sb.append(description); sb.append("\n");
		sb.append("roomID: ");
		sb.append(roomID); sb.append("\n");
		sb.append("creator: ");
		sb.append(creator); sb.append("\n");
		return sb.toString();
	}
	
	public String getDescription() {
		return description;
	}
	public long getEndTime() {
		return endTime;
	}
	public int getEntryID() {
		return entryID;
	}
	public String getLocation() {
		return location;
	}
	public String getRoomID() {
		return roomID;
	}
	public long getStartTime() {
		return startTime;
	}

	public String getCreator(){
		return creator;
	}
}