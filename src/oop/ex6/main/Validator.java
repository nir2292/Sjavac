package oop.ex6.main;

import java.util.ArrayList;

import oop.ex6.scopes.Scope;

public class Validator {
	private final String valueAssignmentRegex = "\\w+\\=\\w+\\;";
	private ArrayList<Scope> scopes;
	
	public Validator(ArrayList<Scope> scopesArray) {
		this.scopes = scopesArray;
	}
	
	public boolean isValid(){
		return ValidateScopes();
	}
	
	private boolean ValidateScopes(){
		for(Scope scope:scopes){
			ArrayList<Variable> knownVariables = scope.getKnownVariables();
			ArrayList<Variable> changedVariables = scope.getChangedVars();
			for(Variable var:changedVariables)
				if(!knownVariables.contains(var))
					return false;
		}
		return true;
	}
}
