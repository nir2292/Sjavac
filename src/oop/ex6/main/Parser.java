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
	final String varLineRegex = "([a-zA-Z]+)\\s+(" + varValuesRegex + ",)*(" + varValuesRegex + ")?\\s*;";
	final String methodValuesRegex = "([a-zA-Z]+)\\s+([\\p{Punct}\\w]+)";
	final String methodStartRegex = "void\\s+([\\w]+)\\s*\\(\\s*(("+ methodValuesRegex +"\\s*,\\s*)*\\s*(" + methodValuesRegex + ")?)\\s*\\)\\s*\\{";
	final String startScopeRegex = "(if|while)\\s+\\(([TODO])\\)\\s*\\{";
	final String endScopeRegex = "\\}";
	static final String COMMENT_PREFIX = "//";
	static final String EMPTY_LINE = "[\\s]*";
	
	static final String LEGAL_CHARS = "\\!#\\$\\%\\&\\(\\)\\*\\+\\-\\.\\/\\:\\;\\<\\=\\>\\?@\\[\\]\\^\\_\\`{\\|}\\~";
	BufferedReader buffer;
	private ArrayList<Scope> methods;
	private ArrayList<Variable> globalVars;
	
	public Parser(File path) throws IOException, badFileFormatException {
		this.buffer =  new BufferedReader(new FileReader(path));
		this.methods = new ArrayList<>();
		this.globalVars = new ArrayList<>();
		setMethods();
		initializeVars(this.methods);
	}
	

	private void setMethods() throws IOException, badFileFormatException {
		String line = buffer.readLine();
		Pattern p;
		Matcher m;
		while(line != null) {
			if (checkToIgnore(line)) {
				line = buffer.readLine();
				continue;
			} else if  (Pattern.matches(methodStartRegex, line)) {
				p = Pattern.compile(methodStartRegex);
				m = p.matcher(line);
				m.matches();
				String MethodName = m.group(1);
				String methodParameters = m.group(2);
				Scope method = getChunk();
				method.addAllVars(handleVar(methodParameters));
				methods.add(method);
				line = buffer.readLine();
			} else if(Pattern.matches(varLineRegex, line)){
				//calls handleVar method to check for variables in this line.
				globalVars.addAll(handleVar(line));
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
	
	
	public Scope getChunk() throws IOException, badFileFormatException {
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
				sc.addAllVars(handleVar(currentLine));
				currentLine = buffer.readLine();
				continue;
			}
			if(Pattern.matches(startScopeRegex, currentLine)){
				sc.addScope(getChunk());
				continue;
			}
			if(Pattern.matches(endScopeRegex, currentLine)){
				return sc;
			}
			throw new illegalLineException("Line does not match format");
		}
		throw new illegalLineException("unexpected EOF");
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
		Pattern p = Pattern.compile(varLineRegex);
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
				vars.add(new Variable(var, m.group(1) , m.group(3)));
			} else {
				vars.add(new Variable(var, m.group(1)));
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