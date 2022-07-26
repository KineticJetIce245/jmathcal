package Jmathcal.Number.Rational;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import Jmathcal.Number.Computable;
import Jmathcal.Number.Complex.ComplexNum;
import Jmathcal.Number.RealAnalytic.Analytic;

public class RationalNum extends Number implements Comparable<RationalNum>, Computable<RationalNum>, Analytic {

    // Serialization
    @java.io.Serial
    private static final long serialVersionUID = 8531450068622154759L;

    private final BigInteger denominator;
    private final BigInteger numerator;
    private final int precision;

    public static final RationalNum ZERO = new RationalNum(new BigInteger("1"), new BigInteger("0"));
    public static final RationalNum ONE = new RationalNum(new BigInteger("1"), new BigInteger("1"));

    // Constructors
    public RationalNum(String val, RationalInputType inputType) {
        this(val, inputType, 32);
    }

    public RationalNum(String val, RationalInputType inputType, int precision) {

        String[] separatedStr = null;
        RationalNum intPart = null;
        RationalNum thisNum = null;

        switch (inputType) {
            case FRACTION:
                this.precision = precision;

                separatedStr = val.split("/");
                if (separatedStr.length != 2)
                    throw new NumberFormatException("not the right number format.");

                this.denominator = new BigInteger(separatedStr[1]);
                if (denominator.compareTo(BigInteger.ZERO) == 0)
                    throw new ArithmeticException("/ by zero.");

                this.numerator = new BigInteger(separatedStr[0]);
                break;

            case DECIMAL:
                this.precision = precision;

                separatedStr = null;
                separatedStr = val.split("\\.");
                if (separatedStr.length != 2)
                    throw new NumberFormatException("not the right number format.");

                RationalNum deciPart = (new RationalNum((new BigInteger("10")).pow(separatedStr[1].length()),
                        new BigInteger(separatedStr[1]))).reduce();
                intPart = new RationalNum(separatedStr[0], RationalInputType.INT);

                thisNum = deciPart.add(intPart);

                this.denominator = thisNum.denominator;
                this.numerator = thisNum.numerator;
                break;

            case RECURRING_DECIMAL:
                this.precision = precision;
                separatedStr = val.split("\\.");
                if (separatedStr.length != 2)
                    throw new NumberFormatException("not the right number format.");
                intPart = new RationalNum(separatedStr[0], RationalInputType.INT);

                separatedStr = separatedStr[1].split("R");
                if (separatedStr.length != 2)
                    throw new NumberFormatException("not the right number format.");

                RationalNum deciPart1 = (new RationalNum((new BigInteger("10")).pow(separatedStr[0].length()),
                        new BigInteger(separatedStr[0]))).reduce();

                String denoForDeciPart2 = "";
                for (int i = 0; i < separatedStr[1].length(); i++) {
                    denoForDeciPart2 = denoForDeciPart2 + "9";
                }
                for (int i = 0; i < separatedStr[0].length(); i++) {
                    denoForDeciPart2 = denoForDeciPart2 + "0";
                }

                RationalNum deciPart2 = (new RationalNum(new BigInteger(denoForDeciPart2),
                        new BigInteger(separatedStr[1]))).reduce();

                thisNum = intPart.add(deciPart1.add(deciPart2));

                this.denominator = thisNum.denominator;
                this.numerator = thisNum.numerator;
                break;

            default:
                this.precision = precision;
                this.denominator = new BigInteger("1");
                this.numerator = new BigInteger(val);
                break;
        }
    }

    public RationalNum(String denominator, String numerator) {
        this(new BigInteger(denominator), new BigInteger(numerator));
    }

    public RationalNum(BigInteger denominator, BigInteger numerator) {
        this(denominator, numerator, 32);
    }

    public RationalNum(BigInteger denominator, BigInteger numerator, int precision) {
        this.precision = precision;
        if (denominator.compareTo(BigInteger.ZERO) == 0)
            throw new ArithmeticException(" / by zero.");
        this.denominator = denominator;
        this.numerator = numerator;
    }

    // Methods
    private static BigInteger findLCM(BigInteger firstNum, BigInteger secondNum) {
        return firstNum.multiply(secondNum.divide(firstNum.gcd(secondNum))).abs();
    }

    /**
     * Return the negative value of {@code this}.
     * 
     * @return {@code -this}.
     */
    public RationalNum negate() {
        RationalNum thisNum = this.reduce();
        BigInteger newNumer = thisNum.numerator.negate();
        return new RationalNum(thisNum.denominator, newNumer);
    }

    /**
     * Simplifies the RationalNum {@code this}.
     * 
     * @return {@code (denominator/gcd) / (numerator/gcd)}
     */
    public RationalNum reduce() {
        if (numerator.compareTo(BigInteger.ZERO) == 0)
            return ZERO;
        if (denominator.compareTo(BigInteger.ZERO) == 0)
            throw new ArithmeticException(" / by zero.");

        BigInteger gcd = numerator.gcd(denominator);
        BigInteger newDenom = denominator.divide(gcd);
        BigInteger newNumer = numerator.divide(gcd);

        newNumer = newDenom.compareTo(BigInteger.ZERO) * newNumer.compareTo(BigInteger.ZERO) == 1 ? newNumer.abs()
                : newNumer.abs().negate();
        newDenom = newDenom.abs();

        return new RationalNum(newDenom, newNumer, precision);
    }

