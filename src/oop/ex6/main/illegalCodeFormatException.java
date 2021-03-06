package oop.ex6.main;

/**
 * Thrown when the code is of illegal format.
 */
public class illegalCodeFormatException extends InValidCodeException {
	private static final long serialVersionUID = 1L;
	/**
	 * constructor.
	 * @param message
	 */
	public illegalCodeFormatException(String message){
		super(message);
	}
}
