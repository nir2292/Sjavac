package oop.ex6.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
	
	static final String LEGAL_CHARS = "\\!#\\$\\%\\&\\(\\)\\*\\+\\-\\.\\/\\:\\;\\<\\=\\>\\?@\\[\\]\\^\\_\\`{\\|}\\~";
	static final String COMMENT_PREFIX = "\\\\";
	BufferedReader buffer;
	
	public Parser(File path) throws IOException {
		buffer =  new BufferedReader(new FileReader(path));
	}
	
	
	private void getChunk() throws IOException{
		//int chr = buffer.read();
		String line = buffer.readLine();
		
		while (line != null) {
			if (line.startsWith(COMMENT_PREFIX)) {
				line = buffer.readLine();
			}
			
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
//	if (true) {
//		boo(1,2,"hello");
//		while (false) {
//			soo(true);
//		}
//	}
//	return;
//}
//
//void soo(boolean b) {
//	return;
//}

