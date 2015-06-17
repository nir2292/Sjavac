package oop.ex6.scopes;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oop.ex6.main.Type;
import oop.ex6.main.Variable;
import oop.ex6.main.noSuchVariable;

public class ConditionScope extends Scope {
	private static final String ACCEPTED_TYPES = "int|boolean|double";
	private Matcher conditionBooleanMatcher;
	private ArrayList<String> conditions;
	private Matcher conditionValueMatcher;
	
	public ConditionScope(String name) {
		super(name);
		this.conditions = new ArrayList<>();
	}
	
	public ConditionScope(String name, String[] conditions) {
		super(name);
		this.conditions = new ArrayList<>();
		addContitions(conditions);
	}
	

	public ConditionScope(String name, String[] conditions, ArrayList<Variable> vars) throws illegalVariableDeclerationException {
		super(name, vars);
		this.conditions = new ArrayList<>();
		addContitions(conditions);
	}
	
	@Override
	public String toString() {
		String representation = super.toString() + " Conditions: ";
		for (String condition : conditions) {
			representation = representation + condition + ", ";
		}
		return representation;
	}
	public void addContitions(String[] conditions) {
		if (conditions != null) {
			for (String condition : conditions){
				this.conditions.add(condition.trim());
			}
		}
	}
	
	public boolean validateConditions() throws badConditionFormat, noSuchVariable{
		checkConditions();
		return true;
	}

	private void checkConditions() throws badConditionFormat, noSuchVariable {
		for(String condition:conditions){
			conditionBooleanMatcher = Type.BOOLEAN.getMatcher(condition);
			if(conditionBooleanMatcher.matches())
				continue;
			else{
				String conditionType = getVariableByName(condition).getType().toLowerCase();
				if(Pattern.matches(ACCEPTED_TYPES, conditionType))
					continue;
			}
			throw new badConditionFormat("Condition has to be of type boolean.");
		}
	}

}
