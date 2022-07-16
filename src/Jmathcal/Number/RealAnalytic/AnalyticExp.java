package Jmathcal.Number.RealAnalytic;

import java.io.Serializable;
import java.math.MathContext;

import Jmathcal.Number.Computable;
import Jmathcal.Number.Complex.*;
import Jmathcal.Number.RealAnalytic.Rational.*;

public class AnalyticExp implements Serializable, Computable<AnalyticExp>, Analytic {
    // Appease the serialization god, LOL
    private static final long serialVersionUID = 8547052652255377391L;

    private final Analytic[] val;
    private final OperationType type;
    // positive if true
    private boolean oprSign;

    public static final AnalyticExp ZERO = new AnalyticExp(RationalNum.ZERO);
    public static final AnalyticExp ONE = new AnalyticExp(RationalNum.ONE);

    // Rational number's constructor
    public AnalyticExp(RationalNum num) {
        val = new Analytic[1];
        val[0] = num;
        type = OperationType.NUM;
    }

    public AnalyticExp(RationalNum num, boolean oprSign) {
        val = new Analytic[1];
        val[0] = num;
        type = OperationType.NUM;
        this.oprSign = oprSign;
    }

    public AnalyticExp(String val, RationalInputType type) {
        this.val = new Analytic[1];
        this.val[0] = new RationalNum(val, type);
        this.type = OperationType.NUM;
    }

    public AnalyticExp(String val, RationalInputType type, boolean oprSign) {
        this.val = new Analytic[1];
        this.val[0] = new RationalNum(val, type);
        this.type = OperationType.NUM;
        this.oprSign = oprSign;
    }

    // clone()
    public AnalyticExp clone() {
        if (type != OperationType.NUM)
            return new AnalyticExp(val, type, this.oprSign);
        return new AnalyticExp((RationalNum) val[0], this.oprSign);
    }

    // Constructor for clone()
    private AnalyticExp(Analytic[] val, OperationType type, boolean sign) {
        this.val = new Analytic[val.length];
        for (int i = 0; i < val.length; i++) {
            this.val[i] = ((AnalyticExp) val[i]).clone();
        }
        this.type = type;
        this.oprSign = sign;
    }

    // Constructor for operations
    // This method is not safe
    private AnalyticExp(AnalyticExp val1, AnalyticExp val2, OperationType type, boolean[] sign) {
        this.val = new Analytic[2];
        val[0] = val1;
        val[1] = val2;
        this.type = type;
        val1.oprSign = sign[0];
        val2.oprSign = sign[1];
    }

    // Constructor for operations
    // This method is not safe
    public AnalyticExp(Analytic[] vals, OperationType type) {
        this.val = vals;
        this.type = type;
    }

    @Override
    public AnalyticExp add(AnalyticExp augend) {
        return this.mergeP(augend, OperationType.ADD);
    }

    @Override
    public AnalyticExp subtract(AnalyticExp subtrahend) {
        return this.mergeN(subtrahend, OperationType.ADD);
    }

    @Override
    public AnalyticExp multiply(AnalyticExp multiplicand) {
        return this.mergeP(multiplicand, OperationType.MUL);
    }

    @Override
    public AnalyticExp divide(AnalyticExp divisor) {
        return this.mergeN(divisor, OperationType.MUL);
    }

    public AnalyticExp pow(AnalyticExp argument) {
        return this.mergeP(argument, OperationType.POW);
    }

    private AnalyticExp mergeP(AnalyticExp num, OperationType opType) {
        AnalyticExp reVal;
        AnalyticExp thisShadow = this.clone();
        AnalyticExp numShadow = num.clone();
        boolean[] sign = { true, true };

        if (thisShadow.type == numShadow.type && thisShadow.type == opType) {
            Analytic[] vals = new Analytic[thisShadow.val.length + numShadow.val.length];
            for (int i = 0; i < thisShadow.val.length; i++) {
                vals[i] = thisShadow.val[i];
            }
            for (int j = thisShadow.val.length; j < thisShadow.val.length + numShadow.val.length; j++) {
                vals[j] = numShadow.val[j - thisShadow.val.length];
            }
            reVal = new AnalyticExp(vals, opType);

        } else if (thisShadow.type == opType && numShadow.type == OperationType.NUM) {
            Analytic[] vals = new Analytic[thisShadow.val.length + 1];
            for (int i = 0; i < thisShadow.val.length; i++) {
                vals[i] = thisShadow.val[i];
            }
            numShadow.oprSign = true;
            vals[thisShadow.val.length] = numShadow;
            reVal = new AnalyticExp(vals, opType);
        } else {
            reVal = new AnalyticExp(thisShadow, numShadow, opType, sign);
        }
        return reVal;
    }

