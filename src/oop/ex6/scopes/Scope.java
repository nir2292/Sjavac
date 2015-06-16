package oop.ex6.scopes;
import java.util.ArrayList;

import oop.ex6.main.*;

public class Scope {
	private ArrayList<Variable> knownVariables;
	private ArrayList<String> changedVars;
	private ArrayList<Scope> internalScopes;
	private String name;
	
	public Scope(String name){
		this.knownVariables = new ArrayList<>();
		this.changedVars = new ArrayList<>();
		this.internalScopes = new ArrayList<>();
		this.name = name;
	}
	
	public Scope(String name, ArrayList<Variable> vars) throws illegalVariableDeclerationException{
		this.knownVariables = new ArrayList<>();
		this.changedVars = new ArrayList<>();
		this.internalScopes = new ArrayList<>();
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
	
	public String getName() {
		return this.name;
	}
	
	public void addVar(Variable var) throws illegalVariableDeclerationException{
		try {
			Variable sameVar = getVariable(var.getName());
		} catch (noSuchVariable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
