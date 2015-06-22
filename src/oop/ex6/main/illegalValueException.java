package oop.ex6.main;

/**
 * Thrown when a value is given to a variable not of it's type.
 */
public class illegalValueException extends badFileFormatException {
	private static final long serialVersionUID = 1L;
	
	public illegalValueException(String message){
		super(message);
	}

}
