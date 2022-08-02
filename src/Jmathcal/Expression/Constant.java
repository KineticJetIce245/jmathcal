package Jmathcal.Expression;

import java.math.MathContext;

import Jmathcal.Number.Complex.ComplexNum;

public class Constant implements ExprElements{
    public final Constants value;
    public Constant(Constants value) {
        this.value = value;
    }
    public ComplexNum getValue(MathContext mc) {
        return this.value.getValue(mc);
    }
    @Override
    public String toString() {
        return this.value.name();
    }
}

