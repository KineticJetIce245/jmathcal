package Jmathcal.Number.Function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import Jmathcal.Number.InfiniteValueException;
import Jmathcal.Number.Complex.ComplexNum;

/**
 * Util class implementing some methods about exponential calculations of
 * {@code BigDecimal} and {@code ComplexNum}.
 * 
 * @author KineticJetIce245
 */
public class Exp {

    /** Euler number with 100 significant figures precision */
    public static final BigDecimal EulerNum100 = new BigDecimal(
            "2.7182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274");

    /** {@code ln(10)} with 100 significant figures precision */
    public static final BigDecimal ln10 = new BigDecimal(
            "2.3025850929940456840179914546843642076011014886287729760333279009675726096773524802359972050895982983");

    public static final BigDecimal TWO = new BigDecimal("2");
    /** {@code BigDecimal} {@value 1 and 1/4} */
    public static final BigDecimal ONEAQUARTER = new BigDecimal("1.25");
    public static final BigDecimal THREE = new BigDecimal("3");

    /**
     * Additional precision for calculation.
     * <p>
     * Default: {@code 10}
     */
    public static int PRECI = 10;

    /**
     * Additional precision for taylor series.
     * Used to compare to new terms of taylor series.
     * If the terms is smaller this than break the loop.
     * <p>
     * Default: {@value 3}
     */
    public static int PRECITEST = 3;

    // Real functions
    /**
     * Returns a {@code BigDecimal} which is the value of <i>e</i> to the
     * {@code num}-th power whose precision is defined by {@code precision}.
     * 
     * @param num
     * @param precision number of significant figures
     * @return {@code e^(num)}
     */
    public static BigDecimal exp(BigDecimal num, int precision) {
        return exp(num, new MathContext(precision));
    }

    /**
     * Returns a {@code BigDecimal} which is the value of <i>e</i> to the
     * {@code num}-th power whose precision and rounding
     * mode are defined by {@code mc}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code e^(num)}
     */
    public static BigDecimal exp(BigDecimal num, MathContext mc) {

        // if num is a largeNumber
        if (num.compareTo(new BigDecimal("1000")) > 0) {
            return largeNumExp(num, mc);
        }

        // precision for calculations
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        BigDecimal reVal;
        // int part of the number
        BigDecimal intNum = BigDecimal.ZERO;

        if (num.abs().compareTo(BigDecimal.ONE) > 0) {
            intNum = num.setScale(0, RoundingMode.DOWN);
        }

        BigDecimal EulerNum = EulerNum100;

        // calculate int part
        if (mc.getPrecision() > 90) {

            // if it's greater than 90, then recalculate the euler number.
            // e to the 1/2th power converges faster than e
            EulerNum = findSqrtEulerNum(calPrecision);

            if (intNum.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal intNumAbs = intNum.abs();
                // e^(-x) = 1/(e^x)
                reVal = BigDecimal.ONE.divide(EulerNum.pow(2 * Integer.valueOf(intNumAbs.toPlainString())),
                        calPrecision);
            } else {
                reVal = EulerNum.pow(2 * Integer.valueOf(intNum.toPlainString()));
            }
            // Don't use toString() here.
            // It will be written in scientific notation.

        } else {
            if (intNum.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal intNumAbs = intNum.abs();
                // e^(-x) = 1/(e^x)
                reVal = BigDecimal.ONE.divide(
                        EulerNum.setScale(calPrecision.getPrecision(), RoundingMode.HALF_UP)
                                .pow(Integer.valueOf(intNumAbs.toPlainString())),
                        calPrecision);
            } else {
                reVal = EulerNum.setScale(calPrecision.getPrecision(), RoundingMode.HALF_UP)
                        .pow(Integer.valueOf(intNum.toPlainString()));
            }
        }
        // calculate decimal part
        reVal = reVal.multiply(findExp(num.subtract(intNum), calPrecision));
        return reVal.round(mc);
    }

