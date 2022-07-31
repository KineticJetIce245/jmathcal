package Jmathcal.Expression;

import java.io.Serializable;
import java.math.MathContext;

public class ExprFunction<T> implements Serializable, ExprElements {

    private static final long serialVersionUID = -4376930957526847386L;

    private OpsType type;
    private int parameterNum;
    private int precedence;
    private MathContext mc;

    public ExprFunction() {
        
    }


}
