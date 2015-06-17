package oop.ex6.scopes;
import java.util.ArrayList;

import oop.ex6.main.*;

public class Scope {
	static public ArrayList<Variable> globalVariables = new ArrayList<>();
	private ArrayList<Variable> knownVariables;
	private ArrayList<String> changedVars;
	private ArrayList<String> calledMethods;
	private ArrayList<MethodScope> internalMethods;
	private ArrayList<ConditionScope> internalConditionScopes;
	private String name;
	
	public Scope(String name){
		this.knownVariables = new ArrayList<>();
		this.changedVars = new ArrayList<>();
		this.internalMethods = new ArrayList<>();
		this.internalConditionScopes = new ArrayList<>();
		this.calledMethods = new ArrayList<>();
		this.name = name;
	}
	
	public Scope(String name, ArrayList<Variable> vars) throws illegalVariableDeclerationException{
		this.knownVariables = new ArrayList<>();
		this.changedVars = new ArrayList<>();
		this.internalMethods = new ArrayList<>();
		this.internalConditionScopes = new ArrayList<>();
		this.calledMethods = new ArrayList<>();
		this.name = name;
		addAllVars(vars);
	}
	
	@Override
	public String toString() {
		String representation = "Scope: " + this.getName() + ", variables: ";
		for (Variable var : knownVariables) {
			representation = representation + var + ", ";
		}
		representation = representation + " Variables to change: ";
		for(String var:changedVars)
			representation = representation + var + " ";
		representation = representation + " Calling methods: ";
		for(String mthd:calledMethods)
			representation = representation + mthd + " ";
		return representation + " " + this.getClass();
	}
	
	public ArrayList<String> getCalledMethods(){
		return this.calledMethods;
	}
	
	public void addCalledMethod(String callingLine){
		// Checking if contains because we don't need to check twice for the same call.
		if(!calledMethods.contains(callingLine))
			calledMethods.add(callingLine);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void addVar(Variable var) throws illegalVariableDeclerationException{
		if(var.isGlobal()){
			if(!globalVariables.contains(var))
				globalVariables.add(var);
			else
				throw new illegalVariableDeclerationException("Variable " + var.getName() + " already declared");
		}
		else
			try {
				Variable sameVar = getVariable(var);
				throw new illegalVariableDeclerationException("Variable " + var.getName() + " already declared");
			} catch (noSuchVariable e) {
				knownVariables.add(var);
				for (MethodScope scope : internalMethods){
					scope.addVar(var);
				}
				for (ConditionScope scope : internalConditionScopes){
					scope.addVar(var);
				}
			}
	}

	public void addAllVars(ArrayList<Variable> vars) throws illegalVariableDeclerationException{
		for(Variable var:vars)
			addVar(var);
	}
	
	public void addAssignmentVar(String var){
		this.changedVars.add(var);
	}
	
	
	public ArrayList<Variable> getKnownVariables() {
		return knownVariables;
	}

	public ArrayList<String> getChangedVars() {
		return changedVars;
	}
	
	public ArrayList<MethodScope> getInternalMethods(){
		return this.internalMethods;
	}
	
	public ArrayList<ConditionScope> getInternalConditionScopes(){
		return this.internalConditionScopes;
	}
	
	/**
	 * Returns the variable wanted
	 * @param var the variable
	 * @return True iff the given var's and the found var's name AND type match.
	 * @throws noSuchVariable
	 */
	public Variable getVariable(Variable variable) throws noSuchVariable{
		Variable foundVar;
		String variableName = variable.getName();
		String variableType = variable.getType();
		for(Variable var:knownVariables)
			if(var.getName().equals(variableName)){
				return var;
			}
		for(Variable var:globalVariables)
			if(var.getName().equals(variableName) && var.getType().equals(variableName))
				return var;
		throw new noSuchVariable("Unknown variable " + variableName);
	}
	
	public void setVariableValue(String varName, String value) throws badFileFormatException{
		try{
			for(Variable var:knownVariables){
				if(var.getName().equals(varName)){
					var.setValue(value);
				}
			}
			for(Variable var:Scope.globalVariables){
				if(var.getName().equals(varName)){
					var.setValue(value);
				}
			}
		}
		catch(badFileFormatException e){
			throw new badFileFormatException("Bad value assignment for var " + varName + " or variable is final");
		}
	}
	
	public void addMethodScope(MethodScope scope) {
		internalMethods.add(scope);
	}
	
	public void addConditionScope(ConditionScope scope){
		internalConditionScopes.add(scope);
	}
	
	/**
	 * Returns the scope with the given name
	 * @param methodName
	 * @return the scope with the given name
	 * @throws noSuchMethodException
	 */
	public MethodScope getInternalMethod(String methodName) throws noSuchMethodException{
		for(Scope sc:internalMethods)
			if(sc.getName().equals(methodName))
				return (MethodScope)sc;
		throw new noSuchMethodException("Method " + methodName + " doesn't exist");
	}

	/**
	 * @param group
	 * @return the local variable with the name varName, if found, if not, the global variable with varName
	 * @throws noSuchVariable if either aren't found
	 */
	public Variable getVariableByName(String varName) {
		Variable localVar = null;
		Variable globalVar = null;
		for(Variable var:knownVariables){
			if(var.getName().equals(varName)){
				if(var.isGlobal())
					globalVar = var;
				else
					localVar = var;
			}
		}
		if(localVar != null)
			return localVar;
		else
			return globalVar;
	}
}
