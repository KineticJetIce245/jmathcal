package Jmathcal.Number.RealAnalytic;

import java.io.Serializable;

import Jmathcal.Number.Computable;
import Jmathcal.Number.Complex.*;
import Jmathcal.Number.RealAnalytic.Rational.*;

public class AnalyticExp implements Serializable, Computable<AnalyticExp>, Analytic {
    //Appease the serialization god, LOL
    private static final long serialVersionUID = 8547052652255377391L;

    private final Analytic[] val;
    private final OperationType type;
    // positive for true
    private boolean oprSign;

    public AnalyticExp(RationalNum num) {
        val = new Analytic[1];
        val[0] = num;
        type = OperationType.NUM;
    }

    public AnalyticExp clone(){
        if (type != OperationType.NUM)
            return new AnalyticExp(val, type);
        return new AnalyticExp(((RationalNum)val[0]));
    }

    public AnalyticExp(String val, RationalInputType type) {
        this.val = new Analytic[1];
        this.val[0] = new RationalNum(val, type);
        this.type = OperationType.NUM;
    }

    public AnalyticExp(AnalyticExp val1, AnalyticExp val2, OperationType type, boolean[] sign) {
        this.val = new Analytic[2];
        val[0] = val1.clone();
        val[1] = val2.clone();
        this.type = type;
        val1.oprSign = sign[0];
        val2.oprSign = sign[1];
    }

    private AnalyticExp(Analytic[] val, OperationType type) {
        this.val = new Analytic[val.length];
        for (int i = 0; i < val.length; i++) {
            this.val[i] = ((AnalyticExp)val[i]).clone();
        }
        this.type = type;
    }
}