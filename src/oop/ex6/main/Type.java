package oop.ex6.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Type {
	INT ("(\\d)+"),
	DOUBLE ("(\\d+)+(.)?(\\d+)*"),
	STRING ("\"[\\w"+ Parser.LEGAL_CHARS +"]+\""),
	BOOLEAN ("(true|false|((\\d)+(.)?(\\d+)*))"),
	CHAR ("\'[\\w"+ Parser.LEGAL_CHARS +"]\'");
	
	private Pattern p;
	
	Type(String format){
		p = Pattern.compile(format);
	}
	
	public Matcher getMatcher(String values) {
		return p.matcher(values); 
	}
}
