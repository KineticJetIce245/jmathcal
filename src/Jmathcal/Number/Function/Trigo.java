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
     * Additional precision for calculation.
     *
     * Default: {@code 10}
     */
    public static int PRECI = 10;

    /**
     * Additional precision for taylor series.
     * Used to compare to new terms of taylor series.
     * If the terms is smaller than break the loop.
     * 
     * Default: {@code 3}
     */
    public static int PRECITEST = 3;

    /**
     * Find the sin of {@code num} with the required
     * precision (significant figures).
     * 
     * @param num
     * @param precision
     * @return {@code sin(num)}
     */
    public static BigDecimal sin(BigDecimal num, int precision) {
        return sin(num, new MathContext(precision));
    }

    /**
     * Find the sin of {@code num} with the required
     * precision (significant figures).
     * 
     * @param num
     * @param mc
     * @return {@code sin(num)}
     */
    public static BigDecimal sin(BigDecimal num, MathContext mc) {
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        // return value
        BigDecimal reVal = BigDecimal.ZERO;
        // sign
        boolean reValSign = true;
        // zero test
        if (num.compareTo(BigDecimal.ZERO) == 0)
            return reVal;
        // sign
        if (num.compareTo(BigDecimal.ZERO) < 0) {
            reValSign = false;
            num = num.abs();
        }
        // Pi value
        BigDecimal piVal;
        if (mc.getPrecision() < 90) {
            piVal = PI100.round(calPrecision);
        }
        piVal = PI(calPrecision);
        // num <= pi/2
        if (num.compareTo(piVal.divide(TWO)) <= 0) {
            return findSin(num, mc);
        }
        // num r/ 2pi
        num = num.subtract(num.divide(piVal.multiply(TWO), 0, RoundingMode.HALF_UP).multiply(piVal.multiply(TWO)));
        reVal = reValSign ? findSin(num, mc) : findSin(num, mc).negate();
        return reVal;
    }

    /**
     * Find the cos of {@code num} with the required
     * precision.
     * 
     * @param num
     * @param precision
     * @return {@code cos(num)}
     */
    public static BigDecimal cos(BigDecimal num, int precision) {
        return cos(num, new MathContext(precision));
    }

    /**
     * Find the cos of {@code num} with the required
     * precision.
     * 
     * @param num
     * @param mc
     * @return {@code cos(num)}
     */
    public static BigDecimal cos(BigDecimal num, MathContext mc) {
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        // return value
        BigDecimal reVal = BigDecimal.ONE;
        // zero Test
        if (num.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ONE;
        // cos(-x) = cos(x)
        num = num.abs();
        // pi value
        BigDecimal piVal;
        if (mc.getPrecision() < 90) {
            piVal = PI100.round(calPrecision);
        }
        piVal = PI(calPrecision);

        // num <= pi/2
        if (num.compareTo(piVal.divide(TWO)) <= 0) {
            return findCos(num, mc);
        }

        // num r/ 2pi
        num = num.subtract(num.divide(piVal.multiply(TWO), 0, RoundingMode.HALF_UP).multiply(piVal.multiply(TWO)));
        reVal = findCos(num, mc);
        return reVal;
    }

    /**
     * Find the tan of {@code num} with the required
     * precision.
     * 
     * @param num
     * @param precision
     * @return {@code tan(num)}
     */
    public static BigDecimal tan(BigDecimal num, int precision) {
        return tan(num, new MathContext(precision));
    }

    /**
     * Find the tan of {@code num} with the required
     * precision.
     * 
     * @param num
     * @param mc
     * @return {@code tan(num)}
     */
    public static BigDecimal tan(BigDecimal num, MathContext mc) {
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        BigDecimal reVal = cos(num, calPrecision);
        if (reVal.compareTo(BigDecimal.ZERO) == 0) {
            throw new InfiniteValueException();
        }
        reVal = sin(num, calPrecision).divide(reVal, mc);
        return reVal;
    }

    /**
     * Return the arcsin of {@code -1 <= num <= 1}.
     * 
     * @param num
     * @param precision
     * @return {@code arcsin(num)}
     */
    public static BigDecimal rArcsin(BigDecimal num, int precision) {
        return rArcsin(num, new MathContext(precision));
    }

    /**
     * Return the arcsin of {@code -1 <= num <= 1}.
     * 
     * @param num
     * @param mc
     * @return {@code arcsin(num)}
     */
    public static BigDecimal rArcsin(BigDecimal num, MathContext mc) {
        // sign of the result
        boolean sign = true;
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);

        // set sign
        if (num.compareTo(BigDecimal.ZERO) == -1) {
            num = num.abs();
            sign = false;
        }
        // check if is real
        if (num.compareTo(BigDecimal.ONE) == 1) {
            throw new ArithmeticException("result is not a real number.");
        }
        // arcsin(x) = pi/2 - arcsin(sqrt(1-x^2))
        // for any x > sqrt(2)/2
        if (num.compareTo(new BigDecimal("0.70711")) >= 0) {
            num = (BigDecimal.ONE.subtract(num.pow(2))).sqrt(calPrecision);
            BigDecimal piVal = PI(calPrecision).divide(TWO);
            BigDecimal reVal = piVal.subtract(rArcsin(num, calPrecision)).round(mc);
            return sign ? reVal : reVal.negate();
        }

        // arcsin x = x + (1/2)(x^3/3) + ((1*3)/(2*4))(x^5/5) + ((1*3*5)/(2*4*6))(x^7/7)
        // +
        // ...
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(mc.getPrecision() + PRECITEST));
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
                            calPrecision);
        } while (currentTermVal.abs().compareTo(precisionTest) > 0);
        reVal = reVal.add(currentTermVal).round(mc);
        return sign ? reVal : reVal.negate();
    }

    /**
     * Return the arccos of {@code -1 <= num <= 1}
     * 
     * @param num
     * @param precision
     * @return {@code arccos(num)}
     */
    public static BigDecimal rArccos(BigDecimal num, int precision) {
        return rArccos(num, new MathContext(precision));
    }

    /**
     * Return the arccos of {@code -1 <= num <= 1}
     * 
     * @param num
     * @param mc
     * @return {@code arccos(num)}
     */
    public static BigDecimal rArccos(BigDecimal num, MathContext mc) {
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        // arccos(x) = pi/2 - arcsin(x)
        BigDecimal piVal = PI100.round(calPrecision).divide(TWO);
        if (mc.getPrecision() > 90) {
            piVal = PI(calPrecision).divide(TWO);
        }
        return piVal.subtract(rArcsin(num, calPrecision)).round(mc);
    }

    /**
     * Return the arctan of {@code num}
     * 
     * @param num
     * @param precision
     * @return {@code arctan(num)}
     */
    public static BigDecimal arctan(BigDecimal num, int precision) {
        return arctan(num, new MathContext(precision));
    }

    /**
     * Return the arctan of {@code num}
     * 
     * @param num
     * @param precision
     * @return {@code arctan(num)}
     */
    public static BigDecimal arctan(BigDecimal num, MathContext mc) {
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        // arctan(x) = arcsin(x/(sqrt(x^2+1)))
        num = num.divide((num.pow(2).add(BigDecimal.ONE)).sqrt(calPrecision), calPrecision);
        return rArcsin(num, mc);
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
        return findSin(num, new MathContext(precision));
    }

    /**
     * Find the sin of {@code num} with the demanded
     * precision with taylor approximation
     * 
     * @param num
     * @param mc
     * @return {@code sin(num)}
     */
    public static BigDecimal findSin(BigDecimal num, MathContext mc) {
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        // calculate sin(x)
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(mc.getPrecision() + PRECITEST));
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
            currentTermVal = currentXVal.divide(divisor, calPrecision);

            reVal = reVal.add(currentTermVal);

            // taylorTC = 2, 4, 6, 8...
            taylorTC = taylorTC.add(TWO);
            currentXVal = currentXVal.multiply(num.multiply(num));
        } while (currentTermVal.abs().compareTo(precisionTest) > 0);

        return reVal.round(mc);
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
        return findCos(num, new MathContext(precision));
    }

    /**
     * Find the cos of {@code num} with the demanded
     * precision with taylor approximation
     * 
     * @param num
     * @param mc
     * @return {@code cos(num)}
     */
    public static BigDecimal findCos(BigDecimal num, MathContext mc) {
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        // calculate cos(x)
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(mc.getPrecision() + PRECITEST));
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
            currentTermVal = currentXVal.divide(divisor, calPrecision);

            reVal = reVal.add(currentTermVal);

            // taylorTC = 1, 3, 5, 7...
            taylorTC = taylorTC.add(TWO);
            currentXVal = currentXVal.multiply(num.multiply(num));
        } while (currentTermVal.abs().compareTo(precisionTest) > 0);

        return reVal.round(mc);
    }

    /**
     * Return pi with the required precision
     * 
     * @param precision
     * @return {@code pi}
     */
    public static BigDecimal PI(int precision) {
        return PI(new MathContext(precision));
    }

    /**
     * Return pi with the required precision
     * 
     * @param precision
     * @return {@code pi}
     */
    public static BigDecimal PI(MathContext mc) {
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(mc.getPrecision() + PRECITEST));
        // Gauss–Legendre algorithm
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ONE
                .divide(Exp.rPow(TWO, new BigDecimal("0.5"), calPrecision), calPrecision);
        BigDecimal t = new BigDecimal("0.25");
        BigDecimal p = BigDecimal.ONE;
        BigDecimal oriA;
        BigDecimal reVal = BigDecimal.ZERO;
        BigDecimal reVal2 = BigDecimal.ZERO;

        do {
            oriA = a;
            reVal2 = reVal;
            a = (a.add(b)).divide(TWO);
            b = Exp.rPow(oriA.multiply(b), new BigDecimal("0.5"), calPrecision);
            t = t.subtract(p.multiply(oriA.subtract(a)).multiply(oriA.subtract(a)));
            p = p.add(p);

            reVal = (a.add(b)).pow(2)
                    .divide(t.multiply(new BigDecimal("4")), calPrecision);
        } while (reVal.subtract(reVal2).abs().compareTo(precisionTest) > 0);

        return reVal.round(mc);
    }

    // Complex functions
    /**
     * Returns the sin of {@code num}.
     * 
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
     * 
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
     * 
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
