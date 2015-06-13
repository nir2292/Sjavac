package oop.ex6.main;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;


public class Sjavac {
	
	
	
	public static void main(String[] args) {
		
		File sjavac = new File(args[0]);
		try {
			Parser parser = new Parser(sjavac);
			parser.getChunk();
		} catch (IOException | badFileFormatException e1) {
			System.out.println("ERROR");
			e1.printStackTrace();
		}
		
		
		
		ArrayList<Type> globalVariables = new ArrayList<>();
		Scanner userInputScanner = new Scanner(System.in);
		String input;
		Type var = null;
		while (true) {
			System.out.println("enter a variable");
			input = userInputScanner.nextLine();
			try {
				var = Type.valueOf(input);
			} catch (IllegalArgumentException e) {
				System.out.println("no such variable");
				break;
			}
			globalVariables.add(var);
		}
		for (Type variable : globalVariables){
			System.out.println(variable);
		}
		userInputScanner.close();
	}
}
