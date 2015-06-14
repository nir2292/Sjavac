package oop.ex6.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oop.ex6.scopes.*;

public class Parser {
	static final String END_OF_CODE_LINE = ";";
	public static final String START_OF_FILE = "START";
	final static String varValuesRegex = "\\s*(\\w+)\\s*(\\=\\s*(\\w+)\\s*)?";
	final static String varDeclerationRegex = "\\s*([a-zA-Z]+)\\s+(" + varValuesRegex + ",)*(" + varValuesRegex + ")?\\s*";
	final static String varLineRegex = varDeclerationRegex + END_OF_CODE_LINE;
	final static String HEADER = "[\\w\\s]+\\([\\w\\s\\,]*\\)\\s*\\{";
	final static String methodValuesRegex = "([a-zA-Z]+)\\s+([\\p{Punct}\\w]+)";
	final static String methodHeader = "void\\s+([\\w]+)\\s*\\(\\s*(("+ methodValuesRegex +"\\s*,\\s*)*\\s*(" + methodValuesRegex + ")?)\\s*\\)\\s*\\{";
	final static String ConditionalScopeHeader = "(while|if)\\s*\\(\\s*([\\w]+)\\s*((\\|\\||\\&\\&)\\s*([\\w]+)\\s*)*\\s*\\)\\s*\\{";
	final static String endScopeRegex = "\\}";
	static final String COMMENT_PREFIX = "//";
	static final String EMPTY_LINE = "[\\s]*";
	
	static final String LEGAL_CHARS = "\\!#\\$\\%\\&\\(\\)\\*\\+\\-\\.\\/\\:\\;\\<\\=\\>\\?@\\[\\]\\^\\_\\`{\\|}\\~";
	BufferedReader buffer;
	private Scope mainScope;
	
	public Parser(File path) throws IOException, badFileFormatException {
		this.buffer =  new BufferedReader(new FileReader(path));
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
		String currentLine = buffer.readLine().trim();
		while(currentLine != null){
			//checks if a line should be ignores (comment, empty line, etc)
			if (checkToIgnore(currentLine)) {
				currentLine = buffer.readLine().trim();
				continue;
			} else if (Pattern.matches(HEADER, currentLine)) {
				sc.addScope(parseScope(currentLine));
				currentLine = buffer.readLine().trim();
				continue;
			} else if (Pattern.matches(varLineRegex, currentLine)) {
				//calls handleVar method to check for variables in this line.
				sc.addAllVars(handleVar(currentLine.substring(0, currentLine.lastIndexOf(END_OF_CODE_LINE))));
				currentLine = buffer.readLine().trim();
				continue;
			} else if(Pattern.matches(endScopeRegex, currentLine)){
				return sc;
			} else {
			throw new illegalLineException("Line does not match format");
			}
		}
		throw new illegalLineException("unexpected EOF");
	}
	
	public static ArrayList<Variable> handleVar(String currentLine) throws badFileFormatException {
		ArrayList<Variable> vars = new ArrayList<>();
		Pattern p = Pattern.compile(varDeclerationRegex);
		Matcher m = p.matcher(currentLine);
		m.matches();
		String varType = m.group(1);
		Type var;
		m.reset(currentLine.substring(m.start(1) + varType.length()));
		m.usePattern(Pattern.compile(varValuesRegex));
		while (m.find()) {
			try {
				var = Type.valueOf(varType.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new noSuchTypeException("illegal value :" + varType);
			}
			if (m.group(3) != null) {
				vars.add(new Variable(var, m.group(1) , m.group(3), false));
			} else {
				vars.add(new Variable(var, m.group(1), false));
			}
		}
		return vars;	
	}
	
	public static String[] handleConditionScope(String subString) throws badConditionFormat, noSuchVariable {
		subString = subString.substring(1, subString.lastIndexOf(")"));
		return subString.split("\\|\\||\\&\\&");
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