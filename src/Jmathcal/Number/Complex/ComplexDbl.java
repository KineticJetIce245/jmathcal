package Jmathcal.Number.Complex;

import java.io.Serializable;

import Jmathcal.Expression.ExprElements;
import Jmathcal.Number.Computable;
/**
 * A {@code ComplexDbl} consists of a real part and an imaginary part
 * each stored as a {@code double}.
 * 
 * @author KineticJetIce245
 */
public class ComplexDbl implements Serializable, Comparable<ComplexDbl>, Computable<ComplexDbl>, ExprElements {

    // Serialization
    @java.io.Serial
    private static final long serialVersionUID = 4333326305403942918L;
    /** Real part of this complex number */
    private final double realValue;
    /** Imaginary part of this complex number */
    private final double imaValue;

    /** Modulus of this complex number */
    private final double rValue;
    /** Argument of this complex number */
    private final double phiValue;

    public static final ComplexDbl ZERO = new ComplexDbl(0.0);
    public static final ComplexDbl ONE = new ComplexDbl(1.0);
    public static final ComplexDbl I = new ComplexDbl(0.0, 1.0);

    /**
     * Constructs a new {@code ComplexDbl} whose imaginary part is zero.
     *
     * @param realValue the real part of this.
     */
    public ComplexDbl(double realValue) {
        this.realValue = realValue;
        this.imaValue = 0.0;
        this.rValue = this.realValue;
        this.phiValue = 0.0;
    }

    /**
     * Constructs a new {@code ComplexDbl} whose real and imaginary part
     * are defined by <i>realValue</i> and <i>imaValue</i>.
     * 
     * @param realValue the real part of this.
     * @param imaValue  the imaginary part of this.
     */
    public ComplexDbl(double realValue, double imaValue) {
        this.realValue = realValue;
        this.imaValue = imaValue;
        this.rValue = this.abs(true);
        this.phiValue = this.calPhiValue();
    }

    /**
     * Constructs a new {@code ComplexDbl} by given modulus and argument.
     * The precision of the {@code ComplexDbl} is the default precision.
     * 
     * @param rValue   modulus
     * @param phiValue argument
     * @return {@code ComplexDbl}
     */
    public static ComplexDbl getComplexDbl(double rValue, double phiValue) {
        return new ComplexDbl(rValue * Math.cos(phiValue), rValue * Math.sin(phiValue));
    }

    /**
     * Returns the <i>realValue</i> of this {@code ComplexDbl}.
     *
     * @return the real part of this {@code ComplexDbl}.
     */
    public double getRealValue() {
        return this.realValue;
    }

    /**
     * Returns the <i>imaValue</i> of this {@code ComplexDbl}.
     *
     * @return the imaginary part of this {@code ComplexDbl}.
     */
    public double getImaValue() {
        return this.imaValue;
    }

    /**
     * Returns the modulus of this complex number.
     * 
     * @return modulus of this
     */
    public double getRValue() {
        return this.rValue;
    }

    /**
     * Returns the modulus of this complex number.
     * 
     * @return modulus of this
     */
    public double getPhiValue() {
        return this.phiValue;
    }

    private double calPhiValue() {
        if (this.realValue == 0.0) {
            if (imaValue == 0.0)
                return 0.0;
            if (imaValue < 0)
                return Math.PI * 0.5;
            if (imaValue > 0)
                return Math.PI * 1.5;
        }

        double proportion = Math.abs(imaValue / realValue);
        if (realValue > 0) {
            if (imaValue > 0) {
                // arctan(y/x) = arctan(y/x)
                return Math.atan(proportion);
            } else {
                // arctan(-y/x) = -arctan(y/x)
                return -Math.atan(proportion);
            }
        } else {
            if (imaValue > 0) {
                // arctan(y/-x) = pi - arctan(y/x)
                return Math.PI - Math.atan(proportion);
            } else {
                // arctan(-y/-x) = arctan(y/x) - pi
                return Math.atan(proportion) - Math.PI;
            }
        }
    }

    @Override
    public ComplexDbl add(ComplexDbl augend) {
        return new ComplexDbl(this.realValue + augend.realValue, this.imaValue + augend.imaValue);
    }

    @Override
    public ComplexDbl subtract(ComplexDbl subtrahend) {
        return new ComplexDbl(this.realValue - subtrahend.realValue, this.imaValue - subtrahend.imaValue);
    }

    @Override
    public ComplexDbl multiply(ComplexDbl multiplicand) {
        return new ComplexDbl(
                this.realValue * multiplicand.realValue - this.imaValue * multiplicand.imaValue,
                this.imaValue * multiplicand.realValue + this.realValue * multiplicand.imaValue);
    }

    @Override
    public ComplexDbl divide(ComplexDbl divisor) {
        ComplexDbl product = this.multiply(divisor.conjugate());
        double divisorAbs = divisor.abs(false);
        return new ComplexDbl(product.realValue / divisorAbs, product.imaValue / divisorAbs);
    }

    /**
     * Returns the negative value of this.
     * 
     * @return {@code -this}
     */
    public ComplexDbl negate() {
        return new ComplexDbl(-realValue, -imaValue);
    }

    /**
     * Returns the conjugate of this {@code ComplexDbl}.
     * 
     * @return {@code conjugate of this}
     */
    public ComplexDbl conjugate() {
        return new ComplexDbl(realValue, -imaValue);
    }

    /**
     * Multiplies this by <i>i</i>.
     * 
     * @return {@code i*this}
     */
    public ComplexDbl multiplyByI() {
        return new ComplexDbl(-imaValue, realValue);
    }

    /**
     * Returns a {@code double} which is the absolute value of this.
     * 
     * @param ifRoot if takes the root
     * @return {@code |this|} or {@code |this|^2}
     */
    public double abs(boolean ifRoot) {
        double reVal = realValue * realValue + imaValue * imaValue;
        return ifRoot ? Math.sqrt(reVal) : reVal;
    }

    /**
     * Compares the absolute value of two {@code ComplexDbl}.
     * 
     * @param o
     * @return a negative integer, zero, or a positive integer as the absolute value
     *         of this {@code ComplexDbl} is less than, equal to, or greater than
     *         the absolute value of specified {@code ComplexDbl}.
     * @see java.lang.Override
     */
    @Override
    public int compareTo(ComplexDbl o) {
        return Double.valueOf(this.abs(false)).compareTo(Double.valueOf(o.abs(false)));
    }

    @Override
    public String toString() {
        return String.valueOf(realValue) + " + " + String.valueOf(imaValue) + "i";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(imaValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(realValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ComplexDbl other = (ComplexDbl) obj;
        if (Double.doubleToLongBits(imaValue) != Double.doubleToLongBits(other.imaValue))
            return false;
        if (Double.doubleToLongBits(realValue) != Double.doubleToLongBits(other.realValue))
            return false;
        return true;
    }

}
