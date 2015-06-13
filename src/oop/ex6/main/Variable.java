package oop.ex6.main;

public class Variable {
	
	Type var;
	String name;
	String value;
	public Variable(Type var, String name, String value) throws illegalValueException{
		if (var.getMatcher(value).matches()) {
			this.var = var;
			this.name = name;
			this.value = value;
		} else {
			throw new illegalValueException("Bad value for type " + var.toString());
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}