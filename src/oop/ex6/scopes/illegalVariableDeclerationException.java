package oop.ex6.scopes;

import oop.ex6.main.badFileFormatException;

public class illegalVariableDeclerationException extends badFileFormatException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public illegalVariableDeclerationException(String message) {
		super(message);
	}

}
