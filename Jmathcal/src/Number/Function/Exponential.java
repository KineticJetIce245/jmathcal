package Number.Function;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import Number.Complex.ComplexNum;

public class Exponential {

    public static final BigDecimal EulerNum100 = new BigDecimal("2.7182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274");

    public static BigDecimal exp(BigDecimal num, int precision) {
        BigDecimal reVal = null;
        MathContext toInt = new MathContext(1, RoundingMode.DOWN);
        BigDecimal intNum = num.round(toInt);
        BigDecimal EulerNum = EulerNum100;

        // calculate int part
        if (precision > 90) {

            // if it's greater than 90, then recalculate the euler number.
            // e to the 1/2th power converges faster than e
            EulerNum = findSqrtEulerNum(precision + 10);
            reVal = EulerNum.pow(2 * Integer.valueOf(intNum.toPlainString()));
            // Don't use toString() here.
            // It will be written in scientific notation.
        } else {
            reVal = EulerNum.pow(Integer.valueOf(intNum.toPlainString()));
        }
        
        // calculate decimal part
        reVal = reVal.multiply(findExp(num.subtract(intNum), precision));
        return reVal.round(new MathContext(precision + 1));
    }

    public static BigDecimal ln(BigDecimal num, int precision) {

    }
/*
    public ComplexNum exp(ComplexNum num, int precision) {
        
    }

    public ComplexNum ln(ComplexNum num, int precision) {

    }
*/

    /**
     * Returns the value of e to the {@code num}'s power, whose precision
     * is {@code precision}. This method uses taylor series. Hence, it's
     * not obligatory but highly recommended to use this method for
     * {@code num} smaller than 1.
     * @param num
     * @param precision
     * @return {@code e^(num)}
     */
    public static BigDecimal findExp(BigDecimal num, int precision) {

        BigDecimal reVal = BigDecimal.ZERO;
        BigDecimal reVal2 = BigDecimal.ZERO;
        BigDecimal precisionTest = new BigDecimal("0.1").pow(precision);
        int taylorTC = 0;

        do {
            taylorTC++;
            reVal2 = reVal;
            reVal = reVal.add(num.pow(taylorTC).divide(
                    new BigDecimal(factorial(new BigInteger(String.valueOf(taylorTC)))),
                    precision + 10,
                    RoundingMode.HALF_UP));
        } while (reVal.subtract(reVal2).compareTo(precisionTest) > 0);

        return (reVal.add(BigDecimal.ONE)).round(new MathContext(precision + 1));
    }

    /**
     * Returns the value of the square root of e, whose precision
     * is {@code precision}.
     * @param precision
     * @return {@code sqrt(e)}
     */
    public static BigDecimal findSqrtEulerNum(int precision) {
        return findExp(new BigDecimal("0.5"), precision);
    }

    public static BigInteger factorial(BigInteger integer) {
        BigInteger reVal = BigInteger.ONE;
        for (;integer.compareTo(BigInteger.ZERO) == 1; integer = integer.subtract(BigInteger.ONE)) {
            reVal = reVal.multiply(integer);    
        }
        return reVal;
    }
    
}
