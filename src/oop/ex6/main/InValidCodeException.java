package oop.ex6.main;


/**
 * Super-class for all invalid-code type exceptions.
 */
public class InValidCodeException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * constructor.
	 * @param message
	 */
	public InValidCodeException(String message){
		super(message);
	}
}
