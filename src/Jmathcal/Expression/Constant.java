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

    public ComplexNum getValue(MathContext mc) {
        return this.value.getValue(mc);
    }

    @Override
    public String toString() {
        return this.value.name();
    }
    @Override
    public ExprNumber toNumber(MathContext mc) {
        return this.getValue(mc).toString();
    }

    public static enum Constants {
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

        Constants(ValFinder val) {
            this.val = val;
        }

        public ComplexNum getValue(MathContext mc) {
            return this.val.getValue(mc);
        }
    }

}
