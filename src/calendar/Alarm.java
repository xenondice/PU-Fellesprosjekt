package calendar;

public class Alarm {
	
	private final long alarmTime;
	private final String username;
	private final long entry_id;
	
	public Alarm(long time, String username, long entry_id){
		this.alarmTime = time;
		this.username = username;
		this.entry_id = entry_id;
	}
	
	public long getAlarmTime() {
		return alarmTime;
	}
	public long getEntry_id() {
		return entry_id;
	}
	public String getUsername() {
		return username;
	}

}
