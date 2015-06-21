package oop.ex6.scopes;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oop.ex6.main.InValidCodeException;
import oop.ex6.main.Parser;
import oop.ex6.main.Variable;
import oop.ex6.main.badFileFormatException;

public class ScopeFactory {
	
	final static String METHOD_VALUE_REGEX = "([a-zA-Z]+)\\s+([\\p{Punct}\\w]+)";
	final static String METHOD_HEADER = "void\\s+([\\w]+)\\s*\\(\\s*(("+ METHOD_VALUE_REGEX +"\\s*,\\s*)*\\s*(" + METHOD_VALUE_REGEX + ")?)\\s*\\)\\s*\\{";
	final static String CONDITION_SCOPE_HEADER = "(while|if)\\s*\\((\\s*([\\w]+)\\s*((\\|\\||\\&\\&)\\s*([\\w]+)\\s*)*\\s*)\\)\\s*\\{";
	final static String END_OF_CODE_LINE = ";";
	final static String CONDITION_STATEMENT = "\\s*(\\|\\||\\s*\\&\\&)\\s*";
	final static String PARAMETER_SEPERATOR = ",";
	
	/**
	 * Factory method for construction of Scope objects.
	 * @param header of the current scope.
	 * @return - new scope object.
	 * @throws badFileFormatException in case of file io error.
	 */
	public static Scope getScope(String header) throws InValidCodeException {
		Pattern p;
		Matcher m;
		if (header == Parser.START_OF_FILE) {
			return new Scope(Parser.START_OF_FILE);
		} else if (Pattern.matches(METHOD_HEADER, header)) {
			p = Pattern.compile(METHOD_HEADER);
			m = p.matcher(header);
			m.matches();
			MethodScope scope = new MethodScope(m.group(1));
			String[] methodParameters = m.group(2).split(PARAMETER_SEPERATOR);
			for (String parameter: methodParameters) {
				ArrayList<Variable> parameters = Parser.handleVar(parameter.trim(), false, scope);
				for(Variable var:parameters)
					var.setValue(Variable.isParameter);
				scope.addAllVars(parameters);
				scope.addToParameters(parameters);
			}
			return scope;
		} else if (Pattern.matches(CONDITION_SCOPE_HEADER, header)) {
			p = Pattern.compile(CONDITION_SCOPE_HEADER);
			m = p.matcher(header);
			m.matches();
			String parametersLine = m.group(2);
			String[] parameters = null;
			parameters = parametersLine.split(CONDITION_STATEMENT);
			return new ConditionScope(m.group(1),parameters);
		}
		return null;
	}
}
