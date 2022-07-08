package Jmathcal.Number.Function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import Jmathcal.Number.InfiniteValueException;
import Jmathcal.Number.Complex.ComplexNum;

public class Exp {

    public static final BigDecimal EulerNum100 = new BigDecimal(
            "2.7182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274");
    public static final BigDecimal ln10 = new BigDecimal(
            "2.3025850929940456840179914546843642076011014886287729760333279009675726096773524802359972050895982983");
    public static final BigDecimal TWO = new BigDecimal("2");
    public static final BigDecimal ONEAQUARTER = new BigDecimal("1.25");
    public static final BigDecimal THREE = new BigDecimal("3");
    public static int PRECI = 10;

    // Real functions
    /**
     * Returns the value of <i>e</i> whose precision is {@code precision} to the
     * BigDecimal {@code num}th power.
     * 
     * @param num
     * @param precision
     * @return {@code e^(num)}
     */
    public static BigDecimal exp(BigDecimal num, int precision) {
        if (num.compareTo(new BigDecimal("1000")) > 0) {
            return largeNumExp(num, precision);
        }
        BigDecimal reVal;
        BigDecimal intNum = BigDecimal.ZERO;
        if (num.abs().compareTo(BigDecimal.ONE) > 0) {
            intNum = num.setScale(0, RoundingMode.DOWN);
        }
        BigDecimal EulerNum = EulerNum100;

        // calculate int part
        if (precision > 90) {

            // if it's greater than 90, then recalculate the euler number.
            // e to the 1/2th power converges faster than e
            EulerNum = findSqrtEulerNum(precision + 10);
            if (intNum.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal intNumAbs = intNum.abs();
                reVal = BigDecimal.ONE.divide(EulerNum.pow(2 * Integer.valueOf(intNumAbs.toPlainString())),
                        precision + 10,
                        RoundingMode.HALF_UP);
            } else {
                reVal = EulerNum.pow(2 * Integer.valueOf(intNum.toPlainString()));
            }
            // Don't use toString() here.
            // It will be written in scientific notation.
        } else {
            if (intNum.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal intNumAbs = intNum.abs();
                reVal = BigDecimal.ONE.divide(
                        EulerNum.setScale(precision + 10, RoundingMode.DOWN).pow(Integer.valueOf(intNumAbs.toPlainString())),
                        precision + 10,
                        RoundingMode.HALF_UP);
            } else {
                reVal = EulerNum.setScale(precision + 10, RoundingMode.DOWN).pow(Integer.valueOf(intNum.toPlainString()));
            }
        }
        // calculate decimal part
        reVal = reVal.multiply(findExp(num.subtract(intNum), precision + 10));
        return reVal.round(new MathContext(precision + 1));
    }

    /**
     * Returns the natural logarithm (base <i>e</i>) of {@code num}.
     * @param num
     * @param precision
     * @return {@code ln(num)}
     */
    public static BigDecimal ln(BigDecimal num, int precision) {
        BigDecimal reVal;
        int tenthPow = 0;
        // ln(num) = ln(x*10^h) = lnx + hln10
        while (num.compareTo(BigDecimal.ONE) == 1) {
            tenthPow++;
            num = num.scaleByPowerOfTen(-1);
        }
        // reVal = hln10
        if (precision > 90) {
            // if it's greater than 90, then recalculate the ln number
            // hln10
            reVal = findLn10(precision + 10)
                    .multiply(new BigDecimal(String.valueOf(tenthPow)));
        } else {
            reVal = ln10.setScale(precision + 10, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(String.valueOf(tenthPow)));
        }
        // reVal += lnx
        reVal = reVal.add(findLn(num, precision));

        return reVal.round(new MathContext(precision + 1));
    }

    /**
     * Returns the logarithm {@code base} of {@code argument}.
     * @param base
     * @param num
     * @param precision
     * @return {@code logb(argument)}
     */
    public static BigDecimal rLog(BigDecimal base, BigDecimal argument, int precision) {
        if (base.compareTo(BigDecimal.ZERO) <= 0 || argument.compareTo(BigDecimal.ZERO) <= 0)
            throw new ArithmeticException("base or argument smaller than zero.");
        return (ln(argument, precision + 10)).divide(ln(base, precision + 10), precision, RoundingMode.HALF_UP);
    }

