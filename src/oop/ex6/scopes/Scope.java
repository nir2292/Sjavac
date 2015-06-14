package oop.ex6.scopes;
import java.util.ArrayList;

import oop.ex6.main.*;

public class Scope {
	private ArrayList<Variable> knownVariables;
	private ArrayList<Variable> changedVars;
	private ArrayList<ConditionScope> internalScopes;
	
	public Scope(){
		this.knownVariables = new ArrayList<>();
		this.changedVars = new ArrayList<>();
		this.internalScopes = new ArrayList<>();
	}
	
	public Scope(ArrayList<Variable> vars){
		this.knownVariables = new ArrayList<>();
		this.changedVars = new ArrayList<>();
		this.internalScopes = new ArrayList<>();
		addAllVars(vars);
	}
	
	public void addVar(Variable var){
		knownVariables.add(var);
		for (Scope internalScope : internalScopes) {
			internalScope.addVar(var);
		}
	}
	
	public void addAllVars(ArrayList<Variable> vars){
		for(Variable var:vars)
			addVar(var);
	}
	
	public void addAssignmentVar(Variable var){
		this.changedVars.add(var);
	}
	

	public void addAllAssignmentVar(ArrayList<Variable> changedVars) {
		for(Variable var:changedVars)
			addAssignmentVar(var);
	}
	
	public ArrayList<Variable> getKnownVariables() {
		return knownVariables;
	}

	public ArrayList<Variable> getChangedVars() {
		return changedVars;
	}
	
	public ArrayList<ConditionScope> getInternalScopes(){
		return this.internalScopes;
	}

	public boolean knowsVariable(Variable var){
		if(knownVariables.contains(var))
			return true;
		return false;
	}
	
	public Variable getVariable(String varName) throws noSuchVariable{
		for(Variable var:knownVariables)
			if(var.getName().equals(varName))
				return var;
		throw new noSuchVariable("Unknown variable" + varName);
	}
	
	public void setVariableValue(String varName, String value) throws badFileFormatException{
		getVariable(varName).setValue(value);
	}
	
	public void addScope(ConditionScope scope) {
		internalScopes.add(scope);
	}
}
