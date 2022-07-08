package Jmathcal.Number.Function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import Jmathcal.Number.InfiniteValueException;
import Jmathcal.Number.Complex.ComplexNum;

public class Trigo {

    public static final BigDecimal TWO = new BigDecimal("2");
    public static final BigDecimal PI100 = new BigDecimal(
            "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170680");

    /**
     * Find the sin of {@code num} with the demanded
     * precision
     * 
     * @param num
     * @param precision
     * @return {@code sin(num)}
     */
    public static BigDecimal sin(BigDecimal num, int precision) {
        BigDecimal reVal = BigDecimal.ZERO;
        boolean reValSign = true;
        if (num.compareTo(BigDecimal.ZERO) == 0)
            return reVal;
        if (num.compareTo(BigDecimal.ZERO) < 0) {
            reValSign = false;
            num = num.abs();
        }
        BigDecimal piVal;
        if (precision < 90) {
            piVal = PI100.round(new MathContext(precision + 10));
        }
        piVal = PI(precision + 10);
        if (num.compareTo(piVal.divide(TWO)) <= 0) {
            return findSin(num, precision);
        }

        // num r/ 2pi
        num = num.subtract(num.divide(piVal.multiply(TWO), 0, RoundingMode.HALF_UP).multiply(piVal.multiply(TWO)));
        reVal = reValSign ? findSin(num, precision) : findSin(num, precision).negate();
        return reVal;
    }

    /**
     * Find the cos of {@code num} with the demanded
     * precision
     * 
     * @param num
     * @param precision
     * @return {@code cos(num)}
     */
    public static BigDecimal cos(BigDecimal num, int precision) {
        BigDecimal reVal = BigDecimal.ONE;
        if (num.compareTo(BigDecimal.ZERO) == 0)
            return reVal;
        num = num.abs();
        BigDecimal piVal;
        if (precision < 90) {
            piVal = PI100.round(new MathContext(precision + 10));
        }
        piVal = PI(precision + 10);
        if (num.compareTo(piVal.divide(TWO)) <= 0) {
            return findCos(num, precision);
        }

        // num r/ 2pi
        num = num.subtract(num.divide(piVal.multiply(TWO), 0, RoundingMode.HALF_UP).multiply(piVal.multiply(TWO)));
        reVal = findCos(num, precision);
        return reVal;
    }

    public static BigDecimal tan(BigDecimal num, int precision) {
        BigDecimal reVal = cos(num, precision + 10);
        if (reVal.compareTo(BigDecimal.ZERO) == 0) {
            throw new InfiniteValueException();
        }
        reVal = sin(num, precision + 10).divide(reVal, precision + 10, RoundingMode.HALF_UP);
        return reVal.round(new MathContext(precision + 1));
    }

    /**
     * Return the arcsin of {@code -1 <= num <= 1}
     * @param num
     * @param precision
     * @return {@code arcsin(num)}
     */
    public static BigDecimal rArcsin(BigDecimal num, int precision) {
        boolean sign = true;

        // set sign
        if (num.compareTo(BigDecimal.ZERO) == -1) {
            num = num.abs();
            sign = false;
        }
        if (num.compareTo(BigDecimal.ONE) == 1) {
            throw new ArithmeticException("result is not a real number.");
        }
        // arcsin(x) = pi/2 - arcsin(sqrt(1-x^2))
        // for any x > sqrt(2)/2
        if (num.compareTo(new BigDecimal("0.70711")) >= 0) {
            num = (BigDecimal.ONE.subtract(num.pow(2))).sqrt(new MathContext(precision + 10));
            BigDecimal piVal = PI(precision + 10).divide(TWO);
            BigDecimal reVal =  piVal.subtract(rArcsin(num, precision + 10)).round(new MathContext(precision + 1));
            return sign ? reVal : reVal.negate();
        }

        // arcsin = x + (1/2)(x^3/3) + ((1*3)/(2*4))(x^5/5) + ((1*3*5)/(2*4*6))(x^7/7) + ...
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(precision + 1));
        BigDecimal reVal = BigDecimal.ZERO;
        BigDecimal currentTermVal = num;
        BigDecimal currentXVal = num;
        BigDecimal taylorTC = BigDecimal.ZERO;
        BigDecimal divisor = BigDecimal.ONE;
        BigDecimal multiplicand = BigDecimal.ONE;

