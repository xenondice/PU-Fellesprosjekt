package calendar;

import interfaces.Builder;

public class NotificationBuilder implements Builder<Notification> {
	private long notifiationID;
	private String description = null;
	private boolean isOpened = false;
	private long time = -1;
	private String username = null;
	private long entry_id = -1;
	
	public NotificationBuilder() {
		
	}
	
	/**
	 * copies the given notification into the builder.</br>
	 * great to use when a immutable notification has to be changed. 
	 * -> just create a new instance with the wanted values</br>
	 * @param n
	 */
	public NotificationBuilder(Notification n) {
		setNotifiationID(n.getNotificationID());
		setDescription(n.getDescription());
		setOpened(n.isOpened());
		setTime(n.getTime());
		setUsername(n.getUsername());
		setEntry_id(n.getEntry_id());
	}

	@Override
	public Notification build() {
		return new Notification(notifiationID, description, isOpened, time, username, entry_id);
	}
	
	public long getNotifiationID() {
		return notifiationID;
	}
	public String getDescription() {
		return description;
	}
	public long getEntry_id() {
		return entry_id;
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
	
	public void setNotifiationID(long notifiationID) {
		this.notifiationID = notifiationID;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setEntry_id(long l) {
		this.entry_id = l;
	}
	public void setOpened(boolean isOpened) {
		this.isOpened = isOpened;
	}

	public void setTime(long time) {
		this.time = time;
	}
	public void setUsername(String username) {
		this.username = username;
	}

}
