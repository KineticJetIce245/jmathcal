package Jmathcal.Expression;

public enum OpsType {
    ADD(2,1),
    SUB(2,1),
    MUL(2,2),
    DIV(2,2),
    POW(2,3),
    SQRT(1,4),
    SIN(1, 4),
    COS(1, 4),
    TAN(1, 4),
    ASIN(1, 4),
    ACOS(1, 4),
    ATAN(1, 4),
    LOG(1, 4),
    LN(1, 4),
    OPEN_P(0,0),
    CLOSE_P(0,0),
    SUM(4,0),
    PRO(4,0);

    public final int parameterNum;
    public final int precedence;

    OpsType(int parameterNum, int precedence) {
        this.parameterNum = parameterNum;
        this.precedence = precedence;
    }
}
