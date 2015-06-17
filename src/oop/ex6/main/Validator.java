package oop.ex6.main;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oop.ex6.scopes.*;

public class Validator {
	private final String valueAssignmentRegex = "\\w+\\=\\w+\\;";
	private final String varRegex = "\\s*(\\w+)\\s*";
	private final String methodDeclerationRegex = "([\\w]+)\\s*\\(\\s*((([\\w]+)\\s*\\,\\s*)*([\\w]+)?)*\\s*\\)\\s*";
	private Scope mainScope;
	private ArrayList<MethodScope> methods;
	
	public Validator(Scope scope) {
		this.mainScope = scope;
		this.methods = mainScope.getInternalMethods();
	}
	
	public boolean isValid() throws badFileFormatException{
		addGlobalVarsToMethods(mainScope.getKnownVariables());
		return ValidateScopes();// && ValidateConditions();
	}

	private void addGlobalVarsToMethods(ArrayList<Variable> knownVariables) {
		for(Scope method:methods)
			try {
				method.addAllVars(knownVariables);
			} catch (illegalVariableDeclerationException e) {
				continue;
			}
	}

	private boolean ValidateScopes() throws badFileFormatException{
		for(Scope method:methods){
			if(!validateScope(method))
				return false;
		}
		return true;
	}

	private boolean validateScope(Scope method) throws badFileFormatException {
		ArrayList<String> changedVariables = method.getChangedVars();
		ArrayList<ConditionScope> internalConditionScope = method.getInternalConditionScopes();
		ArrayList<String> calledMethods = method.getCalledMethods();
		for(String var:changedVariables){
			String varInfo[] = var.split("\\, ");
			method.setVariableValue(varInfo[0], varInfo[1]);
		}
		for(String mthd:calledMethods){
			Pattern p = Pattern.compile(methodDeclerationRegex);
			Matcher m = p.matcher(mthd);
			m.matches();
			String methodName = m.group(1);
			MethodScope methodScope = mainScope.getInternalMethod(methodName);
			m.reset(mthd.substring(m.start() + m.group(1).length()));
			m.usePattern(Pattern.compile(varRegex));
			int parameterIndex = 0;
			while(m.find()){
				Variable suitedVar = method.getVariableByName(m.group(1));
				Variable methodParam = methodScope.getParameter(parameterIndex);
				if(!suitedVar.getType().equals(methodParam.getType()))
					throw new badMethodCallException("Parameter " + suitedVar.getName() + " for method " + methodName + " is invalid.");
				parameterIndex++;
			}
		}
		if(method.getName().equals("while") || method.getName().equals("if")){
			ConditionScope cScope = (ConditionScope)method;
			cScope.validateConditions();
		}
		for(ConditionScope scope:internalConditionScope)
			return validateScope(scope);
		return true;
	}
}
