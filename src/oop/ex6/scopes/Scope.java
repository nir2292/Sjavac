package oop.ex6.scopes;
import java.util.ArrayList;

import oop.ex6.main.*;

public abstract class Scope {
	private ArrayList<Variable> knownVariables;
	
	public Scope(){
		this.knownVariables = new ArrayList<>();
	}
	
	public Scope(ArrayList<Variable> vars){
		this.knownVariables = new ArrayList<>();
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
}
