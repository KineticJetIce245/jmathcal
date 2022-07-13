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
    /**
     * Euler number with 100 significant figures precision
     */
    public static final BigDecimal EulerNum100 = new BigDecimal(
            "2.7182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274");
    /**
     * {@code ln(10)} with 100 significant figures precision
     */
    public static final BigDecimal ln10 = new BigDecimal(
            "2.3025850929940456840179914546843642076011014886287729760333279009675726096773524802359972050895982983");
    /**
     * {@code BigDecimal} {@value 2}
     */
    public static final BigDecimal TWO = new BigDecimal("2");
    /**
     * {@code BigDecimal} {@value 1 and 1/4}
     */
    public static final BigDecimal ONEAQUARTER = new BigDecimal("1.25");
    /**
     * {@code BigDecimal} {@value 3}
     */
    public static final BigDecimal THREE = new BigDecimal("3");
    /**
     * Additional precision for calculation.
     *
     * Default: {@code 10}
     */
    public static int PRECI = 10;

    /**
     * Additional precision for taylor series.
     * Used to compare to new terms of taylor series.
     * If the terms is smaller this than break the loop.
     * Default: {@value 3}
     *
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
        // return value
        BigDecimal reVal;
        // int part of the number
        BigDecimal intNum = BigDecimal.ZERO;
        // get the int part
        if (num.abs().compareTo(BigDecimal.ONE) > 0) {
            intNum = num.setScale(0, RoundingMode.DOWN);
        }
        // Euler number
        BigDecimal EulerNum = EulerNum100;

        // calculate int part
        if (mc.getPrecision() > 90) {// high precision

            // if it's greater than 90, then recalculate the euler number.
            // e to the 1/2th power converges faster than e
            EulerNum = findSqrtEulerNum(calPrecision);

            // If int part is smaller then zero.
            if (intNum.compareTo(BigDecimal.ZERO) < 0) {
                // get the absolute value of int part
                BigDecimal intNumAbs = intNum.abs();
                // e^(-x) = 1/(e^x)
                reVal = BigDecimal.ONE.divide(EulerNum.pow(2 * Integer.valueOf(intNumAbs.toPlainString())),
                        calPrecision);
            } else {// If int part is greater
                reVal = EulerNum.pow(2 * Integer.valueOf(intNum.toPlainString()));
            }
            // Don't use toString() here.
            // It will be written in scientific notation.

        } else {// low precision

            // If int part is smaller then zero.
            if (intNum.compareTo(BigDecimal.ZERO) < 0) {
                // get the absolute value of int part
                BigDecimal intNumAbs = intNum.abs();
                // e^(-x) = 1/(e^x)
                reVal = BigDecimal.ONE.divide(
                        EulerNum.setScale(calPrecision.getPrecision(), RoundingMode.HALF_UP)
                                .pow(Integer.valueOf(intNumAbs.toPlainString())),
                        calPrecision);
            } else {// If int part is greater
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
        // return value
        BigDecimal reVal;
        int tenthPow = 0;

        // if the input is smaller than 0
        if (num.compareTo(BigDecimal.ZERO) <= 0)
            throw new InfiniteValueException(false);

        // ln(num) = ln(x*10^h) = lnx + hln10
        while (num.compareTo(BigDecimal.ONE) == 1) {
            tenthPow++;
            num = num.scaleByPowerOfTen(-1);
        }
        // reVal = hln10
        if (mc.getPrecision() > 90) {
            // if it's greater than 90, then recalculate the ln number
            // hln10
            reVal = findLn10(calPrecision)
                    .multiply(new BigDecimal(String.valueOf(tenthPow)));
        } else {
            // hln10
            reVal = ln10.setScale(calPrecision.getPrecision(), RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(String.valueOf(tenthPow)));
        }
        // reVal += lnx
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
        // precision for calculations
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
        // if any of argument or power is smaller than zero
        if (argument.compareTo(BigDecimal.ZERO) < 0)
            throw new ArithmeticException("smaller than zero");

        // if power is a integer
        if ((power.subtract(power.setScale(0, RoundingMode.FLOOR))).compareTo(BigDecimal.ZERO) == 0)
            return (argument.pow(power.intValue())).setScale(mc.getPrecision(), RoundingMode.HALF_UP);

        // if power is 0.5
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

        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);

        // if the input is smaller than 0
        if (num.compareTo(BigDecimal.ZERO) <= 0)
            throw new InfiniteValueException(false);

        // optTerm = (x-1)/(x+1)
        BigDecimal optTerm = (num.subtract(BigDecimal.ONE)).divide(
                num.add(BigDecimal.ONE), calPrecision);
        // 0.0...1 to check precision (when the adding term is smaller enough, stop the
        // addition)
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(mc.getPrecision() + PRECITEST));
        // return value
        BigDecimal reVal = BigDecimal.ZERO;
        // currentTerm
        BigDecimal currentTerm;
        // current x value
        BigDecimal currentXVal = optTerm;
        int divisor = -1;// the divisor

        do {
            // 1, 3, 5, 7...
            divisor += 2;
            // 2/divisor * currentXVal
            currentTerm = currentXVal
                    .divide(new BigDecimal(String.valueOf(divisor)), calPrecision);
            // reVal += currentTerm
            reVal = reVal.add(currentTerm);
            // next x val, currentXVal = currentXVal * optTerm^2
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

        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        // return value
        BigDecimal reVal = BigDecimal.ZERO;
        // 0.0...1 to check precision (when the adding term is smaller enough, stop the
        // addition)
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(mc.getPrecision() + PRECITEST));
        // currentTerm
        BigDecimal currentTerm = BigDecimal.ONE;
        // term no.
        int taylorTC = 0;
        // x/(n+1)
        BigDecimal multiplicand;

        // Taylor loops
        do {
            // x^(n+1)/(n+1)! = x^(n)/n! * x/(n+1)
            taylorTC++;
            // reVal + currentTerm
            reVal = reVal.add(currentTerm);
            // Find x/(n+1)
            multiplicand = num.divide(new BigDecimal(String.valueOf(taylorTC)), calPrecision);
            // x^(n+1)/(n+1)! = x^(n)/n! * x/(n+1)
            currentTerm = currentTerm.multiply(multiplicand);
            // if reVal - reVal2 < precisionTest and computed 5 terms break the loop;
            // HERE NEEDS AN ABS(), BECAUSE WHEN HANDLING THE NEGATIVE NUMBERS...
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
        BigDecimal calLn10 = ln10;
        if (mc.getPrecision() > 90)
            calLn10 = findLn10(calPrecision);

        // find h
        BigDecimal intNum = num.divide(calLn10, calPrecision);
        intNum = intNum.setScale(0, RoundingMode.DOWN);

        // find f
        num = num.subtract(intNum.multiply(calLn10));

        // reVal = e^f
        BigDecimal reVal = findExp(num, calPrecision);

        if (intNum.compareTo(new BigDecimal("-2147483648")) <= 0) {
            throw new ArithmeticException("Unable to handle, the exponent is to " +
                    "small, causing an underflow of int used to express the exponent.");
        } else if (intNum.compareTo(new BigDecimal("2147483647")) >= 0) {
            throw new ArithmeticException("Unable to handle, the exponent is to " +
                    "big, causing an overflow of int used to express the exponent.");
        } else {
            // reVal = 10^h * e^f
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
        // precision for calculation
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
        // precision for calculation
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
        // precision for calculation
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
        // precision for calculation
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
