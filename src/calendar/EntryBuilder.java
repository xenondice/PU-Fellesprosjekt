package calendar;

import java.util.Date;

import interfaces.Builder;

/**
 * This class is here to incrementally build a immutable Entry. 
 *
 */
public class EntryBuilder implements Builder<Entry>{
	private String eventID = null;
	private Date startTime = null;
	private Date endTime = null;
	private String location = null;
	private String description = null;
	private Boolean isActive = null;
	private String roomID = null;
	
	public EntryBuilder(){
		
	}
	
	@Override
	public Entry build(){
		return new Entry(eventID, startTime, endTime, location, description, isActive, roomID);
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public void setEventID(String eventID) {
		this.eventID = eventID;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
}
