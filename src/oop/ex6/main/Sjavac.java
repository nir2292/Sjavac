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
			Validator v = new Validator(parser.ParseFile());
			parser.ParseFile();
			v.isValid();
			
		} catch (IOException | badFileFormatException e1) {
			System.out.println("ERROR");
			e1.printStackTrace();
		}
		
	}
}
