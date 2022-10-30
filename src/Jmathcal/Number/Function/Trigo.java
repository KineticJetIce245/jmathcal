package Jmathcal.Number.Function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Consumer;

import Jmathcal.Number.InfiniteValueException;
import Jmathcal.Number.Complex.ComplexDbl;
import Jmathcal.Number.Complex.ComplexNum;

/**
 * Util class implementing some methods about trigonometry calculations of
 * {@code BigDecimal} and {@code ComplexNum}.
 * 
 * @author KineticJetIce245
 */
public class Trigo {

    public static final BigDecimal TWO = new BigDecimal("2");
    public static final BigDecimal PI100 = new BigDecimal(
            "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170680");
    /**
     * Additional precision for calculation.
     * <p>
     * Default: {@code 10}
     */
    public static int PRECI = 10;

    /**
     * Additional precision for taylor series.
     * Used to compare to new terms of taylor series.
     * If the terms is smaller than break the loop.
     * <p>
     * Default: {@code 3}
     */
    public static int PRECITEST = 3;

    /**
     * Find the sin of {@code num} with the required
     * precision.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code sin(num)}
     */
    public static BigDecimal sin(BigDecimal num, MathContext mc) {
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        BigDecimal reVal = BigDecimal.ZERO;
        boolean reValSign = true;

        if (num.compareTo(BigDecimal.ZERO) == 0)
            return reVal;// sin(0) = 0

        // sin(-x) = -sin(x)
        if (num.compareTo(BigDecimal.ZERO) < 0) {
            reValSign = false;
            num = num.abs();
        }

        // Pi value
        BigDecimal piVal;
        if (mc.getPrecision() < 90) {
            piVal = PI100.round(calPrecision);
        } else {
            piVal = PI(calPrecision);
        }

        // sin(2k*pi + x) = sin(x)
        if (num.compareTo(piVal.multiply(TWO)) > 0) {
            num = num.subtract(num.divide(piVal.multiply(TWO), 0, RoundingMode.HALF_UP).multiply(piVal.multiply(TWO)));
        }

        reVal = reValSign ? findSin(num, mc) : findSin(num, mc).negate();
        return reVal;
    }

