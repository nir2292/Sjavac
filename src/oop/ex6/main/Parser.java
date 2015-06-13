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
	final String varLineRegex = "(int|double|String|char|boolean)(\\s+\\w+\\s+\\=\\w+\\,?)+";
	final String methodStartRegex = "\\void\\s\\w";
	final String startScopeRegex = "(if|while|void)\\(\\w+\\)";
	final String endScopeRegex = "}";
	static final String COMMENT_PREFIX = "\\\\";

	final String varValuesRegex = "(\\s*(\\w+)\\s*\\=\\s*(\\w+)\\s*\\,?)";
	
	static final String LEGAL_CHARS = "\\!#\\$\\%\\&\\(\\)\\*\\+\\-\\.\\/\\:\\;\\<\\=\\>\\?@\\[\\]\\^\\_\\`{\\|}\\~";
	BufferedReader buffer;
	private ArrayList<Scope> methods;
	
	public Parser(File path) throws IOException, illegalValueException, noSuchTypeException {
		this.buffer =  new BufferedReader(new FileReader(path));
		this.methods = new ArrayList<>();
		setMethods();
	}
	
	public Scope getChunk(Stack<String> parenthesesBalance) throws illegalValueException, noSuchTypeException, IOException{
		Scope sc = new Scope();
		String currentLine = buffer.readLine();
		
		while(!Pattern.matches(endScopeRegex, currentLine)){ // && !parenthesesBalance.isEmpty()
			currentLine = currentLine.trim();
			if (currentLine.startsWith(COMMENT_PREFIX)) {
				currentLine = buffer.readLine();
				continue;
			}
			if(Pattern.matches(varLineRegex, currentLine)){
				Pattern p = Pattern.compile(varLineRegex);
				Matcher m = p.matcher(currentLine);
				m.matches();
				String varType = m.group(1);
				Type var;
				m.usePattern(Pattern.compile(varValuesRegex));
				m.reset();
				while (m.find()) {
					var = Type.valueOf(varType.toUpperCase());
					sc.addVar(new Variable(var, m.group(2) , m.group(3)));
				}
				
				currentLine = buffer.readLine();
				continue;
			}
			if(Pattern.matches(startScopeRegex, currentLine)){
				//TO-DO
				//
			}
			if(Pattern.matches(endScopeRegex, currentLine)){
				//TO-DO
			}
		}
		return sc;
	}
	
	private void setMethods() throws IOException, illegalValueException, noSuchTypeException{
		String line = buffer.readLine();
		while(line != null){
			if(Pattern.matches(methodStartRegex, line))
				methods.add(getChunk(new Stack<String>()));
		}
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
