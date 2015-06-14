package oop.ex6.main;

public class Variable {
	
	Type var;
	String name;
	String value;
	final boolean finalFlag;
	
	public Variable(Type var, String name, String value, boolean finalFlag) throws illegalValueException{
		if (name == null || value == null) {
			throw new illegalValueException("Bad value for type " + var.toString());
		}
		if (var.getMatcher(value).matches()) {
			this.value = value;
		} else {
			throw new illegalValueException("Bad value for type " + var.toString());
		}
		this.var = var;
		this.name = name;
		this.finalFlag = finalFlag;
	}
	
	public Variable(Type var, String name, boolean finalFlag) throws illegalValueException {
		if (name == null) {
			throw new illegalValueException("Bad value for type " + var.toString());
		}
		this.var = var;
		this.name = name;
		this.finalFlag = finalFlag;
	}
	
	public String toString() {
		return name + ": (" + var + " , " + value + ")";
	}
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	/**
	 * @param value the value to set
	 * @throws illegalValueException 
	 * @throws illegalAssignmentException 
	 */
	public void setValue(String value) throws badFileFormatException {
		if (!finalFlag) {
			if (var.getMatcher(value).matches()) {
				this.value = value;
			} else {
				throw new illegalValueException("Bad value for type " + var);
			}
		} else {
			throw new illegalAssignmentException("Cannot set value for final variable.");
		}
	}
}