package Jmathcal.Expression;

import java.math.MathContext;
import java.math.BigDecimal;
import java.math.BigInteger;

import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.Function.Trigo;
import Jmathcal.Number.Function.Exp;

public class Constant implements ExprElements {
    public final Constants value;

    public Constant(Constants value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value.name();
    }
    @Override
    public ExprNumber toNumber(MathContext mc) {
        return this.value.val.getValue(mc);
    }

    public static enum Constants {
        i(new ValFinder() {
            @Override
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(ComplexNum.I.toString());
            }
        }),
        e(new ValFinder() {
            @Override
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(new ComplexNum(Exp.findExp(BigDecimal.ONE, mc)).toString());
            }
        }),
        pi(new ValFinder() {
            @Override
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(new ComplexNum(Trigo.PI(mc)).toString());
            }
        }),
        g(new ValFinder() {
            @Override
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(new ComplexNum(new BigDecimal("9.80665")).toString());
            }
        }),
        G(new ValFinder() {
            @Override
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(new ComplexNum(new BigDecimal(new BigInteger("667430"), 16)).toString());
            }
        });

        private final ValFinder val;

        private interface ValFinder {
            ExprNumber getValue(MathContext mc);
        }

        Constants(ValFinder val) {
            this.val = val;
        }
    }

}
