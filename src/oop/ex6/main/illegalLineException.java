package oop.ex6.main;

/**
 * Thrown when a line doesn't match the format.
 *
 */
public class illegalLineException extends badFileFormatException {
	private static final long serialVersionUID = 1L;
	
	public illegalLineException(String message) {
		super(message);
	}

}
