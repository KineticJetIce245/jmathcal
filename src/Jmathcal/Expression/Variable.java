package Jmathcal.Expression;

import Jmathcal.Number.Complex.ComplexNum;

public class Variable implements ExprElements{

    private ComplexNum value;
    private String name;
    public final String label;

    public Variable(String label) {
        this.label = label;
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
        return "(" + this.label + "," + this.name + "," + this.value + ")";
    }

}
