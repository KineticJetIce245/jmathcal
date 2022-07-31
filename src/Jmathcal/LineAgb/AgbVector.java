package Jmathcal.LineAgb;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

import Jmathcal.Number.Function.Trigo;

/**
 * The {@code AgbVector} class wraps a {@code BigDecimal} array.
 * <p>
 * This class provides methods implementing some basic operations of vector in
 * linear algebra.
 * <p>
 * An object of type {@code AgbVector} is immutable if none of the elements in
 * its array is {@code null}.
 * 
 * @author KineticJetIce245
 */
public class AgbVector {

    /**
     * Additional precision for calculation.
     * <p>
     * Default: {@code 10}
     */
    public static int PRECI = 10;

    private final BigDecimal[] values;

    /**
     * Constructs an {@code AgbVector} with specified length.
     * <p>
     * To add value to an empty position, use {@code addValue} method.
     * 
     * @param length
     */
    public AgbVector(int length) {
        values = new BigDecimal[length];
    }

    /**
     * Constructs an {@code AgbVector} using a an array of {@code BigDecimal}.
     * 
     * @param values
     */
    public AgbVector(BigDecimal[] values) {
        this.values = new BigDecimal[values.length];
        for (int i = 0; i < values.length; i++) {
            this.values[i] = values[i];
        }
    }

    /**
     * Constructs an {@code AgbVector} using a an array of {@code String}.
     * 
     * @param values
     */
    public AgbVector(String[] values) {
        this.values = new BigDecimal[values.length];
        for (int i = 0; i < values.length; i++) {
            this.values[i] = new BigDecimal(values[i]);
        }
    }

    /**
     * Returns the length of this {@code AgbVector}.
     * 
     * @return the length of the array that {@code this} contains
     */
    public int getLength() {
        return values.length;
    }

    /**
     * Returns the element at the specified position in this {@code AgbVector}.
     * 
     * @param index
     * @return the element at the specified position in this vector
     */
    public BigDecimal getValue(int index) {
        return values[index];
    }

    /**
     * Adds elements to the specified empty position in this {@code AgbVector}.
     * 
     * @param val   element to be added
     * @param index
     * @throws IllegalArgumentException if there is already an element at the
     *                                  specified position.
     */
    public void addValue(BigDecimal val, int index) {
        if (this.values[index] != null) {
            throw new IllegalArgumentException("the specified position is already filled.");
        }
        values[index] = val;
    }

    /**
     * Adds elements to the specified empty position in this {@code AgbVector}.
     * 
     * @param val   element to be added
     * @param index
     * @throws IllegalArgumentException if there is already an element at the
     *                                  specified position.
     */
    public void addValue(String val, int index) {
        if (this.values[index] != null) {
            throw new IllegalArgumentException("the specified position is already filled.");
        }
        values[index] = new BigDecimal(val);
    }

    // Number operations
    /**
     * Returns an {@code AgbVector}, each of whose elements is equal to the sum of
     * the corresponding element in {@code this} and {@code augend}.
     * 
     * @param augend
     * @return {@code augend + this}
     * @throws IncompleteVectorOrMatrixException if {@code this} is incomplete.
     */
    public AgbVector add(BigDecimal augend) {

        if (!this.ifComplete())
            throw new ArithmeticException("doing addition on incomplete vector.");

        AgbVector returnVector = new AgbVector(values.length);

        for (int i = 0; i < values.length; i++) {
            returnVector.addValue(this.values[i].add(augend), i);
        }

        return returnVector;
    }

    /**
     * Returns an {@code AgbVector}, the result of the product of {@code this}, a
     * vector and {@code multiplicand}, a number.
     *
     * @param multiplicand number to be multiplied to {@code this}'s elements.
     * @return {@code multiplicand * this}
     * @throws IncompleteVectorOrMatrixException if {@code this} is incomplete.
     */
    public AgbVector multiply(BigDecimal multiplicand) {

        if (!this.ifComplete())
            throw new ArithmeticException("doing multiplication on incomplete vector.");

        AgbVector returnVector = new AgbVector(values.length);

        for (int i = 0; i < values.length; i++) {
            returnVector.addValue(values[i].multiply(multiplicand), i);
        }

        return returnVector;
    }