    /**
     * Returns the value of the {@code argument} to {@code power}th power.
     * @param base
     * @param num
     * @param precision
     * @return {@code (power)^(argument)}
     */
    public static BigDecimal rPow(BigDecimal argument, BigDecimal power, int precision) {
        // if any of argument or power is smaller than zero
        if (argument.compareTo(BigDecimal.ZERO) < 0)
            throw new ArithmeticException("smaller than zero");
        
        // if power is a integer
        if ((power.subtract(power.setScale(0, RoundingMode.FLOOR))).compareTo(BigDecimal.ZERO) == 0)
            return (argument.pow(power.intValue())).setScale(precision, RoundingMode.HALF_UP);
        
        // if power is 0.5
        if (power.compareTo(new BigDecimal("0.5")) == 0)
            return argument.sqrt(new MathContext(precision + 1));
        
        BigDecimal exponent = power.multiply(ln(argument, precision + 10));
        return exp(exponent, precision + 10).round(new MathContext(precision + 1));
    }

    /**
     * Returns the natural logarithm (base <i>e</i>) of {@code num}.
     * This method uses taylor series. Hence, it's
     * not obligatory but highly recommended to use this method for
     * {@code num} close to 1.
     * 
     * @param num
     * @param precision
     * @return {@code ln(num)}
     */
    public static BigDecimal findLn(BigDecimal num, int precision) {

        if (num.compareTo(BigDecimal.ZERO) <= 0)
            throw new InfiniteValueException(false);

        BigDecimal optTerm = (num.subtract(BigDecimal.ONE)).divide(
                num.add(BigDecimal.ONE), precision + 10, RoundingMode.HALF_UP);
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(precision + 3));
        BigDecimal reVal = BigDecimal.ZERO;
        BigDecimal currentTermVal;
        BigDecimal currentXVal = optTerm;
        int divisor = -1;// the divisor

