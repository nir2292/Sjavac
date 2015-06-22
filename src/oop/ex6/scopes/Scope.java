package oop.ex6.scopes;
import java.util.ArrayList;

import oop.ex6.main.*;

/**
 * Represents a scope in the code.
 */
public class Scope {
	static public ArrayList<Variable> globalVariables = new ArrayList<>();
	ArrayList<Variable> knownVariables;
	private ArrayList<String> changedVars;
	private ArrayList<String> calledMethods;
	private ArrayList<String> chronologyRun;
	private ArrayList<MethodScope> internalMethods;
	private ArrayList<ConditionScope> internalConditionScopes;
	private String name;
	
	/**
	 * constructor for Scope class.
	 * @param name of scope
	 */
	public Scope(String name){
		this.knownVariables = new ArrayList<>();
		this.changedVars = new ArrayList<>();
		this.internalMethods = new ArrayList<>();
		this.internalConditionScopes = new ArrayList<>();
		this.calledMethods = new ArrayList<>();
		this.chronologyRun = new ArrayList<>();
		this.name = name;
	}
	
	/**
	 * constructor for Scope class.
	 * @param name of scope.
	 * @param vars to initiate.
	 * @throws illegalVariableDeclerationException
	 */
	public Scope(String name, ArrayList<Variable> vars) throws illegalVariableDeclerationException{
		this.knownVariables = new ArrayList<>();
		this.changedVars = new ArrayList<>();
		this.internalMethods = new ArrayList<>();
		this.internalConditionScopes = new ArrayList<>();
		this.calledMethods = new ArrayList<>();
		this.chronologyRun = new ArrayList<>();
		this.name = name;
		addAllVars(vars);
	}
	
	/**
	 * @return chronological order of scopes.
	 */
	public ArrayList<String> getChronologyRun() {
		return chronologyRun;
	}
	
	/**
	 * adds methods for chronological run list.
	 * @param chronologyRun
	 */
	public void addChronologyRun(String chronologyRun) {
		this.chronologyRun.add(chronologyRun);
	}

	@Override
	public String toString() {
		String representation = "Scope: " + this.getName() + ", variables: ";
		for (Variable var : knownVariables) {
			representation = representation + var + ", ";
		}
		representation = representation + " Variables to change: ";
		for(String var:changedVars)
			representation = representation + var + " ";
		representation = representation + " Calling methods: ";
		for(String mthd:calledMethods)
			representation = representation + mthd + " ";
		representation = representation + " Chronology ";
		for(String chrn:chronologyRun)
			representation = representation + chrn + " ";
		return representation + " " + this.getClass();
	}
	
	/**
	 * @return list of calls to methods.
	 */
	public ArrayList<String> getCalledMethods(){
		return this.calledMethods;
	}
	
	/**
	 * Adds a call to a method.
	 * @param callingLine the call to add.
	 */
	public void addCalledMethod(String callingLine){
		// Checking if contains because we don't need to check twice for the same call.
		if(!calledMethods.contains(callingLine))
			calledMethods.add(callingLine);
	}
	
	/**
	 * @return name of scope.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Adds a variable to the scope's known variables.
	 * @param var the variable to add.
	 * @throws illegalVariableDeclerationException
	 */
	public void addVar(Variable var) throws illegalVariableDeclerationException{
		if(var.isGlobal()){
			if(!containGlobalVar(var))
				globalVariables.add(var);
			else
				throw new illegalVariableDeclerationException("Variable " + var.getName() + " already declared");
		}
		else
			try {
				Variable sameVar = getVariable(var);
				throw new illegalVariableDeclerationException("Variable " + var.getName() + " already declared");
			} catch (noSuchVariableException e) {
				knownVariables.add(var);
				for (MethodScope scope : internalMethods){
					scope.addVar(var);
				}
				for (ConditionScope scope : internalConditionScopes){
					scope.addVar(var);
				}
			}
	}
	
	/**
	 * Checks whether the given variable is in the global variables.
	 * @param var the variable to check.
	 * @return true iff the given var is global.
	 */
	private boolean containGlobalVar(Variable var) {
		for(Variable globalVar:globalVariables)
			if(globalVar.getName().equals(var.getName()))
				return true;
		return false;
	}
	
