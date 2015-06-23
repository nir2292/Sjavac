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
	
	/**
	 * @param varName - name of parameter
	 * @return - Variable object representing parameter of scope.
	 */
	public Variable getParameterByName(String varName) {
		for(Variable var:parameters){
			if(var.getName().equals(varName)){
					return var;
			}
		}
		return null;
	}
	
	/**
	 * @param name of scope.
	 * @param parameters - list of parameters for scope.
	 */
	public MethodScope(String name, ArrayList<Variable> parameters) {
		super(name);
		this.parameters = parameters;
	}
	
	/**
	 * @param name of scope
	 * @param parameters - list of parameters for scope.
	 * @param vars - known variables.
	 * @throws illegalVariableDeclerationException
	 */
	public MethodScope(String name, ArrayList<Variable> parameters, ArrayList<Variable> vars) throws illegalVariableDeclerationException {
		super(name, vars);
		this.parameters = parameters;
	}
	
	/**
<<<<<<< HEAD
	 * 
	 * @param index
	 * @return
=======
	 * @param index of parameter to return.
	 * @return parameter of index.
>>>>>>> branch 'master' of https://github.com/nir2292/Sjavac.git
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
	
	/**
	 * @return list of parameters for scope.
	 */
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
	
	/**
	 * marks scope as returned - has return declaration.
	 */
	public void markReturned(){
		this.returned = true;
	}
	

	/**
	 * @return true if scope has a return declaration.
	 */
	public boolean isReturned(){
		return this.returned;
	}
}
