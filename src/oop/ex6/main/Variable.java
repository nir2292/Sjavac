package oop.ex6.main;

import java.util.regex.Pattern;

public class Variable {
	
	final static String FINAL = "final";
	final static String varNameRegex = "(^[\\D_]\\w+)|[a-zA-Z]";
	
	Type var;
	String name;
	String value;
	final boolean globaFlag;
	final boolean finalFlag;
	
	public Variable(Type var, String name, String value, String finalFlag, boolean globalFlag) throws illegalValueException, illegalNameException{
		if(!Pattern.matches(varNameRegex, name)){
			throw new illegalNameException("Bad name " + name);
		}
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
		this.globaFlag = globalFlag;
		if (finalFlag==null) {
			this.finalFlag = false;
		} else {
			this.finalFlag = finalFlag.equals(FINAL);
		}
	}
	
	public Variable(Type var, String name, String finalFlag, boolean globalFlag) throws illegalValueException, illegalNameException {
		if (name == null || !Pattern.matches(varNameRegex, name)) {
			throw new illegalNameException("Bad name " + name);
		}
		this.var = var;
		this.name = name;
		this.globaFlag = globalFlag;
		if (finalFlag==null) {
			this.finalFlag = false;
		} else {
			this.finalFlag = finalFlag.equals(FINAL);
		}
	}
	
	@Override
	public String toString() {
		String finalToType = "";
		if (this.finalFlag) {
			finalToType = FINAL + ",";
		}
		return name + "(" + finalToType + var + "," + value + ")";
	}
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String getType(){
		return var.name();
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

	public boolean isGlobal() {
		return this.globaFlag;
	}
}