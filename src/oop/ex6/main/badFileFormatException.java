package oop.ex6.main;

/**
 * Thrown when the file has a bad format.
 *
 */
public class badFileFormatException extends InValidCodeException {

	
	private static final long serialVersionUID = 1L;
	/**
	 * constructor.
	 * @param message
	 */
	public badFileFormatException(String message){
		super(message);
	}
}
