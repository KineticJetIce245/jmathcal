package Number.RealAnalytic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;

import Number.Computable;
import Number.Complex.*;
import Number.RealAnalytic.Rational.*;

public class AnalyticExp implements Serializable, Computable<AnalyticExp>, Analytic {
    //Appease the serialization god, LOL
    private static final long serialVersionUID = 8547052652255377391L;

    /**
     * val[0] = father expression
     * val[1], val[2] = son expression
     */
    private final Analytic[] val = new Analytic[3];
    private final OperationType type;

    public AnalyticExp(String val, RationalInputType type) {
        this.type = OperationType.NUM;
        this.val[1] = (new RationalNum(val, type)).reduce();
    }

    public AnalyticExp(Analytic number) {
        this.type = OperationType.NUM;
        this.val[1] = number;
    }

    public AnalyticExp(Analytic[] val, OperationType type){
        System.arraycopy(val, 0, this.val, 0, 3);
        this.type = type;
    }

    @Override
    public AnalyticExp add(AnalyticExp augend) {
        Analytic[] reVal = new Analytic[3];
        // Setting the numbers
        reVal[1] = this;
        reVal[2] = augend;
        // Setting the type
        AnalyticExp reExpression = new AnalyticExp(reVal, OperationType.ADD);
        // Setting their father expression
        this.val[0] = reExpression;
        augend.val[0] = reExpression;
        return reExpression;
    }

    @Override
    public AnalyticExp multiply(AnalyticExp multiplicand) {
        Analytic[] reVal = new Analytic[3];
        // Setting the numbers
        reVal[1] = this;
        reVal[2] = multiplicand;
        // Setting the type
        AnalyticExp reExpression = new AnalyticExp(reVal, OperationType.MUL);
        // Setting their father expression
        this.val[0] = reExpression;
        multiplicand.val[0] = reExpression;
        return reExpression;
    }

    public AnalyticExp pow(AnalyticExp exponent) {
        Analytic[] reVal = new Analytic[3];
        // Setting the numbers
        reVal[1] = this;
        reVal[2] = exponent;
        // Setting the type
        AnalyticExp reExpression = new AnalyticExp(reVal, OperationType.POW);
        // Setting their father expression
        this.val[0] = reExpression;
        exponent.val[0] = reExpression;
        return reExpression;
    }

    @Override
    public AnalyticExp subtract(AnalyticExp subtrahend) {
        return this.add(subtrahend.multiply(new AnalyticExp(RationalNum.ONE.negate())));
    }

    @Override
    public AnalyticExp divide(AnalyticExp divisor) {
        return this.multiply(divisor.pow(new AnalyticExp(RationalNum.ONE.negate())));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + Arrays.hashCode(val);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnalyticExp other = (AnalyticExp) obj;
        if (type != other.type)
            return false;
        if (!Arrays.equals(val, other.val))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return this.type != OperationType.NUM ? type.name() + "(" + val[1] + "," + val[2] + ")" : val[1].toString();
    }

    @Override
    public ComplexNum compute() {
        switch (this.type) {
            case ADD:
                return this.val[1].compute().add(this.val[2].compute());
            case MUL :
                return this.val[1].compute().multiply(this.val[2].compute());
            case POW :
                return this.val[1].compute().p;
            default:
                return this.val[1].compute();
        }
    }
    
}