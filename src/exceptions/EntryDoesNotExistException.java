package exceptions;

public class EntryDoesNotExistException extends Exception {

	public EntryDoesNotExistException() {
	}

	public EntryDoesNotExistException(String string) {
		super(string);
	}
	
	public EntryDoesNotExistException(long entry_id){
		super("The entryID '"+entry_id+"' does not exist in the DB");
	}
}
