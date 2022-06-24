package JMathcal.Number;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Immutable, arbitrary-precision signed decimal complex numbers. A
 * {@code ComplexNumber} consists of a real part and an imaginary part
 * each stored as a {@code BigDecimal}.
 */
public class ComplexNum implements Serializable, Comparable<ComplexNum>, Computable<ComplexNum> {

    // Serialization
    @java.io.Serial
    private static final long serialVersionUID = -3266019408160301724L;

    private final int precision;
    private final BigDecimal realValue;
    private final BigDecimal imaValue;

    public ComplexNum(BigDecimal realValue, BigDecimal imaValue, int precision) {
        this.precision = precision;
        this.realValue = realValue;
        this.imaValue = imaValue;
    }

    /**
     * Returns the value of {@code (this + augend)}, and whose precision
     * is {@code max(this.precision, augend.precision)}.
     * @param augend value to be added.
     * @return {@code this + augend}
     */
    public ComplexNum add(ComplexNum augend) {
        return new ComplexNum(this.realValue.add(augend.realValue),
                this.imaValue.add(augend.imaValue),
                this.precision > augend.precision ? this.precision : augend.precision);
    }

    /**
     * Returns the value of {@code (this - augend)}, and whose precision
     * is {@code max(this.precision, augend.precision)}.
     * @param subtrahend value subtracted from the original value.
     * @return {@code this - augend}
     */
    public ComplexNum subtract(ComplexNum subtrahend) {
        return new ComplexNum(this.realValue.subtract(subtrahend.realValue),
        this.imaValue.subtract(subtrahend.imaValue),
        this.precision > subtrahend.precision ? this.precision : subtrahend.precision);
    }

    @Override
    public int compareTo(ComplexNum o) {
        // TODO Auto-generated method stub
        return 0;
    }

}