package Number.RealAnalytic;

import Number.Complex.ComplexNum;

public interface Analytic{

    /**
     * Returns the value of an analytic expression in {@code ComplexNum}.
     * @return value of {@code this}.
     */
    public ComplexNum compute();

}
