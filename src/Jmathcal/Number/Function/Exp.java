package Jmathcal.Number.Function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import Jmathcal.Number.InfiniteValueException;

public class Exp {

    public static final BigDecimal EulerNum100 = new BigDecimal("2.7182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274");
    public static final BigDecimal ln10 = new BigDecimal("2.3025850929940456840179914546843642076011014886287729760333279009675726096773524802359972050895982983");
    public static final BigDecimal TWO = new BigDecimal("2");
    public static final BigDecimal TWOHALF = new BigDecimal("2.5");
    public static int PRECI = 10;

    // Real functions
    /**
     * Returns the value of e whose precision is {@code precision} to the
     * BigDecimal {@code num}th power.
     * @param num
     * @param precision
     * @return {@code e^(num)}
     */
    public static BigDecimal exp(BigDecimal num, int precision) {
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
            EulerNum = findSqrtEulerNum(precision+10);
            reVal = EulerNum.pow(2 * Integer.valueOf(intNum.toPlainString()));
            // Don't use toString() here.
            // It will be written in scientific notation.
        } else {
            reVal = EulerNum.pow(Integer.valueOf(intNum.toPlainString()));
        }
        
        // calculate decimal part
        reVal = reVal.multiply(findExp(num.subtract(intNum), precision+10));
        return reVal.round(new MathContext(precision+1));   
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
        while (num.compareTo(BigDecimal.ONE) == 1) {
            tenthPow++;
            // num = x*10^h, -1 < x < 1
            num = num.scaleByPowerOfTen(-1);
        }

        // ln(num) = ln(x*10^h) = lnx + hln10
        // reVal = hln10
        if (precision > 90) {
            // if it's greater than 90, then recalculate the ln number
            // hln10 = h*(2ln2 + ln2.5)
            reVal = (TWO.multiply(findLn(TWO, precision+tenthPow+10))
                    .add(findLn(TWOHALF, precision+tenthPow+10)))
                            .multiply(new BigDecimal(String.valueOf(tenthPow)));
        } else {
            reVal = ln10.multiply(new BigDecimal(String.valueOf(tenthPow)));
        }
        // reVal += lnx
        reVal = reVal.add(findLn(num, precision));

        return reVal.round(new MathContext(precision+1));
    }

    /**
     * Returns the logarithm {@code base} of {@code argument}.
     * @param base
     * @param num
     * @param precision
     * @return {@code logb(argument)}
     */ 
    public static BigDecimal log(BigDecimal base, BigDecimal argument, int precision) {
        if (base.compareTo(BigDecimal.ZERO) <= 0 || argument.compareTo(BigDecimal.ZERO) <= 0)
            throw new ArithmeticException("base or argument smaller than zero.");
        return (ln(argument, precision+10)).divide(ln(base, precision+10), precision, RoundingMode.HALF_UP);
    }

    public static BigDecimal rPow(BigDecimal argument, BigDecimal power, int precision) {
        if (argument.compareTo(BigDecimal.ZERO) < 0)
            throw new ArithmeticException("smaller than zero");
        BigDecimal exponent = power.multiply(ln(argument, precision+10));
        return exp(exponent, precision+10).round(new MathContext(precision+1));
    }

    /**
     * Returns the natural logarithm (base <i>e</i>) of {@code num}.
     * This method uses taylor series. Hence, it's
     * not obligatory but highly recommended to use this method for
     * {@code num} close to 1.
     * @param num
     * @param precision
     * @return {@code ln(num)}
     */ 
    public static BigDecimal findLn(BigDecimal num, int precision) {

        if (num.compareTo(BigDecimal.ZERO) <= 0) throw new InfiniteValueException(false);

        BigDecimal optTerm = (num.subtract(BigDecimal.ONE)).divide(
                num.add(BigDecimal.ONE), precision+10, RoundingMode.HALF_UP);
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(precision+3));
        BigDecimal reVal = BigDecimal.ZERO;
        BigDecimal currentTermVal;
        BigDecimal currentXVal = optTerm;
        int divisor = -1;// the divisor

        do {
            // 1, 3, 5, 7...
            divisor += 2;
            // 2/divisor * currentXVal
            currentTermVal = currentXVal.divide(new BigDecimal(String.valueOf(divisor)), precision+10, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("2"));
            reVal = reVal.add(currentTermVal);
            // next x val, currentXVal = currentXVal * optTerm^2
            currentXVal = currentXVal.multiply(optTerm.multiply(optTerm));
        } while (divisor < 25 || currentTermVal.abs().compareTo(precisionTest) > 0);
        return reVal.round(new MathContext(precision+1));
    }

    /**
     * Returns the value of <i>e</i> to the {@code num}th power, whose precision
     * is {@code precision}. This method uses taylor series. Hence, it's
     * not obligatory but highly recommended to use this method for
     * {@code num} smaller than 1.
     * @param num
     * @param precision
     * @return {@code e^(num)}
     */
    public static BigDecimal findExp(BigDecimal num, int precision) {

        BigDecimal reVal = BigDecimal.ZERO;// first val
        BigDecimal reVal2 = BigDecimal.ZERO;// second val

        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(precision+3));
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
                    precision+10,
                    RoundingMode.HALF_UP));
        // if reVal - reVal2 < precisionTest and computed 5 terms break the loop;
        // HERE NEEDS AN ABS FOR REVAL, BECAUSE WHEN HANDLING THE NEGATIVE NUMBERS...
        } while (taylorTC < 5 || reVal.subtract(reVal2).abs().compareTo(precisionTest) > 0);

        return (reVal.add(BigDecimal.ONE)).round(new MathContext(precision+1));
    }

    /**
     * Returns the value of the square root of <i>e</i>, whose precision
     * is {@code precision}.
     * @param precision
     * @return {@code sqrt(e)}
     */
    public static BigDecimal findSqrtEulerNum(int precision) {
        return findExp(new BigDecimal("0.5"), precision);
    }
    
}
