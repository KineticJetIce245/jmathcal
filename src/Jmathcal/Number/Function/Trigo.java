package Jmathcal.Number.Function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Trigo {

    public static final BigDecimal TWO = new BigDecimal("2");
    public static final BigDecimal PI100 = new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170680");

    public static BigDecimal sin(BigDecimal num, int precision) {
        BigDecimal reVal = BigDecimal.ZERO;
        return reVal;
    }

    public static BigDecimal findSin(BigDecimal num, int precision) {
        // calculate sin(x)
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(precision+1));
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
            currentTermVal = currentXVal.divide(divisor, precision+10, RoundingMode.HALF_UP);

            reVal = reVal.add(currentTermVal);

            // taylorTC = 2, 4, 6, 8...
            taylorTC = taylorTC.add(TWO);
            currentXVal = currentXVal.multiply(num.multiply(num));
        } while (currentTermVal.abs().compareTo(precisionTest) > 0);

        return reVal.round(new MathContext(precision + 1));
    }

    public static BigDecimal findArcsin(BigDecimal num, int precision) {
        // arcsin = (x^(2n+1)) * ((2n)!) / ((4^n) * ((n!)^2) * (2n+1))
        return null;
    }

    public static BigDecimal PI(int precision) {
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(precision+3));
        // Gauss–Legendre algorithm
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ONE
                .divide(Exp.rPow(TWO, new BigDecimal("0.5"), precision+10),
                        precision+10,
                        RoundingMode.HALF_UP);
        BigDecimal t = new BigDecimal("0.25");
        BigDecimal p = BigDecimal.ONE;
        BigDecimal oriA;
        BigDecimal reVal = BigDecimal.ZERO;
        BigDecimal reVal2 = BigDecimal.ZERO;
        int i = 0;

        do {
            i++;
            oriA = a;
            reVal2 = reVal;
            System.out.println("--");
            System.out.println(System.currentTimeMillis());
            a = (a.add(b)).divide(TWO);
            System.out.println(System.currentTimeMillis());
            b = Exp.rPow(oriA.multiply(b), new BigDecimal("0.5"), precision+10);
            System.out.println(System.currentTimeMillis());
            t = t.subtract(p.multiply(oriA.subtract(a)).multiply(oriA.subtract(a)));
            System.out.println(System.currentTimeMillis());
            System.out.println("--");
            p = p.add(p);

            reVal = (a.add(b)).pow(2)
                    .divide(t.multiply(new BigDecimal("4")), precision+10, RoundingMode.HALF_UP);
        } while (reVal.subtract(reVal2).abs().compareTo(precisionTest) > 0);

        System.out.println(i);

        return reVal.round(new MathContext(precision+1));
    }

}
