package calendar;

/**
 * Represents an Entry in a Calendar
 * The class is immutable. That means no instance of this class can change any of its attributes.
 *
 */
public class Entry {
	
	private final  String eventID; 
	private final  String startTime; 
	private final  String endTime; // TODO change to Timestamp
	private final  String location; // TODO change to Timestamp
	private final  String description; 
	private final  boolean isActive; 
	private final  String roomID; 
	
	public Entry(String eventID, String startTime, String endTime, String location,
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
	
	public String getDescription() {
		return description;
	}
	public String getEndTime() {
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
	public String getStartTime() {
		return startTime;
	}
	public boolean isActive(){
		return isActive;
	}
	
	
}