    /**
     * Returns the result of the dot product of {@code this} and a vector having the
     * same size of {@code this} whose elements are all equal to {@code scalar}.
     * 
     * @param scalar
     * @return {@code (scalar, ..., scalar) · this}
     * @throws IncompleteVectorOrMatrixException if {@code this} is incomplete.
     */
    public BigDecimal dotProduct(BigDecimal scalar) {
        if (!this.ifComplete())
            throw new ArithmeticException("doing dot product on incomplete vector.");

        return AgbVector.getZeroVector(this.getLength()).add(scalar).dotProduct(this);
    }

    // Vector operations
    /**
     * Returns an {@code AgbVector}, a vector formed by {@code (this +
     * augend)}.
     *
     * @param augend value to be added to this {@code Vector}.
     * @return {@code this + augend}
     * @throws IncompleteVectorOrMatrixException if any of the two vectors is
     *                                           incomplete.
     * @throws ArithmeticException               if the two vectors don't have the
     *                                           same length.
     */
    public AgbVector add(AgbVector augend) {

        if ((!this.ifComplete()) || (!augend.ifComplete()))
            throw new IncompleteVectorOrMatrixException("doing addition on incomplete vectors.");
        if (!this.ifSameSize(augend))
            throw new ArithmeticException("doing addition on vectors of different length.");

        AgbVector returnVector = new AgbVector(values.length);

        for (int i = 0; i < values.length; i++) {
            returnVector.addValue(this.values[i].add(augend.values[i]), i);
        }

        return returnVector;
    }

    /**
     * Returns an {@code AgbVector}, a vector formed by {@code (this -
     * subtrahend)}.
     *
     * @param subtrahend value to be subtracted from this {@code AgbVector}.
     * @return {@code this - subtrahend}
     * @throws IncompleteVectorOrMatrixException if any of the two vectors is
     *                                           incomplete.
     * @throws ArithmeticException               if the two vectors don't have the
     *                                           same length.
     */
    public AgbVector subtract(AgbVector subtrahend) {

        if ((!this.ifComplete()) || (!subtrahend.ifComplete()))
            throw new ArithmeticException("doing subtraction on incomplete vectors.");
        if (!this.ifSameSize(subtrahend))
            throw new ArithmeticException("doing subtraction on vectors of different size.");

        AgbVector returnVector = new AgbVector(values.length);

        for (int i = 0; i < values.length; i++) {
            returnVector.addValue(this.values[i].subtract(subtrahend.values[i]), i);
        }

        return returnVector;
    }

    /**
     * Returns an {@code AgbVector}, the Hadamard Product of {@code this} and
     * {@code multiplicand}.
     * 
     * @param multiplicand
     * @return {@code multiplicand ⊙ this}
     * @throws IncompleteVectorOrMatrixException if any of the two vectors is
     *                                           incomplete.
     * @throws ArithmeticException               if the two vectors don't have the
     *                                           same length.
     */
    public AgbVector hadamardProduct(AgbVector multiplicand) {

        if ((!this.ifComplete()) || (!multiplicand.ifComplete()))
            throw new ArithmeticException("doing Hadamard Product on incomplete vector.");
        if (!this.ifSameSize(multiplicand))
            throw new ArithmeticException("multiplying vector of different length");

        AgbVector returnVector = new AgbVector(values.length);

        for (int i = 0; i < values.length; i++) {
            returnVector.addValue(this.values[i].multiply(multiplicand.values[i]), i);
        }

        return returnVector;

    }

    /**
     * Returns an {@code AgbVector}, the dot product of {@code this} and
     * {@code multiplicand}.
     * 
     * @param multiplicand
     * @return {@code multiplicand · this}
     * @throws IncompleteVectorOrMatrixException if any of the two vectors is
     *                                           incomplete
     * @throws ArithmeticException               if the two vectors don't have the
     *                                           same length
     */
    public BigDecimal dotProduct(AgbVector multiplicand) {

        if ((!this.ifComplete()) || (!multiplicand.ifComplete()))
            throw new ArithmeticException("doing dot product on incomplete vector.");
        if (!this.ifSameSize(multiplicand))
            throw new ArithmeticException("multiplying vectors of different length.");

        BigDecimal result = BigDecimal.ZERO;

        for (int i = 0; i < values.length; i++) {
            result = result.add(this.values[i].multiply(multiplicand.values[i]));
        }

        return result;
    }

    /**
     * Returns a matrix whose element of ith row and jth column is equal to the
     * product ith element of {@code this} and jth element of {@code multiplicand} .
     * 
     * @param multiplicand
     * @return
     */
    public AgbMatrix matrixFormMultiply(AgbVector multiplicand) {
        AgbMatrix returnMatrix = new AgbMatrix(this.values.length, multiplicand.getLength());
        for (int i = 0; i < this.values.length; i++) {
            returnMatrix.addVector(multiplicand.multiply(this.values[i]), i);
        }
        return returnMatrix;
    }

