
package calendar;

import java.util.Date;
/**
 * Represents an CalendarEntry in a Calendar
 * The class is immutable. That means no instance of this class can change any of its attributes.
 *
*/
public class CalendarEntry {
	
	private final  long entryID;
	private final  long startTime; 
	private final  long endTime; 
	private final  String location; 
	private final  String description; 
	private final  String roomID; 
	private final  String creator; // username of the creator of the entry
	
	public CalendarEntry(long entryID, long startTime, long endTime, String location,
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
		StringBuilder builder = new StringBuilder();
		builder.append("CalendarEntry [entryID=");
		builder.append(entryID);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", location=");
		builder.append(location);
		builder.append(", description=");
		builder.append(description);
		builder.append(", roomID=");
		builder.append(roomID);
		builder.append(", creator=");
		builder.append(creator);
		builder.append("]");
		return builder.toString();
	}
	
	public String getDescription() {
		return description;
	}
	public long getEndTime() {
		return endTime;
	}
	public long getEntryID() {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (int) (endTime ^ (endTime >>> 32));
		result = prime * result + (int) (entryID ^ (entryID >>> 32));
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((roomID == null) ? 0 : roomID.hashCode());
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CalendarEntry)) {
			return false;
		}
		CalendarEntry other = (CalendarEntry) obj;
		if (creator == null) {
			if (other.creator != null) {
				return false;
			}
		} else if (!creator.equals(other.creator)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (endTime != other.endTime) {
			return false;
		}
		if (entryID != other.entryID) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (roomID == null) {
			if (other.roomID != null) {
				return false;
			}
		} else if (!roomID.equals(other.roomID)) {
			return false;
		}
		if (startTime != other.startTime) {
			return false;
		}
		return true;
	}

	
}