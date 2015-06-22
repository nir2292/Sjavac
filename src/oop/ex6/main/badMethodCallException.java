package oop.ex6.main;

/**
 * Thrown when a call to a method is incorrect.
 *
 */
public class badMethodCallException extends badFileFormatException {
	
	private static final long serialVersionUID = 1L;
	/**
	 * constructor.
	 * @param message
	 */
	public badMethodCallException(String message) {
		super(message);
	}

}
