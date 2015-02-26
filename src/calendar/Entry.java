package calendar;

public class Entry {
	
	private final  String eventID; 
	private final  String startTime; 
	private final  String endTime; 
	private final  String location; 
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
	
}
