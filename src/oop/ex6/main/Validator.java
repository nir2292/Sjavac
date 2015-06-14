package oop.ex6.main;

import java.util.ArrayList;

import oop.ex6.scopes.*;

public class Validator {
	private final String valueAssignmentRegex = "\\w+\\=\\w+\\;";
	private ArrayList<Scope> scopes;
	
	public Validator(ArrayList<Scope> scopesArray) {
		this.scopes = scopesArray;
	}
	
	public boolean isValid() throws badConditionFormat, noSuchVariable{
		return ValidateScopes() && ValidateConditions();
	}
	
	private boolean ValidateConditions() throws badConditionFormat, noSuchVariable {
		for(Scope scope:scopes){
			ArrayList<ConditionScope> conditionScopes = scope.getInternalScopes();
			for(ConditionScope cScope:conditionScopes)
				cScope.validateConditions();
		}
		return true;
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
