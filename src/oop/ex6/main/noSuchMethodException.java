package oop.ex6.main;

/**
 * Thrown when a call to a method that doesn't exist is performed.
 */
public class noSuchMethodException extends badFileFormatException {

	private static final long serialVersionUID = 1L;

	/**
	 * constructor.
	 * @param message
	 */
	public noSuchMethodException(String message) {
		super(message);
	}

}
