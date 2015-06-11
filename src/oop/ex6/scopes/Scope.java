package oop.ex6.scopes;
import java.util.ArrayList;

import oop.ex6.main.*;

public abstract class Scope {
	private static ArrayList<Variable> globalVariables;
	private ArrayList<Variable> knownVariables;
	private ArrayList<Scope> subScopes;
	
	public Scope(){
		Scope.globalVariables = new ArrayList<>();
		this.knownVariables = new ArrayList<>();
		this.subScopes = new ArrayList<>();
	}
	
	public Scope(ArrayList<Variable> vars){
		Scope.globalVariables = new ArrayList<>();
		this.knownVariables = new ArrayList<>();
		this.subScopes = new ArrayList<>();
		addGlobalVars(vars);
	}
	
	private void addGlobalVars(ArrayList<Variable> vars){
		for(Variable var:vars)
			Scope.globalVariables.add(var);
	}
}
