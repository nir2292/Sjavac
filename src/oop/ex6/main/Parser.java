package oop.ex6.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oop.ex6.scopes.*;

public class Parser {
	static final String END_OF_CODE_LINE = ";";
	static final String OPEN_SCOPE_REGEX = "\\s*\\{";
	static final String END_SCOPE_REGEX = "\\s*\\}";
	static final String COMMENT_PREFIX = "//";
	static final String EMPTY_LINE = "[\\s]*";
	static final String FINAL_MODIFIER = "final";
	static final String LEGAL_CHARS = "\\!#\\$\\%\\&\\(\\)\\*\\+\\-\\.\\/\\:\\;\\<\\=\\>\\?@\\[\\]\\^\\_\\`{\\|}\\~";
	static final String VARIABLE_CHANGE_REGEX = "(\\w+)\\s*=\\s*\"*\\'*([\\w.*\\-]+)\\'*\"*\\s*;";
	static final String VARIABLE_VALUE_REGEX = "\\s*(\\w+)\\s*(\\=\\s*([\"\\']*[\\w"+LEGAL_CHARS+"]+[\"\\']*)\\s*)?";
	static final String VARIABLE_MODIFIER = "\\s*(final)*\\s*";
	static final String VARIABLE_DECLERATION = VARIABLE_MODIFIER + "\\s*([a-zA-Z]+)\\s+(" + VARIABLE_VALUE_REGEX + ",)*(" + VARIABLE_VALUE_REGEX + ")?\\s*";
	static final String VARIABLE_LINE_REGEX = VARIABLE_DECLERATION + END_OF_CODE_LINE;
	static final String HEADER = "[\\w\\s]+\\([\\w\\s\\,]*\\)\\s*\\{";
	static final String METHOD_MODIFIER_REGEX = "void\\s+";
	static final String METHOD_NAME = "([a-zA-Z]\\w*)";
	static final String METHOD_VALUE_REGEX = "((\\w+)\\s+(\\w+))";
	static final String METHOD_DECLERATION = METHOD_NAME  + "\\s*\\(\\s*("+ METHOD_VALUE_REGEX +"\\s*,\\s*)*\\s*" + METHOD_VALUE_REGEX + "?\\s*\\)\\s*";
	static final String METHOD_HEADER = METHOD_MODIFIER_REGEX + METHOD_DECLERATION + OPEN_SCOPE_REGEX;
	static final String CALL_METHOD_REGEX = METHOD_NAME  + "\\s*\\(\\s*(['\"]*\\s*(\\w+\\.*\\w*)\\s*['\"]*\\s*,\\s*)*\\s*['\"]*\\s*(\\w+\\.*\\w*)?\\s*['\"]*\\s*\\)\\s*" + END_OF_CODE_LINE;
	static final String CONDITION_SCOPE_HEADER = "(while|if)\\s*\\(\\s*([\\w]+)\\s*((\\|\\||\\&\\&)\\s*([\\w]+)\\s*)*\\s*\\)\\s*\\{";
	static final String RETURN_STATEMENT = "\\s*(return)\\s*" + END_OF_CODE_LINE;
	static final String CLOSE_BRACKET = ")";
	static final String SEPERATOR = ", ";
	static final String ASSIGNMENT_SEPERATOR = "\\s*\\=\\s*";
	/**
	 * global flag for start-of-file.
	 */
	public static final String START_OF_FILE = "START";
	
	
	BufferedReader buffer;
	private Scope mainScope;
	
	/**
	 * Constructor for Parser class.
	 * @param path of sjava file.
	 * @throws IOException - in case of file io error.
	 * @throws badFileFormatException - in case code does not compile because of syntax error.
	 */
	public Parser(File path) throws IOException, badFileFormatException {
		this.buffer =  new BufferedReader(new FileReader(path));
	}
	
	/**
	 * Parses the file and checks if the file is syntax-valid. 
	 * @return the main scope, ie entire code as a Scope objects, which holds the different methods as scopes.
	 * @throws IOException - in case of file io error.
	 * @throws badFileFormatException if file does not have correct syntax.
	 */
	public Scope parseFile() throws IOException, InValidCodeException{
		parseMain();
		return this.mainScope;
	}
	
	/*
	 * main parsing methods.
	 */
	private void parseMain() throws IOException, InValidCodeException {
		mainScope = parseScope(START_OF_FILE);
	}

