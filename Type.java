package Sjavac.oop.ex6.main;

public enum Type {
	INT ("(\\d)+"),
	DOUBLE ("(\\d)+(.\\d+)?"),
	STRING ("(\\w)+"),
	BOOLEAN ("(true|false)"),
	CHAR ("\\w");
	
	private String regex;
	
	Type(String expression){
		this.regex = expression;
	}
	
}
