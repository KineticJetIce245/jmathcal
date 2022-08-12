package Jmathcal.Expression;

import java.math.MathContext;

import Jmathcal.Number.Complex.ComplexDbl;
import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.Function.Trigo;

public class ExprNumber implements ExprElements {
    public static int DBL = 10;
    public final String value;
    public ExprNumber(String value) {
        this.value = value;
    }
    public ComplexNum toComplexNum() {
        return new ComplexNum(this.value);
    }
    public ComplexDbl toComplexDbl() {
        return new ComplexDbl(Double.valueOf(this.value));
    }
    @Override
    public String toString() {
        return value;
    }
    @Override
    public ExprNumber toNumber(MathContext mc) {
        return this;
    }
    public ExprNumber add(ExprNumber augend, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            ComplexDbl b = new ComplexNum(augend).toComplexDbl();
            return new ExprNumber(a.add(b).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            ComplexNum b = new ComplexNum(augend);
            return new ExprNumber(a.add(b).round(mc).toString());
        }
    }
    public ExprNumber subtract(ExprNumber subtrahend, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            ComplexDbl b = new ComplexNum(subtrahend).toComplexDbl();
            return new ExprNumber(a.subtract(b).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            ComplexNum b = new ComplexNum(subtrahend);
            return new ExprNumber(a.subtract(b).round(mc).toString());
        }
    }
    public ExprNumber multiply(ExprNumber multiplicand, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            ComplexDbl b = new ComplexNum(multiplicand).toComplexDbl();
            return new ExprNumber(a.multiply(b).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            ComplexNum b = new ComplexNum(multiplicand);
            return new ExprNumber(a.multiply(b).round(mc).toString());
        }
    }
    public ExprNumber divide(ExprNumber dividend, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            ComplexDbl b = new ComplexNum(dividend).toComplexDbl();
            return new ExprNumber(a.divide(b).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            ComplexNum b = new ComplexNum(dividend);
            return new ExprNumber(a.divide(b, mc).toString());
        }
    }
    public ExprNumber pow(ExprNumber exponent, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            ComplexDbl b = new ComplexNum(exponent).toComplexDbl();
            return new ExprNumber(a.pow(b).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            ComplexNum b = new ComplexNum(exponent);
            return new ExprNumber(a.pow(b, mc).toString());
        }
    }
    public ExprNumber percent(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            return new ExprNumber(a.divide(new ComplexDbl(100)).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            return new ExprNumber(a.scaleByPowerOfTen(-2).round(mc).toString());
        }
    }
    public ExprNumber sqrt(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            return new ExprNumber(a.pow(new ComplexDbl(0.5)).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            return new ExprNumber(a.pow(new ComplexNum("0.5"), mc).toString());
        }
    }
    public ExprNumber sin(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            return new ExprNumber((Trigo.sin(a)).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            return new ExprNumber(Trigo.sin(a, mc).toString());
        }
    }
    public ExprNumber cos(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            return new ExprNumber((Trigo.cos(a)).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            return new ExprNumber(Trigo.cos(a, mc).toString());
        }
    }
}
