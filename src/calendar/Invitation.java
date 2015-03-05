package calendar;

public class Invitation {
	
	private final boolean isGoing;
	private final boolean isShowing;
	private final String username;
	private final long entry_id;
	
	public Invitation(boolean going, boolean showing, String username, long entry_id) {
		this.isGoing = going;
		this.isShowing = showing;
		this.username = username;
		this.entry_id = entry_id;
	}
	
	public long getEntry_id() {
		return entry_id;
	}
	public String getUsername() {
		return username;
	}
	public boolean isGoing() {
		return isGoing;
	}
	public boolean isShowing() {
		return isShowing;
	}
	

}
