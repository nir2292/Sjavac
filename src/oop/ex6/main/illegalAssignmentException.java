package oop.ex6.main;

/**
 * Thrown when an illegal variable assignment is made.
 */
public class illegalAssignmentException extends illegalCodeFormatException {
	
	/**
	 * constructor.
	 * @param message
	 */
	public illegalAssignmentException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
