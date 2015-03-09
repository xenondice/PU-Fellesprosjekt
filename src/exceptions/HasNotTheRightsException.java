package exceptions;

public class HasNotTheRightsException extends Exception {
	
	public HasNotTheRightsException() {}

	public HasNotTheRightsException(String string) {
		super(string);
	}
	
	public HasNotTheRightsException(String username, long entry_id) {
		super(	"'"+username+"' is not a admin of '"+entry_id+"'!");
	}

}
