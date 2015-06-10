package Sjavac.oop.ex6.main;


import java.util.ArrayList;
import java.util.Scanner;


public class Sjavac {
	
	
	
	public static void main(String[] args){
		
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
	}
}
