package calendar;

import interfaces.Builder;

/**
 * This class is here to incrementally build a immutable CalendarEntry. 
 *
 */
public class CalendarEntryBuilder implements Builder<CalendarEntry>{
	private Long entryID = null;
	private Long startTime = null;
	private Long endTime = null;
	private String location = null;
	private String description = null;
	private String roomID = null;
	private String creator = null;
	
	public CalendarEntryBuilder(){
		
	}
	
	public CalendarEntryBuilder(CalendarEntry e){
		this.setDescription(e.getDescription());
		this.setEndTime(e.getEndTime());
		this.setEntryID(e.getEntryID());
		this.setLocation(e.getLocation());
		this.setRoomID(e.getRoomID());
		this.setStartTime(e.getStartTime());
		this.setCreator(e.getCreator());
	}
	
	@Override
	public CalendarEntry build(){
		return new CalendarEntry(entryID, startTime, endTime, location, description, roomID, creator);
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public void setEntryID(Long entryID) {
		this.entryID = entryID;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
}
