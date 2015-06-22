package oop.ex6.scopes;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oop.ex6.main.Type;
import oop.ex6.main.Variable;
import oop.ex6.main.noSuchVariableException;

/**
 * Represents an if/while scope in the code.
 */
public class ConditionScope extends Scope {
	private static final String ACCEPTED_TYPES = "int|boolean|double";
	private Matcher conditionBooleanMatcher;
	private ArrayList<String> conditions;
	
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
	
	/**
	 *  Adds the given conditions to the current conditions.
	 * @param conditions the conditions to add.
	 */
	public void addContitions(String[] conditions) {
		if (conditions != null) {
			for (String condition : conditions){
				this.conditions.add(condition.trim());
			}
		}
	}
	
	/**
	 * Validates the conditions are legal.
	 * @return true iff the conditions are legal.
	 * @throws badConditionFormat
	 * @throws noSuchVariableException
	 */
	public boolean validateConditions() throws badConditionFormat, noSuchVariableException{
		checkConditions();
		return true;
	}
	
	/**
	 * Checks whether each of the conitions is legal.
	 * @throws badConditionFormat
	 * @throws noSuchVariableException
	 */
	private void checkConditions() throws badConditionFormat, noSuchVariableException {
		for(String condition:conditions){
			conditionBooleanMatcher = Type.BOOLEAN.getMatcher(condition);
			if(conditionBooleanMatcher.matches())
				continue;
			else{
				Variable var = getVariableByName(condition);
				String conditionType = var.getType().toLowerCase();
				if(Pattern.matches(ACCEPTED_TYPES, conditionType) && var.getValue() != null)
					continue;
			}
			throw new badConditionFormat("Condition has to be of type boolean and initialized.");
		}
	}

}