	private Scope parseScope(String header) throws IOException, InValidCodeException {
		Scope sc = ScopeFactory.getScope(header);
		String currentLine = buffer.readLine();
		while(currentLine != null){
			currentLine = currentLine.trim();
			//checks if a line should be ignores (comment, empty line, etc)
			if (checkToIgnore(currentLine)) {
				currentLine = buffer.readLine();
				continue;
			}
			if (Pattern.matches(METHOD_HEADER, currentLine)) {
				Scope newScope = parseScope(currentLine);
				newScope.addAllVars(sc.getKnownVariables());
				try {
					sc.addMethodScope((MethodScope)newScope);
				}
				catch(ClassCastException e){
					throw new badFileFormatException("Bad method decleration");
				}
				currentLine = buffer.readLine();
				continue;
			}
			if (Pattern.matches(VARIABLE_LINE_REGEX, currentLine)) {
				//calls handleVar method to check for variables in this line.
				ArrayList<Variable> varsToAdd;
				if(sc.getName().equals(START_OF_FILE)){ //add as globals
					varsToAdd = handleVar(currentLine.substring(0, currentLine.lastIndexOf(END_OF_CODE_LINE)), true, sc);
				//don't add as globals
				} else {
					varsToAdd = handleVar(currentLine.substring(0, currentLine.lastIndexOf(END_OF_CODE_LINE)), false, sc);
				}
				sc.addAllVars(varsToAdd);
				sc.addChronologyRun(currentLine.substring(0, currentLine.lastIndexOf(END_OF_CODE_LINE)));
				currentLine = buffer.readLine();
				continue;
			}
			if (!sc.getName().equals(START_OF_FILE)) {
				if(Pattern.matches(CONDITION_SCOPE_HEADER, currentLine)){
					Scope newScope = parseScope(currentLine);
					newScope.addAllVars(sc.getKnownVariables());
					try {
						sc.addConditionScope((ConditionScope)newScope);
						sc.addChronologyRun(currentLine);
					}
					catch(java.lang.ClassCastException e){
						throw new badFileFormatException("Bad " + newScope.getName() + " decleration");
					}
					currentLine = buffer.readLine();
					continue;
				}
				if(Pattern.matches(VARIABLE_CHANGE_REGEX, currentLine)){
					sc.addAssignmentVar(handleAssignmentVar(currentLine.substring(0, currentLine.lastIndexOf(END_OF_CODE_LINE))));
					sc.addChronologyRun(handleAssignmentVar(currentLine.substring(0, currentLine.lastIndexOf(END_OF_CODE_LINE))));
					currentLine = buffer.readLine();
					continue;
				}
				if(Pattern.matches(RETURN_STATEMENT, currentLine)){
					currentLine = buffer.readLine().trim();
					try {
						MethodScope scs = (MethodScope)sc;
						scs.markReturned();
						if (!Pattern.matches(END_SCOPE_REGEX, currentLine)) {
							throw new illegalLineException("Return statement is not at the end of method scope");
						} else {
							return sc;
						}
					} catch (ClassCastException e) {
						continue;
					}
				}
				if(Pattern.matches(CALL_METHOD_REGEX, currentLine)){
					currentLine = currentLine.substring(0, currentLine.lastIndexOf(CLOSE_BRACKET)+1);
					sc.addCalledMethod(currentLine);
					sc.addChronologyRun(currentLine);
					currentLine = buffer.readLine();
					continue;
				}
				if(Pattern.matches(END_SCOPE_REGEX, currentLine)){
					return sc;
				}
			}
			throw new illegalLineException("Line does not match format");
		}
		if (sc.getName() == START_OF_FILE) {
			return sc;
		} else {
			throw new illegalLineException("unexpected EOF");
		}
	}
	
	/*
	 * handles assignments line
	 */
	private String handleAssignmentVar(String currentLine) {
		String[] lineSplit = currentLine.split(ASSIGNMENT_SEPERATOR);
		return lineSplit[0] + SEPERATOR + lineSplit[1];
	}

	/**
	 * Receives a line declaring a variable.
	 * for example: int a = 3 \ int a \ int a,b,c=6,
	 * @param currentLine - line of variables.
	 * @param globalFlag - variable is global.
	 * @param sc - current scope.
	 * @return ArrayList of VAriable type objects representing variables.
	 * @throws badFileFormatException
	 */
	public static ArrayList<Variable> handleVar(String currentLine, boolean globalFlag, Scope sc) throws badFileFormatException {
		ArrayList<Variable> vars = new ArrayList<>();
		if(currentLine.equals(""))
			return vars;
		Pattern p = Pattern.compile(VARIABLE_DECLERATION);
		Matcher m = p.matcher(currentLine);
		m.matches();
		String varModifier = m.group(1), varType = m.group(2);
		Type var;
		m.reset(currentLine.substring(m.start(2) + varType.length()));
		m.usePattern(Pattern.compile(VARIABLE_VALUE_REGEX));
		while (m.find()) {
			String varName = m.group(1);
			String varValue = m.group(3);
			try {
				var = Type.valueOf(varType.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new noSuchTypeException("illegal value :" + varType);
			}
			if (varValue != null) {
				try{
					vars.add(new Variable(var, varName , varValue, varModifier, globalFlag));
				}
				catch(InValidCodeException e){
					vars.add(new Variable(var, varName, varModifier, globalFlag));
					sc.addAssignmentVar(varName + SEPERATOR + varValue);
				}
			} else {
				if(varModifier != null){
					if(varModifier.equals(FINAL_MODIFIER))
						throw new illegalValueException("Final variable has to be initialized with a value");
				}
				vars.add(new Variable(var, m.group(1), varModifier, globalFlag));
			}
		}
		return vars;	
	}
	
	/*
	 * checks if a line in the code should be ignored.
	 */
	private boolean checkToIgnore(String line) {
		if (line.startsWith(COMMENT_PREFIX)) {
			return true;
		}
		if (Pattern.matches(EMPTY_LINE, line)){
			return true;
		}
		return false;
	}
}
