package oop.ex6.main;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import oop.ex6.scopes.Scope;


public class Sjavac {
	
	
	public static void printScopeTree() {
		
	}
	
	public static void main(String[] args) {
		
		File sjavac = new File(args[0]);
		try {
			Parser parser = new Parser(sjavac);
			Scope a = parser.parseFile();
			
			//Validator v = new Validator(a);
			//v.isValid();
			
		} catch (IOException | badFileFormatException e1) {
			System.out.println("ERROR");
			e1.printStackTrace();
		}
		
	}
}
