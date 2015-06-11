package oop.ex6.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
	
	static final String LEGAL_CHARS = "\\!#\\$\\%\\&\\(\\)\\*\\+\\-\\.\\/\\:\\;\\<\\=\\>\\?@\\[\\]\\^\\_\\`{\\|}\\~";
	BufferedReader buffer;
	
	public Parser(File path) throws IOException {
		buffer =  new BufferedReader(new FileReader(path));
	}
	
	private void getChunk(){
		
	}
	
}