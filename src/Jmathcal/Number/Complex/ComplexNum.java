package Jmathcal.Number.Complex;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Jmathcal.Expression.ExprNumber;
import Jmathcal.Number.Computable;
import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;

/**
 * Immutable, arbitrary-precision signed decimal complex numbers. A
 * {@code ComplexNum} consists of a real part and an imaginary part
 * each stored as a {@code BigDecimal}.
 * 
 * @author KineticJetIce245
 */
public class ComplexNum implements Serializable, Comparable<ComplexNum>, Computable<ComplexNum> {

    // Serialization
    @java.io.Serial
    private static final long serialVersionUID = -3266019408160301724L;

    /** Real part of this complex number */
    private final BigDecimal realValue;
    /** Imaginary part of this complex number */
    private final BigDecimal imaValue;

    /** The default {@code MathContext} of the class. */
    public static MathContext DEF_CONTEXT = new MathContext(32, RoundingMode.HALF_UP);

    public static ComplexNum I = new ComplexNum("0", "1");

    public static ComplexNum ZERO = new ComplexNum("0");

    public static ComplexNum ONE = new ComplexNum("1");

    /**
     * Additional precision for calculation.
     *
     * Default: {@code 10}
     */
    public static int PRECI = 10;

    /**
     * Constructs a new {@code ComplexNum} whose imaginary part is zero.
     *
     * @param realValue the real part of this.
     */
    public ComplexNum(BigDecimal realValue) {
        this.realValue = realValue;
        this.imaValue = BigDecimal.ZERO;
    }

    /**
     * Constructs a new {@code ComplexNum} whose real and imaginary part
     * are defined by <i>realValue</i> and <i>imaValue</i>.
     * 
     * @param realValue the real part of this.
     * @param imaValue  the imaginary part of this.
     */
    public ComplexNum(BigDecimal realValue, BigDecimal imaValue) {
        this.realValue = realValue;
        this.imaValue = imaValue;
    }

    /**
     * Constructs a new {@code ComplexNum} whose imaginary part is zero.
     * 
     * @param realValue the real part of this.
     */
    public ComplexNum(String realValue) {
        this(new BigDecimal(realValue));
    }

    /**
     * Constructs a new {@code ComplexNum} strictly represented by the following
     * form : {@value a+bi}, where {@code a} and {@code b} are {@code String} that can be
     * directly turned into {@code BigDecimal}.
     * 
     * @param complexValue
     */
    public ComplexNum(ExprNumber exprNumber) {
        StringBuffer complexValue = new StringBuffer(exprNumber.toString());
        Pattern numPattern = Pattern.compile("^(\\+|\\-)?\\d+(\\.\\d+)?(E(\\+|\\-)?\\d+)?");
        Matcher numMatcher = numPattern.matcher(complexValue);
        if (numMatcher.find()) {
            this.realValue = new BigDecimal(complexValue.substring(numMatcher.start(), numMatcher.end()));
            complexValue.delete(numMatcher.start(), numMatcher.end() + 1);
            complexValue.deleteCharAt(complexValue.length() - 1);
            this.imaValue = new BigDecimal(complexValue.toString());
        } else {
            throw new NumberFormatException();
        }
    }

    /**
     * Constructs a new {@code ComplexNum} whose real and imaginary part
     * are defined by <i>realValue</i> and <i>imaValue</i>.
     * 
     * @param realValue the real part of this.
     * @param imaValue  the imaginary part of this.
     */
    public ComplexNum(String realValue, String imaValue) {
        this(new BigDecimal(realValue), new BigDecimal(imaValue));
    }

    /**
     * Constructs a new {@code ComplexNum} by given modulus and argument.
     * The precision of the {@code ComplexNum} is the default precision.
     * 
     * @param rValue
     * @param phiValue
     * @return {@code ComplexNum}
     */
    public static ComplexNum getComplexNum(BigDecimal rValue, BigDecimal phiValue) {
        return getComplexNum(rValue, phiValue, DEF_CONTEXT);
    }

