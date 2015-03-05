package calendar;

import interfaces.Builder;

public class InvitationBuilder implements Builder<Invitation> {
	
	private boolean isGoing;
	private boolean isShowing;
	private String username;
	private long entry_id;
	
	public InvitationBuilder() {
	}
	
	public InvitationBuilder(Invitation inv) {
		this.isGoing = inv.isGoing();
		this.isShowing = inv.isShowing();
		this.username = inv.getUsername();
		this.entry_id = inv.getEntry_id();
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
	
	public void setEntry_id(long entry_id) {
		this.entry_id = entry_id;
	}
	public void setGoing(boolean isGoing) {
		this.isGoing = isGoing;
	}
	public void setShowing(boolean isShowing) {
		this.isShowing = isShowing;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	

	@Override
	public Invitation build() {
		// TODO Auto-generated method stub
		return null;
	}

}
