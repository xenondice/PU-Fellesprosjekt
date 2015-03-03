package calendar;

import interfaces.Builder;

/**
 * This class is here to incrementally build a immutable Entry. 
 *
 */
public class EntryBuilder implements Builder<Entry>{
	private int entryID = 0;
	private long startTime = 0;
	private long endTime = 0;
	private String location = null;
	private String description = null;
	private Boolean isActive = true;
	private String roomID = null;
	
	public EntryBuilder(){
		
	}
	
	public EntryBuilder(Entry e){
		this.setDescription(e.getDescription());
		this.setEndTime(e.getEndTime());
		this.setEventID(e.getEventID());
		this.setIsActive(e.isActive());
		this.setLocation(e.getLocation());
		this.setRoomID(e.getRoomID());
		this.setStartTime(e.getStartTime());
	}
	
	@Override
	public Entry build(){
		return new Entry(entryID, startTime, endTime, location, description, isActive, roomID);
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public void setEventID(int entryID) {
		this.entryID = entryID;
	}
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}