    /**
     * Constructs a new {@code ComplexNum} by given modulus and argument.
     * The precision of the {@code ComplexNum} is defined by {@code mc}.
     * 
     * @param rValue
     * @param phiValue
     * @param mc       number of significant figures and rounding mode
     * @return {@code ComplexNum}
     */
    public static ComplexNum getComplexNum(BigDecimal rValue, BigDecimal phiValue, MathContext mc) {
        BigDecimal realVal = rValue.multiply(Trigo.cos(phiValue, mc));
        BigDecimal imaVal = rValue.multiply(Trigo.sin(phiValue, mc));
        return new ComplexNum(realVal, imaVal);
    }

    /**
     * Returns the <i>realValue</i> of this {@code ComplexNum}.
     *
     * @return the real part of this {@code ComplexNum}.
     */
    public BigDecimal getRealValue() {
        return this.realValue;
    }

    /**
     * Returns the <i>imaValue</i> of this {@code ComplexNum}.
     *
     * @return the imaginary part of this {@code ComplexNum}.
     */
    public BigDecimal getImaValue() {
        return this.imaValue;
    }

    /**
     * Calculates the modulus of this complex number
     * with required precision.
     * 
     * @param mc
     * @return modulus of this
     */
    public BigDecimal calRValue(MathContext mc) {
        return this.abs(true, mc);
    }

    /**
     * Calculates the argument of this complex number
     * with required precision.
     * Spacial case : if real part and imaginary part are both
     * zero, it returns zero.
     * 
     * @param mc
     * @return argument of this
     */
    public BigDecimal calPhiValue(MathContext mc) {
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);

        if (realValue.compareTo(BigDecimal.ZERO) == 0) {
            if (imaValue.compareTo(BigDecimal.ZERO) == 0)
                return BigDecimal.ZERO;
            if (imaValue.compareTo(BigDecimal.ZERO) > 0)
                return Trigo.PI(calPrecision).divide(Trigo.TWO).round(mc);
            if (imaValue.compareTo(BigDecimal.ZERO) < 0)
                return Trigo.PI(calPrecision).multiply(new BigDecimal("1.5")).round(mc);
        }

