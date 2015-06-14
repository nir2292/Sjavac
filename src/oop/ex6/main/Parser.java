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
	final String varValuesRegex = "\\s*(\\w+)\\s*(\\=\\s*(\\w+)\\s*)?";
	final String varDeclerationRegex = "\\s*([a-zA-Z]+)\\s+(" + varValuesRegex + ",)*(" + varValuesRegex + ")?\\s*";
	final String varLineRegex = varDeclerationRegex + END_OF_CODE_LINE;
	final String methodValuesRegex = "([a-zA-Z]+)\\s+([\\p{Punct}\\w]+)";
	final String methodStartRegex = "void\\s+([\\w]+)\\s*\\(\\s*(("+ methodValuesRegex +"\\s*,\\s*)*\\s*(" + methodValuesRegex + ")?)\\s*\\)\\s*\\{";
	final String startScopeRegex = "(while|if)\\s*\\(\\s*([\\w]+)\\s*((\\|\\||\\&\\&)\\s*([\\w]+)\\s*)*\\s*\\)s*\\{";
	final String endScopeRegex = "\\}";
	static final String COMMENT_PREFIX = "//";
	static final String END_OF_CODE_LINE = ";";
	static final String EMPTY_LINE = "[\\s]*";
	
	static final String LEGAL_CHARS = "\\!#\\$\\%\\&\\(\\)\\*\\+\\-\\.\\/\\:\\;\\<\\=\\>\\?@\\[\\]\\^\\_\\`{\\|}\\~";
	BufferedReader buffer;
	private ArrayList<Scope> methods;
	private ArrayList<Variable> globalVars;
	
	public Parser(File path) throws IOException, badFileFormatException {
		this.buffer =  new BufferedReader(new FileReader(path));
		this.methods = new ArrayList<>();
		this.globalVars = new ArrayList<>();
	}
	
	public ArrayList<Scope> ParseFile() throws IOException, badFileFormatException{
		setMethods();
		initializeVars(this.methods);
		return this.methods;
	}

	private void setMethods() throws IOException, badFileFormatException {
		String line = buffer.readLine();
		Pattern p;
		Matcher m;
		while(line != null) {
			line = line.trim();
			if (checkToIgnore(line)) {
				line = buffer.readLine();
				continue;
			} else if  (Pattern.matches(methodStartRegex, line)) {
				p = Pattern.compile(methodStartRegex);
				m = p.matcher(line);
				m.matches();
				String MethodName = m.group(1);
				String[] methodParameters = m.group(2).split(",");
				Scope method = parseScope();
				for (String parameter: methodParameters) {
					method.addAllVars(handleVar(parameter.trim()));
				}
				methods.add(method);
				line = buffer.readLine();
			} else if(Pattern.matches(varLineRegex, line)){
				//calls handleVar method to check for variables in this line.
				globalVars.addAll(handleVar(line.substring(0, line.lastIndexOf(END_OF_CODE_LINE))));
				line = buffer.readLine();
			} else {
				throw new illegalLineException("Line does not match format");
			}
		}
		for (Scope method: methods) {
			method.addAllVars(globalVars);
		}
	}
	
	private void initializeVars(ArrayList<Scope> methods){
		for (Scope method: methods) {
			method.addAllVars(globalVars);
		}
	}
	
	
	public Scope parseScope() throws IOException, badFileFormatException {
		Scope sc = new Scope();
		String currentLine = buffer.readLine();
		
		while(currentLine != null){
			currentLine = currentLine.trim();
			//checks if a line should be ignores (comment, empty line, etc)
			if (checkToIgnore(currentLine)) {
				currentLine = buffer.readLine();
				continue;
			}
			if(Pattern.matches(varLineRegex, currentLine)){
				//calls handleVar method to check for variables in this line.
				sc.addAllVars(handleVar(currentLine.substring(0, currentLine.lastIndexOf(END_OF_CODE_LINE))));
				currentLine = buffer.readLine();
				continue;
			}
			if(Pattern.matches(startScopeRegex, currentLine)){
				ConditionScope condScope = (ConditionScope) parseScope();
				condScope.addContitions(handleConditionScope(currentLine.substring(currentLine.indexOf("("), currentLine.length())));
				sc.addScope(condScope);
				currentLine = buffer.readLine();
				continue;
			}
			if(Pattern.matches(endScopeRegex, currentLine)){
				return sc;
			}
			throw new illegalLineException("Line does not match format");
		}
		throw new illegalLineException("unexpected EOF");
	}
	
	private String[] handleConditionScope(String subString) throws badConditionFormat, noSuchVariable {
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
	
	//TODO!! fix method so it works on method-parameters.
	private ArrayList<Variable> handleVar(String currentLine) throws badFileFormatException {
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
}
//
////some int member
//final int a = 5;
//
////Another int member
//final int b = a;
//
////now - a string member
//String s;
//
//void boo(int a, int b, String s) {
//if (true) {
//	boo(1,2,"hello");
//	while (false) {
//		soo(true);
//	}
//}
//return;
//}
//
//void soo(boolean b) {
//return;
//}
