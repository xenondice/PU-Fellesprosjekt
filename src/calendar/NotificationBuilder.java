package calendar;

import interfaces.Builder;

public class NotificationBuilder implements Builder<Notification> {
	private long notifiationID;
	private String description = null;
	private boolean isOpened = false;
	private long time = System.currentTimeMillis();
	private String username = null;
	
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
		if(time <= 0){time = System.currentTimeMillis();}
		setUsername(n.getUsername());
	}

	@Override
	public Notification build() {
		return new Notification(notifiationID, description, isOpened, time, username);
	}
	
	public long getNotifiationID() {
		return notifiationID;
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
	
	public void setNotifiationID(long notifiationID) {
		this.notifiationID = notifiationID;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setOpened(boolean isOpened) {
		this.isOpened = isOpened;
	}

	public void setTime(long time) {
		if(time <= 0){
			time = System.currentTimeMillis();
		}else{
			this.time = time;
		}
		
	}
	public void setUsername(String username) {
		this.username = username;
	}

}
