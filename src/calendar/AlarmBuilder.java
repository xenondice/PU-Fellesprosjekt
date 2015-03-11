package calendar;

import interfaces.Builder;

public class AlarmBuilder implements Builder<Alarm> {
	private long alarmTime = 0;
	private String username = null;
	private long entry_id = -1;
	
	public AlarmBuilder(Alarm a){
		this.alarmTime = a.getAlarmTime();
		this.username = a.getUsername();
		this.entry_id = a.getEntry_id();
	}
	
	public AlarmBuilder(){
		
	}
	
	public void setAlarmTime(long alarmTime) {
		this.alarmTime = alarmTime;
	}
	public void setEntry_id(long entry_id) {
		this.entry_id = entry_id;
	}
	public void setUsername(String username) {
		this.username = username;
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
	
	
	
	public Alarm build(){
		return new Alarm(alarmTime, username, entry_id);
	}
}
