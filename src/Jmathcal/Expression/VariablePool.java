package Jmathcal.Expression;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import Jmathcal.IOControl.IOBridge;

public class VariablePool {

    private HashMap<String, Variable> variablePool = new HashMap<String, Variable>();

    public VariablePool() {
    }

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

    public void askForValue(IOBridge bridge, MathContext mc) {
        Set<String> keySet = this.variablePool.keySet();
        for (String i : keySet) {
            this.variablePool.get(i).askForValue(bridge, mc);
        }
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
            Expressions inputExpressions = Expressions.parseFromFlattenExpr(
                    bridge.askForInput("Value of variable " + label + ":"),
                    getOuter(),
                    bridge);

            this.value = inputExpressions.calculate(mc);
            return this.value;
        }

        public VariablePool getPool() {
            return getOuter();
        }

    }
}
