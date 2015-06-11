package oop.ex6.scopes;
import java.util.ArrayList;

import oop.ex6.main.*;

public abstract class Scope {
	private static ArrayList<Variable<Type>> globalVariables;
	private ArrayList<Variable<Type>> knownVariables;
	private ArrayList<Scope> subScopes;
	
	public Scope(){
		Scope.globalVariables = new ArrayList<>();
		this.knownVariables = new ArrayList<>();
		this.subScopes = new ArrayList<>();
	}
	
	public Scope(ArrayList<Variable<Type>> vars){
		Scope.globalVariables = new ArrayList<>();
		this.knownVariables = new ArrayList<>();
		this.subScopes = new ArrayList<>();
		addGlobalVars(vars);
	}
	
	private void addGlobalVars(ArrayList<Variable<Type>> vars){
		for(Variable<Type> var:vars)
			Scope.globalVariables.add(var);
	}
}
