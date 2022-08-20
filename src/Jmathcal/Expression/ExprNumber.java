package Jmathcal.Expression;

import java.math.MathContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Jmathcal.Number.Complex.ComplexDbl;
import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;

public class ExprNumber implements ExprElements {
    public static int DBL = 10;
    public final String value;
    public ExprNumber(String value) {
        this.value = value;
    }
    public ComplexNum toComplexNum() {
        return new ComplexNum(this);
    }
    public ComplexDbl toComplexDbl() {
        return new ComplexNum(this).toComplexDbl();
    }
    @Override
    public String toString() {
        return value;
    }
    public String toAnsString() {
        return this.toComplexNum().toAnsString();
    }
    @Override
    public ExprNumber toNumber(MathContext mc) {
        return this.round(mc);
    }
    public int intValue() {
        Pattern intPat = Pattern.compile("^\\d+");
        Matcher intMat = intPat.matcher(this.value);
        if (intMat.find()) {
            return (Integer.valueOf(this.value.substring(intMat.start(), intMat.end())));
        } else {
            throw new NumberFormatException();
        }
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
    public ExprNumber tan(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            return new ExprNumber((Trigo.tan(a)).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            return new ExprNumber(Trigo.tan(a, mc).toString());
        }
    }
    public ExprNumber arcsin(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            return new ExprNumber((Trigo.arcsin(a)).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            return new ExprNumber(Trigo.arcsin(a, mc).toString());
        }
    }
    public ExprNumber arccos(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            return new ExprNumber((Trigo.arccos(a)).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            return new ExprNumber(Trigo.arccos(a, mc).toString());
        }
    }
    public ExprNumber arctan(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            return new ExprNumber((Trigo.arctan(a)).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            return new ExprNumber(Trigo.arctan(a, mc).toString());
        }
    }
    public ExprNumber log(ExprNumber baseExpr,MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl num = new ComplexNum(this).toComplexDbl();
            ComplexDbl base = new ComplexNum(baseExpr).toComplexDbl();
            return new ExprNumber((Exp.log(base, num)).toString());
        } else {
            ComplexNum num = new ComplexNum(this);
            ComplexNum base = new ComplexNum(baseExpr);
            return new ExprNumber((Exp.log(base, num, mc)).toString());
        }
    }
    public ExprNumber ln(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            ComplexDbl a = new ComplexNum(this).toComplexDbl();
            return new ExprNumber((Exp.ln(a)).toString());
        } else {
            ComplexNum a = new ComplexNum(this);
            return new ExprNumber(Exp.ln(a, mc).toString());
        }
    }
    public ExprNumber round(MathContext mc) {
        return new ExprNumber(new ComplexNum(this).round(mc).toString());
    }
}
