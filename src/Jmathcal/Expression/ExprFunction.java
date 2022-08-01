package Jmathcal.Expression;

import java.io.Serializable;

public class ExprFunction implements Serializable, ExprElements {

    private static final long serialVersionUID = -4376930957526847386L;

    private final OpsType type;

    public ExprFunction(OpsType type) {
        this.type = type;
    }

    public OpsType getType() {
        return this.type;
    }
    
    public int compPrecedence(ExprFunction o) {
        return Integer.valueOf(this.type.precedence).compareTo(o.type.precedence);
    }

    @Override
    public String toString() {
        return type.name();
    }
}
