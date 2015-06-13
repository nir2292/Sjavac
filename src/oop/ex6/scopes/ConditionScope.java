package oop.ex6.scopes;

import java.util.ArrayList;
import java.util.regex.Matcher;

import oop.ex6.main.Type;
import oop.ex6.main.Variable;
import oop.ex6.main.noSuchVariable;

public class ConditionScope extends Scope {
	private Matcher conditionBooleanMatcher;
	private Matcher conditionValueMatcher;

	public ConditionScope(String[] conditions) throws badConditionFormat, noSuchVariable {
		super();
		addConditions(conditions);
	}

	public ConditionScope(String[] conditions, ArrayList<Variable> vars) throws badConditionFormat, noSuchVariable {
		super(vars);
		addConditions(conditions);
	}

	private void addConditions(String[] conditions) throws badConditionFormat, noSuchVariable {
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
