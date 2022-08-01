package Jmathcal.Expression;

import java.util.HashMap;

public class VariablePool {
    private HashMap<String, Variable> variablePool = new HashMap<String, Variable>();
    public VariablePool() {}
    public boolean contains(String valLabel) {
        return this.variablePool.containsKey(valLabel);
    }
    public Variable getVariable(String valLabel) {
        return this.variablePool.get(valLabel);
    }
    public void addVariable(Variable variable) {
        if (this.contains(variable.label)) throw new VariableLabelOccupiedException();
        this.variablePool.put(variable.label, variable);
    }
}
