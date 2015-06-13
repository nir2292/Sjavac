package oop.ex6.scopes;
import java.util.ArrayList;

import oop.ex6.main.*;

public class Scope {
	private ArrayList<Variable> globalVariables;
	private ArrayList<Variable> knownVariables;
	private ArrayList<Scope> internalScopes;
	
	public Scope(){
		this.knownVariables = new ArrayList<>();
		this.globalVariables = new ArrayList<>();
	}
	
	public Scope(ArrayList<Variable> vars){
		this.knownVariables = new ArrayList<>();
		this.globalVariables = new ArrayList<>();
		addAllVars(vars);
	}
	
	public void addVar(Variable var){
		knownVariables.add(var);
	}
	
	public void addAllVars(ArrayList<Variable> vars){
		for(Variable var:vars)
			this.knownVariables.add(var);
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
		for(Variable var:globalVariables)
			if(var.getName().equals(varName))
				return var;
		throw new noSuchVariable("Unknown variable" + varName);
	}
	
	public void addScope(Scope scope) {
		internalScopes.add(scope);
	}
}
