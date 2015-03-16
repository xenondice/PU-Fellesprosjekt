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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (entry_id ^ (entry_id >>> 32));
		result = prime * result + (isGoing ? 1231 : 1237);
		result = prime * result + (isShowing ? 1231 : 1237);
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
		if (!(obj instanceof Invitation)) {
			return false;
		}
		Invitation other = (Invitation) obj;
		if (entry_id != other.entry_id) {
			return false;
		}
		if (isGoing != other.isGoing) {
			return false;
		}
		if (isShowing != other.isShowing) {
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
	
	@Override
	public String toString() {
		System.out.println("-->"+isGoing);
		String str = "Invitation to entry " + entry_id + ", current answer: " + (isGoing?"Attending":"Not attending");
		return str;
	}
}
