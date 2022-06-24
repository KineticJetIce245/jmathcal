package JMathcal.Number.Rational;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import JMathcal.Number.Computable;

public class RationalNum extends Number implements Comparable<RationalNum>, Computable<RationalNum> {

    // Serialization
    @java.io.Serial
    private static final long serialVersionUID = 8531450068622154759L;

    private final BigInteger denominator;
    private final BigInteger numerator;
    private final int precision;

    private static final RationalNum ZERO = new RationalNum(new BigInteger("1"), new BigInteger("0"));

    public RationalNum(String val, InputType inputType) {
        this(val, inputType, 32);
    }

    public RationalNum(String val, InputType inputType, int precision) {

        String[] separatedStr = null;
        RationalNum intPart = null;
        RationalNum thisNum = null;

        switch (inputType) {
            case INT_FRACTION:
                this.precision = precision;

                separatedStr = val.split("/");
                if (separatedStr.length != 2) throw new NumberFormatException("not the right number format.");

                this.denominator = new BigInteger(separatedStr[1]);
                if (denominator.compareTo(BigInteger.ZERO) == 0) throw new ArithmeticException("/ by zero.");

                this.numerator = new BigInteger(separatedStr[0]);
                break;
            
            case DECIMAL:
                this.precision = precision;

                separatedStr = null;
                separatedStr = val.split("\\.");
                if (separatedStr.length != 2) throw new NumberFormatException("not the right number format.");

                RationalNum deciPart = (new RationalNum((new BigInteger("10")).pow(separatedStr[1].length()),
                        new BigInteger(separatedStr[1]))).reduce();
                intPart = new RationalNum(separatedStr[0], InputType.INT);

                thisNum = deciPart.add(intPart);

                this.denominator = thisNum.denominator;
                this.numerator = thisNum.numerator;
                break;
            
            case RECURRING_DECIMAL:
                this.precision = precision;
                separatedStr = val.split("\\.");
                if (separatedStr.length != 2) throw new NumberFormatException("not the right number format.");
                intPart = new RationalNum(separatedStr[0], InputType.INT);

                separatedStr = separatedStr[1].split("R");
                if (separatedStr.length != 2) throw new NumberFormatException("not the right number format.");

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

    private static BigInteger findLCM(BigInteger firstNum, BigInteger secondNum) {
        return firstNum.multiply(secondNum.divide(firstNum.gcd(secondNum))).abs();
    }

    public RationalNum reduce() {
        if (numerator.compareTo(BigInteger.ZERO) == 0)
            return ZERO;
        if (denominator.compareTo(BigInteger.ZERO) == 0)
            throw new ArithmeticException(" / by zero.");

        BigInteger gcd = numerator.gcd(denominator);
        BigInteger newDenom = denominator.divide(gcd);
        BigInteger newNumer = numerator.divide(gcd);

        boolean ifPositive = newDenom.compareTo(BigInteger.ZERO) * newNumer.compareTo(BigInteger.ZERO) == 1 ? true
                : false;
        newDenom = newDenom.abs();
        newNumer = ifPositive ? newNumer.abs() : newNumer.abs().negate();

        return new RationalNum(newDenom, newNumer, precision);
    }

    public RationalNum reverse() {
        if (denominator.compareTo(BigInteger.ZERO) == 0 || numerator.compareTo(BigInteger.ZERO) == 0)
            throw new ArithmeticException(" / by zero.");

        return new RationalNum(numerator, denominator, precision);
    }

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

    @Override
    public RationalNum subtract(RationalNum subtrahend) {
        RationalNum thisNum = this.reduce();
        subtrahend = subtrahend.reduce();

        BigInteger newDenominator = findLCM(thisNum.denominator, subtrahend.denominator);

        BigInteger newThisNumerator = thisNum.numerator.multiply(newDenominator.divide(thisNum.denominator));
        BigInteger newSubNumerator = subtrahend.numerator.multiply(newDenominator.divide(subtrahend.denominator));

        BigInteger newNumerator = newThisNumerator.subtract(newSubNumerator);
        return (new RationalNum(newDenominator, newNumerator,
                thisNum.precision > subtrahend.precision ? thisNum.precision : subtrahend.precision)).reduce();
    }

    @Override
    public RationalNum multiply(RationalNum multiplicand) {
        return (new RationalNum(this.denominator.multiply(multiplicand.denominator),
                this.numerator.multiply(multiplicand.numerator),
                this.precision > multiplicand.precision ? this.precision : multiplicand.precision)).reduce();
    }

    @Override
    public RationalNum divide(RationalNum divisor) {
        return this.multiply(divisor.reverse()).reduce();
    }

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
        RationalNum other = (RationalNum) obj;
        if (denominator == null) {
            if (other.denominator != null)
                return false;
        } else if (!denominator.equals(other.denominator))
            return false;
        if (numerator == null) {
            if (other.numerator != null)
                return false;
        } else if (!numerator.equals(other.numerator))
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
        return numerator.toString() + "/" + denominator.toString();
    }

}