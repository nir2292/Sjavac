package oop.ex6.main;

import java.io.File;
import java.io.IOException;
import oop.ex6.scopes.Scope;

public class Sjavac {
	
	public static void main(String[] args) {
		File sjavac = new File(args[0]);
		try {
			Parser parser = new Parser(sjavac);
			Scope a = parser.parseFile();
			Validator v = new Validator(a);
			if(!v.isValid())
				throw new badFileFormatException("");
			Scope.resetGlobalVariables();
			System.out.println("0");

		} catch (badFileFormatException e2){
			Scope.resetGlobalVariables();
			System.out.println("1");
			e2.printStackTrace();
		} catch (IOException e1){
			Scope.resetGlobalVariables();
			System.out.println("2");
		}
	}
}
