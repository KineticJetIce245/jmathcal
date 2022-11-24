package Jmathcal.Expression;

import java.math.MathContext;
import java.util.HashMap;
import java.util.Set;

import Jmathcal.IOControl.IOBridge;

public class VariablePool {

    private HashMap<String, Variable> variablePool = new HashMap<String, Variable>();

    public VariablePool() {
    }

    public boolean contains(String valLabel) {
        return this.variablePool.containsKey(valLabel);
    }

    public Variable[] containsXY() {
        Variable[] variables = new Variable[2];
        if (this.contains("x")) {
            variables[0] = this.getVariable("x");
        }
        if (this.contains("y")) {
            variables[1] = this.getVariable("y");
        }
        return variables;
    }

    public Variable getVariable(String valLabel) {
        return this.variablePool.get(valLabel);
    }

    private VariablePool getOuter() {
        return this;
    }

    public void combinePool(VariablePool vp) {
        Set<String> vpKeySet = vp.variablePool.keySet();
        for (String vpLabel : vpKeySet) {
            if (!this.contains(vpLabel))
                this.variablePool.put(vpLabel, vp.getVariable(vpLabel));
        }
    }

    @Override
    public String toString() {
        return this.variablePool.toString();
    }

    public void setValueOf(String label, ExprNumber value) {
        this.getVariable(label).setValue(value);
    }

    public void setNameOf(String label, String name) {
        this.getVariable(label).setName(name);
    }

    public void askForValue(IOBridge bridge, MathContext mc) {
        Set<String> keySet = this.variablePool.keySet();
        for (String i : keySet) {
            this.variablePool.get(i).askForValue(bridge, mc);
        }
    }

    public void clearValues() {
        Set<String> keySet = this.variablePool.keySet();
        for (String i : keySet) {
            this.variablePool.get(i).setValue(null);
        }
    }

    public Set<String> getLabelSet() {
        return this.variablePool.keySet();
    }

    public void remove(String label) {
        this.variablePool.remove(label);
    }

    public class Variable implements ExprElements {

        private ExprNumber value;
        private String name;
        public final String label;

        public Variable(String label) throws VariableLabelOccupiedException {
            this.label = label;
            if (getOuter().contains(this.label))
                throw new VariableLabelOccupiedException();
            getOuter().variablePool.put(label, this);
        }

        public ExprNumber getValue() {
            return value;
        }

        public void setValue(ExprNumber value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public ExprNumber toNumber(MathContext mc) {
            return this.value;
        }

        @Override
        public String toString() {
            return "(" + this.label + ", " + this.name + ", " + this.value + ")";
        }

        public ExprNumber askForValue(IOBridge bridge, MathContext mc) {
            String inputFormula = bridge.askForInput("Value of variable " + label + ":");
            if (inputFormula == null) {
                throw new ExprSyntaxErrorException("Input is void");
            }
            Expressions inputExpressions = Expressions.parseFromFlattenExpr(
                    inputFormula, getOuter(), bridge);

            this.value = inputExpressions.calculate(mc);
            return this.value;
        }

        public VariablePool getPool() {
            return getOuter();
        }

    }
}
