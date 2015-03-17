package calendar;

import java.util.Date;

public class Notification {
	private final long notificationID;
	private final String description;
	private final boolean isOpened;
	private final long time;
	private final String username;
	
	public Notification(long id, String description, boolean isOpened, long time, String username) {
		this.notificationID = id;
		this.description = description;
		this.isOpened = isOpened;
		if(time <= 0){time = System.currentTimeMillis();}
		this.time = time;
		this.username = username;
	}
	public long getNotificationID() {
		return notificationID;
	}
	public String getDescription() {
		return description;
	}
	public long getTime() {
		return time;
	}
	public String getUsername() {
		return username;
	}
	public boolean isOpened(){
		return isOpened;
	}

	
	@Override
	public String toString() {
		String str = "(" + new Date(time) + ") ID =" + notificationID +" message: "+ description;
		
		if (!isOpened) str += " *";
		
		return str;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + (isOpened ? 1231 : 1237);
		result = prime * result
				+ (int) (notificationID ^ (notificationID >>> 32));
		result = prime * result + (int) (time ^ (time >>> 32));
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		if (!(obj instanceof Notification)) {
			return false;
		}
		Notification other = (Notification) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (isOpened != other.isOpened) {
			return false;
		}
		if (notificationID != other.notificationID) {
			return false;
		}
		if (time != other.time) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}
}
