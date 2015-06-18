package oop.ex6.main;

import java.io.File;
import java.io.IOException;

import oop.ex6.scopes.ConditionScope;
import oop.ex6.scopes.MethodScope;
import oop.ex6.scopes.Scope;

public class Sjavac {
	
	public static void printScopeTree(Scope mainScope) {
		System.out.println(mainScope);
		for (MethodScope interScope: mainScope.getInternalMethods()) {
			printScopeTree(interScope);
		}
		for (ConditionScope interScope: mainScope.getInternalConditionScopes()) {
			printScopeTree(interScope);
		}
	}
	
	public static void main(String[] args) {
		
		File sjavac = new File(args[0]);
		try {
			Parser parser = new Parser(sjavac);
			Scope a = parser.parseFile();
			Validator v = new Validator(a);
//			System.out.println("Before:");
//			System.out.println("Global vars: ");
//			for(Variable var:Scope.globalVariables)
//				System.out.println(var);
//			printScopeTree(a);
			v.isValid();
//			System.out.println("After:");
//			System.out.println("Global vars: ");
//			for(Variable var:Scope.globalVariables)
//				System.out.println(var);
//			printScopeTree(a);
		} catch (badFileFormatException e2){
			System.out.println("1");
		} catch (IOException e1){
			System.out.println("2");
		}
		System.out.println("0");
	}
}
