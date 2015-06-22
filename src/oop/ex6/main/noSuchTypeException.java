package oop.ex6.main;

/**
 * Thrown when a variable of a type that doesn't exist is declared.
 */
public class noSuchTypeException extends badFileFormatException {
	private static final long serialVersionUID = 1L;
	
	public noSuchTypeException(String message) {
		super(message);
	}
}
