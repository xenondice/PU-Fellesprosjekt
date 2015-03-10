package exceptions;

public class WrongPasswordException extends Exception {

	public WrongPasswordException() {
	}

	public WrongPasswordException(String string) {
		super(string);
	}
}
