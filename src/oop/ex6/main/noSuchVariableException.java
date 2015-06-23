package oop.ex6.main;

/**
 * Thrown when a reference to a variable that doens't exist is performed.
 */
public class noSuchVariableException extends badFileFormatException {

	private static final long serialVersionUID = 1L;

	/**
	 * constructor.
	 * @param message
	 */
	public noSuchVariableException(String message) {
		super(message);
	}

}
