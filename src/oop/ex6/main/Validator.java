package oop.ex6.main;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oop.ex6.scopes.*;

public class Validator {
	private final String valueAssignmentRegex = "\\w+\\=\\w+\\;";
	private final String varRegex = "\\w+\\s+\\w+";
	private final String methodDeclerationRegex = "([\\w]+)\\s*\\(\\s*((([\\w]+)\\s*\\,\\s*)*([\\w]+)?)*\\s*\\)\\s*";
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
		ArrayList<String> calledMethods = method.getCalledMethods();
		for(String var:changedVariables){
			String varInfo[] = var.split("\\, ");
			method.setVariableValue(varInfo[0], varInfo[1]);
		}
		for(String mthd:calledMethods){
			boolean methodExists = false; //assume the method doesn't exist
			Pattern p = Pattern.compile(methodDeclerationRegex);
			Matcher m = p.matcher(mthd);
			m.matches();
			String methodName = m.group(1);
			MethodScope methodScope = mainScope.getInternalScope(methodName);
			m.reset(mthd.substring(m.start() + m.group(1).length()));
			m.usePattern(Pattern.compile("\\s*(\\w+)\\s*"));
			int parameterIndex = 0;
			while(m.find()){
				Variable suitedVar = method.getVariable(m.group(1));
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
		if(!internalScopes.isEmpty())
			for(Scope scope:internalScopes)
				return validateScope(scope);
		return true;
	}
}
