package oop.ex6.main;

public class noSuchTypeException extends badFileFormatException {
	private static final long serialVersionUID = 1L;
	
	public noSuchTypeException(String message) {
		super(message);
	}
}