package oop.ex6.main;

public enum Type {
	INT ("(\\d)+"),
	DOUBLE ("(\\d)+(.)?(\\d+)*"),
	STRING ("(\\w)*"),
	BOOLEAN ("(true|false)|\\d"),
	CHAR ("\\w");
	
	private String regex;
	
	Type(String expression){
		this.regex = expression;
	}
	
	String getRegex(){
		return this.regex;
	}
}
