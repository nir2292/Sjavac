package oop.ex6.main;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oop.ex6.scopes.*;

public class Validator {
	private final String valueAssignmentRegex = "\\w+\\=\\w+\\;";
	private final String varRegex = "\\s*(['\"]*\\s*\\w+\\s*['\"]*)\\s*";
	private final String methodDeclerationRegex = "([\\w]+)\\s*\\(\\s*(['\"]*\\s*(\\w+\\.*\\w*)\\s*['\"]*\\s*,\\s*)*\\s*['\"]*\\s*(\\w+\\.*\\w*)?\\s*['\"]*\\s*\\)\\s*";
	private Scope mainScope;
	private ArrayList<MethodScope> methods;
	
	public Validator(Scope scope) {
		this.mainScope = scope;
		this.methods = mainScope.getInternalMethods();
	}

	private boolean ValidateMethodAssignments(Scope method) throws badFileFormatException {
		ArrayList<String> changedVariables = method.getChangedVars();
		for(String var:changedVariables){
			String varInfo[] = var.split("\\, ");
			String varName = varInfo[0];
			String varNewValue = varInfo[1];
			Variable varOfValue = method.getVariableByName(varNewValue);
			if(varOfValue != null){
				method.setVariableValue(varName, varOfValue.getValue());
			}
			else
				method.setVariableValue(varName, varNewValue);
		}
		return true;
	}

	public boolean isValid() throws badFileFormatException{
		for(Scope method:methods){
			if(!(validateScope(method) && ValidateMethodAssignments(method)))
				return false;
		}
		return true;
	}

	private boolean validateScope(Scope method) throws badFileFormatException {
		ArrayList<String> changedVariables = method.getChangedVars();
		ArrayList<ConditionScope> internalConditionScope = method.getInternalConditionScopes();
		ArrayList<String> calledMethods = method.getCalledMethods();
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
				if(suitedVar != null && !suitedVar.getType().equals(methodParam.getType())){
					throw new badMethodCallException("Parameter " + suitedVar.getName() + " for method " + methodName + " is invalid.");
				} else {
					String methodParamType = methodParam.getType();
					Matcher methodParamTypeMatcher = Type.valueOf(methodParamType).getMatcher(m.group(1));
					if(!methodParamTypeMatcher.matches()){
						throw new badMethodCallException("Parameter " + m.group(1) + " for method " + methodName + " is invalid.");
					}
				}
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
