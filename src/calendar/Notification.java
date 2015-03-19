package calendar;

import java.util.Date;

/**
 * A Notification appears if a user is added or removed from a group, 
 * a calendar entry the user is attending is removed or edited, 
 * or if another user declines an invitation to a calendar entry the (first) user have made.
 *
 */
public class Notification {
	private final long notificationID;
	private final String description;
	private final boolean isOpened;
	private final long time;
	private final String username;
	
	/**
	 * The Notification constructor
	 * @param id
	 * @param description
	 * @param isOpened
	 * @param time
	 * @param username
	 */
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
		StringBuilder builder = new StringBuilder();
		builder.append("Notification [notificationID=");
		builder.append(notificationID);
		builder.append(", description=");
		builder.append(description);
		builder.append(", isOpened=");
		builder.append(isOpened);
		builder.append(", time=");
		builder.append(time);
		builder.append(", username=");
		builder.append(username);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (notificationID ^ (notificationID >>> 32));
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
		if (notificationID != other.notificationID) {
			return false;
		}
		return true;
	}
}
