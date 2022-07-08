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
 * {@code ComplexNumber} consists of a real part and an imaginary part
 * each stored as a {@code BigDecimal}.
 */
public class ComplexNum implements Serializable, Comparable<ComplexNum>, Computable<ComplexNum> {

    // Serialization
    @java.io.Serial
    private static final long serialVersionUID = -3266019408160301724L;

    private final BigDecimal realValue;
    private final BigDecimal imaValue;

    private final BigDecimal rValue;
    private final BigDecimal phiValue;

    public static ComplexNum I = new ComplexNum("0", "1", 32);
    public static int PRECI = 32;

    public ComplexNum(BigDecimal realValue) {
        this.realValue = realValue;
        this.imaValue = BigDecimal.ZERO;
        this.rValue = realValue;
        this.phiValue = BigDecimal.ZERO;
    }

    public ComplexNum(BigDecimal realValue, BigDecimal imaValue, int precision) {
        this.realValue = realValue;
        this.imaValue = imaValue;
        this.rValue = this.abs(true, precision);
        this.phiValue = calPhiValue(precision);
    }

    public ComplexNum(String realValue) {
        this(new BigDecimal(realValue));
    }

    public ComplexNum(String realValue, String imaValue, int precision) {
        this(new BigDecimal(realValue), new BigDecimal(imaValue), precision);
    }

    public BigDecimal getRealValue() {
        return this.realValue;
    }

    public BigDecimal getImaValue() {
        return this.imaValue;
    }

    public BigDecimal getRValue() {
        return this.rValue;
    }

    public BigDecimal getPhiValue() {
        return this.phiValue;
    }

    public BigDecimal calRValue(int precision) {
        return this.abs(true, precision);
    }

    public BigDecimal calPhiValue(int precision) {
        // zero test
        if (realValue.compareTo(BigDecimal.ZERO) == 0) {
            if (imaValue.compareTo(BigDecimal.ZERO) <= 0)
                return Trigo.PI(precision).divide(Trigo.TWO);
            if (imaValue.compareTo(BigDecimal.ZERO) > 0)
                return Trigo.PI(precision).multiply(new BigDecimal("1.5"));
        }

        // y/x
        BigDecimal proportion = imaValue.divide(realValue, precision + 10, RoundingMode.HALF_UP).abs();
        // cases
        if (realValue.compareTo(BigDecimal.ZERO) == 1) {
            if (imaValue.compareTo(BigDecimal.ZERO) >= 0) {
                // arctan(y/x) = arctan(y/x)
                return Trigo.arctan(proportion, precision);
            } else {
                // arctan(-y/x) = -arctan(y/x)
                return Trigo.arctan(proportion, precision).negate();  
            }
        } else {
            if (imaValue.compareTo(BigDecimal.ZERO) >= 0) {
                // arctan(y/-x) = pi - arctan(y/x)
                return Trigo.PI(precision).subtract(Trigo.arctan(proportion, precision));
            } else {
                // arctan(-y/-x) = arctan(y/x) - pi
                return Trigo.PI(precision).subtract(Trigo.arctan(proportion, precision)).negate();
            }
        }
    }

    @Override
    public ComplexNum add(ComplexNum augend) {
        return this.add(augend, PRECI);
    }
    public ComplexNum add(ComplexNum augend, int precision) {
        return new ComplexNum(this.realValue.add(augend.realValue),
                this.imaValue.add(augend.imaValue), precision);
    }

    @Override
    public ComplexNum subtract(ComplexNum subtrahend) {
        return this.subtract(subtrahend, PRECI);
    }
    public ComplexNum subtract(ComplexNum subtrahend, int precision) {
        return new ComplexNum(this.realValue.subtract(subtrahend.realValue),
                this.imaValue.subtract(subtrahend.imaValue), precision);
    }

    @Override
    public ComplexNum multiply(ComplexNum multiplicand) {
        return this.multiply(multiplicand, PRECI);
    }
    public ComplexNum multiply(ComplexNum multiplicand, int precision) {
        BigDecimal newRealVal = this.realValue.multiply(multiplicand.realValue)
                .subtract(this.imaValue.multiply(multiplicand.imaValue));
        BigDecimal newImaVal = this.realValue.multiply(multiplicand.imaValue)
                .add(this.imaValue.multiply(multiplicand.realValue));
        return new ComplexNum(newRealVal, newImaVal, precision);
    }

    @Override
    public ComplexNum divide(ComplexNum divisor) {
        return this.divide(divisor, PRECI);
    }
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

    public ComplexNum negate() {
        return new ComplexNum(realValue.negate(), imaValue.negate(), PRECI);
    }
    public ComplexNum negate(int precision) {
        return new ComplexNum(realValue.negate(), imaValue.negate(), precision);
    }

    public ComplexNum multiplyByI() {
        return new ComplexNum(imaValue.negate(), realValue, PRECI);
    }
    public ComplexNum multiplyByI(int precision) {
        return new ComplexNum(imaValue.negate(), realValue, precision);
    }

    /**
     * Return the value of {@code this^exponent}.
     * @param exponent
     * @return {@code this^exponent}
     */
    public ComplexNum pow(ComplexNum exponent) {
        return Exp.pow(this, exponent, PRECI);
    }

    /**
     * Return the value of {@code this^exponent}.
     * @param exponent
     * @param precision : the precision of the result
     * @return {@code this^exponent}
     */
    public ComplexNum pow(ComplexNum exponent, int precision) {
        return Exp.pow(this, exponent, precision);
    }

    /**
     * Returns the conjugate of this {@code ComplexNum}
     * 
     * @return {@code conjugate of this}
     */
    public ComplexNum conjugate() {
        return new ComplexNum(realValue, imaValue.negate(), PRECI);
    }

    public ComplexNum conjugate(int precision) {
        return new ComplexNum(realValue, imaValue.negate(), precision);
    }

    @Override
    public int compareTo(ComplexNum o) {
        return this.abs().compareTo(o.abs());
    }

    /**
     * Returns the absolute value of this {@code ComplexNum},
     * with 32 precision.
     * @return {@code |this|} or {@code |this|^2}
     */
    public BigDecimal abs() {
        return this.abs(true, PRECI);
    }

    /**
     * Returns the absolute value of this {@code ComplexNum}
     * with 32 precision.
     * @param ifRoot : if takes the root
     * @return {@code |this|} or {@code |this|^2}
     */
    public BigDecimal abs(boolean ifRoot) {
        return this.abs(ifRoot, PRECI);
    }

    /**
     * Returns the absolute value of this {@code ComplexNum}.
     * @param ifRoot : if takes the root
     * @return {@code |this|}
     */
    public BigDecimal abs(boolean ifRoot, int precision) {
        BigDecimal reVal = (realValue.pow(2).add(imaValue.pow(2)));
        return ifRoot ? (reVal).sqrt(new MathContext(precision + 1, RoundingMode.HALF_UP)) : reVal;
    }

    @Override
    public String toString() {
        return this.realValue.toString() + " + " + this.imaValue.toString() + "i";
    }

    public ComplexNum round(MathContext mc) {
        return new ComplexNum(this.realValue.round(mc), this.imaValue.round(mc), mc.getPrecision());
    }

}