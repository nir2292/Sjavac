package oop.ex6.scopes;

import oop.ex6.main.badFileFormatException;

/**
 * Thrown when an if/while condition isn't in the right format. (initialized and/or boolean)
 */
public class badConditionFormat extends badFileFormatException {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * constructor.
	 * @param message
	 */
	public badConditionFormat(String message) {
		super(message);
	}
}
