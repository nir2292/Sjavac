package oop.ex6.main;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oop.ex6.scopes.*;
/**
 * Validates the code is legit.
 *
 */
public class Validator {
	private final String varRegex = "\\s*(['\"]*\\s*\\w+\\.*\\w*\\s*['\"]*)\\s*";
	private final String methodDeclerationRegex = "([\\w]+)\\s*\\(\\s*(['\"]*\\s*(\\w+\\.*\\w*)\\s*['\"]*\\s*,\\s*)*\\s*['\"]*\\s*(\\w+\\.*\\w*)?\\s*['\"]*\\s*\\)\\s*";
	final static String varValuesRegex = "\\s*(\\w+)\\s*(\\=\\s*([\"\\']*[\\w"+Parser.LEGAL_CHARS+"]+[\"\\']*)\\s*)?";
	final static String varModifierRegex = "\\s*(final)*\\s*";
	final static String varDeclerationRegex = varModifierRegex + "\\s*([a-zA-Z]+)\\s+(" + varValuesRegex + ",)*(" + varValuesRegex + ")?\\s*";
	final static String varChangeRegex = "\\w+\\, \"*\\'*\\w+\"*\\'*";
	final static String methodName = "([a-zA-Z]\\w*)";
	final static String callMethod = methodName  + "\\s*\\(\\s*(['\"]*\\s*(\\w+\\.*\\w*)\\s*['\"]*\\s*,\\s*)*\\s*['\"]*\\s*(\\w+\\.*\\w*)?\\s*['\"]*\\s*\\)\\s*";
	final static String ConditionalScopeHeader = "(while|if)\\s*\\(\\s*([\\w]+)\\s*((\\|\\||\\&\\&)\\s*([\\w]+)\\s*)*\\s*\\)\\s*\\{";
	private Scope mainScope;
	private ArrayList<MethodScope> methods;
	
	public Validator(Scope scope) {
		this.mainScope = scope;
		this.methods = mainScope.getInternalMethods();
	}

	/**
	 * Runs the tests on each of the code's scopes.
	 * @return true iff the code is valid.
	 * @throws InValidCodeException
	 */
	public boolean isValid() throws InValidCodeException{
		ArrayList<Variable> originalGlobalVars = new ArrayList<>();
		for(Variable var:Scope.globalVariables){
			originalGlobalVars.add(new Variable(var));
		}
		ArrayList<Variable> originalMainScopeVars = mainScope.getKnownVariables();
		if(!(validateMethodCalls(mainScope) && runMethod(mainScope.getName(), mainScope, 0))){
			Scope.resetGlobalVariables();
			return false;
		}
		Scope.setGlobalVariables(originalGlobalVars);
		mainScope.setKnownVariables(originalMainScopeVars);
		for(MethodScope method:methods){
			ArrayList<Variable> originalMethodVars = method.getKnownVariables();
			if(!method.isReturned())
				throw new badFileFormatException("No return statement at the end of method " + method.getName());
			if(!(validateMethodCalls(method) && runMethod(method.getName(), method, 0))){
				Scope.resetGlobalVariables();
				return false;
			}
			Scope.setGlobalVariables(originalGlobalVars);
			method.setKnownVariables(originalMethodVars);
		}
		Scope.resetGlobalVariables();
		return true;
	}
	
	/**
	 * "Running" the method and checking for illegal operations.
	 * @param parentMethodName the current checked method's name.
	 * @param method the method/scope currenlt checked.
	 * @param conditionIndex the index of the conditional scope we have to check next, 
	 * 		if exists in the method.
	 * @return true iff the method is valid.
	 * @throws InValidCodeException
	 */
	private boolean runMethod(String parentMethodName, Scope method, int conditionIndex) throws InValidCodeException{
		for(String command:method.getChronologyRun()){
			if(Pattern.matches(callMethod, command)){
				if(command.substring(0, command.indexOf("(")).trim().equals(parentMethodName.trim()))
					continue;
				String methodName = command.substring(0, command.indexOf("(")).trim();
				runMethod(methodName, mainScope.getInternalMethod(methodName), 0);
			}
			if(Pattern.matches(varChangeRegex, command))
				if(!validateAssignment(method, command))
					return false;
			if(Pattern.matches(Parser.VARIABLE_DECLERATION, command)){
				validateNewVariable(method, command);
			}
			if(Pattern.matches(ConditionalScopeHeader, command)){
				ConditionScope methodCondition = method.getInternalConditionScopes().get(conditionIndex);
				runMethod(parentMethodName, methodCondition, 0);
				conditionIndex++;
			}
		}
		return true;
	}
	
