package oop.ex6.scopes;
import java.util.ArrayList;

import oop.ex6.main.*;

public class Scope {
	private ArrayList<Variable> knownVariables;
	private ArrayList<String> changedVars;
	private ArrayList<Scope> internalScopes;
	private ArrayList<String> calledMethods;
	private String name;
	
	public Scope(String name){
		this.knownVariables = new ArrayList<>();
		this.changedVars = new ArrayList<>();
		this.internalScopes = new ArrayList<>();
		this.calledMethods = new ArrayList<>();
		this.name = name;
	}
	
	public Scope(String name, ArrayList<Variable> vars) throws illegalVariableDeclerationException{
		this.knownVariables = new ArrayList<>();
		this.changedVars = new ArrayList<>();
		this.internalScopes = new ArrayList<>();
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
		return representation;
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
		try {
			Variable sameVar = getVariable(var.getName());
			throw new illegalVariableDeclerationException("Variable already declared");
		} catch (noSuchVariable e) {
			knownVariables.add(var);
			for (Scope scope : internalScopes){
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
	
	public ArrayList<Scope> getInternalScopes(){
		return this.internalScopes;
	}

	public boolean contains(Variable var){
		if(knownVariables.contains(var))
			return true;
		return false;
	}
	
	public Variable getVariable(String varName) throws noSuchVariable{
		for(Variable var:knownVariables)
			if(var.getName().equals(varName))
				return var;
		throw new noSuchVariable("Unknown variable " + varName);
	}
	
	public void setVariableValue(String varName, String value) throws badFileFormatException{
		getVariable(varName).setValue(value);
	}
	
	public void addScope(Scope scope) {
		internalScopes.add(scope);
	}
}
