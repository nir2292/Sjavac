package oop.ex6.scopes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oop.ex6.main.Parser;
import oop.ex6.main.badFileFormatException;
import oop.ex6.main.noSuchVariable;

public class ScopeFactory {
	
	final static String methodValuesRegex = "([a-zA-Z]+)\\s+([\\p{Punct}\\w]+)";
	final static String methodHeader = "void\\s+([\\w]+)\\s*\\(\\s*(("+ methodValuesRegex +"\\s*,\\s*)*\\s*(" + methodValuesRegex + ")?)\\s*\\)\\s*\\{";
	final static String ConditionalScopeHeader = "(while|if)\\s*\\(\\s*([\\w]+)\\s*((\\|\\||\\&\\&)\\s*([\\w]+)\\s*)*\\s*\\)\\s*\\{";
	final static String END_OF_CODE_LINE = ";";
	
	public static Scope getScope(String header) throws badFileFormatException {
		Pattern p;
		Matcher m;
		if (header == Parser.START_OF_FILE) {
			return new Scope();
		} else if (Pattern.matches(methodHeader, header)) {
			//Scope scope = new Scope(Parser.handleVar(header.substring(0, header.lastIndexOf("{"))));
			Scope scope = new Scope();
			p = Pattern.compile(methodHeader);
			m = p.matcher(header);
			m.matches();
			//not used. ???
			//String MethodName = m.group(1);
			String[] methodParameters = m.group(2).split(",");
			for (String parameter: methodParameters) {
				scope.addAllVars(Parser.handleVar(parameter.trim()));
			}
			return scope;
		} else if (Pattern.matches(ConditionalScopeHeader, header)) {
			ConditionScope condScope = new ConditionScope();
			condScope.addContitions(Parser.handleConditionScope(header.substring(header.indexOf("("), header.length())));
			return condScope;
		}
		return null;
	}
}
