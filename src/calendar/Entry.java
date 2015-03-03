package calendar;

import java.util.Date;

/**
 * Represents an Entry in a Calendar
 * The class is immutable. That means no instance of this class can change any of its attributes.
 *
 */
public class Entry {
	
	private final  int entryID;
	private final  long startTime; 
	private final  long endTime; 
	private final  String location; 
	private final  String description; 
	private final  boolean isActive; 
	private final  String roomID; 
	
	public Entry(int entryID, long startTime, long endTime, String location,
			String description,	boolean isActive, String roomID){
		this.entryID = entryID; 
		this.startTime = startTime; 
		this.endTime = endTime; 
		this.location = location; 
		this.description = description; 
		this.isActive = isActive; 
		this.roomID = roomID; 
	}
	
	/**
	 * @return a (immutable) clone of this instance
	 */
	public Entry clone(){
		return new Entry(entryID, startTime, endTime, location, description, isActive, roomID);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Entry (id: ");
		sb.append(entryID); sb.append(")\n");
		sb.append("start: ");
		sb.append(new Date(startTime)); sb.append("\n");
		sb.append("end: ");
		sb.append(new Date(endTime)); sb.append("\n");
		sb.append("location: ");
		sb.append(location); sb.append("\n");
		sb.append("description: ");
		sb.append(description); sb.append("\n");
		sb.append("isActive: ");
		sb.append(isActive); sb.append("\n");
		sb.append("roomID: ");
		sb.append(roomID); sb.append("\n");
		return sb.toString();
	}
	
	public String getDescription() {
		return description;
	}
	public long getEndTime() {
		return endTime;
	}
	public int getEventID() {
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
	public boolean isActive(){
		return isActive;
	}	
}