        do {
            // 1, 3, 5, 7...
            divisor += 2;
            // 2/divisor * currentXVal
            currentTermVal = currentXVal
                    .divide(new BigDecimal(String.valueOf(divisor)), precision + 10, RoundingMode.HALF_UP);
            reVal = reVal.add(currentTermVal);
            // next x val, currentXVal = currentXVal * optTerm^2
            currentXVal = currentXVal.multiply(optTerm.multiply(optTerm));
        } while (divisor < 25 || currentTermVal.abs().compareTo(precisionTest) > 0);
        return reVal.multiply(new BigDecimal("2")).round(new MathContext(precision + 1));
    }

    /**
     * Returns the value of <i>e</i> to the {@code num}th power, whose precision
     * is {@code precision}. This method uses taylor series. Hence, it's
     * not obligatory but highly recommended to use this method for
     * {@code num} smaller than 10.
     * 
     * @param num
     * @param precision
     * @return {@code e^(num)}
     */
    public static BigDecimal findExp(BigDecimal num, int precision) {

        BigDecimal reVal = BigDecimal.ZERO;// first val
        BigDecimal reVal2 = BigDecimal.ZERO;// second val

        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(precision + 3));
        BigDecimal currentXVal = BigDecimal.ONE;
        BigDecimal divisor = BigDecimal.ONE;
        int taylorTC = 0;// the current term

        do {
            taylorTC++;
            // x^n
            currentXVal = currentXVal.multiply(num);
            reVal2 = reVal;
            // n!
            divisor = divisor.multiply(new BigDecimal(String.valueOf(taylorTC)));
            // reVal + x^n/n!
            reVal = reVal.add(currentXVal.divide(
                    divisor,
                    precision + 10,
                    RoundingMode.HALF_UP));
            // if reVal - reVal2 < precisionTest and computed 5 terms break the loop;
            // HERE NEEDS AN ABS FOR REVAL, BECAUSE WHEN HANDLING THE NEGATIVE NUMBERS...
        } while (taylorTC < 5 || reVal.subtract(reVal2).abs().compareTo(precisionTest) > 0);

        return (reVal.add(BigDecimal.ONE)).round(new MathContext(precision + 1));
    }

    /**
     * Returns the value of the square root of <i>e</i>, whose precision
     * is {@code precision}.
     * 
     * @param precision
     * @return {@code sqrt(e)}
     */
    public static BigDecimal findSqrtEulerNum(int precision) {
        return findExp(new BigDecimal("0.5"), precision);
    }

    /**
     * Find <i>e</i>^x of a large number
     * @param num
     * @param precision
     * @return {@code num}
     */
    public static BigDecimal largeNumExp(BigDecimal num, int precision) {
        // x = hln10 + f;
        BigDecimal calLn10 = ln10;
        if (precision > 90)
            calLn10 = findLn10(precision);

        // find h
        BigDecimal intNum = num.divide(calLn10, precision + 10, RoundingMode.HALF_UP);
        intNum = intNum.setScale(0, RoundingMode.DOWN);

        // find f
        num = num.subtract(intNum.multiply(calLn10));

        BigDecimal reVal = findExp(num, precision);

        if (intNum.compareTo(new BigDecimal("-2147483648")) <= 0) {
            throw new ArithmeticException("Unable to handle, int underflow");
        } else if (intNum.compareTo(new BigDecimal("2147483647")) >= 0) {
            throw new ArithmeticException("Unable to handle, int overflow");
        } else {
            return reVal.scaleByPowerOfTen(intNum.intValue()).round(new MathContext(precision + 1));
        }
    }

    // super magic numbers
    private static final BigDecimal SUPMAGNUM1 = new BigDecimal("0.8");
    private static final BigDecimal SUPMAGNUM2 = new BigDecimal("1.073741824");
    /**
     * Returns the ln(10) with required precision
     * @param precision
     * @return {@code ln(10)}
     */
    public static BigDecimal findLn10(int precision) {
        // ln10 = ln1.073741824 - 10ln0.8
        BigDecimal reVal = findLn(SUPMAGNUM2, precision + 10)
                .subtract(findLn(SUPMAGNUM1, precision + 10).scaleByPowerOfTen(1));
        return reVal;
    }

    // Complex functions
    /**
     * Returns a complex number which is the result
     * of <i>e</i>^({@code num})
     * This {@code ComplexNum} that this method returns
     * has the same precision as {@code num}.
     * @param num
     * @param precision
     * @return {@code e^(num)}
     */
    public static ComplexNum exp(ComplexNum num, int precision) {
        // e^(a+bi) = (e^a)*(e^(bi)) = (e^a)(cos(b) + i*sin(b))
        BigDecimal coefficient = exp(num.getRealValue(), precision + 10);
        BigDecimal rVal = Trigo.cos(num.getImaValue(), precision + 10);
        BigDecimal iVal = Trigo.sin(num.getImaValue(), precision + 10);
        rVal = coefficient.multiply(rVal).round(new MathContext(precision + 1));
        iVal = coefficient.multiply(iVal).round(new MathContext(precision + 1));
        return new ComplexNum(rVal, iVal, precision);
    }

    /**
     * Compute the {@code ln(num)}, where num is any non
     * zero number.
     * @param num
     * @param precision
     * @return  {@code ln(num)}
     */
    public static ComplexNum compLn(BigDecimal num, int precision) {
        BigDecimal iVal = BigDecimal.ZERO;
        if (num.compareTo(BigDecimal.ZERO) < 0) {
            iVal = Trigo.PI(precision);
            num.abs();
        }
        BigDecimal rVal = ln(num, precision);
        return new ComplexNum(rVal, iVal, precision);
    }

    /**
     * Returns a complex number which is the result
     * of ln(num). 
     * This method recalculates the rValue and the phiValue
     * of the num.
     * @param num
     * @param precision
     * @return {@code ln(num)}
     */
    public static ComplexNum ln(ComplexNum num, int precision) {
        BigDecimal rVal = ln(num.calRValue(precision), precision);
        BigDecimal iVal = num.calPhiValue(precision);
        return new ComplexNum(rVal, iVal, precision);
    }

    public static ComplexNum pow(ComplexNum base, ComplexNum exponent, int precision) {
        // x = exponent * (ln(base))
        ComplexNum x = ln(base, precision + 10).multiply(exponent, precision);
        return exp(x, precision);
    }

    public static ComplexNum log(ComplexNum base, ComplexNum argument, int precision) {
        return ln(argument, precision + 10)
                .divide(ln(base, precision + 10), precision)
                .round(new MathContext(precision + 1));
    }
}