    /**
     * Returns a {@code BigDecimal} which is the natural logarithm (base <i>e</i>)
     * of {@code num} whose precision is defined by {@code precision}.
     * 
     * @param num       any {@code BigDecimal} > 0
     * @param precision number of significant figures
     * @return {@code ln(num)}
     */
    public static BigDecimal ln(BigDecimal num, int precision) {
        return ln(num, new MathContext(precision));
    }

    /**
     * Returns a {@code BigDecimal} which is the natural logarithm (base <i>e</i>)
     * of {@code num} whose precision and rounding mode are defined by {@code mc}.
     * 
     * @param num any {@code BigDecimal} > 0
     * @param mc  number of significant figures and rounding mode
     * @return {@code ln(num)}
     */
    public static BigDecimal ln(BigDecimal num, MathContext mc) {
        // precision for calculations
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        BigDecimal reVal;
        int tenthPow = 0;

        if (num.compareTo(BigDecimal.ZERO) <= 0)
            throw new InfiniteValueException(false);

        // ln(num) = ln(x*10^h) = lnx + hln10
        // finding h
        while (num.compareTo(BigDecimal.ONE) == 1) {
            tenthPow++;
            num = num.scaleByPowerOfTen(-1);
        }
        // reVal = hln10
        if (mc.getPrecision() > 90) {
            reVal = findLn10(calPrecision)
                    .multiply(new BigDecimal(String.valueOf(tenthPow)));
        } else {
            reVal = ln10.setScale(calPrecision.getPrecision(), RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(String.valueOf(tenthPow)));
        }

        // lnx + hln10
        reVal = reVal.add(findLn(num, calPrecision));
        return reVal.round(mc);
    }

    /**
     * Returns a {@code BigDecimal} which is value of the logarithm of
     * {@code argument} to base {@code base} whose precision is defined
     * by {@code precision}.
     * 
     * @param base      any {@code BigDecimal} > 0
     * @param argument  any {@code BigDecimal} > 0
     * @param precision number of significant figures
     * @return {@code log(base)(argument)}
     */
    public static BigDecimal rLog(BigDecimal base, BigDecimal argument, int precision) {
        return rLog(base, argument, new MathContext(precision));
    }

    /**
     * Returns a {@code BigDecimal} which is value of the logarithm of
     * {@code argument} to base {@code base} whose precision and rounding mode are
     * defined by {@code mc}.
     * 
     * @param base     any {@code BigDecimal} > 0
     * @param argument any {@code BigDecimal} > 0
     * @param mc       number of significant figures and rounding mode
     * @return {@code log(base)(argument)}
     */
    public static BigDecimal rLog(BigDecimal base, BigDecimal argument, MathContext mc) {

        if (base.compareTo(BigDecimal.ZERO) <= 0 || argument.compareTo(BigDecimal.ZERO) <= 0)
            throw new ArithmeticException("base or argument smaller than zero.");

        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        return (ln(argument, calPrecision)).divide(ln(base, calPrecision), mc);
    }

    /**
     * Returns the value of the {@code argument} to {@code power}th power.
     * 
     * @param base
     * @param num
     * @param precision number of significant figures
     * @return {@code (power)^(argument)}
     */
    public static BigDecimal rPow(BigDecimal argument, BigDecimal power, int precision) {
        return rPow(argument, power, new MathContext(precision));
    }

    /**
     * Returns the value of the {@code argument} to {@code power}th power.
     * 
     * @param base
     * @param num
     * @param mc   number of significant figures and rounding mode
     * @return {@code (power)^(argument)}
     */
    public static BigDecimal rPow(BigDecimal argument, BigDecimal power, MathContext mc) {
        // precision for calculations
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);

        if (argument.compareTo(BigDecimal.ZERO) < 0)
            throw new ArithmeticException("smaller than zero");