    private AnalyticExp mergeN(AnalyticExp num, OperationType opType) {
        AnalyticExp reVal;
        AnalyticExp thisShadow = this.clone();
        AnalyticExp numShadow = num.clone();
        boolean[] sign = { true, false };

        if (thisShadow.type == numShadow.type && thisShadow.type == opType) {
            Analytic[] vals = new Analytic[thisShadow.val.length + numShadow.val.length];
            for (int i = 0; i < thisShadow.val.length; i++) {
                vals[i] = thisShadow.val[i];
            }
            // switch sign
            for (int j = thisShadow.val.length; j < thisShadow.val.length + numShadow.val.length; j++) {
                vals[j] = numShadow.val[j - thisShadow.val.length];
                ((AnalyticExp) vals[j]).oprSign = !((AnalyticExp) vals[j]).oprSign;
            }
            reVal = new AnalyticExp(vals, opType);

        } else if (thisShadow.type == opType && numShadow.type == OperationType.NUM) {
            Analytic[] vals = new Analytic[thisShadow.val.length + 1];
            for (int i = 0; i < thisShadow.val.length; i++) {
                vals[i] = thisShadow.val[i];
            }
            numShadow.oprSign = false;
            vals[thisShadow.val.length] = numShadow;
            reVal = new AnalyticExp(vals, opType);
        } else {
            reVal = new AnalyticExp(thisShadow, numShadow, opType, sign);
        }
        return reVal;
    }

    public AnalyticExp negate() {
        return ZERO.subtract(this);
    }

    @Override
    public String toString() {

        String reVal = "(" + val[0] + ")";
        String sign;
        switch (this.type) {
            case ADD:
                for (int i = 1; i < val.length; i++) {
                    sign = ((AnalyticExp) val[i]).oprSign ? " + " : " - ";
                    reVal = reVal + sign + "(" + ((AnalyticExp) val[i]).toString() + ")";
                }
                break;
            case MUL:
                for (int i = 1; i < val.length; i++) {
                    sign = ((AnalyticExp) val[i]).oprSign ? " * " : " / ";
                    reVal = reVal + sign + "(" + ((AnalyticExp) val[i]).toString() + ")";
                }
                break;
            case POW:
                for (int i = 1; i < val.length; i++) {
                    reVal = reVal + "^" + "(" + ((AnalyticExp) val[i]).toString() + ")";
                }
                break;
            case NUM:
                reVal = val[0].toString();
                break;
        }
        return reVal;
    }

    @Override
    public ComplexNum compute(int precision) {
        return this.compute(new MathContext(precision));
    }

    @Override
    public ComplexNum compute(MathContext mc) {
        return this.compute(mc, true);
    }

    public ComplexNum compute(MathContext mc, boolean ifFirstCal) {
        MathContext calMC = mc;

        if (ifFirstCal)
            calMC = new MathContext(mc.getPrecision() + 10, mc.getRoundingMode());
        
        ComplexNum reVal = val[0] instanceof AnalyticExp ? ((AnalyticExp) val[0]).compute(calMC, false)
                : val[0].compute(calMC);
        switch (this.type) {
            case ADD:
                for (int i = 1; i < val.length; i++) {
                    if (((AnalyticExp) val[i]).oprSign) {
                        reVal = reVal.add(((AnalyticExp) val[i]).compute(calMC, false));
                    } else {
                        reVal = reVal.subtract(((AnalyticExp) val[i]).compute(calMC, false));
                    }
                }
                break;
            case MUL:
                for (int i = 1; i < val.length; i++) {
                    if (((AnalyticExp) val[i]).oprSign) {
                        reVal = reVal.multiply(((AnalyticExp) val[i]).compute(calMC, false)).round(mc);
                    } else {
                        reVal = reVal.divide(((AnalyticExp) val[i]).compute(calMC, false), mc);
                    }
                }
                break;
            case POW:
                reVal = reVal.pow(((AnalyticExp) val[1]).compute(calMC, false), mc);
                break;
            default:
                break;
        }
        if (ifFirstCal)
            reVal = reVal.round(mc);
        return reVal;
    }

}