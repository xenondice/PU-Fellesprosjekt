package calendar;

import java.util.Date;

/**
 * Represents an Entry in a Calendar
 * The class is immutable. That means no instance of this class can change any of its attributes.
 *
 */
public class Entry {
	
	private final  String eventID;
	private final  Date startTime; 
	private final  Date endTime; 
	private final  String location; 
	private final  String description; 
	private final  boolean isActive; 
	private final  String roomID; 
	
	public Entry(String eventID, Date startTime, Date endTime, String location,
			String description,	boolean isActive, String roomID){
		this.eventID =  eventID; 
		this.startTime =  startTime; 
		this.endTime =  endTime; 
		this.location =  location; 
		this.description =  description; 
		this.isActive = isActive; 
		this.roomID =  roomID; 
	}
	
	/**
	 * @return a (immutable) clone of this instance
	 */
	public Entry clone(){
		return new Entry(eventID, startTime, endTime, location, description, isActive, roomID);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Entry (id: ");
		sb.append(eventID); sb.append(")\n");
		sb.append("start: ");
		sb.append(startTime); sb.append("\n");
		sb.append("end: ");
		sb.append(endTime); sb.append("\n");
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
	public Date getEndTime() {
		return endTime;
	}
	public String getEventID() {
		return eventID;
	}
	public String getLocation() {
		return location;
	}
	public String getRoomID() {
		return roomID;
	}
	public Date getStartTime() {
		return startTime;
	}
	public boolean isActive(){
		return isActive;
	}
	
	
}