        // y/x
        BigDecimal proportion = imaValue.divide(realValue, calPrecision).abs();
        // cases
        if (realValue.compareTo(BigDecimal.ZERO) == 1) {
            if (imaValue.compareTo(BigDecimal.ZERO) >= 0) {
                // arctan(y/x) = arctan(y/x)
                return Trigo.arctan(proportion, mc);
            } else {
                // arctan(-y/x) = -arctan(y/x)
                return Trigo.arctan(proportion, mc).negate();
            }
        } else {
            if (imaValue.compareTo(BigDecimal.ZERO) >= 0) {
                // arctan(y/-x) = pi - arctan(y/x)
                return Trigo.PI(calPrecision).subtract(Trigo.arctan(proportion, calPrecision)).round(mc);
            } else {
                // arctan(-y/-x) = arctan(y/x) - pi
                return Trigo.PI(calPrecision).subtract(Trigo.arctan(proportion, calPrecision)).round(mc).negate();
            }
        }
    }

    /**
     * Returns the value of (this + augend).
     * 
     * @param augend value to be added
     * @return {@code this + augend}
     */
    @Override
    public ComplexNum add(ComplexNum augend) {
        return new ComplexNum(this.realValue.add(augend.realValue),
                this.imaValue.add(augend.imaValue));
    }

    /**
     * Returns the value of (this - augend).
     * 
     * @param subtrahend value to be subtracted from the original value
     * @return {@code this - augend}
     */
    @Override
    public ComplexNum subtract(ComplexNum subtrahend) {
        return new ComplexNum(this.realValue.subtract(subtrahend.realValue),
                this.imaValue.subtract(subtrahend.imaValue));
    }

    /**
     * Returns the value of (this * augend).
     * 
     * @param multiplicand value to be multiplied
     * @return {@code this * augend}
     */
    @Override
    public ComplexNum multiply(ComplexNum multiplicand) {
        BigDecimal newRealVal = this.realValue.multiply(multiplicand.realValue)
                .subtract(this.imaValue.multiply(multiplicand.imaValue));
        BigDecimal newImaVal = this.realValue.multiply(multiplicand.imaValue)
                .add(this.imaValue.multiply(multiplicand.realValue));
        return new ComplexNum(newRealVal, newImaVal);
    }

    /**
     * Returns the value of (this / augend). The precision of the result is the
     * default precision.
     * 
     * @param divisor value divided from the original value.
     * @return {@code this / augend}
     */
    @Override
    public ComplexNum divide(ComplexNum divisor) {
        return this.divide(divisor, DEF_CONTEXT);
    }

    /**
     * Returns the value of (this / augend). The precision of the result is defined
     * by {@code mc}.
     * 
     * @param divisor value divided from the original value.
     * @param mc      number of significant figures and rounding mode
     * @return {@code this / augend}
     */
    public ComplexNum divide(ComplexNum divisor, MathContext mc) {
        if (divisor.imaValue.compareTo(BigDecimal.ZERO) == 0) {
            return new ComplexNum(
                    realValue.divide(divisor.realValue, mc),
                    imaValue.divide(divisor.realValue, mc));
        }
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        ComplexNum product = this.multiply(divisor.conjugate());
        BigDecimal divisorAbs = divisor.abs(false, calPrecision);
        return new ComplexNum(
                product.realValue.divide(divisorAbs, mc),
                product.imaValue.divide(divisorAbs, mc));
    }

    /**
     * Returns the negative value of this.
     * 
     * @return {@code -this}
     */
    public ComplexNum negate() {
        return new ComplexNum(realValue.negate(), imaValue.negate());
    }

    /**
     * Returns the negative value of this and round it with {@code mc}.
     * 
     * @param mc number of significant figures and rounding mode
     * @return {@code -this}
     */
    public ComplexNum negate(MathContext mc) {
        return new ComplexNum(realValue.negate().round(mc), imaValue.negate().round(mc));
    }

    /**
     * Multiplies this by <i>i</i>.
     * 
     * @param mc number of significant figures and rounding mode
     * @return {@code i*this}
     */
    public ComplexNum multiplyByI() {
        return new ComplexNum(imaValue.negate(), realValue);
    }

    /**
     * Multiplies this by <i>i</i>, and round the result with {@code mc}.
     * 
     * @param mc number of significant figures and rounding mode
     * @return {@code i*this}
     */
    public ComplexNum multiplyByI(MathContext mc) {
        return new ComplexNum(imaValue.negate().round(mc), realValue.round(mc));
    }

    /**
     * Return the value of {@code this^exponent}. The precision of the
     * result is the default precision.
     * 
     * @param exponent
     * @return {@code this^exponent}
     */
    public ComplexNum pow(ComplexNum exponent) {
        return Exp.pow(this, exponent, DEF_CONTEXT);
    }

    /**
     * Return the value of {@code this^exponent}. The precision of the
     * result is defined by {@code mc}.
     * 
     * @param exponent
     * @param mc       number of significant figures and rounding mode
     * @return {@code this^exponent}
     */
    public ComplexNum pow(ComplexNum exponent, MathContext mc) {
        return Exp.pow(this, exponent, mc);
    }

    /**
     * Return the value of {@code this^exponent}. The precision of the
     * result is the default precision.
     * 
     * @param exponent
     * @param mc       number of significant figures and rounding mode
     * @return {@code this^exponent}
     */
    public ComplexNum pow(String exponent) {
        return Exp.pow(this, new ComplexNum(exponent), DEF_CONTEXT);
    }

    /**
     * Return the value of {@code this^exponent}. The precision of the
     * result is defined by {@code mc}.
     * 
     * @param exponent
     * @param mc       number of significant figures and rounding mode
     * @return {@code this^exponent}
     */
    public ComplexNum pow(String exponent, MathContext mc) {
        return Exp.pow(this, new ComplexNum(exponent), mc);
    }

    /**
     * Returns the conjugate of this {@code ComplexNum}.
     * 
     * @return {@code conjugate of this}
     */
    public ComplexNum conjugate() {
        return new ComplexNum(realValue, imaValue.negate());
    }

    /**
     * Returns the conjugate of this {@code ComplexNum} and round
     * the result with {@code mc}.
     * 
     * @return {@code conjugate of this}
     */
    public ComplexNum conjugate(MathContext mc) {
        return new ComplexNum(realValue.round(mc), imaValue.negate().round(mc));
    }

    /**
     * Compares the absolute value of two {@code ComplexNum}.
     * 
     * @param o
     * @return a negative integer, zero, or a positive integer as the absolute value
     *         of this {@code ComplexNum} is less than, equal to, or greater than
     *         the absolute value of specified {@code ComplexNum}.
     * @see java.lang.Override
     */
    @Override
    public int compareTo(ComplexNum o) {
        return this.abs().compareTo(o.abs());
    }

    /**
     * Returns the absolute value of this {@code ComplexNum},
     * with the default precision.
     * 
     * @return {@code |this|} or {@code |this|^2}
     */
    public BigDecimal abs() {
        return this.abs(true, DEF_CONTEXT);
    }

    /**
     * Returns a {@code BigDecimal} which is the absolute value of this whose
     * precision is the default precision.
     * 
     * @param ifRoot if takes the root
     * @return {@code |this|} or {@code |this|^2}
     */
    public BigDecimal abs(boolean ifRoot) {
        return this.abs(ifRoot, DEF_CONTEXT);
    }

    /**
     * Returns a {@code BigDecimal} which is the absolute value of this whose
     * precision is defined by {@code mc}.
     * 
     * @param ifRoot if takes the root
     * @param mc     number of significant figures and rounding mode
     * @return {@code |this|} or {@code |this|^2}
     */
    public BigDecimal abs(boolean ifRoot, MathContext mc) {
        BigDecimal reVal = (realValue.pow(2).add(imaValue.pow(2)));
        return ifRoot ? (reVal).sqrt(mc) : reVal;
    }

    @Override
    public String toString() {
        return this.realValue.toString() + "+" + this.imaValue.toString() + "i";
    }

    /**
     * Returns a {@code ComplexNum} which is rounded according {@code mc}.
     * 
     * @param mc number of significant figures and rounding mode.
     * @return {@code this}
     */
    public ComplexNum round(MathContext mc) {
        return new ComplexNum(this.realValue.round(mc), this.imaValue.round(mc));
    }

    /**
     * Returns a {@code ComplexNum} whose scale (number of digits after decimal
     * separator) of the imaginary part and the real part is defined by
     * {@code mc}
     * 
     * @param mc number of digits after decimal separator and rounding mode
     * @return a {@code ComplexNum} whose scale is the specified value.
     */
    public ComplexNum scale(MathContext mc) {
        return new ComplexNum(this.realValue.setScale(mc.getPrecision(), mc.getRoundingMode()),
                this.imaValue.setScale(mc.getPrecision(), mc.getRoundingMode()));
    }

    public ComplexNum scaleByPowerOfTen(int factor) {
        return new ComplexNum(this.realValue.scaleByPowerOfTen(factor), this.imaValue.scaleByPowerOfTen(factor));
    }

    /**
     * Converts a {@code ComplexNum} to a {@code ComplexDbl}.
     * 
     * @return a {@code ComplexDbl} converted from this {@code ComplexNum}
     */
    public ComplexDbl toComplexDbl() {
        return new ComplexDbl(this.realValue.doubleValue(), this.imaValue.doubleValue());
    }

}