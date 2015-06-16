package oop.ex6.scopes;

import java.util.ArrayList;

import oop.ex6.main.Variable;

public class MethodScope extends Scope {
	private ArrayList<Variable> parameters;

	//Constructs a method with no parameters
	public MethodScope(String name) {
		super(name);
	}
	
	public MethodScope(String name, ArrayList<Variable> parameters) {
		super(name);
		this.parameters = parameters;
	}
	

	public MethodScope(String name, ArrayList<Variable> parameters, ArrayList<Variable> vars) throws illegalVariableDeclerationException {
		super(name, vars);
		this.parameters = parameters;
	}

}
