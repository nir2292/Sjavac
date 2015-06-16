package oop.ex6.main;

import java.io.File;
import java.io.IOException;
import oop.ex6.scopes.Scope;

public class Sjavac {
	
	public static void printScopeTree(Scope mainScope) {
		System.out.println(mainScope);
		for (Scope interScope: mainScope.getInternalScopes()) {
			printScopeTree(interScope);
		}
	}
	
	public static void main(String[] args) {
		
		File sjavac = new File(args[0]);
		try {
			Parser parser = new Parser(sjavac);
			Scope a = parser.parseFile();
			printScopeTree(a);
			Validator v = new Validator(a);
			v.isValid();
			
		} catch (IOException | badFileFormatException e1) {
			System.out.println("ERROR");
			e1.printStackTrace();
		}
		
	}
}
