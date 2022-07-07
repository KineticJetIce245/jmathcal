package Jmathcal.Number.RealAnalytic;

import Jmathcal.Number.Complex.ComplexNum;

public interface Analytic {

    /**
     * Returns the value of an analytic expression
     * in {@code ComplexNum}.
     * @param precision
     * @return value of {@code this}.
     */
    public ComplexNum compute(int precision);

}
