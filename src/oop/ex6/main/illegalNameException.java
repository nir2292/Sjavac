package oop.ex6.main;

/**
 * Thrown when a variable's name is not allowed.
 */
public class illegalNameException extends badFileFormatException {
	private static final long serialVersionUID = 1L;

	public illegalNameException(String message) {
		super(message);
	}

}