    /**
     * Returns the inverse value of {@code this}.
     * 
     * @return {@code 1 / this}
     */
    public RationalNum inverse() {
        if (denominator.compareTo(BigInteger.ZERO) == 0 || numerator.compareTo(BigInteger.ZERO) == 0)
            throw new ArithmeticException(" / by zero.");

        return new RationalNum(numerator, denominator, precision);
    }

    /**
     * Returns the value of {@code (this + augend)}, and whose precision
     * is {@code max(this.precision, augend.precision)}.
     * 
     * @param augend value to be added.
     * @return {@code this + augend}
     */
    @Override
    public RationalNum add(RationalNum augend) {
        RationalNum thisNum = this.reduce();
        augend = augend.reduce();

        BigInteger newDenominator = findLCM(thisNum.denominator, augend.denominator);

        BigInteger newThisNumerator = thisNum.numerator.multiply(newDenominator.divide(thisNum.denominator));
        BigInteger newAugNumerator = augend.numerator.multiply(newDenominator.divide(augend.denominator));

        BigInteger newNumerator = newThisNumerator.add(newAugNumerator);
        return (new RationalNum(newDenominator, newNumerator,
                thisNum.precision > augend.precision ? thisNum.precision : augend.precision)).reduce();
    }

    /**
     * Returns the value of {@code (this - augend)}, and whose precision
     * is {@code max(this.precision, augend.precision)}.
     * 
     * @param subtrahend value subtracted from the original value.
     * @return {@code this - augend}
     */
    @Override
    public RationalNum subtract(RationalNum subtrahend) {
        return this.add(subtrahend.negate());
    }

    /**
     * Returns the value of {@code (this * augend)}, and whose precision
     * is {@code max(this.precision, augend.precision)}.
     * 
     * @param multiplicand value to be multiplied.
     * @return {@code this * augend}
     */
    @Override
    public RationalNum multiply(RationalNum multiplicand) {
        return (new RationalNum(this.denominator.multiply(multiplicand.denominator),
                this.numerator.multiply(multiplicand.numerator),
                this.precision > multiplicand.precision ? this.precision : multiplicand.precision)).reduce();
    }

    /**
     * Returns the value of {@code (this / augend)}, and whose precision
     * is {@code max(this.precision, augend.precision)}.
     * 
     * @param divisor value divided from the original value.
     * @return {@code this / augend}
     */
    @Override
    public RationalNum divide(RationalNum divisor) {
        return this.multiply(divisor.inverse()).reduce();
    }

    @Override
    public int compareTo(RationalNum o) {
        if (this.equals(o))
            return 0;

        RationalNum thisNum = this.reduce();
        o = o.reduce();

        BigInteger newDenominator = findLCM(thisNum.denominator, o.denominator);

        BigInteger newThisNumerator = thisNum.numerator.multiply(newDenominator.divide(thisNum.denominator));
        BigInteger newAugNumerator = o.numerator.multiply(newDenominator.divide(o.denominator));

        return newThisNumerator.compareTo(newAugNumerator);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((denominator == null) ? 0 : denominator.hashCode());
        result = prime * result + ((numerator == null) ? 0 : numerator.hashCode());
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
        RationalNum other = ((RationalNum) obj).reduce();
        RationalNum thisNum = this.reduce();
        if (thisNum.denominator == null) {
            if (other.denominator != null)
                return false;
        } else if (!thisNum.denominator.equals(other.denominator))
            return false;
        if (thisNum.numerator == null) {
            if (other.numerator != null)
                return false;
        } else if (!thisNum.numerator.equals(other.numerator))
            return false;
        return true;
    }

    @Override
    public int intValue() {
        return numerator.divide(denominator).intValue();
    }

    @Override
    public long longValue() {
        return numerator.divide(denominator).longValue();
    }

    @Override
    public float floatValue() {
        return (new BigDecimal(numerator)).divide(new BigDecimal(numerator), precision, RoundingMode.HALF_UP)
                .floatValue();
    }

    @Override
    public double doubleValue() {
        return (new BigDecimal(numerator)).divide(new BigDecimal(numerator), precision, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Override
    public String toString() {
        if (this.compareTo(ZERO) == 0)
            return "0";
        if (this.compareTo(ONE) == 0)
            return "1";
        if (this.denominator.compareTo(BigInteger.ONE) == 0)
            return numerator.toString();
        return numerator.toString() + "/" + denominator.toString();
    }

    @Override
    public ComplexNum compute(int precision) {
        return new ComplexNum(
                (new BigDecimal(numerator)).divide(new BigDecimal(denominator), precision, RoundingMode.HALF_UP));
    }

    @Override
    public ComplexNum compute(MathContext mc) {
        return new ComplexNum((new BigDecimal(numerator)).divide(new BigDecimal(denominator), mc));
    }
}