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
	final static String openScopeRegex = "\\s*\\{";
	final static String endScopeRegex = "\\s*\\}";
	static final String COMMENT_PREFIX = "//";
	static final String EMPTY_LINE = "[\\s]*";
	public static final String START_OF_FILE = "START";
	final static String varChangeRegex = "(\\w+)\\s*=\\s*([\\w.*]+)\\s*;";
	final static String varValuesRegex = "\\s*(\\w+)\\s*(\\=\\s*([\\w.\"*]+)\\s*)?";
	final static String varModifierRegex = "\\s*(final)*\\s*";
	final static String varDeclerationRegex = varModifierRegex + "\\s*([a-zA-Z]+)\\s+(" + varValuesRegex + ",)*(" + varValuesRegex + ")?\\s*";
	final static String varLineRegex = varDeclerationRegex + END_OF_CODE_LINE;
	final static String HEADER = "[\\w\\s]+\\([\\w\\s\\,]*\\)\\s*\\{";
	final static String methodDecleration = "([\\w]+)\\s*\\(\\s*((([\\w]+)\\s*\\,\\s*)*([\\w]+)?)*\\s*\\)\\s*";
//	final static String methodValuesRegex = "([a-zA-Z]+)\\s+([\\p{Punct}\\w]+)";
//	final static String methodModifier = "void\\s+";
//	final static String methodDecleration = "([\\w]+)\\s*\\(\\s*(("+ methodValuesRegex +"\\s*,\\s*)*\\s*(" + methodValuesRegex + ")?)\\s*\\)";
//	final static String methodHeader = methodModifier + methodDecleration + openScopeRegex;
//	final static String methodHeader = "void\\s+([\\w]+)\\s*\\(\\s*(("+ methodValuesRegex +"\\s*,\\s*)*\\s*(" + methodValuesRegex + ")?)\\s*\\)\\s*\\{";
	final static String ConditionalScopeHeader = "(while|if)\\s*\\(\\s*([\\w]+)\\s*((\\|\\||\\&\\&)\\s*([\\w]+)\\s*)*\\s*\\)\\s*\\{";
	final static String returnStatement = "\\s*(return)\\s*" + END_OF_CODE_LINE;

	static final String LEGAL_CHARS = "\\!#\\$\\%\\&\\(\\)\\*\\+\\-\\.\\/\\:\\;\\<\\=\\>\\?@\\[\\]\\^\\_\\`{\\|}\\~";
	BufferedReader buffer;
	private ArrayList<Variable> globalVariables;
	private Scope mainScope;
	
	public Parser(File path) throws IOException, badFileFormatException {
		this.buffer =  new BufferedReader(new FileReader(path));
		this.globalVariables = new ArrayList<>();
	}
	
	public Scope parseFile() throws IOException, badFileFormatException{
		parseMain();
		//setMethods();
		//initializeVars(this.methods);
		return this.mainScope;
	}
	
	public void parseMain() throws IOException, badFileFormatException {
		mainScope = parseScope(START_OF_FILE);
		
	}
	
//	private void initializeVars(ArrayList<Scope> methods){
//		for (Scope method: methods) {
//			method.addAllVars(globalVars);
//		}
//	}
	
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
			if (Pattern.matches(HEADER, currentLine)) {
				Scope newScope = parseScope(currentLine);
				newScope.addAllVars(sc.getKnownVariables());
				try{
					sc.addMethodScope((MethodScope)newScope);
				}
				catch(java.lang.ClassCastException e){
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
				} else {//don't add as globals
					varsToAdd = handleVar(currentLine.substring(0, currentLine.lastIndexOf(END_OF_CODE_LINE)), false, sc);
				}
				sc.addAllVars(varsToAdd);
				currentLine = buffer.readLine();
				continue;
			}
			if (!sc.getName().equals(START_OF_FILE)) {
				if(Pattern.matches(ConditionalScopeHeader, currentLine)){
					Scope newScope = parseScope(currentLine);
					newScope.addAllVars(sc.getKnownVariables());
					try{
						sc.addConditionScope((ConditionScope)newScope);
					}
					catch(java.lang.ClassCastException e){
						throw new badFileFormatException("Bad " + newScope.getName() + " decleration");
					}
					currentLine = buffer.readLine();
					continue;
				}
				if(Pattern.matches(varChangeRegex, currentLine)){
					sc.addAssignmentVar(handleAssignmentVar(currentLine));
					currentLine = buffer.readLine();
					continue;
				}
				if(Pattern.matches(returnStatement, currentLine)){
					currentLine = buffer.readLine();
					//TODO
					continue;
				}
				if(Pattern.matches(methodDecleration + END_OF_CODE_LINE, currentLine)){
					currentLine = currentLine.substring(0, currentLine.lastIndexOf(")")+1);
					sc.addCalledMethod(currentLine);
					currentLine = buffer.readLine();
					continue;
				}
				if(Pattern.matches(endScopeRegex, currentLine)){
					return sc;
				}
			}
			throw new illegalLineException("Line does not match format");
		}//while
		if (sc.getName() == START_OF_FILE) {
//			currentLine = buffer.readLine();
//			if (checkToIgnore(currentLine)) {
			return sc;
		} else {
			throw new illegalLineException("unexpected EOF");
		}
	}
	
	private String handleAssignmentVar(String currentLine) {
		Pattern p = Pattern.compile(varChangeRegex);
		Matcher m = p.matcher(currentLine);
		m.matches();
		String varName = m.group(1).trim();
		String varNewValue = m.group(2).trim();
		return varName + ", " + varNewValue;
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
			try {
				var = Type.valueOf(varType.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new noSuchTypeException("illegal value :" + varType);
			}
			if (m.group(3) != null) {
				Variable varOfValue = sc.getVariableByName(m.group(3));
				if(varOfValue != null)
					vars.add(new Variable(var, m.group(1), varOfValue.getValue(), varModifier, globalFlag));
				else
					vars.add(new Variable(var, m.group(1) , m.group(3), varModifier, globalFlag));
			} else {
				vars.add(new Variable(var, m.group(1), varModifier, globalFlag));
			}
		}
		return vars;	
	}
	
	private void validateMethods() {
		return;
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