	/*
	 * Validates the assignment of a new variable is legal.
	 */
	private void validateNewVariable(Scope method, String command) throws InValidCodeException {
		Pattern p = Pattern.compile(varDeclerationRegex);
		Matcher m = p.matcher(command);
		m.matches();
		String varModifier = m.group(1), varType = m.group(2);
		Type var;
		m.reset(command.substring(m.start(2) + varType.length()));
		m.usePattern(Pattern.compile(varValuesRegex));
		while(m.find()){
			String varName = m.group(1);
			String varValue = m.group(3);
			Variable varToSet = method.getVariableByName(varName);
			Variable varToChange = method.getVariableByName(m.group(3));
			if(varToChange != null){
				if(varToChange.getValue() == Variable.isParameter && varToChange.getType().equals(varToSet.getType()))
					break;
				if(varToChange.isGlobal() && method.getVariableByName(varName).isGlobal())
					if(Scope.globalVariables.indexOf(varToChange) > Scope.globalVariables.indexOf(method.getVariableByName(varName)))
						throw new illegalAssignmentException("can't assign value of " + varToChange.getName() + " to variable " + varName);
				if(varToChange.getValue() == null)
					throw new illegalAssignmentException("can't assign null to var " + varName);
				else{
					method.setVariableValue(varName, varToChange.getValue());
				}
			}
			if(method.getVariableByName(varName).getValue() == null && varValue != null)
				throw new illegalAssignmentException("illegal assignment for var " + varName);
		}
	}
	
	/*
	 * Validates the assignment of an existing variable is legal.
	 */
	private boolean validateAssignment(Scope method, String var) throws InValidCodeException {
		MethodScope msc = null;
		try{
			msc = (MethodScope)method;
		}
		catch(java.lang.ClassCastException e){
		}
		String varInfo[] = var.split("\\, ");
		String varName = varInfo[0];
		String varNewValue = varInfo[1];
		Variable varToChange = method.getVariableByName(varName);
		if(varToChange == null)
			throw new noSuchVariableException("unknown variable " + varName);
		Variable varOfValue = method.getVariableByName(varNewValue);
		if(varOfValue != null){
			if(varOfValue.getValue() == null){
				if(msc != null){
					Variable methodParam = msc.getParameterByName(varOfValue.getName());
					if(methodParam == null)
						throw new illegalAssignmentException("can't assign null to value of " + varName);
					else
						return true;
				}
				else
					throw new illegalAssignmentException("can't assign null to value of " + varName);
			}
			else method.setVariableValue(varName, varOfValue.getValue());
		}
		else
			method.setVariableValue(varName, varNewValue);
		return true;
	}
	
	/*
	 * Validates the method calls in each of the scopes.
	 */
	private boolean validateMethodCalls(Scope method) throws InValidCodeException {
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
				if(parameterIndex >= methodScope.getParamers().size())
					throw new badMethodCallException("Bad give parameters for method " + methodScope.getName());
				String givenParameterName = m.group(1).trim();
				Variable suitedVar = method.getVariableByName(givenParameterName);
				Variable methodParam = methodScope.getParameter(parameterIndex);
				if(suitedVar != null && !suitedVar.getType().equals(methodParam.getType())){
					throw new badMethodCallException("Parameter " + suitedVar.getName() + " for method " + methodName + " is invalid.");
				} else {
					if(suitedVar != null){
						parameterIndex++;
						continue;
					}
					String methodParamType = methodParam.getType();
					Matcher methodParamTypeMatcher = Type.valueOf(methodParamType).getMatcher(givenParameterName);
					if(!methodParamTypeMatcher.matches()){
						throw new badMethodCallException("Parameter " + givenParameterName + " for method " + methodName + " is invalid.");
					}
				}
				parameterIndex++;
			}
			if(methodScope.getParamers().size() != 0 && parameterIndex == 0)
				throw new badMethodCallException("Bad give parameters for method " + methodScope.getName());
		}
		if(method.getName().equals("while") || method.getName().equals("if")){
			ConditionScope cScope = (ConditionScope)method;
			cScope.validateConditions();
		}
		for(ConditionScope scope:internalConditionScope)
			return validateMethodCalls(scope);
		return true;
	}
}
