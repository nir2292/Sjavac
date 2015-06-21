package oop.ex6.scopes;
import java.util.ArrayList;

import oop.ex6.main.*;

public class Scope {
	static public ArrayList<Variable> globalVariables = new ArrayList<>();
	ArrayList<Variable> knownVariables;
	private ArrayList<String> changedVars;
	private ArrayList<String> calledMethods;
	private ArrayList<String> chronologyRun;
	private ArrayList<MethodScope> internalMethods;
	private ArrayList<ConditionScope> internalConditionScopes;
	private String name;
	
	public Scope(String name){
		this.knownVariables = new ArrayList<>();
		this.changedVars = new ArrayList<>();
		this.internalMethods = new ArrayList<>();
		this.internalConditionScopes = new ArrayList<>();
		this.calledMethods = new ArrayList<>();
		this.chronologyRun = new ArrayList<>();
		this.name = name;
	}
	
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
	
	public ArrayList<String> getChronologyRun() {
		return chronologyRun;
	}

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
	
	public ArrayList<String> getCalledMethods(){
		return this.calledMethods;
	}
	
	public void addCalledMethod(String callingLine){
		// Checking if contains because we don't need to check twice for the same call.
		if(!calledMethods.contains(callingLine))
			calledMethods.add(callingLine);
	}
	
	public String getName() {
		return this.name;
	}
	
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

	private boolean containGlobalVar(Variable var) {
		for(Variable globalVar:globalVariables)
			if(globalVar.getName().equals(var.getName()))
				return true;
		return false;
	}

	public void addAllVars(ArrayList<Variable> vars) throws illegalVariableDeclerationException{
		for(Variable var:vars)
			addVar(var);
	}
	
	public void addAssignmentVar(String var){
		this.changedVars.add(var);
	}
	
	
	public ArrayList<Variable> getKnownVariables() {
		return knownVariables;
	}

	public ArrayList<String> getChangedVars() {
		return changedVars;
	}
	
	public static void setGlobalVariables(ArrayList<Variable> globalVars) {
		Scope.resetGlobalVariables();
		for(Variable var:globalVars)
			Scope.globalVariables.add(new Variable(var));
	}

	public void setKnownVariables(ArrayList<Variable> knownVariables) {
		this.knownVariables = knownVariables;
	}

	public ArrayList<MethodScope> getInternalMethods(){
		return this.internalMethods;
	}
	
	public ArrayList<ConditionScope> getInternalConditionScopes(){
		return this.internalConditionScopes;
	}
	
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
	
	public void addMethodScope(MethodScope scope) {
		internalMethods.add(scope);
	}
	
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
