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
        int betterPrecision = mc.getPrecision() < 16 ? 16 : mc.getPrecision();
        MathContext calMc = new MathContext(betterPrecision, mc.getRoundingMode());
        return this.value.val.getValue(calMc);
    }

    public static enum Constants {
        i(new ValFinder() {
            @Override
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(ComplexNum.I);
            }
        }),
        e(new ValFinder() {
            @Override
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(new ComplexNum(Exp.findExp(BigDecimal.ONE, mc)));
            }
        }),
        pi(new ValFinder() {
            @Override
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(new ComplexNum(Trigo.PI(mc)));
            }
        }),
        g(new ValFinder() {
            @Override
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(new ComplexNum(new BigDecimal("9.80665")));
            }
        }),
        G(new ValFinder() {
            @Override
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(new ComplexNum(new BigDecimal(new BigInteger("667430"), 16)));
            }
        }),
        RAN(new ValFinder() {
            @Override
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(new ComplexNum(new BigDecimal(Double.toString(Math.random()))));
            }
        }),
        NA(new ValFinder() {
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(new ComplexNum(new BigDecimal("6.02214076E+23")));
            }
        }),
        PHI(new ValFinder() {
            public ExprNumber getValue(MathContext mc) {
                return new ExprNumber(new ComplexNum(new BigDecimal("5").sqrt(mc).add(BigDecimal.ONE).divide(new BigDecimal("2"))));
            }
        })
        ;

        private final ValFinder val;

        private interface ValFinder {
            ExprNumber getValue(MathContext mc);
        }

        Constants(ValFinder val) {
            this.val = val;
        }
    }

}
