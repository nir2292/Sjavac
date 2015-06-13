package oop.ex6.main;


public class VariableFactory{
	
	/**
	 * Constructs a new filter according to the given string.
	 * @param filterString the filter to create.
	 * @return the filter requested.
	 * @throws illegalValueException 
	 * @throws noSuchTypeException 
	 * @throws BadFilterException
	 */
	public static Variable createVariable(String varLine) throws illegalValueException, noSuchTypeException{
		String[] splittedString = varLine.split("\\s+");
		String varType = splittedString[0];
		String varName = splittedString[1];
		String varValue = splittedString[3];
		if(varType.equals("int"))
			return new Variable(Type.INT, varName, varValue);
		if(varType.equals("double"))
			return new Variable(Type.DOUBLE, varName, varValue);
		if(varType.equals("boolean"))
			return new Variable(Type.BOOLEAN, varName, varValue);
		if(varType.equals("String"))
			return new Variable(Type.STRING, varName, varValue);
		if(varType.equals("char"))
			return new Variable(Type.CHAR, varName, varValue);
		throw new noSuchTypeException("No such type");
	}
}
