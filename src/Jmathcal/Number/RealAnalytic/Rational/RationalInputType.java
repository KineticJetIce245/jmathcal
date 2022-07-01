package Jmathcal.Number.RealAnalytic.Rational;

/**
 * Specify the String format for {@code RationalNum}'s constructor.
 */
public enum RationalInputType {
    /**
     * Separated by {@code /}, the first integer represents the numerator
     * and the second integer represents the denominator.<p>
     * Example :<P>
     * In {@code "5/6"}, {@code 5} is the numerator and {@code 6} is the
     * denominator.
     */
    INT_FRACTION,

    /**
     * Same format as a normal decimal number.<p>
     * Example :<p>
     * {@code "0.25"} will be store as {@code 1/4}
     */
    DECIMAL,

    /**
     * Separated by {@code R}, the second part represents the <i>repetend</i>.<p>
     * Example :<p>
     * {@code "0.45R34"} equals to {@code 0.45343434...}
     */
    RECURRING_DECIMAL,

    /**
     * Same format as a normal integer.<p>
     * Example :<p>
     * {@code "4"} will be store as {@code 4/1}
     */
    INT
}
