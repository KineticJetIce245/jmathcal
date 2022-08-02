package Jmathcal.Expression;

import java.util.HashMap;

import Jmathcal.Number.Complex.ComplexNum;

public class VariablePool {

    private HashMap<String, Variable> variablePool = new HashMap<String, Variable>();
    public VariablePool() {}
    public boolean contains(String valLabel) {
        return this.variablePool.containsKey(valLabel);
    }
    public Variable getVariable(String valLabel) {
        return this.variablePool.get(valLabel);
    }
    private VariablePool getOuter() {
        return this;
    }
    @Override
    public String toString() {
        return this.variablePool.toString();
    }

    public class Variable implements ExprElements {
        private ComplexNum value;
        private String name;
        public final String label;
    
        public Variable(String label) throws VariableLabelOccupiedException {
            this.label = label;
            if (getOuter().contains(this.label)) throw new VariableLabelOccupiedException();
            getOuter().variablePool.put(label, this);
        }
        public ComplexNum getValue() {
            return value;
        }
        public void setValue(ComplexNum value) {
            this.value = value;
        }
        public String getName() {
            return name;    
        }
        public void setName(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return "(" + this.label + ", " + this.name + ", " + this.value + ")";
        }
        public ComplexNum askForValue() {
            //TODO
            return new ComplexNum("1");
        }
    
    }
}
