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
	static final String openScopeRegex = "\\s*\\{";
	static final String endScopeRegex = "\\s*\\}";
	static final String COMMENT_PREFIX = "//";
	static final String EMPTY_LINE = "[\\s]*";
	public static final String START_OF_FILE = "START";
	static final String LEGAL_CHARS = "\\!#\\$\\%\\&\\(\\)\\*\\+\\-\\.\\/\\:\\;\\<\\=\\>\\?@\\[\\]\\^\\_\\`{\\|}\\~";
	static final String varChangeRegex = "(\\w+)\\s*=\\s*\"*\\'*([\\w.*\\-]+)\\'*\"*\\s*;";
	static final String varValuesRegex = "\\s*(\\w+)\\s*(\\=\\s*([\"\\']*[\\w"+LEGAL_CHARS+"]+[\"\\']*)\\s*)?";
	static final String varModifierRegex = "\\s*(final)*\\s*";
	static final String varDeclerationRegex = varModifierRegex + "\\s*([a-zA-Z]+)\\s+(" + varValuesRegex + ",)*(" + varValuesRegex + ")?\\s*";
	static final String varLineRegex = varDeclerationRegex + END_OF_CODE_LINE;
	static final String HEADER = "[\\w\\s]+\\([\\w\\s\\,]*\\)\\s*\\{";
	static final String methodModifier = "void\\s+";
	static final String methodName = "([a-zA-Z]\\w*)";
	static final String methodValuesRegex = "((\\w+)\\s+(\\w+))";
	static final String methodDecleration = methodName  + "\\s*\\(\\s*("+ methodValuesRegex +"\\s*,\\s*)*\\s*" + methodValuesRegex + "?\\s*\\)\\s*";
	static final String methodHeader = methodModifier + methodDecleration + openScopeRegex;
	static final String callMethod = methodName  + "\\s*\\(\\s*(['\"]*\\s*(\\w+\\.*\\w*)\\s*['\"]*\\s*,\\s*)*\\s*['\"]*\\s*(\\w+\\.*\\w*)?\\s*['\"]*\\s*\\)\\s*" + END_OF_CODE_LINE;
	static final String ConditionalScopeHeader = "(while|if)\\s*\\(\\s*([\\w]+)\\s*((\\|\\||\\&\\&)\\s*([\\w]+)\\s*)*\\s*\\)\\s*\\{";
	static final String returnStatement = "\\s*(return)\\s*" + END_OF_CODE_LINE;

	BufferedReader buffer;
	private Scope mainScope;
	
	public Parser(File path) throws IOException, badFileFormatException {
		this.buffer =  new BufferedReader(new FileReader(path));
	}
	
	public Scope parseFile() throws IOException, badFileFormatException{
		parseMain();
		return this.mainScope;
	}
	
	public void parseMain() throws IOException, badFileFormatException {
		mainScope = parseScope(START_OF_FILE);
		
	}

	public Scope parseScope(String header) throws IOException, badFileFormatException {
		Scope sc = ScopeFactory.getScope(header);
		String currentLine = buffer.readLine();
		while(currentLine != null){
			currentLine = currentLine.trim();
			//checks if a line should be ignores (comment, empty line, etc)
			if (checkToIgnore(currentLine)) {
				currentLine = buffer.readLine();
				continue;
			}
			if (Pattern.matches(methodHeader, currentLine)) {
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
			if (Pattern.matches(varLineRegex, currentLine)) {
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
				if(Pattern.matches(ConditionalScopeHeader, currentLine)){
					Scope newScope = parseScope(currentLine);
					newScope.addAllVars(sc.getKnownVariables());
					try{
						sc.addConditionScope((ConditionScope)newScope);
						sc.addChronologyRun(currentLine);
					}
					catch(java.lang.ClassCastException e){
						throw new badFileFormatException("Bad " + newScope.getName() + " decleration");
					}
					currentLine = buffer.readLine();
					continue;
				}
				if(Pattern.matches(varChangeRegex, currentLine)){
					sc.addAssignmentVar(handleAssignmentVar(currentLine.substring(0, currentLine.lastIndexOf(END_OF_CODE_LINE))));
					sc.addChronologyRun(handleAssignmentVar(currentLine.substring(0, currentLine.lastIndexOf(END_OF_CODE_LINE))));
					currentLine = buffer.readLine();
					continue;
				}
				if(Pattern.matches(returnStatement, currentLine)){
					currentLine = buffer.readLine().trim();
					try {
						MethodScope scs = (MethodScope)sc;
						scs.markReturned();
						if (!Pattern.matches(endScopeRegex, currentLine)) {
							throw new illegalLineException("Return statement is not at the end of method scope");
						} else {
							return sc;
						}
					} catch (ClassCastException e) {
						continue;
					}
				}
				if(Pattern.matches(callMethod, currentLine)){
					currentLine = currentLine.substring(0, currentLine.lastIndexOf(")")+1);
					sc.addCalledMethod(currentLine);
					sc.addChronologyRun(currentLine);
					currentLine = buffer.readLine();
					continue;
				}
				if(Pattern.matches(endScopeRegex, currentLine)){
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
	
	private String handleAssignmentVar(String currentLine) {
		String[] lineSplit = currentLine.split("\\s*\\=\\s*");
		return lineSplit[0] + ", " + lineSplit[1];
	}

	/*
	 * Receives a line declaring a variable.
	 * for example: int a = 3 \ int a \ int a,b,c=6
	 */
	public static ArrayList<Variable> handleVar(String currentLine, boolean globalFlag, Scope sc) throws badFileFormatException {
		ArrayList<Variable> vars = new ArrayList<>();
		if(currentLine.equals(""))
			return vars;
		Pattern p = Pattern.compile(varDeclerationRegex);
		Matcher m = p.matcher(currentLine);
		m.matches();
		String varModifier = m.group(1), varType = m.group(2);
		Type var;
		m.reset(currentLine.substring(m.start(2) + varType.length()));
		m.usePattern(Pattern.compile(varValuesRegex));
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
				catch(illegalValueException e){
					vars.add(new Variable(var, varName, varModifier, globalFlag));
					sc.addAssignmentVar(varName + ", " + varValue);
				}
			} else {
				if(varModifier != null){
					if(varModifier.equals("final"))
						throw new illegalValueException("Final variable has to be initialized with a value");
				}
				vars.add(new Variable(var, m.group(1), varModifier, globalFlag));
			}
		}
		return vars;	
	}
	
	private boolean checkToIgnore(String line) {
		if (line.startsWith(COMMENT_PREFIX)){
			return true;
		}
		if (Pattern.matches(EMPTY_LINE, line)){
			return true;
		}
		return false;
	}
}
