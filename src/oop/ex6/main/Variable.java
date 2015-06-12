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
}
