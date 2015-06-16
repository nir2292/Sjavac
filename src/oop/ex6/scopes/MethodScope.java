package oop.ex6.scopes;

import java.util.ArrayList;

import oop.ex6.main.Variable;
import oop.ex6.main.badMethodCallException;

public class MethodScope extends Scope {
	private ArrayList<Variable> parameters;

	//Constructs a method with no parameters
	public MethodScope(String name) {
		super(name);
		this.parameters = new ArrayList<>();
	}
	
	public MethodScope(String name, ArrayList<Variable> parameters) {
		super(name);
		this.parameters = parameters;
	}
	

	public MethodScope(String name, ArrayList<Variable> parameters, ArrayList<Variable> vars) throws illegalVariableDeclerationException {
		super(name, vars);
		this.parameters = parameters;
	}
	
	public Variable getParameter(int index) throws badMethodCallException{
		try{
			return parameters.get(index);
		}
		catch (NullPointerException e){
			throw new badMethodCallException("Bad method call");
		}
	}

	public void addToParameters(ArrayList<Variable> params) {
		for(Variable var:params)
			this.parameters.add(var);
	}
}