        do {
            reVal = reVal.add(currentTermVal);
            // 1 2 3 4...
            taylorTC = taylorTC.add(BigDecimal.ONE);
            // 1 1*3 1*3*5 1*3*5*7...
            multiplicand = multiplicand.multiply(taylorTC.multiply(TWO).subtract(BigDecimal.ONE));
            // 2 2*4 2*4*6 2*4*6*8...
            divisor = divisor.multiply(taylorTC.multiply(TWO));
            // x^3 x^5 x^7...
            currentXVal = currentXVal.multiply(num.multiply(num));

            currentTermVal = currentXVal.multiply(multiplicand)
                    .divide(divisor.multiply(taylorTC.multiply(TWO).add(BigDecimal.ONE)),
                            precision + 10, RoundingMode.HALF_UP);
        } while (currentTermVal.abs().compareTo(precisionTest) > 0);
        reVal = reVal.add(currentTermVal).round(new MathContext(precision + 1));
        return sign ? reVal : reVal.negate();
    }

    /**
     * Return the arccos of {@code -1 <= num <= 1}
     * @param num
     * @param precision
     * @return {@code arccos(num)}
     */
    public static BigDecimal rArccos(BigDecimal num, int precision) {
        // arccos(x) = pi/2 - arcsin(x)
        BigDecimal piVal = PI100.round(new MathContext(precision + 11)).divide(TWO);
        if (precision > 90) {
            piVal = PI(precision + 10).divide(TWO);
        }
        return piVal.subtract(rArcsin(num, precision + 10)).round(new MathContext(precision + 1));
    }

    /**
     * Return the arctan of {@code num}
     * @param num
     * @param precision
     * @return {@code arctan(num)}
     */
    public static BigDecimal arctan(BigDecimal num, int precision) {
        // arctan(x) = arcsin(x/(sqrt(x^2+1)))
        num = num.divide((num.pow(2).add(BigDecimal.ONE)).sqrt(new MathContext(precision + 10)),
                precision + 10,
                RoundingMode.HALF_UP);
        
        return rArcsin(num, precision);
    }

    /**
     * Find the sin of {@code num} with the demanded
     * precision with taylor approximation
     * 
     * @param num
     * @param precision
     * @return {@code sin(num)}
     */
    public static BigDecimal findSin(BigDecimal num, int precision) {
        // calculate sin(x)
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(precision + 1));
        // reVal = x
        BigDecimal reVal = num;
        // currentXVal = x^3
        BigDecimal currentXVal = num.multiply(num.multiply(num));
        BigDecimal currentTermVal;
        // divisor = 1
        BigDecimal divisor = BigDecimal.ONE;
        BigDecimal taylorTC = TWO;

        do {
            // divisor = divisor * taylorTC * (taylorTC + 1) * (-1)^n
            // divisor = -3!, 5!, -7!, 9!...
            divisor = divisor.multiply(taylorTC.multiply(taylorTC.add(BigDecimal.ONE))).negate();
            // x^n / n!
            currentTermVal = currentXVal.divide(divisor, precision + 10, RoundingMode.HALF_UP);

            reVal = reVal.add(currentTermVal);

            // taylorTC = 2, 4, 6, 8...
            taylorTC = taylorTC.add(TWO);
            currentXVal = currentXVal.multiply(num.multiply(num));
        } while (currentTermVal.abs().compareTo(precisionTest) > 0);

        return reVal.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Find the cos of {@code num} with the demanded
     * precision with taylor approximation
     * 
     * @param num
     * @param precision
     * @return {@code cos(num)}
     */
    public static BigDecimal findCos(BigDecimal num, int precision) {
        // calculate cos(x)
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(precision + 1));
        // reVal = 1
        BigDecimal reVal = BigDecimal.ONE;
        // currentXVal = x^2
        BigDecimal currentXVal = num.multiply(num);
        BigDecimal currentTermVal;
        // divisor = 2
        BigDecimal divisor = BigDecimal.ONE;
        BigDecimal taylorTC = BigDecimal.ONE;

        do {
            // divisor = divisor * taylorTC * (taylorTC + 1) * (-1)
            // divisor = -2!, 4!, -6!, 8!...
            divisor = divisor.multiply(taylorTC.multiply(taylorTC.add(BigDecimal.ONE))).negate();
            // x^n / n!
            currentTermVal = currentXVal.divide(divisor, precision + 10, RoundingMode.HALF_UP);

            reVal = reVal.add(currentTermVal);

            // taylorTC = 1, 3, 5, 7...
            taylorTC = taylorTC.add(TWO);
            currentXVal = currentXVal.multiply(num.multiply(num));
        } while (currentTermVal.abs().compareTo(precisionTest) > 0);

        return reVal.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Return pi with the demanded precision
     * 
     * @param precision
     * @return {@code pi}
     */
    public static BigDecimal PI(int precision) {
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(precision + 3));
        // Gauss–Legendre algorithm
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ONE
                .divide(Exp.rPow(TWO, new BigDecimal("0.5"), precision + 10),
                        precision + 10,
                        RoundingMode.HALF_UP);
        BigDecimal t = new BigDecimal("0.25");
        BigDecimal p = BigDecimal.ONE;
        BigDecimal oriA;
        BigDecimal reVal = BigDecimal.ZERO;
        BigDecimal reVal2 = BigDecimal.ZERO;

        do {
            oriA = a;
            reVal2 = reVal;
            a = (a.add(b)).divide(TWO);
            b = Exp.rPow(oriA.multiply(b), new BigDecimal("0.5"), precision + 10);
            t = t.subtract(p.multiply(oriA.subtract(a)).multiply(oriA.subtract(a)));
            p = p.add(p);

            reVal = (a.add(b)).pow(2)
                    .divide(t.multiply(new BigDecimal("4")), precision + 10, RoundingMode.HALF_UP);
        } while (reVal.subtract(reVal2).abs().compareTo(precisionTest) > 0);

        return reVal.round(new MathContext(precision + 1));
    }

    // Complex functions
    /**
     * Returns the sin of {@code num}.
     * @param num
     * @param precision
     * @return {@code sin(num)}
     */
    public static ComplexNum sin(ComplexNum num, int precision) {
        ComplexNum x = num.multiplyByI();
        ComplexNum y = Exp.exp(x.negate(), precision + 10)
                .subtract(Exp.exp(x, precision + 10)).multiplyByI();
        return y.divide(new ComplexNum("2")).round(new MathContext(precision + 1));
    }
    /**
     * Returns the cos of {@code num}.
     * @param num
     * @param precision
     * @return {@code cos(num)}
     */
    public static ComplexNum cos(ComplexNum num, int precision) {
        ComplexNum x = num.multiplyByI();
        ComplexNum y = Exp.exp(x, precision + 10)
                .add(Exp.exp(x.negate(), precision + 10));
        return y.divide(new ComplexNum("2")).round(new MathContext(precision + 1));
    }
    /**
     * Returns the tan of {@code num}.
     * @param num
     * @param precision
     * @return {@code tan(num)}
     */
    public static ComplexNum tan(ComplexNum num, int precision) {
        ComplexNum x = num.multiplyByI().multiply(new ComplexNum("2"));
        ComplexNum y = Exp.exp(x, precision + 10);
        return new ComplexNum("1").subtract(y)
                .divide(y.add(new ComplexNum("1")), precision + 10)
                .multiplyByI(precision);
    }
}
