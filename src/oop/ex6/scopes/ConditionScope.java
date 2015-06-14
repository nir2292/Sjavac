package oop.ex6.scopes;

import java.util.ArrayList;
import java.util.regex.Matcher;

import oop.ex6.main.Type;
import oop.ex6.main.Variable;
import oop.ex6.main.noSuchVariable;

public class ConditionScope extends Scope {
	private Matcher conditionBooleanMatcher;
	private String[] conditions;
	private Matcher conditionValueMatcher;
	
	public ConditionScope() {
		super();
	}
	
	public ConditionScope(String[] conditions) {
		super();
		this.conditions = conditions;
	}
	

	public ConditionScope(String[] conditions, ArrayList<Variable> vars) {
		super(vars);
		this.conditions = conditions;
	}

	public ConditionScope(Scope scope) {
		super();
		addAllVars(scope.getKnownVariables());
		addAllAssignmentVar(scope.getChangedVars());
	}

	public void addContitions(String[] conditions) {
		this.conditions = conditions;
	}
	
	public boolean validateConditions() throws badConditionFormat, noSuchVariable{
		addConditions();
		return true;
	}

	private void addConditions() throws badConditionFormat, noSuchVariable {
		for(String condition:conditions){
			conditionBooleanMatcher = Type.BOOLEAN.getMatcher(condition);
			if(conditionBooleanMatcher.matches())
				continue;
			else{
				conditionValueMatcher = Type.BOOLEAN.getMatcher(getVariable(condition).getValue());
				if(conditionValueMatcher.matches())
					continue;
			}
			throw new badConditionFormat("Condition has to be of type boolean.");
		}
	}

}
