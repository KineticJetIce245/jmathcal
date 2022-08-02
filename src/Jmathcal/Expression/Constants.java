package Jmathcal.Expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;

public enum Constants {
    i(new ValFinder() {
        @Override
        public ComplexNum getValue(MathContext mc) {
            return ComplexNum.I;
        }
    }),
    e(new ValFinder() {
        @Override
        public ComplexNum getValue(MathContext mc) {
            return new ComplexNum(Exp.findExp(BigDecimal.ONE, mc));
        }
    }),
    pi(new ValFinder() {
        @Override
        public ComplexNum getValue(MathContext mc) {
            return new ComplexNum(Trigo.PI(mc));
        }
    }),
    g(new ValFinder() {
        @Override
        public ComplexNum getValue(MathContext mc) {
            return new ComplexNum(new BigDecimal("9.80665"));
        }
    }),
    G(new ValFinder() {
        @Override
        public ComplexNum getValue(MathContext mc) {
            return new ComplexNum(new BigDecimal(new BigInteger("667430"), 16));
        }
    });

    private final ValFinder val;
    private interface ValFinder {
        ComplexNum getValue(MathContext mc);
    }
    Constants (ValFinder val) {
        this.val = val;
    }
    public ComplexNum getValue(MathContext mc) {
        return this.val.getValue(mc);
    }

    public static void main(String args[]) {
        System.out.println(Constants.g.getValue(new MathContext(16)));
    }
}
