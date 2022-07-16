package Jmathcal.Number.RealAnalytic;

import java.math.MathContext;

import Jmathcal.Number.Complex.ComplexNum;

public interface Analytic {

    /**
     * Returns the value of an analytic expression
     * in {@code ComplexNum}.
     * @param precision number of significant figures
     * @return value of {@code this}.
     */
    public ComplexNum compute(int precision);

    /**
     * Returns the value of an analytic expression
     * in {@code ComplexNum}.
     * @param mc number of significant figures and rounding mode
     * @return value of {@code this}.
     */
    public ComplexNum compute(MathContext mc);

}