	/**
	 * adds all variables to scope.
	 * @param vars to add
	 * @throws illegalVariableDeclerationException
	 */
	public void addAllVars(ArrayList<Variable> vars) throws illegalVariableDeclerationException{
		for(Variable var:vars)
			addVar(var);
	}
	
	/**
	 * Adds a variable assignment wanted to be performed.
	 * @param var the variable and it's new value to assign.
	 */
	public void addAssignmentVar(String var){
		this.changedVars.add(var);
	}
	
	/**
	 * @return known variables to scope.
	 */
	public ArrayList<Variable> getKnownVariables() {
		return knownVariables;
	}
	
	/**
	 * @return list of changed variables.
	 */
	public ArrayList<String> getChangedVars() {
		return changedVars;
	}
	/**
	 * Sets the global variables list to the given list.
	 * @param globalVars the vars to set.
	 */
	public static void setGlobalVariables(ArrayList<Variable> globalVars) {
		Scope.resetGlobalVariables();
		for(Variable var:globalVars)
			Scope.globalVariables.add(new Variable(var));
	}
	
	/**
	 * sets all known variables.
	 * @param knownVariables
	 */
	public void setKnownVariables(ArrayList<Variable> knownVariables) {
		this.knownVariables = knownVariables;
	}
	
	/**
	 * @return list of internal method scopes.
	 */
	public ArrayList<MethodScope> getInternalMethods(){
		return this.internalMethods;
	}
	
	/**
	 * @return list of internal condition scopes.
	 */
	public ArrayList<ConditionScope> getInternalConditionScopes(){
		return this.internalConditionScopes;
	}
	
	/**
	 * resets global variables.
	 */
	static public void resetGlobalVariables(){
		Scope.globalVariables = new ArrayList<>();
	}
	
	/**
	 * Returns the variable wanted
	 * @param var the variable
	 * @return True iff the given var's and the found var's name AND type match.
	 * @throws noSuchVariable
	 */
	public Variable getVariable(Variable variable) throws noSuchVariableException{
		Variable foundVar;
		String variableName = variable.getName();
		String variableType = variable.getType();
		for(Variable var:knownVariables)
			if(var.getName().equals(variableName)){
				return var;
			}
		for(Variable var:globalVariables)
			if(var.getName().equals(variableName) && var.getType().equals(variableName))
				return var;
		throw new noSuchVariableException("Unknown variable " + variableName);
	}
	
	/**
	 * Sets the given value to the given variable.
	 * @param varName the variable to change's name
	 * @param value the value to change to
	 * @throws InValidCodeException
	 */
	public void setVariableValue(String varName, String value) throws InValidCodeException{
		try{
			for(Variable var:knownVariables){
				if(var.getName().equals(varName)){
					var.setValue(value);
				}
			}
			for(Variable var:Scope.globalVariables){
				if(var.getName().equals(varName)){
					var.setValue(value);
				}
			}
		}
		catch(badFileFormatException e){
			throw new badFileFormatException("Bad value assignment for var " + varName);
		}
	}
	
	/**
	 * adds internal method scope.
	 * @param scope
	 */
	public void addMethodScope(MethodScope scope) {
		internalMethods.add(scope);
	}
	
	/**
	 * adds internal condition scope.
	 * @param scope
	 */
	public void addConditionScope(ConditionScope scope){
		internalConditionScopes.add(scope);
	}
	
	/**
	 * Returns the scope with the given name
	 * @param methodName
	 * @return the scope with the given name
	 * @throws noSuchMethodException
	 */
	public MethodScope getInternalMethod(String methodName) throws noSuchMethodException{
		for(Scope sc:internalMethods)
			if(sc.getName().equals(methodName))
				return (MethodScope)sc;
		throw new noSuchMethodException("Method " + methodName + " doesn't exist");
	}

	/**
	 * @param varName
	 * @return the local variable with the name varName, if found, if not, the global variable with varName
	 * @throws noSuchVariable if either aren't found
	 */
	public Variable getVariableByName(String varName) {
		for(Variable var:knownVariables){
			if(var.getName().equals(varName)){
					return var;
			}
		}
		for(Variable var:Scope.globalVariables){
			if(var.getName().equals(varName)){
				return var;
			}
		}
		return null;
	}
}
