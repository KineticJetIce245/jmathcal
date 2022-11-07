package Jmathcal.Expression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Jmathcal.Number.InfiniteValueException;
import Jmathcal.Number.UndefinedValueException;
import Jmathcal.Number.Complex.ComplexDbl;
import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.Function.AngleType;
import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;

public class ExprNumber implements ExprElements {
    public static int DBL = 16;
    private ComplexDbl valueDBL;
    private ComplexNum valueSTR;

    /**
     * Constructs a new {@code ExprNumber} strictly represented by the following
     * form : {@value a+bi}, where {@code a} and {@code b} are {@code String} that
     * can be
     * directly turned into {@code BigDecimal}.
     * 
     * @param value
     */
    public ExprNumber(String value) {
        StringBuffer complexValue = new StringBuffer(value);
        Pattern numPattern = Pattern.compile("^(\\+|\\-)?\\d+(\\.\\d+)?(E(\\+|\\-)?\\d+)?");
        Matcher numMatcher = numPattern.matcher(complexValue);

        Pattern infPattern = Pattern.compile("Infinity");
        Matcher infMatcher = infPattern.matcher(complexValue);

        Pattern nanPattern = Pattern.compile("NaN");
        Matcher nanMatcher = nanPattern.matcher(complexValue);

        BigDecimal realValue = null;
        BigDecimal imaValue = null;

        if (numMatcher.find()) {
            realValue = new BigDecimal(complexValue.substring(numMatcher.start(), numMatcher.end()));
            complexValue.delete(numMatcher.start(), numMatcher.end() + 1);
            complexValue.deleteCharAt(complexValue.length() - 1);
            imaValue = new BigDecimal(complexValue.toString());
        } else if (infMatcher.find()) {
            throw new InfiniteValueException(!(complexValue.charAt(0) == '-'));
        } else if (nanMatcher.find()) {
            throw new UndefinedValueException();
        } else {
            throw new NumberFormatException();
        }
        valueSTR = new ComplexNum(realValue, imaValue);
        valueDBL = valueSTR.toComplexDbl();
    }

    private ExprNumber(ComplexDbl valueDBL) {
        this.valueDBL = valueDBL;
        this.valueSTR = null;
    }

    private ExprNumber(ComplexNum valueSTR) {
        this.valueDBL = null;
        this.valueSTR = valueSTR;
    }

    public ComplexNum toComplexNum() {
        return valueSTR == null ? valueDBL.toComplexNum() : valueSTR;
    }

    public ComplexDbl toComplexDbl() {
        return valueDBL == null ? valueSTR.toComplexDbl() : valueDBL;
    }

    @Override
    public String toString() {
        return valueSTR == null ? valueDBL.toString() : valueSTR.toString();
    }

    public String toAnsString() {
        return this.toComplexNum().toAnsString();
    }

    @Override
    public ExprNumber toNumber(MathContext mc) {
        return this.round(mc);
    }

    public int intValue() {
        String value = valueSTR == null ? valueDBL.toString() : valueSTR.toString();
        Pattern intPat = Pattern.compile("^\\d+");
        Matcher intMat = intPat.matcher(value);
        if (intMat.find()) {
            return (Integer.valueOf(value.substring(intMat.start(), intMat.end())));
        } else {
            throw new NumberFormatException();
        }
    }