    /**
     * Returns the cos of {@code num} with the required
     * precision.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code cos(num)}
     */
    public static BigDecimal cos(BigDecimal num, MathContext mc) {
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        BigDecimal reVal = BigDecimal.ONE;
        if (num.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ONE;// cos(0) = 1
        // cos(-x) = cos(x)
        num = num.abs();

        BigDecimal piVal;
        if (mc.getPrecision() < 90) {
            piVal = PI100.round(calPrecision);
        } else {
            piVal = PI(calPrecision);
        }

        // cos(2k*pi + x) = cos(x)
        if (num.compareTo(piVal.multiply(TWO)) > 0) {
            num = num.subtract(num.divide(piVal.multiply(TWO), 0, RoundingMode.HALF_UP).multiply(piVal.multiply(TWO)));
        }

        reVal = findCos(num, mc);
        return reVal;
    }

    /**
     * Returns the tan of {@code num} with the required
     * precision.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code tan(num)}
     */
    public static BigDecimal tan(BigDecimal num, MathContext mc) {
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
     * @param mc  number of significant figures and rounding mode
     * @return {@code arcsin(num)}
     */
    public static BigDecimal rArcsin(BigDecimal num, MathContext mc) {
        boolean sign = true;
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);

        // arcsin(-x) = -arcsin(x)
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
            num = (BigDecimal.ONE.subtract(num.pow(2))).sqrt(calPrecision);
            BigDecimal piVal = PI(calPrecision).divide(TWO);
            BigDecimal reVal = piVal.subtract(rArcsin(num, calPrecision)).round(mc);
            return sign ? reVal : reVal.negate();
        }

        // arcsin x = x + (1/2)(x^3/3) + ((1*3)/(2*4))(x^5/5) +
        // ((1*3*5)/(2*4*6))(x^7/7) + ...
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
     * @param mc  number of significant figures and rounding mode
     * @return {@code arccos(num)}
     */
    public static BigDecimal rArccos(BigDecimal num, MathContext mc) {
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
     * @param mc  number of significant figures and rounding mode
     * @return {@code arctan(num)}
     */
    public static BigDecimal arctan(BigDecimal num, MathContext mc) {
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
     * @param mc  number of significant figures and rounding mode
     * @return {@code sin(num)}
     */
    public static BigDecimal findSin(BigDecimal num, MathContext mc) {
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(mc.getPrecision() + PRECITEST));

        BigDecimal reVal = num;
        BigDecimal currentXVal = num.multiply(num.multiply(num));
        BigDecimal currentTermVal;
        BigDecimal divisor = BigDecimal.ONE;
        BigDecimal taylorTC = TWO;

        do {
            // -3!, 5!, -7!, 9!...
            divisor = divisor.multiply(taylorTC.multiply(taylorTC.add(BigDecimal.ONE))).negate();

            currentTermVal = currentXVal.divide(divisor, calPrecision);
            reVal = reVal.add(currentTermVal);

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
     * @param mc  number of significant figures and rounding mode
     * @return {@code cos(num)}
     */
    public static BigDecimal findCos(BigDecimal num, MathContext mc) {
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        BigDecimal precisionTest = BigDecimal.ONE.scaleByPowerOfTen(-(mc.getPrecision() + PRECITEST));

        BigDecimal reVal = BigDecimal.ONE;
        BigDecimal currentXVal = num.multiply(num);
        BigDecimal currentTermVal;
        BigDecimal divisor = BigDecimal.ONE;
        BigDecimal taylorTC = BigDecimal.ONE;

        do {
            // -2!, 4!, -6!, 8!...
            divisor = divisor.multiply(taylorTC.multiply(taylorTC.add(BigDecimal.ONE))).negate();

            currentTermVal = currentXVal.divide(divisor, calPrecision);
            reVal = reVal.add(currentTermVal);

            taylorTC = taylorTC.add(TWO);
            currentXVal = currentXVal.multiply(num.multiply(num));
        } while (currentTermVal.abs().compareTo(precisionTest) > 0);

        return reVal.round(mc);
    }

    /**
     * Return pi with the required precision
     * 
     * @param mc number of significant figures and rounding mode
     * @return {@code pi}
     */
    public static BigDecimal PI(MathContext mc) {
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
     * @param mc  number of significant figures and rounding mode
     * @return {@code sin(num)}
     */
    public static ComplexNum sin(ComplexNum num, MathContext mc) {
        if (num.getImaValue().compareTo(BigDecimal.ZERO) == 0)
            return new ComplexNum(sin(num.getRealValue(), mc));
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        ComplexNum x = num.multiplyByI();
        ComplexNum y = Exp.exp(x.negate(), calPrecision)
                .subtract(Exp.exp(x, calPrecision)).multiplyByI();
        return y.divide(new ComplexNum("2")).round(mc);
    }

    /**
     * Returns the cos of {@code num}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code cos(num)}
     */
    public static ComplexNum cos(ComplexNum num, MathContext mc) {
        if (num.getImaValue().compareTo(BigDecimal.ZERO) == 0)
            return new ComplexNum(cos(num.getRealValue(), mc));
        // precision for calculation
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        ComplexNum x = num.multiplyByI();
        ComplexNum y = Exp.exp(x, calPrecision)
                .add(Exp.exp(x.negate(), calPrecision));
        return y.divide(new ComplexNum("2")).round(mc);
    }

    /**
     * Returns the tan of {@code num}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code tan(num)}
     */
    public static ComplexNum tan(ComplexNum num, MathContext mc) {
        if (num.getImaValue().compareTo(BigDecimal.ZERO) == 0)
            return new ComplexNum(tan(num.getRealValue(), mc));
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        ComplexNum x = num.multiplyByI().multiply(new ComplexNum("2"));
        ComplexNum y = Exp.exp(x, calPrecision);
        return new ComplexNum("1").subtract(y)
                .divide(y.add(new ComplexNum("1")), calPrecision)
                .multiplyByI(mc);
    }

    /**
     * Returns the arcsin of {@code num}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code arcsin(num)}
     */
    public static ComplexNum arcsin(ComplexNum num, MathContext mc) {
        if (num.getImaValue().compareTo(BigDecimal.ZERO) == 0
                && num.getRealValue().abs().compareTo(BigDecimal.ONE) < 0) {
            return new ComplexNum(rArcsin(num.getRealValue(), mc));
        }

        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);

        ComplexNum reVal = ComplexNum.ONE.subtract(num.multiply(num)).pow("0.5", calPrecision);
        reVal = reVal.add(num.multiplyByI());
        reVal = Exp.ln(reVal, calPrecision);
        return reVal.multiplyByI().round(mc).negate(mc);
    }

    /**
     * Returns the arccos of {@code num}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code arccos(num)}
     */
    public static ComplexNum arccos(ComplexNum num, MathContext mc) {
        if (num.getImaValue().compareTo(BigDecimal.ZERO) == 0
                && num.getRealValue().abs().compareTo(BigDecimal.ONE) < 0) {
            return new ComplexNum(rArccos(num.getRealValue(), mc));
        }
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);

        ComplexNum reVal = num.multiply(num).subtract(ComplexNum.ONE).pow("0.5", calPrecision);
        reVal = reVal.add(num);
        reVal = Exp.ln(reVal, calPrecision);
        return reVal.multiplyByI().round(mc).negate(mc);
    }

    /**
     * Returns the arctan of {@code num}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code arctan(num)}
     */
    public static ComplexNum arctan(ComplexNum num, MathContext mc) {
        if (num.getImaValue().compareTo(BigDecimal.ZERO) == 0) {
            return new ComplexNum(arctan(num.getRealValue(), mc));
        }
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);

        ComplexNum reVal = num.multiplyByI();
        reVal = reVal.add(ComplexNum.ONE).divide(ComplexNum.ONE.subtract(reVal), calPrecision);
        reVal = Exp.ln(reVal, calPrecision);
        return reVal.divide(new ComplexNum("2"), mc).multiplyByI().negate(mc);
    }

    // ComplexDbl Functions
    /**
     * Returns the sin of {@code num}.
     * 
     * @param num
     * @return {@code sin(num)}
     */
    public static ComplexDbl sin(ComplexDbl num) {
        if (num.getImaValue() == 0)
            return new ComplexDbl(Math.sin(num.getRealValue()));
        ComplexDbl x = num.multiplyByI();
        ComplexDbl y = Exp.exp(x.negate())
                .subtract(Exp.exp(x)).multiplyByI();
        return y.divide(new ComplexDbl(2));
    }

    /**
     * Returns the cos of {@code num}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code cos(num)}
     */
    public static ComplexDbl cos(ComplexDbl num) {
        if (num.getImaValue() == 0)
            return new ComplexDbl(Math.cos(num.getRealValue()));
        ComplexDbl x = num.multiplyByI();
        ComplexDbl y = Exp.exp(x).add(Exp.exp(x.negate()));
        return y.divide(new ComplexDbl(2));
    }

    /**
     * Returns the tan of {@code num}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code tan(num)}
     */
    public static ComplexDbl tan(ComplexDbl num) {
        if (num.getImaValue() == 0)
            return new ComplexDbl(Math.tan(num.getRealValue()));
        ComplexDbl x = num.multiplyByI().multiply(new ComplexDbl(2));
        ComplexDbl y = Exp.exp(x);
        return ComplexDbl.ONE.subtract(y)
                .divide(y.add(ComplexDbl.ONE))
                .multiplyByI();
    }

    /**
     * Returns the arcsin of {@code num}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code arcsin(num)}
     */
    public static ComplexDbl arcsin(ComplexDbl num) {
        if (num.getImaValue() == 0 && Math.abs(num.getRealValue()) <= 1)
            return new ComplexDbl(Math.asin(num.getRealValue()));
        ComplexDbl reVal = Exp.pow(ComplexDbl.ONE.subtract(num.multiply(num)), new ComplexDbl(0.5));
        reVal = reVal.add(num.multiplyByI());
        reVal = Exp.ln(reVal);
        return reVal.multiplyByI().negate();
    }
    
    /**
     * Returns the arccos of {@code num}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code arccos(num)}
     */
    public static ComplexDbl arccos(ComplexDbl num) {
        if (num.getImaValue() == 0)
            return new ComplexDbl(Math.acos(num.getRealValue()));
        ComplexDbl reVal = Exp.pow(num.multiply(num).subtract(ComplexDbl.ONE), new ComplexDbl(0.5));
        reVal = reVal.add(num);
        reVal = Exp.ln(reVal);
        return reVal.multiplyByI().negate();
    }

    /**
     * Returns the arctan of {@code num}.
     * 
     * @param num
     * @param mc  number of significant figures and rounding mode
     * @return {@code arctan(num)}
     */
    public static ComplexDbl arctan(ComplexDbl num) {
        if (num.getImaValue() == 0)
            return new ComplexDbl(Math.atan(num.getRealValue()));
        ComplexDbl reVal = num.multiplyByI();
        reVal = reVal.add(ComplexDbl.ONE).divide(ComplexDbl.ONE.subtract(reVal));
        reVal = Exp.ln(reVal);
        return reVal.divide(new ComplexDbl(2.0)).multiplyByI().negate();
    }

    /**
     * Converts the an angle from one unit to another with specified precision and
     * rounding mode.
     * 
     * @param value
     * @param original   original angle unit
     * @param targetType result's angle unit
     * @param mc
     * @return the result of the conversion of the angle from one unit to another
     */
    public static BigDecimal angleConvert(BigDecimal value, AngleType original, AngleType targetType, MathContext mc) {
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        return value.multiply(targetType.giveCONST(calPrecision)).divide(original.giveCONST(calPrecision), mc);
    }

    public static ComplexNum angleConvert(ComplexNum value, AngleType original, AngleType targetType, MathContext mc) {
        MathContext calPrecision = new MathContext(mc.getPrecision() + PRECI, RoundingMode.HALF_UP);
        return value.multiply(new ComplexNum(targetType.giveCONST(calPrecision)))
                .divide(new ComplexNum(original.giveCONST(calPrecision)), mc);
    }

    public static ComplexDbl angleConvert(ComplexDbl value, AngleType original, AngleType targetType) {
        interface Lambda {
            public double run(AngleType angleType);
        }
        Lambda giveCONST = (s) -> {
            switch (s) {
                case DEG:
                    return 180.0;
                case RAD:
                    return Math.PI;
                default:
                    return 200.0;
            }
        };
        return value.multiply(new ComplexDbl(giveCONST.run(targetType))).divide(new ComplexDbl(giveCONST.run(original)));
    }
}
