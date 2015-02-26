package calendar;

import interfaces.Builder;

public class EntryBuilder implements Builder<Entry>{
	private String eventID = null;
	private String startTime = null;
	private String endTime = null;
	private String location = null;
	private String description = null;
	private Boolean isActive = null;
	private String roomID = null;
	
	public EntryBuilder(){
		
	}
	
	@Override
	public Entry build(){
		// @TODO
		return null;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	public void setEndTime(String endTime) {
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
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
}
