package oop.ex6.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import oop.ex6.scopes.*;

public class Parser {
	final String varLineRegex = "(int|double|String|char|boolean)\\w+\\s+\\=\\w+";
	final String methodStartRegex = "\\void\\s\\w";
	final String startScopeRegex = "(if|while|void)\\(\\w+\\)";
	final String endScopeRegex = "}";
	
	static final String LEGAL_CHARS = "\\!#\\$\\%\\&\\(\\)\\*\\+\\-\\.\\/\\:\\;\\<\\=\\>\\?@\\[\\]\\^\\_\\`{\\|}\\~";
	BufferedReader buffer;
	private ArrayList<Scope> methods;
	
	public Parser(File path) throws IOException, illegalValueException, noSuchTypeException {
		this.buffer =  new BufferedReader(new FileReader(path));
		this.methods = new ArrayList<>();
		setMethods();
	}
	
	private Scope getChunk(Stack<String> parenthesesBalance) throws illegalValueException, noSuchTypeException{
		Scope sc = new Scope();
		String currentLine = new String();
		while(!Pattern.matches(endScopeRegex, currentLine) && !parenthesesBalance.isEmpty()){
			if(Pattern.matches(varLineRegex, currentLine)){
				Variable var = VariableFactory.createVariable(currentLine);
				sc.addVar(var);
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