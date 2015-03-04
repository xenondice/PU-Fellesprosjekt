package calendar;

import interfaces.Builder;

public class NotificationBuilder implements Builder<Notification> {
	
	private String description = null;
	private boolean isOpened = false;
	private long time = -1;
	private String username = null;
	private int entry_id = -1;
	
	public NotificationBuilder() {
		
	}
	
	/**
	 * copies the given notification into the builder.</br>
	 * great to use when a immutable notification has to be changed. 
	 * -> just create a new instance with the wanted values</br>
	 * @param n
	 */
	public NotificationBuilder(Notification n) {
		setDescription(n.getDescription());
		setOpened(n.isOpened());
		setTime(n.getTime());
		setUsername(n.getUsername());
		setEntry_id(n.getEntry_id());
	}

	@Override
	public Notification build() {
		return new Notification(description, isOpened, time, username, entry_id);
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
	
	
	public void setDescription(String description) {
		this.description = description;
	}
	public void setEntry_id(int entry_id) {
		this.entry_id = entry_id;
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
