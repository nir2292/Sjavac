package oop.ex6.scopes;

import java.util.ArrayList;

import oop.ex6.main.Variable;
import oop.ex6.main.badFileFormatException;
import oop.ex6.main.badMethodCallException;

public class MethodScope extends Scope {
	private ArrayList<Variable> parameters;

	//Constructs a method with no parameters
	public MethodScope(String name) {
		super(name);
		this.parameters = new ArrayList<>();
	}
	
	public Variable getParameterByName(String varName) {
		for(Variable var:parameters){
			if(var.getName().equals(varName)){
					return var;
			}
		}
		return null;
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

	public void addToParameters(ArrayList<Variable> params) throws badFileFormatException {
		for(Variable var:params){
			var.setValue(Variable.isParameter);
			this.parameters.add(var);
		}
	}

	public ArrayList<Variable> getParamers() {
		return this.parameters;
	}

	public boolean containsParameter(Variable varOfValue) {
		for(Variable var:parameters)
			if(var.getName().equals(varOfValue) && var.getType().equals(varOfValue))
				return true;
		return false;
	}
}
