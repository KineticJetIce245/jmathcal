package Number.Complex;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import Number.Computable;

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

    private final BigDecimal rValue;
    private final BigDecimal phiValue;

    public ComplexNum(BigDecimal realValue, int precision) {
        this.precision = precision;
        this.realValue = realValue;
        this.imaValue = BigDecimal.ZERO;
    }

    public ComplexNum(BigDecimal realValue, BigDecimal imaValue, int precision) {
        this.precision = precision;
        this.realValue = realValue;
        this.imaValue = imaValue;
        this.rValue = this.abs();
    }

    @Override
    public ComplexNum add(ComplexNum augend) {
        return new ComplexNum(this.realValue.add(augend.realValue),
                this.imaValue.add(augend.imaValue),
                this.precision > augend.precision ? this.precision : augend.precision);
    }

    @Override
    public ComplexNum subtract(ComplexNum subtrahend) {
        return new ComplexNum(this.realValue.subtract(subtrahend.realValue),
                this.imaValue.subtract(subtrahend.imaValue),
                this.precision > subtrahend.precision ? this.precision : subtrahend.precision);
    }

    @Override
    public ComplexNum multiply(ComplexNum multiplicand) {
        BigDecimal newRealVal = this.realValue.multiply(multiplicand.realValue)
                .subtract(this.imaValue.multiply(multiplicand.imaValue));
        
        BigDecimal newImaVal = this.realValue.multiply(multiplicand.imaValue)
                .add(this.imaValue.multiply(multiplicand.realValue));

        return new ComplexNum(newRealVal, newImaVal,
                this.precision > multiplicand.precision ? this.precision : multiplicand.precision);
    }

    @Override
    public ComplexNum divide(ComplexNum divisor) {
        int globPrecision = this.precision > divisor.precision ? this.precision : divisor.precision;
        ComplexNum product = this.multiply(divisor.conjugate());
        return new ComplexNum(product.realValue.divide(divisor.abs(false), globPrecision, RoundingMode.HALF_UP),
                product.imaValue.divide(divisor.abs(false), globPrecision, RoundingMode.HALF_UP),
                globPrecision);
    }
    
    public ComplexNum conjugate() {
        return new ComplexNum(realValue, imaValue.negate(), precision);
    }

    @Override
    public int compareTo(ComplexNum o) {
        return this.abs().compareTo(o.abs());
    }

    public BigDecimal abs() {
        return this.abs(true);
    }

    public BigDecimal abs(boolean ifRoot) {
        BigDecimal reVal = (realValue.pow(2).add(imaValue.pow(2)));
        return ifRoot ? (reVal).sqrt(new MathContext(precision, RoundingMode.HALF_UP)) : reVal;
    }

}