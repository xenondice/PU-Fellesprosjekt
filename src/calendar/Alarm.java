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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (alarmTime ^ (alarmTime >>> 32));
		result = prime * result + (int) (entry_id ^ (entry_id >>> 32));
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
		if (!(obj instanceof Alarm)) {
			return false;
		}
		Alarm other = (Alarm) obj;
		if (alarmTime != other.alarmTime) {
			return false;
		}
		if (entry_id != other.entry_id) {
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
