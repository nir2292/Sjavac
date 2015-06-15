package oop.ex6.main;

import java.util.ArrayList;

import oop.ex6.scopes.*;

public class Validator {
	private final String valueAssignmentRegex = "\\w+\\=\\w+\\;";
	private Scope mainScope;
	private ArrayList<Scope> methods;
	
	public Validator(Scope scope) {
		this.mainScope = scope;
		this.methods = mainScope.getInternalScopes();
	}
	
	public boolean isValid() throws badFileFormatException{
		return ValidateScopes();// && ValidateConditions();
	}

	private boolean ValidateScopes() throws badFileFormatException{
		for(Scope method:methods){
			if(!validateScope(method))
				return false;
		}
		return true;
	}

	private boolean validateScope(Scope method) throws badFileFormatException {
		ArrayList<Variable> knownVariables = method.getKnownVariables();
		ArrayList<String> changedVariables = method.getChangedVars();
		ArrayList<Scope> internalScopes = method.getInternalScopes();
		for(String var:changedVariables){
			String varInfo[] = var.split("\\, ");
			method.setVariableValue(varInfo[0], varInfo[1]);
		}
		if(method.getName().equals("while") || method.getName().equals("if")){
			ConditionScope cScope = (ConditionScope)method;
			cScope.validateConditions();
		}
		if(!internalScopes.isEmpty())
			for(Scope scope:internalScopes)
				return validateScope(scope);
		return true;
	}
}