    /**
     * Find the angle between two vectors in radian with specified precision and
     * rounding mode.
     * 
     * @param o
     * @param mc number of significant figures and rounding mode
     * @return {@code arccos((this·o)/(||this||*||o||))}
     */
    public BigDecimal angleBetween(AgbVector o, MathContext mc) {
        if ((!this.ifComplete()) || (!o.ifComplete()))
            throw new IncompleteVectorOrMatrixException("trying to find angle between incomplete vectors.");
        if (this.values.length != o.values.length)
            throw new ArithmeticException("trying to find angle between vectors of different length.");

        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);

        BigDecimal cosOfTheAngle = (this.dotProduct(o).abs())
                .divide(this.magnitude(calPrecision).multiply(o.magnitude(calPrecision)), calPrecision);

        return Trigo.rArccos(cosOfTheAngle, mc);
    }

    // Matrix operations
    /**
     * Returns product of {@code multiplicand}, a matrix and {@code this}, a column
     * vector.
     * 
     * @param multiplicand
     * @return
     */
    public AgbVector multiply(AgbMatrix multiplicand) {

        AgbVector returnVector = new AgbVector(multiplicand.getMatrixLength());

        for (int i = 0; i < multiplicand.getMatrixLength(); i++) {
            returnVector.addValue(this.dotProduct(multiplicand.getVector(i)), i);
        }

        return returnVector;
    }

    // Self operations
    /**
     * Returns an {@code AgbVector}, the dot product of {@code this} and
     * {@code this}.
     * 
     * @return {@code this · this}
     */
    public BigDecimal dotSquare() {
        return this.dotProduct(this);
    }

    /**
     * Returns the magnitude in {@code BigDecimal} of {@code this}, with the
     * specified
     * precision and rounding mode defined by {@code mc}.
     * 
     * @param mc
     * @return {@code ||this||}
     */
    public BigDecimal magnitude(MathContext mc) {
        return this.dotSquare().sqrt(mc);
    }

    /**
     * Returns an {@code AgbVector} whose size is equal to size of {@code this} plus
     * {@code addedSpace}.
     * 
     * @param addedSpace
     * @return an {@code AgbVector} whose size is equal to size of {@code this} plus
     *         {@code addedSpace}.
     */
    public AgbVector expandVector(int addedSpace) {

        AgbVector returnVector = new AgbVector(values.length + addedSpace);

        for (int i = 0; i < values.length; i++) {
            returnVector.addValue(values[i], i);
        }

        return returnVector;
    }

    public AgbVector negate() {
        AgbVector reVector = new AgbVector(this.values.length);
        for (int i = 0; i < this.values.length; i++) {
            try {
                reVector.values[i] = this.values[i].negate();
            } catch (NullPointerException e) {
                e.printStackTrace();
                reVector.values[i] = null;
            }
        }
        return reVector;
    }

    // Static methods
    /**
     * Returns an {@code AgbVector} of specified size and whose elements are all
     * zero.
     * 
     * @param length
     * @return an {@code AgbVector} of specified size and whose elements are all
     *         zero.
     */
    public static AgbVector getZeroVector(int length) {

        AgbVector returnVector = new AgbVector(length);

        for (int i = 0; i < length; i++) {
            returnVector.addValue(BigDecimal.ZERO, i);
        }

        return returnVector;

    }

    // Comparing methods
    /**
     * Checks if an object of type {@code AgbVector} is complete, which means that
     * none of its element is null.
     * 
     * @return if none of its element is null
     */
    public boolean ifComplete() {
        for (BigDecimal i : this.values) {
            if (i == null)
                return false;
        }
        return true;
    }

    /**
     * Checks if {@code this} and {@code o} have the same size.
     * 
     * @param o
     * @return if {@code this} and {@code o} have the same size
     */
    public boolean ifSameSize(AgbVector o) {
        if (o.values.length != this.values.length)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(values);
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
        AgbVector other = (AgbVector) obj;
        if (!Arrays.equals(values, other.values))
            return false;
        return true;
    }

    // toXXX() methods
    @Override
    public String toString() {
        return Arrays.toString(values);
    }

    public double[] toDoubleArray() {
        double[] reVal = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            reVal[i] = Double.valueOf(this.values[i].toPlainString());
        }
        return reVal;
    }
}
