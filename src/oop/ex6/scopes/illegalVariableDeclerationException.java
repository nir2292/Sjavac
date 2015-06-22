package oop.ex6.scopes;

import oop.ex6.main.badFileFormatException;

/**
 * Thrown when an illegal variable declaration is performed.
 */
public class illegalVariableDeclerationException extends badFileFormatException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * constructor. 
	 * @param message
	 */
	public illegalVariableDeclerationException(String message) {
		super(message);
	}

}
