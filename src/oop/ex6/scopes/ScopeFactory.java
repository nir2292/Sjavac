package oop.ex6.scopes;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oop.ex6.main.Parser;
import oop.ex6.main.badFileFormatException;

public class ScopeFactory {
	
	final static String methodValuesRegex = "([a-zA-Z]+)\\s+([\\p{Punct}\\w]+)";
	final static String methodHeader = "void\\s+([\\w]+)\\s*\\(\\s*(("+ methodValuesRegex +"\\s*,\\s*)*\\s*(" + methodValuesRegex + ")?)\\s*\\)\\s*\\{";
	final static String ConditionalScopeHeader = "(while|if)\\s*\\((\\s*([\\w]+)\\s*((\\|\\||\\&\\&)\\s*([\\w]+)\\s*)*\\s*)\\)\\s*\\{";
	final static String END_OF_CODE_LINE = ";";
	final static String conditionStatements = "\\|\\||\\&\\&";
	
	
	public static Scope getScope(String header) throws badFileFormatException {
		Pattern p;
		Matcher m;
		if (header == Parser.START_OF_FILE) {
			return new Scope(Parser.START_OF_FILE);
		} else if (Pattern.matches(methodHeader, header)) {
			p = Pattern.compile(methodHeader);
			m = p.matcher(header);
			m.matches();
			Scope scope = new Scope(m.group(1));
			String[] methodParameters = m.group(2).split(",");
			for (String parameter: methodParameters) {
				scope.addAllVars(Parser.handleVar(parameter.trim()));
			}
			return scope;
		} else if (Pattern.matches(ConditionalScopeHeader, header)) {
			p = Pattern.compile(ConditionalScopeHeader);
			m = p.matcher(header);
			m.matches();			
			String parametersLine = m.group(2);
			String[] parameters = null;
			parameters = parametersLine.split(conditionStatements);
			return new ConditionScope(m.group(1),parameters);
		}
		return null;
	}
}