    public ExprNumber add(ExprNumber augend, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            if (augend.valueDBL == null)
                augend.valueDBL = augend.valueSTR.toComplexDbl();
            return new ExprNumber(this.valueDBL.add(augend.valueDBL));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            if (augend.valueSTR == null)
                augend.valueSTR = augend.valueDBL.toComplexNum();
            return new ExprNumber(valueSTR.add(augend.valueSTR).round(mc));
        }
    }

    public ExprNumber subtract(ExprNumber subtrahend, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            if (subtrahend.valueDBL == null)
                subtrahend.valueDBL = subtrahend.valueSTR.toComplexDbl();
            return new ExprNumber(this.valueDBL.subtract(subtrahend.valueDBL));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            if (subtrahend.valueSTR == null)
                subtrahend.valueSTR = subtrahend.valueDBL.toComplexNum();
            return new ExprNumber(this.valueSTR.subtract(subtrahend.valueSTR).round(mc));
        }
    }

    public ExprNumber multiply(ExprNumber multiplicand, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            if (multiplicand.valueDBL == null)
                multiplicand.valueDBL = multiplicand.valueSTR.toComplexDbl();
            return new ExprNumber(this.valueDBL.multiply(multiplicand.valueDBL));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            if (multiplicand.valueSTR == null)
                multiplicand.valueSTR = multiplicand.valueDBL.toComplexNum();
            return new ExprNumber(this.valueSTR.multiply(multiplicand.valueSTR).round(mc));
        }
    }

    public ExprNumber divide(ExprNumber dividend, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            if (dividend.valueDBL == null)
                dividend.valueDBL = dividend.valueSTR.toComplexDbl();
            return new ExprNumber(this.valueDBL.divide(dividend.valueDBL));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            if (dividend.valueSTR == null)
                dividend.valueSTR = dividend.valueDBL.toComplexNum();
            return new ExprNumber(this.valueSTR.divide(dividend.valueSTR, mc));
        }
    }

    public ExprNumber pow(ExprNumber exponent, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            if (exponent.valueDBL == null)
                exponent.valueDBL = exponent.valueSTR.toComplexDbl();
            return new ExprNumber(this.valueDBL.pow(exponent.valueDBL));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            if (exponent.valueSTR == null)
                exponent.valueSTR = exponent.valueDBL.toComplexNum();
            return new ExprNumber(this.valueSTR.pow(exponent.valueSTR, mc));
        }
    }

    public ExprNumber percent(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber(this.valueDBL.divide(new ComplexDbl(100)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber(this.valueSTR.scaleByPowerOfTen(-2).round(mc));
        }
    }

    public ExprNumber sqrt(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber(this.valueDBL.pow(new ComplexDbl(0.5)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber(this.valueSTR.pow(new ComplexNum("0.5"), mc));
        }
    }

    public ExprNumber sin(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber((Trigo.sin(this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber(Trigo.sin(this.valueSTR, mc));
        }
    }

    public ExprNumber cos(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber((Trigo.cos(this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber(Trigo.cos(this.valueSTR, mc));
        }
    }

    public ExprNumber tan(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber((Trigo.tan(this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber(Trigo.tan(this.valueSTR, mc));
        }
    }

    public ExprNumber arcsin(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber((Trigo.arcsin(this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber(Trigo.arcsin(this.valueSTR, mc));
        }
    }

    public ExprNumber arccos(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber((Trigo.arccos(this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber(Trigo.arccos(this.valueSTR, mc));
        }
    }

    public ExprNumber arctan(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber((Trigo.arctan(this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber(Trigo.arctan(this.valueSTR, mc));
        }
    }

    public ExprNumber log(ExprNumber baseExpr, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            if (baseExpr.valueDBL == null)
                baseExpr.valueDBL = baseExpr.valueSTR.toComplexDbl();
            return new ExprNumber((Exp.log(baseExpr.valueDBL, this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            if (baseExpr.valueSTR == null)
                baseExpr.valueSTR = baseExpr.valueDBL.toComplexNum();
            return new ExprNumber((Exp.log(baseExpr.valueSTR, this.valueSTR, mc)));
        }
    }

    public ExprNumber ln(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber((Exp.ln(this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber(Exp.ln(this.valueSTR, mc));
        }
    }

    public ExprNumber csc(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber(ComplexDbl.ONE.divide(Trigo.sin(this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            MathContext calPrecision = new MathContext(mc.getPrecision(), RoundingMode.HALF_UP);
            return new ExprNumber(ComplexNum.ONE.divide(Trigo.sin(this.valueSTR, calPrecision), mc));
        }
    }

    public ExprNumber sec(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber(ComplexDbl.ONE.divide(Trigo.cos(this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            MathContext calPrecision = new MathContext(mc.getPrecision(), RoundingMode.HALF_UP);
            return new ExprNumber(ComplexNum.ONE.divide(Trigo.cos(this.valueSTR, calPrecision), mc));
        }
    }

    public ExprNumber cot(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber(ComplexDbl.ONE.divide(Trigo.tan(this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            MathContext calPrecision = new MathContext(mc.getPrecision(), RoundingMode.HALF_UP);
            return new ExprNumber(ComplexNum.ONE.divide(Trigo.tan(this.valueSTR, calPrecision), mc));
        }
    }

    public ExprNumber sinh(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber(ComplexDbl.I.multiply(Trigo.sin(ComplexDbl.I.multiply(this.valueDBL))).negate());
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            MathContext calPrecision = new MathContext(mc.getPrecision(), RoundingMode.HALF_UP);
            return new ExprNumber(
                    ComplexNum.I.multiply(Trigo.sin(ComplexNum.I.multiply(this.valueSTR), calPrecision)).negate());
        }
    }

    public ExprNumber cosh(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber(Trigo.cos(ComplexDbl.I.multiply(this.valueDBL)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber(Trigo.cos(ComplexNum.I.multiply(this.valueSTR), mc));
        }
    }

    public ExprNumber tanh(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber(ComplexDbl.I.multiply(Trigo.tan(ComplexDbl.I.multiply(this.valueDBL))).negate());
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            MathContext calPrecision = new MathContext(mc.getPrecision(), RoundingMode.HALF_UP);
            return new ExprNumber(
                    ComplexNum.I.multiply(Trigo.tan(ComplexNum.I.multiply(this.valueSTR), calPrecision)).negate());
        }
    }

    public ExprNumber arsinh(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber(ComplexDbl.I.multiply(Trigo.arcsin(ComplexDbl.I.multiply(this.valueDBL))).negate());
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            MathContext calPrecision = new MathContext(mc.getPrecision(), RoundingMode.HALF_UP);
            return new ExprNumber(
                    ComplexNum.I.multiply(Trigo.arcsin(ComplexNum.I.multiply(this.valueSTR), calPrecision)).negate());
        }
    }

    public ExprNumber arcosh(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber(ComplexDbl.I.multiply(Trigo.cos(this.valueDBL)).negate());
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber((ComplexNum.I.multiply(Trigo.cos(this.valueSTR, mc)).negate()));
        }
    }

    public ExprNumber artanh(MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber(ComplexDbl.I.multiply(Trigo.arctan(ComplexDbl.I.multiply(this.valueDBL))).negate());
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            MathContext calPrecision = new MathContext(mc.getPrecision(), RoundingMode.HALF_UP);
            return new ExprNumber(
                    ComplexNum.I.multiply(Trigo.arctan(ComplexNum.I.multiply(this.valueSTR), calPrecision)).negate());
        }
    }

    public ExprNumber angleConvert(AngleType ori, AngleType target, MathContext mc) {
        if (mc.getPrecision() < DBL) {
            if (this.valueDBL == null)
                this.valueDBL = this.valueSTR.toComplexDbl();
            return new ExprNumber((Trigo.angleConvert(this.valueDBL, ori, target)));
        } else {
            if (this.valueSTR == null)
                this.valueSTR = this.valueDBL.toComplexNum();
            return new ExprNumber((Trigo.angleConvert(this.valueSTR, ori, target, mc)));
        }
    }

    public ExprNumber round(MathContext mc) {
        if (this.valueSTR == null)
            this.valueSTR = this.valueDBL.toComplexNum();
        return new ExprNumber(this.valueSTR.round(mc));
    }

    /**
     * Method to verify if an {@code ExprNumber} is a real number or not.
     * <p>
     * Due to the imprecision of the calculator, it happens that for
     * some scenario, the imaginary part of the result is not equal to
     * zero while it should.
     * <p>
     * Thus, to verify if a number is a real number, the real part and
     * the imaginary part of the function is compared.
     * <p>
     * For two numbers {@code a = p*10^v} and {@code b = q*10^w},
     * if {@code v-w > difference}, we say that the imaginary part is
     * equal to zero.
     * 
     * @param difference the minimum difference between the exponent of 10 of the
     *                   real part and the imaginary part to consider the imaginary
     *                   part as zero
     * @return if an {@code ExprNumber} number is a real number or not
     */
    public boolean isRealDBL(int difference) {
        if (this.toComplexDbl().getImaValue() == 0)
            return true;
        int realMag = Math.getExponent(this.toComplexDbl().getRealValue());
        int imaMag = Math.getExponent(this.toComplexDbl().getImaValue());
        final double LOG210 = 0.30102999566398;
        double minDifference = (difference-1)*LOG210;
        if (realMag - imaMag > minDifference) {
            return true;
        }
        return false;
    }
}
