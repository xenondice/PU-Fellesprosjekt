package calendar;

public class Notification {
	private final long notificationID;
	private final String description;
	private final boolean isOpened;
	private final long time;
	private final String username;
	private final int entry_id;
	
	public Notification(long id, String description, boolean isOpened, long time, String username, int entry_id) {
		this.notificationID = id;
		this.description = description;
		this.isOpened = isOpened;
		this.time = time;
		this.username = username;
		this.entry_id = entry_id;
				
	}
	public long getNotificationID() {
		return notificationID;
	}
	public String getDescription() {
		return description;
	}
	public int getEntry_id() {
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
}