        // if power is a integer
        if ((power.subtract(power.setScale(0, RoundingMode.FLOOR))).compareTo(BigDecimal.ZERO) == 0)
            return (argument.pow(power.intValue())).setScale(mc.getPrecision(), RoundingMode.HALF_UP);

        if (power.compareTo(new BigDecimal("0.5")) == 0)
            return argument.sqrt(mc);

        BigDecimal exponent = power.multiply(ln(argument, calPrecision));
        return exp(exponent, calPrecision).round(mc);
    }

    /**
     * Returns the natural logarithm (base <i>e</i>) of {@code num}.
     * This method uses taylor series. Hence, it's
     * not obligatory but highly recommended to use this method for
     * {@code num} close to 1.
     * 
     * @param num
     * @param precision number of significant figures
     * @return {@code ln(num)}
     */
    public static BigDecimal findLn(BigDecimal num, int precision) {
        return findLn(num, new MathContext(precision));
    }

    /**
     * Returns the natural logarithm (base <i>e</i>) of {@code num}.
     * This method uses taylor series. Hence, it's
     * not obligatory but highly recommended to use this method for
     * {@code num} close to 1.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code ln(num)}
     */
    public static BigDecimal findLn(BigDecimal num, MathContext mc) {
        // s = (x-1)/(x+1)
        // ln(x) = 2(s + s^3/3 + s^5/5 + ...)

        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);

        if (num.compareTo(BigDecimal.ZERO) <= 0)
            throw new InfiniteValueException(false);

        // optTerm = (x-1)/(x+1)
        BigDecimal optTerm = (num.subtract(BigDecimal.ONE)).divide(
                num.add(BigDecimal.ONE), calPrecision);
        // checking if adding term is smaller then required precision
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(mc.getPrecision() + PRECITEST));
        BigDecimal reVal = BigDecimal.ZERO;
        BigDecimal currentTerm;
        BigDecimal currentXVal = optTerm;
        int divisor = -1;

        do {
            // 1, 3, 5, 7...
            divisor += 2;
            // 2/divisor * currentXVal
            currentTerm = currentXVal
                    .divide(new BigDecimal(String.valueOf(divisor)), calPrecision);

            reVal = reVal.add(currentTerm);
            // next x val
            currentXVal = currentXVal.multiply(optTerm.multiply(optTerm));
        } while (divisor < 25 || currentTerm.abs().compareTo(precisionTest) > 0);
        return reVal.multiply(TWO).round(mc);
    }

    /**
     * Returns the value of <i>e</i> to the {@code num}th power, whose precision
     * is defined by {@code precision}. This method uses taylor series. Hence, it's
     * not obligatory but highly recommended to use this method for
     * {@code num} smaller than 10.
     * 
     * @param num
     * @param precision number of significant figures
     * @return {@code e^(num)}
     */
    public static BigDecimal findExp(BigDecimal num, int precision) {
        return findExp(num, new MathContext(precision));
    }

    /**
     * Returns the value of <i>e</i> to the {@code num}th power, whose precision
     * and rounding mode are defined by {@code mc}. This method uses
     * taylor series. Hence, it's not obligatory but highly recommended
     * to use this method for {@code num} smaller than 10.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code e^(num)}
     */
    public static BigDecimal findExp(BigDecimal num, MathContext mc) {

        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        BigDecimal reVal = BigDecimal.ZERO;
        // checking if adding term is smaller then required precision
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(mc.getPrecision() + PRECITEST));
        BigDecimal currentTerm = BigDecimal.ONE;
        int taylorTC = 0;
        // x/(n+1)
        BigDecimal multiplicand;
        do {
            // x^(n+1)/(n+1)! = x^(n)/n! * x/(n+1)
            taylorTC++;
            reVal = reVal.add(currentTerm);
            // x/(n+1)
            multiplicand = num.divide(new BigDecimal(String.valueOf(taylorTC)), calPrecision);
            currentTerm = currentTerm.multiply(multiplicand);
            // HERE NEEDS AN ABS()
        } while (taylorTC < 5 || currentTerm.abs().compareTo(precisionTest) > 0);

        return (reVal.round(mc));
    }

    /**
     * Returns the value of the square root of <i>e</i>, whose precision
     * is defined by {@code precision}.
     * 
     * @param precision number of significant figures
     * @return {@code sqrt(e)}, rounded as necessary.
     */
    public static BigDecimal findSqrtEulerNum(int precision) {
        return findExp(new BigDecimal("0.5"), precision);
    }

    /**
     * Returns the value of the square root of <i>e</i>, whose precision
     * and rounding mode are defined by {@code mc}.
     * 
     * @param mc number of significant figures and rounding mode
     * @return {@code sqrt(e)}
     */
    public static BigDecimal findSqrtEulerNum(MathContext mc) {
        return findExp(new BigDecimal("0.5"), mc);
    }

    /**
     * Find <i>e</i>^x of a large number
     * 
     * @param num
     * @param precision number of significant figures
     * @return {@code e^num}
     */
    public static BigDecimal largeNumExp(BigDecimal num, int precision) {
        return largeNumExp(num, new MathContext(precision));
    }

    /**
     * Find <i>e</i>^x of a large number
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code e^num}
     */
    public static BigDecimal largeNumExp(BigDecimal num, MathContext mc) {
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);

        // x = hln10 + f
        // e^x = 10^h * e^f
        BigDecimal calLn10 = ln10;
        if (mc.getPrecision() > 90)
            calLn10 = findLn10(calPrecision);

        // h
        BigDecimal intNum = num.divide(calLn10, calPrecision);
        intNum = intNum.setScale(0, RoundingMode.DOWN);

        // e^f
        num = num.subtract(intNum.multiply(calLn10));
        BigDecimal reVal = findExp(num, calPrecision);

        if (intNum.compareTo(new BigDecimal("-2147483648")) <= 0) {
            throw new ArithmeticException("Unable to handle, the exponent is to " +
                    "small, causing an underflow of int used to express the exponent.");
        } else if (intNum.compareTo(new BigDecimal("2147483647")) >= 0) {
            throw new ArithmeticException("Unable to handle, the exponent is to " +
                    "big, causing an overflow of int used to express the exponent.");
        } else {
            // 10^h * e^f
            return reVal.scaleByPowerOfTen(intNum.intValue()).round(mc);
        }
    }

    // super magic numbers
    private static final BigDecimal SUPMAGNUM1 = new BigDecimal("0.8");
    private static final BigDecimal SUPMAGNUM2 = new BigDecimal("1.073741824");

    /**
     * Returns the ln(10) with required precision.
     * 
     * @param precision number of significant figures
     * @return {@code ln(10)}
     */
    public static BigDecimal findLn10(int precision) {
        return findLn10(new MathContext(precision));
    }

    /**
     * Returns the ln(10) with required precision.
     * 
     * @param mc number of significant figures and rounding mode
     * @return {@code ln(10)}
     */
    public static BigDecimal findLn10(MathContext mc) {
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        // ln10 = ln1.073741824 - 10ln0.8
        BigDecimal reVal = findLn(SUPMAGNUM2, calPrecision)
                .subtract(findLn(SUPMAGNUM1, calPrecision).scaleByPowerOfTen(1));
        return reVal.round(mc);
    }

    // Complex functions
    /**
     * Returns a complex number which is the result
     * of <i>e</i>^({@code num})
     * This {@code ComplexNum} that this method returns
     * has the same precision as {@code num}.
     * 
     * @param num
     * @param precision number of significant figures
     * @return {@code e^(num)}
     */
    public static ComplexNum exp(ComplexNum num, int precision) {
        return exp(num, new MathContext(precision));
    }

    /**
     * Returns a complex number which is the result
     * of <i>e</i>^({@code num})
     * This {@code ComplexNum} that this method returns
     * has the same precision as {@code num}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code e^(num)}
     */
    public static ComplexNum exp(ComplexNum num, MathContext mc) {
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        // e^(a+bi) = (e^a)*(e^(bi)) = (e^a)(cos(b) + i*sin(b))
        BigDecimal coefficient = exp(num.getRealValue(), calPrecision);
        BigDecimal rVal = Trigo.cos(num.getImaValue(), calPrecision);
        BigDecimal iVal = Trigo.sin(num.getImaValue(), calPrecision);
        rVal = coefficient.multiply(rVal).round(mc);
        iVal = coefficient.multiply(iVal).round(mc);
        return new ComplexNum(rVal, iVal, mc);
    }

    /**
     * Compute the {@code ln(num)}, where num is any not
     * zero number.
     * 
     * @param num
     * @param precision number of significant figures
     * @return {@code ln(num)}
     */
    public static ComplexNum compLn(BigDecimal num, int precision) {
        return compLn(num, new MathContext(precision));
    }

    /**
     * Compute the {@code ln(num)}, where num is any not
     * zero number.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code ln(num)}
     */
    public static ComplexNum compLn(BigDecimal num, MathContext mc) {
        // ln(-x) = ln(x) + pi*i
        BigDecimal iVal = BigDecimal.ZERO;
        if (num.compareTo(BigDecimal.ZERO) < 0) {
            iVal = Trigo.PI(mc);
            num.abs();
        }
        BigDecimal rVal = ln(num, mc);
        return new ComplexNum(rVal, iVal, mc);
    }

    /**
     * Returns a complex number which is the result
     * of ln(num).
     * This method recalculates the rValue and the phiValue
     * of the num.
     * 
     * @param num
     * @param precision number of significant figures
     * @return {@code ln(num)}
     */
    public static ComplexNum ln(ComplexNum num, int precision) {
        return ln(num, new MathContext(precision));
    }

    /**
     * Returns a complex number which is the result
     * of ln(num).
     * This method recalculates the rValue and the phiValue
     * of the num.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code ln(num)}
     */
    public static ComplexNum ln(ComplexNum num, MathContext mc) {
        // ln(re^(i*x)) = ln(r) + x*i
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        BigDecimal rVal = ln(num.calRValue(calPrecision), mc);
        BigDecimal iVal = num.calPhiValue(mc);
        return new ComplexNum(rVal, iVal, mc);
    }

    /**
     * Returns a complex number whose value is (base^exponent).
     * 
     * @param base
     * @param exponent
     * @param precision number of significant figures
     * @return {@code (base)^(exponent)}
     */
    public static ComplexNum pow(ComplexNum base, ComplexNum exponent, int precision) {
        return pow(base, exponent, new MathContext(precision));
    }

    /**
     * Returns a complex number whose value is (base^exponent).
     * 
     * @param base
     * @param exponent
     * @param mc       number of significant figures and rounding mode
     * @return {@code (base)^(exponent)}
     */
    public static ComplexNum pow(ComplexNum base, ComplexNum exponent, MathContext mc) {
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        // x = exponent * (ln(base))
        ComplexNum x = ln(base, calPrecision).multiply(exponent);
        return exp(x, mc).round(mc);
    }

    /**
     * Returns a complex number whose value is log(base)(argument)
     * 
     * @param base
     * @param argument
     * @param mc       number of significant figures and rounding mode
     * @return {@code log(base)(argument)}
     */
    public static ComplexNum log(ComplexNum base, ComplexNum argument, MathContext mc) {
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        return ln(argument, calPrecision)
                .divide(ln(base, calPrecision), mc)
                .round(mc);
    }

    /**
     * Returns a complex number whose value is log(base)(argument)
     * 
     * @param base
     * @param argument
     * @param precision number of significant figures
     * @return {@code log(base)(argument)}
     */
    public static ComplexNum log(ComplexNum base, ComplexNum argument, int precision) {
        return log(base, argument, new MathContext(precision));
    }
}
