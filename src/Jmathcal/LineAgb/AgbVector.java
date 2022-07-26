package Jmathcal.LineAgb;

import java.math.BigDecimal;
import java.util.Arrays;

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

    private final BigDecimal[] values;

    /**
     * Constructs a {@code AgbVector} with specified length.
     * <p>
     * To add value to an empty position, use {@code addValue} method.
     * 
     * @param length
     */
    public AgbVector(int length) {
        values = new BigDecimal[length];
    }

    /**
     * Constructs a {@code AgbVector} using a an array of {@code BigDecimal}.
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
     * Constructs a {@code AgbVector} using a an array of {@code String}.
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
     * @return the length of the array that this contains
     */
    public int getLength() {
        return values.length;
    }

    /**
     * Returns the element at the specified position in this vector.
     * 
     * @param index
     * @return the element at the specified position in this vector
     */
    public BigDecimal getValue(int index) {
        return values[index];
    }

    /**
     * Adds elements to the specified empty position in this vector.
     * 
     * @param val   element to be added
     * @param index
     * @throws IllegalArgumentException if there is already an element at the
     *                                  specified position
     */
    public void addValue(BigDecimal val, int index) {
        if (this.values[index] != null) {
            throw new IllegalArgumentException("the space is already filled");
        }
        values[index] = val;
    }

    /**
     * Returns a {@code AgbVector}, a vector formed by {@code (this +
     * augend)}.
     *
     * @param augend value to be added to this {@code Vector}.
     * @return {@code this + augend}
     * @throws ArithmeticException if the two vectors don't have the
     *                             same length.
     */
    public AgbVector add(AgbVector augend) {

        AgbVector returnVector = new AgbVector(values.length);

        if (augend.values.length != this.values.length) {
            throw new ArithmeticException("adding not equivalent length vector");
        } else {
            for (int i = 0; i < values.length; i++) {
                returnVector.addValue(this.values[i].add(augend.values[i]), i);
            }
        }

        return returnVector;
    }

    /**
     * Returns a {@code AgbVector}, a vector formed by {@code (this -
     * subtrahend)}.
     *
     * @param subtrahend value to be subtracted from this {@code AgbVector}.
     * @return {@code this - subtrahend}
     * @throws ArithmeticException if the two vectors don't have the
     *                             same length.
     */
    public AgbVector subtract(AgbVector subtrahend) {

        AgbVector returnVector = new AgbVector(values.length);

        if (subtrahend.getLength() != values.length) {
            throw new ArithmeticException("subtracting not equivalent length vector");
        } else {
            for (int i = 0; i < values.length; i++) {
                returnVector.addValue(this.values[i].subtract(subtrahend.values[i]), i);
            }
        }

        return returnVector;
    }

    /**
     * Returns a {@code AgbVector}, a vector formed by {@code (multiplicand *
     * this)}.
     *
     * @param multiplicand value to be multiplied to this's elements.
     * @return {@code multiplicand * this}
     */
    public AgbVector multiply(BigDecimal multiplicand) {

        AgbVector returnVector = new AgbVector(values.length);

        for (int i = 0; i < values.length; i++) {
            returnVector.addValue(values[i].multiply(multiplicand), i);
        }

        return returnVector;
    }

    public AgbVector add(BigDecimal augend) {

        AgbVector returnVector = new AgbVector(values.length);

        for (int i = 0; i < values.length; i++) {
            returnVector.addValue(this.values[i].add(augend), i);
        }

        return returnVector;
    }

    public AgbVector hadamardProduct(AgbVector multiplicand) {

        AgbVector returnVector = new AgbVector(values.length);

        if (multiplicand.getLength() != values.length) {
            throw new ArithmeticException("multiplying vector of different length");
        } else {
            for (int i = 0; i < values.length; i++) {
                returnVector.addValue(this.values[i].multiply(multiplicand.values[i]), i);
            }
        }

        return returnVector;

    }

    public AgbMatrix matrixFormMultiply(AgbVector multiplicand) {
        AgbMatrix returnMatrix = new AgbMatrix(this.values.length, multiplicand.getLength());
        for (int i = 0; i < this.values.length; i++) {
            returnMatrix.addVector(multiplicand.multiply(this.values[i]), i);
        }
        return returnMatrix;
    }

    public BigDecimal dotProduct(BigDecimal scalar) {
        return AgbVector.getZeroVector(this.getLength()).add(scalar).dotProduct(this);
    }

    public BigDecimal dotProduct(AgbVector multiplicand) {

        BigDecimal result = BigDecimal.ZERO;

        if (multiplicand.getLength() != values.length) {
            throw new ArithmeticException("multiplying vectors of different length.");
        } else {
            for (int i = 0; i < values.length; i++) {
                result = result.add(this.values[i].multiply(multiplicand.values[i]));
            }
        }

        return result;
    }

    public BigDecimal dotSquare() {
        return this.dotProduct(this);
    }

    public AgbVector multiplyByMatrix(AgbMatrix multiplicand) {

        AgbVector returnVector = new AgbVector(multiplicand.getMatrixLength());

        for (int i = 0; i < multiplicand.getMatrixLength(); i++) {
            returnVector.addValue(this.dotProduct(multiplicand.getVector(i)), i);
        }

        return returnVector;
    }

    public AgbVector expandVector(int addSpace) {

        AgbVector returnVector = new AgbVector(values.length + addSpace);

        for (int i = 0; i < values.length; i++) {
            returnVector.addValue(values[i], i);
        }

        return returnVector;
    }

    public static AgbVector getZeroVector(int length) {

        AgbVector returnVector = new AgbVector(length);

        for (int i = 0; i < length; i++) {
            returnVector.addValue(BigDecimal.ZERO, i);
        }

        return returnVector;

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
