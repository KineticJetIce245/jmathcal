package Jmathcal.Number.Complex;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import Jmathcal.Number.Computable;
import Jmathcal.Number.Function.Exp;
import Jmathcal.Number.Function.Trigo;

/**
 * Immutable, arbitrary-precision signed decimal complex numbers. A
 * {@code ComplexNum} consists of a real part and an imaginary part
 * each stored as a {@code BigDecimal}.
 * <p>
 * The modulus and the argument of a {@code ComplexNum} are calculated and
 * stored when the object is constructed, as <i>rValue</i> and <i>phiValue</i>.
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

    /** Modulus of this complex number */
    private final BigDecimal rValue;
    /** Argument of this complex number */
    private final BigDecimal phiValue;
    
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
        this.rValue = realValue;
        this.phiValue = BigDecimal.ZERO;
    }

    /**
     * Constructs a new {@code ComplexNum}, modulus and argument are calculated
     * with the default precision.
     * 
     * @param realValue the real part of this.
     * @param imaValue  the imaginary part of this.
     */
    public ComplexNum(BigDecimal realValue, BigDecimal imaValue) {
        this(realValue, imaValue, DEF_CONTEXT);
    }

    /**
     * Constructs a new {@code ComplexNum}, modulus and argument are calculated
     * with the default precision.
     * 
     * @param realValue the real part of this.
     * @param imaValue  the imaginary part of this.
     * @param precision the precision of modulus and argument
     */
    public ComplexNum(BigDecimal realValue, BigDecimal imaValue, int precision) {
        this(realValue, imaValue, new MathContext(precision));
    }

    /**
     * Constructs a new {@code ComplexNum}, modulus and argument are calculated
     * with the required precision.
     * 
     * @param realValue the real part of this.
     * @param imaValue  the imaginary part of this.
     * @param mc        number of significant figures and rounding mode of modulus
     *                  and argument
     */
    public ComplexNum(BigDecimal realValue, BigDecimal imaValue, MathContext mc) {
        this.realValue = realValue;
        this.imaValue = imaValue;
        this.rValue = this.abs(true, mc);
        this.phiValue = this.calPhiValue(mc);
    }

    /**
     * Constructs a new {@code ComplexNum}, modulus and argument are calculated
     * with the default precision.
     * 
     * @param realValue the real part of this.
     */
    public ComplexNum(String realValue) {
        this(new BigDecimal(realValue));
    }

    /**
     * Constructs a new {@code ComplexNum}, modulus and argument are calculated
     * with the default precision.
     * 
     * @param realValue the real part of this.
     * @param imaValue  the imaginary part of this.
     */
    public ComplexNum(String realValue, String imaValue) {
        this(new BigDecimal(realValue), new BigDecimal(imaValue));
    }

    /**
     * Constructs a new {@code ComplexNum}, modulus and argument are calculated
     * with the required precision.
     * 
     * @param realValue the real part of this.
     * @param imaValue  the imaginary part of this.
     * @param precision the precision of modulus and argument
     */
    public ComplexNum(String realValue, String imaValue, int precision) {
        this(new BigDecimal(realValue), new BigDecimal(imaValue), precision);
    }

    /**
     * Constructs a new {@code ComplexNum}, modulus and argument are calculated
     * with the required precision.
     * 
     * @param realValue the real part of this.
     * @param imaValue  the imaginary part of this.
     * @param mc        number of significant figures and rounding mode of modulus
     *                  and argument
     */
    public ComplexNum(String realValue, String imaValue, MathContext mc) {
        this(new BigDecimal(realValue), new BigDecimal(imaValue), mc);
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
     * Returns the <i>rValue</i> calculated when this is constructed of this
     * {@code ComplexNum}.
     *
     * @return the modulus of this {@code ComplexNum}.
     */
    public BigDecimal getRValue() {
        return this.rValue;
    }

    /**
     * Returns the <i>phiValue</i> calculated when this is constructed of this
     * {@code ComplexNum}.
     * 
     * @return the argument of this {@code ComplexNum}.
     */
    public BigDecimal getPhiValue() {
        return this.phiValue;
    }

    /**
     * Calculates the modulus of this complex number
     * with required precision.
     * 
     * @param precision
     * @return modulus of this
     */
    public BigDecimal calRValue(int precision) {
        return this.abs(true, precision);
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
     * 
     * @param precision
     * @return argument of this
     */
    public BigDecimal calPhiValue(int precision) {
        return this.calPhiValue(new MathContext(precision));
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
            if (imaValue.compareTo(BigDecimal.ZERO) < 0)
                return Trigo.PI(calPrecision).divide(Trigo.TWO).round(mc);
            if (imaValue.compareTo(BigDecimal.ZERO) > 0)
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
     * Returns the value of (this + augend). The precision of the modulus and the
     * argument of the result is the default precision.
     * 
     * @param augend value to be added
     * @return {@code this + augend}
     */
    @Override
    public ComplexNum add(ComplexNum augend) {
        return this.add(augend, DEF_CONTEXT);
    }

    /**
     * Returns the value of (this + augend). The precision of the modulus and the
     * argument of the result is defined by {@code precision}.
     * 
     * @param augend    value to be added
     * @param precision number of significant figures
     * @return {@code this + augend}
     */
    public ComplexNum add(ComplexNum augend, int precision) {
        return this.add(augend, new MathContext(precision));
    }

    /**
     * Returns the value of (this + augend). The precision of the modulus and the
     * argument of the result is defined by {@code mc}.
     * 
     * @param augend value to be added
     * @param mc     number of significant figures and rounding mode
     * @return {@code this + augend}
     */
    public ComplexNum add(ComplexNum augend, MathContext mc) {
        return new ComplexNum(this.realValue.add(augend.realValue),
                this.imaValue.add(augend.imaValue), mc);
    }

    /**
     * Returns the value of (this - augend). The precision of the modulus and the
     * argument of the result is the default precision.
     * 
     * @param subtrahend value to be subtracted from the original value
     * @return {@code this - augend}
     */
    @Override
    public ComplexNum subtract(ComplexNum subtrahend) {
        return this.subtract(subtrahend, DEF_CONTEXT);
    }

    /**
     * Returns the value of (this - augend). The precision of the modulus and the
     * argument of the result is defined by {@code precision}.
     * 
     * @param subtrahend value to be subtracted from the original value
     * @param precision  number of significant figures
     * @return {@code this - augend}
     */
    public ComplexNum subtract(ComplexNum subtrahend, int precision) {
        return this.subtract(subtrahend, new MathContext(precision));
    }

    /**
     * Returns the value of (this - augend). The precision of the modulus and the
     * argument of the result is defined by {@code mc}.
     * 
     * @param subtrahend value to be subtracted from the original value
     * @param mc         number of significant figures and rounding mode
     * @return {@code this - augend}
     */
    public ComplexNum subtract(ComplexNum subtrahend, MathContext mc) {
        return new ComplexNum(this.realValue.subtract(subtrahend.realValue),
                this.imaValue.subtract(subtrahend.imaValue), mc);
    }

    /**
     * Returns the value of (this * augend). The precision of the modulus and the
     * argument of the result is default precision.
     * 
     * @param multiplicand value to be multiplied
     * @return {@code this * augend}
     */
    @Override
    public ComplexNum multiply(ComplexNum multiplicand) {
        return this.multiply(multiplicand, DEF_CONTEXT);
    }

    /**
     * Returns the value of (this * augend). The precision of the modulus and the
     * argument of the result is defined by {@code precision}.
     * 
     * @param multiplicand value to be multiplied
     * @param precision    number of significant figures
     * @return {@code this * augend}
     */
    public ComplexNum multiply(ComplexNum multiplicand, int precision) {
        return this.multiply(multiplicand, new MathContext(precision));
    }

    /**
     * Returns the value of (this * augend). The precision of the modulus and the
     * argument of the result is defined by {@code mc}.
     * 
     * @param multiplicand value to be multiplied
     * @param mc           number of significant figures
     * @return {@code this * augend}
     */
    public ComplexNum multiply(ComplexNum multiplicand, MathContext mc) {
        BigDecimal newRealVal = this.realValue.multiply(multiplicand.realValue)
                .subtract(this.imaValue.multiply(multiplicand.imaValue));
        BigDecimal newImaVal = this.realValue.multiply(multiplicand.imaValue)
                .add(this.imaValue.multiply(multiplicand.realValue));
        return new ComplexNum(newRealVal, newImaVal, mc);
    }

    /**
     * Returns the value of (this / augend). The precision of the modulus and the
     * argument of the result and the result itself is the default precision.
     * 
     * @param divisor value divided from the original value.
     * @return {@code this * augend}
     */
    @Override
    public ComplexNum divide(ComplexNum divisor) {
        return this.divide(divisor, DEF_CONTEXT);
    }

    /**
     * Returns the value of (this / augend). The precision of the modulus and the
     * argument of the result and the result itself is defined by {@code precision}.
     * 
     * @param divisor   value divided from the original value.
     * @param precision number of significant figures
     * @return {@code this * augend}
     */
    public ComplexNum divide(ComplexNum divisor, int precision) {
        ComplexNum product = this.multiply(divisor.conjugate());
        BigDecimal divisorAbs = divisor.abs(false, precision + 10);
        return new ComplexNum(
                product.realValue
                        .divide(divisorAbs, precision, RoundingMode.HALF_UP),
                product.imaValue
                        .divide(divisorAbs, precision, RoundingMode.HALF_UP),
                precision);
    }

    /**
     * Returns the value of (this / augend). The precision of the modulus and the
     * argument of the result and the result itself is defined by {@code mc}.
     * 
     * @param divisor value divided from the original value.
     * @param mc      number of significant figures and rounding mode
     * @return {@code this * augend}
     */
    public ComplexNum divide(ComplexNum divisor, MathContext mc) {
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        ComplexNum product = this.multiply(divisor.conjugate());
        BigDecimal divisorAbs = divisor.abs(false, calPrecision);
        return new ComplexNum(
                product.realValue
                        .divide(divisorAbs, mc),
                product.imaValue
                        .divide(divisorAbs, mc),
                mc);
    }

    /**
     * Returns the negative value of this. The precision of the modulus and the
     * argument of the result is the default precision.
     * 
     * @return {@code -this}
     */
    public ComplexNum negate() {
        return new ComplexNum(realValue.negate(), imaValue.negate(), DEF_CONTEXT);
    }

    /**
     * Returns the negative value of this. The precision of the modulus and the
     * argument of the result is defined by {@code precision}.
     * 
     * @param precision number of significant figures
     * @return {@code -this}
     */
    public ComplexNum negate(int precision) {
        return new ComplexNum(realValue.negate(), imaValue.negate(), precision);
    }

    /**
     * Returns the negative value of this. The precision of the modulus and the
     * argument of the result is defined by {@code mc}.
     * 
     * @param mc number of significant figures and rounding mode
     * @return {@code -this}
     */
    public ComplexNum negate(MathContext mc) {
        return new ComplexNum(realValue.negate(), imaValue.negate(), mc);
    }

    public ComplexNum multiplyByI() {
        return new ComplexNum(imaValue.negate(), realValue, DEF_CONTEXT);
    }

    public ComplexNum multiplyByI(int precision) {
        return new ComplexNum(imaValue.negate(), realValue, precision);
    }

    public ComplexNum multiplyByI(MathContext mc) {
        return new ComplexNum(imaValue.negate(), realValue, mc);
    }

    /**
     * Return the value of {@code this^exponent}.
     * 
     * @param exponent
     * @return {@code this^exponent}
     */
    public ComplexNum pow(ComplexNum exponent) {
        return Exp.pow(this, exponent, DEF_CONTEXT);
    }

    /**
     * Return the value of {@code this^exponent}.
     * 
     * @param exponent
     * @param precision number of significant figures
     * @return {@code this^exponent}
     */
    public ComplexNum pow(ComplexNum exponent, int precision) {
        return Exp.pow(this, exponent, precision);
    }

    public ComplexNum pow(ComplexNum exponent, MathContext mc) {
        return Exp.pow(this, exponent, mc);
    }

    /**
     * Returns the conjugate of this {@code ComplexNum}
     * 
     * @return {@code conjugate of this}
     */
    public ComplexNum conjugate() {
        return new ComplexNum(realValue, imaValue.negate(), DEF_CONTEXT);
    }

    /**
     * Returns the conjugate of this {@code ComplexNum}
     * 
     * @return {@code conjugate of this}
     */
    public ComplexNum conjugate(int precision) {
        return new ComplexNum(realValue, imaValue.negate(), precision);
    }

    /**
     * Returns the conjugate of this {@code ComplexNum}
     * 
     * @return {@code conjugate of this}
     */
    public ComplexNum conjugate(MathContext mc) {
        return new ComplexNum(realValue, imaValue.negate(), mc);
    }

    @Override
    public int compareTo(ComplexNum o) {
        return this.abs().compareTo(o.abs());
    }

    /**
     * Returns the absolute value of this {@code ComplexNum},
     * with 32 precision.
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
     * precision is defined by {@code precision}.
     * 
     * @param ifRoot    if takes the root
     * @param precision number of significant figures
     * @return {@code |this|} or {@code |this|^2}
     */
    public BigDecimal abs(boolean ifRoot, int precision) {
        return this.abs(ifRoot, new MathContext(precision));
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
        return this.realValue.toString() + " + " + this.imaValue.toString() + "i";
    }

    /**
     * Returns a {@code ComplexNum} which is rounded according {@code mc}.
     * 
     * @param mc number of significant figures and rounding mode.
     * @return {@code this}
     */
    public ComplexNum round(MathContext mc) {
        return new ComplexNum(this.realValue.round(mc), this.imaValue.round(mc), mc);
    }

    /**
     * Returns a {@code ComplexNum} whose scale (number of digits after decimal
     * separator) of the imaginary part and the real part is defined by
     * {@code precision}
     * 
     * @param precision number of digits after decimal separator
     * @return a {@code ComplexNum} whose scale is the specified value.
     * @see java.math.BigDecimal.setScale
     * @apiNote (From java.math.BigDecimal.setScale): Since {@code ComplexNum}
     *          objects are immutable, calls of this method do not result in the
     *          original object being modified.
     */
    public ComplexNum scale(int precision) {
        return scale(new MathContext(precision));
    }

    /**
     * Returns a {@code ComplexNum} whose scale (number of digits after decimal
     * separator) of the imaginary part and the real part is defined by
     * {@code precision}
     * 
     * @param mc number of digits after decimal separator and rounding mode
     * @return a {@code ComplexNum} whose scale is the specified value.
     * @see java.math.BigDecimal.setScale
     * @apiNote (From java.math.BigDecimal.setScale): Since {@code ComplexNum}
     *          objects are immutable, calls of this method do not result in the
     *          original object being modified.
     */
    public ComplexNum scale(MathContext mc) {
        return new ComplexNum(this.realValue.setScale(mc.getPrecision(), mc.getRoundingMode()),
                this.imaValue.setScale(mc.getPrecision(), mc.getRoundingMode()), mc);
    }

}