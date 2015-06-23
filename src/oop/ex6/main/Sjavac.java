package oop.ex6.main;

import java.io.File;
import java.io.IOException;
import oop.ex6.scopes.Scope;

public class Sjavac {
	
	/**
	 * checks if a sjava file is valid.
	 * returns 0 if file is valid,
	 * 1 if file is invalid,
	 * 2 if there was a problem with file reading.
	 * calls Parser object for prsing and validating syntax.
	 * calls Validator object for validating legal code.
	 * @param args - path of file to validate.
	 */
	public static void main(String[] args) {
		File sjavac = new File(args[0]);
		try {
			Parser parser = new Parser(sjavac);
			Scope a = parser.parseFile();
			Validator v = new Validator(a);
			if(!v.isValid())
				throw new illegalCodeFormatException("");
			Scope.resetGlobalVariables();
			System.out.println("0");
		} catch (InValidCodeException e2){
			Scope.resetGlobalVariables();
			System.out.println("1");
			System.out.println(e2.getMessage());
		} catch (IOException e1){
			Scope.resetGlobalVariables();
			System.out.println("2");
			System.out.println(e1.getMessage());
		}
	}
}
