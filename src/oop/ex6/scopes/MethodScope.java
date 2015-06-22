package oop.ex6.scopes;

import java.util.ArrayList;

import oop.ex6.main.InValidCodeException;
import oop.ex6.main.Variable;
import oop.ex6.main.badMethodCallException;

/**
 * Represents a method scope in the code.
 *
 */
public class MethodScope extends Scope {
	private ArrayList<Variable> parameters;
	private boolean returned;

	//Constructs a method with no parameters
	public MethodScope(String name) {
		super(name);
		this.parameters = new ArrayList<>();
		this.returned = false;
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
	
	/**
	 * 
	 * @param index
	 * @return
	 * @throws badMethodCallException
	 */
	public Variable getParameter(int index) throws badMethodCallException{
		if(index < parameters.size())
			return parameters.get(index);
		else
			throw new badMethodCallException("Bad method call");
	}
	
	/**
	 * Adds parameters to the method's parameters.
	 * @param params the parameters to add.
	 * @throws InValidCodeException
	 */
	public void addToParameters(ArrayList<Variable> params) throws InValidCodeException {
		for(Variable var:params){
			var.setValue(Variable.isParameter);
			this.parameters.add(var);
		}
	}

	public ArrayList<Variable> getParamers() {
		return this.parameters;
	}
	
	/**
	 * Checks whether the given variable is a paramater of the method.
	 * @param varOfValue the variable to check.
	 * @return
	 */
	public boolean containsParameter(Variable varToCheck) {
		for(Variable var:parameters)
			if(var.getName().equals(varToCheck) && var.getType().equals(varToCheck))
				return true;
		return false;
	}
	
	public void markReturned(){
		this.returned = true;
	}
	
	public boolean isReturned(){
		return this.returned;
	}
}
